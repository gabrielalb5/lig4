## 🪐 Astro - Lig4 Multiplayer Online

### 📌 Definição

Astro é uma implementação multiplayer online do clássico jogo de tabuleiro Lig 4, também conhecido como <i>Connect Four</i>. Dois jogadores se conectam remotamente e competem para formar uma sequência de quatro fichas consecutivas da mesma cor na horizontal, vertical ou diagonal.

O jogo é estruturado com base no modelo m,n,k (7,6,4), com regras reais de gravidade e turnos. Ele também conta com interface gráfica (Java Swing) e um sistema de comunicação cliente-servidor com sockets, além de persistência em arquivos XML para armazenar placares.

<img width="1377" height="787" alt="image" src="https://github.com/user-attachments/assets/53a22aab-53be-4cc0-a44c-dd53dd993f8a" />

### 🚀 Funcionalidades
- Modo Multiplayer Online com 2 jogadores via rede local ou internet.
- Interface gráfica interativa feita com Java Swing.
- Controle de início e reinício de partidas.
- Armazenamento e exibição de placar entre os jogadores.
- Salvamento e leitura do histórico de partidas em XML.
- Utilização de Threads para comunicação assíncrona e fluida entre cliente e servidor.
- Estrutura baseada em boas práticas de Programação Orientada a Objetos (POO), como encapsulamento, herança e polimorfismo.

### 🛠️ Tecnologias e Ferramentas</h2>
- Java 8+
- Threads e Sockets TCP/IP
- Java Swing (GUI)
- Manipulação de arquivos XML
- IDE recomendada: NetBeans

### ▶️ Como Executar o Projeto

1. Clone o repositório:

```
git clone https://github.com/gabrielalb5/lig4.git
```

2. Abra o projeto preferencialmente no NetBeans (em outra IDE talvez seja necessário realizar uma modificação. Veja [Erros comuns](#erros-comuns) no fim dessa seção).
3. Descubra seu IP: abra seu terminal e digite ipconfig (prompt de comando no Windows)
4. Copie seu endereço IPv4, que estará em uma linha semelhante a esta:
```
Endereço IPv4. . . . . . . .  . . . . . . . : 192.168.0.0
```
5. Abra o arquivo `config.xml` com um editor de texto e troque o valor de `ip` pelo número copiado no passo anterior. Salve e feche.
6. Compile e execute a classe `ServidorLig4.java` e em seguida `ClienteLig4.java`

#### Modos de Jogo
- Multiplayer Local (mesmo computador): Execute duas instâncias da classe `ClienteLig4.java` no mesmo computador.
- Multiplayer em Rede (computadores diferentes): Um dos computadores deve executar o `ServidorLig4.java`. Os demais devem apontar o IP no `config.xml` para o IP do servidor e estar conectados na mesma rede (Wi-Fi ou cabo).

#### Erros comuns
- Rede Wi-Fi vs Cabo: Computadores conectados via Wi-Fi e Ethernet não se comunicam facilmente. Use o mesmo tipo de conexão em ambos.
- IDE fora do NetBeans: Se estiver usando outra IDE, e a conexão não encontrar o arquivo de configuração, mova o arquivo `config.xml` da raiz do projeto para dentro da pasta `src`.

### 🧑‍💻 Autores
<a href="https://www.linkedin.com/in/eduardo-bonifacio-0a802b2a8/">Eduardo Bonifacio</a><br>
<a href="https://www.linkedin.com/in/gabrielalbino05/">Gabriel Albino</a>
