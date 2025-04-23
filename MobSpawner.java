import java.util.ArrayList;
import java.util.concurrent.*;
;

public class MobSpawner {
    
    int spawnX, spawnY; // Where the mob will spawn in the room, x = (5 => 40), y = (5 => 28) 
    int level;
    private int spawnRate; 
    private int spawnedCount;
    private int maxSpawned;
    private ArrayList<Enemy> spawnedEnemies; 

    private static final int HIGHESTX = 40;
    private static final int LOWESTX = 5;
    private static final int HIGHESTY = 28;
    private static final int LOWESTY = 5;
    private static final int INITIALSPAWNDELAY = 10;
    
    private static String[] spawnableEnemies = {"Rat"};
    private ScheduledExecutorService spawnMobsScheduler;

    private ServerMaster gsm;

    public MobSpawner(int level, ServerMaster gsm){
        this.level = level;
        this.gsm = gsm;
        spawnRate =  5; // Spawns one enemy per spawnRate seconds
        spawnMobsScheduler = Executors.newSingleThreadScheduledExecutor();
        spawnedEnemies = new ArrayList<>();
        spawnedCount = 0;
        maxSpawned = 5;
    }

    public void spawn() {

        Runnable enemySpawnThread = new Runnable() {
            @Override
            public void run() {
                try {
                    // Stop spawning if maxSpawned has been reached.
                    if (spawnedCount >= maxSpawned) return;

                    Room currentRoom = gsm.getCurrentRoom();

                    // Don't spawn in the boss room. At least not yet
                    if (currentRoom.isEndRoom()) return;
    
                    // Pick a randoom tile coordinate
                    int spawnX = currentRoom.getWorldX() + ((LOWESTX + (int) (Math.random() * ((HIGHESTX - LOWESTX) + 1))) * GameCanvas.TILESIZE);
                    int spawnY = currentRoom.getWorldY() + ((LOWESTY + (int) (Math.random() * ((HIGHESTY - LOWESTY) + 1))) * GameCanvas.TILESIZE);
                    
                    // Pick random enemy to spawn
                    String toSpawn = spawnableEnemies[(int)(Math.random() * spawnableEnemies.length)];
                    switch (toSpawn) {
                        case "Rat":
                            Rat r = new Rat(spawnX, spawnY);
                            spawnedEnemies.add(r);
                            gsm.addEntity(r);
                            break;
                        default:
                            break;
                    }
                    spawnedCount++;   
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
    
}
