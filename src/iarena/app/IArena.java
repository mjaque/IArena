package iarena.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import iarena.Disparo;
import iarena.FichaGuerrero;
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

public class IArena extends Application implements EventHandler<MouseEvent> {
	// Constantes y Atributos del Juego
	private AnimationTimer animador;
	private static Map<Guerrero, FichaGuerrero> guerreros;
	private static Map<FichaGuerrero, Disparo> disparos;
	private static Random rand = new Random();

	// Constantes y Atributos del Escenario
	Scene escenario1;
	Humano humano = null;
	public static final int ANCHO = 800;
	public static final int ALTO = 700;

	// Constantes JavaFX
	public static final long INTERVALO_CICLO = 1; // segundos
	public static final double PASO_MOVIMIENTO = 1;
	public static final double PASO_DISPARO = 10;
	public static final double ESCALA = 1; // Número de pixels por posición
											// del tablero.
	protected static final double ANCHO_GUERRERO = 25;
	protected static final double ALTO_GUERRERO = ANCHO_GUERRERO * 1.25;
	protected static final double RADIO_DISPARO = 5;
	private static final Image imgHerida = new Image(
			IArena.class.getClassLoader().getResourceAsStream("recursos/blood-splatter.png"));
	private Image imgAtaud = new Image(
			IArena.class.getClassLoader().getResourceAsStream("recursos/death.png"));

	// Atributos JavaFX
	private Stage ventana;
	private Group raiz;
	private ScrollPane scrollPane;
	private boolean cicloNuevo;
	private long ultimoCiclo = 0;
	private List<Node> disparosFX = new ArrayList<>();
	private Map<FichaGuerrero, Node> barrasPuntos = new HashMap<>();


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

		this.animador = new AnimationTimer() {

			// Inicialización
			{
				System.out.println("Inicializando animador.");
				// Colocamos los guerreros en sus posiciones iniciales
				for (FichaGuerrero guerrero : IArena.guerreros.values()) {
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
				if(ultimoCiclo == 0){
					ultimoCiclo = now;
					return;
				}
				if (now > ultimoCiclo + INTERVALO_CICLO * 1E9){
					ultimoCiclo = now;
					jugar();
				}

				animar(now);

			}// handle()
		};
		this.animador.start();

	}
	
	public void jugar(){
		System.out.println("Ejecutando ciclo de juego.");

		// Vemos si ha habido vencedor
		int numVivos = 0;
		for (FichaGuerrero guerrero : IArena.guerreros.values())
			if (guerrero.estado == Estado.VIVO)
				numVivos++;
		System.out.println("Quedan " + numVivos + " vivos.");
		if (numVivos <= 1) {
			gameOver();
			return;
		}

		// Resolvemos las Jugadas
		disparos = new HashMap<>();

		for (FichaGuerrero ficha : IArena.guerreros.values()) {
			if (ficha.estado == Estado.MUERTO)
				continue;
			try {
				Jugada jugada = ficha.guerrero.getJugada();
				System.out.println(ficha.id + ") " + ficha.nombre + ": " + jugada);
				if (jugada instanceof Movimiento)
					resolver(ficha, (Movimiento) jugada);
				if (jugada instanceof Disparo) {
					resolver(ficha, (Disparo) jugada);
				}
			} catch (Exception e) {
				System.out.println("Error en la jugada de " + ficha.getClass().getSimpleName());
				e.printStackTrace();
			}
		} // for

		// Mostramos la situación
		for (FichaGuerrero guerrero : guerreros.values())
			System.out.println(guerrero);

		setCicloNuevo(true);

	}
	
	private void resolver(FichaGuerrero guerrero, Disparo disparo) {
		// El disparo no es fiable.
		Posicion destino = null;
		if (disparo.destino != null)
			destino = disparo.destino;
		if (disparo.objetivo != null){
			FichaGuerrero objetivo = IArena.guerreros.get(disparo.objetivo);
			destino = objetivo.posicion;
			if (guerrero.disparos <= 0) 
				System.out.println("Sin balas");
			else{
				System.out.println("Resto disparo");
				System.out.println(guerrero);
				guerrero.disparos--;
				System.out.println(guerrero);
				if (IArena.distancia(guerrero.posicion, objetivo.posicion) < guerrero.alcance_disparo){
					System.out.println("Diana");
					objetivo.vida -= guerrero.dano_disparo;
					
				}
			}
		}
		disparo.destino = destino;
		IArena.disparos.put(guerrero, disparo);
	}

	private void resolver(FichaGuerrero guerrero, Movimiento movimiento) {
		// El movimiento no es fiable
		Posicion destino = null;
		if (movimiento.posicion != null)
			destino = movimiento.posicion;
		if (movimiento.objetivo != null)
			destino = IArena.guerreros.get(movimiento.objetivo).posicion;

		if (destino.x < 0 || destino.x > ANCHO || destino.y < 0 || destino.y > ALTO)
			return;

		double deltaY = destino.y - guerrero.posicion.y;
		double deltaX = destino.x - guerrero.posicion.x;
		double angulo = Math.atan2(deltaY, deltaX);
		guerrero.posicion = new Posicion(guerrero.posicion.x + guerrero.velocidad * Math.cos(angulo),
				guerrero.posicion.y + guerrero.velocidad * Math.sin(angulo));

	}
	
