import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.geom.*;
import javax.swing.*;
import java.util.*;

public class Tank extends Applet implements Runnable, MouseMotionListener, MouseListener, KeyListener
{    
    public class ImageMatrix{
        public int currentImg;
        public Point currentPosition;
        public Point imgDim;

        public ImageMatrix(Point imgDim, int currentImg){
            this.currentImg = currentImg;
            this.imgDim = imgDim;
            this.currentPosition = CurrentImageToPoint(currentImg);
        }

        public Point CurrentImageToPoint(int tilenum)
        {
            return new Point((tilenum % imgDim.x), (tilenum / imgDim.y));
        }

        public int PointToCurrentImage(Point Point)
        {
            return (Point.y + 1) * (imgDim.x) - ((imgDim.x) - (Point.x));
        }

        public void update(){
            currentImg = PointToCurrentImage(currentPosition);
        }
    }
    public class BulletsAndExplosions{
        public Sprite bullet;
        public AnimatedSprite explosion;

        public BulletsAndExplosions(AnimatedSprite explosion, Sprite bullet){
            this.explosion = explosion;
            this.bullet = bullet;
        }
    }
    public class Enemy{
        public Sprite bullet;
        public Sprite turret;
        public Enemy(Sprite bullet, Sprite turret){
            this.bullet = bullet;
            this.turret = turret;
        }
    }
    public class ImagesAndBounds{
        public ImageEntity image; 
        public ArrayList<Rectangle> r;
        public LinkedList<Enemy> e;
        public ImagesAndBounds(ImageEntity image, ArrayList<Rectangle> r, LinkedList<Enemy> e){
            this.image = image;
            this.r = r;
            this.e = e;
        }

    }

    public SoundClip explosionClip;
    public SoundClip blastClip;
    Thread loop;
    BufferedImage b;
    AffineTransform I = new AffineTransform();
    Graphics2D G;
    Sprite turret;
    Sprite tank;

    LinkedList<Sprite> bullet;
    int mx, my;
    double o,a;
    Random r = new Random();
    ImageMatrix i;
    Rectangle upperBound, lowerBound, leftBound, rightBound;
    boolean up, left, down, right, shot;
    AnimatedSprite smoke;
    LinkedList<BulletsAndExplosions> be;
    String test = "test";
    LinkedList<ImagesAndBounds> images;
    ArrayList<ArrayList<Rectangle>> rectangleBounds;
    Testing _a;
    int timer = 0;
    int bulletPointer = 0;
    public void initThings(){
        //bullets
        bullet = new LinkedList<Sprite>();
        //rectanglebounds
        upperBound = new Rectangle(0, -100, this.getSize().width, 100);
        lowerBound = new Rectangle(0, this.getSize().height, this.getSize().width, 100);
        leftBound = new Rectangle(-100, 0, 100, this.getSize().height);
        rightBound = new Rectangle(this.getSize().width, 0, 100, this.getSize().height);
        //enemies

        Map m1 = new HashMap(); 
        m1.put(0, new ArrayList<Point>(Arrays.asList(new Point(30,54), new Point(71,400))));
        m1.put(1, new ArrayList<Point>(Arrays.asList(new Point(30,54), new Point(71,400))));
        m1.put(2, new ArrayList<Point>(Arrays.asList(new Point(42,130), new Point(264,278), new Point(83,345))));
        m1.put(4, new ArrayList<Point>(Arrays.asList(new Point(30,54), new Point(71,400))));

        //images
        _a = new Testing(); 
        rectangleBounds = _a.rectangles;

        images = new LinkedList<ImagesAndBounds>();

        ArrayList<String> imageNames = new ArrayList<String>(Arrays.asList("maps/Tile 1.png","maps/Tile 2.png","maps/Tile 3.png",
                    "maps/Tile 4.png","maps/Tile 5.png","maps/Tile 6.png",
                    "maps/Tile 7.png","maps/Tile 8.png","maps/Tile 9.png"));

        ArrayList<ArrayList<Rectangle>> rectangles = new ArrayList<ArrayList<Rectangle>>();
        for(int a = 0; a < 9; a ++){
            //int n = r.nextInt(imageNames.size());
            LinkedList<Enemy> e = new LinkedList<Enemy>();
            if(m1.containsKey(a)){
                ArrayList<Point> ep = (ArrayList<Point>)m1.get(a);                
                for(int b=0; b<ep.size(); b++  ){
                    Sprite bull = new Sprite(this,G);
                    bull.load("bullet.png");
                    Sprite tur = new Sprite(this,G);
                    tur.load("enemy.png");
                    tur.setPosition(new Point2D.Double(ep.get(b).x, ep.get(b).y));
                    tur.setAlive(true);
                    e.add(new Enemy(bull,tur));
                }
            }
            ImageEntity temp = new ImageEntity(this);
            temp.setGraphics(G);
            temp.load(imageNames.get(a));
            images.add(new ImagesAndBounds(temp, rectangleBounds.get(a), e));

            //imageNames.remove(n);
            //rectangleBounds.remove(n);
        }
        i = new ImageMatrix(new Point(3,3),4);
        //turret
        turret = new Sprite(this,G);
        turret.load("turret.png");
        turret.setPosition(new Point2D.Double(this.getSize().width/2, (this.getSize().height/2)));
        //tank
        tank = new Sprite(this,G);
        tank.load("tank.png");
        tank.setPosition(new Point2D.Double(this.getSize().width/2, this.getSize().height/2));
        tank.health = 60;
        //smoke
        smoke = new AnimatedSprite(this,G);
        smoke.load("smoke.png", 5,5, 109,104);
        smoke.frameDelay = 8;
        //explosions
        //bullets and explosions
        be = new LinkedList<BulletsAndExplosions>();
        //sound
        //explosionClip = new SoundClip();
        //explosionClip.load("explosion.wav");
        blastClip = new SoundClip();
        blastClip.load("blast.wav");
    }

