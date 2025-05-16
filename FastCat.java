import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.*;

public class FastCat extends Player{
    public FastCat(int cid, int x, int y){
        this.clientId = cid;
        identifier = NetworkProtocol.FASTCAT;
        baseSpeed = 4;
        speed = baseSpeed;
        height = 16;
        width = 16;
        screenX = 800/2 - width/2;
        screenY = 600/2 - height/2;
        worldX = x;
        worldY = y;
        maxHealth = 500;
        hitPoints = maxHealth;
        damage = 5;
        isDown = false;
        coolDownDuration = 600;

        matchHitBoxBounds();
    }

    @Override
    public void draw(Graphics2D g2d, int xOffset, int yOffset){
        Rectangle2D.Double sprite = new Rectangle2D.Double(xOffset, yOffset, width, height);
        g2d.setColor(Color.GREEN);
        g2d.fill(sprite);
    }

    @Override
    public void matchHitBoxBounds() {
        hitBoxBounds = new int[4];
        hitBoxBounds[0]= worldY;
        hitBoxBounds[1] = worldY + height;
        hitBoxBounds[2]= worldX;
        hitBoxBounds[3] = worldX + width;
    }
}