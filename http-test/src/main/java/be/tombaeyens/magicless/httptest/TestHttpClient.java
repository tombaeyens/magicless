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

package be.tombaeyens.magicless.httptest;

import be.tombaeyens.magicless.httpclient.ClientRequest;
import be.tombaeyens.magicless.httpclient.HttpClient;

public class TestHttpClient extends HttpClient {

  @Override
  public ClientRequest newRequest(String method, String url) {
    return new TestClientRequest(this, method, resolveUrl(url));
  }
}
