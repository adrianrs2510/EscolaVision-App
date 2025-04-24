/**
 * Cliente Retrofit para la comunicación con el servidor de EscolaVision.
 * 
 * Este archivo implementa un patrón Singleton para gestionar una única instancia
 * de la configuración de Retrofit en toda la aplicación. Se encarga de:
 * 
 * - Configurar la URL base para todas las llamadas a la API
 * - Establecer el convertidor GSON para la serialización/deserialización JSON
 * - Proporcionar una instancia única de ApiService mediante inicialización perezosa
 * - Gestionar la configuración de red de manera centralizada
 * 
 * La implementación utiliza el patrón Builder de Retrofit para una configuración
 * limpia y mantenible de los servicios de red.
 */

 import com.escolavision.testescolavision.API.ApiService
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Objeto singleton que maneja la configuración y creación del cliente Retrofit
object RetrofitClient {
    // URL base de la API a la que se conectará la aplicación
    private const val BASE_URL = "https://proxy-vercel-ten.vercel.app/"

    // Configuración del serializador GSON para manejar las respuestas JSON
    // setLenient() permite un parsing más flexible de JSON
    private val gson = GsonBuilder()
        .setLenient()
        .create()

    // Propiedad lazy que inicializa la instancia de ApiService solo cuando se necesita
    // Esto sigue el patrón de inicialización perezosa
    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)                                    // Establece la URL base
            .addConverterFactory(GsonConverterFactory.create(gson)) // Añade el convertidor GSON
            .build()                                              // Construye la instancia de Retrofit
            .create(ApiService::class.java)                       // Crea la implementación de la interfaz ApiService
    }
}
