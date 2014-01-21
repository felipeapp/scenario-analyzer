package br.ufrn.dimap.rtquality.history;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IAccessRule;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNDiffClient;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc.SVNWCClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import br.ufrn.dimap.rtquality.plugin.Activator;
import br.ufrn.dimap.rtquality.plugin.SysOutProgressMonitor;
import br.ufrn.dimap.rtquality.util.ProjectUtil;
import br.ufrn.dimap.rtquality.util.TestUtil;
import br.ufrn.dimap.ttracker.data.CoveredMethod;
import br.ufrn.dimap.ttracker.data.Revision;
import br.ufrn.dimap.ttracker.data.Task;
import br.ufrn.dimap.ttracker.util.FileUtil;

import com.thoughtworks.xstream.XStream;

public class History {
	private SVNConfig sVNConfig;
	private SVNRepository repository;
	private IWorkspace iWorkspace;
	
	private static final String LIBRARY = "1";
	private static final String CONTAINER = "2";
	private static final String PROJECT = "3";
	
	public History(SVNConfig sVNConfig, IWorkspace iWorkspace) throws SVNException{
		this.sVNConfig = sVNConfig;
		this.iWorkspace = iWorkspace;
		setupLibrary();
		createRepository(sVNConfig.getSvnUrl());
	}

