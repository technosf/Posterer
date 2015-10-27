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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.SubnodeConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.commons.configuration.tree.xpath.XPathExpressionEngine;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.technosf.posterer.models.Properties;
import com.github.technosf.posterer.models.Request;
import com.github.technosf.posterer.models.RequestBean;
import com.github.technosf.posterer.models.base.AbstractPropertiesModel;
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
public class CommonsConfiguratorPropertiesImpl extends AbstractPropertiesModel
        implements Properties
{

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
    // private final static String PROP_REQUEST = "request";

    /**
     * A blank properties file template
     */
    final static String blankfile =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><configuration><"
                    + PROP_DEFAULT + "/><" + PROP_REQUESTS
                    + "/></configuration>";

    /**
     * XML configuration
     */
    private final XMLConfiguration config;

    /**
     * RequestBean map
     */
    private final Map<Integer, RequestBean> requestProperties =
            new HashMap<Integer, RequestBean>();

    /**
     * End point map
     */
    private final Map<String, Integer> endpoints =
            new TreeMap<String, Integer>();


    /**
     * Injection point for Guice
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
                || FileUtils.sizeOf(propsFile) < blankfile.length())
        /*
         * Create a blank properties file if it does not exist
         */
        {
            FileUtils.writeStringToFile(propsFile, blankfile);
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
    }


    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.models.Properties#getBasicPropertiesFileContent()
     */
    @Override
    public String getBasicPropertiesFileContent()
    {
        return blankfile;
    }


    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.models.Properties#getData()
     */
    @Override
    public List<Request> getData()
    {
        return new ArrayList<Request>(requestProperties.values());
    }


    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.models.Properties#addData(com.github.technosf.posterer.models.Properties.impl.PropertiesModel.Request)
     */
    @Override
    public boolean addData(final Request propertyData)
    {
        boolean result = false;

        if (propertyData != null)
        {
            RequestBean pdi = new RequestBean(propertyData);
            if (pdi.isActionable() && (result =
                    !requestProperties.containsKey(pdi.hashCode())))
            {
                requestProperties.put(pdi.hashCode(), pdi);
                config.addProperty("requests/request@id", pdi.hashCode());
                SubnodeConfiguration property = getRequest(pdi.hashCode());
                property.addProperty("endpoint", pdi.getEndpoint());
                property.addProperty("payload", pdi.getPayload());
                property.addProperty("method", pdi.getMethod());
                property.addProperty("security", pdi.getSecurity());
                property.addProperty("contentType", pdi.getContentType());
                property.addProperty("base64", pdi.getBase64());
//                property.addProperty("proxyHost", pdi.getProxyHost());
//                property.addProperty("proxyPort", pdi.getProxyPort());
//                property.addProperty("proxyUser", pdi.getProxyUser());
//                property.addProperty("proxyPassword", pdi.getProxyPassword());
                save();
            }
        }

        return result;
    }


    /**
     * @param id
     * @return
     */
    // @SuppressWarnings("null")
    private SubnodeConfiguration getRequest(final int id)
    {
        return config.configurationAt(
                String.format("requests/request[@id='%1$s']", id), true);
    }


    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.models.Properties#removeData(com.github.technosf.posterer.models.Properties.impl.PropertiesModel.Request)
     */
    // @SuppressWarnings("null")
    @Override
    public boolean removeData(final Request propertyData)
    {
        boolean result = false;

        if (propertyData != null)
        {
            RequestBean pdi = new RequestBean(propertyData);
            String key = String
                    .format("requests/request[@id='%1$s']", pdi.hashCode());

            if (pdi.isActionable()
                    &&
                    (result = (requestProperties
                            .remove(pdi.hashCode()) != null))
            // Check and remove the properties
            )
            {
                removeEndpoint(pdi.getEndpoint());
                try
                {
                    config.clearTree(key);
                    result = true;
                }
                catch (Exception e)
                {
                    e.printStackTrace(System.out);
                }

                save();
            }
        }

        return result;
    }


    /**
     * Load saved requests into current session
     */
    // @SuppressWarnings("null")
    private void initializeRequestSet()
    {
        for (HierarchicalConfiguration c : config
                .configurationsAt("requests/request"))
        /*
         * Deserialize each stored request into a RequestBean
         */
        {
            int hashCode = Integer.parseInt((String) c.getRootNode()
                    .getAttributes("id").get(0).getValue());
            SubnodeConfiguration request = getRequest(hashCode);

            RequestBean pdi = new RequestBean(request.getString("endpoint"),
                    request.getString("payload"),
                    request.getString("method"),
                    request.getString("security"),
                    request.getString("contentType"),
                    request.getBoolean("base64", false)
//                    , request.getString("proxyHost"),
//                    request.getString("proxyPort"),
//                    request.getString("proxyUser"),
//                    request.getString("proxyPassword")
                    );

            if (pdi.isActionable())
            /*
             * Property is good.
             */
            {
                //System.out.printf("%1$S::%2$s", hashCode, pdi.hashCode());
                if (hashCode != pdi.hashCode())
                /*
                 * The config hash changed and needs reindexing
                 */
                {
                    request.setSubnodeKey(Integer.toString(pdi.hashCode()));
                    //TODO - Need to rebuild the store and save
                }

                /*
                 * Put the deserialized bean into the request map
                 */
                requestProperties.put(pdi.hashCode(), pdi);

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
            }

        } // for (HierarchicalConfiguration c : config

        save();

    } // private void initializeRequestSet()


    /**
     * Add an endpoint to the current enpoint map
     * 
     * @param endpoint
     */
    private synchronized void addEndpoint(final String endpoint)
    {
        int endpointCount =
                (endpoints.containsKey(endpoint) ? endpoints.get(endpoint) : 0);
        endpoints.put(endpoint, ++endpointCount);
    }


    /**
     * Remove an endpoint from the current endpoint map
     * 
     * @param endpoint
     */
    private synchronized void removeEndpoint(final String endpoint)
    {
        int endpointCount =
                (endpoints.containsKey(endpoint) ? endpoints.get(endpoint) : 0);

        if (endpointCount > 1)
        {
            endpoints.put(endpoint, --endpointCount);
        }
        else
        {
            endpoints.remove(endpoint);
        }
    }


    /**
     * Save the properties file
     */
    private void save()
    {
        try
        {
            config.save();
        }
        catch (ConfigurationException e)
        {
            LOG.error("Could not save configuration", e);
        }
    }

}
