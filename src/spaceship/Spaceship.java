
package spaceship;

import java.io.*;
import javax.sound.sampled.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.File;
//make an array of stars
//star collison 
//star image
public class Spaceship extends JFrame implements Runnable {
    static final int WINDOW_WIDTH = 420;
    static final int WINDOW_HEIGHT = 445;
    final int XBORDER = 20;
    final int YBORDER = 20;
    final int YTITLE = 25;
    boolean animateFirstTime = true;
    int xsize = -1;
    int ysize = -1;
    Image image;
    Graphics2D g;

    sound zsound = null;
    sound bgSound = null;
    Image outerSpaceImage;

//variables for rocket.
    Image rocketImage;
    boolean rocketright;
    int rocketXPos;
    int rocketYPos;
    int numStars;
    int starX[];
    int starY[];
    int hp;
    int lrspeed;
    int udspeed;
    int rocketwidth;
    int rocketheight;
    boolean staractive[];
    boolean first_hit[];
    boolean gameover;
    int timecount;
    int timealive;
    
//    int num_missiles = 10;
//    int missileX[] = new int[num_missiles];
//    int missileY[] = new int[num_missiles];
//    boolean missile_active[] = new boolean[num_missiles];
//    int missilespeed[] = new int[num_missiles];
//    int currentmissileindex;
    Missile missiles[];
    
    
    
    boolean pause;
    int score;
    int forcefield;
    
    boolean rocketActive;
    static Spaceship frame;
    public static void main(String[] args) {
        frame = new Spaceship();
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public Spaceship() {
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.BUTTON1 == e.getButton()) {
                    //left button

// location of the cursor.
                    int xpos = e.getX();
                    int ypos = e.getY();

                }
                if (e.BUTTON3 == e.getButton()) {
                    //right button
                    reset();
                }
                repaint();
            }
        });

    addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseDragged(MouseEvent e) {
        repaint();
      }
    });

    addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseMoved(MouseEvent e) {

        repaint();
      }
    });

        addKeyListener(new KeyAdapter() {

            public void keyPressed(KeyEvent e) {
                if (e.VK_UP == e.getKeyCode()) {
                   
                    udspeed -=3;
                    //rocketYPos +=5;
                } else if (e.VK_DOWN == e.getKeyCode()) {
                    
                    udspeed +=3;
                    //rocketYPos -=5;
                } else if (e.VK_LEFT == e.getKeyCode()) {
                    lrspeed -=1;
                } else if (e.VK_RIGHT == e.getKeyCode()) {
                    lrspeed +=1;
                }
                else if (e.VK_INSERT == e.getKeyCode()) {
                    zsound = new sound("ouch.wav");                    
                }
                else if(e.VK_SPACE == e.getKeyCode())
                {
                    
                        if(Missile.currentmissileindex < Missile.num_missiles)
                        {
                        System.out.println(Missile.currentmissileindex);
                        missiles[Missile.currentmissileindex].missile_active = true;
                        missiles[Missile.currentmissileindex].missileX = rocketXPos;
                        missiles[Missile.currentmissileindex].missileY=rocketYPos;
                        if(rocketright)
                        missiles[Missile.currentmissileindex].missilespeed = 5; 
                        if(!rocketright)
                        missiles[Missile.currentmissileindex].missilespeed = -5; 
                        Missile.currentmissileindex++;
                        }
                        else
                        {
                            Missile.currentmissileindex=0;
                        }
                        
                    
                }
            
                repaint();
            }
        });
        init();
        start();
    }
    Thread relaxer;
////////////////////////////////////////////////////////////////////////////
    public void init() {
        requestFocus();
    }
////////////////////////////////////////////////////////////////////////////
    public void destroy() {
    }



