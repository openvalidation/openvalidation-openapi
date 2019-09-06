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

package io.openvalidation.openapi.utils;

import io.openvalidation.common.utils.ResourceUtils;
import io.openvalidation.openapi.model.OApiRuleContainer;
import io.openvalidation.openapi.parser.OApiSpecParserKaizen;
import io.swagger.v3.oas.models.OpenAPI;
import java.util.List;
import org.openapitools.codegen.serializer.SerializerUtils;

public class OApiParserUtils {
  public static List<OApiRuleContainer> getOperationRules(OpenAPI openApi) {
    String spec = SerializerUtils.toYamlString(openApi);
    return getOperationRules(spec);
  }

  public static List<OApiRuleContainer> getOperationRules(String plainApiSpec) {
    try {
      OApiSpecParserKaizen parser = new OApiSpecParserKaizen();
      return parser.parse(plainApiSpec).getRuleContainers();
    } catch (Exception exp) {
      throw new RuntimeException(exp);
    }
  }

  public List<OApiRuleContainer> getOperationRulesByResourceSpec(String name) {
    String spec = null;

    try {
      spec = ResourceUtils.getResourceConent(name);
    } catch (Exception exp) {
      throw new RuntimeException(exp);
    }

    return getOperationRules(spec);
  }
}
