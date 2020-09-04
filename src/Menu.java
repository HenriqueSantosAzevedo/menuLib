import org.codehaus.commons.compiler.CompileException;
import org.codehaus.janino.ScriptEvaluator;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import javax.xml.parsers.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Scanner;

class Menu{
    private String path;
    private int depth = 0;
    private Element MenuDoc;
    private Menu parent = null;

    public Menu(String path, Menu parent) throws IOException, SAXException, ParserConfigurationException, InvocationTargetException, CompileException {
        this.path = path;
        this.parent = parent;
        this.MenuDoc = toMenu();
        updateDepth();
        this.show(this.MenuDoc);
    }
    public Menu(String path) throws IOException, SAXException, ParserConfigurationException, InvocationTargetException, CompileException {
        this.path = path;
        this.MenuDoc = toMenu();
        this.show(this.MenuDoc);
    }
    public Menu(Element elm, Menu parent) throws IOException, SAXException, ParserConfigurationException, InvocationTargetException, CompileException {
        this.MenuDoc = elm;
        this.parent = parent;
        this.show(this.MenuDoc);
        updateDepth();
    }
    public Menu(Element elm) throws IOException, SAXException, ParserConfigurationException, InvocationTargetException, CompileException {
        this.MenuDoc = elm;
        this.show(this.MenuDoc);
    }

    private static int readInput() throws IOException {
        int input;
        try {
            input = Integer.parseInt(new BufferedReader(new InputStreamReader(System.in)).readLine());
        } catch (NumberFormatException e) {
            System.err.println("Please enter a number");
            input = readInput();
        }
        return input;
    }

    private Element toMenu() throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new File(this.path));
        document.getDocumentElement().normalize();
        return document.getDocumentElement();
    }

    private Node[] toSingleNodeList(@NotNull Element elm){
        Node[] nodes = new Node[0];
        NodeList nList = elm.getChildNodes();
        for (int temp = 0; temp < nList.getLength(); temp++)
        {
            Node node = nList.item(temp);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                nodes = Arrays.copyOf(nodes, nodes.length + 1);
                nodes[nodes.length-1] = node;
            }
        }
        return nodes;
    }

    private void show(Element elm) throws IOException, ParserConfigurationException, SAXException, InvocationTargetException, CompileException {
        Node[] menu = toSingleNodeList(elm);
        StringBuilder spacer = new StringBuilder();

        for(int j = 0; j < this.depth; j++) {
            spacer.append("\t");
        }

        int i = 0;
        for (i = 0; i < menu.length; i++){
            Element eElement = (Element) menu[i];
            System.out.println(spacer.toString() + i + ". " +eElement.getAttribute("text"));
        }
        if(this.parent != null){
            System.out.println(spacer.toString() + (i) + ". <-");
        }


        int input = readInput();
        if(input>=0 && input < menu.length) {
            HandleInput(menu[input]);
            show(elm);
        }else if(input == menu.length && this.parent != null) {
            this.parent.show(this.parent.MenuDoc);
        }else {
            if(this.parent != null){
                System.err.println(spacer.toString() + "You must select between 0 and " + menu.length);
            }else {
                System.err.println(spacer.toString() + "You must select between 0 and " + (menu.length -1));
            }
            show(elm);
        }
    }

    private void HandleInput(Node menu) throws IOException, ParserConfigurationException, SAXException, InvocationTargetException, CompileException {
        Node[] nodes = toSingleNodeList((Element) menu);
        Element element = (Element) nodes[0];
        switch (element.getTagName()) {
            case "menu":
                if(!element.getAttribute("src").equals("")) {
                    new Menu(System.getProperty("user.dir") + "/src/custom/" + element.getAttribute("src"), this);
                }else {
                    new Menu((Element) element.getChildNodes(), this);
                }
                break;
            case "description":
                System.out.println(element.getTextContent());
                break;
            case "executable":
                StringBuilder sb = new StringBuilder();
                if(!element.getAttribute("src").equals("")) {
                    File myObj = new File(System.getProperty("user.dir") + "/src/custom/" + element.getAttribute("src"));
                    Scanner myReader = new Scanner(myObj);
                    while (myReader.hasNextLine()) {
                        String data = myReader.nextLine();
                        sb.append(data);
                    }
                    myReader.close();
                }

                ScriptEvaluator ev = new ScriptEvaluator();
                ev.cook(sb.toString());
                ev.evaluate(null);

                break;
            default:
                this.parent.show(this.parent.MenuDoc);
                break;
        }
    }

    public int getDepth() {
        return this.depth;
    }
    private void updateDepth() {
        this.depth = this.parent.getDepth() + 1;
    }
}