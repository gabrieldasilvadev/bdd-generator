1. Cenario: Quando um usuário tenta se juntar a uma sala vazia

```gherkin
Feature: Join Chat Room
  In this feature we will verify the behavior of joining a chat room in the application.

Scenario: When a user tries to join an empty room
    Given there is no room with the specified id
    And a user wants to join a specific room
    When the user sends a request to join the specified room
    Then a response should be returned indicating that the room does not exist

Scenario: When a user tries to join a room with an invalid name
    Given there is a room with a valid id but with an invalid name
    And a user wants to join the room
    When the user sends a request to join the specified room
    Then a response should be returned indicating that the name of the participant is mandatory
```

2. Cenario: Quando um usuário se junta com sucesso a uma sala existente

```gherkin
Scenario: When a user successfully joins an existing room
    Given there is a room with the specified id and a valid name
    And a participant with a valid name
    When the user sends a request to join the specified room with the valid participant name
    Then the user should be able to join the room successfully
```

1. Como um participante não inscrito:
    Title: Tentativa de entrada sem registro anterior
    Scenario: Um participante tenta entrar em uma sala de bate-papo sem estar registrado
    Given: Existe uma sala de bate-papo com ID definido
    And: Não há o participante na lista de participantes do ChatRoom
    When: O Participante não inscrito faz a solicitação para entrar na Sala de Bate-Papo
    Then: A solicitação é recusada e nenhum evento é emitido

2. Como um participante inscrito:
    Title: Entrada bem sucedida em uma sala de bate-papo
    Scenario: Um participante já cadastrado faz a solicitação para entrar na Sala de Bate-Papo
    Given: Existe uma sala de bate-papo com ID definido
    And: O participante que deseja entrar já está na lista de participantes do ChatRoom
    When: O Participante cadastrado faz a solicitação para entrar na Sala de Bate-Papo
    Then: A solicitação é aceita e um evento ParticipantJoinedRoomEvent é emitido

3. Como um participante inscrito:
    Title: Saída bem sucedida da sala de bate-papo
    Scenario: Um participante já cadastrado faz a solicitação para sair da Sala de Bate-Papo
    Given: Existe uma sala de bate-papo com ID definido e o participante já está na lista de participantes do ChatRoom
    When: O Participante cadastrado faz a solicitação para sair da Sala de Bate-Papo
    Then: A solicitação é aceita e um evento ParticipantLeftRoomEvent é emitido e o participante é removido da lista de participantes do ChatRoom

Titulo: Cenários de Teste para o Método `joinChatRoom` da Classe `CommandController`

```gherkin
Cenario: Um usuário pode entrar em uma sala de chat existente
    Dado uma sala de chat com um identificador único e um conjunto de participantes existentes
    Quando um novo usuário tentar entrar na sala de chat
    Então o método `joinChatRoom` deve atualizar a lista de participantes da sala de chat, adicionando o usuário novo

Cenario: Um usuário não pode entrar em uma sala de chat inexistente
    Dado um identificador único de uma sala de chat que não existe
    Quando um novo usuário tentar entrar na sala de chat
    Então o método `joinChatRoom` deve lançar uma exceção, indicando que a sala de chat não foi encontrada

Cenario: Um usuário já cadastrado pode entrar em uma sala de chat existente várias vezes
    Dado um identificador único de uma sala de chat com um conjunto de participantes existentes e um usuário já cadastrado na sala
    Quando esse usuário tentar entrar novamente na sala de chat
    Então o método `joinChatRoom` deve atualizar a lista de participantes da sala de chat, adicionando o usuário duas ou mais vezes
```

1. Titulo: Verificação da adição de participante no salão de bate-papo

   ```scenario
    Dado que exista um salão de bate-papo com id "salão1" e nome "Salão 1"
    Quando o comando para se juntar ao salão for enviado pela participante "Participante 1" com o ID do salão "salão1"
    Entao o salão deve ter uma lista de participantes que contém a participante "Participante 1"
   ```

2. Titulo: Verificação da remoção de participante do salão de bate-papo

   ```scenario
    Dado que exista um salão de bate-papo com id "salão1" e nome "Salão 1" e uma lista de participantes contendo a participante "Participante 1"
    Quando o comando para sair do salão for enviado pela participante "Participante 1" com o ID do salão "salão1"
    Entao a lista de participantes do salão não deve mais conter a participante "Participante 1"
   ```

