/*
 * Copyright 2018 technosf [https://github.com/technosf]
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
import java.util.function.BooleanSupplier;

import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
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
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.technosf.posterer.models.Request;
import com.github.technosf.posterer.models.ResponseModel;
import com.github.technosf.posterer.models.impl.base.AbstractResponseModelTask;
import com.github.technosf.posterer.utils.Auditor;

/**
 * Apache Commons implementation of {@ResponsetModel}
 * <p>
 * Uses Commons HTTP frameworks to make HTTP requests and process responses.
 * 
 * @author technosf
 * @since 0.0.1
 * @version 0.0.1
 */
@SuppressWarnings("restriction")
public final class CommonsResponseModelTaskImpl
        extends AbstractResponseModelTask<HttpResponse>
        implements ResponseModel
{
    /**
     * Logger
     */
    @SuppressWarnings("null")
    private static final Logger LOG = LoggerFactory
            .getLogger(CommonsResponseModelTaskImpl.class);

    /* Messages */
    private static final String CONST_ERR_NULL_CLIENT = "Client is null";
    private static final String CONST_ERR_NO_REPONSE =
            "Can't get response body";
    private static final String CONST_ERR_UNKNOWN_METHOD = "Unknow method: {}";

    /**
     * CRLF
     */
    private static final String CRLF = "\r\n";

    /**
     * the Http Client
     */
    private HttpClientBuilder clientBuilder;

    /**
     * the Http Client
     */
    private @Nullable CloseableHttpClient client;

    /**
     * The Http Request
     */
    private @Nullable HttpUriRequest httpUriRequest;

    /**
     * has the response been processed?
     */
    private boolean isResponseProcessed = false;

    /**
     * Function that determines if client auth is needed
     */
    private final BooleanSupplier neededClientAuth;


    /**
     * Creates a new {@code CommonsResponseModelTaskImpl} for the given request
     * 
     * @param requestId
     *            the request reference id
     * @param timeout
     *            connection timeout
     * @param request
     *            the request
     * @param preStatus
     *            Status provided by calling class
     */
    public CommonsResponseModelTaskImpl(final int requestId, Auditor auditor,
            final HttpClientBuilder clientBuilder, final int timeout,
            final Request request, BooleanSupplier neededClientAuth)
    {
        super(requestId, auditor, timeout, request);
        this.clientBuilder = clientBuilder;
        this.neededClientAuth = neededClientAuth;
    }


    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.models.impl.base.AbstractResponseModelTask#prepareClient()
     */
    @Override
    protected void prepareClient()
    {

        if (getRequest().getAuthenticate())
    	/*
    	 * build a client with auth scope
    	 */
        {
        	CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        	credentialsProvider.setCredentials(AuthScope.ANY, 
        	    new UsernamePasswordCredentials(getRequest().getUsername(), getRequest().getPassword()));
            client = clientBuilder.setDefaultCredentialsProvider(credentialsProvider).build();
        }
        else
    	/*
    	 * client with no auth scope
    	 */
        {
            client = clientBuilder.build();
        }
        
        
        //Create the request
        HttpUriRequest newHttpUriRequest =
                createRequest(getRequest().getUri(),
                        getRequest().getMethod());

        if (newHttpUriRequest != null
                && !getRequest().getPayload().isEmpty()
                && HttpEntityEnclosingRequestBase.class
                        .isInstance(newHttpUriRequest))
        /*
         * If there is a payload and the request can carry a payload,
         * create and add the payload
         */
        {
            ContentType ct = ContentType.create(getRequest().getContentType(),
                    Consts.UTF_8);
            StringEntity payload =
                    new StringEntity(getRequest().getPayload(), ct);
            ((HttpEntityEnclosingRequestBase) newHttpUriRequest)
                    .setEntity(payload);
            LOG.debug("Creating payload with MIME type: {}", ct.getMimeType());
        }

        httpUriRequest = newHttpUriRequest;
    }


    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.models.impl.base.AbstractResponseModelTask#getReponse()
     */
    @SuppressWarnings("null")
    @Override
    protected HttpResponse getReponse(Auditor auditor)
            throws ClientProtocolException, IOException
    {
        this.auditor = auditor;

        if (client != null)
        /*
         * Execute the request
         */
        {
            return client.execute(httpUriRequest);
        }

        LOG.error(CONST_ERR_NULL_CLIENT);
        throw new ClientProtocolException(CONST_ERR_NULL_CLIENT);
    }


    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.models.impl.base.AbstractResponseModelTask#closeClient()
     */
    @Override
    protected void closeClient()
    {
        HttpClientUtils.closeQuietly(getValue());
    }


    /**
     * Generates the specific request type
     * 
     * @param uri
     *            the uri
     * @param method
     *            the request method
     * @return the request
     */
    @Nullable
    private static HttpUriRequest createRequest(final @Nullable URI uri,
            final @Nullable String method)
    {
        if (method != null && uri != null)
        {
            switch (method)
            {
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

        LOG.error(CONST_ERR_UNKNOWN_METHOD, method);
        return null;
    }


    /*
     * (non-Javadoc)
     * 
     * @see com.github.technosf.posterer.models.AbstractResponseModelTask#processResponse()
     */
    @SuppressWarnings("null")
    @Override
    protected synchronized void processResponse()
    {
        if (!isResponseProcessed)
        {
            HttpResponse httpResponse = getValue();
            if (httpResponse != null)
            {
                auditor.postscript(false,
                        httpResponse.getStatusLine().toString());
                // headers = Arrays.toString(response.getAllHeaders());
                responseHeaders =
                        prettyPrintHeaders(httpResponse.getAllHeaders());
                if (httpResponse.getEntity() != null)
                {
                    try
                    {
                        responseBody =
                                EntityUtils.toString(httpResponse.getEntity());
                    }
                    catch (ParseException | IOException e)
                    {
                        LOG.error(CONST_ERR_NO_REPONSE, e);
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
    @SuppressWarnings("null")
    private String prettyPrintHeaders(final Header[] headers)
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
     * @see com.github.technosf.posterer.models.impl.base.AbstractResponseModelTask#isResponseProcessed()
     */
    @Override
    protected boolean isResponseProcessed()
    {
        return isResponseProcessed;
    }


    /**
     * {@inheritDoc}
     *
     * @see com.github.technosf.posterer.models.ResponseModel#getStatus()
     */
    @Override
    public String getStatus()
    {
        return auditor.toString();
    }


    /**
     * {@inheritDoc}
     *
     * @see com.github.technosf.posterer.models.ResponseModel#neededClientAuth()
     */
    @Override
    public boolean neededClientAuth()
    {
        return neededClientAuth.getAsBoolean();
    }
}
