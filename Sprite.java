/*********************************************************
 * Beginning Java Game Programming, 3rd Edition
 * by Jonathan S. Harbour
 * Sprite class
 **********************************************************/
import java.awt.*;
import java.applet.*;
import javax.swing.*;
import java.awt.geom.*;

public class Sprite extends Object {
    private ImageEntity entity;
    protected Point2D.Double pos;
    protected Point2D.Double vel;
    protected double rotRate;
    protected boolean type; //true for applet false for jframe
    protected int currentState;
	public double health;

    //constructor
    Sprite(JFrame a, Graphics2D g2d) {
        type = false;
        entity = new ImageEntity(a);
        entity.setGraphics(g2d);
        entity.setAlive(false);
        pos = new Point.Double(0, 0);
        vel = new Point.Double(0, 0);
        rotRate = 0.0;
        currentState = 0;        
    }
    
    Sprite(Applet a, Graphics2D g2d) {
        type = true;
        entity = new ImageEntity(a);
        entity.setGraphics(g2d);
        entity.setAlive(false);
        pos = new Point.Double(0, 0);
        vel = new Point.Double(0, 0);
        rotRate = 0.0;
        currentState = 0;       
    }

    //load bitmap file
    public void load(String filename) {
        entity.load(filename);
    }

    //perform affine transformations
    public void transform() {
        entity.setX(pos.x);
        entity.setY(pos.y);
        entity.transform();
    }

    //draw the image
    public void draw() {
        if(type){
            entity.g2d.drawImage(entity.getImage(),entity.at,entity.app);
        }
        else{
            entity.g2d.drawImage(entity.getImage(),entity.at,entity.frame);
    
        }
    }
    //draw bounding rectangle around sprite
    public void drawBounds(Color c) {
        entity.g2d.setColor(c);
        entity.g2d.draw(getBounds());
    }

    //update the position based on velocity
    public void updatePosition() {
        pos.x += vel.x;
        pos.y += vel.y;
    }

    //methods related to automatic rotation factor
    public double rotationRate() { return rotRate; }

    public void setRotationRate(double rate) { rotRate = rate; }

    public void updateRotation() {
        setFaceAngle(faceAngle() + rotRate);
        if (faceAngle() < 0)
            setFaceAngle(360 - rotRate);
        else if (faceAngle() > 360)
            setFaceAngle(rotRate);
    }

    //generic sprite state variable (alive, dead, collided, etc)
    public int state() { return currentState; }

    public void setState(int state) { currentState = state; }

    //returns a bounding rectangle
    public Rectangle getBounds() { return entity.getBounds(); }

    //sprite position
    public Point2D.Double position() { return pos; }

    public void setPosition(Point2D.Double pos) { this.pos = pos; }

    //sprite movement velocity
    public Point2D.Double velocity() { return vel; }

    public void setVelocity(Point2D.Double vel) { this.vel = vel; }

    //returns the center of the sprite as a Point
    public Point2D.Double center() {
        Double x = entity.getCenterX();
        Double y = entity.getCenterY();
        return(new Point2D.Double(x,y));
    }

    //generic variable for selectively using sprites
    public boolean alive() { return entity.isAlive(); }

    public void setAlive(boolean alive) { entity.setAlive(alive); }

    //face angle indicates which direction sprite is facing
    public double faceAngle() { return entity.getFaceAngle(); }

    public void setFaceAngle(double angle) {
        entity.setFaceAngle(angle);
    }

    public void setFaceAngle(float angle) {
        entity.setFaceAngle((double) angle);
    }

    public void setFaceAngle(int angle) {
        entity.setFaceAngle((double) angle);
    }

    //move angle indicates direction sprite is moving
    public double moveAngle() { return entity.getMoveAngle(); }

    public void setMoveAngle(double angle) {
        entity.setMoveAngle(angle);
    }

    public void setMoveAngle(float angle) {
        entity.setMoveAngle((double) angle);
    }

    public void setMoveAngle(int angle) {
        entity.setMoveAngle((double) angle);
    }

    //returns the source image width/height
    public int imageWidth() { return entity.width(); }

    public int imageHeight() { return entity.height(); }

    //check for collision with a rectangular shape
    public boolean collidesWith(Rectangle rect) {
        return (rect.intersects(getBounds()));
    }
    //check for collision with another sprite
    public boolean collidesWith(Sprite sprite) {
        return (getBounds().intersects(sprite.getBounds()));
    }
    //check for collision with a point
    public boolean collidesWith(Point point) {
        return (getBounds().contains(point.x, point.y));
    }
	
    public JFrame frame() { return entity.frame; }
    
    public Applet app(){ return entity.app;}
    
    public Graphics2D graphics() { return entity.g2d; }

    public Image image() { return entity.image; }

    public void setImage(Image image) { entity.setImage(image); }
    
    public Line2D.Double top(){
        return new Line2D.Double(getBounds().x,getBounds().y, 
            getBounds().x+getBounds().width, getBounds().y  );
        
    }
    
    public Line2D.Double bottom(){
        return new Line2D.Double(getBounds().x, getBounds().y+getBounds().height, 
            getBounds().x+getBounds().width, getBounds().y+getBounds().height  );
        
    }
    
    public Line2D.Double left(){
        return new Line2D.Double(getBounds().x,getBounds().y, 
            getBounds().x, getBounds().y+getBounds().height );
        
        
    }
    
    public Line2D.Double right(){
        return new Line2D.Double(getBounds().x+getBounds().width  ,getBounds().y, 
            getBounds().x, getBounds().y+getBounds().height );
        
    }

}
