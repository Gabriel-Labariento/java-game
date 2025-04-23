import java.awt.Graphics2D;
import java.io.*;
import javax.imageio.ImageIO;

public final class TileManager {
    private GameCanvas gc;
    public Tile[] tileImages;
    private int[][] mapTileNum;

    public TileManager(GameCanvas gc) {
        this.gc = gc;
        tileImages = new Tile[24];
        mapTileNum = null;
        setTileImages();
        // loadMap();
    }

    /**
     * Creates N tiles in Tile[] tile and sets their image field.
     */
    public void setTileImages() {
        try {
            // TODO : ADD TILES
            for (int i = 0; i < tileImages.length; i++) {
                tileImages[i] = new Tile();
                String path = "/Tile Images/tile" + i + ".png";
                InputStream is = getClass().getResourceAsStream(path);
                if (is == null) {
                    System.err.println("Null Input Stream in getTileImage(), early return");
                    return;
                }
                tileImages[i].image = ImageIO.read(is);
            }
        } catch (IOException e) {
            System.out.println("IOException in getTileImage");
        }
            
    }

    public void drawTiledObject (Graphics2D g2d, GameObject go, int cameraX, int cameraY) {
        
        // Create a 2D array to store the object layout
        loadLayout(go);

        // Get the position of the object in the world
        int goX = go.getWorldX();
        int goY = go.getWorldY();
        int heightTiles = go.getHeight() / GameCanvas.TILESIZE;
        int widthTiles = go.getWidth() / GameCanvas.TILESIZE;
        
        // For each row and column
        for (int row = 0; row < heightTiles; row++) {
            for (int col = 0; col < widthTiles; col++) {
                int tileNum = mapTileNum[row][col];
                int screenX = goX + col * GameCanvas.TILESIZE - cameraX;
                int screenY = goY + row * GameCanvas.TILESIZE - cameraY;
                g2d.drawImage(tileImages[tileNum].image, screenX, screenY, GameCanvas.TILESIZE, GameCanvas.TILESIZE, null);       
            }
        }
    }

    public void loadLayout(GameObject go) {
        if (go == null) { System.err.println("Object not found in loadLayout"); return; }
        String filePath = null;
        int heightTiles = go.getHeight() / GameCanvas.TILESIZE;
        int widthTiles = go.getWidth() / GameCanvas.TILESIZE;

        // Check what type of object it is. TODO: MAKE PRETTIER
        if ((go instanceof Door)) {
            mapTileNum = new int[heightTiles][widthTiles];

            if (((Door) go).isOpen()) {
                filePath = "Object Layouts\\openDoor.txt";
            } else filePath = "Object Layouts\\closedDoor.txt";
            System.out.println(filePath);

        } else if (go instanceof  Room) {
            mapTileNum = new int[heightTiles][widthTiles]; // TODO: ASSIGN PROPERLY
            filePath = "Object Layouts\\roomLayout0.txt";
        }

        // Safety first
        if (filePath == null) { System.err.println("Object layout not found in loadLayout()"); return; } 
        try {
            InputStream is = getClass().getResourceAsStream(filePath);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
                for (int row = 0; row < heightTiles; row++) {
                    String line = br.readLine();
                    for (int col = 0; col < widthTiles; col++) {
                        String[] nums = line.split(",");
                        int num = Integer.parseInt(nums[col]);
                        mapTileNum[row][col] = num;
                    }
            }                
        } catch (IOException | NumberFormatException e) {
            System.out.println("Exception in loadMap()");
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
            tile[20].image = ImageIO.read(getClass().getResourceAsStream("Tile Images\\tile16.png"));

            // Open Door Top Right
            tile[21] = new Tile();
            tile[21].image = ImageIO.read(getClass().getResourceAsStream("Tile Images\\tile17.png"));

            // Open Door Bottom Left
            tile[22] = new Tile();
            tile[22].image = ImageIO.read(getClass().getResourceAsStream("Tile Images\\tile18.png"));

            // Open Door Bottom Right
            tile[23] = new Tile();
            tile[23].image = ImageIO.read(getClass().getResourceAsStream("Tile Images\\tile19.png"));
*/