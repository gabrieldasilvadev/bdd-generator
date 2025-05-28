# BDD Generator

Este projeto é um gerador automático de cenários de teste BDD (Behavior Driven Development) utilizando modelos de linguagem natural (LLM) e embeddings. Ele indexa o código-fonte de um projeto e, a partir de instruções, gera arquivos `.md` com cenários de teste em Gherkin, em português do Brasil.

## Pré-requisitos

- Java 17+ (JDK)
- Kotlin
- Gradle
- [Ollama](https://ollama.com/) rodando localmente ou em um servidor acessível
- [Qdrant](https://qdrant.tech/) rodando localmente ou em um servidor acessível

## Configuração

Você pode configurar o projeto usando variáveis de ambiente. Todas possuem valores padrão, mas podem ser sobrescritas conforme sua necessidade:

| Variável                      | Descrição                                 | Padrão                        |
|------------------------------|-------------------------------------------|-------------------------------|
| OLLAMA_BASE_URL              | URL do Ollama                             | http://localhost:11434        |
| EMBEDDING_MODEL              | Modelo de embedding                       | nomic-embed-text              |
| LANGUAGE_MODEL               | Modelo de linguagem                       | mistral:7b-instruct           |
| QDRANT_HOST                  | Host do Qdrant                            | localhost                     |
| QDRANT_PORT                  | Porta do Qdrant                           | 6334                          |
| QDRANT_COLLECTION            | Nome da coleção Qdrant                    | rag-tests                     |
| CHUNK_SIZE                   | Tamanho dos chunks de indexação           | 500                           |
| CHUNK_OVERLAP                | Sobreposição dos chunks                   | 50                            |
| MAX_RESULTS                  | Máximo de resultados na busca             | 20                            |
| MIN_SCORE                    | Score mínimo para considerar um resultado  | 0.5                           |
| BDD_GENERATOR_OUTPUT_PATH    | Caminho para salvar arquivos gerados       | src/main/resources/generated  |

Exemplo de configuração:

```sh
export OLLAMA_BASE_URL=http://localhost:11434
export QDRANT_HOST=localhost
export QDRANT_PORT=6334
export BDD_GENERATOR_OUTPUT_PATH=src/main/resources/generated
```

## Como rodar o projeto

1. **Clone o repositório:**
   ```sh
   git clone <url-do-repositorio>
   cd bdd-generator
   ```

2. **Configure as variáveis de ambiente** (opcional, veja tabela acima).

3. **Execute o projeto:**
   ```sh
   ./gradlew run
   ```

4. **Acesse a API:**
   O servidor Javalin estará disponível em `http://localhost:7070`.

   - Para indexar um projeto:
     - Faça um POST para `/index` com o parâmetro `path` apontando para o diretório do seu código-fonte.
   - Para gerar cenários de teste:
     - Faça um POST para `/generate` com os parâmetros:
       - `instruction`: instrução/texto do que deseja gerar
       - `destination_path` (opcional): caminho para salvar o arquivo `.md`

Exemplo usando `curl`:

```sh
curl -X POST -F 'path=/caminho/do/seu/codigo' http://localhost:7070/index

curl -X POST -F 'instruction=Crie cenários de teste para a classe X' http://localhost:7070/generate
```

## Saída

Os arquivos `.md` gerados ficarão no diretório definido por `BDD_GENERATOR_OUTPUT_PATH` (ou no padrão `src/main/resources/generated`).

## Observações

- Os cenários são gerados em Gherkin, em português do Brasil.
- O projeto é flexível e permite customização dos modelos e parâmetros via variáveis de ambiente.

## Licença

MIT

