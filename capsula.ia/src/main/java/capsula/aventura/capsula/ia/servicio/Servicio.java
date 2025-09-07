package capsula.aventura.capsula.ia.servicio;

import capsula.aventura.capsula.ia.modelo.ArranqueAventura;
import capsula.aventura.capsula.ia.modelo.SesionAventura;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.ai.openai.OpenAiAudioSpeechOptions;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.ai.openai.audio.speech.SpeechPrompt;
import org.springframework.ai.openai.audio.speech.SpeechResponse;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.ai.image.ImageModel;

import java.io.IOException;
import java.util.*;

@Service
public class Servicio {
    private final OpenAiAudioSpeechModel speechModel;
    private ImageModel imageModel;
    final SimpleVectorStore vectorStore;
    private ChatModel chatModel;

    @Value("classpath:templates/inicio-aventura.st")
    private Resource getAventuraPrompt;

    @Value("classpath:templates/opciones.st")
    private Resource opcionesPrompt;

    @Value("classpath:templates/continuacion.st")
    private Resource continuacionPrompt;

    @Value("classpath:templates/resumenPrompt.st")
    private Resource resumenPrompt;

    @Value("classpath:templates/final-historia.st")
    private Resource finalHistoriaPrompt;

    @Autowired
    public Servicio(OpenAiAudioSpeechModel speechModel, SimpleVectorStore vectorStore, ChatModel chatModel, ImageModel imageModel) {
        this.speechModel = speechModel;
        this.vectorStore = vectorStore;
        this.chatModel = chatModel;
        this.imageModel = imageModel;
    }

    public String arranqueHistoria(ArranqueAventura aventura) {
        PromptTemplate promptTemplate = new PromptTemplate(getAventuraPrompt);
        Prompt prompt = promptTemplate.create(Map.of(
                "genero", aventura.getGenero(),
                "descripcion", aventura.getDescripcionPersonaje(),
                "duracion", aventura.getCantidadTurnos(),
                "ubicacion", aventura.getUbicacion(),
                "personajes", aventura.getCantidadPersonajes()
        ));
        ChatResponse response = chatModel.call(prompt);
        return (response.getResult().getOutput().getText());
    }

