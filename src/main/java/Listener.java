import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import java.io.IOException;

public class Listener extends ClobalBaseListener{

    public final STGroup templates = new STGroupFile("src/main/resources/testClobal.stg");
    GlobalScope globalscope;

    public int index;
    public Listener(GlobalScope globalscope) {
        this.globalscope = globalscope;
        this.index = 0;
    }

    public int getIndex() {
        this.index++;
        return index;
    }

    ParseTreeProperty<BLabel> bWert = new ParseTreeProperty<BLabel>();
    public void setBWert(ParseTree node, BLabel value) { bWert.put(node, value); }
    public BLabel getBWert(ParseTree node) { return bWert.get(node); }
    ParseTreeProperty<ST> code = new ParseTreeProperty<ST>();
    public void setCode(ParseTree node, ST value) { code.put(node, value); }
    public ST getCode(ParseTree node) { return code.get(node); }

    public void exitFunctionDecl(ClobalParser.FunctionDeclContext ctx) {
        ST block = getCode(ctx.block());
        ST result;
        if (ctx.ID().getText().equals("main")) {
            result = templates.getInstanceOf("functionMain").add("name",ctx.ID().getText()).add("block",block);
        } else {
            result = templates.getInstanceOf("function").add("name",ctx.ID().getText()).add("block",block);

        }
        setCode(ctx,result);

    }
    public void exitVarDecl(ClobalParser.VarDeclContext ctx) {
        ST result = templates.getInstanceOf("empty");
        setCode(ctx,result);
    }
    public void exitAssign(ClobalParser.AssignContext ctx) {
        setCode(ctx,getCode(ctx.assignStat()));
    }
    public void exitAssignStat(ClobalParser.AssignStatContext ctx) {
        String name = ctx.ID().getText();
        ST expr = getCode(ctx.expr());
        //int var;
        int var = globalscope.resolve(name).getIndex();
        ST result = templates.getInstanceOf("gstore").add("v",var).add("e",expr);
        setCode(ctx,result);

    }
    public void exitVar(ClobalParser.VarContext ctx) {
        String name = ctx.ID().getSymbol().getText();
        Symbol variable =  globalscope.resolve(name);
        if (variable == null || variable instanceof FunctionSymbol) {
            System.out.println("no variable with this name: " + name);
        } else {
            ST result = templates.getInstanceOf("varRef").add("v",variable.getIndex());
            setCode(ctx,result);
            //System.out.println("variable: " + variable.getName() + " type: " + variable.getType());
        }



    }
    public void exitAddSub(ClobalParser.AddSubContext ctx) {

        ST left = getCode(ctx.expr(0));
        ST right = getCode(ctx.expr(1));

        ST result;
        if ( ctx.op.getType() == ClobalParser.ADD ){
            result = templates.getInstanceOf("add").add("e1",left).add("e2",right);
        } else {
            result = templates.getInstanceOf("sub").add("e1",left).add("e2",right);
        }

        setCode(ctx,result);
    }
    public void exitMulDiv(ClobalParser.MulDivContext ctx) {
        ST left = getCode(ctx.expr(0));
        ST right = getCode(ctx.expr(1));


        if ( ctx.op.getType() == ClobalParser.MUL ){
            ST result = templates.getInstanceOf("mul").add("e1",left).add("e2",right);
            setCode(ctx,result);
        }


    }

    public void exitNegate(ClobalParser.NegateContext ctx) {
        ST left = getCode(ctx.expr());
        String intText = "-1";
        ST right = templates.getInstanceOf("const").add("val",intText);
        ST result = templates.getInstanceOf("mul").add("e1",left).add("e2",right);
        setCode(ctx,result);

    }
    public void exitPrintStat(ClobalParser.PrintStatContext ctx) {

        ST expr = getCode(ctx.expr());
        if (expr == null) {
            System.out.println("print\n expr is empty");
        } else {
            ST result = templates.getInstanceOf("print").add("e",expr);
            setCode(ctx,result);


        }
    }

    public void exitStatBlock(ClobalParser.StatBlockContext ctx) {
        setCode(ctx,getCode(ctx.block()));
    }

