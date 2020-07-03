package br.com.compasso.votacao.api.controller;

import br.com.compasso.votacao.api.adapter.CpfResponse;
import br.com.compasso.votacao.api.utils.CPFUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "CPF Generator", description = "Geração e validação do número de CPF")
@Slf4j
@RestController
@RequestMapping(value = "/cpfs", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
public class CpfController {
  
  @Operation(summary = "Gera CPF válido ", description = "Gera um número de CPF aleatório e válido")
  @ApiResponse(description = "Número do CPF e status de validação",
      content = @Content(mediaType = "application/json",
          schema = @Schema(implementation = CpfResponse.class)), responseCode = "200")
  @GetMapping("/random")
  public CpfResponse generate() {
    log.debug("Receiving request to generate cpf number ...");
    final String cpfNumber = CPFUtils.createNumberCpf();
    log.info("Cpf number [{}] generated successfully", cpfNumber);
    return new CpfResponse(cpfNumber, Boolean.TRUE);
  }
  
  @Operation(summary = "Valida CPF", description = "Valida o número de CPF")
  @ApiResponse(description = "Número do CPF e status de validação", content = @Content(mediaType = "application/json",
      schema = @Schema(implementation = CpfResponse.class)), responseCode = "200")
  @GetMapping("/test/{cpf}")
  public CpfResponse test(
      @PathVariable("cpf")
      @Pattern(regexp = "[0-9]{11}", message = "Cpf number is wrong")
          String cpfNumber) {
    log.debug("Receiving request to cpf number [{}]", cpfNumber);
    final Boolean isValid = CPFUtils.isValid(cpfNumber);
    log.info("Cpf number [{}] is valid: [{}]", cpfNumber, isValid);
    return new CpfResponse(cpfNumber, isValid);
  }
}
