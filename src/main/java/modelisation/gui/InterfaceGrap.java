package modelisation.gui;


import javafx.application.Application;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
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
 * @author eboma
 */

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
        Button selectC2 = new Button("Supprimer");
        Button validerS2 = new Button("Annuler");
        treeTolbar.setPrefHeight(20);
        treeTolbar.getItems().addAll(selectC2, validerS2);

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

    private boolean checkColumnSelections() {
        Column idColumn = tbleViewSelect.getIdColumn(), targetColumn = tbleViewSelect.getTargetColumn();
        if (idColumn == null || targetColumn == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            // TODO: translate labels?
            alert.setTitle("Oops");
            alert.setHeaderText("Bad choice");
            alert.setContentText("Choose target and ID columns");

            alert.showAndWait();
            return false;
        }

        if (tbleViewSelect.getDataColumnIndexes().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            // TODO: translate labels?
            alert.setTitle("Oops");
            alert.setHeaderText("Bad choice");
            alert.setContentText("Choose at least one data column!");

            alert.showAndWait();
            return false;
        }

        return true;
    }

    private DecisionTree buildTree(Column targetColumn, Column idColumn) {
        System.out.println("building tree using " + config.getSplittingStrategy().getName());
        DecisionTreeBuilder treeBuilder = new DecisionTreeBuilder(
                data,
                idColumn.getIndex(),
                targetColumn.getIndex(),
                tbleViewSelect.getDataColumnIndexes(),
                config
        );
        return treeBuilder.buildTree();
    }

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
    }

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

        imprimer.setOnAction(evt -> {
            if (!checkColumnSelections()) {
                return;
            }
            treeView.setTree(buildTree(tbleViewSelect.getTargetColumn(), tbleViewSelect.getIdColumn()));
        });

        toolbar.setPrefHeight(55);
        toolbar.getItems().addAll(ouvrir, enregistre, parametrage, imprimer);
        return toolbar;
    }

    private Stage buildParametrage() {
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
        return s;
    }
}
