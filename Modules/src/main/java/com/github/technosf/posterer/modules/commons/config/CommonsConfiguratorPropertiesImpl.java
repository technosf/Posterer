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

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.builder.fluent.XMLBuilderParameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.tree.ImmutableNode;
import org.apache.commons.configuration2.tree.xpath.XPathExpressionEngine;
import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.technosf.posterer.models.Actionable;
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
     * A convenience class for creating parameter objects for initializing
     * configuration builder objects.
     */
    private final static Parameters PARAMS = new Parameters();

    /**
     * The XML configuration builder
     */
    private final FileBasedConfigurationBuilder<XMLConfiguration> builder;

    /**
     * The XML configuration
     */
    private final XMLConfiguration config;


    /* ---------------------------------------------------------------- */

    /**
     * Constructor and injection point for <b>Guice</b>
     * <p>
     * 
     * @param prefix
     * @throws IOException
     * @throws ConfigurationException
     */
    @SuppressWarnings("null")
    @Inject
    public CommonsConfiguratorPropertiesImpl(
            @Named("PropertiesPrefix") final String prefix,
            @Nullable @Named("PropertiesDir") final File directory,
            @Nullable @Named("PropertiesFile") final String filename)
            throws IOException, ConfigurationException
    {
        super(prefix, directory, filename);

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
         * Create the config builder
         */
        builder = createBuilder(propsFile);

        /*
         * Load the properties file
         */
        config = builder.getConfiguration();

        /*
         * Load up saved requests
         */
        initializeRequestSet();
        initializeProxySet();
        initializeKeyStoreSet();
        if (isDirty())
            save();

    }


    /* ---------------------------------------------------------------- 
     * 
     * Properties methods
     * 
     * ----------------------------------------------------------------
     */

    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.models.Properties#getBasicPropertiesFileContent()
     */
    @Override
    public @NonNull String getBasicPropertiesFileContent()
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
                HierarchicalConfiguration<ImmutableNode> property =
                        getRequest(pdi.hashCode());
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
                HierarchicalConfiguration<ImmutableNode> property =
                        getProxy(pdi.hashCode());
                property.addProperty("proxyHost", pdi.getProxyHost());
                property.addProperty("proxyPort", pdi.getProxyPort());
                property.addProperty("proxyUser", pdi.getProxyUser());
                property.addProperty("proxyPassword", pdi.getProxyPassword());
                dirty();
            }
        }

        return result;
    }


    /* ---------------------------------------------------------------- 
     * 
     * AbstractProperties methods
     * 
     * ----------------------------------------------------------------
     */

    /**
     * {@inheritDoc}
     *
     * @see com.github.technosf.posterer.models.impl.base.AbstractPropertiesModel#erase(com.github.technosf.posterer.models.impl.RequestBean)
     */
    @Override
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
     * @see com.github.technosf.posterer.models.impl.base.AbstractPropertiesModel#write()
     */
    @Override
    protected boolean write()
    {
        try
        {
            LOG.debug("Saving properties file.");
            builder.save();
            return true;
        }
        catch (ConfigurationException e)
        {
            LOG.error("Could not save configuration", e);
        }

        return false;
    }


    /**
     * {@inheritDoc}
     *
     * @see com.github.technosf.posterer.models.impl.base.AbstractPropertiesModel#addKeystore(java.lang.String)
     */
    @Override
    protected void addKeystore(String filepath)
    {
        LOG.debug("Adding keystor file: {}", filepath);
        config.addProperty(PROP_KEYSTORES_KEYSTORE, filepath);
    }


    /* ---------------------------------------------------------------- 
     * 
     * Implementation methods
     * 
     * ----------------------------------------------------------------
     */

    /**
     * Returns the Subnode config for the given request id
     * 
     * @param id
     *            the request id
     * @return the Subnode config holding the request if any
     */
    @SuppressWarnings("null")
    private HierarchicalConfiguration<ImmutableNode> getRequest(final int id)
    {
        return config.configurationAt(
                String.format(PROP_REQUESTS_REQUEST_ID_QUERY, id), true);
    }


    /**
     * Returns the Subnode config for the given proxy id
     * 
     * @param id
     *            the proxy id
     * @return the Subnode config holding the proxy if any
     */
    @SuppressWarnings("null")
    private HierarchicalConfiguration<ImmutableNode> getProxy(final int id)
    {
        return config.configurationAt(
                String.format(PROP_PROXIES_PROXY_ID_QUERY, id), true);
    }


    /**
     * Load saved requests into current session
     * <p>
     * Re-id the requests if there are duplicate issues in the file
     */
    @SuppressWarnings("null")
    private void initializeRequestSet()
    {
        for (HierarchicalConfiguration<ImmutableNode> c : config
                .configurationsAt(PROP_REQUESTS_REQUEST))
        /*
         * Deserialize each stored request into a RequestBean
         */
        {
            int requestNodeId = Integer.parseInt((String) c.getProperty("@id"));

            HierarchicalConfiguration<ImmutableNode> requestNode =
                    getRequest(requestNodeId);

            RequestBean request =
                    new RequestBean(requestNode.getString("endpoint"),
                            requestNode.getString("payload"),
                            requestNode.getString("method"),
                            requestNode.getString("security"),
                            requestNode.getString("contentType"),
                            requestNode.getBoolean("base64", false));

            if (actionable(request, requestNodeId, requestNode, c))
            {
                /*
                 * Put the deserialized bean into the request map
                 */
                putIfAbsent(request);

                /*
                 * Add this request endpoint to current endpoints
                 */
                addEndpoint(request.getEndpoint());
            }
            else
            {
                dirty();
            }

        } // for (HierarchicalConfiguration c : config

    } // private void initializeRequestSet()


    /**
     * Load saved proxies into current session
     */
    @SuppressWarnings("null")
    private void initializeProxySet()
    {
        for (HierarchicalConfiguration<ImmutableNode> c : config
                .configurationsAt(PROP_PROXIES_PROXY))
        /*
         * Deserialize each stored proxy into a proxyBean
         */
        {
            int proxyNodeId = Integer.parseInt((String) c.getProperty("@id"));
            HierarchicalConfiguration<ImmutableNode> proxyNode =
                    getProxy(proxyNodeId);

            ProxyBean proxy = new ProxyBean(proxyNode.getString("proxyHost"),
                    proxyNode.getString("proxyPort"),
                    proxyNode.getString("proxyUser"),
                    proxyNode.getString("proxyPassword"));

            if (actionable(proxy, proxyNodeId, proxyNode, c))
            {
                putIfAbsent(proxy);
            }
            else
            {
                dirty();
            }

        } // for (HierarchicalConfiguration c : config

    } // private void initializeProxySet()


    /**
     * Load saved proxies into current session
     */
    @SuppressWarnings("null")
    private void initializeKeyStoreSet()
    {
        for (HierarchicalConfiguration<ImmutableNode> c : config
                .configurationsAt((PROP_KEYSTORES_KEYSTORE)))
        {
            String filepath = c.toString();
            if (filepath != null && !filepath.isEmpty())
            {
                if (!putIfAbsent(filepath))
                {
                    c.clear();
                    dirty();
                }
            }
        }

    } // private void initializeProxySet()


    /* ---------------------------------------------------------------- */

    /**
     * Check for actionability
     * <p>
     * If not actionable, remove, otherwise re-key as needed.
     * 
     * @param actionable
     *            the actionable being checked
     * @param nodeId
     *            the node id of the of actionable representation
     * @param node
     *            the actionable representation
     * @param config
     *            the config holding the actionable representation
     * @return true if rekeys
     */
    private static boolean actionable(Actionable actionable,
            int nodeId,
            HierarchicalConfiguration<ImmutableNode> node,
            HierarchicalConfiguration<ImmutableNode> config)
    {

        if (actionable.isActionable())
        /*
         * Property is good.
         */
        {
            // System.out.printf("%1$S::%2$s", hashCode, pdi.hashCode());
            if (nodeId != actionable.hashCode())
            /*
             * The config hash changed and needs reindexing
             */
            {
                node.setProperty("id", actionable.hashCode());
            }

            return true;

        } // if (proxy.isActionable())
        else
        /*
         * Property was ill formed - remove from file
         */
        {
            String key = node.getString("id");
            config.clearTree(key);
            return false;
        }
    }


    /**
     * Creates a config builder for the given file
     * <p>
     * The builder handles I/O tasks
     * 
     * @param file
     *            the config file
     * @return the builder
     * @throws IOException
     */
    @SuppressWarnings("null")
    private static FileBasedConfigurationBuilder<XMLConfiguration> createBuilder(
            File file)
            throws IOException
    {
        XMLBuilderParameters xmlParams = PARAMS.xml()
                .setThrowExceptionOnMissing(true)
                .setValidating(false)
                .setEncoding("UTF-8")
                .setFileName(file.getCanonicalPath())
                .setExpressionEngine(new XPathExpressionEngine());

        return new FileBasedConfigurationBuilder<>(
                XMLConfiguration.class).configure(xmlParams);

    }

}
