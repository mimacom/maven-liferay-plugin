package org.mimacom.maven.plugins.liferay;


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