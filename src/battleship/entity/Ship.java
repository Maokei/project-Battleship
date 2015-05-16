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
	protected String shipName;
	protected int length;
	protected int health;
	protected int posX;
	protected int posY;
	protected int posX1;
	protected int posY1;
	protected Alignment alignment;
	protected boolean alive = true;
	
	
	public Ship() {
		shipName = SimpleShipName.getInstance().generateName();
	}
	
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
	public int getX1() {return posX1;}
	public int getY1() {return posY1;}
	public String getShipName() {return shipName;}
	
	public void setAlignment(Alignment alignment) {
		this.alignment = alignment;
	}
	
	public void setPosition(int x, int y) {
		posX = x;
		posY = y;
		if(alignment == Alignment.HORIZONTAL)
			posX1 += length;
		else
			posY1 += length;
	}
	
	public boolean wasHit(int x, int y) {
		if(alignment == Alignment.HORIZONTAL) {
			if(y == posY && (x >= posX && x <= posX1)) {
				hit();
				return true;
			}
		}else{ //vertical
			if(x == posX && (y >= posY && y <= posY1)) {
				hit();
				return true;
			}
		}
		return false;
	}
	
	private void hit() {
		if(health > 0) health--;
		else alive = false;
	}
	
	
	public abstract void draw();
}






