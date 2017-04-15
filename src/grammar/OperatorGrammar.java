package grammar;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;

import ea.FitnessFunction;

/*
 * This class defines a generic the "grammar" of operators.
 * It is able to build random operators based on the one
 * and two dimensional atomic operators as defined in the paper. 
 * */
public class OperatorGrammar<T> {
	
	private OneDimensionOperatorFactory<T> oneDimensionOperators[];
	private TwoDimensionsOperatorFactory<T> twoDimensionOperators[];
	private FitnessFunction<T> function;
	
	public OperatorGrammar(
			OneDimensionOperatorFactory<T>[] oneDimensionOperators,
			TwoDimensionsOperatorFactory<T>[] twoDimensionOperators,
			FitnessFunction<T> function) {
		
		this.oneDimensionOperators = oneDimensionOperators;
		this.twoDimensionOperators = twoDimensionOperators;
		this.function = function;
	}

	private OneDimensionOperator<T> randomOneDimensionOperator(){
		return oneDimensionOperators[ ThreadLocalRandom.current().nextInt(oneDimensionOperators.length) ].create();
	}
	
	private TwoDimensionsOperator<T> randomTwoDimensionsOperator(){
		return twoDimensionOperators[ ThreadLocalRandom.current().nextInt(twoDimensionOperators.length) ].create();
	}
	
	public GrammarNode randomTree( int maximumDepth ){
		return doRandomTree( maximumDepth );
	}
	
	private GrammarNode doRandomTree( int maximumDepth ){
		if( maximumDepth == 0 )
			return new GrammarNode( ThreadLocalRandom.current().nextBoolean() );
		
		if( ThreadLocalRandom.current().nextBoolean() ){ //One child
			return new GrammarNode( randomOneDimensionOperator() , randomTree(maximumDepth-1) );
		}else{ //Two children
			return new GrammarNode( randomTwoDimensionsOperator() , randomTree(maximumDepth-1), randomTree(maximumDepth-1) );
		}
	}
	
	/*
	 * This is the class for the actual operator.
	 * As it is recursively defined, this class
	 * defines a single node.
	 */
	public class GrammarNode {
		private OneDimensionOperator<T> f;
		private TwoDimensionsOperator<T> g;
		
		private int degree;
		private GrammarNode leftChild, rightChild;
		private boolean argument;
		
		public GrammarNode( GrammarNode node ){
			this.f = node.f;
			this.g = node.g;
			this.degree = node.degree;
			this.argument = node.argument;
			this.leftChild = node.leftChild == null ? null : new GrammarNode(node.leftChild);
			this.rightChild = node.rightChild == null ? null : new GrammarNode(node.rightChild);
		}
		
		
		public GrammarNode clone(){
			return new GrammarNode(this);
		}
		
		public void mutate(){
			GrammarNode node = randomNode();
			switch( node.degree ){
				case 2:
					node.g = randomTwoDimensionsOperator();
					break;
				case 1:
					node.f = randomOneDimensionOperator();
					break;
			}
		}
		
		public ArrayList< GrammarNode > recombine( GrammarNode tree ){
			
			GrammarNode tree1 = new GrammarNode(this);
			GrammarNode tree2 = new GrammarNode(tree);
			
			GrammarNode n1 = tree1.randomNode();
			GrammarNode n2 = tree2.randomNode();
			
			swapNodes(n1,n2);
			
			ArrayList< GrammarNode > ret = new ArrayList<OperatorGrammar<T>.GrammarNode>();
			ret.add( tree1 );
			ret.add( tree2 );
			return ret;
		}
		
		private void swapNodes( GrammarNode tree1, GrammarNode tree2 ){
	
			GrammarNode aux = new GrammarNode(false);
			aux.f = tree1.f;
			aux.g = tree1.g;
			aux.degree = tree1.degree;
			aux.leftChild = tree1.leftChild;
			aux.rightChild = tree1.rightChild;
			aux.argument = tree1.argument;
			
			tree1.f = tree2.f;
			tree1.g = tree2.g;
			tree1.degree = tree2.degree;
			tree1.leftChild = tree2.leftChild;
			tree1.rightChild = tree2.rightChild;
			tree1.argument = tree2.argument;
			
			tree2.f = aux.f;
			tree2.g = aux.g;
			tree2.degree = aux.degree;
			tree2.leftChild = aux.leftChild;
			tree2.rightChild = aux.rightChild;
			tree2.argument = aux.argument;
			
			
		}
		
		public GrammarNode randomNode(){
			GrammarNode ret = null;
			double n = 1;
			Stack<GrammarNode> st = new Stack<>();
			st.push(this);
			while( !st.empty() ){
				GrammarNode curr = st.pop();
				if( ThreadLocalRandom.current().nextDouble() <= 1.0 / n ){
					ret = curr;
				}
				
				switch (curr.degree) {
					case 2:
						st.push( curr.leftChild );
						st.push( curr.rightChild );
						break;
						
					case 1:
						st.push( curr.leftChild );
						break;
						
					default:
						break;
				}
				
				n++;
			} 
			return ret;
		}
		
		public GrammarNode(OneDimensionOperator<T> f, GrammarNode child) {
			this.f = f;
			this.g = null;
			this.degree = 1;
			this.leftChild = child;
		}
		
		public GrammarNode(TwoDimensionsOperator<T> g, GrammarNode leftChild, GrammarNode rightChild ) {
			this.g = g;
			this.f = null;
			this.degree = 2;
			this.leftChild = leftChild;
			this.rightChild = rightChild;
		}
		
		public GrammarNode(boolean argument){
			this.degree = 0; //leaf
			this.argument = argument;
		}
		
		public boolean isLeaf(){
			return degree == 0;
		}
		
		public T[] operate(T chromosome1[], T chromosome2[]){
			if( degree == 2 )
				return g.operate( 
						leftChild.operate(chromosome1, chromosome2) , 
						rightChild.operate(chromosome1, chromosome2) , 
						function);
			else if( degree == 1 )
				return f.operate( 
						leftChild.operate(chromosome1, chromosome2) , 
						function);
			else
				return argument ? chromosome1 : chromosome2;
		}
		
		private String getLabel(){
			if(degree == 2)
				return g.getName();
			else if(degree == 1)
				return f.getName();
			else 
				return Boolean.toString(argument);
		}
		
		public List<String> computeInOrder(){
			List<String> data = new ArrayList<String>();
			doInorder(data);
			return data;
		}
		
		private void doInorder(List<String> data){			
			if(leftChild != null)
				leftChild.doInorder(data);
			
			data.add(getLabel() + "_" + hashCode());
			
			if(rightChild != null)
				rightChild.doInorder(data);
		}
		
		public List<String> computePreorder(){
			List<String> data = new ArrayList<String>();
			doPreorder(data);
			return data;
		}
		
		private void doPreorder(List<String> data){
			data.add(getLabel() + "_" + hashCode());
			
			if(leftChild != null)
				leftChild.doPreorder(data);
			
			if(rightChild != null)
				rightChild.doPreorder(data);
		}
		
		
		
		public String toString(){
			return doString();
		}
		
		private String doString(){
			if( degree == 2 ){
				return g.getName()+"(" + leftChild.doString() + "," + rightChild.doString() + ")";
			}else if( degree == 1 ){
				return f.getName()+"(" + leftChild.doString() + ")";
			}else
				return Boolean.toString(argument) + "()";
		}
	}
}
