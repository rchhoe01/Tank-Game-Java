import static java.nio.file.StandardOpenOption.*;
import java.nio.file.*;
import java.io.*;
import java.awt.geom.*;
import java.util.*;
import java.awt.*;

public class Testing
{
    public String[] names = {"Tile 1 Bounds.txt","Tile 2 Bounds.txt","Tile 3 Bounds.txt","Tile 4 Bounds.txt","Tile 5 Bounds.txt","Tile 6 Bounds.txt","Tile 7 Bounds.txt","Tile 8 Bounds.txt","Tile 9 Bounds.txt"};
    public ArrayList<ArrayList<Rectangle>> rectangles;
    public Testing()
    {
        rectangles = new ArrayList<ArrayList<Rectangle>>();
        for(int a = 0; a < names.length; a ++){
            rectangles.add(fileToRectangleList("bounds/" + names[a]));
        }
        printRectangles();
    }

    public ArrayList<Rectangle> fileToRectangleList(String s)
    {
        Path file = Paths.get(s);
        ArrayList<Rectangle> rectangles = new ArrayList<Rectangle>();
        try (InputStream in = Files.newInputStream(file);
        BufferedReader reader =
            new BufferedReader(new InputStreamReader(in))) {
            String line = null;            
            while ((line = reader.readLine()) != null) {
                //System.out.println(line);

                String[] meep = line.split("[\\s\\{},Rectangle]+");
                if(meep.length>1){
                    int [] a = new int[4]; 
                    for(int b = 0; b < a.length; b++){
                        a[b] = Integer.parseInt(meep[b+2]);
                    }
                    rectangles.add(new Rectangle(a[0], a[1], a[2], a[3]));
                }
            }            
        } catch (IOException x) {
            System.err.println(x);
        }
        return rectangles;
    }
    
    public void printRectangles(){
        for(int a =0; a < rectangles.size(); a ++){
            for(int b =0; b < rectangles.get(a).size(); b++){
                int x = rectangles.get(a).get(b).x; 
                int y = rectangles.get(a).get(b).y; 
                int width = rectangles.get(a).get(b).width; 
                int height = rectangles.get(a).get(b).height; 
                System.out.print("{"+ x + ","+ y  + "," + width +","+ height +"}");
            }
            System.out.println();
        }
    
    }

    public static void main(String args[]){
        new Testing();
    }
}
