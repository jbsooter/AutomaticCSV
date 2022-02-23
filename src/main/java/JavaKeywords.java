import java.util.ArrayList;
import java.util.Arrays;

//List comes from this Oracle Documentation
//https://docs.oracle.com/javase/tutorial/java/nutsandbolts/_keywords.html
class JavaKeywords {
    private final ArrayList<String> JAVA_KEYWORDS = new ArrayList<>(Arrays.asList(
            "abstract",
            "continue",
            "for",
            "new",
            "switch",
            "assert",
            "default",
            "goto",
            "package",
            "synchronized",
            "boolean",
            "do",
            "if",
            "private",
            "this",
            "break",
            "double",
            "implements",
            "protected",
            "throw",
            "byte",
            "else",
            "import",
            "public",
            "throws",
            "case",
            "enum",
            "instanceof",
            "return",
            "transient",
            "catch",
            "extends",
            "int",
            "short",
            "try",
            "char",
            "final",
            "interface",
            "static",
            "void",
            "class",
            "finally",
            "long",
            "strictfp",
            "volatile",
            "const",
            "float",
            "native",
            "super",
            "while",
            "true",
            "false"
    ));

    public JavaKeywords() {
    }

    public ArrayList<String> getJAVA_KEYWORDS() {
        return JAVA_KEYWORDS;
    }
}