    public byte[] getImagen() {
        SesionAventura sesion = SesionAventura.getInstancia();
       /* String prompt = String.format(sesion.getUltimos300Caracteres());
        var options = OpenAiImageOptions.builder()
                .width(1024)
                .height(1024)
                .responseFormat("b64_json")
                .build();
        ImagePrompt imagePrompt = new ImagePrompt(
                prompt,
                options);
        var imageResponse = imageModel.call(imagePrompt);
        sesion.setImagen(Base64.getDecoder().decode(imageResponse.getResult().getOutput().getB64Json()));
        */
        String relativePath = "static/imagen_prueba.jpg";

        try {
            ClassPathResource resource = new ClassPathResource(relativePath);
            byte[] imageBytes = resource.getInputStream().readAllBytes();
            sesion.setImagen(imageBytes);

        } catch (IOException e) {
            System.err.println("Error al leer la imagen por defecto: " + e.getMessage());
        }
        return sesion.getImagen();
    }
    public List<String> getOpciones() {
        SesionAventura sesion = SesionAventura.getInstancia();
        if (sesion == null) return Collections.emptyList();

        String historia = sesion.getHistoriaAcumulada();
        int cantidad = sesion.getComplejidad();
        String format = "json";

        PromptTemplate promptTemplate = new PromptTemplate(opcionesPrompt);
        Prompt prompt = promptTemplate.create(Map.of(
                "historia", historia,
                "cantidad", cantidad,
                "format", format
        ));

        ChatResponse response = chatModel.call(prompt);
        String respuesta = response.getResult().getOutput().getText();


        respuesta = respuesta.trim();
        if (respuesta.startsWith("```")) {
            respuesta = respuesta.replaceAll("```json", "")
                    .replaceAll("```", "")
                    .trim();
        }


        int start = respuesta.indexOf("[");
        int end = respuesta.lastIndexOf("]");
        if (start != -1 && end != -1) {
            respuesta = respuesta.substring(start, end + 1);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(
                    respuesta,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, String.class)
            );
        } catch (JsonProcessingException e) {
            System.err.println("Error al parsear la respuesta JSON del modelo: " + e.getMessage());
            System.err.println("Respuesta recibida: " + respuesta);
            return Collections.emptyList();
        }
    }

    public String continuarHistoria(String opcionElegida) {
        SesionAventura sesion = SesionAventura.getInstancia();
        if (sesion == null) return "No hay una sesión activa.";

        boolean esUltimoTurno = (sesion.getTurnoActual() + 1) == sesion.getDuracionTotal();

        if (!esUltimoTurno) {

            String consultaRag = opcionElegida != null && !opcionElegida.isBlank()
                    ? opcionElegida
                    : sesion.getHistoriaAcumulada();

            List<Document> documents = vectorStore.similaritySearch(
                    SearchRequest.builder()
                            .query(consultaRag)
                            .topK(5)
                            .build()
            );

            if (documents == null) {
                documents = Collections.emptyList();
            }

            List<String> contentList = documents.stream()
                    .map(Document::getText)
                    .toList();

            String joinedDocs = contentList.isEmpty()
                    ? "No hay información adicional disponible."
                    : String.join("\n", contentList);


            sesion.getDecisionesTomadas().add(opcionElegida);


            PromptTemplate promptTemplate = new PromptTemplate(continuacionPrompt);
            Prompt prompt = promptTemplate.create(Map.of(
                    "historia", sesion.getHistoriaAcumulada(),
                    "opcion", opcionElegida,
                    "documents", joinedDocs
            ));
            ChatResponse response = chatModel.call(prompt);
            String nuevoFragmento = response.getResult().getOutput().getText();

            sesion.setHistoriaAcumulada(sesion.getHistoriaAcumulada() + " " + nuevoFragmento);
            sesion.setTurnoActual(sesion.getTurnoActual() + 1);

            return nuevoFragmento;
        }else{
            PromptTemplate promptTemplate = new PromptTemplate(finalHistoriaPrompt);
            Prompt prompt = promptTemplate.create(Map.of(
                    "historia", sesion.getHistoriaAcumulada()
            ));
            ChatResponse response = chatModel.call(prompt);
            String nuevoFragmento = response.getResult().getOutput().getText();
            sesion.setHistoriaAcumulada(sesion.getHistoriaAcumulada() + " " + nuevoFragmento);
            sesion.setFinalizada(true);
            return nuevoFragmento;
        }

    }

    public String generarResumen() {
        SesionAventura sesion = SesionAventura.getInstancia();

        if (!sesion.isFinalizada()) {
            return "La aventura aún no ha finalizado.";
        }

        PromptTemplate resumenTemplate = new PromptTemplate(resumenPrompt);
        Prompt resumenPromptInstance = resumenTemplate.create(Map.of(
                "historia", sesion.getHistoriaAcumulada(),
                "decisiones", String.join(", ", sesion.getDecisionesTomadas())
        ));

        ChatResponse response = chatModel.call(resumenPromptInstance);
        sesion.setResumen(response.getResult().getOutput().getText());
        return response.getResult().getOutput().getText();
    }


    public byte[] generarAudio() {
        SesionAventura sesion = SesionAventura.getInstancia();
        OpenAiAudioSpeechOptions speechOptions = OpenAiAudioSpeechOptions.builder()
                .voice(OpenAiAudioApi.SpeechRequest.Voice.ALLOY)
                .speed(1.0f)
                .responseFormat(OpenAiAudioApi.SpeechRequest.AudioResponseFormat.MP3)
                .model(OpenAiAudioApi.TtsModel.TTS_1.value)
                .build();

        SpeechPrompt speechPrompt = new SpeechPrompt(sesion.getResumen(), speechOptions);

        SpeechResponse response = speechModel.call(speechPrompt);

        return response.getResult().getOutput();
    }

}