    public void exitIfStat(ClobalParser.IfStatContext ctx) {
        // die labels für den booleschen Ausdruck
        String bTrue = getBWert(ctx.bexpr()).getbTrue();
        String bFalse = getBWert(ctx.bexpr()).getbFalse();
        int len = ctx.stat().size();
        // den booleschen Ausdruck mit den Sprüngen
        ST bexpr = getCode(ctx.bexpr());
        ST result;
        ST stat1 = getCode(ctx.stat(0));
        // überprüfung ob es sich um ifelse handelt
        if (len > 1) {

            String bNext = "bNext" + getBWert(ctx.bexpr()).getIndex();
            ST stat2 = getCode(ctx.stat(1));
            // unbedingte Sprünge zu Ende wenn True aus gewertet wurde
            ST bNextCode = templates.getInstanceOf("trueFalse").add("value",bNext);
            result = templates.getInstanceOf("ifElse").add("be",bexpr).add("stat1",stat1).add("stat2",stat2).add("bTrue",bTrue).add("bFalse",bFalse).add("bNextCode",bNextCode).add("bNext",bNext);
        } else {
            result = templates.getInstanceOf("if").add("be",bexpr).add("stat",stat1).add("bTrue",bTrue).add("bFalse",bFalse);

        }
        setCode(ctx,result);
    }
    public void exitWhileStat(ClobalParser.WhileStatContext ctx) {

        String bTrue = getBWert(ctx.bexpr()).getbTrue();
        String bFalse = getBWert(ctx.bexpr()).getbFalse();
        String begin = "begin" + getBWert(ctx.bexpr()).getIndex();
        ST bexpr = getCode(ctx.bexpr());
        ST stat = getCode(ctx.stat());
        ST beginCode = templates.getInstanceOf("trueFalse").add("value",begin);

        ST result = templates.getInstanceOf("while");
        result.add("begin",begin).add("be",bexpr).add("bTrue",bTrue).add("beginCode",beginCode).add("bFalse",bFalse).add("stat",stat);
        setCode(ctx,result);
    }
    public void exitWhileloop(ClobalParser.WhileloopContext ctx) {
        setCode(ctx,getCode(ctx.whileStat()));
    }

    public void exitForStat(ClobalParser.ForStatContext ctx) {
        String bTrue = getBWert(ctx.bexpr()).getbTrue();
        String bFalse = getBWert(ctx.bexpr()).getbFalse();
        String begin = "begin" + getBWert(ctx.bexpr()).getIndex();
        ST bexpr = getCode(ctx.bexpr());
        ST stat = getCode(ctx.stat());
        ST assign1 = getCode(ctx.assignStat(0));
        ST assign2 = getCode(ctx.assignStat(1));
        ST beginCode = templates.getInstanceOf("trueFalse").add("value",begin);

        ST result = templates.getInstanceOf("for");
        result.add("assign1",assign1).add("begin",begin).add("be",bexpr).add("bTrue",bTrue);
        result.add("stat",stat).add("assign2",assign2).add("beginCode",beginCode).add("bFalse",bFalse);
        setCode(ctx,result);

    }


    public void exitForloop(ClobalParser.ForloopContext ctx) {
        setCode(ctx,getCode(ctx.forStat()));
    }

    public void exitIfElse(ClobalParser.IfElseContext ctx) {
        setCode(ctx,getCode(ctx.ifStat()));
    }
    public void exitBlock(ClobalParser.BlockContext ctx) {
        int len = ctx.stat().size();
        ST result = templates.getInstanceOf("block");
        for (int i = 0; i < len; i++) {
            result.add("value",getCode(ctx.stat(i)));


        }
        setCode(ctx,result);

    }
    public void exitPrint(ClobalParser.PrintContext ctx) {
        setCode(ctx,getCode(ctx.printStat()));
    }
    public void exitInt(ClobalParser.IntContext ctx) {


        String intText = ctx.INT().getText();
        ST result = templates.getInstanceOf("const").add("val",intText);
        setCode(ctx,result);

    }
    public void exitFile(ClobalParser.FileContext ctx) {

        ST result = templates.getInstanceOf("file");
        result.add("num",globalscope.symbols.size());
        int len = ctx.functionDecl().size();
        for (int i = 0; i < len; i++) {
            result.add("function",getCode(ctx.functionDecl(i)));
        }

        setCode(ctx,result);
    }

    public void enterTrue(ClobalParser.TrueContext ctx) {
        int i = getIndex();
        BLabel result = new BLabel(i);
        setBWert(ctx,result);
    }
    public void exitTrue(ClobalParser.TrueContext ctx) {
        String value = getBWert(ctx).bTrue;
        setCode(ctx,templates.getInstanceOf("trueFalse").add("value",value));
    }
    public void exitFalse(ClobalParser.FalseContext ctx) {
        String value = getBWert(ctx).bFalse;
        setCode(ctx,templates.getInstanceOf("trueFalse").add("value",value));
    }
    public void enterFalse(ClobalParser.FalseContext ctx) {
        int i = getIndex();
        BLabel result = new BLabel(i);
        setBWert(ctx,result);
    }

