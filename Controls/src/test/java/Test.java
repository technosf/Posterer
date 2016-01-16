
import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Test extends Application
{

    @Override
    public void start(Stage stage) throws Exception
    {
        String app = "Test.fxml";

        URL url = getClass().getResource(app);

        Parent root = FXMLLoader.load(url);

        Scene scene = new Scene(root, 500, 300);

        stage.setTitle("FXML Test");
        stage.setScene(scene);
        stage.show();
    }


    public static void main(String[] args)
    {
        launch(Test.class);
    }
}
