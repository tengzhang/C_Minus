package Semantic;

import commons.Error;
import Parser.ParserTree;
import Parser.ParserTreeNode;
import commons.DataType;
import commons.Int;

import java.util.ArrayList;
import java.util.List;

/**
 * Semantic
 * @author zhangteng
 *
 */

public class Semantic {

    private ParserTree parserTree;
    public List<Error> errorList;
    Scope scope = new Scope();
    Int layer = new Int(0);
    public List<SymbolTableNode> symbolTable = new ArrayList<SymbolTableNode>();

    public void init(ParserTree parserTree) {
        this.parserTree = parserTree;
        errorList = new ArrayList<Error>();
        scope.newLayer();
        layer.setValue(0);
    }

    public void print() {
        for(SymbolTableNode node : symbolTable) {
            System.out.println(node.name + "\t" + node.type + "\t" + node.kind + "\t" + node.level.value + "\t" + node.offset.value);
        }
    }

    public void scanner() {
        Int offset = new Int(0);
        _program(parserTree.getNode(0), offset);
    }

    private void _program(ParserTreeNode curNode, Int offset) {
        if(curNode.getChildNum() == 0)
            return ;
        ParserTreeNode childNode0 = parserTree.getNode(curNode.getChildNode(0));
        _declaration_list(childNode0, offset);
    }

    private void _declaration_list(ParserTreeNode curNode, Int offset) {
        //设定参数偏移为0
        Int paramOff = new Int(0);
        DataType.TypeKind type = DataType.TypeKind.nullTy;
        ParserTreeNode childNode0 = parserTree.getNode(curNode.getChildNode(0));
        _declaration(childNode0, paramOff, offset, type);
        ParserTreeNode childNode1 = parserTree.getNode(curNode.getChildNode(1));
        _declaration_list_left(childNode1, paramOff, offset, type);
    }

    private void _declaration(ParserTreeNode curNode, Int paramOff, Int offset, DataType.TypeKind lastType) {
        //标识符或过程声明
        if(curNode.getChildNum() == 3) {
            lastType = DataType.TypeKind.nullTy;

            //得到类型
            ParserTreeNode childNode0 = parserTree.getNode(curNode.getChildNode(0));
            DataType.TypeKind type = _type_specifier(childNode0);

            //得到标识符名
            ParserTreeNode childNode1 = parserTree.getNode(curNode.getChildNode(1));
            String name = _IDEN(childNode1);

            //新建符号表项
            SymbolTableNode symbolTableNode = new SymbolTableNode();
            symbolTableNode.name = name;
            symbolTableNode.type = type;
            symbolTableNode.level = new Int(0);
            symbolTableNode.offset.value = offset.value;
            //得到标识符种类
            ParserTreeNode childNode2 = parserTree.getNode(curNode.getChildNode(2));
            symbolTableNode.kind = _declaration_sub(childNode2, paramOff, symbolTableNode.arraySize, symbolTableNode.params);
            //普通标识符
            if(symbolTableNode.kind == DataType.IdKind.varKind) {
                if(type == DataType.TypeKind.intTy) {
                    offset.value += DataType.INTSIZE;
                }
                if(type == DataType.TypeKind.voidTy) {
                    String msg = "语义错误:" + "标识符" + name + "类型不能为void。";
                    errorList.add(new Error(msg, curNode.getLineNum(), 0));
                }
                if(!scope.insertId(symbolTableNode)) {
                    String msg = "语义错误：" + "标识符" + name + "重复定义。";
                    errorList.add(new Error(msg, curNode.getLineNum(), 0));
                }
            }
            //数组标识符
            if(symbolTableNode.kind == DataType.IdKind.arrayKind) {
                if(type == DataType.TypeKind.intTy) {
                    offset.value += symbolTableNode.arraySize.value * DataType.INTSIZE;
                }
                if(!scope.insertId(symbolTableNode)) {
                    String msg = "语义错误：" + "标识符" + name + "重复定义。";
                    errorList.add(new Error(msg, curNode.getLineNum(), 0));
                }
            }
            //过程标识符
            if(symbolTableNode.kind == DataType.IdKind.procKind) {
                symbolTableNode.level.value = 1;
                symbolTableNode.offset.value = 0;
                //添加到符号表
                if(!scope.insertIdUpper(symbolTableNode)) {
                    String msg = "语义错误：" + "标识符" + name + "重复定义。";
                    errorList.add(new Error(msg, curNode.getLineNum(), 0));
                }
                lastType = symbolTableNode.type;
            }
            //回填到树节点中
            curNode.setSymbolTableNode(symbolTableNode);
            symbolTable.add(symbolTableNode);
        }
        //语句块声明
        else {
            layer.value ++;
            ParserTreeNode childNode0 = parserTree.getNode(curNode.getChildNode(0));
            DataType.TypeKind type = _compound_stmt(childNode0, paramOff);
            if(type!=lastType && lastType== DataType.TypeKind.intTy) {
                String msg = "语义错误：该过程返回值不匹配。";
                errorList.add(new Error(msg, curNode.getLineNum(), 0));
            }
            paramOff.value = 0;
            layer.value --;
            scope.dropLayer();
        }
    }

