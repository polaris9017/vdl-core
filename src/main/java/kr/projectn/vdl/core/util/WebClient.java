/**
 * Copyright 2016-2018 qscx9512 <moonrise917@gmail.com>
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package kr.projectn.vdl.core.util;

import org.apache.commons.validator.routines.UrlValidator;
import org.apache.http.NameValuePair;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Http request utility class
 *
 * Created by qscx9512 on 2017-05-03.
 */

public class WebClient {
    private Executor execEntity;
    private final String strUserAgent;
    private URIBuilder builder;
    private URI uri;
    private Map<String, String> customHeader;
    private Response responseEntity;

    public WebClient() {
        strUserAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36";
        builder = new URIBuilder();

        try {
            execEntity = Executor.newInstance(WebClient.ignoreRequestSSLError());
        } catch (Exception e) {
            System.exit(0);
        }

        customHeader = new HashMap<>();
    }

    public WebClient request(String reqMethod) {

        Request reqEntity = null;

        switch (reqMethod) {
            case "get":
                reqEntity = Request.Get(uri);
                break;
            case "post":
                reqEntity = Request.Post(uri);
        }

        reqEntity.addHeader("User-Agent", strUserAgent);
        reqEntity.addHeader("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4,ja;q=0.2");
        //taken from http://stove99.tistory.com/96
        if (!customHeader.isEmpty()) {
            for (Map.Entry<String, String> el : customHeader.entrySet()) {
                reqEntity.addHeader(el.getKey(), el.getValue());
            }
        }

        try {
            responseEntity = execEntity.execute(reqEntity);
        } catch (Exception e) {
            System.exit(0);
        }

        return this;
    }

    public WebClient request(String reqMethod, Map<String, String> header) {
        customHeader = header;
        this.request(reqMethod);
        return this;
    }

    public WebClient setClientConnection(String url) {
        try {
            URL urlContainer = new URL(Optional.ofNullable(url).orElse(""));
            Optional<String> protocol;
            customHeader = new HashMap<>();
            builder = new URIBuilder();

            if (new UrlValidator().isValid(urlContainer.toString())) {
                protocol = Optional.ofNullable(urlContainer.getProtocol());
                builder = builder.setScheme(protocol.orElse("http"))
                        .setHost(urlContainer.getHost())
                        .setPort(urlContainer.getPort())
                        .setPath(urlContainer.getPath())
                        .setCustomQuery(urlContainer.getQuery());
            }

            uri = builder.build();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            System.exit(0);
        }

        return this;
    }

    public WebClient setConnectionParameter(List<NameValuePair> param) {
        try {
            builder = builder.setParameters(param);
            uri = builder.build();
        } catch (URISyntaxException e) {
            System.exit(0);
        }
        return this;
    }

    public WebClient setHeader(String key, String value) {
        customHeader.put(key, value);
        return this;
    }

    // Taken from http://www.codepreference.com/2017/02/using-apache-httpclient-fluent-api.html
    private static CloseableHttpClient ignoreRequestSSLError() throws Exception {
        final SSLContext sslContext = new SSLContextBuilder()
                .loadTrustMaterial(null, (x509CertChain, authType) -> true)
                .build();

        return HttpClientBuilder.create()
                .setSSLContext(sslContext)
                .setConnectionManager(
                        new PoolingHttpClientConnectionManager(
                                RegistryBuilder.<ConnectionSocketFactory>create()
                                        .register("http", PlainConnectionSocketFactory.INSTANCE)
                                        .register("https", new SSLConnectionSocketFactory(sslContext,
                                                NoopHostnameVerifier.INSTANCE))
                                        .build()
                        ))
                .build();
    }

    public String getAsString() {
        try {
            return responseEntity.returnContent().asString();
        } catch (Exception e) {
            System.exit(0);
        }
        return null;
    }

    public void writeFile(String title) {
        try {
            responseEntity.saveContent(new File(title));
        } catch (Exception e) {
            System.exit(0);
        }
    }
}