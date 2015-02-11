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
    protected abstract AbstractResponseModelTask<T> getClassUnderTest();


    /* ------------------ Test Setup and Teardown -------------------- */

    @Test
    public void call()
    {
        throw new RuntimeException("Test not implemented");
    }


    @Test
    public void closeClient()
    {
        throw new RuntimeException("Test not implemented");
    }


    @Test
    public void getBody()
    {
        throw new RuntimeException("Test not implemented");
    }


    @Test
    public void getContentType()
    {
        throw new RuntimeException("Test not implemented");
    }


    @Test
    public void getElaspedTimeMili()
    {
        throw new RuntimeException("Test not implemented");
    }


    @Test
    public void getEncode()
    {
        throw new RuntimeException("Test not implemented");
    }


    @Test
    public void getHeaders()
    {
        throw new RuntimeException("Test not implemented");
    }


    @Test
    public void getMethod()
    {
        throw new RuntimeException("Test not implemented");
    }


    @Test
    public void getReferenceId()
    {
        throw new RuntimeException("Test not implemented");
    }


    @Test
    public void getReponse()
    {
        throw new RuntimeException("Test not implemented");
    }


    @Test
    public void getResponse()
    {
        throw new RuntimeException("Test not implemented");
    }


    @Test
    public void getTimeout()
    {
        throw new RuntimeException("Test not implemented");
    }


    @Test
    public void getUri()
    {
        throw new RuntimeException("Test not implemented");
    }


    @Test
    public void getUser()
    {
        throw new RuntimeException("Test not implemented");
    }


    @Test
    public void isComplete()
    {
        throw new RuntimeException("Test not implemented");
    }


    @Test
    public void prepareClient()
    {
        throw new RuntimeException("Test not implemented");
    }


    @Test
    public void processResponse()
    {
        throw new RuntimeException("Test not implemented");
    }
}