    public void exitReturnStat(ClobalParser.ReturnStatContext ctx) {
        ST expr = getCode(ctx.expr());
        ST result = templates.getInstanceOf("return").add("e",expr);
        setCode(ctx,result);

    }
    public void exitReturn(ClobalParser.ReturnContext ctx) {
        setCode(ctx,getCode(ctx.returnStat()));
    }
    public void exitCall(ClobalParser.CallContext ctx) {
        //ST result = templates.getInstanceOf("call");
        String name = ctx.ID().getSymbol().getText();
        Symbol func = globalscope.resolve(name);
        if (func == null || func instanceof VariableSymbol) {
            System.out.println("no such function: "+name);
        } else {
            ST result = templates.getInstanceOf("functionCall");

            result.add("name",name);
            setCode(ctx,result);
        }

    }
    public void exitFunCall(ClobalParser.FunCallContext ctx) {
        setCode(ctx,getCode(ctx.expr()));
    }

    public void enterVergleich(ClobalParser.VergleichContext ctx) {
            BLabel test = getBWert(ctx.getParent());
            BLabel result;
            if (test != null) {
                result = new BLabel(test.index);
                if (ctx.getParent() instanceof ClobalParser.NotContext) {
                    System.out.println("entered the rev section");
                    result.setbTrue(test.getbFalse());
                    result.setbFalse(test.getbTrue());
                } else {
                    result.setbTrue(test.getbTrue());
                    result.setbFalse(test.getbFalse());
                }
            } else {
                int i = getIndex();
                result = new BLabel(i);
            }

            setBWert(ctx,result);

    }
    public void exitVergleich(ClobalParser.VergleichContext ctx) {

        String bTrue = getBWert(ctx).getbTrue();
        String bFalse = getBWert(ctx).getbFalse();

        ST left = getCode(ctx.expr(0));

        ST right = getCode(ctx.expr(1));

        ST result;
        if ( ctx.op.getType() == ClobalParser.EQUALS ){
            result = templates.getInstanceOf("relop").add("e1",left).add("e2",right).add("bTrue",bTrue).add("bFalse",bFalse).add("operator","ieq");
        } else if ( ctx.op.getType() == ClobalParser.UNEQUALS ) {
            result = templates.getInstanceOf("relop").add("e1",left).add("e2",right).add("bTrue",bFalse).add("bFalse",bTrue).add("operator","ieq");
        } else if (ctx.op.getType() == ClobalParser.SMALLER) {

            result = templates.getInstanceOf("relop").add("e1",left).add("e2",right).add("bTrue",bTrue).add("bFalse",bFalse).add("operator","ilt");
        } else {
            result = templates.getInstanceOf("relop").add("e1",right).add("e2",left).add("bTrue",bTrue).add("bFalse",bFalse).add("operator","ilt");        }

        setCode(ctx,result);
    }

    public void enterNot(ClobalParser.NotContext ctx) {
        BLabel test = getBWert(ctx.getParent());
        BLabel result;
        if (test != null) {
            result = new BLabel(test.index);
            if (ctx.getParent() instanceof ClobalParser.NotContext) {
                System.out.println("entered the rev section");
                result.setbTrue(test.getbFalse());
                result.setbFalse(test.getbTrue());
            }else {
                result.setbTrue(test.getbTrue());
                result.setbFalse(test.getbFalse());
            }
        } else {
            int i = getIndex();
            result = new BLabel(i);
        }
        setBWert(ctx,result);
    }
    public void exitNot(ClobalParser.NotContext ctx) {
        setCode(ctx,getCode(ctx.bexpr()));
    }
    public void enterVergleichParens(ClobalParser.VergleichParensContext ctx) {

        BLabel test = getBWert(ctx.getParent());
        BLabel result;
        if (test != null) {
            result = new BLabel(test.index);
            if (ctx.getParent() instanceof ClobalParser.NotContext) {
                result.setbTrue(test.getbFalse());
                result.setbFalse(test.getbTrue());
            }else {
                result.setbTrue(test.getbTrue());
                result.setbFalse(test.getbFalse());
            }
        } else {
            int i = getIndex();
            result = new BLabel(i);
        }
        setBWert(ctx,result);
    }
    public void exitVergleichParens(ClobalParser.VergleichParensContext ctx) {
        setCode(ctx,getCode(ctx.bexpr()));
    }
    public void exitParens(ClobalParser.ParensContext ctx) {
        setCode(ctx,getCode(ctx.expr()));
    }

    public static void main(String[] args) throws IOException {
        String filename = "src/main/resources/ClobalProgs/not.clobal.c";
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
    }


}