package iarena;

import javafx.scene.Group;

public class Disparo extends Jugada{
	public enum Resultado {DIANA, FALLO};
	public Posicion destino;	//en coordenadas de juego
	public Guerrero objetivo;
	public Resultado resultado;
	public Group grupo;
	
	public Disparo(Posicion posicion) {
		this.destino = posicion;
		this.grupo = new Group();
	}

	public Disparo(Guerrero objetivo) {
		this.objetivo = objetivo;
		this.grupo = new Group();
	}
	
	@Override
	public String toString() {
		if (objetivo != null)
			return "Disparo [objetivo = " + objetivo.getIdentificacion() + "]";
		else
			return "Disparo [objetivo = null]";
	}
	
}
