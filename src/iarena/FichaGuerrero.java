package iarena;

public class FichaGuerrero {
	public String nombre;
	public Posicion posicion;
	public int vida;
	public int disparos;

	public FichaGuerrero clon(){
		FichaGuerrero clon = new FichaGuerrero();
		clon.nombre = this.nombre;
		clon.posicion = new Posicion(this.posicion.x, this.posicion.y);
		clon.vida = this.vida;
		clon.disparos = this.disparos;
		return clon;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + disparos;
		result = prime * result + ((nombre == null) ? 0 : nombre.hashCode());
		result = prime * result + ((posicion == null) ? 0 : posicion.hashCode());
		result = prime * result + vida;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FichaGuerrero other = (FichaGuerrero) obj;
		if (disparos != other.disparos)
			return false;
		if (nombre == null) {
			if (other.nombre != null)
				return false;
		} else if (!nombre.equals(other.nombre))
			return false;
		if (posicion == null) {
			if (other.posicion != null)
				return false;
		} else if (!posicion.equals(other.posicion))
			return false;
		if (vida != other.vida)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "FichaGuerrero [nombre=" + nombre + ", posicion=" + posicion + ", vida=" + vida + ", disparos="
				+ disparos + "]";
	}
	
}
