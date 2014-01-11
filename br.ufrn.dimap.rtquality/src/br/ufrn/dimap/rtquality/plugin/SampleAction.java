package br.ufrn.dimap.rtquality.plugin;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.Runner;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.tmatesoft.svn.core.SVNException;

import com.thoughtworks.xstream.XStream;

import br.ufrn.dimap.rtquality.history.History;
import br.ufrn.dimap.rtquality.history.Project;
import br.ufrn.dimap.rtquality.history.SVNConfig;
import br.ufrn.dimap.rtquality.regressiontest.DiffRegressionTest;
import br.ufrn.dimap.rtquality.regressiontest.RegressionTestTechnique;
import br.ufrn.dimap.rtquality.util.MathUtil;
import br.ufrn.dimap.rtquality.util.ProjectUtil;
import br.ufrn.dimap.rtquality.util.TestUtil;
import br.ufrn.dimap.ttracker.data.MethodData;
import br.ufrn.dimap.ttracker.data.MethodState;
import br.ufrn.dimap.ttracker.data.Revision;
import br.ufrn.dimap.ttracker.data.Task;
import br.ufrn.dimap.ttracker.data.TestCoverage;
import br.ufrn.dimap.ttracker.data.TestCoverageMapping;
import br.ufrn.dimap.ttracker.util.FileUtil;
import br.ufrn.dimap.ttracker.util.ObjectUtil;

/**
 * Our sample action implements workbench action delegate. The action proxy will
 * be created by the workbench and shown in the UI. When the user tries to use
 * the action, this delegate will be created and execution will be delegated to
 * it.
 * 
 * @see IWorkbenchWindowActionDelegate
 */
public class SampleAction implements IWorkbenchWindowActionDelegate {
	private IWorkbenchWindow window;

	/**
	 * The constructor.
	 */
	public SampleAction() {
	}

	/**
	 * The action has been activated. The argument of the method represents the
	 * 'real' action sitting in the workbench UI.
	 * 
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action) {
		System.out.println("run(IAction action)");
		MessageDialog.openInformation(window.getShell(), "TestTracker", "Iniciando Estudo Emp�rico...");
		try {
			estudoEmpirico();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void estudoEmpirico() throws Exception {
		List<Revision> revisionForCheckout = new ArrayList<Revision>();
		List<Task> tasks = loadTasks(revisionForCheckout);
		
		List<Project> projects = new ArrayList<Project>(); //TODO: verificar o forCheckout pois ele n�o pode mais ser nulo aqui
		projects.add(new Project("/LIBS", "/LIBS", null, false));
		projects.add(new Project("/Arquitetura", "/01_Arquitetura", null, false));
		projects.add(new Project("/ServicosIntegrados", "/03_ServicosIntegrados", null, false));
		projects.add(new Project("/EntidadesComuns", "/02_EntidadesComuns", null, false));
		projects.add(new Project("/SharedResources", "/04_SharedResources", null, false));
		projects.add(new Project("/ServicoRemotoBiblioteca", "/ServicoRemotoBiblioteca", null, false));
		projects.add(new Project("/SIGAAMobile/implementacao/codigo/SIGAAMobileObjects", "/SIGAAMobileObjects", null, false));
		Set<String> packagesToTest = new HashSet<String>(1);
		packagesToTest.add("/SIGAA/biblioteca");
		projects.add(new Project("/SIGAA", "/SIGAA", null, true, packagesToTest)); //TODO: o ttracker est� realmente rastreando apenas este projeto ou acaba saindo dele?
		
		String URL = FileUtil.loadTextFromFile(new File("D:/UFRN/Scenario-analyzer/workspace-biblioteca/URL.txt"));
		String usuario = FileUtil.loadTextFromFile(new File("D:/UFRN/Scenario-analyzer/workspace-biblioteca/usuario.txt"));
		String senha = FileUtil.loadTextFromFile(new File("D:/UFRN/Scenario-analyzer/workspace-biblioteca/senha.txt"));
		SVNConfig sVNConfig = new SVNConfig(URL, projects, usuario, senha);
		
		RegressionTestTechnique regressionTestTechnique = new DiffRegressionTest();
		
		List<Project> projectForExecuteAllTests = new ArrayList<Project>();
		for(Project project : projects) {
			if(project.isAspectJNature())
				projectForExecuteAllTests.add(project);
		}
		
		/*
		 * Os m�todos modificados
		 * A=R4-R0
		 * B=(R1-R0)+(R2-R1)+(R4-R3)
		 * C=intersec��o(A,B)
		 */
		
