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

package be.tombaeyens.configuration.advanced;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public interface ValueType {

  Object parseValue(String textValue);
  String getTypeText();

  ValueType STRING = new ValueType() {
    @Override
    public Object parseValue(String textValue) {
      return textValue;
    }
    @Override
    public String getTypeText() {
      return "string";
    }
  };

  ValueType INTEGER = new ValueType() {
    @Override
    public Object parseValue(String textValue) {
      return Integer.parseInt(textValue);
    }
    @Override
    public String getTypeText() {
      return "integer";
    }
  };

  class IntegerType implements ValueType {
    Integer minValue;
    Integer maxValue;

    public IntegerType(Integer minValue, Integer maxValue) {
      this.minValue = minValue;
      this.maxValue = maxValue;
    }
    @Override
    public Object parseValue(String textValue) {
      int value = Integer.parseInt(textValue);
      if (minValue!=null && value<minValue) {
        throw new RuntimeException("Value "+textValue+" is less than the minimum value "+minValue);
      }
      if (maxValue!=null && value>maxValue) {
        throw new RuntimeException("Value "+textValue+" is greater than the maximum value "+maxValue);
      }
      return value;
    }
    @Override
    public String getTypeText() {
      return "integer["+minValue+".."+maxValue+"]";
    }
  }

  class EnumType implements ValueType {
    List<String> possibleValues;
    public EnumType(String[] possibleValues) {
      this.possibleValues = Arrays.asList(possibleValues);
    }
    @Override
    public Object parseValue(String textValue) {
      if (!possibleValues.contains(textValue)) {
        throw new RuntimeException("Value "+textValue+" is not one of the possible values: "+possibleValues);
      }
      return textValue;
    }
    @Override
    public String getTypeText() {
      return "enum["+possibleValues.stream().collect(Collectors.joining(","))+"]";
    }
  }
}
