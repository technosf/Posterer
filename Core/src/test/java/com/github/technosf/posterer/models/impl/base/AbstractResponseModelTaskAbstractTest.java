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
package com.github.technosf.posterer.models.impl.base;

import static org.testng.Assert.assertNotNull;

import org.testng.annotations.Test;

import com.github.technosf.posterer.models.ResponseModelAbstractTest;

/**
 * Basic tests for classes implementing {@code PropertiesModel}
 * 
 * @author technosf
 * @since 0.0.1
 * @version 0.0.1
 */
public abstract class AbstractResponseModelTaskAbstractTest<T>
        extends ResponseModelAbstractTest
{

    /* ------------------ Abstract Methods ------------------------- */

    /**
     * Get the concrete class under test
     * 
     * @return class under test
     */
    @Override
    protected abstract AbstractResponseModelTask<T> getClassUnderTest();


    /* ------------------ Tests -------------------- */

    @Test(groups = { "init.1" })
    public final void call() throws Exception
    {
        T value = getClassUnderTest().call();
        assertNotNull(value);
    }


    @Test(groups = { "init.1" })
    public final void closeClient()
    {
        getClassUnderTest().closeClient();
    }


    @Test(groups = { "init" })
    public final void getReponse() throws Exception
    {
        T value = getClassUnderTest().getReponse();
        assertNotNull(value);
    }


    @Test(groups = { "init" })
    public final void prepareClient()
    {
        getClassUnderTest().prepareClient();
    }


    @Test(groups = { "init" })
    public final void processResponse()
    {
        getClassUnderTest().processResponse();
    }
}
