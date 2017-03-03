package iarena.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import iarena.Disparo;
import iarena.Disparo.Resultado;
import iarena.Guerrero;
import iarena.Guerrero.Estado;
import iarena.Jugada;
import iarena.Movimiento;
import iarena.Posicion;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import jugador.FactoriaGuerreros;
import jugador.Humano;

public class IArena extends Application implements EventHandler<MouseEvent>{
	// Constantes y Atributos del Juego
	private ScheduledExecutorService cicloJuego;
	private AnimationTimer animador;
	private static Map<Integer, Guerrero> guerreros;
	private static Map<Guerrero, Movimiento> movimientos;
	private static Map<Guerrero, Disparo> disparos;
	private static Random rand = new Random();

	// Constantes y Atributos del Escenario
	Scene escenario1;
	Humano humano = null;
	public static final int ANCHO = 800;
	public static final int ALTO = 700;

	// Constantes JavaFX
	public static final long INTERVALO_ANIMACION = 1; // segundos
	public static final double PASO_MOVIMIENTO = 1;
	public static final double PASO_DISPARO = 3;
	public static final double ESCALA = 1; // Número de pixels por posición
											// del tablero.
	protected static final double ANCHO_GUERRERO = 25;
	protected static final double ALTO_GUERRERO = ANCHO_GUERRERO * 1.25;
	protected static final double RADIO_DISPARO = 5;

	// Atributos JavaFX
	private Stage ventana;
	private Group raiz;
	private ScrollPane scrollPane;
	private boolean cicloNuevo;

	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		ventana = primaryStage;
		ventana.setWidth(ANCHO * ESCALA);
		ventana.setHeight(ALTO * ESCALA);
		ventana.setTitle("IArena");

		// Configuración del Escenario
		raiz = new Group();
		scrollPane = new ScrollPane(raiz);
		Image imgFondo = new Image(this.getClass().getClassLoader().getResourceAsStream("recursos/grass.jpg"));
		ImageView fondo = new ImageView(imgFondo);
		fondo.setFitWidth(ANCHO * ESCALA);
		fondo.setFitHeight(ALTO * ESCALA);
		raiz.getChildren().add(fondo);
		escenario1 = new Scene(scrollPane);
		ventana.setScene(escenario1);

		preparar();
		ventana.show();

		this.cicloJuego = Executors.newScheduledThreadPool(1);
		this.cicloJuego.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				System.out.println("Ejecutando ciclo de juego.");

				// Vemos si ha habido vencedor
				int numVivos = 0;
				for (Guerrero guerrero : IArena.guerreros.values())
					if (guerrero.estado == Estado.VIVO)
						numVivos++;
				System.out.println("Quedan " + numVivos + " vivos.");
				if (numVivos <= 1) {
					gameOver();
					return;
				}

				// Resolvemos las Jugadas
				movimientos = new HashMap<>();
				disparos = new HashMap<>();

				for (Guerrero guerrero : IArena.guerreros.values()) {
					if (guerrero.estado == Estado.MUERTO)
						continue;
					try {
						Jugada jugada = guerrero.getJugada();
						System.out.println(guerrero.id + ") " + guerrero.nombre + ": " + jugada);
						if (jugada instanceof Movimiento)
							resolver(guerrero, (Movimiento) jugada);
						if (jugada instanceof Disparo) {
							resolver(guerrero, (Disparo) jugada);
						}
					} catch (Exception e) {
						System.out.println("Error en la jugada de " + guerrero.getClass().getSimpleName());
						e.printStackTrace();
					}
				} // for

				// Resolvemos los movimientos
				for (Entry<Guerrero, Movimiento> entry : movimientos.entrySet()) {
					Guerrero guerrero = entry.getKey();
					Movimiento movimiento = entry.getValue();
					Posicion destino = null;
					if (movimiento.posicion != null)
						destino = movimiento.posicion;
					if (movimiento.objetivo != null)
						destino = movimiento.objetivo.posicion;

					double deltaY = destino.y - guerrero.posicion.y;
					double deltaX = destino.x - guerrero.posicion.x;
					double angulo = Math.atan2(deltaY, deltaX);
					guerrero.posicion = new Posicion(guerrero.posicion.x + guerrero.velocidad * Math.cos(angulo),
							guerrero.posicion.y + guerrero.velocidad * Math.sin(angulo));
				}

