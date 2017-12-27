package modelisation;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Window extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
    	
    	
    	 // Creation Menubar
        MenuBar menuBar = new MenuBar();
        
        // Creation menus
        Menu fichierMenu = new Menu("Fichier");
        Menu fonctionsMenu = new Menu("Fonctions");
        Menu aideMenu = new Menu("Aide");
        
        // Creation menu item
        MenuItem nouveauItem = new MenuItem("Nouveau");
        MenuItem ouvertureFichierItem = new MenuItem("Ouvrir fichier CSV");
        MenuItem enregistreFichierItem = new MenuItem("Enregistrer arbre");
        MenuItem quitterItem = new MenuItem("Quitter");
        
        MenuItem giniItem = new MenuItem("coefficient de Gini");
        MenuItem khideuxItem = new MenuItem("khi-deux");
        MenuItem entropieItem = new MenuItem("entropie");
        
        // Ajout menu item au menu
        fichierMenu.getItems().addAll(nouveauItem, ouvertureFichierItem,enregistreFichierItem, quitterItem);
        fonctionsMenu.getItems().addAll(giniItem, khideuxItem,entropieItem);
        
        // Ajout du menu à la barre de Menu
        menuBar.getMenus().addAll(fichierMenu, fonctionsMenu, aideMenu);
        
        
        // Creation tableau
    	final TableView<String> tableView = new TableView();
    	
    	// Ajout colonnes au tableau
    	int nbreColonnes = 9;
    	for(int i=0; i<nbreColonnes;i++)
    	{
    		TableColumn<String,String> col = new TableColumn<>(""+i);
    		tableView.getColumns().add(col);
    	}
    	
    	
    	
    	AnchorPane root = new AnchorPane();
    	root.setTopAnchor(menuBar,  10.0);
        root.setLeftAnchor(menuBar, 10.0);
        AnchorPane.setRightAnchor(menuBar, 10.0);
        root.getChildren().add(menuBar);
        root.setTopAnchor(tableView, 40.0);
        root.setLeftAnchor(tableView, 10.0);
        root.setRightAnchor(tableView, 190.0);
        root.setBottomAnchor(tableView, 10.0);
        root.getChildren().add(tableView);
    	Scene scene = new Scene(root, 400, 200);
        primaryStage.setTitle("Arbre");
        primaryStage.setScene(scene);
        primaryStage.show();
        
    }
}
