package com.github.technosf.posterer;

import static org.testng.Assert.assertNotNull;

import java.io.IOException;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.github.technosf.posterer.models.Properties;
import com.github.technosf.posterer.models.RequestModel;
import com.github.technosf.posterer.modules.ModuleException;

public class FactoryITest
{
    Factory factory;


    @BeforeClass
    public void beforeClass() throws ModuleException
    {
        factory = new Factory("");
    }


    @Test
    public void getPropertiesTest() throws IOException
    {
        Properties p = factory.getProperties();
        assertNotNull(p);
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
