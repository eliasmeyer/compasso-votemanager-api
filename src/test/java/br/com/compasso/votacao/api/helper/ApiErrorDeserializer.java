package br.com.compasso.votacao.api.helper;

import br.com.compasso.votacao.api.error.ApiError;
import br.com.compasso.votacao.api.error.ApiSubError;
import br.com.compasso.votacao.api.error.ApiValidationError;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.springframework.http.HttpStatus;

public class ApiErrorDeserializer extends StdDeserializer<ApiError> {
  
  public ApiErrorDeserializer() {
    this(null);
  }
  
  public ApiErrorDeserializer(Class<?> vc) {
    super(vc);
  }
  
  @Override
  public ApiError deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
      throws IOException {
    
    JsonNode errorNode = jsonParser.getCodec().readTree(jsonParser);
    ApiError apiError = new ApiError(HttpStatus.NOT_FOUND);
    apiError.setHttpStatus(HttpStatus.valueOf(errorNode.get("httpStatus").textValue()));
    apiError.setTimestamp(LocalDateTime.now());
    apiError.setPath(errorNode.get("path").textValue());
    apiError.setMessage(errorNode.get("message").textValue());
    apiError.setDebugMessage(errorNode.get("debugMessage").textValue());
    apiError.setPath(errorNode.get("path").textValue());
    
    List<ApiSubError> apiValidationErrorList = new ArrayList<>();
    ArrayNode subErrorsNode = (ArrayNode) errorNode.get("subErrors");
    Iterator<JsonNode> modelArrayIterator = subErrorsNode.iterator();
    while (modelArrayIterator.hasNext()) {
      JsonNode elementNode = modelArrayIterator.next();
      ApiValidationError apiValidationError = new ApiValidationError();
      apiValidationError.setObject(elementNode.get("object").textValue());
      apiValidationError.setField(elementNode.get("field").textValue());
      apiValidationError.setRejectedValue(elementNode.get("rejectedValue").textValue());
      apiValidationError.setMessage(elementNode.get("message").textValue());
      apiValidationErrorList.add(apiValidationError);
    }
    
    apiError.setSubErrors(apiValidationErrorList);
    return apiError;
  }
}
