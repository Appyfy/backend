//package rnd.util;
//
//import java.security.Key;
//
//import javax.crypto.Cipher;
//import javax.crypto.spec.SecretKeySpec;
//
//import sun.misc.BASE64Decoder;
//import sun.misc.BASE64Encoder;
//
//public class EncryptionUtils {
//
//	private static final String AES = "AES";
//
//	private static final byte[] keyValue = new byte[] { 'T', 'h', 'e', 'B', 'e', 's', 't', 'S', 'e', 'c', 'r', 'e', 't', 'K', 'e', 'y' };
//
//	public static String encrypt(String Data, byte[] sapKey) throws Exception {
//		Key key = null;
//		if (sapKey == null) {
//			key = generateKey(null);
//		} else {
//			key = generateKey(sapKey);
//		}
//		Cipher c = Cipher.getInstance(AES);
//		c.init(Cipher.ENCRYPT_MODE, key);
//		byte[] encVal = c.doFinal(Data.getBytes());
//		String encryptedValue = new BASE64Encoder().encode(encVal);
//		return encryptedValue;
//	}
//
//	public static String decrypt(String encryptedData, byte[] sapKey) throws Exception {
//		Key key = null;
//		if (sapKey == null) {
//			key = generateKey(null);
//		} else {
//			key = generateKey(sapKey);
//		}
//		Cipher c = Cipher.getInstance(AES);
//		c.init(Cipher.DECRYPT_MODE, key);
//		byte[] decordedValue = new BASE64Decoder().decodeBuffer(encryptedData);
//		byte[] decValue = c.doFinal(decordedValue);
//		String decryptedValue = new String(decValue);
//		return decryptedValue;
//	}
//
//	private static Key generateKey(byte[] sapKey) throws Exception {
//		Key key = null;
//		if (sapKey == null) {
//			key = new SecretKeySpec(keyValue, AES);
//		} else {
//			key = new SecretKeySpec(sapKey, AES);
//		}
//		return key;
//	}
//
//	public static void main(String[] args) {
//		try {
//			String pwd = EncryptionUtils.encrypt("", null);
//			System.out.println("pwd " + pwd);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//}
