package br.com.gabs.rag

import dev.langchain4j.data.document.Document
import dev.langchain4j.data.document.DocumentSplitter
import dev.langchain4j.data.document.splitter.DocumentSplitters
import dev.langchain4j.data.embedding.Embedding
import dev.langchain4j.data.segment.TextSegment
import dev.langchain4j.model.embedding.EmbeddingModel
import dev.langchain4j.model.ollama.OllamaEmbeddingModel
import dev.langchain4j.store.embedding.EmbeddingStore
import dev.langchain4j.store.embedding.qdrant.QdrantEmbeddingStore
import java.io.File

object Indexer {
  private const val ENV_OLLAMA_BASE_URL = "OLLAMA_BASE_URL"
  private const val ENV_EMBEDDING_MODEL = "EMBEDDING_MODEL"
  private const val ENV_QDRANT_HOST = "QDRANT_HOST"
  private const val ENV_QDRANT_PORT = "QDRANT_PORT"
  private const val ENV_COLLECTION_NAME = "QDRANT_COLLECTION"
  private const val ENV_CHUNK_SIZE = "CHUNK_SIZE"
  private const val ENV_CHUNK_OVERLAP = "CHUNK_OVERLAP"

  private const val DEFAULT_OLLAMA_BASE_URL = "http://localhost:11434"
  private const val DEFAULT_EMBEDDING_MODEL = "nomic-embed-text"
  private const val DEFAULT_QDRANT_HOST = "localhost"
  private const val DEFAULT_QDRANT_PORT = 6334
  private const val DEFAULT_COLLECTION_NAME = "rag-tests"
  private const val DEFAULT_CHUNK_SIZE = 500
  private const val DEFAULT_CHUNK_OVERLAP = 50

  private val ollamaBaseUrl: String = System.getenv(ENV_OLLAMA_BASE_URL) ?: DEFAULT_OLLAMA_BASE_URL
  private val embeddingModelName: String = System.getenv(ENV_EMBEDDING_MODEL) ?: DEFAULT_EMBEDDING_MODEL
  private val qdrantHost: String = System.getenv(ENV_QDRANT_HOST) ?: DEFAULT_QDRANT_HOST
  private val qdrantPort: Int = System.getenv(ENV_QDRANT_PORT)?.toIntOrNull() ?: DEFAULT_QDRANT_PORT
  private val collectionName: String = System.getenv(ENV_COLLECTION_NAME) ?: DEFAULT_COLLECTION_NAME
  private val chunkSize: Int = System.getenv(ENV_CHUNK_SIZE)?.toIntOrNull() ?: DEFAULT_CHUNK_SIZE
  private val chunkOverlap: Int = System.getenv(ENV_CHUNK_OVERLAP)?.toIntOrNull() ?: DEFAULT_CHUNK_OVERLAP

  private var splitter: DocumentSplitter = createDefaultSplitter()
  private var embeddingModel: EmbeddingModel = createDefaultEmbeddingModel()
  private var store: EmbeddingStore<TextSegment> = createDefaultEmbeddingStore()

  private fun createDefaultSplitter(): DocumentSplitter {
    return DocumentSplitters.recursive(chunkSize, chunkOverlap)
  }

  private fun createDefaultEmbeddingModel(): EmbeddingModel {
    return OllamaEmbeddingModel.builder()
      .baseUrl(ollamaBaseUrl)
      .modelName(embeddingModelName)
      .build()
  }

  private fun createDefaultEmbeddingStore(): EmbeddingStore<TextSegment> {
    return QdrantEmbeddingStore.builder()
      .host(qdrantHost)
      .port(qdrantPort)
      .collectionName(collectionName)
      .build()
  }

  fun setSplitter(splitter: DocumentSplitter) {
    this.splitter = splitter
  }

  fun setEmbeddingModel(model: EmbeddingModel) {
    this.embeddingModel = model
  }

  fun setEmbeddingStore(store: EmbeddingStore<TextSegment>) {
    this.store = store
  }

  fun configureOllama(
    baseUrl: String = ollamaBaseUrl,
    embeddingModelName: String = this.embeddingModelName
  ) {
    this.embeddingModel = OllamaEmbeddingModel.builder()
      .baseUrl(baseUrl)
      .modelName(embeddingModelName)
      .build()
  }

  fun configureChunking(chunkSize: Int = this.chunkSize, overlap: Int = this.chunkOverlap) {
    this.splitter = DocumentSplitters.recursive(chunkSize, overlap)
  }

  fun logCurrentConfig() {
    println("Current Indexer Configuration:")
    println("Ollama Base URL: $ollamaBaseUrl")
    println("Embedding Model: $embeddingModelName")
    println("Qdrant Host: $qdrantHost")
    println("Qdrant Port: $qdrantPort")
    println("Collection Name: $collectionName")
    println("Chunk Size: $chunkSize")
    println("Chunk Overlap: $chunkOverlap")
  }

  fun indexProject(path: String) {
    val files = File(path).walkTopDown().filter { it.extension in listOf("java", "kotlin", "kt") }

    files.forEach { file ->
      val text = file.readText()
      val doc = Document.from(text)
      val chunks = splitter.split(doc)

      chunks.forEach { chunk ->
        val embeddingResponse = embeddingModel.embed(chunk.text())
        val vetor = embeddingResponse.content().vector()
        store.add(Embedding(vetor), chunk)
        println("Indexed chunk: ${chunk.text()}")
      }
    }
  }
}

