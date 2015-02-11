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
package com.github.technosf.posterer.models.base;

import org.testng.annotations.Test;

import com.github.technosf.posterer.models.RequestModelAbstractTest;
import com.github.technosf.posterer.models.ResponseModel;

/**
 * Basic tests for classes implementing {@code PropertiesModel}
 * 
 * @author technosf
 * @since 0.0.1
 * @version 0.0.1
 */
public abstract class AbstractRequestModelAbstractTest<T extends ResponseModel>
        extends RequestModelAbstractTest
{

    /* ------------------ Abstract Methods ------------------------- */

    /**
     * Get the concrete class under test
     * 
     * @return class under test
     */
    protected abstract AbstractRequestModel<T> getClassUnderTest();


    /* ------------------ Test Setup and Teardown -------------------- */

    /* ------------------ Tests -------------------- */

    @Test
    public void createRequestintURIintStringStringbooleanStringString()
    {
        throw new RuntimeException("Test not implemented");
    }


    @Test
    public void createRequestintRequestBean()
    {
        throw new RuntimeException("Test not implemented");
    }


    @Test
    public void doRequest()
    {
        throw new RuntimeException("Test not implemented");
    }

}
