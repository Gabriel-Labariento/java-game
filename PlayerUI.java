import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.*;

public class PlayerUI extends GameObject{
    public PlayerUI(){}

    @Override
    public void matchHitBoxBounds() {}

    public void drawPlayerUI(Graphics2D g2d, Player userPlayer){
        int userHealth = userPlayer.getHitPoints();
        double xOffset = 110.4;
        double yOffset = 26.1;
        while (userHealth > 0){
            if (userHealth % 2 == 0){
                //If 
                xOffset += 51.4;
            }
            else{
                xOffset += 14;
            }

            Rectangle2D.Double heart = new Rectangle2D.Double(xOffset, yOffset, 14, 28);
            g2d.setColor(Color.WHITE);
            g2d.fill(heart);
            userHealth -= 1;
        }

    }




}