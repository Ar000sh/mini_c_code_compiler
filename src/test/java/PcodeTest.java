import StackInterpreter.Interpreter;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PcodeTest {
    @ParameterizedTest
    @CsvSource({"src/main/resources/ClobalProgs/for.clobal.c,8",
                "src/main/resources/ClobalProgs/forTwice.clobal.c,64",
                "src/main/resources/ClobalProgs/funcCall.clobal.c,30",
                "src/main/resources/ClobalProgs/ifElse.clobal.c,1",
                "src/main/resources/ClobalProgs/ifgt.clobal.c,1",
                "src/main/resources/ClobalProgs/iflt.clobal.c,empty",
                "src/main/resources/ClobalProgs/ifTrueFalse.clobal.c,1",
                "src/main/resources/ClobalProgs/neq.clobal.c,1",
                "src/main/resources/ClobalProgs/not.clobal.c,4",
                "src/main/resources/ClobalProgs/printf.clobal.c,26",
                "src/main/resources/ClobalProgs/while.clobal.c,8"})
    public void test(String filename,String actual) throws Exception {
        String pcode = getPcode(filename);
        System.out.println(pcode);
        String expected = Interpreter.run2(pcode);
        //System.out.println(expected);
        if (actual.equals("empty")) {
            actual = "";

        }
        assertEquals(actual,expected);



    }
    public static String getPcode(String filename) throws IOException {
        CharStream input = CharStreams.fromFileName(filename);
        ClobalLexer lexer = new ClobalLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ClobalParser parser = new ClobalParser(tokens);
        parser.setBuildParseTree(true);
        ParseTree tree = parser.file();
        ParseTreeWalker walker = new ParseTreeWalker();
        DefPhase def = new DefPhase();
        walker.walk(def, tree);
        Listener ref = new Listener(def.globalscope);
        walker.walk(ref, tree);
        return ref.getCode(tree).render().trim();
    }
}
