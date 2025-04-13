import java.awt.Graphics2D;

public abstract class Entity {
    public char identifier;
    public int speed;
    public int worldX;
    public int worldY;
    public int width;
    public int height;
    public double health;
    public double attack;
    public int clientId;

    public void draw(Graphics2D g2d, int xOffset, int yOffset){}

    public void update(){}

    public int getWorldX(){
        return worldX;
    }

    public int getWorldY(){
        return worldY;
    }

    public int getClientId(){
        return clientId;
    }

    public char getIdentifier(){
        return identifier;
    }

    public String getAssetData(){
        return "" + identifier + worldX + ',' + worldY;
    };

    // FOR TRANSFER
    // public byte[] getByteData(){
    //     ByteBuffer buffer = ByteBuffer.allocate(40);
    //     buffer.putInt(identifier);
    //     buffer.putInt(worldX);
    //     buffer.putInt(worldY);
    //     buffer.putInt(width);
    //     buffer.putInt(height);
    //     buffer.putDouble(health);
    //     buffer.putDouble(attack);
    //     return buffer.array();
    // }
}
    