    private DataType.TypeKind _type_specifier(ParserTreeNode curNode) {
        ParserTreeNode childNode0 = parserTree.getNode(curNode.getChildNode(0));
        if(childNode0.getType().equals("int")) {
            return DataType.TypeKind.intTy;
        }
        return DataType.TypeKind.voidTy;
    }

    private String _IDEN(ParserTreeNode curNode) {
        return curNode.getValue();
    }

    private DataType.IdKind _declaration_sub(ParserTreeNode curNode, Int paramOff, Int arraySize, List<SymbolTableNode> params) {
        ParserTreeNode childNode0 = parserTree.getNode(curNode.getChildNode(0));
        //过程声明
        if(childNode0.getType().equals("(")) {
            //新建一层
            scope.newLayer();
            //处理过程参数
            ParserTreeNode childNode1 = parserTree.getNode(curNode.getChildNode(1));
            _params(childNode1, paramOff, params);
            return DataType.IdKind.procKind;
        }
        //普通变量声明或数组声明
        else {
            return _var_declaration_sub(childNode0, arraySize);
        }
    }

    private void _params(ParserTreeNode curNode, Int paramOff, List<SymbolTableNode> params) {
        ParserTreeNode childNode0 = parserTree.getNode(curNode.getChildNode(0));
        if(childNode0.getType() == "int") {
            //类型为整型
            DataType.TypeKind type = DataType.TypeKind.intTy;
            //获得标识符名
            ParserTreeNode childNode1 = parserTree.getNode(curNode.getChildNode(1));
            String name = _IDEN(childNode1);
            //标识符种类
            ParserTreeNode childNode2 = parserTree.getNode(curNode.getChildNode(2));
            DataType.IdKind kind = _param_sub(childNode2);
            //新建符号表项
            SymbolTableNode symbolTableNode = new SymbolTableNode();
            symbolTableNode.name = name;
            symbolTableNode.type = type;
            symbolTableNode.kind = kind;
            symbolTableNode.level = new Int(1);
            symbolTableNode.offset.value = paramOff.value;
            //参数偏移累加
            paramOff.value += DataType.INTSIZE;
            //添加到符号表
            if(!scope.insertId(symbolTableNode)) {
                String msg = "语义错误：" + "标识符" + name + "重复定义。";
                errorList.add(new Error(msg, curNode.getLineNum(), 0));
            }
            //回填到树节点中
            curNode.setSymbolTableNode(symbolTableNode);
            symbolTable.add(symbolTableNode);
            //添加到过程参数表中
            params.add(symbolTableNode);

            ParserTreeNode childNode3 = parserTree.getNode(curNode.getChildNode(3));
            _param_list_sub(childNode3, paramOff, params);
        } else {
            ParserTreeNode childNode1 = parserTree.getNode(curNode.getChildNode(1));
            _params_left(childNode0, paramOff, params);
        }
    }

    private DataType.IdKind  _param_sub(ParserTreeNode curNode) {
        if(curNode.getChildNum() == 0) {
            return DataType.IdKind.varKind;
        } else {
            return DataType.IdKind.arrayKind;
        }
    }

    private void _param_list_sub(ParserTreeNode curNode, Int paramOff, List<SymbolTableNode> params) {
        if(curNode.getChildNum() != 0) {
            ParserTreeNode childNode1 = parserTree.getNode(curNode.getChildNode(1));
            _param(childNode1, paramOff, params);
            ParserTreeNode childNode2 = parserTree.getNode(curNode.getChildNode(2));
            _param_list_sub(childNode2, paramOff, params);
        }
    }

