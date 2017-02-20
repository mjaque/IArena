package jugador;

import java.util.ArrayList;
import java.util.List;

import iarena.Disparo;
import iarena.FichaGuerrero;
import iarena.Guerrero;
import iarena.Jugada;
import iarena.Posicion;
import iarena.app.IArena;
import javafx.scene.image.Image;

public class Shooter extends Guerrero {
	private Posicion pos;
	private FichaGuerrero objetivo = null;

	@Override
	public Jugada getJugada() {
		List<FichaGuerrero> fichas = IArena.getFichas();
		List<FichaGuerrero> enemigos = new ArrayList<>();
		List<FichaGuerrero> enemigosATiro = new ArrayList<>();
		for (FichaGuerrero ficha : fichas) {
			if (ficha.nombre.equals(this.getClass().getSimpleName()))
				this.pos = ficha.posicion;
			else
				enemigos.add(ficha);
		}
		for (FichaGuerrero ficha2 : enemigos) {
			if (!ficha2.nombre.equals(this.getClass().getSimpleName()))
				if (IArena.distancia(pos, ficha2.posicion) < IArena.DISTANCIA_DISPARO)
					enemigosATiro.add(ficha2);
		}
		return new Disparo(enemigosATiro.get(0));
	}

	@Override
	public Image getAvatar() {
		return new Image(this.getClass().getClassLoader().getResourceAsStream("recursos/BomberCard.png"));
	}

}
