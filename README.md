hello-samza (and coast!)
===========

Hello Samza is a starter project for [Apache Samza](http://samza.apache.org/) jobs.

Please see [Hello Samza](http://samza.apache.org/startup/hello-samza/0.8/) to get started.

### Running the Coast Jobs

This project also includes examples for the [Coast](https://github.com/bkirwi/coast)
project, a high-level streaming framework that compiles to Samza.

The new example jobs are here:

- [WikipediaWordCount.scala](/src/main/scala/samza/examples/coast/WikipediaWordCount.scala)
- [WikipediaStats.scala](/src/main/scala/samza/examples/coast/WikipediaStats.scala)

Both jobs expect their input in the `wikipedia-raw` topic, so run the existing
`wikipedia-feed` job:

    deploy/samza/bin/run-job.sh --config-factory=org.apache.samza.config.factories.PropertiesConfigFactory --config-path=file://$PWD/deploy/samza/config/wikipedia-feed.properties

The `coast` jobs are started with `run-class.sh`:

    deploy/samza/bin/run-class.sh samza.examples.coast.WikipediaWordCount run --config-file deploy/samza/config/coast-base.properties
    deploy/samza/bin/run-class.sh samza.examples.coast.WikipediaStats run --config-file deploy/samza/config/coast-base.properties

To have a look at the output, you can use the standard console consumer:

    deploy/kafka/bin/kafka-console-consumer.sh --zookeeper localhost:2181 --topic wikipedia-wordcount
    
### Pull requests and questions

[Hello Samza](http://samza.apache.org/startup/hello-samza/0.8/) is developed as part of the [Apache Samza](http://samza.apache.org) project. Please direct questions, improvements and bug fixes there.  Questions about [Hello Samza](http://samza.apache.org/startup/hello-samza/0.8/) are welcome on the [dev list](http://samza.apache.org/community/mailing-lists.html) and the [Samza JIRA](https://issues.apache.org/jira/browse/SAMZA) has a hello-samza component for filing tickets.