    private void _param(ParserTreeNode curNode, Int paramOff, List<SymbolTableNode> params) {
        //得到类型
        ParserTreeNode childNode0 = parserTree.getNode(curNode.getChildNode(0));
        DataType.TypeKind type = _type_specifier(childNode0);
        //得到标识符名
        ParserTreeNode childNode1 = parserTree.getNode(curNode.getChildNode(1));
        String name = _IDEN(childNode1);
        //得到种类
        ParserTreeNode childNode2 = parserTree.getNode(curNode.getChildNode(2));
        DataType.IdKind kind = _param_sub(childNode2);
        //新建符号表项
        SymbolTableNode symbolTableNode = new SymbolTableNode();
        symbolTableNode.name = name;
        symbolTableNode.type = type;
        symbolTableNode.kind = kind;
        symbolTableNode.level = new Int(1);
        symbolTableNode.offset.value = paramOff.value;
        //参数偏移累加
        if(type == DataType.TypeKind.intTy)
            paramOff.value += DataType.INTSIZE;
        //添加到符号表中
        if(!scope.insertId(symbolTableNode)) {
            String msg = "语义错误：" + "标识符" + name + "重复定义。";
            errorList.add(new Error(msg, childNode1.getLineNum(), 0));
        }
        //回填到树节点中
        curNode.setSymbolTableNode(symbolTableNode);
        symbolTable.add(symbolTableNode);
        //添加到过程参数表中
        params.add(symbolTableNode);
    }

    private void _params_left(ParserTreeNode curNode, Int paramOff, List<SymbolTableNode> params) {
        if(curNode.getChildNum() != 0) {
            //标识符类型
            DataType.TypeKind type = DataType.TypeKind.voidTy;
            //标识符名字
            ParserTreeNode childNode0 = parserTree.getNode(curNode.getChildNode(0));
            String name = _IDEN(childNode0);
            //标识符种类
            ParserTreeNode childNode1 = parserTree.getNode(curNode.getChildNode(1));
            DataType.IdKind kind = _param_sub(childNode1);
            //新建符号表项
            SymbolTableNode symbolTableNode = new SymbolTableNode();
            symbolTableNode.name = name;
            symbolTableNode.type = type;
            symbolTableNode.kind = kind;
            symbolTableNode.level = new Int(1);
            symbolTableNode.offset.value = paramOff.value;
            //添加到符号表中
            if(!scope.insertId(symbolTableNode)) {
                String msg = "语义错误：" + "标识符" + name + "重复定义。";
                errorList.add(new Error(msg, childNode1.getLineNum(), 0));
            }
            //回填到树节点中
            curNode.setSymbolTableNode(symbolTableNode);
            symbolTable.add(symbolTableNode);
            //添加到参数表中
            params.add(symbolTableNode);

            ParserTreeNode childNode2 = parserTree.getNode(curNode.getChildNode(2));
            _param_list_sub(childNode2, paramOff, params);
        }
    }

    private DataType.IdKind _var_declaration_sub(ParserTreeNode curNode, Int arraySize) {
        ParserTreeNode childNode0 = parserTree.getNode(curNode.getChildNode(0));
        if(childNode0.getType().equals(";")) {
            return DataType.IdKind.varKind;
        } else {
            ParserTreeNode childNode1 = parserTree.getNode(curNode.getChildNode(1));
            arraySize.value = Integer.parseInt(childNode1.getValue());
            return  DataType.IdKind.arrayKind;
        }
    }

    private DataType.TypeKind _compound_stmt(ParserTreeNode curNode, Int offset) {
        //声明内容
        ParserTreeNode childNode1 = parserTree.getNode(curNode.getChildNode(1));
        _local_declarations(childNode1, offset);
        //语句内容
        ParserTreeNode childNode2 = parserTree.getNode(curNode.getChildNode(2));
        return _statement_list(childNode2, offset);
    }

    private void _local_declarations(ParserTreeNode curNode, Int offset) {
        if(curNode.getChildNum() != 0) {
            ParserTreeNode childNode0 = parserTree.getNode(curNode.getChildNode(0));
            _var_declaration(childNode0, offset);
            ParserTreeNode childNode1 = parserTree.getNode(curNode.getChildNode(1));
            _local_declarations(childNode1, offset);
        }
    }

