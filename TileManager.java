import java.awt.Graphics2D;
import java.io.*;
import javax.imageio.ImageIO;

public final class TileManager {
    public Tile[] tiles;

    public TileManager() {
        tiles = new Tile[46];
        setTileImages();
        setTileCollisions();
    }



    /**
     * Creates N tiles in Tile[] tile and sets their image field.
     */
    private void setTileImages() {
        try {
            // TODO : ADD TILES
            for (int i = 0; i < tiles.length; i++) {
                tiles[i] = new Tile();
                String path = "/resources/Tile Images/tile" + i + ".png";
                InputStream is = getClass().getResourceAsStream(path);
                if (is == null) {
                    System.err.println("Null Input Stream in getTileImage(), early return");
                    return;
                }
                tiles[i].image = ImageIO.read(is);
            }
        } catch (IOException e) {
            System.out.println("IOException in getTileImage");
        }
            
    }

    /**
     * Goes through the list of tiles and sets their collision field
     * to true if they are solid (non-walkable)
     */
    private void setTileCollisions(){
        // Water bush
        for (int i = 3; i < 7; i++){
            tiles[i].collision = true;
        }

        // Water Hole
        for (int i = 8; i < 16; i++){
            tiles[i].collision = true;
        }

        // Closed Door
        for (int i = 16; i < 20; i++){
            tiles[i].collision = true;
        }

        // Lava
        tiles[32].collision = true;
        
        // Fire room wall
        tiles[45].collision = true;


    }

    public void drawTiledObject (Graphics2D g2d, GameObject go, int cameraX, int cameraY) {
        if (!(go instanceof Tileable)) return;
        // Create a 2D array to store the object layout
        
        Tileable tiledObj = (Tileable) go;
        int[][] layout = tiledObj.loadLayout();

        // Get the position of the object in the world
        int goX = go.getWorldX();
        int goY = go.getWorldY();
        int heightTiles = go.getHeight() / GameCanvas.TILESIZE;
        int widthTiles = go.getWidth() / GameCanvas.TILESIZE;
        
        // For each row and column
        for (int row = 0; row < heightTiles; row++) {
            for (int col = 0; col < widthTiles; col++) {
                int tileNum = layout[row][col];
                int screenX = goX + col * GameCanvas.TILESIZE - cameraX;
                int screenY = goY + row * GameCanvas.TILESIZE - cameraY;
                g2d.drawImage(tiles[tileNum].image, screenX, screenY, GameCanvas.TILESIZE, GameCanvas.TILESIZE, null);       
            }
        }
    }
}

// TODO: DELETE TILE REFERENCE AFTER ALL DESIGNS
/** 
            // Normal Grass
            tile[0] = new Tile();
            tile[0].image = ImageIO.read(getClass().getResourceAsStream("Tile Images\\tile0.png")); 

            // Dry Land
            tile[1] = new Tile();
            tile[1].image = ImageIO.read(getClass().getResourceAsStream("Tile Images\\tile1.png")); 

            // Normal Water
            tile[2] = new Tile();
            tile[2].image = ImageIO.read(getClass().getResourceAsStream("Tile Images\\tile2.png"));

            // Top Left Water Bush
            tile[3] = new Tile();
            tile[3].image = ImageIO.read(getClass().getResourceAsStream("Tile Images\\tile3.png"));

            // Top Right Water Bush
            tile[4] = new Tile();
            tile[4].image = ImageIO.read(getClass().getResourceAsStream("Tile Images\\tile4.png"));

            // Bottom Left Water Bush
            tile[5] = new Tile();
            tile[5].image = ImageIO.read(getClass().getResourceAsStream("Tile Images\\tile5.png"));

            // Bottom Right Water Bush
            tile[6] = new Tile();
            tile[6].image = ImageIO.read(getClass().getResourceAsStream("Tile Images\\tile6.png"));

            // Light Grass
            tile[7] = new Tile();
            tile[7].image = ImageIO.read(getClass().getResourceAsStream("Tile Images\\tile7.png"));

            // Water Hole Top Left
            tile[8] = new Tile();
            tile[8].image = ImageIO.read(getClass().getResourceAsStream("Tile Images\\tile8.png"));

            // Water Hole Top Middle
            tile[9] = new Tile();
            tile[9].image = ImageIO.read(getClass().getResourceAsStream("Tile Images\\tile9.png"));
            
            // Water Hole Top Right
            tile[10] = new Tile();
            tile[10].image = ImageIO.read(getClass().getResourceAsStream("Tile Images\\tile10.png"));

            // Water Hole Left
            tile[11] = new Tile();
            tile[11].image = ImageIO.read(getClass().getResourceAsStream("Tile Images\\tile11.png"));

            // Note: Water Hole Middle is just Water

            // Water Hole Right
            tile[12] = new Tile();
            tile[12].image = ImageIO.read(getClass().getResourceAsStream("Tile Images\\tile12.png"));

            // Water Hole Bottom Left
            tile[13] = new Tile();
            tile[13].image = ImageIO.read(getClass().getResourceAsStream("Tile Images\\tile13.png"));

            // Water Hole Bottom Middle
            tile[14] = new Tile();
            tile[14].image = ImageIO.read(getClass().getResourceAsStream("Tile Images\\tile14.png"));

            // Water Hole Bottom Right
            tile[15] = new Tile();
            tile[15].image = ImageIO.read(getClass().getResourceAsStream("Tile Images\\tile15.png"));

            // Closed Door Top Left
            tile[16] = new Tile();
            tile[16].image = ImageIO.read(getClass().getResourceAsStream("Tile Images\\tile16.png"));

            // Closed Door Top Right
            tile[17] = new Tile();
            tile[17].image = ImageIO.read(getClass().getResourceAsStream("Tile Images\\tile17.png"));

            // Closed Door Bottom Left
            tile[18] = new Tile();
            tile[18].image = ImageIO.read(getClass().getResourceAsStream("Tile Images\\tile18.png"));

            // Closed Door Bottom Right
            tile[19] = new Tile();
            tile[19].image = ImageIO.read(getClass().getResourceAsStream("Tile Images\\tile19.png"));

            // Open Door Top Left
            tile[20] = new Tile();
            tile[20].image = ImageIO.read(getClass().getResourceAsStream("Tile Images\\tile20.png"));

            // Open Door Top Right
            tile[21] = new Tile();
            tile[21].image = ImageIO.read(getClass().getResourceAsStream("Tile Images\\tile21.png"));

            // Open Door Bottom Left
            tile[22] = new Tile();
            tile[22].image = ImageIO.read(getClass().getResourceAsStream("Tile Images\\tile22.png"));

            // Open Door Bottom Right
            tile[23] = new Tile();
            tile[23].image = ImageIO.read(getClass().getResourceAsStream("Tile Images\\tile23.png"));

            24 PATH UPPER CORNER LEFT
            25 PATH UPPER
            26 PATH UPPER CORNER RIGHT
            27 PATH RIGHT
            28 PATH BOTTOM CORNER RIGHT
            29 PATH BOTTOM
            30 PATH BOTTOM CORNER LEFT
            31 PATH LEFT

            32 LAVA
            33-44 left to right, top to bottom purple floor tile pattern
            45 PURPLE ROOM WALL
*/