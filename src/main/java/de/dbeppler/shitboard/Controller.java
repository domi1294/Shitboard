package de.dbeppler.shitboard;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.io.FilenameUtils;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Dominik Beppler
 * @since 05.02.2016
 */
public class Controller {
	private static Controller instance;

	@FXML
	private FlowPane fpBtnContainer;
	@FXML
	private Button btn0;
	@FXML
	private Button btn1;
	@FXML
	private Button btn2;
	@FXML
	private Button btn3;
	private FileChooser imageFileChooser;
	private DirectoryChooser folderChooser;
	//private JIntellitype jIntellitype;
	private Twitter twitter;
	private String[] imageExtensions = new String[]{"jpg", "png", "gif"};
	private Stage settingsStage;
	private int boardButtonCount = 4;
	private int boardButtonWidth = 90;
	private int boardButtonHeight = 40;

	private Controller() {
		imageFileChooser = new FileChooser();
		imageFileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		imageFileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("All Files", "*.*"),
				new FileChooser.ExtensionFilter("JPG", "*.jpg"),
				new FileChooser.ExtensionFilter("PNG", "*.png"),
				new FileChooser.ExtensionFilter("GIF", "*.gif")
		);
		folderChooser = new DirectoryChooser();
		folderChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		//jIntellitype = JIntellitype.getInstance();
	}

	public static Controller getInstance() {
		if (instance == null) {
			instance = new Controller();
		}
		return instance;
	}

	public void initialize(Twitter twitter, Stage settingsStage) {
		this.twitter = twitter;
		this.settingsStage = settingsStage;
		setBoardButtonWidth(90);
		setBoardButtonHeight(40);
		btn0.setOnAction(event -> {
			try {
				twitter.updateStatus("Folgt");
			} catch (TwitterException e) {
				e.printStackTrace();
			}
		});
		btn1.setOnAction(event -> {
			try {
				twitter.updateStatus("Alles");
			} catch (TwitterException e) {
				e.printStackTrace();
			}
		});
		btn2.setOnAction(event -> {
			try {
				twitter.updateStatus("Fakten");
			} catch (TwitterException e) {
				e.printStackTrace();
			}
		});
		btn3.setOnAction(event -> {
			try {
				twitter.updateStatus("FUN");
			} catch (TwitterException e) {
				e.printStackTrace();
			}
		});
	}

	@FXML
	public void settingsButtonPressed() {
		settingsStage.show();
	}

	/*@FXML
	public void menuButtonPressed(ActionEvent evt) {
		System.out.println(((MenuItem) evt.getSource()).getStyleClass().get(1));
		switch (((MenuItem) evt.getSource()).getStyleClass().get(1)) {
			case "set-name":
				menuSetNamePressed(evt);
				break;
			case "set-icon":
				menuSetIconPressed(evt);
				break;
			case "set-hotkey":
				menuSetHotkeyPressed(evt);
				break;
			case "set-text":
				menuSetTextPressed(evt);
				break;
			case "set-image":
				menuSetImagePressed(evt);
				break;
			case "set-image-text":
				menuSetImageTextPressed(evt);
				break;
			case "all-image-folder":
				menuAllImageFolderPressed(evt);
				break;
			case "random-image-folder":
				menuRandomImageFolderPressed(evt);
				break;
		}
		System.out.println(evt.getSource());
		System.out.println(((MenuItem) evt.getSource()).getParentPopup());
		System.out.println(((MenuItem) evt.getSource()).getParentPopup().getId());
	}*/

	@FXML
	public void menuSetNamePressed(ActionEvent evt) {
		char id = ((MenuItem) evt.getSource()).getParentPopup().getId().charAt(5);
		Button btn = (Button) fpBtnContainer.lookup("#btn" + id);
		TextInputDialog dialog = new TextInputDialog(btn.getText());
		dialog.setTitle("Change Button Name");
		dialog.setHeaderText("Enter new button name.");
		dialog.showAndWait().ifPresent(btn::setText);
	}

	@FXML
	public void menuSetIconPressed(ActionEvent evt) {
		char id = ((MenuItem) evt.getSource()).getParentPopup().getId().charAt(5);
		Button btn = (Button) fpBtnContainer.lookup("#btn" + id);
		imageFileChooser.setTitle("Change Button Icon");
		File f = imageFileChooser.showOpenDialog(((MenuItem) evt.getSource()).getParentPopup().getOwnerWindow());
		try {
			btn.setGraphic(new ImageView(new Image(f.toURI().toURL().toString())));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void menuSetHotkeyPressed(ActionEvent evt) {
		evt.getSource();
		Alert a = new Alert(Alert.AlertType.INFORMATION);
		a.setTitle("Not yet implemented");
		a.setHeaderText("Coming soon\u2122");
		a.show();
	}

	@FXML
	public void menuSetTextPressed(ActionEvent evt) {
		char id = ((MenuItem) evt.getSource()).getParentPopup().getId().charAt(5);
		Button btn = (Button) fpBtnContainer.lookup("#btn" + id);
		TextInputDialog dialog = new TextInputDialog(btn.getText());
		dialog.setTitle("Set Tweet Text");
		dialog.setHeaderText(String.format(
				"Enter new tweet text. Special expressions:%n" +
						"\t<> - Replaced with an incrementing number starting at 1%n" +
						"\t<x> - Replaced with an incrementing number starting at x"
		));
		int[] counter = new int[1];
		counter[0] = 1;
		dialog.showAndWait().ifPresent(s -> {
			Matcher m = Pattern.compile("<\\d+>").matcher(s);
			String val;
			if (m.find()) {
				String match = m.group();
				counter[0] = Integer.parseInt(match.substring(1, match.length() - 1));
				val = s.replace(match, "<>");
			} else
				val = s;
			btn.setOnAction(event -> {
				try {
					twitter.updateStatus(val.replace("<>", "" + counter[0]));
					counter[0]++;
				} catch (TwitterException e) {
					e.printStackTrace();
				}
			});
		});

	}

	@FXML
	public void menuSetImagePressed(ActionEvent evt) {
		char id = ((MenuItem) evt.getSource()).getParentPopup().getId().charAt(5);
		Button btn = (Button) fpBtnContainer.lookup("#btn" + id);
		imageFileChooser.setTitle("Set Tweet Image");
		File f = imageFileChooser.showOpenDialog(((MenuItem) evt.getSource()).getParentPopup().getOwnerWindow());
		if (f.exists() && f.canRead() && f.isFile())
			btn.setOnAction(event -> tweetImage(f));
	}

	@FXML
	public void menuSetImageTextPressed(ActionEvent evt) {
		char id = ((MenuItem) evt.getSource()).getParentPopup().getId().charAt(5);
		Button btn = (Button) fpBtnContainer.lookup("#btn" + id);
		imageFileChooser.setTitle("Set Tweet Image");
		TextInputDialog dialog = new TextInputDialog(btn.getText());
		dialog.setTitle("Set Tweet Text");
		dialog.setHeaderText(String.format(
				"Enter new tweet text. Special expressions:%n" +
						"\t<> - Replaced with an incrementing number starting at 1%n" +
						"\t<x> - Replaced with an incrementing number starting at x"
		));
		int[] counter = new int[1];
		counter[0] = 1;
		dialog.showAndWait().ifPresent(s -> {
			File f = imageFileChooser.showOpenDialog(((MenuItem) evt.getSource()).getParentPopup().getOwnerWindow());
			if (f.exists() && f.canRead() && f.isFile()) {
				Matcher m = Pattern.compile("<\\d+>").matcher(s);
				String val;
				if (m.find()) {
					String match = m.group();
					counter[0] = Integer.parseInt(match.substring(1, match.length() - 1));
					val = s.replace(match, "<>");
				} else
					val = s;
				btn.setOnAction(event -> {
					try {
						StatusUpdate statusUpdate = new StatusUpdate(val.replace("<>", "" + counter[0]));
						statusUpdate.setMedia(f);
						twitter.updateStatus(statusUpdate);
						counter[0]++;
					} catch (TwitterException e) {
						e.printStackTrace();
					}
				});
			}
		});
	}

	@FXML
	public void menuAllImageFolderPressed(ActionEvent evt) {
		char id = ((MenuItem) evt.getSource()).getParentPopup().getId().charAt(5);
		Button btn = (Button) fpBtnContainer.lookup("#btn" + id);
		File f = folderChooser.showDialog(((MenuItem) evt.getSource()).getParentPopup().getOwnerWindow());
		if (f.exists() && f.canRead() && f.isDirectory()) {
			btn.setOnAction(event -> {
				try (Stream<Path> paths = Files.list(f.toPath())) {
					paths
							.map(Path::toFile)
							.filter(file -> file.isFile() && FilenameUtils.isExtension(file.getName(), imageExtensions))
							.forEach(this::tweetImage);
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		}
	}

	@FXML
	public void menuRandomImageFolderPressed(ActionEvent evt) {
		char id = ((MenuItem) evt.getSource()).getParentPopup().getId().charAt(5);
		Button btn = (Button) fpBtnContainer.lookup("#btn" + id);
		File f = folderChooser.showDialog(((MenuItem) evt.getSource()).getParentPopup().getOwnerWindow());
		if (f.exists() && f.canRead() && f.isDirectory()) {
			btn.setOnAction(event -> {
				List<File> files;
				try (Stream<Path> paths = Files.list(f.toPath())) {
					files = paths
							.map(Path::toFile)
							.filter(file -> file.isFile() && FilenameUtils.isExtension(file.getName(), imageExtensions))
							.collect(Collectors.toList());
					Collections.shuffle(files);
					tweetImage(files.get(0));
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		}
	}

	public synchronized void setBoardButtonCount(int newCount) {
		int oldCount = boardButtonCount;
		boardButtonCount = newCount;
		if (newCount < oldCount) {
			ObservableList<Node> list = fpBtnContainer.getChildren();
			for (; oldCount >= newCount; oldCount--)
				list.remove(fpBtnContainer.lookup("#btn" + oldCount));
		} else if (newCount > oldCount) {
			ObservableList<Node> list = fpBtnContainer.getChildren();
			for (; oldCount < newCount; oldCount++) {
				Button btn = BoardButtonFactory.createNewBoardButton(oldCount);
				btn.setPrefWidth(boardButtonWidth);
				btn.setPrefHeight(boardButtonHeight);
				list.add(btn);
			}
		}
	}

	public synchronized void setBoardButtonWidth(int newWidth) {
		boardButtonWidth = newWidth;
		fpBtnContainer.lookupAll(".board-button")
				.forEach(btn -> ((Button) btn).setPrefWidth(newWidth));
	}

	public synchronized void setBoardButtonHeight(int newHeight) {
		boardButtonHeight = newHeight;
		fpBtnContainer.lookupAll(".board-button")
				.forEach(btn -> ((Button) btn).setPrefHeight(newHeight));
	}

	private void tweetImage(File image) {
		tweetImage(image, "-fx-pref-width: 90px;");
	}

	private void tweetImage(File image, String text) {
		try {
			StatusUpdate statusUpdate = new StatusUpdate(text);
			statusUpdate.setMedia(image);
			System.out.println(twitter.updateStatus(statusUpdate));
		} catch (TwitterException e) {
			e.printStackTrace();
		}
	}

}