    private void _var_declaration(ParserTreeNode curNode, Int offset) {
        //标识符类型
        ParserTreeNode childNode0 = parserTree.getNode(curNode.getChildNode(0));
        DataType.TypeKind type = _type_specifier(childNode0);
        //标识符名字
        ParserTreeNode childNode1 = parserTree.getNode(curNode.getChildNode(1));
        String name = _IDEN(childNode1);
        //标识符种类，只能是普通标识符和数组标识符
        Int arraySize = new Int(0);
        ParserTreeNode childNode2 = parserTree.getNode(curNode.getChildNode(2));
        DataType.IdKind kind = _var_declaration_sub(childNode2, arraySize);
        //新建符号表项
        SymbolTableNode symbolTableNode = new SymbolTableNode();
        symbolTableNode.name = name;
        symbolTableNode.type = type;
        symbolTableNode.kind = kind;
        symbolTableNode.level.value = layer.value;
        symbolTableNode.offset.value = offset.value;
        symbolTableNode.arraySize.value = arraySize.value;
        //普通标识符
        if(kind == DataType.IdKind.varKind) {
            if(type == DataType.TypeKind.intTy)
                offset.value += DataType.INTSIZE;
        }
        //数组标识符
        if(kind == DataType.IdKind.arrayKind) {
            if(type == DataType.TypeKind.intTy)
                offset.value += arraySize.value*DataType.INTSIZE;
        }
        //添加到符号表
        if(!scope.insertId(symbolTableNode)) {
            String msg = "语义错误：" + "标识符" + name + "重复定义。";
            errorList.add(new Error(msg, childNode1.getLineNum(), 0));
        }
        //回填到树节点
        curNode.setSymbolTableNode(symbolTableNode);
        symbolTable.add(symbolTableNode);
    }

    private DataType.TypeKind _statement_list(ParserTreeNode curNode, Int offset) {
        if(curNode.getChildNum() != 0) {
            ParserTreeNode childNode0 = parserTree.getNode(curNode.getChildNode(0));
            DataType.TypeKind type1 = _statement(childNode0, offset);
            ParserTreeNode childNode1 = parserTree.getNode(curNode.getChildNode(1));
            DataType.TypeKind type2 = _statement_list(childNode1, offset);
            if(type1 != DataType.TypeKind.nullTy) {
                return type1;
            }
            if(type2 != DataType.TypeKind.nullTy) {
                return type2;
            }
            return DataType.TypeKind.nullTy;
        }
        return DataType.TypeKind.nullTy;
    }

    private DataType.TypeKind _statement(ParserTreeNode curNode, Int offset) {
        ParserTreeNode childNode0 = parserTree.getNode(curNode.getChildNode(0));
        if(childNode0.getType().equals("expression-stmt")) {
            _expression_stmt(childNode0);
            return DataType.TypeKind.nullTy;
        }
        if(childNode0.getType().equals("compound-stmt")) {
            layer.value ++;
            scope.newLayer();
            _compound_stmt(childNode0, offset);
            scope.dropLayer();
            layer.value --;
            return DataType.TypeKind.nullTy;
        }
        if(childNode0.getType().equals("selection-stmt")) {
            _selection_stmt(childNode0, offset);
            return DataType.TypeKind.nullTy;
        }
        if(childNode0.getType().equals("iteration-stmt")) {
            _iteration_stmt(childNode0, offset);
            return DataType.TypeKind.nullTy;
        }
        if(childNode0.getType().equals("return-stmt")) {
            return _return_stmt(childNode0);
        }
        return DataType.TypeKind.nullTy;
    }

    private void _expression_stmt(ParserTreeNode curNode) {
        ParserTreeNode childNode0 = parserTree.getNode(curNode.getChildNode(0));
        if(childNode0.getType() != ";") {
            _expression(childNode0);
        }
    }

    private DataType.TypeKind _expression(ParserTreeNode curNode) {
        //遇到使用标识符
        ParserTreeNode childNode0 = parserTree.getNode(curNode.getChildNode(0));
        if(childNode0.getType().equals("IDENTIFIER")) {
            //得到标识符名字
            String name = _IDEN(childNode0);
            //从符号表中得到该标识符的信息
            SymbolTableNode symbolTableNode = new SymbolTableNode();
            if(!scope.findId(name, symbolTableNode)) {
                String msg = "语义错误：" + "标识符" + name + "未声明。";
                errorList.add(new Error(msg, childNode0.getLineNum(), 0));
            } else{
                childNode0.setSymbolTableNode(symbolTableNode);
            }
            //判断使用是否符合语义信息
            ParserTreeNode childNode1 = parserTree.getNode(curNode.getChildNode(1));
            _expression_sub(childNode1, symbolTableNode);
            return symbolTableNode.type;
        }
        if(childNode0.getType().equals("(")) {
            //括号中表达式
            ParserTreeNode childNode1 = parserTree.getNode(curNode.getChildNode(1));
            DataType.TypeKind type1 = _expression(childNode1);
            //表达式后面部分
            ParserTreeNode childNode3 = parserTree.getNode(curNode.getChildNode(3));
            DataType.TypeKind type2 = _expression_none(childNode3);
            if(type1!=type2 && type1!= DataType.TypeKind.nullTy && type2!= DataType.TypeKind.nullTy) {
                String msg = "语义错误:" + "类型" + type1 + "与类型" + type2 + "不兼容。";
                errorList.add(new Error(msg, childNode0.getLineNum(), 0));
            }
            return type1;
        }
        if(childNode0.getType() == "NUMBER") {
            ParserTreeNode childNode1 = parserTree.getNode(curNode.getChildNode(1));
            DataType.TypeKind type = _expression_none(childNode1);
            if(type == DataType.TypeKind.voidTy) {
                String msg = "语义错误:" + "类型\"int\"与类型\"void\"不兼容。";
                errorList.add(new Error(msg, childNode0.getLineNum(), 0));
            }
            return DataType.TypeKind.intTy;
        }
        return DataType.TypeKind.nullTy;

    }

