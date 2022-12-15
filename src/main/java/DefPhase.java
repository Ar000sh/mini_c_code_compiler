import java.util.ArrayList;
import java.util.List;

public class DefPhase extends ClobalBaseListener{

    //public final STGroup templates = new STGroupFile("src/main/resources/test.stg");

    GlobalScope globalscope = new GlobalScope();


    public void exitVarDecl(ClobalParser.VarDeclContext ctx) {
        int index = globalscope.getNextAvailableIndex();
        String name = ctx.ID().getText();
        String type = ctx.type().getText();
        VariableSymbol var = new VariableSymbol(name,type,index);
        globalscope.define(var);
    }

    public void exitFunctionDecl(ClobalParser.FunctionDeclContext ctx) {
        String name = ctx.ID().getText();
        String type = ctx.type().getText();
        FunctionSymbol sym = new FunctionSymbol(name,type);
        globalscope.define(sym);
    }
}
