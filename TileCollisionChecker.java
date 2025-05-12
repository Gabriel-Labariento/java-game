

public class TileCollisionChecker {

    private TileManager tileManager;

    public TileCollisionChecker(TileManager tileManager){
        this.tileManager = tileManager;
    }

    public void checkTile(Entity entity){
        int[] entityHitBoxBounds = entity.getHitBoxBounds(); // 0 - TOP, 1 - BOTTOM, 2 - LEFT, 3 - RIGHT
        int velocityX = entity.getVelocityX();
        int velocityY = entity.getVelocityY();
        
        int nextTop = entityHitBoxBounds[0] + velocityY;
        int nextBottom = entityHitBoxBounds[1] + velocityY;
        int nextLeft = entityHitBoxBounds[2] + velocityX;
        int nextRight = entityHitBoxBounds[2] + velocityX;

        int entityTopRow = nextTop / GameCanvas.TILESIZE;
        int entityBottomRow = nextBottom / GameCanvas.TILESIZE;
        int entityLeftCol = nextLeft / GameCanvas.TILESIZE;
        int entityRightCol = nextRight / GameCanvas.TILESIZE;

        // Keep result within bounds
        entityTopRow = Math.max(0, entityTopRow);
        entityBottomRow = Math.min(Room.HEIGHT_TILES - 1, entityBottomRow);
        entityLeftCol = Math.max(0, entityLeftCol);
        entityRightCol = Math.min(Room.WIDTH_TILES - 1, entityRightCol);


        checkVerticalCollisions(entity, entityTopRow, entityBottomRow, entityLeftCol, entityRightCol);
        checkHorizontalCollisions(entity, entityTopRow, entityBottomRow, entityLeftCol, entityRightCol);
    }

    private void checkVerticalCollisions(Entity entity, int entityTopRow, int entityBottomRow, int entityLeftCol, int entityRightCol){
        int velocityY = entity.getVelocityY();
        
        if (velocityY < 0) {                                               // Upwards Movement
            int tileNum1 = getTileNum(entity, entityTopRow, entityLeftCol);
            int tileNum2 = getTileNum(entity, entityTopRow, entityRightCol);

            if (tileManager.tiles[tileNum1].collision || tileManager.tiles[tileNum2].collision) {
                entity.setWorldY((entityTopRow + 1) * GameCanvas.TILESIZE);
                entity.setIsCollidingWithSolidTile(true);
            }
        } else if (velocityY > 0){                                              // Downwards Movement
            int tileNum1 = getTileNum(entity, entityBottomRow, entityLeftCol);
            int tileNum2 = getTileNum(entity, entityBottomRow, entityRightCol);

            if (tileManager.tiles[tileNum1].collision || tileManager.tiles[tileNum2].collision){
                int bottomOffset = entity.getHitBoxBounds()[1] - entity.getWorldY();
                entity.setWorldY(entityBottomRow * GameCanvas.TILESIZE - bottomOffset);
                entity.setIsCollidingWithSolidTile(true);
            }
        }
    }
    private void checkHorizontalCollisions(Entity entity, int entityTopRow, int entityBottomRow, int entityLeftCol, int entityRightCol){
        int velocityX = entity.getVelocityX();
        
        if (velocityX > 0) {                                               // Rightwards Movement
            int tileNum1 = getTileNum(entity, entityTopRow, entityRightCol);
            int tileNum2 = getTileNum(entity, entityBottomRow, entityRightCol);

            if (tileManager.tiles[tileNum1].collision || tileManager.tiles[tileNum2].collision) {
                int rightOffset = entity.getHitBoxBounds()[3] - entity.getWorldX();
                entity.setWorldX((entityLeftCol + 1) * GameCanvas.TILESIZE - rightOffset);
                entity.setIsCollidingWithSolidTile(true);
            }
        } else if (velocityX < 0){                                              // Leftwards Movement
            int tileNum1 = getTileNum(entity, entityBottomRow, entityLeftCol);
            int tileNum2 = getTileNum(entity, entityTopRow, entityLeftCol);

            if (tileManager.tiles[tileNum1].collision || tileManager.tiles[tileNum2].collision){
                entity.setWorldX((entityLeftCol + 1) * GameCanvas.TILESIZE);
                entity.setIsCollidingWithSolidTile(true);
            }
        }
    }


    private int getTileNum(Entity e, int row, int col){
        Room roomOfEntity = e.getCurrentRoom();
        int[][] roomLayout = roomOfEntity.loadedLayout;

        if (row < 0 || row >= Room.HEIGHT_TILES || col < 0 || col >= Room.WIDTH_TILES) return 0;

        return roomLayout[row][col];
    }
}