    private void _expression_sub(ParserTreeNode curNode, SymbolTableNode symbolTableNode) {
        //该标识符是普通标识符或数组
        ParserTreeNode childNode0 = parserTree.getNode(curNode.getChildNode(0));
        if(childNode0.getType() == "var-sub") {
            //确定是普通标识符还是数组标识符
            _var_sub(childNode0, symbolTableNode);
            //分析右部表达式，并检查兼容性
            ParserTreeNode childNode1 = parserTree.getNode(curNode.getChildNode(1));
            DataType.TypeKind type = _expression_sub_sub(childNode1);
            if(symbolTableNode.type!=type && type!= DataType.TypeKind.nullTy && symbolTableNode.type!= DataType.TypeKind.nullTy) {
                String msg = "语义错误:" + "类型" + symbolTableNode.type + "与类型" + type +"不兼容。";
                errorList.add(new Error(msg, childNode0.getLineNum(), 0));
            }
        }
        //该标识符是过程标识符
        else {
            //获取过程调用参数
            List<DataType.TypeKind> procParams = new ArrayList<DataType.TypeKind>();
            ParserTreeNode childNode1 = parserTree.getNode(curNode.getChildNode(1));
            _args(childNode1, procParams);
            //该标识符是否为过程标识符
            if(symbolTableNode.kind!= DataType.IdKind.procKind && !symbolTableNode.name.equals("")) {
                String msg = "语义错误:" + "标识符" + symbolTableNode.name + "当作过程标识符使用。";
                errorList.add(new Error(msg, childNode0.getLineNum(), 0));
            }
            //参数匹配判定
            //参数个数
            if(procParams.size() != symbolTableNode.params.size()) {
                String msg = "语义错误:" + "过程" + symbolTableNode.name + "调用时参数个数不匹配。";
                errorList.add(new Error(msg, childNode0.getLineNum(), 0));
            }
            else {
                //参数类型
                for(int i = 0;i < procParams.size(); ++i) {
                    DataType.TypeKind type = procParams.get(i);
                    if(type != symbolTableNode.getParamKind(i)) {
                        String msg = "语义错误:" + "过程" + symbolTableNode.name + "调用是第" + (i+1) + "个参数类型不匹配。";
                        errorList.add(new Error(msg, childNode0.getLineNum(), 0));
                    }
                }
            }
            ParserTreeNode childNode3 = parserTree.getNode(curNode.getChildNode(3));
            _expression_none(childNode3);
        }
    }

