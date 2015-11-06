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

import java.io.IOException;
import java.net.URI;
import com.github.technosf.posterer.models.Request;
import com.github.technosf.posterer.ui.components.FileChooserComboBox;
import com.github.technosf.posterer.ui.controllers.Controller;

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
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.StackPane;

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
public abstract class AbstractRequestController 
	extends AbstractController 
	implements Controller
{
	/**
	 * The FXML definition of the View
	 */
	public static final String FXML = "/fxml/Request.fxml";


	/*
	 * ------------ FXML Components -----------------
	 */

	@FXML
	protected ComboBox<URI> endpoint;

	@FXML
	protected ComboBox<String> endpointFilter, useAlias, proxyCombo;

	@FXML
	protected FileChooserComboBox certificateFileChooser;

	@FXML
	protected TextField timeoutText, proxyHost, proxyPort, proxyUser, proxyPassword, homedir;

	@FXML
	protected Label proxyComboLabel, proxyHostLabel, proxyPortLabel, proxyUserLabel, proxyPasswordLabel;

	@FXML
	protected PasswordField password, certificatePassword;

	@FXML
	protected Slider timeoutSlider;

	@FXML
	protected TextArea statusWindow, payload;

	@FXML
	protected ProgressIndicator progress;

	@FXML
	protected Button fire1, fire2, fire3, fire4, fire5, save, validateCertificate, saveProxy;

	@FXML
	protected ToggleButton proxyToggle1, proxyToggle2, proxyToggle3, proxyToggle4, proxyToggle5;

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
	protected TableColumn<Request, String> endpointColumn, payloadColumn, methodColumn, securityColumn, contentTypeColumn;
	//			proxyHostColumn, proxyPortColumn, proxyUserColumn, proxyPasswordColumn;
	@FXML
	protected TableColumn<Request, Boolean> base64Column;

	/* Context Menus */
	protected RadioButton payloadWrap = new RadioButton("Wrap");
	protected CustomMenuItem payloadWrapMI = new CustomMenuItem(payloadWrap);
	protected MenuItem payloadFormat = new MenuItem("Format");
	protected ContextMenu payloadCM = new ContextMenu(payloadWrapMI, payloadFormat);

	/*
	 * ---------- Code ------------------
	 */

	protected AbstractRequestController(String title)
	{
		super(title);
	}
	
	/*
	 * ---------- Events -------------------
	 */

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
	 * Fire event - User hits the {@code Fire} button
	 * <p>
	 * Create a response task and fires it off in the back ground.
	 * 
	 * @throws IOException
	 */
	protected abstract void fire();

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

}
