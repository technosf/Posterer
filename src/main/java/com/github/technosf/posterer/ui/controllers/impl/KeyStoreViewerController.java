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
package com.github.technosf.posterer.ui.controllers.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.technosf.posterer.models.impl.KeyStoreBean;
import com.github.technosf.posterer.ui.controllers.Controller;
import com.github.technosf.posterer.ui.controllers.impl.base.AbstractController;

import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * Controller for the KeyStore window
 * 
 * @author technosf
 * @since 0.0.1
 * @version 0.0.1
 */
public class KeyStoreViewerController
        extends AbstractController
        implements Controller
{
    /**
     * The FXML definition of the View
     */
    public final static String FXML = "/fxml/KeyStoreViewer.fxml";

    /* ---- Constants ----- */

    private static final Logger LOG = LoggerFactory
            .getLogger(KeyStoreViewerController.class);

    /**
     * The window title formatter
     */
    private final static String FORMAT_TITLE =
            "Posterer :: CertificateStoreViewer : [ %1$s ]";

    /*
     * ------------ FXML Components -----------------
     */

    @FXML
    private TextField file, type, size;

    @FXML
    private Accordion accordion;

    /* --------------- Storage ---------------------- */

    //private KeyStoreBean keyStore;


    /*
     * ------------ Statics -----------------
     */

    /**
     * Load the stage and update it with the keystore
     * 
     * @param keyStore
     * @return
     */
    public static Stage loadStage(final @NonNull KeyStoreBean keyStore)
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
            LOG.error("Cannot load Controller.", e);
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
        LOG.debug("Instantiated.");

    }


    /**
     * Updates this stage with event handlers
     * 
     * @param responseModel
     */
    public void updateStage(final @NonNull KeyStoreBean keyStore)
    {
        /*
         * Set the title to provide info on the HTTP request
         */
        setTitle(String.format(FORMAT_TITLE, keyStore.getFileName()));
        //this.keyStore = keyStore;
        file.setText(keyStore.getFile().getAbsolutePath());
        type.setText(keyStore.getType());
        size.setText(Integer.toString(keyStore.getSize()));
        List<TitledPane> panes = new ArrayList<TitledPane>();
        for (String alias : keyStore.getAliases())
        {
            TextArea certificate =
                    new TextArea(keyStore.getCertificate(alias).toString());
            certificate.setEditable(false);
            certificate.setWrapText(true);
            certificate.setFont(Font.font("Monospaced", 12));
            certificate.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            TitledPane tp =
                    new TitledPane(String.format("[%2$s]  %1$s", alias,
                            keyStore
                                    .getCertificate(alias).getType()),
                            certificate);
            panes.add(tp);
        }

        accordion.getPanes().setAll(panes);
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

}
