package capsula.aventura.capsula.ia.modelo;

import java.util.List;

public class SesionAventura {

    private static SesionAventura instancia;

    private String historiaAcumulada;
    private int turnoActual;
    private int duracionTotal;
    private int complejidad;
    private List<String> decisionesTomadas;
    private boolean finalizada = Boolean.FALSE;
    private String resumen;
    private byte[] audio;
    private byte[] imagen;

    public SesionAventura(String historiaAcumulada, int turnoActual, int duracionTotal, int complejidad, List<String> decisionesTomadas) {
        this.historiaAcumulada = historiaAcumulada;
        this.turnoActual = turnoActual;
        this.duracionTotal = duracionTotal;
        this.complejidad = complejidad;
        this.decisionesTomadas = decisionesTomadas;
    }



    public SesionAventura() {
    }

    public static SesionAventura getInstancia() {
        return instancia;
    }

    public static void setInstancia(SesionAventura instancia) {
        SesionAventura.instancia = instancia;
    }

    public String getHistoriaAcumulada() {
        return historiaAcumulada;
    }

    public void setHistoriaAcumulada(String historiaAcumulada) {
        this.historiaAcumulada = historiaAcumulada;
    }

    public int getDuracionTotal() {
        return duracionTotal;
    }

    public void setDuracionTotal(int duracionTotal) {
        this.duracionTotal = duracionTotal;
    }

    public int getTurnoActual() {
        return turnoActual;
    }

    public void setTurnoActual(int turnoActual) {
        this.turnoActual = turnoActual;
    }

    public int getComplejidad() {
        return complejidad;
    }

    public void setComplejidad(int complejidad) {
        this.complejidad = complejidad;
    }

    public List<String> getDecisionesTomadas() {
        return decisionesTomadas;
    }

    public void setDecisionesTomadas(List<String> decisionesTomadas) {
        this.decisionesTomadas = decisionesTomadas;
    }
    public byte[] getAudio() {
        return audio;
    }

    public void setAudio(byte[] audio) {
        this.audio = audio;
    }


    public String getResumen() {
        return resumen;
    }

    public void setResumen(String resumen) {
        this.resumen = resumen;
    }

    public boolean isFinalizada() {
        return finalizada;
    }

    public void setFinalizada(boolean finalizada) {
        this.finalizada = finalizada;
    }

    public byte[] getImagen() {
        return imagen;
    }

    public void setImagen(byte[] imagen) {
        this.imagen = imagen;
    }
    public String getUltimos300Caracteres() {
        if (historiaAcumulada == null) return "";
        int length = historiaAcumulada.length();
        if (length <= 200) {
            return historiaAcumulada;
        }
        return historiaAcumulada.substring(length - 200);
    }
}
