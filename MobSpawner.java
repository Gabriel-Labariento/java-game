import java.util.ArrayList;
import java.util.concurrent.*;
;

public class MobSpawner {
    
    private int spawnX, spawnY; // Where the mob will spawn in the room, x = (5 => 40), y = (5 => 28) 
    private int level;
    private int difficulty;
    private boolean inBossRoom;
    private int spawnRate; 
    private int spawnedCount;
    private int maxSpawned;
    private ArrayList<Enemy> spawnedEnemies; 
    private boolean isSpawning;
    private ScheduledExecutorService spawnMobsScheduler;

    private static final int HIGHESTX = 40;
    private static final int LOWESTX = 5;
    private static final int HIGHESTY = 28;
    private static final int LOWESTY = 5;
    private static final int INITIALSPAWNDELAY = 1;
    
    private static String[][] spawnableEnemiesAtLevel = { // TODO: ADD OTHER ENEMIES
        {"Rat"}
    };

    private static String[] bosses = { // TODO: ADD OTHER BOSSES
        "RatKing" 
    };

    
    public MobSpawner(int level, int difficulty){
        this.level = level;
        this.difficulty = difficulty;
        spawnMobsScheduler = Executors.newSingleThreadScheduledExecutor();
        
        spawnRate = Math.max(2, 5 - (level / 2 - difficulty)); // Spawns 2 if > (5 - (level / 2) - difficulty)  
        maxSpawned = (difficulty == 3) ? 1 : (3 + level + difficulty); // Spawns only one enemy in boss room, otherwise spawns at least 3
        
        spawnedEnemies = new ArrayList<>();
        spawnedCount = 0;
        isSpawning = false;
    }

    public void spawn() {

        Runnable enemySpawnThread = new Runnable() {
            @Override
            public void run() {
                try {
                    // Stop spawning if maxSpawned has been reached.
                    if (spawnedCount >= maxSpawned) return;

                    Room currentRoom = ServerMaster.getInstance().getCurrentRoom();
                    System.out.println("Current room in server master is Room " + currentRoom.getRoomId());

                    // Don't spawn in the boss room. At least not yet
                    if (currentRoom.isEndRoom()) return;
    
                    // Pick a randoom tile coordinate
                    int spawnX = currentRoom.getWorldX() + ((LOWESTX + (int) (Math.random() * ((HIGHESTX - LOWESTX) + 1))) * GameCanvas.TILESIZE);
                    int spawnY = currentRoom.getWorldY() + ((LOWESTY + (int) (Math.random() * ((HIGHESTY - LOWESTY) + 1))) * GameCanvas.TILESIZE);
                    
                    Enemy enemy = null;

                    if (inBossRoom && spawnedCount == 0) {
                        spawnX = currentRoom.getCenterX();
                        spawnY = currentRoom.getCenterY();

                        String bossType = bosses[level];
                        enemy = createEnemy(bossType, spawnX, spawnY);
                    } else {
                        // Pick a random enemy to spawn out of the available in the list for the level
                        String toSpawn = spawnableEnemiesAtLevel[level][(int) (Math.random() * (spawnableEnemiesAtLevel[level].length))];
                        enemy = createEnemy(toSpawn, spawnX, spawnY);
                    }

                    if ( enemy != null ) {
                        spawnedCount++;   
                        isSpawning = true;
                        spawnedEnemies.add(enemy);
                        ServerMaster.getInstance().addEntity(enemy);
                        System.out.println("Added enemy: " + enemy.getAssetData(false) );
                    }
                } catch (Exception e) {
                    System.out.println("Exception in spawn() method");
                }
            }            
        };
        spawnMobsScheduler.scheduleAtFixedRate(enemySpawnThread, INITIALSPAWNDELAY, spawnRate, TimeUnit.SECONDS);
    }

    public void stopSpawn() {
        try {
            spawnMobsScheduler.shutdownNow();    
        } catch (Exception e) {
            System.out.println("Exception in stop() method of MobSpawner");
        }
        
    }

    public int getKilledCount(){
        int killed = 0;

        for (Enemy e : spawnedEnemies) {
            if (e.isDead()) killed++;    
        }

        return killed;
    }

    /**
     * Checks if all the enemies spawned have been killed.
     * @return true if all the enemies spawned have been killed, false otherwise.
     */
    public boolean isAllKilled(){
        return (getKilledCount() >= maxSpawned);
    }

    public boolean isSpawning() {
        return isSpawning;
    }
    
    private Enemy createEnemy(String name, int x, int y) {
        switch (name) {
            case "Rat":
                return new Rat(x, y);
            default:
                System.out.println("Undetected enemy " + name );
                return new Rat(x, y);
        }
    }

    public boolean isInBossRoom() {
        return inBossRoom;
    }

    public void setInBossRoom(boolean inBossRoom) {
        this.inBossRoom = inBossRoom;
    }

}
