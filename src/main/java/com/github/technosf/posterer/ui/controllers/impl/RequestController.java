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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.technosf.posterer.App;
import com.github.technosf.posterer.models.Properties;
import com.github.technosf.posterer.models.Request;
import com.github.technosf.posterer.models.RequestModel;
import com.github.technosf.posterer.models.ResponseModel;
import com.github.technosf.posterer.models.StatusModel;
import com.github.technosf.posterer.models.impl.KeyStoreBean;
import com.github.technosf.posterer.models.impl.RequestBean;
import com.github.technosf.posterer.models.impl.KeyStoreBean.KeyStoreBeanException;
import com.github.technosf.posterer.models.impl.ProxyBean;
import com.github.technosf.posterer.ui.components.FileChooserComboBox;
import com.github.technosf.posterer.ui.controllers.Controller;
import com.github.technosf.posterer.ui.controllers.impl.base.AbstractController;
import com.github.technosf.posterer.ui.controllers.impl.base.AbstractRequestController;
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
 * <p>
 * Listeners are provided for timeout and proxy changes to modify the underlying request model.
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

	private static final Logger LOG = LoggerFactory.getLogger(RequestController.class);

	// private static final String ERROR_URL_VALIDATION = "URL is invalid.
	// Correct and try again.";

	private static final String INFO_PROPERTIES = "Error :: Cannot store endpoints and requests: %1$s";

	private static final String INFO_FIRED = "Fired request #%1$d:   Method [%2$s]   Endpoint [%3$s] ";

	private static final String LEGEND_PROXY_ON = "Proxy On";
	private static final String LEGEND_PROXY_OFF = "Proxy Off";

	private static final Paint CONST_PAINT_BLACK = Paint.valueOf("#292929");
	private static final Paint CONST_PAINT_GREY = Paint.valueOf("#808080");

	/* ---- Private state vars ----- */

	private final Properties propertiesModel;

	private final RequestModel requestModel = App.INJECTOR.getInstance(RequestModel.class);

	/**
	 * The request data to reflect in this window
	 */
	private final RequestBean requestBean = new RequestBean();
	private final ProxyBean proxyBean = new ProxyBean();

	private final boolean preferencesAvailable = true;

	private StatusController statusController;
	private StatusModel status;

	
	/*
	 * ------------ FXML Bindings -----------------
	 */

	IntegerProperty timeoutProperty = new SimpleIntegerProperty();

	BooleanProperty useProxyProperty = new SimpleBooleanProperty(requestModel.getUseProxy());

	StringProperty useProxyTextProperty = new SimpleStringProperty(useProxyProperty.get() ? LEGEND_PROXY_ON : LEGEND_PROXY_OFF);

	ObservableList<Request> propertiesList = FXCollections.observableArrayList();

	FilteredList<Request> filteredPropertiesList = new FilteredList<>(propertiesList, p -> true);
	SortedList<Request> sortedPropertiesList = new SortedList<>(filteredPropertiesList);

	/*
	 * ------------ FXML Helpers -----------------
	 */

	private final FadeTransition status_fade = new FadeTransition(Duration.millis(5000), statusWindow);

	/*
	 * ============================ Code =====================
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
		} catch (IOException e)
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

		propertiesModel = App.INJECTOR.getInstance(Properties.class);

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

		statusController = StatusController.loadController(statusWindow.textProperty());
		statusController.setStyle(getStyle());
		status = statusController.getStatusModel();

		/*
		 * Bulk initializations
		 */
		
		initializeListeners();
		
		initializeCertificateFileChooser();

		initializeProperties();

		initializeBindings();

		/*
		 * Preferences
		 */
		try
		{
			homedir.textProperty().set(propertiesModel.getPropertiesDir());
		} catch (IOException e)
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
	 * 
	 */
	private void initializeListeners()
	{
		// TODO Refactor status window
		/*
		 * Status windows
		 */
		statusWindow.textProperty().addListener(new ChangeListener<Object>()
		{
			@Override
			public void changed(ObservableValue<?> observable, Object oldValue, Object newValue)
			{
				statusWindow.setScrollTop(Double.MAX_VALUE); // this will scroll
																// to the bottom
				// use Double.MIN_VALUE to scroll to the top
			}
		});

		endpoint.getEditor().focusedProperty().addListener(new ChangeListener<Object>()
		{
			@Override
			public void changed(ObservableValue<?> arg0, Object arg1, Object arg2)
			{
				endpointValidate(endpoint.getEditor().getText());
			}
		});
		
		proxyHost.focusedProperty().addListener(new ChangeListener<Object>()
		{
			@Override
			public void changed(ObservableValue<?> arg0, Object arg1, Object arg2)
			{
				proxyUpdate();
			}
		});		
		proxyPort.focusedProperty().addListener(new ChangeListener<Object>()
		{
			@Override
			public void changed(ObservableValue<?> arg0, Object arg1, Object arg2)
			{
				proxyUpdate();
			}
		});		
		proxyUser.focusedProperty().addListener(new ChangeListener<Object>()
		{
			@Override
			public void changed(ObservableValue<?> arg0, Object arg1, Object arg2)
			{
				proxyUpdate();
			}
		});		
		proxyPassword.focusedProperty().addListener(new ChangeListener<Object>()
		{
			@Override
			public void changed(ObservableValue<?> arg0, Object arg1, Object arg2)
			{
				proxyUpdate();
			}
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

		useProxyProperty.bindBidirectional(proxyToggle1.selectedProperty());
		useProxyProperty.bindBidirectional(proxyToggle2.selectedProperty());
		useProxyProperty.bindBidirectional(proxyToggle3.selectedProperty());
		useProxyProperty.bindBidirectional(proxyToggle4.selectedProperty());
		useProxyProperty.bindBidirectional(proxyToggle5.selectedProperty());

		proxyToggle1.textProperty().bind(useProxyTextProperty);
		proxyToggle2.textProperty().bind(useProxyTextProperty);
		proxyToggle3.textProperty().bind(useProxyTextProperty);
		proxyToggle4.textProperty().bind(useProxyTextProperty);
		proxyToggle5.textProperty().bind(useProxyTextProperty);

		// Link proxy fields together from host
		proxyHostLabel.textFillProperty().bind(proxyComboLabel.textFillProperty());
		proxyPortLabel.textFillProperty().bind(proxyComboLabel.textFillProperty());
		proxyUserLabel.textFillProperty().bind(proxyComboLabel.textFillProperty());
		proxyPasswordLabel.textFillProperty().bind(proxyComboLabel.textFillProperty());

		proxyCombo.disableProperty().bind(useProxyProperty.not());
		proxyHost.disableProperty().bind(useProxyProperty.not());
		proxyPort.disableProperty().bind(useProxyProperty.not());
		proxyUser.disableProperty().bind(useProxyProperty.not());
		proxyPassword.disableProperty().bind(useProxyProperty.not());
		saveProxy.disableProperty().bind(useProxyProperty.not());

		// proxyHost.textProperty().set(requestModel.getProxyHost());
		// proxyPort.textProperty().set(requestModel.getProxyPort());
		// proxyUser.textProperty().set(requestModel.getProxyUser());
		// proxyPassword.textProperty().set(requestModel.getProxyPassword());

		payload.wrapTextProperty().bind(payloadWrap.selectedProperty().not());
	}

	/**
	 * Initialize the certificateFileChooser
	 */
	private void initializeCertificateFileChooser()
	{
		LOG.debug("Initializing Certificate File Chooser");

		certificateFileChooser.setRoot(getRoot());
		certificateFileChooser.getChosenFileProperty().addListener(new ChangeListener<File>()
		{
			@Override
			public void changed(ObservableValue<? extends File> arg0, File oldValue, File newValue)
			{
				setCertificateFile(newValue);
			}
		});
	}

	/**
	 * Initialize the properties sub system
	 */
	private void initializeProperties()
	{
		LOG.debug("Initializing Properties");

		propertiesTable.setRowFactory(new Callback<TableView<Request>, TableRow<Request>>()
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
						propertiesList.remove(row.getItem());
					}
				});
				rowMenu.getItems().addAll(removeItem);
				row.contextMenuProperty().bind(Bindings.when(Bindings.isNotNull(row.itemProperty())).then(rowMenu)
						.otherwise((ContextMenu) null));
				return row;
			}
		});

		propertiesTable.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
			if (event.getClickCount() > 1 && event.getButton().equals(MouseButton.PRIMARY))
			{
				requestLoad(propertiesTable.getSelectionModel().getSelectedItem());
			}
		});

		sortedPropertiesList.comparatorProperty().bind(propertiesTable.comparatorProperty());
		propertiesTable.setItems(sortedPropertiesList);

		endpointColumn.setCellValueFactory(new PropertyValueFactory<Request, String>("endpoint"));
		payloadColumn.setCellValueFactory(new PropertyValueFactory<Request, String>("payload"));
		methodColumn.setCellValueFactory(new PropertyValueFactory<Request, String>("method"));
		securityColumn.setCellValueFactory(new PropertyValueFactory<Request, String>("security"));
		contentTypeColumn.setCellValueFactory(new PropertyValueFactory<Request, String>("contentType"));
		base64Column.setCellValueFactory(new PropertyValueFactory<Request, Boolean>("base64"));
		// proxyHostColumn.setCellValueFactory(new PropertyValueFactory<Request,
		// String>("proxyHost"));
		// proxyPortColumn.setCellValueFactory(new PropertyValueFactory<Request,
		// String>("proxyPort"));
		// proxyUserColumn.setCellValueFactory(new PropertyValueFactory<Request,
		// String>("proxyUser"));
		// proxyPasswordColumn.setCellValueFactory(new
		// PropertyValueFactory<Request, String>("proxyPassword"));

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
	public void proxySave()
	{
		// updateRequest();
		// propertiesModel.addData(requestBean);
		processProperties();
	}

	/**
	 * User asks to save the current request configuration via (@code save}
	 * button.
	 */
	public void requestSave()
	{
		requestUpdate();
		propertiesModel.addData(requestBean);
		processProperties();
	}

	/**
	 * Fire event - User hits the {@code Fire} button
	 * <p>
	 * Create a response task and fires it off in the back ground.
	 * 
	 * @throws IOException
	 */
	public void fire()
	{
		LOG.debug("Fire  --  Starts");

		// if (!endpointValid(endpoint.getValue()))
		// return;

		progress.setVisible(true); // Show we're busy

		try
		{
			requestUpdate();
			proxyUpdate();

			// URI uri = new URI(StringUtils.trim(endpoint.getValue()));

			if (!endpoint.getItems().contains(endpoint.getValue()))
			/*
			 * Add endpoint if not already added
			 * 
			 */
			{
				endpoint.getItems().add(endpoint.getValue());
			}

			/* Fire off the request */
			ResponseModel 
				response = requestModel.doRequest(requestBean.copy());
			

			/* Feedback to Request status panel */
			status.append(INFO_FIRED, response.getReferenceId(), response.getRequest().getMethod(),
					response.getRequest().getUri());

			/*
			 * Open the Response window managing this request instance
			 */
			ResponseController.loadStage(response).show();

			// } catch (URISyntaxException e)
			// /*
			// * uri did not compute
			// */
			// {
			// LOG.debug("Fire endpoint is not a URI.");
			// tabs.getSelectionModel().select(destination);
			// status.write(ERROR_URL_VALIDATION);
			// // status_fade = new FadeTransition(Duration.millis(5000),
			// status);
			// // status_fade.play();

		} finally
		/*
		 * Clear the progress ticker
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
	public void proxyToggle()
	{
		proxyToggle(null);
	}

	/**
	 * Toggles the proxy if required.
	 * 
	 * @param proxy true for on, false for off, null for toggle
	 */
	private void proxyToggle(Boolean proxy)
	{
		if ((useProxyProperty.get() && proxy == null) || (proxy != null && proxy))
		/*
		 * The useProxy is set, so set the proxy
		 */
		{
			useProxyTextProperty.setValue(LEGEND_PROXY_ON);
			proxyComboLabel.setTextFill(CONST_PAINT_BLACK);
			saveProxy.setTextFill(CONST_PAINT_BLACK);
			proxyUpdate();
			LOG.debug("Proxy activted");
		} else
		/*
		 * useProxy is unset, so unset the proxy
		 */
		{
			useProxyTextProperty.setValue(LEGEND_PROXY_OFF);
			proxyComboLabel.setTextFill(CONST_PAINT_GREY);
			saveProxy.setTextFill(CONST_PAINT_GREY);
			proxyBean.reset();
			LOG.debug("Proxy deactivted");
		}
	}

	/**
	 * Update the request bean from the values bound to the window
	 */
	public void requestUpdate()
	{
		requestBean.setEndpoint(endpoint.getValue().toString());
		requestBean.setMethod(method.getValue());
		requestBean.setSecurity(security.getValue());
		requestBean.setPayload(StringUtils.trim(payload.getText()));
		requestBean.setContentType(mime.getValue());
		requestBean.setBase64(encode.isSelected());		
	}
	
	/**
	 * Update the request bean from the values bound to the window
	 */
	public void proxyUpdate()
	{
		proxyBean.reset(proxyHost.getText(), proxyPort.getText(), proxyUser.getText(), proxyPassword.getText());	
		proxyCombo.setValue(proxyBean.toString());
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
			keyStore = new KeyStoreBean(certificateFileChooser.getValue(), certificatePassword.getText());
			KeyStoreViewerController.loadStage(keyStore).show();
			ObservableList<String> aliases = FXCollections.observableArrayList("Do not use certificate");
			aliases.addAll(keyStore.getAliases());
			useAlias.itemsProperty().setValue(aliases);
			useAlias.setDisable(false);
		} catch (KeyStoreBeanException e)
		{
			LOG.debug(e.getMessage(), e);
			status.append("Certificate file cannot be opened: [%1$s]", e.getCause().getMessage());
		}
	}

	/**
	 * Show custom payload context menu on tripple click
	 */
	public void onPayloadSelected(MouseEvent mouseEvent)
	{
		if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 3)
		{
			payloadCM.show(payload, mouseEvent.getScreenX(), mouseEvent.getScreenY());
		}
	}

	/**
	 * Open the stand alone status window on Status double click
	 */
	public void onStatusSelected(MouseEvent mouseEvent)
	{
		Stage stage;
		if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2
				&& (statusController != null) && (stage = statusController.getStage()) != null)
		{
			stage.show();
		}
	}

	/* ------------ Utilities ------------------ */

	/**
	 * Transfers stored properties to the UI properties tab
	 */
	private void processProperties()
	{

		LOG.debug("processing Properties");

		propertiesList.clear();

		if (!preferencesAvailable || propertiesModel == null)
			return;

		List<Request> props = propertiesModel.getData(); // Get properties

		/*
		 * populate endpoint drop down
		 */
		endpointFilter.getItems().setAll("");
		Set<URI> endpoints = new TreeSet<URI>();

		for (Request prop : props)
		{
			endpoints.add(RequestBean.constructUri(prop.getEndpoint()));
			endpointFilter.getItems().add(prop.getEndpoint());
		}

		endpoint.getItems().setAll(endpoints);

		/*
		 * Create properties table
		 */
		endpointFilter.valueProperty().addListener((observable, oldValue, newValue) -> {
			filteredPropertiesList.setPredicate(request -> {
				/*
				 * If filter text is empty, display all requests.
				 */
				if (newValue == null || newValue.isEmpty())
				{
					return true;
				}

				/*
				 * Compare first name and last name of every person with filter
				 * text
				 */
				if (request.getEndpoint().toLowerCase().contains(newValue.toLowerCase()))
				{
					return true; // Filter matches first name.
				}

				return false; // Does not match.
			});
		});

		propertiesList.addAll(props);

	}

	/**
	 * Loads a {@code Request} into the {@code RequestController} bound vars.
	 * 
	 * @param requestdata
	 *            the {@code Request} to pull into the ui
	 */
	private void requestLoad(Request requestdata)
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
		// proxyHost.setText(requestdata.getProxyHost());
		// proxyPort.setText(requestdata.getProxyPort());
		// proxyUser.setText(requestdata.getProxyUser());
		// proxyPassword.setText(requestdata.getProxyPassword());
		// if ((requestdata.getProxyHost() == null || requestdata.getProxyPort()
		// == null
		// || requestdata.getProxyHost().isEmpty() ||
		// requestdata.getProxyPort().isEmpty())
		// )
		// {
		// toggleProxy(false);
		// } else if (requestdata.getProxyHost() != null &&
		// requestdata.getProxyPort() != null
		// && !requestdata.getProxyHost().isEmpty() &&
		// !requestdata.getProxyPort().isEmpty())
		// {
		// toggleProxy(true);
		// }

		status.append("Loaded request for endpoint:[%1$s]", requestdata.getEndpoint());
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
		if (file != null && (!file.exists() || !file.canRead()))
		/*
		 * Chosen file cannot be read, turn off FXML assets
		 */
		{
			status.append("Certificate file cannot be read: [{}]", file.getPath());
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

	/**
	 * Validate and place the endpoint where needed, set tls etc
	 * 
	 * @param endpoint
	 * @param security
	 */
	private void endpointValidate(String endpoint, String security)
	{
		endpointValidate(endpoint);
		this.security.setValue(security);
	}

	/**
	 * Validate and place the endpoint where needed, set tls etc
	 * 
	 * @param endpoint
	 */
	private void endpointValidate(String endpoint)
	{
		endpointValidate(RequestBean.constructUri(endpoint));
	}

	/**
	 * Validate and place the endpoint where needed, set tls etc
	 * 
	 * @param endpoint
	 */
	private void endpointValidate(URI endpoint)
	{
		this.endpoint.setValue(endpoint);
		if ("HTTPS".equalsIgnoreCase(endpoint.getScheme()))
		{
			security.setDisable(false);
		} else
		{
			security.setDisable(true);
		}
	}

}
