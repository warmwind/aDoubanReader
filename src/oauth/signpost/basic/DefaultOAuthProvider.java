/*
 * Copyright (c) 2009 Matthias Kaeppler Licensed under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package oauth.signpost.basic;

import oauth.signpost.AbstractOAuthProvider;
import oauth.signpost.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.net.MalformedURLException;

/**
 * This default implementation uses {@link java.net.HttpURLConnection} type GET
 * requests to receive tokens from a service provider.
 *
 * @author Matthias Kaeppler
 */
public class DefaultOAuthProvider extends AbstractOAuthProvider {

    private static final long serialVersionUID = 1L;

    public DefaultOAuthProvider(String requestTokenEndpointUrl, String accessTokenEndpointUrl,
                                String authorizationWebsiteUrl) {
        super(requestTokenEndpointUrl, accessTokenEndpointUrl, authorizationWebsiteUrl);
    }

    protected HttpRequest createRequest(String endpointUrl) throws MalformedURLException,
            IOException {
        return new UrlStringRequestAdapter(endpointUrl);
    }

    protected HttpResponse sendRequest(final HttpRequest request) throws IOException {
        return new DefaultHttpClient().execute(new HttpGet(String.valueOf(request.unwrap())));
    }

}
