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

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.notNull;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.function.BooleanSupplier;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.github.technosf.posterer.core.models.Request;
import com.github.technosf.posterer.core.models.impl.RequestBean;
import com.github.technosf.posterer.core.models.impl.base.AbstractResponseModelTaskAbstractTest;
import com.github.technosf.posterer.core.utils.Auditor;

/**
 * @author technosf
 * @since 0.0.1
 * @version 0.0.1
 */
//@RunWith(PowerMockRunner.class)
@PrepareForTest({ HttpClientBuilder.class, RequestBean.class })
public class CommonsResponseModelTaskImplTest
        extends AbstractResponseModelTaskAbstractTest<HttpResponse>
{

    private static final int TIMEOUT = 30;

    private HttpClientBuilder httpClientBuilder = mock("Builder1",HttpClientBuilder.class);
    private CloseableHttpClient closeableHttpClient =
            mock("Client1",CloseableHttpClient.class);
    private CloseableHttpResponse closeableHttpResponse =
            mock("Resp1",CloseableHttpResponse.class);

    // private BooleanSupplier bsFalse = new BooleanSupplier()
    // {

    //     @Override
    //     public boolean getAsBoolean()
    //     {
    //         return false;
    //     }
    // };

    private BooleanSupplier bsTrue = new BooleanSupplier()
    {

        @Override
        public boolean getAsBoolean()
        {
            return true;
        }
    };

    private Request request = mock("Req1", Request.class);    


    /*
     * The class under test
     */
    private CommonsResponseModelTaskImpl classUnderTest =
            new CommonsResponseModelTaskImpl(0, new Auditor(),
                    httpClientBuilder, 0,
                    request, bsTrue);

    /* ------------------ Test Setup and Teardown -------------------- */


    @Override
    protected CommonsResponseModelTaskImpl getClassUnderTest()
    {
        return classUnderTest;
    }


    /* ------------------ Tests -------------------- */

    @BeforeClass
    public void beforeClass() throws ClientProtocolException, IOException
    {
        classUnderTest =
                new CommonsResponseModelTaskImpl(1, new Auditor(),
                        httpClientBuilder, TIMEOUT,
                        request, bsTrue);

        reset(httpClientBuilder, closeableHttpClient, closeableHttpResponse,
                request);

        expect(httpClientBuilder.build()).andStubReturn(closeableHttpClient);
        expect(closeableHttpClient.execute(notNull(HttpUriRequest.class)))
                .andStubReturn(closeableHttpResponse);
        expect(request.getAuthenticate()).andReturn(false).anyTimes();

        try
        {
            expect(request.getUri()).andStubReturn(new URI("http://testuri"));
            expect(request.getMethod()).andStubReturn("GET");
            expect(request.getPayload()).andStubReturn("-=Payload=-");
        }
        catch (URISyntaxException e)
        {
            e.printStackTrace();
        }
        replay(httpClientBuilder, closeableHttpClient, closeableHttpResponse,
                request);
    }


    @Test
    public void dummy()
    {
    }
}
