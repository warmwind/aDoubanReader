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
package oauth.signpost;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import oauth.signpost.http.HttpParameters;
import oauth.signpost.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

/**
 * ABC for all provider implementations. If you're writing a custom provider,
 * you will probably inherit from this class, since it takes a lot of work from
 * you.
 * 
 * @author Matthias Kaeppler
 */
public abstract class AbstractOAuthProvider implements OAuthProvider {

    private static final long serialVersionUID = 1L;

    private String requestTokenEndpointUrl;

    private String accessTokenEndpointUrl;

    private String authorizationWebsiteUrl;

    private HttpParameters responseParameters;

    private Map<String, String> defaultHeaders;

    private boolean isOAuth10a;

    private transient OAuthProviderListener listener;

    public AbstractOAuthProvider(String requestTokenEndpointUrl, String accessTokenEndpointUrl,
            String authorizationWebsiteUrl) {
        this.requestTokenEndpointUrl = requestTokenEndpointUrl;
        this.accessTokenEndpointUrl = accessTokenEndpointUrl;
        this.authorizationWebsiteUrl = authorizationWebsiteUrl;
        this.responseParameters = new HttpParameters();
        this.defaultHeaders = new HashMap<String, String>();
    }

    public synchronized String retrieveRequestToken(OAuthConsumer consumer, String callbackUrl,
            String... customOAuthParams) throws OAuthMessageSignerException,
            OAuthNotAuthorizedException, OAuthExpectationFailedException,
            OAuthCommunicationException {

        // invalidate current credentials, if any
        consumer.setTokenWithSecret(null, null);

        // 1.0a expects the callback to be sent while getting the request token.
        // 1.0 service providers would simply ignore this parameter.
        HttpParameters params = new HttpParameters();
        params.putAll(customOAuthParams, true);
        params.put(OAuth.OAUTH_CALLBACK, callbackUrl, true);

        retrieveToken(consumer, requestTokenEndpointUrl, params);

        String callbackConfirmed = responseParameters.getFirst(OAuth.OAUTH_CALLBACK_CONFIRMED);
        responseParameters.remove(OAuth.OAUTH_CALLBACK_CONFIRMED);
        isOAuth10a = Boolean.TRUE.toString().equals(callbackConfirmed);

        // 1.0 service providers expect the callback as part of the auth URL,
        // Do not send when 1.0a.
        if (isOAuth10a) {
            return OAuth.addQueryParameters(authorizationWebsiteUrl, OAuth.OAUTH_TOKEN,
                consumer.getToken());
        } else {
            return OAuth.addQueryParameters(authorizationWebsiteUrl, OAuth.OAUTH_TOKEN,
                consumer.getToken(), OAuth.OAUTH_CALLBACK, callbackUrl);
        }
    }

    public synchronized void retrieveAccessToken(OAuthConsumer consumer, String oauthVerifier,
            String... customOAuthParams) throws OAuthMessageSignerException,
            OAuthNotAuthorizedException, OAuthExpectationFailedException,
            OAuthCommunicationException {

        if (consumer.getToken() == null || consumer.getTokenSecret() == null) {
            throw new OAuthExpectationFailedException(
                    "Authorized request token or token secret not set. "
                            + "Did you retrieve an authorized request token before?");
        }

        HttpParameters params = new HttpParameters();
        params.putAll(customOAuthParams, true);

        if (isOAuth10a && oauthVerifier != null) {
            params.put(OAuth.OAUTH_VERIFIER, oauthVerifier, true);
        }
        retrieveToken(consumer, accessTokenEndpointUrl, params);
    }

