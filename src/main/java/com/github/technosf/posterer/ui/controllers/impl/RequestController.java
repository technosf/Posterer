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
package com.github.technosf.posterer.ui.controllers.impl;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.technosf.posterer.App;
import com.github.technosf.posterer.models.Properties;
import com.github.technosf.posterer.models.Proxy;
import com.github.technosf.posterer.models.Request;
import com.github.technosf.posterer.models.RequestModel;
import com.github.technosf.posterer.models.ResponseModel;
import com.github.technosf.posterer.models.impl.KeyStoreBean;
import com.github.technosf.posterer.models.impl.KeyStoreBean.KeyStoreBeanException;
import com.github.technosf.posterer.models.impl.ProxyBean;
import com.github.technosf.posterer.models.impl.RequestBean;
import com.github.technosf.posterer.ui.controllers.Controller;
import com.github.technosf.posterer.ui.controllers.impl.base.AbstractRequestController;
import com.github.technosf.posterer.utils.ssl.SslUtils;

import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Concrete Controller for the Request window that implements the non-display
 * related and overt event handling
 * 
 * @author technosf
 * @since 0.0.1
 * @version 0.0.1
 */
// @SuppressWarnings("null")
public class RequestController
        extends AbstractRequestController
{

    /* ---- Constants ----- */

    private static final Logger LOG =
            LoggerFactory.getLogger(RequestController.class);

    /* ---- Private state vars ----- */

    @NonNull
    private final Properties properties;

    @NonNull
    private final RequestModel requestModel;

    private KeyStoreBean keyStoreBean;

    /*
     * ------------ FXML Helpers -----------------
     */

    private final FadeTransition status_fade =
            new FadeTransition(Duration.millis(5000), statusWindow);

    /*
     * ============================ Code =====================
     */


    /*
     * ------------ Statics -----------------
     */

    /**
     * Loads the controller onto the stage
     * 
     * @param stage
     * @return
     */
    public static Controller loadStage(Stage stage)
    {
        Controller controller = null;

        try
        {
            controller = RequestController.loadController(stage, FXML);
        }
        catch (IOException e)
        {
            LOG.error("Cannot load Controller.", e);
        }

        return controller;
    }


    /**
     * Default constructor
     */
    @SuppressWarnings("null")
    public RequestController()
    {
        super("Posterer");

        status_fade.setFromValue(1.0);
        status_fade.setToValue(0.0);
        status_fade.setCycleCount(4);
        status_fade.setAutoReverse(true);

        properties = App.INJECTOR.getInstance(Properties.class);
        requestModel = App.INJECTOR.getInstance(RequestModel.class);

        LOG.debug("Instantiated");
    }

    /*
     * ------------ Initiators -----------------
     */


    /**
     * {@inheritDoc}
     * <p>
     * Could've have put this functionality into {@code close()} too.
     *
     * @see com.github.technosf.posterer.ui.controllers.Controller#onStageClose()
     */
    @Override
    public void onStageClose(Stage stage)
    {
        LOG.debug("Closing StatusController");
        statusController.onStageClose(stage);
    }


    /*
     * ---------- Events -------------------
     */

    /**
     * {@inheritDoc}
     *
     * @see com.github.technosf.posterer.ui.controllers.impl.base.AbstractRequestController#proxySave()
     */
    @SuppressWarnings("null")
    @Override
    public void proxySave()
    {
        properties.addData(proxyBean);
        propsProcess();
    }


    /**
     * {@inheritDoc}
     *
     * @see com.github.technosf.posterer.ui.controllers.impl.base.AbstractRequestController#requestSave()
     */
    @SuppressWarnings("null")
    @Override
    public void requestSave()
    {
        requestUpdate();
        properties.addData(requestBean);
        propsProcess();
    }


    /**
     * Fires a request off and returns the Response
     * 
     * @param request
     *            the request
     * @return the response
     */
    @SuppressWarnings("null")
    protected @NonNull ResponseModel requestFire(final @NonNull Request request)
    {
        /*
         * Proxy and certificate
         */
        if (proxyOnProperty.get() && keyStoreBean != null
                && keyStoreBean.isValid())
        {
            return requestModel.doRequest(request, proxyCombo.getValue(),
                    keyStoreBean, useCertificateAlias.getValue());
        }

        /*
         * Certificate only
         */
        if (!proxyOnProperty.get() && keyStoreBean != null
                && keyStoreBean.isValid())
        {
            return requestModel.doRequest(request,
                    keyStoreBean, useCertificateAlias.getValue());
        }

        /*
         * Proxy only
         */
        if (proxyOnProperty.get() && (keyStoreBean == null
                || !keyStoreBean.isValid()))
        {
            return requestModel.doRequest(request, proxyCombo.getValue());
        }

        /*
         * basic
         */
        return requestModel.doRequest(request);

    }


    /**
     * {@inheritDoc}
     *
     * @see com.github.technosf.posterer.ui.controllers.impl.base.AbstractRequestController#proxyToggle()
     */
    @Override
    public void proxyToggle()
    {
        proxyToggle(null);
    }


    /**
     * Toggles the proxy if required.
     * 
     * @param proxy
     *            true for on, false for off, null for toggle
     */
    private void proxyToggle(final @Nullable Boolean proxy)
    {
        if ((proxyOnProperty.get() && proxy == null)
                || (proxy != null && proxy))
        /*
         * The useProxy is set, so set the proxy
         */
        {
            proxyEnableFields(true);
            proxyUpdate();
            LOG.debug("Proxy activted");
        }
        else
        /*
         * useProxy is unset, so unset the proxy
         */
        {
            proxyEnableFields(false);
            proxyBean.reset();
            LOG.debug("Proxy deactivted");
        }
    }


    /**
     * {@inheritDoc}
     *
     * @see com.github.technosf.posterer.ui.controllers.impl.base.AbstractRequestController#requestUpdate()
     */
    @SuppressWarnings("null")
    @Override
    public void requestUpdate()
    {
        Object uri = endpoint.getValue();
        if (String.class.isInstance(uri))
        {
            requestBean.setEndpoint((String) uri);
        }
        else
        {
            requestBean.setEndpoint(endpoint.getValue().toASCIIString());
        }
        requestBean.setMethod(method.getValue());
        requestBean.setSecurity(security.getValue());
        requestBean.setPayload(StringUtils.trim(payload.getText()));
        requestBean.setContentType(mime.getValue());
        requestBean.setBase64(encode.isSelected());
    }


    /**
     * {@inheritDoc}
     *
     * @see com.github.technosf.posterer.ui.controllers.impl.base.AbstractRequestController#proxyUpdate()
     */
    @Override
    public void proxyUpdate()
    {
        ProxyBean newProxyBean =
                new ProxyBean(proxyHost.getText(), proxyPort.getText(),
                        proxyUser.getText(), proxyPassword.getText());

        if (!newProxyBean.equals(proxyBean) && newProxyBean.isActionable())
        {
            proxyBean = newProxyBean;
            proxyCombo.setValue(proxyBean);
            if (!proxyCombo.getItems().contains(proxyBean))
            /*
             * Add proxy if not already added
             * 
             */
            {
                proxyCombo.getItems().add(proxyBean.copy());
            }
        }

        fireDisabledProperty.set(!newProxyBean.isActionable());
    }


    /**
     * {@inheritDoc}
     *
     * @see com.github.technosf.posterer.ui.controllers.impl.base.AbstractRequestController#certificateValidate()
     */
    @SuppressWarnings("null")
    @Override
    public void certificateValidate()
    {
        KeyStoreBean keyStore = null;
        try
        {
            keyStore = new KeyStoreBean(certificateFileChooser.getValue(),
                    certificatePassword.getText());
            KeyStoreViewerController.loadStage(keyStore).show();
            ObservableList<String> aliases =
                    FXCollections.observableArrayList("Do not use certificate");
            aliases.addAll(keyStore.getAliases());
            useCertificateAlias.itemsProperty().setValue(aliases);
            useCertificateAlias.setDisable(false);
            keyStoreBean = keyStore;
        }
        catch (KeyStoreBeanException e)
        {
            LOG.debug("Certificate file cannot be opened:\n"
                    + e.getMessage().replaceAll("\n", "\n\t"), e);
            status.append("Certificate file cannot be opened: [%1$s]",
                    e.getCause().getMessage());
        }
    }


    /**
     * Show custom payload context menu on tripple click
     */
    public void onPayloadSelected(final MouseEvent mouseEvent)
    {
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY)
                && mouseEvent.getClickCount() == 3)
        {
            payloadCM.show(payload, mouseEvent.getScreenX(),
                    mouseEvent.getScreenY());
        }
    }


    /**
     * Open the stand alone status window on Status double click
     */
    public void onStatusSelected(final MouseEvent mouseEvent)
    {
        Stage stage;
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY)
                && mouseEvent.getClickCount() == 2
                && (statusController != null)
                && (stage = statusController.getStage()) != null)
        {
            stage.show();
        }
    }

    /* ------------ Utilities ------------------ */


    /**
     * {@inheritDoc}
     *
     * @see com.github.technosf.posterer.ui.controllers.impl.base.AbstractRequestController#propsProcess()
     */
    @SuppressWarnings("null")
    @Override
    protected void propsProcess()
    {

        LOG.debug("processing Properties");

        requestPropertiesList.clear();

        if (!preferencesAvailable)
            return;

        List<Request> requests = properties.getRequests(); // Get properties

        /*
         * populate endpoint drop down
         */
        endpointFilter.getItems().setAll("");
        Set<URI> endpoints = new TreeSet<URI>();

        for (Request prop : requests)
        {
            endpoints.add(RequestBean.constructUri(prop.getEndpoint()));
            if (!endpointFilter.getItems().contains(prop.getEndpoint()))
            {
                endpointFilter.getItems().add(prop.getEndpoint());
            }
        }

        endpoint.getItems().setAll(endpoints);

        /*
         * Create properties table
         */
        requestPropertiesList.addAll(requests);

        /*
         * populate proxy drop down
         */
        List<Proxy> proxies = properties.getProxies(); // Get properties
        proxyCombo.getItems().setAll();
        for (Proxy prop : proxies)
        {
            proxyCombo.getItems().add(new ProxyBean(prop));
        }
    }


    /**
     * {@inheritDoc}
     *
     * @see com.github.technosf.posterer.ui.controllers.impl.base.AbstractRequestController#requestLoad(com.github.technosf.posterer.models.Request)
     */
    @Override
    protected void requestLoad(final @Nullable Request requestdata)
    {
        if (requestdata == null)
        {
            return;
        }

        LOG.debug("Loading saved request");

        endpointValidate(requestdata.getEndpoint(), requestdata.getSecurity());
        payload.setText(requestdata.getPayload());
        method.setValue(requestdata.getMethod());
        mime.setValue(requestdata.getContentType());
        encode.setSelected(requestdata.getBase64());
        status.append("Loaded request for endpoint:[%1$s]",
                requestdata.getEndpoint());
    }


    /**
     * {@inheritDoc}
     *
     * @see com.github.technosf.posterer.ui.controllers.impl.base.AbstractRequestController#certificateFile(java.io.File)
     */
    @Override
    protected void certificateFile(final @Nullable File file)
    {
        if (file != null && (!file.exists() || !file.canRead()))
        /*
         * Chosen file cannot be read, turn off FXML assets
         */
        {
            status.append("Certificate file cannot be read: [{}]",
                    file.getPath());
            certificatePassword.setDisable(true);
            useCertificateAlias.setDisable(true);
            validateCertificate.setDisable(true);
            return;
        }

        // Switch off Cert-file FXML assets if the file is null
        certificatePassword.setDisable(file == null);
        // useCertificate.setDisable(file == null);
        validateCertificate.setDisable(file == null);
    }


    /**
     * Validate and place the endpoint where needed, set tls etc
     * 
     * @param endpoint
     * @param security
     */
    private void endpointValidate(final String endpoint, final String security)
    {
        endpointValidate(endpoint);
        this.security.setValue(security);
    }


    /**
     * {@inheritDoc}
     *
     * @see com.github.technosf.posterer.ui.controllers.impl.base.AbstractRequestController#endpointValidate(java.lang.String)
     */
    @Override
    protected void endpointValidate(final @Nullable String endpoint)
    {
        endpointValidate(RequestBean.constructUri(endpoint));
    }


    /**
     * Validate and place the endpoint where needed, set tls etc
     * 
     * @param endpoint
     */
    private void endpointValidate(final URI endpoint)
    {
        this.endpoint.setValue(endpoint);
        if ("HTTPS".equalsIgnoreCase(endpoint.getScheme()))
        {
            security.setDisable(false);
        }
        else
        {
            security.setDisable(true);
        }
    }


    /**
     * {@inheritDoc}
     *
     * @see com.github.technosf.posterer.ui.controllers.impl.base.AbstractRequestController#propsRemoveRequest(com.github.technosf.posterer.models.Request)
     */
    @Override
    protected void propsRemoveRequest(final @NonNull Request request)
    {
        properties.removeData(request);
    }


    /**
     * {@inheritDoc}
     *
     * @see com.github.technosf.posterer.ui.controllers.impl.base.AbstractRequestController#propsDirectory()
     */
    @Override
    protected @NonNull String propsDirectory() throws IOException
    {
        return properties.getPropertiesDir();
    }


    @Override
    protected void initializeOther()
    {
        securityChoicesList.addAll(SslUtils.getSecurityChoices());
        security.setValue(securityChoicesList.get(0));
    }

}
