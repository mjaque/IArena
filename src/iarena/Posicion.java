package iarena;

public class Posicion {
	public double x;
	public double y;
	
	public Posicion(double x, double y){
		this.x = x;
		this.y = y;
	}

	@Override
	public String toString() {
		return "Posicion [x=" + x + ", y=" + y + "]";
	}
	
}
