package jugador;

import java.util.ArrayList;
import java.util.List;

import iarena.Guerrero;
import iarena.InterfaceFactoriaGuerreros;

public class FactoriaGuerreros implements InterfaceFactoriaGuerreros{

	@Override
	public List<Guerrero> getGuerreros() {
		List<Guerrero> lista = new ArrayList<>();
		lista.add(new Sparring());
		lista.add(new Berserk());
		lista.add(new Sparring());
		return lista;
	}

}
