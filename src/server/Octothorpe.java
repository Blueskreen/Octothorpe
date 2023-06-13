package server;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Octothorpe {
    public final char[] mapChars = { '|', '─', '┼', '█', '-', '+', '#'};
    public final int maxPlayers = 8;
    public final int lineOfSight = 5;
    public final int treasureLimit = 5;
    private int mapLength, mapWidth;
    private String mapText;
    private char[][] map;
    private List<Player> players = new ArrayList<Player>();
    private List<Treasure> treasures = new ArrayList<Treasure>();
    
    public Octothorpe(String mapText) {
        this.mapText = mapText;
        fillMap();
        spawnTreasures(treasureLimit);
    }
    
    private void fillMap() {
        Scanner mapScanner = new Scanner(mapText);
        String next = "";
        List<String> mapLines = new ArrayList<String>();
        while (mapScanner.hasNext()) {
            next = mapScanner.nextLine();
            next.strip();
            mapLines.add(next);
        }
        map = new char[mapLines.size()][next.length()];
        for (int i = 0; i < mapLines.size(); i++) {
            map[i] = mapLines.get(i).toCharArray();
        }
        mapLength = map.length;
        mapWidth = map[0].length;
        mapScanner.close();
    }
    
    public boolean playerExists(Player player) {
        return players.contains(player);
    }
    
    public boolean hasMaxPlayers() {
        return players.size() >= maxPlayers;
    }
    
    public int getNumberOfPlayers() {
        return players.size();
    }
    
    public List<Treasure> getVisableTreasures(Player player) {
        List<Treasure> results = new ArrayList<Treasure>();
        for (Treasure t : treasures) {
            int distance = (int) (Math.hypot((player.getLocation().x - t.getLocation().x),
                    (player.getLocation().y - t.getLocation().y)));
            if (distance <= 5)
                results.add(t);
        }
        return results;
    }
    
    public List<Treasure> claimableTreasure(Player player) {
        List<Treasure> results = new ArrayList<Treasure>();
        for (Treasure t : treasures) {
            if (player.getLocation().equals(t.getLocation())) {
                results.add(t);
            }
        }
        return results;
    }
    
    public void takeTreasure(Treasure treasure, Player player) {
        treasures.remove(treasure);
        player.setScore(player.getScore() + treasure.getValue());
        spawnTreasures(1);
    }
    
    /*
     * remember that the map works like this: char[][] where the first [] is y and
     * the second [] is x 0 1 2 3 4 0 _________ 1 | r <--- that's a player 2 | 3 | 4
     * | T <-- that's a treasure
     */
    
    public boolean isValidMove(Point move) {
        if (move.x < 0 || move.y < 0)
            return false;
        else if (move.x >= map[0].length || move.y >= map.length)
            return false;
        else if (isWall(move))
            return false;
        else
            return true;
    }
    
    public boolean isWall(Point p) {
        switch (map[p.y][p.x]) {
            case ('|'):
                return true;
            case ('─'):
                return true;
            case ('┼'):
                return true;
            case ('█'):
                return true;
            case ('-'):
                return true;
            case ('+'):
                return true;
            default:
                return false;
        }
    }
    
    public Point spawnPlayer() {
        int spawnX = (int) (Math.random() * map[0].length);
        int spawnY = (int) (Math.random() * map.length);
        Point spawnPoint = new Point(spawnX, spawnY);
        while (isWall(spawnPoint))
            spawnPoint = new Point(((int) (Math.random() * map[0].length)), ((int) (Math.random() * map.length)));
        return new Point(spawnX, spawnY);
    }
    
    public void spawnTreasures(int numberOfTreasures) {
        for (int i = 0; i < numberOfTreasures; i++) {
            treasures.add(new Treasure(spawnPlayer(), (((int) (Math.random() * 100))),
                    Treasure.treasureTypes[((int) (Math.random() * Treasure.treasureTypes.length))]));
        }
    }
    
    public int getMapLength() {
        return mapLength;
    }
    
    public String getMapText() {
        return mapText;
    }
    
    public void setMapText(String mapText) {
        this.mapText = mapText;
    }
    
    public List<Player> getPlayers() {
        return players;
    }
    
    public void setPlayers(List<Player> players) {
        this.players = players;
    }
    
    public int getMapWidth() {
        return mapWidth;
    }
    
    public List<Treasure> getTreasures() {
        return treasures;
    }
}
