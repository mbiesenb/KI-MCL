package sample;

public class Particle {
    Point centerPoint;
    double rotation;
    Intersect intersectPoint;

    public Particle(Point centerPoint, double rotation){
        this.centerPoint = centerPoint;
        this.rotation = rotation;
    }
    public void setIntersect( Point relPoint, Point absPoint , double absDistance){
        intersectPoint = new Intersect(relPoint , absPoint , absDistance);
    }
    class Intersect{
        Point relPoint;
        Point absPoint;
        double absDistance;

        public Intersect(Point relPoint, Point absPoint, double absDistance) {
            this.relPoint = relPoint;
            this.absPoint = absPoint;
            this.absDistance = absDistance;
        }
    }
}
