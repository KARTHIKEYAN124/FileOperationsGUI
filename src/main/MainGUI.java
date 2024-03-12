package main;

import java.io.*;
import java.util.*;
import java.util.logging.*;

import org.fxmisc.richtext.StyleClassedTextArea;

import file.operations.FileWrite;
import javafx.application.*;
import javafx.concurrent.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.*;


public class MainGUI extends Application {

	private static final Logger logger = Logger.getLogger(MainGUI.class.getName());
    private StyleClassedTextArea partitionTextArea1 = new StyleClassedTextArea();
    private StyleClassedTextArea partitionTextArea2 = new StyleClassedTextArea();
    private Label messageLabel = new Label("Message:");
    

    @Override
    public void start(Stage primaryStage) {
        initLogger();
        // Set the stage size based on screen width
        primaryStage.getIcons().add(new Image("file:src/images/exe_file_icon.png"));

        primaryStage.setWidth(900);
        primaryStage.setHeight(500);
        primaryStage.setResizable(true); // Allow resizing

        // Create menu bar for File 1
        MenuBar menuBarFile1 = createMenuBar(partitionTextArea1, partitionTextArea2);

        // Create vertical partitions
        VBox partition1 = createPartition("File 1", menuBarFile1, partitionTextArea1);
        VBox partition2 = createPartition("File 2", menuBarFile1, partitionTextArea2);
        VBox partition3 = createPartition3();

        HBox boxWithPartitions = new HBox(partition1, partition2, partition3);
        HBox.setHgrow(partition1, Priority.ALWAYS);
        HBox.setHgrow(partition2, Priority.ALWAYS);
        HBox.setHgrow(partition3, Priority.ALWAYS);
        boxWithPartitions.setSpacing(0); // Remove spacing between partitions
        boxWithPartitions.setAlignment(Pos.TOP_LEFT);

        // Set properties for message label
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
        primaryStage.setTitle("File Operations");

        // Add event handler to handle window close request
        primaryStage.setOnCloseRequest(event -> {
            boolean close = true; 
            if (!isFileSaved()) {
               
                close = showConfirmationDialog(primaryStage);
            }
            if (!close) {
                
                event.consume();
            }
        });
        primaryStage.show();
    }
    private boolean showConfirmationDialog(Stage primaryStage) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Close");
        alert.setHeaderText("Unsaved Changes");
        alert.setContentText("The files are not saved. Are you sure you want to close?");

        // Add buttons to the confirmation dialog
        ButtonType yesButton = new ButtonType("Yes");
        ButtonType noButton = new ButtonType("No");
        alert.getButtonTypes().setAll(yesButton, noButton);

        // Show the confirmation dialog and wait for user response
        alert.showAndWait();

        // Return true if the user clicks "Yes", false otherwise
        return alert.getResult() == yesButton;
    }
    
    private boolean isFileSaved() {
        // Check if the message label contains the "File saved successfully" message
        String successMessage = "File saved successfully.";
        return messageLabel.getText().equals(successMessage);
    }
    
