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
import static org.hamcrest.Matchers.*;

import com.reprezen.kaizen.oasparser.model3.Schema;
import io.openvalidation.common.utils.ResourceUtils;
import io.openvalidation.openapi.model.OApiRule;
import io.openvalidation.openapi.model.OApiRuleContainer;
import io.openvalidation.openapi.parser.OApiSpecParserKaizen;
import java.util.List;
import java.util.stream.Collectors;
import org.json.JSONObject;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class OApiSpecParserKaizenTest {

  @Test
  public void load_schema_from_string() throws Exception {
    OApiSpecParserKaizen gen = new OApiSpecParserKaizen();

    String yaml = ResourceUtils.getResourceConent("sample.yaml");

    Schema sma = gen.parse(yaml).getSchemaOfResponse("/simple", "post", "200");

    assertThat(sma, is(notNullValue()));
    assertThat(sma.getProperty("code"), is(notNullValue()));
  }

  @Test
  public void get_schema_of_response() throws Exception {
    OApiSpecParserKaizen gen = new OApiSpecParserKaizen();

    Schema sma =
        gen.parseFromLocalResource("sample.yaml").getSchemaOfResponse("/simple", "post", "200");

    assertThat(sma, is(notNullValue()));
    assertThat(sma.getProperty("code"), is(notNullValue()));
    assertThat(sma.getProperty("code").getType(), is("number"));
  }

  @Test
  public void resolve_nested_schema_recursive() throws Exception {
    OApiSpecParserKaizen gen = new OApiSpecParserKaizen();

    Schema sma =
        gen.parseFromLocalResource("nestedschema.yaml").getSchemaOfRequest("/life/contract", "put");

    assertThat(sma, is(notNullValue()));

    JSONObject jsonSchema = gen.resolveSchema(sma);
    assertThat(jsonSchema, is(notNullValue()));
    JSONObject properties = jsonSchema.getJSONObject("properties");

    assertThat(properties, is(notNullValue()));
    assertThat(properties.keySet().size(), is(2));

    assertThat(properties.getJSONObject("product"), is(notNullValue()));
    assertThat(properties.getJSONObject("portfolio"), is(notNullValue()));
    assertThat(properties.getJSONObject("product").get("type").toString(), is("string"));
    assertThat(properties.getJSONObject("portfolio").get("type").toString(), is("object"));

    JSONObject portfolio = properties.getJSONObject("portfolio").getJSONObject("properties");

    assertThat(portfolio, is(notNullValue()));
    assertThat(portfolio.getJSONObject("name"), is(notNullValue()));

    JSONObject shares = portfolio.getJSONObject("shares");

    assertThat(shares, is(notNullValue()));
    assertThat(shares.get("type"), is("array"));
    assertThat(shares.has("items"), is(true));
    assertThat(shares.getJSONObject("items"), is(notNullValue()));
    assertThat(shares.getJSONObject("items").has("properties"), is(true));
    assertThat(shares.getJSONObject("items").has("type"), is(true));
    assertThat(shares.getJSONObject("items").get("type").toString(), is("object"));
    assertThat(
        shares.getJSONObject("items").getJSONObject("properties").has("percentage"), is(true));
    assertThat(
        shares
            .getJSONObject("items")
            .getJSONObject("properties")
            .getJSONObject("percentage")
            .has("type"),
        is(true));
    assertThat(
        shares
            .getJSONObject("items")
            .getJSONObject("properties")
            .getJSONObject("percentage")
            .get("type")
            .toString(),
        is("integer"));
  }

  @Test
  public void resolve_schema_rule_containers() throws Exception {
    OApiSpecParserKaizen gen = new OApiSpecParserKaizen();

    List<OApiRuleContainer> containers =
        gen.parseFromLocalResource("sample.yaml").getRuleContainers();

    assertThat(containers, is(notNullValue()));
    assertThat(containers.size(), is(3));
  }

  @Test
  public void resolve_schema_rule_ref_container() throws Exception {
    OApiSpecParserKaizen gen = new OApiSpecParserKaizen();

    List<OApiRuleContainer> containers =
        gen.parseFromLocalResource("sample.yaml").getRuleContainers();

    assertThat(containers, is(notNullValue()));
    assertThat(containers.size(), is(3));

    OApiRule rule = containers.get(1).getRule();
    assertThat(rule, is(notNullValue()));
    assertThat(rule.getPlainRule(), containsString("123"));
  }

  @Test
  public void resolve_schema_rule_name() throws Exception {
    OApiSpecParserKaizen gen = new OApiSpecParserKaizen();

    List<OApiRuleContainer> containers =
        gen.parseFromLocalResource("sample.yaml").getRuleContainers();

    assertThat(containers, is(notNullValue()));
    assertThat(containers.size(), is(3));

    OApiRuleContainer cnt = containers.get(0);
    assertThat(cnt, is(notNullValue()));
    assertThat(cnt.getPath(), is("/simple"));
    assertThat(cnt.getMethod(), is("POST"));

    OApiRuleContainer cnt1 = containers.get(1);
    assertThat(cnt1, is(notNullValue()));
    assertThat(cnt1.getPath(), is("/oneof"));
    assertThat(cnt1.getMethod(), is("POST"));

    OApiRuleContainer cnt2 = containers.get(2);
    assertThat(cnt2, is(notNullValue()));
    assertThat(cnt2.getPath(), is("/duplicates"));
    // assertThat(cnt2.getMethod(), is("POST"));
  }

  @Test
  public void should_contains_culture_param() throws Exception {
    OApiSpecParserKaizen gen = new OApiSpecParserKaizen();

    String yaml = ResourceUtils.getResourceConent("sample.yaml");

    List<OApiRuleContainer> containers = gen.parse(yaml).getRuleContainers();

    List<String> cultures =
        containers.stream()
            .map(c -> c.getOptions().getLocale().toString())
            .collect(Collectors.toList());

    assertThat(cultures, is(notNullValue()));
    assertThat(cultures, hasItem("en"));
  }

  // ****************************************************************
  //
  //                  ALL OF, ONE OF, ANY OF...
  //
  // ****************************************************************

  @Test
  @Disabled
  public void resolve_schema_allOf() throws Exception {
    OApiSpecParserKaizen gen = new OApiSpecParserKaizen();

    Schema sma = gen.parseFromLocalResource("sample.yaml").getSchemaOfRequest("/allof", "post");

    assertThat(sma, is(notNullValue()));

    JSONObject jsonSchema = gen.resolveSchema(sma);
    assertThat(jsonSchema, is(notNullValue()));
    JSONObject properties = jsonSchema.getJSONObject("properties");

    assertThat(properties, is(notNullValue()));
    assertThat(properties.keySet().size(), is(2));

    assertThat(properties.getJSONObject("name"), is(notNullValue()));
    assertThat(properties.getJSONObject("value"), is(notNullValue()));
    assertThat(properties.getJSONObject("name").get("type").toString(), is("string"));
    assertThat(properties.getJSONObject("value").get("type").toString(), is("integer"));
  }

  @Test
  @Disabled
  public void resolve_schema_allOf_from_response() throws Exception {
    OApiSpecParserKaizen gen = new OApiSpecParserKaizen();

    Schema sma =
        gen.parseFromLocalResource("sample.yaml").getSchemaOfResponse("/allof", "post", "200");

    assertThat(sma, is(notNullValue()));

    JSONObject jsonSchema = gen.resolveSchema(sma);
    assertThat(jsonSchema, is(notNullValue()));
    JSONObject properties = jsonSchema.getJSONObject("properties");

    assertThat(properties, is(notNullValue()));
    assertThat(properties.keySet().size(), is(2));

    assertThat(properties.getJSONObject("name"), is(notNullValue()));
    assertThat(properties.getJSONObject("value"), is(notNullValue()));
    assertThat(properties.getJSONObject("name").get("type").toString(), is("string"));
    assertThat(properties.getJSONObject("value").get("type").toString(), is("integer"));
  }

  @Test
  @Disabled
  public void resolve_schema_duplicates() throws Exception {
    OApiSpecParserKaizen gen = new OApiSpecParserKaizen();

    Schema sma =
        gen.parseFromLocalResource("sample.yaml").getSchemaOfRequest("/duplicates", "post");

    assertThat(sma, is(notNullValue()));

    JSONObject jsonSchema = gen.resolveSchema(sma);
    assertThat(jsonSchema, is(notNullValue()));
    JSONObject properties = jsonSchema.getJSONObject("properties");

    assertThat(properties, is(notNullValue()));
    assertThat(properties.keySet().size(), is(1));

    assertThat(properties.getJSONObject("name"), is(notNullValue()));
    assertThat(properties.getJSONObject("name").get("type").toString(), is("string"));
  }

  @Test
  @Disabled
  public void resolve_schema_oneOf() throws Exception {
    OApiSpecParserKaizen gen = new OApiSpecParserKaizen();

    Schema sma = gen.parseFromLocalResource("sample.yaml").getSchemaOfRequest("/oneof", "post");

    assertThat(sma, is(notNullValue()));

    JSONObject jsonSchema = gen.resolveSchema(sma);
    assertThat(jsonSchema, is(notNullValue()));
    JSONObject properties = jsonSchema.getJSONObject("properties");

    assertThat(properties, is(notNullValue()));
    assertThat(properties.keySet().size(), is(2));

    assertThat(properties.getJSONObject("name"), is(notNullValue()));
    assertThat(properties.getJSONObject("value"), is(notNullValue()));
    assertThat(properties.getJSONObject("name").get("type").toString(), is("string"));
    assertThat(properties.getJSONObject("value").get("type").toString(), is("integer"));
  }

  @Test
  @Disabled
  public void resolve_schema_anyOf() throws Exception {
    OApiSpecParserKaizen gen = new OApiSpecParserKaizen();

    Schema sma = gen.parseFromLocalResource("sample.yaml").getSchemaOfRequest("/anyof", "post");

    assertThat(sma, is(notNullValue()));

    JSONObject jsonSchema = gen.resolveSchema(sma);
    assertThat(jsonSchema, is(notNullValue()));
    JSONObject properties = jsonSchema.getJSONObject("properties");

    assertThat(properties, is(notNullValue()));
    assertThat(properties.keySet().size(), is(2));

    assertThat(properties.getJSONObject("name"), is(notNullValue()));
    assertThat(properties.getJSONObject("value"), is(notNullValue()));
    assertThat(properties.getJSONObject("name").get("type").toString(), is("string"));
    assertThat(properties.getJSONObject("value").get("type").toString(), is("integer"));
  }

  @Test
  @Disabled
  public void resolve_schema_recursive() throws Exception {
    OApiSpecParserKaizen gen = new OApiSpecParserKaizen();

    Schema sma = gen.parseFromLocalResource("sample.yaml").getSchemaOfRequest("/recursive", "post");

    assertThat(sma, is(notNullValue()));

    JSONObject jsonSchema = gen.resolveSchema(sma);
    assertThat(jsonSchema, is(notNullValue()));
    JSONObject properties = jsonSchema.getJSONObject("properties");

    assertThat(properties, is(notNullValue()));
    assertThat(properties.keySet().size(), is(3));

    assertThat(properties.getJSONObject("name"), is(notNullValue()));
    assertThat(properties.getJSONObject("value"), is(notNullValue()));
    assertThat(properties.getJSONObject("art"), is(notNullValue()));
    assertThat(properties.getJSONObject("name").get("type").toString(), is("string"));
    assertThat(properties.getJSONObject("value").get("type").toString(), is("integer"));
    assertThat(properties.getJSONObject("art").get("type").toString(), is("boolean"));
  }
}
