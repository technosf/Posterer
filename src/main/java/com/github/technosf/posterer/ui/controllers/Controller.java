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
package com.github.technosf.posterer.ui.controllers;

import org.eclipse.jdt.annotation.Nullable;

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
 *      href=
 *      "https://sites.google.com/site/paratumignempetere/software-development/javafx/javafx-hello-world">
 *      Paratum Ignem Petere</a>
 */
public interface Controller
{
    /**
     * Gets the root {@code Parent} containing the assets
     * 
     * @return the root {@code Parent} for the {@code Scene}
     */
    public Parent getRoot();


    /**
     * Returns the desired title for the {@code Controller} {@code Stage}
     * 
     * @return the title
     */
    public String getTitle();


    /**
     * Returns the desired width for this {@code Controller} {@code Scene}
     * 
     * @return the width
     */
    public double getWidth();


    /**
     * Returns the desired height of the {@code Controller} {@code Scene}
     * 
     * @return height
     */
    public double getHeight();


    /**
     * Returns the {@code Stage} for this {@code Controller}
     * 
     * @return the {@code Stage}
     */
    @Nullable
    public Stage getStage();


    /**
     * Set the {@code Stage}, placing the {@code Controller}'s assets into it.
     * 
     * @param stage
     *            where our assets go
     * @return the {@code Scene} we create on the {@code Stage}
     */
    public Scene setStage(Stage stage);


    /**
     * Set the {@code Scene} style to the CSS
     * 
     * @param css
     *            the location of the CSS
     */
    public void setStyle(String css);


    /**
     * Set the {@code Stage}, placing our assets into it and apply a CSS
     * 
     * @param stage
     *            the {@code Stage} where our assets go
     * @param css
     *            the location of the CSS
     * @return the {@code Scene} created on the stage
     */
    public Scene setStage(Stage stage, String css);


    /**
     * Called after the {@code Controller} has loaded, initialization code and
     * event
     * wiring should here in the implementing {@code Controller}.
     */
    public void initialize();


    /**
     * Clean up method when the application {@code CloseRequest} has been
     * called (on its stage).
     * 
     * @param stage
     *            the {@code Stage} being closed
     */
    public void onStageClose(Stage stage);

}
