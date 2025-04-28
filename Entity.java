import java.awt.Graphics2D;

public abstract class Entity {
    public char identifier;
    public int speed;
    public int worldX;
    public int worldY;
    public int exWorldX;
    public int exWorldY;
    public int width;
    public int height;
    public double mass;
    public double health;
    public double damage;
    public int clientId;
    public int[] hitBoxBounds;
    public int endTime;

    public void draw(Graphics2D g2d, int xOffset, int yOffset){}

    public void move(char input){
        exWorldX = worldX;
        exWorldY = worldY;

        if(input == 'W')
            worldY -= speed;
        if(input == 'A')
            worldX -= speed;
        if(input == 'S')
            worldY += speed;
        if(input == 'D')
            worldX += speed;
        
        matchHitBoxBounds();
    }

    // public void apply
    
    public void matchHitBoxBounds(){}

    public void changeHealth(double change){
        health += change;
    }

    public void setWorldX (int x){
        worldX = x;
    }

    public void setWorldY (int y){
        worldY = y;
    }

    public String getAssetData(){
        return "" + identifier + worldX + ',' + worldY;
    }

    public int[] getHitBoxBounds(){
        return hitBoxBounds;
    }

    public int getWorldX(){
        return worldX;
    }

    public int getWorldY(){
        return worldY;
    }

    public int getExWorldX(){
        return exWorldX;
    }

    public int getExWorldY(){
        return exWorldY;
    }

    public int getClientId(){
        return clientId;
    }

    public char getIdentifier(){
        return identifier;
    }

    public double getDamage(){
        return damage;
    }

    public double getHealth(){
        return health;
    }

    public int getWidth(){
        return width;
    }

    public int getHeight(){
        return height;
    }

    public int getSpeed(){
        return speed;
    }

    public double getMass(){
        return mass;
    }
}
    
