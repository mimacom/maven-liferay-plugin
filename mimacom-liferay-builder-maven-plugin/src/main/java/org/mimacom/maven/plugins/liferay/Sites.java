package org.mimacom.maven.plugins.liferay;


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

import org.edorasframework.tools.common.func.impl.DefaultFunctionalEnhancer;
import org.edorasframework.tools.maven.util.SiteCreator;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static org.edorasframework.tools.common.func.Funcs.f;
import static org.edorasframework.tools.maven.util.SinkFunc.*;


public class Sites {
    private final static Sites INSTANCE = DefaultFunctionalEnhancer.getInstance().enhancedInstance(Sites.class,
        Sites.class.getClassLoader());

    Sites() {}

    static Sites getInstance() {
        return INSTANCE;
    }

    void patchesSite(SiteCreator siteCreator,String title,String text, Map<String, List<String>> patches) {
        Entry<String, List<String>> patch = null;
        String place = null;

        document(siteCreator.getMainSite(),
            startSite(title,text,//
                table(//
                    tableRow(//
                        tableHeader(text("File")), //
                        tableHeader(text("Defined in"))//
                    ),//
                    forEach(patches.entrySet(), f(patch, //
                        tableRow(//
                            tableCell(text(patch.getKey())), //
                            tableCell(list(//
                            forEach(patch.getValue(), f(place, //
                                listItem(text(place))))//
                            ))//
                        ))//
                    )//
                )//
            )//
        );
    }

}