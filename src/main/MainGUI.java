package main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import org.fxmisc.richtext.EditActions;
import org.fxmisc.richtext.StyleClassedTextArea;
import org.fxmisc.flowless.VirtualizedScrollPane;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import file.operations.FileRead;
import file.operations.FileWrite;
import fileprocessors.Sorter;

public class MainGUI extends Application {

    private StyleClassedTextArea partitionTextArea1 = new StyleClassedTextArea();
    private StyleClassedTextArea partitionTextArea2 = new StyleClassedTextArea();

    @Override
    public void start(Stage primaryStage) {
        // Set the stage size
        primaryStage.setWidth(1000);
        primaryStage.setHeight(600);

        // Create menu bar for File 1
        MenuBar menuBarFile1 = createMenuBar(partitionTextArea1, partitionTextArea2);

        // Create vertical partitions
        VBox partition1 = createPartition("File 1", menuBarFile1, partitionTextArea1);
        VBox partition2 = createPartition("File 2", menuBarFile1, partitionTextArea2);
        VBox partition3 = createPartition3();

        // Create scroll panes for partitions
        ScrollPane scrollPane1 = createScrollPane(partition1, true); 
        ScrollPane scrollPane2 = createScrollPane(partition2, true); 
        ScrollPane scrollPane3 = createScrollPane(partition3, false); 

        // Create box with three partitions
        HBox boxWithPartitions = new HBox(scrollPane1, scrollPane2, scrollPane3);

        // Create message bar
        Label messageLabel = new Label("Message:");
        messageLabel.setMaxWidth(Double.MAX_VALUE);
        messageLabel.setStyle("-fx-background-color: lightgray;");
        messageLabel.setPadding(new Insets(5));

        // Create VBox for the entire layout
        VBox root = new VBox(menuBarFile1, boxWithPartitions, messageLabel);
        VBox.setVgrow(boxWithPartitions, Priority.ALWAYS); 
        VBox.setVgrow(messageLabel, Priority.ALWAYS); 

        // Create the scene and set it in the stage
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("JavaFX Application");
        primaryStage.show();
    }

    private MenuBar createMenuBar(StyleClassedTextArea partitionTextArea1, StyleClassedTextArea partitionTextArea2) {
        // Create menu bar
        MenuBar menuBar = new MenuBar();

        // File menu
        Menu fileMenu = new Menu("File");
        MenuItem browseFile1MenuItem = new MenuItem("Browse and Select File 1");
        MenuItem browseFile2MenuItem = new MenuItem("Browse and Select File 2");
        MenuItem saveFile1MenuItem = new MenuItem("Save File 1");
        MenuItem saveFile2MenuItem = new MenuItem("Save File 2");

        // Set actions for menu items
        browseFile1MenuItem.setOnAction(event -> browseAndSelectFile("File 1"));
        browseFile2MenuItem.setOnAction(event -> browseAndSelectFile("File 2"));
        saveFile1MenuItem.setOnAction(event -> saveOutput(partitionTextArea1, "output.txt"));
        saveFile2MenuItem.setOnAction(event -> saveOutput(partitionTextArea2, "output.txt"));

        // Add menu items to the file menu
        fileMenu.getItems().addAll(
                browseFile1MenuItem,
                browseFile2MenuItem,
                new SeparatorMenuItem(),
                saveFile1MenuItem,
                saveFile2MenuItem
        );

        // Help menu
        Menu helpMenu = new Menu("Help");
        MenuItem aboutMenuItem = new MenuItem("About");

        helpMenu.getItems().add(aboutMenuItem);

        // Add menus to menu bar
        menuBar.getMenus().addAll(fileMenu, helpMenu);
        return menuBar;
    }

