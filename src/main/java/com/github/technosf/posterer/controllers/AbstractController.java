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
package com.github.technosf.posterer.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

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
 *      href="https://sites.google.com/site/paratumignempetere/software-development/javafx/javafx-hello-world">
 *      Paratum Ignem Petere</a>
 */
public abstract class AbstractController implements Initializable, Controller
{
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
    private Stage stage;


    /**
     * Create a (@code Controller}
     */
    protected AbstractController()
    {
        this.title = null;
        this.width = null;
        this.height = null;
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
        this.width = null;
        this.height = null;
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
     * @see paratum.ignem.petere.Controller#getRoot()
     */
    @Override
    public abstract Parent getRoot();


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


    public final void setTitle(String title)
    {
        this.title = title;
        stage.setTitle(title);
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
        return scene;
    }


    /**
     * Creates a Scene based on the instantiation parameters.
     * 
     * @return the scene
     */
    private final Scene createScene()
    {
        Scene scene;
        if (width == null || height == null)
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
        FXMLLoader loader = new FXMLLoader();

        try (InputStream fxmlStream = // Read the FXML
                Controller.class.getResourceAsStream(fxml))
        {
            loader.load(fxmlStream); // Load the (FXML) View

            Controller controller = // Load the Controller specified in the FXML
                    (Controller) loader.getController();

            controller.initialize(); // Do post load initialization

            return controller;
        }
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

            return controller;
        }
    }
}