				// Resolvemos los disparos
				for (Entry<Guerrero, Disparo> entry : disparos.entrySet()) {
					Guerrero guerrero = IArena.guerreros.get(entry.getKey().id);
					Disparo disparo = entry.getValue();
					if (guerrero.disparos <= 0) {
						System.out.println("Sin balas");
						break;
					}
					guerrero.disparos--;
					if (IArena.distancia(guerrero.posicion, disparo.objetivo.posicion) < guerrero.alcance_disparo) {
						disparo.resultado = Resultado.DIANA;
						IArena.guerreros.get(disparo.objetivo.id).vida -= guerrero.dano_disparo;
					} else {
						disparo.resultado = Resultado.FALLO;
					}
				}

				// Mostramos la situación
				for (Guerrero guerrero : guerreros.values())
					System.out.println(guerrero);

				setCicloNuevo(true);
			}

			private void resolver(Guerrero guerrero, Disparo disparo) {
				// El disparo no es fiable.
				Posicion destino = null;
				if (disparo.destino != null)
					destino = disparo.destino;
				if (disparo.objetivo != null)
					destino = IArena.guerreros.get(disparo.objetivo.id).posicion;
				disparo.destino = destino;
				IArena.disparos.put(guerrero, disparo);
			}

			private void resolver(Guerrero guerrero, Movimiento movimiento) {
				// El movimiento no es fiable
				Posicion destino = null;
				if (movimiento.posicion != null)
					destino = movimiento.posicion;
				if (movimiento.objetivo != null)
					destino = IArena.guerreros.get(movimiento.objetivo.id).posicion;

				double deltaY = destino.y - guerrero.posicion.y;
				double deltaX = destino.x - guerrero.posicion.x;
				double angulo = Math.atan2(deltaY, deltaX);
				guerrero.posicion = new Posicion(guerrero.posicion.x + guerrero.velocidad * Math.cos(angulo),
						guerrero.posicion.y + guerrero.velocidad * Math.sin(angulo));

			}
		}, 2, INTERVALO_ANIMACION, TimeUnit.SECONDS);

		this.animador = new AnimationTimer() {

			private List<Node> disparosFX = new ArrayList<>();
			private Map<Guerrero, Node> barrasPuntos = new HashMap<>();
			private Image imgHerida = new Image(
					this.getClass().getClassLoader().getResourceAsStream("recursos/blood-splatter.png"));
			private Image imgAtaud = new Image(
					this.getClass().getClassLoader().getResourceAsStream("recursos/death.png"));

			// Inicialización
			{
				// Colocamos los guerreros en sus posiciones iniciales
				for (Guerrero guerrero : IArena.guerreros.values()) {
					guerrero.grupo.setTranslateX(guerrero.posicion.x * ESCALA - ANCHO_GUERRERO / 2);
					guerrero.grupo.setTranslateY(guerrero.posicion.y * ESCALA - ALTO_GUERRERO / 2);
					Rectangle barraPuntos = new Rectangle(0, ALTO_GUERRERO, ANCHO_GUERRERO, 4);
					barraPuntos.setFill(Color.RED);
					guerrero.grupo.getChildren().add(barraPuntos);
					barrasPuntos.put(guerrero, barraPuntos);
				}
			}

			@Override
			public void handle(long now) {

				if (cicloNuevo) {
					// Quitamos los disparos del ciclo anterior
					for (Node disparoFX : disparosFX)
						raiz.getChildren().remove(disparoFX);
					disparosFX = new ArrayList<>();
					for(Guerrero guerrero : IArena.guerreros.values()){
						if (guerrero.estado == Estado.VIVO){
							Rectangle barraPuntos = (Rectangle)barrasPuntos.get(guerrero);
							barraPuntos.setWidth(ANCHO_GUERRERO * guerrero.vida/guerrero.vidaInicial);
							if (guerrero.vida <= 0) {
								ImageView ivAtaud = new ImageView(imgAtaud);
								ivAtaud.setFitHeight(ALTO_GUERRERO);
								ivAtaud.setPreserveRatio(true);
								guerrero.grupo.getChildren().add(ivAtaud);
								guerrero.estado = Estado.MUERTO;
							}
						}
					}
					// Colocamos los disparos en sus posiciones iniciales
					for (Entry<Guerrero, Disparo> entry : IArena.disparos.entrySet()) {
						Disparo disparo = entry.getValue();
						Guerrero tirador = entry.getKey();
						Circle proyectil = new Circle(RADIO_DISPARO);
						proyectil.setFill(Color.RED);
						disparo.grupo.getChildren().add(proyectil);
						disparo.grupo.setTranslateX(tirador.grupo.getTranslateX() * ESCALA + ANCHO_GUERRERO / 2);
						disparo.grupo.setTranslateY(tirador.grupo.getTranslateY() * ESCALA + ALTO_GUERRERO / 2);
						disparosFX.add(disparo.grupo);
						raiz.getChildren().add(disparo.grupo);
					}
					cicloNuevo = false;
				}

				// Animación de Disparos
				Iterator<Entry<Guerrero, Disparo>> it = IArena.disparos.entrySet().iterator();
				while (it.hasNext()) {
					Entry<Guerrero, Disparo> entry = it.next();
					Disparo disparo = entry.getValue();
					Guerrero tirador = entry.getKey();
					Posicion pActual = new Posicion(disparo.grupo.getTranslateX(), disparo.grupo.getTranslateY());

					// Movemos el disparo
					// System.out.println("DISPARO: " + pActual + " -> " +
					// disparo.destino);
					double deltaY = disparo.destino.y * ESCALA - ALTO_GUERRERO / 2 - disparo.grupo.getTranslateY();
					double deltaX = disparo.destino.x * ESCALA - ANCHO_GUERRERO / 2 - disparo.grupo.getTranslateX();
					double angulo = Math.atan2(deltaY, deltaX);
					disparo.grupo.setTranslateX(disparo.grupo.getTranslateX() + PASO_DISPARO * Math.cos(angulo));
					disparo.grupo.setTranslateY(disparo.grupo.getTranslateY() + PASO_DISPARO * Math.sin(angulo));

					// Vemos si ha llegado al final
					double xInicial = tirador.grupo.getTranslateX() * ESCALA + ANCHO_GUERRERO / 2;
					double yInicial = tirador.grupo.getTranslateY() * ESCALA + ANCHO_GUERRERO / 2;
					double xActual = disparo.grupo.getTranslateX() + RADIO_DISPARO / Math.sqrt(2);
					double yActual = disparo.grupo.getTranslateY() + RADIO_DISPARO / Math.sqrt(2);
					// distancia en pixels
					double distancia = Math.sqrt(Math.pow(xInicial - xActual, 2) + Math.pow(yInicial - yActual, 2));
					if (distancia > tirador.alcance_disparo / ESCALA) {
						// Quitamos el disparo
						raiz.getChildren().remove(disparo.grupo);
						disparosFX.remove(disparo.grupo);
						it.remove();
						continue;
					}

					// Vemos si le da
					if (disparo.objetivo != null) {
						Guerrero objetivo = guerreros.get(disparo.objetivo.id);
						if (xActual > objetivo.grupo.getTranslateX()
								&& xActual < objetivo.grupo.getTranslateX() + ANCHO_GUERRERO)
							if (yActual > objetivo.grupo.getTranslateY()
									&& yActual < objetivo.grupo.getTranslateY() + ANCHO_GUERRERO) {
								System.out.println("Le da");
								ImageView ivHerida = new ImageView(imgHerida);
								ivHerida.setFitWidth(ANCHO_GUERRERO);
								ivHerida.setPreserveRatio(true);
								disparo.grupo.getChildren().removeAll(disparo.grupo.getChildren());
								disparo.grupo.getChildren().add(ivHerida);
								it.remove();// Ya no se moverá
							}

					}
				}
				// Animación de Movimientos
				for (Guerrero guerrero : IArena.guerreros.values()) {
					// Calculamos diferencias entre posición de destino y
					// posición actual del Group.
					double deltaY = guerrero.posicion.y * ESCALA - ALTO_GUERRERO / 2 - guerrero.grupo.getTranslateY();
					double deltaX = guerrero.posicion.x * ESCALA - ANCHO_GUERRERO / 2 - guerrero.grupo.getTranslateX();
					if (Math.abs(deltaY) > PASO_MOVIMIENTO || Math.abs(deltaX) > PASO_MOVIMIENTO) {
						double angulo = Math.atan2(deltaY, deltaX);
						guerrero.grupo.setTranslateX(
								guerrero.grupo.getTranslateX() + PASO_MOVIMIENTO * Math.cos(angulo));
						guerrero.grupo.setTranslateY(
								guerrero.grupo.getTranslateY() + PASO_MOVIMIENTO * Math.sin(angulo));
					}
				} // for
			}// handle()
		};
		this.animador.start();

	}

	@Override
	public void stop() {
		System.out.println("Stage is closing");
		this.cicloJuego.shutdown();
	}

	protected void setCicloNuevo(boolean b) {
		this.cicloNuevo = b;
	}

	private void gameOver() {
		System.out.println("Ha ganado " + guerreros.get(0).getIdentificacion());
		this.cicloJuego.shutdown();
	}

	private void preparar() {
		FactoriaGuerreros fg = new FactoriaGuerreros();
		IArena.guerreros = new HashMap<>();
		IArena.movimientos = new HashMap<>();
		IArena.disparos = new HashMap<>();

		List<Guerrero> listaGuerreros = fg.getGuerreros();
		Collections.shuffle(listaGuerreros);
		for (Guerrero guerrero : listaGuerreros) {
			if (!guerrero.esValido()){
				System.out.println("El guerrero " + guerrero.getIdentificacion() + " es rechazado.");
				continue;
			}
			if (guerrero.nombre == null)
				guerrero.nombre = guerrero.getClass().getSimpleName();
			
			guerrero.posicion = new Posicion(rand.nextInt(ANCHO), rand.nextInt(ALTO));
			Guerrero clon = guerrero.clon();
			ImageView avatar = new ImageView(guerrero.getAvatar());
			avatar.setFitHeight(ALTO_GUERRERO);
			avatar.setPreserveRatio(true);
			clon.grupo = new Group();
			clon.grupo.getChildren().add(avatar);
			raiz.getChildren().add(clon.grupo);
			clon.grupo.setTranslateX(clon.posicion.x * ESCALA - ANCHO_GUERRERO / 2);
			clon.grupo.setTranslateY(clon.posicion.y * ESCALA - ALTO_GUERRERO / 2);

			IArena.guerreros.put(clon.id, clon); // La lista que maneja IArena
													// (con el Group)
			
			for (Guerrero clon2 : IArena.guerreros.values()){
				if (clon2 instanceof Humano){
					System.out.println("HAY Humano.");
					escenario1.setOnMouseClicked(this);
					this.humano = (Humano)clon2;
				}
			}
		}
		System.out.println("Registrados " + IArena.guerreros.size() + " guerreros.");

		System.out.println("Comienza la lucha...");
	}

	public static List<Guerrero> getGuerreros() {
		List<Guerrero> clones = new ArrayList<>();
		for (Guerrero guerrero : IArena.guerreros.values())
			clones.add(guerrero.clon());
		return clones;
	}

	public static List<Guerrero> getEnemigosVivos(Guerrero yo) {
		List<Guerrero> clones = new ArrayList<>();
		for (Guerrero guerrero : IArena.guerreros.values())
			if ((guerrero.id != yo.id) && (guerrero.estado == Estado.VIVO))
				clones.add(guerrero.clon());
		return clones;
	}

	public static double distancia(Posicion a, Posicion b) {
		return Math.sqrt(Math.pow(b.x - a.x, 2) + Math.pow(b.y - a.y, 2));
	}

	public static double distanciaFX(Node n1, Node n2) {
		return Math.sqrt(Math.pow(n1.getTranslateX() - n2.getTranslateX(), 2)
				+ Math.pow(n1.getTranslateY() - n2.getTranslateY(), 2));
	}

	@Override
	public void handle(MouseEvent event) {
		
		if (humano == null)
			return;
		
		if (event.getButton().equals(MouseButton.PRIMARY)){
			Posicion destino = new Posicion(event.getSceneX() / ESCALA, event.getSceneY() / ESCALA); 
			humano.jugada = new Movimiento(destino);
		}
		if (event.getButton().equals(MouseButton.SECONDARY)){
			for (Guerrero guerrero : IArena.guerreros.values())
				if (event.getSceneX() > guerrero.grupo.getTranslateX() && event.getSceneX() < guerrero.grupo.getTranslateX() + ANCHO_GUERRERO)
					if (event.getSceneY() > guerrero.grupo.getTranslateY() && event.getSceneY() < guerrero.grupo.getTranslateY() + ALTO_GUERRERO)
						humano.jugada = new Disparo(guerrero);
		}
			
	}

}
