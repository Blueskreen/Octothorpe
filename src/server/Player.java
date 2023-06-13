package server;

import java.awt.Point;

public class Player implements Comparable<Player> {
    private Point location;
    private int score;
    private String name;
    
    public Player(Point location, int score, String name) {
        this.location = location;
        this.score = score;
        this.name = name;
    }
    
    @Override
    public String toString() {
        return name + ", (" + location.x + ", " + location.y + "), " + score;
    }
    
    @Override
    public boolean equals(Object other) {
        if (other instanceof Player)
            return this.getName().equalsIgnoreCase(((Player) other).getName());
        else
            return false;
    }
    
    @Override
    public int compareTo(Player other) {
        return this.getScore() - other.getScore();
    }
    
    public Point getLocation() {
        return location;
    }
    
    public void setLocation(Point location) {
        this.location = location;
    }
    
    public int getScore() {
        return score;
    }
    
    public void setScore(int score) {
        this.score = score;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
}
