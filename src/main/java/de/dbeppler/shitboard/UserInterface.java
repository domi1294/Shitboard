package de.dbeppler.shitboard;/**
 * @author Dominik Beppler
 * @since 05.02.2016
 */

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import java.io.*;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class UserInterface extends Application {

	private Twitter twitter;
	private Path workingDirectory;
	private Path userCfg;

	@Override
	public void start(Stage stage) throws Exception {
		stage.getIcons().add(new Image(URLClassLoader.getSystemResourceAsStream("icons/affun16px.png")));
		stage.getIcons().add(new Image(URLClassLoader.getSystemResourceAsStream("icons/affun32px.png")));
		stage.getIcons().add(new Image(URLClassLoader.getSystemResourceAsStream("icons/affun256px.png")));
		stage.setTitle("Early Alpha - Twitter Shitposting Board");
		twitter = new TwitterFactory().getInstance();
		if (System.getProperty("os.name").toUpperCase().contains("WIN")) {
			workingDirectory = Paths.get(System.getenv("AppData"), "TwitterShitpostingBoard");
		} else {
			//TODO do something more useful with this
			workingDirectory = Paths.get(System.getProperty("user.home"));
		}
		userCfg = Paths.get(workingDirectory.toString(), "cfg", "user.cfg");
		if (Files.exists(userCfg)) {
			try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(userCfg.toFile()))) {
				Object o = ois.readObject();
				if (o instanceof AccessToken) {
					twitter.setOAuthAccessToken((AccessToken) o);
					initStages(stage);
				} else
					authenticate(stage);
			} catch (ClassNotFoundException | IOException e) {
				authenticate(stage);
			}
		} else {
			authenticate(stage);
		}
	}

	private void authenticate(Stage stage) {
		Parent login = null;
		try {
			login = FXMLLoader.load(URLClassLoader.getSystemResource("login.fxml"));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		WebEngine webEngine = ((WebView) login.lookup(".web-view")).getEngine();

		stage.setScene(new Scene(login));
		RequestToken requestToken;
		try {
			requestToken = twitter.getOAuthRequestToken();
		} catch (TwitterException e) {
			e.printStackTrace();
			System.exit(2);
			return;//never used, but compiler error otherwise
		}
		webEngine.load(requestToken.getAuthenticationURL());
		stage.show();
		webEngine.locationProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue.startsWith("http://affun.de/")) {
				Matcher m = Pattern.compile("(\\w|\\d){32}").matcher(newValue);
				if (m.find()) {
					AccessToken accessToken;
					try {
						accessToken = twitter.getOAuthAccessToken(requestToken, m.group());
						Files.createDirectories(userCfg.getParent());
						Files.createFile(userCfg);
						try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(userCfg.toFile()))) {
							oos.writeObject(accessToken);
							initStages(stage);
						} catch (IOException e) {
							e.printStackTrace();
						}
					} catch (IOException | TwitterException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	private void initStages(Stage stage) throws IOException, TwitterException {
		Stage settingsStage = new Stage();
		FXMLLoader settingsLoader = new FXMLLoader(URLClassLoader.getSystemResource("settings.fxml"));
		Parent settingsRoot;
		SettingsController settingsController;

		FXMLLoader fxmlLoader = new FXMLLoader(URLClassLoader.getSystemResource("sample.fxml"));
		fxmlLoader.setControllerFactory(p -> Controller.getInstance());
		Parent root = fxmlLoader.load();
		Controller controller = fxmlLoader.<Controller>getController();
		controller.initialize(twitter, settingsStage);
		stage.setScene(new Scene(root));

		settingsStage.getIcons().add(new Image(URLClassLoader.getSystemResourceAsStream("icons/affun16px.png")));
		settingsStage.getIcons().add(new Image(URLClassLoader.getSystemResourceAsStream("icons/affun32px.png")));
		settingsStage.getIcons().add(new Image(URLClassLoader.getSystemResourceAsStream("icons/affun256px.png")));
		settingsStage.setTitle("Settings");
		settingsRoot = settingsLoader.load();
		settingsController = settingsLoader.<SettingsController>getController();
		settingsController.initialize(twitter, userCfg, controller);
		settingsStage.setScene(new Scene(settingsRoot));

		if (!stage.isShowing()) stage.show();
	}

}