	private void createRepository(String svnUrl) throws SVNException {
		repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(svnUrl));
		ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(sVNConfig.getUserName(), sVNConfig.getPassword());
		repository.setAuthenticationManager(authManager);
	}
	
    public Set<String> getChangedMethodsSignatures(String projectPath, Integer oldRevision, Integer currentRevision) throws SVNException, IOException {
    	Collection<UpdatedMethod> updatedMethods = getChangedMethods(projectPath,oldRevision,currentRevision);
    	Set<String> changedMethodsSignatures = new HashSet<String>(); 
    	for (UpdatedMethod m : updatedMethods)
    		changedMethodsSignatures.add(m.getMethodLimit().getSignature());
    	return changedMethodsSignatures;
    }
	
    public Set<String> getChangedMethodsSignaturesFromProjects(Set<Project> projects, Integer oldRevision, Integer currentRevision) throws SVNException, IOException {
    	Set<String> changedMethodsSignatures = new HashSet<String>(); 
    	for(Project project : projects) {
    		for(String changedMethodSignature : getChangedMethodsSignatures(project.getPath(), oldRevision, currentRevision))
    			changedMethodsSignatures.add(project.getName()+"."+changedMethodSignature);
    	}
    	return changedMethodsSignatures;
    }
    
    public void checkouOrUpdateProjects(Integer revision) throws SVNException, CoreException, IOException {
    	SVNClientManager client = SVNClientManager.newInstance();
		client.setAuthenticationManager(repository.getAuthenticationManager());
		SVNUpdateClient sVNUpdateClient = client.getUpdateClient();
		SVNWCClient sVNWCClient = client.getWCClient();
		Map<Project,File> projectFile = new HashMap<Project,File>(sVNConfig.getProjects().size());
    	for(int i=1;i<=sVNConfig.getProjects().size();i++) { //TODO: estes projetos possuem informaÁıes que ser„o perdidas caso a execuÁ„o seja interrompida, salvar esta informaÁ„o
    		Project project = sVNConfig.getProjects().get(i);
    		project.setIProject(iWorkspace.getRoot().getProject(project.getName())); //O projeto n„o precisa existir no workspace para setar esta informaÁ„o.
			File file = new File(iWorkspace.getRoot().getLocation().toString()+project.getName());
			if(!file.exists()) {
				file.mkdir();
				sVNUpdateClient.doCheckout(SVNURL.parseURIEncoded(sVNConfig.getSvnUrl()+project.getPath()),
						file, SVNRevision.create(revision-1), SVNRevision.create(revision), SVNDepth.INFINITY, true);
				project.getProjectRevisionInformations().setRevision(revision);
			}
			else if(!project.getProjectRevisionInformations().getRevision().equals(revision)) {
				projectFile.put(project,file);
				sVNWCClient.doCleanup(file);
			}
    	}
    	if(!projectFile.isEmpty()){
    		sVNUpdateClient.doUpdate(projectFile.values().toArray(new File[0]), SVNRevision.create(revision), SVNDepth.INFINITY, true, true);
    		for(Project project : projectFile.keySet())
    			project.getProjectRevisionInformations().setRevision(revision);
    	}
		importConfigureRefreshBuild();
    }
    
	private void importConfigureRefreshBuild() throws CoreException, IOException {
//		Map<String,ProjectRevisionInformations> projectRevisionInformations = new HashMap<String,ProjectRevisionInformations>();
		for(int i=1;i<=sVNConfig.getProjects().size();i++) {
    		Project project = sVNConfig.getProjects().get(i);
			if(project.getProjectRevisionInformations().getRevisionBuilded().equals(project.getProjectRevisionInformations().getRevision()))
				continue;
			importProject(project);
			if(project.isAspectJNature())
				configureAspectJ(project.getIProject());
			else if(project.getName().equals("/LIBS")) { //TODO: CÛdigo especÌfico para o SIGAA
				changeLib(project, "aspectjrt*.jar", "aspectjrt.jar");
				changeLib(project, "aspectjweaver*.jar", "aspectjweaver.jar");
			}
			project.getIProject().refreshLocal(IResource.DEPTH_INFINITE, new SysOutProgressMonitor());
			buildingProject(project.getIProject());
			project.getProjectRevisionInformations().setRevisionBuilded(project.getProjectRevisionInformations().getRevision());
//			projectRevisionInformations.put(project.getPath(), project.getProjectRevisionInformations());
		}
//		FileUtil.saveObjectToFile(projectRevisionInformations, iWorkspace.getRoot().getLocation().toString()+"/config", "Projects", "obj");
	}

	private void changeLib(Project project, String oldLib, String newLib) { //TODO: CÛdigo especÌfico para o SIGAA
		File destino = new File(project.getIProject().getLocation().toString()+"/app/libs.jar");
		FileFilter fileFilter = new WildcardFileFilter(oldLib);
		File[] filesDestino = destino.listFiles(fileFilter);
		for (File fileDestino : filesDestino) {
			String fileNameDestino = fileDestino.getName();
			fileDestino.delete();
			copyFile(project.getIProject(), "/lib/"+newLib, "/app/libs.jar/"+fileNameDestino);
		}
	}

	private void buildingProject(IProject iProject) throws CoreException {
		SysOutProgressMonitor.out.println("Building eclipse project: " + iProject.getName());
		iProject.build(IncrementalProjectBuilder.FULL_BUILD, new SysOutProgressMonitor());
		IMarker[] problems = iProject.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
		for(IMarker iMarker : problems){
			if(iMarker.getAttribute(IMarker.SEVERITY, IMarker.SEVERITY_INFO) == IMarker.SEVERITY_ERROR)
				System.out.println(iMarker.getAttribute(IMarker.SEVERITY, IMarker.SEVERITY_INFO));
		}
		SysOutProgressMonitor.out.println("Eclipse project builded");
	}
    
    private void importProject(Project project) throws CoreException, IOException {
    	final IProject iProject = project.getIProject();
    	SysOutProgressMonitor.out.println("Importing eclipse project: " + iProject.getName());
    	InputStream inputStream = new FileInputStream(iWorkspace.getRoot().getLocation().toString()+"/"+iProject.getName()+"/.project");
    	final IProjectDescription iProjectDescription = iWorkspace.loadProjectDescription(inputStream);
    	
    	
    	iWorkspace.run(new IWorkspaceRunnable() {
    		@Override
    		public void run(IProgressMonitor monitor) throws CoreException {
    			// create project as java project
    			if ( !iProject.exists()) {
    				iProjectDescription.setLocation(null);
    				iProject.create(iProjectDescription, monitor);
    				iProject.open(IResource.NONE, monitor);
    			}
    		}
    	}, iWorkspace.getRoot(), IWorkspace.AVOID_UPDATE, new SysOutProgressMonitor());
    	
    	SysOutProgressMonitor.out.println("Eclipse project imported");
    }
    
    private void configureAspectJ(IProject iProject) throws CoreException, IOException {
//    	copyFile(iProject, "/lib/aspectjweaver.jar");
//    	copyFile(iProject, "/lib/ttracker.jar");
//    	iProject.refreshLocal(IResource.DEPTH_INFINITE, new SysOutProgressMonitor()); //TODO: Verificar se comentar essa linha deu certo
    	SysOutProgressMonitor.out.println("Adding AspectJ nature");
    	IProjectDescription iProjectDescription = iProject.getDescription();
		String ajNature = "org.eclipse.ajdt.ui.ajnature";
		if(!iProjectDescription.hasNature(ajNature)){
			List<String> natureIds = Arrays.asList(iProjectDescription.getNatureIds());
			List<String> natureIdsList = new ArrayList<String>(natureIds.size()+1);
			natureIdsList.addAll(natureIds);
			natureIdsList.add(ajNature);
			iProjectDescription.setNatureIds(natureIdsList.toArray(new String[natureIdsList.size()]));
		}
		iProject.setDescription(iProjectDescription, IProject.FORCE, new SysOutProgressMonitor());
		SysOutProgressMonitor.out.println("AspectJ nature added");
		
    	SysOutProgressMonitor.out.println("Configuring classpath to support AspectJ");
    	IJavaProject iJavaProject = JavaCore.create(iProject);
    	
    	IAccessRule iAccessRule[] = {};
//    	IClasspathAttribute restriction = JavaCore.newClasspathAttribute("org.eclipse.ajdt.inpath.restriction", "aspectjweaver.jar");
    	IClasspathAttribute ajdtInpath = JavaCore.newClasspathAttribute("org.eclipse.ajdt.inpath", "org.eclipse.ajdt.inpath");
//    	IClasspathAttribute restrictions[] = {restriction};
    	IClasspathAttribute ajdtInpaths[] = {ajdtInpath};
    	IProject tTrackerIProject = iProject.getWorkspace().getRoot().getProject("/br.ufrn.dimap.ttracker");
//		IClasspathEntry aspectjContainer = JavaCore.newContainerEntry(new Path("org.eclipse.ajdt.core.ASPECTJRT_CONTAINER")); //TODO: Quando o projeto n„o possuir AspectJ, nem a natureza nem o .jar descomentar esta linha
//		IClasspathEntry aspectjJar = JavaCore.newLibraryEntry(new Path(iWorkspace.getRoot().getLocation().toString()+"/LIBS/app/libs.jar/aspectjrt-1.2.1.jar"), null, null, iAccessRule, ajdtInpaths, false); //TODO: Quando o projeto j· tiver o AspectJ, seja a natureza ou o .jar, n„o adicionar nada
//		IClasspathEntry aspectjweaverJar = JavaCore.newLibraryEntry(new Path(iProject.getFullPath().toString()+"/lib/aspectjweaver.jar"), null, null);
		IClasspathEntry testtrackerJar = JavaCore.newProjectEntry(tTrackerIProject.getFullPath(), iAccessRule, false, ajdtInpaths, false);
//		IClasspathEntry requiredPlugins = JavaCore.newContainerEntry(new Path("org.eclipse.pde.core.requiredPlugins"),iAccessRule,restrictions,false);
		
		List<IClasspathEntry> rawClasspath = Arrays.asList(iJavaProject.getRawClasspath());
		List<IClasspathEntry> classpathEntriesList = new ArrayList<IClasspathEntry>(rawClasspath.size()+3); //TODO: precisa ser 3?
		classpathEntriesList.addAll(rawClasspath);
		
//		if(!classpathEntriesList.contains(aspectjContainer)){
//			SysOutProgressMonitor.out.println("Adding AspectJ container");
//			classpathEntriesList.add(aspectjContainer);
//		}
		
		//TODO: Verificar se este if-else s√£o necess√°rios ou se o AspectJ roda sem isso
//		addClasspathEntryWithAttribute(restriction, requiredPlugins, classpathEntriesList, CONTAINER);
		addClasspathEntryWithAttribute(ajdtInpath, testtrackerJar, classpathEntriesList, PROJECT);
		
//		if(!classpathEntriesList.contains(aspectjweaverJar)){
//			SysOutProgressMonitor.out.println("Adding AspectJ Weaver jar");
//			classpathEntriesList.add(aspectjweaverJar);
//		}
		iJavaProject.setRawClasspath(classpathEntriesList.toArray(new IClasspathEntry[classpathEntriesList.size()]), new SysOutProgressMonitor());
		iJavaProject.save(new SysOutProgressMonitor(), true);
		SysOutProgressMonitor.out.println("Classpath configured");
    }

	/**
	 * @throws IOException
	 */
	private String findResolveURL(String relativePath) throws IOException {
		URL url = FileLocator.find(Activator.getDefault().getBundle(), new Path(relativePath), null);
    	url = FileLocator.resolve(url);
    	return url.getPath();
	}

	/**
	 * @param attribute
	 * @param classpathEntry
	 * @param classpathEntriesList
	 * @param type
	 */
	private void addClasspathEntryWithAttribute(IClasspathAttribute attribute, IClasspathEntry classpathEntry,
			List<IClasspathEntry> classpathEntriesList, final String type) {
		IClasspathEntry classpathEntryTemp = null;
		for (IClasspathEntry iClasspathEntry : classpathEntriesList) {
			if(iClasspathEntry.getPath().equals(classpathEntry.getPath())){
				classpathEntryTemp = iClasspathEntry;
				break;
			}
		}
		if(classpathEntryTemp != null){
			classpathEntriesList.remove(classpathEntryTemp);
			List<IClasspathAttribute> iClasspathAttributes = Arrays.asList(classpathEntryTemp.getExtraAttributes());
			List<IClasspathAttribute> iClasspathAttributesList = new ArrayList<IClasspathAttribute>(iClasspathAttributes.size()+1);
			iClasspathAttributesList.addAll(iClasspathAttributes);
			if(!iClasspathAttributesList.contains(attribute)){
				SysOutProgressMonitor.out.println("Adding attribute to the classpath entry");
				iClasspathAttributesList.add(attribute);
				switch(type){
				case PROJECT:
					classpathEntry = JavaCore.newProjectEntry(classpathEntryTemp.getPath());
					break;
				case LIBRARY:
					classpathEntry = JavaCore.newLibraryEntry(classpathEntryTemp.getPath(), null, null, classpathEntryTemp.getAccessRules(), iClasspathAttributesList.toArray(new IClasspathAttribute[iClasspathAttributesList.size()]), classpathEntryTemp.isExported());
					break;
				case CONTAINER:
					classpathEntry = JavaCore.newContainerEntry(classpathEntryTemp.getPath(),classpathEntryTemp.getAccessRules(),iClasspathAttributesList.toArray(new IClasspathAttribute[iClasspathAttributesList.size()]),classpathEntryTemp.isExported());
					break;
				}
			}
			classpathEntriesList.add(classpathEntry);
		}
		else{
			SysOutProgressMonitor.out.println("Adding classpath entry with the attribute");
			classpathEntriesList.add(classpathEntry);
		}
	}

	/**
	 * @param iProject
	 * @param aspectjweaver
	 */
	private void copyFile(IProject iProject, String relativePath) {
		try{
    	    File f1 = new File(findResolveURL(relativePath));
    	    File f2 = new File(iProject.getLocation().toString()+"/lib");
    	    File f3 = new File(iProject.getLocation().toString()+relativePath);
    		if(!f2.exists())
    			f2.mkdir();
    		InputStream in = new FileInputStream(f1);
    		OutputStream out = new FileOutputStream(f3);
    		byte[] buf = new byte[1024];
    		int len;
    		while ((len = in.read(buf)) > 0){
    			out.write(buf, 0, len);
    		}
    		in.close();
    		out.close();
    	}
    	catch(FileNotFoundException ex){
    		ex.printStackTrace();
    	}
    	catch(IOException e){
    		e.printStackTrace();
    	}
	}
	
	private void copyFile(IProject iProject, String origem, String destino) {
		try{
    	    File f1 = new File(findResolveURL(origem));
    	    File f2 = new File(iProject.getLocation().toString()+destino);
    		InputStream in = new FileInputStream(f1);
    		OutputStream out = new FileOutputStream(f2);
    		byte[] buf = new byte[1024];
    		int len;
    		while ((len = in.read(buf)) > 0){
    			out.write(buf, 0, len);
    		}
    		in.close();
    		out.close();
    	}
    	catch(FileNotFoundException ex){
    		ex.printStackTrace();
    	}
    	catch(IOException e){
    		e.printStackTrace();
    	}
	}

    public File checkoutFile(String projectPath, String fileName, long revision) throws SVNException{
    	SVNClientManager client = SVNClientManager.newInstance();
		client.setAuthenticationManager(repository.getAuthenticationManager());
		SVNUpdateClient sVNUpdateClient = client.getUpdateClient();
		File file = new File(fileName);
		sVNUpdateClient.doCheckout(SVNURL.parseURIEncoded(sVNConfig.getSvnUrl()+projectPath+fileName),
				file, SVNRevision.create(0), SVNRevision.create(revision), SVNDepth.INFINITY, true);
    	return file;
    }

	private Collection<UpdatedMethod> getChangedMethods(String projectPath, Integer startRevision, Integer endRevision) throws SVNException, IOException {
		Collection<UpdatedMethod> updatedMethods = new ArrayList<UpdatedMethod>();
		SVNClientManager client = SVNClientManager.newInstance();
		client.setAuthenticationManager(repository.getAuthenticationManager());
		SVNDiffClient diffClient = client.getDiffClient();
		TestTrackerSVNDiffGenerator testTrackerSVNDiffGenerator = new TestTrackerSVNDiffGenerator();
		diffClient.setDiffGenerator(testTrackerSVNDiffGenerator);
		try {
			File xmlFile = new File("ProjectUpdates.xml");
			FileOutputStream fOS = new FileOutputStream(xmlFile);
			startProjectUpdatesXML(testTrackerSVNDiffGenerator, fOS);
			diffClient.doDiff(SVNURL.parseURIEncoded(sVNConfig.getSvnUrl()+projectPath),
					SVNRevision.create(startRevision),
					SVNURL.parseURIEncoded(sVNConfig.getSvnUrl()+projectPath),
			        SVNRevision.create(endRevision),
			        SVNDepth.INFINITY,
			        true,
			        fOS);
			finishProjectUpdatesXML(testTrackerSVNDiffGenerator, fOS);
			fOS.close();
			ProjectUpdates projectUpdates = (ProjectUpdates) getObjectFromXML(xmlFile);
			xmlFile.delete();
			updatedMethods = projectUpdates.getUpdatedMethods(startRevision, endRevision);
			return updatedMethods;
		} finally {
			try {
				File tempFolder = new File("temp");
				if(tempFolder != null && tempFolder.isDirectory())
					FileUtils.deleteDirectory(tempFolder);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param testTrackerSVNDiffGenerator
	 * @param fOS
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 */
	private void startProjectUpdatesXML(TestTrackerSVNDiffGenerator testTrackerSVNDiffGenerator,
			FileOutputStream fOS) throws IOException,
			UnsupportedEncodingException {
		fOS.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>".getBytes(testTrackerSVNDiffGenerator.getEncoding()));
		fOS.write(testTrackerSVNDiffGenerator.getEOL());
		fOS.write("<br.ufrn.dimap.rtquality.history.ProjectUpdates>".getBytes(testTrackerSVNDiffGenerator.getEncoding()));
		fOS.write(testTrackerSVNDiffGenerator.getEOL());
		fOS.write("  <classUpdates>".getBytes(testTrackerSVNDiffGenerator.getEncoding()));
		fOS.write(testTrackerSVNDiffGenerator.getEOL());
	}

	/**
	 * @param testTrackerSVNDiffGenerator
	 * @param fOS
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 */
	private void finishProjectUpdatesXML(TestTrackerSVNDiffGenerator testTrackerSVNDiffGenerator,
			FileOutputStream fOS) throws IOException,
			UnsupportedEncodingException {
		fOS.write("  </classUpdates>".getBytes(testTrackerSVNDiffGenerator.getEncoding()));
		fOS.write(testTrackerSVNDiffGenerator.getEOL());
		fOS.write("</br.ufrn.dimap.rtquality.history.ProjectUpdates>".getBytes(testTrackerSVNDiffGenerator.getEncoding()));
	}
	
	private Object getObjectFromXML(File xmlFile){
		xmlFile.getAbsolutePath();
		String xml = FileUtil.loadTextFromFile(xmlFile);
		XStream xstream = new XStream();
		Object object = (Object) xstream.fromXML(xml);
		return object;
	}

    /*
     * Initializes the library to work with a repository via 
     * different protocols.
     */
    private static void setupLibrary() {
        DAVRepositoryFactory.setup(); //For using over http:// and https://
        SVNRepositoryFactoryImpl.setup(); //For using over svn:// and svn+xxx://
        FSRepositoryFactory.setup();//For using over file:///
    }

	public SVNConfig getsVNConfig() {
		return sVNConfig;
	}

	public void setsVNConfig(SVNConfig sVNConfig) {
		this.sVNConfig = sVNConfig;
	}

	public IWorkspace getiWorkspace() {
		return iWorkspace;
	}

	public void setiWorkspace(IWorkspace iWorkspace) {
		this.iWorkspace = iWorkspace;
	}

}