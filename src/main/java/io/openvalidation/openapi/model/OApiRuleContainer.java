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

package io.openvalidation.openapi.model;

import io.openvalidation.core.OpenValidationOptions;
import org.json.JSONObject;

public class OApiRuleContainer {

  private String _name;
  private JSONObject _schema;
  private OApiRule _rule;
  private String _modelName;
  private String _path;
  private String _method;
  private String _operationId;
  private OpenValidationOptions _options = new OpenValidationOptions();

  private boolean isProcessed;

  public OApiRuleContainer() {}

  public String getName() {
    return _name;
  }

  public void setName(String name) {
    this._name = name;
  }

  public JSONObject getSchema() {
    return _schema;
  }

  public void setSchema(JSONObject _schema) {
    this._schema = _schema;
  }

  public void setModelName(String name) {
    this._modelName = name;
  }

  public OApiRule getRule() {
    return _rule;
  }

  public void setRule(OApiRule _rule) {
    this._rule = _rule;
  }

  public String getRuleAsStirng() {
    return this.getRule().getPlainRule();
  }

  public String getSchemaAsString() {
    return this.getSchema().toString();
  }

  public String getModelName() {
    return this._modelName;
  }

  public String getPath() {
    return _path;
  }

  public void setPath(String _path) {
    this._path = _path;
  }

  public String getMethod() {
    return _method;
  }

  public void setMethod(String _method) {
    this._method = _method;
  }

  public String getOperationId() {
    return _operationId;
  }

  public void setOperationId(String _operationId) {
    this._operationId = _operationId;
  }

  public String getValidatorId() {
    return (this.getPath() + "/" + this.getMethod()).toLowerCase();
  }

  public boolean isProcessed() {
    return isProcessed;
  }

  public void setProcessed(boolean processed) {
    isProcessed = processed;
  }

  public OpenValidationOptions getOptions() {
    return _options;
  }

  public void setOptions(OpenValidationOptions options) {
    this._options = options;
  }
}
