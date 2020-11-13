package osahner.service

import org.springframework.http.codec.ServerSentEvent
import org.springframework.stereotype.Component
import osahner.dto.CallDto
import reactor.core.publisher.DirectProcessor
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxProcessor
import reactor.core.publisher.FluxSink

@Component
class CallService(
    private val medicoService: MedicoService
) {

    private val processor: FluxProcessor<CallDto, CallDto> = DirectProcessor.create<CallDto>().serialize();
    private val sink: FluxSink<CallDto> = processor.sink();

    fun publisherCall(call: CallDto) {
        this.sink.next(call);
    }

    fun listener(): Flux<ServerSentEvent<CallDto>> {
        return this.processor.map { e ->
            val medico = this.medicoService.findByRamal(e.extensionPhone);

            if (medico.isPresent()) {
                e.url = medico.get().url
                e.medico = medico.map { it.toCustomDTO() }.get()
            }

            ServerSentEvent.builder(e).build()
        };
    }
}