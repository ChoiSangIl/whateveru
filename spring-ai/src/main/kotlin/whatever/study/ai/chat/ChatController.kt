package whatever.study.ai.chat

import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.io.IOException

data class ChatRequest(val message: String)
data class ChatResponse(val answer: String)

@RestController
@RequestMapping("/api/chat")
class ChatController(
    private val chatModel: ChatModel
) {
    @PostMapping
    fun chat(@RequestBody req: ChatRequest): ChatResponse {
        val answer = chatModel.call(req.message)
        return ChatResponse(answer)
    }

    @PostMapping(
        "/stream",
        produces = [MediaType.TEXT_EVENT_STREAM_VALUE],
        consumes = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun stream(@RequestBody req: ChatRequest): SseEmitter {
        val emitter = SseEmitter(0L)

        val prompt = Prompt(listOf(UserMessage(req.message)))

        val disposable = chatModel.stream(prompt)
            .mapNotNull { it.result.output.text }
            .filter { it?.isNotBlank() == true }
            .subscribe(
                { chunk ->
                    try {
                        chunk?.let {
                            emitter.send(
                                SseEmitter.event()
                                    .name("token")
                                    .data(chunk)
                            )
                        }
                    } catch (e: IOException) {
                        emitter.completeWithError(e)
                    }
                },
                { err ->
                    try {
                        emitter.send(
                            SseEmitter.event()
                                .name("error")
                                .data(err.message ?: "unknown error")
                        )
                    } catch (_: Exception) {
                        // ignore
                    } finally {
                        emitter.completeWithError(err)
                    }
                },
                {
                    try {
                        emitter.send(
                            SseEmitter.event()
                                .name("done")
                                .data("[DONE]")
                        )
                    } catch (_: Exception) {
                        // ignore
                    } finally {
                        emitter.complete()
                    }
                }
            )

        emitter.onCompletion { disposable.dispose() }
        emitter.onTimeout {
            disposable.dispose()
            emitter.complete()
        }
        emitter.onError {
            disposable.dispose()
        }

        return emitter
    }
}
