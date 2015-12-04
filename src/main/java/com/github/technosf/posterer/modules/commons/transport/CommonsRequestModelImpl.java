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

import com.github.technosf.posterer.models.Proxy;
import com.github.technosf.posterer.models.Request;
import com.github.technosf.posterer.models.RequestModel;
import com.github.technosf.posterer.models.impl.KeyStoreBean;
import com.github.technosf.posterer.models.impl.base.AbstractRequestModel;
import com.github.technosf.posterer.modules.commons.transport.ssl.AuditingSSLSocketFactory;
import com.github.technosf.posterer.utils.Auditor;
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
     * bean to return multiple assets from builder creation
     */
    private class BuilderBean
    {
        final HttpClientBuilder builder;
        final BooleanSupplier neededClientAuth;


        BuilderBean(HttpClientBuilder builder,
                @Nullable BooleanSupplier neededClientAuth)
        {
            this.builder = builder;
            if (neededClientAuth == null)
            {
                this.neededClientAuth = new BooleanSupplier()
                {

                    @Override
                    public boolean getAsBoolean()
                    {
                        return false;
                    }
                };
            }
            else
            {
                this.neededClientAuth = neededClientAuth;
            }
        }
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
            final Auditor auditor,
            final int timeout, final Request request)
    {
        BuilderBean builderAssets = getBuilder(auditor, request.getSecurity());
        return new CommonsResponseModelTaskImpl(requestId, auditor,
                builderAssets.builder,
                timeout,
                request, builderAssets.neededClientAuth);
    }


    /**
     * {@inheritDoc}
     *
     * @see com.github.technosf.posterer.models.impl.base.AbstractRequestModel#createRequest(int,
     *      com.github.technosf.posterer.utils.Auditor, int,
     *      com.github.technosf.posterer.models.Request,
     *      com.github.technosf.posterer.models.Proxy,
     *      com.github.technosf.posterer.models.impl.KeyStoreBean,
     *      java.lang.String)
     */
    @Override
    protected CommonsResponseModelTaskImpl createRequest(int requestId,
            final Auditor auditor, int timeout,
            final Request request,
            final Proxy proxy)
    {
        BuilderBean builderAssets =
                getBuilder(auditor, request.getSecurity(), proxy);

        return new CommonsResponseModelTaskImpl(requestId, auditor,
                builderAssets.builder,
                timeout,
                request, builderAssets.neededClientAuth);
    }


    /**
     * {@inheritDoc}
     *
     * @see com.github.technosf.posterer.models.impl.base.AbstractRequestModel#createRequest(int,
     *      com.github.technosf.posterer.utils.Auditor, int,
     *      com.github.technosf.posterer.models.Request,
     *      com.github.technosf.posterer.models.Proxy,
     *      com.github.technosf.posterer.models.impl.KeyStoreBean,
     *      java.lang.String)
     */
    @Override
    protected CommonsResponseModelTaskImpl createRequest(int requestId,
            final Auditor auditor, int timeout,
            final Request request,
            final KeyStoreBean keyStoreBean,
            final String alias)
    {
        BuilderBean builderAssets =
                getBuilder(auditor, request.getSecurity(), keyStoreBean,
                        alias);

        return new CommonsResponseModelTaskImpl(requestId, auditor,
                builderAssets.builder,
                timeout,
                request, builderAssets.neededClientAuth);
    }


    /**
     * {@inheritDoc}
     *
     * @see com.github.technosf.posterer.models.impl.base.AbstractRequestModel#createRequest(int,
     *      com.github.technosf.posterer.utils.Auditor, int,
     *      com.github.technosf.posterer.models.Request,
     *      com.github.technosf.posterer.models.Proxy,
     *      com.github.technosf.posterer.models.impl.KeyStoreBean,
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
        BuilderBean builderAssets =
                getBuilder(auditor, request.getSecurity(), proxy, keyStoreBean,
                        alias);

        return new CommonsResponseModelTaskImpl(requestId, auditor,
                builderAssets.builder,
                timeout,
                request, builderAssets.neededClientAuth);
    }


    /**
     * Creates a builder for the given ssl impl
     * 
     * @param ssl
     * @return the builder
     */
    @SuppressWarnings("null")
    private BuilderBean getBuilder(Auditor auditor,
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
        return new BuilderBean(builder, neededClientAuth);
    }


    /**
     * Creates a builder for the given ssl impl and proxy
     * 
     * @param auditor
     * @param string
     * @return the builder
     */
    @SuppressWarnings("null")
    private BuilderBean getBuilder(final Auditor auditor,
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

        return new BuilderBean(builder, neededClientAuth);
    }


    /**
     * Creates a builder for the given ssl impl and proxy
     * 
     * @param auditor
     * @param string
     * @return the builder
     */
    @SuppressWarnings("null")
    private BuilderBean getBuilder(final Auditor auditor,
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

        return new BuilderBean(builder, neededClientAuth);
    }


    /**
     * Creates a builder for the given ssl impl and proxy
     * 
     * @param auditor
     * @param string
     * @return the builder
     */
    @SuppressWarnings("null")
    private BuilderBean getBuilder(final Auditor auditor,
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

        return new BuilderBean(builder, neededClientAuth);
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
            auditor.append(true, "SSL :: Key exception").append(false, "\t%1$s",
                    e.getMessage());
        }
        catch (NoSuchAlgorithmException e)
        {
            auditor.append(true, "SSL :: Algo exception").append(false,
                    "\t%1$s", e.getMessage());
        }
        catch (KeyStoreException e)
        {
            auditor.append(true, "SSL :: Key Store exception").append(false,
                    "\t%1$s", e.getMessage());
        }
        catch (FileNotFoundException e)
        {
            auditor.append(true, "SSL :: File exception").append(false,
                    "\t%1$s", e.getMessage());
        }
        catch (CertificateException e)
        {
            auditor.append(true, "SSL :: Certificate exception").append(false,
                    "\t%1$s", e.getMessage());
        }
        catch (IOException e)
        {
            auditor.append(true, "SSL :: IO exception").append(false,
                    "\t%1$s", e.getMessage());
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
            auditor.append(true, "SSL :: Key exception").append(false, "\t%1$s",
                    e.getMessage());
        }
        catch (NoSuchAlgorithmException e)
        {
            auditor.append(true, "SSL :: Algo exception").append(false,
                    "\t%1$s", e.getMessage());
        }
        catch (KeyStoreException e)
        {
            auditor.append(true, "SSL :: Key Store exception").append(false,
                    "\t%1$s", e.getMessage());
        }
        catch (FileNotFoundException e)
        {
            auditor.append(true, "SSL :: File exception").append(false,
                    "\t%1$s", e.getMessage());
        }
        catch (CertificateException e)
        {
            auditor.append(true, "SSL :: Certificate exception").append(false,
                    "\t%1$s", e.getMessage());
        }
        catch (IOException e)
        {
            auditor.append(true, "SSL :: IO exception").append(false,
                    "\t%1$s", e.getMessage());
        }

        return null;
    }

}
