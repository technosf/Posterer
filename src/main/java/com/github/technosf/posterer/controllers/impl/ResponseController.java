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
package com.github.technosf.posterer.controllers.impl;

import java.io.IOException;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.technosf.posterer.controllers.AbstractController;
import com.github.technosf.posterer.controllers.Controller;
import com.github.technosf.posterer.models.ResponseModel;

/**
 * Controller backing {@code Response.fxml}.
 * <p>
 * Implements the management and communication of the HTTP request and response
 * passed to it as a {@code Task}, along with the event management of that task.
 * 
 * @author technosf
 * @since 0.0.1
 * @version 0.0.1
 */
public class ResponseController
        extends AbstractController
        implements Controller
{

    /**
     * The FXML definition of the View
     */
    public final static String FXML = "/fxml/Response.fxml";

    private static final Logger logger = LoggerFactory
            .getLogger(ResponseController.class);

    /**
     * The window title formatter
     */
    private final static String FORMAT_TITLE =
            "Posterer :: Response #%1$d [%2$s %3$s]";

    /**
     * The task running the HTTP request/response.
     */
    private Task<?> responseModelTask;

    /**
     * Is the task cancellable?
     */
    private boolean cancellable = true;

    /*
     * ------------ FXML Components -----------------
     */

    @FXML
    private TextArea headers, response;

    @FXML
    private TextField status;

    @FXML
    private ProgressIndicator progress;

    @FXML
    private Button button;


    /*
     * ------------ Statics -----------------
     */

    public static Stage loadStage(ResponseModel response)
    {
        Stage stage = new Stage();
        ResponseController controller;
        try
        {
            controller = (ResponseController) ResponseController
                    .loadController(stage, FXML);
            controller.updateStage(response);
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
    public ResponseController()
    {
        super(FORMAT_TITLE); // TODO Title may not be needed
        logger.debug("Instantiated.");
    }


    /**
     * Updates this stage with event handlers
     * 
     * @param responseModel
     */
    public void updateStage(final ResponseModel responseModel)
    {
        /*
         * Set the title to provide info on the HTTP request
         */
        setTitle(String.format(FORMAT_TITLE, responseModel.getReferenceId(),
                responseModel.getMethod(),
                responseModel.getUri()));

        /*
         * Ensure that the incoming {@code ResponseModel} is also a {@code Task}
         */
        if (!Task.class.isInstance(responseModel))
            return;

        /*
         *  The ResponseModel is also a Task, so proceed
         */
        responseModelTask = (Task<?>) responseModel;

        /*
         * Set the {@code OnSucceeded} Handler
         */
        responseModelTask
                .setOnSucceeded(new EventHandler<WorkerStateEvent>()
                {
                    @Override
                    public void handle(WorkerStateEvent arg0)
                    {
                        requestSucceeded(responseModel);

                    }
                });

        /*
         * Set the {@code OnFailed} Handler
         */
        responseModelTask.setOnFailed(new EventHandler<WorkerStateEvent>()
        {
            @Override
            public void handle(WorkerStateEvent arg0)
            {
                requestFailed(responseModelTask.getException().getMessage());

            }
        });

        /*
         * Set the {@code OnCancelled} Handler
         */
        responseModelTask
                .setOnCancelled(new EventHandler<WorkerStateEvent>()
                {
                    @Override
                    public void handle(WorkerStateEvent arg0)
                    {
                        cancelOrClose();

                    }
                });

        Platform.runLater(responseModelTask);

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


    /* ----------------  Event Handlers  ---------------------- */

    /**
     * Cancel (the task) or close the window
     */
    public void cancelOrClose()
    {
        if (cancellable)
        // Cancel
        {
            cancellable = false;
            status.setText("Cancelling...");
            responseModelTask.cancel();
            progress.setVisible(false);
            button.setText("Close");
            status.setText("Cancelled.");
        }
        else
        // Close
        {
            getStage().close();
        }
    }


    /**
     * Handler for erring task events
     * <p>
     * Update the window status and provide feedback
     */
    private void requestFailed(String error)
    {
        status.setText("Fail: " + error);
        progress.setVisible(false);
        cancellable = false;
        button.setText("Close");
    }


    /**
     * Handler for succeed task events
     * <p>
     * Update the window status and provide output
     */
    private void requestSucceeded(ResponseModel responseModel)
    {
        headers.setText(responseModel.getHeaders());
        response.setText(responseModel.getBody());
        progress.setVisible(false);
        cancellable = false;
        button.setText("Close");
    }

}