    public void init()
    {
        b = new BufferedImage(this.getSize().width, this.getSize().height, BufferedImage.TYPE_INT_RGB);
        G = b.createGraphics(); 

        initThings();

        addMouseMotionListener(this);
        addMouseListener(this);
        addKeyListener(this);
    }

    public void start()
    {
        loop = new Thread(this);
        loop.start();
    }

    public void stop()
    {

    }

    public void paint(Graphics g)
    {
        g.drawImage(b, 0, 0, this);
    }

    public void stats()
    {
        G.setColor(Color.WHITE);
        G.drawString("TankPos: " + tank.position().x + "," + tank.position().y,5,10);
        G.drawString("TankAngle: " + tank.faceAngle(),5, 25 );
        G.drawString("TurretAngle: " + turret.faceAngle(),5, 40 );
        G.drawString("MousePos: " + mx + "," + my,5, 55);
        G.drawString(test,5, 70);
    }

    public void tank()
    {
        tank.draw();
        
        tank.transform();
        tank.updatePosition();
        tank.updateRotation();
    }

    public void turret(){
        turret.draw();
        
        turret.transform();
        turret.setPosition(new Point2D.Double(tank.center().x - turret.imageWidth()/2, tank.center().y - turret.imageHeight()/2));
        turret.updatePosition();
        turret.updateRotation();
    }

    public void stopExplosions(){
        for(int a = 0; a < be.size(); a++){
            be.get(a).explosion.currentState = 1;
            be.get(a).explosion.alive = false;
            be.get(a).bullet.setAlive(false);
        }
    }

    public void boundaryhit(Sprite a){
        for(int b = 0; b < images.get(i.currentImg).r.size(); b++){
            //if(a.collidesWith(images.get(i.currentImg).r.get(b))){            
            Rectangle r = images.get(i.currentImg).r.get(b);

            Line2D.Double top = new Line2D.Double(r.x,r.y, 
                    r.x+r.width, r.y  );
            Line2D.Double bottom = new Line2D.Double(r.x, r.y+r.height, 
                    r.x+r.width, r.y+r.height  );
            Line2D.Double left = new Line2D.Double(r.x,r.y, 
                    r.x, r.y+r.height );
            Line2D.Double right = new Line2D.Double(r.x+r.width  ,r.y, 
                    r.x, r.y+r.height );

            //top of tank hits bottom of border
            if(tank.top().intersects(r)){
                tank.setPosition( new Point2D.Double(tank.position().x, r.y+r.height+2)  );
            }

            //tank collides at bottom
            if(tank.bottom().intersects(r)){
                tank.setPosition( new Point2D.Double(tank.position().x, r.y-tank.imageHeight()-2 )  );
            }

            //tank collides at left

            if(tank.left().intersects(r)){
                //tank.setPosition( new Point2D.Double(r.x -tank.imageWidth()-3, tank.position().y)  );
            }

            //tank collides at right

            if(tank.right().intersects(r)){
                //tank.setPosition( new Point2D.Double(r.x+r.width + 3, tank.position().y)  );
            }
            // }
        }

    }