3. Titulo: Verificação da visualização da lista de participantes em um salão de bate-papo

   ```scenario
    Dado que exista um salão de bate-papo com id "salão1" e nome "Salão 1" e uma lista de participantes contendo a participante "Participante 1"
    Quando o usuário solicitar a listagem das participantes do salão com o ID do salão "salão1"
    Entao o salão deve ser retornado com a lista de participantes que contém a participante "Participante 1"
   ```

Como o contexto fornecido contém duas classes distintas (ParticipantJoinedRoomEvent e JoinRoomCommand), vamos escrever cenários para cada uma delas.

**Cenário: Verificar se um comando é processado corretamente quando um usuário entra em uma sala de chat**
```
Feature: Teste do controle de comandos de entrada de chat
  Com o objetivo de verificar se os comandos são processados corretamente ao entrar na sala de chat.

Scenario: Participante se junta a uma sala de chat
  Dado um usuário que entra na sala de chat
  Quando ele enviar um comando para se juntar à sala de chat
  Então o sistema deve processar corretamente o comando e adicionar o usuário à lista de participantes da sala de chat
```
**Cenário: Verificar se uma evento é emitido corretamente quando um usuário sai de uma sala de chat**
```
Feature: Teste do controle de comandos de entrada e saída de chat
  Com o objetivo de verificar se os comandos são processados corretamente ao entrar e sair da sala de chat.

Scenario: Participante sai de uma sala de chat
  Dado um usuário que entra na sala de chat
  Quando ele enviar um comando para sair da sala de chat
  Então o sistema deve emitir corretamente o evento e remover o usuário da lista de participantes da sala de chat
```

1. Titulo: Verificar se um participante pode entrar em uma sala de chat existente

    Dado que existe uma sala de chat com id "roomId" e nome "nomeDaSala"
    Quando o participante "participante" executar o comando JoinRoomCommand("roomId", "participante")
    Então a sala de chat deve ter o participante "participante" presente

2. Titulo: Verificar se um participante pode entrar em uma sala de chat inexistente

    Dado que não existe nenhuma sala de chat com id "roomId"
    Quando o participante "participante" executar o comando JoinRoomCommand("roomId", "participante")
    Então deve ser gerada uma exceção ou ocorrer algum tipo de tratamento de erro

3. Titulo: Verificar se um participante não pode entrar em mais de uma sala de chat com o mesmo id

    Dado que existe uma sala de chat com id "roomId" e nome "nomeDaSala" com o participante "participante" presente
    Quando o participante "participante" executar novamente o comando JoinRoomCommand("roomId", "participante")
    Então a sala de chat deve permanecer com o mesmo número de participantes e não haver nenhuma mudança

4. Titulo: Verificar se um participante pode sair de uma sala de chat existente

    Dado que existe uma sala de chat com id "roomId" e nome "nomeDaSala" com o participante "participante" presente
    Quando o participante "participante" executar o comando LeaveRoomCommand("roomId", "participante")
    Então a sala de chat deve ter um número menor de participantes e o participante "participante" não dever ser mais presente

5. Titulo: Verificar se um participante não pode sair de uma sala de chat inexistente

    Dado que não existe nenhuma sala de chat com id "roomId" e o participante "participante" estando presente
    Quando o participante "participante" executar o comando LeaveRoomCommand("roomId", "participante")
    Então deve ser gerada uma exceção ou ocorrer algum tipo de tratamento de erro

1. Titulo: Verificar que o comando para entrar em uma sala é processado pelo CommandController

```gherkin
Feature: CommandController - joinChatRoom

  Escenario: Quando enviado um comando válido para entrar em uma sala existente, a sala deve ser atualizada com o participante novo
    Dado que existe uma sala identificada por um ID ("roomId")
    E que um participante ("participant") deseja entrar naquela sala
    Quando envio um comando para entrar na sala usando o CommandController
    Então a sala deve possuir um novo participante no seu estado atual

  Escenario: Quando enviado um comando para entrar em uma sala que não existe, nenhuma mudança deverá ocorrer
    Dado que não existe uma sala identificada por um ID ("roomId")
    E que um participante ("participant") deseja entrar nela
    Quando envio um comando para entrar na sala usando o CommandController
    Então a mesma deve manter seu estado atual
```

