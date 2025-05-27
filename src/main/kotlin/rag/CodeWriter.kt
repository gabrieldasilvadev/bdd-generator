package br.com.gabs.rag

import java.io.File

object CodeWriter {
  private const val ENV_OUTPUT_PATH = "BDD_GENERATOR_OUTPUT_PATH"

  fun save(fileName: String, content: String, sourcePath: String) {
    val envPath = System.getenv(ENV_OUTPUT_PATH)

    val path = if (envPath != null) {
      println("Using path from environment variable $ENV_OUTPUT_PATH: $envPath")
      envPath
    } else if (File(sourcePath).isAbsolute) {
      sourcePath
    } else {
      sourcePath
    }

    val directory = File(path)
    if (!directory.exists()) {
      directory.mkdirs()
    }

    val file = File(directory, fileName)
    file.writeText(content)

    println("File saved in: ${file.absolutePath}")
  }
}

