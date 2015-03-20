hello-samza (and coast!)
===========

Hello Samza is a starter project for [Apache Samza](http://samza.apache.org/) jobs.

Please see [Hello Samza](http://samza.apache.org/startup/hello-samza/0.8/) to get started.

This also relies on the [Coast](https://github.com/bkirwi/coast) project; if you're trying this out, you'll want to check out that source as well.

### Running the Coast Jobs

This repo has been updated with a couple example jobs for the `coast` streaming
framework:

- [WikipediaWordCount.scala](/samza-wikipedia/src/main/scala/samza/examples/coast/WikipediaWordCount.scala)
- [WikipediaStats.scala](/samza-wikipedia/src/main/scala/samza/examples/coast/WikipediaStats.scala)

To compile this source, you'll need to make `coast` visible to maven. The
project is already configured to publish to the local maven repo, so checking
out that source and running `sbt publish` should do the trick.

Coast's Samza integration works by generating config, so you'll need somewhere
to put them:

    mkdir -p conf/coast

Both jobs expect their input in the `wikipedia-raw` topic, so run the existing
`wikipedia-feed` job:

    deploy/samza/bin/run-job.sh --config-factory=org.apache.samza.config.factories.PropertiesConfigFactory --config-path=file://$PWD/deploy/samza/config/wikipedia-feed.properties

To run the wordcount job:

    # This generates two config files, one for each job stage
    deploy/samza/bin/run-class.sh samza.examples.coast.WikipediaWordCount deploy/samza/config/coast-base.properties conf/coast

    # Both jobs should be deployed from config
    deploy/samza/bin/run-job.sh --config-factory=org.apache.samza.config.factories.PropertiesConfigFactory --config-path=file://$PWD/conf/coast/wikipedia-wordcount-words.properties
    deploy/samza/bin/run-job.sh --config-factory=org.apache.samza.config.factories.PropertiesConfigFactory --config-path=file://$PWD/conf/coast/wikipedia-wordcount.properties

    # Output should quickly become visible in wikipedia-wordcount
    deploy/kafka/bin/kafka-console-consumer.sh --zookeeper localhost:2181 --topic wikipedia-wordcount

Likewise, to run the statistics-calculating job:

    # This job only has a single stage
    deploy/samza/bin/run-class.sh samza.examples.coast.WikipediaStats deploy/samza/config/coast-base.properties conf/coast
    deploy/samza/bin/run-job.sh --config-factory=org.apache.samza.config.factories.PropertiesConfigFactory --config-path=file://$PWD/conf/coast/wikipedia-statistics.properties

    # Events are buffered in small batches, but output should appear after a few seconds
    deploy/kafka/bin/kafka-console-consumer.sh --zookeeper localhost:2181 --topic wikipedia-statistics
    
### Custom Samza    

By default, Hello Samza uses a recent release of Samza from a Maven repository. If you want to use a custom
version of Samza, you can publish it to your local Maven repository in `$HOME/.m2` by running the following
in the Samza repository:

    ./gradlew publishToMavenLocal

You can then use that version in Hello Samza by specifying the `samza.version` property when building
Hello Samza, for example:

    mvn package -Dsamza.version=0.8.0-SNAPSHOT

### Pull requests and questions

[Hello Samza](http://samza.apache.org/startup/hello-samza/0.8/) is developed as part of the [Apache Samza](http://samza.apache.org) project. Please direct questions, improvements and bug fixes there.  Questions about [Hello Samza](http://samza.apache.org/startup/hello-samza/0.8/) are welcome on the [dev list](http://samza.apache.org/community/mailing-lists.html) and the [Samza JIRA](https://issues.apache.org/jira/browse/SAMZA) has a hello-samza component for filing tickets.
