package br.com.gabs.rag

import dev.langchain4j.data.segment.TextSegment
import dev.langchain4j.model.embedding.EmbeddingModel
import dev.langchain4j.model.language.LanguageModel
import dev.langchain4j.model.ollama.OllamaEmbeddingModel
import dev.langchain4j.model.ollama.OllamaLanguageModel
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever
import dev.langchain4j.rag.query.Query
import dev.langchain4j.store.embedding.EmbeddingStore
import dev.langchain4j.store.embedding.qdrant.QdrantEmbeddingStore
import rag.PromptBuilder

object Generator {
  private const val ENV_OLLAMA_BASE_URL = "OLLAMA_BASE_URL"
  private const val ENV_EMBEDDING_MODEL = "EMBEDDING_MODEL"
  private const val ENV_LANGUAGE_MODEL = "LANGUAGE_MODEL"
  private const val ENV_QDRANT_HOST = "QDRANT_HOST"
  private const val ENV_QDRANT_PORT = "QDRANT_PORT"
  private const val ENV_COLLECTION_NAME = "QDRANT_COLLECTION"
  private const val ENV_MAX_RESULTS = "MAX_RESULTS"
  private const val ENV_MIN_SCORE = "MIN_SCORE"

  private const val DEFAULT_OLLAMA_BASE_URL = "http://localhost:11434"
  private const val DEFAULT_EMBEDDING_MODEL = "nomic-embed-text"
  private const val DEFAULT_LANGUAGE_MODEL = "mistral:7b-instruct"
  private const val DEFAULT_QDRANT_HOST = "localhost"
  private const val DEFAULT_QDRANT_PORT = 6334
  private const val DEFAULT_COLLECTION_NAME = "rag-tests"
  private const val DEFAULT_MAX_RESULTS = 20
  private const val DEFAULT_MIN_SCORE = 0.5

  private val ollamaBaseUrl: String = System.getenv(ENV_OLLAMA_BASE_URL) ?: DEFAULT_OLLAMA_BASE_URL
  private val embeddingModelName: String = System.getenv(ENV_EMBEDDING_MODEL) ?: DEFAULT_EMBEDDING_MODEL
  private val languageModelName: String = System.getenv(ENV_LANGUAGE_MODEL) ?: DEFAULT_LANGUAGE_MODEL
  private val qdrantHost: String = System.getenv(ENV_QDRANT_HOST) ?: DEFAULT_QDRANT_HOST
  private val qdrantPort: Int = System.getenv(ENV_QDRANT_PORT)?.toIntOrNull() ?: DEFAULT_QDRANT_PORT
  private val collectionName: String = System.getenv(ENV_COLLECTION_NAME) ?: DEFAULT_COLLECTION_NAME
  private val maxResults: Int = System.getenv(ENV_MAX_RESULTS)?.toIntOrNull() ?: DEFAULT_MAX_RESULTS
  private val minScore: Double = System.getenv(ENV_MIN_SCORE)?.toDoubleOrNull() ?: DEFAULT_MIN_SCORE

  private var embeddingModel: EmbeddingModel = createDefaultEmbeddingModel()
  private var languageModel: LanguageModel = createDefaultLanguageModel()
  private var embeddingStore: EmbeddingStore<TextSegment> = createDefaultEmbeddingStore()
  private var contentRetriever = createDefaultContentRetriever()

  private fun createDefaultEmbeddingModel(): EmbeddingModel {
    return OllamaEmbeddingModel.builder()
      .baseUrl(ollamaBaseUrl)
      .modelName(embeddingModelName)
      .build()
  }

  private fun createDefaultLanguageModel(): LanguageModel {
    return OllamaLanguageModel.builder()
      .baseUrl(ollamaBaseUrl)
      .modelName(languageModelName)
      .build()
  }

