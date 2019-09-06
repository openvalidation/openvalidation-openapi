/*
 * Copyright 2019 BROCKHAUS AG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.openvalidation.openapi.parser;

import io.openvalidation.openapi.model.OApiRule;
import io.openvalidation.openapi.model.OApiRuleContainer;
import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.parser.core.models.ParseOptions;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OApiSpecParserSimple {
  private OpenAPI _oapi;
  ParseOptions po = new ParseOptions();

  public OApiSpecParserSimple(String url) {
    po.setResolve(true);
    po.setResolveFully(true);

    this._oapi = new OpenAPIParser().readLocation(url, null, po).getOpenAPI();
  }

  public List<PathItem> getPaths() {
    List<PathItem> items = new ArrayList<>();

    for (String key : _oapi.getPaths().keySet()) {
      items.add(_oapi.getPaths().get(key));
    }

    return items;
  }

  public List<Operation> getOperations() {
    List<Operation> operations = new ArrayList<>();

    for (PathItem path : this.getPaths()) {
      operations.addAll(path.readOperations());
    }

    return operations;
  }

  public List<RequestBody> getRequestBodies() {
    List<RequestBody> requestBodies = new ArrayList<>();

    for (Operation operation : this.getOperations()) {
      requestBodies.add(operation.getRequestBody());
    }

    return requestBodies;
  }

  public List<MediaType> getBodyMediaTypes() {

    List<MediaType> mediaTypes = new ArrayList<>();

    for (RequestBody body : this.getRequestBodies()) {

      for (String mk : body.getContent().keySet()) {
        mediaTypes.add(body.getContent().get(mk));
      }
    }

    return mediaTypes;
  }

  public List<OApiRuleContainer> getRuleContainers() {
    List<OApiRuleContainer> xOVRules = new ArrayList<>();

    for (MediaType mtype : this.getBodyMediaTypes()) {
      Map<String, Object> extensions = mtype.getExtensions();

      if (extensions != null && extensions.size() > 0) {

        if (extensions.containsKey("x-ov-rules")) {
          Map<String, String> xovrules = (Map<String, String>) extensions.get("x-ov-rules");

          if (xovrules != null && xovrules.size() > 0) {

            // new OpenAPIParser().readLocation(url, null, new ParseOptions())

            OApiRuleContainer ruleContainer = new OApiRuleContainer();
            // ruleContainer.setName(xovrules.get("name"));
            ruleContainer.setRule(new OApiRule(xovrules.get("rule")));

            xOVRules.add(ruleContainer);
          }
        }
      }
    }

    return xOVRules;
  }
}
