package modelisation.gui;


import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import modelisation.builder.DecisionTreeBuilder;
import modelisation.builder.strategies.*;
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class InterfaceGrap extends Application {
    TableView<Integer> tbleView; //tableau pour stocker les données
    ColumnSelectTableView tbleViewSelect;  // tableau pour stocker les colonnes selectionees
    DecisionTreeView treeView;

    @Nullable TrainingData data;
    DecisionTreeBuilder.Configuration config = DecisionTreeBuilder.DEFAULT_CONFIG;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Creation tableau
        tbleView = new TableView<>();
        tbleViewSelect = new ColumnSelectTableView();
        treeView = new DecisionTreeView();

        BorderPane rootPane = new BorderPane();

        ToolBar toolbar = buildToolbar();
        toolbar.setPrefHeight(67);
        rootPane.setTop(toolbar);

        SplitPane splitHorizontal = new SplitPane();
        splitHorizontal.setOrientation(Orientation.HORIZONTAL);

        SplitPane splitVertical = new SplitPane();
        splitVertical.setOrientation(Orientation.VERTICAL);
        splitVertical.getItems().addAll(tbleView, tbleViewSelect);

        ToolBar treeTolbar = new ToolBar();
        Button generer = new Button("Generer Arbre");
        Button supprimer = new Button("Supprimer");
        Button annuler = new Button("Annuler");
        treeTolbar.setPrefHeight(20);
        treeTolbar.getItems().addAll(generer,supprimer, annuler);
        
        // generation de l'arbre (TreeView)
        generer.setOnAction(evt -> {
            if (!checkColumnSelections()) {
                return;
            }
            treeView.setTree(buildTree(tbleViewSelect.getTargetColumn(), tbleViewSelect.getIdColumn()));
        });
        BorderPane treePane = new BorderPane();
        treePane.setTop(treeTolbar);
        treePane.setCenter(treeView);

        splitHorizontal.getItems().addAll(splitVertical, treePane);
        rootPane.setCenter(splitHorizontal);

        Scene scene = new Scene(rootPane, 800, 600);
        primaryStage.setTitle("Arbre");
        primaryStage.setResizable(true);
        primaryStage.setScene(scene);
        primaryStage.show();
        scene.setFill(Color.GHOSTWHITE);

        readDataFromCsv(new File("datasets/train.csv"));
    }
    /**
     * methode de verification de la selection des colonnes
     * @return
     */
    private boolean checkColumnSelections() {
        Column idColumn = tbleViewSelect.getIdColumn(), targetColumn = tbleViewSelect.getTargetColumn();
        if (idColumn == null || targetColumn == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            // TODO: translate labels?
            alert.setTitle("Probleme");
            alert.setHeaderText("Mauvais choix");
            alert.setContentText("Choisissez la cible et l ID des colonnes");

            alert.showAndWait();
            return false;
        }

        if (tbleViewSelect.getDataColumnIndexes().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            // TODO: translate labels?
            alert.setTitle("Probleme");
            alert.setHeaderText("Mauvais choix");
            alert.setContentText("Choisissez au moins une colonne de donnees!");

            alert.showAndWait();
            return false;
        }

        return true;
    }

    private DecisionTree buildTree(Column targetColumn, Column idColumn) {
        System.out.println("building tree using " + config.getSplittingStrategy().getName());
        DecisionTreeBuilder.Configuration realConfig = config;
        if (!targetColumn.isDiscrete()) {
            realConfig = realConfig.withSplittingStrategy(new VarianceReduction());
        }
        DecisionTreeBuilder treeBuilder = new DecisionTreeBuilder(
                data,
                idColumn.getIndex(),
                targetColumn.getIndex(),
                tbleViewSelect.getDataColumnIndexes(),
                realConfig
        );
        return treeBuilder.buildTree();
    }
    /***
     * methode de sauvegarde de l arbre au format png
     * @param targetColumn
     * @param idColumn
     * @param png
     */
    private void exportTreeAsPng(Column targetColumn, Column idColumn, File png) {
        DecisionTree tree = buildTree(targetColumn, idColumn);

        try {
            System.out.println("Saving tree to png " + png.getAbsolutePath());
            GraphvizTreeWriter treeWriter = new GraphvizTreeWriter(png);
            treeWriter.write(tree, targetColumn.getIndex());
            System.out.println("done");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readDataFromCsv(File csv) {
        try {
            data = lireCSV(csv);
            creationTableau(data);
            populateColumnSelectionTable();
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

    private void populateColumnSelectionTable() {
        if (data == null) {
            return;
        }
        tbleViewSelect.setColumns(data.getColumns());

    }

    private TableColumn<Integer, ?> getTableColumn(Column column) {
        TableColumn<Integer, String> tableColumn = new TableColumn<>(column.getHeader());
        tableColumn.setCellValueFactory(idx -> new SimpleStringProperty(column.getValueAsString(idx.getValue())));
        tableColumn.setUserData(column);
        tableColumn.setSortable(false);
        return tableColumn;
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
    }
    
    /**
     * methode de creation de la barre de menu
     * @return
     */
    private ToolBar buildToolbar() {
        ToolBar toolbar = new ToolBar();

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

        //récupérer le CSV bouton ouvrir
        ouvrir.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extFilterCSV = new FileChooser.ExtensionFilter("CSV files(*.csv)", "*.csv");
            fileChooser.getExtensionFilters().addAll(extFilterCSV);

            File csv = fileChooser.showOpenDialog(null);
            if (csv != null) {
                readDataFromCsv(csv);
            }
        });

        //sauvegarde de l'arbre
        enregistre.setOnAction(event -> {
            if (!checkColumnSelections()) {
                return;
            }
            FileChooser fileChooser = new FileChooser();

            // filtre extension
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png");
            fileChooser.getExtensionFilters().add(extFilter);

            // bo�te de dialogue sauvegarde
            File save = fileChooser.showSaveDialog(null);
            if (save != null) {
                exportTreeAsPng(tbleViewSelect.getTargetColumn(), tbleViewSelect.getIdColumn(), save);
            }
        });


        parametrage.setOnAction(evt -> buildParametrage().show());
        
        //impression de l arbre
        imprimer.setOnAction(evt -> {
        	Stage s = new Stage();
        	impression(treeView, s);
        });

        toolbar.setPrefHeight(55);
        toolbar.getItems().addAll(ouvrir, enregistre, parametrage, imprimer);
        return toolbar;
    }
    
    /**
     * methode creation de la fenetre de choix des indicateurs
     * @return
     */
    private Stage buildParametrage() {
        Stage s = new Stage();

        GridPane root = new GridPane();
        HBox hbox = new HBox();

        RadioButton rbClassif = new RadioButton("Classification");
        RadioButton rbRegress = new RadioButton("Regression");
        BooleanProperty isRegressionTree = tbleViewSelect.isRegressionTreeProperty();
        ObjectProperty<Column> targetColumn = tbleViewSelect.targetColumnProperty();

        ToggleGroup treeTypeRadioGroup = new ToggleGroup();
        rbClassif.setToggleGroup(treeTypeRadioGroup);
        rbRegress.setToggleGroup(treeTypeRadioGroup);
        rbClassif.setSelected(!isRegressionTree.getValue());
        rbRegress.setSelected(isRegressionTree.getValue());
        treeTypeRadioGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            isRegressionTree.setValue(newValue == rbRegress);
        });
        targetColumn.addListener((observable, oldValue, newValue) -> {
            boolean disable = newValue == null || !newValue.canSetContinuity();
            rbClassif.setDisable(disable);
            rbRegress.setDisable(disable);
        });

        VBox vboxTreeType = new VBox();
        vboxTreeType.getChildren().addAll(rbClassif, rbRegress);
        vboxTreeType.setSpacing(3);
        vboxTreeType.setAlignment(Pos.TOP_LEFT);
        root.add(vboxTreeType, 0, 1);

        ToggleGroup metricRadioGroup = new ToggleGroup();
        List<SplittingStrategy> metrics = Arrays.asList(
                new ChiSquared(),
                new GiniImpurity(),
                new EntropyReduction(),
                new ClassificationError()
        );
        Map<SplittingStrategy, RadioButton> metricRbs = metrics.stream()
                .collect(Collectors.toMap(split -> split, split -> {
                    RadioButton radio = new RadioButton(split.getName());
                    radio.setUserData(split);
                    radio.setToggleGroup(metricRadioGroup);
                    radio.setSelected(split.equals(config.getSplittingStrategy()));
                    radio.setVisible(!isRegressionTree.getValue());
                    return radio;
                }));
        metricRadioGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                SplittingStrategy strategy = (SplittingStrategy) newValue.getUserData();
                config = config.withSplittingStrategy(strategy);
            }
        });

        isRegressionTree.addListener((observable, wasRegression, isRegression) -> {
            if (isRegression) {
                rbRegress.setSelected(true);
            } else {
                rbClassif.setSelected(true);
            }

            for (RadioButton rb : metricRbs.values()) {
                rb.setVisible(!isRegression);
            }
        });

        VBox vboxMetrics = new VBox();
        vboxMetrics.getChildren().addAll(metricRbs.values());
        vboxMetrics.setAlignment(Pos.TOP_LEFT);
        vboxMetrics.setSpacing(3);
        root.add(vboxMetrics, 1, 1);

        Button btnV = new Button("Valider");
        btnV.setOnAction(event -> s.close());
        root.setPadding(new Insets(20));

        hbox.setAlignment(Pos.TOP_CENTER);
        hbox.getChildren().add(btnV);
        hbox.setSpacing(35);
        hbox.setAlignment(Pos.CENTER_RIGHT);
        root.add(hbox, 1, 3);

        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setHgap(10);
        root.setVgap(10);
        s.setTitle("Parametrage");
        s.setScene(new Scene(root));
        return s;
    }
    
    /**
     * methode d impression de l arbre
     * @param node
     * @param s
     */
    private void impression(Node node,Stage s){   	
    	PrinterJob print = PrinterJob.createPrinterJob();
   		if (print == null) 
   			return;
   		boolean bol = print.showPrintDialog(s);
   		if (bol){
   			boolean printed = print.printPage(node);
   			if (printed) 
   				print.endJob();
   		}
   	}
}
