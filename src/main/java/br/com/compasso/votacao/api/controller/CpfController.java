package br.com.compasso.votacao.api.controller;

import br.com.compasso.votacao.api.adapter.CpfResponse;
import br.com.compasso.votacao.api.utils.CPFUtils;
import javax.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/cpfs",
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
public class CpfController {
  
  @GetMapping("/random")
  public CpfResponse generate() {
    log.debug("Receiving request to generate cpf number ...");
    final String cpfNumber = CPFUtils.createNumberCpf();
    log.info("Cpf number [{}] generated successfully", cpfNumber);
    return new CpfResponse(cpfNumber, Boolean.TRUE);
  }
  
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
