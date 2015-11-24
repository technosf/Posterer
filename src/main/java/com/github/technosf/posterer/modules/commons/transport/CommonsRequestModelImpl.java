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

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.apache.http.HttpHost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.eclipse.jdt.annotation.NonNull;

import com.github.technosf.posterer.models.Proxy;
import com.github.technosf.posterer.models.Request;
import com.github.technosf.posterer.models.RequestModel;
import com.github.technosf.posterer.models.impl.base.AbstractRequestModel;
import com.github.technosf.posterer.utils.Auditor;
import com.github.technosf.posterer.utils.ssl.FlexibleX509TrustManager;
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

    /* ------------------------------------------------ */

    /**
     * {@inheritDoc}
     *
     * @see com.github.technosf.posterer.models.impl.base.AbstractRequestModel#createRequest(int,
     *      int, com.github.technosf.posterer.models.Request)
     */
    @Override
    protected CommonsResponseModelTaskImpl createRequest(final int requestId,
            final Auditor auditor,
            final int timeout, final Request request)
    {
        HttpClientBuilder builder = getBuilder(auditor, request.getSecurity());
        return new CommonsResponseModelTaskImpl(requestId, auditor,
                builder,
                timeout,
                request);
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
            final Auditor auditor,
            final int timeout, final Request request, final Proxy proxy)
    {
        HttpClientBuilder builder =
                getBuilder(auditor, request.getSecurity(), proxy);

        return new CommonsResponseModelTaskImpl(requestId, auditor,
                builder,
                timeout,
                request);
    }


    /**
     * Creates a builder for the given ssl impl
     * 
     * @param ssl
     * @return the builder
     */
    @SuppressWarnings("null")
    private HttpClientBuilder getBuilder(Auditor auditor,
            final String ssl)
    {
        @NonNull HttpClientBuilder builder = HttpClientBuilder.create();

        if (!ssl.isEmpty())
        /*
         * Use custom builder
         */
        {
            /*
             * Configure builder
             */
            buildInSSL(auditor, builder, ssl);
        }
        return builder;
    }


    /**
     * Creates a builder for the given ssl impl and proxy
     * 
     * @param auditor
     * @param string
     * @return the builder
     */
    @SuppressWarnings("null")
    private HttpClientBuilder getBuilder(Auditor auditor,
            final @NonNull String ssl,
            final Proxy proxy)
    {
        @NonNull HttpClientBuilder builder = HttpClientBuilder.create();

        if (!ssl.isEmpty() || !proxy.toString().isEmpty())
        /*
         * Use custom builder 
         */
        {
            /*
             * Configure builder
             */
            buildInSSL(auditor, builder, ssl);
            buildInProxy(auditor, builder, proxy);
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
    private void buildInProxy(Auditor auditor, HttpClientBuilder builder,
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
    @SuppressWarnings("null")
    private void buildInSSL(Auditor auditor,
            HttpClientBuilder builder,
            final String ssl)
    {
        TrustManager[] myTMs =
                new TrustManager[] {
                        new FlexibleX509TrustManager(auditor, true) };
        SSLContext ctx;
        try
        {
            ctx = SSLContext.getInstance(ssl);
            ctx.init(null, myTMs, null);
            builder.setSSLContext(ctx);
        }
        catch (NoSuchAlgorithmException | KeyManagementException e)
        {
            auditor.append(false, e.getStackTrace().toString());
        }
        builder.setSSLHostnameVerifier(
                new PromiscuousHostnameVerifier(auditor));
    }
}
