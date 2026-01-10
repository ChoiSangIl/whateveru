package whatever.study.ai.tool

import org.springaicommunity.mcp.annotation.McpTool
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class SimpleMcpTool(
    private val webClient: WebClient
) {

    @McpTool(description = "whateveru tistory(티스토리) blog rss(피드) 검색")
    fun getBlogRss(): String {
        return webClient.get()
            .uri("https://whateveru.tistory.com/rss")
            .accept(
                MediaType.APPLICATION_XML,
                MediaType.TEXT_XML,
                MediaType("application", "rss+xml"),
                MediaType.ALL
            )
            .retrieve()
            .toEntity(String::class.java)
            .block()
            .toMcpJson()
    }
}

fun ResponseEntity<String>?.toMcpJson(): String {
    if (this == null) {
        return """{"error":"no_response"}"""
    }

    if (!this.statusCode.is2xxSuccessful) {
        return """{"error":"http_error","status":${this.statusCode.value()}}"""
    }

    val body = this.body
    if (body.isNullOrBlank()) {
        return """{"error":"empty_body"}"""
    }

    return body
}

@Configuration
class WebClientConfiguration {

    @Bean
    fun webClient(builder: WebClient.Builder): WebClient {
        return builder
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .build()
    }
}