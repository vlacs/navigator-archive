# aspire

## Developer Workflow
### First
Set up a PostgreSQL DB for your dev/testing instance of Aspire.
```bash
$ bash dev-set-up.sh
```
Then edit all the files in ~/.aspire/ to ensure they're correct for your
testing/dev setup.

### Second
Update src/brepl/aspire/brepl.cljs to make sure your URI is correct, and that
you're not fighting with another dev on the same system for that port.

### Third
```bash
$ lein repl
```
...or start your editor and connect to your favorite nREPL.

### Fourth
We're using tools.namespace, so to start and restart Aspire safely in your REPL, *stay in the* ```user``` *namespace* and say:
```clojure
(reset)
```
You'll want to ```(reset)``` any time you change your clj code, and your changes will
take effect instantly.

### Fifth
In another terminal, run this to rebuild your cljs automatically any time you
change a source file:
```bash
$ lein cljsbuild auto
```
Any time you change your cljs, you need to reload your browser page to pick up
the new compiled .js.

### Sixth
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

Copyright © 2014 VLACS http://vlacs.org

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
