package modelisation;


import javafx.application.Application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Parametrage extends Application {
	
	
	
	GridPane root = new GridPane();
	VBox vbox1 = new VBox();
	VBox vbox2 = new VBox();
	VBox vbox3 = new VBox();
	ToggleGroup group1 = new ToggleGroup();
	ToggleGroup group2 = new ToggleGroup();
	ToggleGroup group3 = new ToggleGroup();
	RadioButton rb1 = new RadioButton("Classification");
	RadioButton rb2 = new RadioButton("Regression");
	Label label = new Label("Select ");
	RadioButton rb3 = new RadioButton("Automatique");
	RadioButton rb4 = new RadioButton("Semi-automatique");
	RadioButton rb5 = new RadioButton("Utilisateur");
	
	RadioButton rb6 = new RadioButton("Chi2");
	RadioButton rb7 = new RadioButton("Gini");
	RadioButton rb8 = new RadioButton("Entropie");
	RadioButton rb9 = new RadioButton("Erreur de classement");
	Button btnV= new Button("Valider");
	Button btnC = new Button("Annule");
	HBox hbox = new HBox();
	Slider slider = new Slider();
	VBox vbox4 = new VBox();
    
   
   @Override
   public void start( Stage primaryStage) throws Exception {
	   
	   rb1.setToggleGroup(group1);
	   rb1.setSelected(true);
	   rb2.setToggleGroup(group1);
	   vbox1.getChildren().addAll(rb1,rb2);
	   vbox1.setSpacing(3);
	   vbox1.setAlignment(Pos.TOP_LEFT);
	   root.add(vbox1, 0, 1);
	   GridPane.setMargin(vbox1, new Insets(2,2,2,2));
	   
	   rb3.setToggleGroup(group2);
	   rb3.setSelected(true);
	   rb4.setToggleGroup(group2);
	   rb5.setToggleGroup(group2);
	   vbox2.getChildren().addAll(rb3,rb4,rb5);
	   vbox2.setAlignment(Pos.TOP_LEFT);
	   vbox2.setSpacing(3);
	   root.add(vbox2, 0, 2);
	   GridPane.setMargin(vbox2, new Insets(2,2,2,2));
	   
	   rb6.setToggleGroup(group2);
	   rb6.setSelected(true);
	   rb7.setToggleGroup(group2);
	   rb8.setToggleGroup(group2);
	   rb9.setToggleGroup(group2);
	   vbox3.getChildren().addAll(rb6,rb7,rb8,rb9);
	   vbox3.setAlignment(Pos.TOP_LEFT);
	   vbox3.setSpacing(3);
	   root.add(vbox3, 1, 1);
	   GridPane.setMargin(vbox3, new Insets(2,2,2,2));
	   root.setPadding(new Insets(20));
	   
	   vbox4.getChildren().addAll(label, slider);
	   vbox4.setSpacing(5);
	   hbox.setAlignment(Pos.TOP_CENTER);
	   
	   slider.setMin(0);
	   slider.setMax(100);
	   slider.setValue(10);
	   //slider.setShowTickLabels(true);
	  // slider.setShowTickMarks(true);
	   slider.setMajorTickUnit(50);
	   slider.setMinorTickCount(5);
	   slider.setBlockIncrement(10);
	   root.add(vbox4, 1, 2);
	   GridPane.setMargin(vbox4, new Insets(2,2,2,2));
	   
	   		hbox.getChildren().add(btnV);
	   		hbox.getChildren().add(btnC);
	   		hbox.setSpacing(35);
	   		hbox.setAlignment(Pos.CENTER_RIGHT);
		  root.add(hbox, 1, 3);
		  GridPane.setMargin(hbox, new Insets(10,2,0,0));
	
	  root.setAlignment(Pos.CENTER);
	  root.setPadding(new Insets(20));
	  root.setHgap(10);
	  root.setVgap(10);
	  primaryStage.setMinWidth(300);
	  primaryStage.setMinHeight(300);
	  primaryStage.setScene(new Scene (root));
	  
      primaryStage.show();
	  
   }
   
   public static void main(String[] args) {
		launch(args);
	}
   
   
  
}