# readme

## Command line flags

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


To set which output file to create/write to, use the flags:\
Write  client_cache.txt: `-wcc` `--write-client-cache`

Write  server_cache.txt: `-wsc` `--write-server-cache`

Write  naive_server.txt: `-wns` `--write-naive-server`\
If none of these flags are given, the default output file is `naive_server.txt`.
\


### `ServerSimulator.java`

Takes in two boolean arguments at the command line.
The first argument enables server cache:\
`true` - server cache enabled\
`false` - server cache disabled\
The second argument enables the OLDEST cache method:\
`true` - OLDEST method used\
`false` - FIFO method used
