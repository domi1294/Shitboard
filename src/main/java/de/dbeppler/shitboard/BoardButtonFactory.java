package de.dbeppler.shitboard;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;

import java.io.IOException;
import java.net.URLClassLoader;

/**
 * @author Dominik Beppler
 * @since 06.02.2016
 */
public class BoardButtonFactory {
	private static FXMLLoader boardButtonLoader;

	public static Button createNewBoardButton(int id) {
		boardButtonLoader = new FXMLLoader(URLClassLoader.getSystemResource("board-button.fxml"));
		boardButtonLoader.setControllerFactory(p -> Controller.getInstance());
		try {
			Button btn = boardButtonLoader.load();
			btn.setId("btn" + id);
			btn.getContextMenu().setId("ctxMn" + id);
			return btn;
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
