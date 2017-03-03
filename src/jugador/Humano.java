package jugador;

import iarena.Guerrero;
import iarena.Jugada;
import javafx.scene.image.Image;

public class Humano extends Guerrero{
	public Jugada jugada = null;
	
	public Humano(){
		super();
		this.nombre = "Paty";
		this.disparos = 50;
		this.vidaInicial = 10;
		this.velocidad = 10;
		this.alcance_disparo = 200;
		this.dano_disparo = 3;
	}

	@Override
	public Jugada getJugada() {
		return jugada;
	}

	@Override
	public Image getAvatar() {
		return new Image(this.getClass().getClassLoader().getResourceAsStream("recursos/MusketeerCard.png"));
	}

}
