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
package com.github.technosf.posterer;

import static com.google.inject.Guice.createInjector;
import javafx.application.Application;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.technosf.posterer.controllers.impl.RequestController;
import com.github.technosf.posterer.transports.commons.CommonsModule;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * Hello world!
 * 
 * @author technosf
 * @since 0.0.1
 * @version 0.0.1
 */
public class App extends Application
{

    private static final Logger logger = LoggerFactory
            .getLogger(App.class);

    private static final String PROPS_PREFIX = "main.";
    private static final Module MODULE = new CommonsModule(PROPS_PREFIX);

    /**
     * A common Guice Injector
     */
    public static Injector INJECTOR;

    RequestController requestController;


    public static void main(String[] args)
    {
        INJECTOR = createInjector(MODULE); // Link to commons with a prefix
        launch(args);
    }


    /**
     * {@inheritDoc}
     * 
     * @see javafx.application.Application#start(javafx.stage.Stage)
     */
    @Override
    public void start(Stage stage) throws Exception
    {
        logger.debug("Starting.");
        RequestController.loadStage(stage);
        stage.show();
        logger.debug("Started.");
    }

}
