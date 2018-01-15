package modelisation;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import modelisation.io.CsvDataReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class Window extends Application {
    TableView<String[]> tableView; //tableau pour stocker les données

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
        fichierMenu.getItems().addAll(nouveauItem, ouvertureFichierItem, enregistreFichierItem, quitterItem);
        fonctionsMenu.getItems().addAll(giniItem, khideuxItem, entropieItem);

        // Ajout du menu à la barre de Menu
        menuBar.getMenus().addAll(fichierMenu, fonctionsMenu, aideMenu);


        // Creation tableau
        tableView = new TableView<>();

        // Set Accelerator for Exit MenuItem.
        quitterItem.setAccelerator(KeyCombination.keyCombination("Ctrl+X"));

        // Quand l'utilisateur clic sur le bouteau quitter
        quitterItem.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                System.exit(0);
            }
        });

        //recuperation CSV
        ouvertureFichierItem.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent t) {
                // ouvre une boite de dialogue pour la selection d'un fichier
                FileChooser fileChooser = new FileChooser();
                // extension fichier
                FileChooser.ExtensionFilter extFilterCSV = new FileChooser.ExtensionFilter("CSV files(*.csv)", "*.csv");

                fileChooser.getExtensionFilters().addAll(extFilterCSV);

                File csv = fileChooser.showOpenDialog(null);
                // si le csv existe lire le CSV
                if (csv != null) {
                    lireCSV(csv);
                }
            }

            private void lireCSV(File file) {
                try {
                    TrainingData data = new CsvDataReader(new FileInputStream(file)).read();
                    creationTableau(data);
                } catch (FileNotFoundException e) {
                    System.out.println("FileNotFoundException :" + e.getMessage());
                } catch (IOException e) {
                    System.out.println("IOException:" + e.getMessage());
                }
            }
        });


        //sauvegarde de l'arbre
        enregistreFichierItem.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();

                // filtre extension
                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.txt");
                fileChooser.getExtensionFilters().add(extFilter);

                // boîte de dialogue sauvegarde
                File save = fileChooser.showSaveDialog(primaryStage);
            }
        });

        //test arbre
        TreeView<String> treeView = new TreeView<String>();

        final TreeItem<String> trainItem = new TreeItem<>("train");
        trainItem.getChildren().setAll(new TreeItem("locomotive"));
        trainItem.setExpanded(true);

        final TreeItem<String> voitureItem = new TreeItem<>("voiture");
        voitureItem.getChildren().setAll(new TreeItem("toyota"));
        voitureItem.setExpanded(true);

        final TreeItem<String> treeRoot = new TreeItem<>("vehicules");
        treeRoot.setExpanded(true);
        treeRoot.getChildren().setAll(trainItem, voitureItem);
        treeView.setRoot(treeRoot);

        // supprimer noeuds arbre
        Button supprimer = new Button("Supprimer Noeuds");
        supprimer.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                TreeItem delete = (TreeItem) treeView.getSelectionModel().getSelectedItem();
                boolean suppr = delete.getParent().getChildren().remove(delete);
                System.out.println("suppression");
            }

        });
        
        
    	
    	
    	
        /*AnchorPane root = new AnchorPane();
        root.setTopAnchor(menuBar,  10.0);
        root.setLeftAnchor(menuBar, 10.0);
        AnchorPane.setRightAnchor(menuBar, 10.0);
       root.setTopAnchor(tableView, 60.0);
        root.setLeftAnchor(tableView, 10.0);
        root.setRightAnchor(tableView, 800.0);
        root.setBottomAnchor(tableView, 10.0);
        root.setTopAnchor(treeView, 60.0);
        root.setLeftAnchor(treeView, 800.0);
        root.setRightAnchor(treeView, 10.0);
        root.setBottomAnchor(treeView, 10.0);
        root.setTopAnchor(supprimer, 60.0);
        root.setLeftAnchor(supprimer, 600.0);
        root.setRightAnchor(supprimer, 600.0);
        root.setBottomAnchor(supprimer, 600.0);
        root.getChildren().add(menuBar);
        root.getChildren().add(tableView);
        root.getChildren().add(treeView);
        root.getChildren().add(supprimer);
    	Scene scene = new Scene(root, 400, 200);
        primaryStage.setTitle("Arbre");
        primaryStage.setScene(scene);
        primaryStage.show();*/

        BorderPane root = new BorderPane();
        root.setTop(menuBar);
        root.setLeft(tableView);
        root.setRight(treeView);
        root.setCenter(supprimer);
        Scene scene = new Scene(root, 400, 200);
        primaryStage.setTitle("Arbre");
        primaryStage.setScene(scene);
        primaryStage.show();


    }

    private void creationTableau(TrainingData data) {
        ArrayList<TableColumn<String[], String>> columns = new ArrayList<>(data.getColumns().size());
        String[] headers = data.getHeaders();
        for (int columnIndex = 0; columnIndex < data.getColumns().size(); ++columnIndex) {
            int finalColumnIndex = columnIndex;
            TableColumn<String[], String> tableColumn = new TableColumn<>(headers[columnIndex]);
            tableColumn.setCellValueFactory(row -> new SimpleStringProperty(row.getValue()[finalColumnIndex]));
            columns.add(tableColumn);
        }

        ObservableList<String[]> rowIndexes = FXCollections.observableArrayList();
        rowIndexes.addAll(data.getRawLines());
        tableView.getColumns().setAll(columns);
        tableView.setItems(rowIndexes); // ajouts des données au tableView
    }
}
