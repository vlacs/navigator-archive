# aspire

## Developer Workflow
### First
```bash
$ lein repl
```
...or start your favorite nREPL.

### Second
We're using tools.namespace, so to start and restart Aspire safely in your REPL, say:
```clojure
(reset)
```
You'll want to ```(reset)``` any time you change your clj code, and your changes will
take effect instantly.

### Third
In another terminal, run this to rebuild your cljs automatically any time you
change a source file:
```bash
$ lein cljsbuild auto
```
Any time you change your cljs, you need to reload your browser page to pick up
the new compiled .js.

### Fourth
Open up user.clj. This is the place to add all the dev/debugging tools. It's
evaluated automatically every time you open a REPL, and you're already in its
namespace, so any tooling you put in user.clj is always available in your REPL
without any extra effort.

user.clj is NOT included in a compiled JAR or .js file.

See http://thinkrelevance.com/blog/2013/06/04/clojure-workflow-reloaded for
background on this workflow.

## Usage

There's nothing much here, yet. This will tell you more:
```clojure
./run dev --help
```

## License

Copyright © 2013 VLACS http://vlacs.org

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
