package com.bakkenbaeck.token.headless.signal;

import org.whispersystems.signalservice.api.push.TrustStore;

import java.io.InputStream;

class WhisperTrustStore implements TrustStore {

    @Override
    public InputStream getKeyStoreInputStream() {
        return WhisperTrustStore.class.getResourceAsStream("token.store");
    }

    @Override
    public String getKeyStorePassword() {
        return "whisper";
    }
}