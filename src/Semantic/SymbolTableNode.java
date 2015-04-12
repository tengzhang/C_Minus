package Semantic;

import commons.DataType;
import commons.Int;

import java.util.ArrayList;
import java.util.List;

/**
 * SymbolTableNode
 * @author zhangteng
 *
 */

public class SymbolTableNode {

    public String name;                    //标识符名称
    // 属性信息
    public DataType.TypeKind type;                  //标识符类型
    public DataType.IdKind kind;                    //标识符种类
    public Int level;                      //层数
    public Int offset;                     //偏移量
    //数组有效
    public Int arraySize;                  //数组大小
    //函数标识符有效
    public List<SymbolTableNode> params;   //参数列表
    public Int code;
    public Int size;


    public SymbolTableNode() {
        name = "";
        type = DataType.TypeKind.nullTy;
        kind = DataType.IdKind.varKind;
        level = new Int(0);
        offset = new Int(0);
        arraySize = new Int(0);
        params = new ArrayList<SymbolTableNode>();
        code = new Int(0);
        size = new Int(0);
    }

    public DataType.TypeKind getParamKind(int id) {
        for(int i = 0;i < params.size(); ++i) {
            if(id == i) {
                return params.get(i).type;
            }
        }
        return  DataType.TypeKind.nullTy;
    }

    public void assign(SymbolTableNode node) {
        this.name = node.name;
        this.type = node.type;
        this.kind = node.kind;
        this.level = node.level;
        this.offset = node.offset;
        this.arraySize = node.arraySize;
        this.params = node.params;
        this.code = node.code;
        this.size = node.size;
    }
}
