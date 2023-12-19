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
package com.github.technosf.posterer.core.models;

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
    @SuppressWarnings("unused")
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
        beforeClassImpl();
    }


    public abstract void beforeClassImpl();

    /* ------------------ Tests -------------------- */


    @Test(dependsOnGroups = { "RequestModelAbstractTest" })
    public abstract void doRequest();

    /* ------------------ Getter Setter Tests -------------------- */


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

}
