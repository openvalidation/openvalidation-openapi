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

package io.openvalidation.openapi.parser;

import com.reprezen.jsonoverlay.Overlay;
import com.reprezen.kaizen.oasparser.OpenApi3Parser;
import com.reprezen.kaizen.oasparser.model3.*;
import io.openvalidation.common.utils.JsonUtils;
import io.openvalidation.openapi.model.OApiRule;
import io.openvalidation.openapi.model.OApiRuleContainer;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.json.JSONObject;

public class OApiSpecParserKaizen {
  private OpenApi3 _model;

  public OApiSpecParserKaizen() {}

  public OApiSpecParserKaizen parse(String conent) throws Exception {
    OpenApi3Parser parser = new OpenApi3Parser();

    URL url = new URL("file:/C:"); // pseudo base url

    this._model = parser.parse(conent, url);

    return this;
  }

  public OApiSpecParserKaizen parseFromLocalResource(String resourcName) throws Exception {
    URL url = getClass().getClassLoader().getResource(resourcName);
    return parseFromURL(url);
  }

  public OApiSpecParserKaizen parseFromURLString(String url) throws Exception {
    return parseFromURL(new URL(url));
  }

  public OApiSpecParserKaizen parseFromURL(URL url) throws Exception {
    OpenApi3Parser parser = new OpenApi3Parser();
    this._model = parser.parse(url);

    return this;
  }

  public Schema getSchemaOfRequest(String path, String method) {
    Map<String, Path> paths = _model.getPaths();
    return paths
        .get(path)
        .getOperations()
        .get(method)
        .getRequestBody()
        .getContentMediaType("application/json")
        .getSchema();
  }

  public Schema getSchemaOfResponse(String path, String method, String response) {
    Map<String, Path> paths = _model.getPaths();
    return paths
        .get(path)
        .getOperations()
        .get(method)
        .getResponse(response)
        .getContentMediaType("application/json")
        .getSchema();
  }

  public JSONObject resolveSchema(Schema schema) {
    JSONObject obj = new JSONObject();
    obj.put("type", schema.getType());

    if (schema.hasProperties()) {
      JSONObject resolved =
          resolveSchemaArray(schema.getProperties().values().stream().collect(Collectors.toList()));
      JsonUtils.combineProperties(obj, resolved);
    }

    if (schema.hasAllOfSchemas()) {
      JSONObject resolved = resolveSchemaArray(schema.getAllOfSchemas());
      JsonUtils.combineProperties(obj, resolved);
    }

    if (schema.hasOneOfSchemas()) {
      JSONObject resolved = resolveSchemaArray(schema.getOneOfSchemas());
      JsonUtils.combineProperties(obj, resolved);
    }

    if (schema.hasAnyOfSchemas()) {
      JSONObject resolved = resolveSchemaArray(schema.getAnyOfSchemas());
      JsonUtils.combineProperties(obj, resolved);
    }

    if (schema.hasEnums()) {
      obj.put("enum", schema.getEnums());
    }

    return obj;
  }

  public JSONObject resolveSchemaArray(List<Schema> schemas) {
    JSONObject out = new JSONObject();
    out.put("properties", new JSONObject());

    for (Schema sma : schemas) {
      String type = (sma.getType() != null) ? sma.getType().toLowerCase() : null;
      String name = sma.getName();
      List<Object> enums = sma.getEnums();
      //            if (name != null && name.contains("_"))
      //                name = name.substring(name.indexOf("_")+1);

      if (type != null && !type.equals("object") && !type.equals("array")) {
        JSONObject json = new JSONObject();
        json.put("type", type);

        if (enums != null && enums.size() > 0) json.put("enum", enums);

        out.getJSONObject("properties").put(name, json);
      } else if (type != null && type.equals("array")) {
        JSONObject json = new JSONObject();
        json.put("type", type);

        if (sma.getItemsSchema() != null) json.put("items", resolveSchema(sma.getItemsSchema()));

        out.getJSONObject("properties").put(name, json);
      } else {
        JSONObject jso = resolveSchema(sma);

        if (name == null) JsonUtils.combineProperties(out, jso);
        else {
          out.getJSONObject("properties").put(name, jso);
        }
      }
    }

    return out;
  }

  public List<MediaTypeMeta> findSchemaByTagName(String extension) {
    List<MediaTypeMeta> containers = new ArrayList<>();

    if (this._model.hasPaths()) {
      for (Path p : this._model.getPaths().values()) {
        if (p.hasOperations()) {
          for (Operation o : p.getOperations().values()) {
            RequestBody body = o.getRequestBody();

            if (body != null) {
              MediaType appjson = body.getContentMediaType("application/json");
              if (appjson != null) {

                // TODO: Can't find the extension if SCHEMA is an inline_model!!!!

                if (appjson.hasExtension(extension)) {
                  MediaTypeMeta mtm = new MediaTypeMeta();
                  mtm.media = appjson;
                  mtm.path = p.getPathString();
                  mtm.operationId = o.getOperationId();
                  mtm.method = Overlay.of(o).getPathInParent().toUpperCase();

                  containers.add(mtm);
                }
              }
            }

            if (o.hasResponses()) {
              for (Response res : o.getResponses().values()) {
                MediaType mt = res.getContentMediaType("application/json");
                if (mt != null && mt.hasExtension(extension)) {
                  MediaTypeMeta mtm = new MediaTypeMeta();
                  mtm.media = mt;
                  mtm.path = p.getPathString();

                  containers.add(mtm);
                }
              }
            }
          }
        }
      }
    }

    return containers;
  }

  public List<OApiRuleContainer> getRuleContainers() throws Exception {

    List<OApiRuleContainer> out = new ArrayList<>();
    List<MediaTypeMeta> mediaTypes = findSchemaByTagName("x-ov-rules");

    if (mediaTypes != null && mediaTypes.size() > 0) {
      for (MediaTypeMeta mt : mediaTypes) {
        OApiRuleContainer ruleContainer = new OApiRuleContainer();
        ruleContainer.setSchema(resolveSchema(mt.media.getSchema()));
        ruleContainer.setModelName("MDL");
        ruleContainer.setPath(mt.path);
        ruleContainer.setMethod(mt.method);

        LinkedHashMap jo = (LinkedHashMap) mt.media.getExtension("x-ov-rules");

        String plainRule = jo.get("rule").toString();

        if (jo.containsKey("culture"))
          ruleContainer.getOptions().setLocale(jo.get("culture").toString());

        //                String name = null;
        //
        //                if(jo.containsKey("name"))
        //                    name = jo.get("name").toString();
        //                else {
        //                    throw new OpenValidationException("x-ov-rules Element should contains
        // a 'name' property!");
        //                }

        OApiRule oar = new OApiRule(plainRule);
        // ruleContainer.setName(name);
        ruleContainer.setRule(oar);

        out.add(ruleContainer);
      }
    }

    return out;
  }

  public static boolean isValidSchema(String rawJson) {
    try {
      OpenApi3Parser parser = new OpenApi3Parser();
      URL url = new URL("file:/C:"); // pseudo b
      OpenApi3 model = parser.parse(rawJson, url);

      return model != null;
    } catch (Exception e) {
      return false;
    }
  }

  private class MediaTypeMeta {
    public String path;
    public String method;
    public MediaType media;
    public String operationId;
  }
}
