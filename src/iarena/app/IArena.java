package iarena.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import iarena.Disparo;
import iarena.DisparoFX;
import iarena.FichaGuerrero;
import iarena.Guerrero;
import iarena.Jugada;
import iarena.Movimiento;
import iarena.Posicion;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import jugador.FactoriaGuerreros;

public class IArena extends Application {
	public static final int ANCHO = 15;
	public static final int ALTO = 10;
	public static final int VIDA = 10;
	public static final int DISPAROS = 50;
	public static final double DISTANCIA_DISPARO = 10;

	private static List<Guerrero> guerreros;
	private static Map<Guerrero, FichaGuerrero> fichas;
	private static Random rand = new Random();

	private static Map<FichaGuerrero, Movimiento> movimientos;

	// Constantes JavaFX
	public static final double ANCHO_PANTALLA = 780; // pixels
	public static final double ALTO_PANTALLA = 680; // pixels
	public static final double ANCHO_CELDA = 50; // pixels
	public static final double ALTO_CELDA = ANCHO_CELDA * 1.25;
	public static final double INTERVALO_ANIMACION = 2; // segundos
	public static final double ERROR_MOV = 0.1;
	public static final double PASO_MOVIMIENTO = 1;
	public static final double PASO_DISPARO = 10;

	// Atributos JavaFX
	private Stage ventana;
	private Group raiz;
	private ScrollPane scrollPane;
	private List<ImageView> heridas = new ArrayList<>();
	private List<DisparoFX> disparosFX = new ArrayList<>();

