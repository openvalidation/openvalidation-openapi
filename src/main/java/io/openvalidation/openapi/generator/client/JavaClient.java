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

package io.openvalidation.openapi.generator.client;

import io.openvalidation.common.model.Language;
import io.openvalidation.openapi.OApiValidationGenerator;
import java.util.List;
import java.util.Map;
import org.openapitools.codegen.*;
import org.openapitools.codegen.languages.JavaClientCodegen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// after generation, there is some trouble with javadoc, just disable errors while transform package
// mvn clean package -DfailOnError=false

public class JavaClient extends JavaClientCodegen implements CodegenConfig {
  private static final Logger LOGGER = LoggerFactory.getLogger(JavaScriptClient.class);
  protected OApiValidationGenerator ovCodegen;

  @Override
  public String getName() {
    return "ov-java-client";
  }

  @Override
  public String getHelp() {
    return "Generates a java client library including validation rules generation (>= java 1.8 ). powered by openVALIDATION.";
  }

  public JavaClient() {
    super();
    this.setJava8Mode(true);
    this.setDateLibrary("java8");
    this.additionalProperties.put("java8", "true");
    this.additionalProperties.put("threetenbp", false);
  }

  @Override
  public Map<String, Object> postProcessOperationsWithModels(
      Map<String, Object> api, List<Object> allModels) {

    if (ovCodegen == null) {
      ovCodegen =
          new OApiValidationGenerator(this, LOGGER)
              .withSourceFolder(sourceFolder)
              .withBasePackage(invokerPackage)
              .withModelPackage(modelPackage)
              .withLanguage(Language.Java)
              .generateOVFramework();
    }

    super.postProcessOperationsWithModels(api, allModels);

    ovCodegen.generateOVRulesImplementation(globalOpenAPI, api);
    ovCodegen.generateOVFactoryAtFinalIteration();

    return api;
  }
}
