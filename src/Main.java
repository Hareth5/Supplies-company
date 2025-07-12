import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {
    private static BorderPane main;
    private Catalog catalog;

    public Main() {
        main = new BorderPane();
        catalog = new Catalog();
    }

    public static void setMain(Node node) {
        main.setCenter(node);
    }

    @Override
    public void start(Stage primaryStage) {
        SideButtons sideButtons = new SideButtons(primaryStage);
        main.setLeft(sideButtons.main());
        main.setStyle("-fx-background-color: #C8C8C8;");
        main.getLeft().setStyle("-fx-background-color: #0d1b58; -fx-pref-width: 100;");

        setMain(new ProductManagement().main());

        Scene scene = new Scene(main, 900, 700);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        primaryStage.setMaximized(true);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Asha Management System");
        primaryStage.show();
    }
}