/*
 * Copyright 2018 technosf [https://github.com/technosf]
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
package com.github.technosf.posterer.core.models.impl.base;

import com.github.technosf.posterer.core.models.Proxy;
import com.github.technosf.posterer.core.models.Request;
import com.github.technosf.posterer.core.models.RequestModel;
import com.github.technosf.posterer.core.models.ResponseModel;
import com.github.technosf.posterer.core.models.impl.KeyStoreBean;
import com.github.technosf.posterer.core.utils.Auditor;

/**
 * Abstract implementation of base {@code RequestModel} functions
 * <p>
 * Timeout is designed to be maintained in state with the
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
     * {@inheritDoc}
     *
     * @see com.github.technosf.posterer.core.models.RequestModel#doRequest(com.github.technosf.posterer.core.models.impl.RequestBean)
     */
    @Override
    public ResponseModel doRequest(final Request request)
    {
        return createRequest(++requestId, new Auditor(), timeout, request);
    }


    /**
     * {@inheritDoc}
     *
     * @see com.github.technosf.posterer.core.models.RequestModel#doRequest(com.github.technosf.posterer.core.models.Request,
     *      com.github.technosf.posterer.core.models.Proxy)
     */
    @Override
    public ResponseModel doRequest(final Request request, final Proxy proxy)
    {
        return createRequest(++requestId, new Auditor(), timeout, request,
                proxy);
    }


    /**
     * {@inheritDoc}
     *
     * @see com.github.technosf.posterer.core.models.RequestModel#doRequest(com.github.technosf.posterer.core.models.Request,
     *      com.github.technosf.posterer.core.models.impl.KeyStoreBean,
     *      java.lang.String)
     */
    @Override
    public ResponseModel doRequest(final Request request,
            final KeyStoreBean keyStoreBean, final String alias)
    {
        return createRequest(++requestId, new Auditor(), timeout, request,
                keyStoreBean, alias);
    }


    /**
     * {@inheritDoc}
     *
     * @see com.github.technosf.posterer.core.models.RequestModel#doRequest(com.github.technosf.posterer.core.models.Request,
     *      com.github.technosf.posterer.core.models.Proxy,
     *      com.github.technosf.posterer.core.models.impl.KeyStoreBean,
     *      java.lang.String)
     */
    @Override
    public ResponseModel doRequest(final Request request, final Proxy proxy,
            final KeyStoreBean keyStoreBean, final String alias)
    {
        return createRequest(++requestId, new Auditor(), timeout, request,
                proxy,
                keyStoreBean, alias);
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
    protected abstract T createRequest(int requestId, Auditor auditor,
            int timeout,
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
    protected abstract T createRequest(int requestId, Auditor auditor,
            int timeout,
            final Request request, final Proxy proxy);


    /**
     * Create a request and produce a response
     * 
     * @param requestId
     *            the request unique identifier
     * @param timeout
     *            the timeout in seconds
     * @param request
     *            the request
     * @param keyStoreBean
     *            the certificate store
     * @param alias
     *            the alias of the certificate to use
     * @return the response bean
     */
    protected abstract T createRequest(int requestId, Auditor auditor,
            int timeout,
            final Request request,
            final KeyStoreBean keyStoreBean,
            final String alias);


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
     * @param keyStoreBean
     *            the certificate store
     * @param alias
     *            the alias of the certificate to use
     * @return the response bean
     */
    protected abstract T createRequest(int requestId, Auditor auditor,
            int timeout,
            final Request request, final Proxy proxy,
            final KeyStoreBean keyStoreBean,
            final String alias);


    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.core.models.RequestModel#setTimeout(int)
     */
    @Override
    public final void setTimeout(int timeout)
    {
        this.timeout = timeout;
    }


    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.core.models.RequestModel#getTimeout()
     */
    @Override
    public final int getTimeout()
    {
        return timeout;
    }

}
