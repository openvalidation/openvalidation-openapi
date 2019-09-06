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

import static org.hamcrest.core.Is.*;

import io.openvalidation.openapi.*;
import org.junit.jupiter.api.Test;

public class OApi2OVBridgeTest {

  @Test
  public void load_schema_from_string() throws Exception {

    //        OpenValidationResult result = new OApi2OVBridge().withSpecFromResource("sample.yaml")
    //                                                           .generateRulesForOperations();
    //
    //        assertThat(result, is(notNullValue()));
    //        assertThat(result.hasErrors(), is(false));
    //
    //        assertThat(result.getResults(), is(notNullValue()));
    //        assertThat(result.getResults().size(), is(4));
    //
    //        CodeGenerationResult firstResult = result.getResults().get(0);
    //        assertThat(firstResult, is(notNullValue()));
    //        assertThat(firstResult.getCode(), containsString("was für ein Quatsch!"));
    //
    //        CodeGenerationResult freameworkResult = result.getFrameworkResult();
    //        assertThat(freameworkResult, is(notNullValue()));
    //        assertThat(freameworkResult.getCodeKind(), is(CodeKind.Framework));
    //        assertThat(freameworkResult.getName(), containsString("HUMLFramework"));
  }

  @Test
  public void class_names_should_be_names_from_oapispec() throws Exception {

    //        OpenValidationResult result = new OApi2OVBridge().withSpecFromResource("sample.yaml")
    //                .generateRulesForOperations();
    //
    //        assertThat(result, is(notNullValue()));
    //        assertThat(result.getResults().get(0).getCode(), containsString("public class
    // Simple"));
  }
}
