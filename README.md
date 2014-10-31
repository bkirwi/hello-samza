hello-samza (and coast!)
===========

Hello Samza is a starter project for [Apache Samza](http://samza.incubator.apache.org/) (Incubating) jobs.

Please see [Hello Samza](http://samza.incubator.apache.org/startup/hello-samza/0.7.0/) to get started.

This also relies on the [Coast](https://github.com/bkirwi/coast) project; if you're trying this out, you'll want to check out that source as well.

### Running the Coast Jobs

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
Hello Samza is developed as part of the Apache Samza project. Please direct questions, improvements and
bug fixes there.  Questions about Hello Samza are welcome on the dev list (details on the main
site above) and the Samza JIRA has a hello-samza component for filing tickets.

### Using Vagrant

If you'd like to use Vagrant to get up and running, follow these instructions.

1) Install Vagrant [http://www.vagrantup.com/](http://www.vagrantup.com/)  
2) Install Virtual Box [https://www.virtualbox.org/](https://www.virtualbox.org/)  

Then once that is done (or if done already) clone this repository and boot the virtual machine up.
 
    cd hello-samza
    vagrant up  

This will take ~ 10-15 minutes to install Kafka, Hadoop/YARN, Samza, configure everything together and launch the jobs.

Once the VM is launched and you are back at a command prompt go into the virtual machine and see whats running.

    vagrant ssh
    cd /vagrant

The wikipedia-feed Samza job that is running is consuming a feed of real-time edits from Wikipedia, and producing them to a Kafka topic called "wikipedia-raw".  You can view this in real-time by using the Kafka console consumer to view the topic.

    deploy/kafka/bin/kafka-console-consumer.sh --zookeeper localhost:2181 --topic wikipedia-raw

The wikipedia-parser Samza job is then parsing the messages in wikipedia-raw, and extracting information about the size of the edit, who made the change, etc. It outputs these counts to the wikipedia-edits topic.

    deploy/kafka/bin/kafka-console-consumer.sh --zookeeper localhost:2181 --topic wikipedia-edits

The wikipedia-stats Samza job reads messages from the wikipedia-edits topic, and calculates counts, every ten seconds, for all edits that were made during that window. It outputs these counts to the wikipedia-stats topic.

    deploy/kafka/bin/kafka-console-consumer.sh --zookeeper localhost:2181 --topic wikipedia-stats

You can view the Samza jobs running in the YARN UI http://192.168.80.20:8088/cluster/apps too.

To see how this was setup and works look at `vagrant/bootstrap.sh` and [Hello Samza](http://samza.incubator.apache.org/).
