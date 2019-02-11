package game2;

import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;
import javax.swing.Timer;

public class game2Data {
	public ImageIcon character;	//main character
	public ImageIcon flames;		//floor flames 
	
	//images to alternate between to create an effect of fire
	public ImageIcon flame1;		
	public ImageIcon flame2;
	public ImageIcon flame3;
	public ImageIcon flame4;
	
	public ImageIcon smoke;		//smoke to show when character falls into flames
	public ImageIcon ghost;		//ghost icon
	
	public final int WIDTH = 800;		//screen width
    public final int HEIGHT = 600;		//screen height
    public final int floor = 100;		//floor height
    public final int roof = 60;			//roof height
    
    public int speed; 	//holds speed of the ghosts
    
    public int x, y;		//character location
    public int floorx, floory;	//floor image location
    
    public int score;	//holds current score
    public int highscore;

    //public Rectangle character;

    public ArrayList<Rectangle> cloud;		//cloud in back of ghosts
    public ArrayList<Rectangle> food;		//food for bonus points
    
    public Random rand;

    public boolean start, gameover;

    public int tick;	//counter for how long without action performed
    public Timer timer;
    
    public String bonusPoints = "bonus.wav";	//file for bonus point audio
    public String hit = "hit.wav";				//file for hit sound
    
