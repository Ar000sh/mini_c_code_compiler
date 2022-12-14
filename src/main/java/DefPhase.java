import java.util.ArrayList;
import java.util.List;

public class DefPhase extends ClobalBaseListener{

    //public final STGroup templates = new STGroupFile("src/main/resources/test.stg");
    List<String> vars = new ArrayList<>();


    public void exitVarDecl(ClobalParser.VarDeclContext ctx) {
        vars.add(ctx.ID().getText());
    }
}
