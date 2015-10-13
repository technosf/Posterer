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

import java.util.concurrent.TimeUnit;

import com.github.technosf.posterer.modules.RequestBean;
import com.github.technosf.posterer.ui.models.ResponseModel;

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
    protected final RequestBean requestBean;

    //@Nullable
    protected String body;

    //@Nullable
    protected String headers;

    private long elapsedTimeMilli;

    //@Nullable
    protected T response;


    /**
     * Creates a task to produce a response from the given request.
     * 
     * @param requestId
     *            the request id
     * @param requestBean
     *            the request definition bean
     */
    protected AbstractResponseModelTask(final int requestId,
            final RequestBean requestBean)
    {
        this.requestId = requestId;
        this.requestBean = requestBean;
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
     * @return the response bean
     * @throws Exception
     *             could return the response
     */
    protected abstract T getReponse() throws Exception;


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
        T response = null;

        prepareClient();

        long startTime = System.nanoTime();

        try
        {
            response = getReponse();
        }
        finally
        {

            elapsedTimeMilli =
                    TimeUnit.NANOSECONDS
                            .toMillis(System.nanoTime() - startTime);
        }

        return response;
    }


    /*
     * ------------------------------------------------------------------------
     */

    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.ui.models.ResponseModel#getReferenceId()
     */
    @Override
    public final int getReferenceId()
    {
        return requestId;
    }


    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.ui.models.ResponseModel#getElaspedTimeMilli()
     */
    @Override
    public final long getElaspedTimeMilli()
    {
        return elapsedTimeMilli;
    }


    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.ui.models.ResponseModel#getResponse()
     */
    // @SuppressWarnings("null")
    @Override
    public final String getResponse()
    {
        return java.util.Objects.toString(response);
    }


    /**
     * {@inheritDoc}
     *
     * @see com.github.technosf.posterer.ui.models.ResponseModel#getRequestBean()
     */
    @Override
    public final RequestBean getRequestBean()
    {
        return requestBean;
    }


    /*
     * ------------------------------------------------------------------------
     */

    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.ui.models.ResponseModel#getHeaders()
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
     * @see com.github.technosf.posterer.ui.models.ResponseModel#getBody()
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
     * @see com.github.technosf.posterer.ui.models.ResponseModel#isComplete()
     */
    @Override
    public final boolean isComplete()
    {
        processResponse();
        return isResponseProcessed();
    }

}
