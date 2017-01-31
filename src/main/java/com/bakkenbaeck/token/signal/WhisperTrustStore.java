package com.bakkenbaeck.token.signal;

import org.whispersystems.signalservice.api.push.TrustStore;

import java.io.InputStream;

class WhisperTrustStore implements TrustStore {

    @Override
    public InputStream getKeyStoreInputStream() {
        return WhisperTrustStore.class.getResourceAsStream("heroku.store");
    }

    @Override
    public String getKeyStorePassword() {
        return "whisper";
    }
}