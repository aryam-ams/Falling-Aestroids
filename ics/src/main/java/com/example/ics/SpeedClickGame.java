package com.example.ics;

import java.util.*;
import java.io.*;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SpeedClickGame extends Application {
    private static final int OBJECT_RADIUS = 40;
    private static final int BOARD_WIDTH = 1200;
    private static final int BOARD_HEIGHT = 700;
    private static final int NUM_OBJECTS = 30;
    private static final int NUM_CONCURRENT_OBJECTS = 3;
    private SimpleIntegerProperty score = new SimpleIntegerProperty(0);
    private SortedSet<Integer> topScores = new TreeSet<>(Comparator.reverseOrder());
    private Pane gameBoard;
    private Label scoreLabel;
    private VBox gameOverBox;
    private ImageView tryAgainButton;
    private int objectsFallen = 0;
    private ImageView startButtonImage;
    private List<Image> planetImages;
    private List<Timeline> timelines = new ArrayList<>();
    private final List<Integer> planetValues = new ArrayList<>(Arrays.asList(10, 20, 30, 40));
    private Label gameTitleLabel;
    private Label gameOverLabel;
    private Label planetsScores;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        StackPane root = new StackPane();

        gameBoard = new Pane();
        gameOverBox = createGameOverBox();
        readTopScores();

        planetImages = new ArrayList<>();
        planetImages.add(new Image("https://i.postimg.cc/FsqXjhRQ/1.png"));
        planetImages.add(new Image("https://i.postimg.cc/pT73KZWJ/2.png"));
        planetImages.add(new Image("https://i.postimg.cc/nhk53ZVz/3.png"));
        planetImages.add(new Image("https://i.postimg.cc/50HR9CBY/4.png"));


        Image backgroundImage = new Image("https://i.postimg.cc/Rh7pJZ6F/background.gif");
        BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.REPEAT,
                BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);

        gameBoard.setBackground(new Background(background));
        gameBoard.setMinSize(BOARD_WIDTH, BOARD_HEIGHT);
        gameBoard.setMaxSize(BOARD_WIDTH, BOARD_HEIGHT);

        scoreLabel = new Label("Score : 0");
        scoreLabel.setStyle("-fx-font-size: 20; -fx-text-fill: white;-fx-font-weight: bold;");
        scoreLabel.textProperty().bind(score.asString("Score : %d"));
        scoreLabel.setTranslateY(-BOARD_HEIGHT / 2 + 30);
        scoreLabel.setVisible(false);


        Image startImage = new Image("https://i.postimg.cc/zvHZQQtQ/start.png");
        startButtonImage = new ImageView(startImage);
        startButtonImage.setFitHeight(300);
        startButtonImage.setFitWidth(300);
        startButtonImage.setTranslateY(100);
        startButtonImage.setOnMouseClicked(e -> startGame());


        Image gameTitle = new Image("https://i.postimg.cc/T361rjhx/title.png");
        ImageView imageView = new ImageView(gameTitle);
        gameTitleLabel = new Label();
        gameTitleLabel.setGraphic(imageView);
        gameTitleLabel.setTranslateY(-65);
        gameTitleLabel.setMouseTransparent(true);


        Image planetPic = new Image("https://i.postimg.cc/638cKxrB/planets.png");
        ImageView imageView2 = new ImageView(planetPic);
        imageView2.setFitWidth(250);
        imageView2.setFitHeight(200);
        planetsScores = new Label();
        planetsScores.setGraphic(imageView2);
        planetsScores.setTranslateY(-290);
        planetsScores.setTranslateX(470);
        planetsScores.setMouseTransparent(true);


        root.getChildren().addAll(gameBoard, scoreLabel, gameOverBox, startButtonImage);
        root.getChildren().add(gameTitleLabel);
        root.getChildren().add(planetsScores);
        planetsScores.setVisible(false);


        Scene scene = new Scene(root, BOARD_WIDTH, BOARD_HEIGHT);
        primaryStage.setTitle("Falling Asteroids");
        primaryStage.setScene(scene);
        primaryStage.show();

    }
    private VBox createGameOverBox() {
        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);
        box.setMinSize(BOARD_WIDTH, BOARD_HEIGHT);
        box.setMaxSize(BOARD_WIDTH, BOARD_HEIGHT);
        box.setStyle("-fx-background-color: rgba(0, 0, 0, 0.75);");

        Image gameOverTitle = new Image("https://i.postimg.cc/ydRc1mNb/gameOver.gif");
        ImageView imageOverView = new ImageView(gameOverTitle);
        gameOverLabel = new Label();
        gameOverLabel.setGraphic(imageOverView);
        imageOverView.setFitWidth(400);
        imageOverView.setFitHeight(150);
        gameOverLabel.setMouseTransparent(true);

        Image againImage = new Image("https://i.postimg.cc/wB9q111t/again.png");
        tryAgainButton = new ImageView(againImage);
        tryAgainButton.setFitHeight(300);
        tryAgainButton.setFitWidth(300);
        tryAgainButton.setTranslateY(-100);
        tryAgainButton.setOnMouseClicked(e -> startGame());

        VBox topScoresBox = new VBox(10);
        topScoresBox.setAlignment(Pos.CENTER);
        topScoresBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.5); -fx-padding: 15px;");

        box.getChildren().addAll(gameOverLabel, topScoresBox, tryAgainButton);
        box.setVisible(false);


        return box;
    }

    private void startGame() {
        gameBoard.getChildren().clear();
        score.set(0);
        objectsFallen = 0;
        gameOverBox.setVisible(false);
        startButtonImage.setVisible(false);
        gameTitleLabel.setVisible(false);
        planetsScores.setVisible(true);
        scoreLabel.setVisible(true);

        for (int i = 1; i <= NUM_CONCURRENT_OBJECTS; i++) {
            if (i == 1) {
                spawnObject(i + 1);
            }
            if (i == 2) {
                spawnObject(i + 1.5);
            }
            if (i == 3) {
                spawnObject(i + 2);
            }
        }
    }


    private void spawnObject(double speed) {
        if (objectsFallen < (NUM_OBJECTS-2)) {
            Random random = new Random();
            double x = random.nextInt((int) (BOARD_WIDTH - 4 * OBJECT_RADIUS)) + OBJECT_RADIUS;
            ImageView object = createObject(x, -OBJECT_RADIUS, OBJECT_RADIUS, speed);
            Label valueLabel = createValueLabel(object);
            object.setUserData(valueLabel);

            boolean overlap = false;

            for (Node node : gameBoard.getChildren()) {
                if (node instanceof ImageView && node.getBoundsInParent().intersects(object.getBoundsInParent())) {
                    overlap = true;
                    break;
                }
            }

            if (overlap) {
                spawnObject(speed);
            } else {
                gameBoard.getChildren().addAll(object, valueLabel);
                Timeline timeline = new Timeline();
                KeyValue kv = new KeyValue(object.translateYProperty(), BOARD_HEIGHT + OBJECT_RADIUS);
                KeyFrame kf = new KeyFrame(Duration.millis(2000 + (NUM_OBJECTS - speed) * 200), kv);
                timeline.getKeyFrames().add(kf);
                timeline.setOnFinished(e -> {
                    gameBoard.getChildren().removeAll(object, valueLabel);
                    objectsFallen++;
                    if (objectsFallen < (NUM_OBJECTS-2)) {
                        double nextSpeed = speed + NUM_CONCURRENT_OBJECTS ;
                        if (objectsFallen % 3 == 0) {
                            nextSpeed++;
                        }
                        spawnObject(nextSpeed);
                    }
                    else {
                        endGame();
                    }
                });
                timeline.play();
                timelines.add(timeline);
            }
        }
    }


    private ImageView createObject(double x, double y, double radius, double speed) {
        Image planetImage = planetImages.get((int) (Math.random() * planetImages.size()));
        int planetValue = planetValues.get(planetImages.indexOf(planetImage));
        ImageView object = new ImageView(planetImage);
        object.setFitWidth(radius * 5);
        object.setFitHeight(radius * 5);


        double maxObjectX = BOARD_WIDTH - 4 * radius;
        double minObjectX = radius;
        x = Math.max(minObjectX, Math.min(maxObjectX, x));

        object.setTranslateX(x - radius);
        object.setTranslateY(y - radius);
        object.setId(Double.toString(speed));
        object.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            score.set(score.get() + planetValue);
            ImageView imageView = (ImageView) event.getSource();
            Label valueLabel = (Label) imageView.getUserData();
            valueLabel.setOpacity(0); // set opacity to 0 to hide the value
            gameBoard.getChildren().remove(imageView);
            event.consume();
        });

        return object;
    }

    private Label createValueLabel(ImageView object) {
        int planetValue = planetValues.get(planetImages.indexOf(object.getImage()));
        Label valueLabel = new Label("+" + planetValue);
        valueLabel.setStyle("-fx-font-size: 20; -fx-text-fill: white; -fx-font-weight: bold;");


        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.BLACK);
        shadow.setOffsetX(2);
        shadow.setOffsetY(2);
        shadow.setRadius(4);


        valueLabel.setEffect(shadow);
        valueLabel.layoutXProperty().bind(object.translateXProperty().add(object.getBoundsInParent().getWidth() / 2).subtract(valueLabel.widthProperty().divide(2)));
        valueLabel.layoutYProperty().bind(object.translateYProperty().add(object.getBoundsInParent().getHeight() / 2).subtract(valueLabel.heightProperty().divide(2)));


        object.setOnMouseClicked(e -> {
            String text = "";
            if (planetValue == 40) {
                text = "Perfect!";
            } else if (planetValue == 30) {
                text = "Great!";
            } else if (planetValue == 20) {
                text = "Cool!";
            } else if (planetValue == 10) {
                text = "Good!";
            }

            Label feedbackLabel = new Label(text);
            feedbackLabel.setStyle("-fx-font-size: 16; -fx-text-fill: white; -fx-font-weight: bold;");
            feedbackLabel.layoutXProperty().bind(object.translateXProperty().add(object.getBoundsInParent().getWidth() / 2).subtract(feedbackLabel.widthProperty().divide(2)));
            feedbackLabel.layoutYProperty().bind(object.translateYProperty().subtract(20));
            gameBoard.getChildren().add(feedbackLabel);


            Timeline hideFeedbackTimeline = new Timeline(new KeyFrame(Duration.seconds(1), new KeyValue(feedbackLabel.opacityProperty(), 0)));
            hideFeedbackTimeline.play();


            valueLabel.setOpacity(0);
        });

        return valueLabel;
    }


    private void endGame() {
        topScores.add(score.get());

        if (topScores.size() > 5) {
            int lastScore = topScores.last();
            topScores.remove(lastScore);


            topScores.removeIf(s -> s < lastScore);
        }

        updateTopScores();
        gameOverBox.setVisible(true);
        planetsScores.setVisible(false);
        saveTopScores();
    }

    private void saveTopScores() {
        try {
            FileWriter fw = new FileWriter("topScores.txt");
            for (Integer score : topScores) {
                fw.write(score + "\n");
            }
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void readTopScores() {
        try {
            FileReader fr = new FileReader("topScores.txt");
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                topScores.add(Integer.parseInt(line));
            }
            br.close();
        } catch (FileNotFoundException e) {

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateTopScores() {
        VBox topScoresBox = (VBox) gameOverBox.getChildren().get(1);

        topScoresBox.getChildren().clear();
        int count = 0;
        for (Integer score : topScores) {
            if (count >= 5) {
                break;
            }
            Label scoreLabel = new Label("Score: " + score);
            scoreLabel.setStyle("-fx-font-size: 18; -fx-text-fill: black;-fx-font-weight: bold;");
            topScoresBox.getChildren().add(scoreLabel);
            count++;
        }
    }
}