package com.escolavision.testescolavision.API

import com.squareup.moshi.JsonClass
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

// Data class para la solicitud de inicio de sesión
data class   LoginRequest(val usuario: String, val contrasena: String)

// Data class para la respuesta de inicio de sesión
data class LoginResponse(
    val status: String,
    val message: String,
    val id: Int,
    val nombre: String,
    val apellido: String,
    val is_orientador: Int,
    val tipo: String,
    val dni: String,
    val id_centro: String
)

// Data class para los tests
@JsonClass(generateAdapter = true)
data class Test(
    val id: Int,
    val nombretest: String,
    val isVisible: Int
)


// Data class para los profesores
data class Preguntas(
    val id: Int,
    val idtest: Int,
    val enunciado: String,
)

// Data class para las areas
data class Area(
    val id: Int,
    val nombre: String,
    val descripción: String,
    val logo: String
)

// Data class para los profesores
data class PxA(
    val id: Int,
    val idpregunta: Int,
    val idarea: Int,
)


// Data class para la solicitud de registro
@JsonClass(generateAdapter = true)
data class RegisterRequest(
    val tabla: String,
    val datos: Usuarios
)

// Data class para la solicitud de intentos
@JsonClass(generateAdapter = true)
data class IntentoRequest(
    val tabla: String,
    val datos: Intento
)

// Data class para los alumnos
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
    val id_centro : String
)

@JsonClass(generateAdapter = true)
data class Intento(
    val idtest: Int = 0,
    val idusuario: Int = 0,
    val fecha: String = "",
    val hora: String = "",
    val resultados: String = "",
)



// Data class para la respuesta del registro
data class RegisterResponse(
    val status: String,
    val message: String? = null
)

// Data class para la respuesta del intento
data class IntentoResponse(
    val status: String,
    val message: String? = null
)


// Data class para la respuesta de los tests
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


// Interfaz de Retrofit para las llamadas API
interface ApiService {
    @POST("login.php")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @GET("leer.php?tabla=tests")
    fun getTests(): Call<TestsResponse>

    @POST("insertar.php")
    fun register(@Body request: RegisterRequest): Call<RegisterResponse>

    @GET("leer.php")
    fun getUsuarioData(@Query("tabla") tabla: String = "usuarios", @Query("id_centro") id_centro: String): Call<UsuariosListResponse>

    @GET("leer.php?tabla=preguntas")
    fun getPreguntas(): Call<PreguntasListResponse>

    @GET("leer.php?tabla=pxa")
    fun getPxa(): Call<PxaListResponse>

    @POST("insertar.php")
    fun insertarIntento(@Body request: IntentoRequest): Call<IntentoResponse>

    @GET("leer.php?tabla=areas")
    fun getAreas(): Call<AreaListResponse>

    @GET("leer.php")
    fun getIntentos(@Query("tabla") tabla: String = "intentos", @Query("id_centro") id_centro: String): Call<IntentoListResponse>

    @HTTP(method = "DELETE", path = "borrar.php", hasBody = true)
    fun delete(@Body request: DeleteRequest): Call<DeleteResponse>

    @PUT("actualizar.php")
    fun update(@Body request: UpdateRequest): Call<UpdateProfileResponse>

    @GET("leer.php")
    fun searchCentros(@Query("tabla") tabla: String = "centros", @Query("localidad") localidad: String): Call<CentroListResponse>

    @GET("leer.php")
    fun getCentro(@Query("tabla") tabla: String = "centros", @Query("id") id: String): Call<CentroResponse>

}

