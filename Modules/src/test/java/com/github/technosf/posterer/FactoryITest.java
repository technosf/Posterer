package com.github.technosf.posterer;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.github.technosf.posterer.models.Properties;
import com.github.technosf.posterer.models.RequestModel;
import com.github.technosf.posterer.modules.ModuleException;

public class FactoryITest
{

    Factory factory;
    String resourcesDirectoryPath;
    File resourcesDirectory;


    @BeforeClass
    public void beforeClass() throws ModuleException
    {
        ClassLoader classLoader = FactoryITest.class.getClassLoader();
        resourcesDirectoryPath =
                classLoader.getResource("test.posterer.properties").getFile();
        resourcesDirectory =
                new File(resourcesDirectoryPath).getParentFile();
        factory = Factory.getFactory("test.", resourcesDirectory,
                "test.posterer.properties");
    }


    @Test
    public void getPropertiesTest() throws IOException
    {
        Properties p = factory.getProperties();
        assertNotNull(p);
        assertEquals(p.getPropertiesDir(), resourcesDirectory.toString());
        System.out.println(p.getPropertiesDir());
    }


    @Test
    public void getRequestModelTest()
    {
        RequestModel rm = factory.getRequestModel();
        assertNotNull(rm);
        System.out.println(rm.getTimeout());
    }
}
