package capsula.aventura.capsula.ia.controlador;

import capsula.aventura.capsula.ia.modelo.ArranqueAventura;
import capsula.aventura.capsula.ia.modelo.SesionAventura;
import capsula.aventura.capsula.ia.servicio.Servicio;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/api/aventura")
public class Controlador {

    private Servicio servicio;

    public Controlador(Servicio servicio) {
        this.servicio = servicio;
    }

    //endpoint de entrada donde el usuario define los parametros de la historia
    @PostMapping("/arrancar")
    public ResponseEntity<SesionAventura> recibirConfiguracion(@RequestBody ArranqueAventura data) {
        String historiaInicial = servicio.arranqueHistoria(data);
        if (SesionAventura.getInstancia() == null) {
            SesionAventura sesion = new SesionAventura();
            sesion.setHistoriaAcumulada(historiaInicial);
            sesion.setTurnoActual(0);
            sesion.setDuracionTotal(data.getCantidadTurnos());
            sesion.setComplejidad(data.getCantidadOpcionesTurnos());
            sesion.setDecisionesTomadas(new ArrayList<>());
            sesion.setHistoriaAcumulada(historiaInicial);
            SesionAventura.setInstancia(sesion);
        }

        return ResponseEntity.ok(SesionAventura.getInstancia());
    }

//se le pasa la historia generada hasta el momento para que devuelva las opciones que el personaje puede tomar para seguir la historia
    @PostMapping("/opciones")
    public ResponseEntity<List<String>> obtenerOpciones() {
        List<String> opciones = servicio.getOpciones();
        if (opciones == null || opciones.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(opciones);
    }

    //devuelve la imagen que representa los ultimos acontecimientos de la historia
    @PostMapping(value = "/imagen", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] getImage() {
        return servicio.getImagen();
    }

    //envia la opcion elegida por el usuario para que continue la historia
    //tiene un vectorStore para recaudar las caracteristicas del megalodon
    @PostMapping("/continuar")
    public ResponseEntity<String> continuarHistoria(@RequestBody String opcionElegida) {
        String resultado = servicio.continuarHistoria(opcionElegida);
        return ResponseEntity.ok(resultado);
    }

    //narra el resumen generado en pdf
    @PostMapping(value ="/audio", produces = "audio/mpeg")
    public byte[] getAudio() {
        return servicio.generarAudio();
    }

    //genera un resumen escrito en pdf de toda la historia, menciona cuantas veces aparece el megalodon y cierra con la frase "gracias por abrirnos los caminos"
    @GetMapping(value = "/resumenEscrito", produces = MediaType.TEXT_PLAIN_VALUE)
    public String resumenEscrito() {
        SesionAventura sesion = SesionAventura.getInstancia();
        servicio.generarResumen();
        return sesion.getResumen();
    }

    //reinicia la historia
    @PostMapping("/reset")
    public ResponseEntity<String> resetSesion() {
        SesionAventura.setInstancia(null);
        return ResponseEntity.ok("Sesión reiniciada");
    }
//devuelve la instancia para ver su estado, los turnos actuales, restantes, historia, etc
    @GetMapping("/estado")
    public SesionAventura getSesion() {
        SesionAventura sesion = SesionAventura.getInstancia();
        return sesion;
    }
}
