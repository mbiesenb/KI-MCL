package sample;

import java.util.ArrayList;

public class Map {

    private ArrayList<Line> lines = new ArrayList<Line>();
    private ArrayList<Point> polygon = new ArrayList<Point>();
    private ArrayList<Particle> particles = new ArrayList<Particle>();
    private int x, y;

    public Map(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void addLine(Line l){
        lines.add(l);
    }

    public void addPoint(Point p){
        polygon.add(p);
    }

    public void addParticle(Particle p){
        particles.add(p);
    }

    public ArrayList<Line> getLines(){
        return lines;
    }

    public ArrayList<Point> getPolygon(){
        return polygon;
    }

    public int getPolygonPointCount(){
        return polygon.size();
    }

    public boolean checkPointInsidePolygon(Point p){
        int i, j, vertNum;
        boolean c = false;
        vertNum = polygon.size();
        double testx = p.x;
        double testy = p.y;

        for (i=0, j=vertNum-1; i < vertNum; j = i++){
            if(  ((polygon.get(i).y > p.y) != (polygon.get(j).y > p.y))
                    && (testx < (polygon.get(j).x - polygon.get(i).x) * (testy-polygon.get(i).y) / (polygon.get(j).y-polygon.get(i).y) + polygon.get(i).x) )
            {
                c = !c;
            }
        }

        return c;
    }


}
