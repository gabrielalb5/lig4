import java.util.Scanner;

public class Lig4 {
    static final int LINHAS = 6;
    static final int COLUNAS = 7;
    static char[][] tabuleiro = new char[LINHAS][COLUNAS];
    
    public static void main(String[] args) {
        Scanner leitor = new Scanner(System.in);
        boolean jogoAtivo = true;

        while (jogoAtivo) {
            char jogadorAtual = 'X';
            criarTabuleiro();

            boolean partidaAtiva = true;

            while(partidaAtiva){
                imprimirTabuleiro();
                System.out.print("Jogador "+jogadorAtual+" escolha a coluna (1 a "+COLUNAS+"): ");

                int colunaEscolhida;
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

                int linha = jogarPeca(colunaEscolhida, jogadorAtual);

                if(linha == -1) {
                    System.out.println("Essa coluna está cheia. Escolha outra.");
                    continue;
                    //break
                    //descomentar pra testar 
                }

                jogadorAtual = (jogadorAtual == 'X') ? 'O' : 'X';
            }

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

        leitor.close();
    }
    
    static void criarTabuleiro(){
        for(int i=0;i<LINHAS;i++){
            for(int j=0;j<COLUNAS;j++){
                tabuleiro[i][j] = '.';
            }
        }
    }
    
    static void imprimirTabuleiro(){
        System.out.println("\nTabuleiro");
        for(int i=0;i<LINHAS;i++){
            for(int j=0;j<COLUNAS;j++){
                if(tabuleiro[i][j] == '.'){
                    System.out.print("[ ]");
                }else{
                    System.out.print("["+tabuleiro[i][j]+"]");
                }
            }
            System.out.println();
        }
        for(int i=1;i<=COLUNAS;i++){
            System.out.print(" "+i+" ");
        }
        System.out.println();
    }
    
    static int jogarPeca(int col, char peca){
        col--;
        for(int i=LINHAS-1;i>=0;i--){
            if (tabuleiro[i][col] == '.'){
                tabuleiro[i][col] = peca;
                return i;
            }
        }
        return -1;
    }
}