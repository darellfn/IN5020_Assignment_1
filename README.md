
# User guide
Step-by-step instructions for compiling, building, deploying and running the application:
1. First, navigate to the `src\main\java\` folder\
   `cd src\main\java\`
2. Compile all java files within `com\ass1\`\
     `javac com\ass1\*\*.java`
3. Run `Proxy.java` \
     `java com\ass1\proxy\Proxy.java`
4. Run `ServerSimulator.java` with two arguments\
     `java com\ass1\server\ServerSimulator.java <arg1> <arg2>`
5. Run `Client.java` with optional flag arguments\
     `java com\ass1\client\Client.java <flags>`

Flags and arguments are explained in the [next section](#command-line-flags).

## Command line flags

### `ServerSimulator.java`

Takes in two boolean arguments at the command line.
The first argument enables server cache:\
`true` - server cache enabled\
`false` - server cache disabled

The second argument enables the OLDEST cache method:\
`true` - OLDEST method used\
`false` - FIFO method used

### `Client.java`

To enable client cache, use the flags:
`-cc`
`--client-cache`

To set the cache method, use the flags:
`-fifo`
`-oldest`\
These flags are only valid if a client-cache-enabling flag precedes them.

To set the delay time (in milliseconds) use the flags:
`-20`
`-50`\
If none of these flags are given, the default delay is 20ms.

To set which output file to create/write to, use the following flags:\
`-wcc` `--write-client-cache` (writes  `client_cache.txt`)\
`-wsc` `--write-server-cache` (writes  `server_cache.txt`)\
`-wns` `--write-naive-server` (writes  `naive_server.txt`)\
If none of these flags are given, the default output file is `naive_server.txt`.


