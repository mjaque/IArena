package iarena;

public class Movimiento extends Jugada{
	public Posicion posicion = null;
	public Guerrero objetivo = null;
	
	public Movimiento(Posicion posicion){
		this.posicion = posicion;
	}
	
	public Movimiento(Guerrero objetivo){
		this.objetivo = objetivo;
	}

	@Override
	public String toString() {
		if (posicion != null)
			return "Movimiento [posicion = " + posicion + "]";
		else
			return "Movimiento [objetivo = " + objetivo.getIdentificacion() + "]";
	}
	
}
