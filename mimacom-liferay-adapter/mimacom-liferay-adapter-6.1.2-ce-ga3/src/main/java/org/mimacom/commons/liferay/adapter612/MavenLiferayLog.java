package org.mimacom.commons.liferay.adapter612;


import com.liferay.portal.kernel.log.Log;


public class MavenLiferayLog implements Log {
    private org.apache.maven.plugin.logging.Log mlog;

    private String name;

    public MavenLiferayLog(String name, org.apache.maven.plugin.logging.Log mlog) {
        this.name = name;
        this.mlog = mlog;
    }

    public void debug(Object msg) {
        mlog.debug(name + ": " + msg.toString());
    }

    public void debug(Throwable t) {
        mlog.debug(t);
    }

    public void debug(Object msg, Throwable t) {
        mlog.debug(name + ": " + msg.toString(), t);
    }

    public void error(Object msg) {
        mlog.error(name + ": " + msg.toString());
    }

    public void error(Throwable t) {
        mlog.error(t);
    }

    public void error(Object msg, Throwable t) {
        mlog.error(name + ": " + msg.toString(), t);
    }

    public void fatal(Object msg) {
        mlog.error(name + ": " + msg.toString());
    }

    public void fatal(Throwable t) {
        mlog.error(t);
    }

    public void fatal(Object msg, Throwable t) {
        mlog.error(name + ": " + msg.toString(), t);
    }

    public void info(Object msg) {
        mlog.info(name + ": " + msg.toString());
    }

    public void info(Throwable t) {
        mlog.info(t);
    }

    public void info(Object msg, Throwable t) {
        mlog.info(name + ": " + msg.toString(), t);
    }

    public boolean isDebugEnabled() {
        return mlog.isDebugEnabled();
    }

    public boolean isErrorEnabled() {
        return mlog.isErrorEnabled();
    }

    public boolean isFatalEnabled() {
        return mlog.isErrorEnabled();
    }

    public boolean isInfoEnabled() {
        return mlog.isInfoEnabled();
    }

    public boolean isTraceEnabled() {
        return mlog.isDebugEnabled();
    }

    public boolean isWarnEnabled() {
        return mlog.isWarnEnabled();
    }

    public void trace(Object msg) {
        mlog.debug(name + ": " + msg.toString());
    }

    public void trace(Throwable t) {
        mlog.debug(t);
    }

    public void trace(Object msg, Throwable t) {
        mlog.debug(name + ": " + msg.toString(), t);
    }

    public void warn(Object msg) {
        mlog.warn(name + ": " + msg.toString());
    }

    public void warn(Throwable t) {
        mlog.warn(t);
    }

    public void warn(Object msg, Throwable t) {
        mlog.warn(name + ": " + msg.toString(), t);
    }

}