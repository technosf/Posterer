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
package com.github.technosf.posterer.models.impl.base;

import org.eclipse.jdt.annotation.Nullable;

import com.github.technosf.posterer.models.Proxy;
import com.github.technosf.posterer.models.Request;
import com.github.technosf.posterer.models.RequestModel;
import com.github.technosf.posterer.models.ResponseModel;

/**
 * Abstract implementation of base {@code RequestModel} functions
 * <p>
 * Timeout and proxy are designed to be maintained in state with the
 * implementing event driven container, whereas the request provided once when
 * fired.
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
     * Request counter
     */
    protected static int requestId = 0;

    /*
     * Default timeout
     */
    protected int timeout = 30;

    /**
     * 
     */
    boolean useProxy = false;

    /**
     * 
     */
    @Nullable
    protected Proxy proxy;


    /**
     * {@inheritDoc}
     *
     * @see com.github.technosf.posterer.models.RequestModel#doRequest(com.github.technosf.posterer.models.impl.RequestBean)
     */
    @Override
    public ResponseModel doRequest(final Request request)
    {
        if (useProxy && proxy != null)
        {
            return createRequest(++requestId, timeout, request, proxy);
        }
        else
        {
            return createRequest(++requestId, timeout, request);
        }
    }


    /**
     * Create a request and produce a response
     * 
     * @param requestId
     *            the request unique identifier
     * @param timeout
     *            the timeout in seconds
     * @param request
     *            the request
     * @return the response bean
     */
    protected abstract T createRequest(int requestId, int timeout,
            final Request request);


    /**
     * Create a request and produce a response
     * 
     * @param requestId
     *            the request unique identifier
     * @param timeout
     *            the timeout in seconds
     * @param request
     *            the request
     * @param proxy
     *            use this proxy
     * @return the response bean
     */
    protected abstract T createRequest(int requestId, int timeout,
            final Request request, final Proxy proxy);


    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.models.RequestModel#setTimeout(int)
     */
    @Override
    public final void setTimeout(int timeout)
    {
        this.timeout = timeout;
    }


    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.models.RequestModel#getTimeout()
     */
    @Override
    public final int getTimeout()
    {
        return timeout;
    }


    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.models.RequestModel#getUseProxy()
     */
    @Override
    public final boolean getUseProxy()
    {
        return useProxy;
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * com.github.technosf.posterer.models.RequestModel#setUseProxy(boolean)
     */
    public final void setUseProxy(boolean useProxy)
    {
        this.useProxy = useProxy;
    }


    /*
     * (non-Javadoc)
     * 
     * @see
     * com.github.technosf.posterer.models.RequestModel#setProxy(com.github.
     * technosf.posterer.models.Proxy)
     */
    public final void setProxy(final Proxy proxy)
    {
        this.proxy = proxy;
    }

}
