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
package com.github.technosf.posterer.modules.commons.transport;

import org.apache.http.HttpResponse;
import org.apache.http.impl.client.HttpClientBuilder;
import org.easymock.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.testng.annotations.BeforeClass;

import com.github.technosf.posterer.models.RequestBean;
import com.github.technosf.posterer.models.base.AbstractResponseModelTaskAbstractTest;
import com.github.technosf.posterer.modules.commons.transport.CommonsResponseModelTaskImpl;

/**
 * @author technosf
 * @since 0.0.1
 * @version 0.0.1
 */
@PrepareForTest(HttpClientBuilder.class)
public class CommonsResponseModelTaskImplTest
        extends AbstractResponseModelTaskAbstractTest<HttpResponse>
{

    @Mock
    RequestBean requestBean;

    /*
     * The class under test
     */
    private CommonsResponseModelTaskImpl classUnderTest;


    /* ------------------ Test Setup and Teardown -------------------- */

    @Override
    protected CommonsResponseModelTaskImpl getClassUnderTest()
    {
        return classUnderTest;
    }


    /* ------------------ Tests -------------------- */

    @BeforeClass
    public void beforeClass()
    {
        classUnderTest =
                new CommonsResponseModelTaskImpl(1, requestBean);
    }
}
