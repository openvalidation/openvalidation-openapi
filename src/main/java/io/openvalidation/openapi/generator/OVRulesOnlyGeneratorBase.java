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

package io.openvalidation.openapi.generator;

import ch.qos.logback.classic.Level;
import io.openvalidation.common.model.Language;
import io.openvalidation.openapi.OApiValidationGenerator;
import java.util.List;
import java.util.Map;
import org.openapitools.codegen.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class OVRulesOnlyGeneratorBase extends DefaultCodegen implements CodegenConfig {

  protected static final Logger LOGGER = LoggerFactory.getLogger("openVALIDATION");
  private OApiValidationGenerator ovCodegen;

  @Override
  public CodegenType getTag() {
    return CodegenType.OTHER;
  }

  protected OVRulesOnlyGeneratorBase() {
    super();

    Logger rootLogger = LoggerFactory.getLogger("openVALIDATION");
    if (rootLogger instanceof ch.qos.logback.classic.Logger) {
      // the logging level can be changed at runtime
      ((ch.qos.logback.classic.Logger) rootLogger).setLevel(Level.INFO);
    }

    importMapping.clear();
    supportingFiles.clear();

    cliOptions.add(
        new CliOption(CodegenConstants.INVOKER_PACKAGE, CodegenConstants.INVOKER_PACKAGE_DESC));
    cliOptions.add(
        new CliOption(CodegenConstants.MODEL_PACKAGE, CodegenConstants.MODEL_PACKAGE_DESC));

    embeddedTemplateDir = templateDir = "openvalidation-rules";
  }

  @Override
  public Map<String, Object> postProcessOperationsWithModels(
      Map<String, Object> api, List<Object> allModels) {
    super.postProcessOperationsWithModels(api, allModels);

    if (ovCodegen == null) {
      ovCodegen =
          new OApiValidationGenerator(this, LOGGER)
              .withSourceFolder("")
              .withValidationFolder("")
              .withValidationPackage(getInvokerPackage())
              .withBasePackage(getInvokerPackage())
              .withModelPackage(getModelPackage()) // modelPackage
              .withLanguage(getLanguage())
              .generateOVFramework();

      setIgnoreFilePathOverride(null);
    }

    ovCodegen.generateOVRulesImplementation(globalOpenAPI, api);
    ovCodegen.generateOVFactoryAtFinalIteration();
    ovCodegen.generateOVReadmeFile();

    return api;
  }

  private String getInvokerPackage() {
    return (additionalProperties.get(CodegenConstants.INVOKER_PACKAGE) != null)
        ? additionalProperties.get(CodegenConstants.INVOKER_PACKAGE).toString()
        : "";
  }

  private String getModelPackage() {
    return (additionalProperties.get(CodegenConstants.MODEL_PACKAGE) != null)
        ? additionalProperties.get(CodegenConstants.MODEL_PACKAGE).toString()
        : "";
  }

  @Override
  public String getIgnoreFilePathOverride() {
    return null;
  }

  @Override
  public String getName() {
    return "ov-" + getLanguageString() + "-rules";
  }

  @Override
  public String getHelp() {
    return "Generates a "
        + getLanguageString()
        + " classes including validation rules. powered by openVALIDATION.";
  }

  protected abstract Language getLanguage();

  protected String getLanguageString() {
    return getLanguage().toString().toLowerCase();
  }
}
