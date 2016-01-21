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
import java.net.URL;

import org.eclipse.jdt.annotation.Nullable;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;

/**
 * Component to enter, choose URLs and keep a pull-down of the past choices.
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
    /**
     * The selected URL property
     */
    private ReadOnlyObjectWrapper<URL> urlProperty =
            new ReadOnlyObjectWrapper<URL>();

    private ReadOnlyBooleanWrapper usesSSLProperty =
            new ReadOnlyBooleanWrapper();

    private ReadOnlyBooleanWrapper supportsSSLProperty =
            new ReadOnlyBooleanWrapper();

    private ReadOnlyBooleanWrapper isValidProperty =
            new ReadOnlyBooleanWrapper();

    private ReadOnlyBooleanWrapper isReachableProperty =
            new ReadOnlyBooleanWrapper();

    /* ----------------------------------------------------------------
    * 
    * State vars
    * 
    * ----------------------------------------------------------------
    */

    private ListCell<URL> listCell;

    Background b1 = new Background(new BackgroundFill(Color.AZURE, null, null));
    Background b2 = new Background(new BackgroundFill(Color.RED, null, null));

    /* ================================================================
    * 
    * Code
    * 
    * ================================================================
    */


    /**
     * 
     */
    public URLComboBox()
    {
        super();
        initialize();
    }

    /* ----------------------------------------------------------------
     * 
     * Properties
     * 
     * ----------------------------------------------------------------
     */


    /**
     * Returns the current chosen {@code URL} or {@code null} is none chosen.
     * 
     * @return the chosen {@code URL}
     */
    public ReadOnlyObjectProperty<URL> getURL()
    {
        return urlProperty.getReadOnlyProperty();
    }


    /**
     * Does this {@code URL} use SSL?
     * 
     * @return true if the URL uses SSL
     */
    public ReadOnlyBooleanProperty usesSSL()
    {
        return usesSSLProperty.getReadOnlyProperty();
    }


    /**
     * Does the current URL Scheme support SSL
     * 
     * @return true if the scheme supports SSL
     */
    public ReadOnlyBooleanProperty supportsSSL()
    {
        return supportsSSLProperty.getReadOnlyProperty();
    }


    /**
     * Is the current URL valid
     * 
     * @return true if the scheme supports SSL
     */
    public ReadOnlyBooleanProperty isValidProperty()
    {
        return isValidProperty.getReadOnlyProperty();
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
                //                if (!empty)
                //                {
                //                    if (item == NEW_FILE_PROMPT_INDICATOR)
                //                    {
                //                        setGraphic(getNewFilePrompt());
                //                    }
                //                    else
                //                    {
                //                        setText(item.getPath());
                //                    }
                //                }
                //                else
                //                {
                setText(null);
                // }
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
        getEditor().backgroundProperty().set(b2);
        validate(getEditor().getText());
    }


    /**
     * Hide event handler.
     * <p>
     * If the chooser should be open, but isn't, open the choosers and update
     * the file list.
     * <p>
     * Update the selected value with the last selected.
     */
    private void hide(Event event)
    {
        System.out.printf("Hide: [%1$s]\n", event);
    }


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

        setOnAction(

                event -> {
                    action(event);
                });

        getEditor().backgroundProperty().set(b1);

        getEditor().setOnKeyTyped(event -> typed(event));

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
        System.out.printf("Validating endpoint: [%1$s]\n", endpoint);
        try
        {
            url = new URL(endpoint);
        }
        catch (MalformedURLException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return url;
    }

}
