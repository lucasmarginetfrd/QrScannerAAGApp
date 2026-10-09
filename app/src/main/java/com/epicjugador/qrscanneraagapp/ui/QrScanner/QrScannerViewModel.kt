package com.epicjugador.qrscanneraagapp.ui.QrScanner

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.epicjugador.qrscanneraagapp.data.repository.FirebaseRepository
import com.epicjugador.qrscanneraagapp.domain.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import jakarta.inject.Inject
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.jsoup.Jsoup
import com.epicjugador.qrscanneraagapp.utils.DeviceUuidManager
import com.epicjugador.qrscanneraagapp.data.model.ScanEvent
import com.epicjugador.qrscanneraagapp.data.model.DeviceRegister
import kotlinx.coroutines.withContext
import java.time.Instant
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers

@HiltViewModel
class QrScannerViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository,
    private val application: Application
) : AndroidViewModel(application) {

    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    private val _navigationEvent = MutableLiveData<Event<String>>()
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    val navigationEvent: LiveData<Event<String>> = _navigationEvent

    private val _result = MutableLiveData<String>()
    val result: LiveData<String> = _result

    private val DOMINIO_CORRECTO: String = "aag.ydo.com.ar"
    private val DOMINIO_VIEJO: String = "aag.ydo.org.ar"


    @RequiresApi(Build.VERSION_CODES.O)
    fun processQr(url: String) {

        viewModelScope.launch {

            _loading.value = true

            // 🔹 CASO MATRICULADOS (lo que ya tenías)
            if (url.startsWith(
                    "https://golf.org.ar/enrolled/detailMobile",
                    ignoreCase = true
                )
            ) {

                val result = fetchQrData(url)

                val matricula = extraerMatricula(result)

                Log.d("AAG_MATRICULA", "Matricula detectada: $matricula")

                if (matricula.isNotBlank()) {
                    registrarIngresoMatriculado(matricula)
                }

                _navigationEvent.value = Event(result)

                _loading.value = false

                saveScanEvent(url)
                return@launch
            }

            // 🔹 CASO ENTRADAS AAG
            if (
                url.contains("aag.ydo.com.ar", ignoreCase = true) ||
                url.contains("aag.ydo.org.ar", ignoreCase = true)
            ) {

                val result = validarEntradaConServidor(url)
                _navigationEvent.value = Event(result)

                _loading.value = false

                saveScanEvent(url)
                return@launch
            }

            // ❌ Cualquier otro QR
            _navigationEvent.value = Event("QR no válido")
            _loading.value = false
        }
    }

    private fun extraerMatricula(texto: String): String {

        val regex = Regex("""(?i)matricula\s+(\d+)""")
        val match = regex.find(texto)

        return match?.groupValues?.get(1) ?: ""
    }

    private fun registrarIngresoMatriculado(matricula: String) {

        if (matricula.isBlank()) return

        viewModelScope.launch(Dispatchers.IO) {

            try {

                val equipoId = DeviceUuidManager
                    .getUuid(getApplication())

                val urlFinal =
                    "http://aag.ydo.com.ar/asis_asistencia.php?m=$matricula&e=$equipoId"

                httpClient.get(urlFinal)

            } catch (e: Exception) {
                Log.e("AAG_REGISTRO_ERROR", e.toString())
            }
        }
    }

    private suspend fun validarEntradaConServidor(originalUrl: String): String {

        return withContext(kotlinx.coroutines.Dispatchers.IO) {

            try {

                // 🔥 1️⃣ Reemplazar dominio si es el viejo
                val urlCorregida = if (originalUrl.contains(DOMINIO_VIEJO, ignoreCase = true)) {
                    originalUrl.replace(DOMINIO_VIEJO, DOMINIO_CORRECTO)
                } else {
                    originalUrl
                }

                android.util.Log.d("AAG_STEP", "URL corregida: $urlCorregida")

                val uri = urlCorregida.toUri()
                val idEntrada = uri.getQueryParameter("identrada")

                if (idEntrada.isNullOrEmpty()) {
                    return@withContext "Entrada inválida"
                }

                val context = getApplication<Application>().applicationContext
                val idEquipo = DeviceUuidManager.getUuid(context)

                // 🔥 2️⃣ Construir URL final SIEMPRE con dominio correcto
                val finalUrl =
                    "http://$DOMINIO_CORRECTO/asis_asistencia.php?identrada=$idEntrada&e=$idEquipo"

                android.util.Log.d("AAG_STEP", "Final URL: $finalUrl")

                val responseHtml: String = httpClient.get(finalUrl).body()

                val doc = org.jsoup.Jsoup.parse(responseHtml)
                val bodyText = doc.body()?.text()

                return@withContext bodyText ?: "Respuesta vacía"

            } catch (e: Exception) {
                android.util.Log.e("AAG_ERROR", "ERROR:", e)
                return@withContext "Error de conexión con el servidor"
            }
        }
    }

    private suspend fun fetchQrData(url: String): String {

        return withContext(kotlinx.coroutines.Dispatchers.IO) {

            val response: String = httpClient.get(url).body()
            val doc = Jsoup.parse(response)

            val spans = doc.select("span").take(2)
            val spanText = spans.joinToString("\n") { it.text() }

            val table = doc.selectFirst("table")
            val tableText = table?.select("tr")
                ?.joinToString("\n") { row ->
                    row.select("td, th").joinToString(" ") { it.text() }
                } ?: "No se encontró ninguna tabla"

            "$spanText\n$tableText"
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveScanEvent(url: String) {

        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {

            try {

                val context = getApplication<Application>().applicationContext

                val scanEvent = ScanEvent(
                    qrUrl = url,
                    timestamp = getArgentinaTime(),
                    deviceUuid = DeviceUuidManager.getUuid(context)
                )

                firebaseRepository.saveScanEvent(scanEvent)

            } catch (_: Exception) {}
        }
    }



    @RequiresApi(Build.VERSION_CODES.O)
    private fun getArgentinaTime(): String {

        val zoneId = java.time.ZoneId.of("America/Argentina/Buenos_Aires")

        val formatter = java.time.format.DateTimeFormatter
            .ofPattern("dd/MM/yyyy HH:mm:ss")
            .withZone(zoneId)

        return formatter.format(java.time.Instant.now())
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun registerDeviceIfNeeded() {

        val context = getApplication<Application>().applicationContext
        val uuid = DeviceUuidManager.getUuid(context)

        viewModelScope.launch {

            try {

                val now = getArgentinaTime()

                if (!DeviceUuidManager.isRegistered(context)) {

                    val deviceRegister = DeviceRegister(
                        uuid = uuid,
                        firstOpenTime = now,
                        lastOpenTime = now
                    )

                    firebaseRepository.registerDevice(deviceRegister)

                    DeviceUuidManager.markRegistered(context)

                } else {

                    firebaseRepository.updateLastOpen(
                        uuid,
                        now
                    )
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }




    override fun onCleared() {
        super.onCleared()
        httpClient.close()
    }
}
