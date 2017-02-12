package iarena.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import iarena.Disparo;
import iarena.FichaGuerrero;
import iarena.Guerrero;
import iarena.Jugada;
import iarena.Movimiento;
import iarena.Posicion;
import jugador.FactoriaGuerreros;

public class IArena {
	public static final int ANCHO = 50;
	public static final int ALTO = 50;
	public static final int VIDA = 10;
	public static final int DISPAROS = 50;
	public static final double DISTANCIA_DISPARO = 10;

	private static List<Guerrero> guerreros;
	private static Map<Guerrero, FichaGuerrero> fichas;
	private static Random rand = new Random();

	private static Map<FichaGuerrero, Movimiento> movimientos;

	public static void main(String[] args) {
		FactoriaGuerreros fg = new FactoriaGuerreros();
		guerreros = fg.getGuerreros();
		fichas = new HashMap<>();
		for (Guerrero guerrero : guerreros) {
			FichaGuerrero ficha = new FichaGuerrero();
			ficha.nombre = guerrero.getClass().getSimpleName();
			ficha.posicion = new Posicion(rand.nextInt(ANCHO), rand.nextInt(ALTO));
			ficha.disparos = DISPAROS;
			ficha.vida = VIDA;
			fichas.put(guerrero, ficha);
		}
		System.out.println("Registrados " + fichas.size() + " guerreros: ");
		for (FichaGuerrero ficha : fichas.values())
			System.out.println("\t" + ficha.nombre);

		System.out.println("Comienza la lucha...");
		while (guerreros.size() > 1) {
			movimientos = new HashMap<>();

			// Resolvemos los disparos. Guardamos los movimientos
			for (Guerrero guerrero : guerreros) {
				Jugada jugada = guerrero.getJugada();
				System.out.println(guerrero.getClass().getSimpleName() + ": " + jugada);
				if (jugada instanceof Movimiento)
					movimientos.put(fichas.get(guerrero), (Movimiento) jugada);
				if (jugada instanceof Disparo) {
					for (FichaGuerrero ficha : fichas.values())
						if (ficha.nombre.equals(((Disparo) jugada).objetivo.nombre)) {
							System.out.print(fichas.get(guerrero).nombre + " dispara a " + ficha.nombre + "... ");
							if (fichas.get(guerrero).disparos > 0) {
								fichas.get(guerrero).disparos--;
								if (distancia(fichas.get(guerrero).posicion, ficha.posicion) < DISTANCIA_DISPARO) {
									ficha.vida -= 1;
									System.out.println(" Y le da.");
								} else
									System.out.println(" Y no le da.");
							}
							else
								System.out.println("Pero no tiene balas.");
							break;
						}
				}
			}

			// Resolvemos los movimientos
			for (Entry<FichaGuerrero, Movimiento> entry : movimientos.entrySet()) {
				switch (entry.getValue().direccion) {
				case Abajo:
					if (entry.getKey().posicion.y < ALTO)
						entry.getKey().posicion.y++;
					break;
				case Arriba:
					if (entry.getKey().posicion.y > 0)
						entry.getKey().posicion.y--;
					break;
				case Derecha:
					if (entry.getKey().posicion.x < ANCHO)
						entry.getKey().posicion.x++;
					break;
				case Izquierda:
					if (entry.getKey().posicion.x > 0)
						entry.getKey().posicion.x--;
					break;
				}
			}

			// Vemos si alguno ha muerto
			Iterator<Entry<Guerrero, FichaGuerrero>> it = fichas.entrySet().iterator();
			while (it.hasNext()) {
				Entry<Guerrero, FichaGuerrero> entry = it.next();
				System.out.println(entry.getValue());
				if (entry.getValue().vida < 0) {
					System.out.println("Ha muerto " + entry.getValue().nombre);
					guerreros.remove(entry.getKey());
					it.remove();
				}
			}
		}
		System.out.println("Ha ganado " + guerreros.get(0).getClass().getSimpleName());
	}

	public static List<FichaGuerrero> getFichas() {
		List<FichaGuerrero> listaFichas = new ArrayList<>();
		for (FichaGuerrero ficha : fichas.values())
			listaFichas.add(ficha.clon());
		return listaFichas;
	}

	public static double distancia(Posicion a, Posicion b) {
		return Math.sqrt(Math.pow(b.x - a.x, 2) + Math.pow(b.y - a.y, 2));
	}

}
