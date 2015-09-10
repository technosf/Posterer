/*
 * Copyright 2014 technosf [https://github.com/technosf]
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.github.technosf.posterer.controllers.impl.base;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.technosf.posterer.controllers.Controller;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * JavaFX {@code AbstractController} for JavaFX version 2.2
 * <p>
 * Abstract class implementing common and immutable functionality for our JavaFX
 * {@code Controller} for our pattern.
 * 
 * @author technosf
 * @since 0.0.1
 * @version 0.0.1
 * @see <a
 *      href=
 *      "https://sites.google.com/site/paratumignempetere/software-development/javafx/javafx-hello-world">
 *      Paratum Ignem Petere</a>
 */
public abstract class AbstractController
        implements Initializable, Controller
{
    private static final Logger LOG = LoggerFactory
            .getLogger(AbstractController.class);

    /* --------------- Common FXML Components ------------------- */

    /**
     * The root component for all controllers extending
     * {@code AbstractController} must have a <i>fx:id</i> of <i>root</i>
     * <p>
     * Although the matching method {@code getRoot} is defined in controller, I
     * decided to promote <i>root</i> from being an implementation class
     * requirement to being implemented in the abstract class. This was to
     * simplify requirements on implementing classes ever-so-slightly given the
     * complexity a heavily populated FXML can bring.
     */
    @FXML
    private Parent root;

    /* --------------- Common Components ------------------- */

    /**
     * Stage title
     */
    private String title;

    /**
     * Scene width
     */
    private final Double width;

    /**
     * Scene height
     */
    private final Double height;

    /**
     * The Stage the controller is on
     */
    //@Nullable
    private Stage stage;

    /**
     * The last set CSS
     */
    //@Nullable
    private String css;


    /**
     * Create a (@code Controller}
     */
    protected AbstractController()
    {
        this.title = "";
        this.width = Double.NaN;
        this.height = Double.NaN;
    }


    /**
     * Create a (@code Controller} with a {@code Stage} Title
     * 
     * @param title
     *            the title for the Stage
     */
    protected AbstractController(String title)
    {
        this.title = title;
        this.width = Double.NaN;
        this.height = Double.NaN;
    }


    /**
     * Create a (@code Controller} with a {@code Stage} Title and dimensions for
     * the {@code Scene}
     * 
     * @param title
     *            the title for the {@code Stage}
     * @param width
     *            the width of the {@code Scene}
     * @param height
     *            the height of the {@code Scene}
     */
    protected AbstractController(String title, double width, double height)
    {
        this.title = title;
        this.width = width;
        this.height = height;
    }


    /**
     * {@inheritDoc}
     *
     * @see com.github.technosf.posterer.controllers.Controller#getRoot()
     */
    //@Nullable
    public Parent getRoot()
    {
        return root;
    }


    /**
     * {@inheritDoc}
     * 
     * @see paratum.ignem.petere.Controller#getTitle()
     */
    @Override
    public final String getTitle()
    {
        return title;
    }


    /**
     * Set the stage title
     * 
     * @param title
     *            the title
     */
    public final void setTitle(String title)
    {
        this.title = title;
        if (stage != null)
        {
            stage.setTitle(title);
        }
    }


    /**
     * {@inheritDoc}
     * 
     * @see paratum.ignem.petere.Controller#getWidth()
     */
    @Override
    public final double getWidth()
    {
        return width;
    }


    /**
     * {@inheritDoc}
     * 
     * @see paratum.ignem.petere.Controller#getHeight()
     */
    @Override
    public final double getHeight()
    {
        return height;
    }


    /**
     * {@inheritDoc}
     * 
     * @see javafx.fxml.Initializable#initialize(java.net.URL,
     *      java.util.ResourceBundle)
     */
    @Override
    public void initialize(URL arg0, ResourceBundle arg1)
    {
        // NOOP - This Initializable interface method does nothing by default
        // Override this if you need.
    }


    /**
     * {@inheritDoc}
     * 
     * @see paratum.ignem.petere.Controller#getStage()
     */
    //@Nullable
    @Override
    public final Stage getStage()
    {
        return stage;
    }


    /**
     * {@inheritDoc}
     * 
     * @see paratum.ignem.petere.Controller#setStage(javafx.stage.Stage,
     *      java.lang.String)
     */
    @Override
    public final Scene setStage(Stage stage, String css)
    {
        Scene scene = setStage(stage);
        scene.getStylesheets().add(css);
        return scene;
    }


    /**
     * {@inheritDoc}
     * 
     * @see paratum.ignem.petere.Controller#setStage(Stage)
     */
    @Override
    public final Scene setStage(Stage stage)
    {
        this.stage = stage;
        Scene scene = createScene();
        stage.setScene(scene);
        stage.setTitle(getTitle());

        stage.setOnCloseRequest(new EventHandler<WindowEvent>()
        // Inform the implementing class that stage is closing.
        {
            @Override
            public void handle(WindowEvent event)
            {
                onStageClose((Stage) event.getTarget());
            }
        });

        return scene;
    }


    /**
     * {@inheritDoc}
     *
     * @see com.github.technosf.posterer.controllers.Controller#setStyle(java.lang.String)
     */
    public final void setStyle(final String css)
    {
        if (root != null && css != null
                && !css.isEmpty())
        {
            root.getStylesheets().clear();
            root.getStylesheets().add(css);
        }
    }


    /* ---------------- Helpers ------------------ */

    /**
     * Returns the last good CSS
     * 
     * @return
     */
    //@Nullable
    protected final String getStyle()
    {
        return css;
    }


    /**
     * Creates a Scene based on the instantiation parameters.
     * 
     * @return the scene
     */
    private final Scene createScene()
    {
        Scene scene;
        if (width == null || height == null || width.isNaN() || height.isNaN())
        {
            scene = new Scene(getRoot());
        }
        else
        {
            scene = new Scene(getRoot(), width, height);
        }
        return scene;
    }


    /**
     * Loads the Controller from the FXML and returns it.
     * 
     * @param fxml
     *            defines the layout and controller
     * @return the Controller
     * @throws IOException
     *             you may care to catch this should you get your FXML location
     *             wrong.
     */
    public final static Controller loadController(String fxml)
            throws IOException
    {
        LOG.debug("loadController :: FXML: [{}]", fxml);

        FXMLLoader loader = new FXMLLoader();

        try (InputStream fxmlStream = // Read the FXML
                Controller.class.getResourceAsStream(fxml))
        {
            loader.load(fxmlStream); // Load the (FXML) View

            Controller controller = // Load the Controller specified in the FXML
                    (Controller) loader.getController();

            controller.initialize(); // Do post load initialization

            LOG.debug("loadController :: FXML - Returning controller: [{}]",
                    controller);

            return controller;
        }
    }


    /**
     * Loads the Controller from the FXML, sets the stage, returning the
     * controller.
     * 
     * @param stage
     *            the stage to apply to the controller
     * @param fxml
     *            defines the layout and controller
     * @return the Controller
     * @throws IOException
     *             you may care to catch this should you get your FXML location
     *             wrong.
     */
    public final static Controller loadController(Stage stage, String fxml)
            throws IOException
    {
        LOG.debug("loadController :: Stage: [{}], FXML: [{}]", stage, fxml);

        Controller controller =
                loadController(fxml);
        controller.setStage(stage);

        LOG.debug(
                "loadController :: Stage, FXML - Returning controller: [{}]",
                controller);

        return controller;
    }


    /**
     * Loads the Controller from the FXML and returns it.
     * 
     * @param fxml
     *            defines the layout and controller
     * @return the Controller
     * @throws IOException
     *             you may care to catch this should you get your FXML location
     *             wrong.
     */
    public final static Controller loadController(Parent root, String fxml)
            throws IOException
    {
        LOG.debug("loadController :: Root: [{}], FXML: [{}]", root, fxml);

        FXMLLoader loader = new FXMLLoader();

        loader.setRoot(root);

        try (InputStream fxmlStream = // Read the FXML
                Controller.class.getResourceAsStream(fxml))
        {

            loader.load(fxmlStream); // Load the (FXML) View

            Controller controller = // Load the Controller specified in the FXML
                    (Controller) loader.getController();

            controller.initialize(); // Do post load initialization

            loader.setController(controller);

            LOG.debug(
                    "loadController :: Root, FXML - Returning controller: [{}]",
                    controller);

            return controller;
        }
    }


    /**
     * {@inheritDoc}
     *
     * @see com.github.technosf.posterer.controllers.Controller#onStageClose(javafx.stage.Stage)
     */
    public void onStageClose(Stage stage)
    {
        LOG.debug("{} :: onStageClose called", this
                .getClass().getSimpleName());
        close();
    }


    /**
     * Close the stage.
     * <p>
     * Override this method if other functionality is needed in the implementing
     * class.
     */
    protected void close()
    {
        if (stage != null)
        {
            stage.close();
            LOG.debug("{} :: Stage closed", this.getClass().getSimpleName());
        }
    }
}
