package C18395266;

import processing.core.PApplet;
import ddf.minim.*;
import ddf.minim.analysis.*;

public class Visuals extends PApplet {

    Minim minim;
    AudioPlayer music;
    FFT fft;

    float low = 0;
    float mid = 0;
    float high = 0;
    float sLow = 0.05f; 
    float sMid = 0.15f;  
    float sHigh = 0.15f;   
    float oldLow = low;
    float oldMid = mid;
    float oldHigh = high;

    float decrease = 80;

    private int distanceCubeOffset = 200;
    private int distanceBetweenCubes = 55;
    private double speedMultiplier = 0.3;
    private int size = 4;

    private boolean vertRotation = false;
    private boolean horizRotation = false;
    private boolean diagRotation = false;

    private boolean drawCube = false;
    private boolean drawSpiral = false;

    private float pos1;
    private float pos2;
    private float pos3;
    private float speed;

    int frequencyOfWalls = 800;
    Wall[] surface;

int colorFromOffset(int offset) {
    return (int) ((distanceCubeOffset+offset) / (distanceCubeOffset*2.0)*255);
}

public void drawDimensionBox() {

    oldLow = low;
    oldMid = mid;
    oldHigh = high;
    
    low = 0;
    mid = 0;
    high = 0;
    
    fft.forward(music.mix);

    for (int i = 0; i < sLow*fft.specSize(); i++) low += fft.getBand(i);
    for (int i = (int) (sLow*fft.specSize()); i < sMid*fft.specSize(); i++) mid += fft.getBand(i);
    for (int i = (int) (sMid*fft.specSize()); i < sHigh*fft.specSize(); i++) high += fft.getBand(i);

    if (oldLow > low) low = oldLow - decrease;
    if (oldMid > mid) mid = oldMid - decrease;
    if (oldHigh > high) high = oldHigh - decrease;

    final float total = (float) (0.66 * low + 0.8 * mid + 1 * high);

    //add minor background flicker
    background(low / 100, mid / 100, high / 100);
    
    for (int i = 0; i < frequencyOfWalls; i++) {
        final float intensity = fft.getBand(i % ((int) (fft.specSize() * sHigh)));
        surface[i].display(low, mid, high, intensity, total);
    }
}

public void drawCube() {

    background(30);

    translate(width/2, height/2, -distanceCubeOffset);

    //add axis rotation when toggled via key press
    if(horizRotation) rotateX((float) (frameCount * .01));
    if(vertRotation) rotateY((float) (frameCount * .01));
    if(diagRotation) rotateZ((float) (frameCount * .01));

    for (int i = -distanceCubeOffset; i <= distanceCubeOffset; i += distanceBetweenCubes) {
        for (int j = -distanceCubeOffset; j <= distanceCubeOffset; j += distanceBetweenCubes) {
            for (int y = -distanceCubeOffset; y <= distanceCubeOffset; y += distanceBetweenCubes) {

                pushMatrix();
                translate(i, j, y);

                //rotate along 3 axis with a set speed modifier
                rotateX((float) (speedMultiplier*frameCount));
                rotateY((float) (speedMultiplier*frameCount));
                rotateZ((float) (speedMultiplier*frameCount));

                //adds color to each of the individual cubes within main cube 
                fill(colorFromOffset(i), colorFromOffset(j), colorFromOffset(y));
                box((float) (20 + (Math.sin(frameCount / 20.0)) * 20));
                popMatrix();
            }
        }
    }
}

public void drawSpiral() {

    noStroke();
    fill(0, 70);  

    rect(0, 0, width, height);
    translate(width/2, height/2);
 
    for (int i = 0; i < music.bufferSize() -1; i++) {
 
        float angle = sin(i+(pos1-2))*40;
        float modifiedAngle = sin(radians(i))*(pos1/angle);
        
        //determines how thick the spiral
        float spiralThickness = music.left.level() * 10;
        ellipse(i, i, spiralThickness, spiralThickness);

        rotateZ((float) (pos1*-PI/3*0.05)); 

        //blue
        fill(26, 117, 255);
    }
 
    pos1 += 0.1 ;
    pos2 += speed;
    pos3 += speed;
}

public void settings() {

    size(900, 900, P3D);
}

public void setup() {

    //load the music for the visual program
    minim = new Minim(this);
    music = minim.loadFile("astronomia.mp3");
    fft = new FFT(music.bufferSize(), music.sampleRate());

    surface = new Wall[frequencyOfWalls];

    //draws 4 walls for the dimension box
    for (int i = 0; i < frequencyOfWalls; i += size) surface[i] = new Wall(0, height / 2, 10, height);
    for (int i = 1; i < frequencyOfWalls; i += size) surface[i] = new Wall(width, height / 2, 10, height);
    for (int i = 2; i < frequencyOfWalls; i += size) surface[i] = new Wall(width / 2, height, width, 10);
    for (int i = 3; i < frequencyOfWalls; i += size) surface[i] = new Wall(width / 2, 0, width, 10);

    background(0);

    music.play(0);
}
 
public void draw() {

    drawDimensionBox();

    if(drawSpiral)
        drawSpiral();

    if(drawCube)
        drawCube();
}

public void keyPressed() {
    
    switch(key) {

        case ' ':
            drawCube = !drawCube;
            drawSpiral = false;
            break;
        
        case 'v':
            drawSpiral = !drawSpiral;
            break;
        
        case 'z':
            vertRotation = !vertRotation;
            break;
        
        case 'x':
            horizRotation = !horizRotation;
            break;
        
        case 'c':
            diagRotation = !diagRotation;
            break;
    }
}

class Cube {
    private float diagonalStart = -10000;
    private float maxZ = 1000;
  
