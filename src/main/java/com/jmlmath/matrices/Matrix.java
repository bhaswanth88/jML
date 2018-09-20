package com.jmlmath.matrices;

import java.text.DecimalFormat;

import com.jmlmath.exceptions.InvalidColumnIndexException;
import com.jmlmath.exceptions.InvalidMatrixMultiplication;
import com.jmlmath.exceptions.InvalidRowIndexException;
import com.jmlmath.exceptions.InvalidSizeException;
import com.jmlmath.exceptions.LengthMismatchException;
import com.jmlmath.exceptions.NotASquareMatrixException;
import com.jmlmath.exceptions.SizeMismatchException;

public class Matrix {
	Double[][] matrixData;
	MatrixSize size;

	Matrix(int rows, int columns) throws InvalidSizeException {
		if (rows == 0 || columns == 0) {
			throw new InvalidSizeException();
		}
		matrixData = new Double[rows][columns];
		size = new MatrixSize(rows, columns);
	}

	Matrix(MatrixSize size) throws InvalidSizeException {
		if (size == null || size.getColumns() == 0 || size.getRows() == 0) {
			throw new InvalidSizeException();
		}
		matrixData = new Double[size.getRows()][size.getColumns()];
		this.size = size;
	}

	public Double[] getRow(int rowIndex) throws InvalidRowIndexException {
		if (rowIndex >= matrixData.length) {
			throw new InvalidRowIndexException();
		}
		return matrixData[rowIndex];
	}

	public MatrixSize getSize() {
		return size;
	}

	public Double getElement(int rowIndex, int colIndex) throws InvalidRowIndexException, InvalidColumnIndexException {
		if (rowIndex >= matrixData.length) {
			throw new InvalidRowIndexException();
		}
		if (colIndex >= matrixData[0].length) {
			throw new InvalidColumnIndexException();
		}
		return matrixData[rowIndex][colIndex];
	}

	public void setElement(int rowIndex, int colIndex, Double value)
			throws InvalidRowIndexException, InvalidColumnIndexException {
		if (rowIndex >= matrixData.length) {
			throw new InvalidRowIndexException();
		}
		if (colIndex >= matrixData[0].length) {
			throw new InvalidColumnIndexException();
		}
		matrixData[rowIndex][colIndex] = value;
	}

	public void setRow(int rowIndex, Double[] rowValues) throws InvalidRowIndexException, LengthMismatchException {
		if (rowIndex >= matrixData.length) {
			throw new InvalidRowIndexException();
		}
		if (rowValues.length != matrixData[0].length) {
			throw new LengthMismatchException();
		}
		matrixData[rowIndex] = rowValues;
	}

	public Double[] getColumn(int colIndex) throws InvalidColumnIndexException {
		if (colIndex >= matrixData[0].length) {
			throw new InvalidColumnIndexException();
		}
		Double[] array = new Double[matrixData.length];
		for (int i = 0; i < matrixData.length; i++) {
			array[i] = matrixData[i][colIndex];
		}
		return array;
	}

	public void setColumn(int colIndex, Double[] colValues)
			throws LengthMismatchException, InvalidColumnIndexException {
		if (colIndex >= matrixData[0].length) {
			throw new InvalidColumnIndexException();
		}
		if (colValues.length != matrixData.length) {
			throw new LengthMismatchException();
		}
		for (int i = 0; i < matrixData.length; i++) {
			matrixData[i][colIndex] = colValues[i];
		}

	}

	public void print() {
		DecimalFormat formatter = new DecimalFormat("#.##");
		System.out.println("Printing matrix");
		System.out.print("---");
		for (int i = 0; i < (getSize().getColumns() * 2) - 1; i++) {
			System.out.print("\t");
		}
		System.out.print("      ---");
		System.out.println("");

		for (int rowIndex = 0; rowIndex < getSize().getRows(); rowIndex++) {
			System.out.print("|");
			for (int colIndex = 0; colIndex < getSize().getColumns(); colIndex++) {
				System.out.print("\t" + formatter.format(matrixData[rowIndex][colIndex]) + "\t");
			}

			System.out.print("|");
			System.out.println("");
		}
		System.out.print("---");
		for (int i = 0; i < (getSize().getColumns() * 2) - 1; i++) {
			System.out.print("\t");
		}
		System.out.print("      ---  " + (getSize().getRows() + "x" + getSize().getColumns()));
		System.out.println("");
	}

	public Double trace() throws NotASquareMatrixException {
		if (getSize().getRows() != getSize().getColumns()) {
			throw new NotASquareMatrixException();
		}

		Double trace = 0.0;
		for (int index = 0; index < getSize().getRows(); index++) {
			try {
				trace = trace + getElement(index, index);
			} catch (InvalidRowIndexException | InvalidColumnIndexException ignore) {
				ignore.printStackTrace();
				// will never come to this case
			}
		}
		return trace;
	}

