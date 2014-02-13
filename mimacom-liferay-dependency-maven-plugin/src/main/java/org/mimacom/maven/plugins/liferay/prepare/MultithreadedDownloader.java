package org.mimacom.maven.plugins.liferay.prepare;


/*
 * Copyright (c) 2014 mimacom a.g.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


class MultithreadedDownloader {
    public static interface ProgressObserver {
        void notify(Counter counter);
    }

    protected HttpClient httpClient;

    private int threads;

    MultithreadedDownloader(int threads) {
        this.threads = threads;
        MultiThreadedHttpConnectionManager conMgr = new MultiThreadedHttpConnectionManager();
        HostConfiguration hc = new HostConfiguration();
        HttpConnectionManagerParams params = conMgr.getParams();
        params.setMaxConnectionsPerHost(hc, 10);
        httpClient = new HttpClient(conMgr);
        httpClient.setHostConfiguration(hc);
    }


    @SuppressWarnings("unused")
    public void close() throws IOException {
        httpClient = null;
    }

    public long download(String url, File dest, int progressStep, ProgressObserver progressObserver) throws IOException {
        dest.getParentFile().mkdirs();
        if (dest.exists()) {
            dest.delete();
        }
        Counter counter = new Counter(progressStep, progressObserver);
        GetMethod download = new GetMethod(url);
        if (canPartialDownload(download)) {
            ExecutorService es = Executors.newFixedThreadPool(threads);
            es.submit(new Download(httpClient, url, dest, 1000000, counter, download));
            for (int i = 1; i < threads; i++) {
                es.submit(new Download(httpClient, url, dest, 1000000, counter, null));
            }
            es.shutdown();
            try {
                es.awaitTermination(60 * 60 * 10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                //ignore
            }
            counter.notifyObserver();
            if (!counter.isSuccess()) {
                throw new IOException(counter.getErrorMessage());
            }
        } else {
            new Download(httpClient, url, dest, 1000000, counter, download).call();
            counter.notifyObserver();
        }
        return counter.getTotal();
    }

    private boolean canPartialDownload(HttpMethod method) throws IOException {
        method.setRequestHeader("Range", "bytes=0-1");
        int status = httpClient.executeMethod(method);
        return (status == HttpStatus.SC_PARTIAL_CONTENT);
    }

    public static class Counter {
        private final ProgressObserver progressObserver;

        private int count = 0;

        private int end = -1;

        private String errorEnd;

        private long total = 0;

        private int step = 0;

        private final int observerStep;

        private long start;

        public Counter(int observerStep, ProgressObserver progressObserver) {
            this.observerStep = observerStep;
            this.progressObserver = progressObserver;
        }

        public synchronized void addTotal(int value) {
            total += value;
            if (total / observerStep > step) {
                step++;
                notifyObserver();
            }
        }

        public void notifyObserver() {
            if (progressObserver != null) {
                progressObserver.notify(this);
            }
        }

        public int getStep() {
            return step;
        }

        public long getTotal() {
            return total;
        }

        public int getBytesPerSecond() {
            return (int)(1000L * getTotal() / (System.currentTimeMillis() - start));
        }

        public synchronized int getCount() {
            if (count == 0) {
                start = System.currentTimeMillis();
            }
            if (end >= 0 && count > end) {
                return -1;
            }
            return count++;
        }

        public synchronized void setEnd(int end) {
            if (this.end == -1 || end < this.end) {
                this.end = end;
            }
        }

        public synchronized void setErrorEnd(String message) {
            this.errorEnd = message;
            setEnd(0);
        }

        public boolean isSuccess() {
            return errorEnd == null;
        }

        public String getErrorMessage() {
            return errorEnd;
        }
    }

    private static class Download implements Callable<Void> {
        private final HttpClient httpClient;

        private final String url;

        private final File file;

        private final int size;

        private final Counter counter;

        private final HttpMethod method;

        public Download(HttpClient httpClient, String url, File file, int size, Counter counter, HttpMethod method) {
            this.httpClient = httpClient;
            this.url = url;
            this.file = file;
            this.size = size;
            this.counter = counter;
            this.method = method;
        }

        public Void call() {
            HttpMethod download = method != null ? method : new GetMethod(url);
            try {
                for (;;) {
                    int c = counter.getCount();
                    if (c < 0) {
                        return null;
                    }
                    download.setRequestHeader("Range", "bytes=" + (c * size) + "-" + ((c + 1) * size - 1));
                    int status = httpClient.executeMethod(download);
                    if (status == HttpStatus.SC_REQUESTED_RANGE_NOT_SATISFIABLE) {
                        counter.setEnd(c);
                        return null;
                    }
                    if (status != HttpStatus.SC_OK && status != HttpStatus.SC_PARTIAL_CONTENT) {
                        throw new HttpException("File " + file + " could not be downloaded, status: " + status);
                    }
                    if (status == HttpStatus.SC_OK) {
                        if (counter.getCount() < 0) {
                            return null;
                        }
                        counter.setEnd(0);
                    }
                    byte[] buf = new byte[8192];
                    InputStream in = new BufferedInputStream(download.getResponseBodyAsStream());
                    RandomAccessFile raf = new RandomAccessFile(file, "rw");
                    int read, pos = c * size;
                    while ((read = in.read(buf)) > 0) {
                        raf.seek(pos);
                        raf.write(buf, 0, read);
                        pos += read;
                        counter.addTotal(read);
                    }
                    raf.close();
                    in.close();
                    // less: last segment, more: no range supported
                    if (pos != (c + 1) * size) {
                        counter.setEnd(c);
                    }
                }
            } catch (Exception e) {
                counter.setErrorEnd(e.getMessage());
            } finally {
                download.releaseConnection();
            }
            return null;
        }
    }
}
