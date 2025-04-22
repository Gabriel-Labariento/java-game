public abstract class GameObject {
    protected int worldX, worldY, height, width;

    /**
     * Returns the x position of the object in terms of the entire game world. 
     * @return the object's world x-coordinate
     */
    public int getWorldX() {
        return worldX;
    }

    /**
     * Returns the y position of the object in terms of the entire game world
     * @return the object's world y-coordinate
     */
    public int getWorldY() {
        return worldY;
    }

    /**
     * Computes for the object's centerX via its x-position and width 
     * @return integer value of the object's center x coordinate
     */
    public int getCenterX() {
        return ( (int) ((worldX + width) / 2));
    }

    /**
     * Computes for the object's centerY via its y-position and height
     * @return integer value of the object's center y coordinate
     */
    public int getCenterY() {
        return ( (int) ((worldY + height) / 2));
    }

    /**
     * Gets the object's height
     * @return an integer which is the object's height 
     */
    public int getHeight() {
        return height;
    }

    /**
     * Gets the object's width
     * @return an integer which is the object's width 
     */
    public int getWidth() {
        return width;
    }

}