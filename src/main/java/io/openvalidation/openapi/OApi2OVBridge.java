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

package io.openvalidation.openapi;

import io.openvalidation.common.ast.ASTModel;
import io.openvalidation.common.model.CodeGenerationResult;
import io.openvalidation.common.model.Language;
import io.openvalidation.common.model.OpenValidationResult;
import io.openvalidation.common.model.RuleValidatorInfo;
import io.openvalidation.common.utils.Constants;
import io.openvalidation.core.OpenValidation;
import io.openvalidation.core.OpenValidationOptions;
import io.openvalidation.openapi.model.OApiRuleContainer;
import io.openvalidation.openapi.utils.OApiModelUtils;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OApi2OVBridge {
  private static final Logger LOGGER = LoggerFactory.getLogger("openVALIDATION");

  private OpenValidationOptions _options;
  private OpenValidation _openValidation;
  private String _modelPackage;
  private String _basePackage;

  public OApi2OVBridge() {
    this._openValidation = OpenValidation.createDefault();
    this._options = new OpenValidationOptions();
    this._options.setVerbose(true);
    // this._options.setLocale("de");

    //        LoggerContext lc = new LoggerContext();
    //        lc.getLogger(OApi2OVBridge.class).setLevel(Level.INFO);

  }

  public OApi2OVBridge withLanguage(Language language) {
    this._options.setLanguage(language);
    return this;
  }

  public OApi2OVBridge withPackage(String pckg) {
    this._basePackage = pckg;
    this._options.setParam("generated_class_namespace", pckg);
    return this;
  }

  public OApi2OVBridge withModelPackage(String pckg) {
    this._modelPackage = pckg;
    return this;
  }

  public OpenValidationResult generateRulesForOperations(List<OApiRuleContainer> containers) {
    OpenValidationResult results = new OpenValidationResult();
    List<RuleValidatorInfo> validatorsInfo = new ArrayList<>();
    String operandId =
        (containers != null && containers.size() > 0)
            ? containers.get(containers.size() - 1).getOperationId()
            : "NOID";

    try {
      LOGGER.info("generate validation rules for operation '" + operandId + "'");

      if (containers != null) {

        for (OApiRuleContainer container : containers) {

          OpenValidationResult res = executeContainer(container);
          res.getResults()
              .forEach(
                  c -> {
                    c.setCode(
                        c.getCode()
                            .replaceAll("package " + this._basePackage + ";", "")
                            .replaceAll("HUMLFramework", "OVFramework")
                            .trim());
                    if (this._options.getLanguage() == Language.Java) {
                      c.setCode(
                          c.getCode().replaceAll("public class", "public static class").trim());
                    }
                  });
          results.addResults(res.getResults());
          results.addErrors(res.getErrors());

          results.setPlainRule(results.getPlainRule() + "\n\n" + res.getPlainRule());
          results.setPreprocessedRule(
              results.getPreprocessedRule() + "\n\n" + res.getPreprocessedRule());

          container.setProcessed(true);
        }
      }

      if (results.hasErrors()) {

        StringBuilder sb = new StringBuilder();
        sb.append(results.getErrorPrint(false));
        sb.append(results.getRuleSetPrint());
        sb.append(results.getASTModelPrint());

        System.out.print(Constants.KEYWORD_SYMBOL + "hallo");

        LOGGER.error(
            "ERRORS occured while generating validation rules for operation '"
                + operandId
                + "' :\n\n"
                + sb.toString());

        LOGGER.error("üäü \u2B9F  ++  ⮟");

        // LOGGER.error(results.getSummaryPrint());
        // LOGGER.error("\n", results.getErrorPrint(true));
        // throw new OpenValidationException(results.getErrorPrint(false));
      }
    } catch (Exception exp) {
      throw new RuntimeException(exp); // TODO: exception handling...
    }

    return results;
  }

  public OpenValidationResult executeContainer(OApiRuleContainer container) throws Exception {

    this._openValidation.setOptions(_options);

    // override options
    this._openValidation.setRule(container.getRuleAsStirng());
    this._openValidation.setSchema(container.getSchemaAsString());
    this._openValidation
        .getOptions()
        .setLocale(
            container
                .getOptions()
                .getLocale()); // get the locale from oapi container, 'de' should be default value

    if (this._modelPackage.isEmpty()) {
      this._openValidation.setParam("model_type", container.getModelName());
    } else {
      this._openValidation.setParam(
          "model_type", this._modelPackage + "." + container.getModelName());
    }
    this._openValidation.setParam("validatorID", container.getValidatorId());

    String name = container.getOperationId() + "validation";
    Character c = name.charAt(0);
    name = c.toString().toUpperCase() + name.substring(1);
    container.setName(name);

    this._openValidation.setParam("generated_class_name", name);

    return this._openValidation.generate(true);
  }

  public CodeGenerationResult generateRulesFramework() {
    OpenValidation ov = OpenValidation.createDefault();

    try {
      ov.setOptions(this._options);

      ASTModel ast = new ASTModel();
      ast.addParams(this._options.getParams());

      CodeGenerationResult result = ov.generateFramework(ast);
      result.setCode(result.getCode().replaceAll("HUMLFramework", "OVFramework"));

      return result;
    } catch (Exception exp) {
      throw new RuntimeException(exp);
    }
  }

  public CodeGenerationResult generateValidatorFactory(List<RuleValidatorInfo> validatorInfos) {
    OpenValidation ov = OpenValidation.createDefault();

    try {
      this._options.setParam("validators", validatorInfos);
      ov.setOptions(this._options);

      CodeGenerationResult result = ov.generateValidatorFactory(this._options.getParams());
      result.setCode(result.getCode().replaceAll("HUMLFramework", "OVFramework"));

      return result;
    } catch (Exception exp) {
      throw new RuntimeException(exp);
    }
  }

  public OpenValidationResult generateAndAppendSingleFileOutput(
      List<OApiRuleContainer> containers) {
    OpenValidationResult result = this.generateRulesForOperations(containers);
    result.addResult(this.generateRulesFramework());

    List<RuleValidatorInfo> validatorInfos =
        OApiModelUtils.convertToRuleValidatorInfo(containers, null);
    result.addResult(this.generateValidatorFactory(validatorInfos));

    return result;
  }
}
