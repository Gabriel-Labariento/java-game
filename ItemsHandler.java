import java.util.*;
import java.util.concurrent.*;

public class ItemsHandler{
    private static final ConcurrentHashMap<String, Integer> availableItems = new ConcurrentHashMap<>();
    private static double dropChanceSum;
    static {
        //Use static initializer block to make hashmap of ITEM - DROPCHANCE
        availableItems.put("None", 60);
        availableItems.put("Redfish", 15);
        availableItems.put("Cat Treat", 15);
        availableItems.put("Milk", 4);
        availableItems.put("Premium Cat Food++", 4);
        availableItems.put("Goldfish", 2);
        availableItems.put("Light Scarf", 2);
        availableItems.put("Thick Sweater", 2);
        availableItems.put("Bag of Catnip", 2);
        availableItems.put("Loud Bell", 2);
        availableItems.put("Pringles Can", 2);
        for (Integer dropChance: availableItems.values()){
            dropChanceSum += dropChance;
        }
    }
    

    public Item rollItem(Enemy enemy){
        //Get a random number between 0 to whatever drop chance is (in this case 100)
        double roll = Math.random() * dropChanceSum;
        double cumulativeChance = 0;

        for (Map.Entry<String, Integer> entry : availableItems.entrySet()){
            double dropChance = entry.getValue();

            //Add to var to project all of the dropchances within their respective domains from the interval [0,100]
            cumulativeChance += dropChance;
            
            //Check to see if random number from 0-100 is within the selection interval for a specific item
            if (roll < cumulativeChance) {
                //Create a new item from the middle of the enemy
                switch(entry.getKey()){
                    case "None":
                        System.out.println("0");
                        return null;
                    case "Redfish":
                        System.out.println("1");
                        return new RedFish(enemy.getWorldX(), enemy.getWorldY());
                    case "Cat Treat":
                        System.out.println("2");
                        return new CatTreat(enemy.getWorldX(), enemy.getWorldY());
                    case "Milk":
                        System.out.println("3");
                        return new Milk(enemy.getWorldX(), enemy.getWorldY());
                    case "Premium Cat Food++":
                        System.out.println("4");    
                        return new PremiumCatFood(enemy.getWorldX(), enemy.getWorldY());
                    case "Goldfish":
                        System.out.println("5");
                        return new Goldfish(enemy.getWorldX(), enemy.getWorldY());
                    case "Light Scarf":
                        System.out.println("6");
                        return new LightScarf(enemy.getWorldX(), enemy.getWorldY());
                    case "Thick Sweater":
                        System.out.println("7");
                        return new ThickSweater(enemy.getWorldX(), enemy.getWorldY());
                    case "Bag of Catnip":
                        System.out.println("8");
                        return new BagOfCatnip(enemy.getWorldX(), enemy.getWorldY());
                    case "Loud Bell":
                        System.out.println("9");
                        return new LoudBell(enemy.getWorldX(), enemy.getWorldY());
                    case "Pringles Can":
                        System.out.println("10");
                        return new PringlesCan(enemy.getWorldX(), enemy.getWorldY());
                }
            }
        }
        return null;
    }

}