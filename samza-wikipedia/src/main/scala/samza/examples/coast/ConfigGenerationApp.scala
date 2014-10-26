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

import com.monovore.coast
import org.apache.samza.config.factories.PropertiesConfigFactory

import scala.collection.JavaConverters._

/**
 * Given a Flow and a base config, generates the necessary Samza config and
 * writes it to a target directory.
 *
 * If this works well, it should be moved into coast-samza directly.
 */
trait ConfigGenerationApp {

  def flow: coast.Flow[Unit]

  def main(args: Array[String]): Unit = {

    val baseConfigURI = new URI(args(0))
    val targetConfigPath = new File(args(1))

    val configFactory = new PropertiesConfigFactory

    val baseConfig = configFactory.getConfig(baseConfigURI)

    val configFiles = coast.samza.configureFlow(flow)(
      system = "kafka",
      baseConfig = baseConfig
    )

    configFiles.foreach { case (name, config) =>

      val properties = new Properties()
      val propertiesFile = new File(targetConfigPath, s"$name.properties")

      config.asScala.foreach { case (k, v) => properties.setProperty(k, v) }

      val fos = new FileOutputStream(propertiesFile)
      properties.store(fos, null)
      fos.close()
    }
  }
}
