import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

// vamo tratar isso como o cérebro do jogo
// ela conecta a interface com a lógica e a rede
public class Lig4 {
    private Tabuleiro tabuleiro;
    private Placar placar;
    private JanelaJogo janela;
    
    // atributos de rede
    private Socket servidorConexao;
    private ObjectInputStream servidorEntrada;
    private ObjectOutputStream servidorSaida;
    
    // variaveis que controlam o estado do jogo
    private boolean suaVez;
    private char peca;
    private boolean jogoAtivo = true;
    private int rodadaAtual = 1;

    // variaveis pra controlar a lógica de reiniciar a partida
    private int minhaDecisaoReinicio = -1;
    private int decisaoOponenteReinicio = -1;

    // construtor que recebe a janela pra poder se comunicar com ela e cria os objetos do jogo
    public Lig4(JanelaJogo janela) {
        this.janela = janela;
        this.tabuleiro = new Tabuleiro();
        this.tabuleiro.criarTabuleiro();
        this.placar = new Placar();
    }

    // inicial do jogo, chamado pela janela depois que ela já tá montada
    public void iniciarJogo() {
        try {
            conectar();
            // cria e inicia a thread que vai ficar só escutando a rede, pra não travar a interface
            new Thread(this::escutarServidor).start();
            janela.atualizarPlacar(); // mostra o placar inicial
        } catch (Exception e) {
            janela.setStatus("Erro ao conectar: " + e.getMessage());
        }
    }

    // faz a primeira conexão com o servidor, pega nossa peça (X ou O) e descobre quem começa
    private void conectar() throws Exception {
        servidorConexao = new Socket(InetAddress.getByName(Config.getIp()), Config.getPorta());
        servidorSaida = new ObjectOutputStream(servidorConexao.getOutputStream());
        servidorEntrada = new ObjectInputStream(servidorConexao.getInputStream());
        
        String mensagem = (String) servidorEntrada.readObject();
        String[] info = mensagem.split(";");
        this.peca = info[0].charAt(0);
        this.suaVez = Boolean.parseBoolean(info[1]);
        
        if(peca == 'X'){
            janela.setStatus(suaVez ? "Sol, sua vez de jogar." : "Sol, aguarde a jogada do oponente.");
        }else{
            janela.setStatus(suaVez ? "Lua, sua vez de jogar." : "Lua, aguarde a jogada do oponente.");
        }
        
    }

    // esse método roda numa thread separada e fica SÓ escutando o que o servidor manda
    // o while(true) garante que a thread nunca morra, a não ser que a conexão caia
    private void escutarServidor() {
        while (true) {
            try {
                // fica aqui travado esperando uma mensagem chegar
                String mensagem = (String) servidorEntrada.readObject();
                // quando chega, ele só passa pro 'processarMensagem' decidir o que fazer
                processarMensagem(mensagem);
            } catch (Exception e) {
                // se der erro (ex: o outro jogador fechou o jogo), a gente sai do loop e a thread morre
                if (jogoAtivo) {
                    janela.setStatus("Erro de conexão. Fim de jogo.");
                }
                break;
            }
        }
    }

    // método chamado pela interface quando a gente clica numa coluna
    public void tentarJogada(int coluna) {
        // só faz alguma coisa se for a nossa vez e uma partida estiver acotnecendo
        if (suaVez && jogoAtivo) {
            int linha = tabuleiro.jogarPeca(coluna, this.peca);
            
            if (linha != -1) { // se a jogada foi válida (coluna não tava cheia)
                janela.redesenhar();
                enviarMensagem("JOGADA;" + coluna + ";" + this.peca); // manda a jogada pro outro jogador

                // depois de jogar, verifica se a gente ganhou ou se deu empate
                if (tabuleiro.verificaVitoria(linha, coluna - 1, this.peca)) {
                    janela.setStatus("Você venceu!");
                    placar.adicionarPonto(this.peca);
                    janela.atualizarPlacar();
                    proporReinicio();
                } else if (tabuleiro.tabuleiroCheio()) {
                    janela.setStatus("Empate!");
                    proporReinicio();
                } else {
                    // se o jogo continua, passa a vez e avisa o jogador
                    suaVez = false;
                    if(peca == 'X'){
                        janela.setStatus("Sol, aguarde a jogada do oponente.");
                    }else{
                        janela.setStatus("Lua, aguarde a jogada do oponente.");
                    }
                }
            } else {
                janela.setStatus("Coluna cheia. Tente outra.");
            }
        }
    }

    // um 'roteador': recebe a msg da rede e manda pro método certo cuidar dela
    private void processarMensagem(String mensagem) {
        String[] partes = mensagem.split(";");
        String tipoMensagem = partes[0];
        switch (tipoMensagem) {
            case "JOGADA":
                processarJogadaOponente(partes);
                break;
            case "REINICIAR":
                processarRespostaReinicio(partes);
                break;
        }
    }

