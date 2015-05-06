/**
 * @file Ship.java
 * @date 2015-05-05
 * @author rickard(rijo1001), lars(lama1205)
 * */
package battleship.entity;

/**
 * 
 * */
public abstract class Ship {
	protected String type;
	protected int length;
	protected int health;
	protected int posX;
	protected int posY;
	protected Alignment alignment;
	protected boolean alive = true;
	
	
	public String toString() {
		return String.format("%s, %d, %d, %d, %d, %s", type, length, health, posX, posY, alignment);
	}
	
	public String getType() { return type; }
	public int getLength() { return length; }
	public int getHealth() { return health; }
	public Alignment getAlignment() { return alignment; }
	public boolean isAlive() { return alive; }
	public int getX() {return posX;}
	public int getY() {return posY;}
	
	public void setAlignment(Alignment alignment) {
		this.alignment = alignment;
	}
	
	public void setPosition(int x, int y) {
		posX = x;
		posY = y;
	}
	
	public void hit() {
		if(health > 0) health--;
		else alive = false;
	}
	
	public abstract void draw();
}






