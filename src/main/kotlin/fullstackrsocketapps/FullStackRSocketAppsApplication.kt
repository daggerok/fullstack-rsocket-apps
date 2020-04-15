package fullstackrsocketapps

import org.apache.logging.log4j.LogManager
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.core.io.ClassPathResource
import org.springframework.http.MediaType
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Flux
import java.time.Duration
import java.time.Instant

private val log = LogManager.getLogger()

@Controller
class RSocketResource {
  @MessageMapping("hello")
  fun hello() = Flux.interval(Duration.ofSeconds(1))
      .map { "Hello $it at ${Instant.now()}" }
      .map { mapOf("result" to it) }
      .doOnNext(log::info)
}

@SpringBootApplication
class FullStackRSocketAppsApplication {
  @Bean fun routes() = router {
    resources("/", ClassPathResource("classpath:/static/"))
    "/".nest {
      "/api".nest {
        GET("/hello") {
          ok().bodyValue(mapOf("hello" to "world"))
        }
        path("/**") {
          val baseUrl = it.uri().run { "$scheme://$authority" }
          ok().bodyValue(mapOf(
              "hello _self" to it.uri(),
              "hello GET" to "$baseUrl/api/hello"
          ))
        }
      }
      path("/") {
        ok().contentType(MediaType.TEXT_HTML)
            .render("index", mapOf("message" to "Hello, Bebe!"))
      }
    }
  }
}

fun main(args: Array<String>) {
  runApplication<FullStackRSocketAppsApplication>(*args)
}
