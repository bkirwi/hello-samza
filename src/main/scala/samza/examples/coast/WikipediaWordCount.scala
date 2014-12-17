/*
 * Copyright 2014 Ben Kirwin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package samza.examples.coast

import com.monovore.coast
import com.monovore.coast.flow
import com.monovore.coast.samza.ConfigGenerator
import org.apache.samza.config.Config
import samza.examples.wikipedia.system.WikipediaFeed.WikipediaFeedEvent
import samza.examples.wikipedia.task.WikipediaParserStreamTask

import scala.util.Try

object WikipediaWordCount extends ExampleApp(coast.samza.Safe) {

  import coast.wire.pretty._

  val graph = for {

    // split the edit summary up into words and regroup
    counts <- flow.stream("wikipedia-wordcount-words") {

      flow.source(Wikipedia.Edits)
        .flatMap { json => Option(WikipediaFeedEvent.fromJson(json).getRawEvent).toSeq }
        .flatMap { event =>
          Try { WikipediaParserStreamTask.parse(event).get("summary").asInstanceOf[String] }
            .filter { _ != null}
            .toOption.toSeq
        }
        .flatMap { _.split("\\s+") }
        .map { _ -> 1 }
        .groupByKey
    }

    // sum up the counts for each word and pretty-print
    _ <- flow.sink(Wikipedia.WordCount) {

      counts.sum
        .updates
        .withKeys.map { k => v => s"$k: $v" }
    }

  } yield ()
}
