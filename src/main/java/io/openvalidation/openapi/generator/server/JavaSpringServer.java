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

package io.openvalidation.openapi.generator.server;

import ch.qos.logback.classic.Level;
import io.openvalidation.common.model.Language;
import io.openvalidation.openapi.*;
import io.openvalidation.openapi.OApiValidationGenerator;
import io.openvalidation.openapi.utils.OApiCodegenTemplateUtils;
import java.util.List;
import java.util.Map;
import org.openapitools.codegen.CliOption;
import org.openapitools.codegen.CodegenConfig;
import org.openapitools.codegen.languages.SpringCodegen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavaSpringServer extends SpringCodegen implements CodegenConfig {
  private static final Logger LOGGER = LoggerFactory.getLogger("openVALIDATION");
  protected OApiValidationGenerator ovCodegen;

  @Override
  public String getName() {
    return "ov-java-spring-server";
  }

  @Override
  public String getHelp() {
    return "Generates a java spring REST Service library including validation rules generation. powered by openVALIDATION.";
  }

  public JavaSpringServer() {
    super();

    Logger rootLogger = LoggerFactory.getLogger("openVALIDATION");
    if (rootLogger instanceof ch.qos.logback.classic.Logger) {
      // the logging level can be changed at runtime
      ((ch.qos.logback.classic.Logger) rootLogger).setLevel(Level.INFO);
    }

    //        Stream.of("openVALIDATION")
    //                .map(lc::getLogger)
    //                .peek(logger -> logger.detachAppender("STDOUT"))
    //                .reduce((logger, next) -> logger.getName().equals("openVALIDATION") ? logger :
    // next)
    //                .map(root -> root.getAppender("STDERR"))
    //                .ifPresent(FilterAttachable::clearAllFilters);

    LOGGER.info("start creating validation rules...");

    setDelegatePattern(true);
    setUseBeanValidation(true);
    setPerformBeanValidation(true);

    setReturnSuccessCode(true);
    this.cliOptions.add(
        CliOption.newBoolean(
            "returnSuccessCode", "Generated server returns 2xx code", this.returnSuccessCode));
    this.additionalProperties.put("returnSuccessCode", this.returnSuccessCode);
  }

  @Override
  public Map<String, Object> postProcessOperationsWithModels(
      Map<String, Object> api, List<Object> allModels) {
    if (ovCodegen == null) {
      ovCodegen =
          OApiValidationGenerator.createDefault(
              this,
              LOGGER,
              g -> {
                g.withSourceFolder(sourceFolder)
                    .withBasePackage(basePackage)
                    .withModelPackage(modelPackage)
                    .withLanguage(Language.Java)
                    .generateOVFramework();

                OApiCodegenTemplateUtils.addOVAdapterTemplate(
                    this, g.getValidationFolder(), g.getLanguage(), "OVSpringValdator");
              });
    }

    super.postProcessOperationsWithModels(api, allModels);
    ovCodegen.generateOVRulesImplementation(globalOpenAPI, api);
    ovCodegen.generateOVFactoryAtFinalIteration();

    return api;
  }
}
