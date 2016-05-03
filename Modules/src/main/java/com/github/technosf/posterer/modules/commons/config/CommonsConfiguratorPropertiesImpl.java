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
package com.github.technosf.posterer.modules.commons.config;

import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.SubnodeConfiguration;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.tree.xpath.XPathExpressionEngine;
import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.technosf.posterer.models.Properties;
import com.github.technosf.posterer.models.Proxy;
import com.github.technosf.posterer.models.Request;
import com.github.technosf.posterer.models.impl.ProxyBean;
import com.github.technosf.posterer.models.impl.RequestBean;
import com.github.technosf.posterer.models.impl.base.AbstractPropertiesModel;
import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * Basic implementation of {@code PreferencesModel} using Apache Commons
 * Configurator
 * <p>
 * Properties are saved in XML format.
 * 
 * @author technosf
 * @since 0.0.1
 * @version 0.0.1
 */
public final class CommonsConfiguratorPropertiesImpl
        extends AbstractPropertiesModel
        implements Properties
{

    @SuppressWarnings("null")
    private static final Logger LOG =
            LoggerFactory.getLogger(CommonsConfiguratorPropertiesImpl.class);

    /**
     * Default properties prefix
     */
    private final static String PROP_DEFAULT = "default";

    /**
     * Request properties prefix
     */
    private final static String PROP_REQUESTS = "requests";
    private final static String PROP_REQUESTS_REQUEST =
            PROP_REQUESTS + "/request";
    private final static String PROP_REQUESTS_REQUEST_ID =
            PROP_REQUESTS_REQUEST + "@id";
    private final static String PROP_REQUESTS_REQUEST_ID_QUERY =
            PROP_REQUESTS_REQUEST + "[@id='%1$s']";

    /**
     * Proxy properties prefix
     */
    private final static String PROP_PROXIES = "proxies";
    private final static String PROP_PROXIES_PROXY = PROP_PROXIES + "/proxy";
    private final static String PROP_PROXIES_PROXY_ID =
            PROP_PROXIES_PROXY + "@id";
    private final static String PROP_PROXIES_PROXY_ID_QUERY =
            PROP_PROXIES_PROXY + "[@id='%1$s']";

    /**
     * KeyStore properties prefix
     */
    private final static String PROP_KEYSTORES = "keystores";
    private final static String PROP_KEYSTORES_KEYSTORE =
            PROP_KEYSTORES + "/keystore";

    /**
     * A blank properties file template
     */
    final static String TEMPLATE =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><configuration><"
                    + PROP_DEFAULT + "/><" + PROP_REQUESTS + "/><"
                    + PROP_PROXIES + "/><" + PROP_KEYSTORES
                    + "/></configuration>";

    /**
     * XML configuration
     */
    private final XMLConfiguration config;


    /**
     * Constructor and injection point for <b>Guice</b>
     * <p>
     * 
     * @param prefix
     * @throws IOException
     * @throws ConfigurationException
     */
    @Inject
    public CommonsConfiguratorPropertiesImpl(
            @Named("PropertiesPrefix") final String prefix)
            throws IOException, ConfigurationException
    {
        super(prefix);

        if (!propsFile.exists()
                || FileUtils.sizeOf(propsFile) < TEMPLATE.length())
        /*
         * Create a blank properties file if it does not exist
         */
        {
            FileUtils.writeStringToFile(propsFile, TEMPLATE,
                    Charset.defaultCharset());
        }

        /*
         * Set reload strategy
         */
        FileChangedReloadingStrategy strategy =
                new FileChangedReloadingStrategy();
        strategy.setRefreshDelay(5000);

        /*
         * Load the properties file
         */
        config = new XMLConfiguration(propsFile);
        config.setExpressionEngine(new XPathExpressionEngine());
        config.setReloadingStrategy(strategy);

        /*
         * Load up saved requests
         */
        initializeRequestSet();
        initializeProxySet();
        initializeKeyStoreSet();
        if (isDirty())
            save();

    }


    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.models.Properties#getBasicPropertiesFileContent()
     */
    @Override
    public String getBasicPropertiesFileContent()
    {
        return TEMPLATE;
    }


    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.models.Properties#addData(com.github.technosf.posterer.models.Properties.impl.PropertiesModel.Request)
     */
    @Override
    public boolean addData(final @Nullable Request request)
    {
        boolean result = false;

        if (request != null)
        {
            RequestBean pdi = new RequestBean(request);
            if (pdi.isActionable() && (result =
                    putIfAbsent(pdi)))
            {
                config.addProperty(PROP_REQUESTS_REQUEST_ID, pdi.hashCode());
                SubnodeConfiguration property = getRequest(pdi.hashCode());
                property.addProperty("endpoint", pdi.getEndpoint());
                property.addProperty("payload", pdi.getPayload());
                property.addProperty("method", pdi.getMethod());
                property.addProperty("security", pdi.getSecurity());
                property.addProperty("contentType", pdi.getContentType());
                property.addProperty("base64", pdi.getBase64());
                dirty();
            }
        }

        return result;
    }


    /**
     * Returns the Subnode config for the given request id
     * 
     * @param id
     *            the request id
     * @return the Subnode config holding the request if any
     */
    @SuppressWarnings("null")
    private SubnodeConfiguration getRequest(final int id)
    {
        return config.configurationAt(
                String.format(PROP_REQUESTS_REQUEST_ID_QUERY, id), true);
    }


    /**
     * {@inheritDoc}
     *
     * @see com.github.technosf.posterer.models.Properties#addData(com.github.technosf.posterer.models.Proxy)
     */
    @Override
    public boolean addData(final @Nullable Proxy proxy)
    {
        boolean result = false;

        if (proxy != null)
        {
            ProxyBean pdi = new ProxyBean(proxy);
            if (pdi.isActionable()
                    && (result = putIfAbsent(pdi)))
            {
                config.addProperty(PROP_PROXIES_PROXY_ID, pdi.hashCode());
                SubnodeConfiguration property = getProxy(pdi.hashCode());
                property.addProperty("proxyHost", pdi.getProxyHost());
                property.addProperty("proxyPort", pdi.getProxyPort());
                property.addProperty("proxyUser", pdi.getProxyUser());
                property.addProperty("proxyPassword", pdi.getProxyPassword());
                dirty();
            }
        }

        return result;
    }


    /* ---------------------------------------------------------------- */

    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.models.Properties#removeData(com.github.technosf.posterer.models.Properties.impl.PropertiesModel.Request)
     */
    protected boolean erase(final RequestBean requestBean)
    {
        removeEndpoint(requestBean.getEndpoint());

        String key = String.format(PROP_REQUESTS_REQUEST_ID_QUERY,
                requestBean.hashCode());
        try
        {
            config.clearTree(key);
            dirty();
            return true;
        }
        catch (Exception e)
        {
            LOG.debug("Could not clear the config tree", e);
        }

        return false;
    }


    /**
     * {@inheritDoc}
     *
     * @see com.github.technosf.posterer.models.Properties#save()
     */
    protected final boolean write()
    {
        try
        {
            LOG.debug("Saving properties file.");
            return config.save(); // FIXME
        }
        catch (ConfigurationException e)
        {
            LOG.error("Could not save configuration", e);
        }

        return false;
    }


    protected void addKeystore(String filepath)
    {
        LOG.debug("Adding keystor file: {}", filepath);
        config.addProperty(PROP_KEYSTORES_KEYSTORE, filepath);
    }

    /* ---------------------------------------------------------------- */


    /**
     * Returns the Subnode config for the given proxy id
     * 
     * @param id
     *            the proxy id
     * @return the Subnode config holding the proxy if any
     */
    @SuppressWarnings("null")
    private SubnodeConfiguration getProxy(final int id)
    {
        return config.configurationAt(
                String.format(PROP_PROXIES_PROXY_ID_QUERY, id), true);
    }


    /**
     * Load saved requests into current session
     */
    @SuppressWarnings("null")
    private void initializeRequestSet()
    {
        for (HierarchicalConfiguration c : config
                .configurationsAt(PROP_REQUESTS_REQUEST))
        /*
         * Deserialize each stored request into a RequestBean
         */
        {
            int hashCode = Integer.parseInt((String) c.getRootNode()
                    .getAttributes("id").get(0).getValue());
            SubnodeConfiguration request = getRequest(hashCode);

            RequestBean pdi = new RequestBean(request.getString("endpoint"),
                    request.getString("payload"),
                    request.getString("method"), request.getString("security"),
                    request.getString("contentType"),
                    request.getBoolean("base64", false));

            if (pdi.isActionable())
            /*
             * Property is good.
             */
            {
                // System.out.printf("%1$S::%2$s", hashCode, pdi.hashCode());
                if (hashCode != pdi.hashCode())
                /*
                 * The config hash changed and needs reindexing
                 */
                {
                    request.setSubnodeKey(Integer.toString(pdi.hashCode()));
                    // TODO - Need to rebuild the store and save
                }

                /*
                 * Put the deserialized bean into the request map
                 */
                putIfAbsent(pdi);

                /*
                 * Add this request endpoint to current endpoints
                 */
                addEndpoint(pdi.getEndpoint());

            } // if (pdi.isActionable())
            else
            /*
             * Property was ill formed - remove from file
             */
            {
                String key = request.getSubnodeKey();
                config.clearTree(key);
                dirty();
            }

        } // for (HierarchicalConfiguration c : config

    } // private void initializeRequestSet()


    /**
     * Load saved proxies into current session
     */
    private void initializeProxySet()
    {
        for (HierarchicalConfiguration c : config
                .configurationsAt(PROP_PROXIES_PROXY))
        /*
         * Deserialize each stored proxy into a proxyBean
         */
        {
            int hashCode = Integer.parseInt((String) c.getRootNode()
                    .getAttributes("id").get(0).getValue());
            SubnodeConfiguration proxy = getProxy(hashCode);

            ProxyBean pdi = new ProxyBean(proxy.getString("proxyHost"),
                    proxy.getString("proxyPort"),
                    proxy.getString("proxyUser"),
                    proxy.getString("proxyPassword"));

            if (pdi.isActionable())
            /*
             * Property is good.
             */
            {
                // System.out.printf("%1$S::%2$s", hashCode, pdi.hashCode());
                if (hashCode != pdi.hashCode())
                /*
                 * The config hash changed and needs reindexing
                 */
                {
                    proxy.setSubnodeKey(Integer.toString(pdi.hashCode()));
                }

                /*
                 * Put the deserialized bean into the request map
                 */
                putIfAbsent(pdi);

            } // if (pdi.isActionable())
            else
            /*
             * Property was ill formed - remove from file
             */
            {
                String key = proxy.getSubnodeKey();
                config.clearTree(key);
                dirty();
            }

        } // for (HierarchicalConfiguration c : config

    } // private void initializeProxySet()


    /**
     * Load saved proxies into current session
     */
    private void initializeKeyStoreSet()
    {
        for (HierarchicalConfiguration c : config
                .configurationsAt((PROP_KEYSTORES_KEYSTORE)))
        {
            String file = (String) c.getRoot().getValue();
            if (file != null && !file.isEmpty())
            {
                if (!putIfAbsent(file))
                {
                    c.clear();
                    dirty();
                }
            }
        }

    } // private void initializeProxySet()

}
