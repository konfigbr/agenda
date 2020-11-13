package osahner.utils

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import osahner.repository.AgendamentoRepository
import osahner.repository.AssistenteRepository
import osahner.repository.HistoricoAgendamentoStatusRepository
import osahner.repository.MedicoRepository
import osahner.service.AgendamentoService
import osahner.service.CarrinhoService
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Mark this class an injectable component so that the Spring environment will create
 * an instance of this class when it starts up.
 */
@Component
class ScheduleTasks(
  private val carrinhoService: CarrinhoService)
{

    private val logger = LoggerFactory.getLogger(ScheduleTasks::class.java)
    private val TIME_ZONE : String = "America/Sao_Paulo"

  /**
     * This @Schedule annotation run every 5 seconds in this case. It can also
     * take a cron like syntax.
     * See https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/scheduling/support/CronSequenceGenerator.html
     */
    @Scheduled(fixedRate = 30000, zone = "America/Sao_Paulo" )
    fun reportTime(){
        logger.info("The time is now ${DateTimeFormatter.ISO_LOCAL_TIME.format(LocalDateTime.now())}")
        carrinhoService.encerrarCarrinhoPendente()
    }
}

