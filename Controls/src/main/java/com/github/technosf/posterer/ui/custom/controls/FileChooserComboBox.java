/*
 * Copyright 2015 technosf [https://github.com/technosf]
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

import java.io.File;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 * Component to choose files and keep a pull-down of the past choices.
 * 
 * @author technosf
 * @since 0.0.1
 * @version 0.0.1
 */
public class FileChooserComboBox
        extends ComboBox<File>
{

    /**
    * 
    */
    private static final String NEW_FILE_PROMPT_PATH = "";

    private static final File NEW_FILE_PROMPT_INDICATOR =
            new File(NEW_FILE_PROMPT_PATH);

    private Label newFilePrompt;

    /**
     * The selected file property
     */
    private ReadOnlyObjectWrapper<File> chosenFileProperty =
            new ReadOnlyObjectWrapper<File>();

    /**
     * The selected file property
     */
    private ReadOnlyBooleanWrapper isChooserOpenProperty =
            new ReadOnlyBooleanWrapper();

    /* ----------------------------------------------------------------
    * 
    * State vars
    * 
    * ----------------------------------------------------------------
    */

    /**
     * Should the chooser be opened
     */
    private boolean requestChooserOpenFlag = false;

    /**
     * Is the chooser opened?
     */
    private SimpleBooleanProperty isChooserOpenFlag =
            new SimpleBooleanProperty(false);

    /**
     * The last directory the file chooser selected
     */
    private File lastDirectorySelected;

    private ListCell<File> listCell;

    /* ----------------------------------------------------------------
    * 
    * Sub Components
    * 
    * ----------------------------------------------------------------
    */

    /**
     * The FileChooser that performs the file system navigation and selection.
     */
    private final FileChooser fileChooser = new FileChooser();

    /* ================================================================
    * 
    * Code
    * 
    * ================================================================
    */

    /**
    * 
    */
    //    public FileChooserComboBox(Label newFilePrompt,
    //            List<ExtensionFilter> extentionFilters)
    //    {
    //        super();
    //        initialize();
    //        this.newFilePrompt = newFilePrompt;
    //        this.extentionFilter = extentionFilter;
    //    }
    //
    //
    //    /**
    //     * 
    //     */
    //    public FileChooserComboBox(Label newFilePrompt)
    //    {
    //        super();
    //        initialize();
    //        this.newFilePrompt = newFilePrompt;
    //    }
    //
    //
    //    /**
    //     * 
    //     */
    //    public FileChooserComboBox(List<ExtensionFilter> extentionFilters)
    //    {
    //        super();
    //        initialize();
    //        this.extentionFilters = extentionFilters;
    //    }


    /**
     * 
     */
    public FileChooserComboBox()
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


    // -- NewFilePrompt

    /**
     * Sets the new file prompt
     * 
     * @param value
     *            the new file prompt
     */
    public Label getNewFilePrompt()
    {
        if (newFilePrompt == null)
        /*
         * No new file prompt has been defined, so use the default
         */
        {
            newFilePrompt = new Label();
        }

        return newFilePrompt;
    }


    /**
     * Sets the new file prompt
     * <p>
     * The prompt is the first item in the item list
     * 
     * @param value
     *            the new file prompt
     */
    public void setNewFilePrompt(Label label)
    {
        newFilePrompt = label;
    }


    /**
     * @return
     */
    public ReadOnlyObjectProperty<File> getChosenFile()
    {
        return chosenFileProperty.getReadOnlyProperty();
    }


    /**
     * @return
     */
    public ReadOnlyBooleanProperty isChooserOpen()
    {
        return isChooserOpenProperty.getReadOnlyProperty();
    }


    // -- Items

    /**
     * The items to display in the choice box. The selected item (as indicated
     * in the
     * selection model) must always be one of these items.
     */
    //    private ObjectProperty<ObservableList<ExtensionFilter>> fileExtentions =
    //            new SimpleObjectProperty(fileChooser.getExtensionFilters());

    public final void setFileExtentionFilter(ExtensionFilter value)
    {
        fileChooser.getExtensionFilters().clear();
        fileChooser.getExtensionFilters().addAll(value);
    }


    public final ObservableList<ExtensionFilter> getFileExtentions()
    {
        return fileChooser.getExtensionFilters();
    }


    /*
     * ------------------------
     */

    /**
     * @param param
     * @return
     */
    public ListCell<File> cellFactory(ListView<File> param)
    {
        if (param.getItems() != null && param.getItems().size() == 0)
        /*
         * Empty list, so add the new file prompt indicator
         */
        {
            param.getItems().add(NEW_FILE_PROMPT_INDICATOR);
        }

        listCell = new ListCell<File>()
        {
            @Override
            public void updateItem(File item, boolean empty)
            {
                super.updateItem(item, empty);
                if (!empty)
                {
                    if (item == NEW_FILE_PROMPT_INDICATOR)
                    {
                        setGraphic(getNewFilePrompt());
                    }
                    else
                    {
                        setText(item.getPath());
                    }
                }
                else
                {
                    setText(null);
                }
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
     * Display the selection
     * <p>
     * Fired on requests to select a new file and sets the open chooser
     * flag if needed.
     */
    private void display(ActionEvent event)
    {
        //FIXME Handle editable event
        if (getValue() != null
                && getValue().equals(NEW_FILE_PROMPT_INDICATOR))
        /* 
         * The new file prompt was selected, so flag the file chooser to open                   
        */
        {
            requestChooserOpenFlag = true; // Flag to open the chooser
        }
        else
        /* 
         * Select an existing value, update base dir
         * 
         */
        {
            if (getValue() != null)
            {
                chosenFileProperty.set(getValue());
                lastDirectorySelected = getValue().getParentFile();
            }
        }
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
        if (!isChooserOpenProperty.getValue()
                && requestChooserOpenFlag)
        /*
         *  Chooser is not open, but has been requested to open:
         *  Open the chooser and updates the selected values
         */
        {
            updateFileSelection(chooseFile());
        }

        setValue(chosenFileProperty.get()); // Sets the displayed value with the last selected

        requestChooserOpenFlag = false; // Chooser does not need to be opened.
    }


    /* ----------------------------------------------------------------
    * 
    * Attribute Functions
    * 
    * ----------------------------------------------------------------
    */

    /**
     * Set the window title.
     * 
     * @param title
     *            the window title
     */
    public void setTitle(String title)
    {
        fileChooser.setTitle(title);
    }


    /**
     * Set the window initial directory
     * 
     * @param directory
     *            the initial directory
     */
    public void setInitialDirectory(String directory)
    {
        File dir = new File(directory);
        if (dir.exists() && dir.isDirectory())
        {
            fileChooser.setInitialDirectory(dir);
        }
    }


    /**
     * Sets the new file prompt
     * <p>
     * The prompt is the first item in the item list
     * 
     * @param value
     *            the new file prompt
     */
    public void setExtensionFilter(ExtensionFilter filter)
    {
        fileChooser.setSelectedExtensionFilter(filter);
    }


    /**
     * Adds file to items in the list, checking for pre-existance first based on
     * path.
     * 
     * @param file
     *            the file to add
     * @return true if the file was added
     */
    public boolean addItem(File file)
    {
        if (file != null
                && file.exists()
                && file.isFile()
                && file.canRead()
                && !getItems().contains(file))
        /*
         * / Selected a new-to-us, extant file
         */
        {
            //filePaths.add(file);
            getItems().add(file);
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
    public boolean removeItem(File file)
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
                                "FileChooserComboBox.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        setOnAction(
                event -> {
                    display(event);
                });

        setOnHiding(
                event -> {
                    hide(event);
                });

        this.getItems().add(0, NEW_FILE_PROMPT_INDICATOR);

        setCellFactory(

                param -> cellFactory(param));

        isChooserOpenProperty.bind(isChooserOpenFlag);

    }


    /**
     * Opens the file chooser and returns the file chosen, if any.
     * <p>
     * If the chooser is already open, or no file is chosen returns {@code null}
     * 
     * @return the chosen file.
     */
    private File chooseFile()
    {
        File file = null;

        if (!isChooserOpenProperty.getValue())
        // Chooser isn't open, so open it.
        {
            isChooserOpenFlag.set(true); // Chooser is opening

            fileChooser.setInitialDirectory(lastDirectorySelected); // Set chooser location to last directory

            /*
             * This blocking call opens the chooser until a file is chosen or op is cancelled and the chooser closes.
             */
            file = fileChooser.showOpenDialog(this.getScene()
                    .getWindow());

            isChooserOpenFlag.set(false);
        }

        return file;
    }


    /**
     * Updates the file selection data where the given file exists.
     * 
     * @param file
     *            the current selected file
     */
    private void updateFileSelection(File file)
    {
        if (getItems().contains(file) || addItem(file))
        /*
         * / Selected a extant file
         */
        {
            chosenFileProperty.set(file);
            lastDirectorySelected = file.getParentFile();
        }
    }
}
