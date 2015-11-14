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

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpHost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.eclipse.jdt.annotation.NonNull;

import com.github.technosf.posterer.models.Proxy;
import com.github.technosf.posterer.models.Request;
import com.github.technosf.posterer.models.RequestModel;
import com.github.technosf.posterer.models.impl.base.AbstractRequestModel;
import com.github.technosf.posterer.utils.ssl.PromiscuousHostnameVerifier;

/**
 * Apache Commons implementation of {@RequestModel}
 * <p>
 * 
 * @author technosf
 * @since 0.0.1
 * @version 0.0.1
 * @param <T>
 *            The implementing type for the Response
 */
public class CommonsRequestModelImpl
        extends AbstractRequestModel<CommonsResponseModelTaskImpl>
        implements RequestModel
{

    /**
     * Default client builder id string
     */
    private static final String DEFAULT_BUILDER_STRING = "";

    /**
     * Default direct builder
     */
    @SuppressWarnings("null")
    private static final HttpClientBuilder DEFAULT_BUILDER =
            HttpClientBuilder.create();

    /**
     * Cache of client builder configs
     */
    private static final Map<String, HttpClientBuilder> HTTP_CLIENT_BUILDERS =
            new HashMap<>();


    {
        /*
         * Static initializer
         * 
         * Create and cache the default client builder
         */
        HTTP_CLIENT_BUILDERS.put(DEFAULT_BUILDER_STRING,
                DEFAULT_BUILDER);
        DEFAULT_BUILDER
                .setSSLHostnameVerifier(new PromiscuousHostnameVerifier());
    }


    /**
     * {@inheritDoc}
     *
     * @see com.github.technosf.posterer.models.impl.base.AbstractRequestModel#createRequest(int,
     *      int, com.github.technosf.posterer.models.Request)
     */
    @Override
    protected CommonsResponseModelTaskImpl createRequest(final int requestId,
            final int timeout, final Request request)
    {
        return new CommonsResponseModelTaskImpl(requestId, DEFAULT_BUILDER,
                timeout,
                request, new StringBuilder());
    }


    /**
     * {@inheritDoc}
     *
     * @see com.github.technosf.posterer.models.impl.base.AbstractRequestModel#createRequest(int,
     *      int, com.github.technosf.posterer.models.Request,
     *      com.github.technosf.posterer.models.Proxy)
     */
    @Override
    protected CommonsResponseModelTaskImpl createRequest(final int requestId,
            final int timeout, final Request request, final Proxy proxy)
    {
        HttpClientBuilder builder = DEFAULT_BUILDER;
        StringBuilder status = new StringBuilder();

        if (proxy.isActionable())
        {
            builder = getBuilder(proxy);
            status.append("\nProxy: ").append(proxy.toString()).append("\n");
        }
        return new CommonsResponseModelTaskImpl(requestId, builder, timeout,
                request, status);
    }


    /**
     * Creates a builder for the given proxy
     * 
     * @param proxy
     *            the proxy
     * @return the builder
     */
    @SuppressWarnings("null")
    private HttpClientBuilder getBuilder(final Proxy proxy)
    {
        @NonNull HttpClientBuilder builder = DEFAULT_BUILDER;

        if (!HTTP_CLIENT_BUILDERS.containsValue(proxy.toString()))
        /*
         * New proxy reference
         */
        {
            builder = HttpClientBuilder.create();
            HttpHost httpproxy =
                    new HttpHost(proxy.getProxyHost(),
                            Integer.parseInt(proxy.getProxyPort()));
            builder.setProxy(httpproxy);
            builder.setSSLHostnameVerifier(new PromiscuousHostnameVerifier());
            HTTP_CLIENT_BUILDERS.put(proxy.toString(), builder);
        }
        else
        /*
         * use existing builder
         */
        {
            builder = HTTP_CLIENT_BUILDERS
                    .get(proxy.toString());
        }

        return builder;
    }

}
