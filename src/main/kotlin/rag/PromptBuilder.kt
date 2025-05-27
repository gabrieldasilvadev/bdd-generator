package rag

object PromptBuilder {
  fun build(context: String, instruction: String): String {
        return """
          Voce é um assistente de IA especializado em analisar codigo e montar cenarios de teste.
          Siga o modelo do BDD (Behavior Driven Development) para gerar os testes.
          O contexto é o seguinte:
          $context
          A tarefa é a seguinte:
          $instruction
          Nao precisa gerar testess de unidade, apenas cenarios de teste.
          Os cenarios devem ser escritos em Gherkin.
          Os cenarios devem ser escritos em Portugues do Brasil.
          Novamente, nao precisa gerar testes de unidade, apenas cenarios de teste.
            """.trimIndent().trim()
      }
    }