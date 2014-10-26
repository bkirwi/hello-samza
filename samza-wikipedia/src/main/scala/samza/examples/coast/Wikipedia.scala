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

object Wikipedia {

  val Edits = coast.Name[String, String]("wikipedia-raw")

  val WordCount = coast.Name[String, String]("wikipedia-wordcount")

  val Statistics = coast.Name[String, String]("wikipedia-statistics")
}
