package tests;

import java.lang.reflect.Member;

public class TestSignature {

	private static Class<?> convertNameToClass(String type) {
		switch (type) {
			case "boolean" : return boolean.class;
			case "byte": return byte.class;
			case "char": return char.class;
			case "double": return double.class;
			case "float": return float.class;
			case "int": return int.class;
			case "long": return long.class;
			case "short": return short.class;
			default: return null;
		}
	}
	
	private static boolean isPrimitive(String type) {
		switch (type) {
			case "boolean":
			case "byte":
			case "char":
			case "double":
			case "float":
			case "int":
			case "long":
			case "short":
				return true;
			default:
				return false;
		}
	}
	
	public static Member signature2Member(String memberSignature) {
		String class_name = memberSignature.substring(0, memberSignature.indexOf('('));
		String params = memberSignature.substring(memberSignature.indexOf('(') + 1, memberSignature.lastIndexOf(')'));
		
		String[] types = params.split(",");
		Class<?>[] cls_types = new Class<?>[types.length];
		
		System.out.println(class_name);
		System.out.println("-----------");
		
		for (int i = 0; i < types.length; i++) {
			if (isPrimitive(types[i]))
				cls_types[i] = convertNameToClass(types[i]);
			else
				try {
					cls_types[i] = Class.forName(types[i]);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			
			System.out.println(cls_types[i].getName());
		}
		
		System.out.println("-----------");
		
		return null;
	}
	
	public static void main(String[] args) throws ClassNotFoundException {
		
		System.out.println(int[][].class.getName());
		System.out.println(int[].class.getName());
		System.out.println(char[].class.getName());
		System.out.println(Integer.TYPE.getName());
		System.out.println(Object[].class.getName());
		System.out.println(Class.forName("[Ljava.lang.String;").getName());
		
		signature2Member("tests.MultDivCalculator.div(float,float)");
//		System.out.println(signature2Member("tests.MultDivCalculator.div(float,float)").getName());
		
	}
	
}