    public void wallhit(Sprite a)
    {
        if(a.collidesWith(upperBound)){

            if(i.currentPosition.y > 0){
                i.currentPosition.y--;
                i.update();
                tank.setPosition(new Point2D.Double(tank.position().x, this.getSize().height- (tank.imageHeight() + 1 ))  );
                stopExplosions();
            }
            else
            {
                tank.setPosition(new Point2D.Double(tank.position().x, 1)  );
            }
        }
        if(a.collidesWith(lowerBound)){

            if(i.currentPosition.y < i.imgDim.y-1){
                i.currentPosition.y++;
                i.update();
                tank.setPosition(new Point2D.Double(tank.position().x, 1));
                stopExplosions();
            }
            else
            {
                tank.setPosition(new Point2D.Double(tank.position().x, this.getSize().height- (tank.imageHeight() + 1 ))  );
            }
        }
        if(a.collidesWith(leftBound)){

            if(i.currentPosition.x > 0){
                i.currentPosition.x--;
                i.update();
                tank.setPosition(new Point2D.Double(this.getSize().width - (tank.imageWidth()+1), tank.position().y));
                stopExplosions();
            }
            else
            {
                tank.setPosition(new Point2D.Double(1, tank.position().y));
            }
        }
        if(a.collidesWith(rightBound)){

            if(i.currentPosition.x < i.imgDim.x-1){
                i.currentPosition.x++;
                i.update();
                tank.setPosition(new Point2D.Double(1, tank.position().y));
                stopExplosions();
            }
            else
            {
                tank.setPosition(new Point2D.Double(this.getSize().width - (tank.imageWidth()+1), tank.position().y));
            }
        }
    }

    public void bullethit(Sprite a, AnimatedSprite b)
    {
        if(a.alive()){
            if(a.collidesWith(upperBound)){
                a.setState(0);
                b.position = new Point((int)a.position().x - b.frameWidth/2, (int)a.position().y - b.frameHeight/2);
                b.alive = true;
            }
            if(a.collidesWith(lowerBound)){
                a.setState(0);
                b.position = new Point((int)a.position().x - b.frameWidth/2, (int)a.position().y - b.frameHeight/2);
                b.alive = true;
            }
            if(a.collidesWith(leftBound)){
                a.setState(0);
                b.position = new Point((int)a.position().x - b.frameWidth/2, (int)a.position().y - b.frameHeight/2);
                b.alive = true;
            }
            if(a.collidesWith(rightBound)){
                a.setState(0);
                b.position = new Point((int)a.position().x - b.frameWidth/2, (int)a.position().y - b.frameHeight/2);
                b.alive = true;
            }
            for(int c = 0; c < images.get(i.currentImg).e.size(); c++){
                if(images.get(i.currentImg).e.get(c).turret.alive()){
                    if(images.get(i.currentImg).e.get(c).turret.collidesWith(a)){
                        a.setState(0);
                        b.position = new Point((int)a.position().x - b.frameWidth/2, (int)a.position().y - b.frameHeight/2);
                        b.alive = true;
                        images.get(i.currentImg).e.get(c).turret.setAlive(false);
                    }
                }
            }
        }
    }

    public void bulletsAndExplosions()
    {

        for(int a =0; a < be.size(); a++){
            bullethit(be.get(a).bullet, be.get(a).explosion);
            if(be.get(a).bullet.state()==1){
                be.get(a).bullet.draw();
                //bullet.get(a).drawBounds(Color.WHITE);
                be.get(a).bullet.transform();
                be.get(a).bullet.updatePosition();
                be.get(a).bullet.updateRotation();
            }

            if(be.get(a).explosion.currentState==0 && be.get(a).explosion.alive){
                be.get(a).explosion.draw();

            }

            if(be.get(a).explosion.currentFrame > be.get(a).explosion.totalFrames-2){
                be.get(a).explosion.currentState=1;
            }

        }

    }
    
    public void drawturretandtankboundary(){
        turret.drawBounds(Color.WHITE);
        tank.drawBounds(Color.WHITE);
    }

    public void drawallboundaries(){

        for(int b = 0; b < images.get(i.currentImg).r.size(); b++){
            G.draw(images.get(i.currentImg).r.get(b));
        }
    }