    /**
     * <p>
     * Implemented by subclasses. The responsibility of this method is to
     * contact the service provider at the given endpoint URL and fetch a
     * request or access token. What kind of token is retrieved solely depends
     * on the URL being used.
     * </p>
     * <p>
     * Correct implementations of this method must guarantee the following
     * post-conditions:
     * <ul>
     * <li>the {@link OAuthConsumer} passed to this method must have a valid
     * {@link OAuth#OAUTH_TOKEN} and {@link OAuth#OAUTH_TOKEN_SECRET} set by
     * calling {@link OAuthConsumer#setTokenWithSecret(String, String)}</li>
     * <li>{@link #getResponseParameters()} must return the set of query
     * parameters served by the service provider in the token response, with all
     * OAuth specific parameters being removed</li>
     * </ul>
     * </p>
     * 
     * @param consumer
     *        the {@link OAuthConsumer} that should be used to sign the request
     * @param endpointUrl
     *        the URL at which the service provider serves the OAuth token that
     *        is to be fetched
     * @param customOAuthParams
     *        you can pass custom OAuth parameters here (such as oauth_callback
     *        or oauth_verifier) which will go directly into the signer, i.e.
     *        you don't have to put them into the request first.
     * @throws OAuthMessageSignerException
     *         if signing the token request fails
     * @throws OAuthCommunicationException
     *         if a network communication error occurs
     * @throws OAuthNotAuthorizedException
     *         if the server replies 401 - Unauthorized
     * @throws OAuthExpectationFailedException
     *         if an expectation has failed, e.g. because the server didn't
     *         reply in the expected format
     */
    protected void retrieveToken(OAuthConsumer consumer, String endpointUrl,
            HttpParameters customOAuthParams) throws OAuthMessageSignerException,
            OAuthCommunicationException, OAuthNotAuthorizedException,
            OAuthExpectationFailedException {

        if (consumer.getConsumerKey() == null || consumer.getConsumerSecret() == null) {
            throw new OAuthExpectationFailedException("Consumer key or secret not set");
        }

        HttpRequest request = null;
        HttpResponse response = null;
        try {
            request = createRequest(endpointUrl);
            for (String header : defaultHeaders.keySet()) {
                request.setHeader(header, defaultHeaders.get(header));
            }
            if (customOAuthParams != null && !customOAuthParams.isEmpty()) {
//                consumer.setAdditionalParameters(customOAuthParams);
            }
            
            if (this.listener != null) {
                this.listener.prepareRequest(request);
            }

            consumer.sign(request);
            
            if (this.listener != null) {
                this.listener.prepareSubmission(request);
            }

            response = sendRequest(request);

            HttpParameters responseParams = OAuth.decodeForm(EntityUtils.toString(response.getEntity()));

            String token = responseParams.getFirst(OAuth.OAUTH_TOKEN);
            String secret = responseParams.getFirst(OAuth.OAUTH_TOKEN_SECRET);
            responseParams.remove(OAuth.OAUTH_TOKEN);
            responseParams.remove(OAuth.OAUTH_TOKEN_SECRET);

            setResponseParameters(responseParams);

            if (token == null || secret == null) {
                throw new OAuthExpectationFailedException(
                        "Request token or token secret not set in server reply. "
                                + "The service provider you use is probably buggy.");
            }

            consumer.setTokenWithSecret(token, secret);

        } catch (OAuthNotAuthorizedException e) {
            throw e;
        } catch (OAuthExpectationFailedException e) {
            throw e;
        } catch (Exception e) {
            throw new OAuthCommunicationException(e);
        } finally {
            try {
                closeConnection(request, response);
            } catch (Exception e) {
                throw new OAuthCommunicationException(e);
            }
        }
    }

    /**
     * Overrride this method if you want to customize the logic for building a
     * request object for the given endpoint URL.
     * 
     * @param endpointUrl
     *        the URL to which the request will go
     * @return the request object
     * @throws Exception
     *         if something breaks
     */
    protected abstract HttpRequest createRequest(String endpointUrl) throws Exception;

    /**
     * Override this method if you want to customize the logic for how the given
     * request is sent to the server.
     * 
     *
     * @param request
     *        the request to send
     * @return the response to the request
     * @throws Exception
     *         if something breaks
     */
    protected abstract org.apache.http.HttpResponse sendRequest(HttpRequest request) throws Exception;

    /**
     * Called when the connection is being finalized after receiving the
     * response. Use this to do any cleanup / resource freeing.
     * 
     *
     *
     * @param request
     *        the request that has been sent
     * @param response
     *        the response that has been received
     * @throws Exception
     *         if something breaks
     */
    protected void closeConnection(HttpRequest request, HttpResponse response) throws Exception {
        // NOP
    }

    public HttpParameters getResponseParameters() {
        return responseParameters;
    }

    /**
     * Returns a single query parameter as served by the service provider in a
     * token reply. You must call {@link #setResponseParameters} with the set of
     * parameters before using this method.
     * 
     * @param key
     *        the parameter name
     * @return the parameter value
     */
    protected String getResponseParameter(String key) {
        return responseParameters.getFirst(key);
    }

    public void setResponseParameters(HttpParameters parameters) {
        this.responseParameters = parameters;
    }

    public void setOAuth10a(boolean isOAuth10aProvider) {
        this.isOAuth10a = isOAuth10aProvider;
    }

    public boolean isOAuth10a() {
        return isOAuth10a;
    }

    public String getRequestTokenEndpointUrl() {
        return this.requestTokenEndpointUrl;
    }

    public String getAccessTokenEndpointUrl() {
        return this.accessTokenEndpointUrl;
    }

    public String getAuthorizationWebsiteUrl() {
        return this.authorizationWebsiteUrl;
    }

    public void setRequestHeader(String header, String value) {
        defaultHeaders.put(header, value);
    }

    public Map<String, String> getRequestHeaders() {
        return defaultHeaders;
    }

    public void setListener(OAuthProviderListener listener) {
        this.listener = listener;
    }

    public void removeListener(OAuthProviderListener listener) {
        this.listener = null;
    }
}
