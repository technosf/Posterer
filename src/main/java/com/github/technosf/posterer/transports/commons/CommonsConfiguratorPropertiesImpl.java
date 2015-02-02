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
package com.github.technosf.posterer.transports.commons;

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

import com.github.technosf.posterer.models.PropertiesModel;
import com.github.technosf.posterer.models.RequestBean;
import com.github.technosf.posterer.models.RequestData;
import com.github.technosf.posterer.models.base.AbstractPropertiesModel;
import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * Basic implementation of {@code PreferencesModel}
 * 
 * @author technosf
 * @since 0.0.1
 * @version 0.0.1
 */
public class CommonsConfiguratorPropertiesImpl
        extends AbstractPropertiesModel
        implements PropertiesModel
{

    static String PROP_DEFAULT = "default";
    static String PROP_REQUESTS = "requests";
    static String PROP_REQUEST = "request";

    static String blankfile =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><configuration><"
                    + PROP_DEFAULT
                    + "/><"
                    + PROP_REQUESTS
                    + "/></configuration>";

    static final String requestFormat =
            "<request><endpoint>\"%1$s\"</endpoint><payload>\"%2$s\"</payload><method>\"%3$s\"</method><contentType>\"%4$s\"<contentType><base64>%5$s</base64><httpUser>%6$s</httpUser><httpPassword>%7$s</httpPassword></request>";

    private final XMLConfiguration config;

    private final Map<Integer, RequestBean> requestProperties =
            new HashMap<Integer, RequestBean>();

    private final Map<String, Integer> endpoints =
            new TreeMap<String, Integer>();


    /**
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
        {
            FileUtils.writeStringToFile(propsFile, blankfile);
        }

        FileChangedReloadingStrategy strategy =
                new FileChangedReloadingStrategy();
        strategy.setRefreshDelay(5000);

        config = new XMLConfiguration(propsFile);
        config.setExpressionEngine(new XPathExpressionEngine());
        config.setReloadingStrategy(strategy);

        initializeRequestSet();
    }


    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.models.PropertiesModel#getBasicPropertiesFileContent()
     */
    @Override
    public String getBasicPropertiesFileContent()
    {
        return blankfile;
    }


    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.models.PropertiesModel#getData()
     */
    @Override
    public List<RequestData> getData()
    {
        return new ArrayList<RequestData>(requestProperties.values());
    }


    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.models.PropertiesModel#addData(com.github.technosf.posterer.models.impl.PropertiesModel.RequestData)
     */
    @Override
    public boolean addData(final RequestData propertyData)
    {
        boolean result = false;

        if (propertyData != null)
        {
            RequestBean pdi = new RequestBean(propertyData);
            if (pdi.isActionable()
                    && (result = !requestProperties.containsKey(pdi.hashCode())))
            {
                requestProperties.put(pdi.hashCode(), pdi);
                config.addProperty("requests/request@id", pdi.hashCode());
                SubnodeConfiguration property = getRequest(pdi.hashCode());
                property.addProperty("endpoint", pdi.getEndpoint());
                property.addProperty("payload", pdi.getPayload());
                property.addProperty("method", pdi.getMethod());
                property.addProperty("contentType", pdi.getContentType());
                property.addProperty("base64", pdi.getBase64());
                property.addProperty("httpUser", pdi.getHttpUser());
                property.addProperty("httpPassword", pdi.getHttpPassword());
                save();
            }
        }

        return result;
    }


    /**
     * @param id
     * @return
     */
    private SubnodeConfiguration getRequest(final int id)
    {
        return config.configurationAt(String.format(
                "requests/request[@id='%1$s']", id));
    }


    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.models.PropertiesModel#removeData(com.github.technosf.posterer.models.impl.PropertiesModel.RequestData)
     */
    @Override
    public boolean removeData(final RequestData propertyData)
    {
        boolean result = false;

        if (propertyData != null)
        {
            RequestBean pdi = new RequestBean(propertyData);
            if (pdi.isActionable()
                    && result == (requestProperties.remove(pdi.hashCode()) != null) // Check
                                                                                    // and
                                                                                    // remove
                                                                                    // the
                                                                                    // properties
            )
            {
                removeEndpoint(pdi.getEndpoint());
                getRequest(pdi.hashCode()).clear();
                save();
            }
        }

        return result;
    }


    /**
     * 
     */
    private void initializeRequestSet()
    {
        for (HierarchicalConfiguration c : config
                .configurationsAt("requests/request"))
        {
            int hashCode =
                    Integer.parseInt((String) c.getRootNode()
                            .getAttributes("id").get(0).getValue());
            SubnodeConfiguration request = getRequest(hashCode);
            RequestBean pdi =
                    new RequestBean(request.getString("endpoint")
                            , request.getString("payload")
                            , request.getString("method")
                            , request.getString("contentType")
                            , request.getBoolean("base64")
                            , request.getString("httpUser")
                            , request.getString("httpPassword"));

            assert (hashCode == pdi.hashCode());

            requestProperties.put(pdi.hashCode(), pdi);

            addEndpoint(pdi.getEndpoint());
        }
    }


    /**
     * @param endpoint
     */
    private synchronized void addEndpoint(final String endpoint)
    {
        int endpointCount =
                (endpoints.containsKey(endpoint)
                        ? endpoints
                                .get(endpoint)
                        : 0
                );
        endpoints.put(endpoint, ++endpointCount);
    }


    /**
     * @param endpoint
     */
    private synchronized void removeEndpoint(final String endpoint)
    {
        int endpointCount =
                (endpoints.containsKey(endpoint)
                        ? endpoints
                                .get(endpoint)
                        : 0
                );

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
     * 
     */
    private void save()
    {
        try
        {
            config.save();
        }
        catch (ConfigurationException e)
        {
            e.printStackTrace();
        }
    }
}
