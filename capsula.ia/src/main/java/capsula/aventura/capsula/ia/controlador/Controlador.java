package capsula.aventura.capsula.ia.controlador;

import capsula.aventura.capsula.ia.modelo.ArranqueAventura;
import capsula.aventura.capsula.ia.modelo.SesionAventura;
import capsula.aventura.capsula.ia.servicio.Servicio;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/aventura")
public class Controlador {

    private final Servicio servicio;

    public Controlador(Servicio servicio) {
        this.servicio = servicio;
    }

    @PostMapping("/arrancar")
    public Mono<ResponseEntity<SesionAventura>> recibirConfiguracion(@RequestBody ArranqueAventura data) {
        return Mono.fromCallable(() -> {
            String historiaInicial = servicio.arranqueHistoria(data);

            if (SesionAventura.getInstancia() == null) {
                SesionAventura sesion = new SesionAventura();
                sesion.setHistoriaAcumulada(historiaInicial);
                sesion.setTurnoActual(0);
                sesion.setDuracionTotal(data.getCantidadTurnos());
                sesion.setComplejidad(data.getCantidadOpcionesTurnos());
                sesion.setDecisionesTomadas(new ArrayList<>());
                SesionAventura.setInstancia(sesion);
            }

            return ResponseEntity.ok(SesionAventura.getInstancia());
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @PostMapping("/opciones")
    public Mono<ResponseEntity<?>> obtenerOpciones() {
        return Mono.fromCallable(() -> {
            List<String> opciones = servicio.getOpciones();
            if (opciones == null || opciones.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
            return ResponseEntity.ok(opciones);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @PostMapping(value = "/imagen", produces = MediaType.IMAGE_PNG_VALUE)
    public Mono<byte[]> getImage() {
        return Mono.fromCallable(servicio::getImagen)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @PostMapping("/continuar")
    public Mono<ResponseEntity<String>> continuarHistoria(@RequestBody String opcionElegida) {
        return Mono.fromCallable(() -> {
            String resultado = servicio.continuarHistoria(opcionElegida);
            return ResponseEntity.ok(resultado);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @PostMapping(value = "/audio", produces = "audio/mpeg")
    public Mono<byte[]> getAudio() {
        return Mono.fromCallable(servicio::generarAudio)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @GetMapping(value = "/resumenEscrito", produces = MediaType.TEXT_PLAIN_VALUE)
    public Mono<String> resumenEscrito() {
        return Mono.fromCallable(() -> {
            SesionAventura sesion = SesionAventura.getInstancia();
            servicio.generarResumen();
            return sesion.getResumen();
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @PostMapping("/reset")
    public Mono<ResponseEntity<String>> resetSesion() {
        return Mono.fromCallable(() -> {
            SesionAventura.setInstancia(null);
            return ResponseEntity.ok("Sesión reiniciada");
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @GetMapping("/estado")
    public Mono<SesionAventura> getSesion() {
        return Mono.fromCallable(SesionAventura::getInstancia)
                .subscribeOn(Schedulers.boundedElastic());
    }
}
