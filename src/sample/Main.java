package sample;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class Main extends Application {
    //private static final int SCALE_FACTOR = 1;
    public static final int CANVAS_WITDH = 1200;
    public static final int CANVAS_HEIGHT = 500;
    public static final int SVG_MAX_WIDTH = 600;
    public static final int SVG_MAX_HEIGHT = 150;
    public static final int PARTICLE_LENGTH = 7;

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("MCL Gui - C_LOOSER");
        Group root = new Group();
        //Canvas canvas = new Canvas(CANVAS_WITDH+ 2, CANVAS_HEIGHT+ 2);
        Canvas canvas = new Canvas(CANVAS_WITDH, CANVAS_HEIGHT);

        GraphicsContext gc = canvas.getGraphicsContext2D();
        drawMap(gc);
        root.getChildren().add(canvas);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }


    /*
        Helpers´ by the master himself ;)
     */
    private double rel ( int max , int cur){
        return ( (double) cur / (double) max);
    }
    private int abs ( int  max , double rel){
        return  ( int ) ((double)max * (double)rel);
    }
    private int absMapX ( double rel){
        int a = abs(CANVAS_WITDH ,rel);
        return  a;
    }
    private int absMapY ( double rel){
        return  abs(CANVAS_HEIGHT, rel);
    }
    private Point absMapPoint ( Point point){
        return new Point(abs(CANVAS_WITDH , point.x) , abs(CANVAS_HEIGHT , point.y));
    }
    private double distance ( Point a , Point b ){
        return Math.sqrt(Math.pow(b.x - a.x , 2) + Math.pow(b.y - b.y , 2) );
    }
    private boolean isBetween( Point a , Point b , Point c){
            double dline =  Math.sqrt(Math.pow(b.x - a.x , 2) + Math.pow(b.y - b.y , 2) );
            double dPoint =  Math.sqrt(Math.pow(c.x - a.x , 2) + Math.pow(c.y - b.y , 2) );
            return dPoint <= dline;
    }

    private ArrayList<Point> rayCast (Point from , Point to ,  ArrayList<Line> lines){
        ArrayList<Point> points = new ArrayList<>();
        Point shortest = null;
        for (Line l : lines) {
            Point lineA = new Point(l.x1, l.y1);
            Point lineB = new Point(l.x2, l.y2);
            Point xp = calculateInterceptionPoint(from.translate(), to.translate(), lineA.translate(), lineB.translate()).translate();

            if (xp != null) {
                boolean add = false;
                if ( lineA.x == lineB.x){
                    if ( xp.y >= lineA.y && xp.y <= lineB.y){
                        add = true;
                    }
                    if ( xp.y >= lineB.y && xp.y <= lineA.y){
                        add = true;
                    }
                }
                else if ( lineA.y == lineB.y){
                    if ( xp.x >= lineA.x && xp.x <= lineB.x){
                        add = true;
                    }
                    if ( xp.x >= lineB.x && xp.x <= lineA.x){
                        add = true;
                    }
                }
                if ( add) {
                    points.add(xp);
                }
            }
        }
        return points;
    }

    private Point getShortest( Point from , ArrayList<Point> points){
        Point shortest = null;
        for ( Point p : points){
            if ( shortest == null){
                shortest = p;
            }else{
                if ( distance(from , p)  <  distance(shortest , p)){
                    shortest = p;
                }
            }
        }
        return shortest;
    }

    private voi
    private void drawMap(GraphicsContext gc) {

        File map = new File("map.svg");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        Document doc;
        Map m = new Map(SVG_MAX_WIDTH, SVG_MAX_HEIGHT);
        Random rand = new Random();

        try {
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(map);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("line");
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node n = nList.item(temp);
                if (n.getNodeType() == Node.ELEMENT_NODE) {
                    NamedNodeMap nnm = n.getAttributes();
                    int x1 = Integer.parseInt(nnm.getNamedItem("x1").getNodeValue().replace("px", ""));
                    int y1 = Integer.parseInt(nnm.getNamedItem("y1").getNodeValue().replace("px", ""));
                    int x2 = Integer.parseInt(nnm.getNamedItem("x2").getNodeValue().replace("px", ""));
                    int y2 = Integer.parseInt(nnm.getNamedItem("y2").getNodeValue().replace("px", ""));
                    m.addLine(new Line(rel(SVG_MAX_WIDTH , x1), rel(SVG_MAX_HEIGHT , y1), rel(SVG_MAX_WIDTH , x2), rel(SVG_MAX_HEIGHT , y2)));
                    m.addPoint(new Point(rel(SVG_MAX_WIDTH , x1), rel(SVG_MAX_HEIGHT , y1)));
                }
            }

        } catch (Exception e) {
            System.out.println(e.toString());
        }


        gc.setFill(Color.BLACK);
        gc.setStroke(Color.DARKRED);
        gc.setLineWidth(2);

        int olli = 0;
        for (int i = 0; i < 50; ) {
            Point randPoint = new Point(rand.nextDouble(), rand.nextDouble());//rand.nextInt(150));
            //Point randPoint = new Point(0.5, 0.5);//rand.nextInt(150));
            if (m.checkPointInsidePolygon(randPoint)) {
                double randRotation = rand.nextDouble() * Math.PI  * 2;
                //double randRotation = 0.1* Math.PI  * 2;
                double r = 0.050;
                double x = randPoint.x + Math.cos(randRotation) * r;
                double y = randPoint.y  + Math.sin(randRotation) * r  * (CANVAS_WITDH / CANVAS_HEIGHT); // <-- Good quess

                Point p = new Point(x,  y);
                gc.strokeLine(absMapX(randPoint.x), absMapY(randPoint.y ), absMapX(x), absMapY(y));
                gc.fillOval(absMapX(randPoint.x), absMapY(randPoint.y ), 4,4 );
                m.addParticle(new Particle(randPoint, randRotation));
                i++;
                int bum = 0;
            }
        }
        ArrayList<Point> intersects = rayCast()
        if ( shortest != null) {
            gc.fillOval(absMapX(shortest.x), absMapY(shortest.y), 8, 8);
        }

        System.out.println(olli);
        int numOfPoints = m.getPolygonPointCount();
        double[] x_point_arr = new double[numOfPoints];
        double[] y_point_arr = new double[numOfPoints];
        int i = 0;
        for (Point p : m.getPolygon()) {
            x_point_arr[i] = (double) absMapX(p.x);
            y_point_arr[i] = (double) absMapY( p.y);
            i++;
        }
        gc.strokePolyline(x_point_arr, y_point_arr, numOfPoints);


        System.out.println(m.checkPointInsidePolygon(new Point(205, 10)));

    }


    public static void main(String[] args) {
        launch(args);

    }


    static Point calculateInterceptionPoint(Point A, Point B, Point C, Point D) {
        // Line AB represented as a1x + b1y = c1
        double a1 = B.y - A.y;
        double b1 = A.x - B.x;
        double c1 = a1 * (A.x) + b1 * (A.y);

        // Line CD represented as a2x + b2y = c2
        double a2 = D.y - C.y;
        double b2 = C.x - D.x;
        double c2 = a2 * (C.x) + b2 * (C.y);

        double determinant = a1 * b2 - a2 * b1;

        if (determinant == 0) {
            // The lines are parallel. This is simplified
            // by returning a pair of FLT_MAX
            return null;//new Point(Double.MAX_VALUE, Double.MAX_VALUE);
        } else {
            double x = (b2 * c1 - b1 * c2) / determinant;
            double y = (a1 * c2 - a2 * c1) / determinant;
            return new Point(x, y);
        }
    }

}
