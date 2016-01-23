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
package com.github.technosf.posterer.ui.controllers.impl.base;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.technosf.posterer.models.Request;
import com.github.technosf.posterer.models.ResponseModel;
import com.github.technosf.posterer.models.StatusModel;
import com.github.technosf.posterer.models.impl.ProxyBean;
import com.github.technosf.posterer.models.impl.RequestBean;
import com.github.technosf.posterer.ui.controllers.Controller;
import com.github.technosf.posterer.ui.controllers.impl.ResponseController;
import com.github.technosf.posterer.ui.controllers.impl.StatusController;
import com.github.technosf.posterer.ui.custom.controls.FileChooserComboBox;
import com.github.technosf.posterer.utils.PrettyPrinters;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
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

/**
 * Abstract Controller for the Request window that holds the FXML objects and
 * display relationships
 * <p>
 * Listeners are provided for timeout and proxy changes to modify the underlying
 * request model.
 * 
 * @author technosf
 * @since 0.0.1
 * @version 0.0.1
 */
public abstract class AbstractRequestController
        extends AbstractController
        implements Controller
{
    /*
     * ------------ FXML Components -----------------
     */

    @FXML
    protected ComboBox<URI> endpoint;

    @FXML
    protected ComboBox<ProxyBean> proxyCombo;

    @FXML
    protected ComboBox<String> endpointFilter, useCertificateAlias;

    @FXML
    protected FileChooserComboBox certificateFileChooser;

    @FXML
    protected TextField timeoutText, proxyHost, proxyPort, proxyUser,
            proxyPassword, homedir;

    @FXML
    protected Label proxyComboLabel, proxyHostLabel, proxyPortLabel,
            proxyUserLabel, proxyPasswordLabel;

    @FXML
    protected PasswordField password, certificatePassword;

    @FXML
    protected Slider timeoutSlider;

    @FXML
    protected TextArea statusWindow, payload;

    @FXML
    protected ProgressIndicator progress;

    @FXML
    protected Button fire1, fire2, fire3, fire4, fire5, save,
            validateCertificate, saveProxy, closeresponses;

    @FXML
    protected ToggleButton proxyToggle1, proxyToggle2, proxyToggle3,
            proxyToggle4, proxyToggle5;

    @FXML
    protected ChoiceBox<String> method, mime, security;

    @FXML
    protected RadioButton encode;

    @FXML
    protected TabPane tabs;

    @FXML
    protected Tab destination, configuration, store;

    @FXML
    protected StackPane stack;

    @FXML
    protected TableView<Request> propertiesTable;

    @FXML
    protected TableColumn<Request, String> endpointColumn, payloadColumn,
            methodColumn, securityColumn, contentTypeColumn;

    @FXML
    protected TableColumn<Request, Boolean> base64Column;

    /* Context Menus */
    protected RadioButton payloadWrap = new RadioButton("Wrap");
    protected CustomMenuItem payloadWrapMI = new CustomMenuItem(payloadWrap);
    protected MenuItem payloadFormat = new MenuItem("Format");
    protected ContextMenu payloadCM =
            new ContextMenu(payloadWrapMI, payloadFormat);

    /* ---- State vars ----- */

    /**
     * The request data to reflect in this window
     */
    protected final RequestBean requestBean = new RequestBean();

    protected final boolean preferencesAvailable = true;

    protected ProxyBean proxyBean = new ProxyBean();

    protected StatusController statusController;

    protected StatusModel status;

    private List<Stage> responseStages = new ArrayList<>();

    /* ---- Display Constants ----- */

    /**
     * The FXML definition of the View
     */
    public static final String FXML = "/fxml/Request.fxml";

    private static final Logger LOG =
            LoggerFactory.getLogger(AbstractRequestController.class);

    private static final String INFO_URI =
            "Error :: URI is not valid: %1$s";

    private static final String INFO_PROPERTIES =
            "Error :: Cannot store endpoints and requests: %1$s";

    private static final String INFO_FIRED =
            "Fired request #%1$d:   Method [%2$s]   Endpoint [%3$s]  %4$s";

    private static final String LEGEND_PROXY_ON = "Proxy On";
    private static final String LEGEND_PROXY_OFF = "Proxy Off";
    private static final Paint CONST_PAINT_BLACK = Paint.valueOf("#292929");
    private static final Paint CONST_PAINT_GREY = Paint.valueOf("#808080");

    @NonNull
    private static final String CONST_PROVIDE_PROXY =
            "Proxy selected. Please provide a valid proxy";
    @NonNull
    private static final String CONST_NO_PROXY =
            "Proxy deselected.";

    /*
     * ------------ FXML Bindings -----------------
     */

    /*
     * The time out
     */
    private IntegerProperty timeoutProperty = new SimpleIntegerProperty();

    /**
     * Fire enabled?
     */
    protected BooleanProperty fireDisabledProperty =
            new SimpleBooleanProperty(false);

    /**
     * Use a proxy?
     */
    protected BooleanProperty proxyOnProperty =
            new SimpleBooleanProperty(false);

    /*
     * Toggle proxy button text
     */
    private StringProperty useProxyTextProperty = new ReadOnlyStringWrapper(
            proxyOnProperty.get() ? LEGEND_PROXY_ON : LEGEND_PROXY_OFF);

    protected ObservableList<Request> requestPropertiesList =
            FXCollections.observableArrayList();

    private FilteredList<Request> filteredRequestPropertiesList =
            new FilteredList<>(requestPropertiesList, p -> true);

    private SortedList<Request> sortedRequestPropertiesList =
            new SortedList<>(filteredRequestPropertiesList);

    protected ObservableList<String> securityChoicesList =
            FXCollections.observableArrayList();

    private SortedList<String> securityChoices =
            new SortedList<>(securityChoicesList);


    /*
     * ---------- Code ------------------
     */

    /**
     * Instantiate with the given title
     * 
     * @param title
     */
    protected AbstractRequestController(String title)
    {
        super(title);
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

        statusController =
                StatusController.loadController(statusWindow.textProperty());
        statusController.setStyle(getStyle());
        status = statusController.getStatusModel();

        /*
         * Bulk initializations
         */

        initializeValues();

        initializeListeners();

        initializeProperties();

        initializeBindings();

        LOG.debug("Initializing Others");

        initializeOther();

        LOG.debug("Initialization complete");

    }


    /**
     * Initialize listeners
     */
    private void initializeValues()
    {

    }


    /**
     * Initialize listeners
     */
    private void initializeListeners()
    {

        LOG.debug("Initializing Listeners");

        endpoint.getEditor().focusedProperty()
                .addListener((observable, oldValue, newValue) -> {
                    endpointValidate(endpoint.getEditor().getText());
                });

        proxyHost.focusedProperty()
                .addListener((observable, oldValue, newValue) -> {
                    proxyUpdate();
                });

        proxyPort.focusedProperty()
                .addListener((observable, oldValue, newValue) -> {
                    proxyUpdate();
                });

        proxyUser.focusedProperty()
                .addListener((observable, oldValue, newValue) -> {
                    proxyUpdate();
                });

        proxyUser.focusedProperty()
                .addListener((observable, oldValue, newValue) -> {
                    proxyUpdate();
                });

        proxyPassword.focusedProperty()
                .addListener((observable, oldValue, newValue) -> {
                    proxyUpdate();
                });

        /*
         * Listener to manage the proxy
         */
        proxyCombo.valueProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue != null)
                    {
                        proxyHost.setText(newValue.getProxyHost());
                        proxyPort.setText(newValue.getProxyPort());
                        proxyUser.setText(newValue.getProxyUser());
                        proxyPassword.setText(newValue.getProxyPassword());
                        proxyUpdate();
                    }
                });

        /*
         * Listener to manage the endpoint filter
         */
        endpointFilter.valueProperty()
                .addListener((observable, oldValue, newValue) -> {
                    filteredRequestPropertiesList.setPredicate(

                            request -> {
                        /*
                         * If filter text is empty, display all requests.
                         */
                        if (newValue == null || newValue.isEmpty())
                        {
                            return true;
                        }

                        /*
                         * Compare each endpoint to see if it starts with the filter.
                         */
                        if (request.getEndpoint().toLowerCase().trim()
                                .startsWith(newValue.toLowerCase().trim()))
                        {
                            return true; // Filter matches first name.
                        }

                        return false; // Does not match.
                    });
                });

        /* 
         * Listener to manage the certificate file 
         */
        certificateFileChooser.chosenFileProperty()
                .addListener((observable, oldValue, newValue) -> {
                    certificateFile(newValue);
                });
    }


    /**
     * Initialize the bindings
     */
    private void initializeBindings()
    {
        LOG.debug("Initializing Bindings");

        timeoutText.textProperty().bind(timeoutProperty.asString("%d"));
        timeoutProperty.bind(timeoutSlider.valueProperty());

        /*
         * Disable fire buttons
         */
        fire1.disableProperty().bind(fireDisabledProperty);
        fire2.disableProperty().bind(fireDisabledProperty);
        fire3.disableProperty().bind(fireDisabledProperty);
        fire4.disableProperty().bind(fireDisabledProperty);
        fire5.disableProperty().bind(fireDisabledProperty);

        /*
         * Bidirectionally Bind the proxy buttons to a single property
         * so that when one button is clicked they all are
         */
        proxyOnProperty.bindBidirectional(proxyToggle1.selectedProperty());
        proxyOnProperty.bindBidirectional(proxyToggle2.selectedProperty());
        proxyOnProperty.bindBidirectional(proxyToggle3.selectedProperty());
        proxyOnProperty.bindBidirectional(proxyToggle4.selectedProperty());
        proxyOnProperty.bindBidirectional(proxyToggle5.selectedProperty());

        /*
         * Bind proxy button text to single property
         */
        proxyToggle1.textProperty().bind(useProxyTextProperty);
        proxyToggle2.textProperty().bind(useProxyTextProperty);
        proxyToggle3.textProperty().bind(useProxyTextProperty);
        proxyToggle4.textProperty().bind(useProxyTextProperty);
        proxyToggle5.textProperty().bind(useProxyTextProperty);

        // Link proxy fields together from host
        proxyHostLabel.textFillProperty()
                .bind(proxyComboLabel.textFillProperty());
        proxyPortLabel.textFillProperty()
                .bind(proxyComboLabel.textFillProperty());
        proxyUserLabel.textFillProperty()
                .bind(proxyComboLabel.textFillProperty());
        proxyPasswordLabel.textFillProperty()
                .bind(proxyComboLabel.textFillProperty());

        proxyCombo.disableProperty().bind(proxyOnProperty.not());
        proxyHost.disableProperty().bind(proxyOnProperty.not());
        proxyPort.disableProperty().bind(proxyOnProperty.not());
        proxyUser.disableProperty().bind(proxyOnProperty.not());
        proxyPassword.disableProperty().bind(proxyOnProperty.not());
        saveProxy.disableProperty().bind(proxyOnProperty.not());

        payload.wrapTextProperty().bind(payloadWrap.selectedProperty().not());
    }


    /**
     * Initialize the properties sub system
     */
    @SuppressWarnings("null")
    private void initializeProperties()
    {
        LOG.debug("Initializing Properties");

        /*
         * Preferences
         */
        try
        {
            homedir.textProperty().set(propsDirectory());
        }
        catch (IOException e)
        {
            store.setDisable(true);
            status.write(INFO_PROPERTIES, e.getMessage());
        }

        payloadFormat.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent e)
            {
                payload.setText(PrettyPrinters.xml(payload.getText(), true));
            }
        });

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
                                propsRemoveRequest(row.getItem());
                                requestPropertiesList.remove(row.getItem());
                            }
                        });
                        rowMenu.getItems().addAll(removeItem);
                        row.contextMenuProperty()
                                .bind(Bindings
                                        .when(Bindings
                                                .isNotNull(row.itemProperty()))
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
                        requestLoad(propertiesTable.getSelectionModel()
                                .getSelectedItem());
                    }
                });

        sortedRequestPropertiesList.comparatorProperty()
                .bind(propertiesTable.comparatorProperty());

        propertiesTable.setItems(sortedRequestPropertiesList);

        security.setItems(securityChoices);

        endpointColumn.setCellValueFactory(
                new PropertyValueFactory<Request, String>("endpoint"));
        payloadColumn.setCellValueFactory(
                new PropertyValueFactory<Request, String>("payload"));
        methodColumn.setCellValueFactory(
                new PropertyValueFactory<Request, String>("method"));
        securityColumn.setCellValueFactory(
                new PropertyValueFactory<Request, String>("security"));
        contentTypeColumn.setCellValueFactory(
                new PropertyValueFactory<Request, String>("contentType"));
        base64Column.setCellValueFactory(
                new PropertyValueFactory<Request, Boolean>("base64"));

        LOG.debug("Processing Properties");
        propsProcess();
    }


    /**
     * Other initialization tasks
     */
    protected abstract void initializeOther();


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
     * Close all open response windows
     */
    public final void closeResponses()
    {
        responseStages.stream().forEach(stage -> stage.close());
        closeresponses.setDisable(true);
    }


    /**
     * Fire event - User hits the {@code Fire} button
     * <p>
     * Create a response task and fires it off in the back ground.
     * 
     * @throws IOException
     */
    @SuppressWarnings("null")
    public final void fire()
    {
        LOG.debug("Fire  --  Starts");

        progress.setVisible(true); // Show we're busy

        Object urivalue = endpoint.getValue();

        try
        {
            requestUpdate();
            proxyUpdate();
            URI uri = null;
            if (URI.class.isInstance(urivalue))
            {
                uri = (URI) urivalue;
            }
            else
            {
                uri = new URI((String) urivalue);
            }

            if (!endpoint.getItems().contains(uri))
            /*
             * Add endpoint if not already added
             * 
             */
            {
                endpoint.getItems().add(uri);
            }

            /* 
             * Fire off the request 
             */
            ResponseModel response = requestFire(requestBean.copy());

            /* Feedback to Request status panel */
            status.append(INFO_FIRED, response.getReferenceId(),
                    response.getRequest().getMethod(),
                    response.getRequest().getUri(),
                    proxyOnProperty.get() == true
                            ? proxyCombo.getValue().toString() : "");
            statusWindow.setScrollTop(Double.MAX_VALUE);

            /*
             * Open the Response window managing this request instance
             */
            Controller controller = ResponseController.loadStage(response);
            String style = this.getStyle();
            controller.setStyle(style);
            Stage stage = controller.getStage();
            responseStages.add(stage);
            closeresponses.setDisable(false);
            if (stage == null)
            {
                LOG.error("Could not get stage");
            }
            else
            {
                stage.show();
            }
        }
        catch (URISyntaxException e)
        {
            status.append(INFO_URI, urivalue);
            statusWindow.setScrollTop(Double.MAX_VALUE);
        }
        finally
        /*
         * Clear the progress ticker
         */
        {
            progress.setVisible(false); // No longer busy
            fireDisabledProperty.set(false);
        }

        LOG.debug("Fire  --  ends");
    }


    /**
     * Enable the proxy fields
     * 
     * @param enable
     *            true to enable
     */
    protected final void proxyEnableFields(boolean enable)
    {
        if (enable)
        /*
         * Enable proxy, protect fire button
         */
        {

            LOG.debug("Enabling Proxy");

            useProxyTextProperty.setValue(LEGEND_PROXY_ON);
            proxyComboLabel.setTextFill(CONST_PAINT_BLACK);
            saveProxy.setTextFill(CONST_PAINT_BLACK);
            if ((proxyCombo.getValue() == null
                    || !proxyCombo.getValue().isActionable())
                    && !fireDisabledProperty.get())
            /*
             * proxy is not actionable and fire is not disabled, so disable fore
             */
            {

                LOG.debug("Bad Proxy");

                fireDisabledProperty.set(false);
                if (!CONST_PROVIDE_PROXY.equals(status.lastMessage()))
                {
                    status.append(CONST_PROVIDE_PROXY);
                    statusWindow.setScrollTop(Double.MAX_VALUE);
                }
            }
        }
        else
        /*
         * Disable proxy, unprotect fire button
         */
        {
            LOG.debug("Disabling Proxy");

            useProxyTextProperty.setValue(LEGEND_PROXY_OFF);
            proxyComboLabel.setTextFill(CONST_PAINT_GREY);
            saveProxy.setTextFill(CONST_PAINT_GREY);
            fireDisabledProperty.set(false);
            status.append(CONST_NO_PROXY);
            statusWindow.setScrollTop(Double.MAX_VALUE);
        }
    }


    /**
     * User asks to save the current request configuration via (@code save}
     * button.
     */
    protected abstract void proxySave();


    /**
     * User asks to save the current request configuration via (@code save}
     * button.
     */
    protected abstract void requestSave();


    /**
     * Proxy Toggle event - Use toggles the {@code Proxy} button.
     * <p>
     * The {@code useProxy} property has already received any change prior to
     * toggleProxy being fired from the FXML. Could have used a {@code Listener}
     * in the {@code initialize} method where the bindings are done, but using
     * the {@code onAction} methods is easier.
     */
    protected abstract void proxyToggle();


    /**
     * Update the request bean from the values bound to the window
     */
    protected abstract void requestUpdate();


    /**
     * Update the request bean from the values bound to the window
     */
    protected abstract void proxyUpdate();


    /**
     * Validate the security Certificate selected
     * <p>
     * Loads the certificate and give it to the cert viewer
     */
    protected abstract void certificateValidate();


    /* --------------- Business Functions ----------------- */

    /**
     * Fires a request off and returns the Response
     * 
     * @param request
     *            the request
     * @return the response
     */
    @NonNull
    protected abstract ResponseModel requestFire(
            final @NonNull Request request);


    /**
     * Assures the existence of the certificate file selection and configures
     * the UI.
     * 
     * @param file
     *            the new certificate file
     */
    protected abstract void certificateFile(final @Nullable File file);


    /**
     * Removes the given request from the properties
     * 
     * @param request
     *            the request to remove
     */
    protected abstract void propsRemoveRequest(
            final @NonNull Request request);


    /**
     * Transfers stored properties to the UI properties tab
     */
    protected abstract void propsProcess();


    /**
     * Loads a {@code Request} into the {@code RequestController} bound vars.
     * 
     * @param requestdata
     *            the {@code Request} to pull into the ui
     */
    protected abstract void requestLoad(final @NonNull Request requestdata);


    /**
     * Validate and place the endpoint where needed, set tls etc
     * 
     * @param endpoint
     */
    protected abstract void endpointValidate(final @Nullable String endpoint);


    /**
     * Returns the directory where the properties file reside
     * 
     * @return the directory
     * @throws IOException
     */
    @NonNull
    protected abstract String propsDirectory() throws IOException;
}
