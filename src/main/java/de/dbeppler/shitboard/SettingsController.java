package de.dbeppler.shitboard;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static de.dbeppler.shitboard.SettingsController.Property.*;

/**
 * @author Dominik Beppler
 * @since 05.02.2016
 */
public class SettingsController {
	@FXML
	public TextField txtButtonCount;
	@FXML
	public Slider sldButtonWidth;
	@FXML
	public TextField txtButtonWidth;
	@FXML
	public Slider sldButtonHeight;
	@FXML
	public TextField txtButtonHeight;
	@FXML
	public Label lblLoggedInAs;
	@FXML
	public Button btnLogout;
	@FXML
	private Slider sldButtonCount;
	private Twitter twitter;
	private Path userCfg;
	private Controller mainController;

	public SettingsController() {
	}

	public void initialize(Twitter twitter, Path userCfg, Controller mainController) throws TwitterException {
		this.twitter = twitter;
		this.userCfg = userCfg;
		this.mainController = mainController;
		lblLoggedInAs.setText("Currently logged in as " + twitter.getScreenName());
		initListeners();
	}

	private void initListeners() {
		mapSliderToTextField(sldButtonCount, txtButtonCount, COUNT);
		mapTextFieldToSlider(sldButtonCount, txtButtonCount, COUNT);
		mapSliderToTextField(sldButtonWidth, txtButtonWidth, WIDTH);
		mapTextFieldToSlider(sldButtonWidth, txtButtonWidth, WIDTH);
		mapSliderToTextField(sldButtonHeight, txtButtonHeight, HEIGHT);
		mapTextFieldToSlider(sldButtonHeight, txtButtonHeight, HEIGHT);
	}

	private void mapSliderToTextField(Slider slider, TextField textField, Property property) {
		slider.valueProperty().addListener((observable, oldValue, newValue) -> {
			int i = newValue.intValue();
			textField.setText(i + "");
			setBoardButtonProperty(i, property);
		});
	}

	private void mapTextFieldToSlider(Slider slider, TextField textField, Property property) {
		textField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue.matches("\\d*")) {
				if (!newValue.equals("")) {
					int i = Integer.parseInt(newValue);
					slider.adjustValue(i);
					setBoardButtonProperty(i, property);
				}
			} else
				textField.setText(oldValue);
		});
	}

	private void setBoardButtonProperty(int value, Property property) {
		switch (property) {
			case COUNT:
				mainController.setBoardButtonCount(value);
				break;
			case WIDTH:
				mainController.setBoardButtonWidth(value);
				break;
			case HEIGHT:
				mainController.setBoardButtonHeight(value);
				break;
		}
	}

	@FXML
	public void logoutPressed() {
		try {
			Files.delete(userCfg);
			lblLoggedInAs.setTextFill(Color.RED);
			lblLoggedInAs.setText("Restart application to login again");
			twitter.setOAuthAccessToken(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	enum Property {
		COUNT, WIDTH, HEIGHT
	}
}
