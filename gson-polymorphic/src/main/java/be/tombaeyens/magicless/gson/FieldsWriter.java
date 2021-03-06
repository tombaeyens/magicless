/*
 * Copyright (c) 2018 Tom Baeyens
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
package be.tombaeyens.magicless.gson;

import com.google.gson.stream.JsonWriter;

public class FieldsWriter {

  JsonWriter out;
  Object bean;
  PolymorphicTypeAdapter<?> typeAdapter;

  public FieldsWriter(JsonWriter out, Object bean, PolymorphicTypeAdapter<?> typeAdapter) {
    this.out = out;
    this.bean = bean;
    this.typeAdapter = typeAdapter;
  }

  public void writeFields(String typeName) throws Exception {
    PolymorphicTypeFields polymorphicTypeFields = typeAdapter.polymorphicTypesByName.get(typeName);
    polymorphicTypeFields.write(out, bean);
  }
}
