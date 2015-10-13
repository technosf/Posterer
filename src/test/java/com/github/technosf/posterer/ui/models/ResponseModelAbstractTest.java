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
package com.github.technosf.posterer.ui.models;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.concurrent.ExecutionException;

import org.testng.annotations.Test;

import com.github.technosf.posterer.modules.RequestBean;
import com.github.technosf.posterer.ui.models.ResponseModel;

/**
 * @author technosf
 * @since 0.0.1
 * @version 0.0.1
 */
public abstract class ResponseModelAbstractTest
{
    /* ------------------ Abstract Methods ------------------------- */

    /**
     * Get the concrete class under test
     * 
     * @return class under test
     */
    protected abstract ResponseModel getClassUnderTest();


    /* ------------------ Tests -------------------- */

    @Test(dependsOnGroups = { "init" })
    public final void getBody()
    {
        String value = getClassUnderTest().getBody();
        assertNotNull(value);
    }


    @Test(dependsOnGroups = { "init" })
    public final void getElaspedTimeMili()
    {
        long value = getClassUnderTest().getElaspedTimeMilli();
        assertTrue(value > 0);
    }


    @Test(dependsOnGroups = { "init" })
    public final void getHeaders()
    {
        long value = getClassUnderTest().getElaspedTimeMilli();
        assertTrue(value > 0);
    }


    @Test(dependsOnGroups = { "init" })
    public final void getReferenceId()
    {
        long value = getClassUnderTest().getReferenceId();
        assertTrue(value > 0);
    }


    @Test(dependsOnGroups = { "init.*" })
    public final void getRequestBean()
    {
        RequestBean value = getClassUnderTest().getRequestBean();
        assertNotNull(value);
    }


    @Test(dependsOnGroups = { "init.*" })
    public final void getResponse()
    {
        String value = getClassUnderTest().getResponse();
        assertNotNull(value);
    }


    @Test(dependsOnGroups = { "init.*" })
    public final void isComplete()
            throws InterruptedException, ExecutionException
    {
        boolean value = getClassUnderTest().isComplete();
        assertTrue(value);
    }
}
