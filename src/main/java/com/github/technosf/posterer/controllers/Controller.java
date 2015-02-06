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

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX {@code Controller} for JavaFX version 2.2
 * <p>
 * Defines our JavaFX {@code Controller} common behavior for our pattern.
 * 
 * @author technosf
 * @since 0.0.1
 * @version 0.0.1
 * @see <a
 *      href="https://sites.google.com/site/paratumignempetere/software-development/javafx/javafx-hello-world">
 *      Paratum Ignem Petere</a>
 */
public interface Controller
{
    /**
     * Gets the root Parent containing the assets
     * 
     * @return the root Parent for the Scene
     */
    public Parent getRoot();


    /**
     * Returns the desired title for the Controller Stage
     * 
     * @return the title
     */
    public String getTitle();


    /**
     * Returns the desired width for this Controller Scene
     * 
     * @return the width
     */
    public double getWidth();


    /**
     * Returns the desired height of the Controller Scene
     * 
     * @return height
     */
    public double getHeight();


    /**
     * Returns the Stage for this Controller
     * 
     * @return the stage
     */
    public Stage getStage();


    /**
     * Set the Stage, placing our Controllers assets into it.
     * 
     * @param stage
     *            where our assets go
     * @return the scene we create on the stage
     */
    public Scene setStage(Stage stage);


    /**
     * Set the stage, placing our assets into it and apply a CSS
     * 
     * @param stage
     *            the Stage where our assets go
     * @param css
     *            the location of the CSS
     * @return the Scene created on the stage
     */
    public Scene setStage(Stage stage, String css);


    /**
     * Called after the Controller has loaded, initialization code and event
     * wiring should here in the implementing
     * Controller.
     */
    public void initialize();


    /**
     * Clean up method when the application {@code CloseRequest} has been
     * called.
     * 
     * @param stage
     *            the stage being closed
     */
    public void onStageClose(Stage stage);

}
