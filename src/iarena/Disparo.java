package iarena;

public class Disparo extends Jugada{
	public FichaGuerrero objetivo;
	
	public Disparo(FichaGuerrero ficha) {
		this.objetivo = ficha;
	}

	@Override
	public String toString() {
		return "Disparo [objetivo=" + objetivo.nombre + "]";
	}
	
}