    // processa a jogada que o nosso oponente fez (espelho do 'tentarJogada')
    private void processarJogadaOponente(String[] partes) {
        int colunaJogada = Integer.parseInt(partes[1]);
        char pecaOponente = partes[2].charAt(0);
        int linha = tabuleiro.jogarPeca(colunaJogada, pecaOponente);
        janela.redesenhar();

        // se a jogada do oponente resulta no fim do jogo
        if (tabuleiro.verificaVitoria(linha, colunaJogada - 1, pecaOponente)) {
            janela.setStatus("Você perdeu. O oponente venceu.");
            placar.adicionarPonto(pecaOponente);
            janela.atualizarPlacar();
            // a gente tem que chamar a UI de 'jogar de novo?' a partir da thread certa
            SwingUtilities.invokeLater(this::proporReinicio);
        } else if (tabuleiro.tabuleiroCheio()) {
            janela.setStatus("Empate!");
            SwingUtilities.invokeLater(this::proporReinicio);
        } else {
            // se o jogo continua, agora é a nossa vez
            suaVez = true;
            if(pecaOponente == 'X'){
                janela.setStatus("Lua, sua vez de jogar.");
            }else{
                janela.setStatus("Sol, sua vez de jogar.");
            }
        }
    }

    // --- LÓGICA DE REINÍCIO ---
    
    // mostra a caixinha de "jogar de novo?", envia nossa resposta e espera o outro jogador
    private void proporReinicio() {
        jogoAtivo = false; // pausa o jogo pra ninguém clicar enquanto a caixa de dialogo tá na tela
        int resposta = JOptionPane.showConfirmDialog(null, "Deseja jogar novamente?", "Fim da Partida", JOptionPane.YES_NO_OPTION);
        minhaDecisaoReinicio = (resposta == JOptionPane.YES_OPTION) ? 1 : 0;
        enviarMensagem("REINICIAR;" + minhaDecisaoReinicio);
        janela.setStatus("Aguardando resposta do outro jogador...");
        verificarDecisaoReinicio(); // já checa se por acaso a outra resposta não chegou antes
    }

    // quando a resposta do oponente chega, a gente guarda e chama o verificador
    private void processarRespostaReinicio(String[] partes) {
        decisaoOponenteReinicio = Integer.parseInt(partes[1]);
        verificarDecisaoReinicio();
    }

    // só quando tem as duas respostas (a minha e a do outro) ele toma uma decisão
    private void verificarDecisaoReinicio() {
        if (minhaDecisaoReinicio != -1 && decisaoOponenteReinicio != -1) {
            if (minhaDecisaoReinicio == 1 && decisaoOponenteReinicio == 1) {
                // se os dois falaram sim, reinicia tudo
                janela.setStatus("Ambos concordaram. Reiniciando partida...");
                reiniciarPartida();
            } else {
                // se alguém disse não, encerra o jogo de vez
                janela.setStatus("Fim de Jogo. Um dos jogadores não quis continuar.");
                jogoAtivo = false;
                encerrarConexao();
            }
        }
    }

    // zera o tabuleiro e as variaveis de controle pra começar uma nova rodada
    private void reiniciarPartida() {
        tabuleiro.criarTabuleiro();
        janela.redesenhar();
        minhaDecisaoReinicio = -1;
        decisaoOponenteReinicio = -1;
        jogoAtivo = true;
        
        // usa o contador de rodada pra alternar quem começa, um jeito mais seguro que só inverter a vez pq tava dando problema de sincroniazcao as vezes
        rodadaAtual++;
        if (this.peca == 'X') {
            suaVez = (rodadaAtual % 2 != 0); // X joga nas rodadas impares
        } else {
            suaVez = (rodadaAtual % 2 == 0);  // O joga nas rodadas pares
        }
        if(peca == 'X'){
            janela.setStatus(suaVez ? "Sol, sua vez de jogar." : "Sol, aguarde a jogada do oponente.");
        }else{
            janela.setStatus(suaVez ? "Lua, sua vez de jogar." : "Lua, aguarde a jogada do oponente.");
        }
        
        janela.atualizarPlacar();
    }

    // --- MÉTODOS AUXILIARES ---

    // um método só pra mandar mensagem, pra não repetir código
    private void enviarMensagem(String mensagem) {
        try {
            servidorSaida.writeObject(mensagem);
            servidorSaida.flush();
        } catch (Exception e) {
            if (jogoAtivo) {
                janela.setStatus("Erro de conexão ao enviar dados.");
            }
        }
    }

    // fecha os streams e o socket de forma limpa quando o jogo acaba de vez
    private void encerrarConexao() {
        try {
            if (servidorSaida != null) servidorSaida.close();
            if (servidorEntrada != null) servidorEntrada.close();
            if (servidorConexao != null) servidorConexao.close();
        } catch (IOException e) {
            System.err.println("Erro ao fechar os recursos de rede.");
        }
    }

    // métodos 'get' pra view poder pegar as informações que precisa
    public Tabuleiro getTabuleiro() {
        return this.tabuleiro;
    }

    public Placar getPlacar() {
        return this.placar;
    }
}