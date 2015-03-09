package br.ufrn.ppgsc.scenario.analyzer.cstatic.processors.impl;

import java.io.FileNotFoundException;
import java.io.PrintStream;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import br.ufrn.ppgsc.scenario.analyzer.cstatic.model.AbstractQAData;
import br.ufrn.ppgsc.scenario.analyzer.cstatic.model.MethodData;
import br.ufrn.ppgsc.scenario.analyzer.cstatic.model.ScenarioData;
import br.ufrn.ppgsc.scenario.analyzer.cstatic.model.impl.IDataStructure;
import br.ufrn.ppgsc.scenario.analyzer.cstatic.processors.IProjectProcessor;
import br.ufrn.ppgsc.scenario.analyzer.cstatic.util.ScenarioAnalyzerQuery;
import br.ufrn.ppgsc.scenario.analyzer.cstatic.util.ScenarioAnalyzerUtil;

public class JavaProjectProcessor implements IProjectProcessor {

	private IJavaProject createJavaProject(IProject project) throws CoreException {
		IJavaProject javaProject = null;

		if (project.isNatureEnabled(JavaCore.NATURE_ID))
			javaProject = JavaCore.create(project);

		return javaProject;
	}

	private void processJavaProject(IProject project) throws CoreException, FileNotFoundException {
		IJavaProject javaProject = createJavaProject(project);

		System.out.println("--- Project Name: " + project.getName());

		if (javaProject == null) {
			System.out.println("It isn't a java project! Aborting...");
			return;
		}

		IDataStructure data = ScenarioAnalyzerUtil.createDataStructure(project.getName());
		System.out.println("--- Data structure created");
		
		data.buildCallGraph(javaProject);
		System.out.println("--- Call graph built");
		
		data.buildIndexes(javaProject);
		System.out.println("--- Indexes built");
		
		ScenarioAnalyzerUtil.getAnnotationProcessor().process(data);
		System.out.println("--- Annotation processed");
		
		data.printInfo();
		System.out.println("--- Info printed");
		
//		ScenarioAnalyzerUtil.printNodes(data.getCallGraph());
//		System.out.println("--- Call graph printed");
		
//		ScenarioAnalyzerUtil.printMethod("br.com.ecommerce.arq.sbeans.RegistroSBean.cadastrar(br.com.ecommerce.arq.dominio.CadastroDB)");
//		System.out.println("--- Method root printed");
		
		ScenarioAnalyzerUtil.printDataStructure(data, new PrintStream(javaProject.getResource().getLocation() + "/out.txt"));
		ScenarioAnalyzerUtil.printDataStructure(data, System.out);
		System.out.println("--- Data structure printed");
		
		ScenarioAnalyzerQuery query = new ScenarioAnalyzerQuery(data);
		
		for (MethodData method : data.getMethodDataAsArray()) {
			System.out.println("Method: " + method.getName());
			for (ScenarioData scenario : query.getScenariosFromMethod(method)) {
				System.out.println("\tScenario: " + scenario.getName());
			}
		}
		
		for (String annotation_name : new String[]{"Performance", "Security", "Reliability"}) {
			System.out.println("\nAnnotation: " + annotation_name);
			
			System.out.println("\tDirect:");
			for (MethodData method : query.getDirectMethods(annotation_name))
				System.out.println("\t\t" + method.getDeclaringClass().getName() + "." + method.getName());
			
			System.out.println("\tIndirect:");
			for (MethodData method : query.getIndirectMethods(annotation_name))
				System.out.println("\t\t" + method.getDeclaringClass().getName() + "." + method.getName());
			
			System.out.println("\tScenarios:");
			for (ScenarioData scenario : query.getScenariosByQA(annotation_name))
				System.out.println("\t\t" + scenario.getName());
		}
		
		// dado o cenário, quais QAs atuam nele (retorna todas as instâncias)
		System.out.println();
		for (ScenarioData scenario : data.getScenarios()) {
			System.out.println("Scenario: " + scenario.getName());
			for (AbstractQAData qa : query.getQAsByScenario(scenario))
				System.out.println("\t" + (qa.getName().isEmpty()? "No Name" : qa.getName())
						+ ", " + qa.getType().getSimpleName());
		}
		
		/* dado o cenário, quais os TIPOS QAs atuam nele (retorna apenas os tipos)
		 * OBS: cenários com mais de um tipo de QA atuante tem tendência a gerar conflitos
		 */
		System.out.println();
		for (ScenarioData scenario : data.getScenarios()) {
			System.out.println("Scenario: " + scenario.getName());
			for (String qa_name : query.getQATypesByScenario(scenario))
				System.out.println("\t" + qa_name);
		}
		
		// retorna todos os cenários com conflitos de atributos de qualidade
		System.out.println("\nScenarios trade off:");
		for (ScenarioData scenario : query.getConflitedScenarios())
			System.out.println("\t" + scenario.getName());
		
		// dado o cenário, retornar os métodos com atributos de qualidade
		for (ScenarioData scenario : data.getScenarios()) {
			System.out.println("\nScenario:");
			
			for (MethodData method : query.getMethodQAByScenario(scenario)) {
				System.out.println("\t" + method.getName());
				
				for (AbstractQAData qa : method.getQualityAttributes()) {
					System.out.println("\t\t" + (qa.getName().isEmpty()? "No Name" : qa.getName())
							+ ", " + qa.getType().getSimpleName());
				}
			}
		}

	}

	public void process(IProject project) {
		try {
			processJavaProject(project);
		} catch (CoreException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Seria possível usar esse algoritmo? Aparenta que seria mais rápido indexar IMethods
	 * do que indexar MethodDeclarations, porém desdes é possível realizar parser direto e
	 * daqueles não. De fato não consegui uma forma de fazer parser de um IMethod
	 */
	public static void printIMethods(ICompilationUnit unit) throws JavaModelException {
		for (IType type : unit.getAllTypes())
			for (IMethod method : type.getMethods())
				System.out.println("[printIMethods] Key: " + method.getKey());
	}

}