    //Constructor
    public  game2Data() {
    	
    	
        rand = new Random();
        
        speed = 10;
        
        score = 0;
        highscore = 0;
        
        start = false;
        gameover = false;
        
    }
    
    
    //--------------------------------------------------------------
    //  Method to add a cloud to the screen from an array list of clouds
    //--------------------------------------------------------------
    public void addGhost(boolean first) {
        int width = 50;			//width of clouds
        int height = 60;		//height of clouds
        int ghosty = rand.nextInt(HEIGHT-200) + roof;	//random integer between roof and bottom of screen
        
        if (first) {
        	//sets x as a coordinate to the far right of frame
        	int ghostx = WIDTH + width + cloud.size() * 300;
        	//draw first cloud at 300 spaces to the right
            cloud.add(new Rectangle(ghostx, ghosty, width, height));
            
            //reset current score when adding first cloud
            score = 0;
    }																														
        else {
        	//sets x as a coordinate 300 spaces to the right of last rectangle
        	int ghostx = cloud.get(cloud.size() - 1).x + 300;
            cloud.add(new Rectangle(ghostx, ghosty, width, height));		
        }

    }
    
    
    //--------------------------------------------------------------
    //  Move ghosts and food into screen from the right
    //--------------------------------------------------------------
    public void moveObjectsIntoScreen() {
    	//move all ghost objects into screen
        for (int i = 0; i < cloud.size(); i++) {
            Rectangle rect = cloud.get(i);
            rect.x -= speed;			//move all rectangles to the left "speed" spaces
        }
        
        // move all food objects into screen
        for (int i = 0; i < food.size(); i++) {
            Rectangle circ = food.get(i);
            circ.x -= speed;			//move all food to the left "speed" spaces
        }
    }
    
    
    //--------------------------------------------------------------
    //	deletes shots and food that have left the screen to the left
    //--------------------------------------------------------------
    public void checkOutsideObjects() {
    	//delete ghosts that have left screen and add more to the right
        for (int i = 0; i < cloud.size(); i++) {
            Rectangle rect = cloud.get(i);	//get coordinates of cloud

            //if the cloud goes out of the screen on the left, delete it from the array list of clouds
            if (rect.x + rect.width < 0) {
                cloud.remove(rect);		//remove cloud thats out of the screen
                addGhost(false);		//add next cloud from the right
            }
        }
        
    	//delete food that has left screen and add more to the right
        for (int i = 0; i < food.size(); i++) {
            Rectangle circ = food.get(i);	//get coordinates of food

            //if the food goes out of the screen on the left, delete it from the array list of food
            if (circ.x + circ.width < 0) {
                food.remove(circ);		//remove food thats out of the screen
                addFood(false);		//add next food from the right
            }
        }
    }
    
    
    //--------------------------------------------------------------
    //  Method to flap the character and add clouds to screen 
    //--------------------------------------------------------------
    public void flap() {

        if (gameover) {		//if game is over when you flap
            x= 100;
            y= 200;
            //clear all ghosts and draw new ones
            cloud.clear();
            //add more clouds
            for(int i=0; i<8; i++) {
            	addGhost(true);
            }
            
            gameover = false;
        }

        //if you flap when game has not started, start again
        if (!start) {
            start = true;
        }
        else {
            y -= 70;				//raise character up 70 pixels
            tick = 0;				//reset time
        }

    }
    
    
    //--------------------------------------------------------------
    //	Method to flip between images to animate the floor
    //--------------------------------------------------------------
    public void animateFloor() {
    	
    	//changes flames from flame1, to flame2, to flame3, and flame 4 repeatedly
        if (flames == flame1) {
          	flames = flame2;
          }
          else if(flames == flame2) {
          	flames = flame3;
          }
          else if(flames == flame3) {
          	flames = flame4;
          }
          else {
          	flames = flame1;
          }
    }
    

    
    //--------------------------------------------------------------
    //	1. check for collision of character and ghosts 
    //	2. check for collision of character and bonus points 
    //  3. check for collision with roof or floor
    //--------------------------------------------------------------
    public void checkCollision() {
        
       //for every cloud
       //if the character touches the cloud, set game-over to true and move character left
       for (Rectangle rect : cloud) {
       	if (rect.intersects(x ,y,character.getIconWidth(),character.getIconHeight())){
               gameover = true;
               x -= speed;		//moves character left 
               
               //play audio when image collides with ghost
               playAudio(hit);

           }
       }
       
       //for every food
       //if the character touches the food increase score by 100 
       for (int i = 0; i < food.size(); i++) {
           Rectangle circ = food.get(i);
       	if (circ.intersects(x ,y,character.getIconWidth()+10,character.getIconHeight())){
       			score += 10;
       			food.remove(circ);		//make food disappear after eaten
       			addFood(false);			//add food to back 
       			
                //play audio when image touches a plus ten
       			playAudio(bonusPoints);
           }
       }
       
       //if it touches the roof or floor game is over and play audio
       if (y >=  HEIGHT - floor || y <= roof) {
    	  if (!gameover)	//play crash audio sound just once 
    		  playAudio(hit);
          gameover = true;	
       }
    }
    
    
    //--------------------------------------------------------------
    //  method to increment scores and determine high score
    //	Counts scores when a ghost is avoided and save high score
    //--------------------------------------------------------------
    public void countScores() {
        //counter for how many clouds passed
        for (Rectangle rect : cloud) {
            if (rect.x + rect.width == x ) {		//if character avoids a cloud
            	
            	//if game is not over, increase score
            	if(!gameover) 
            		score += 1;

                //sets new high score, if higher than previous scores
                if(score >= highscore) 
                    	highscore = score;        	
            }
        }
    }
    


    //------------------------------------------------------------------------
    //  Method to add food to the array list of foods
    //------------------------------------------------------------------------
    public void addFood(boolean first) {
        int width = 15;
        int height = 15;
        
        int foody = rand.nextInt(HEIGHT-200) + roof;	//random integer between roof and bottom of screen
        
        if (first) {
        	//sets x as a coordinate to the far right of frame
        	int foodx = WIDTH + width + food.size() * 1500;
        	//draw first cloud at 300 spaces to the right
            food.add(new Rectangle(foodx, foody, width, height));
        }	
        
        else {
        	//sets x as a coordinate 300 spaces to the right of last rectangle
        	int foodx = food.get(food.size() - 1).x + 1000;
            food.add(new Rectangle(foodx, foody, width, height));		
        }

    }
    

    //------------------------------------------------------------------------
    //  Method to play an audio file once
    //------------------------------------------------------------------------
    public void playAudio(String file) {
         try {
          	Clip music = AudioSystem.getClip();
          	music.open(AudioSystem.getAudioInputStream(new File(file)));
          	music.loop(0); 
          }
          catch(Exception e){		//catch any exception from opening or getting the clip
          	e.printStackTrace(System.out);
          }
    }
    
}
