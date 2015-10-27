/*
 * Copyright 2014 technosf [https://github.com/technosf]
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
package com.github.technosf.posterer.ui.models.base;

import com.github.technosf.posterer.modules.RequestBean;
import com.github.technosf.posterer.ui.models.RequestModel;
import com.github.technosf.posterer.ui.models.ResponseModel;

/**
 * Abstract implementation of base {@code RequestModel} functions
 * 
 * @author technosf
 * @since 0.0.1
 * @version 0.0.1
 * @param <T>
 *            The implementing type for the Response
 */
public abstract class AbstractRequestModel<T extends ResponseModel>
        implements RequestModel
{

    /**
     * System Proxy property strings
     */
    private static final String KEY_HTTP_PROXY_SET = "http.proxySet";
    private static final String KEY_HTTP_PROXY_HOST = "http.proxyHost";
    private static final String KEY_HTTP_PROXY_PORT = "http.proxyPort";
    private static final String KEY_HTTP_PROXY_USER = "http.proxyUser";
    private static final String KEY_HTTP_PROXY_PASSWORD = "http.proxyPassword";

    /**
     * Request counter
     */
    protected static int requestId = 0;
    protected int timeout;


    /*
     * (non-Javadoc)
     * 
     * @see com.github.technosf.posterer.models.RequestModel#doRequest(java.net.URI,
     * java.lang.String, java.lang.String, int, boolean, java.lang.String, java.lang.String)
     */
    // @Override
    // public ResponseModel doRequest(URI uri, int timeout,
    // String method,
    // String contentType,
    // boolean encode,
    // String user,
    // String password)
    // {
    // return createRequest(++requestId, uri, timeout,
    // method,
    // contentType,
    // encode,
    // user,
    // password);
    // }

    /**
     * {@inheritDoc}
     *
     * @see com.github.technosf.posterer.ui.models.RequestModel#doRequest(com.github.technosf.posterer.modules.RequestBean)
     */
    @Override
    public ResponseModel doRequest(final RequestBean requestBean)
    {
        return createRequest(++requestId, requestBean);
    }


    /**
     * Generates a HTTP request object per the implementing framework and
     * returns the resulting
     * object back to be managed by the Response code.
     * 
     * @param requestId
     *            a unique Request identifier
     * @param uri
     *            the endpoint URI
     * @param method
     *            the HTTP method
     * @param contentType
     *            the mime content type
     * @param timeout
     *            the timeout
     * @param encode
     *            base64 encoding flag
     * @param user
     *            http authentication user
     * @param password
     *            http authentication password
     * @return the implementing HTTP framework encapsulation of the request and
     *         response
     *         //
     */
    //    protected abstract T createRequest(int requestId, URI uri, int timeout,
    //            String method,
    //            String contentType,
    //            boolean encode,
    //            String user,
    //            String password);

    /**
     * Create a request and produce a response
     * 
     * @param requestId
     *            the request unique identifier
     * @param requestBean
     *            the request bean
     * @return the response bean
     */
    protected abstract T createRequest(int requestId,
            final RequestBean requestBean);


    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.ui.models.RequestModel#setTimeout(int)
     */
    @Override
    public final void setTimeout(int timeout)
    {
        this.timeout = timeout;
    }


    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.ui.models.RequestModel#getTimeout()
     */
    @Override
    public final int getTimeout()
    {
        return timeout;
    }


    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.ui.models.RequestModel#getUseProxy()
     */
    @Override
    public final boolean getUseProxy()
    {
        return Boolean.parseBoolean(System.getProperty(KEY_HTTP_PROXY_SET));
    }


    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.ui.models.RequestModel#setUseProxy(boolean)
     */
    @Override
    public final void setUseProxy(boolean flag)
    {
        System.setProperty(KEY_HTTP_PROXY_SET, Boolean.toString(flag));
    }


    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.ui.models.RequestModel#getProxyHost()
     */
    //@Nullable
    @Override
    public final String getProxyHost()
    {
        return System.getProperty(KEY_HTTP_PROXY_HOST);
    }


    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.ui.models.RequestModel#setProxyHost(java.lang.String)
     */
    @Override
    public final void setProxyHost(final String host)
    {
        System.setProperty(KEY_HTTP_PROXY_HOST,
                java.util.Objects.toString(host, ""));
    }


    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.ui.models.RequestModel#getProxyPort()
     */
    //@Nullable
    @Override
    public final String getProxyPort()
    {
        return System.getProperty(KEY_HTTP_PROXY_PORT);
    }


    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.ui.models.RequestModel#setProxyPort(java.lang.String)
     */
    @Override
    public final void setProxyPort(final String port)
    {
        System.setProperty(KEY_HTTP_PROXY_PORT,
                java.util.Objects.toString(port, ""));
    }


    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.ui.models.RequestModel#getProxyUser()
     */
    //@Nullable
    @Override
    public final String getProxyUser()
    {
        return System.getProperty(KEY_HTTP_PROXY_USER);
    }


    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.ui.models.RequestModel#setProxyUser(java.lang.String)
     */
    @Override
    public final void setProxyUser(final String user)
    {
        System.setProperty(KEY_HTTP_PROXY_USER,
                java.util.Objects.toString(user, ""));
    }


    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.ui.models.RequestModel#getproxyPass()
     */
    //@Nullable
    @Override
    public final String getProxyPassword()
    {
        return System.getProperty(KEY_HTTP_PROXY_PASSWORD);
    }


    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.ui.models.RequestModel#setProxyPassword(java.lang.String)
     */
    @Override
    public final void setProxyPassword(final String password)
    {
        System.setProperty(KEY_HTTP_PROXY_PASSWORD,
                java.util.Objects.toString(password, ""));
    }
}
