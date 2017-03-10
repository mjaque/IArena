package jugador;

import iarena.Guerrero;
import iarena.Jugada;
import javafx.scene.image.Image;

public class Sparring extends Guerrero{
	
	public Sparring(){
		super();
		this.nombre = "Sparring";
		this.disparos = 0;
		this.vida = 10;
		this.velocidad = 0;
		this.alcance_disparo = 0;
		this.dano_disparo = 0;
	}

	@Override
	public Jugada getJugada() {
		return null;
	}

	@Override
	public Image getAvatar() {
		return new Image(this.getClass().getClassLoader().getResourceAsStream("recursos/GolemCard.png"));
	}

}
