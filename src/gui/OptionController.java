package gui;

import backend.BackEndManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import util.ServerType;
import java.util.regex.Pattern;

public class OptionController {

    private Main main;
    private BackEndManager manager;
    private ServerType serverType = ServerType.SERVER;

    public void setUp(Main main) {
        this.main = main;
        this.manager = BackEndManager.getSingletonInstance();

        portField.setTextFormatter(new TextFormatter<>(change -> {
            if (!change.isContentChange()) {
                return change;
            }
            String text = change.getControlNewText();
            if(text.equals("")) {
                return change;
            }
            if(!Pattern.matches("[0-9]+", text )) {
                return null;
            }
            return change;
        }));

        ipAddressField.setTextFormatter(new TextFormatter<>(change -> {
            if (!change.isContentChange()) {
                return change;
            }
            String text = change.getControlNewText();
            if(text.equals("")) {
                return change;
            }
            if(!Pattern.matches("[0-9.]+", text )) {
                return null;
            }
            return change;
        }));
    }

    @FXML private VBox connectionInfoBox;

    @FXML private Label errorLabel;

    @FXML private TextField portField;

    @FXML private TextField ipAddressField;


    @FXML public void continueToMain() {
        if(serverType == ServerType.SERVER) {
            String setUpResults = manager.startUp(serverType);
            //If the results are null, then set up was successful and can proceed to the next window.
            if(setUpResults == null) {
                main.goToMainPage();
            } else {
                setErrorLabel(setUpResults);
            }
        }
        //If the node type is not a server, then check to make sure the user entered information for the
        //IP and Port fields.
        else if(portField.getText().equals("") || ipAddressField.getText().equals("")) {
            setErrorLabel("Please enter an IP Address and Port");
        }
        else {
            String setUpResults = manager.startUp(serverType, ipAddressField.getText(), Integer.parseInt(portField.getText()));
            //If the results are null, then set up was successful and can proceed to the next window.
            if(setUpResults == null) {
                main.goToMainPage();
            } else {
                setErrorLabel(setUpResults);
            }
        }

    }

    @FXML public void setServerToggleOption() {
        hideConnectionInfo();
        serverType = ServerType.SERVER;
    }

    @FXML public void setClientToggleOption() {
        displayConnectionInfo();
        serverType = ServerType.CLIENT_SERVER;
    }

    @FXML public void setTesterToggleOption() {
        displayConnectionInfo();
        serverType = ServerType.CLIENT_TESTER;
    }

    private void displayConnectionInfo() {
        connectionInfoBox.setVisible(true);
    }

    private void hideConnectionInfo() {
        connectionInfoBox.setVisible(false);
    }

    private void setErrorLabel(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}
