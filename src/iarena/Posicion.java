package iarena;

public class Posicion {
	public int x;
	public int y;
	
	public Posicion(int x, int y){
		this.x = x;
		this.y = y;
	}

	@Override
	public String toString() {
		return "Posicion [x=" + x + ", y=" + y + "]";
	}
	
}
