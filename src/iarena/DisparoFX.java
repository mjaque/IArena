package iarena;

import iarena.app.IArena;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class DisparoFX {
	public enum Resultado {SIGUE, DIANA, FALLO};
	private Circle circle;
	private FichaGuerrero origen, objetivo;

	public DisparoFX(FichaGuerrero origen, FichaGuerrero objetivo) {
		this.origen = origen;
		this.objetivo = objetivo;
		circle = new Circle(5);
		circle.setFill(Color.RED);
		circle.setTranslateX((origen.posicion.x + 0.5) * IArena.ANCHO_CELDA);
		circle.setTranslateY((origen.posicion.y + 0.5) * IArena.ALTO_CELDA);
	}

	public Node getNode() {
		return circle;
	}

	public Resultado mover() {
		double deltaY = objetivo.grupo.getTranslateY() + IArena.ALTO_CELDA/2 - circle.getTranslateY();
		double deltaX = objetivo.grupo.getTranslateX() + IArena.ANCHO_CELDA/2 - circle.getTranslateX();
		double angulo = Math.atan2(deltaY, deltaX);
		circle.setTranslateX(circle.getTranslateX() + IArena.PASO_DISPARO * Math.cos(angulo));
		circle.setTranslateY(circle.getTranslateY() + IArena.PASO_DISPARO * Math.sin(angulo));
		
		if ( circle.getTranslateX() > objetivo.grupo.getTranslateX() &&
				circle.getTranslateX() < objetivo.grupo.getTranslateX() + IArena.ANCHO_CELDA )
				if ( circle.getTranslateY() > objetivo.grupo.getTranslateY() &&
						circle.getTranslateY() < objetivo.grupo.getTranslateY() + IArena.ALTO_CELDA )
						return Resultado.DIANA;
		if (IArena.distancia(circle, origen.grupo) > IArena.DISTANCIA_DISPARO * IArena.ALTO_CELDA)	//TODO: Mejorar.
			return Resultado.FALLO;
		
		return Resultado.SIGUE;
	}

	public FichaGuerrero getObjetivo() {
		return objetivo;
	}

}
