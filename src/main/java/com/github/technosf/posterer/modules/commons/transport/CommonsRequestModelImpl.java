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

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.apache.http.HttpHost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.eclipse.jdt.annotation.NonNull;

import com.github.technosf.posterer.models.Proxy;
import com.github.technosf.posterer.models.Request;
import com.github.technosf.posterer.models.RequestModel;
import com.github.technosf.posterer.models.impl.base.AbstractRequestModel;
import com.github.technosf.posterer.utils.ssl.MyX509TrustManager;
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


    /* ------------------------------------------------ */

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
        StringBuilder status = new StringBuilder();
        HttpClientBuilder builder = getBuilder(status, request.getSecurity());
        return new CommonsResponseModelTaskImpl(requestId,
                builder,
                timeout,
                request, status);
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
        StringBuilder status = new StringBuilder();
        HttpClientBuilder builder =
                getBuilder(request.getSecurity(), proxy);

        return new CommonsResponseModelTaskImpl(requestId, builder, timeout,
                request, status);
    }


    /**
     * Creates a builder for the given ssl impl
     * 
     * @param ssl
     * @return the builder
     */
    @SuppressWarnings("null")
    private HttpClientBuilder getBuilder(final StringBuilder status,
            final String ssl)
    {
        @NonNull HttpClientBuilder builder = DEFAULT_BUILDER;

        if (!ssl.isEmpty())
        /*
         * Use custom builder
         */
        {
            String key = "[" + ssl + "][]";
            if (HTTP_CLIENT_BUILDERS.containsKey(key))
            /*
             * Use existing builder
             */
            {
                builder = HTTP_CLIENT_BUILDERS.get(key);
            }
            else
            /*
             * Create new builder
             */
            {
                builder = HttpClientBuilder.create();
                HTTP_CLIENT_BUILDERS.put(key, builder);
                /*
                 * Configure builder
                 */
                buildInSSL(builder, ssl);
            }
        }
        return builder;
    }


    /**
     * Creates a builder for the given ssl impl and proxy
     * 
     * @param status
     * @param string
     * @return the builder
     */
    @SuppressWarnings("null")
    private HttpClientBuilder getBuilder(final @NonNull String ssl,
            final Proxy proxy)
    {
        @NonNull HttpClientBuilder builder = DEFAULT_BUILDER;

        if (!ssl.isEmpty() || !proxy.toString().isEmpty())
        /*
         * Use custom builder 
         */
        {

            String key = "[" + ssl + "][" + proxy.toString() + "]";
            if (HTTP_CLIENT_BUILDERS.containsKey(key))
            /*
             * Use existing builder
             */
            {
                builder = HTTP_CLIENT_BUILDERS.get(key);
            } // Existing custom builder
            else
            /*
             * Create new builder
             */
            {
                builder = HttpClientBuilder.create();
                HTTP_CLIENT_BUILDERS.put(key, builder);

                /*
                 * Configure builder
                 */
                buildInSSL(builder, ssl);
                buildInProxy(builder, proxy);
            } // new custom builder
        } // custom builder

        return builder;
    }


    /**
     * Configures builder for the given proxy
     * 
     * @param builder
     *            the builder to configure
     * @param proxy
     *            the proxy info
     */
    private void buildInProxy(HttpClientBuilder builder,
            final Proxy proxy)
    {
        HttpHost httpproxy =
                new HttpHost(proxy.getProxyHost(),
                        Integer.parseInt(proxy.getProxyPort()));

        if (!proxy.getProxyUser().isEmpty())
        /* 
         * Add proxy auth
         */
        {
            // TODO Implement proxy auth
            //ProxyAuthenticationStrategy auth = new ProxyAuthenticationStrategy();
        }
        builder.setProxy(httpproxy);
    }


    /**
     * Configures builder for the given SSL/TLS version
     * 
     * @param builder
     *            the builder to configure
     * @param ssl
     *            the ssl info
     */
    private void buildInSSL(HttpClientBuilder builder,
            final String ssl)
    {
        TrustManager[] myTMs = new TrustManager[] { new MyX509TrustManager() };
        SSLContext ctx;
        try
        {
            ctx = SSLContext.getInstance(ssl);
            ctx.init(null, myTMs, null);
            builder.setSSLContext(ctx);
        }
        catch (NoSuchAlgorithmException | KeyManagementException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        builder.setSSLHostnameVerifier(
                new PromiscuousHostnameVerifier());
    }
}
