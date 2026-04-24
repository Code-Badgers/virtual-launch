package codebadger.virtual_launch.domain.persona.presentation;

import codebadger.virtual_launch.common.api.SuccessResponse;
import codebadger.virtual_launch.domain.persona.application.PersonaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/personas")
public class PersonaController {

    private final PersonaService personaService;

    @PostMapping
    public ResponseEntity<SuccessResponse<Long>> createPersona(@Valid @RequestBody PersonaCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(SuccessResponse.ok(personaService.createPersona(request)));
    }

}
