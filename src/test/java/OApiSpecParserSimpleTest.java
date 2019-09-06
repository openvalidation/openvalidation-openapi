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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import io.openvalidation.openapi.model.OApiRuleContainer;
import io.openvalidation.openapi.parser.OApiSpecParserSimple;
import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.parameters.RequestBody;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class OApiSpecParserSimpleTest {
  private static String TEST_SPEC_FILE = "sample.yaml";

  @Test
  public void load_operations_from_schema() throws Exception {
    OApiSpecParserSimple gen = new OApiSpecParserSimple(TEST_SPEC_FILE);
    List<Operation> operations = gen.getOperations();

    assertThat(operations, is(notNullValue()));
    assertThat(operations.size(), is(7));
  }

  @Test
  public void load_requestbodies_from_schema() throws Exception {
    OApiSpecParserSimple gen = new OApiSpecParserSimple(TEST_SPEC_FILE);
    List<RequestBody> bodies = gen.getRequestBodies();

    assertThat(bodies, is(notNullValue()));
    assertThat(bodies.size(), is(7));
  }

  @Test
  public void load_bodymediatypes_from_schema() throws Exception {
    OApiSpecParserSimple gen = new OApiSpecParserSimple(TEST_SPEC_FILE);
    List<MediaType> mediaTypes = gen.getBodyMediaTypes();

    assertThat(mediaTypes, is(notNullValue()));
    assertThat(mediaTypes.size(), is(7));
  }

  @Test
  @Disabled
  public void load_xovrules_from_schema() throws Exception {
    OApiSpecParserSimple gen = new OApiSpecParserSimple(TEST_SPEC_FILE);
    List<OApiRuleContainer> ruleContainers = gen.getRuleContainers();

    assertThat(ruleContainers, is(notNullValue()));
    assertThat(ruleContainers.size(), is(2));

    assertThat(ruleContainers.get(0).getName(), is("Simple"));
    assertThat(ruleContainers.get(0).getRuleAsStirng(), containsString("was für ein Quatsch!"));

    assertThat(ruleContainers.get(1).getName(), is("OneOfValidation"));
  }
}
