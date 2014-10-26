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
import samza.examples.wikipedia.system.WikipediaFeed.WikipediaFeedEvent
import samza.examples.wikipedia.task.WikipediaParserStreamTask

import collection.JavaConverters._

import scala.util.Try

object WikipediaStats extends ConfigGenerationApp {

  import coast.format.pretty._

  case class Stats(edits: Int = 0, bytesAdded: Int = 0, titles: Set[String] = Set.empty[String])

  implicit val statsFormat = coast.format.javaSerialization.formatFor[(Stats, Int)]

  // roll up stats across all events, like the existing parser / stats job
  // coast does not yet have clocks, so this windows by number of messages instead of time
  override def flow: coast.Flow[Unit] = coast.sink(Wikipedia.Statistics) {

    coast.source(Wikipedia.Edits)
      .map { json => WikipediaFeedEvent.fromJson(json) }
      .map { event =>

        val parsed = Option(event.getRawEvent)
          .flatMap { raw => Try { WikipediaParserStreamTask.parse(raw) }.toOption }
          .map { _.asScala }
          .getOrElse(Map.empty[String, Any])

        Stats(
          edits = 1,
          bytesAdded = parsed.getOrElse("diff-bytes", 0).asInstanceOf[Int],
          titles = Set(parsed.getOrElse("title", "<unknown>").asInstanceOf[String])
        )
      }
      .windowed(size = 5)(Stats()) { (previous, next) =>

          Stats(
            edits = previous.edits + next.edits,
            bytesAdded = previous.bytesAdded + next.bytesAdded,
            titles = previous.titles ++ next.titles
          )
        }
      .map { stats =>
        s"edit window size: ${stats.edits}; total bytes: ${stats.bytesAdded}; unique titles: ${stats.titles.size}"
      }
  }
}
