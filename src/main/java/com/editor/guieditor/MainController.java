package com.editor.guieditor;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;

import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import jfxtras.labs.util.event.MouseControlUtil;
import org.controlsfx.dialog.FontSelectorDialog;

import javax.imageio.ImageIO;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import static javafx.embed.swing.SwingFXUtils.fromFXImage;

public class MainController implements Initializable {
    @FXML
    private ColorPicker ColorPicker;
    @FXML
    private AnchorPane MainAnchorPane;
    @FXML
    private ToggleButton EllipseBtn;
    @FXML
    private ToggleButton RectangleBtn;
    @FXML
    private Button FontBtn;
    @FXML
    private ToggleButton TextBtn;
    @FXML
    private ToggleButton GroupBtn;
    @FXML
    private Button PictureBrn;

    private double mouseAnchorX, mouseAnchorY;
    private double initialX, initialY;

    private final List<Shape> selectedShapes = new ArrayList<>();
    private final Group mainShapes = new Group();
    private final ToggleGroup ToggleBtnGroup = new ToggleGroup();

    FontSelectorDialog FontDialog = new FontSelectorDialog(null);
    Optional<Font> NewFont = Optional.ofNullable(Font.font("Arial", 26));
    TextInputDialog TextDialog = new TextInputDialog();
    Optional<String> NewString;
    FileChooser fileChooser = new FileChooser();

    private EventHandler<MouseEvent> mouseDragHandler;
    private EventHandler<MouseEvent> mousePressedHandler;

    private final static int MARGIN_RESIZE = 5;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        MainAnchorPane.getChildren().add(mainShapes);

        EllipseBtn.setToggleGroup(ToggleBtnGroup);
        RectangleBtn.setToggleGroup(ToggleBtnGroup);
        GroupBtn.setToggleGroup(ToggleBtnGroup);
        TextBtn.setToggleGroup(ToggleBtnGroup);

        TextDialog.setHeaderText("Enter the text");
        TextDialog.setContentText("Text: ");

