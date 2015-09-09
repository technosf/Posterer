/*
 * Copyright 2014 technosf [https://github.com/technosf]
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.github.technosf.posterer.transports.commons;

import java.io.IOException;
import java.net.URI;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.github.technosf.posterer.models.RequestBean;
import com.github.technosf.posterer.models.ResponseModel;
import com.github.technosf.posterer.models.base.AbstractResponseModelTask;

/**
 * Apache Commons implementation of {@ResponsetModel}
 * <p>
 * 
 * @author technosf
 * @since 0.0.1
 * @version 0.0.1
 */
public final class CommonsResponseModelTaskImpl
        extends AbstractResponseModelTask<HttpResponse>
        implements ResponseModel
{

    //@SuppressWarnings("null")
    private static final HttpClientBuilder clientBuilder = HttpClientBuilder
            .create().useSystemProperties();

    private static final String CRLF = "\r\n";

    private CloseableHttpClient client;

    private HttpUriRequest request;

    private boolean isResponseProcessed = false;


    /**
     * @param requestId
     * @param requestData
     */
    public CommonsResponseModelTaskImpl(int requestId,
            RequestBean requestBean)
    {
        super(requestId, requestBean);
    }


    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.models.base.AbstractResponseModelTask#prepareClient()
     */
    //@SuppressWarnings("null")
    @Override
    protected void prepareClient()
    {
        client = clientBuilder.build();
        request =
                createRequest(requestBean.getUri(),
                        requestBean.getMethod());
    }


    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.models.base.AbstractResponseModelTask#getReponse()
     */
    @Override
    protected HttpResponse getReponse()
            throws ClientProtocolException, IOException
    {
        if (client != null)
        {
            return client.execute(request);
        }
        return null;
    }


    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.models.base.AbstractResponseModelTask#closeClient()
     */
    @Override
    protected void closeClient()
    {
        HttpClientUtils.closeQuietly(getValue());
    }


    /**
     * @param uri
     * @param method
     * @return
     */
    private static HttpUriRequest createRequest(URI uri,
            String method)
    {
        if (method != null && uri != null)
        {
            switch (method) {
                case "GET":
                    return new HttpGet(uri);
                case "HEAD":
                    return new HttpHead(uri);
                case "POST":
                    return new HttpPost(uri);
                case "PUT":
                    return new HttpPut(uri);
                case "DELETE":
                    return new HttpDelete(uri);
                case "TRACE":
                    return new HttpTrace(uri);
                case "OPTIONS":
                    return new HttpOptions(uri);
                case "PATCH":
                    return new HttpPatch(uri);
            }
        }

        return null;
    }


    /*
     * (non-Javadoc)
     * 
     * @see com.github.technosf.posterer.models.AbstractResponseModelTask#processResponse()
     */
    // @SuppressWarnings("null")
    @Override
    protected synchronized void processResponse()
    {
        if (!isResponseProcessed)
        {
            HttpResponse httpResponse = getValue();
            if (httpResponse != null)
            {
                // headers = getValue().getStatusLine().toString();
                // headers = Arrays.toString(response.getAllHeaders());
                headers = prettyPrintHeaders(httpResponse.getAllHeaders());
                if (httpResponse.getEntity() != null)
                {
                    try
                    {
                        body = EntityUtils.toString(httpResponse.getEntity());
                    }
                    catch (ParseException | IOException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            closeClient();
            isResponseProcessed = true;
            response = httpResponse;
        }
    }


    /**
     * @param headers
     * @return
     */
    //@SuppressWarnings("null")
    private String prettyPrintHeaders(Header[] headers)
    {
        StringBuilder sb = new StringBuilder();
        for (Header header : headers)
        {
            if (sb.length() > 0)
            {
                sb.append(CRLF);
            }
            sb.append(header.getName())
                    .append("=")
                    .append(header.getValue());
        }

        return sb.toString();
    }
}
