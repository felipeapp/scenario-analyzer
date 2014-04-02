package br.ufrn.ppgsc.scenario.analyzer.cstatic.wala;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import br.ufrn.ppgsc.scenario.analyzer.cstatic.util.ScenarioAnalyzerUtil;

import com.ibm.wala.classLoader.CallSiteReference;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.types.TypeName;
import com.ibm.wala.util.strings.Atom;

public abstract class WalaAnalyzerUtil {

	public static String getStandartMethodSignature(IMethod method) {
		StringBuffer result = new StringBuffer();

		// O pacote do método será null se ele estiver no pacote padrão
		Atom methodPackage = method.getDeclaringClass().getName().getPackage();
		if (methodPackage != null) {
			result.append(method.getDeclaringClass().getName().getPackage().toString().replaceAll("/", "."));
			result.append(".");
		}

		result.append(method.getDeclaringClass().getName().getClassName());
		result.append(".");

		if (method.isInit())
			result.append(method.getDeclaringClass().getName().getClassName());
		else
			result.append(method.getName());

		result.append("(");

		for (int i = 0; i < method.getSelector().getDescriptor().getNumberOfParameters(); i++) {
			TypeName type = method.getSelector().getDescriptor().getParameters()[i];

			if (type.getPackage() != null) {
				result.append(type.getPackage().toString().replaceAll("/", "."));
				result.append(".");
			}

			result.append(ScenarioAnalyzerUtil.convertTypeSignatureToName(type.getClassName().toString()));

			if (type.isArrayType()) {
				int j = 0;

				while (type.toString().charAt(j++) == '[')
					result.append("[]");
			}

			result.append(",");
		}

		if (result.charAt(result.length() - 1) == ',')
			result.deleteCharAt(result.length() - 1);

		return result + ")";
	}

	public static String getPartialMethodSignature(IMethod method) {
		StringBuffer result = new StringBuffer();

		if (method.isInit())
			result.append(method.getDeclaringClass().getName().getClassName());
		else
			result.append(method.getName());

		result.append("(");

		for (int i = 0; i < method.getSelector().getDescriptor().getNumberOfParameters(); i++) {
			TypeName type = method.getSelector().getDescriptor().getParameters()[i];

			if (type.getPackage() != null) {
				result.append(type.getPackage().toString().replaceAll("/", "."));
				result.append(".");
			}

			result.append(ScenarioAnalyzerUtil.convertTypeSignatureToName(type.getClassName().toString()));

			if (type.isArrayType()) {
				int j = 0;

				while (type.toString().charAt(j++) == '[')
					result.append("[]");
			}

			result.append(",");
		}

		if (result.charAt(result.length() - 1) == ',')
			result.deleteCharAt(result.length() - 1);

		return result + ")";
	}

	public static String getStandartMethodSignature(Method method) {
		StringBuffer result = new StringBuffer();

		result.append(method.getDeclaringClass().getName());
		result.append(".");
		result.append(method.getName());
		result.append("(");

		for (Class<?> cls : method.getParameterTypes()) {
			result.append(cls.getCanonicalName());
			result.append(",");
		}

		if (result.charAt(result.length() - 1) == ',')
			result.deleteCharAt(result.length() - 1);

		return result + ")";
	}

	public static void printNodes(CallGraph cg) {
		for (Iterator<CGNode> it = cg.getSuccNodes(cg.getFakeRootNode()); it.hasNext();)
			printNodes(cg, it.next(), new HashSet<CGNode>(), "");
	}

	private static void printNodes(CallGraph cg, CGNode root, Set<CGNode> visited, String str) {
		if (root.getMethod().getDeclaringClass().getClassLoader().toString().equals("Primordial"))
			return;

		if (visited.contains(root)) {
			System.out.println(str + "[*]" + root.getMethod().getSignature());
			return;
		} else
			System.out.println(str + root.getMethod().getSignature());

		visited.add(root);

		for (Iterator<CallSiteReference> it = root.iterateCallSites(); it.hasNext();)
			for (CGNode cgNode : cg.getPossibleTargets(root, it.next()))
				printNodes(cg, cgNode, visited, str + "\t");

		visited.remove(root);
	}

}
