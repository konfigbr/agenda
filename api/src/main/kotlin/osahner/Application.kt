package osahner

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.annotation.EnableScheduling
import osahner.filestorage.FileStorage
import java.util.*
import javax.annotation.PostConstruct


@SpringBootApplication(exclude = [(RepositoryRestMvcAutoConfiguration::class)])
@EnableCaching
@EnableScheduling
class Application : SpringBootServletInitializer()

@Autowired
lateinit var fileStorage: FileStorage;


@Bean
fun run() = CommandLineRunner {
  fileStorage.deleteAll()
  fileStorage.init()
}


fun main(args: Array<String>) {
  //TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo"));
  configureApplication(SpringApplicationBuilder()).run(*args)

}

fun configureApplication(builder: SpringApplicationBuilder): SpringApplicationBuilder {
  return builder.sources(Application::class.java)
}
