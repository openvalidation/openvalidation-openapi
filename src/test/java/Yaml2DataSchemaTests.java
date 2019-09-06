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
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import com.reprezen.kaizen.oasparser.model3.Schema;
import io.openvalidation.common.data.DataSchema;
import io.openvalidation.common.utils.Console;
import io.openvalidation.core.OpenValidation;
import io.openvalidation.openapi.parser.OApiSpecParserKaizen;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

public class Yaml2DataSchemaTests {

  @Test
  public void resolve_bipro_claims_habitation_schema() throws Exception {
    DataSchema schema = convert("org.bipro.claims.habitation__ov.yaml", "/claims", "post");

    assertThat(schema, is(notNullValue()));
  }

  private DataSchema convert(String yamlFile, String path, String method) throws Exception {

    DataSchema schema = null;
    Schema kaizenSchema = null;
    JSONObject jsonSchema = null;
    String json = null;

    try {
      OApiSpecParserKaizen gen = new OApiSpecParserKaizen();

      kaizenSchema = gen.parseFromLocalResource(yamlFile).getSchemaOfRequest(path, method);

      assertThat(kaizenSchema, is(notNullValue()));

      jsonSchema = gen.resolveSchema(kaizenSchema);
      assertThat(jsonSchema, is(notNullValue()));

      Console.printl(jsonSchema.toString(4));

      OpenValidation ov = OpenValidation.createDefault();
      json = jsonSchema.toString();
      assertThat(json, is(notNullValue()));

      ov.setSchema(json);

      schema = ov.getSchema();
    } catch (Exception exp) {
      Console.error("ERROR while coverting yaml to json \n\n");

      if (jsonSchema != null) {
        Console.printl("JSON: \n");
        Console.printl(jsonSchema.toString(4));
      }

      Console.printError(exp);
    }

    return schema;
  }
}
