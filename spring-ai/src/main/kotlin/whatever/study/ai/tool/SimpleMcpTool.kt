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

    @McpTool(description = "크몽 서비스 1차 카테고리 탐색(검색), 루트 카테고리 기준으로 검색")
    fun getCategoryGroups(): String {
        return webClient.get()
            .uri("https://api.kmong.com/gig-app/category/v1/global-navigation-bar")
            .retrieve()
            .toEntity(String::class.java)
            .block()
            .toMcpJson()
    }

    @McpTool(description = "크몽 서비스 상세 카테고리 탐색 (categoryId 기준)")
    fun getCategoryDetails(categoryId: Long): String {
        return webClient.get()
            .uri(
                "https://api.kmong.com/gig-app/category/v1/categories/{categoryId}/side-navigation-bar",
                categoryId
            )
            .retrieve()
            .toEntity(String::class.java)
            .block()
            .toMcpJson()
    }

    @McpTool(description = "크몽 서비스 카테고리 주소 링크")
    fun getKmongCategoryLink(categoryId: Long): String {
        return "https://kmong.com/category/${categoryId}"
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