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
package com.github.technosf.posterer.modules.commons.transport;

import java.io.IOException;
import java.net.URI;

import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.technosf.posterer.modules.RequestBean;
import com.github.technosf.posterer.ui.models.ResponseModel;
import com.github.technosf.posterer.ui.models.base.AbstractResponseModelTask;

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
    private static final Logger LOG = LoggerFactory
            .getLogger(CommonsResponseModelTaskImpl.class);

    private static final HttpClientBuilder clientBuilder = HttpClientBuilder
            .create().useSystemProperties();

    private static final String CRLF = "\r\n";

    private CloseableHttpClient client;

    private HttpUriRequest request;

    private boolean isResponseProcessed = false;

    private String statusLine;


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
     * @see com.github.technosf.posterer.ui.models.base.AbstractResponseModelTask#prepareClient()
     */
    //@SuppressWarnings("null")
    @Override
    protected void prepareClient()
    {
        // Create the client that will manage the connetion
        client = clientBuilder.build();

        //Create the request
        request =
                createRequest(requestBean.getUri(),
                        requestBean.getMethod());

        if (!requestBean.getPayload().isEmpty()
                && HttpEntityEnclosingRequestBase.class.isInstance(request))
        /*
         * If there is a payload and the request can carry a payload,
         * create and add the payload
         */
        {
            StringEntity payload = new StringEntity(requestBean.getPayload(),
                    ContentType.create(requestBean.getContentType(),
                            Consts.UTF_8));
            ((HttpEntityEnclosingRequestBase) request).setEntity(payload);
        }
    }


    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.ui.models.base.AbstractResponseModelTask#getReponse()
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
     * @see com.github.technosf.posterer.ui.models.base.AbstractResponseModelTask#closeClient()
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

        LOG.error("Unknow method: {}", method);
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
                statusLine = httpResponse.getStatusLine().toString();
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
                        LOG.error("Can't get response body", e);
                    }
                }
            }
            closeClient();
            isResponseProcessed = true;
            response = httpResponse;
        }
    }


    /**
     * Pretty print header array
     * 
     * @param headers
     * @return nicely formatted headers
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


    /**
     * {@inheritDoc}
     *
     * @see com.github.technosf.posterer.ui.models.base.AbstractResponseModelTask#isResponseProcessed()
     */
    @Override
    protected boolean isResponseProcessed()
    {
        return isResponseProcessed;
    }


    /**
     * {@inheritDoc}
     *
     * @see com.github.technosf.posterer.ui.models.ResponseModel#getStatus()
     */
    @Override
    public String getStatus()
    {
        return statusLine;
    }
}
