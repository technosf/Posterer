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
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.technosf.posterer.App;
import com.github.technosf.posterer.modules.Properties;
import com.github.technosf.posterer.modules.Request;
import com.github.technosf.posterer.modules.RequestBean;
import com.github.technosf.posterer.ui.components.FileChooserComboBox;
import com.github.technosf.posterer.ui.controllers.Controller;
import com.github.technosf.posterer.ui.controllers.impl.base.AbstractController;
import com.github.technosf.posterer.ui.models.KeyStoreBean;
import com.github.technosf.posterer.ui.models.KeyStoreBean.KeyStoreBeanException;
import com.github.technosf.posterer.ui.models.RequestModel;
import com.github.technosf.posterer.ui.models.ResponseModel;
import com.github.technosf.posterer.ui.models.StatusModel;
import com.github.technosf.posterer.utils.PrettyPrinters;

import javafx.animation.FadeTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

/**
 * Controller for the Request window.
 * 
 * @author technosf
 * @since 0.0.1
 * @version 0.0.1
 */
//@SuppressWarnings("null")
public class RequestController
        extends AbstractController
        implements Controller
{
    /**
     * The FXML definition of the View
     */
    public static final String FXML = "/fxml/Request.fxml";

    /* ---- Constants ----- */

    private static final Logger LOG = LoggerFactory
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

    /* ---- Private state vars ----- */

    private final Properties propertiesModel;

    private final RequestModel requestModel = App.INJECTOR
            .getInstance(RequestModel.class);

    /**
     * The request data to reflect in this window
     */
    private final RequestBean requestBean = new RequestBean();

    private final boolean preferencesAvailable = true;

    private StatusController statusController;
    private StatusModel status;

    /*
     * ------------ FXML Components -----------------
     */

    @FXML
    private ComboBox<String> endpoint, endpointFilter, useAlias;

    @FXML
    private FileChooserComboBox certificateFileChooser;

    @FXML
    private TextField timeoutText, user, proxyhost,
            proxyport, proxyuser, proxypassword, homedir;

    @FXML
    private Label proxyhostlabel, proxyportlabel, proxyuserlabel,
            proxypasswordlabel;
    @FXML
    private PasswordField password, certificatePassword;

    @FXML
    private Slider timeoutSlider;

    @FXML
    private TextArea statusWindow, payload;

    @FXML
    private ProgressIndicator progress;

    @FXML
    private Button fire1, fire2, fire3, fire4, fire5, save,
            validateCertificate;

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
    private TableView<Request> propertiesTable;

    @FXML
    private TableColumn<Request, String> endpointColumn, payloadColumn,
            methodColumn, contentTypeColumn, httpUserColumn,
            httpPasswordColumn;
    @FXML
    private TableColumn<Request, Boolean> base64Column;

    private RadioButton payloadWrap = new RadioButton("Wrap");
    private CustomMenuItem payloadWrapMI = new CustomMenuItem(payloadWrap);
    private MenuItem payloadFormat = new MenuItem("Format");
    private ContextMenu payloadCM =
            new ContextMenu(payloadWrapMI, payloadFormat);

    /*
     * ------------ FXML Bindings -----------------
     */
    IntegerProperty timeout = new SimpleIntegerProperty();

    BooleanProperty useProxy = new SimpleBooleanProperty(
            requestModel.getUseProxy());

    StringProperty useProxyText = new SimpleStringProperty(useProxy.get()
            ? LEGEND_PROXY_ON : LEGEND_PROXY_OFF);

    ObservableList<Request> properties = FXCollections
            .observableArrayList();

    FilteredList<Request> filteredProperties =
            new FilteredList<>(properties, p -> true);
    SortedList<Request> sortedProperties = new SortedList<>(filteredProperties);

    /*
     * ------------ FXML Helpers -----------------
     */

    private final FadeTransition status_fade = new FadeTransition(
            Duration.millis(5000), statusWindow);

    /*
     * ================== Code =====================
     */


    /*
     * ------------ Statics -----------------
     */
    /**
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
    public RequestController()
    {
        super("Posterer");

        status_fade.setFromValue(1.0);
        status_fade.setToValue(0.0);
        status_fade.setCycleCount(4);
        status_fade.setAutoReverse(true);

        propertiesModel = App.INJECTOR
                .getInstance(Properties.class);

        LOG.debug("Instantiated");
    }


    /*
     * ------------ Initiators -----------------
     */

    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.ui.controllers.Controller#initialize()
     */
    @Override
    public void initialize()
    {
        LOG.debug("Initialization starts");

        //TODO Refactor status window
        /*
         * Status windows
         */
        statusWindow.textProperty().addListener(new ChangeListener<Object>()
        {
            @Override
            public void changed(ObservableValue<?> observable, Object oldValue,
                    Object newValue)
            {
                statusWindow.setScrollTop(Double.MAX_VALUE); //this will scroll to the bottom
                //use Double.MIN_VALUE to scroll to the top
            }
        });
        statusController =
                StatusController.loadController(statusWindow.textProperty());
        statusController.setStyle(getStyle());
        status = statusController.getStatusModel();

        /*
         * Bulk initializations
         */
        initializeCertificateFileChooser();

        initializeProperties();

        initializeBindings();

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
            status.write(INFO_PROPERTIES, e.getMessage());
        }

        payloadFormat.setOnAction(new EventHandler<ActionEvent>()
        {
            public void handle(ActionEvent e)
            {
                payload.setText(PrettyPrinters.xml(payload.getText(), true));
            }
        });

        LOG.debug("Initialization complete");
    }


    /**
     * Initialize the bindings
     */
    private void initializeBindings()
    {
        LOG.debug("Initializing Bindings");

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

        payload.wrapTextProperty().bind(payloadWrap.selectedProperty().not());
    }


    /**
     * Initialize the certificateFileChooser
     */
    private void initializeCertificateFileChooser()
    {
        LOG.debug("Initializing Certificate File Chooser");

        certificateFileChooser.setRoot(getRoot());
        certificateFileChooser.getChosenFileProperty().addListener(
                new ChangeListener<File>()
                {
                    @Override
                    public void changed(ObservableValue<? extends File> arg0,
                            File oldValue, File newValue)
                    {
                        setCertificateFile(newValue);
                    }
                });
    }


    /**
     * Initialize the properties
     */
    private void initializeProperties()
    {
        LOG.debug("Initializing Properties");

        propertiesTable.setRowFactory(
                new Callback<TableView<Request>, TableRow<Request>>()
                {
                    @Override
                    public TableRow<Request> call(TableView<Request> tableView)
                    {
                        final TableRow<Request> row = new TableRow<>();

                        final ContextMenu rowMenu = new ContextMenu();

                        ContextMenu tableMenu = tableView.getContextMenu();
                        if (tableMenu != null)
                        {
                            rowMenu.getItems().addAll(tableMenu.getItems());
                            rowMenu.getItems().add(new SeparatorMenuItem());
                        }
                        MenuItem removeItem = new MenuItem("Delete");
                        removeItem.setOnAction(new EventHandler<ActionEvent>()
                        {
                            @Override
                            public void handle(ActionEvent e)
                            {
                                propertiesModel.removeData(row.getItem());
                                properties.remove(row.getItem());
                            }
                        });
                        rowMenu.getItems().addAll(removeItem);
                        row.contextMenuProperty().bind(
                                Bindings.when(
                                        Bindings.isNotNull(row.itemProperty()))
                                        .then(rowMenu)
                                        .otherwise((ContextMenu) null));
                        return row;
                    }
                });

        propertiesTable.addEventFilter(MouseEvent.MOUSE_CLICKED,
                event -> {
                    if (event.getClickCount() > 1
                            && event.getButton().equals(MouseButton.PRIMARY))
                    {
                        loadRequest(propertiesTable.getSelectionModel()
                                .getSelectedItem());
                    }
                });

        sortedProperties.comparatorProperty()
                .bind(propertiesTable.comparatorProperty());
        propertiesTable.setItems(sortedProperties);

        endpointColumn
                .setCellValueFactory(
                        new PropertyValueFactory<Request, String>(
                                "endpoint"));
        payloadColumn
                .setCellValueFactory(
                        new PropertyValueFactory<Request, String>(
                                "payload"));
        methodColumn
                .setCellValueFactory(
                        new PropertyValueFactory<Request, String>(
                                "method"));
        contentTypeColumn
                .setCellValueFactory(
                        new PropertyValueFactory<Request, String>(
                                "contentType"));
        base64Column
                .setCellValueFactory(
                        new PropertyValueFactory<Request, Boolean>(
                                "base64"));
        httpUserColumn
                .setCellValueFactory(
                        new PropertyValueFactory<Request, String>(
                                "httpUser"));
        httpPasswordColumn
                .setCellValueFactory(
                        new PropertyValueFactory<Request, String>(
                                "httpPassword"));

        processProperties();
    }


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
        LOG.debug("Fire  --  Starts");

        if (StringUtils.isBlank(endpoint.getValue()))
            return;

        progress.setVisible(true); // Show we're busy

        try
        {
            updateRequest();

            URI uri = new URI(StringUtils.trim(endpoint.getValue()));

            if (!endpoint.getItems().contains(uri.toString()))
            /* 
             * Add endpoint if not already added
             * 
             */
            {
                endpoint.getItems().add(uri.toString());
            }

            /* Fire off the request */
            ResponseModel response = requestModel.doRequest(requestBean.copy());

            /* Feedback to Request status panel */
            status.append(INFO_FIRED,
                    response.getReferenceId(), response.getRequestBean()
                            .getMethod(),
                    response.getRequestBean().getUri());

            /*
             * Open the Response window managing this request instance
             */
            ResponseController.loadStage(response).show();
        }
        catch (URISyntaxException e)
        /*
         *  uri did not compute
         */
        {
            LOG.debug("Fire endpoint is not a URI.");
            tabs.getSelectionModel().select(destination);
            status.write(ERROR_URL_VALIDATION);
            // status_fade = new FadeTransition(Duration.millis(5000), status);
            // status_fade.play();
        }
        finally
        /*
         *  Clear the progress ticker
         */
        {
            progress.setVisible(false); // No longer busy
        }

        LOG.debug("Fire  --  ends");
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
        /*
         *  The useProxy is set, so set the proxy
         */
        {
            useProxyText.setValue(LEGEND_PROXY_ON);
            proxyhostlabel.setTextFill(CONST_PAINT_BLACK);

            requestModel.setProxyHost(proxyhost.getText());
            requestModel.setProxyPort(proxyport.getText());
            requestModel.setProxyUser(proxyuser.getText());
            requestModel.setProxyPass(proxypassword.getText());

        }
        else
        /*
         *  useProxy is unset, so unset the proxy
         */
        {
            useProxyText.setValue(LEGEND_PROXY_OFF);
            proxyhostlabel.setTextFill(CONST_PAINT_GREY);

            requestModel.setProxyHost(null);
            requestModel.setProxyPort(null);
            requestModel.setProxyUser(null);
            requestModel.setProxyPass(null);
        }

        requestModel.setUseProxy(useProxy.get());
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
     * Validate the security Certificate selected
     * <p>
     * Loads the certificate and give it to the cert viewer
     */
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
            useAlias.itemsProperty().setValue(aliases);
            useAlias.setDisable(false);
        }
        catch (KeyStoreBeanException e)
        {
            LOG.debug(e.getMessage(), e);
            status.append(
                    "Certificate file cannot be opened: [%1$s]", e.getCause()
                            .getMessage());
        }
    }


    /**
     * Format the payload
     */
    public void onPayloadSelected(MouseEvent mouseEvent)
    {
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY)
                && mouseEvent.getClickCount() == 3)
        {
            payloadCM.show(payload, mouseEvent.getScreenX(),
                    mouseEvent.getScreenY());
        }
    }


    /**
     * Open the stand alone status window
     */
    public void onStatusSelected(MouseEvent mouseEvent)
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
     * Transfers properties to the UI
     */
    private void processProperties()
    {

        LOG.debug("processing Properties");

        properties.clear();

        if (!preferencesAvailable || propertiesModel == null)
            return;

        List<Request> props = propertiesModel.getData(); // Get properties

        /*
         * populate endpoint drop down
         */
        Set<String> endpoints = new TreeSet<String>();

        for (Request prop : props)
        {
            endpoints.add(prop.getEndpoint());
        }

        endpoint.getItems().setAll(endpoints);
        endpointFilter.getItems().setAll("");
        endpointFilter.getItems().addAll(endpoints);

        /*
         * Create properties table
         */
        endpointFilter.valueProperty()
                .addListener((observable, oldValue, newValue) -> {
                    filteredProperties.setPredicate(request -> {
                        // If filter text is empty, display all requests.
                        if (newValue == null || newValue.isEmpty())
                        {
                            return true;
                        }

                        // Compare first name and last name of every person with filter text.
                        String lowerCaseFilter = newValue.toLowerCase();

                        if (request.getEndpoint().toLowerCase()
                                .contains(lowerCaseFilter))
                        {
                            return true; // Filter matches first name.
                        }

                        return false; // Does not match.
                    });
                });

        properties.addAll(props);

    }


    /**
     * Loads a {@code Request} into the {@code RequestController} bound vars.
     * 
     * @param requestdata
     *            the {@code Request} to pull into the controller
     */
    private void loadRequest(Request requestdata)
    {
        if (requestdata == null)
        {
            return;
        }

        endpoint.setValue(requestdata.getEndpoint());
        method.setValue(requestdata.getMethod());
        payload.setText(requestdata.getPayload());
        mime.setValue(requestdata.getContentType());
        encode.setSelected(requestdata.getBase64());
        user.setText(requestdata.getHttpUser());
        password.setText(requestdata.getHttpPassword());

        status.append("Loaded request for endpoint:[%1$s]",
                requestdata.getEndpoint());
    }


    /**
     * Assures the existence of the certificate file selection and configures
     * the UI.
     * 
     * @param file
     *            the new certificate file
     */
    private void setCertificateFile(File file)
    {
        if (file != null
                && (!file.exists() || !file.canRead()))
        /*
         *  Chosen file cannot be read, turn off FXML assets
         */
        {
            status.append(
                    "Certificate file cannot be read: [{}]",
                    file.getPath());
            certificatePassword.setDisable(true);
            useAlias.setDisable(true);
            validateCertificate.setDisable(true);
            return;
        }

        // Switch off Cert-file FXML assets if the file is null
        certificatePassword.setDisable(file == null);
        // useCertificate.setDisable(file == null);
        validateCertificate.setDisable(file == null);
    }

}
