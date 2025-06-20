public class Tabuleiro {
    private final int LINHAS;
    private final int COLUNAS;
    private final char[][] tabuleiro;
    
    public Tabuleiro(int linhas, int colunas) {
        this.LINHAS = linhas;
        this.COLUNAS = colunas;
        this.tabuleiro = new char[LINHAS][COLUNAS];
    }
    
    public void criarTabuleiro(){
        for(int i=0;i<LINHAS;i++){
            for(int j=0;j<COLUNAS;j++){
                tabuleiro[i][j] = '.';
            }
        }
    }
    
    public void imprimirTabuleiro(){
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
    
    public int jogarPeca(int col, char peca){
        col--;
        for(int i=LINHAS-1;i>=0;i--){
            if (tabuleiro[i][col] == '.'){
                tabuleiro[i][col] = peca;
                return i;
            }
        }
        return -1;
    }
    
    public boolean verificaVitoria(int linha, int coluna, char peca){
        return verificaDirecao(linha, coluna, peca, 0, 1)//Horizontal
            || verificaDirecao(linha, coluna, peca, 1, 0)//Vertical
            || verificaDirecao(linha, coluna, peca, 1, 1)//Diagonal (topo esquerdo, canto inferior direito)
            || verificaDirecao(linha, coluna, peca, 1, -1);//Diagonal (topo direito, canto inferior esquerdo)
    }
    
    public boolean verificaDirecao(int linha, int coluna, char peca, int dirLinha, int dirColuna) {
        int cont = 1; //começa em 1 pois é a peça que acabou de ser colocada
        int l = linha + dirLinha;
        int c = coluna + dirColuna;
        
        while(nosLimites(l,c) && tabuleiro[l][c]==peca){
            cont++;
            l += dirLinha;
            c += dirColuna;
        }

        l = linha - dirLinha;
        c = coluna - dirColuna;
        while(nosLimites(l,c) && tabuleiro[l][c]==peca){
            cont++;
            l -= dirLinha;
            c -= dirColuna;
        }
        
        return cont>=4;
    }
    
    public boolean nosLimites(int linha, int coluna){
        return linha>=0 && linha<LINHAS && coluna>=0 && coluna<COLUNAS;
    }
    
    public boolean tabuleiroCheio() {
        for (int i=0;i<COLUNAS;i++){
            if(tabuleiro[0][i]=='.') {
                return false;
            }
        }
        return true;
    }
}
