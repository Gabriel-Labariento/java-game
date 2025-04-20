import java.util.concurrent.*;
;

public class MobSpawner {
    
    int spawnX, spawnY; // Where the mob will spawn in the room, x = (5 => 40), y = (5 => 28) 
    int level;
    private int spawnRate; 
    private int spawnedEntities;
    private int maxSpawned;
    private static final int HIGHESTX = 40;
    private static final int LOWESTX = 5;
    private static final int HIGHESTY = 28;
    private static final int LOWESTY = 5;

    private static String[] spawnableEntities = {"Rat"};
    private ScheduledExecutorService spawnMobsScheduler;

    private GameStateManager gsm;

    public MobSpawner(int level, GameStateManager gsm){
        this.level = level;
        this.gsm = gsm;
        spawnRate =  5; //((6 - level) < 1) ? 1 : 6 - level; // Min spawn rate is 1 enemy every 6 seconds, max is 1 per second. modifiable
        spawnMobsScheduler = Executors.newSingleThreadScheduledExecutor();
        spawnedEntities = 0;
        maxSpawned = 5;
    }

    public void spawn() {
        
        Runnable enemySpawnThread = new Runnable() {
            @Override
            public void run() {
                Room currentRoom = gsm.getCurrentRoom();

                // Don't spawn in the boss room. At least not yet
                if (currentRoom.isEndRoom() || spawnedEntities >= maxSpawned ) return;

                // Pick a randoom tile coordinate
                int spawnX = currentRoom.getX() + ((LOWESTX + (int) (Math.random() * ((HIGHESTX - LOWESTX) + 1))) * GameCanvas.TILESIZE);
                int spawnY = currentRoom.getY() + ((LOWESTY + (int) (Math.random() * ((HIGHESTY - LOWESTY) + 1))) * GameCanvas.TILESIZE);
                
                // Pick random enemy to spawn
                String toSpawn = spawnableEntities[(int)(Math.random() * spawnableEntities.length)];
                switch (toSpawn) {
                    case "Rat":
                        Rat r = new Rat(spawnX, spawnY);
                        gsm.addEntity(r);
                        System.out.println("Added a rat to spawnedEntities");
                        break;
                    default:
                        break;
                }
                spawnedEntities++;
            }            
        };
        spawnMobsScheduler.scheduleAtFixedRate(enemySpawnThread, spawnRate, spawnRate, TimeUnit.SECONDS);
    }

}
