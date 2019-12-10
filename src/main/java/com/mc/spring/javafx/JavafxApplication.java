package com.mc.spring.javafx;

import com.mc.spring.javafx.controller.BirthdayStatisticsController;
import com.mc.spring.javafx.controller.MainController;
import com.mc.spring.javafx.controller.PersonController;
import com.mc.spring.javafx.controller.PersonEditController;
import com.mc.spring.javafx.model.Person;
import com.mc.spring.javafx.model.PersonListWrapper;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

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

			MainController controller = fxmlLoader.getController();
			controller.setMainApp(this);

			mainStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}

		File file = getPersonFilePath();
		if (file != null) {
			loadPersonsFromFile(file);
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

	public File getPersonFilePath() {
		Preferences prefs = Preferences.userNodeForPackage(getClass());
		String filePath = prefs.get("filePath", null);
		if (filePath != null) {
			return new File(filePath);
		} else {
			return null;
		}
	}

	public void setPersonFilePath(File file) {
		Preferences prefs = Preferences.userNodeForPackage(getClass());
		if (file != null) {
			prefs.put("filePath", file.getPath());

			mainStage.setTitle("AdressApp - " + file.getName());
		} else {
			prefs.remove("filePath");
			mainStage.setTitle("AdressApp");
		}
	}

	public void loadPersonsFromFile(File file) {
		try {
			JAXBContext context = JAXBContext.newInstance(PersonListWrapper.class);
			Unmarshaller um = context.createUnmarshaller();
			PersonListWrapper wrapper = (PersonListWrapper) um.unmarshal(file);
			personData.clear();
			personData.addAll(wrapper.getPersons());
			setPersonFilePath(file);
		} catch (JAXBException e) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Could not load data");
			alert.setContentText("Could not load data from file:\n" + file.getPath());

			alert.showAndWait();
		}
	}

	public void savePersonDataToFile(File file) {
		try {
			JAXBContext context = JAXBContext.newInstance(PersonListWrapper.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			PersonListWrapper wrapper = new PersonListWrapper();
			wrapper.setPersons(personData);
			m.marshal(wrapper, file);

			setPersonFilePath(file);

		} catch (JAXBException e) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Could not save data");
			alert.setContentText("Could not save data to file:\n" + file.getPath());

			alert.showAndWait();
		}
	}

	public void showBirthdayStatistics() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(JavafxApplication.class.getResource("/fxml/BirthdayStatistics.fxml"));
			AnchorPane page = (AnchorPane) loader.load();
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Birthday Statistics");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(mainStage);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);

			BirthdayStatisticsController controller = loader.getController();

			controller.setPersonData(personData);
			dialogStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public Stage getMainStage() {
		return mainStage;
	}
}
