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

package com.github.technosf.posterer.components;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.property.ReadOnlyObjectPropertyBase;
import javafx.beans.property.ReadOnlyProperty;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.stage.FileChooser;

/**
 * Component to choose files and keep a pull-down of the past choices.
 * 
 * @author technosf
 * @since 0.0.1
 * @version 0.0.1
 */
public class FileChooserComboBox
        extends ComboBox<File>
/* TODO Reimplement without extending Combobox - 
 * Combobox allows implementer to replace the action listener we need
 */
{
    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory
            .getLogger(FileChooserComboBox.class);

    /**
     * Read Only File property. Private class to hold the selected file as a
     * property that can be observer
     */
    private class ROFileProperty
            extends ReadOnlyObjectPropertyBase<File>
    {
        File selectedfile;


        /**
         * Set the selected file when there is a valid change
         * 
         * @param file
         *            the new file for the property
         * @return the current property file
         */
        private File set(final File file)
        {
            if ((selectedfile == null && file != null)
                    ||
                    (selectedfile != null && !selectedfile.equals(file)))
            // Fire only when there is change
            {
                selectedfile = file;
                fireValueChangedEvent();
            }

            return file;
        }


        /**
         * {@inheritDoc}
         *
         * @see javafx.beans.property.ReadOnlyProperty#getBean()
         */
        @Override
        public Object getBean()
        {
            return this;
        }


        /**
         * {@inheritDoc}
         *
         * @see javafx.beans.property.ReadOnlyProperty#getName()
         */
        @Override
        public String getName()
        {
            return "ChoosenFile";
        }


        /**
         * {@inheritDoc}
         *
         * @see javafx.beans.value.ObservableObjectValue#get()
         */
        @Override
        public File get()
        {
            return selectedfile;
        }
    }

    /**
     * A JavaFX instance to fo the file system navigation
     */
    private final FileChooser fileChooser = new FileChooser();

    /**
     * The parent to spawn the chooser of for modal operation.
     */
    private Parent root;

    /**
     * The last directory the file chooser selected
     */
    private File lastDirectorySelected;

    /**
     * The selected file property
     */
    private ROFileProperty fileSelected = new ROFileProperty();

    /**
     * The prompt to display in the combobox drop for kicking off the chooser
     */
    private String newFilePromptProperty;

    /**
     * Filter extension description
     */
    private String filterDescriptionProperty = "All files";

    /**
     * Filter extensions
     */
    private List<String> filterExtensionsProperty = new ArrayList<String>();

    /**
     * Should the chooser be opened
     */
    private boolean openChooserFlag = false;

    /**
     * Is the chooser opened?
     */
    private boolean isChooserOpenFlag = false;


    /*
     * ================== Code  ====================
     */

    /**
     * Instantiate and set handlers.
     */
    public FileChooserComboBox()
    {
        super();
        filterExtensionsProperty.add("*");
        setExtentionFilter();
        setOnAction(actionHandler);
        setOnHiding(hideHandler);
        logger.debug("Instantiated.");
    }

    /* --------------  Handler logic  ------------- */

    /**
     * ComboBox Action event handler
     * <p>
     * Checks for the requests to select a new file and sets the open chooser
     * flag if needed.
     */
    private EventHandler<ActionEvent> actionHandler =
            new EventHandler<ActionEvent>()
            {
                @Override
                public void handle(ActionEvent event)
                {
                    logger.debug(
                            "Action Event -- Starts, selected value: [{}]",
                            getValue());

                    if (getValue() != null
                            && getValue().getPath().equals(
                                    newFilePromptProperty))
                    // The new file prompt was selected, so open the file chooser                   

                    {
                        logger.debug("Action Event -- Open Chooser flagged");
                        openChooserFlag = true; // Flag to open the chooser
                    }
                    else
                    // Select an existing value
                    {
                        logger.debug("Action Event -- Using value selected");
                        fileSelected.set(getValue());
                    }

                    logger.debug("Action Event -- Ends");
                }
            };

    /**
     * ComboBox Hide event handler.
     * <p>
     * If the chooser should be open, but isn't, open the choosers and update
     * the file list.
     * <p>
     * Update the combobox selected value with the last selected.
     */
    private EventHandler<Event> hideHandler =
            new EventHandler<Event>()
            {
                @Override
                public void handle(Event event)
                {
                    logger.debug("Hide Event -- Starts");

                    if (!isChooserOpenFlag
                            && openChooserFlag)
                    // Chooser is not open, but has been requested to open
                    {
                        logger.debug("Hide Event -- Opening Chooser");

                        // Opens the chooser and updates the selected values
                        updateFileSelection(chooseFile());
                    }

                    setValue(fileSelected.get()); // Sets the displayed value with the last selected

                    openChooserFlag = false; // Chooser does not need to be opened.

                    logger.debug("Hide Event -- Ends");
                }
            };


    /* --------------  Event functions  ------------- */

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

        if (!isChooserOpenFlag)
        // Chooser isn't open, so open it.
        {
            isChooserOpenFlag = true; // Chooser is opening

            logger.debug("Chooser opening");

            fileChooser.setInitialDirectory(lastDirectorySelected); // Set chooser location to last directory

            /*
             * This blocking call opens the chooser until a file is chosen or op is cancelled and the chooser closes.
             */
            file = fileChooser.showOpenDialog(root.getScene()
                    .getWindow());

            isChooserOpenFlag = false;
            logger.debug("Chooser closed");
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
        if (file != null && file.exists())
        // Selected a extant file
        {
            logger.debug("File selected");

            lastDirectorySelected = file.getParentFile();

            if (!getItems().contains(file))
            // The file selected isn't in the current list, i.e. it's new
            {
                getItems().add(fileSelected.set(file)); // Add the new file to the list
                logger.debug("File added");
            }
        }
    }


    /* ---------------  Custom Properties  -------------- */

    /**
     * Used for observers for changes in chosen files.
     * 
     * @return the Chosen File property
     */
    public ReadOnlyProperty<File> getChosenFileProperty()
    {
        return fileSelected;
    }


    /**
     * The New File prompt as a property
     * 
     * @return the new file prompt property
     */
    public String newFilePromptProperty()
    {
        return newFilePromptProperty;
    }


    /**
     * Returns the new file prompt
     * 
     * @return the new file prompt
     */
    public String getNewFilePrompt()
    {
        return newFilePromptProperty;
    }


    /**
     * Sets the new file prompt
     * 
     * @param value
     *            the new file prompt
     */
    public void setNewFilePrompt(String value)
    {
        if (newFilePromptProperty != null && getItems().size() > 0
                && newFilePromptProperty.equals(getItems().get(0)))
        // First time setting the property
        {
            getItems().remove(0);
        }
        getItems().add(0, new File(newFilePromptProperty = value));

    }


    /**
     * @return
     */
    public String filterDescriptionProperty()
    {
        return filterDescriptionProperty;
    }


    /**
     * @param description
     */
    public String getFilterDescription()
    {
        return filterDescriptionProperty;
    }


    /**
     * @param description
     */
    public void setFilterDescription(String description)
    {
        filterDescriptionProperty = description;
        setExtentionFilter();

    }


    /**
     * @return
     */
    public String filterExtensionsProperty()
    {
        return filterDescriptionProperty;
    }


    /**
     * @param extentions
     * @return
     */
    public List<String> getFilterExtensions()
    {
        return filterExtensionsProperty;
    }


    /**
     * @param extentions
     */
    public void setFilterExtensions(List<String> extentions)
    {
        filterExtensionsProperty = extentions;
        setExtentionFilter();
    }


    /* ---------------  Setter, Getters  -------------- */

    /**
     * Sets the root to attach the FileChooser to.
     * 
     * @param parent
     *            the choosers parent root.
     */
    public void setRoot(Parent root)
    {
        this.root = root;
    }


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


    /* ----------------  Utilities  -------------------- */

    /**
     * Clears and resets the FileChooser ExtentionFilter
     */
    private final void setExtentionFilter()
    {
        FileChooser.ExtensionFilter filter =
                new FileChooser.ExtensionFilter(filterDescriptionProperty,
                        filterExtensionsProperty);
        fileChooser.getExtensionFilters().clear();
        fileChooser.getExtensionFilters().add(filter);
        fileChooser.setSelectedExtensionFilter(filter);
    }

}
