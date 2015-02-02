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

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.stage.FileChooser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Component to choose files and keep a pull-down of the past choices.
 * 
 * @author technosf
 * @since 0.0.1
 * @version 0.0.1
 */
public class FileChooserComboBox extends ComboBox<File>
{
    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory
            .getLogger(FileChooserComboBox.class);

    /**
     * A JavaFX instance to fo the file system navigation
     */
    private final FileChooser fileChooser = new FileChooser();

    /**
     * File filters
     */
    private FileChooser.ExtensionFilter filter =
            new FileChooser.ExtensionFilter("All files", "*");

    /**
     * The parent to spawn the chooser of for modal operation.
     */
    private Parent root;

    /**
     * The last directory the file chooser selected
     */
    private File lastDirectorySelected;

    /**
     * The selected file
     */
    private File fileSelected;

    /**
     * The prompt to display in the combobox drop for kicking off the chooser
     */
    private String newFilePromptProperty;

    /**
     * Should the chooser be opened
     */
    boolean openChooserFlag = false;

    /**
     * Is the chooser opened?
     */
    boolean isChooserOpenFlag = false;


    /*
     * ------------ Code -----------------
     */

    /**
     * Instantiate and set handlers.
     */
    public FileChooserComboBox()
    {
        super();
        fileChooser.setSelectedExtensionFilter(filter);
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
                        fileSelected = getValue();
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

                    setValue(fileSelected); // Sets the displayed value with the last selected

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
                getItems().add(fileSelected = file); // Add the new file to the list
                logger.debug("File added");
            }
        }
    }


    /* ---------------  Custom Properties  -------------- */

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
     * The New File prompt as a property
     * 
     * @return the new file prompt property
     */
    public String newFilePromptProperty()
    {
        return newFilePromptProperty;
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

}
