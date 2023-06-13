package server;

import java.awt.Point;

public class Treasure {
    public static final String[] treasureTypes = { "coin", "sack", "jewls", "chest", "other" };
    private Point location;
    private int value;
    private String id;
    
    public Treasure(Point location, int value, String id) {
        this.location = location;
        this.value = value;
        this.id = id;
    }
    
    public String toString() {
        return id + ", " + value + " points";
    }
    
    public String toStringWithLocation() {
        return id + ", (" + location.x + ", " + location.y + "), " + value + " points";
    }
    
    public Point getLocation() {
        return location;
    }
    
    public void setLocation(Point location) {
        this.location = location;
    }
    
    public int getValue() {
        return value;
    }
    
    public void setValue(int value) {
        this.value = value;
    }
    
    public String getId() {
        return id;
    }
    
}
