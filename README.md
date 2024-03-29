# navigator [Build Status](https://codeship.io/projects/d10dbc00-d464-0131-d880-3ea79a4fc40b/status) [![Coverage](http://img.shields.io/coveralls/vlacs/navigator/master.svg)](http://coveralls.io/r/vlacs/navigator)

<img src="doc/Tapley_NPS.png" alt="Agnes Tapley: Navigating" width="200px" />

[Agnes Tapley, navigating.](http://commons.wikimedia.org/wiki/File:Tapley_NPS.jpg) Navigator is part of VLACS' competencies mapping system.

## Project Environment Set-Up
The following instructions provide the steps needed to get the Navigator project running on *UX Environments.

######NOTE: Windows Users: Modifications to install an Navigator development environment using Eclipse can be found in [win-eclipse.md](https://github.com/vlacs/navigator/blob/master/win-eclipse.md).  Steps which require modification below are indicated with following symbol<sup>(M$)</sup>

###Prerequisites
####Core:
- JDK 1.7: http://www.oracle.com/java
- PostgresSQL Server: http://www.postgresql.org/
- Leiningen Runtime: http://leiningen.org/

####<b>TODO</b> - Provide other Linux/Unix prerequisites -
It is assumed that your environment has VI or a comparable Text Editor installed

## Running Tests
You can build the necessary dependencies and run the tests all with one command.

```$ sh test.sh```

### 1. Setting up the Test Datastore
Set up a PostgreSQL DB for your dev/testing instance of Navigator.

<b>TODO</b>: Provide Installation Instructions for Unix / Creating a new Database / Creating a new Role


### 2. Set up the Local Development Environment
Now run the following command<sup>(M$)</sup>:
```bash
$ bash dev-set-up.sh
```

This shell script will create a local directory on the target machine and will copy the following configurations files from the projects '/sample-confs' directory.
Once copied, a VI editor will be launched, allowing you to edit the following files:
- conf-sql-db.edn
- conf-web.edn

These files should be modified to reflect your local development environment configurations for your PostgresSQL (separate install, see above),
and Jetty Web Server (Runtime included in project).

### 3. Populating the Test Datastore with Data
Now that the project bones are in place, it is time to configure the test data schema and load it with some test data.

Run the following command to install the database schema that has be pre-defined in 'schema.edn' at the navigator project root<sup>(M$)</sup>.
```
$lein run --config-path ~/.navigator/ --init-sql
```

You can verify that the operation was a success by viewing the tables defined in the PostgresSQL database that you are using.  It should be similiar to:
<table>
    <tr>
      <th class="ReportTableHeaderCell" width="33.3333333333333%">Table</th>
      <th class="ReportTableHeaderCell" width="33.3333333333333%">Owner</th>
      <th class="ReportTableHeaderCell" width="33.3333333333333%">Comment</th>
    </tr>
    <tr class="ReportDetailsEvenDataRow">
      <td class="ReportTableValueCell">comp</td>
      <td class="ReportTableValueCell">vlacs</td>
      <td class="ReportTableValueCell"> </td>
    </tr>
    <tr class="ReportDetailsOddDataRow">
      <td class="ReportTableValueCell">comp2comp_tag</td>
      <td class="ReportTableValueCell">vlacs</td>
      <td class="ReportTableValueCell"> </td>
    </tr>
    <tr class="ReportDetailsEvenDataRow">
      <td class="ReportTableValueCell">comp2perf_asmt</td>
      <td class="ReportTableValueCell">vlacs</td>
      <td class="ReportTableValueCell"> </td>
    </tr>
    <tr class="ReportDetailsOddDataRow">
      <td class="ReportTableValueCell">comp_tag</td>
      <td class="ReportTableValueCell">vlacs</td>
      <td class="ReportTableValueCell"> </td>
    </tr>
    <tr class="ReportDetailsEvenDataRow">
      <td class="ReportTableValueCell">comp_tag2parent</td>
      <td class="ReportTableValueCell">vlacs</td>
      <td class="ReportTableValueCell"> </td>
    </tr>
    <tr class="ReportDetailsOddDataRow">
      <td class="ReportTableValueCell">comp_tag_disp_ctx</td>
      <td class="ReportTableValueCell">vlacs</td>
      <td class="ReportTableValueCell"> </td>
    </tr>
    <tr class="ReportDetailsEvenDataRow">
      <td class="ReportTableValueCell">comp_tag_disp_ctx2comp_tag</td>
      <td class="ReportTableValueCell">vlacs</td>
      <td class="ReportTableValueCell"> </td>
    </tr>
    <tr class="ReportDetailsOddDataRow">
      <td class="ReportTableValueCell">config</td>
      <td class="ReportTableValueCell">vlacs</td>
      <td class="ReportTableValueCell"> </td>
    </tr>
    <tr class="ReportDetailsEvenDataRow">
      <td class="ReportTableValueCell">perf_asmt</td>
      <td class="ReportTableValueCell">vlacs</td>
      <td class="ReportTableValueCell"> </td>
    </tr>
    <tr class="ReportDetailsOddDataRow">
      <td class="ReportTableValueCell">user2comp</td>
      <td class="ReportTableValueCell">vlacs</td>
      <td class="ReportTableValueCell"> </td>
    </tr>
</table>


Now that the table has been created it is time to load it with some dummy data.  Now load and execute the 'test.sql' file found in the root of the Navigator project<sup>(M$)</sup>.
```bash
$ cat test.sql | psql yourdb
```

<b>NOTE</b>: Make sure you replace ```yourdb``` with the Datastore that you created and configured in the prior steps.

<b>NOTE</b>:  The test.sql script is not set up to run multiple times.  If you need to refresh your test data, truncate the contents of the following tables prior to re-running the script:
- comp
- comp_tag
- comp2comp_tag
- comp_tag2parent


### 4. Running the Navigator Project
Start a REPL(ReadEvalPrintLoop)<sup>(M$)</sup>
```bash
$ lein repl
```

<b>NOTE</b>: The Navigator project is using tools.namespace, so to start and restart Navigator safely in your REPL, *stay in the* ```user``` *namespace*

Type the following command to 'start' the environment.
```clojure
(reset)
```

The ```(reset)``` command is a convenience function which will initialize the connections to your PostgresSQL and Jetty instances.
You'll want to ```(reset)``` any time you change your clj code to make sure that your latest changes are loaded and evaluated.


### 5. Verify the environment is running
Using your Web Browser go to the following URL's:
- http://localhost:port/
- http://localhost:port/admin


<b>NOTE</b>: Dont forget to replace ```localhost:port``` with the real server/port of your configured environment


### 6. Rejoice
At this point you should have a development environment running the Navigator project, so get to work and enjoy!


## Customizing your Development Environment
Open up dev/user.clj. This file is the place to add all the dev/debugging tools that you wish to customize your development environment with.
It's evaluated automatically every time you open a REPL, and you're already in its namespace, so any tooling you put in user.clj
 is always available in your REPL without any extra effort.

<b>NOTE</b>: user.clj is <b>NOT</b> included in a compiled JAR or .js file.

See http://thinkrelevance.com/blog/2013/06/04/clojure-workflow-reloaded for
background on this workflow.


## Getting Help

Run the following command to see all of the current usage documentation available<sup>(M$)</sup>.
This is a work in progress and will be fully documented at a later date.
```clojure
./run dev --help
```

## License

Copyright © 2014 VLACS http://vlacs.org

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
