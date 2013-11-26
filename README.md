# aspire

## Developer Workflow
### First
```bash
lein repl
```
...or start your favorite nREPL.

### Second
We're using tools.namespace, so to start and restart Aspire safely in your REPL, say:
```clojure
(reset)
```

### Third
In another terminal, run this to rebuild your cljs automatically any time you
change a source file:
```bash
lein cljsbuild auto
```

### Fourth
Any time you change your cljs, you need to reload your browser page to pick up
the new compiled .js.

See these:
* http://thinkrelevance.com/blog/2013/06/04/clojure-workflow-reloaded
* http://swannodette.github.io/2013/10/27/the-essence-of-clojurescript/

## Usage

There's nothing much here, yet. This will tell you more:
```clojure
./run dev --help
```

## License

Copyright © 2013 VLACS http://vlacs.org

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
