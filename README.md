# Token Headless Client

The headless client for connecting to the [Token platform](https://www.tokenbrowser.com)

The headless client is a full client for the Chat service that exposes a control
interface via Redis pubsub. It is primarily intended for use by bots.


## Building executable fat jar

```bash
> gradle TokenHeadlessClientCapsule
> #path: build/libs/token-headless-1.0-SNAPSHOT-capsule.jar
```


## Running
```bash
> java -jar build/libs/token-headless-1.0-SNAPSHOT-capsule.jar path/to/config.yml
```

## Configuring
Configuration is done via passing a `config.yml` file as an argument to the client.
Some configuration keys are optional, and will be pulled from ENV vars if missing.

```yaml
server: https://token-chat-service.herokuapp.com # URL of Chat service
address: '0x...' # Ethereum address, pulled from env var TOKEN_CLIENT_ADDRESS if omitted
username: '' # username to be registered/updated with the ID server
seed: # wallet master seed, 12 word phrase, pulled from env var TOKEN_CLIENT_SEED if omitted
store: store # path to durable storage directory for Signal store
redis:
  uri: redis://username:password@hostname:port
  timeout: 2000
```

### Redis Configuration

3 different forms are accepted for Redis configuration:

URI
```yaml
redis:
  uri: redis://username:password@hostname:port
```

EnvKey (useful for environments such as Heroku that set a fixed env variable whose value is the Redis uri)
```yaml
redis:
  envKey: REDIS_URL
```

Parts
```yaml
redis:
  host: hostname
  port: 8000
  password: secret
```