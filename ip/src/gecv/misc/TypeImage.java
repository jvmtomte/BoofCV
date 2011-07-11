/*
 * Copyright 2011 Peter Abeles
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package gecv.misc;

import gecv.struct.image.*;


/**
 * Image information for auto generated code
 *
 * @author Peter Abeles
 */
public enum TypeImage {
	I("ImageInteger","int",true,0),
	I8("ImageInt8","byte",true,8),
	U8(ImageUInt8.class),
	S8(ImageSInt8.class),
	I16("ImageInt16","short",true,16),
	U16(ImageUInt16.class),
	S16(ImageSInt16.class),
	S32(ImageSInt32.class),
	F32(ImageFloat32.class),
	F64(ImageFloat64.class);

	private String imageName;
	private String dataType;
	private String bitWise;
	private String sumType;
	private boolean isInteger;
	private boolean isSigned;
	private int numBits;
	private String abbreviatedType;

	private Class<?> primitiveType;

	TypeImage( Class<?> imageType ) {

		imageName = imageType.getSimpleName();
		bitWise = "";
		try {
			ImageBase img = (ImageBase)imageType.newInstance();
			primitiveType = img._getPrimitiveType();
			dataType = primitiveType.getSimpleName();
			if( ImageInteger.class.isAssignableFrom(imageType)) {
				isInteger = true;
				sumType = "int";
				if( !((ImageInteger)img).isSigned() ) {
					abbreviatedType = "U";
					isSigned = false;
					if( byte.class == primitiveType) {
						bitWise = "& 0xFF";
					} else if( short.class == primitiveType) {
						bitWise = "& 0xFFFF";
					}
				} else {
					abbreviatedType = "S";
					isSigned = true;
				}
			} else {
				abbreviatedType = "F";
				isSigned = true;
				isInteger = false;
				if( imageType == ImageFloat32.class ) {
					sumType = "float";
				} else {
					sumType = "double";
				}
			}
			if( primitiveType == byte.class ) {
				numBits = 8;
			} else if( primitiveType == short.class ) {
				numBits = 16;
			} else if( primitiveType == int.class || primitiveType == float.class ) {
				numBits = 32;
			} else if( primitiveType == long.class || primitiveType == double.class ) {
				numBits = 64;
			} else {
				throw new IllegalArgumentException("Unknown number of bits");
			}
			abbreviatedType += numBits;

		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	TypeImage(String imageName, String dataType, boolean isInteger , int numBits ) {
		this.imageName = imageName;
		this.dataType = dataType;
		this.isInteger = isInteger;
		this.numBits = numBits;

		if( isInteger ) {
			this.abbreviatedType = "I";
			this.sumType = "int";
		} else {
			this.sumType = "double";
		}
		abbreviatedType += numBits;

	}

	public static TypeImage[] getIntegerTypes() {
		return new TypeImage[]{U8,S8,U16,S16,S32};
	}

	public static TypeImage[] getFloatingTypes() {
		return new TypeImage[]{F32,F64};
	}

	public static TypeImage[] getGenericTypes() {
		return new TypeImage[]{I8,I16,S32,F32,F64};
	}

	public static TypeImage[] getSpecificTypes() {
		return new TypeImage[]{U8,S8,U16,S16,S32,F32,F64};
	}

	public static TypeImage[] getSigned() {
		return new TypeImage[]{S8,S16,S32,F32,F64};
	}

	public static TypeImage[] getUnsigned() {
		return new TypeImage[]{U8,U16};
	}

	public String getImageName() {
		return imageName;
	}

	public String getDataType() {
		return dataType;
	}

	public String getBitWise() {
		return bitWise;
	}

	public String getSumType() {
		return sumType;
	}

	public boolean isInteger() {
		return isInteger;
	}

	public boolean isSigned() {
		return isSigned;
	}

	public int getNumBits() {
		return numBits;
	}

	public Class<?> getPrimitiveType() {
		return primitiveType;
	}

	public String getTypeCastFromSum() {
		if( sumType.compareTo(dataType) != 0 )
			return "("+dataType+")";
		else
			return "";
	}

	public String getAbbreviatedType() {
		return abbreviatedType;
	}

	public String getRandType() {
		return primitiveType == float.class ? "Float" : "Double";
	}

	public Number getMax() {
		if( isInteger ) {
			if( byte.class == primitiveType) {
				if( isSigned ) {
					return Byte.MAX_VALUE;
				} else {
					return 0xFF;
				}
			} else if( short.class == primitiveType) {
				if( isSigned ) {
					return Short.MAX_VALUE;
				} else {
					return 0xFFFF;
				}
			} else {
				return Integer.MAX_VALUE;
			}
		} else if( float.class == primitiveType) {
			return Float.MAX_VALUE;
		} else {
			return Double.MAX_VALUE;
		}
	}

	public Number getMin() {
		if( isInteger ) {
			if( byte.class == primitiveType) {
				if( isSigned ) {
					return Byte.MIN_VALUE;
				} else {
					return 0;
				}
			} else if( short.class == primitiveType) {
				if( isSigned ) {
					return Short.MIN_VALUE;
				} else {
					return 0;
				}
			} else {
				return Integer.MIN_VALUE;
			}
		} else if( float.class == primitiveType) {
			return Float.MIN_VALUE;
		} else {
			return Double.MIN_VALUE;
		}
	}
}
