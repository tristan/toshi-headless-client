CREATE TABLE IF NOT EXISTS bot_sessions (
    eth_address TEXT,
    data JSON,
    PRIMARY KEY(eth_address)
);

CREATE TABLE IF NOT EXISTS key_value_store (
    key TEXT,
    value TEXT,
    type TEXT,
    PRIMARY KEY(key)
);

CREATE TABLE IF NOT EXISTS local_identities (
    eth_address TEXT,
    device_id INTEGER DEFAULT 0,
    password TEXT,
    identity_key BLOB,
    registration_id INTEGER,
    signaling_key BLOB,
    prekey_id_offset INTEGER DEFAULT 0,
    next_signed_prekey_id INTEGER DEFAULT 0,
    registered BOOLEAN DEFAULT FALSE,

    PRIMARY KEY (eth_address)
);

-- signal protocol store

CREATE TABLE IF NOT EXISTS signal_identity_store (
    name TEXT,
    device_id INTEGER,
    identity_key BLOB,
    updated BOOLEAN DEFAULT FALSE,

    PRIMARY KEY (name, device_id)
);

CREATE TABLE IF NOT EXISTS signal_prekey_store (
    prekey_id INTEGER,
    record BLOB,

    PRIMARY KEY (prekey_id)
);

CREATE TABLE IF NOT EXISTS signal_session_store (
    name TEXT,
    device_id INTEGER,
    record BLOB,

    PRIMARY KEY (name, device_id)
);

CREATE TABLE IF NOT EXISTS signal_signed_prekey_store (
    signed_prekey_id INTEGER,
    record BLOB,

    PRIMARY KEY (signed_prekey_id)
);

-- contacts

CREATE TABLE IF NOT EXISTS contacts_store (
    number TEXT,
    name TEXT,
    color TEXT,

    PRIMARY KEY (number)
);

CREATE TABLE IF NOT EXISTS thread_store (
    thread_id TEXT,
    message_expiration_time INT,

    PRIMARY KEY (thread_id)
);

CREATE TABLE IF NOT EXISTS group_store (
    user_id SERIAL,
    group_id BLOB,
    name TEXT,
    avatar_id BIGINT,
    active BOOLEAN,

    PRIMARY KEY (group_id)
);

CREATE TABLE IF NOT EXISTS group_members_store (
    group_id BLOB,
    number TEXT,

    PRIMARY KEY (group_id, number)
);
