import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.*;

public class HeavyCat extends Player{
    public HeavyCat(int cid, int x, int y){
        this.clientId = cid;
        identifier = NetworkProtocol.HEAVYCAT;
        baseSpeed = 2;
        speed = baseSpeed;
        height = 16;
        width = 16;
        screenX = 800/2 - width/2;
        screenY = 600/2 - height/2;
        worldX = x;
        worldY = y;
        maxHealth = 6;
        hitPoints = maxHealth;
        damage = 5;
        isDown = false;
        coolDownDuration = 1200;

        matchHitBoxBounds();
    }

    public void draw(Graphics2D g2d, int xOffset, int yOffset){
        Rectangle2D.Double sprite = new Rectangle2D.Double(xOffset, yOffset, width, height);
        g2d.setColor(Color.BLUE);
        g2d.fill(sprite);
    }

    @Override
    public void levelUpStats(){
        hitPoints += 1;
        maxHealth += 1;
        damage += 1;
        speed += 0;
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