private void initLogger() {
        try {
            // Create log file handler
            FileHandler fileHandler = new FileHandler("logger.log");
            // Add file handler to logger
            logger.addHandler(fileHandler);
            // Set log level (optional)
            logger.setLevel(Level.ALL);
        } catch (IOException e) {
            // Log initialization exception
            logger.log(Level.SEVERE, "Failed to initialize logger", e);
        }
    }

    private MenuBar createMenuBar(StyleClassedTextArea partitionTextArea1, StyleClassedTextArea partitionTextArea2) {
        // Create menu bar
        MenuBar menuBar = new MenuBar();

        // File menu
        Menu fileMenu = new Menu("File");
        MenuItem browseFile1MenuItem = new MenuItem("Browse and Select File 1");
        MenuItem browseFile2MenuItem = new MenuItem("Browse and Select File 2");
        MenuItem saveFile1MenuItem = new MenuItem("Save File 1 As");
        MenuItem saveFile2MenuItem = new MenuItem("Save File 2 As");

        // Set actions for menu items
        browseFile1MenuItem.setOnAction(event -> {
            browseAndSelectFile("File 1");
        });
        browseFile2MenuItem.setOnAction(event -> {
            browseAndSelectFile("File 2");
        });
        saveFile1MenuItem.setOnAction(event -> {
            saveOutput(partitionTextArea1);
        });
        saveFile2MenuItem.setOnAction(event -> {
            saveOutput(partitionTextArea2);
        });

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

    private void saveOutput(StyleClassedTextArea partitionTextArea) {
        try {
            String text = partitionTextArea.getText();
            List<String> lines = Arrays.asList(text.split("\n"));

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Output File");

            // Set initial directory
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

            // Set extension filter
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Text files (*.txt)", "*.txt");
            fileChooser.getExtensionFilters().add(extFilter);

            // Show save file dialog
            File outputFile = fileChooser.showSaveDialog(null);

            if (outputFile != null) {
                FileWrite.WriteLinesToFile(lines, outputFile.getAbsolutePath());
                messageLabel.setText("File saved successfully.");
            } else {
                messageLabel.setText("Output file not saved.");
            }
        } catch (Exception e) {
        	logger.log(Level.SEVERE, "Error occurred while saving output", e);
        }
    }

    private void browseAndSelectFile(String fileType) {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = null;

        if (fileType.equals("File 1") || fileType.equals("File 2")) {
            // For selecting input files
            fileChooser.setTitle("Select " + fileType + " File");
            selectedFile = fileChooser.showOpenDialog(null);
        } else if (fileType.equals("Save File 1")) {
            // For saving output file 1
            selectedFile = new File("output_file1.txt");
        } else if (fileType.equals("Save File 2")) {
            // For saving output file 2
            selectedFile = new File("output_file2.txt");
        }

        // Process the selected file
        if (selectedFile != null) {
            final File finalSelectedFile = selectedFile; // Final variable to use inside lambda

            Task<Void> loadTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    StringBuilder contentBuilder = new StringBuilder();
                    try (BufferedReader reader = new BufferedReader(new FileReader(finalSelectedFile))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            contentBuilder.append(line).append("\n");
                        }
                    } catch (IOException e) {
                    	logger.log(Level.SEVERE, "Error occurred while saving output", e);
                    }

                    String content = contentBuilder.toString();
                    String finalContent = content; // Final variable for lambda expression
                    Platform.runLater(() -> {
                        if (fileType.equals("File 1")) {
                            partitionTextArea1.replaceText(finalContent);
                        } else if (fileType.equals("File 2")) {
                            partitionTextArea2.replaceText(finalContent);
                        }
                    });
                    return null;
                }
            };


            Thread thread = new Thread(loadTask);
            thread.setDaemon(true);
            thread.start();
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
        HBox buttonsBox = createButtonsBox(partitionTextArea);

        // Create white scene with page numbers and horizontal/vertical scroll
        partitionTextArea.setParagraphGraphicFactory(line -> {
            Label label = new Label(Integer.toString(line + 1));
            label.setPadding(new Insets(0, 5, 0, 5)); // Add padding to improve visibility
            return label;
        });

        partitionTextArea.setStyle("-fx-background-color: white; -fx-border-color: black;");

        // Add line numbers
        ScrollPane scrollPane = createScrollPane(partitionTextArea);

        partition.getChildren().addAll(buttonsBox, scrollPane);

        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        return partition;
    }

    private HBox createButtonsBox(StyleClassedTextArea partitionTextArea) {
        HBox buttonsBox = new HBox();
        buttonsBox.setSpacing(5);

        // Add buttons
        Button cutButton = createButton("cut.png", () -> partitionTextArea.cut(), partitionTextArea);
        Button copyButton = createButton("copy.png", () -> partitionTextArea.copy(), partitionTextArea);
        Button pasteButton = createButton("paste.png", () -> partitionTextArea.paste(), partitionTextArea);
        Button leftAlignButton = createButton("left_align.png", () -> setAlignment(partitionTextArea, "left"), partitionTextArea);
        Button centerAlignButton = createButton("center.png", () -> setAlignment(partitionTextArea, "center"), partitionTextArea);
        Button rightAlignButton = createButton("right_align.png", () -> setAlignment(partitionTextArea, "right"), partitionTextArea);
        Button saveButton = createButton("save.png", () -> {
            saveFile(partitionTextArea);
        }, partitionTextArea);

        buttonsBox.getChildren().addAll(
                cutButton,
                copyButton,
                pasteButton,
                leftAlignButton,
                centerAlignButton,
                rightAlignButton,
                saveButton
        );

        return buttonsBox;
    }
    private void saveFile(StyleClassedTextArea partitionTextArea) {
        try {
            String text = partitionTextArea.getText();
            String defaultFilePath = "output.txt"; // Default file path
            
            File outputFile = new File(defaultFilePath);
            FileWrite.WriteLinesToFile(Collections.singletonList(text), outputFile.getAbsolutePath());
            messageLabel.setText("File saved successfully.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error occurred while saving file", e);
        }
    }
        

    private void setAlignment(StyleClassedTextArea partitionTextArea, String alignment) {
        String style;
        switch(alignment) {
            case "left":
                style = "-fx-text-alignment: left;";
                break;
            case "center":
                style = "-fx-text-alignment: center;";
                break;
            case "right":
                style = "-fx-text-alignment: right;";
                break;
            default:
                style = "";
        }
        partitionTextArea.setStyle(style);
    }
    private Button createButton(String imageName, Runnable action, StyleClassedTextArea partitionTextArea) {
        Button button = new Button();
        button.setPrefSize(30, 30);
        button.setGraphic(createImageView(imageName));
        button.setOnAction(event -> {
            partitionTextArea.requestFocus(); // Ensure the text area is focused
            action.run();
        });
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

    private ScrollPane createScrollPane(StyleClassedTextArea partitionTextArea) {
        // Create scroll pane with given content
        ScrollPane scrollPane = new ScrollPane(partitionTextArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS); 
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS); 

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
        VBox partition4 = createSubPartition4(partitionTextArea1, partitionTextArea2);
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
            sortLines(partitionTextArea, true, !checkBox.isSelected());
            messageLabel.setText("Sorted in ascending order.");
        });

        sortDescendingButton.setOnAction(event -> {
            sortLines(partitionTextArea, false, !checkBox.isSelected());
            messageLabel.setText("Sorted in descending order.");
        });

        keepDuplicatesButton.setOnAction(event -> {
            String text = partitionTextArea.getText();
            String[] lines = text.split("\\n");
            List<String> uniqueLines = !checkBox.isSelected() ? new ArrayList<>(Arrays.asList(lines)) : new ArrayList<>(new HashSet<>(Arrays.asList(lines)));
            String result = String.join("\n", uniqueLines);
            partitionTextArea.replaceText(result);
            messageLabel.setText("Duplicate lines kept.");
        });

        removeDuplicatesButton.setOnAction(event -> {
            String text = partitionTextArea.getText();
            String[] lines = text.split("\\n");
            Set<String> uniqueLines = !checkBox.isSelected() ? new HashSet<>(Arrays.asList(lines)) : new HashSet<>(Arrays.asList(lines));
            String result = String.join("\n", uniqueLines);
            partitionTextArea.replaceText(result);
            messageLabel.setText("Duplicate lines removed.");
        });

        // Event handler for checkbox
        checkBox.setOnAction(event -> {
            boolean caseSensitive = checkBox.isSelected();
            // Update the labels of the sort buttons based on the state of the checkbox
            sortAscendingButton.setText(caseSensitive ? "Sort Ascending " : "Sort Ascending");
            sortDescendingButton.setText(caseSensitive ? "Sort Descending " : "Sort Descending");
        });

        return subPartition;
    }

    private void sortLines(StyleClassedTextArea partitionTextArea, boolean ascending, boolean caseSensitive) {
        String text = partitionTextArea.getText();
        String[] lines = text.split("\\n");
        Comparator<String> comparator = caseSensitive ? String::compareTo : String.CASE_INSENSITIVE_ORDER;
        if (ascending) {
            Arrays.sort(lines, comparator);
        } else {
            Arrays.sort(lines, comparator.reversed());
        }
        String sortedText = String.join("\n", lines);
        partitionTextArea.replaceText(sortedText);
    }

    private VBox createSubPartition4(StyleClassedTextArea partitionTextArea1, StyleClassedTextArea partitionTextArea2) {
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

        // Event handler for button1: Remove entries from file 1 in file 2
        button1.setOnAction(event -> {
            String text1 = partitionTextArea1.getText();
            String text2 = partitionTextArea2.getText();
            List<String> lines1 = new ArrayList<>(Arrays.asList(text1.split("\\n")));
            List<String> lines2 = Arrays.asList(text2.split("\\n"));

            lines1.removeAll(lines2); // Remove entries from file 1 in file 2

            String result = String.join("\n", lines1);
            partitionTextArea1.replaceText(result);
            messageLabel.setText("Entries removed from file 1 that are in file 2.");
        });

        // Event handler for button2: Remove entries from file 1 not in file 2
        button2.setOnAction(event -> {
            String text1 = partitionTextArea1.getText();
            String text2 = partitionTextArea2.getText();
            List<String> lines1 = new ArrayList<>(Arrays.asList(text1.split("\\n")));
            List<String> lines2 = Arrays.asList(text2.split("\\n"));

            lines1.retainAll(lines2); // Remove entries from file 1 not in file 2

            String result = String.join("\n", lines1);
            partitionTextArea1.replaceText(result);
            messageLabel.setText("Entries removed from file 1 that are not in file 2.");
        });

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