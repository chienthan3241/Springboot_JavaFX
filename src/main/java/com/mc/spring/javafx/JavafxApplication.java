package com.mc.spring.javafx;

import com.mc.spring.javafx.controller.PersonController;
import com.mc.spring.javafx.model.Person;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

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

	private void initPerson() {
		personData.add(new Person("Ha Mi", "Tran"));
		personData.add(new Person("Ha Linh", "Tran"));
		personData.add(new Person("Manh Cuong", "Tran"));
	}

	@Override
	public void start(Stage stage) throws Exception {
		mainStage = stage;
		mainStage.setTitle("test");
		initRootlayout();
		showPerson();
	}
}