    public void fireenemybullets(Sprite a, Sprite b){
        a.setPosition(calcRotatedOrigin(new Point2D.Double(b.center().x, b.center().y ), b.faceAngle(), 
                new Point2D.Double(b.center().x, b.pos.y), 
                new Point2D.Double(a.imageWidth(), a.imageHeight() ) 
            ));

        a.setFaceAngle( b.faceAngle() );

        a.setVelocity(new Point2D.Double((calcAngleMoveX(b.faceAngle() , 5)) ,
                calcAngleMoveY(b.faceAngle() , 5)   
            ));
        a.setAlive(true);

    }

    public void drawenemybullets(Sprite a){
        if(a.alive()){
            a.draw();
            a.transform();
            a.updatePosition();
            a.updateRotation();
        }
    }

    public void enemyhittank(Sprite a, Sprite b){
        if(a.collidesWith(b)){
            a.setAlive(false);
            b.health = b.health - .07;
        }
    }

    public void drawenemies(){

        for(int a = 0; a < images.get(i.currentImg).e.size(); a ++    ){
            if(images.get(i.currentImg).e.get(a).turret.alive()){
                images.get(i.currentImg).e.get(a).turret.draw();
                images.get(i.currentImg).e.get(a).turret.transform();
                images.get(i.currentImg).e.get(a).turret.updatePosition();
                images.get(i.currentImg).e.get(a).turret.updateRotation();
                computeRotation(tank, images.get(i.currentImg).e.get(a).turret);

                if(timer ==90){
                    fireenemybullets(images.get(i.currentImg).e.get(a).bullet,images.get(i.currentImg).e.get(a).turret );
                }
                enemyhittank(images.get(i.currentImg).e.get(a).bullet,tank);
                drawenemybullets(images.get(i.currentImg).e.get(a).bullet);

            }        
        }
        timer++;
        if(timer>90){
            timer = 0;
        }

    }

    public void showhealth(){
        G.fillRect((int)tank.position().x, (int)tank.position().y-10, (int)tank.health, 4 );
    }

    public boolean enemyalive(Sprite a){
        if(a.alive()){
            return true;
        }
        return false;
    }

    public boolean checkwin(){
        for(int a = 0; a < images.size(); a++){
            for(int b = 0; b < images.get(a).e.size(); b++){
                if(enemyalive(images.get(a).e.get(b).turret)){
                    return false;
                }
            }
        }
        return true;
    }

    public void update(Graphics g)
    {
        G.setTransform(I);
        G.setPaint(Color.RED);
        G.fillRect(0,0,this.getSize().width,this.getSize().height);

        images.get(i.currentImg).image.draw();

        //stats();

        movementChecker();
        tank();
        showhealth();

        turret();
        drawenemies();
        bulletsAndExplosions();

        wallhit(tank);
        //drawallboundaries();
        //drawturretandtankboundary();
        boundaryhit(tank);

        if(smoke.alive){
            smoke.draw();
            if(smoke.currentFrame > smoke.totalFrames-2){
                smoke.alive = false;
                smoke.currentFrame = 0;
            }
        }

        if(checkwin()){
            G.setPaint(Color.WHITE);
            G.drawString("YOU WIN",250,250);
        }

        if(tank.health < 0){
            G.setPaint(Color.BLACK);
            G.fillRect(0,0,this.getSize().width,this.getSize().height);
            G.setPaint(Color.WHITE);
            G.drawString("YOU LOSE",250,250);
        }

        paint(g);
    }    

    public void run()
    {
        Thread t = Thread.currentThread();
        while(t==loop){
            try{
                Thread.sleep(16);
            }
            catch(InterruptedException e){
                e.printStackTrace();
            }
            repaint();

        }
    }

    public void mouseDragged(MouseEvent e){}

    public void mouseMoved(MouseEvent e){
        mx=e.getX(); 
        my=e.getY();
        o = my - turret.center().y;
        a = mx - turret.center().x;
        if(mx<turret.center().x){
            turret.setFaceAngle(270+Math.toDegrees(Math.atan(o/a)));
        }      
        if(mx>turret.center().x){
            turret.setFaceAngle(90+Math.toDegrees(Math.atan(o/a)) );
        }  

    }

    public void computeRotation(Sprite tank, Sprite turret){
        Double x= tank.center().x;
        Double y= tank.center().y;
        Double o = y - turret.center().y;
        Double a = x - turret.center().x;
        if(x<turret.center().x){
            turret.setFaceAngle(270+Math.toDegrees(Math.atan(o/a)));
        }      
        if(x>turret.center().x){
            turret.setFaceAngle(90+Math.toDegrees(Math.atan(o/a)) );
        }  
    }

