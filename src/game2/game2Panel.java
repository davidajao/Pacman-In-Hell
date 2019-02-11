package game2;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;

public class game2Panel extends JPanel{
	
	//instantiate new data object for game
	game2Data data = new game2Data();
    
	
    //panel constructor
    public game2Panel() {
    	 //set panel area
    	 setPreferredSize(new Dimension(data.WIDTH, data.HEIGHT));
    	 setBackground(new Color(16,16,16));
        
    	 //create listener and set focusable for spacebar
    	 gameListener listener = new gameListener();
    	 setFocusable(true);
    	  
         data.timer = new Timer(20, listener);
         addKeyListener(listener);
         
         data.cloud = new ArrayList<Rectangle>();
         data.food = new ArrayList<Rectangle>();
         
         //add character and set its initial location
         data.character = new ImageIcon("pacman1.gif");
         data.x = 100;
         data.y = 200;

         //Initialize 4 images to animate floor with
         data.flame1 = new ImageIcon("fireanim1.gif");
         data.flames = data.flame1;
         data.flame2 = new ImageIcon("fireanim2.gif");
         data.flame3 = new ImageIcon("fireanim3.gif");
         data.flame4 = new ImageIcon("fireanim4.gif");
         //position for floor flames
         data.floorx = 0;
         data.floory = 500;
         
         //initialize smoke and ghost images
         data.smoke = new ImageIcon("smoke1_thumb.gif");       
         data.ghost = new ImageIcon("redghost.gif");
         
         //add ghosts to screen
         data.addGhost(true);
         data.addGhost(true);
         data.addGhost(true);
         data.addGhost(true);
         data.addGhost(true);
         data.addGhost(true);
         data.addGhost(true);
         data.addGhost(true);
         
         //add food to screen
         data.addFood(true);
         data.addFood(true);
         data.addFood(true);
         data.addFood(true);
         data.addFood(true);
         data.addFood(true);

         //start timer for action listener
         data.timer.start();
    }
    
    
    //--------------------------------------------------------------
    //  PAINT METHOD
    //--------------------------------------------------------------
    public void paintComponent (Graphics page) {
    	
    	super.paintComponent(page);
    	
    	//paint the floor
    	page.setColor(new Color(16,16,16));		//close to black			
        page.fillRect(0, data.HEIGHT - data.floor, data.WIDTH, 100);		
        
        //draw flames at floor
        data.flames.paintIcon(this, page, data.floorx, data.floory);
        
        //paint character
        data.character.paintIcon(this, page, data.x, data.y);
    	
    	//paint roof
    	page.setColor(new Color(128,128,128));	//a shade of grey
    	page.fillRect(0, 0, data.WIDTH, data.roof);
        
        //paint smoke if game is over
        if (data.y >=  data.HEIGHT - data.floor || data.y <= data.roof) {
        	data.gameover = true;
            if(data.y >=  data.HEIGHT - data.floor) 
            	data.smoke.paintIcon(this, page, data.floorx+80, data.floory-85);
        }

        //draw clouds
        for (Rectangle rect : data.cloud) {
            page.fillRect(rect.x, rect.y, rect.width, rect.height);
            data.ghost.paintIcon(this,  page, rect.x, rect.y);				//place the ghost icon above every cloud 
        }
        
        //draw food
        page.setFont(new Font("Arial", 1 ,18));
        for (Rectangle circ : data.food) {
        	page.setColor(Color.green);
        	page.fillOval(circ.x, circ.y, circ.width, circ.height);
        	page.drawString("+10" , circ.x+10, circ.y);
        }

        // text to to show when game starts or game is over
        page.setColor(Color.orange); 
        page.setFont(new Font("Courier", 1 ,55));
        if (!data.start) {
            page.drawString("Press space to start!", 75, data.HEIGHT/2);
        }
        else if (data.gameover) {
        	page.setColor(Color.red); 
        	page.setFont(new Font("Courier", 1 ,100));
            page.drawString("Game Over!", 100, data.HEIGHT/2);
        }
        
        // display scores 
        page.setFont(new Font("Arial", 1 ,15));				//Set font style and size
        page.setColor(Color.orange);
        page.drawString("Score: " + data.score, 20, data.HEIGHT/12);
        page.drawString("High score: " + data.highscore, 20, data.HEIGHT/18);
        
        // instructions to change difficulty
        page.setColor(Color.white);  
        page.drawString("\"Use up and down arrows to choose between hard and easy\"", 190, 20);
        
        // show difficulty
        page.setColor(Color.orange);  
        if(data.speed == 10)
        	page.drawString("Difficulty: Easy", data.WIDTH-150, data.HEIGHT/12);			// show difficulty as easy if speed equals 10
        else
        	page.drawString("Difficulty: Hard", data.WIDTH-150, data.HEIGHT/12);			//show difficulty as hard if speed equals 20
        	
    }
    

    
    //--------------------------------------------------------------
    //  Action Listener for timer and Key Listener for when space is entered
    //--------------------------------------------------------------
    
    private class gameListener implements ActionListener, KeyListener{
	    @Override
	      //--------------------------------------------------------------
	      //  Updates the position of the character and the clouds 
	      //  whenever the timer fires an action event.
	      //--------------------------------------------------------------
	    public void actionPerformed(ActionEvent event) {
	
	        if (data.start) {
	        	
	        	//if game starts, move ghosts into screen
	        	data.moveObjectsIntoScreen();
	        	
	        	//if ghosts go out of screen, delete ghost
	        	data.checkOutsideObjects();
	            
	        	//if there is collision, end game
	        	data.checkCollision();

	            //if a ghost is avoided, count scores
	        	data.countScores();           
	            
	            
	        	data.tick ++;
	
	            //if tick is still counting and character is still in screen, 
	            //drop the character down tick spaces
	            if (data.tick % 2 == 0 && data.y < data.HEIGHT-data.floor) {
	            	data.y += data.tick;
	            }
	
	            //when game is over falls into floor, move character left 
	            if (data.gameover && data.y >= data.HEIGHT - data.floor) {
	            	data.x -= data.speed;
	            }
	            
	            if (data.tick % 5 == 0) {	//at intervals, animate images
	            	data.animateFloor();
	            }
	            
	        }
	        repaint();
	    }
	
	    
	    //-----------------------------------------------------------------------------------------
	    //KEY LISTENERS
	    //-----------------------------------------------------------------------------------------
	    //  Flaps the bird every time space-bar is pressed and up and down arrows to change speed 
	    //-----------------------------------------------------------------------------------------
	    @Override
	    public void keyReleased(KeyEvent event) {
	        if (event.getKeyCode() == KeyEvent.VK_SPACE) {
	        	data.flap();
	        }
	        if (event.getKeyCode() == KeyEvent.VK_UP) {
	        	data.speed=20;
	        }
	        if (event.getKeyCode() == KeyEvent.VK_DOWN) {
	        	data.speed=10;
	        }
	    }
	    
	    @Override
	    public void keyTyped(KeyEvent event) {
	
	    }
	
	    @Override
	    public void keyPressed(KeyEvent event) {
	
	    } 

    } 
}