	public static void main(String[] args) {
		//System.out.println(Math.toDegrees(Math.atan2(-1, -1)));
		Application.launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		ventana = primaryStage;
		ventana.setWidth(ANCHO_PANTALLA);
		ventana.setHeight(ALTO_PANTALLA);
		ventana.setTitle("IArena");

		raiz = new Group();
		scrollPane = new ScrollPane(raiz);
		Image imgFondo = new Image(this.getClass().getClassLoader().getResourceAsStream("recursos/grass.jpg"));
		Image imgHerida = new Image(
				this.getClass().getClassLoader().getResourceAsStream("recursos/blood-splatter.png"));
		Image imgDisparo = new Image(
				this.getClass().getClassLoader().getResourceAsStream("recursos/blood-splatter.png"));
		ImageView fondo = new ImageView(imgFondo);
		fondo.setFitWidth(ANCHO * ANCHO_CELDA);
		fondo.setFitHeight(ALTO * ALTO_CELDA);
		raiz.getChildren().add(fondo);

		Scene escena1 = new Scene(scrollPane);
		ventana.setScene(escena1);

		raiz.getChildren().add(construirRejilla());

		preparar();
		ventana.show();

		AnimationTimer anim = new AnimationTimer() {

			private long ultimaAnim;
			private List<Line> disparos = new ArrayList<>();

			int i = 100;

			@Override
			public void handle(long now) {
				//Inicialización
				if (ultimaAnim == 0) {
					ultimaAnim = now;
					return;
				}
				
				//Animación de Disparos
				Iterator<DisparoFX> it = disparosFX.iterator();
				while (it.hasNext()){
					DisparoFX disparo = it.next();
					switch(disparo.mover()){
					case DIANA:
						ImageView ivHerida = new ImageView(imgHerida);
						ivHerida.setFitWidth(ANCHO_CELDA);
						ivHerida.setPreserveRatio(true);
						disparo.getObjetivo().grupo.getChildren().add(ivHerida);
						heridas.add(ivHerida);
						//Buscamos a quien ha dado
						for (FichaGuerrero ficha : fichas.values())
							if (ficha.id == disparo.getObjetivo().id)
								ficha.vida -= 1;
					case FALLO:
						raiz.getChildren().remove(disparo.getNode());
						it.remove();
						break;
					case SIGUE:
						break;
					default:
						break;
						
					}
				}
				
				// Animación de Movimientos
				for (FichaGuerrero ficha : fichas.values()) {
					if (ficha.grupo.getTranslateX() < ficha.posicion.x * ANCHO_CELDA - ERROR_MOV)
						ficha.grupo.setTranslateX(ficha.grupo.getTranslateX() + PASO_MOVIMIENTO);
					if (ficha.grupo.getTranslateX() > ficha.posicion.x * ANCHO_CELDA + ERROR_MOV)
						ficha.grupo.setTranslateX(ficha.grupo.getTranslateX() - PASO_MOVIMIENTO);
					if (ficha.grupo.getTranslateY() < ficha.posicion.y * ALTO_CELDA - ERROR_MOV)
						ficha.grupo.setTranslateY(ficha.grupo.getTranslateY() + PASO_MOVIMIENTO);
					if (ficha.grupo.getTranslateY() > ficha.posicion.y * ALTO_CELDA + ERROR_MOV)
						ficha.grupo.setTranslateY(ficha.grupo.getTranslateY() - PASO_MOVIMIENTO);
				}

				//Retardo para el turno
				if (now < (ultimaAnim + INTERVALO_ANIMACION * 1E9))
					return;
				ultimaAnim = now;

				// Quitamos disparos anteriores
				for (Line disparo : disparos)
					raiz.getChildren().remove(disparo);

				// Quitamos las heridas anteriores
				for (FichaGuerrero ficha : fichas.values()) {
					Iterator<ImageView> it2 = heridas.iterator();
					while (it2.hasNext()) {
						ImageView herida = it2.next();
						if (ficha.grupo.getChildren().remove(herida))
							it2.remove();
						;
					}
				}

				// Guardamos disparos y movimientos
				movimientos = new HashMap<>();
				for (Guerrero guerrero : guerreros) {
					Jugada jugada = guerrero.getJugada();
					System.out.println(guerrero.getClass().getSimpleName() + ": " + jugada);
					if (jugada instanceof Movimiento)
						movimientos.put(fichas.get(guerrero), (Movimiento) jugada);
					if (jugada instanceof Disparo) {
						for (FichaGuerrero ficha : fichas.values()){
							System.out.println(ficha.id + " =? " + (((Disparo) jugada).objetivo.id));
							if (ficha.id == (((Disparo) jugada).objetivo.id)) {
								System.out.print(fichas.get(guerrero).nombre + " dispara a " + ficha.nombre + "... ");
								DisparoFX disparoFX = new DisparoFX(fichas.get(guerrero), ficha);
								raiz.getChildren().add(disparoFX.getNode());
								disparosFX.add(disparoFX);

								
//								double startX, startY, endX, endY;
//								startX = fichas.get(guerrero).posicion.x * ANCHO_CELDA + ANCHO_CELDA / 2;
//								startY = fichas.get(guerrero).posicion.y * ALTO_CELDA + ALTO_CELDA / 2;
//								endX = ficha.posicion.x * ANCHO_CELDA + ANCHO_CELDA / 2;
//								endY = ficha.posicion.y * ALTO_CELDA + ALTO_CELDA / 2;
//								Line disparo = new Line(startX, startY, endX, endY);
//								disparo.setStroke(Color.RED);
//								disparos.add(disparo);
//								raiz.getChildren().add(disparo);
//								if (fichas.get(guerrero).disparos > 0) {
//									fichas.get(guerrero).disparos--;
//									if (distancia(fichas.get(guerrero).posicion, ficha.posicion) < DISTANCIA_DISPARO) {
//										ficha.vida -= 1;
//										System.out.println(" Y le da.");
//										ImageView ivHerida = new ImageView(imgHerida);
//										ivHerida.setFitWidth(ANCHO_CELDA);
//										ivHerida.setPreserveRatio(true);
//										ficha.grupo.getChildren().add(ivHerida);
//										heridas.add(ivHerida);
//									} else
//										System.out.println(" Y no le da.");
//								} else
//									System.out.println("Pero no tiene balas.");
								break;
							}
						}
					}
				}

				// Resolvemos los movimientos
				for (Entry<FichaGuerrero, Movimiento> entry : movimientos.entrySet()) {
					FichaGuerrero ficha = entry.getKey();
					switch (entry.getValue().direccion) {
					case Abajo:
						if (ficha.posicion.y < (ALTO - 1))
							ficha.posicion.y++;
						break;
					case Arriba:
						if (ficha.posicion.y > 0)
							ficha.posicion.y--;
						break;
					case Derecha:
						if (ficha.posicion.x < (ANCHO - 1))
							ficha.posicion.x++;
						break;
					case Izquierda:
						if (ficha.posicion.x > 0)
							ficha.posicion.x--;
						break;
					}
					// ficha.grupo.setTranslateX(ficha.posicion.x *
					// ANCHO_CELDA);
					// ficha.grupo.setTranslateY(ficha.posicion.y * ALTO_CELDA);
				}

				// Vemos si alguno ha muerto
				Iterator<Entry<Guerrero, FichaGuerrero>> it3 = fichas.entrySet().iterator();
				while (it3.hasNext()) {
					Entry<Guerrero, FichaGuerrero> entry = it3.next();
					System.out.println(entry.getValue());
					if (entry.getValue().vida <= 0) {
						System.out.println("Ha muerto " + entry.getValue().nombre);
						raiz.getChildren().remove(entry.getValue().grupo);
						guerreros.remove(entry.getKey());
						it3.remove();
					}
				}

				if (guerreros.size() == 1) {
					this.stop();
					gameOver();
				}
			}
		};
		anim.start();
	}

