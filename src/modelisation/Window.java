package modelisation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

public class Window extends Application {
	
	ObservableList<String> headers = FXCollections.observableArrayList();
	ObservableList<ObservableList> données = FXCollections.observableArrayList();
	
	
	TableView<ObservableList> tableView;

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
    	tableView = new TableView();
    	
    	
    	
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
      	  
        	public void handle(ActionEvent t){
        		// ouvre une boite de dialogue pour la selection d'un fichier
    			FileChooser fileChooser = new FileChooser(); 
    			// extension fichier
    			FileChooser.ExtensionFilter extFilterCSV = new FileChooser.ExtensionFilter("CSV files(*.csv)","*.csv"); 
    			
    			fileChooser.getExtensionFilters().addAll(extFilterCSV);

    			File csv = fileChooser.showOpenDialog(null);
    			// si le csv existe lire le CSV
    			if(csv!=null) {
    				lireCSV(csv);
        	}
        }
        
        private void lireCSV(File file) {
        	try {
        		// si le fichier existe et qu'il peut être lu
        		if(verificationFichier(file)) {
        			FileInputStream in = new FileInputStream(file);
        			InputStreamReader sr = new InputStreamReader(in, "UTF-8");
        			BufferedReader br = new BufferedReader(sr);
        			
        			String ligne; // lignes du CSV (ligne d'entête "nom colonnes" nom compris)
        			boolean header = true;
        			while((ligne = br.readLine()) != null) {
        				// découpe la string en une suite de mots séparés par le ";"
        				StringTokenizer séparateur = new StringTokenizer(ligne, ";");
        				ObservableList<String>ligneListe = FXCollections.observableArrayList();
        				
        				if(header) {
        					// tant que j'ai encore un séparateur ";"
        					while(séparateur.hasMoreTokens()) {
        						headers.add(séparateur.nextToken());
        					}
        					header = false;
        				} else {
        					while (séparateur.hasMoreTokens()) {
        						ligneListe.add(séparateur.nextToken());
        					}
        					données.add(ligneListe);
        					
        				}
        			}
        		} else {
        			System.out.println("Le fichier n'existe pas / erreur de lecture.");
        		}
        	} catch (FileNotFoundException e) {
        		System.out.println("FileNotFoundException :"+e.getMessage());
        	} catch (IOException e) {
        		System.out.println("IOException:"+e.getMessage());
        	}
        	
        	creationTableau();
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
        treeRoot.getChildren().setAll(trainItem,voitureItem);
        treeView.setRoot(treeRoot);
        
        // supprimer noeuds arbre
        Button supprimer = new Button("Supprimer Noeuds");
        supprimer.setOnAction(new EventHandler<ActionEvent>() {
     	   public void handle(ActionEvent e) {
     		   TreeItem delete = (TreeItem)treeView.getSelectionModel().getSelectedItem();
     		   boolean suppr = delete.getParent().getChildren().remove(delete);
     		   System.out.println("suppression");
     	   }
     	   
        });
        
        
    	
    	
    	
        AnchorPane root = new AnchorPane();
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
        primaryStage.show();
        
    }
    
    private void creationTableau() {
    	int numeroColonne = 0;
    	TableColumn[] colonne = new TableColumn[headers.size()];
    	for (String nomColonne : headers) {
    		final int indice = numeroColonne;
    		colonne[numeroColonne] = new TableColumn(nomColonne);
    		colonne[numeroColonne].setCellValueFactory(
    				new Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
    					public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param){
    						return new SimpleStringProperty(param.getValue().get(indice).toString());
    					}
    				});
    		numeroColonne++;
    	}
    	tableView.getColumns().addAll(colonne);
    	tableView.setItems(données); // ajouts des données au tableView
    }
    
    private boolean verificationFichier(File file) {
    	if(file.exists()) {
    		if(file.isFile() && file.canRead()) {
    			return true;
    		}
    		
    	}
    	return false;
    }
}
