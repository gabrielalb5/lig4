import java.io.File;
import java.io.FileWriter;
import java.util.Map;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class Placar{
    private final String nomeArquivo = "placar.xml";
    private Map<Character, Integer> pontos;

    public Placar() {
        pontos = new HashMap<>();
        pontos.put('X',0);
        pontos.put('O',0);
        carregarPlacar();
    }

    public void adicionarPonto(char jogador){
        pontos.put(jogador, pontos.getOrDefault(jogador, 0)+1);
        salvarPlacar();
    }

    public int getPontos(char jogador){
        return pontos.getOrDefault(jogador,0);
    }

    public void exibirPlacar(){
        System.out.println("\n--- Placar Atual ---");
        System.out.println("Jogador X: " +getPontos('X'));
        System.out.println("Jogador O: " +getPontos('O'));
        System.out.println("--------------------");
    }

    private void salvarPlacar(){
        try{
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element root = doc.createElement("placar");
            doc.appendChild(root);

            for(Map.Entry<Character, Integer> entry : pontos.entrySet()){
                Element jogador = doc.createElement("jogador");
                jogador.setAttribute("id", String.valueOf(entry.getKey()));
                jogador.setTextContent(String.valueOf(entry.getValue()));
                root.appendChild(jogador);
            }

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            FileWriter writer = new FileWriter(nomeArquivo);
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            writer.close();

        }catch (Exception e){
            System.out.println("Erro ao salvar o placar: " + e.getMessage());
        }
    }

    private void carregarPlacar(){
        File arquivo = new File(nomeArquivo);
        if(!arquivo.exists()) return;
        
        try{
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(arquivo);
            NodeList jogadores = doc.getElementsByTagName("jogador");

            for(int i = 0; i < jogadores.getLength(); i++) {
                Node node = jogadores.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element jogador = (Element) node;
                    char id = jogador.getAttribute("id").charAt(0);
                    int valor = Integer.parseInt(jogador.getTextContent());
                    pontos.put(id, valor);
                }
            }
        }catch(Exception e){
            System.out.println("Erro ao carregar placar: " + e.getMessage());
        }
    }

    public void excluirPlacar(){
        File arquivo = new File(nomeArquivo);
        if(arquivo.exists()){
            if(arquivo.delete()){
                System.out.println("Placar final apagado.");
            }else{
                System.out.println("Não foi possível apagar o placar. (Talvez já tenha sido apagado pelo outro jogador)");
            }
        }else{
            System.out.println("Arquivo de placar não encontrado. (Talvez já tenha sido apagado)");
        }
    }

    public boolean verificarSalvo(){
        File arquivo = new File(nomeArquivo);
        boolean existe = false;
        if(arquivo.exists()){
            existe = true;
        }
        return existe;
    }
}
