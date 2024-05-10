
import antlr.Java8Lexer;
import antlr.Java8Parser;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import org.antlr.v4.gui.TreeViewer;

import java.io.*;
import java.util.Arrays;

/**
 * Clase principal para analizar el código fuente de Java y generar un árbol de sintaxis.
 */
public class Main {
    /**
     * Método principal para la aplicación.
     * @param args Argumentos de línea de comandos.
     * @throws IOException Si ocurre un error de E/S.
     */
    public static void main(String[] args) throws IOException {
        // Leer el código fuente de un archivo
        String fileName = "src/HelloWorld.java";
        String code = readFile(fileName);

        // Crear un lexer
        Java8Lexer lexer = new Java8Lexer(CharStreams.fromString(code));
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        // Crear un analizador
        Java8Parser parser = new Java8Parser(tokens);
        ParseTree tree = parser.compilationUnit();

        // Mostrar el árbol de análisis
        showParseTree(tree, parser);

        // Convertir el árbol de análisis al formato DOT
        String dot = toDot(tree, parser);

        // Escribir el archivo DOT
        String dotFileName = "syntax_tree.dot";
        writeToFile(dot, dotFileName);

        // Generar el diagrama de sintaxis usando Graphviz
        String pngFileName = "syntax_tree.png";
        generateSyntaxDiagram(dotFileName, pngFileName);

        // Mostrar los tokens encontrados
        System.out.println("Tokens encontrados:");
        for (Token token : tokens.getTokens()) {
            System.out.printf("Token: %s, Tipo: %s\n", token.getText(), Java8Lexer.VOCABULARY.getDisplayName(token.getType()));
        }
    }

    /**
     * Lee el contenido de un archivo.
     * @param fileName El nombre del archivo a leer.
     * @return El contenido del archivo.
     * @throws IOException Si ocurre un error de E/S.
     */
    private static String readFile(String fileName) throws IOException {
        InputStream inputStream = new FileInputStream(fileName);
        StringBuilder stringBuilder = new StringBuilder();
        int ch;
        while ((ch = inputStream.read()) != -1) {
            stringBuilder.append((char) ch);
        }
        inputStream.close();
        return stringBuilder.toString();
    }

    /**
     * Convierte el árbol de análisis al formato DOT.
     * @param tree El árbol de análisis.
     * @param parser El analizador.
     * @return El árbol de análisis en formato DOT.
     */
    private static String toDot(ParseTree tree, Parser parser) {
        StringBuilder buf = new StringBuilder();
        buf.append("digraph {\n");
        toDot(tree, parser, buf);
        buf.append("}\n");
        return buf.toString();
    }

    /**
     * Método recursivo para convertir el árbol de análisis al formato DOT.
     * @param tree El árbol de análisis.
     * @param parser El analizador.
     * @param buf El StringBuilder para agregar el formato DOT.
     */
    private static void toDot(ParseTree tree, Parser parser, StringBuilder buf) {
        if (tree.getChildCount() > 0) {
            for (int i = 0; i < tree.getChildCount(); i++) {
                ParseTree child = tree.getChild(i);
                String parentNodeName = getNodeName(tree, parser);
                String childNodeName = getNodeName(child, parser);
                buf.append("\"").append(parentNodeName).append("\"");
                buf.append(" -> ");
                buf.append("\"").append(childNodeName).append("\"");
                buf.append(";\n");
                toDot(child, parser, buf);
            }
        }
    }

    /**
     * Obtiene el nombre de un nodo en el árbol de análisis.
     * @param tree El árbol de análisis.
     * @param parser El analizador.
     * @return El nombre del nodo.
     */
    private static String getNodeName(ParseTree tree, Parser parser) {
        if (tree instanceof RuleNode) {
            RuleContext ruleContext = ((RuleNode) tree).getRuleContext();
            return parser.getRuleNames()[ruleContext.getRuleIndex()];
        } else {
            return tree.getText();
        }
    }

    /**
     * Muestra el árbol de análisis.
     * @param tree El árbol de análisis.
     * @param parser El analizador.
     */
    private static void showParseTree(ParseTree tree, Parser parser) {
        // Crear un visor de árboles
        TreeViewer viewer = new TreeViewer(
                Arrays.asList(parser.getRuleNames()),
                tree
        );
        viewer.open();
    }

    /**
     * Escribe contenido en un archivo.
     * @param content El contenido a escribir.
     * @param fileName El nombre del archivo a escribir.
     * @throws IOException Si ocurre un error de E/S.
     */
    private static void writeToFile(String content, String fileName) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
        writer.write(content);
        writer.close();
    }

    /**
     * Genera un diagrama de sintaxis usando Graphviz.
     * @param dotFileName El nombre del archivo DOT.
     * @param pngFileName El nombre del archivo PNG a generar.
     * @throws IOException Si ocurre un error de E/S.
     */
    private static void generateSyntaxDiagram(String dotFileName, String pngFileName) throws IOException {
        ProcessBuilder pb = new ProcessBuilder("dot", "-Tpng", "-o", pngFileName, dotFileName);
        pb.redirectErrorStream(true);
        Process p = pb.start();
        try {
            p.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
