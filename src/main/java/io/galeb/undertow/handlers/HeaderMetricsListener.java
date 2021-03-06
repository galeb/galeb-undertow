/*
 * Copyright (c) 2014-2015 Globo.com - ATeam
 * All rights reserved.
 *
 * This source is subject to the Apache License, Version 2.0.
 * Please see the LICENSE file for more information.
 *
 * Authors: See AUTHORS file
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.galeb.undertow.handlers;

import io.galeb.core.logging.Logger;
import io.galeb.core.statsd.StatsdClient;
import io.undertow.server.ExchangeCompletionListener;
import io.undertow.server.HttpServerExchange;

class HeaderMetricsListener implements ExchangeCompletionListener {

    private Logger logger;
    private StatsdClient statsdClient;

    @Override
    public void exchangeEvent(final HttpServerExchange exchange, final NextListener nextListener) {
        final String real_dest = exchange.getAttachment(BackendSelector.REAL_DEST);
        if (real_dest != null) {
            String virtualhost = exchange.getHostName();
            String backend = real_dest;
            String httpStatus = String.valueOf(exchange.getResponseCode());
            long requestTime = (System.nanoTime() - exchange.getRequestStartTime())/1000000L;
            sendHttpStatusCount(virtualhost, backend, httpStatus);
            sendRequestTime(virtualhost, backend, requestTime);
        }
        nextListener.proceed();
    }

    public HeaderMetricsListener setStatsd(StatsdClient statsdClient) {
        this.statsdClient = statsdClient;
        return this;
    }

    public HeaderMetricsListener setLogger(Logger logger) {
        this.logger = logger;
        return this;
    }

    private void sendHttpStatusCount(String virtualhostId, String backendId, String httpStatus) {
        final String virtualhost = StatsdClient.cleanUpKey(virtualhostId);
        final String backend = StatsdClient.cleanUpKey(backendId);
        final String key = virtualhost + "." + backend + "." + StatsdClient.PROP_HTTPCODE_PREFIX+httpStatus;
        statsdClient.incr(key);
    }


    private void sendRequestTime(String virtualhostId, String backendId, long requestTime) {
        final String virtualhost = StatsdClient.cleanUpKey(virtualhostId);
        final String backend = StatsdClient.cleanUpKey(backendId);
        final String key = virtualhost + "." + backend + "." + StatsdClient.PROP_REQUESTTIME;
        statsdClient.timing(key, requestTime);
    }

}