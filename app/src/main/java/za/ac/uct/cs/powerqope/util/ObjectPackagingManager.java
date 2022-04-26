package za.ac.uct.cs.powerqope.util;

public interface ObjectPackagingManager {
	
	int objectSize();
	
	Object bytesToObject(byte[] data, int offs);
	
	void objectToBytes(Object object, byte[] data, int offs);
	
}
