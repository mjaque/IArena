package iarena;

public class Movimiento extends Jugada{
	public enum Direccion {Arriba, Abajo, Izquierda, Derecha};
	public Direccion direccion;
	
	public Movimiento(Direccion dir){
		this.direccion = dir;
	}

	@Override
	public String toString() {
		return "Movimiento [direccion=" + direccion + "]";
	}
	
}
