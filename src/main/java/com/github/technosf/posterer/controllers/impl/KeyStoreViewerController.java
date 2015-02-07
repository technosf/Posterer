package com.github.technosf.posterer.controllers.impl;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.technosf.posterer.controllers.AbstractController;
import com.github.technosf.posterer.controllers.Controller;
import com.github.technosf.posterer.models.KeyStoreBean;

public class KeyStoreViewerController extends AbstractController
        implements Controller
{
    /**
     * The FXML definition of the View
     */
    public final static String FXML = "/fxml/KeyStoreViewer.fxml";

    private static final Logger logger = LoggerFactory
            .getLogger(KeyStoreViewerController.class);

    /**
     * The window title formatter
     */
    private final static String FORMAT_TITLE =
            "Posterer :: CertificateStoreViewer";

    /*
     * ------------ FXML Components -----------------
     */

    @FXML
    private TextField certificateFile, certificateCount;


    /*
     * ------------ Statics -----------------
     */

    public static Stage loadStage(KeyStoreBean keyStore)
    {
        Stage stage = new Stage();
        KeyStoreViewerController controller;

        try
        {
            controller = (KeyStoreViewerController) KeyStoreViewerController
                    .loadController(stage, FXML);
            controller.updateStage(keyStore);
        }
        catch (IOException e)
        {
            logger.error("Cannot load Controller.", e);
        }

        return stage;
    }


    /*
     * ------------ Code -----------------
     */

    /**
     * Instantiate and set the title.
     */
    public KeyStoreViewerController()
    {
        super(FORMAT_TITLE); // TODO Title may not be needed
        logger.debug("Instantiated.");
    }


    /**
     * Updates this stage with event handlers
     * 
     * @param responseModel
     */
    public void updateStage(final KeyStoreBean keyStore)
    {
        /*
         * Set the title to provide info on the HTTP request
         */
        //        setTitle(String.format(FORMAT_TITLE, responseModel.getReferenceId(),
        //                responseModel.getMethod(),
        //                responseModel.getUri()));

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

}
