package spectrogramtrigger;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;
import processing.core.PApplet;
import ddf.minim.analysis.*;
import ddf.minim.*;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by colin on 8/2/17.
 */
public class SpectrogramUI extends PApplet{

    private Minim minim;
    private AudioInput in;
    private FFT fft;

    private int bandSize = 200;

    private LinkedList<float[]> bands = new LinkedList<float[]>();
    private ArrayList<MatchingRule> rules = new ArrayList<MatchingRule>();
    private MatchingRule currentRule;

    private Cell highlightCell;

    public void settings(){
        //size(1024, 600);
        fullScreen(P3D, 1);
    }

    public void setup(){
        minim = new Minim(this);
        in = minim.getLineIn(Minim.STEREO, 512);
        fft = new FFT(in.bufferSize(), in.sampleRate());
        fft.linAverages(64);

        int color = color(255, 255, 0, 255);
        MatchingRule m = new MatchingRule(this, 5, fft.specSize(), color);
        rules.add(m);
        currentRule = m;

        strokeWeight(4);
        strokeCap(SQUARE);

        cursor(CROSS);
    }

    public void draw(){
        background(0);
        fft.forward(in.mix);

        float[] currentBands = new float[fft.specSize()];
        for(int i = 0; i < fft.specSize(); i++){
            currentBands[i] = fft.getBand(i);
        }
        bands.add(currentBands);
        //System.out.println(currentBands[0]);

        if(bands.size() > bandSize){
            bands.pollFirst();
        }

        //stroke(255, 255, 255, 0);

        strokeWeight(4);

        int inc = 5;
        int currentX = 0;

        //for(int i = 0; i < bandSize; i++) {
        for(float[] drawBands : bands){
            for (int j = 0; j < fft.specSize(); j++) {
                //fill(PApplet.parseInt(map(drawBands[j] * 32, 0, 60, 0, 255)), 0, 0, 255);
                //quad(currentX, height - j*inc, currentX + inc, height - j * inc, currentX+inc, height - (j * inc) + inc, currentX, height - (j* inc)+inc);

                stroke(PApplet.parseInt(map(drawBands[j] * 32, 0, 60, 0, 255)), 0, 0, 255);

                point(currentX, height - j * inc );

                //quad(i*inc,0,i*inc,inc,(i*inc)+inc,inc,(i*inc)+inc,0);
                //quad(0,i*inc,inc,i*inc, inc,(i*inc)+inc,0,(i*inc)+inc);
            }
            currentX += inc;

        }

        //currentRule.evaluate(bands);

        //currentRule.draw();
        for(MatchingRule r: rules){
            r.evaluate(bands);
            r.draw();
        }

        if(currentRule != null){
            currentRule.handleHighlight(mouseX, mouseY);
        }

        strokeWeight(1);
        stroke(255);
        line(bandSize * inc, 0, bandSize * inc, height);
        fill(255);
        textSize(30);
        text(frameRate, width - 175, height - 50);
    }

    public void mouseClicked(){
        if(currentRule != null){
            currentRule.handleClick(mouseX, mouseY);
        }
    }

    public void mousePressed(){
        if(currentRule != null){
            currentRule.selectionStart(mouseX, mouseY);
        }
    }

    public void mouseReleased(){
        if(currentRule != null){
            currentRule.selectionEnd(mouseX, mouseY);
        }
    }

    public void keyPressed(){
        System.out.println("Test pressed " + key + " " + (key == 'd') + " " + currentRule);
        if(key == 'd' && currentRule != null){
            System.out.println("Clearing");
            currentRule.clear();
        }
    }

    public static void main(String[] args){
        PApplet.main("spectrogramtrigger.SpectrogramUI");
    }
}
