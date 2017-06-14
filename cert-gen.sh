#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'
# This script will generate trust store certs for different environments

askEnvironment() {
    read -p "Which environment? \"dev\" or \"release\": [dev]" ENV
    ENV=${ENV:-dev}
}

askHost() {
    read -p "Enter address: [$HOST]" CERTHOST
    CERTHOST=${CERTHOST:-$HOST}
}

getBouncyCastle() {
    if [ ! -f /tmp/bcprov-jdk15on-156.jar ]
    then
        curl -s -S -L -o /tmp/bcprov-jdk15on-156.jar https://downloads.bouncycastle.org/java/bcprov-jdk15on-156.jar
    fi
}

generateCertificate() {
    getBouncyCastle
    if [ -z ${CERTFILE:-} ]; then
        openssl s_client -connect $CERTHOST:443 </dev/null | sed -ne '/-BEGIN CERTIFICATE-/,/-OCEND CERTIFICATE-/p' > /tmp/chatstore.cert
        CERTFILE="/tmp/chatstore.cert"
    fi
    keytool -import -noprompt -trustcacerts -alias $CERTHOST -file $CERTFILE -keystore chatkey.store -storepass whisper -storetype BKS -providerclass org.bouncycastle.jce.provider.BouncyCastleProvider -providerpath /tmp/bcprov-jdk15on-156.jar
}

moveCertificate() {
    MOVE_PATH="./src/main/resources/com/bakkenbaeck/token/headless/signal/heroku.store"
    if [[ $ENV == "release" ]]
    then
        MOVE_PATH="./src/main/resources/com/bakkenbaeck/token/headless/signal/token.store"
    fi
    mv chatkey.store $MOVE_PATH
}

while true; do
    askEnvironment

    if [[ $ENV == "dev" ]] || [[ $ENV == "release" ]]
    then
        HOST="token-chat-service-development.herokuapp.com"
        if [[ $ENV == "release" ]]
        then
            HOST="chat.service.tokenbrowser.com"
        fi

        askHost
        generateCertificate
        moveCertificate
        echo "Done"
        break
    fi
done

exit 0
