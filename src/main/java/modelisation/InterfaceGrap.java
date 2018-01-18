package modelisation;


import javafx.application.Application;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import modelisation.builder.DecisionTreeBuilder;
import modelisation.builder.strategies.ChiSquared;
import modelisation.builder.strategies.ClassificationError;
import modelisation.builder.strategies.EntropyReduction;
import modelisation.builder.strategies.GiniImpurity;
import modelisation.data.Column;
import modelisation.data.TrainingData;
import modelisation.io.CsvDataReader;
import modelisation.io.GraphvizTreeWriter;
import modelisation.tree.DecisionTree;
import org.eclipse.jdt.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Classe d'affichage de l'interface graphique
 *
 * @author agnerayq
 */

public class InterfaceGrap extends Application {
    TableView<Integer> tbleView; //tableau pour stocker les données
    TableView<Integer> tbleViewSelect;  // tableau pour stocker les colonnes selectionees
    @Nullable TrainingData data;
    List<Integer> indicesColonnes = new ArrayList<>();
    Integer targetColumnIndex, idColumnIndex;
    ComboBox<String> cbbColonne = new ComboBox<>();

    DecisionTreeBuilder.Configuration config = DecisionTreeBuilder.DEFAULT_CONFIG;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Creation tableau
        tbleView = new TableView<>();
        tbleViewSelect = new TableView<>();

        BorderPane rootPane = new BorderPane();
        AnchorPane acp = new AnchorPane();
        AnchorPane anchorData = new AnchorPane();

        ToolBar tlb1 = new ToolBar();
        MenuBar menuB = new MenuBar();
        Menu fichier = new Menu("Fichier");
        Menu parametre = new Menu("Parametrage");
        Menu aide = new Menu("Aide");
        Menu sousMnu1 = new Menu("Classification");
        Menu sousMnu2 = new Menu("Regression");
        MenuItem mItemO = new MenuItem("Ouvrir");
        MenuItem mItemE = new MenuItem("Enregistre");
        MenuItem mItemEs = new MenuItem("Enregistre sous...");
        MenuItem mItemEx = new MenuItem("Exporter");
        MenuItem mItemImp = new MenuItem("Imprimer");
        MenuItem mItemQ = new MenuItem("Quitter");
        MenuItem gini = new MenuItem("Gini");
        MenuItem entropie = new MenuItem("Entropie");
        MenuItem chi2 = new MenuItem("Chi2");
        MenuItem erreur = new MenuItem("Erreur de Classification");
        MenuItem carb = new MenuItem("CarB");
        MenuItem apropos = new MenuItem("A propos");
        menuB.getMenus().addAll(fichier, parametre, aide);
        parametre.getItems().addAll(sousMnu1, sousMnu2);
        sousMnu1.getItems().addAll(gini, chi2, entropie, erreur);
        sousMnu2.getItems().addAll(carb);
        fichier.getItems().addAll(mItemO, mItemE, mItemEs, mItemEx, mItemImp, mItemQ);
        aide.getItems().addAll(apropos);

        //appelle à l'indicateur Chi2
        chi2.setOnAction(event -> config = config.withSplittingStrategy(new ChiSquared()));

        //appelle à l'indicateur Gini
        gini.setOnAction(event -> config = config.withSplittingStrategy(new GiniImpurity()));

        //appelle à l'indicateur Entropie
        entropie.setOnAction(event -> config = config.withSplittingStrategy(new EntropyReduction()));

        //appelle à l'indicateur Erreur classement
        erreur.setOnAction(event -> config = config.withSplittingStrategy(new ClassificationError()));

