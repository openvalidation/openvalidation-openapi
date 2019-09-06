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

import io.openvalidation.common.model.CodeGenerationResult;
import io.openvalidation.common.model.Language;
import io.openvalidation.common.utils.LanguageUtils;
import io.openvalidation.common.utils.StringUtils;
import java.util.*;
import org.openapitools.codegen.DefaultCodegen;
import org.openapitools.codegen.SupportingFile;

public class OApiCodegenTemplateUtils {

  public static void addOVFrameworkTemplate(
      DefaultCodegen codegen, String code, String validationFolder, Language language) {
    codegen.additionalProperties().put("framework_code", code);
    codegen
        .supportingFiles()
        .add(
            new SupportingFile(
                "ovFramework.mustache",
                validationFolder,
                "OVFramework." + LanguageUtils.getExtensionFor(language)));
  }

  public static void addOVAdapterTemplate(
      DefaultCodegen codegen,
      String validationFolder,
      Language language,
      String customAdapterClassName) {

    codegen
        .supportingFiles()
        .add(
            new SupportingFile(
                "ovValidatorAdapter.mustache",
                validationFolder,
                (StringUtils.isNullOrEmpty(customAdapterClassName)
                        ? "OVValdatorAdapter"
                        : customAdapterClassName)
                    + "."
                    + LanguageUtils.getExtensionFor(language)));
  }

  public static void addOVFactoryTemplate(
      DefaultCodegen codegen, String code, String validationFolder, Language language) {
    // with factory
    codegen.additionalProperties().put("factory_code", code);
    codegen
        .supportingFiles()
        .add(
            new SupportingFile(
                "ovFactory.mustache",
                validationFolder,
                "OpenValidatorFactory." + LanguageUtils.getExtensionFor(language)));
  }

  public static void addOVReadmeTemplate(
      DefaultCodegen codegen, String content, String validationFolder) {
    // with factory
    codegen.additionalProperties().put("content", content);
    codegen.supportingFiles().add(new SupportingFile("readme.md", validationFolder, "readme.md"));
  }

  public static void addOVSingleFileTemplate(
      DefaultCodegen codegen, String code, String validationFolder, Language language) {
    // with factory
    codegen.additionalProperties().put("single_file_code", code);
    codegen
        .supportingFiles()
        .add(
            new SupportingFile(
                "ovRules.mustache",
                validationFolder,
                "OVRules." + LanguageUtils.getExtensionFor(language)));
  }

  public static void addOVRulesImplementationTemplate(
      List<CodeGenerationResult> results,
      DefaultCodegen codegen,
      Language language,
      String validationFolder) {

    String extension = LanguageUtils.getExtensionFor(language);

    codegen.additionalProperties().put("code_results", results);
    codegen
        .supportingFiles()
        .add(
            new SupportingFile(
                "ovValidationRules.mustache", validationFolder, "OVRules." + extension));
  }
}