2. Titulo: Verificar se é possível criar um objeto LeaveRoomCommand com os dados obrigatórios

```gherkin
Feature: CommandController - LeaveRoomCommand

  Escenario: Quando crio um objeto LeaveRoomCommand com valores inválidos, deve ser possível capturar o erro
    Dado que um objeto LeaveRoomCommand é criado com um roomId vazio ("roomId")
    E que o objeto LeaveRoomCommand é criado com um participante vazio ("participant")
    Quando tento cria-lo sem validação
    Então deverá ser capturado um erro devido ao campo roomId ser obrigatório e estar vazio

  Escenario: Quando crio um objeto LeaveRoomCommand com valores válidos, deve ser possível criar o mesmo sem problemas
    Dado que um objeto LeaveRoomCommand é criado com um roomId não vazio ("roomId")
    E que o objeto LeaveRoomCommand é criado com um participante não vazio ("participant")
    Quando tento cria-lo sem validação
    Então deverá ser possível criar o objeto sem erro
```

1. Cenário: Verificar se o método joinChatRoom é chamado quando uma requisição POST é enviada para a URL /chat/{roomId}

   ```
   Como um usuário autenticado, eu quero entrar em uma sala de bate-papo existente.
   Dado um objeto CommandGateway instanciado em CommandController,
   e um objeto JoinRoomCommand com um identificador válido de sala,
   quando eu faço uma requisição POST para /chat/{roomId},
   então o método joinChatRoom deve ser chamado no CommandGateway com o comando JoinRoomCommand.
   ```

2. Cenário: Verificar se a sala é adicionada ao usuário autenticado quando a requisição POST é enviada para a URL /chat/{roomId} e o método joinChatRoom é chamado com sucesso.

   ```
   Como um usuário autenticado, eu quero entrar em uma sala de bate-papo existente.
   Dado que um objeto CommandGateway instanciado em CommandController,
   e um objeto JoinRoomCommand com um identificador válido de sala,
   e o método joinChatRoom do CommandGateway é chamado com sucesso,
   então a sala deve ser adicionada ao usuário autenticado.
   ```

3. Cenário: Verificar se ocorre uma exceção quando uma requisição POST é enviada para a URL /chat/{roomId} e o identificador da sala não é válido.

   ```
   Como um usuário autenticado, eu quero entrar em uma sala de bate-papo existente.
   Dado que um objeto CommandGateway instanciado em CommandController,
   e um objeto JoinRoomCommand com um identificador inválido de sala,
   então deve ocorrer uma exceção ao chamar o método joinChatRoom do CommandGateway.
   ```

1. Cenario: Entrada no chat-room correta
    Dado que eu acesso a URL `/chat-rooms/{id}`, onde o `{id}` é um identificador valido
    Quando eu envio uma requisição POST com os dados necessários para entrar no chat-room
    Então o método joinChatRoom da classe CommandController deve retornar uma resposta HTTP 200 (OK)

  2. Cenario: Entrada no chat-room incorreta - ID inválido
    Dado que eu acesso a URL `/chat-rooms/{id}`, onde o `{id}` é um identificador invalido
    Quando eu envio uma requisição POST com os dados necessários para entrar no chat-room
    Então o método joinChatRoom da classe CommandController deve retornar uma resposta HTTP 400 (Bad Request)

  3. Cenario: Entrada no chat-room incorreta - Usuário já está dentro do chat-room
    Dado que eu já estou conectado ao chat-room com o identificador `{id}`
    Quando eu envio uma requisição POST para entrar novamente no chat-room com os mesmos dados
    Então o método joinChatRoom da classe CommandController deve retornar uma resposta HTTP 409 (Conflict)

  4. Cenario: Entrada em um chat-room fechado
    Dado que eu acesso a URL `/chat-rooms/{id}`, onde o chat-room com o identificador `{id}` é fechado
    Quando eu envio uma requisição POST com os dados necessários para entrar no chat-room
    Então o método joinChatRoom da classe CommandController deve retornar uma resposta HTTP 403 (Forbidden)