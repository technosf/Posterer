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
import java.util.prefs.BackingStoreException;

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

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.lang.StringUtils;

import com.github.technosf.posterer.App;
import com.github.technosf.posterer.controllers.AbstractController;
import com.github.technosf.posterer.controllers.Controller;
import com.github.technosf.posterer.models.impl.PropertiesModel;
import com.github.technosf.posterer.models.impl.PropertiesModel.PropertiesData;
import com.github.technosf.posterer.models.impl.RequestBean;
import com.github.technosf.posterer.models.impl.RequestModel;
import com.github.technosf.posterer.models.impl.ResponseModel;
import com.github.technosf.posterer.transports.commons.CommonsConfiguratorPropertiesImpl;

/**
 * @author technosf
 * @since 0.0.1
 * @version 0.0.1
 */
public class RequestController
				extends AbstractController
				implements Controller
{

	// private static final RequestModel requestModel =
	// new CommonsRequestModelImpl();

	/**
	 * The FXML definition of the View
	 */
	public static final String FXML = "/fxml/Request.fxml";

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

	private final PropertiesModel propertiesModel;

	private final RequestModel requestModel = App.INJECTOR
					.getInstance(RequestModel.class);

	private final RequestBean propertiesData = new RequestBean();

	private boolean preferencesAvailable = true;


	/*
	 * ------------ FXML Components -----------------
	 */

	@FXML
	private Parent root;

	@FXML
	private ComboBox<String> endpoint;

	@FXML
	private TextField status, timeoutText, user, proxyhost,
					proxyport, proxyuser, proxypassword, homedir;

	@FXML
	private Label proxyhostlabel, proxyportlabel, proxyuserlabel,
					proxypasswordlabel;
	@FXML
	private PasswordField password;

	@FXML
	private Slider timeoutSlider;

	@FXML
	private TextArea request;

	@FXML
	private ProgressIndicator progress;

	@FXML
	private Button fire1, fire2, fire3, fire4, save;

	@FXML
	private ToggleButton proxyToggle1, proxyToggle2, proxyToggle3,
					proxyToggle4;

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
	private TableView<PropertiesData> propertiesTable;

	@FXML
	private TableColumn<PropertiesData, String> endpointColumn, payloadColumn,
					methodColumn, contentTypeColumn, httpUserColumn,
					httpPasswordColumn;
	@FXML
	private TableColumn<PropertiesData, Boolean> base64Column;

	/*
	 * ------------ FXML Bindings -----------------
	 */
	IntegerProperty timeout = new SimpleIntegerProperty();

	BooleanProperty useProxy = new SimpleBooleanProperty(
					requestModel.useProxy());

	StringProperty useProxyText = new SimpleStringProperty(useProxy.get()
					? LEGEND_PROXY_ON : LEGEND_PROXY_OFF);

	ObservableList<PropertiesData> properties = FXCollections
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
	 * @throws IOException
	 * @throws BackingStoreException
	 */
	public RequestController()
	{
		super("Poster");

		status_fade.setFromValue(1.0);
		status_fade.setToValue(0.0);
		status_fade.setCycleCount(4);
		status_fade.setAutoReverse(true);

		PropertiesModel props = null;

		try
		{
			props =
							new CommonsConfiguratorPropertiesImpl("main.");
		}
		catch (ConfigurationException | IOException e) // TODO - get more specific with action?
		{
			preferencesAvailable = false;
		}

		propertiesModel = props;
	}


	/**
	 * Transfers properties to the UI
	 */
	private void processProperties()
	{
		properties.clear();

		if (preferencesAvailable && propertiesModel != null)
		{

			List<PropertiesData> props = propertiesModel.getData();

			properties.addAll(props);

			Set<String> endpoints = new TreeSet<String>();

			for (PropertiesData prop : props)
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
						.setCellValueFactory(new PropertyValueFactory<PropertiesData, String>(
										"endpoint"));
		payloadColumn
						.setCellValueFactory(new PropertyValueFactory<PropertiesData, String>(
										"payload"));
		methodColumn
						.setCellValueFactory(new PropertyValueFactory<PropertiesData, String>(
										"method"));
		contentTypeColumn
						.setCellValueFactory(new PropertyValueFactory<PropertiesData, String>(
										"contentType"));
		base64Column
						.setCellValueFactory(new PropertyValueFactory<PropertiesData, Boolean>(
										"base64"));
		httpUserColumn
						.setCellValueFactory(new PropertyValueFactory<PropertiesData, String>(
										"httpUser"));
		httpPasswordColumn
						.setCellValueFactory(new PropertyValueFactory<PropertiesData, String>(
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

		proxyToggle1.textProperty().bind(useProxyText);
		proxyToggle2.textProperty().bind(useProxyText);
		proxyToggle3.textProperty().bind(useProxyText);
		proxyToggle4.textProperty().bind(useProxyText);

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
	 * User asks to save the current request configuration via (@code save} button.
	 */
	public void saveCurrentConfig()
	{
		updateRequest();
		propertiesModel.addData(propertiesData);
		processProperties();
	}


	/**
	 * Fire event - User hits the {@code Fire} button.
	 * 
	 * @throws IOException
	 */
	public void fire() throws IOException
	{
		if (StringUtils.isNotBlank(endpoint.getValue()))
		{
			progress.setVisible(true);

			try
			{
				ResponseModel response = null;

				try
				{
					updateRequest();

					URI uri = new URI(StringUtils.trim(endpoint.getValue()));

					endpoint.getItems().add(uri.toString());

					response = requestModel.doRequest(propertiesData);

					status.setText(String.format(INFO_FIRED,
									response.getReferenceId(), response.getMethod(),
									response.getUri()));
				}
				catch (URISyntaxException e)
				{
					tabs.getSelectionModel().select(destination);
					status.setText(ERROR_URL_VALIDATION);
					// status_fade = new FadeTransition(Duration.millis(5000), status);
					// status_fade.play();
				}

				Stage stage = new Stage();
				ResponseController controller =
								(ResponseController) ResponseController
												.loadController(ResponseController.FXML);
				controller.setStage(stage);
				controller.updateStage(response);
				stage.show();
			}
			finally
			{
				progress.setVisible(false);
			}
		}
	}


	/**
	 * Proxy Toggle event - Use toggles the {@code Proxy} button.
	 * <p>
	 * The {@code useProxy} property has already received any change prior to toggleProxy being
	 * fired from the FXML. Could have used a {@code Listener} in the {@code initialize} method
	 * where the bindings are done, but using the {@code onAction} methods is easier.
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
		// The useProxy is unset, so unset the proxy
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
		System.out.println("");
	}


	/**
     * 
     */
	public void updateRequest()
	{
		propertiesData.setEndpoint(StringUtils.trim(endpoint.getValue()));
		propertiesData.setMethod(method.getValue());
		propertiesData.setPayload(StringUtils.trim(request.getText()));
		propertiesData.setContentType(mime.getValue());
		propertiesData.setBase64(encode.isSelected());
		propertiesData.setHttpUser(StringUtils.trim(user.getText()));
		propertiesData.setHttpPassword(StringUtils.trim(password.getText()));
	}


	/**
	 * @param propertiesData
	 */
	private void loadRequest(PropertiesData propertiesData)
	{
		endpoint.setValue(propertiesData.getEndpoint());
		method.setValue(propertiesData.getMethod());
		request.setText(propertiesData.getPayload());
		mime.setValue(propertiesData.getContentType());
		encode.setSelected(propertiesData.getBase64());
		user.setText(propertiesData.getHttpUser());
		password.setText(propertiesData.getHttpPassword());
		status.appendText(String.format("\nLoaded request for endpoint:[%1$s]",
						propertiesData.getEndpoint()));
	}

}
