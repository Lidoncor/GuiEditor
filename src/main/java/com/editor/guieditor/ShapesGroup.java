package com.editor.guieditor;

import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.List;


public class ShapesGroup extends Group {
    public List<Shape> shapes;
    Rectangle groupRect;
    private double mouseAnchorX;
    private double mouseAnchorY;

    public ShapesGroup(List<Shape> selectedShapes) {
        shapes = new ArrayList<>();
        shapes.addAll(selectedShapes);

        double xMin = Double.MAX_VALUE;
        double yMin = Double.MAX_VALUE;

        double xMax = -Double.MAX_VALUE;
        double yMax = -Double.MAX_VALUE;

        for (Shape sh : shapes) {
            xMin = Math.min(xMin, sh.getBoundsInParent().getMinX());
            yMin = Math.min(yMin, sh.getBoundsInParent().getMinY());
            xMax = Math.max(xMax, sh.getBoundsInParent().getMaxX());
            yMax = Math.max(yMax, sh.getBoundsInParent().getMaxY());

            if (sh.getOnMousePressed() != null) sh.setOnMousePressed(null);
            if (sh.getOnMouseDragged() != null) sh.setOnMouseDragged(null);
        }

        double width = xMax - xMin;
        double height = yMax - yMin;

        width += 10;
        height += 10;

        groupRect = new Rectangle(width, height, Color.TRANSPARENT);
        groupRect.setStroke(Color.BLACK);
        groupRect.setY(yMin - 5);
        groupRect.setX(xMin - 5);

        groupRect.setOnMousePressed(mouseEvent -> {
            mouseAnchorX = mouseEvent.getX();
            mouseAnchorY = mouseEvent.getY();

        });

        final boolean[] first = new boolean[shapes.size()];
        final double[] deltaX = new double[shapes.size()];
        final double[] deltaY = new double[shapes.size()];

        groupRect.setOnMouseDragged(mouseEvent -> {
            groupRect.setLayoutX(mouseEvent.getSceneX() - mouseAnchorX);
            groupRect.setLayoutY(mouseEvent.getSceneY() - mouseAnchorY);

            for (Shape sh : shapes) {
                if (sh == groupRect) continue;

                double inPointX, inPointY;
                inPointX = groupRect.getBoundsInParent().getMinX() - (sh.getBoundsInParent().getMinX() - sh.getLayoutX());
                inPointY = groupRect.getBoundsInParent().getMinY() - (sh.getBoundsInParent().getMinY() - sh.getLayoutY());

                if (!first[shapes.indexOf(sh)]) {
                    deltaX[shapes.indexOf(sh)] = sh.getLayoutX() - inPointX;
                    deltaY[shapes.indexOf(sh)] = sh.getLayoutY() - inPointY;
                    first[shapes.indexOf(sh)] = true;
                }

                sh.setLayoutX(inPointX + deltaX[shapes.indexOf(sh)]);
                sh.setLayoutY(inPointY + deltaY[shapes.indexOf(sh)]);
            }
        });

        shapes.add(groupRect);

        getChildren().addAll(shapes);

    }

}
