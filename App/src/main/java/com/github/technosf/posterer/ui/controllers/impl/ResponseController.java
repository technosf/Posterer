/*
 * Copyright 2018 technosf [https://github.com/technosf]
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
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.technosf.posterer.models.ResponseModel;
import com.github.technosf.posterer.models.StatusModel;
import com.github.technosf.posterer.ui.controllers.Controller;
import com.github.technosf.posterer.ui.controllers.impl.base.AbstractController;
import com.github.technosf.posterer.utils.PrettyPrinters;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Controller backing {@code Response.fxml}.
 * <p>
 * Orchestrates the management and communication of the HTTP request and
 * response passed to it as a {@code Task}, along with the event management of
 * that task.
 * <p>
 * The {@code Task} that performs the call is fired at the end of the stage
 * update
 * 
 * @author technosf
 * @since 0.0.1
 * @version 0.0.2
 */
public class ResponseController
        extends AbstractController
        implements Controller
{

    /**
     * The FXML definition of the View
     */
    public final static String FXML = "/fxml/Response.fxml";

    /* ---- Constants ----- */

    /**
     * Logger
     */
    private final static Logger LOG = LoggerFactory
            .getLogger(ResponseController.class);

    /**
     * The window title formatter
     */
    private final static String FORMAT_TITLE =
            "Posterer :: Response #%1$d [%2$s %3$s]";

    /*
     * ------------ State -----------------
     */

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
    private TextArea headers, request, response, statusWindow;

    @FXML
    private SplitPane requestResponse;

    @FXML
    private AnchorPane requestAnchor, responseAnchor;

    @FXML
    private ProgressIndicator progress;

    @FXML
    private Button button;

    /* Context Menus */
    protected RadioButton requestWrap = new RadioButton("Wrap");
    protected RadioButton responseWrap = new RadioButton("Wrap");
    protected CustomMenuItem requestWrapMI = new CustomMenuItem(requestWrap);
    protected CustomMenuItem responseWrapMI = new CustomMenuItem(responseWrap);
    protected MenuItem requestFormat = new MenuItem("Format");
    protected MenuItem responseFormat = new MenuItem("Format");
    protected ContextMenu requestCM =
            new ContextMenu(requestWrapMI, requestFormat);
    protected ContextMenu responseCM =
            new ContextMenu(responseWrapMI, responseFormat);

    protected StatusController statusController;
    protected StatusModel status;


    /*
     * ------------ Statics -----------------
     */

    /**
     * Configure and load the JavaFX stage
     * 
     * @param response
     *            the response to load
     * @return the stage
     */
    public static Controller loadStage(final @NonNull ResponseModel response)
    {
        LOG.debug("Loading controler onto stage");
        Stage stage = new Stage();
        ResponseController controller = null;
        try
        {
            controller = (ResponseController) ResponseController
                    .loadController(stage, FXML);
            controller.updateStage(response);
        }
        catch (IOException e)
        {
            LOG.error("Cannot load Controller.", e);
        }

        return controller;
    }


    /*
     * ------------ Code -----------------
     */

    /**
     * Starts the task
     * <p>
     * Broken out to emphasize how the asynchronous call is made
     */
    private static void startTask(Task<?> responseModelTask)
    {
        //Run the task in a new thread
        new Thread(responseModelTask).start();
    }


    /**
     * Instantiate and set the title.
     */
    public ResponseController()
    {
        super(FORMAT_TITLE); // TODO Title may not be needed
        LOG.debug("Instantiated.");
    }


    /**
     * Updates this stage with event handlers and starts the ResponseModelTask
     * 
     * @param responseModel
     */
    public void updateStage(final @NonNull ResponseModel responseModel)
    {
        LOG.debug("Updating stage with reponse");

        /*
         * Set the title to provide info on the HTTP request
         */
        setTitle(String.format(FORMAT_TITLE, responseModel.getReferenceId(),
                responseModel.getRequest().getMethod(),
                responseModel.getRequest().getEndpoint()));

        /*
         * Ensure that the incoming {@code ResponseModel} is also a {@code Task}
         */
        if (!Task.class.isInstance(responseModel))
            return;

        String requestPayload = responseModel.getRequest().getPayload();
        if (requestPayload.isEmpty())
        /*
         * Hide request payload pane
         */
        {
            requestAnchor.setDisable(true);
            requestResponse.getItems().remove(requestAnchor);
        }
        else
        /*
         * Add request payload to split
         */
        {
            request.setText(requestPayload);
        }

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

        startTask(responseModelTask);

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

        statusController =
                StatusController.loadController(statusWindow.textProperty());
        statusController.setStyle(getStyle());
        status = statusController.getStatusModel();

        requestFormat.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent e)
            {
                request.setText(PrettyPrinters.xml(request.getText(), true));
            }
        });

        requestWrap.setSelected(request.wrapTextProperty().get());
        request.wrapTextProperty().bind(requestWrap.selectedProperty());

        responseFormat.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent e)
            {
                response.setText(PrettyPrinters.xml(response.getText(), true));
            }
        });

        responseWrap.setSelected(response.wrapTextProperty().get());
        response.wrapTextProperty().bind(responseWrap.selectedProperty());

    }


    /* ----------------  Event Handlers  ---------------------- */

    /**
     * Show custom payload context menu on tripple click
     */
    public void onRequestSelected(final MouseEvent mouseEvent)
    {
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY)
                && mouseEvent.getClickCount() == 3)
        {
            requestCM.show(request, mouseEvent.getScreenX(),
                    mouseEvent.getScreenY());
        }
    }


    /**
     * Show custom payload context menu on tripple click
     */
    public void onResponseSelected(final MouseEvent mouseEvent)
    {
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY)
                && mouseEvent.getClickCount() == 3)
        {
            responseCM.show(response, mouseEvent.getScreenX(),
                    mouseEvent.getScreenY());
        }
    }


    /**
     * Cancel (the task) or close the window
     */
    public void cancelOrClose()
    {
        if (cancellable)
        /*
         *  Cancel
         */
        {
            cancellable = false;
            status.append("Cancelling...");
            responseModelTask.cancel();
            progress.setVisible(false);
            button.setText("Close");
            status.append("Cancelled.");
        }
        else
        /*
         *  Close
         */
        {
            Stage stage;
            if ((stage = getStage()) != null)
            {
                stage.close();
            }
        }
    }


    /**
     * Handler for erring task events
     * <p>
     * Update the window status and provide feedback
     */
    private void requestFailed(final @Nullable String error)
    {
        status.append("Fail: " + Objects.toString(error, "Error not provided"));
        progress.setVisible(false);
        cancellable = false;
        button.setText("Close");
    }


    /**
     * Handler for succeed task events
     * <p>
     * Update the window status and provide output
     */
    private void requestSucceeded(final @NonNull ResponseModel responseModel)
    {
        try
        {
            if (responseModel.isComplete())
            {
                status.append("Completed in "
                        + responseModel.getElaspedTimeMilli()
                        + "ms :\n\t"
                        + responseModel.getStatus().replaceAll("\n", "\n\t"));
                headers.setText(responseModel.getHeaders());
                response.setText(responseModel.getBody());
                progress.setVisible(false);
                cancellable = false;
                button.setText("Close");
            }
        }
        catch (InterruptedException | ExecutionException e)
        {
            status.append("Could not complete request: " + e.getMessage());
        }
    }


    /**
     * Open the stand alone status window on Status double click
     */
    public void onStatusSelected(final MouseEvent mouseEvent)
    {
        Stage stage;
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY)
                && mouseEvent.getClickCount() == 2
                && (statusController != null)
                && (stage = statusController.getStage()) != null)
        {
            stage.show();
        }
    }

}