        MainAnchorPane.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                mainShapes.getChildren().remove(mouseEvent.getTarget());
                for (Node node : MainAnchorPane.getChildren()) {
                    if (node instanceof ShapesGroup) {
                        ShapesGroup temp = (ShapesGroup) node;
                        if ((temp.shapes.get(temp.shapes.size() - 1)) == mouseEvent.getTarget()) {
                            for (int i = 0; i < temp.shapes.size() - 1; i++) {
                                makeDraggable(temp.shapes.get(i));
                                mainShapes.getChildren().add(temp.shapes.get(i));
                            }
                            MainAnchorPane.getChildren().remove(temp);
                        }
                    }
                }
            }
        });
    }

    @FXML
    void EllipseBtnClick(ActionEvent event) {
        if (ButtonReset(EllipseBtn)) return;
        else Reset();

        final Ellipse[] NewEllipse = new Ellipse[1];
        mousePressedHandler = new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                NewEllipse[0] = new Ellipse();
                makeDraggable(NewEllipse[0]);
                NewEllipse[0].setFill(ColorPicker.getValue());
                mainShapes.getChildren().add(NewEllipse[0]);
                mouseAnchorX = event.getX();
                mouseAnchorY = event.getY();
                NewEllipse[0].setCenterX(mouseAnchorX);
                NewEllipse[0].setCenterY(mouseAnchorY);
                NewEllipse[0].setRadiusX(0);
                NewEllipse[0].setRadiusY(0);
            }
        };

        mouseDragHandler = new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                NewEllipse[0].setCenterX(Math.min(event.getX(), mouseAnchorX));
                NewEllipse[0].setRadiusX(Math.abs(event.getX() - mouseAnchorX));
                NewEllipse[0].setCenterY(Math.min(event.getY(), mouseAnchorY));
                NewEllipse[0].setRadiusY(Math.abs(event.getY() - mouseAnchorY));
            }
        };

        MainAnchorPane.addEventHandler(MouseEvent.MOUSE_PRESSED, mousePressedHandler);
        MainAnchorPane.addEventHandler(MouseEvent.MOUSE_DRAGGED, mouseDragHandler);
    }

    @FXML
    void RectangleBtnClick(ActionEvent event) {
        if (ButtonReset(RectangleBtn)) return;
        else Reset();

        final Rectangle[] NewRectangle = new Rectangle[1];
        mousePressedHandler = new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                NewRectangle[0] = new Rectangle();
                makeDraggable(NewRectangle[0]);
                NewRectangle[0].setFill(ColorPicker.getValue());
                mainShapes.getChildren().add(NewRectangle[0]);
                mouseAnchorX = event.getX();
                mouseAnchorY = event.getY();
                NewRectangle[0].setX(mouseAnchorX);
                NewRectangle[0].setY(mouseAnchorY);
                NewRectangle[0].setWidth(0);
                NewRectangle[0].setHeight(0);
            }
        };

        mouseDragHandler = new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                NewRectangle[0].setX(Math.min(event.getX(), mouseAnchorX));
                NewRectangle[0].setWidth(Math.abs(event.getX() - mouseAnchorX));
                NewRectangle[0].setY(Math.min(event.getY(), mouseAnchorY));
                NewRectangle[0].setHeight(Math.abs(event.getY() - mouseAnchorY));
            }
        };

        MainAnchorPane.addEventHandler(MouseEvent.MOUSE_PRESSED, mousePressedHandler);
        MainAnchorPane.addEventHandler(MouseEvent.MOUSE_DRAGGED, mouseDragHandler);
    }

    @FXML
    void TextBtnClick(ActionEvent event) {
        if (ButtonReset(TextBtn)) return;
        else Reset();

        mousePressedHandler = new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                NewString = TextDialog.showAndWait();
                if (NewString.isPresent() && NewFont.isPresent()) {
                    Text NewText = new Text(NewString.get());
                    NewText.setFill(ColorPicker.getValue());
                    NewText.setFont(NewFont.get());
                    mouseAnchorX = event.getX();
                    mouseAnchorY = event.getY();
                    NewText.setX(mouseAnchorX);
                    NewText.setY(mouseAnchorY);
                    makeDraggable(NewText);
                    mainShapes.getChildren().add(NewText);
                }
            }
        };

        MainAnchorPane.addEventHandler(MouseEvent.MOUSE_PRESSED, mousePressedHandler);
    }

    @FXML
    void GroupBtnClick(ActionEvent event) {
        if (ButtonReset(GroupBtn)) return;
        else Reset();

        Rectangle selectionRect = new Rectangle(10, 10, Color.DEEPSKYBLUE);
        selectionRect.setOpacity(0.5);
        selectionRect.setStroke(Color.DODGERBLUE);

        EventHandler<MouseEvent> mouseDragHandlerSelect = new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                for (Node node : mainShapes.getChildren()) {
                    Shape shape = (Shape) node;
                    if (selectionRect.getBoundsInParent().intersects(shape.getBoundsInParent())) {
                        if(!selectedShapes.contains(shape)) {
                            selectedShapes.add(shape);
                        }
                    } else {
                        selectedShapes.remove(shape);
                    }
                }
            }
        };

        EventHandler<MouseEvent> mouseReleaseHandlerSelect = new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                MainAnchorPane.setOnMouseDragged(null);
                MainAnchorPane.setOnMousePressed(null);
                MainAnchorPane.setOnMouseReleased(null);
                GroupBtn.setSelected(false);
                ShapesGroup newGroup = new ShapesGroup(selectedShapes);
                selectedShapes.clear();
                MainAnchorPane.getChildren().add(newGroup);
            }
        };

        MouseControlUtil.addSelectionRectangleGesture(MainAnchorPane, selectionRect, mouseDragHandlerSelect, null, mouseReleaseHandlerSelect);
    }

    @FXML
    void PictureBtnClick(ActionEvent event) throws IOException {
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("png files (*.png)", "*.png"));
        File file = fileChooser.showSaveDialog(null);
        if(file != null){
            WritableImage writableImage = new WritableImage((int) MainAnchorPane.getWidth(), (int)MainAnchorPane.getHeight());
            MainAnchorPane.snapshot(null, writableImage);
            ImageIO.write(fromFXImage(writableImage, null), "png", file);
        }
    }

    @FXML
    void FontBtnClick(ActionEvent event) {
        NewFont = FontDialog.showAndWait();
    }

    void makeDraggable(Shape shape) {
        shape.setOnMouseClicked(mouseEvent -> {
            shape.toFront();
        });

        shape.setOnMousePressed(mouseEvent -> {
            var selectedToggle = ToggleBtnGroup.getSelectedToggle();
            if (selectedToggle != null) {
                selectedToggle.setSelected(false);
                Reset();
            }
            shape.toFront();
            mouseAnchorX = mouseEvent.getX();
            mouseAnchorY = mouseEvent.getY();

            initialX = mouseEvent.getSceneX();
            initialY = mouseEvent.getSceneY();
        });

        shape.setOnMouseDragged(mouseEvent -> {
            if (!shape.getCursor().equals(Cursor.DEFAULT)) {
                if (shape.getCursor().equals(Cursor.SE_RESIZE) && shape instanceof Rectangle) {
                    ((Rectangle) shape).setHeight(((Rectangle) shape).getHeight() + (mouseEvent.getSceneY() - initialY));
                    initialY = mouseEvent.getSceneY();
                    ((Rectangle) shape).setWidth(((Rectangle) shape).getWidth() + (mouseEvent.getSceneX() - initialX));
                    initialX = mouseEvent.getSceneX();
                } else if (shape.getCursor().equals(Cursor.S_RESIZE) && shape instanceof Ellipse) {
                    ((Ellipse) shape).setRadiusY(((Ellipse) shape).getRadiusY() + (mouseEvent.getSceneY() - initialY));
                    initialY = mouseEvent.getSceneY();
                } else if (shape.getCursor().equals(Cursor.E_RESIZE) && shape instanceof Ellipse) {
                    ((Ellipse) shape).setRadiusX(((Ellipse) shape).getRadiusX() + (mouseEvent.getSceneX() - initialX));
                    initialX = mouseEvent.getSceneX();
                }
            } else {
                shape.setLayoutX(mouseEvent.getSceneX() - mouseAnchorX);
                shape.setLayoutY(mouseEvent.getSceneY() - mouseAnchorY);
            }
        });

        shape.setOnMouseMoved(mouseEvent -> {
            if (mouseEvent.getY() >= (shape.getLayoutBounds().getMaxY() - MARGIN_RESIZE) && mouseEvent.getX() >= (shape.getLayoutBounds().getMaxX() - MARGIN_RESIZE)) {
                shape.setCursor(Cursor.SE_RESIZE);
            } else if (mouseEvent.getY() >= (shape.getLayoutBounds().getMaxY() - MARGIN_RESIZE) && shape instanceof Ellipse) {
                shape.setCursor(Cursor.S_RESIZE);
            } else if (mouseEvent.getX() >= (shape.getLayoutBounds().getMaxX() - MARGIN_RESIZE) && shape instanceof Ellipse) {
                shape.setCursor(Cursor.E_RESIZE);
            } else {
                shape.setCursor(Cursor.DEFAULT);
            }
        });
    }

    void Reset() {
        try {
            MainAnchorPane.removeEventHandler(MouseEvent.MOUSE_PRESSED, mousePressedHandler);
            MainAnchorPane.removeEventHandler(MouseEvent.MOUSE_DRAGGED, mouseDragHandler);
        } catch (Exception ignored) {};
    }

    Boolean ButtonReset(ToggleButton btn) {
        if (!btn.isSelected()) {
            Reset();
            return true;
        }
        else return false;
    }
}
