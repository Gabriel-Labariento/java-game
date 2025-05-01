import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

public class RatKing extends Enemy{
    public static int ratKingId = 0;
    private int id;
    private BufferedImage sprite;

    public RatKing(int x, int y) {
        id = ratKingId++;
        identifier = NetworkProtocol.RAT_KING.toCharArray()[0];
        speed = 1;
        height = 16;
        width = 16;
        worldX = x;
        worldY = y;
        maxHealth = 50;
        hitPoints = maxHealth;
        currentRoom = null;
        setImage();
    }

    @Override
    public void matchHitBoxBounds() {
        hitBoxBounds = new int[4];
        hitBoxBounds[0]= worldY;
        hitBoxBounds[1] = worldY + height;
        hitBoxBounds[2]= worldX;
        hitBoxBounds[3] = worldX + width;
    }

    @Override
    public void draw(Graphics2D g2d, int xOffset, int yOffset){
        // Rectangle2D.Double sprite = new Rectangle2D.Double(xOffset, yOffset, width, height);
        AffineTransform reset = g2d.getTransform();
        g2d.scale(4, 4);
        g2d.drawImage(sprite, xOffset, yOffset, width, height, null);
        g2d.setTransform(reset);
    }

    @Override
    public String getAssetData(boolean isUserPlayer) {
        StringBuilder sb = new StringBuilder();
        // System.out.println("In getAssetData of Rat, identifier is " + identifier);
        // String format: B,id,x,y,currentRoomId|
        sb.append(identifier).append(NetworkProtocol.SUB_DELIMITER)
        .append(id).append(NetworkProtocol.SUB_DELIMITER)
        .append(worldX).append(NetworkProtocol.SUB_DELIMITER)
        .append(worldY).append(NetworkProtocol.SUB_DELIMITER)
        .append(currentRoom.getRoomId()).append(NetworkProtocol.DELIMITER);

        return sb.toString();
    }

    @Override
    public void updateEntity(ServerMaster gsm){
        // TODO: ENEMY AI LOGIC
        
        Player pursued = scanForPlayer(gsm);
        if (pursued != null) pursuePlayer(pursued);  
        
        matchHitBoxBounds();
    }
    
    private void setImage() {
        try {
            String path = "Sprites\\rat_sprite_0.png";
            InputStream is = getClass().getResourceAsStream(path);
            sprite = ImageIO.read(is);
        } catch (IOException e) {
            System.out.println("IOException in setImage of " + getClass() + getId());
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
