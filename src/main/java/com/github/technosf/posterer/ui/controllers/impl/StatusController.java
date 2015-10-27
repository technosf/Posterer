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
package com.github.technosf.posterer.ui.controllers.impl;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.technosf.posterer.models.StatusModel;
import com.github.technosf.posterer.ui.controllers.Controller;
import com.github.technosf.posterer.ui.controllers.impl.base.AbstractController;

import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

/**
 * JavaFX Controller for the global <em>status</em> window and tray.
 * <p>
 * Controls the stats
 * 
 * @author technosf
 * @since 0.0.1
 * @version 0.0.1
 */
public class StatusController
        extends AbstractController
        implements Controller
{

    /**
     * The FXML definition of the View
     */
    public final static String FXML = "/fxml/Status.fxml";

    /* ---- Constants ----- */

    private static final Logger LOG = LoggerFactory
            .getLogger(StatusController.class);

    /**
     * The window title formatter
     */
    private final static String FORMAT_TITLE =
            "Posterer :: Status Window";

    /**
     * Status
     */
    private final StringBuilder status = new StringBuilder();

    private final StatusModel statusModel = new StatusModel()
    {

        /**
         * {@inheritDoc}
         *
         * @see com.github.technosf.posterer.models.StatusModel#write(java.lang.String)
         */
        public void write(String message)
        {
            status.append(message);
            statusWindow.setText(message);
            statusWindow.setScrollTop(Double.MAX_VALUE);
            statusWindow.appendText("\n");
        }


        /**
         * {@inheritDoc}
         *
         * @see com.github.technosf.posterer.models.StatusModel#write(java.lang.String,
         *      java.lang.Object)
         */
        public void write(String format, Object... args)
        {
            write(String.format(format, args));
        }


        /**
         * {@inheritDoc}
         *
         * @see com.github.technosf.posterer.models.StatusModel#append(java.lang.String)
         */
        public void append(String message)
        {
            if (StringUtils.isNotBlank(message))
            {
                //status.append("\n").append(message);
                //statusWindow.setText(status.toString());
                statusWindow.appendText(message);
                statusWindow.setScrollTop(Double.MAX_VALUE);
                statusWindow.appendText("\n");

            }
        }


        /**
         * {@inheritDoc}
         *
         * @see com.github.technosf.posterer.models.StatusModel#append(java.lang.String,
         *      java.lang.Object)
         */
        public void append(String format, Object... args)
        {
            append(String.format(format, args));
        }
    };

    /*
     * ------------ FXML Components -----------------
     */

    @FXML
    private TextArea statusWindow;


    /*
     * ------------ Statics -----------------
     */

    /**
     * Instantiates a StatusController, binding the given {@code StringProperty}
     * to its text.
     * 
     * @param stringProperty
     * @return a new StatusControler
     */
    public static StatusController loadController(StringProperty stringProperty)
    {
        Stage stage = new Stage();
        StatusController controller = null;

        try
        {
            controller = (StatusController) StatusController
                    .loadController(stage, FXML);
            controller.updateStage(stringProperty);
        }
        catch (IOException e)
        {
            LOG.error("Cannot load Controller.", e);
        }

        return controller;
    }


    /*
     * ------------ FX Code -----------------
     */

    /**
     * Instantiate and set the title.
     */
    public StatusController()
    {
        super(FORMAT_TITLE); // TODO Title may not be needed
        LOG.debug("Instantiated.");
    }


    /**
     * Updates this stage with event handlers
     * 
     * @param responseModel
     */
    public void updateStage(final StringProperty status)
    {
        // Bind the status text property to the StatusController value
        status.bind(statusWindow.textProperty());
    }


    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.ui.controllers.Controller#initialize()
     */
    @Override
    public void initialize()
    {
        LOG.debug("Initialize.");
    }


    /*
     * ------------ Getters -----------------
     */

    /**
     * Returns the status object
     * 
     * @return the status model
     */
    public StatusModel getStatusModel()
    {
        return statusModel;
    }
}
