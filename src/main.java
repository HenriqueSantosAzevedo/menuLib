import javafx.scene.control.MenuItem;
import org.codehaus.commons.compiler.CompileException;
import org.xml.sax.SAXException;
import org.yaml.snakeyaml.Yaml;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.Map;

public class main {
    private static Map<String, Object> config;

    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException, InvocationTargetException, CompileException {
        config = loadConfig();

        Menu menu = new Menu(System.getProperty("user.dir") + "/src/" + config.get("menuLink"));
    }

    public static Map<String, Object> loadConfig() throws FileNotFoundException {
        Yaml yaml = new Yaml();
        InputStream inputStream = new FileInputStream(new File(System.getProperty("user.dir")+"/src/config.yaml"));
        Map<String, Object> obj = yaml.load(inputStream);
        return obj;
    }
}
