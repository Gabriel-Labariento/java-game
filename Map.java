import java.awt.*;

public class Map {
    
    private Tile[][] tiles;
    private final int TILECOUNTX = 32;
    private final int TILECOUNTY = 24;
    private final int TILEHEIGHT = (int) (540.0 / TILECOUNTY);
    private final int TILEWIDTH = (int) (720.0 / TILECOUNTX);

    public Map(){
        // Instantiate the tiles array and populate it with tile objects
        tiles = new Tile[TILECOUNTY][TILECOUNTX];
        for (int i = 0; i < TILECOUNTY; i++) {
            for (int j = 0; j < TILECOUNTX; j++) {
                // The x and y coordinate of the tile corresponds to its index in the 2D array
                tiles[i][j] = new Tile(j * TILEWIDTH, i * TILEHEIGHT);
            }
        }
    }

    public void drawMap(Graphics2D g2d){
        // Each primary index is an array of tiles
        for (Tile[] tilesArray : tiles) {
            // Each secondary index is a tile 
            for (Tile tile : tilesArray) {
                tile.drawTile(g2d, TILEWIDTH, TILEHEIGHT);
            }
        }
        
    }

    public int getTILECOUNTX() {
        return TILECOUNTX;
    }

    public int getTILECOUNTY() {
        return TILECOUNTY;
    }

}
