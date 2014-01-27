package br.ufrn.dimap.rtquality.plugin;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.internal.resources.mapping.ResourceChangeDescriptionFactory;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.mapping.IResourceChangeDescriptionFactory;
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
import org.tmatesoft.svn.core.wc.SVNRevision;

import com.sun.corba.se.impl.orbutil.DenseIntMapImpl;
import com.thoughtworks.xstream.XStream;

import br.ufrn.dimap.rtquality.history.History;
import br.ufrn.dimap.rtquality.history.Project;
import br.ufrn.dimap.rtquality.history.ProjectRevisionInformations;
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
import br.ufrn.dimap.ttracker.data.TaskType;
import br.ufrn.dimap.ttracker.data.TaskTypeSet;
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
		MessageDialog.openInformation(window.getShell(), "TestTracker", "Iniciando Estudo Empírico...");
		try {
			estudoEmpirico();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public enum Dependence implements Serializable {
		ARQ("arq","Arquitetura"), COMUM("comum","EntidadesComuns"), DTO("dto","ServicosIntegrados"), LIBS("libs", "LIBS"), SERVICOS_BIBLIOTECA("servicos_biblioteca", "SharedResources"), SIGAA_MOBILE_OBJECTS("sigaa_mobile_objects", "Arquitetura"), SharedResources("shared_resources", "SharedResources");		
		private String version;
		private String jarName;
		private String name;

		Dependence(String jarName, String name) {
			this.jarName = jarName;
			this.name = name;
		}
		
		public String getVersion() {
			return version;
		}

		public void setVersion(String version) {
			this.version = version;
		}

		public String getJarName() {
			return jarName;
		}

		public String getName() {
			return name;
		}
		
	}
	
	public void estudoEmpirico2() {
		String sistema = "SIGAA";
		String versoes[] = {"3.11.24","3.12.18"};
		String URL = "http://desenvolvimento.info.ufrn.br/projetos/tags";
		String usuario = "joaoguedes";
		String senha = "v0c3m35m0";
		String pastaDdependencias = "dependencias";
		
		for(String versao : versoes) {
			String URLCompleta = URL+"/"+sistema+" "+versao;
			String URLDependencias = URLCompleta+"/"+pastaDdependencias;
			String dependencies[] = {};
			for(String dependencie : dependencies) {
				int start = dependencie.lastIndexOf("-");
				int end = dependencie.lastIndexOf(".");
				String dependencieVersion = dependencie.substring(start, end);
				String dependencieName = dependencie.substring(0, start-1);
			}
		}
	}

	public void estudoEmpirico() throws Exception {
		Boolean automatic = true;
		
		//Limpando os resultados da última execução
		IWorkspace iWorkspace = ResourcesPlugin.getWorkspace();
		try {
			File tempFolder = new File("temp");
			if(tempFolder != null && tempFolder.isDirectory())
				FileUtils.deleteDirectory(tempFolder); //TODO: Deletar o ProjectsUpdates.xml (Quando?)
			File projectsUpdates = new File("ProjectsUpdates.xml");
			if(projectsUpdates.exists())
				projectsUpdates.delete();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Map<Integer,Project> projects = loadProjects(iWorkspace.getRoot().getLocation().toString()+"/config");
		
		String URL = FileUtil.loadTextFromFile(new File(iWorkspace.getRoot().getLocation().toString()+"/config/URL.txt"));
		String usuario = FileUtil.loadTextFromFile(new File(iWorkspace.getRoot().getLocation().toString()+"/config/usuario.txt"));
		String senha = FileUtil.loadTextFromFile(new File(iWorkspace.getRoot().getLocation().toString()+"/config/senha.txt"));
		SVNConfig sVNConfig = new SVNConfig(URL, projects, usuario, senha);
		
		Map<Integer,Project> projects2 = new HashMap<Integer,Project>(); //TODO: verificar o forCheckout pois ele não pode mais ser nulo aqui
		projects2.put(1, new Project("/br.ufrn.dimap.ttracker", "/br.ufrn.dimap.ttracker", null, false));
		String URL2 = FileUtil.loadTextFromFile(new File(iWorkspace.getRoot().getLocation().toString()+"/config/URL2.txt"));
		String usuario2 = FileUtil.loadTextFromFile(new File(iWorkspace.getRoot().getLocation().toString()+"/config/usuario2.txt"));
		String senha2 = FileUtil.loadTextFromFile(new File(iWorkspace.getRoot().getLocation().toString()+"/config/senha2.txt"));
		SVNConfig sVNConfig2 = new SVNConfig(URL2, projects2, usuario2, senha2);
		
		RegressionTestTechnique regressionTestTechnique = new DiffRegressionTest();
		
		List<Project> projectForExecuteAllTests = new ArrayList<Project>();
		for(int i=1;i<=projects.size();i++) {
			Project project = projects.get(i);
			if(project.isAspectJNature())
				projectForExecuteAllTests.add(project);
		}
		
		/*
		 * Os métodos modificados
		 * A=R4-R0
		 * B=(R1-R0)+(R2-R1)+(R4-R3)
		 * C=intersecção(A,B)
		 */
		
		try {
			History history = new History(sVNConfig, iWorkspace);
			history.generateTasksXML();
			List<Revision> revisionForCheckout = new ArrayList<Revision>();
			List<Task> tasks = loadTasks(revisionForCheckout, iWorkspace.getRoot().getLocation().toString()+"/config");
			if(tasks == null) {
				System.out.println("Nenhuma informação sobre as tarefas foram encontradas...");
				return;
			}
			for(Task task : tasks) {
				if(task.isDoAndUndoDone()) {
					System.out.println("A tarefa "+task.getId().toString()+" já foi analisada...");
					continue;
				}
				for(Revision revision : task.getRevisions()) {
					if(revision.isDoAndUndoDone()) {
						System.out.println("A revisão "+revision.getId().toString()+" já foi analisada...");
						continue;
					}
					System.out.println("Analisando revisão: "+revision.getId().toString());
					try {
						Set<String> A = history.getChangedMethodsSignaturesFromProjects(new HashSet<Project>(projects.values()), revision.getOldId(), revision.getId());
						Set<String> B = history.getChangedMethodsSignaturesFromProjects(new HashSet<Project>(projects.values()), task.getOldRevision().getId(), revision.getId());
						Set<String> C = MathUtil.intersection(A,B);
						Set<String> D = new HashSet<String>(A);
						D.removeAll(C);
						revision.setModifiedMethods(C);
						revision.setUndoModifiedMethods(D);
						revision.setDoAndUndoDone(true);
					} catch (SVNException svne) {
						svne.printStackTrace();
						System.out.println("Conexão Perdida... ou Revisão muito antiga, e o arquivo/projeto ainda nem existe...");
						revision.setDoAndUndoDone(false);
						return;
					} catch (IOException ioe) {
						System.out.println("Problemas ao acessar arquivos...");
						revision.setDoAndUndoDone(false);
						return;
					} finally {
						if(revision.isDoAndUndoDone())
							FileUtil.saveObjectToFile(tasks, iWorkspace.getRoot().getLocation().toString()+"/config", "Tasks", "obj");
					}
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
				task.setDoAndUndoDone(true);
				FileUtil.saveObjectToFile(tasks, iWorkspace.getRoot().getLocation().toString()+"/config", "Tasks", "obj");
			}
			/*
			 * Loop para cada revisão
			 * 	Obtenho todas as modificações da revisão atual para a anterior (Rx-Rx-1)
			 * 	Obtenho todas as modificações da revisão atual para a inicial (Rx-R0)
			 *  Obtenho o conjunto das modificações desfeitas pela revisão Ex = (Rx-Rx-1)-((Rx-Rx-1)I(Rx-R0))
			 * End Loop
			 * Variavel E = {}
			 * Loop para cada revisão (da maior para a menor)
			 * 	Faz a união entre E com Ex salvando em E
			 * 	Faz a intersecção entre atual-anterior e E e salvando em atual-anterior
			 * End Loop
			 * Faz a união das modificações atual-anterior para obter o conjunto total das modificações que não foram desfeitas
			 *  
			 */
			
			History history2 = new History(sVNConfig2, iWorkspace);
//TODO: Essa revisão deve ser alterada quando seu código for atualizado
			history2.checkouOrUpdateProjects(295);
			for(Revision revision : revisionForCheckout) {
				String resultPath = iWorkspace.getRoot().getLocation().toString()+"/result";
				if(!(new File(resultPath+"/TCM_"+revision.getId()+".tcm")).exists()) {
					history.checkouOrUpdateProjects(revision.getId());
					Project aProject = projectForExecuteAllTests.get(0);
					Class<?> aClass = ProjectUtil.getIProjectClassLoader(aProject.getIProject()).loadClass(ProjectUtil.getAClass(aProject.getIProject()));
					ProjectUtil.saveUtilInformations(FileUtil.getBuildFolderByResource(aClass), iWorkspace.getRoot().getLocation().toString(), revision.getId(), aProject.getName());
					if(automatic) {
						for(Project project : projectForExecuteAllTests) {
							if(!project.isExecuteTests())
								continue;
							IProject iProject = project.getIProject();
							ClassLoader iProjectClassLoader = ProjectUtil.getIProjectClassLoader(iProject);
							TestUtil.executeTests(iProjectClassLoader, ProjectUtil.getAllTestClasses(iProject, project.getPackagesToTest()));
//TODO: Verificar se os TCMs não estão sendo acumulados, se sim, algum procedimento deve zerar a instância do TCM utilizado para cada 
							ProjectUtil.setAllUncoveredMethods(project, "TCM_"+revision.getId());
						}
					}
					else {
						Runtime.getRuntime().exec("D:/UFRN/Test_Quality/servers/SIGAA_2/bin/run.bat");
						System.out.println("Aguarde o JBoss e execute os testes manuais antes de continuar...");
						System.out.println("Testes finalizados...");
						for(Project project : projectForExecuteAllTests) {
							ProjectUtil.setAllUncoveredMethods(project, "TCM_"+revision.getId());
						}
					}
				}
				else {
					for(int i=1;i<=sVNConfig.getProjects().size();i++) {
			    		Project project = sVNConfig.getProjects().get(i);
			    		project.setIProject(iWorkspace.getRoot().getProject(project.getName())); //O projeto não precisa existir no workspace para setar esta informação.
					}
				}
				
				for(Project project : projectForExecuteAllTests) {
					for(Task task : revision.getOldTasks()) {
						String allOldTestsResultName = "AllOldTests_"+task.getId();
						String selectionResultName = "RTSSelection_"+regressionTestTechnique.getName()+"_"+task.getId();
						String exclusionResultName = "RTSExclusion_"+regressionTestTechnique.getName()+"_"+task.getId();
						if(!(new File(resultPath+"/"+selectionResultName+".slc")).exists() || !(new File(resultPath+"/"+exclusionResultName+".slc")).exists() || !(new File(resultPath+"/"+allOldTestsResultName+".slc")).exists()) {
							// Pythia - a regression test selection tool based on textual differencing
							regressionTestTechnique.setName("Pythia");
							regressionTestTechnique.setRevision(revision);
							regressionTestTechnique.setIProject(project.getIProject());
							TestCoverageMapping TCM = ProjectUtil.getTestCoverageMapping(resultPath, "TCM_"+revision.getId());
							Object configurations[] = { TCM };
							regressionTestTechnique.setConfiguration(configurations);
							//TODO: cada técnica de teste de regressão deve implementar sua própria técnica de obtenção dos métodos modificados
							regressionTestTechnique.setModifiedMethods(task.getModifiedMethods()); //TODO: Verificar se o TCM que está dentro do DiffRegressionTest é uma cópia ou o mesmo objeto (deve ser o mesmo), este objeto não deve ser salvo pois prejudicaria a construção do MethodState de outras versões
							Set<TestCoverage> allOldTests = TCM.getTestCoverages(); 
							Set<TestCoverage> techniqueSelection = regressionTestTechnique.executeRegression();
							Set<TestCoverage> techniqueExclusion = new HashSet<TestCoverage>(allOldTests);
							techniqueExclusion.removeAll(techniqueSelection);
							FileUtil.saveObjectToFile(allOldTests, iWorkspace.getRoot().getLocation().toString()+"/result", allOldTestsResultName, "slc");
							FileUtil.saveObjectToFile(techniqueSelection, iWorkspace.getRoot().getLocation().toString()+"/result", selectionResultName, "slc");
							FileUtil.saveObjectToFile(techniqueExclusion, iWorkspace.getRoot().getLocation().toString()+"/result", exclusionResultName, "slc");
						}
					}
					for(Task task : revision.getCurrentTasks()) { //TODO: Para que tecnicas que utilizam a currente funcionem terei que colocar a execução dela aqui
						String allOldTestsResultName = "AllOldTests_"+task.getId();
						String selectionResultName = "PerfectSelection_"+task.getId();
						String exclusionResultName = "PerfectExclusion_"+task.getId();
						if(!(new File(resultPath+"/"+selectionResultName+".slc")).exists() || !(new File(resultPath+"/"+exclusionResultName+".slc")).exists()) {
							Set<String> modifiedMethods = task.getModifiedMethods();
							TestCoverageMapping TCM = ProjectUtil.getTestCoverageMapping(resultPath, "TCM_"+revision.getId());
							TCM.setModifiedMethods(modifiedMethods);
							Set<TestCoverage> toolSelection = TCM.getModifiedChangedTestsCoverage();
							Set<TestCoverage> allOldTests = (Set<TestCoverage>) FileUtil.loadObjectFromFile(iWorkspace.getRoot().getLocation().toString()+"/result", allOldTestsResultName, "slc");
							Set<TestCoverage> perfectExclusion = new HashSet<TestCoverage>(allOldTests);
							Set<TestCoverage> perfectSelection = MathUtil.intersection(allOldTests, toolSelection);
							perfectExclusion.removeAll(perfectSelection);
							FileUtil.saveObjectToFile(perfectSelection, iWorkspace.getRoot().getLocation().toString()+"/result", selectionResultName, "slc");
							FileUtil.saveObjectToFile(perfectExclusion, iWorkspace.getRoot().getLocation().toString()+"/result", exclusionResultName, "slc");
						}
					}
				}
				File currentRevisionFile = new File(iWorkspace.getRoot().getLocation().toString()+"/config/currentRevision.txt");
				String currentRevision = FileUtil.loadTextFromFile(currentRevisionFile);
				if(currentRevision == null || currentRevision.equals(revision.getId().toString())) {
					for(int i=sVNConfig.getProjects().size();i>=1;i--) {
						Project project = sVNConfig.getProjects().get(i);
						IProject iProject = project.getIProject();
						if(iProject.exists()) {
							if(iProject.isOpen()) {
								iProject.build(IncrementalProjectBuilder.CLEAN_BUILD, new SysOutProgressMonitor());
								iProject.close(new SysOutProgressMonitor());
							}
							iProject.delete(true, new SysOutProgressMonitor());
						}
						File projectFile = new File(iWorkspace.getRoot().getLocation().toString()+project.getName());
						if(projectFile.exists()) {
							try {
								FileUtils.deleteDirectory(projectFile);
							} catch (IOException ioe) {
								ioe.printStackTrace();
							}
						}
					}
					if(currentRevisionFile != null && currentRevisionFile.exists())
						currentRevisionFile.delete();
				}
				//TODO: cada revisão pode conter novos testes ou excluídos testes da revisão anterior, ao calcular as métricas tem de levar em consideração apenas o que já existia
			}
			
			Map<TaskType,TaskTypeSet> taskTypes = new HashMap<TaskType,TaskTypeSet>(4);
			taskTypes.put(TaskType.APRIMORAMENTO, new TaskTypeSet(TaskType.APRIMORAMENTO));
			taskTypes.put(TaskType.ERRO, new TaskTypeSet(TaskType.ERRO));
			taskTypes.put(TaskType.ERRONEGOCIOVALIDACAO, new TaskTypeSet(TaskType.ERRONEGOCIOVALIDACAO));
			taskTypes.put(TaskType.VERIFICACAO, new TaskTypeSet(TaskType.VERIFICACAO));
			for(Task task : tasks) {
				Set<TestCoverage> techniqueSelection = getTestCoverageSet(iWorkspace.getRoot().getLocation().toString()+"/result", "RTSSelection_"+task.getId());
				Set<TestCoverage> techniqueExclusion = getTestCoverageSet(iWorkspace.getRoot().getLocation().toString()+"/result", "RTSExclusion_"+task.getId());
				Set<TestCoverage> perfectSelection = getTestCoverageSet(iWorkspace.getRoot().getLocation().toString()+"/result", "PerfectSelection_"+task.getId());
				Set<TestCoverage> perfectExclusion = getTestCoverageSet(iWorkspace.getRoot().getLocation().toString()+"/result", "PerfectExclusion_"+task.getId());
				task.setInclusion(TestUtil.getInclusionMeasure(techniqueSelection, perfectSelection)*100);
				task.setPrecision(TestUtil.getPrecisionMeasure(techniqueExclusion, perfectExclusion)*100);
				TaskTypeSet taskTypeSet = taskTypes.get(task.getType());
				taskTypeSet.setInclusion(taskTypeSet.getInclusion()+task.getInclusion());
				taskTypeSet.setPrecision(taskTypeSet.getPrecision()+task.getPrecision());
				taskTypeSet.getTasks().add(task);
			}
			
			for(TaskTypeSet taskTypeSet : taskTypes.values()) {
				taskTypeSet.setInclusion(taskTypeSet.getInclusion()/new Float(taskTypeSet.getTasks().size()));
				taskTypeSet.setPrecision(taskTypeSet.getPrecision()/new Float(taskTypeSet.getTasks().size()));
			}
			System.out.println();
			
		} catch (SVNException e1) {
			e1.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Map<Integer,Project> loadProjects(String location) throws Exception {
		Map<Integer,Project> projects = loadProjectsManually();
//		Map<String,ProjectRevisionInformations> projectRevisionInformations = null;
//		try {
//			Object obj = FileUtil.loadObjectFromFile(location, "Projects", "obj");
//			if(obj != null && obj instanceof HashMap<?,?>) {
//				projectRevisionInformations = (HashMap<String,ProjectRevisionInformations>) obj;
//				if(projectRevisionInformations != null) {
//					for(Project project : projects)
//						project.setProjectRevisionInformations(projectRevisionInformations.get(project.getPath()));
//				}
//			}
//			return projects;
//		} catch(ClassCastException cce) {
//			return projects;
//		}
		return projects;
	}

	private Map<Integer,Project> loadProjectsManually() throws Exception {
		Map<Integer,Project> projects = new HashMap<Integer,Project>();
		projects.put(1, new Project("/LIBS", "/LIBS", null, false));
		projects.put(2, new Project("/Arquitetura", "/01_Arquitetura", null, false));
		projects.put(3, new Project("/ServicosIntegrados", "/03_ServicosIntegrados", null, false));
		projects.put(4, new Project("/EntidadesComuns", "/02_EntidadesComuns", null, false));
		projects.put(5, new Project("/SharedResources", "/04_SharedResources", null, false));
		projects.put(6, new Project("/ServicoRemotoBiblioteca", "/ServicoRemotoBiblioteca", null, false));
		projects.put(7, new Project("/SIGAAMobile/implementacao/codigo/SIGAAMobileObjects", "/SIGAAMobileObjects", null, false));
		Set<String> packagesToTest = new HashSet<String>(1);
		packagesToTest.add("/SIGAA/biblioteca");
		projects.put(8, new Project("/SIGAA", "/SIGAA", null, true, packagesToTest)); //TODO: o ttracker está realmente rastreando apenas este projeto ou acaba saindo dele? Não deveria sair dele?
		return projects;
	}
	
	private List<Task> loadTasks(List<Revision> revisionForCheckout, String location) {
		List<Task> tasks = null;
		Object obj = FileUtil.loadObjectFromFile(location, "Tasks", "obj");
		if(obj != null && obj instanceof ArrayList<?>) {
			tasks = (List<Task>) obj;
			if(tasks != null) {
				for(Task task : tasks) {
					revisionForCheckout.add(task.getOldRevision());
					revisionForCheckout.add(task.getCurrentRevision());
				}
				Collections.sort(revisionForCheckout);
				return tasks;
			}
		}
		String xml = FileUtil.loadTextFromFile(new File(location+"/Tasks.xml"));
		if(xml != null) {
			XStream xstream = new XStream();
			List<Task> tempList = (List<Task>) xstream.fromXML(xml);
			tasks = new ArrayList<Task>(tempList.size());
			tasks.addAll(tempList);
			Map<Integer,Revision> allRevisionsMap = new HashMap<Integer,Revision>();
			for (Task task : tasks) {
				task.setDoAndUndoDone(false);
				for (Revision revision : task.getRevisions()) {
					revision.setDoAndUndoDone(false);
					revision.setOldId(revision.getId()>1 ? revision.getId()-1 : 1);
				}
				Collections.sort(task.getRevisions());
				
				if(task.getRevisions().size() > 0) {
					//Definindo a oldRevision da oldTask e as oldTasks da oldRevision
					Set<Task> oldTasks = new HashSet<Task>(1);
					oldTasks.add(task);
					Revision oldRevision = null;
					if(allRevisionsMap.containsKey(task.getRevisions().get(0).getOldId())) {
						oldRevision = allRevisionsMap.get(task.getRevisions().get(0).getOldId());
						if(oldRevision.getOldTasks() == null)
							oldRevision.setOldTasks(new HashSet<Task>(oldTasks.size())); //TODO: Verificar se a lista já esta inicializada, se não está ok e pode remover este comentário, se sim não há necessidade deste teste, podendo excluir esta e a linha acima 
						oldRevision.getOldTasks().addAll(oldTasks);
					}
					else {
						oldRevision = new Revision(task.getRevisions().get(0).getOldId(),oldTasks,new HashSet<Task>(1));
						allRevisionsMap.put(oldRevision.getId(),oldRevision);
					}
					task.setOldRevision(oldRevision);
					
					//Definindo a currentRevision da currentTask e as currentTasks da currentRevision
					Set<Task> currentTasks = new HashSet<Task>(1);
					currentTasks.add(task);
					Revision currentRevision = task.getRevisions().get(task.getRevisions().size()-1);
					if(allRevisionsMap.containsKey(currentRevision.getId()))
						allRevisionsMap.get(currentRevision.getId()).getCurrentTasks().addAll(currentTasks);
					else {
						if(currentRevision.getCurrentTasks() == null)
							currentRevision.setCurrentTasks(new HashSet<Task>(currentTasks.size())); //TODO: Verificar se a lista já esta inicializada, se não está ok e pode remover este comentário, se sim não há necessidade deste teste, podendo excluir esta e a linha acima
						currentRevision.getCurrentTasks().addAll(currentTasks);
						allRevisionsMap.put(currentRevision.getId(),currentRevision);
					}
					task.setCurrentRevision(currentRevision);
				}
			}
			for(Revision revision : allRevisionsMap.values()) {
				if(revision.getOldTasks() == null)
					revision.setOldTasks(new HashSet<Task>(0));
				if(revision.getCurrentTasks() == null)
					revision.setCurrentTasks(new HashSet<Task>(0));
			}
			revisionForCheckout.addAll(allRevisionsMap.values());
			Collections.sort(revisionForCheckout);
			return tasks;
		}
		return null;
	}

	private Set<TestCoverage> getTestCoverageSet(String folder, String name) {
		Object obj = FileUtil.loadObjectFromFile(folder, name, "slc");
		if(obj != null && obj instanceof Set<?>)
			return (Set<TestCoverage>) FileUtil.loadObjectFromFile(folder, name, "slc");
		return new HashSet<TestCoverage>(0);
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