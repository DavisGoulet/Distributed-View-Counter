package gui;

import backend.BackEndManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("OptionPage.fxml"));
        Parent root = (Parent)loader.load();
        primaryStage.setTitle("Distributed View Counter");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.sizeToScene();
        OptionController currentController = loader.getController();
        currentController.setUp(this);

        primaryStage.show();
    }

    void goToMainPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MainPage.fxml"));
            Parent root = (Parent) loader.load();
            primaryStage.getScene().setRoot(root);
            primaryStage.sizeToScene();
            MainController currentController = loader.getController();
            currentController.setUp();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** This overridden function is needed otherwise the socket threads that listen to the port would never finish.
     * @throws Exception from the super classes stop() function. */
    @Override
    public void stop() throws Exception {
        System.exit(0);
        super.stop();
    }
}
