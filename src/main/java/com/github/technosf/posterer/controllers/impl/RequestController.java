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
package com.github.technosf.posterer.controllers.impl;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javafx.animation.FadeTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.util.Duration;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.technosf.posterer.App;
import com.github.technosf.posterer.components.FileChooserComboBox;
import com.github.technosf.posterer.controllers.AbstractController;
import com.github.technosf.posterer.controllers.Controller;
import com.github.technosf.posterer.models.PropertiesModel;
import com.github.technosf.posterer.models.RequestBean;
import com.github.technosf.posterer.models.RequestData;
import com.github.technosf.posterer.models.RequestModel;
import com.github.technosf.posterer.models.ResponseModel;

/**
 * Controller for the Request window.
 * 
 * @author technosf
 * @since 0.0.1
 * @version 0.0.1
 */
public class RequestController
        extends AbstractController
        implements Controller
{
    /**
     * The FXML definition of the View
     */
    public static final String FXML = "/fxml/Request.fxml";

    /* ----  Constants ----- */

    private static final Logger logger = LoggerFactory
            .getLogger(RequestController.class);

    private static final String ERROR_URL_VALIDATION =
            "URL is invalid. Correct and try again.";

    private static final String INFO_PROPERTIES =
            "Error :: Cannot store endpoints and requests: %1$s";

    private static final String INFO_FIRED =
            "Fired request #%1$d:   Method [%2$s]   Endpoint [%3$s] ";

    private static final String LEGEND_PROXY_ON = "Proxy On";
    private static final String LEGEND_PROXY_OFF = "Proxy Off";

    private static final Paint CONST_PAINT_BLACK = Paint.valueOf("#292929");
    private static final Paint CONST_PAINT_GREY = Paint.valueOf("#808080");

    /* ----  Private state vars ----- */

    private final PropertiesModel propertiesModel;

    private final RequestModel requestModel = App.INJECTOR
            .getInstance(RequestModel.class);

    /**
     * The request data to reflect in this window
     */
    private final RequestBean requestBean = new RequestBean();

    private boolean preferencesAvailable = true;

    /*
     * ------------ FXML Components -----------------
     */

    @FXML
    private Parent root;

    @FXML
    private ComboBox<String> endpoint;

    @FXML
    private FileChooserComboBox certificateFile;

    @FXML
    private TextField status, timeoutText, user, proxyhost,
            proxyport, proxyuser, proxypassword, homedir;

    @FXML
    private Label proxyhostlabel, proxyportlabel, proxyuserlabel,
            proxypasswordlabel;
    @FXML
    private PasswordField password, certificatePassword;

    @FXML
    private Slider timeoutSlider;

    @FXML
    private TextArea payload;

    @FXML
    private ProgressIndicator progress;

    @FXML
    private Button fire1, fire2, fire3, fire4, fire5, save,
            certificateValidate;

    @FXML
    private ToggleButton proxyToggle1, proxyToggle2, proxyToggle3,
            proxyToggle4, proxyToggle5;

    @FXML
    private ChoiceBox<String> method, mime;

    @FXML
    private RadioButton encode;

    @FXML
    private TabPane tabs;

    @FXML
    private Tab destination, configuration, store;

    @FXML
    private StackPane stack;

    @FXML
    private TableView<RequestData> propertiesTable;

    @FXML
    private TableColumn<RequestData, String> endpointColumn, payloadColumn,
            methodColumn, contentTypeColumn, httpUserColumn,
            httpPasswordColumn;
    @FXML
    private TableColumn<RequestData, Boolean> base64Column;

    /*
     * ------------ FXML Bindings -----------------
     */
    IntegerProperty timeout = new SimpleIntegerProperty();

    BooleanProperty useProxy = new SimpleBooleanProperty(
            requestModel.useProxy());

    StringProperty useProxyText = new SimpleStringProperty(useProxy.get()
            ? LEGEND_PROXY_ON : LEGEND_PROXY_OFF);

    ObservableList<RequestData> properties = FXCollections
            .observableArrayList();

    /*
     * ------------ FXML Helpers -----------------
     */

    private final FadeTransition status_fade = new FadeTransition(
            Duration.millis(5000), status);


    /*
     * ------------ Code -----------------
     */

    /**
     * Default constructor
     */
    public RequestController()
    {
        super("Posterer");

        status_fade.setFromValue(1.0);
        status_fade.setToValue(0.0);
        status_fade.setCycleCount(4);
        status_fade.setAutoReverse(true);

        propertiesModel = App.INJECTOR
                .getInstance(PropertiesModel.class);

        logger.debug("Instantiated.");
    }


    /**
     * Transfers properties to the UI
     */
    private void processProperties()
    {
        properties.clear();

        if (preferencesAvailable && propertiesModel != null)
        {

            List<RequestData> props = propertiesModel.getData();

            properties.addAll(props);

            Set<String> endpoints = new TreeSet<String>();

            for (RequestData prop : props)
            {
                endpoints.add(prop.getEndpoint());
            }

            endpoint.getItems().setAll(endpoints);
        }

    }


    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.controllers.Controller#initialize()
     */
    @Override
    public void initialize()
    {
        logger.debug("Initialization begins.");

        certificateFile.setRoot(getRoot());

        propertiesTable.addEventFilter(MouseEvent.MOUSE_CLICKED,
                new EventHandler<MouseEvent>()
                {
                    @Override
                    public void handle(MouseEvent event)
                    {
                        if (event.getClickCount() > 1
                                && event.getButton().ordinal() == 1)
                        {
                            loadRequest(propertiesTable.getSelectionModel()
                                    .getSelectedItem());
                        }
                    }
                });

        propertiesTable.setItems(properties);
        endpointColumn
                .setCellValueFactory(new PropertyValueFactory<RequestData, String>(
                        "endpoint"));
        payloadColumn
                .setCellValueFactory(new PropertyValueFactory<RequestData, String>(
                        "payload"));
        methodColumn
                .setCellValueFactory(new PropertyValueFactory<RequestData, String>(
                        "method"));
        contentTypeColumn
                .setCellValueFactory(new PropertyValueFactory<RequestData, String>(
                        "contentType"));
        base64Column
                .setCellValueFactory(new PropertyValueFactory<RequestData, Boolean>(
                        "base64"));
        httpUserColumn
                .setCellValueFactory(new PropertyValueFactory<RequestData, String>(
                        "httpUser"));
        httpPasswordColumn
                .setCellValueFactory(new PropertyValueFactory<RequestData, String>(
                        "httpPassword"));

        processProperties();

        /*
         * Bindings
         */
        timeoutText.textProperty().bind(timeout.asString("%d"));
        timeout.bind(timeoutSlider.valueProperty());

        useProxy.bindBidirectional(proxyToggle1.selectedProperty());
        useProxy.bindBidirectional(proxyToggle2.selectedProperty());
        useProxy.bindBidirectional(proxyToggle3.selectedProperty());
        useProxy.bindBidirectional(proxyToggle4.selectedProperty());
        useProxy.bindBidirectional(proxyToggle5.selectedProperty());

        proxyToggle1.textProperty().bind(useProxyText);
        proxyToggle2.textProperty().bind(useProxyText);
        proxyToggle3.textProperty().bind(useProxyText);
        proxyToggle4.textProperty().bind(useProxyText);
        proxyToggle5.textProperty().bind(useProxyText);

        proxyportlabel.textFillProperty().bind(
                proxyhostlabel.textFillProperty());
        proxyuserlabel.textFillProperty().bind(
                proxyhostlabel.textFillProperty());
        proxypasswordlabel.textFillProperty().bind(
                proxyhostlabel.textFillProperty());

        proxyhost.disableProperty().bind(useProxy.not());
        proxyport.disableProperty().bind(useProxy.not());
        proxyuser.disableProperty().bind(useProxy.not());
        proxypassword.disableProperty().bind(useProxy.not());

        /*
         * Preferences
         */

        proxyhost.textProperty().set(requestModel.getProxyHost());
        proxyport.textProperty().set(requestModel.getProxyPort());
        proxyuser.textProperty().set(requestModel.getProxyUser());
        proxypassword.textProperty().set(requestModel.getProxyPass());

        try
        {
            homedir.textProperty().set(propertiesModel.getPropertiesDir());
        }
        catch (IOException e)
        {
            store.setDisable(true);
            status.textProperty().set(
                    String.format(INFO_PROPERTIES, e.getMessage()));
        }

        logger.debug("Initialization complete.");
    }


    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.controllers.AbstractController#getRoot()
     */
    @Override
    public Parent getRoot()
    {
        return root;
    }


    /*
     * ---------- Events -------------------
     */

    /**
     * User asks to save the current request configuration via (@code save}
     * button.
     */
    public void saveCurrentConfig()
    {
        updateRequest();
        propertiesModel.addData(requestBean);
        processProperties();
    }


    /**
     * Fire event - User hits the {@code Fire} button
     * 
     * @throws IOException
     */
    public void fire() throws IOException
    {
        logger.debug("Fire begins.");

        if (StringUtils.isNotBlank(endpoint.getValue()))
            return;

        progress.setVisible(true); // Show we're busy

        try
        {
            updateRequest();

            URI uri = new URI(StringUtils.trim(endpoint.getValue()));

            endpoint.getItems().add(uri.toString());

            /* Fire off the request */
            ResponseModel response = requestModel.doRequest(requestBean);

            /* Feedback to Request status panel */
            status.setText(String.format(INFO_FIRED,
                    response.getReferenceId(), response.getMethod(),
                    response.getUri()));

            /*
             * Open the Response window managing this request instance
             */
            Stage stage = new Stage();
            ResponseController controller =
                    (ResponseController) ResponseController
                            .loadController(ResponseController.FXML);
            controller.setStage(stage);
            controller.updateStage(response);
            stage.show();

        }
        catch (URISyntaxException e)
        // uri did not compute
        {
            logger.debug("Fire endpoint is not a URI.");
            tabs.getSelectionModel().select(destination);
            status.setText(ERROR_URL_VALIDATION);
            // status_fade = new FadeTransition(Duration.millis(5000), status);
            // status_fade.play();
        }
        finally
        {
            progress.setVisible(false); // No longer busy
        }

        logger.debug("Fire ends.");
    }


    /**
     * Proxy Toggle event - Use toggles the {@code Proxy} button.
     * <p>
     * The {@code useProxy} property has already received any change prior to
     * toggleProxy being fired from the FXML. Could have used a {@code Listener}
     * in the {@code initialize} method where the bindings are done, but using
     * the {@code onAction} methods is easier.
     */
    public void toggleProxy()
    {
        if (useProxy.get())
        // The useProxy is set, so set the proxy
        {
            useProxyText.setValue(LEGEND_PROXY_ON);
            proxyhostlabel.setTextFill(CONST_PAINT_BLACK);

            requestModel.setProxyHost(proxyhost.getText());
            requestModel.setProxyPort(proxyport.getText());
            requestModel.setProxyUser(proxyuser.getText());
            requestModel.setProxyPass(proxypassword.getText());

        }
        else
        // useProxy is unset, so unset the proxy
        {
            useProxyText.setValue(LEGEND_PROXY_OFF);
            proxyhostlabel.setTextFill(CONST_PAINT_GREY);

            requestModel.setProxyHost(null);
            requestModel.setProxyPort(null);
            requestModel.setProxyUser(null);
            requestModel.setProxyPass(null);
        }

        requestModel.useProxy(useProxy.get());
    }


    public void propertySelected()
    {
        // TODO Implement
        System.out.println("Property Selected");
    }


    /**
     * Validate the security Certificate selected
     */
    public void certificateValidate()
    {
        // TODO Implement
        System.out.println("Validate Certificate");
        System.out.println(certificateFile.getValue());
        System.out.println(certificatePassword.getText());
    }


    /**
     * Update the request bean from the values bound to the window
     */
    public void updateRequest()
    {
        requestBean.setEndpoint(StringUtils.trim(endpoint.getValue()));
        requestBean.setMethod(method.getValue());
        requestBean.setPayload(StringUtils.trim(payload.getText()));
        requestBean.setContentType(mime.getValue());
        requestBean.setBase64(encode.isSelected());
        requestBean.setHttpUser(StringUtils.trim(user.getText()));
        requestBean.setHttpPassword(StringUtils.trim(password.getText()));
    }


    /**
     * Loads a {@code Request} into the {@code RequestController} bound vars.
     * 
     * @param requestdata
     *            the {@code Request} to pull into the controller
     */
    private void loadRequest(RequestData requestdata)
    {
        endpoint.setValue(requestdata.getEndpoint());
        method.setValue(requestdata.getMethod());
        payload.setText(requestdata.getPayload());
        mime.setValue(requestdata.getContentType());
        encode.setSelected(requestdata.getBase64());
        user.setText(requestdata.getHttpUser());
        password.setText(requestdata.getHttpPassword());
        status.appendText(String.format("\nLoaded request for endpoint:[%1$s]",
                requestdata.getEndpoint()));
    }

}
