package osahner.controller

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.codec.ServerSentEvent
import org.springframework.web.bind.annotation.*
import osahner.dto.CallDto
import osahner.service.CallService
import reactor.core.publisher.Flux

@RestController
@RequestMapping("/calls")
class CallController (
        private val callService: CallService
) {

    @PostMapping("/new")
    @ResponseStatus(HttpStatus.OK)
    fun newCall(@RequestBody call: CallDto) {
        this.callService.publisherCall(call);
    }

    @GetMapping(value = ["/listener"], produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun listener(): Flux<ServerSentEvent<CallDto>> {
        return this.callService.listener();
    }
}