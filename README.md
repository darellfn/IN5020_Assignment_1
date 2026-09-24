
# User guide
Step-by-step instructions for compiling, building, deploying and running the application:
1. Download the repository from this github
2. Open the terminal and make sure you are in the `IN5020_Asignment_1` directory
3. Run the proxy in the terminal\
     `java -cp target/solution-1.0-SNAPSHOT.jar com.ass1.proxy.Proxy`
4. Open a new terminal and run the server simulator with the two required arguments\
     `java -cp target/solution-1.0-SNAPSHOT.jar com.ass1.server.ServerSimulator <arg1> <arg2>`
5. Open a new terminal and run the client with optional flag arguments\
     `java -cp target/solution-1.0-SNAPSHOT.jar com.ass1.client.Client <flags>`

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
These flags are only valid if a client-cache flag precedes them.

To set the delay time (in milliseconds) use the flags:
`-20`
`-50`\
If none of these flags are given, the default delay is 20ms.

To set which output file to create/write to, use the following flags:\
`-wcc` `--write-client-cache` (writes  `client_cache.txt`)\
`-wsc` `--write-server-cache` (writes  `server_cache.txt`)\
`-wns` `--write-naive-server` (writes  `naive_server.txt`)\
If none of these flags are given, the default output file is `naive_server.txt`.
