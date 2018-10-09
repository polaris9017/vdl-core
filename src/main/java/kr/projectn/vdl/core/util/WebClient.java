/**
 * Copyright 2016-2018 polaris9017 <moonrise917@gmail.com>
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
package kr.projectn.vdl.core.util;

import org.apache.commons.validator.routines.UrlValidator;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Web request util class wrapping Apache HttpClient library with Fluent API
 * @since 1.0
 */
public class WebClient {
    private Executor execEntity;
    private final String strUserAgent;
    private URIBuilder builder;
    private URI uri;
    private Map<String, String> customHeader;
    private Response responseEntity;
    private static BasicCookieStore cookieStore;

    /**
     * Create new {@code WebClient} and set required settings
     */
    public WebClient() throws Exception {
        strUserAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36";
        builder = new URIBuilder();
        cookieStore = new BasicCookieStore();

        execEntity = Executor.newInstance(WebClient.ignoreRequestSSLError());

        customHeader = new HashMap<>();
    }

    /**
     * Requests web entity with HTTP.<br><br>
     * Supported REST API for request is GET and POST and this can use with passing {@code reqMethod} to
     * {@code get}, {@code post} as string respectively.
     *
     * @param reqMethod request REST API type
     * @return {@link WebClient} entity contains response from web
     */
    public WebClient request(String reqMethod) throws IOException {

        Request reqEntity = null;

        switch (reqMethod) {
            case "get":
                reqEntity = Request.Get(uri);
                break;
            case "post":
                reqEntity = Request.Post(uri);
                break;
        }

        reqEntity.addHeader("User-Agent", strUserAgent);
        reqEntity.addHeader("Accept-Language", "ko-KR,ko;q=0.8,en-US;q=0.6,en;q=0.4,ja;q=0.2");
        //taken from http://stove99.tistory.com/96
        if (!customHeader.isEmpty()) {
            for (Map.Entry<String, String> el : customHeader.entrySet()) {
                reqEntity.addHeader(el.getKey(), el.getValue());
            }
        }
        responseEntity = execEntity.execute(reqEntity);

        return this;
    }

    /**
     * Requests web entity with HTTP.<br><br>
     * Supported REST API for request is GET and POST and this can use with passing {@code reqMethod} to
     * {@code get}, {@code post} as string respectively.<br>
     * Additional header should be passed to {@code header} parameter.
     * @param reqMethod request REST API type
     * @param header additional header
     * @return {@link WebClient} entity contains response from web
     */
    public WebClient request(String reqMethod, Map<String, String> header) throws IOException {
        customHeader = header;
        this.request(reqMethod);
        return this;
    }

    /**
     * Requests web entity with HTTP.<br><br>
     * For {@link WebClient} {@code c}, the expressions {@code c.request("get")} and {@code c.request()} is equivalent.
     * @return {@link WebClient} entity contains response from web
     */
    public WebClient request() throws IOException {
        this.request("get");
        return this;
    }

    /**
     * Requests web entity with HTTP.<br><br>
     * For {@link WebClient} {@code c} and header map {@code h},
     * the expressions {@code c.request("get", h)} and {@code c.request(h)} is equivalent.
     * @param header additional header
     * @return {@link WebClient} entity contains response from web
     */
    public WebClient request(Map<String, String> header) throws IOException {
        this.request("get", header);
        return this;
    }

    /**
     * Set destination URL for request
     * @param url destination URL
     * @return {@link WebClient} entity contains destination URL
     */
    public WebClient setClientConnection(String url) throws MalformedURLException, URISyntaxException {
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

        return this;
    }

    /**
     * Set URL parameter for request.<br><br>
     * This parameter used both GET and POST request method.
     * @param param URL parameter
     * @return {@link WebClient} entity contains URL parameter
     */
    public WebClient setConnectionParameter(List<NameValuePair> param) throws URISyntaxException {
        builder = builder.setParameters(param);
        uri = builder.build();

        return this;
    }

    /**
     * Set user-custom header for request in advance.
     * @param key header key
     * @param value header value matched to key
     * @return {@link WebClient} entity contains header
     */
    public WebClient setHeader(String key, String value) {
        customHeader.put(key, value);
        return this;
    }

    // Taken from http://www.codepreference.com/2017/02/using-apache-httpclient-fluent-api.html
    // Solve Cookie warnings from https://stackoverflow.com/questions/7459279/httpclient-warning-cookie-rejected-illegal-domain-attribute/12324927
    private static CloseableHttpClient ignoreRequestSSLError() throws Exception {
        final SSLContext sslContext = new SSLContextBuilder()
                .loadTrustMaterial(null, (x509CertChain, authType) -> true)
                .build();

        return HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        // Waiting for a connection from connection manager
                        .setConnectionRequestTimeout(10000)
                        // Waiting for connection to establish
                        .setConnectTimeout(5000)
                        .setExpectContinueEnabled(false)
                        // Waiting for data
                        .setSocketTimeout(5000)
                        .setCookieSpec("easy")
                        .build())
                .setSSLContext(sslContext)
                .setConnectionManager(
                        new PoolingHttpClientConnectionManager(
                                RegistryBuilder.<ConnectionSocketFactory>create()
                                        .register("http", PlainConnectionSocketFactory.INSTANCE)
                                        .register("https", new SSLConnectionSocketFactory(sslContext,
                                                NoopHostnameVerifier.INSTANCE))
                                        .build()
                        ))
                .setDefaultCookieStore(cookieStore)
                .build();
    }

    /**
     * Returns response body to string.<br><br>
     * This method is suitable for plain HTML body, not for binary response.
     * @return response body string
     */
    public String getAsString() throws IOException {
        return responseEntity.returnContent().asString();
    }

    /**
     * Writes response body into file with {@code title} as file name.<br><br>
     * This method is suitable for both plain HTML body and binary responses.
     * @param title file name you desire to write
     * @throws IOException
     */
    public void writeFile(String title) throws IOException {
        responseEntity.saveContent(new File(title));
    }
}