        Button ouvrir = new Button();
        Button enregistre = new Button();
        Button parametrage = new Button();
        Button imprimer = new Button();
        ouvrir.setPrefHeight(39);
        ouvrir.setPrefWidth(52);
        enregistre.setPrefHeight(39);
        enregistre.setPrefWidth(52);
        parametrage.setPrefHeight(39);
        parametrage.setPrefWidth(52);
        imprimer.setPrefHeight(39);
        imprimer.setPrefWidth(52);
        Image image1 = new Image(getClass().getResourceAsStream("/open.png"));
        Image image2 = new Image(getClass().getResourceAsStream("/enregistrer.png"));
        Image image3 = new Image(getClass().getResourceAsStream("/paramettre.png"));
        Image image4 = new Image(getClass().getResourceAsStream("/printer.png"));
        ImageView imageView1 = new ImageView(image1);
        imageView1.setFitHeight(39);
        imageView1.setFitWidth(52);
        ImageView imageView2 = new ImageView(image2);
        imageView2.setFitHeight(39);
        imageView2.setFitWidth(52);
        ImageView imageView3 = new ImageView(image3);
        imageView3.setFitHeight(39);
        imageView3.setFitWidth(52);
        ImageView imageView4 = new ImageView(image4);
        imageView4.setFitHeight(39);
        imageView4.setFitWidth(52);

        ouvrir.setGraphic(imageView1);
        enregistre.setGraphic(imageView2);
        parametrage.setGraphic(imageView3);
        imprimer.setGraphic(imageView4);

        EventHandler<ActionEvent> ouvrirFichier = t -> {
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extFilterCSV = new FileChooser.ExtensionFilter("CSV files(*.csv)", "*.csv");
            fileChooser.getExtensionFilters().addAll(extFilterCSV);

            File csv = fileChooser.showOpenDialog(null);
            if (csv != null) {
                readDataFromCsv(csv);
            }
        };

        //récupérer le CSV bouton ouvrir
        ouvrir.setOnAction(ouvrirFichier);
        //récupérer le CSV ouvrir
        mItemO.setOnAction(ouvrirFichier);

        //sauvegarde de l'arbre
        enregistre.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();

                // filtre extension
                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png");
                fileChooser.getExtensionFilters().add(extFilter);

