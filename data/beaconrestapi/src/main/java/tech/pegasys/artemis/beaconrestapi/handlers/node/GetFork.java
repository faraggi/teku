/*
 * Copyright 2019 ConsenSys AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package tech.pegasys.artemis.beaconrestapi.handlers.node;

import static io.javalin.core.util.Header.CACHE_CONTROL;
import static tech.pegasys.artemis.beaconrestapi.CacheControlUtils.CACHE_NONE;
import static tech.pegasys.artemis.beaconrestapi.RestApiConstants.NO_CONTENT_PRE_GENESIS;
import static tech.pegasys.artemis.beaconrestapi.RestApiConstants.RES_INTERNAL_ERROR;
import static tech.pegasys.artemis.beaconrestapi.RestApiConstants.RES_NO_CONTENT;
import static tech.pegasys.artemis.beaconrestapi.RestApiConstants.RES_OK;
import static tech.pegasys.artemis.beaconrestapi.RestApiConstants.TAG_NODE;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.plugin.openapi.annotations.HttpMethod;
import io.javalin.plugin.openapi.annotations.OpenApi;
import io.javalin.plugin.openapi.annotations.OpenApiContent;
import io.javalin.plugin.openapi.annotations.OpenApiResponse;
import tech.pegasys.artemis.api.ChainDataProvider;
import tech.pegasys.artemis.api.schema.Fork;
import tech.pegasys.artemis.provider.JsonProvider;

public class GetFork implements Handler {
  public static final String ROUTE = "/node/fork";
  private final JsonProvider jsonProvider;
  private final ChainDataProvider provider;

  public GetFork(ChainDataProvider provider, JsonProvider jsonProvider) {
    this.provider = provider;
    this.jsonProvider = jsonProvider;
  }

  @OpenApi(
      path = ROUTE,
      method = HttpMethod.GET,
      summary = "Get the fork of the current head.",
      tags = {TAG_NODE},
      description =
          "Returns information about the fork of the beacon chain head from the node’s perspective.",
      responses = {
        @OpenApiResponse(
            status = RES_OK,
            content = @OpenApiContent(from = Fork.class),
            description = "An object containing the fork of the current head."),
        @OpenApiResponse(status = RES_NO_CONTENT, description = NO_CONTENT_PRE_GENESIS),
        @OpenApiResponse(status = RES_INTERNAL_ERROR)
      })
  @Override
  public void handle(Context ctx) throws Exception {
    ctx.header(CACHE_CONTROL, CACHE_NONE);
    Fork result = provider.getFork();
    ctx.result(jsonProvider.objectToJSON(result));
  }
}
