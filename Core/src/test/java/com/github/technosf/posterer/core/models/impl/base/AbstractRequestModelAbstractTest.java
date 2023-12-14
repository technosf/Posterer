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
package com.github.technosf.posterer.core.models.impl.base;

import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.testng.Assert.assertNotNull;

import org.eclipse.jdt.annotation.NonNull;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.github.technosf.posterer.core.models.Proxy;
import com.github.technosf.posterer.core.models.Request;
import com.github.technosf.posterer.core.models.RequestModelAbstractTest;
import com.github.technosf.posterer.core.models.ResponseModel;
import com.github.technosf.posterer.core.models.impl.KeyStoreBean;
import com.github.technosf.posterer.core.models.impl.base.AbstractRequestModel;
import com.github.technosf.posterer.core.utils.Auditor;

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
    private static final int TIMEOUT = 30;


    /* ------------------ Abstract Methods ------------------------- */

    /**
     * Get the concrete class under test
     * 
     * @return class under test
     */
    @NonNull
    protected abstract AbstractRequestModel<T> getClassUnderTest();


    protected abstract Request getRequest();

    /* ------------------ Test Setup and Teardown -------------------- */

    //@SuppressWarnings("null")
    Proxy proxy = mock(Proxy.class);
    //@SuppressWarnings("null")
    KeyStoreBean keyStoreBean = mock(KeyStoreBean.class);


    @BeforeMethod
    public void beforeMethod()
    {
        reset(proxy, keyStoreBean);
        replay(proxy, keyStoreBean);
    }


    /* ------------------ Tests -------------------- */

    @Test
    public void createRequestintRequestBean()
    {
        AbstractRequestModel<T> classUnderTest = getClassUnderTest();
        Request request = getRequest();
        T requestImpl =
                classUnderTest.createRequest(1, new Auditor(), TIMEOUT,
                        request);
        assertNotNull(requestImpl);
    }


    @Test
    public void createRequestintRequestBeanProxy()
    {
        AbstractRequestModel<T> classUnderTest = getClassUnderTest();
        Request request = getRequest();
        T requestImpl =
                classUnderTest.createRequest(1, new Auditor(), TIMEOUT,
                        request);
        assertNotNull(requestImpl);
    }


    @Test
    public void createRequestintRequestBeanKeyStoreBeanAlias()
    {
        AbstractRequestModel<T> classUnderTest = getClassUnderTest();
        Request request = getRequest();
        T requestImpl =
                classUnderTest.createRequest(1, new Auditor(), TIMEOUT,
                        request);
        assertNotNull(requestImpl);
    }


    @Test
    public void createRequestintRequestBeanProxyKeyStoreBeanAlias()
    {
        AbstractRequestModel<T> classUnderTest = getClassUnderTest();
        Request request = getRequest();
        T requestImpl =
                classUnderTest.createRequest(1, new Auditor(), TIMEOUT,
                        request);
        assertNotNull(requestImpl);
    }


    @Test
    public void doRequest()
    {
        ResponseModel responseModel =
                getClassUnderTest().doRequest(getRequest());
        assertNotNull(responseModel);
    }

}
