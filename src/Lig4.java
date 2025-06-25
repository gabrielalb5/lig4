import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Lig4 {
    static final int LINHAS = 6;
    static final int COLUNAS = 7;
    static Tabuleiro tabuleiro = new Tabuleiro(LINHAS,COLUNAS);
    static Placar placar = new Placar();
    private Scanner leitor;
    
    private Socket servidorConexao;
    private ObjectInputStream servidorEntrada;
    private ObjectOutputStream servidorSaida;

    private boolean suaVez;
    private char peca;

    
    public Lig4() throws Exception{
        leitor = new Scanner(System.in);
        conectar();
        jogar();
    }
    
    public void jogar() throws Exception{
        boolean jogoAtivo = true;
        
        placar.verificarSalvo(leitor);
        placar.exibirPlacar();
        
        while (jogoAtivo){
            tabuleiro.criarTabuleiro();            
            boolean partidaAtiva = true;
            int colunaEscolhida = -1;
            String mensagem;
            
            while(partidaAtiva){
                if(!isSuaVez()){
                    tabuleiro.imprimirTabuleiro();
                    System.out.println("Aguarde sua vez.");
                    
                    mensagem = (String) servidorEntrada.readObject();
                    String[] info = mensagem.split(";");
                    char jogadorAtual = info[1].charAt(0);
                                    
                    try{
                        colunaEscolhida = Integer.parseInt(info[0]);
                    }catch(Exception e){
                        System.out.println("Entrada inválida. Digite um número de 1 a "+COLUNAS+".");
                        leitor.nextLine();
                        continue;
                    }

                    if(colunaEscolhida<1 || colunaEscolhida>COLUNAS){
                        System.out.println("Coluna fora do intervalo. Tente novamente.");
                        continue;
                    }
                        
                    int linha = tabuleiro.jogarPeca(colunaEscolhida, jogadorAtual);

                    if(linha == -1) {
                        System.out.println("Essa coluna está cheia. Escolha outra.");
                        continue;
                    }

                    if(tabuleiro.verificaVitoria(linha, colunaEscolhida-1, jogadorAtual)){
                        tabuleiro.imprimirTabuleiro();
                        System.out.println("Jogador " + jogadorAtual + " venceu!");
                        placar.adicionarPonto(jogadorAtual);
                        break;
                    }

                    if(tabuleiro.tabuleiroCheio()){
                        tabuleiro.imprimirTabuleiro();
                        System.out.println("Empate! O tabuleiro está cheio.");
                        break;
                    }
                    
                    setSuaVez(true);
                }
                
                boolean jogado = false;
                while(partidaAtiva && !jogado){
                    tabuleiro.imprimirTabuleiro();
                    System.out.print("Jogador "+getPeca()+" escolha a coluna (1 a "+COLUNAS+"): ");

                    try{
                        colunaEscolhida = leitor.nextInt();
                    }catch(Exception e){
                        System.out.println("Entrada inválida. Digite um número de 1 a "+COLUNAS+".");
                        leitor.nextLine();
                        continue;
                    }

                    if(colunaEscolhida<1 || colunaEscolhida>COLUNAS){
                        System.out.println("Coluna fora do intervalo. Tente novamente.");
                        continue;
                    }

                    int linha = tabuleiro.jogarPeca(colunaEscolhida, getPeca());

                    if(linha == -1) {
                        System.out.println("Essa coluna está cheia. Escolha outra.");
                        continue;
                    }

                    if(tabuleiro.verificaVitoria(linha, colunaEscolhida-1, getPeca())){
                        tabuleiro.imprimirTabuleiro();
                        System.out.println("Jogador " + getPeca() + " venceu!");
                        placar.adicionarPonto(getPeca());
                        break;
                    }

                    if(tabuleiro.tabuleiroCheio()){
                        tabuleiro.imprimirTabuleiro();
                        System.out.println("Empate! O tabuleiro está cheio.");
                        break;
                    }
                    
                    setSuaVez(false);
                    jogado = true;
                    mensagem = colunaEscolhida + ";" + getPeca();
                    servidorSaida.writeObject(mensagem);
                }

            }
            placar.exibirPlacar();
            
            boolean respostaValida = false;
            while (!respostaValida) {
                System.out.print("\nDeseja jogar novamente? (S/N): ");
                String resposta = leitor.next().trim().toUpperCase();

                switch (resposta) {
                    case "S" -> respostaValida = true;
                    case "N" -> {
                        jogoAtivo = false;
                        respostaValida = true;
                        System.out.println("Obrigado por jogar!");
                    }
                    default -> System.out.println("Não entendi. Digite 'S' para sim ou 'N' para não.");
                }
            }
        }
        
        boolean respostaValida = false;
        while (!respostaValida) {
            System.out.print("\nDeseja excluir o placar? (S/N): ");
            String resposta = leitor.next().trim().toUpperCase();

            switch (resposta) {
                case "N" -> respostaValida = true;
                case "S" -> {
                    respostaValida = true;
                    placar.excluirPlacar();
                }
                default -> System.out.println("Não entendi. Digite 'S' para sim ou 'N' para não.");
            }
        }

        leitor.close();
    }
    
    private void conectar() throws Exception {
        
        servidorConexao = new Socket(InetAddress.getByName(Config.getIp()),Config.getPorta());
        
        servidorSaida = new ObjectOutputStream(servidorConexao.getOutputStream());
        servidorSaida.flush();
        
        servidorEntrada = new ObjectInputStream(servidorConexao.getInputStream());
        
        String mensagem = (String) servidorEntrada.readObject();
        String[] info = mensagem.split(";");
        setPeca(info[0].charAt(0));
        
        if(info[1].equals("true")){
            setSuaVez(true);
        }else{
            setSuaVez(false);
        }
    }
    
    public boolean isSuaVez() {
        return suaVez;
    }

    public void setSuaVez(boolean suaVez) {
        this.suaVez = suaVez;
    }

    public char getPeca() {
        return peca;
    }

    public void setPeca(char peca) {
        this.peca = peca;
    }
}