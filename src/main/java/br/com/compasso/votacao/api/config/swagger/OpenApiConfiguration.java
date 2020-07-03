package br.com.compasso.votacao.api.config.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

@OpenAPIDefinition(
    info = @Info(
        title = "API Gerenciador de Sessão de Votação",
        description = "API para gerenciamento de votação de pautas para tomadas de decisão "
            + "em assembleias."
        ,
        contact = @Contact(name = "Elias Meyer", email = "eliasmeyer@gmail.com"),
        license = @License(name = "Apache Licence 2.0", url = " http://www.apache.org/licenses/LICENSE-2.0.txt"),
        version = "1.0.0-beta"
    )
)

public class OpenApiConfiguration {
    
}
