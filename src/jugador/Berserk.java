package jugador;

import java.util.List;
import java.util.Random;

import iarena.Disparo;
import iarena.FichaGuerrero;
import iarena.Guerrero;
import iarena.Jugada;
import iarena.Movimiento;
import iarena.Movimiento.Direccion;
import iarena.app.IArena;

public class Berserk extends Guerrero{

	Random rand = new Random();
	
	@Override
	public Jugada getJugada() {
		switch (rand.nextInt(2)){
		case 0:	//Se mueve
			switch (rand.nextInt(4)){
			case 0:
				return new Movimiento(Direccion.Arriba);
			case 1:
				return new Movimiento(Direccion.Abajo);
			case 2:
				return new Movimiento(Direccion.Izquierda);
			case 3:
				return new Movimiento(Direccion.Derecha);
			}
		case 1:
			List<FichaGuerrero> fichas = IArena.getFichas();
			for(FichaGuerrero ficha : fichas){
				if (!ficha.nombre.equals(this.getClass().getSimpleName()))
					return new Disparo(ficha);
			}
		}
		return null;
	}

}
