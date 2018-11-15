package DNSRelay;

public class TypeConvert {
	/**
	 * Byte to Integer
	 */
	public static int byteToInt(byte[] array, int start)
	{
		final int length = 1;
		int result = 0;
		byte loop;
		for (int i = start; i < start + length; i++) {
			loop = array[i];
			int offSet = length - (i - start) -1;
			result += (loop & 0xFF) << (8 * offSet);
		}
		return result;
	}
	
	/**
	 * Integer to Byte
	 * @param value Integer
	 * @param array Byte array
	 * @param start Start index in array
	 * @return Offset
	 */
	public static int intToByte(int value, byte[] array, int start) {
		final int length = 4;
		byte loop;
		for (int i = start; i < start + length; i++) {
			int offSet = length - (i - start) -1;
			loop = (byte) ((byte) (value >> (8 * offSet)) & 0xFF);
			array[i] = loop;
	    }
		return length;
	}
	
	/**
	 * Integer to Byte
	 * @param value Integer
	 * @return byte[]
	 */
	public static byte[] intToByte(int value) {
		byte[] array = new byte[4];
		intToByte(value, array, 0);
		return array;
	}
	
	/**
	 * byte to Short
	 */
	public static short byteToShort(byte[] array, int start)
	{
		final int length = 2;
		short result = 0;
		
		byte loop;
		for (int i = start; i < start + length; i++) {
			loop = array[i];
			int offSet = length - (i - start) -1; //(i - start);
			result += (loop & 0xFF) << (8 * offSet);
		}
		return result;
	}
	
	/**
	 * Short to Byte
	 * @param value Short
	 * @param array Byte array
	 * @param start Start index in array
	 * @return Offset
	 */
	public static int shortToByte(short value, byte[] array, int start) {
		final int length = 2;
		byte loop;
		for (int i = start; i < start + length; i++) {
			int offSet = length - (i - start) -1;
			loop = (byte) ((byte) (value >> (8 * offSet)) & 0xFF);
			array[i] = loop;
	    }
		return length;
	}
	
	/**
	 * Short to Byte
	 * @param value Short
	 * @return Byte[]
	 */
	public static byte[] shortToByte(short value) {
		byte[] array = new byte[2];
		shortToByte(value, array, 0);
		return array;
	}
	
	/**
	 * byte array to char array
	 * @param b bit
	 * @param start start bit
	 * @param length length
	 * @return c char array
	 */
	public static char[] byteToChar(byte [] b, int start, int length) {
		char [] c = new char[length];
		for(int i = 0; i < length; i++) {
			c[i] = (char) b[start + i];
		}
		return c;
	}
	
	/**
	 * byte array to string
	 * @param b bit
	 * @param start start point
	 * @param length length
	 * @return String
	 */
	public static String byteToString(byte [] b, int start, int length) {
		return String.valueOf(byteToChar(b, start, length));
	}
}
