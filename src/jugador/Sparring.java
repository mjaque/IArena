package jugador;

import iarena.Guerrero;
import iarena.Jugada;
import javafx.scene.image.Image;

public class Sparring extends Guerrero{

	@Override
	public Jugada getJugada() {
		return null;
	}

	@Override
	public Image getAvatar() {
		return new Image(this.getClass().getClassLoader().getResourceAsStream("recursos/GolemCard.png"));
	}

}
