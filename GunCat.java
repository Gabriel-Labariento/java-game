public class GunCat extends Player{
    public GunCat(int cid, int x, int y){
        this.clientId = cid;
        identifier = 'B';
        speed = 5;
        height = 16;
        width = 16;
        screenX = 720/2 - width/2;
        screenY = 540/2 - height/2;
        worldX = x;
        worldY = y;
        maxHealth = 50;
        hitPoints = maxHealth;
        damage = 5;
        isDown = false;
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