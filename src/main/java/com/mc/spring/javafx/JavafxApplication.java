package com.mc.spring.javafx;

import com.mc.spring.javafx.controller.PersonController;
import com.mc.spring.javafx.controller.PersonEditController;
import com.mc.spring.javafx.model.Person;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;

import javax.imageio.ImageIO;

@SpringBootApplication
public class JavafxApplication extends Application {
	private ConfigurableApplicationContext springContext;
	private Stage mainStage;
	private BorderPane rootLayout;

	private ObservableList<Person> personData = FXCollections.observableArrayList();

	public static void main(String[] args) {
		launch(args);
	}

	public ObservableList<Person> getPersonData() {
		return personData;
	}

	public void initRootlayout() {
		try {
			initPerson();
			springContext = SpringApplication.run(JavafxApplication.class);
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Rootlayout.fxml"));
			fxmlLoader.setControllerFactory(springContext::getBean);
			rootLayout = (BorderPane) fxmlLoader.load();

			Scene scene = new Scene(rootLayout);
			mainStage.setScene(scene);
			mainStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void showPerson() {
		try {
			// Load person overview.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/fxml/Person.fxml"));
			AnchorPane personOverview = (AnchorPane) loader.load();

			// Set person overview into the center of root layout.
			rootLayout.setCenter(personOverview);
			// Give the controller access to the main app.
			PersonController controller = loader.getController();
			controller.setMainApp(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean showPersonEditDialog(Person person) {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(JavafxApplication.class.getResource("/fxml/PersonEdit.fxml"));
			AnchorPane page = (AnchorPane) loader.load();
			// Create the dialog Stage.
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Edit Person");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(mainStage);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);

			//set the person into the controller
			PersonEditController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setPerson(person);

			//Show the dialog and wait until the user close it
			dialogStage.showAndWait();
			return controller.isOkClicked();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	private void initPerson() {
		personData.add(new Person("Ha Mi", "Tran"));
		personData.add(new Person("Ha Linh", "Tran"));
		personData.add(new Person("Manh Cuong", "Tran"));
	}

	@Override
	public void start(Stage stage) throws Exception {
		mainStage = stage;
		mainStage.setTitle("test");
		try {
			Image icon = new Image(JavafxApplication.class.getResourceAsStream("/images/iconfinder_Address_Book_86957.png"));
			mainStage.getIcons().add(icon);
		} catch (Exception e) {
			System.out.println("App icon can not be loaded!");
		}
		initRootlayout();
		showPerson();
	}

	public Stage getMainStage() {
		return mainStage;
	}
}