    public void mouseClicked(MouseEvent e){
        AnimatedSprite tempe = new AnimatedSprite(this,G);
        tempe.load("explosion.png", 6, 30, 128, 128);
        tempe.frameDelay = 2;
        Sprite tempb = new Sprite(this, G);  
        tempb.load("bullet.png");
        tempb.setPosition(calcRotatedOrigin(new Point2D.Double(turret.center().x, turret.center().y ), turret.faceAngle(), 
                new Point2D.Double(turret.center().x, turret.pos.y), 
                new Point2D.Double(tempb.imageWidth(), tempb.imageHeight() ) 
            ));

        tempb.setFaceAngle( turret.faceAngle() );

        tempb.setVelocity(new Point2D.Double((calcAngleMoveX(turret.faceAngle() , 4)) ,
                calcAngleMoveY(turret.faceAngle() , 4)   
            ));
        tempb.setState(1);
        tempb.setAlive(true);

        be.add(new BulletsAndExplosions(tempe, tempb));

        Point2D.Double temp = calcRotatedOrigin(new Point2D.Double(turret.center().x, turret.center().y ), turret.faceAngle(), 
                new Point2D.Double(turret.center().x, turret.pos.y), 
                new Point2D.Double(smoke.frameWidth, smoke.frameHeight) ) ;
        smoke.position = new Point((int)temp.x, (int)temp.y);
        smoke.alive = true;
        blastClip.play();

    }

    public void mouseEntered(MouseEvent e){}

    public void mouseExited(MouseEvent e){}

    public void mousePressed(MouseEvent e){}

    public void mouseReleased(MouseEvent e){}

    public void keyReleased(KeyEvent k){
        int keyCode = k.getKeyCode();
        switch ((int)keyCode) 
        {
            case KeyEvent.VK_UP:
            up = false;
            break;
            
            case KeyEvent.VK_DOWN:
            down = false;
            break;
        }
    }

    public void keyTyped(KeyEvent k){}

    public void keyPressed(KeyEvent k){
        int keyCode = k.getKeyCode();
        switch ((int)keyCode) 
        {
            case KeyEvent.VK_LEFT:
            left = true;
            break;

            case KeyEvent.VK_RIGHT:
            right = true;
            break;

            case KeyEvent.VK_UP:
            up = true;
            break;

            case KeyEvent.VK_DOWN:
            down = true;
            break;

        }
    }

    public void movementChecker(){
        if(up){
            tank.setVelocity(new Point2D.Double(
                    calcAngleMoveX(tank.faceAngle() , 4) ,
                    calcAngleMoveY(tank.faceAngle() , 4)   
                ));   
        }
        else{
            tank.setVelocity(new Point2D.Double(0,0));
        }
        
        if(down){
            tank.setVelocity(new Point2D.Double(
                    calcAngleMoveX(tank.faceAngle() , -4) ,
                    calcAngleMoveY(tank.faceAngle() , -4)   
                ));   
            //tank.setVelocity(new Point2D.Double(tank.velocity().x, tank.velocity().y));
        }

        if(left){

            tank.setFaceAngle(tank.faceAngle() -5);
            if(tank.faceAngle() < 0) tank.setFaceAngle(360-5);
            turret.setFaceAngle(turret.faceAngle() -5);
            if(turret.faceAngle() < 0) turret.setFaceAngle(360-5);
            left = false;

        }

        if(right){
            tank.setFaceAngle(tank.faceAngle() +5);
            if(tank.faceAngle() >360) tank.setFaceAngle(5);            
            turret.setFaceAngle(turret.faceAngle() +5);
            if(turret.faceAngle() >360) turret.setFaceAngle(5);     
            right = false;
        }

    }

    public double calcAngleMoveX(double angle, double num) {
        return (double) (Math.sin(Math.toRadians(angle))) * num;
    }

    public double calcAngleMoveY(double angle, double num) {
        return (double) (Math.cos(Math.toRadians(angle))) * num*-1;
    }

    public Point2D.Double calcRotatedOrigin(Point2D.Double B, Double theta, Point2D.Double A, Point2D.Double offset){
        theta = -(theta + 180) % 360;
        //b is orign, a is point rotated about origin
        Double x = B.x + (A.x - B.x)*Math.cos(Math.toRadians(theta)) - (A.y - B.y)*Math.sin(Math.toRadians(theta)); 
        Double y = B.y + (A.x - B.x)*Math.sin(Math.toRadians(theta)) - (A.y - B.y)*Math.cos(Math.toRadians(theta));
        x-=(offset.x/2);
        y-=(offset.y/2);
        return new Point2D.Double(x,y);
    }
}