	public Matrix removeRow(int rowIndex)
			throws InvalidSizeException, InvalidRowIndexException, LengthMismatchException {
		MatrixSize size = new MatrixSize(this.getSize().getRows() - 1, getSize().getColumns());
		Matrix result = new MatrixBuilder().setSize(size).build();
		int rowCount = 0;
		for (int index = 0; index < getSize().getRows(); index++) {
			if (index != rowIndex) {
				result.setRow(rowCount, getRow(index));
				rowCount++;
			}
		}
		return result;
	}

	public Matrix removeColumn(int colIndex)
			throws LengthMismatchException, InvalidColumnIndexException, InvalidSizeException {
		MatrixSize size = new MatrixSize(this.getSize().getRows(), getSize().getColumns() - 1);
		Matrix result = new MatrixBuilder().setSize(size).build();
		int colCount = 0;
		for (int index = 0; index < getSize().getColumns(); index++) {
			if (index != colIndex) {
				result.setColumn(colCount, getColumn(index));
				colCount++;
			}
		}
		return result;
	}

	public Matrix cofactor() throws InvalidRowIndexException, InvalidColumnIndexException, LengthMismatchException,
			InvalidSizeException {
		Matrix mat = new Matrix(this.getSize().getRows(), this.getSize().getColumns());
		for (int i = 0; i < this.getSize().getRows(); i++) {
			for (int j = 0; j < this.getSize().getColumns(); j++) {
				mat.setElement(i, j, changeSign(i) * changeSign(j) * det(this.getMinorMatrix(i, j)));
			}
		}

		return mat;
	}

	private int changeSign(int index) {
		if (index % 2 == 0) {
			return 1;
		} else
			return -1;
	}

	public Matrix inverse() throws InvalidRowIndexException, InvalidColumnIndexException, LengthMismatchException,
			InvalidSizeException {
		return this.adjoint().multiply(1 / det(this));
	}

	public Matrix adjoint() throws InvalidRowIndexException, InvalidColumnIndexException, LengthMismatchException,
			InvalidSizeException {
		return this.cofactor().transpose();
	}

	public Double determinent() {
		// http://www.math.harvard.edu/archive/21b_fall_04/exhibits/2dmatrices/index.html
		// https://blog.udanax.org/2009/06/jacobi-eigenvalue-algorithm.html
		return det(this);
	}

	private Double det(Matrix m) {
		Double detValue = 0.0;
		if (m.getSize().getColumns() == m.getSize().getRows() && m.getSize().getRows() == 2) {
			try {
				detValue = m.getElement(0, 0) * m.getElement(1, 1) - m.getElement(0, 1) * m.getElement(1, 0);
				// System.out.println("returning det value::" + detValue);
			} catch (InvalidRowIndexException | InvalidColumnIndexException e) {
				e.printStackTrace();
			}
		} else {
			for (int i = 0; i < m.getSize().getColumns(); i++) {
				try {
					detValue += changeSign(i) * m.getElement(0, i) * det(m.getMinorMatrix(0, i));
				} catch (InvalidRowIndexException | InvalidColumnIndexException | LengthMismatchException
						| InvalidSizeException e) {
					e.printStackTrace();
				}

			}
		}
		return detValue;
	}

	public Matrix getMinorMatrix(int rowIndex, int colIndex) throws LengthMismatchException,
			InvalidColumnIndexException, InvalidSizeException, InvalidRowIndexException {
		Matrix result = this.removeRow(rowIndex).removeColumn(colIndex);
		// result.print();
		return result;
	}

	public Matrix add(Matrix matrixB) throws SizeMismatchException {
		if (!this.getSize().equals(matrixB.getSize())) {
			throw new SizeMismatchException();
		}
		try {
			Matrix result = new MatrixBuilder().setSize(this.getSize()).build();
			for (int rowIndex = 0; rowIndex < this.getSize().getRows(); rowIndex++) {
				for (int colIndex = 0; colIndex < this.getSize().getColumns(); colIndex++) {
					result.setElement(rowIndex, colIndex,
							this.getElement(rowIndex, colIndex) + matrixB.getElement(rowIndex, colIndex));
				}
			}
			return result;

		} catch (InvalidSizeException | InvalidRowIndexException | InvalidColumnIndexException ignore) {
			ignore.printStackTrace();
			/// exceptions can be ignored unless size is changed inbetween by any other
			/// thread, which is not supported at the moment
		}
		return null;

	}

