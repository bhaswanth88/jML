package com.jmlmath.matrices;

import com.jmlmath.exceptions.InvalidSizeException;

public class MatrixBuilder {

	MatrixSize size;
	boolean asIdentityMatrix = false;
	boolean asConstantMatrix = false;
	double constantValue;

	public MatrixBuilder setSize(MatrixSize size) {
		this.size = size;
		return this;
	}

	public MatrixBuilder asIdentityMatrix() {
		asIdentityMatrix = true;
		return this;
	}

	public MatrixBuilder asConstantMatrix(double constantValue) {
		asConstantMatrix = true;
		this.constantValue = constantValue;
		return this;
	}

	public Matrix build() throws InvalidSizeException {
		if (size == null) {
			throw new InvalidSizeException();
		}
		Matrix matrix = new Matrix(size);
		if (asIdentityMatrix) {
			for (int rowIndex = 0; rowIndex < size.getRows(); rowIndex++) {
				for (int colIndex = 0; colIndex < size.getColumns(); colIndex++) {
					if (rowIndex == colIndex) {
						try {
							matrix.setElement(rowIndex, colIndex, 1.0);
						} catch (Exception ignore) {
							ignore.printStackTrace();
							// will never come to this case
						}
					}
				}
			}
			
		}

		if (asConstantMatrix) {
			for (int rowIndex = 0; rowIndex < size.getRows(); rowIndex++) {
				for (int colIndex = 0; colIndex < size.getColumns(); colIndex++) {
					try {
						matrix.setElement(rowIndex, colIndex, constantValue);
					} catch (Exception ignore) {
						ignore.printStackTrace();
						// will never come to this case
					}

				}
			}
		}
		return matrix;
	}

}
