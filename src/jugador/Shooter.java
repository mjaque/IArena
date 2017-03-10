package jugador;

import java.util.ArrayList;
import java.util.List;

import iarena.Disparo;
import iarena.FichaGuerrero;
import iarena.Guerrero;
import iarena.Jugada;
import iarena.Movimiento;
import iarena.app.IArena;
import javafx.scene.image.Image;

public class Shooter extends Guerrero {
	
	public Shooter(){
		super();
		this.nombre = "Shooter";
		this.disparos = 10;
		this.vida = 20;
		this.velocidad = 1;
		this.alcance_disparo = 409;
		this.dano_disparo = 29;
	}

	@Override
	public Jugada getJugada() {
		//BUG:
//		this.posicion = new Posicion(0,0);
//		return new Movimiento(new Posicion(0,0));
//		this.vida = 5000;
		List<FichaGuerrero> fichas = IArena.getEnemigosVivos(this);
		List<FichaGuerrero> enemigosATiro = new ArrayList<>();
		for (FichaGuerrero g : fichas)
			if (IArena.distancia(this.posicion, g.posicion) < this.alcance_disparo)
				enemigosATiro.add(g);
		if (enemigosATiro.size() > 0)
			return new Disparo(enemigosATiro.get(0).guerrero);
		else
			return new Movimiento(fichas.get(0).guerrero);
	}

	@Override
	public Image getAvatar() {
		return new Image(this.getClass().getClassLoader().getResourceAsStream("recursos/BomberCard.png"));
	}

}
