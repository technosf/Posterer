
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

import com.github.technosf.posterer.ui.custom.controls.FileChooserComboBox;
import com.github.technosf.posterer.ui.custom.controls.URLComboBox;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class Test extends Application
        implements Initializable
{

    static String FXML = "Test.fxml";

    @FXML
    private Parent root;

    @FXML
    private FileChooserComboBox filechoosercombobox;

    @FXML
    private Label filechoosercomboboxfeedback;

    @FXML
    private URLComboBox urlcombobox;


    public static void main(String[] args)
    {
        launch(Test.class);
    }


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

        test.initialize();

    }


    private void initialize()
    {
        urlcombobox.setOnAction(event -> {
            urlaction(event);
        });

        filechoosercomboboxfeedback.textProperty()
                .bind(filechoosercombobox.chosenFileNameProperty());

    }


    private void urlaction(ActionEvent event)
    {
        System.out.println("Test: URL Action");
    }


    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        // NOOP - This Initializable interface method does nothing by default
        // Override this if you need.
    }

}
