/**
 * @file Ship.java
 * */
package battleship.entity;


public abstract class Ship {
	public static final int NUM_OF_CARRIERS = 1;
	public static final int NUM_OF_DESTROYERS = 3;
	public static final int NUM_OF_SUBMARINES = 5;

	protected String id;
	protected String type;
	protected int length;
	protected int health;
	protected Alignment alignment;
	protected boolean alive = true;
	
	public String toString() {
		return String.format("%s, %s, %d, %d %s", type, id, length, health, alignment);
	}
	
	public String getId() { return id; }
	public String getType() { return type; }
	public int getLength() { return length; }
	public int getHealth() { return health; }
	public Alignment getAlignment() { return alignment; }
	public boolean isAlive() { return alive; }
	
	public void setAlignment(Alignment alignment) {
		this.alignment = alignment;
	}
	
	public void hit() {
		if(health > 0) health--;
		else alive = false;
	}
	
	public abstract void draw();
}

class Carrier extends Ship {
	
	public Carrier(String id) {
		this.id = id;
		type = "Carrier";
		length = health = 5;
		alignment = Alignment.HORIZONTAL;
	}
	
	@Override
	public void draw() {
		System.out.println("I'm drawing a Carrier");
	}
}

class Destroyer extends Ship {

	public Destroyer(String id) {
		this.id = id;
		type = "Destroyer";
		length = health = 3;
		alignment = Alignment.HORIZONTAL;
	}
	@Override
	public void draw() {
		System.out.println("I'm drawing a Destroyer");
	}
	
}

class Submarine extends Ship {

	public Submarine(String id) {
		this.id = id;
		type = "Submarine";
		length = health = 1;
		alignment = Alignment.HORIZONTAL;
	}
	@Override
	public void draw() {
		System.out.println("I'm drawing a Submarine");
	}
	
}
