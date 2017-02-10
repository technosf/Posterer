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
package com.github.technosf.posterer.models.impl;

import static org.apache.commons.lang3.StringEscapeUtils.escapeXml11;
import static org.apache.commons.lang3.StringEscapeUtils.unescapeXml;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.technosf.posterer.models.Proxy;
import com.github.technosf.posterer.models.Request;

/**
 * Implementation of a {@code Request} as a java bean.
 * 
 * @author technosf
 * @since 0.0.1
 * @version 0.0.1
 */
public final class RequestBean
        implements Request
{
    @SuppressWarnings("null")
    private static final Logger LOG = LoggerFactory
            .getLogger(RequestBean.class);

    /*
     * {@code Request} fields
     */
    private String endpoint;

    private BidiMap headers = new DualHashBidiMap();

    private String payload;

    private String method;

    private String security;

    private String contentType;

    private boolean base64;

    /*
     * Session and derived fields
     */
    @Nullable
    private URI uri;

    private int timeout;


    /**
     * Default, blank, {@code RequestBean}
     */
    public RequestBean()
    {
        this.uri = null;
        this.endpoint = "";
        this.payload = "";
        this.method = "";
        this.security = "";
        this.contentType = "";
    }


    /**
     * Instantiation of a {@code RequestBean} as a copy from a {@code Request}
     * implementation.
     * 
     * @param request
     *            the {@code Request} to copy
     */
    public RequestBean(Request request)
    {
        this(request.getEndpoint(),
                request.getHeaders(),
                request.getPayload(),
                request.getMethod(),
                request.getSecurity(),
                request.getContentType(),
                request.getBase64());
    }


    /**
     * Instantiates a bean from component values.
     * 
     * @param endpoint
     * @param payload
     * @param method
     * @param security
     * @param contentType
     * @param base64
     */
    public RequestBean(String endpoint,
            Map<String, String> headers,
            String payload,
            String method,
            String security,
            String contentType,
            Boolean base64,
            Proxy proxy)
    {
        this.endpoint = endpoint;
        this.headers.putAll(headers);
        this.payload = payload;
        this.method = method;
        this.security = security;
        this.contentType = contentType;
        this.base64 = base64;
        this.uri = constructUri(endpoint);
    }


    /**
     * Instantiates a bean from component values.
     * 
     * @param endpoint
     * @param payload
     * @param method
     * @param security
     * @param contentType
     * @param base64
     */
    public RequestBean(String endpoint,
            Map<String, String> headers,
            String payload,
            String method,
            String security,
            String contentType,
            Boolean base64)
    {
        this.endpoint = endpoint;
        this.headers.putAll(headers);
        this.payload = payload;
        this.method = method;
        this.security = security;
        this.contentType = contentType;
        this.base64 = base64;
        this.uri = constructUri(endpoint);
    }


    /* -------------  Sessions and derived Getters ------------------ */

    /**
     * Returns the endpoint as an {@code URI}
     * 
     * @return the endpoint
     */
    @Override
    @Nullable
    public URI getUri()
    {
        return uri;
    }


    /**
     * Returns the timeout set for this {@code Request} in this session
     * 
     * @return the timeout
     */
    public int getTimeout()
    {
        return timeout;
    }


    /* -------------  Request Getters and Setters  ------------------ */

    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.models.Request#getEndpoint()
     */
    @Override
    public String getEndpoint()
    {
        return endpoint;
    }


    /**
     * {@inheritDoc}
     *
     * @see com.github.technosf.posterer.models.Request#getHeaders()
     */
    @SuppressWarnings({ "unchecked" })
    @Override
    public Map<String, String> getHeaders()
    {
        return headers;
    }


    /**
     * @param endpoint
     *            the endpoint to set
     */
    public RequestBean setEndpoint(String endpoint)
    {
        this.endpoint = endpoint;
        this.uri = constructUri(endpoint);
        return this;
    }


    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.models.Request#getPayload()
     */
    @SuppressWarnings("null")
    @Override
    public String getPayload()
    {
        return unescapeXml(payload);
    }


    /**
     * @return
     */
    public String getPayloadRaw()
    {
        return payload;
    }


    /**
     * @param payload
     *            the request to set
     */
    @SuppressWarnings("null")
    public RequestBean setPayload(String payload)
    {
        this.payload = escapeXml11(payload);
        return this;
    }


    /**
     * @param method
     *            the method to set
     */
    public RequestBean setMethod(String method)
    {
        this.method = method;
        return this;
    }


    /**
     * Updates the headers
     * 
     * @param headers
     */
    public void setHeaders(Collection<Entry<String, String>> headers)
    {
        Map<String, String> headermap = new HashMap<>();
        headers.forEach(h -> headermap.put(h.getKey(), h.getValue()));
        setHeaders(headermap);
    }


    /**
     * Updates the headers
     * 
     * @param headers
     */
    public void setHeaders(Map<String, String> headers)
    {
        this.headers.clear();
        this.headers.putAll(headers);
    }


    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.models.Request#getMethod()
     */
    @Override
    public String getMethod()
    {
        return method;
    }


    /**
     * @param method
     *            the method to set
     */
    public RequestBean setSecurity(String security)
    {
        this.security = security;
        return this;
    }


    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.models.Request#getSecurity()
     */
    @Override
    public String getSecurity()
    {
        return security;
    }


    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.models.Request#getContentType()
     */
    @Override
    public String getContentType()
    {
        return contentType;
    }


    /**
     * @param contentType
     *            the contentType to set
     */
    public RequestBean setContentType(String contentType)
    {
        this.contentType = contentType;
        return this;
    }


    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.models.Request#getBase64()
     */
    @Override
    public Boolean getBase64()
    {
        return base64;
    }


    /**
     * @param base64
     *            the base64 to set
     */
    public RequestBean setBase64(boolean base64)
    {
        this.base64 = base64;
        return this;
    }


    /* ------------------  Object functions  ------------------------ */

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return hashCode(this);
    }


    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(@Nullable Object obj)
    {
        if (obj != null)
        {
            return Request.class.isInstance(obj)
                    && hashCode() == hashCode((Request) obj);
        }
        return false;
    }


    /* ----------------  Helpers  ---------------------- */

    /**
     * Returns a copy of the current bean.
     * 
     * @return a copy of the bean
     */
    public RequestBean copy()
    {
        return new RequestBean(this);
    }


    /**
     * Test for {@code Request} actionability.
     * 
     * @return True if {@code Request} can sent via HTTP
     */
    @Override
    public boolean isActionable()
    {
        return isActionable(this);
    }


    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#toString()
     */
    @Override
    @SuppressWarnings("null")
    public final String toString()
    {
        return String.format("%1$s\n%2$s\n%3$s\n%4$s\n%5$s\n%6$s",
                hashCode(this), getEndpoint(), getHeaders(),
                //request.getPayload(),
                getMethod(),
                getSecurity(),
                getContentType(),
                getBase64());
    }


    /* ----------------  Static  ----------------------*/

    /**
     * Test for {@code Request} actionability.
     * 
     * @return True if {@code Request} can sent via HTTP
     */
    public static boolean isActionable(final @Nullable Request request)
    {
        if (request == null)
        {
            return false;
        }

        return isNotBlank(request.getEndpoint())
                && isNotBlank(request.getMethod())
                && isNotBlank(request.getContentType());
    }


    /**
     * Create a formatted {@code String} object for the {@code Request}
     * 
     * @param format
     *            The format to apply to the {@code Request} components
     * @return The {@code Request} as a {@code String}
     */
    @SuppressWarnings("null")
    public static String toString(final @Nullable String format,
            final @Nullable Request request)
    {
        if (format == null || request == null)
        {
            return "";
        }

        return String.format(format, // FIXME Headers
                request.getEndpoint(),
                request.getPayload(),
                request.getMethod(),
                request.getSecurity(),
                request.getContentType(),
                request.getBase64());
    }


    /**
     * Create a hashcode for the {@code Request}
     * 
     * @param request
     * @return
     */
    private static int hashCode(final @Nullable Request request)
    {
        if (request == null)
        {
            return 0;
        }

        return Objects.hash( // FIXME Headers
                Objects.toString(request.getEndpoint()),
                request.getHeaders(),
                Objects.toString(request.getPayload()),
                Objects.toString(request.getMethod()),
                Objects.toString(request.getSecurity()),
                Objects.toString(request.getContentType()),
                Objects.toString(request.getBase64()));
    }


    /**
     * Returns the given endpoint as an {@code URI} object.
     * 
     * @param endpoint
     *            the endpoint
     * @return the {@code URI} of the endpoint
     */
    @Nullable
    public static URI constructUri(final @Nullable String endpoint)
    {
        URI uri = null;

        try
        {
            uri = new URI(endpoint);
        }
        catch (URISyntaxException | NullPointerException e)
        {
            LOG.debug("Bad endpoint: {}", endpoint);
        }

        return uri;
    }

}
