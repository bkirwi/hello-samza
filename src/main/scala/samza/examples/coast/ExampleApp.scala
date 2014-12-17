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

import java.io.{FileOutputStream, File}
import java.net.URI
import java.util.Properties

import com.google.common.base.Charsets
import com.google.common.io.Files
import com.monovore.coast
import com.monovore.coast.dot.Dot
import com.monovore.coast.model.Graph
import com.monovore.coast.samza.ConfigGenerator
import org.apache.samza.config.Config
import org.apache.samza.config.factories.PropertiesConfigFactory

import scala.collection.JavaConverters._

/**
 * Given a Flow and a base config, generates the necessary Samza config and
 * writes it to a target directory.
 *
 * If this works well, it should be moved into coast-samza directly.
 */
abstract class ExampleApp(builder: Config => ConfigGenerator) {

  def graph: Graph

  def main(args: Array[String]): Unit = {

    args.toList match {
      case "dot" :: Nil => {
        println(Dot.describe(graph))
      }
      case "dot" :: fileName :: Nil => {
        Files.asCharSink(new File(fileName), Charsets.UTF_8).write(Dot.describe(graph))
      }
      case "gen-config" :: basePath :: targetPath :: Nil => {

        val baseConfigURI = new URI(args(0))
        val targetDirectory = new File(args(1))
        val configFactory = new PropertiesConfigFactory

        val baseConfig = configFactory.getConfig(baseConfigURI)
        val configs = builder(baseConfig).configure(graph)

        generateConfigFiles(targetDirectory, configs)
      }
      case Nil => println("No arguments!")
      case unknown => println("Unrecognized arguments: " + unknown.mkString(" "))
    }
  }

  private[this] def generateConfigFiles(directory: File, configs: Map[String, Config]): Unit = {

    configs.foreach { case (name, config) =>

      val properties = new Properties()
      val propertiesFile = new File(directory, s"$name.properties")

      config.asScala.foreach { case (k, v) => properties.setProperty(k, v) }

      val fos = new FileOutputStream(propertiesFile)
      properties.store(fos, null)
      fos.close()
    }
  }
}
