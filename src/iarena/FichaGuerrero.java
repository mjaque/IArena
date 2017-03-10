package iarena;

import iarena.Guerrero.Estado;
import javafx.scene.Group;

public class FichaGuerrero {
	public Guerrero guerrero;
	public int id;
	public Group grupo;
	public Posicion posicion;
	public Estado estado;
	public int vida;
	public int vidaInicial;
	public String nombre;
	public double velocidad;
	public int disparos;
	public int alcance_disparo;
	public int dano_disparo;

	public FichaGuerrero(Guerrero guerrero) {
		this.guerrero = guerrero;
		this.id = guerrero.id;
		// this.grupo = guerrero.grupo;
		if (guerrero.posicion != null)
			this.posicion = new Posicion(guerrero.posicion.x, guerrero.posicion.y);
		this.estado = guerrero.estado;
		this.vida = guerrero.vida;
		this.vidaInicial = this.vida;
		this.nombre = guerrero.nombre;
		this.velocidad = guerrero.velocidad;
		this.disparos = guerrero.disparos;
		this.alcance_disparo = guerrero.alcance_disparo;
		this.dano_disparo = guerrero.dano_disparo;
	}

	public FichaGuerrero clon() {
		FichaGuerrero clon = new FichaGuerrero(this.guerrero);
		// clon = getClass().getDeclaredConstructor().newInstance();
		clon.id = this.id;
		// this.grupo = this.grupo;
		clon.posicion.x = this.posicion.x;
		clon.posicion.y = this.posicion.y;
		clon.estado = this.estado;
		clon.vida = this.vida;
		clon.vidaInicial = this.vidaInicial;
		clon.nombre = this.nombre;
		clon.velocidad = this.velocidad;
		clon.disparos = this.disparos;
		clon.alcance_disparo = this.alcance_disparo;
		clon.dano_disparo = this.dano_disparo;
		return clon;
	}

	@Override
	public String toString() {
		return "FichaGuerrero [guerrero=" + guerrero.id + ", id=" + id + ", grupo=" + grupo + ", posicion=" + posicion
				+ ", estado=" + estado + ", vida=" + vida + ", nombre=" + nombre + ", velocidad=" + velocidad
				+ ", disparos=" + disparos + ", alcance_disparo=" + alcance_disparo + ", dano_disparo=" + dano_disparo
				+ "]";
	}
	

}
