
    import java.awt.Color;
    import java.awt.Graphics2D;
    import java.awt.geom.*;

    public class PlayerSlash extends Attack{
        private static int slashCount = Integer.MIN_VALUE; // To hold more
        private int id;

        public PlayerSlash(int cid, int x, int y, int w, int h, boolean isFriendly, int speed){
            id = slashCount++;
            clientId = cid;
            owner = ServerMaster.getInstance().getPlayerFromClientId(cid);
            identifier = NetworkProtocol.SLASH.toCharArray()[0];
            this.isFriendly = isFriendly;
            this.speed = speed;
            damage = 2;
            //Temporary hitPoints allocation
            hitPoints = 100;
            height = h;
            width = w;
            worldX = x;
            worldY = y;

            //For checking attack duration
            duration = GameServer.GAMELOOPINTERVAL * 2; // 2 frames
            setExpirationTime(duration);

            matchHitBoxBounds();
            // TODO: temporarily commented, trying to see if causing bug 
        }

        @Override
        public void draw(Graphics2D g2d, int xOffset, int yOffset){
            Rectangle2D.Double sprite = new Rectangle2D.Double(xOffset, yOffset, width, height);
            g2d.setColor(Color.RED);
            g2d.fill(sprite);
        }

        @Override
        public void matchHitBoxBounds(){
            // Bounds array is formatted as such: top, bottom, left, right; SIZES SUBJECT TO CHANGE PER ENTITY
            hitBoxBounds = new int[4];
            hitBoxBounds[0]= worldY;
            hitBoxBounds[1] = worldY + height;
            hitBoxBounds[2]= worldX;
            hitBoxBounds[3] = worldX + width;
        }

        @Override
        public String getAssetData(boolean isUserPlayer) {
            StringBuilder sb = new StringBuilder();
            // System.out.println("In getAssetData of PlayerSlash, identifier is " + identifier);
            // String format: S,id,x,y,currentRoomId|
            sb.append(identifier).append(NetworkProtocol.SUB_DELIMITER)
            .append(id).append(NetworkProtocol.SUB_DELIMITER)
            .append(worldX).append(NetworkProtocol.SUB_DELIMITER)
            .append(worldY).append(NetworkProtocol.SUB_DELIMITER)
            .append(currentRoom.getRoomId()).append(NetworkProtocol.DELIMITER);

            return sb.toString();
        }

        @Override
        public void updateEntity(ServerMaster gsm) {
            if (isExpired) {
                System.out.println("PlayerSlash expired");
                return;
            } 
            matchHitBoxBounds();
        }

        
    }
