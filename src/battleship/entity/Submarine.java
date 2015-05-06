package battleship.entity;

class Submarine extends Ship {

	public Submarine() {
		type = "Submarine";
		length = health = 1;
		alignment = Alignment.HORIZONTAL;
	}
	
	@Override
	public void draw() {
		System.out.println("I'm drawing a Submarine");
	}
	
}
