package tests;

import java.lang.reflect.Method;

import br.ufrn.arq.erros.DAOException;

public class RunReflect {

	public synchronized String getSession() throws DAOException {
		return "felipe";
	}

	public static void main(String[] args) throws Exception {
		RunReflect r = new RunReflect();

		Method m = r.getClass().getMethod("getSession");
		String s = (String) m.invoke(r);

		System.out.println(s);
	}

}