    private void _var_sub(ParserTreeNode curNode, SymbolTableNode symbolTableNode) {
        //数组标识符
        if(curNode.getChildNum() != 0) {
            //将普通标示符当作数组使用
            if(symbolTableNode.kind== DataType.IdKind.varKind && !symbolTableNode.name.equals("")) {
                System.out.println(curNode.getType() + " " + curNode.getValue() + " " + curNode.getChildNum());
                ParserTreeNode childNode0 = parserTree.getNode(curNode.getChildNode(0));
                String msg = "语义错误:" + "普通标识符" + symbolTableNode.name + "当作数组标识符使用。";
                errorList.add(new Error(msg, childNode0.getLineNum(), 0));
            }
            //将过程标识符当作数组使用
            if(symbolTableNode.kind== DataType.IdKind.procKind && !symbolTableNode.name.equals("")) {
                ParserTreeNode childNode0 = parserTree.getNode(curNode.getChildNode(0));
                String msg = "语义错误:" + "过程标识符" + symbolTableNode.name + "当作数组标识符使用。";
                errorList.add(new Error(msg, childNode0.getLineNum(), 0));
            }
            ParserTreeNode childNode1 = parserTree.getNode(curNode.getChildNode(1));
            _expression(childNode1);
        }
        //普通标识符
        else {
            //将数组标识符当作普通标识符
            if(symbolTableNode.kind== DataType.IdKind.arrayKind && !symbolTableNode.name.equals("")) {
                ParserTreeNode childNode0 = parserTree.getNode(curNode.getChildNode(0));
                String msg = "语义错误:" + "数组标识符" + symbolTableNode.name + "当作普通标识符使用。";
                System.out.println("lineNum:---" + childNode0.getLineNum());
                errorList.add(new Error(msg, childNode0.getLineNum(), 0));
            }
            //将过程标识符当作普通标识符
            if(symbolTableNode.kind== DataType.IdKind.procKind && !symbolTableNode.name.equals("")) {
                ParserTreeNode childNode0 = parserTree.getNode(curNode.getChildNode(0));
                String msg = "语义错误:" + "过程标识符" + symbolTableNode.name + "当作普通标识符使用。";
                errorList.add(new Error(msg, childNode0.getLineNum(), 0));
            }
        }
    }

    private DataType.TypeKind _expression_sub_sub(ParserTreeNode curNode) {
        //赋值操作
        ParserTreeNode childNode0 = parserTree.getNode(curNode.getChildNode(0));
        if(childNode0.getType() == "=") {
            ParserTreeNode childNode1 = parserTree.getNode(curNode.getChildNode(1));
            return _expression(childNode1);
        }
        //非赋值操作
        else {
            return _expression_none(childNode0);
        }
    }

    private DataType.TypeKind _expression_none(ParserTreeNode curNode) {
        ParserTreeNode childNode0 = parserTree.getNode(curNode.getChildNode(0));
        DataType.TypeKind type1 = _term_sub(childNode0);
        ParserTreeNode childNode1 = parserTree.getNode(curNode.getChildNode(1));
        DataType.TypeKind type2 = _addictive_expression_sub(childNode1);
        if(type1!=type2 && type1!= DataType.TypeKind.nullTy && type2!= DataType.TypeKind.nullTy) {
            String msg = "语义错误:" + "类型" + type1 + "与类型" + type2 + "不兼容。";
            errorList.add(new Error(msg, childNode0.getLineNum(), 0));
        }
        ParserTreeNode childNode2 = parserTree.getNode(curNode.getChildNode(2));
        DataType.TypeKind type3 = _simple_expresiion_sub(childNode2);
        if(type2!=type3 && type2!=DataType.TypeKind.nullTy && type3!=DataType.TypeKind.nullTy) {
            String msg = "语义错误:" + "类型" + type2 + "与类型" + type3 + "不兼容。";
            errorList.add(new Error(msg, childNode0.getLineNum(), 0));
        }
        return type1;
    }


    private DataType.TypeKind _term_sub(ParserTreeNode curNode) {
        if(curNode.getChildNum() != 0) {
            ParserTreeNode childNode1 = parserTree.getNode(curNode.getChildNode(1));
            DataType.TypeKind type1 = _factor(childNode1);
            ParserTreeNode childNode2 = parserTree.getNode(curNode.getChildNode(2));
            DataType.TypeKind type2 = _term_sub(childNode2);
            if(type1!=type2 && type1!=DataType.TypeKind.nullTy && type2!=DataType.TypeKind.nullTy) {
                ParserTreeNode childNode0 = parserTree.getNode(curNode.getChildNode(0));
                String msg = "语义错误:" + "类型" + type1 + "与类型" + type2 + "不兼容。";
                errorList.add(new Error(msg, childNode0.getLineNum(), 0));
            }
            return type1;
        }
        else {
            return DataType.TypeKind.nullTy;
        }
    }