	public Matrix subtract(Matrix matrixB) throws SizeMismatchException {
		if (!this.getSize().equals(matrixB.getSize())) {
			throw new SizeMismatchException();
		}
		try {
			Matrix result = new MatrixBuilder().setSize(this.getSize()).build();
			for (int rowIndex = 0; rowIndex < this.getSize().getRows(); rowIndex++) {
				for (int colIndex = 0; colIndex < this.getSize().getColumns(); colIndex++) {
					result.setElement(rowIndex, colIndex,
							this.getElement(rowIndex, colIndex) - matrixB.getElement(rowIndex, colIndex));
				}
			}
			return result;

		} catch (InvalidSizeException | InvalidRowIndexException | InvalidColumnIndexException ignore) {
			ignore.printStackTrace();
			/// exceptions can be ignored unless size is changed inbetween by any other
			/// thread, which is not supported at the moment
		}
		return null;
	}

	public Matrix multiply(Double constant) {
		try {
			Matrix result = new MatrixBuilder().setSize(this.getSize()).build();
			for (int rowIndex = 0; rowIndex < this.getSize().getRows(); rowIndex++) {
				for (int colIndex = 0; colIndex < this.getSize().getColumns(); colIndex++) {
					result.setElement(rowIndex, colIndex, this.getElement(rowIndex, colIndex) * constant);
				}
			}
			return result;

		} catch (InvalidSizeException | InvalidRowIndexException | InvalidColumnIndexException ignore) {
			ignore.printStackTrace();
			/// exceptions can be ignored unless size is changed inbetween by any other
			/// thread, which is not supported at the moment
		}
		return null;
	}

	public Matrix multiply(Matrix matrixB) throws InvalidMatrixMultiplication {
		if (this.getSize().getColumns() != matrixB.getSize().getRows()) {
			throw new InvalidMatrixMultiplication();
		}

		try {
			Matrix result = new MatrixBuilder()
					.setSize(new MatrixSize(this.getSize().getRows(), matrixB.getSize().getColumns())).build();

			for (int rowIndex = 0; rowIndex < result.getSize().getRows(); rowIndex++) {
				for (int colIndex = 0; colIndex < result.getSize().getColumns(); colIndex++) {
					Double value = 0.0;
					for (int index = 0; index < this.getRow(rowIndex).length; index++) {
						value = value + this.getElement(rowIndex, index) * matrixB.getElement(index, colIndex);
					}
					result.setElement(rowIndex, colIndex, value);
				}
			}
			return result;
		} catch (InvalidSizeException | InvalidRowIndexException | InvalidColumnIndexException ignore) {
			ignore.printStackTrace();
		}
		return null;
	}

	public Matrix transpose() {
		try {
			Matrix result = new MatrixBuilder()
					.setSize(new MatrixSize(this.getSize().getColumns(), this.getSize().getRows())).build();
			for (int rowIndex = 0; rowIndex < result.getSize().getRows(); rowIndex++) {
				result.setRow(rowIndex, this.getColumn(rowIndex));
			}
			return result;
		} catch (InvalidSizeException | InvalidRowIndexException | InvalidColumnIndexException ignore) {
			ignore.printStackTrace();
		} catch (LengthMismatchException e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean isSymmetric() {
		return this.transpose().equals(this);
	}
	public boolean isOrthogonal() {
		try {
			return this.transpose().equals(this.inverse());
		} catch (InvalidRowIndexException | InvalidColumnIndexException | LengthMismatchException
				| InvalidSizeException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (!(obj instanceof Matrix))
			return false;
		Matrix compareTo = (Matrix) obj;
		if (!compareTo.getSize().equals(this.getSize()))
			return false;

		for (int i = 0; i < compareTo.getSize().getRows(); i++) {
			for (int j = 0; j < compareTo.getSize().getColumns(); j++) {
				try {
					if (compareTo.getElement(i, j) != this.getElement(i, j))
						return false;
				} catch (InvalidRowIndexException | InvalidColumnIndexException e) {
					e.printStackTrace();
					return false;
				}
			}
		}
		return true;
	}

	public static void main(String[] args) {
		Matrix b;
		try {
			b = new MatrixBuilder().setSize(new MatrixSize(3, 3)).build();
			b.setElement(0, 0, 1.0);
			b.setElement(0, 1, 2.0);
			b.setElement(0, 2, 3.0);
			b.setElement(1, 0, 0.0);
			b.setElement(1, 1, 4.0);
			b.setElement(1, 2, 5.0);
			b.setElement(2, 0, 1.0);
			b.setElement(2, 1, 0.0);
			b.setElement(2, 2, 6.0);
			// b.cofactor().print();
			b.print();

			Matrix m = b.inverse();
			m.print();

			m.multiply(b).print();
			// b.getMinorMatrix(0, 0).print();
			// b.getMinorMatrix(0, 1).print();
			// b.getMinorMatrix(0, 2).print();
			// System.out.println(b.determinent());
			;
			// b.multiply(b.transpose()).print();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
