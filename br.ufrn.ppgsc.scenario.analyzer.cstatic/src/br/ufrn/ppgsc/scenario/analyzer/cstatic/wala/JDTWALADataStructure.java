package br.ufrn.ppgsc.scenario.analyzer.cstatic.wala;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.Annotation;

import br.ufrn.ppgsc.scenario.analyzer.cstatic.Activator;
import br.ufrn.ppgsc.scenario.analyzer.cstatic.model.ClassData;
import br.ufrn.ppgsc.scenario.analyzer.cstatic.model.MethodData;
import br.ufrn.ppgsc.scenario.analyzer.cstatic.model.ScenarioData;
import br.ufrn.ppgsc.scenario.analyzer.cstatic.model.impl.IDataStructure;

import com.ibm.wala.cast.java.client.JDTJavaSourceAnalysisEngine;
import com.ibm.wala.cast.java.ipa.callgraph.JavaSourceAnalysisScope;
import com.ibm.wala.classLoader.CallSiteReference;
import com.ibm.wala.client.AbstractAnalysisEngine;
import com.ibm.wala.ide.util.EclipseFileProvider;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.callgraph.CallGraphStats;
import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.collections.Pair;

public class JDTWALADataStructure implements IDataStructure {
	
	private CallGraph callGraph;
	
	// Versão do sistema da estrutura de dados usada
	private String version;
	
	private Map<String, Pair<MethodData, CGNode>> indexMethod;
	private Map<String, List<Annotation>> indexAnnotation;
	private Map<String, ClassData> indexClassData;
	private List<ScenarioData> listScenario;

	public JDTWALADataStructure() {
		indexAnnotation = new HashMap<String, List<Annotation>>();
		indexMethod = new HashMap<String, Pair<MethodData, CGNode>>();
		indexClassData = new HashMap<String, ClassData>();
		listScenario = new ArrayList<ScenarioData>();
	}
	
	public void showCallGraphStats() {
		System.out.println(CallGraphStats.getStats(callGraph));
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void addMethodToIndex(String key, MethodData method, CGNode node) {
		indexMethod.put(key, Pair.make(method, node));
	}
	
	public MethodData getMethodDataFromIndex(String key) {
		if (indexMethod.get(key) == null)
			return null;
		
		return indexMethod.get(key).fst;
	}
	
	private CGNode getMethodNodeFromIndex(String key) {
		return indexMethod.get(key).snd;
	}
	
	public List<MethodData> getMethodInvocations(String signature) {
		List<MethodData> result = new ArrayList<MethodData>();
		CGNode node = getMethodNodeFromIndex(signature);
		
		for (Iterator<CallSiteReference> it = node.iterateCallSites(); it.hasNext();) {
			for (CGNode child : callGraph.getPossibleTargets(node, it.next())) {
				if (child.getMethod().getDeclaringClass().getClassLoader().getReference().equals(JavaSourceAnalysisScope.SOURCE))
					result.add(getMethodDataFromIndex(WalaAnalyzerUtil.getStandartMethodSignature(child.getMethod())));
			}
		}
		
		return result;
	}
	
	public List<MethodData> getMethodParents(String signature) {
		List<MethodData> result = new ArrayList<MethodData>();
		CGNode node = getMethodNodeFromIndex(signature);
		
		for (Iterator<CGNode> itr = callGraph.getPredNodes(node); itr.hasNext();) {
			CGNode parent = itr.next();
			
			if (parent.getMethod().getDeclaringClass().getClassLoader().getReference().equals(JavaSourceAnalysisScope.SOURCE))
				result.add(getMethodDataFromIndex(WalaAnalyzerUtil.getStandartMethodSignature(parent.getMethod())));
		}
		
		return result;
	}
	
	public MethodData[] getMethodDataAsArray() {
		MethodData list[] = new MethodData[indexMethod.size()];
		
		int i = 0;
		for (Pair<MethodData, CGNode> pair : indexMethod.values())
			list[i++] = pair.fst;
		
		return list;
	}

	public void addClassDataToIndex(String key, ClassData cls) {
		indexClassData.put(key, cls);
	}
	
	public ClassData getClassDataFromIndex(String key) {
		return indexClassData.get(key);
	}
	
	public List<Annotation> getAnnotations(String simple_name) {
		List<Annotation> list = indexAnnotation.get(simple_name);
		
		if (list == null) {
			list = new ArrayList<Annotation>();
			indexAnnotation.put(simple_name, list);
		}
		
		return list;
	}
	
	public List<Annotation> getAnnotations(Class<? extends java.lang.annotation.Annotation> cls) {
		return getAnnotations(cls.getSimpleName());
	}
	
	public void addAnnotationToIndex(String simple_name, Annotation node) {
		getAnnotations(simple_name).add(node);
	}
	
	public void addAnnotationToIndex(Class<? extends java.lang.annotation.Annotation> cls, Annotation node) {
		getAnnotations(cls.getSimpleName()).add(node);
	}

	public void addScenario(ScenarioData scenario) {
		listScenario.add(scenario);
	}

	public List<ScenarioData> getScenarios() {
		return listScenario;
	}
	
	public CallGraph getCallGraph() {
		return callGraph;
	}

	public void printInfo() {
		System.out.println("indexMethod.size() = " + indexMethod.size());
		System.out.println("indexAnnotation.size() = " + indexAnnotation.size());
		System.out.println("indexClassData.size() = " + indexClassData.size());
		System.out.println("listScenario.size() = " + listScenario.size());
	}
	
	/* TODO
	 * O index de métodos deve conter apenas os métodos presentes em algum cenário
	 * - construir o índice enquanto é montado o grafo de chamadas
	 * - este método pode ter o callback para indexar os métodos durante o processo:
	 * - com.ibm.wala.client.AbstractAnalysisEngine.buildDefaultCallGraph()
	 */
	public void buildIndexes(IJavaProject project) {
		WALAElementIndexer indexer = new WALAElementIndexer();
		
		indexer.indexMethod(this);
		System.out.println("--- Methods indexed");
		
		try {
			indexer.indexAnnotation(project, this);
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		System.out.println("--- Annotations indexed");
	}
	
	public void buildCallGraph(IJavaProject project) {
		try {
			callGraph = makeAnalysisEngine(project).buildDefaultCallGraph();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (CancelException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private AbstractAnalysisEngine makeAnalysisEngine(IJavaProject project) {
		AbstractAnalysisEngine engine = null;
		
		try {
			engine = new JDTJavaSourceAnalysisEngine(project) {
				@Override
				public void buildAnalysisScope() throws IOException {
					setExclusionsFile((new EclipseFileProvider()).getFileFromPlugin(
							Activator.getDefault(), "Exclusions.txt").getAbsolutePath());
					
					super.buildAnalysisScope();
				}

				@Override
				protected Iterable<Entrypoint> makeDefaultEntrypoints(AnalysisScope scope, IClassHierarchy cha) {
					return new ScenarioApplicationEntrypoints(cha);
				}
			};
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return engine;
	}

}