    private void saveOutput(StyleClassedTextArea partitionTextArea, String filePath) {
        try {
            List<String> lines = Arrays.asList(partitionTextArea.getText().split("\n"));
            FileWrite.WriteLinesToFile(lines, filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void browseAndSelectFile(String fileType) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select " + fileType + " File");
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            try {
                if (fileType.equals("File 1")) {
                    partitionTextArea1.replaceText(String.join("\n", FileRead.readLinesFromFile(selectedFile.getAbsolutePath())));
                } else if (fileType.equals("File 2")) {
                    partitionTextArea2.replaceText(String.join("\n", FileRead.readLinesFromFile(selectedFile.getAbsolutePath())));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private VBox createPartition(String title, MenuBar menuBar, StyleClassedTextArea partitionTextArea) {
        // Create VBox for a partition
        VBox partition = new VBox();
        partition.setSpacing(10);

        // Add menu bar
        partition.getChildren().add(menuBar);

        // Add title label
        Label titleLabel = new Label(title);
        partition.getChildren().add(titleLabel);

        // Add buttons HBox
        HBox buttonsBox = createButtonsBox();

        // Create white scene with page numbers and horizontal/vertical scroll
        partitionTextArea.setParagraphGraphicFactory(line -> {
            Label label = new Label(Integer.toString(line + 1));
            label.setPadding(new Insets(0, 5, 0, 5)); // Add padding to improve visibility
            return label;
        });

        partitionTextArea.setPrefSize(400, 300); // Set preferred size as needed
        partitionTextArea.setStyle("-fx-background-color: white; -fx-border-color: black;");

        // Add line numbers
        VirtualizedScrollPane<StyleClassedTextArea> scrollPane = new VirtualizedScrollPane<>(partitionTextArea);

        partition.getChildren().addAll(buttonsBox, scrollPane);

        VBox.setVgrow(scrollPane, Priority.ALWAYS); 

        return partition;
    }

    private HBox createButtonsBox() {
        HBox buttonsBox = new HBox();
        buttonsBox.setSpacing(5);

        // Add buttons
        buttonsBox.getChildren().addAll(
                createButton("cut.png"),
                createButton("copy.png"),
                createButton("paste.png"),
                createButton("font.png"),
                createButton("increase_font_size.png"),
                createButton("decrease_font_size.png"),
                createButton("left_align.png"),
                createButton("center.png"),
                createButton("right_align.png")
        );

        return buttonsBox;
    }

    private Button createButton(String imageName) {
        Button button = new Button();
        button.setPrefSize(30, 30); 
        button.setGraphic(createImageView(imageName)); 
        button.setText(null); 
        return button;
    }

    private ImageView createImageView(String imageName) {
        String imagePath = "src/images/" + imageName;
        File file = new File(imagePath);
        Image image = new Image(file.toURI().toString());
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(16); 
        imageView.setFitHeight(16); 
        return imageView;
    }

    private ScrollPane createScrollPane(VBox content, boolean horizontalAndVertical) {
        // Create scroll pane with given content
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        
        if (horizontalAndVertical) {
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS); 
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS); 
        } else {
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); 
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); 
        }
        
        return scrollPane;
    }

    private VBox createPartition3() {
        // Create VBox for partition 3
        VBox partition3 = new VBox();
        partition3.setSpacing(10);

        // Create sub-partitions for partition 3
        VBox subPartition1 = createSubPartition("File 1", partitionTextArea1);
        VBox subPartition2 = createSubPartition("File 2", partitionTextArea2);

        // Add sub-partitions to partition 3
        HBox headingsBox = new HBox(subPartition1, subPartition2); 
        partition3.getChildren().add(headingsBox);

        // Add horizontal partition line
        partition3.getChildren().add(createHorizontalPartition());

        // Add partition 4
        VBox partition4 = createSubPartition4(); 
        partition3.getChildren().add(partition4); 

        return partition3;
    }

    private VBox createSubPartition(String heading, StyleClassedTextArea partitionTextArea) {
        // Create VBox for a sub-partition
        VBox subPartition = new VBox();
        subPartition.setSpacing(10);

        // Add heading label
        Label headingLabel = new Label(heading);
        subPartition.getChildren().add(headingLabel);

        // Add buttons
        Button sortAscendingButton = new Button("Sort Ascending");
        Button sortDescendingButton = new Button("Sort Descending");
        Button keepDuplicatesButton = new Button("Keep Duplicates");
        Button removeDuplicatesButton = new Button("Remove Duplicates");
        CheckBox checkBox = new CheckBox("Case Sensitive");

        // Add buttons and checkbox to sub-partition
        subPartition.getChildren().addAll(
                sortAscendingButton,
                sortDescendingButton,
                keepDuplicatesButton,
                removeDuplicatesButton,
                checkBox
        );

        // Add a separator
        Separator separator = new Separator(Orientation.HORIZONTAL);
        subPartition.getChildren().add(separator);

        // Event handlers for buttons
        sortAscendingButton.setOnAction(event -> {
            String text = partitionTextArea.getText();
            boolean caseSensitive = checkBox.isSelected();
            try {
				Sorter.SortAsc(text, "");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
            // Get updated text from the text area
            partitionTextArea.replaceText(partitionTextArea.getText());
        });

        sortDescendingButton.setOnAction(event -> {
            String text = partitionTextArea.getText();
            boolean caseSensitive = checkBox.isSelected();
            try {
				Sorter.SortDesc(text, "");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
            // Get updated text from the text area
            partitionTextArea.replaceText(partitionTextArea.getText());
        });

        keepDuplicatesButton.setOnAction(event -> {
            // Add logic for keeping duplicates directly here
            // Get updated text from the text area
            partitionTextArea.replaceText(partitionTextArea.getText());
        });

        removeDuplicatesButton.setOnAction(event -> {
            // Add logic for removing duplicates directly here
            // Get updated text from the text area
            partitionTextArea.replaceText(partitionTextArea.getText());
        });

        return subPartition;
    }


    private VBox createSubPartition4() {
        // Create VBox for sub-partition 4
        VBox subPartition4 = new VBox();
        subPartition4.setSpacing(10);
        
        Label headingLabel = new Label("Together");
        subPartition4.getChildren().add(headingLabel);

        // Add buttons
        Button button1 = new Button("Remove entries from file 1 in file 2");
        Button button2 = new Button("Remove entries from file 1 not in file 2");

        // Add buttons to sub-partition 4
        subPartition4.getChildren().addAll(button1, button2);

        return subPartition4;
    }

    private VBox createHorizontalPartition() {
        // Create HBox for horizontal partition
        HBox horizontalPartition = new HBox();
        horizontalPartition.setStyle("-fx-border-width: 1 0 0 0; -fx-border-color: black;");
        return new VBox(horizontalPartition);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
