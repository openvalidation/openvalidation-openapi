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

import io.openvalidation.common.model.CodeGenerationResult;
import io.openvalidation.common.model.Language;
import io.openvalidation.common.model.OpenValidationResult;
import io.openvalidation.common.model.RuleValidatorInfo;
import io.openvalidation.common.utils.LINQ;
import io.openvalidation.openapi.model.OApiOperation;
import io.openvalidation.openapi.model.OApiRuleContainer;
import io.openvalidation.openapi.utils.OApiCodegenTemplateUtils;
import io.openvalidation.openapi.utils.OApiModelUtils;
import io.openvalidation.openapi.utils.OApiParserUtils;
import io.swagger.v3.oas.models.OpenAPI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.openapitools.codegen.DefaultCodegen;
import org.slf4j.Logger;

public class OApiValidationGenerator {

  private Logger LOGGER = null;
  protected String validationPackage;
  protected String validationFolder;
  protected Language language;
  protected List<OApiRuleContainer> operationRules;
  protected List<CodeGenerationResult> ovValidationRuleCodeResults =
      ovValidationRuleCodeResults = new ArrayList<>();
  private List<OpenValidationResult> _results = new ArrayList<>();

  protected String basePackage;
  protected String sourceFolder;
  protected String modelPackage;

  protected DefaultCodegen _oapiCodegen;

  public OApiValidationGenerator(DefaultCodegen oapiCodegen, Logger logger) {
    this.language = Language.Java;
    this._oapiCodegen = oapiCodegen;
    this.LOGGER = logger;
  }

  // generators

  public OApiValidationGenerator generateOVFramework() {
    if (validationPackage == null) validationPackage = basePackage + ".validation";
    if (validationFolder == null)
      validationFolder =
          sourceFolder.replace('\\', '/') + "/" + validationPackage.replace('.', '/');

    CodeGenerationResult frameworkCode =
        new OApi2OVBridge()
            .withLanguage(language)
            .withPackage(validationPackage)
            .withModelPackage(modelPackage)
            .generateRulesFramework();

    OApiCodegenTemplateUtils.addOVFrameworkTemplate(
        this._oapiCodegen, frameworkCode.getCode(), validationFolder, language);
    OApiCodegenTemplateUtils.addOVRulesImplementationTemplate(
        ovValidationRuleCodeResults, this._oapiCodegen, language, validationFolder);

    return this;
  }

  public void generateOVRulesImplementation(OpenAPI globalApi, Map<String, Object> api) {
    LOGGER.info("start generation of validation rules for operation: ''.");

    this.initOperationRules(globalApi);
    List<OApiOperation> operationInfo = OApiModelUtils.getOperationInfo(api);
    List<OApiRuleContainer> rulesContainers =
        OApiModelUtils.mergeOperationRulesWithOperationInfo(operationInfo, this.operationRules);

    if (rulesContainers != null && rulesContainers.size() > 0) {
      OApi2OVBridge ovBridge = new OApi2OVBridge();
      OpenValidationResult result =
          ovBridge
              .withLanguage(language)
              .withPackage(validationPackage)
              .withModelPackage(modelPackage)
              .generateRulesForOperations(rulesContainers);

      ovValidationRuleCodeResults.addAll(result.getImplementationResults());
      _results.add(result);
    }
  }

  public void generateOVFactoryAtFinalIteration() {
    if (!this.hasUnprocessedRules()) {
      this.generateOVFactory();

      if (LINQ.any(_results, r -> r.hasErrors())) {
        LOGGER.error("generation of validation rules failed.");
        throw new RuntimeException("generation of validation rules failed.");
      } else {
        LOGGER.info("generation of validation rules successfull done.");
      }
    }
  }

  public void generateOVFactory() {
    List<RuleValidatorInfo> validatorInfos =
        OApiModelUtils.convertToRuleValidatorInfo(this.operationRules, "OVRules");

    OApi2OVBridge ovWrapper = new OApi2OVBridge();
    CodeGenerationResult result =
        ovWrapper
            .withLanguage(language)
            .withPackage(validationPackage)
            .withModelPackage(modelPackage)
            .generateValidatorFactory(validatorInfos);

    OApiCodegenTemplateUtils.addOVFactoryTemplate(
        _oapiCodegen, result.getCode(), validationFolder, language);
  }

  public void generateOVReadmeFile() {
    generateOVReadmeFile("");
  }

  public void generateOVReadmeFile(String content) {
    OApiCodegenTemplateUtils.addOVReadmeTemplate(_oapiCodegen, content, validationFolder);
  }

  public void generateOVSingleFile(OpenAPI globalApi, Map<String, Object> api) {
    this.initOperationRules(globalApi);

    List<OApiOperation> operationInfo = OApiModelUtils.getOperationInfo(api);
    List<OApiRuleContainer> rulesContainers =
        OApiModelUtils.mergeOperationRulesWithOperationInfo(operationInfo, this.operationRules);

    if (rulesContainers != null && rulesContainers.size() > 0) {
      OApi2OVBridge ovWrapper = new OApi2OVBridge();
      OpenValidationResult result =
          ovWrapper
              .withLanguage(language)
              .withPackage(validationPackage)
              .withModelPackage(modelPackage)
              .generateAndAppendSingleFileOutput(rulesContainers);

      OApiCodegenTemplateUtils.addOVSingleFileTemplate(
          _oapiCodegen, result.getAllCodeContent(), validationFolder, language);

      // Console.print("### RESULT : " + result.toString());
    }
  }

  // end generators

  public OApiValidationGenerator withBasePackage(String packageName) {
    this.basePackage = packageName;

    return this;
  }

  public OApiValidationGenerator withSourceFolder(String folder) {
    this.sourceFolder = folder;
    return this;
  }

  public OApiValidationGenerator withValidationPackage(String packageName) {
    this.validationPackage = packageName;

    return this;
  }

  public OApiValidationGenerator withValidationFolder(String folder) {
    this.validationFolder = folder;

    return this;
  }

  public OApiValidationGenerator withModelPackage(String packageName) {
    this.modelPackage = packageName;
    return this;
  }

  public OApiValidationGenerator withLanguage(Language language) {
    this.language = language;
    return this;
  }

  public void initOperationRules(OpenAPI globalOpenAPI) {
    if (this.operationRules == null)
      this.operationRules = OApiParserUtils.getOperationRules(globalOpenAPI);
  }

  public String getValidationFolder() {
    return this.validationFolder;
  }

  public Language getLanguage() {
    return this.language;
  }

  public boolean hasUnprocessedRules() {
    if (this.operationRules == null || this.operationRules.size() < 1) return false;
    return this.operationRules.stream().anyMatch(o -> o.isProcessed() == false);
  }

  public static OApiValidationGenerator createDefault(
      DefaultCodegen oapiCodegen, Logger logger, Consumer<OApiValidationGenerator> function) {
    OApiValidationGenerator generator = new OApiValidationGenerator(oapiCodegen, logger);

    try {
      function.accept(generator);

      return generator;
    } catch (Exception exp) {
      exp.toString();

      throw exp;
    }
  }
}
