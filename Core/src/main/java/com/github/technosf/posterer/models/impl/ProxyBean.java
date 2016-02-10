/*
 * Copyright 2015 technosf [https://github.com/technosf]
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

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

import java.util.Objects;

import org.eclipse.jdt.annotation.Nullable;

import com.github.technosf.posterer.models.Proxy;

/**
 * Implementation of a {@code Proxy} as a java bean.
 * 
 * @author technosf
 * @since 0.0.1
 * @version 0.0.1
 */
public final class ProxyBean
        implements Proxy
{
    /*
     * {@code Proxy} fields
     */
    private String proxyHost = "";

    private String proxyPort = "";

    private String proxyUser = "";

    private String proxyPassword = "";

    // toString precalc
    private final StringBuilder sb = new StringBuilder();


    /**
     * Default, blank, {@code RequestBean}
     */
    public ProxyBean()
    {
        reset();
    }


    /**
     * Instantiation of a {@code RequestBean} as a copy from a {@code Request}
     * implementation.
     * 
     * @param propertiesData
     *            the {@code Request} to copy
     */
    public ProxyBean(Proxy propertiesData)
    {
        this(propertiesData.getProxyHost(),
                propertiesData.getProxyPort(),
                propertiesData.getProxyUser(),
                propertiesData.getProxyPassword());
    }


    /**
     * Instantiates a bean from component values.
     * 
     * @param proxyHost
     * @param proxyPort
     * @param proxyUser
     * @param proxyPassword
     */
    @SuppressWarnings("null")
    public ProxyBean(@Nullable String proxyHost, @Nullable String proxyPort,
            @Nullable String proxyUser,
            @Nullable String proxyPassword)
    {
        this.proxyHost = trimToEmpty(proxyHost);
        this.proxyPort = trimToEmpty(proxyPort);
        this.proxyUser = trimToEmpty(proxyUser);
        this.proxyPassword = trimToEmpty(proxyPassword);
        update();
    }


    /**
     * Update state stuff on proxy info change
     */
    private void update()
    {
        sb.setLength(0);
        sb.append(proxyHost);
        if (proxyPort.isEmpty())
            return;
        sb.append(":").append(proxyPort);
        if (proxyUser.isEmpty())
            return;
        sb.append("   ").append(proxyUser);
        if (proxyPassword.isEmpty())
            return;
        sb.append("/").append(proxyPassword);
    }


    /* ------------- Request Getters and Setters ------------------ */

    /**
     * {@inheritDoc}
     *
     * @see com.github.technosf.posterer.models.Proxy#getProxyHost()
     */
    @Override
    public String getProxyHost()
    {
        return proxyHost;
    }


    /**
     * Sets the HTTP proxy host
     * 
     * @param host
     *            the proxy host
     */
    @SuppressWarnings("null")
    public void setProxyHost(String proxyHost)
    {
        this.proxyHost = trimToEmpty(proxyHost);
        update();
    }


    /**
     * {@inheritDoc}
     *
     * @see com.github.technosf.posterer.models.Proxy#getProxyPort()
     */
    @Override
    public String getProxyPort()
    {
        return proxyPort;
    }


    /**
     * Sets the HTTP proxy
     * 
     * @param port
     *            the proxy port
     */
    @SuppressWarnings("null")
    public void setProxyPort(String proxyPort)
    {
        this.proxyPort = trimToEmpty(proxyPort);
        update();
    }


    /**
     * {@inheritDoc}
     *
     * @see com.github.technosf.posterer.models.Proxy#getProxyUser()
     */
    @Override
    public String getProxyUser()
    {
        return proxyUser;
    }


    /**
     * Sets the HTTP proxy authentication user
     * 
     * @param user
     *            the proxy authentication user
     */
    @SuppressWarnings("null")
    public void setProxyUser(String proxyUser)
    {
        this.proxyUser = trimToEmpty(proxyUser);
        update();
    }


    /**
     * {@inheritDoc}
     *
     * @see com.github.technosf.posterer.models.Proxy#getProxyPassword()
     */
    @Override
    public String getProxyPassword()
    {
        return proxyPassword;
    }


    /**
     * Sets the HTTP proxy authentication password
     * 
     * @param password
     *            the proxy authentication password
     */
    @SuppressWarnings("null")
    public void setProxyPassword(String proxyPassword)
    {
        this.proxyPassword = trimToEmpty(proxyPassword);
        update();
    }


    /* ------------------ Object functions ------------------------ */

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
            return Proxy.class.isInstance(obj)
                    && hashCode() == hashCode((Proxy) obj);
        }
        return false;
    }


    /**
     * {@inheritDoc}
     *
     * @see com.github.technosf.posterer.models.Proxy#isActionable()
     */
    @Override
    public boolean isActionable()
    {
        return isActionable(this);
    }


    /* ---------------- Helpers ---------------------- */

    /**
     * Resets the values
     */
    public void reset()
    {
        proxyHost = "";
        proxyPort = "";
        proxyUser = "";
        proxyPassword = "";
        update();
    }


    /**
     * Resets the values
     * 
     * @param proxyHost
     * @param proxyPort
     * @param proxyUser
     * @param proxyPassword
     */
    @SuppressWarnings("null")
    public void reset(String proxyHost, String proxyPort, String proxyUser,
            String proxyPassword)
    {
        this.proxyHost = trimToEmpty(proxyHost);
        this.proxyPort = trimToEmpty(proxyPort);
        this.proxyUser = trimToEmpty(proxyUser);
        this.proxyPassword = trimToEmpty(proxyPassword);
        update();
    }


    /**
     * Returns a copy of the current bean.
     * 
     * @return a copy of the bean
     */
    public ProxyBean copy()
    {
        return new ProxyBean(this);
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
        return sb.toString();
    }


    /* ---------------- Static ---------------------- */

    /**
     * Test for {@code Proxy} actionability.
     * 
     * @return True if {@code Proxy} can be used
     */
    public static boolean isActionable(final @Nullable Proxy proxy)
    {
        if (proxy == null)
        {
            return false;
        }

        return isNotBlank(proxy.getProxyHost())
                && isNotBlank(proxy.getProxyPort());
    }


    /**
     * Create a formatted {@code String} object for the {@code Proxy}
     * 
     * @param format
     *            The format to apply to the {@code Proxy} components
     * @return The {@code Proxy} as a {@code String}
     */
    // @SuppressWarnings("null")
    @SuppressWarnings("null")
    public static String toString(final @Nullable String format,
            final @Nullable Proxy proxy)
    {
        if (format == null || proxy == null)
        {
            return "";
        }

        return String.format(format, proxy.getProxyHost(), proxy.getProxyPort(),
                proxy.getProxyUser(),
                proxy.getProxyPassword());
    }


    /**
     * Create a hashcode for the {@code Proxy}
     * 
     * @param proxy
     * @return
     */
    private static int hashCode(final @Nullable Proxy proxy)
    {
        if (proxy == null)
        {
            return 0;
        }

        return Objects.hash(Objects.toString(proxy.getProxyHost()),
                Objects.toString(proxy.getProxyPort()),
                Objects.toString(proxy.getProxyUser()),
                Objects.toString(proxy.getProxyPassword()));
    }

}
