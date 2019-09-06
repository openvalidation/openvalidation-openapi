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

import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.parser.core.models.ParseOptions;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.openapitools.codegen.OpenAPIGenerator;

public class RuleLoaderTest {

  @Test
  public void load_spec_from_resource() throws Exception {
    String spec = getResourceConent("sample.yaml");

    assertThat(spec, is(notNullValue()));
  }

  @Test
  public void load_spec_from_resource_using_oapi() throws Exception {
    OpenAPI openAPI =
        new OpenAPIParser().readLocation("sample.yaml", null, new ParseOptions()).getOpenAPI();

    assertThat(openAPI, is(notNullValue()));

    Paths paths = openAPI.getPaths();
    assertThat(paths, is(notNullValue()));
    assertThat(paths.size(), is(7));
  }

  @Test
  public void execute_generator() throws Exception {
    OpenAPIGenerator generator = new OpenAPIGenerator();

    assertThat(generator, is(notNullValue()));
  }

  public String getResourceConent(String resourceName) throws Exception {
    InputStream is = getCallerClass().getClassLoader().getResourceAsStream(resourceName);
    if (is != null) {
      BufferedReader reader = new BufferedReader(new InputStreamReader(is));
      return reader.lines().collect(Collectors.joining(System.lineSeparator()));
    }
    return null;
  }

  public Class getCallerClass() throws Exception {
    String callerClassName = Thread.currentThread().getStackTrace()[2].getClassName();
    return Class.forName(callerClassName);
  }
}