    private float xPos;
    private float yPos; 
    private float zPos;

    private float rotationX;
    private float rotationY;
    private float rotationZ;

    private float finalX;
    private float finalY;
    private float finalZ;
    
    //constructor for cube
    public Cube() {
        xPos = random(0, width);
        yPos = random(0, height);
        zPos = random(diagonalStart, maxZ);
        
        rotationX = random(0, 1);
        rotationY = random(0, 1);
        rotationZ = random(0, 1);
    }
    //low, mid, high, intensity, total
    void display(final float low, final float mid, final float high, final float intensity, final float total) {

        // final int colorOfDisplay = color(low*0.67f, mid*0.67f, high*0.67f, intensity*5);
        // fill(colorOfDisplay, 255);
        
        final int strokeColor = color(255, 150-(20*intensity));

        stroke(strokeColor);
        strokeWeight(1 + (total/300));
        pushMatrix();
        
        translate(xPos, yPos, zPos);
        
        finalX = finalX + intensity*(rotationX/100);
        finalY = finalY + intensity*(rotationY/100);
        finalZ = finalZ + intensity*(rotationZ/100);
        
        rotateX(finalX);
        rotateY(finalY);
        rotateZ(finalZ);
        box(100+(intensity/2));
        
        popMatrix();
        
        zPos = zPos + (1+(intensity/5)+(pow((total/150), 2)));
        
        if (maxZ <= zPos) {
            xPos = random(0, width);
            yPos = random(0, height);
            zPos = diagonalStart;
        }
    }
}

    class Wall {
        float startingZ = -10000;
        float maxZ = 50;
        
        float x, y, z;
        float sizeX, sizeY;
    
        Wall(final float x, final float y, final float sizeX, final float sizeY) {
            this.x = x;
            this.y = y;
            this.z = random(startingZ, maxZ);  
            
            this.sizeX = sizeX;
            this.sizeY = sizeY;
        }
  
        void display(final float scoreLow, final float scoreMid, final float scoreHi, float intensity, final float scoreGlobal) {

            int displayColor = color(scoreLow*0.67f, scoreMid*0.67f, scoreHi*0.67f, scoreGlobal);
            
            fill(displayColor, ((scoreGlobal-5)/1000)*(255+(z/25)));
            noStroke();
            
            pushMatrix();
            
            translate(x, y, z);
            
            if (intensity > 100) intensity = 100;
            scale(sizeX*(intensity/100), sizeY*(intensity/100), 20);
            
            box(1);
            popMatrix();
            
            //sets the display color for the walls
            displayColor = color(scoreLow*0.5f, scoreMid*0.5f, scoreHi*0.5f, scoreGlobal);
            fill(displayColor, (scoreGlobal/5000)*(255+(z/25)));
            pushMatrix();
            
            translate(x,y,z);
            scale(sizeX, sizeY, 10);
            
            box(1);
            popMatrix();
            
            z = z + (pow((scoreGlobal/150), 2));
            
            if (z >= maxZ)
                z = startingZ;  
        }
    }
}