                // bo�te de dialogue sauvegarde
                File save = fileChooser.showSaveDialog(primaryStage);
                if (save != null) {
                    List<Integer> dataColumnIndexes = new ArrayList<>(indicesColonnes);

                    // TODO: hardcoded...
                    idColumnIndex = 2;
                    targetColumnIndex = 0;
                    dataColumnIndexes.remove(idColumnIndex);
                    dataColumnIndexes.remove(targetColumnIndex);

                    System.out.println("building tree using " + config.getSplittingStrategy().getName());
                    DecisionTreeBuilder treeBuilder = new DecisionTreeBuilder(data, idColumnIndex, targetColumnIndex, dataColumnIndexes, config);
                    DecisionTree tree = treeBuilder.buildTree();

                    try {
                        System.out.println("Saving tree to png " + save.getAbsolutePath());
                        GraphvizTreeWriter treeWriter = new GraphvizTreeWriter(save);
                        treeWriter.write(tree, targetColumnIndex);
                        System.out.println("done");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


        parametrage.setOnAction(evt -> {
            Stage s = new Stage();

            GridPane root = new GridPane();
            VBox vbox1 = new VBox();
            VBox vbox2 = new VBox();
            VBox vbox3 = new VBox();
            ToggleGroup group1 = new ToggleGroup();
            ToggleGroup group2 = new ToggleGroup();
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
            Button btnV = new Button("Valider");
            Button btnC = new Button("Annule");
            HBox hbox = new HBox();
            Slider slider = new Slider();
            VBox vbox4 = new VBox();

            //appelle à l'indicateur Chi2
            rb6.setOnAction(event -> config = config.withSplittingStrategy(new ChiSquared()));

            //appelle à l'indicateur Gini
            rb7.setOnAction(event -> config = config.withSplittingStrategy(new GiniImpurity()));

            //appelle à l'indicateur Entropie
            rb8.setOnAction(event -> config = config.withSplittingStrategy(new EntropyReduction()));

            //appelle à l'indicateur Erreur classement
            rb9.setOnAction(event -> config = config.withSplittingStrategy(new ClassificationError()));


            rb1.setToggleGroup(group1);
            rb1.setSelected(true);
            rb2.setToggleGroup(group1);
            vbox1.getChildren().addAll(rb1, rb2);
            vbox1.setSpacing(3);
            vbox1.setAlignment(Pos.TOP_LEFT);
            root.add(vbox1, 0, 1);
            GridPane.setMargin(vbox1, new Insets(2, 2, 2, 2));

            rb3.setToggleGroup(group2);
            rb3.setSelected(true);
            rb4.setToggleGroup(group2);
            rb5.setToggleGroup(group2);
            vbox2.getChildren().addAll(rb3, rb4, rb5);
            vbox2.setAlignment(Pos.TOP_LEFT);
            vbox2.setSpacing(3);
            root.add(vbox2, 0, 2);
            GridPane.setMargin(vbox2, new Insets(2, 2, 2, 2));

            rb6.setToggleGroup(group2);
            rb6.setSelected(true);
            rb7.setToggleGroup(group2);
            rb8.setToggleGroup(group2);
            rb9.setToggleGroup(group2);
            vbox3.getChildren().addAll(rb6, rb7, rb8, rb9);
            vbox3.setAlignment(Pos.TOP_LEFT);
            vbox3.setSpacing(3);
            root.add(vbox3, 1, 1);
            GridPane.setMargin(vbox3, new Insets(2, 2, 2, 2));
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
            GridPane.setMargin(vbox4, new Insets(2, 2, 2, 2));

            hbox.getChildren().add(btnV);
            hbox.getChildren().add(btnC);
            hbox.setSpacing(35);
            hbox.setAlignment(Pos.CENTER_RIGHT);
            root.add(hbox, 1, 3);
            GridPane.setMargin(hbox, new Insets(10, 2, 0, 0));

            root.setAlignment(Pos.CENTER);
            root.setPadding(new Insets(20));
            root.setHgap(10);
            root.setVgap(10);
            s.setMinWidth(300);
            s.setMinHeight(300);
            s.setScene(new Scene(root));

            s.show();
        });

        imprimer.setOnAction(evt -> {

        });

        //bouton quitter
        mItemQ.setAccelerator(KeyCombination.keyCombination("Ctrl+X"));
        mItemQ.setOnAction(evt -> System.exit(0));


        acp.setPrefHeight(70);
        acp.setPrefWidth(800);
        acp.getChildren().addAll(menuB, tlb1);
        BorderPane.setAlignment(acp, Pos.TOP_CENTER);
        BorderPane.setAlignment(anchorData, Pos.CENTER);

        menuB.setPrefHeight(25);
        menuB.setPrefWidth(800);
        tlb1.setPrefHeight(55);
        tlb1.setPrefWidth(800);
        AnchorPane.setTopAnchor(tlb1, 25.0);
        tlb1.getItems().addAll(ouvrir, enregistre, parametrage, imprimer);


        anchorData.setPrefHeight(520);
        anchorData.setPrefWidth(800);

        SplitPane splitPane1 = new SplitPane();
        splitPane1.setPrefSize(800, 520);
        splitPane1.setOrientation(Orientation.HORIZONTAL);
        AnchorPane acp1 = new AnchorPane();
        SplitPane splitPane2 = new SplitPane();
        splitPane2.setPrefSize(400, 520);
        splitPane2.setOrientation(Orientation.VERTICAL);

        AnchorPane acp3 = new AnchorPane();
        ToolBar tlb2 = new ToolBar();

        Button selectC = new Button("Choix Colonne");
        CheckBox cbxContinuite = new CheckBox("Colonne continue");
        tlb2.setPrefHeight(20);
        tlb2.setPrefWidth(400);
        tbleView.setPrefHeight(215);
        tbleView.setPrefWidth(400);
        AnchorPane.setBottomAnchor(tlb2, 0.0);
        AnchorPane.setTopAnchor(tbleView, 0.0);
        tlb2.getItems().addAll(cbbColonne, selectC, cbxContinuite);
        acp3.getChildren().addAll(tbleView, tlb2);

        AnchorPane acp4 = new AnchorPane();
        tbleViewSelect.setPrefHeight(260);
        tbleViewSelect.setPrefWidth(400);
        acp4.getChildren().addAll(tbleViewSelect);
        splitPane2.getItems().addAll(acp3, acp4);
        acp1.getChildren().add(splitPane2);

        AnchorPane acp2 = new AnchorPane();
        ToolBar tlb3 = new ToolBar();
        TreeView treeView = new TreeView();
        Button selectC2 = new Button("Supprimer");
        Button validerS2 = new Button("Annuler");
        tlb3.setPrefHeight(20);
        tlb3.setPrefWidth(390);
        treeView.setPrefHeight(488);
        treeView.setPrefWidth(390);
        AnchorPane.setTopAnchor(treeView, 32.0);
        tlb3.getItems().addAll(selectC2, validerS2);
        acp2.getChildren().addAll(tlb3, treeView);
        splitPane1.getItems().addAll(acp1, acp2);
        anchorData.getChildren().add(splitPane1);


        cbbColonne.setOnAction(e -> {
            if (data != null) {
                Column column = data.getColumn(cbbColonne.getValue());
                cbxContinuite.setSelected(!column.isDiscrete());
                cbxContinuite.setDisable(!column.canSetContinuity());
            }
        });

        selectC.setOnAction(e -> {
            if (data != null) {
                Column column = data.getColumn(cbbColonne.getValue());
                TableColumn<Integer, ?> tableColumn = getTableColumn(column);
                // addContextMenu(tableColumn);
                tbleViewSelect.getColumns().add(tableColumn);
                indicesColonnes.add(column.getIndex());
                if (column.canSetContinuity()) {
                    column.setContinuity(cbxContinuite.isSelected() ? Column.Continuity.CONTINUOUS : Column.Continuity.DISCRETE);
                }
            }
        });

        //bouton supprimer
        selectC2.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                TreeItem delete = (TreeItem) treeView.getSelectionModel().getSelectedItem();
                boolean suppr = delete.getParent().getChildren().remove(delete);
                System.out.println("suppression");
            }

        });

        rootPane.setTop(acp);
        rootPane.setCenter(anchorData);

        Scene scene = new Scene(rootPane, 800, 600);
        primaryStage.setTitle("Arbre");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        // primaryStage.centerOnScreen();
        primaryStage.show();
        scene.setFill(Color.GHOSTWHITE);

        readDataFromCsv(new File("datasets/train.csv"));
    }

    private void readDataFromCsv(File csv) {
        try {
            data = lireCSV(csv);
            creationTableau(data);
            indicesColonnes.clear();
            tbleViewSelect.getColumns().clear();
            targetColumnIndex = idColumnIndex = null;
            cbbColonne.getItems().setAll(data.getHeaders());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * méthode de lecture d'un CSV à partir d'un fichier File
     *
     * @param file
     */
    private TrainingData lireCSV(File file) throws IOException {
        try {
            return new CsvDataReader(new FileInputStream(file)).read();
        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException :" + e.getMessage());
            throw e;
        } catch (IOException e) {
            System.out.println("IOException:" + e.getMessage());
            throw e;
        }
    }

    private TableColumn<Integer, ?> getTableColumn(Column column) {
        if (column.isDiscrete()) {
            TableColumn<Integer, String> tableColumn = new TableColumn<>(column.getHeader());
            tableColumn.setCellValueFactory(idx -> new SimpleStringProperty(column.getValueAsString(idx.getValue())));
            tableColumn.setUserData(column);
            return tableColumn;
        } else {
            TableColumn<Integer, Number> tableColumn = new TableColumn<>(column.getHeader());
            tableColumn.setCellValueFactory(idx -> new SimpleDoubleProperty(column.getValueAsNumber(idx.getValue()).doubleValue()));
            tableColumn.setUserData(column);
            return tableColumn;
        }
    }

    /**
     * méthode qui crée un tableau et qui ajoute les données du CSV à celui si
     *
     * @param data
     */
    private void creationTableau(TrainingData data) {
        List<TableColumn<Integer, ?>> tableColumns = new ArrayList<>(data.getColumns().size());
        for (Column column : data.getColumns()) {
            tableColumns.add(getTableColumn(column));
        }

        tbleView.getColumns().setAll(tableColumns);

        ObservableList<Integer> rowIndexes = FXCollections.observableArrayList();
        rowIndexes.addAll(IntStream.range(0, data.size()).boxed().collect(Collectors.toList()));
        tbleView.setItems(rowIndexes); // ajouts des données au tableView
        tbleViewSelect.setItems(rowIndexes);
    }


}
