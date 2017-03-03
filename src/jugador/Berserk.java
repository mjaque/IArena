package jugador;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import iarena.Disparo;
import iarena.Guerrero;
import iarena.Jugada;
import iarena.Movimiento;
import iarena.app.IArena;
import javafx.scene.image.Image;

public class Berserk extends Guerrero {

	Random rand = new Random();
	
	public Berserk(){
		super();
		this.nombre = "Berserk";
		this.disparos = 50;
		this.vidaInicial = 10;
		this.velocidad = 10;
		this.alcance_disparo = 200;
		this.dano_disparo = 3;
	}

	@Override
	public Jugada getJugada() {
		List<Guerrero> enemigos = IArena.getEnemigosVivos(this);
		Collections.shuffle(enemigos);
		switch (rand.nextInt(2)) {
		case 0: // Se mueve
			return new Movimiento(enemigos.get(0));
		case 1:
			return new Disparo(enemigos.get(0));
		}
		return null;
	}

	@Override
	public Image getAvatar() {
		return new Image(this.getClass().getClassLoader().getResourceAsStream("recursos/arquera.png"));
	}

}
