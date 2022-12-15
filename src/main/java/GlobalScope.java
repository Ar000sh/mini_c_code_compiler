import java.util.LinkedHashMap;
import java.util.Map;

public class GlobalScope {

    int index;

    public GlobalScope() {
        this.index = -1;
    }

    public int getNextAvailableIndex() {
        this.index++;
        return this.index;
    }
    Map<String, Symbol> symbols = new LinkedHashMap<String, Symbol>();

    public Symbol resolve(String name) {
        return symbols.get(name); // not found
    }
    public void define(Symbol sym) {
        symbols.put(sym.name, sym);
    }
}
