package br.com.gabs

import br.com.gabs.rag.Generator
import br.com.gabs.rag.Indexer
import io.javalin.Javalin
import java.nio.file.Paths

private const val DEFAULT_TEST_GENERATED_SOURCE_PATH = "src/main/resources/generated"

fun main() {
  val app = Javalin.create().start(7070)

  app.post("/index") { ctx ->
    val path = ctx.formParam("path")
    if (path != null) {
      Indexer.indexProject(path)
      ctx.result("Indexing completed.")
    } else {
      ctx.status(400).result("Path parameter is required.")
    }
  }

  app.post("/generate") { ctx ->
    val instruction = ctx.formParam("instruction")
    val destinationPath = ctx.formParam("destination_path")
    if (instruction != null) {
      try {
        val result = Generator.generateBdd(
          instruction = instruction,
          destinationPath = destinationPath ?: DEFAULT_TEST_GENERATED_SOURCE_PATH
        )
        ctx.result(result)
      } catch (e: RuntimeException) {
        println("Error when generating test: ${e.message}")
        ctx.status(500).result("Error when generating test: ${e.message}")
      }
    } else {
      ctx.status(400).result("instruction parameter is required.")
    }
  }
}