  private fun createDefaultEmbeddingStore(): EmbeddingStore<TextSegment> {
    return QdrantEmbeddingStore.builder()
      .host(qdrantHost)
      .port(qdrantPort)
      .collectionName(collectionName)
      .build()
  }

  private fun createDefaultContentRetriever(): EmbeddingStoreContentRetriever {
    return EmbeddingStoreContentRetriever.builder()
      .embeddingStore(embeddingStore)
      .maxResults(maxResults)
      .minScore(minScore)
      .embeddingModel(embeddingModel)
      .build()
  }

  fun setEmbeddingModel(model: EmbeddingModel) {
    this.embeddingModel = model
    this.contentRetriever = EmbeddingStoreContentRetriever.builder()
      .embeddingStore(embeddingStore)
      .maxResults(maxResults)
      .minScore(minScore)
      .embeddingModel(embeddingModel)
      .build()
  }

  fun setLanguageModel(model: LanguageModel) {
    this.languageModel = model
  }

  fun setEmbeddingStore(store: EmbeddingStore<TextSegment>) {
    this.embeddingStore = store
    this.contentRetriever = EmbeddingStoreContentRetriever.builder()
      .embeddingStore(embeddingStore)
      .maxResults(maxResults)
      .minScore(minScore)
      .embeddingModel(embeddingModel)
      .build()
  }

  fun configureOllama(
    baseUrl: String = ollamaBaseUrl,
    embeddingModelName: String = this.embeddingModelName,
    languageModelName: String = this.languageModelName
  ) {
    this.embeddingModel = OllamaEmbeddingModel.builder()
      .baseUrl(baseUrl)
      .modelName(embeddingModelName)
      .build()

    this.languageModel = OllamaLanguageModel.builder()
      .baseUrl(baseUrl)
      .modelName(languageModelName)
      .build()

    this.contentRetriever = EmbeddingStoreContentRetriever.builder()
      .embeddingStore(embeddingStore)
      .maxResults(maxResults)
      .minScore(minScore)
      .embeddingModel(embeddingModel)
      .build()
  }

  fun logCurrentConfig() {
    println("Current Generator Configuration:")
    println("Ollama Base URL: $ollamaBaseUrl")
    println("Embedding Model: $embeddingModelName")
    println("Language Model: $languageModelName")
    println("Qdrant Host: $qdrantHost")
    println("Qdrant Port: $qdrantPort")
    println("Collection Name: $collectionName")
    println("Max Results: $maxResults")
    println("Min Score: $minScore")
  }

  fun generateBdd(instruction: String, destinationPath: String): String {
    val query = Query.from(instruction)
    val results = contentRetriever.retrieve(query)

    if (results.isEmpty()) {
      println("⚠️ No relevant context found for: '$instruction'")
      return "// Not enough context to generate test\n"
    }

    val fullContext = results.joinToString("\n") { it.textSegment().text() }
    val contextBlocks = fullContext.chunked(1000)

    val responses = contextBlocks.mapIndexed { index, block ->
      val prompt = PromptBuilder.build(block, instruction)

      println("⏳ Generating block $index (${block.length} characters)")
      val startTime = System.currentTimeMillis()

      runCatching {
        val response = languageModel.generate(prompt)
        val duration = System.currentTimeMillis() - startTime
        println("✅ Block $index generated in ${duration}ms")
        response.content().trim()
      }.onFailure {
        when (it) {
          is RuntimeException -> println("⏳ Timeout on block $index: ${it.message}")
        }
      }.getOrElse {
        println("❌ Error on block $index: ${it.message}")
      }
    }

    val finalResponse = responses.joinToString("\n\n").trim()
    val sanitizedFileName = instruction.replace("[^a-zA-Z0-9_\\-]".toRegex(), "_")
    CodeWriter.save("${sanitizedFileName}.md", finalResponse, destinationPath)

    println("✅ Test saved as '${sanitizedFileName}.md' in '$destinationPath'")
    return finalResponse
  }
}