////////////////////////////////////////////////////////////////////////////
    public void paint(Graphics gOld) {
        if (image == null || xsize != getSize().width || ysize != getSize().height) {
            xsize = getSize().width;
            ysize = getSize().height;
            image = createImage(xsize, ysize);
            g = (Graphics2D) image.getGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
        }
//fill background
        g.setColor(Color.cyan);
        g.fillRect(0, 0, xsize, ysize);

        int x[] = {getX(0), getX(getWidth2()), getX(getWidth2()), getX(0), getX(0)};
        int y[] = {getY(0), getY(0), getY(getHeight2()), getY(getHeight2()), getY(0)};
//fill border
        g.setColor(Color.black);
        g.fillPolygon(x, y, 4);
// draw border
        g.setColor(Color.red);
        g.drawPolyline(x, y, 5);

        if (animateFirstTime) {
            gOld.drawImage(image, 0, 0, null);
            return;
        }

        g.drawImage(outerSpaceImage,getX(0),getY(0),
                getWidth2(),getHeight2(),this);
        if(lrspeed > 0)
        {
            drawRocket(rocketImage,getX(rocketXPos),getYNormal(rocketYPos),0.0,1.0,1.0 );
            rocketright = true;
        }
        if(lrspeed < 0)
        {
            drawRocket(rocketImage,getX(rocketXPos),getYNormal(rocketYPos),0.0,-1.0,-1.0 );
            rocketright = false;
        }
        if(lrspeed == 0 && rocketright == true)
        {
            drawRocket(rocketImage,getX(rocketXPos),getYNormal(rocketYPos),0.0,1.0,1.0 );
        }
        if(lrspeed == 0 && rocketright == false)
        {
            drawRocket(rocketImage,getX(rocketXPos),getYNormal(rocketYPos),0.0,-1.0,-1.0 );
        }
        for(int i=0; i<numStars; i++)
        {
            if(staractive[i])
            drawCircle(getX(starX[i]),getYNormal(starY[i]),0.0,1,1);
        }
        for(int i=0; i<Missile.num_missiles; i++)
        {
            if(missiles[i].missile_active)
            {
                missile(getX(missiles[i].missileX),getYNormal(missiles[i].missileY),0.0,.5,.5);
            }
        }
        if(gameover)
        {
        g.setColor(Color.WHITE);
        g.setFont(new Font("OCR A Extended", Font.PLAIN,15));
        g.drawString("Game Over; You lose",150,250);
        }
        g.setColor(Color.RED);
        g.setFont(new Font("OCR A Extended", Font.PLAIN,20));
        g.drawString("HP: " + hp,25,45);
        g.drawString("TIME: " + timealive,150,45);

        gOld.drawImage(image, 0, 0, null);
    }
////////////////////////////////////////////////////////////////////////////
    public void missile(int xpos,int ypos,double rot,double xscale,double yscale)
    {
        g.translate(xpos,ypos);
        g.rotate(rot  * Math.PI/180.0);
        g.scale( xscale , yscale );

        g.setColor(Color.white);
        g.fillOval(-10,-10,20,20);

        g.scale( 1.0/xscale,1.0/yscale );
        g.rotate(-rot  * Math.PI/180.0);
        g.translate(-xpos,-ypos);
    }
////////////////////////////////////////////////////////////////////////////
    public void drawCircle(int xpos,int ypos,double rot,double xscale,double yscale)
    {
        g.translate(xpos,ypos);
        g.rotate(rot  * Math.PI/180.0);
        g.scale( xscale , yscale );

        g.setColor(Color.red);
        g.fillOval(-10,-10,20,20);

        g.scale( 1.0/xscale,1.0/yscale );
        g.rotate(-rot  * Math.PI/180.0);
        g.translate(-xpos,-ypos);
    }
////////////////////////////////////////////////////////////////////////////
    public void drawRocket(Image image,int xpos,int ypos,double rot,double xscale,
            double yscale) {
        int width = rocketImage.getWidth(this);
        int height = rocketImage.getHeight(this);
        g.translate(xpos,ypos);
        g.rotate(rot  * Math.PI/180.0);
        g.scale( xscale , yscale );

        g.drawImage(image,-width/2,-height/2,
        width,height,this);

        g.scale( 1.0/xscale,1.0/yscale );
        g.rotate(-rot  * Math.PI/180.0);
        g.translate(-xpos,-ypos);
    }
