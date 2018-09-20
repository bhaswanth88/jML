package com.jmlmath.matrices;

public class MatrixSize {
	private int rows;
	private int columns;

	public MatrixSize(int rows, int cols) {
		this.rows = rows;
		this.columns = cols;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public int getColumns() {
		return columns;
	}

	public void setColumns(int columns) {
		this.columns = columns;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj==null)
			return false;
		if(!(obj instanceof MatrixSize)){
			return false;
		}
		MatrixSize matrixBSize=(MatrixSize)obj;
		if((matrixBSize.getRows()==this.rows) && (matrixBSize.getColumns()==this.columns)){
			return true;
		}
		return false;
	}

}
