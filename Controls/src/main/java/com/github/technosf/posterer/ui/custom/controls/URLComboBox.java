/*
 * Copyright 2016 technosf [https://github.com/technosf]
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

package com.github.technosf.posterer.ui.custom.controls;

import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyMapProperty;
import javafx.beans.property.ReadOnlyMapWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;

/**
 * Component to enter, choose network connection URLs and keep a pull-down of
 * the past choices. Represents the URLs as strings, but makes them available.
 * <p>
 * Control can perform validation on the chosen URL, including connection check
 * via a given proxy.
 * <p>
 * The ComboBox uses Strings to manage the objects as URLs make the control bog
 * down on showing. Added utility functions to provide access to the current URL
 * and to other URLs via string representation.
 * 
 * @author technosf
 * @since 0.0.1
 * @version 0.0.1
 */
public class URLComboBox
        extends ComboBox<String>
{
    /**
     * Unmodifiable Map of URL plaintext protocols and their securable versions.
     * Instantiated with {@code URL} base known secure/securable protocol pair,
     * HTTP.
     */
    @SuppressWarnings("serial")
    public static final Map<String, String> URL_DEFAULT_SECURE_PROTOCOLS =
            Collections.unmodifiableMap(new HashMap<String, String>()
            {
                {
                    put("http", "https");
                }
            });

    /**
     * Default URL Indeterminate background
     */
    public final static Background URL_DEFAULT_BACKGROUND_INDETERMINATE =
            new Background(new BackgroundFill(Color.SILVER, null, null));

    /**
     * Default URL Invalid background
     */
    public final static Background URL_DEFAULT_BACKGROUND_INVALID =
            new Background(new BackgroundFill(Color.RED, null, null));

    /**
     * Default URL Valid background
     */
    public final static Background URL_DEFAULT_BACKGROUND_VALID =
            new Background(new BackgroundFill(Color.CHARTREUSE, null, null));

    /* ----------------------------------------------------------------
     *
     * State vars     
     *
     * ----------------------------------------------------------------
     */

    /**
     * Reference map of URLs as strings.
     */
    Map<String, URL> urlStringReference = new HashMap<>();

    /* ================================================================
     * 
     * Properties
     * 
     * ================================================================
     */

    /* ----------------------------------------------------------------
     *
     * securableProtocols     
     *
     * Securable and secure protocol pairings
     * ----------------------------------------------------------------
     */

    /**
     * The {@code URL} uses a secureable protocol property wrapper
     */
    private ReadOnlyMapWrapper<String, String> securableProtocols =
            new ReadOnlyMapWrapper<>(this, "isSecureProtocol",
                    FXCollections.observableMap(URL_DEFAULT_SECURE_PROTOCOLS));


    /**
     * Returns the {@code URL} uses a secureable property
     * 
     * @return the {@code URL} uses a secureable property
     */
    public ReadOnlyMapProperty<String, String> getSecurableProtocolsProperty()
    {
        return securableProtocols.getReadOnlyProperty();
    }


    /**
     * Is this {@code URL} using a securable protocol, i.e. SSL/TLS?
     * 
     * @return true if the URL uses SSL/TLS
     */
    public final ObservableMap<String, String> getSecurableProtocols()
    {
        return getSecurableProtocolsProperty().get();
    }

    /* ----------------------------------------------------------------
    *
    * secureProtocol     
    *
    * ----------------------------------------------------------------
    */

    /**
     * The {@code URL} uses a secureable protocol property wrapper
     */
    private ReadOnlyBooleanWrapper secureProtocol =
            new ReadOnlyBooleanWrapper(this, "secureProtocol");


    /**
     * Returns the {@code URL} uses a secureable property
     * 
     * @return the {@code URL} uses a secureable property
     */
    public ReadOnlyBooleanProperty secureProtocolProperty()
    {
        return secureProtocol.getReadOnlyProperty();
    }


    /**
     * Is this {@code URL} using a securable protocol, i.e. SSL/TLS?
     * 
     * @return true if the URL uses SSL/TLS
     */
    public final boolean isSecureProtocol()
    {
        return secureProtocolProperty().get();
    }

    /* ----------------------------------------------------------------
     *
     * supportsSSL
     *
     * ----------------------------------------------------------------
     */

    /**
     * The {@code URL} has or is using a protocol that is securable property
     * wrapper
     */
    private ReadOnlyBooleanWrapper securableProtocol =
            new ReadOnlyBooleanWrapper(this, "securableProtocol");


    /**
     * Returns the {@code URL} has or is using protocol that is securable
     * property
     * 
     * @return {@code URL} has or is using protocol that is securable property
     */
    public ReadOnlyBooleanProperty securableProtocolProperty()
    {
        return securableProtocol.getReadOnlyProperty();
    }


    /**
     * Can or is the {@code URL} using a protocol that is securable?
     * 
     * @return true if protocol can be secured.
     */
    public final boolean isSecurableProtocol()
    {
        return securableProtocolProperty().get();
    }

    /* ----------------------------------------------------------------
     *
     * Valid
     * 
     * is the URL valid
     * 
     * ----------------------------------------------------------------
     */
    //FIXME
    /**
     * The selected {@code URL} is valid boolean property wrapper
     */
    private ReadOnlyBooleanWrapper valid =
            new ReadOnlyBooleanWrapper(this, "valid");


    /**
     * Returns the selected {@code URL} is valid boolean property
     * 
     * @return the selected {@code URL} is valid boolean property
     */
    public ReadOnlyBooleanProperty validProperty()
    {
        return valid.getReadOnlyProperty();
    }


    /**
     * Is the current {@code URL} valid?
     * 
     * @return true if the selected {@code URL} is valid
     */
    public final boolean isValid()
    {
        return validProperty().get();
    }

    /* ----------------------------------------------------------------
    *
    * ConnectionTesting 
    *
    * Testing connection to valid URLs
    * ----------------------------------------------------------------
    */

    /**
     * Connection testing property
     */
    private SimpleBooleanProperty connectionTesting =
            new SimpleBooleanProperty(this, "connectionTesting");


    /**
     * Returns the connectionTesting property
     * 
     * @return the connectionTesting property
     */
    public SimpleBooleanProperty connectionTestingProperty()
    {
        return connectionTesting;
    }


    /**
     * Set the connection testing behavior
     * 
     * @param test
     *            true to test
     */
    public final void setConnectionTesting(boolean test)
    {
        connectionTestingProperty().set(test);
    }


    /**
     * re URLs being connection tested
     * 
     * @return true if URLs will get tested
     */
    public final boolean isConnectionTesting()
    {
        return connectionTestingProperty().get();
    }

    /* ----------------------------------------------------------------
     *
     * urlReachable
     * 
     * Is the URL reachable (via the set proxy)
     * 
     * ----------------------------------------------------------------
     */

    /**
     * The {@code URL} is reachable boolean property wrapper
     */
    private ReadOnlyBooleanWrapper urlReachable =
            new ReadOnlyBooleanWrapper(this, "urlReachable");


    /**
     * Returns the {@code URL} is reachable boolean property
     * 
     * @return the selected {@code URL} is reachable boolean property
     */
    public ReadOnlyBooleanProperty urlReachableProperty()
    {
        return urlReachable.getReadOnlyProperty();
    }


    /**
     * Is the current {@code URL} reachable?
     * 
     * @return true if the selected {@code URL} is reachable
     */
    public final boolean isUrlReachable()
    {
        return urlReachableProperty().get();
    }

    /* ----------------------------------------------------------------
    *
    * Proxy
    * 
    * Sets the proxy (or the no proxy default) for connection checks
    * ----------------------------------------------------------------
    */

    /**
     * Proxy property if network connections are to go via a {@code Proxy}
     */
    private ObjectProperty<Proxy> proxy =
            new SimpleObjectProperty<Proxy>(this, "proxy", Proxy.NO_PROXY);


    /**
     * The {@code Proxy} property
     * 
     * @return the {@code Proxy} property
     */
    public ObjectProperty<Proxy> proxyProperty()
    {
        return proxy;
    }


    /**
     * Sets the {@code Proxy} to use
     * 
     * @param value
     *            the {@code Proxy}
     */
    public final void setProxy(Proxy value)
    {
        proxyProperty().set(value);
    }


    /**
     * Return the {@code Proxy}
     * 
     * @return the {@code Proxy}
     */
    public final Proxy getProxy()
    {
        return proxyProperty().get();
    }

    /* ----------------------------------------------------------------
    *
    * Background indicators
    * 
    * ----------------------------------------------------------------
    */

    /**
     * The background property for {@code URL}s of indeterminate validity
     */
    private ObjectProperty<Background> urlIndeterminateBackground =
            new SimpleObjectProperty<Background>(this,
                    "urlIndeterminateBackground",
                    URL_DEFAULT_BACKGROUND_INDETERMINATE);


    /**
     * Returns the indeterminate url background property
     * <p>
     * The background of the selected url can be customized to provide visible
     * feedback on its status
     * 
     * @return The indeterminate url background property
     */
    public ObjectProperty<Background> urlIndeterminateBackgroundProperty()
    {
        return urlIndeterminateBackground;
    }


    /**
     * Sets the indeterminate url background
     * 
     * @param value
     *            indeterminate url background
     */
    public final void setUrlIndeterminatBackground(Background value)
    {
        urlIndeterminateBackgroundProperty().set(value);
    }


    /**
     * Returns the indeterminate url background
     * 
     * @return the indeterminate url background
     */
    public final Background getUrlIndeterminatBackground()
    {
        return urlIndeterminateBackgroundProperty().get();
    }

    /* --------------------- */

    /**
     * The background property for valid {@code URL}s
     */
    private ObjectProperty<Background> urlValidBackground =
            new SimpleObjectProperty<Background>(this, "urlValidBackground",
                    URL_DEFAULT_BACKGROUND_VALID);


    /**
     * Returns the valid url background property
     * <p>
     * The background of the selected url can be customized to provide visible
     * feedback on its status
     * 
     * @return The valid url background property
     */
    public ObjectProperty<Background> urlValidBackgroundProperty()
    {
        return urlValidBackground;
    }


    /**
     * Sets the valid url background
     * 
     * @param value
     *            valid url background
     */
    public final void setUrlValidBackground(Background value)
    {
        urlValidBackgroundProperty().set(value);
    }


    /**
     * Returns the valid url background
     * 
     * @return the valid url background
     */
    public final Background getUrlValidBackground()
    {
        return urlValidBackgroundProperty().get();
    }

    /* --------------------- */

    /**
     * The background property for invalid {@code URL}s of indeterminate
     * validity
     */
    private ObjectProperty<Background> urlInvalidBackground =
            new SimpleObjectProperty<Background>(this, "urlInvalidBackground",
                    URL_DEFAULT_BACKGROUND_INVALID);


    /**
     * Returns the invalid url background property
     * <p>
     * The background of the selected url can be customized to provide visible
     * feedback on its status
     * 
     * @return The invalid url background property
     */
    public ObjectProperty<Background> urlInvalidBackgroundProperty()
    {
        return urlInvalidBackground;
    }


    /**
     * Sets the invalid url background
     * 
     * @param value
     *            invalid url background
     */
    public final void setUrlInvalidBackground(Background value)
    {
        urlInvalidBackgroundProperty().set(value);
    }


    /**
     * Returns the invalid url background
     * 
     * @return the invalid url background
     */
    public final Background getUrlInvalidBackground()
    {
        return urlInvalidBackgroundProperty().get();
    }

    /* ================================================================
     * 
     * State vars
     * 
     * ================================================================
     */


    /* ================================================================
     * 
     * Code
     * 
     * ================================================================
     */

    /**
     * Default constructor
     */
    public URLComboBox()
    {
        super();

        /*
         * Create the control
         */
        FXMLLoader fxmlLoader =
                new FXMLLoader(
                        getClass().getResource(
                                "URLComboBox.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        /*
         * Track events (i.e. enter button presses)
         */
        addEventHandler(ActionEvent.ACTION,
                event -> {
                    action(event);
                });

        /*
         * Track keys types into the text box
         */
        getEditor().setOnKeyTyped(event -> typed(event));

        /*
         * Track changes to the URL list 
         */
        getItems().addListener(
                (ListChangeListener.Change<? extends String> c) -> change(c));
    }


    /* ----------------------------------------------------------------
    * 
    * Events and handlers
    * 
    * ----------------------------------------------------------------
    */

    /**
     * Enter pressed, so process and display the editor contents.
     * <p>
     * Processing is done in the background
     * 
     * @param event
     *            the event
     */
    private void action(ActionEvent event)
    {
        processNewValue(getEditor().getText());
    }


    /**
     * Observes the values keyed into editor and sets feedback
     * 
     * @param event
     *            the key event
     */
    private void typed(KeyEvent event)
    {
        if (KeyEvent.KEY_TYPED.equals(event.getEventType()))
        /*
         * Key typed
         */
        {
            processInputBackgroundOnKey(event.getCharacter().codePointAt(0));
        }
    }


    /**
     * Handler to process list removals and additions into the URL string
     * reference and clean the combobox list of invalid URL string
     * <p>
     * All URL validation and reference map manipulation is done here.
     * 
     * @param change
     */
    private void change(Change<? extends String> change)
    {
        URL x;
        while (change.next())
        /*
         * Cycle through changes
         */
        {
            for (String n : change.getRemoved())
            /*
             * Process removals first
             */
            {
                urlStringReference.remove(n);
            }
            for (String n : change.getAddedSubList())
            /*
             * Process additions last
             */
            {
                if ((x = validate(n)) != null)
                /*
                 * url string is a valid url
                 */
                {
                    urlStringReference.put(n, x);
                }
                else
                /*
                 * url string is not a valid url, make sure it's removed
                 */
                {
                    urlStringReference.remove(n);
                    getItems().remove(n);
                }
            }
        }
        Platform.runLater(() -> syncUrlLists());
    }


    /* ----------------------------------------------------------------
    * 
    * Attribute Functions
    * 
    * ----------------------------------------------------------------
    */

    /**
     * Returns the value as an URL, or null if no value.
     * 
     * @return the URL value
     */
    public URL getUrlValue()
    {
        return urlStringReference.get(getValue());
    }


    /**
     * Utility function to add a single url to the list
     * 
     * @param urlString
     *            the file to add
     * @return true if the file was added
     */
    public boolean addItem(String urlString)
    {
        if (urlString != null && !getItems().contains(urlString))
        /*
         * / Adding a new-to-us Url
         */
        {
            getItems().add(urlString);
        }

        return getItems().contains(urlString);
    }


    /**
     * Utility function to remove a single url from the list
     * 
     * @param urlString
     *            the URL to remove
     * @return true if the URL was removed
     */
    public boolean removeItem(String urlString)
    {
        return getItems().remove(urlString);
    }


    /* ----------------------------------------------------------------
    * 
    * Utility functions
    * 
    * ----------------------------------------------------------------
    */

    /**
     * Sets the gamut of state properties
     * 
     * @param validity
     * @param reachable
     * @param securable
     * @param secure
     * @param background
     */
    private void updateProps(boolean validity, boolean reachable,
            boolean securable, boolean secure, Background background)
    {
        valid.set(validity);
        urlReachable.set(reachable);
        securableProtocol.set(securable);
        secureProtocol.set(secure);
        getEditor().backgroundProperty().set(background);
    }


    /**
     * Sets input area background on intermediate key presses
     * 
     * @param keyCode
     *            key code of the pressed key
     */
    private void processInputBackgroundOnKey(int keyCode)
    {
        if (keyCode != 13
                && !getEditor().backgroundProperty().get()
                        .equals(getUrlIndeterminatBackground()))
        /* 
         * Not the Enter key so set an indeterminate background
         * if not already set
         */
        {
            getEditor().backgroundProperty()
                    .set(getUrlIndeterminatBackground());
        }
    }


    /**
     * Validates a string as an URL, returning valid URLs
     * 
     * @param endpoint
     *            the endpoint to validate
     * @return The validated URL, or null if invalid
     */
    private URL validate(final @Nullable String endpoint)
    {
        try
        {
            return new URL(endpoint);
        }
        catch (MalformedURLException e)
        /*
         * Not an URL
         */
        {
            return null;
        }
    }


    /**
     * Synchronize the combobox URL String list and the internal reference map
     * of strings to URLs, driving from the reference map.
     * <p>
     * This is a secondary, defensive, measure - the primary event handler
     * should get it right.
     */
    private void syncUrlLists()
    {
        if (urlStringReference.containsValue(null))
        /*
         * Go through the map.
         * If there are missing urls in the reference list, expunge
         */
        {
            urlStringReference.forEach((key, value) -> {
                if (value == null)
                /*
                 * Remove the key from both lists, hence syncronizing them
                 */
                {
                    urlStringReference.remove(key);
                    getItems().remove(key);
                }
            });
        }
    }


    /**
     * Processes the entered url string into the list
     * 
     * @param url
     *            the URL to display
     */
    private void processNewValue(String urlString)
    {
        if (addItem(urlString) // Add the value
                || urlStringReference.containsKey(urlString))
        /*
         * Valid Url
         */
        {
            if (getSecurableProtocols().containsKey(
                    urlStringReference.get(urlString).getProtocol()))
            /*
             * Not secured, but securable
             */
            {
                updateProps(true, false, true, false, getUrlValidBackground());
            }
            else if (getSecurableProtocols().containsValue(
                    urlStringReference.get(urlString).getProtocol()))
            /*
             * Securable
             */
            {
                updateProps(true, false, true, true, getUrlValidBackground());
            }
            else
            /*
             * Not secure nor securable 
             */
            {
                updateProps(true, false, false, false, getUrlValidBackground());
            }
        }
        else
        /*
         * Invalid url
         */
        {
            updateProps(false, false, false, false, getUrlInvalidBackground());
        }

    }

}
