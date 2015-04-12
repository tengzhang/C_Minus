package Semantic;

import java.util.ArrayList;
import java.util.List;

/**
 * ScopeNode
 * @author zhangteng
 *
 */

public class ScopeNode {

    private List<SymbolTableNode> symbolTable = new ArrayList<SymbolTableNode>();

    public boolean findId(String s, SymbolTableNode node) {
        if(!symbolTable.isEmpty()) {
            for(SymbolTableNode symbolNode : symbolTable) {
                if(s.equals(symbolNode.name)) {
                    node.assign(symbolNode);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean insertId(SymbolTableNode node) {
        SymbolTableNode tmp = new SymbolTableNode();
        if(findId(node.name, tmp)) {
            System.out.println();
            return false;
        } else {
            symbolTable.add(node);
            return true;
        }
    }
}
