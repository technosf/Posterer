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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;

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
     * Reference map of valid URLs referenced by their string value.
     */
    Map<String, URL> validUrlStringReference = new HashMap<>();

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
            new ReadOnlyBooleanWrapper(this, "secureProtocol", false);


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
     * UnsecurableProtocol
     *
     * ----------------------------------------------------------------
     */

    /**
     * The {@code URL} is using a protocol that does not have securable version
     * property wrapper
     */
    private ReadOnlyBooleanWrapper unsecurableProtocol =
            new ReadOnlyBooleanWrapper(this, "unsecurableProtocol", true);


    /**
     * Returns the {@code URL} has or is using protocol that is unsecurable
     * property
     * 
     * @return {@code URL} has or is using protocol that is unsecurable property
     */
    public ReadOnlyBooleanProperty unsecurableProtocolProperty()
    {
        return unsecurableProtocol.getReadOnlyProperty();
    }


    /**
     * Can or is the {@code URL} using a protocol that is securable?
     * 
     * @return true if protocol can be secured.
     */
    public final boolean isUnsecurableProtocol()
    {
        return unsecurableProtocolProperty().get();
    }

    /* ----------------------------------------------------------------
     *
     * Valid
     * 
     * is the URL valid
     * 
     * ----------------------------------------------------------------
     */

    /**
     * The selected {@code URL} is valid boolean property wrapper
     */
    private ReadOnlyBooleanWrapper valid =
            new ReadOnlyBooleanWrapper(this, "valid", false);


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
            new SimpleBooleanProperty(this, "connectionTesting", false);


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
            new ReadOnlyBooleanWrapper(this, "urlReachable", false);


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
            new SimpleObjectProperty<>(this, "proxy", Proxy.NO_PROXY);


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
            new SimpleObjectProperty<>(this,
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
    public final void setUrlIndeterminateBackground(Background value)
    {
        urlIndeterminateBackgroundProperty().set(value);
    }


    /**
     * Returns the indeterminate url background
     * 
     * @return the indeterminate url background
     */
    public final Background getUrlIndeterminateBackground()
    {
        return urlIndeterminateBackgroundProperty().get();
    }

    /* --------------------- */

    /**
     * The background property for valid {@code URL}s
     */
    private ObjectProperty<Background> urlValidBackground =
            new SimpleObjectProperty<>(this, "urlValidBackground",
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
            new SimpleObjectProperty<>(this, "urlInvalidBackground",
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
         * invoke internal processes on control receiving or loosing focus
         */
        focusedProperty().addListener((observable, oldValue, newValue) -> {
            /*
             * Always check list as values may be inserted by other controls
             */
            cleanUrlLists();

            if (oldValue == true && newValue == false)
            /*
             * Lost focus
             */
            {
                processLostFocus();
            }
        });

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
        getEditor().setOnKeyTyped(

                event -> typed(event));

        /*
         * Track changes to the URL list 
         */
        getItems().addListener(
                (ListChangeListener.Change<? extends String> c) -> change(c));

        updateProps(false, false, false, false, null);

        /*
         * Create the control
         */
        FXMLLoader fxmlLoader =
                new FXMLLoader(
                        getClass().getResource(
                                "URLComboBox.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
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
        processNewValue(addItem(getEditor().getText()), getEditor().getText());
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
                validUrlStringReference.remove(n);
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
                    validUrlStringReference.put(n, x);
                }
                else
                /*
                 * url string is not a valid url, make sure it's removed
                 */
                {
                    validUrlStringReference.remove(n);
                    getItems().remove(n);
                }
            }
        }
        // Platform.runLater(() -> syncUrlLists());
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
        return validUrlStringReference.get(getValue());
    }


    /**
     * Updates the control value to the value in the editor and adds it to the
     * drop down if valid
     */
    public void updateValue()
    {
        setValue(getEditor().getText());
        addItem(getEditor().getText());
    }


    /**
     * Convenience function to add a single url to the list
     * <p>
     * This does not validate the URLdirectly. Validation is done by a listener.
     * 
     * @param urlString
     *            the file to add
     * @return true if the file was added
     */
    public boolean addItem(String urlString)
    {
        URL x;
        if (urlString != null && !(urlString = urlString.trim()).isEmpty()
                && (x = validate(urlString)) != null)
        {
            if (!validUrlStringReference.containsKey(urlString))
            {
                validUrlStringReference.put(urlString, x);
            }
            else if (!getItems().contains(urlString))
            {
                getItems().add(urlString);
            }
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


    /**
     * Toggle the protocol between insecure and securable protocol versions
     * 
     * @return true if the protocol was toggled
     */
    public void toggleProtocolSecurity()
    {
        if (isValid())
        /*
         * Only valid URLs can be toggled
         */
        {
            String protocol = getUrlValue().getProtocol();
            if (securableProtocols.containsKey(protocol))
            /*
             * It's an insecure protocol, moving to secure
             */
            {
                setValue(getUrlValue().toString().replaceFirst(protocol,
                        securableProtocols.get(protocol)));
                return;// true;
            }
            else if (securableProtocols.containsValue(protocol))
            /*
             * It's a secure protocol, moving to insecure
             */
            {
                java.util.Iterator<String> i =
                        securableProtocols.keySet().iterator();
                String key;
                while (i.hasNext())
                {
                    key = i.next();
                    if (protocol.equals(securableProtocols.get(key)))
                    /*
                     * Found the key for this value
                     */
                    {
                        setValue(getUrlValue().toString().replaceFirst(protocol,
                                key));
                        return;// true;
                    }
                }
            }
        }

        //return false;
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
        unsecurableProtocol.set(!securable);
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
                && (getEditor().backgroundProperty().get() == null
                        || !getEditor().backgroundProperty().get()
                                .equals(getUrlIndeterminateBackground())))
        /* 
         * Not the Enter key so set an indeterminate background
         * if not already set
         */
        {
            getEditor().backgroundProperty()
                    .set(getUrlIndeterminateBackground());
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
        if (validUrlStringReference.containsValue(null))
        /*
         * Go through the map.
         * If there are missing urls in the reference list, expunge
         */
        {
            List<String> removals = new ArrayList<>();
            validUrlStringReference.forEach((key, value) -> {
                if (value == null)
                /*
                 * Remove the key from both lists, hence syncronizing them
                 */
                {
                    removals.add(key);
                }
            });

            removals.forEach(key -> {
                validUrlStringReference.remove(key);
                getItems().remove(key);
            });
        }
    }


    /**
     * Cleans up the UrlStringReference from the items list placed in the
     * control at initialization from FXML definition.
     */
    private void cleanUrlLists()
    {
        for (String urlString : getItems())
        {
            validUrlStringReference.put(urlString, validate(urlString));
        }
        syncUrlLists();
    }


    /**
     * Modify control as needed when focus is lost
     */
    private void processLostFocus()
    {
        processNewValue(true, getEditor().getText());
    }


    /**
     * Process the attribute for the added urls string
     * 
     * @param url
     *            the URL to display
     */
    private void processNewValue(boolean processUnconditionally,
            String urlString)
    {
        if (urlString == null || (urlString = urlString.trim()).isEmpty())
        {
            updateProps(true, false, false, false,
                    getUrlIndeterminateBackground());
            return;
        }

        URL url;

        if ((url = validate(urlString)) != null
                &&
                (processUnconditionally
                        || (validUrlStringReference.containsKey(urlString)
                                && (validUrlStringReference
                                        .get(urlString)) != null)))
        /*
         * Url was added or already known
         */
        {
            if (getSecurableProtocols().containsKey(
                    url.getProtocol()))
            /*
             * Not secured, but securable
             */
            {
                updateProps(true, false, true, false,
                        getUrlValidBackground());
            }
            else if (getSecurableProtocols().containsValue(
                    url.getProtocol()))
            /*
             * Securable
             */
            {
                updateProps(true, false, true, true,
                        getUrlValidBackground());
            }
            else
            /*
             * Not secure nor securable 
             */
            {
                updateProps(true, false, false, false,
                        getUrlValidBackground());
            }
            return;
        }

        /*
         * Invalid url
         */
        updateProps(false, false, false, false, getUrlInvalidBackground());

    }

}
