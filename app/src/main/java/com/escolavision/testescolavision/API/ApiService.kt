package com.escolavision.testescolavision.API

import com.squareup.moshi.JsonClass
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

// Clases de datos para la solicitud y respuesta de inicio de sesión
data class LoginRequest(val usuario: String, val contrasena: String)

data class LoginResponse(
    val status: String,      // Estado de la respuesta
    val message: String,     // Mensaje de la respuesta
    val id: Int,            // ID del usuario
    val nombre: String,      // Nombre del usuario
    val apellido: String,    // Apellido del usuario
    val is_orientador: Int,  // Indica si es orientador (1) o no (0)
    val tipo: String,        // Tipo de usuario
    val dni: String,         // DNI del usuario
    val id_centro: String    // ID del centro educativo
)

// Clase para representar un test
@JsonClass(generateAdapter = true)
data class Test(
    val id: Int,
    val nombretest: String,
    val isVisible: Int       // Indica si el test es visible (1) o no (0)
)

// Clase para representar preguntas del test
data class Preguntas(
    val id: Int,
    val idtest: Int,        // ID del test al que pertenece
    val enunciado: String,  // Texto de la pregunta
)

// Clase para representar áreas de evaluación
data class Area(
    val id: Int,
    val nombre: String,
    val descripción: String,
    val logo: String        // Ruta o URL del logo del área
)

// Clase para relacionar preguntas con áreas (PxA = Pregunta por Área)
data class PxA(
    val id: Int,
    val idpregunta: Int,    // ID de la pregunta
    val idarea: Int,        // ID del área relacionada
)

// Clases para el registro de usuarios
@JsonClass(generateAdapter = true)
data class RegisterRequest(
    val tabla: String,      // Nombre de la tabla en la base de datos
    val datos: Usuarios     // Datos del usuario a registrar
)

// Clase para registrar intentos de test
@JsonClass(generateAdapter = true)
data class IntentoRequest(
    val tabla: String,
    val datos: Intento
)

// Clase que representa a un usuario
data class Usuarios(
    val id: Int,
    val nombre: String,
    val dni: String,
    val contraseña: String,
    var foto: String?,
    val tipo_usuario: String,
    val is_orientador: Int,
    val fecha_nacimiento: String,
    val email: String,
    val id_centro: String
)

// Clase que representa un intento de test
@JsonClass(generateAdapter = true)
data class Intento(
    val idtest: Int = 0,
    val idusuario: Int = 0,
    val fecha: String = "",
    val hora: String = "",
    val resultados: String = "",
)

// Clases de respuesta para diferentes operaciones
data class RegisterResponse(
    val status: String,
    val message: String? = null
)

data class IntentoResponse(
    val status: String,
    val message: String? = null
)

// Clases wrapper para listas de respuesta
@JsonClass(generateAdapter = true)
data class TestsResponse(
    val tests: List<Test>
)

@JsonClass(generateAdapter = true)
data class UsuariosListResponse(
    val usuarios: List<Usuarios>
)


@JsonClass(generateAdapter = true)
data class PreguntasListResponse(
    val preguntas: List<Preguntas>
)

@JsonClass(generateAdapter = true)
data class PxaListResponse(
    val pxa: List<PxA>
)

@JsonClass(generateAdapter = true)
data class IntentoListResponse(
    val intentos: List<Intento>
)

@JsonClass(generateAdapter = true)
data class AreaListResponse(
    val areas: List<Area>
)

@JsonClass(generateAdapter = true)
data class DeleteRequest(
    val tabla: String,
    val id: Int
)

@JsonClass(generateAdapter = true)
data class DeleteResponse(
    val status: String,
    val message: String? = null
)

@JsonClass(generateAdapter = true)
data class UpdateRequest(
    val tabla: String,
    val datos: Usuarios,
    val id: Int
)

@JsonClass(generateAdapter = true)
data class UpdateProfileResponse(
    val status: String,
    val message: String? = null
)

@JsonClass(generateAdapter = true)
data class CentroListResponse(
    val centros: List<Centro>
)

data class Centro(
    val denominacion_especifica	: String,
    val id: String
)

data class CentroResponse(
    val centros: List<CentroCompleto>
)

data class CentroCompleto(
    val id: Int,
    val comunidad_autonoma: String,
    val provincia: String,
    val localidad: String,
    val denominacion_generica: String,
    val denominacion_especifica: String,
    val codigo: String,
    val naturaleza: String,
    val domicilio: String,
    val codigo_postal: String,
    val telefono: String,
    val telefono_secundario: String
)


// Interfaz que define todos los endpoints de la API
interface ApiService {
    // Endpoint para inicio de sesión
    @POST("login.php")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    // Endpoint para obtener tests
    @GET("leer.php?tabla=tests")
    fun getTests(): Call<TestsResponse>

    // Endpoint para registro de usuarios
    @POST("insertar.php")
    fun register(@Body request: RegisterRequest): Call<RegisterResponse>

    // Endpoint para obtener datos de usuarios por centro
    @GET("leer.php")
    fun getUsuarioData(@Query("tabla") tabla: String = "usuarios", @Query("id_centro") id_centro: String): Call<UsuariosListResponse>

    // Endpoint para obtener preguntas
    @GET("leer.php?tabla=preguntas")
    fun getPreguntas(): Call<PreguntasListResponse>

    // Endpoint para obtener relaciones pregunta-área
    @GET("leer.php?tabla=pxa")
    fun getPxa(): Call<PxaListResponse>

    // Endpoint para registrar intentos de test
    @POST("insertar.php")
    fun insertarIntento(@Body request: IntentoRequest): Call<IntentoResponse>

    // Endpoint para obtener áreas
    @GET("leer.php?tabla=areas")
    fun getAreas(): Call<AreaListResponse>

    // Endpoint para obtener intentos por centro
    @GET("leer.php")
    fun getIntentos(@Query("tabla") tabla: String = "intentos", @Query("id_centro") id_centro: String): Call<IntentoListResponse>

    // Endpoint para eliminar registros
    @HTTP(method = "DELETE", path = "borrar.php", hasBody = true)
    fun delete(@Body request: DeleteRequest): Call<DeleteResponse>

    // Endpoint para actualizar perfil
    @PUT("actualizar.php")
    fun update(@Body request: UpdateRequest): Call<UpdateProfileResponse>

    // Endpoints para gestión de centros educativos
    @GET("leer.php")
    fun searchCentros(@Query("tabla") tabla: String = "centros", @Query("localidad") localidad: String): Call<CentroListResponse>

    @GET("leer.php")
    fun getCentro(@Query("tabla") tabla: String = "centros", @Query("id") id: String): Call<CentroResponse>
}

