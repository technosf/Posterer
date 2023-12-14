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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.function.BooleanSupplier;

import org.apache.http.HttpHost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.eclipse.jdt.annotation.Nullable;

import com.github.technosf.posterer.core.models.Proxy;
import com.github.technosf.posterer.core.models.Request;
import com.github.technosf.posterer.core.models.impl.KeyStoreBean;
import com.github.technosf.posterer.core.models.impl.base.AbstractRequestModel;
import com.github.technosf.posterer.core.utils.Auditor;
import com.github.technosf.posterer.modules.commons.transport.ssl.AuditingSSLSocketFactory;
import com.github.technosf.posterer.utils.ssl.PromiscuousHostnameVerifier;

/**
 * Apache Commons implementation of {@RequestModel}
 * <p>
 * Create an Apache Commons HTTP call configuration and embeds it into
 * an Apache Commons HTTP implementation of a ResponseModelTask that
 * is ready to be fired off.
 * 
 * @author technosf
 * @since 0.0.1
 * @version 0.0.1
 */
public class CommonsRequestModelImpl
        extends AbstractRequestModel<CommonsResponseModelTaskImpl>
{

    private static final String CONST_FMT = "\t%1$s";

    /**
     * Bean to hold call configuration
     */
    private class CallConfigBean
    {
        final HttpClientBuilder builder;
        final BooleanSupplier neededClientAuth;


        CallConfigBean(HttpClientBuilder builder,
                @Nullable BooleanSupplier neededClientAuth)
        {
            this.builder = builder;
            if (neededClientAuth == null)
            {
                this.neededClientAuth = ()->false;
            }
            else
            {
                this.neededClientAuth = neededClientAuth;
            }
        }
    }

    /* ------------------------------------------------ */

    private static final String CONST_ERR_SSL_KEY = "SSL :: Key exception";
    private static final String CONST_ERR_SSL_ALGO = "SSL :: Algo exception";
    private static final String CONST_ERR_SSL_STORE =
            "SSL :: Key Store exception";
    private static final String CONST_ERR_SSL_FILE = "SSL :: File exception";
    private static final String CONST_ERR_SSL_CERT =
            "SSL :: Certificate exception";
    private static final String CONST_ERR_SSL_IO = "SSL :: IO exception";


    /* ------------------------------------------------ */

    /**
     * {@inheritDoc}
     *
     * @see com.github.technosf.posterer.core.models.impl.base.AbstractRequestModel#createRequest(int,
     *      int, com.github.technosf.posterer.core.models.Request)
     */
    @Override
    protected CommonsResponseModelTaskImpl createRequest(final int requestId,
            final Auditor auditor,
            final int timeout, final Request request)
    {
        CallConfigBean callconfig =
                createCallConfig(auditor, request.getSecurity());
        return new CommonsResponseModelTaskImpl(requestId, auditor,
                callconfig.builder,
                timeout,
                request, callconfig.neededClientAuth);
    }


    /**
     * {@inheritDoc}
     *
     * @see com.github.technosf.posterer.core.models.impl.base.AbstractRequestModel#createRequest(int,
     *      com.github.technosf.posterer.utils.Auditor, int,
     *      com.github.technosf.posterer.core.models.Request,
     *      com.github.technosf.posterer.core.models.Proxy,
     *      com.github.technosf.posterer.core.models.impl.KeyStoreBean,
     *      java.lang.String)
     */
    @Override
    protected CommonsResponseModelTaskImpl createRequest(int requestId,
            final Auditor auditor, int timeout,
            final Request request,
            final Proxy proxy)
    {
        CallConfigBean callconfig =
                createCallConfig(auditor, request.getSecurity(), proxy);

        return new CommonsResponseModelTaskImpl(requestId, auditor,
                callconfig.builder,
                timeout,
                request, callconfig.neededClientAuth);
    }


    /**
     * {@inheritDoc}
     *
     * @see com.github.technosf.posterer.core.models.impl.base.AbstractRequestModel#createRequest(int,
     *      com.github.technosf.posterer.utils.Auditor, int,
     *      com.github.technosf.posterer.core.models.Request,
     *      com.github.technosf.posterer.core.models.Proxy,
     *      com.github.technosf.posterer.core.models.impl.KeyStoreBean,
     *      java.lang.String)
     */
    @Override
    protected CommonsResponseModelTaskImpl createRequest(int requestId,
            final Auditor auditor, int timeout,
            final Request request,
            final KeyStoreBean keyStoreBean,
            final String alias)
    {
        CallConfigBean callconfig =
                createCallConfig(auditor, request.getSecurity(), keyStoreBean,
                        alias);

        return new CommonsResponseModelTaskImpl(requestId, auditor,
                callconfig.builder,
                timeout,
                request, callconfig.neededClientAuth);
    }


    /**
     * {@inheritDoc}
     *
     * @see com.github.technosf.posterer.core.models.impl.base.AbstractRequestModel#createRequest(int,
     *      com.github.technosf.posterer.utils.Auditor, int,
     *      com.github.technosf.posterer.core.models.Request,
     *      com.github.technosf.posterer.core.models.Proxy,
     *      com.github.technosf.posterer.core.models.impl.KeyStoreBean,
     *      java.lang.String)
     */
    @Override
    protected CommonsResponseModelTaskImpl createRequest(int requestId,
            final Auditor auditor, int timeout,
            final Request request,
            final Proxy proxy,
            final KeyStoreBean keyStoreBean,
            final String alias)
    {
        CallConfigBean callconfig =
                createCallConfig(auditor, request.getSecurity(), proxy,
                        keyStoreBean,
                        alias);

        return new CommonsResponseModelTaskImpl(requestId, auditor,
                callconfig.builder,
                timeout,
                request, callconfig.neededClientAuth);
    }


    /**
     * Creates a call config for the given ssl impl
     * 
     * @param ssl
     * @return the call config
     */
    private CallConfigBean createCallConfig(Auditor auditor,
            final String ssl)
    {
        BooleanSupplier neededClientAuth = null;
        HttpClientBuilder builder = HttpClients.custom();

        if (!ssl.isEmpty())
        /*
         * Use custom builder
         */
        {
            /*
             * Configure builder
             */
            neededClientAuth =
                    buildInSSL(auditor, builder, ssl);
        }
        return new CallConfigBean(builder, neededClientAuth);
    }


    /**
     * Creates a call config for the given ssl impl and proxy
     * 
     * @param auditor
     * @param string
     * @return the call config
     */
    private CallConfigBean createCallConfig(final Auditor auditor,
            final String ssl,
            final Proxy proxy)
    {
        BooleanSupplier neededClientAuth = null;
        HttpClientBuilder builder = HttpClients.custom();

        if (!ssl.isEmpty() || !proxy.toString().isEmpty())
        /*
         * Use custom builder 
         */
        {
            /*
             * Configure builder
             */
            neededClientAuth =
                    buildInSSL(auditor, builder, ssl);
            buildInProxy(auditor, builder, proxy);
        } // custom builder

        return new CallConfigBean(builder, neededClientAuth);
    }


    /**
     * Creates a call config for the given ssl impl and proxy
     * 
     * @param auditor
     * @param string
     * @return the call config
     */
    private CallConfigBean createCallConfig(final Auditor auditor,
            final String ssl, final KeyStoreBean keyStoreBean,
            final String alias)
    {
        BooleanSupplier neededClientAuth = null;
        HttpClientBuilder builder = HttpClients.custom();

        if (!ssl.isEmpty())
        /*
         * Use custom builder 
         */
        {
            /*
             * Configure builder
             */
            neededClientAuth =
                    buildInSSL(auditor, builder, ssl,
                            keyStoreBean, alias);
        } // custom builder

        return new CallConfigBean(builder, neededClientAuth);
    }


    /**
     * Creates a call config for the given ssl impl and proxy
     * 
     * @param auditor
     * @param string
     * @return the call config
     */
    private CallConfigBean createCallConfig(final Auditor auditor,
            final String ssl,
            final Proxy proxy, final KeyStoreBean keyStoreBean,
            final String alias)
    {
        BooleanSupplier neededClientAuth = null;
        HttpClientBuilder builder = HttpClients.custom();

        if (!ssl.isEmpty() || !proxy.toString().isEmpty())
        /*
         * Use custom builder 
         */
        {
            /*
             * Configure builder
             */
            neededClientAuth = buildInSSL(auditor,
                    builder, ssl, keyStoreBean, alias);
            buildInProxy(auditor, builder, proxy);
        } // custom builder

        return new CallConfigBean(builder, neededClientAuth);
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
    private @Nullable BooleanSupplier buildInSSL(Auditor auditor,
            HttpClientBuilder builder,
            final String ssl, final KeyStoreBean keyStoreBean,
            final String alias)
    {

        builder.setSSLHostnameVerifier(
                new PromiscuousHostnameVerifier(auditor));
        try
        {
            AuditingSSLSocketFactory auditingSSLSocketFactory =
                    new AuditingSSLSocketFactory(auditor, ssl, keyStoreBean,
                            alias);
            builder.setSSLSocketFactory(auditingSSLSocketFactory);
            return auditingSSLSocketFactory.getNeededClientAuthSupplier();
        }
        catch (KeyManagementException | UnrecoverableKeyException e)
        {
            auditor.append(true, CONST_ERR_SSL_KEY).append(false, CONST_FMT,
                    e.getMessage());
        }
        catch (NoSuchAlgorithmException e)
        {
            auditor.append(true, CONST_ERR_SSL_ALGO).append(false,
                    CONST_FMT, e.getMessage());
        }
        catch (KeyStoreException e)
        {
            auditor.append(true, CONST_ERR_SSL_STORE).append(false,
                    CONST_FMT, e.getMessage());
        }
        catch (FileNotFoundException e)
        {
            auditor.append(true, CONST_ERR_SSL_FILE).append(false,
                    CONST_FMT, e.getMessage());
        }
        catch (CertificateException e)
        {
            auditor.append(true, CONST_ERR_SSL_CERT).append(false,
                    CONST_FMT, e.getMessage());
        }
        catch (IOException e)
        {
            auditor.append(true, CONST_ERR_SSL_IO).append(false,
                    CONST_FMT, e.getMessage());
        }
        return null;
    }


    /**
     * Configures builder for the given SSL/TLS version
     * 
     * @param builder
     *            the builder to configure
     * @param ssl
     *            the ssl info
     */
    private @Nullable BooleanSupplier buildInSSL(Auditor auditor,
            HttpClientBuilder builder,
            final String ssl)
    {
        builder.setSSLHostnameVerifier(
                new PromiscuousHostnameVerifier(auditor));
        try
        {
            AuditingSSLSocketFactory auditingSSLSocketFactory =
                    new AuditingSSLSocketFactory(auditor, ssl);
            builder.setSSLSocketFactory(auditingSSLSocketFactory);
            return auditingSSLSocketFactory.getNeededClientAuthSupplier();
        }
        catch (KeyManagementException | UnrecoverableKeyException e)
        {
            auditor.append(true, CONST_ERR_SSL_KEY).append(false, CONST_FMT,
                    e.getMessage());
        }
        catch (NoSuchAlgorithmException e)
        {
            auditor.append(true, CONST_ERR_SSL_ALGO).append(false,
                    CONST_FMT, e.getMessage());
        }
        catch (KeyStoreException e)
        {
            auditor.append(true, CONST_ERR_SSL_STORE).append(false,
                    CONST_FMT, e.getMessage());
        }
        catch (FileNotFoundException e)
        {
            auditor.append(true, CONST_ERR_SSL_FILE).append(false,
                    CONST_FMT, e.getMessage());
        }
        catch (CertificateException e)
        {
            auditor.append(true, CONST_ERR_SSL_CERT).append(false,
                    CONST_FMT, e.getMessage());
        }
        catch (IOException e)
        {
            auditor.append(true, CONST_ERR_SSL_IO).append(false,
                    CONST_FMT, e.getMessage());
        }

        return null;
    }

}
