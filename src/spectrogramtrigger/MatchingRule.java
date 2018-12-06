package spectrogramtrigger;

import processing.core.PApplet;

import java.util.ArrayList;
import java.util.LinkedList;

import oscP5.*;
import netP5.*;

/**
 * Created by colin on 8/13/17.
 */
public class MatchingRule {
    private PApplet parent;
    private ArrayList<Cell> cells;
    private int cellSize;
    private int gridHeight;
    private boolean matched;

    private int color;
    private int highlightColor;

    private static int drawOffset = 400;

    private NetAddress remoteLocation;
    private OscP5 oscP5;

    private Cell selectCell;

    public MatchingRule(PApplet parent, int cellSize, int gridHeight, int color){
        this.parent = parent;
        this.cells = new ArrayList<Cell>();

        this.cellSize = cellSize;
        this.gridHeight = gridHeight;

        this.color = color;
        this.highlightColor = parent.color(parent.red(color), parent.green(color), parent.blue(color), 128);

        oscP5 = new OscP5(parent, 3333);
        //remoteLocation = new NetAddress("127.0.0.1",12000);
        remoteLocation = new NetAddress("127.0.0.1",2222);
    }

    public void handleHighlight(int mouseX, int mouseY){
        Cell c = Cell.createFromScreen(mouseX, mouseY, cellSize, gridHeight, parent);
        if(c != null){
            if(selectCell != null){
                parent.stroke(0, 0);
                parent.fill(highlightColor);

                float startX = selectCell.gridX * cellSize;
                float startY = parent.height - selectCell.gridY * cellSize;
                float endX = c.gridX * cellSize;
                float endY = parent.height - c.gridY * cellSize;

                parent.rect(startX, startY, endX - startX, endY - startY);
            }
            else {
                parent.strokeWeight(4);
                parent.stroke(highlightColor);
                parent.point(c.gridX * cellSize, parent.height - c.gridY * cellSize);
            }
        }
    }

    public void selectionStart(int mouseX, int mouseY){
        selectCell = Cell.createFromScreen(mouseX, mouseY, cellSize, gridHeight, parent);
    }

    public void selectionEnd(int mouseX, int mouseY){
        Cell endCell = Cell.createFromScreen(mouseX, mouseY, cellSize, gridHeight, parent);

        int startX = Math.min(selectCell.gridX, endCell.gridX);
        int endX =  Math.max(selectCell.gridX, endCell.gridX);
        int startY = Math.min(selectCell.gridY, endCell.gridY);
        int endY =  Math.max(selectCell.gridY, endCell.gridY);

        for(int x = startX; x < endX; x++){
            for(int y = startY; y < endY; y++){
                addCell(new Cell(x, y, 0, 0));
            }
        }
        selectCell = null;
    }

    public void handleClick(int mouseX, int mouseY){
        Cell c = Cell.createFromScreen(mouseX, mouseY, cellSize, gridHeight, parent);
        addCell(c);
    }

    public void addCell(Cell c){
        if(c != null) {
            for(Cell testCell : cells){
                if(testCell.gridX == c.gridX && testCell.gridY == c.gridY){
                    return;
                }
            }
            cells.add(c);
        }
    }

    public void evaluate(LinkedList<float[]> bands){
        matched = false;
        float total = 0;
        for(Cell c: cells){
            if(c.gridX < bands.size()) {
                total += bands.get(c.gridX)[c.gridY];
            }
        }
        total = total / cells.size();
        if(total > 0) {
           // System.out.println("total avg " + total);
        }
        if(total > 1.0){
           // System.out.println("Match");
            matched = true;
            OscMessage myMessage = new OscMessage("/1/randomword");
            myMessage.add(1.0f);
            oscP5.send(myMessage, remoteLocation);
        }
    }

    public void draw(){
        //parent.stroke(255, 255, 0, 128);
        parent.strokeWeight(4);
        parent.stroke(color);
        for(Cell c : cells){
            parent.point(c.gridX * cellSize, parent.height - c.gridY * cellSize);
        }

        parent.strokeWeight(1.0f);
        parent.fill(0);
        parent.rect(parent.width - drawOffset, 0, 100, 100);

        if(matched){
            //parent.fill(255, 255, 0, 255);
            parent.fill(color);
            //parent.rect(0, 0, 200, 200);
            parent.rect(parent.width - drawOffset, 0, 100, 100);
        }
    }

    public void clear(){
        cells.clear();
        selectCell = null;
    }
}