////////////////////////////////////////////////////////////////////////////
// needed for     implement runnable
    public void run() {
        while (true) {
            animate();
            repaint();
            double seconds = 0.04;    //time that 1 frame takes.
            int miliseconds = (int) (1000.0 * seconds);
            try {
                Thread.sleep(miliseconds);
            } catch (InterruptedException e) {
            }
        }
    }
/////////////////////////////////////////////////////////////////////////
    public void reset() {

//init the location of the rocket to the center.
        timecount = 0;
        timealive = 0;
        missiles  = new Missile[Missile.num_missiles];
        rocketXPos = getWidth2()/2;
        rocketYPos = getHeight2()/2;
        first_hit = new boolean[numStars];
        staractive = new boolean[numStars];
        starX = new int[numStars];
        starY = new int[numStars];
        for(int i=0; i<numStars; i++)
        {
            starX[i] = (int)(Math.random()*getWidth2());
            starY[i] = (int)(Math.random()*getHeight2());;
        }
        lrspeed = 0;
        udspeed = 0;
        rocketActive = true;
        rocketright = true;
        rocketwidth = rocketImage.getWidth(this);;
        rocketheight = rocketImage.getHeight(this);; 
        for(int i=0; i<numStars; i++)
        {
        first_hit[i] = false;
        staractive[i] = true;
        }
        
        for(int i=0; i<Missile.num_missiles;i++)
        {
            //num_missiles = 10;    
            //missile_active[i] = false;
            missiles[i] = new Missile();
            //missilespeed[i] = 0;
            //Missile.missileX[i] = rocketXPos;
            //Missile.missileY[i] = rocketYPos;
        }
        Missile.currentmissileindex = 0;
        hp = 100;
        gameover = false;

    }
/////////////////////////////////////////////////////////////////////////
    public void animate() {
        if (animateFirstTime) {
            animateFirstTime = false;
            if (xsize != getSize().width || ysize != getSize().height) {
                xsize = getSize().width;
                ysize = getSize().height;
            }


            readFile();
            outerSpaceImage = Toolkit.getDefaultToolkit().getImage("./outerSpace.jpg");
            rocketImage = Toolkit.getDefaultToolkit().getImage("./rocket.GIF");
            rocketwidth = rocketImage.getWidth(this);;
            rocketheight = rocketImage.getHeight(this);;
            //starImage = Toolkit.getDefaultToolkit().getImage("./starAnim.GIF");
            bgSound = new sound("starwars.wav"); 
            
            reset();
        }
        if(bgSound.donePlaying == true )
        {
            bgSound = new sound("starwars.wav");
        }
       
        for(int i=0;i<numStars;i++)
        {
            starX[i]-=lrspeed;
        }
        for(int i=0;i<numStars;i++)
        {
            if(starX[i]<0)
            {
                staractive[i] = true;
                starX[i] = getWidth2();
                starY[i] = (int)(Math.random()*getHeight2());
            }
            else if(starX[i] >getWidth2())
            {
                staractive[i] = true;
                starX[i] = 0;
                starY[i] = (int)(Math.random()*getHeight2());
            }
            
        }
        
            if(rocketYPos - udspeed > getHeight2())
            {
                udspeed = 0;
                rocketYPos = getHeight2();
            }
            else if(rocketYPos -udspeed <0)
            {
                udspeed = 0;
                rocketYPos = 0;
            }
            else
            {
                rocketYPos -= udspeed;
            }
            
        
        for(int i=0;i<numStars;i++)
        {
//           System.out.println(rocketXPos);
//           System.out.println(starX[i]);
//           System.out.println(rocketYPos);
//           System.out.println(starY[i]);
//           System.out.println("----------------");
           
            if(starX[i] <= rocketXPos+(rocketwidth/2) && starX[i] >= rocketXPos-(rocketwidth/2) && starY[i] <= rocketYPos + (rocketheight/2) && starY[i] >= rocketYPos - (rocketheight/2) && staractive[i] == true)
            {
                
                 if(first_hit[i] == false)
                 {
                    zsound = new sound("ouch.wav");
                    hp-=25;
                    first_hit[i] = true;
                 }
            }
            else
            {
                first_hit[i] = false;
            }
           
            //System.out.println(first_hit[0]);

        }
        for(int i=0;i<Missile.num_missiles;i++)
        {
            if(missiles[i].missile_active)
            {
                missiles[i].missileX+=missiles[i].missilespeed;
            }
            
        }
        for(int i=0;i<numStars;i++)
        {
           for(int b=0;b<Missile.num_missiles;b++)
           {
               if(starX[i] <= missiles[b].missileX+10 && starX[i] >= missiles[b].missileX-10 && starY[i] <= missiles[b].missileY + 10 && starY[i] >= missiles[b].missileY - 10 && staractive[i] == true)
               {
                   staractive[i] = false;
                   
               }
           }
           
        }
//        for(int i=0;i<numStars;i++)
//        {
//            //a^2 + b^2 = c^2
//            //int a = 
//            
//            
//            
//            
//            
//            
//            
//            
//        }
        if(hp <= 0)
        {
            gameover = true;
        }
        timecount++;
        if(timecount%25 == 0)
        {
            timealive++;
        }
        if(!gameover)
        {
            if(timecount == 15*25)
            {
                hp+=24;
            }
        }
    }

