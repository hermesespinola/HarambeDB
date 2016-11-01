package structures.matrix;

import java.util.ListIterator;
import structures.list.DoublyLinkedList;
import structures.node.SparseNode;

public class SparseMatrix<T> implements Matrix<T> {
  private static class SparseMatrixNode<T> extends SparseNode<T> {
    int col;
    private static final long serialVersionUID = 12l;

    SparseMatrixNode(int col, int row, T val) {
      super(row, val);
      this.col = col;
    }

    public int row() {
      return this.index;
    }

    int column() {
      return this.col;
    }

    public String toString() {
      return '[' + this.row() + ", " + this.column() + "] = " + this.getValue();
    }
  }
  private int numCols;
  private int numRows;
	private T zero;
	private DoublyLinkedList<SparseMatrixNode<T>> ch;

	public SparseMatrix(T zeroElement, int numCols, int numRows) {
		this.ch = new DoublyLinkedList<SparseMatrixNode<T>>();
		this.zero = zeroElement;
		this.numRows = numRows;
		this.numCols = numCols;
	}

	public SparseMatrix(T[][] matrix, T zeroElement) {
		this.ch = new DoublyLinkedList<SparseMatrixNode<T>>();
		this.zero = zeroElement;
		this.numRows = matrix.length;
		this.numCols = matrix[0].length;

		for (int i = 0; i < matrix.length; i++)
			for (int j = 0; j < matrix[i].length; j++)
				if (matrix[i][j] != zeroElement)
					this.set(i, j, matrix[i][j]);
	}

	private void checkIndexes(int i, int j) {
		if (i < 0 || j < 0 || i > numCols || j > numRows)
			throw new IndexOutOfBoundsException();
	}

	public void set(int i, int j, T value) {
		if(value != this.zero) {
			checkIndexes(i, j);
			ListIterator<SparseMatrixNode<T>> itr = ch.iterator();
			if(!itr.hasNext())
				ch.add(0, new SparseMatrixNode<T>(i, j, value));
			while (itr.hasNext()) {
				SparseMatrixNode<T> e = itr.next();
				if (i >= e.row() && j >= e.column()) {
					ch.add(itr.nextIndex(), new SparseMatrixNode<T>(i, j, value));
					break;
				}
			}
		}
	}

	public T get(int i, int j) {
		checkIndexes(i, j);
		ListIterator<SparseMatrixNode<T>> itr = ch.iterator();
		while (itr.hasNext()) {
			SparseMatrixNode<T> e = itr.next();
			if (i == e.row() && j == e.column()) {
				return e.getValue();
			}
		}
		return this.zero;
	}

  // FIXME: fix error, deleting almost the whole matrix
	public T remove(int i, int j) {
		checkIndexes(i, j);
		ListIterator<SparseMatrixNode<T>> itr = ch.iterator();
		while (itr.hasNext()) {
			SparseMatrixNode<T> e = itr.next();
			if (i == e.row() && j == e.column()) {
				return ch.remove(itr.previousIndex()).getValue();
			}
		}
		return this.zero;
	}

	public String toString() {
    StringBuilder sb = new StringBuilder();
		for (int j = 0; j < this.numCols; j++) {
      sb.append('|');
			for (int i = 0; i < this.numRows; i++) {
				sb.append(this.get(i, j)).append('\t');
			}	sb.append('|').append('\n');
		}
    return sb.toString();
	}

	public int numColumns() {
		return this.numCols;
	}

	public int numRows() {
		return this.numRows;
	}

	public static void main(String[] args) {
		SparseMatrix<Integer> sparse = new SparseMatrix<Integer>(new Integer[][] {
			{2,3,0,0,0,0,6,0,0,0},
			{6,1,0,0,0,4,0,0,9,0},
			{0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,5,0,0,0,8,0},
			{0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,3,0,2,0,0,0},
			{0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,1,0,0,0,7},
			{0,9,0,0,1,0,0,7,0,1}
		}, 	0);
    System.out.println(sparse);
		System.out.println(sparse.remove(1,0));
    System.out.println(sparse);
	}
}
