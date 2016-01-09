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

import com.github.technosf.posterer.models.Request;
import com.github.technosf.posterer.models.ResponseModel;
import com.github.technosf.posterer.utils.Auditor;

import javafx.concurrent.Task;

/**
 * Basic implementation of {@code ResponseModel} common methods as a JavaFX
 * {@code Task}
 * <p>
 * 
 * @author technosf
 * @since 0.0.1
 * @version 0.0.1
 * @param <T>
 *            the response type used by the implementation
 */
public abstract class AbstractResponseModelTask<T>
        extends Task<T>
        implements ResponseModel
{

    protected final int requestId;
    protected final Request request;

    protected @Nullable String body;

    protected @Nullable String headers;

    private long elapsedTimeMilli;

    protected @Nullable T response;
    protected int timeout;

    protected Auditor auditor;


    /**
     * Creates a task to produce a response from the given request.
     * 
     * @param requestId
     *            the request id
     * @param timeout
     * @param requestBean
     *            the request definition bean
     */
    protected AbstractResponseModelTask(final int requestId, Auditor auditor,
            int timeout, final Request request)
    {
        this.requestId = requestId;
        this.auditor = auditor;
        this.timeout = timeout;
        this.request = request;
    }


    /*
     * ------------------------------------------------------------------------
     */

    /**
     * Prepare the client
     */
    protected abstract void prepareClient();


    /**
     * Returns the response
     * 
     * @param auditor
     *            Auditor
     * @return the response bean
     * @throws Exception
     *             could return the response
     */
    protected abstract T getReponse(Auditor auditor) throws Exception;


    /**
     * Process the response
     */
    protected abstract void processResponse();


    /**
     * Has the call completed and been processed?
     * 
     * @return true if the call has been processed
     */
    protected abstract boolean isResponseProcessed();


    /**
     * Close the client
     */
    protected abstract void closeClient();


    /*
     * ------------------------------------------------------------------------
     */

    /**
     * {@inheritDoc}
     * 
     * @see javafx.concurrent.Task#call()
     */
    @Override
    protected final T call() throws Exception
    {
        prepareClient();
        auditor.start();
        try
        {
            return getReponse(auditor);
        }
        finally
        {
            elapsedTimeMilli = auditor.stop();
        }
    }


    /*
     * ------------------------------------------------------------------------
     */

    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.models.ResponseModel#getReferenceId()
     */
    @Override
    public final int getReferenceId()
    {
        return requestId;
    }


    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.models.ResponseModel#getElaspedTimeMilli()
     */
    @Override
    public final long getElaspedTimeMilli()
    {
        return elapsedTimeMilli;
    }


    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.models.ResponseModel#getResponse()
     */
    @SuppressWarnings("null")
    @Override
    public final String getResponse()
    {
        return java.util.Objects.toString(response);
    }


    /**
     * {@inheritDoc}
     *
     * @see com.github.technosf.posterer.models.ResponseModel#getRequest()
     */
    @Override
    public final Request getRequest()
    {
        return request;
    }


    /*
     * ------------------------------------------------------------------------
     */

    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.models.ResponseModel#getHeaders()
     */
    @Override
    public final String getHeaders()
    {
        processResponse();
        if (headers != null)
        {
            return headers;
        }
        return "";
    }


    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.models.ResponseModel#getBody()
     */
    @Override
    public String getBody()
    {
        processResponse();
        if (body != null)
        {
            return body;
        }
        return "";
    }


    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.models.ResponseModel#isComplete()
     */
    @Override
    public final boolean isComplete()
    {
        processResponse();
        return isResponseProcessed();
    }

}