////////////////////////////////////////////////////////////////////////////
    public void start() {
        if (relaxer == null) {
            relaxer = new Thread(this);
            relaxer.start();
        }
    }
////////////////////////////////////////////////////////////////////////////
    public void stop() {
        if (relaxer.isAlive()) {
            relaxer.stop();
        }
        relaxer = null;
    }
/////////////////////////////////////////////////////////////////////////
    public int getX(int x) {
        return (x + XBORDER);
    }

    public int getY(int y) {
        return (y + YBORDER + YTITLE);
    }

    public int getYNormal(int y) {
        return (-y + YBORDER + YTITLE + getHeight2());
    }
    
    
    public int getWidth2() {
        return (xsize - getX(0) - XBORDER);
    }

    public int getHeight2() {
        return (ysize - getY(0) - YBORDER);
    }
    
    public void readFile() {
        try {
            String inputfile = "info.txt";
            BufferedReader in = new BufferedReader(new FileReader(inputfile));
            String line = in.readLine();
            while (line != null) {
                String newLine = line.toLowerCase();
                if (newLine.startsWith("numstars"))
                {
                    String numStarsString = newLine.substring(9);
                    numStars = Integer.parseInt(numStarsString.trim());
                }
                if (newLine.startsWith("num_missiles"))
                {
                    String numMissilesString = newLine.substring(13);
                    Missile.num_missiles = Integer.parseInt(numMissilesString.trim());
                }
                line = in.readLine();
            }
            
            in.close();
        } catch (IOException ioe) {
        }
    }


}

class sound implements Runnable {
    Thread myThread;
    File soundFile;
    public boolean donePlaying = false;
    sound(String _name)
    {
        soundFile = new File(_name);
        myThread = new Thread(this);
        myThread.start();
    }
    public void run()
    {
        try {
        AudioInputStream ais = AudioSystem.getAudioInputStream(soundFile);
        AudioFormat format = ais.getFormat();
    //    System.out.println("Format: " + format);
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        SourceDataLine source = (SourceDataLine) AudioSystem.getLine(info);
        source.open(format);
        source.start();
        int read = 0;
        byte[] audioData = new byte[16384];
        while (read > -1){
            read = ais.read(audioData,0,audioData.length);
            if (read >= 0) {
                source.write(audioData,0,read);
            }
        }
        donePlaying = true;

        source.drain();
        source.close();
        }
        catch (Exception exc) {
            System.out.println("error: " + exc.getMessage());
            exc.printStackTrace();
        }
    }
    
  

}
class Missile
    {
        public static int num_missiles = 10;
        public static int currentmissileindex = 0;
        
        public int missileX;
        public int missileY;
        public boolean missile_active;
        public int missilespeed;
        
        Missile()
        {
            missile_active = false;
            
        }
        
    }
