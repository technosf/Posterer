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

import com.github.technosf.posterer.models.Proxy;

/**
 * Implementation of a {@code Proxy} as a java bean.
 * 
 * @author technosf
 * @since 0.0.1
 * @version 0.0.1
 */
public final class ProxyBean implements Proxy
{
	/*
	 * {@code Proxy} fields
	 */
	private String proxyHost;

	private String proxyPort;

	private String proxyUser;

	private String proxyPassword;

	/**
	 * Default, blank, {@code RequestBean}
	 */
	public ProxyBean()
	{
		this.reset();
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
		this(propertiesData.getProxyHost(), propertiesData.getProxyPort(), propertiesData.getProxyUser(),
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
	public ProxyBean(String proxyHost, String proxyPort, String proxyUser, String proxyPassword)
	{
		this.proxyHost = trimToEmpty(proxyHost);
		this.proxyPort = trimToEmpty(proxyPort);
		this.proxyUser = trimToEmpty(proxyUser);
		this.proxyPassword = trimToEmpty(proxyPassword);
	}

	/* ------------- Request Getters and Setters ------------------ */

	/**
	 * @return the proxy address
	 */
	@Override
	public String getProxyHost()
	{
		return proxyHost;
	}

	/**
	 * @param proxyHost
	 *            the proxyAddress to set
	 */
	public void setProxyHost(String proxyHost)
	{
		this.proxyHost = trimToEmpty(proxyHost);
	}

	/**
	 * @return the proxy address
	 */
	@Override
	public String getProxyPort()
	{
		return proxyPort;
	}

	/**
	 * @param proxyHost
	 *            the proxyAddress to set
	 */
	public void setProxyPort(String proxyPort)
	{
		this.proxyPort = trimToEmpty(proxyPort);
	}

	/**
	 * @return the httpUser
	 */
	@Override
	public String getProxyUser()
	{
		return proxyUser;
	}

	/**
	 * @param proxyUser
	 *            the proxyUser to set
	 */
	public void setProxyUser(String proxyUser)
	{
		this.proxyUser = trimToEmpty(proxyUser);
	}

	/**
	 * @return the proxyPassword
	 */
	@Override
	public String getProxyPassword()
	{
		return proxyPassword;
	}

	/**
	 * @param proxyPassword
	 *            the proxyPassword to set
	 */
	public void setProxyPassword(String proxyPassword)
	{
		this.proxyPassword = trimToEmpty(proxyPassword);
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
	public boolean equals(Object obj)
	{
		if (obj != null)
		{
			return Proxy.class.isInstance(obj) && hashCode() == hashCode((Proxy) obj);
		}
		return false;
	}

	/* ---------------- Helpers ---------------------- */

	/**
	 * Resets the values
	 */
	public void reset()
	{
		this.proxyHost = "";
		this.proxyPort = "";
		this.proxyUser = "";
		this.proxyPassword = "";
	}

	/**
	 * Resets the values
	 * 
	 * @param proxyHost
	 * @param proxyPort
	 * @param proxyUser
	 * @param proxyPassword
	 */
	public void reset(String proxyHost, String proxyPort, String proxyUser, String proxyPassword)
	{
		this.proxyHost = trimToEmpty(proxyHost);
		this.proxyPort = trimToEmpty(proxyPort);
		this.proxyUser = trimToEmpty(proxyUser);
		this.proxyPassword = trimToEmpty(proxyPassword);
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
	 * Test for {@code Request} actionability.
	 * 
	 * @return True if {@code Request} can sent via HTTP
	 */
	public boolean isActionable()
	{
		return isActionable(this);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see java.lang.Object#toString()
	 */
	public final String toString()
	{
		return String.format("%1$s:%2$s;    %3$s / %4$s", getProxyHost(), getProxyPort(),
				getProxyUser(), getProxyPassword());
	}

	/* ---------------- Static ---------------------- */

	/**
	 * Test for {@code Proxy} actionability.
	 * 
	 * @return True if {@code Proxy} can be used
	 */
	public static boolean isActionable(final Proxy proxy)
	{
		if (proxy == null)
		{
			return false;
		}

		return isNotBlank(proxy.getProxyHost()) && isNotBlank(proxy.getProxyPort());
	}

	/**
	 * Create a formatted {@code String} object for the {@code Proxy}
	 * 
	 * @param format
	 *            The format to apply to the {@code Proxy} components
	 * @return The {@code Proxy} as a {@code String}
	 */
	// @SuppressWarnings("null")
	public static String toString(final String format, final Proxy proxy)
	{
		if (format == null || proxy == null)
		{
			return "";
		}

		return String.format(format, proxy.getProxyHost(), proxy.getProxyPort(), proxy.getProxyUser(),
				proxy.getProxyPassword());
	}

	/**
	 * Create a hashcode for the {@code Proxy}
	 * 
	 * @param proxy
	 * @return
	 */
	private static int hashCode(final Proxy proxy)
	{
		if (proxy == null)
		{
			return 0;
		}

		return Objects.hash(Objects.toString(proxy.getProxyHost()), Objects.toString(proxy.getProxyPort()),
				Objects.toString(proxy.getProxyUser()), Objects.toString(proxy.getProxyPassword()));
	}

}
