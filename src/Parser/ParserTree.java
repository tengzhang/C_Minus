package Parser;

import java.util.ArrayList;
import java.awt.Color;
import java.awt.Graphics;
import java.util.*;

import javax.swing.JPanel;
/**
 * ParserTreeNode
 * 将语法树进行封装
 * @author zhangteng
 *
 */
/**
 * 使用线性结构存储树，由于事先不知道一共有多少个节点，所以不能用数组，采用ArrayList
 * @author zhangteng
 *
 */
/**
 * 添加getTreeWidth()、getTreeXY()函数
 * @author wangpengcheng
 *
 */
public class ParserTree {

	private int nodeNum;
	private ArrayList<ParserTreeNode> parserTree = new ArrayList<ParserTreeNode>();
	private int Maxheight=0;//树的深度
	
	public ParserTree() {
		nodeNum = 0;
	}
	
	public void addNode(ParserTreeNode node) {
		nodeNum ++;
		parserTree.add(node);
	}
	
	public void setNode(int ix, ParserTreeNode node) {
		parserTree.set(ix, node);
	}
	
	public int getMaxheight() {
		return (Maxheight+1)*50;
	}
	
	public ParserTreeNode getNode(int ix) {
		return parserTree.get(ix);
	}
	
	public int Size() {
		return nodeNum;
	}
	
	public int getTreeWidth(ParserTreeNode node)
	{
		if(node.getChildNum()==0){
			node.setWidth(60);
			return 60;
		}
		int tmp=(node.getChildNum()-1)*10;
		for(int i = 0;i < node.getChildNum(); ++i) {
			tmp+=getTreeWidth(this.getNode(node.getChildNode(i)));
		}
		node.setWidth(tmp);
		return tmp;
	}
	
	public void getTreeXY(ParserTreeNode node,int dep)
	{
		for(int i = 0;i < node.getChildNum(); ++i) {
			if(i==0){
				this.getNode(node.getChildNode(i)).setXmin(node.getXmin());
				this.getNode(node.getChildNode(i)).setXmax(node.getXmin()+this.getNode(node.getChildNode(i)).getWidth());
				this.getNode(node.getChildNode(i)).setX(node.getXmin()+(this.getNode(node.getChildNode(i)).getWidth()/2-30));
			}
			else
			{
				this.getNode(node.getChildNode(i)).setXmin(this.getNode(node.getChildNode(i-1)).getXmax()+10);
				this.getNode(node.getChildNode(i)).setXmax(this.getNode(node.getChildNode(i-1)).getXmax()+10+this.getNode(node.getChildNode(i)).getWidth());
				this.getNode(node.getChildNode(i)).setX(this.getNode(node.getChildNode(i-1)).getXmax()+10+(this.getNode(node.getChildNode(i)).getWidth()/2-30));
			}
			this.getNode(node.getChildNode(i)).setY(dep*40);
			getTreeXY(this.getNode(node.getChildNode(i)),dep+1);
		}
		if(Maxheight<dep)
			Maxheight=dep;
       return ;
	}

    public void output() {
        for(ParserTreeNode node : parserTree) {
            System.out.println(node.getId() + " " + node.getParentId() + " " + node.getType() + " " + node.getValue());
        }
    }
}