	public void animar(long now){
		if (cicloNuevo) {
			// Quitamos los disparos del ciclo anterior
			for (Node disparoFX : disparosFX)
				raiz.getChildren().remove(disparoFX);
			disparosFX = new ArrayList<>();
			for(FichaGuerrero guerrero : IArena.guerreros.values()){
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
			for (Entry<FichaGuerrero, Disparo> entry : IArena.disparos.entrySet()) {
				Disparo disparo = entry.getValue();
				FichaGuerrero tirador = entry.getKey();
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
		Iterator<Entry<FichaGuerrero, Disparo>> it = IArena.disparos.entrySet().iterator();
		while (it.hasNext()) {
			Entry<FichaGuerrero, Disparo> entry = it.next();
			Disparo disparo = entry.getValue();
			FichaGuerrero tirador = entry.getKey();
			
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
				FichaGuerrero objetivo = guerreros.get(disparo.objetivo);
				if (xActual > objetivo.grupo.getTranslateX()
						&& xActual < objetivo.grupo.getTranslateX() + ANCHO_GUERRERO)
					if (yActual > objetivo.grupo.getTranslateY()
							&& yActual < objetivo.grupo.getTranslateY() + ANCHO_GUERRERO) {
						ImageView ivHerida = new ImageView(imgHerida);
						ivHerida.setFitWidth(ANCHO_GUERRERO);
						ivHerida.setPreserveRatio(true);
						disparo.grupo.getChildren().removeAll(disparo.grupo.getChildren());
						disparo.grupo.getChildren().add(ivHerida);
						it.remove();// Ya no se moverá
						//TODO: objetivo.vida -=  
					}

			}
		}
		// Animación de Movimientos
		for (FichaGuerrero guerrero : IArena.guerreros.values()) {
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

	}

	protected void setCicloNuevo(boolean b) {
		this.cicloNuevo = b;
	}

	private void gameOver() {
		FichaGuerrero ganador = guerreros.values().iterator().next();
		System.out.println("Ha ganado " + ganador.guerrero.getIdentificacion());
		System.exit(0);
	}

	private void preparar() {
		FactoriaGuerreros fg = new FactoriaGuerreros();
		IArena.guerreros = new HashMap<>();
		IArena.disparos = new HashMap<>();

		List<Guerrero> listaGuerreros = fg.getGuerreros();
		Collections.shuffle(listaGuerreros);
		for (Guerrero guerrero : listaGuerreros) {
			if (!guerrero.esValido()) {
				System.out.println("El guerrero " + guerrero.getIdentificacion() + " es rechazado.");
				continue;
			}
			if (guerrero.nombre == null)
				guerrero.nombre = guerrero.getClass().getSimpleName();

			guerrero.posicion = new Posicion(rand.nextInt(ANCHO), rand.nextInt(ALTO));

			// Creamos la ficha del guerrero y su grupoFX
			FichaGuerrero ficha = new FichaGuerrero(guerrero);
			ImageView avatar = new ImageView(guerrero.getAvatar());
			avatar.setFitWidth(ANCHO_GUERRERO);
			avatar.setFitHeight(ALTO_GUERRERO);
			ficha.grupo = new Group();
			ficha.grupo.getChildren().add(avatar);
			raiz.getChildren().add(ficha.grupo);
			ficha.grupo.setTranslateX(ficha.posicion.x * ESCALA - ANCHO_GUERRERO / 2);
			ficha.grupo.setTranslateY(ficha.posicion.y * ESCALA - ALTO_GUERRERO / 2);
			IArena.guerreros.put(guerrero, ficha);

			if (guerrero instanceof Humano) {
				System.out.println("HAY Humano.");
				escenario1.setOnMouseClicked(this);
				this.humano = (Humano) guerrero;
			}
		}
		System.out.println("Registrados " + IArena.guerreros.size() + " guerreros.");

		System.out.println("Comienza la lucha...");
	}

	public static List<FichaGuerrero> getGuerreros() {
		List<FichaGuerrero> clones = new ArrayList<>();
		for (FichaGuerrero guerrero : IArena.guerreros.values())
			clones.add(guerrero.clon());
		return clones;
	}

	public static List<FichaGuerrero> getEnemigosVivos(Guerrero yo) {
		List<FichaGuerrero> clones = new ArrayList<>();
		for (FichaGuerrero guerrero : IArena.guerreros.values())
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

		if (event.getButton().equals(MouseButton.PRIMARY)) {
			Posicion destino = new Posicion(event.getSceneX() / ESCALA, event.getSceneY() / ESCALA);
			humano.jugada = new Movimiento(destino);
		}
		if (event.getButton().equals(MouseButton.SECONDARY)) {
			for (FichaGuerrero ficha : IArena.guerreros.values())
				if (event.getSceneX() > ficha.grupo.getTranslateX()
						&& event.getSceneX() < ficha.grupo.getTranslateX() + ANCHO_GUERRERO)
					if (event.getSceneY() > ficha.grupo.getTranslateY()
							&& event.getSceneY() < ficha.grupo.getTranslateY() + ALTO_GUERRERO)
						humano.jugada = new Disparo(ficha.guerrero);
		}

	}

}
