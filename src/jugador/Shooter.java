package jugador;

import java.util.ArrayList;
import java.util.List;

import iarena.Disparo;
import iarena.Guerrero;
import iarena.Jugada;
import iarena.Movimiento;
import iarena.Posicion;
import iarena.app.IArena;
import javafx.scene.image.Image;

public class Shooter extends Guerrero {
	private Posicion pos;
	private Guerrero objetivo = null;

	@Override
	public Jugada getJugada() {
		List<Guerrero> fichas = IArena.getGuerreros();
		List<Guerrero> enemigos = new ArrayList<>();
		List<Guerrero> enemigosATiro = new ArrayList<>();
		for (Guerrero ficha : fichas) {
			if (ficha.nombre.equals(this.nombre))
				this.pos = ficha.posicion;
			else
				enemigos.add(ficha);
		}
		if (enemigosATiro.size() > 0)
			return new Disparo(enemigosATiro.get(0));
		else
			return new Movimiento(new Posicion(0,0));
	}

	@Override
	public Image getAvatar() {
		return new Image(this.getClass().getClassLoader().getResourceAsStream("recursos/BomberCard.png"));
	}

}
