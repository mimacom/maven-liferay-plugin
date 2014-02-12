package org.mimacom.maven.plugins.liferay;


import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;



public class ClassListerFilter {
    @Test
    public void simple() {
        Lister cl = new Lister();
        cl.addInclude("com.liferay");
        cl.analyzeList(Arrays.asList("com/liferay/a", "com/liferay/a", "b", "b"));
        Assert.assertEquals(1, cl.getResult().getFiles().size());
    }
}
