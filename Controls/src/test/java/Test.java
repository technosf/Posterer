
/*
 * Copyright 2016 technosf [https://github.com/technosf]
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
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

import com.github.technosf.posterer.ui.custom.controls.FileChooserComboBox;
import com.github.technosf.posterer.ui.custom.controls.URLComboBox;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Test app to demonstrate the controls in situ
 * 
 * @author technosf
 * @since 0.0.1
 * @version 0.0.1
 */
public class Test extends Application
        implements Initializable
{

    /**
     * The app's FXML
     */
    static String FXML = "Test.fxml";

    /**
     * The root {@code Test} application
     */
    @FXML
    private Parent root;

    /**
     * The demo {@code FileChooserComboBox}
     */
    @FXML
    private FileChooserComboBox filechoosercombobox;

    /*
     * Uncomment if binding is being done in code rather than in FXML
     */
    // @FXML
    // private Label filechoosercomboboxfeedback;

    @FXML
    private URLComboBox urlcombobox;


    /**
     * Entry point for the app
     * 
     * @param args
     */
    public static void main(String[] args)
    {
        launch(Test.class);
    }


    /**
     * {@inheritDoc}
     *
     * @see javafx.application.Application#start(javafx.stage.Stage)
     */
    @Override
    public void start(Stage stage) throws Exception
    {
        FXMLLoader loader = new FXMLLoader();
        Test test;
        try (InputStream fxmlStream = // Read the FXML
                Test.class.getResourceAsStream(FXML))
        {
            loader.load(fxmlStream); // Load the (FXML) View

            test = // Load the Controller specified in the FXML
                    (Test) loader.getController();
        }

        Scene scene = new Scene(test.root, 500, 300);
        stage.setTitle("FXML Test");
        stage.setScene(scene);
        stage.show();

    }


    /**
     * {@inheritDoc}
     *
     * @see javafx.fxml.Initializable#initialize(java.net.URL,
     *      java.util.ResourceBundle)
     */
    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        // NOOP - This Initializable interface method does nothing by default
        // Override this if you need.
    }

}
