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
		lista.add(new Sparring());
		lista.add(new Berserk());
//		lista.add(new Shooter());
//		lista.add(new Humano());
		//lista.add(new Minho());
		//lista.add(new Sabueso());
		//lista.add(new TPM());
		//lista.add(new PsychoKiller());
		return lista;
	}

}
