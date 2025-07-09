import java.io.InputStream; // Importação importante
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class Config {
    private static String ip;
    private static int porta = -1;

    private Config() {}

    private static void readConfig() {
        try (InputStream inputStream = Config.class.getResourceAsStream("config.xml")) {
            
            if (inputStream == null) {
                System.err.println("ERRO CRÍTICO: Arquivo 'config.xml' não foi encontrado dentro da pasta 'src'.");
                return;
            }

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(inputStream);
            doc.getDocumentElement().normalize();

            Element root = doc.getDocumentElement();
            Node node = root.getElementsByTagName("servidor").item(0);

            ip = node.getAttributes().getNamedItem("ip").getNodeValue();
            porta = Integer.parseInt(node.getAttributes().getNamedItem("porta").getNodeValue());
            
            System.out.println("Arquivo de configuração lido com sucesso. Conectar em " + ip + ":" + porta);

        } catch (Exception ex) {
            System.err.println("ERRO: Ocorreu um problema ao ler ou processar o arquivo config.xml.");
            ex.printStackTrace();
        }
    }

    public static String getIp() {
        if (ip == null) {
            readConfig();
        }
        return ip;
    }

    public static int getPorta() {
        if (porta == -1) {
            readConfig();
        }
        return porta;
    }
}