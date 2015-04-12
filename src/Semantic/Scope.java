package Semantic;

import java.util.Stack;

/**
 * Scope
 * @author zhangteng
 *
 */

public class Scope {

    private Stack<ScopeNode> analyseScope = new Stack<ScopeNode>();

    public void newLayer() {
        ScopeNode node = new ScopeNode();
        analyseScope.push(node);
    }

    public boolean dropLayer() {
        if(analyseScope.empty())
            return false;
        else {
            analyseScope.pop();
            return true;
        }
    }

    public boolean findId(String str, SymbolTableNode node) {
        for(ScopeNode scopeNode : analyseScope) {
            if(scopeNode.findId(str, node))   {
                return true;
            }
        }
        return false;
    }

    public boolean insertId(SymbolTableNode node) {
        ScopeNode scopeNode = analyseScope.peek();
        return scopeNode.insertId(node);
    }

    public boolean insertIdUpper(SymbolTableNode node) {
        ScopeNode tmp = analyseScope.peek();    analyseScope.pop();
        ScopeNode tmp1 = analyseScope.peek();
        boolean ok = false;
        if(tmp1.insertId(node)) {
            ok = true;
        }
        analyseScope.push(tmp);
        return ok;
    }
}
