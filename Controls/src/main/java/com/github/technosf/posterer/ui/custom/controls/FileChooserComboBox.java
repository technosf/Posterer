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

import java.io.File;
import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
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
 * Selector component to choose files from the local file system and keep a
 * pull-down of the past choices for easy re-selection.
 * 
 * @author technosf
 * @since 0.0.1
 * @version 0.0.1
 */
@SuppressWarnings("restriction")
public class FileChooserComboBox
        extends ComboBox<File>
{

    /**
     * Indicator (a blank file path) for opening the FileChooser
     */
    private static final File NEW_FILE_PROMPT_INDICATOR =
            new File("");

    /* ================================================================
     * 
     * Properties
     * 
     * ================================================================
     */

    /* ----------------------------------------------------------------
     *
     * chosenFile
     * 
     * ----------------------------------------------------------------
     */

    /**
     * The selected file name property wrapper
     */
    private ReadOnlyStringWrapper chosenFileName =
            new ReadOnlyStringWrapper(this, "choosenFileName");


    /**
     * Return the selected file name property
     * 
     * @return the chosen file name property
     */
    public ReadOnlyStringProperty chosenFileNameProperty()
    {
        return chosenFileName.getReadOnlyProperty();
    }


    /**
     * Returns the current chosen file name or {@code null} is none chosen.
     * 
     * @return the chosen file name
     */
    public String getChosenFileName()
    {
        return chosenFileName.get();
    }

    /* ----------------------------------------------------------------
     *
     * isChooserOpen
     * 
     * ----------------------------------------------------------------
     */

    /**
     * The {@code FileChooser} open property wrapper
     */
    private ReadOnlyBooleanWrapper isChooserOpen =
            new ReadOnlyBooleanWrapper();


    /**
     * Returns the {@code FileChooser} open property
     *
     * @return The {@code FileChooser} open property
     */
    public ReadOnlyBooleanProperty isChooserOpenProperty()
    {
        return isChooserOpen.getReadOnlyProperty();
    };


    /**
     * Indicates if the {@code FileChooser} is open or not
     * 
     * @return true if the {@code FileChooser} is open
     */
    public final boolean getIsChooserOpen()
    {
        return isChooserOpen.get();
    }

    /* ----------------------------------------------------------------
     *
     * NewFilePrompt
     * 
     * ----------------------------------------------------------------
     */

    /**
     * The new file prompt property wrapper
     */
    private ObjectProperty<Label> newFilePrompt =
            new SimpleObjectProperty<>(this, "newFilePrompt");


    /**
     * Returns the new file prompt property
     * <p>
     * The new file prompt is a {@code Label} so that it can be formatted to
     * stand out from the list of previously selected files.
     * 
     * @return The new file prompt property
     */
    public ObjectProperty<Label> newFilePromptProperty()
    {
        return newFilePrompt;
    }


    /**
     * Sets the new file prompt
     * 
     * @param value
     *            the file prompt
     */
    public final void setNewFilePrompt(Label value)
    {
        newFilePromptProperty().set(value);
    }


    /**
     * Returns the new file prompt
     * 
     * @return the new file prompt
     */
    public final Label getNewFilePrompt()
    {
        return newFilePromptProperty().get();
    }


    /* ----------------------------------------------------------------
     *
     * ExtensionFilters
     * 
     * ----------------------------------------------------------------
     */

    /**
     * Returns the list of {@code FileChooser} filters
     * 
     * @return {@code ExtensionFilter}s used by the file chooser
     */
    public ObservableList<FileChooser.ExtensionFilter> getExtensionFilters()
    {
        return fileChooser.getExtensionFilters();
    }

    /* ================================================================
    * 
    * State vars
    * 
    * ================================================================
    */

    /**
     * Should the chooser be opened
     */
    private boolean requestChooserOpenFlag = false;

    /**
     * The last directory the file chooser selected
     */
    private File lastDirectorySelected;

    /**
     * The list of previously selected files
     */
    private ListCell<File> listCell;

    /* ================================================================
    * 
    * Sub Components
    * 
    * ================================================================
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
     * Default constructor
     */
    public FileChooserComboBox()
    {
        super();
        initialize();
    }


    /**
     * Constructor that takes a new file prompt and extension filters
     * 
     * @param newFilePrompt
     *            the new file prompt
     * @param extentionFilters
     *            extension filters to apply to chooser
     */
    public FileChooserComboBox(Label newFilePrompt,
            List<ExtensionFilter> extentionFilters)
    {
        super();
        initialize();
        setNewFilePrompt(newFilePrompt);
        getExtensionFilters().addAll(extentionFilters);
    }


    /**
     * Constructor that takes a new file prompt
     * 
     * @param newFilePrompt
     *            the new file prompt
     */
    public FileChooserComboBox(Label newFilePrompt)
    {
        super();
        initialize();
        setNewFilePrompt(newFilePrompt);
    }


    /**
     * Constructor that takes extension filters
     * 
     * @param extentionFilters
     *            extension filters to apply to chooser
     */
    public FileChooserComboBox(List<ExtensionFilter> extentionFilters)
    {
        super();
        initialize();
        getExtensionFilters().addAll(extentionFilters);
    }

    /* ----------------------------------------------------------------
     * 
     * Display helpers
     * 
     * ----------------------------------------------------------------
     */


    /**
     * CellFactory to display files in the drop down
     * 
     * @param param
     *            the file selection
     * @return the cell to display
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
     *
     * @param event
     *            the action event
     */
    private void display(ActionEvent event)
    {
        //TODO Handle editable event

        if (getValue() == null)
            /*
             * Not a known value, so ignore
             */
            return;

        if (getValue().equals(NEW_FILE_PROMPT_INDICATOR))
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
            lastDirectorySelected = getValue().getParentFile();
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
        if (!getIsChooserOpen()
                && requestChooserOpenFlag)
        /*
         *  Chooser is not open, but has been requested to open:
         *  Open the chooser and updates the selected values
         */
        {
            updateFileSelection(chooseFile());
        }

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
     * Set the {@code FileChooser} initial directory
     * 
     * @param directory
     *            the initial directory
     */
    public void setInitialDirectory(String directory)
    {
        File dir = new File(directory);
        if (dir.exists() && dir.isDirectory())
        /*
         * it's a valid directory
         */
        {
            fileChooser.setInitialDirectory(dir);
        }
    }


    /**
     * Sets the {@code FileChooser} filter
     * 
     * @param filter
     *            the file extension filter
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
         * Selected a new-to-us, extant file
         */
        {

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

    /**
     * Initialized the components
     * <p>
     * Loads the component, sets listeners and events.
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

        valueProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null && newValue != null
                    && oldValue.getPath() != null
                    && newValue.getPath() != null
                    && !oldValue.getPath().isEmpty()
                    && !newValue.getPath().isEmpty())
            {
                updateFileSelection(newValue);
            }
        });

        this.getItems().add(0, NEW_FILE_PROMPT_INDICATOR);

        setCellFactory(
                param -> cellFactory(param));
    }


    /**
     * Opens the {@code FileChooser} and returns the file chosen, if any.
     * <p>
     * If the chooser is already open, or no file is chosen returns {@code null}
     * 
     * @return the chosen file.
     */
    private File chooseFile()
    {
        File file = null;

        if (!isChooserOpen.getValue())
        /*
         * Chooser isn't open, so open it.
         */
        {

            fileChooser.setInitialDirectory(lastDirectorySelected); // Set chooser location to last directory

            isChooserOpen.set(true); // Chooser is opening

            /*
             * This blocking call opens the chooser until a file is chosen or op is cancelled and the chooser closes.
             */
            file = fileChooser.showOpenDialog(this.getScene()
                    .getWindow());

            isChooserOpen.set(false);
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
         * Selected a extant file
         */
        {
            setValue(file);
            chosenFileName.set(file.toString());
            lastDirectorySelected = file.getParentFile();
        }
    }
}
