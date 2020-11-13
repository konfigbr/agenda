package osahner.service

import com.google.gson.Gson
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.stereotype.Component
import osahner.dto.justa.request.AlterarCobrancaProgramadaDto
import osahner.dto.justa.request.ProfissionalCfmDto
import java.net.HttpURLConnection
import java.net.URL

@Component
class DocumentoProfissionalService {
    private val logger: Logger = LogManager.getLogger()

    fun connectCfm(inscricao: String, uf: String) : String {
        val token = "XSap-XuNyeoFl4gTgQktT-xzBsfxFDAKof3dfLcC"
        val url = URL("https://api.infosimples.com/api/v1/cfm/cadastro.json?token="+token+"&inscricao="+inscricao+"&uf="+uf)

        var resposta : String? = null
        val connection = url.openConnection() as HttpURLConnection
        connection.setRequestMethod("POST")
        connection.doOutput = true
        connection.setRequestProperty("Content-Type", "application/JSON; charset=UTF-8")

        var writer = connection.outputStream
        writer.flush()
        writer.close()

        connection.connect()
        val connectionRC = connection.getResponseCode()

        if (connectionRC == HttpURLConnection.HTTP_OK) {
            resposta = connection.inputStream.bufferedReader(Charsets.UTF_8).readText()
            logger.info("Resposta " + resposta)
        } else {
            var error = connection.errorStream.bufferedReader(Charsets.UTF_8).readText()
            logger.info(error)
            throw RuntimeException("ERROR: " + connectionRC +" - "+ connection.responseMessage )
        }

        return resposta
    }

    fun connectCfo(inscricao: String, uf: String, categoria: String) : String {
        val token = "XSap-XuNyeoFl4gTgQktT-xzBsfxFDAKof3dfLcC"
        val url = URL("https://api.infosimples.com/api/v1/cfo/cadastro.json?token="+token+"&inscricao="+inscricao+"&uf="+uf+"&categoria="+categoria)

        var resposta : String? = null
        val connection = url.openConnection() as HttpURLConnection
        connection.setRequestMethod("POST")
        connection.doOutput = true
        connection.setRequestProperty("Content-Type", "application/JSON; charset=UTF-8")

        var writer = connection.outputStream
        writer.flush()
        writer.close()

        connection.connect()
        val connectionRC = connection.getResponseCode()

        if (connectionRC == HttpURLConnection.HTTP_OK) {
            resposta = connection.inputStream.bufferedReader(Charsets.UTF_8).readText()
            logger.info("Resposta " + resposta)
        } else {
            var error = connection.errorStream.bufferedReader(Charsets.UTF_8).readText()
            logger.info(error)
            throw RuntimeException("ERROR: " + connectionRC +" - "+ connection.responseMessage )
        }

        return resposta
    }
}