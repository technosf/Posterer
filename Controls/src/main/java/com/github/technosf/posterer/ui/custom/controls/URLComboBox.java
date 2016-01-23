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

import org.eclipse.jdt.annotation.Nullable;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;

/**
 * Component to enter, choose network connection URLs and keep a pull-down of
 * the past choices.
 * <p>
 * Control can perform validation
 * 
 * @author technosf
 * @since 0.0.1
 * @version 0.0.1
 */
public class URLComboBox
        extends ComboBox<URL>
{

    public final static Background URL_DEFAULT_BACKGROUND_INDETERMINATE =
            new Background(new BackgroundFill(Color.SILVER, null, null));
    public final static Background URL_DEFAULT_BACKGROUND_INVALID =
            new Background(new BackgroundFill(Color.RED, null, null));
    public final static Background URL_DEFAULT_BACKGROUND_VALID =
            new Background(new BackgroundFill(Color.CHARTREUSE, null, null));

    /* ================================================================
     * 
     * Properties
     * 
     * ================================================================
     */

    /* ----------------------------------------------------------------
     *
     * Proxy
     * 
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
     * URL
     * 
     * ----------------------------------------------------------------
     */

    /**
     * The {@code URL} property wrapper
     */
    private ReadOnlyObjectWrapper<URL> url =
            new ReadOnlyObjectWrapper<URL>(this, "url");


    /**
     * Returns the {@code URL} property
     * 
     * @return the {@code URL} property
     */
    public ReadOnlyObjectProperty<URL> urlProperty()
    {
        return url.getReadOnlyProperty();
    }


    /**
     * Returns the current chosen {@code URL} or {@code null} is none chosen.
     * 
     * @return the chosen {@code URL}
     */
    public final URL getUrl()
    {
        return urlProperty().get();
    }

    /* ----------------------------------------------------------------
     *
     * usesSSL     
     *
     * ----------------------------------------------------------------
     */

    /**
     * The {@code URL} uses SSL property wrapper
     */
    private ReadOnlyBooleanWrapper usesSSL =
            new ReadOnlyBooleanWrapper(this, "usesSSL");


    /**
     * Returns the {@code URL} uses SSL property
     * 
     * @return the {@code URL} uses SSL property
     */
    public ReadOnlyBooleanProperty usesSSLProperty()
    {
        return usesSSL.getReadOnlyProperty();
    }


    /**
     * Is this {@code URL} using SSL?
     * 
     * @return true if the URL uses SSL
     */
    public final boolean getUsesSSL()
    {
        return usesSSLProperty().get();
    }

    /* ----------------------------------------------------------------
     *
     * supportsSSL
     *
     * ----------------------------------------------------------------
     */

    /**
     * The {@code URL} supports SSL property wrapper
     */
    private ReadOnlyBooleanWrapper supportsSSL =
            new ReadOnlyBooleanWrapper(this, "supportsSSL");


    /**
     * Returns the {@code URL} supports SSL property
     * 
     * @return {@code URL} supports SSL property
     */
    public ReadOnlyBooleanProperty supportsSSLProperty()
    {
        return supportsSSL.getReadOnlyProperty();
    }


    /**
     * Does the current URL Scheme support SSL?
     * 
     * @return true if the scheme supports SSL
     */
    public final boolean getSupportsSSL()
    {
        return supportsSSLProperty().get();
    }

    /* ----------------------------------------------------------------
     *
     * isValid
     * 
     * ----------------------------------------------------------------
     */

    /**
     * The selected {@code URL} is valid boolean property wrapper
     */
    private ReadOnlyBooleanWrapper isValid =
            new ReadOnlyBooleanWrapper(this, "isValid");


    /**
     * Returns the selected {@code URL} is valid boolean property
     * 
     * @return the selected {@code URL} is valid boolean property
     */
    public ReadOnlyBooleanProperty isValidProperty()
    {
        return isValid.getReadOnlyProperty();
    }


    /**
     * Is the current {@code URL} valid?
     * 
     * @return true if the selected {@code URL} is valid
     */
    public final boolean getIsValidL()
    {
        return isValidProperty().get();
    }

    /* ----------------------------------------------------------------
     *
     * isReachable
     * 
     * ----------------------------------------------------------------
     */

    /**
     * The selected {@code URL} is reachable boolean property wrapper
     */
    private ReadOnlyBooleanWrapper isReachable =
            new ReadOnlyBooleanWrapper(this, "isReachable");


    /**
     * Returns the selected {@code URL} is reachable boolean property
     * 
     * @return the selected {@code URL} is reachable boolean property
     */
    public ReadOnlyBooleanProperty isReachableProperty()
    {
        return isReachable.getReadOnlyProperty();
    }


    /**
     * Is the current {@code URL} reachable?
     * 
     * @return true if the selected {@code URL} is reachable
     */
    public final boolean getIsReachableProperty()
    {
        return isReachableProperty().get();
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
    public ObjectProperty<Background> urlIdeterminateBackgroundProperty()
    {
        return urlIndeterminateBackground;
    }


    /**
     * Sets the indeterminate url background
     * 
     * @param value
     *            indeterminate url background
     */
    public final void setUrlIdeterminatBackground(Background value)
    {
        urlIdeterminateBackgroundProperty().set(value);
    }


    /**
     * Returns the indeterminate url background
     * 
     * @return the indeterminate url backgroundv
     */
    public final Background getUrlIdeterminatBackground()
    {
        return urlIdeterminateBackgroundProperty().get();
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
     * @return the valid url backgroundv
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
     * @return the invalid url backgroundv
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

    /**
     * The list of previously selected {@code URL}s
     */
    private ListCell<URL> listCell;


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
        initialize();
    }


    /*
     * ------------------------
     */

    /**
     * Determines how to display each Cell
     * 
     * @param param
     *            the non-displayable list item
     * @return the displayable Cell
     */
    public ListCell<URL> cellFactory(ListView<URL> param)
    {
        listCell = new ListCell<URL>()
        {
            @Override
            public void updateItem(URL item, boolean empty)
            {
                super.updateItem(item, empty);
                setText(null);

            }
        };
        return listCell;
    }


    /* ----------------------------------------------------------------
    * 
    * Events and handlers
    * 
    * ----------------------------------------------------------------
    */

    /**
     * Value was entered in the editor.
     * <p>
     * Indicates that the value should be validated
     */
    private void action(ActionEvent event)
    {
        System.out.printf("Action: [%1$s]\n", getEditor().getText());
        //getEditor().backgroundProperty().set(b2);
        validate(getEditor().getText());
    }


    //    /**
    //     * Hide event handler.
    //     * <p>
    //     * If the chooser should be open, but isn't, open the choosers and update
    //     * the file list.
    //     * <p>
    //     * Update the selected value with the last selected.
    //     */
    //    private void hide(Event event)
    //    {
    //        System.out.printf("Hide: [%1$s]\n", event);
    //    }

    private void typed(KeyEvent event)
    {
        System.out.printf("Typed: [%1$s] [%2$s]\n", event.getText(),
                event.getCharacter());
    }


    /* ----------------------------------------------------------------
    * 
    * Attribute Functions
    * 
    * ----------------------------------------------------------------
    */

    /**
     * Adds file to items in the list, checking for pre-existance first based on
     * path.
     * 
     * @param url
     *            the file to add
     * @return true if the file was added
     */
    public boolean addItem(URL url)
    {
        if (url != null
                && !getItems().contains(url))
        /*
         * / Selected a new-to-us, extant file
         */
        {
            //filePaths.add(file);
            getItems().add(url);
            return true;
        }
        return false;
    }


    /**
     * Removes the file from the list of items
     * 
     * @param file
     *            the file to remove
     * @return true if the file was removed
     */
    public boolean removeItem(URL file)
    {
        return getItems().remove(file);
    }


    /* ----------------------------------------------------------------
    * 
    * Utility functions
    * 
    * ----------------------------------------------------------------
    */

    private void initialize()
    {
        FXMLLoader fxmlLoader =
                new FXMLLoader(
                        getClass().getResource(
                                "URLComboBox.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        setCellFactory(

                param -> cellFactory(param));

        addEventHandler(ActionEvent.ACTION,

                event -> {
                    action(event);
                });

        //getEditor().backgroundProperty().set(b1);

        getEditor().setOnKeyTyped(

                event -> typed(event));

    }


    /**
     * Validates a string as an URL, also fires secondary validation
     * 
     * @param endpoint
     *            the endpoint to validate
     * @return Error messages, or null if valid
     */
    private URL validate(final @Nullable String endpoint)
    {
        URL url = null;

        try
        {
            url = new URL(endpoint);
        }
        catch (MalformedURLException e)
        {
            isValid.set(false);
            isReachable.set(false);
            return null;
        }
        return url;
    }

}
