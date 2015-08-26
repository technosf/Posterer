/*
 * Copyright 2015 technosf [https://github.com/technosf]
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.github.technosf.posterer.models;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Basic tests for classes implementing {@code RequestModel}
 * 
 * @author technosf
 * @since 0.0.1
 * @version 0.0.1
 */
public abstract class RequestModelAbstractTest
{
    private static final String TEST_STRING = "qwerty";


    /* ------------------ Abstract Methods ------------------------- */

    /**
     * Get the concrete class under test
     * 
     * @return class under test
     */
    protected abstract RequestModel getClassUnderTest();


    /* ------------------ Test Setup and Teardown -------------------- */

    /**
     * Ensure that the class under test is present
     */
    @BeforeClass
    public final void beforeClass()
    {
        assertNotNull(getClassUnderTest());
    }


    /* ------------------ Tests -------------------- */

    @Test(dependsOnMethods = { "RequestModelAbstractTest" })
    public abstract void doRequest();


    /* ------------------ Getter Setter Tests -------------------- */

    @Test(groups = { "RequestModelAbstractTest" })
    public void get_setProxyHost()
    {
        String host = getClassUnderTest().getProxyHost();
        assertNotEquals(host, TEST_STRING);

        getClassUnderTest().setProxyHost(TEST_STRING);
        assertEquals(getClassUnderTest().getProxyHost(), TEST_STRING);

        getClassUnderTest().setProxyHost(host);
        if (host == null)
        {
            assertEquals(getClassUnderTest().getProxyHost(), "");
        }
        else
        {
            assertEquals(getClassUnderTest().getProxyHost(), host);
        }
    }


    @Test(groups = { "RequestModelAbstractTest" })
    public void get_setProxyPass()
    {
        String pass = getClassUnderTest().getProxyHost();
        assertNotEquals(pass, TEST_STRING);

        getClassUnderTest().setProxyHost(TEST_STRING);
        assertEquals(getClassUnderTest().getProxyHost(), TEST_STRING);

        getClassUnderTest().setProxyHost(pass);
        assertEquals(getClassUnderTest().getProxyHost(), pass);
    }


    @Test(groups = { "RequestModelAbstractTest" })
    public void get_setProxyPort()
    {
        String port = getClassUnderTest().getProxyPort();
        assertNotEquals(port, TEST_STRING);

        getClassUnderTest().setProxyPort(TEST_STRING);
        assertEquals(getClassUnderTest().getProxyPort(), TEST_STRING);

        getClassUnderTest().setProxyPort(port);
        if (port == null)
        {
            assertEquals(getClassUnderTest().getProxyPort(), "");
        }
        else
        {
            assertEquals(getClassUnderTest().getProxyPort(), port);
        }
    }


    @Test(groups = { "RequestModelAbstractTest" })
    public void get_setProxyUser()
    {
        String user = getClassUnderTest().getProxyUser();
        assertNotEquals(user, TEST_STRING);

        getClassUnderTest().setProxyUser(TEST_STRING);
        assertEquals(getClassUnderTest().getProxyUser(), TEST_STRING);

        getClassUnderTest().setProxyUser(user);
        if (user == null)
        {
            assertEquals(getClassUnderTest().getProxyUser(), "");
        }
        else
        {
            assertEquals(getClassUnderTest().getProxyUser(), user);
        }
    }


    @Test(groups = { "RequestModelAbstractTest" })
    public void get_setTimeout()
    {

        int timeout = getClassUnderTest().getTimeout();
        assertNotEquals(timeout, timeout + 99);

        getClassUnderTest().setTimeout(timeout + 99);
        assertEquals(getClassUnderTest().getTimeout(), timeout + 99);

        getClassUnderTest().setTimeout(timeout);
        assertEquals(getClassUnderTest().getTimeout(), timeout);
    }


    @Test(groups = { "RequestModelAbstractTest" })
    public void get_setUseProxy()
    {
        boolean useprox = getClassUnderTest().getUseProxy();
        assertNotEquals(useprox, !useprox);

        getClassUnderTest().setUseProxy(!useprox);
        assertEquals(getClassUnderTest().getUseProxy(), !useprox);

        getClassUnderTest().setUseProxy(useprox);
        assertEquals(getClassUnderTest().getUseProxy(), useprox);
    }

}
