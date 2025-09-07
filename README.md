# Aventura Interactiva

Aplicación web que permite a un usuario crear y vivir una historia interactiva generada dinámicamente. El usuario configura parámetros iniciales, la aplicación genera una historia inicial y una imagen asociada. Luego, el usuario puede avanzar la historia seleccionando entre opciones que el sistema ofrece, con nuevas imágenes y actualizaciones de la trama. Al finalizar la aventura, se puede descargar un resumen en PDF, escuchar una narración y reiniciar la historia para comenzar de nuevo.
DEMO: https://www.youtube.com/watch?v=vrIoScIsiD0


---

## Características principales

- Configuración inicial de la aventura: descripción del personaje, cantidad de turnos, opciones por turno, ubicación, género y cantidad de personajes.
- Generación dinámica de la historia y una imagen representativa por turno.
- Botón "Continuar" que muestra opciones para que el usuario elija el rumbo de la historia.
- Al final de la aventura:
  - Botón para descargar un resumen completo en PDF.
  - Botón para reproducir una narración en audio.
  - Botón para reiniciar la aventura y comenzar de nuevo.

---

## Arquitectura y tecnologías

- Backend con Spring Boot (`@RestController`).
- API REST para manejo de la aventura.
- Frontend sencillo con HTML, CSS y JavaScript.
- Generación de PDF con [jsPDF](https://github.com/parallax/jsPDF).
- Reproducción de audio generado dinámicamente.
- Imágenes representativas por turno en formato PNG.

---

## Endpoints principales del backend (`/api/aventura`)

| Endpoint                | Método | Descripción                                                                                   |
|-------------------------|---------|-----------------------------------------------------------------------------------------------|
| `/arrancar`             | POST    | Recibe configuración inicial, genera la historia base y crea una nueva sesión de aventura.   |
| `/opciones`             | POST    | Devuelve las opciones disponibles para continuar la historia en el turno actual.             |
| `/imagen`               | POST    | Retorna la imagen PNG que representa el estado actual de la historia.                        |
| `/continuar`            | POST    | Recibe la opción elegida y genera la continuación de la historia.                            |
| `/audio`                | POST    | Devuelve un archivo de audio (mp3) con la narración del resumen final de la aventura.        |
| `/resumenEscrito`       | GET     | Retorna el texto del resumen completo de la aventura para generar el PDF.                    |
| `/reset`                | POST    | Reinicia la sesión de aventura para comenzar de nuevo.                                       |
| `/estado`               | GET     | Devuelve el estado actual de la sesión (historia acumulada, turno actual, etc).               |

---

## Uso

1. Ejecutar la aplicación backend (Spring Boot) en `localhost`.
2. Abrir `http://localhost:8080/index.html` para configurar los parámetros iniciales y comenzar la aventura.
3. Durante la aventura, usar los botones para:
   - Obtener opciones para continuar la historia.
   - Descargar el resumen en PDF.
   - Reproducir la narración en audio.
   - Reiniciar la aventura.

---

## Archivos importantes

- **Controlador.java**: controlador REST que maneja los endpoints de la aventura.
- **index.html**: página inicial para configuración y arranque de la aventura.
- **aventura.html**: página principal donde se desarrolla la aventura interactiva.
- **assets/**: carpeta con imágenes, estilos y scripts necesarios.
- **js/pdf y audio libs**: jsPDF y html2canvas para generación y manejo de PDF y audio.

---

## Consideraciones

- La historia se mantiene en sesión única (`SesionAventura` singleton).
- La imagen cambia dinámicamente según el turno.
- El vector store y detalles del "Megalodón" están integrados en la lógica de historia y generación.
- La aplicación está diseñada para correr localmente, ajusta URLs si la despliegas en otro entorno.
- El tool calling lo implemente, funcionaba pero no de la manera correcta. Elegia correctamente la función pero como esta deprecado según los videos del tutorial, se quedaba en loop.

---



