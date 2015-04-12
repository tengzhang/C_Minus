package Parser;

/**
 * ParserTreeNode
 * 语法树的节点定义
 * @author zhangteng
 *
 */

import Semantic.SymbolTableNode;

/**
 * 非终极符例子
 * id=1,value=null,type="declationlist",lineNum=1,productionId=1,
 * parentId=0,childNum=2,childId[0]=3,childId[1]=2
 * 
 * 终极符例子
 * id=4,value="int",type="int",lineNum=1,productionId=6,
 * parentId=1,chileNum0
 * @author zhangteng
 *
 */
public class ParserTreeNode {
	
	private int id;                      //在语法树中的编号
	private String value;                //节点的值，若这个节点保存的是非终极符，则value为空
	private String type;                 //节点是非终极符还是终极符
	private int lineNum;                 //行号
	private int productionId;            //产生式编号
	private int parentId;                //父节点编号
	private int childNum;                //儿子节点个数
	private int[] childNode = new int[8];  //儿子节点编号
	private int width;                   //节点的宽度
	private int x;                       //节点的x坐标
	private int y;                       //节点的y坐标
    private int xmin;                    //节点最左x坐标
    private int xmax;                    //节点最右x坐标

    private SymbolTableNode symbolTableNode;

	public ParserTreeNode(int parentId, String value, String type, int lineNum, int productionId) {
		setParentId(parentId);
		setValue(value);
		setType(type);
		setLineNum(lineNum);
		setProductionId(productionId);
		setChildNum(0);
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public int getLineNum() {
		return lineNum;
	}
	public void setLineNum(int lineNum) {
		this.lineNum = lineNum;
	}
	
	public int getProductionId() {
		return productionId;
	}
	public void setProductionId(int productionId) {
		this.productionId = productionId;
	}
	
	public int getParentId() {
		return parentId;
	}

    public void setParentId(int parentId) {
		this.parentId = parentId;
	}
	
	public int getChildNum() {
		return childNum;
	}
	public void setChildNum(int childNum) {
		this.childNum = childNum;
	}

	public int getChildNode(int ix) {
		return childNode[ix];
	}
	public void setChild(int[] childNode) {
		this.childNode = childNode;
	}
	
	public void addChild(ParserTreeNode node) {
		childNode[childNum ++] = node.getId();
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public int getXmin() {
		return xmin;
	}
	public void setXmin(int xmin) {
		this.xmin = xmin;
	}
	public int getXmax() {
		return xmax;
	}
	public void setXmax(int xmax) {
		this.xmax = xmax;
	}

    public SymbolTableNode getSymbolTableNode() {
        return symbolTableNode;
    }

    public void setSymbolTableNode(SymbolTableNode symbolTableNode) {
        this.symbolTableNode = symbolTableNode;
    }
}