	private void gameOver() {
		System.out.println("Ha ganado " + guerreros.get(0).getClass().getSimpleName());
	}

	private Node construirRejilla() {
		Group rejilla = new Group();
		Color color = Color.RED;
		for (int i = 0; i < ANCHO; i++) {
			Line linea = new Line(ANCHO_CELDA * (i + 1), 0, ANCHO_CELDA * (i + 1) + 1, ALTO * ALTO_CELDA);
			linea.setStroke(color);
			rejilla.getChildren().add(linea);
		}
		for (int i = 0; i < ALTO; i++) {
			Line linea = new Line(0, ALTO_CELDA * (i + 1), ANCHO * ANCHO_CELDA, ALTO_CELDA * (i + 1) + 1);
			linea.setStroke(color);
			rejilla.getChildren().add(linea);
		}

		return rejilla;
	}

	private void preparar() {
		FactoriaGuerreros fg = new FactoriaGuerreros();
		guerreros = fg.getGuerreros();
		fichas = new HashMap<>();
		int i = 1;
		for (Guerrero guerrero : guerreros) {
			FichaGuerrero ficha = new FichaGuerrero();
			ficha.nombre = guerrero.getClass().getSimpleName();
			ficha.posicion = new Posicion(rand.nextInt(ANCHO), rand.nextInt(ALTO));
			i++;
			ficha.disparos = DISPAROS;
			ficha.vida = VIDA;
			fichas.put(guerrero, ficha);
			ImageView avatar = new ImageView(guerrero.getAvatar());
			avatar.setFitHeight(ALTO_CELDA);
			avatar.setPreserveRatio(true);
			ficha.grupo = new Group();
			ficha.grupo.getChildren().add(avatar);
			ficha.grupo.setTranslateX(ficha.posicion.x * ANCHO_CELDA);
			ficha.grupo.setTranslateY(ficha.posicion.y * ALTO_CELDA);
			raiz.getChildren().add(ficha.grupo);
		}
		System.out.println("Registrados " + fichas.size() + " guerreros: ");
		for (FichaGuerrero ficha : fichas.values())
			System.out.println("\t" + ficha.nombre);

		System.out.println("Comienza la lucha...");
	}

	public static List<FichaGuerrero> getFichas() {
		List<FichaGuerrero> listaFichas = new ArrayList<>();
		for (FichaGuerrero ficha : fichas.values())
			listaFichas.add(ficha.clon());
		return listaFichas;
	}

	public static FichaGuerrero getMiFicha(Guerrero yo){
		return fichas.get(yo);
	}
	
	public static double distancia(Posicion a, Posicion b) {
		return Math.sqrt(Math.pow(b.x - a.x, 2) + Math.pow(b.y - a.y, 2));
	}
	
	public static double distancia(Node n1, Node n2) {
		return Math.sqrt(Math.pow(n1.getTranslateX() - n2.getTranslateX(), 2) + Math.pow(n1.getTranslateY() - n2.getTranslateY(), 2));
	}

}
