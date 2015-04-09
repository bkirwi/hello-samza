hello-samza (and coast!)
===========

Hello Samza is a starter project for [Apache Samza](http://samza.apache.org/) jobs.

Please see [Hello Samza](http://samza.apache.org/startup/hello-samza/0.8/) to get started.

### Running the Coast Jobs

This project also includes examples for the [Coast](https://github.com/bkirwi/coast)
project, a high-level streaming framework that compiles to Samza. To build this
code, you'll want to check out that source and run `sbt publish`, so the jars
are available in your local Maven repository.

The new example jobs are here:

- [WikipediaWordCount.scala](/src/main/scala/samza/examples/coast/WikipediaWordCount.scala)
- [WikipediaStats.scala](/src/main/scala/samza/examples/coast/WikipediaStats.scala)

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
    
### Pull requests and questions

[Hello Samza](http://samza.apache.org/startup/hello-samza/0.8/) is developed as part of the [Apache Samza](http://samza.apache.org) project. Please direct questions, improvements and bug fixes there.  Questions about [Hello Samza](http://samza.apache.org/startup/hello-samza/0.8/) are welcome on the [dev list](http://samza.apache.org/community/mailing-lists.html) and the [Samza JIRA](https://issues.apache.org/jira/browse/SAMZA) has a hello-samza component for filing tickets.
