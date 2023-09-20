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
package com.github.technosf.posterer.models.impl.base;

import org.eclipse.jdt.annotation.Nullable;

import com.github.technosf.posterer.models.Request;
import com.github.technosf.posterer.models.ResponseModel;
import com.github.technosf.posterer.utils.Auditor;

import javafx.concurrent.Task;

/**
 * Basic implementation of {@code ResponseModel} common methods as a JavaFX
 * background executable {@code Task}
 * <p>
 * Specific implementation of the request/response calls is left for the
 * concrete class.
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

    /**
     * The request identifier
     */
    protected final int requestId;

    /**
     * The request itself
     */
    protected final Request request;
    
    /**
     * Request status
     */
    protected @Nullable String status;

    /**
     * The response headers
     */
    protected @Nullable String responseHeaders;

    /**
     * The response body
     */
    protected @Nullable String responseBody;

    /**
     * The response
     */
    protected @Nullable T response;

    /**
     * The call auditor
     */
    protected Auditor auditor;

    /**
     * The call timeout
     */
    protected int timeout;

    /**
     * the call elapsed time in millis
     */
    private long elapsedTimeMilli;


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
     * Abstract calls
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
     * Process the response if it has not already been processed
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
     * Task calls
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
     * ResponseModel calls
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


    /**
     * {@inheritDoc}
     *
     * @see com.github.technosf.posterer.models.ResponseModel#getStatus()
     */
    @Override
    public String getStatus()
    {
    	processResponse();
        return status;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.models.ResponseModel#getHeaders()
     */
    @Override
    public final String getHeaders()
    {
        processResponse();

        return (responseHeaders == null) ? "" : responseHeaders;

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
        
        return (responseBody == null) ? "" : responseBody;

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
