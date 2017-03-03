package iarena;

import javafx.scene.Group;
import javafx.scene.image.Image;

public abstract class Guerrero {
	public enum Estado {VIVO, MUERTO};
	private static int ID = 0;
	public int id = Guerrero.ID++;
	public Group grupo;
	public Posicion posicion;
	public Estado estado;
	public int vida;
	
	//Atributos configurables
	public String nombre;
	public double velocidad;
	public int vidaInicial;
	public int disparos;
	public int alcance_disparo;
	public int dano_disparo;
	
	public Guerrero(){
		this.estado = Estado.VIVO;
		//Valores por defecto
		if (this.nombre == null)
			this.nombre = getClass().getSimpleName();
		this.disparos = 50;
		this.vida = 10;
		this.vidaInicial = vida;
		this.velocidad = 10;
		this.alcance_disparo = 1500;
		this.dano_disparo = 1;
	}
	
	public Guerrero clon(){
		Guerrero clon = null;
		try {
			clon = getClass().getDeclaredConstructor().newInstance();
			clon.id = this.id;
			clon.nombre = this.nombre;
			clon.posicion = new Posicion(this.posicion.x, this.posicion.y);
			clon.velocidad = velocidad;
			clon.vida = this.vida;
			clon.disparos = this.disparos;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return clon;
	}
	
	public abstract Jugada getJugada();
	public abstract Image getAvatar();

	@Override
	public String toString() {
		return "Guerrero [id=" + id + ", nombre=" + nombre + ", posicion=" + posicion + ", vida=" + vida + ", disparos="
				+ disparos + ", estado=" + estado + "]";
	}
	
	public String getIdentificacion(){
		return "Guerrero: " + id + "." + nombre;
	}

	public boolean esValido() {
		System.out.println(this.nombre + ": " + (this.disparos + this.vidaInicial + this.velocidad + this.alcance_disparo / 10 + this.dano_disparo));
		return (this.disparos + this.vidaInicial + this.velocidad + this.alcance_disparo / 10 + this.dano_disparo) <= 100;
	}
	
}