    private DataType.TypeKind _factor(ParserTreeNode curNode) {
        ParserTreeNode childNode0 = parserTree.getNode(curNode.getChildNode(0));
        if(childNode0.getType().equals("(")) {
            ParserTreeNode childNode1 = parserTree.getNode(curNode.getChildNode(1));
            return _expression(childNode1);
        }
        //遇到使用标识符
        if(childNode0.getType().equals("IDENTIFIER")) {
            //得到标识符名字
            String name = _IDEN(childNode0);
            //从符号表中得到该标识符信息
            SymbolTableNode symbolTableNode = new SymbolTableNode();
            if(!scope.findId(name, symbolTableNode)) {
                String msg = "语法错误:" + "标识符" + name + "未声明。";
                errorList.add(new Error(msg, childNode0.getLineNum(), 0));
            }
            else {
                childNode0.setSymbolTableNode(symbolTableNode);
            }
            //判断使用是否符合语义信息
            ParserTreeNode childNode1 = parserTree.getNode(curNode.getChildNode(1));
            _factor_sub(childNode1, symbolTableNode);
            return symbolTableNode.type;
        }
        if(childNode0.getType().equals("NUMBER")) {
            return DataType.TypeKind.intTy;
        }
        return DataType.TypeKind.nullTy;
    }

    private void _factor_sub(ParserTreeNode curNode, SymbolTableNode symbolTableNode) {
        //该标识符是普通标识符或数组
        ParserTreeNode childNode0 = parserTree.getNode(curNode.getChildNode(0));
        if(childNode0.getType().equals("var-sub")) {
            //确定该标识符是普通标示符还是数组
            _var_sub(childNode0, symbolTableNode);
        }
        //该标识符是过程标识符
        else {
            //获取过程调用参数
            List<DataType.TypeKind> procParams = new ArrayList<DataType.TypeKind>();
            _args(curNode, procParams);
            //参数匹配判定
            //参数个数
            if(procParams.size() != symbolTableNode.params.size()) {
                String msg = "语义错误:" + "过程" + symbolTableNode.name + "调用时参数个数不匹配。";
                errorList.add(new Error(msg, childNode0.getLineNum(), 0));
            }
            else {
                //参数类型
                for(int i = 0;i < procParams.size(); ++i) {
                    DataType.TypeKind type = procParams.get(i);
                    if(type != symbolTableNode.getParamKind(i)) {
                        String msg = "语义错误:" + "过程" + symbolTableNode.name + "调用是第" + i+1 + "个参数类型不匹配。";
                        errorList.add(new Error(msg, childNode0.getLineNum(), 0));
                    }
                }
            }
        }
    }

    private void _args(ParserTreeNode curNode, List<DataType.TypeKind> procParams) {
        //参数不为空
        if(curNode.getChildNum() != 0) {
            ParserTreeNode childNode0 = parserTree.getNode(curNode.getChildNode(0));
            _arg_list(childNode0, procParams);
        }
    }

    private void _arg_list(ParserTreeNode curNode, List<DataType.TypeKind> procParams) {
        //添加第一个参数
        ParserTreeNode childNode0 = parserTree.getNode(curNode.getChildNode(0));
        DataType.TypeKind type = _expression(childNode0);
        procParams.add(type);
        //添加后续参数
        ParserTreeNode childNode1 = parserTree.getNode(curNode.getChildNode(1));
        _arg_list_left(childNode1, procParams);
    }

    private void _arg_list_left(ParserTreeNode curNode, List<DataType.TypeKind> procParams) {
        if(curNode.getChildNum() != 0) {
            ParserTreeNode childNode1 = parserTree.getNode(curNode.getChildNode(1));
            DataType.TypeKind type = _expression(childNode1);
            procParams.add(type);
            ParserTreeNode childNode2 = parserTree.getNode(curNode.getChildNode(2));
            _arg_list_left(childNode2, procParams);
        }
    }

    private DataType.TypeKind _addictive_expression_sub(ParserTreeNode curNode) {
        if(curNode.getChildNum() != 0) {
            ParserTreeNode childNode1 = parserTree.getNode(curNode.getChildNode(1));
            DataType.TypeKind type1 = _term(childNode1);
            ParserTreeNode childNode2 = parserTree.getNode(curNode.getChildNode(2));
            DataType.TypeKind type2 = _addictive_expression_sub(childNode2);
            System.out.println("_addictive_expression_sub:" + type1 + " " + type2);
            if(type1!=type2 && type1!= DataType.TypeKind.nullTy && type2 != DataType.TypeKind.nullTy) {
                String msg = "语义错误:" + "类型" + type1 + "与类型" + type2 + "不兼容。";
                ParserTreeNode childNode0 = parserTree.getNode(curNode.getChildNode(0));
                errorList.add(new Error(msg, childNode0.getLineNum(), 0));
            }
            return type1;
        }
        else {
            return DataType.TypeKind.nullTy;
        }
    }

