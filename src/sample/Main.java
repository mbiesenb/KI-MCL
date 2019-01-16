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


/**
 *  SAMPLE -->
 *
 *
 *
 *  Ablauf:
 *      1. Initialer Belief
 *         k Samples auf dem Bildschirm erstellen, die Gleichverteilt auf allen Möglichen Positionen verteilt sind
 *         Der importance factor ist 1/k.
 *         Ein Partikel ( Sample ) repräsentiert eine Position
 *
 *         Ziel: Partikel ( Samples ) zu generieren, welche am ( Hochpunkt ) der Wahrscheinlichkeitsdichte liegen
 *
 *      2. Belief aktualisieren
 *          Ein Sample wird zufällig aus der Menge genommen
 *          Der importance factor gibt die Auswahlwahrscheinlichkeit.
 */
public class Main extends Application {
    //private static final int SCALE_FACTOR = 1;
    public static final int CANVAS_WITDH = 1200;
    public static final int CANVAS_HEIGHT = 300;

    public static final int SVG_MAX_WIDTH = 600;
    public static final int SVG_MAX_HEIGHT = 150;
    public static final int PARTICLE_COUNT = 1;
    public static final int BUILDING_WIDTH_CM = 600;
    public static final int BUILDING_HEIGHT_CM = 150;
    public static final int PARTICLE_LENGTH = 7;

    Map m = new Map(SVG_MAX_WIDTH, SVG_MAX_HEIGHT);
    public ArrayList<Particle> partices = new ArrayList<>();


    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("MCL GUI - D_GELB");
        Group root = new Group();
        //Canvas canvas = new Canvas(CANVAS_WITDH+ 2, CANVAS_HEIGHT+ 2);
        Canvas canvas = new Canvas(CANVAS_WITDH, CANVAS_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        initWall();
        initParticles();
        drawMap(gc);
        root.getChildren().add(canvas);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }


    private void initWall(){
        File map = new File("map.svg");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        Document doc;
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
                    m.addLine(new Line(Helper.rel(SVG_MAX_WIDTH, x1), Helper.rel(SVG_MAX_HEIGHT , y1), Helper.rel(SVG_MAX_WIDTH , x2), Helper.rel(SVG_MAX_HEIGHT , y2)));
                    m.addPoint(new Point(Helper.rel(SVG_MAX_WIDTH , x1), Helper.rel(SVG_MAX_HEIGHT , y1)));
                }
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }


        /*int numOfPoints = m.getPolygonPointCount();
        double[] x_point_arr = new double[numOfPoints];
        double[] y_point_arr = new double[numOfPoints];
        int i = 0;
        for (Point p : m.getPolygon()) {
            x_point_arr[i] = (double) absMapX(p.x);
            y_point_arr[i] = (double) absMapY( p.y);
        }*/
    }

    private void initParticles ( ){
        Random rand = new Random();
        for (int i = 0; i < PARTICLE_COUNT;) {
            Point particleCenter = new Point(rand.nextDouble(), rand.nextDouble());//rand.nextInt(150));
            //Point particleCenter = new Point(0.65, 0.15);//rand.nextInt(150));
            if (m.checkPointInsidePolygon(particleCenter)) {
                double randRotation = rand.nextDouble() * Math.PI  * 2;
                //double randRotation = (Math.PI *2) * (160.0/360.0);
                System.out.println("randomRotation" + randRotation);
                double r = 0.050;
                double x = particleCenter.x + Math.cos(randRotation) * r;
                double y = particleCenter.y  + Math.sin(randRotation) * r  * (CANVAS_WITDH / CANVAS_HEIGHT); // <-- Good quess

                Point particleDirection = new Point(x,  y);
                ArrayList<Point> intersects = Helper.rayCast(particleCenter , particleDirection , m.getLines());

                Point shortestIntersect = Helper.getShortest(particleCenter , intersects);

                Particle particle = new Particle(particleCenter, randRotation);
                double intersectDistance = Helper.distance(Helper.absRealPoint(particleCenter) , Helper.absRealPoint(shortestIntersect));
                //absDistance =  distance(new Point( absMapX(particle) , absMapY() ) , new Point(absMapX() , absMapY()));
                // Set the magic stuff
                particle.setIntersect(shortestIntersect , Helper.absMapPoint(particleCenter) , intersectDistance);
                System.out.println("Intersect Distance: " + intersectDistance);

                partices.add(particle);
                m.addParticle(particle);
                i++;
            }
        }


        //System.out.println(olli);

        //gc.strokePolyline(x_point_arr, y_point_arr, numOfPoints);
        //System.out.println(m.checkPointInsidePolygon(new Point(205, 10)));
    }
    private void drawMap(GraphicsContext gc) {

        gc.setFill(Color.BLACK);
        gc.setStroke(Color.DARKRED);
        gc.setLineWidth(2);

        for ( Line l : m.getLines()){
            gc.strokeLine(Helper.absMapX(l.x1) , Helper.absMapY(l.y1) , Helper.absMapX(l.x2 ), Helper.absMapY(l.y2));
        }

        gc.setLineDashes(10);
        for ( Particle particle : partices) {
            // draw Particle here

            double r = 0.005;
            double x = particle.centerPoint.x + Math.cos(particle.rotation) * r;
            double y = particle.centerPoint.y  + Math.sin(particle.rotation) * r  * (CANVAS_WITDH / CANVAS_HEIGHT); // <-- Good quess

            double deltaX = Math.cos(particle.rotation) * r;
            double deltaY = Math.sin(particle.rotation) * r  * (CANVAS_WITDH / CANVAS_HEIGHT); // <-- Good quess

            Point lineA = new Point(particle.centerPoint.x + deltaX , particle.centerPoint.y + deltaY);
            Point lineB = new Point(particle.centerPoint.x - deltaX , particle.centerPoint.y - deltaY);

            gc.strokeLine(Helper.absMapX(lineA.x), Helper.absMapY(lineA.y), Helper.absMapX(lineB.x), Helper.absMapY(lineB.y));
            gc.fillOval(Helper.absMapX(particle.centerPoint.x)-3, Helper.absMapY(particle.centerPoint.y)-3, 6, 6);
            gc.fillOval( Helper.absMapPoint(lineA).x-2 , Helper.absMapPoint(lineA).y-2 , 4,4 );
            //Point particleDirection = new Point(x,  y);
            gc.strokeLine(Helper.absMapPoint(lineB).x,Helper.absMapPoint(lineB).y , Helper.absMapPoint(particle.intersectPoint.relPoint).x,Helper.absMapPoint(particle.intersectPoint.relPoint).y);
            //gc.strokeLine(absMapX(particle.centerPoint.x), absMapY(particle.centerPoint.y), absMapX(particleDirection.x), absMapY(particleDirection.y));
            //gc.fillOval(absMapX(randPoint.x), absMapY(randPoint.y), 4, 4);
        }
    }


    public static void main(String[] args) {
        launch(args);

    }




}
