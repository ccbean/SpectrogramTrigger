package spectrogramtrigger;

import processing.core.PApplet;

/**
 * Created by colin on 8/13/17.
 */
public class Cell {
    protected int gridX;
    protected int gridY;

    protected int screenX;
    protected int screenY;

    protected Cell(int gridX, int gridY, int screenX, int screenY){
        this.gridX = gridX;
        this.gridY = gridY;
        this.screenX = screenX;
        this.screenY = screenY;
    }

    public static Cell createFromScreen(int screenX, int screenY, int cellSize, int gridHeight, PApplet parent){
        int gridX = screenX / cellSize;
        int gridY = (parent.height - screenY) / cellSize;

        //System.out.println("Test " + gridY + " " +  gridHeight);
        if(gridY > gridHeight){
            return null;
        }
        Cell c = new Cell(gridX, gridY, screenX, screenY);
        return c;
    }


}