		try {
			IWorkspace iWorkspace = ResourcesPlugin.getWorkspace();
			History history = new History(sVNConfig, iWorkspace);
			for(Task task : tasks) {
				for(Revision revision : task.getRevisions()) {
					revision.setModifiedMethods(history.getChangedMethodsSignaturesFromProjects(projects, revision.getOldId(), revision.getId()));
					revision.setUndoModifiedMethods(history.getChangedMethodsSignaturesFromProjects(projects, revision.getOldId(), task.getOldRevision().getId()));
					Set<String> undoModifiedMethods = new HashSet<String>(revision.getModifiedMethods());
					undoModifiedMethods.removeAll(MathUtil.intersection(undoModifiedMethods,revision.getUndoModifiedMethods()));
					revision.setUndoModifiedMethods(undoModifiedMethods);
				}
				Set<String> Undo = new HashSet<String>();
				Set<String> allModifiedMethods = new HashSet<String>();
				for(int index=task.getRevisions().size()-1;index>=0;index--) {
					Revision revision = task.getRevisions().get(index);
					Undo.addAll(revision.getUndoModifiedMethods());
					revision.getModifiedMethods().removeAll(Undo);
					allModifiedMethods.addAll(revision.getModifiedMethods());
				}
				task.setModifiedMethods(allModifiedMethods);
			}
			/*
			 * Loop para cada revis�o
			 * 	Obtenho todas as modifica��es da revis�o atual para a anterior (Rx-Rx-1)
			 * 	Obtenho todas as modifica��es da revis�o atual para a inicial (Rx-R0)
			 *  Obtenho o conjunto das modifica��es desfeitas pela revis�o Ex = (Rx-Rx-1)-((Rx-Rx-1)I(Rx-R0))
			 * End Loop
			 * Variavel E = {}
			 * Loop para cada revis�o (da maior para a menor)
			 * 	Faz a uni�o entre E com Ex salvando em E
			 * 	Faz a intersec��o entre atual-anterior e E e salvando em atual-anterior
			 * End Loop
			 * Faz a uni�o das modifica��es atual-anterior para obter o conjunto total das modifica��es que n�o foram desfeitas
			 *  
			 */
			
			for(Revision revision : revisionForCheckout) {
				if(revisionForCheckout.get(0).equals(revision))
					history.checkoutProjects(revision.getId());
				else
					history.updateProjects(revision.getId());
				
				Integer allTestsCount = 0;
				for(Project project : projectForExecuteAllTests) {
					if(!project.isExecuteTests())
						continue;
					IProject iProject = project.getIProject();
					ClassLoader iProjectClassLoader = ProjectUtil.getIProjectClassLoader(iProject);
					TestUtil.executeTests(iProjectClassLoader, ProjectUtil.getAllTestClasses(iProject, project.getPackagesToTest()), iWorkspace.getRoot().getLocation().toString()+"/result", "TCM_"+revision.getId(), project.getProjectName());
//TODO: Verificar se os TCMs n�o est�o sendo acumulados, se sim, algum procedimento deve zerar a inst�ncia do TCM utilizado para cada 
					ProjectUtil.setAllUncoveredMethods(project, "TCM_"+revision.getId());
				}
				
				for(Project project : projectForExecuteAllTests) {
					for(Task task : revision.getOldTasks()) {
						// Pythia - a regression test selection tool based on textual differencing
						regressionTestTechnique.setName("Pythia");
						regressionTestTechnique.setRevision(revision);
						regressionTestTechnique.setIProject(project.getIProject());
						TestCoverageMapping TCM = ProjectUtil.getTestCoverageMapping(project.getIProject(), "TCM_"+revision.getId());
						Object configurations[] = { TCM };
						regressionTestTechnique.setConfiguration(configurations);
						//TODO: cada t�cnica de teste de regress�o deve implementar sua pr�pria t�cnica de obten��o dos m�todos modificados
						regressionTestTechnique.setModifiedMethods(task.getModifiedMethods()); //TODO: Verificar se o TCM que est� dentro do DiffRegressionTest � uma c�pia ou o mesmo objeto (deve ser o mesmo), este objeto n�o deve ser salvo pois prejudicaria a constru��o do MethodState de outras vers�es
						Set<TestCoverage> allOldTests = TCM.getTestCoverages(); 
						Set<TestCoverage> techniqueSelection = regressionTestTechnique.executeRegression();
						Set<TestCoverage> techniqueExclusion = new HashSet<TestCoverage>(allOldTests);
						techniqueExclusion.removeAll(techniqueSelection);
						FileUtil.saveObjectToFile(allOldTests, iWorkspace.getRoot().getLocation().toString()+"\result", "AllOldTests_"+task.getId(), "slc");
						FileUtil.saveObjectToFile(techniqueSelection, iWorkspace.getRoot().getLocation().toString()+"\result", "RTSSelection_"+regressionTestTechnique.getName()+"_"+task.getId(), "slc");
						FileUtil.saveObjectToFile(techniqueExclusion, iWorkspace.getRoot().getLocation().toString()+"\result", "RTSExclusion_"+regressionTestTechnique.getName()+"_"+task.getId(), "slc");
					}
					for(Task task : revision.getCurrentTasks()) { //TODO: Para que tecnicas que utilizam a currente funcionem terei que colocar a execu��o dela aqui
						Set<String> modifiedMethods = task.getModifiedMethods();
						TestCoverageMapping TCM = ProjectUtil.getTestCoverageMapping(project.getIProject(), "TCM_"+revision.getId());
						TCM.setModifiedMethods(modifiedMethods);
						Set<TestCoverage> toolSelection = TCM.getModifiedChangedTestsCoverage();
						Set<TestCoverage> perfectExclusion = (Set<TestCoverage>) FileUtil.loadObjectFromFile(iWorkspace.getRoot().getLocation().toString()+"\result", "AllOldTests_"+task.getId(), "slc");
						Set<TestCoverage> perfectSelection = MathUtil.intersection(perfectExclusion, toolSelection);
						perfectExclusion.removeAll(perfectSelection);
						FileUtil.saveObjectToFile(perfectSelection, iWorkspace.getRoot().getLocation().toString()+"\result", "PerfectSelection_"+task.getId(), "slc");
						FileUtil.saveObjectToFile(perfectExclusion, iWorkspace.getRoot().getLocation().toString()+"\result", "PerfectExclusion_"+task.getId(), "slc");
					}
				}
				//TODO: cada revis�o pode conter novos testes ou exclu�dos testes da revis�o anterior, ao calcular as m�tricas tem de levar em considera��o apenas o que j� existia
			}
			
			Map<String,Set<Task>> taskTypes = new HashMap<String,Set<Task>>(4);
			taskTypes.put(Task.APRIMORAMENTO, new HashSet<Task>());
			taskTypes.put(Task.ERRO, new HashSet<Task>());
			taskTypes.put(Task.ERRONEGOCIOVALIDACAO, new HashSet<Task>());
			taskTypes.put(Task.VERIFICACAO, new HashSet<Task>());
			for(Task task : tasks) {
				Set<TestCoverage> techniqueSelection = (Set<TestCoverage>) FileUtil.loadObjectFromFile(iWorkspace.getRoot().getLocation().toString()+"\result", "RTSSelection_"+task.getId(), "slc");
				Set<TestCoverage> techniqueExclusion = (Set<TestCoverage>) FileUtil.loadObjectFromFile(iWorkspace.getRoot().getLocation().toString()+"\result", "RTSExclusion_"+task.getId(), "slc");
				Set<TestCoverage> perfectSelection = (Set<TestCoverage>) FileUtil.loadObjectFromFile(iWorkspace.getRoot().getLocation().toString()+"\result", "PerfectSelection_"+task.getId(), "slc");
				Set<TestCoverage> prefectExclusion = (Set<TestCoverage>) FileUtil.loadObjectFromFile(iWorkspace.getRoot().getLocation().toString()+"\result", "PerfectExclusion_"+task.getId(), "slc");
				taskTypes.get(task.getType()).add(task);
			}
			/*
			 ** Cada revision do forCheckout deve possuir uma lista com as tarefas na qual ela � oldRevision e uma lista com as tarefas nas quais ela � currentRevision
			 ** Loop para cada task
			 	 ** Obter o conjunto das modifica��es e salvar na estrutura da task (Lembrar da l�gica de capturar cada modifica��o de cada revis�o e gerar um conjunto consistente)
		 	 ** End Loop
		 	 ** Loop para cada Project em projects
		 	 	 ** Identificar quais projetos devem ter seus testes executados
	 	 	 ** End Loop
			 ** Loop para cada revis�o em forCheckout
			 	 ** Fazer o checkout dos projetos nesta revis�o ou o update, caso a revis�o n�o seja a primeira 
			 	 ** Executar todos os testes nesta revis�o para os projetos identificados 
				 ** Salvar o TestCoverageMapping em arquivos com o n�mero da revis�o (TCM_revisionId)
			 	 ** Loop Para cada task cuja revision seja oldRevision
					 ** Pegar o conjunto de modifica��es da task 
					 ** Executar a RTS passando as modifica��es (RTS baseadas em oldRevision)
					 	* A RTS deve ser respons�vel por obter seu proprio conjunto de modifica��es, j� levando em considera��o quais m�todos s�o inv�lidos 
				 	 ** Salvar uma rela��o contendo todos os testes existentes (AllTestsOld_taskId.slc)
					 ** Salvar o resultado da t�cnica com o n�mero da task (RTSNome_taskId.slc)
				 ** End Loop
				 * Loop Para cada task cuja revision seja currentRevision
				 	 ** Pegar o conjunto de modifica��es da task
				 	 * Executar a ferramenta para achar a solu��o perfeita
				 	 * Salvar uma rela��o contendo todos os testes existentes (AllTestsCurrent_taskId.slc)
				 	 * Salvar o resultado perfeito com o n�mero da task (Perfect_taskId)
			 	 * End Loop
			 * End Loop
			 * Loop Para cada task
			 	 * Calcular as m�tricas carregando os arquivos RTSNome_taskId e Perfect_taskId
		 	 * End Loop
		 	 * Onde entra a atualiza��o dos AllMethods? 
		 	 * -----------------------------------------------------------------------------------------
		 	 * Baixar primeira revis�o
			 * Para cada tarefa
			 	 * Carregar o TestCoverageMapping do arquivo para a oldRevision e currentRevision da tarefa
				 * Obter modifica��es at� a �ltima revis�o da tarefa
				 * Atualizar a estrutura do TestCoverageMapping carregado (n�o atualizar o arquivo)
				 * Obter a solu��o ideal a partir da currentRevision
				 * Executar t�cnicas de RTS tanto as que utilizam a oldRevision quanto as que utilizam a currentRevision
				 * Calcular as m�tricas
				 * Salvar o TestCoverageMapping com a informa��o da revis�o dele no nome do arquivo (Diret�rio results)
				 * Fazer Update
			 * Loop End
			 */
//		/*
//		 * coletar as revisões de cada task chamar passo1 passando todas as
//		 * revisões
//		 */
//		Map<Revision,Set<Task>> forExecuteAllTests = new HashMap<Revision,Set<Task>>();
//		Set<Revision> forRegression = new HashSet<Revision>();
//		for (Task task : tasks) {
//			forRegression.add(task.getOldRevision());
//			if(forExecuteAllTests.containsKey(task.getOldRevision()))
//				forExecuteAllTests.get(task.getOldRevision()).add(task);
//			else {
//				Set<Task> revisionTasks = new HashSet<Task>();
//				revisionTasks.add(task);
//				forExecuteAllTests.put(task.getOldRevision(), revisionTasks);
//			}
//			if(forExecuteAllTests.containsKey(task.getCurrentRevision()))
//				forExecuteAllTests.get(task.getCurrentRevision()).add(task);
//			else {
//				Set<Task> revisionTasks = new HashSet<Task>();
//				revisionTasks.add(task);
//				forExecuteAllTests.put(task.getCurrentRevision(), revisionTasks);
//			}
//		}
			
			
			
			
			
			
//			
//			Integer index = 0;
//			History history = new History(sVNConfig, ResourcesPlugin.getWorkspace());
//			history.checkoutProjects(revisionForCheckout.get(index).getId());
//			do {
//				if(index > 0)
//					history.updateProjects(revisionForCheckout.get(index).getId());
//				
//				//Executar todos os testes nesta revis�o
//				IProject iProject = ResourcesPlugin.getWorkspace().getRoot().getProject("SIGAA");
//				ClassLoader iProjectClassLoader = ProjectUtil.getIProjectClassLoader(iProject);
//				TestUtil.executeTests(iProjectClassLoader, ProjectUtil.getAllTestClasses(iProject, "/SIGAA/biblioteca"), "AllTests_"+revisionForCheckout.get(index).getId());
//				ProjectUtil.setAllUncoveredMethods(iProject, "AllTests_"+revisionForCheckout.get(index).getId());
//				
//				//Obter modifica��es at� a �ltima vers�o da tarefa a qual est� revis�o pertence como "inicial"!!!
//				//Executar t�cnicas que utilizam esta revis�o como a inicial (S� acontece quando existir uma tarefa que tenha esta revis�o como "inicial"
//				//Salvar o TestCoverageMapping com a informa��o da revis�o dele no nome do arquivo (Diret�rio results)
//				//Fazer Update
//				index++;
//			} while(index < revisionForCheckout.size()-1);
//			
//			
//			
//			
			
//			for (Revision revision : forCheckoutAndExecuteAllTests) {
//				for(IProject iProject : ProjectUtil.getIProjects(sVNConfig, revision.getId())) {
//					ClassLoader iProjectClassLoader = ProjectUtil.getIProjectClassLoader(iProject);
//					TestUtil.executeTests(iProjectClassLoader, ProjectUtil.getAllTestClasses(iProject), "AllTests");
//					ProjectUtil.setAllUncoveredMethods(iProject, "AllTests");
//				}
//			}
//			for (Revision revision : forRegression) {
//				try {
//					//Intersecção entre TestCoverageMappings
//					IProject newIProject = ProjectUtil.getIProject(sVNConfig, revision.getId());
//					TestCoverageMapping newTestCoverageMapping = ProjectUtil.getTestCoverageMapping(newIProject, "AllTests");
//					IProject oldIProject = ProjectUtil.getIProject(sVNConfig, revision.getOldId());
//					TestCoverageMapping oldTestCoverageMapping = ProjectUtil.getTestCoverageMapping(oldIProject, "AllTests");
//					TestCoverageMapping.intersection(oldTestCoverageMapping, newTestCoverageMapping);
//					//Intersecção entre ModifiedMethods
//					Set<String> modifiedMethods = history.getChangedMethodsSignatures(revision.getOldId(), revision.getId());
//					Set<String> oldValidModifiedMethods = oldTestCoverageMapping.getValidModifiedMethods(modifiedMethods);
//					Set<String> newValidModifiedMethods = newTestCoverageMapping.getValidModifiedMethods(modifiedMethods);
//					Set<String> validModifiedMethods = MathUtil.intersection(oldValidModifiedMethods, newValidModifiedMethods);
//					
//					// Pythia - a regression test selection tool based on textual differencing
//					regressionTestTechnique.setRevision(revision);
//					regressionTestTechnique.setIProject(newIProject);
//					Object configurations[] = { oldTestCoverageMapping };
//					regressionTestTechnique.setConfiguration(configurations);
//					//TODO: cada técnica de teste de regressão deve implementar sua própria técnica de obtenção dos métodos modificados
//					regressionTestTechnique.setModifiedMethods(validModifiedMethods);
//					Set<TestCoverage> allTestsOldProject = oldTestCoverageMapping.getTestCoverages(); 
//					Set<TestCoverage> techniqueTestSelection = regressionTestTechnique.executeRegression();
//					Map<MethodState, Map<String, MethodData>> techniqueMetricsTable = regressionTestTechnique.getMethodStatePool();
//					
//					// Seleção Ideal
//					//TODO: segundo Gregg Rothermel a identificação das modificações devem ser obtidas a partir
//					//da identificação de diferenças nas pilhas de execução e não pelo text diff
//					newTestCoverageMapping.setModifiedMethods(validModifiedMethods);
//					Set<TestCoverage> allTestsNewProject = newTestCoverageMapping.getTestCoverages();
//					Set<TestCoverage> allValidTests = TestCoverage.intersection(allTestsOldProject, allTestsNewProject);
//					Set<TestCoverage> idealTestSelection = newTestCoverageMapping.getModifiedChangedTestsCoverage();
//					techniqueTestSelection = TestCoverage.intersection(techniqueTestSelection, allValidTests);
//					idealTestSelection = TestCoverage.intersection(idealTestSelection, allValidTests);
//					Map<MethodState, Map<String, MethodData>> idealMetricsTable = newTestCoverageMapping.getMethodStatePool();
//					
//					// Cálculo das Métricas
//					TestUtil.metricMeansurement(techniqueMetricsTable, idealMetricsTable, revision);
//					TestUtil.metricMeansurement2(techniqueTestSelection, idealTestSelection, allValidTests, revision);
//					TestUtil.printRevision(revision);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
		} catch (SVNException e1) {
			e1.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private List<Task> loadTasks(List<Revision> revisionForCheckout) {
		String xml = FileUtil.loadTextFromFile(new File("D:/UFRN/Scenario-analyzer/workspace-biblioteca/br.ufrn.dimap.rtquality/lib/Tasks.xml"));
		XStream xstream = new XStream();
		List<Task> tasks = (List<Task>) xstream.fromXML(xml);
		Map<Integer,Revision> allRevisionsMap = new HashMap<Integer,Revision>();
		for (Task task : tasks) {
			for (Revision revision : task.getRevisions()) 
				revision.setOldId(revision.getId()>1 ? revision.getId()-1 : 1);
			Collections.sort(task.getRevisions());
			
			if(task.getRevisions().size() > 0) {
				//Definindo a oldRevision da oldTask e as oldTasks da oldRevision
				Set<Task> oldTasks = new HashSet<Task>(1);
				oldTasks.add(task);
				Revision oldRevision = new Revision(task.getRevisions().get(0).getOldId(),oldTasks,new HashSet<Task>(1));
				if(allRevisionsMap.containsKey(oldRevision.getId()))
					allRevisionsMap.get(oldRevision.getId()).getOldTasks().addAll(oldTasks);
				else
					allRevisionsMap.put(oldRevision.getId(),oldRevision);
				task.setOldRevision(oldRevision);
				
				//Definindo a currentRevision da currentTask e as currentTasks da currentRevision
				Set<Task> currentTasks = new HashSet<Task>(1);
				currentTasks.add(task);
				Revision currentRevision = task.getRevisions().get(task.getRevisions().size()-1);
				if(allRevisionsMap.containsKey(currentRevision.getId()))
					allRevisionsMap.get(currentRevision.getId()).getCurrentTasks().addAll(currentTasks);
				else {
					if(currentRevision.getCurrentTasks() == null)
						currentRevision.setCurrentTasks(currentTasks);
					else
						currentRevision.getCurrentTasks().addAll(currentTasks);
					allRevisionsMap.put(currentRevision.getId(),currentRevision);
				}
				task.setCurrentRevision(currentRevision);
			}
		}
		revisionForCheckout.addAll(allRevisionsMap.values());
		Collections.sort(revisionForCheckout);
		return tasks;
	}

	private Collection<String> getSourceFolders(IProject iProject) throws JavaModelException {
		IJavaProject iJavaProject = JavaCore.create(iProject);
		Set<String> testSourceFolders = new HashSet<String>();
		for (IPackageFragmentRoot iPackageFragmentRoot : iJavaProject.getAllPackageFragmentRoots()) {
			if (iPackageFragmentRoot.getKind() == IPackageFragmentRoot.K_SOURCE) {
				testSourceFolders.add(iPackageFragmentRoot.getPath().toString());
			}
		}
		return testSourceFolders;
	}

	private String getTestDirectory(IProject iProject, String sourceFolder) {
		return iProject.getWorkspace().getRoot().getLocation().toString() + sourceFolder
				+ "/br/ufrn/dimap/testtracker/tests";
	}

	// private TestCoverageMapping getTestCoverageMapping(String
	// fileDirectories, Revision currentRevision) {
	// TestCoverageMapping testCoverageMapping = new TestCoverageMapping();
	// testCoverageMapping.setCurrentRevision(oldRevision);
	// testCoverageMapping.setOldRevision = new Revision();
	// this.methodPool = new HashMap<String, MethodData>();
	// this.testCoverageBuilding = new HashMap<Long,TestCoverage>();
	// this.testCoverage = new TreeSet<TestCoverage>();
	// this.nextId = 1;
	// fileDirectory = "C:/";
	// FileUtil.loadFileToObject(fileDirectory, fileName, extension)
	// }

	private static boolean aRun(String workspace) {
		boolean failure = false;
		try {
			URL classUrl = new URL("file://" + workspace + "/WebContent/WEB-INF/classes/br/ufrn/tests");
			URL[] classUrls = { classUrl };
			URLClassLoader ucl = new URLClassLoader(classUrls);
			Class<?> allTestsClass = ucl.loadClass("OperacaoBeanTeste");
			Runner r = new BlockJUnit4ClassRunner(allTestsClass);
			JUnitCore c = new JUnitCore();
			Result result = c.run(Request.runner(r));
			failure = result.getFailureCount() > 0;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InitializationError e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return failure;
	}

	/**
	 * Selection in the workbench has been changed. We can change the state of
	 * the 'real' action here if we want, but this can only happen after the
	 * delegate has been created.
	 * 
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

	/**
	 * We can use this method to dispose of any system resources we previously
	 * allocated.
	 * 
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose() {
	}

	/**
	 * We will cache window object in order to be able to provide parent shell
	 * for the message dialog.
	 * 
	 * @see IWorkbenchWindowActionDelegate#init
	 */
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}
}