    private DataType.TypeKind _term(ParserTreeNode curNode) {
        ParserTreeNode childNode0 = parserTree.getNode(curNode.getChildNode(0));
        DataType.TypeKind type1 = _factor(childNode0);
        ParserTreeNode childNode1 = parserTree.getNode(curNode.getChildNode(1));
        DataType.TypeKind type2 = _term_sub(childNode1);
        if(type1!=type2 && type1!= DataType.TypeKind.nullTy && type2!= DataType.TypeKind.nullTy) {
            String msg = "语义错误:" + "类型" + type1 + "与类型" + type2 + "不兼容。";
            errorList.add(new Error(msg, childNode0.getLineNum(), 0));
        }
        return type1;
    }

    private DataType.TypeKind _simple_expresiion_sub(ParserTreeNode curNode) {
        if(curNode.getChildNum() != 0) {
            ParserTreeNode childNode1 = parserTree.getNode(curNode.getChildNode(1));
            return _addictive_expression(childNode1);
        }
        else {
            return DataType.TypeKind.nullTy;
        }
    }

    private DataType.TypeKind _addictive_expression(ParserTreeNode curNode) {
        ParserTreeNode childNode0 = parserTree.getNode(curNode.getChildNode(0));
        DataType.TypeKind type1 = _term(childNode0);
        ParserTreeNode childNode1 = parserTree.getNode(curNode.getChildNode(1));
        DataType.TypeKind type2 = _addictive_expression_sub(childNode1);
        if(type1!=type2 && type1!= DataType.TypeKind.nullTy && type2!= DataType.TypeKind.nullTy) {
            String msg = "语义错误:" + "类型" + type1 + "与类型" + type2 + "不兼容。";
            errorList.add(new Error(msg, childNode0.getLineNum(), 0));
        }
        return type1;
    }

    private void _selection_stmt(ParserTreeNode curNode, Int offset) {
        ParserTreeNode childNode2 = parserTree.getNode(curNode.getChildNode(2));
        DataType.TypeKind type = _expression(curNode);
        //选择语句条件表达式不能为空类型
        if(type == DataType.TypeKind.voidTy) {
            ParserTreeNode childNode0 = parserTree.getNode(curNode.getChildNode(0));
            String msg = "语义错误:" + "条件语句条件表达式类型错误。";
            errorList.add(new Error(msg, childNode0.getLineNum(), 0));
        }
        ParserTreeNode childNode4 = parserTree.getNode(curNode.getChildNode(4));
        _statement(childNode4, offset);
        ParserTreeNode childNode5 = parserTree.getNode(curNode.getChildNode(5));
        _selection_stmt_sub(childNode5, offset);
    }

    private void _selection_stmt_sub(ParserTreeNode curNode, Int offset) {
        if(curNode.getChildNum() != 0) {
            ParserTreeNode childNode1 = parserTree.getNode(curNode.getChildNode(1));
            _statement(childNode1, offset);
        }
    }

    private void _iteration_stmt(ParserTreeNode curNode, Int offset) {
        ParserTreeNode childNode2 = parserTree.getNode(curNode.getChildNode(2));
        DataType.TypeKind type = _expression(childNode2);
        //循环语句条件表达式不能为空类型
        if(type == DataType.TypeKind.voidTy) {
            ParserTreeNode childNode0 = parserTree.getNode(curNode.getChildNode(0));
            String msg = "语义错误:" + "循环语句条件表达式类型错误。";
            errorList.add(new Error(msg, childNode0.getLineNum(), 0));
        }
        ParserTreeNode childNode4 = parserTree.getNode(curNode.getChildNode(4));
        _statement(childNode4, offset);
    }

    private DataType.TypeKind _return_stmt(ParserTreeNode curNode) {
        ParserTreeNode childNode1 = parserTree.getNode(curNode.getChildNode(1));
        return _return_stmt_sub(childNode1);
    }

    private DataType.TypeKind _return_stmt_sub(ParserTreeNode curNode) {
        ParserTreeNode childNode0 = parserTree.getNode(curNode.getChildNode(0));
        if(childNode0.getType() != ";") {
            return _expression(childNode0);
        }
        else {
            return DataType.TypeKind.nullTy;
        }
    }

    private void _declaration_list_left(ParserTreeNode curNode, Int paramOff, Int offset, DataType.TypeKind lastTy) {
        if(curNode.getChildNum() != 0) {
            ParserTreeNode childNode0 = parserTree.getNode(curNode.getChildNode(0));
            _declaration(childNode0, paramOff,offset, lastTy);
            ParserTreeNode childNode1 = parserTree.getNode(curNode.getChildNode(1));
            _declaration_list_left(childNode1, paramOff, offset, lastTy);
        }
    }
}

