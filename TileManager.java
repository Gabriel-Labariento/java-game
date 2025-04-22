import java.awt.Graphics2D;
import java.io.*;
import javax.imageio.ImageIO;

public final class TileManager {
    private GameCanvas gc;
    public Tile[] tile;
    private int[][] mapTileNum;

    public TileManager(GameCanvas gc) {
        this.gc = gc;
        tile = new Tile[20];
        mapTileNum = new int[Room.WIDTH_TILES][Room.HEIGHT_TILES];
        getTileImage();
        loadMap();
    }

    public void getTileImage() {
        try {
            // TODO : ADD TILES

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
            tile[8].image = ImageIO.read(getClass().getResourceAsStream("Tile Images\\tile7.png"));

            // Water Hole Top Middle
            tile[9] = new Tile();
            tile[9].image = ImageIO.read(getClass().getResourceAsStream("Tile Images\\tile7.png"));
            
            // Water Hole Top Right
            tile[10] = new Tile();
            tile[10].image = ImageIO.read(getClass().getResourceAsStream("Tile Images\\tile7.png"));

            // Water Hole Left
            tile[11] = new Tile();
            tile[11].image = ImageIO.read(getClass().getResourceAsStream("Tile Images\\tile7.png"));

            // Note: Water Hole Middle is just Water

            // Water Hole Right
            tile[12] = new Tile();
            tile[12].image = ImageIO.read(getClass().getResourceAsStream("Tile Images\\tile7.png"));

            // Water Hole Bottom Left
            tile[13] = new Tile();
            tile[13].image = ImageIO.read(getClass().getResourceAsStream("Tile Images\\tile7.png"));

            // Water Hole Bottom Middle
            tile[14] = new Tile();
            tile[14].image = ImageIO.read(getClass().getResourceAsStream("Tile Images\\tile7.png"));

            // Water Hole Bottom Right
            tile[15] = new Tile();
            tile[15].image = ImageIO.read(getClass().getResourceAsStream("Tile Images\\tile7.png"));
        } catch (IOException e) {
            System.out.println("IOException in getTileImage");
        }
    }

    public void drawRoom (Graphics2D g2d, Room room, int cameraX, int cameraY) {
        // Get the position of the room in the world
        int roomX = room.getX();
        int roomY = room.getY();
        
        // For each row and column
        for (int row = 0; row < room.getHEIGHT_TILES(); row++) {
            for (int col = 0; col < room.getWIDTH_TILES(); col++) {
                int tileNum = mapTileNum[col][row];

                int screenX = roomX + col * GameCanvas.TILESIZE - cameraX;
                int screenY = roomY + row * GameCanvas.TILESIZE - cameraY;
                g2d.drawImage(tile[tileNum].image, screenX, screenY, GameCanvas.TILESIZE, GameCanvas.TILESIZE, null);       
            }
        }
    }

    public void loadMap() {
        try {
            InputStream is = getClass().getResourceAsStream("Room Layouts\\roomLayout0.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
                System.out.println(br);
                for (int row = 0; row < Room.HEIGHT_TILES; row++) {
                    String line = br.readLine();
                    for (int col = 0; col < Room.WIDTH_TILES; col++) {
                        String[] nums = line.split(",");
                        int num = Integer.parseInt(nums[col]);
                        mapTileNum[col][row] = num;
                    }
                }                
        } catch (IOException | NumberFormatException e) {
            System.out.println("Exception in loadMap()");
        }
    }

}
