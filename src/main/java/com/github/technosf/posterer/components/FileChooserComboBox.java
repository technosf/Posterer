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
import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.stage.FileChooser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author technosf
 * @since 0.0.1
 * @version 0.0.1
 */
public class FileChooserComboBox extends ComboBox<String>
{
    private static final Logger logger = LoggerFactory
            .getLogger(FileChooserComboBox.class);

    private final FileChooser fileChooser = new FileChooser();

    private FileChooser.ExtensionFilter filter =
            new FileChooser.ExtensionFilter("All files", "*");

    private Parent root;

    private File lastDirectory;
    private String lastSelected;

    private String newFilePromptProperty;

    boolean choose = false;
    boolean choosing = false;


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
        //setOnShowing(showHandler);
        setOnHiding(hideHandler);
        logger.debug("Instantiated.");
    }

    /* --------------  Handler logic  ------------- */

    /**
     * Show
     */
    private EventHandler<Event> showHandler =
            new EventHandler<Event>()
            {
                @Override
                public void handle(Event event)
                {
                    logger.debug("Show Event ** starts.");

                    if (!newFilePromptProperty.equals(getSelectionModel()
                            .getSelectedItem()))
                    {
                        logger.debug("Show Event ** Selected.");
                        lastSelected = getValue();
                    }

                    logger.debug("Show Event ** ends.");

                }
            };
    /**
     * Action
     */
    private EventHandler<ActionEvent> actionHandler =
            new EventHandler<ActionEvent>()
            {
                @Override
                public void handle(ActionEvent event)
                {
                    logger.debug(
                            "Action Event ** starts, selected value: [{}].",
                            getValue());

                    if (newFilePromptProperty.equals(getSelectionModel()
                            .getSelectedItem()))
                    // open the file chooser
                    {
                        logger.debug(
                                "Action Event ** Opening Chooser");
                        choose = true;
                    }

                    logger.debug("Action Event ** ends.");
                }
            };

    /**
     * Hide
     */
    private EventHandler<Event> hideHandler =
            new EventHandler<Event>()
            {
                @Override
                public void handle(Event event)
                {
                    logger.debug("Hide Event ** starts.");

                    if (!choosing
                            && choose)
                    // Clear the new file prompt and replace with the last selected
                    {
                        logger.debug(
                                "Hide Event ** Opening Chooser");

                        setStuff(chooseFile());
                    }

                    setValue(lastSelected);

                    choose = false;

                    logger.debug("Hide Event ** ends.");
                }
            };


    /* --------------  Event functions  ------------- */

    /**
     * @return
     */
    private File chooseFile()
    {
        File file = null;

        if (!choosing)
        {
            choosing = true;

            logger.debug("Chooser open.");

            fileChooser.setInitialDirectory(lastDirectory);
            file = fileChooser.showOpenDialog(root.getScene()
                    .getWindow());

            logger.debug("Chooser closed.");

        }

        choosing = false;

        return file;
    }


    private void setStuff(File file)
    {
        if (file != null && file.exists())
        // Selected a file
        {
            logger.debug("File selected");

            try
            {
                lastDirectory = file.getParentFile();
                lastSelected = file.getCanonicalPath();

                if (!getItems().contains(lastSelected))
                {
                    getItems().add(lastSelected);
                    logger.debug("File added");
                }
            }
            catch (IOException e)
            {
                logger.error("Extracting filename", e);
            }
        }
    }


    /* ---------------  Custom Properties  -------------- */

    /**
     * @return
     */
    public String getNewFilePrompt()
    {
        return newFilePromptProperty;
    }


    /**
     * @param value
     */
    public void setNewFilePrompt(String value)
    {
        if (newFilePromptProperty != null && getItems().size() > 0
                && newFilePromptProperty.equals(getItems().get(0)))
        // First time setting the property
        {
            getItems().remove(0);
        }
        getItems().add(0, newFilePromptProperty = value);
    }


    /**
     * @return
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
     *            the parent root.
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
