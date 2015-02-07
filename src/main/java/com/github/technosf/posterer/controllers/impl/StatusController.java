package com.github.technosf.posterer.controllers.impl;

import java.io.IOException;

import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.technosf.posterer.controllers.AbstractController;
import com.github.technosf.posterer.controllers.Controller;

public class StatusController extends AbstractController
        implements Controller
{
    /**
     * The FXML definition of the View
     */
    public final static String FXML = "/fxml/Status.fxml";

    private static final Logger logger = LoggerFactory
            .getLogger(StatusController.class);

    /**
     * The window title formatter
     */
    private final static String FORMAT_TITLE =
            "Posterer :: Status Window";

    private final StringBuilder status = new StringBuilder();

    /*
     * ------------ FXML Components -----------------
     */

    @FXML
    private TextArea statusWindow;


    /*
     * ------------ Statics -----------------
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
            logger.error("Cannot load Controller.", e);
        }

        return controller;
    }


    /*
     * ------------ Code -----------------
     */

    /**
     * Instantiate and set the title.
     */
    public StatusController()
    {
        super(FORMAT_TITLE); // TODO Title may not be needed
        logger.debug("Instantiated.");
    }


    /**
     * Updates this stage with event handlers
     * 
     * @param responseModel
     */
    public void updateStage(final StringProperty status)
    {
        //this.status = status;
    }


    /**
     * {@inheritDoc}
     * 
     * @see com.github.technosf.posterer.controllers.Controller#initialize()
     */
    @Override
    public void initialize()
    {
        logger.debug("Initialize.");
    }


    /*
     * ------------ Setters -----------------
     */

    /**
     * Replace the Status window text
     * 
     * @param message
     *            the message to set the status window to
     */
    void setStatus(String message)
    {
        status.append(message);
        statusWindow.setText(message);
    }


    /**
     * Replace the Status window text
     * 
     * @param message
     *            the message to set the status window to
     */
    void setStatus(String format, Object... args)
    {
        setStatus(String.format(format, args));
    }


    /**
     * Append a message to the Status window.
     * 
     * @param message
     *            the message to append to the status window
     */
    void appendStatus(String message)
    {
        if (StringUtils.isNotBlank(message))
        {
            status.append("\n").append(message);
            statusWindow.setText(status.toString());
        }
    }


    /**
     * Append a message to the Status window.
     * 
     * @param message
     *            the message to append to the status window
     */
    void appendStatus(String format, Object... args)
    {
        appendStatus(String.format(format, args));
    }

}
