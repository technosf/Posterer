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

import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;

import org.eclipse.jdt.annotation.NonNull;
import org.testng.annotations.Test;

import com.github.technosf.posterer.models.Request;
import com.github.technosf.posterer.models.impl.base.AbstractRequestModelAbstractTest;

/**
 * @author technosf
 * @since 0.0.1
 * @version 0.0.1
 */
@SuppressWarnings("null")
public class CommonsRequestModelImplTest
        extends AbstractRequestModelAbstractTest<CommonsResponseModelTaskImpl>
{

    @NonNull
    private CommonsRequestModelImpl classUnderTest =
            new CommonsRequestModelImpl();

    private Request request = mock(Request.class);


    @Override
    protected @NonNull Request getRequest()
    {
        return request;
    }


    /* ------------------ Test Setup and Teardown -------------------- */

    public void beforeClassImpl()
    {
        reset(request);
        replay(request);
    }


    /**
     * {@inheritDoc}
     *
     * @see com.github.technosf.posterer.models.impl.base.AbstractRequestModelAbstractTest#getClassUnderTest()
     */
    @NonNull
    @Override
    protected CommonsRequestModelImpl getClassUnderTest()
    {
        return classUnderTest;
    }


    /* ------------------ Tests -------------------- */

    @Test
    protected void createRequest_bean()
    {
        classUnderTest.getClass(); // TODO Add test
    }


    /**
     * {@inheritDoc}
     *
     * @see com.github.technosf.posterer.models.impl.base.AbstractRequestModel#createRequest(int,
     *      java.net.URI, int, java.lang.String, java.lang.String, boolean,
     *      java.lang.String, java.lang.String)
     */
    @Test
    protected void createRequest_params()
    {
        classUnderTest.getClass(); // TODO Add test
    }

}
