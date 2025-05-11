import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.*;

public class PlayerUI extends GameObject{
    public PlayerUI(){}

    @Override
    public void matchHitBoxBounds() {}

    public void drawPlayerUI(Graphics2D g2d, Player userPlayer, ClientMaster clientMaster, int sf){
        int userHealth = userPlayer.getHitPoints();
        double xpBarPercent = clientMaster.getXPBarPercent();
        int userLvl = clientMaster.getUserLvl();
        
        // System.out.println("UI HEALTH: " + userHealth);

        //PORTRAIT UI ELEMENTS
        Ellipse2D.Double userPortrait = new Ellipse2D.Double(23/sf, 26/sf, 72/sf, 72/sf);
        g2d.setColor(Color.ORANGE);
        g2d.fill(userPortrait);

        //ITEM UI ELEMENTS
        Entity heldItem = clientMaster.generateUIItem();
        if (heldItem != null) heldItem.draw(g2d, 68/sf, 71/sf);

        //LEVELING SYSTEM UI ELEMENTS
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        g2d.drawString("Level " + userLvl, 110/sf, 80/sf);

        Rectangle2D.Double barBorder = new Rectangle2D.Double(110/sf, 91/sf, 186/sf, 12/sf);
        g2d.setColor(Color.WHITE);
        g2d.fill(barBorder);

        double xpBarWidth = Math.floor(178*(xpBarPercent/100));
        Rectangle2D.Double xpBar = new Rectangle2D.Double(114/sf, 94/sf, xpBarWidth/sf, 5/sf);
        g2d.setColor(Color.GREEN);
        g2d.fill(xpBar);

       //HEALTH UI ELEMENTS
        int xOffset = 110/sf;
        int yOffset = 26/sf;
        for(int i = 1; i <= userHealth; i++){

            Rectangle2D.Double heart = new Rectangle2D.Double(xOffset, yOffset, 14/sf, 28/sf);
            g2d.setColor(Color.WHITE);
            g2d.fill(heart);
            
            //Half heart graphics system
            if (i % 2 == 0){
                xOffset += 40/sf;
            }
            else{
                xOffset += 14/sf;
            }
        }
    }




}