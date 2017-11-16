/*****************************************************
 * Beginning Java Game Programming, 3rd Edition
 * by Jonathan S. Harbour
 * AnimatedSprite class
 *****************************************************/
import java.applet.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import javax.swing.*;
import java.net.*;

public class AnimatedSprite {
    protected JFrame frame;
    protected Graphics2D g2d;
    protected Applet app;

    public Image image;
    public boolean alive;
    public boolean reverse;
    public boolean type; //Applet is true, JFrame is false
    public Point position;
    public Point velocity;
    public double rotationRate;
    public int currentState;
    public int currentFrame, totalFrames;
    public int animationDirection;
    public int frameCount, frameDelay;
    public int frameWidth, frameHeight, columns;
    public double moveAngle, faceAngle;

    public AnimatedSprite(Applet _app, Graphics2D _g2d) {
        type = true;
        app =_app;
        g2d = _g2d;
        image = null;
        alive = false;
        position = new Point(0, 0);
        velocity = new Point(0, 0);
        rotationRate = 0.0;
        currentState = 0;
        currentFrame = 0;
        totalFrames = 1;
        animationDirection = 1;
        frameCount = 0;
        frameDelay = 0;
        frameWidth = 0;
        frameHeight = 0;
        columns = 1;
        moveAngle = 0.0;
        faceAngle = 0.0;      
    }

    public AnimatedSprite(JFrame _frame, Graphics2D _g2d) {
        type = false;
        frame = _frame;
        g2d = _g2d;
        image = null;
        alive = false;
        position = new Point(0, 0);
        velocity = new Point(0, 0);
        rotationRate = 0.0;
        currentState = 0;
        currentFrame = 0;
        totalFrames = 1;
        animationDirection = 1;
        frameCount = 0;
        frameDelay = 0;
        frameWidth = 0;
        frameHeight = 0;
        columns = 1;
        moveAngle = 0.0;
        faceAngle = 0.0;    
    }

    public JFrame getJFrame() { return frame; }

    public Applet getApplet(){return app;};

    public Graphics2D getGraphics() { return g2d; }

    public void setGraphics(Graphics2D _g2d) { g2d = _g2d; }

    public void setImage(Image _image) { image = _image; }

    public int getWidth() {
        if (image != null)
            return image.getWidth((type)?app:frame);
        else
            return 0;
    }

    public int getHeight() {
        if (image != null)
            return image.getHeight((type)?app:frame);
        else
            return 0;
    }

    public double getCenterX() {
        return position.x + getWidth() / 2;
    }

    public double getCenterY() {
        return position.y + getHeight() / 2;
    }

    public Point getCenter() {
        int x = (int)getCenterX();
        int y = (int)getCenterY();
        return(new Point(x,y));
    }

    private URL getURL(String filename) {
        URL url = null;
        try {
            url = this.getClass().getResource(filename);
        }
        catch (Exception e) { }

        return url;
    }

    public Rectangle getBounds() {
        return (new Rectangle((int)position.x, (int)position.y, getWidth()/columns, getHeight()/(totalFrames/columns)));
        //return (new Rectangle((int)position.x, (int)position.y, getWidth(), getHeight()));
    }

    public void load(String filename, int _columns, int _totalFrames,
    int _width, int _height)
    {
        Toolkit tk = Toolkit.getDefaultToolkit();
        image = tk.getImage(getURL(filename));
        while(image.getWidth((type)?app:frame) <= 0);
        columns = _columns;
        totalFrames = _totalFrames;
        frameWidth = _width;
        frameHeight = _height;
    }

    protected void update() {
        //update position
        position.x += velocity.x;
        position.y += velocity.y;

        //update rotation
        if (rotationRate > 0.0) {
            faceAngle += rotationRate;
            if (faceAngle < 0)
                faceAngle = 360 - rotationRate;
            else if (faceAngle > 360)
                faceAngle = rotationRate;
        }

        //update animation
        if (totalFrames > 1) {
            frameCount++;
            if (frameCount > frameDelay) {
                frameCount = 0;
                currentFrame += animationDirection;
                if (currentFrame > totalFrames - 1) {
                    currentFrame = 0;
                }
                else if (currentFrame < 0) {
                    currentFrame = totalFrames - 1;
                }
            }
        }

    }

    //draw bounding rectangle around sprite
    public void drawBounds(Color c) {
        g2d.setColor(c);
        g2d.draw(getBounds());
    }

    public void draw() {
        update();

        //get the current frame
        int frameX = (currentFrame % columns) * frameWidth;
        int frameY = (currentFrame / columns) * frameHeight;

        //draw the frame 
        if(reverse)
        {
            if(type){                   
                g2d.drawImage(image, position.x+frameWidth, position.y, position.x, position.y+frameHeight, 
                    frameX, frameY, frameX+frameWidth, frameY+frameHeight, getApplet());     
            }
            else{
                g2d.drawImage(image, position.x+frameWidth, position.y, position.x, position.y+frameHeight, 
                    frameX, frameY, frameX+frameWidth, frameY+frameHeight, getJFrame());     
            }
        }
        else{            
            if(type){
                g2d.drawImage(image, position.x, position.y, position.x+frameWidth, position.y+frameHeight, 
                frameX, frameY, frameX+frameWidth, frameY+frameHeight, getApplet());
                
            }
            else{
                g2d.drawImage(image, position.x, position.y, position.x+frameWidth, position.y+frameHeight, 
                    frameX, frameY, frameX+frameWidth, frameY+frameHeight, getJFrame());
            }            
        }

    }
    //check for collision with a rectangular shape
    public boolean collidesWith(Rectangle rect) {
        return (rect.intersects(getBounds()));
    }
    //check for collision with another sprite
    public boolean collidesWith(AnimatedSprite sprite) {
        return (getBounds().intersects(sprite.getBounds()));
    }
    //check for collision with a point
    public boolean collidesWith(Point point) {
        return (getBounds().contains(point.x, point.y));
    }    
    
    
    
}

