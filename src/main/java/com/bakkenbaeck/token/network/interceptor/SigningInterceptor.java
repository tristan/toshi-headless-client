package com.bakkenbaeck.token.network.interceptor;


import com.bakkenbaeck.token.crypto.HDWallet;
import com.bakkenbaeck.token.crypto.util.HashUtil;

import java.io.IOException;
import java.util.Base64;

import okhttp3.*;
import okio.Buffer;

public class SigningInterceptor implements Interceptor {

    private HDWallet wallet;

    private final String TIMESTAMP_QUERY_PARAMETER = "timestamp";
    private final String ADDRESS_HEADER = "Token-ID-Address";
    private final String SIGNATURE_HEADER = "Token-Signature";
    private final String TIMESTAMP_HEADER = "Token-Timestamp";

    public SigningInterceptor(HDWallet wallet) {
        this.wallet = wallet;
    }


    private static String bodyToString(final RequestBody request){
        try {
            final RequestBody copy = request;
            final Buffer buffer = new Buffer();
            copy.writeTo(buffer);
            return buffer.readUtf8();
        }
        catch (final IOException e) {
            return "did not work";
        }
    }

    @Override
    public Response intercept(final Chain chain) throws IOException {
        final Request original = chain.request();

        final String timestamp = original.url().queryParameter(TIMESTAMP_QUERY_PARAMETER);
        if (this.wallet == null || timestamp == null) {
            // Only signing outgoing requests that have a timestamp argument
            return chain.proceed(original);
        }

        final Buffer buffer = new Buffer();
        final String method = original.method();
        final String path = original.url().encodedPath();
        original.body().writeTo(buffer);
        final byte[] body = buffer.readByteArray();
        final byte[] hashedBody = HashUtil.sha3(body);
        final String encodedBody = Base64.getEncoder().encodeToString(hashedBody);

        final String forSigning = method + "\n" + path + "\n" + timestamp + "\n" + encodedBody;
        final String signature = this.wallet.signIdentity(forSigning);

        final HttpUrl url = chain.request().url()
                .newBuilder()
                .removeAllQueryParameters(TIMESTAMP_QUERY_PARAMETER)
                .build();

        final Request request = original.newBuilder()
                .removeHeader(TIMESTAMP_QUERY_PARAMETER)
                .method(original.method(), original.body())
                .addHeader(TIMESTAMP_HEADER, timestamp)
                .addHeader(SIGNATURE_HEADER, signature)
                .addHeader(ADDRESS_HEADER, this.wallet.getOwnerAddress())
                .url(url)
                .build();


        return chain.proceed(request);
    }
}
