package capsula.aventura.capsula.ia.modelo;

import java.util.List;

public class ArranqueAventura {

    private static SesionAventura instancia;

    private String descripcionPersonaje;
    private int cantidadTurnos;
    private int cantidadOpcionesTurnos;
    private String ubicacion;
    private String genero;
    private int cantidadPersonajes;


    public static SesionAventura getInstancia() {
        return instancia;
    }


    public static void setInstancia(SesionAventura instancia) {
        ArranqueAventura.instancia = instancia;
    }

    public String getDescripcionPersonaje() {
        return descripcionPersonaje;
    }

    public void setDescripcionPersonaje(String descripcionPersonaje) {
        this.descripcionPersonaje = descripcionPersonaje;
    }

    public int getCantidadTurnos() {
        return cantidadTurnos;
    }

    public void setCantidadTurnos(int cantidadTurnos) {
        this.cantidadTurnos = cantidadTurnos;
    }

    public int getCantidadOpcionesTurnos() {
        return cantidadOpcionesTurnos;
    }

    public void setCantidadOpcionesTurnos(int cantidadOpcionesTurnos) {
        this.cantidadOpcionesTurnos = cantidadOpcionesTurnos;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public ArranqueAventura(String descripcionPersonaje, int cantidadTurnos, int cantidadOpcionesTurnos, String ubicacion, String genero, int cantidadPersonajes) {
        this.descripcionPersonaje = descripcionPersonaje;
        this.cantidadTurnos = cantidadTurnos;
        this.cantidadOpcionesTurnos = cantidadOpcionesTurnos;
        this.ubicacion = ubicacion;
        this.genero = genero;
        this.cantidadPersonajes = cantidadPersonajes;
    }

    public int getCantidadPersonajes() {
        return cantidadPersonajes;
    }

    public void setCantidadPersonajes(int cantidadPersonajes) {
        this.cantidadPersonajes = cantidadPersonajes;
    }
}
