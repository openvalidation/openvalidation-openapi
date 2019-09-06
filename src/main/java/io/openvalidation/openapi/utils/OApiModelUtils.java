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

import io.openvalidation.common.model.RuleValidatorInfo;
import io.openvalidation.openapi.model.OApiOperation;
import io.openvalidation.openapi.model.OApiRuleContainer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.openapitools.codegen.CodegenModel;
import org.openapitools.codegen.CodegenOperation;

public class OApiModelUtils {

  public static List<OApiOperation> getOperationInfo(Map<String, Object> objs) {
    List<OApiOperation> out = new ArrayList<>();

    if (objs != null) {
      Map<String, Object> operations = (Map<String, Object>) objs.get("operations");
      if (operations != null) {
        List<CodegenOperation> ops = (List<CodegenOperation>) operations.get("operation");
        for (CodegenOperation operation : ops) {
          if (operation.getHasBodyParam()) {

            OApiOperation modelInfo = new OApiOperation();

            modelInfo.setOperationID(operation.operationId);
            modelInfo.setPath(operation.path);
            modelInfo.setMethod(operation.httpMethod);
            modelInfo.setModelType(operation.bodyParam.dataType);

            out.add(modelInfo);
          }
        }
      }
    }

    return out;
  }

  public static List<CodegenModel> getModelsAsList(List<Object> allModels) {
    List<CodegenModel> out = new ArrayList<>();

    for (Object om : allModels) {
      HashMap hm = (HashMap) om;

      out.addAll(
          (List<CodegenModel>)
              hm.values().stream()
                  .filter(v -> v instanceof CodegenModel)
                  .map(v -> (CodegenModel) v)
                  .collect(Collectors.toList()));
    }

    return out;
  }

  public static List<OApiRuleContainer> mergeOperationRulesWithOperationInfo(
      List<OApiOperation> modelInfos, List<OApiRuleContainer> containers) {
    List<OApiRuleContainer> out = new ArrayList<>();

    if (modelInfos != null && modelInfos.size() > 0) {
      for (OApiOperation mi : modelInfos) {
        OApiRuleContainer cont = mergeOperationRuleWithOperationInfo(mi, containers);
        if (cont != null) out.add(cont);
      }
    }

    return out;
  }

  public static OApiRuleContainer mergeOperationRuleWithOperationInfo(
      OApiOperation modelInfo, List<OApiRuleContainer> containers) {
    if (modelInfo != null && containers != null && containers.size() > 0) {
      for (OApiRuleContainer container : containers) {

        if (modelInfo.getPath().equalsIgnoreCase(container.getPath())
            && modelInfo.getMethod().equalsIgnoreCase(container.getMethod())) {

          container.setModelName(modelInfo.getModelType());
          container.setOperationId(modelInfo.getOperationID());

          return container;
        }
      }
    }

    return null;
  }

  public static List<RuleValidatorInfo> convertToRuleValidatorInfo(
      List<OApiRuleContainer> ruleContainers, String pckg) {
    return ruleContainers.stream()
        .map(o -> new RuleValidatorInfo(o.getName(), o.getOperationId(), pckg))
        .collect(Collectors.toList());
  }
}
