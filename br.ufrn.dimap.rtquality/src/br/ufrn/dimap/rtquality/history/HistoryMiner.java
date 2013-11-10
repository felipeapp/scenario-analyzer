package br.ufrn.dimap.rtquality.history;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.tmatesoft.svn.core.SVNException;

import br.ufrn.dimap.ttracker.data.Revision;

public class HistoryMiner {
	
	public static void main(String[] args){
//		try {
//			SVNConfig sVNConfig = new SVNConfig("http://scenario-analyzer.googlecode.com/svn","/trunk/Calculadora","","");
//			Task task = new Task(1,new ArrayList<Revision>(),Task.REFACTOR);
//			Revision startRevision = new Revision(93, task);
//			Revision endRevision = new Revision(94, task);
//			task.getRevisions().add(startRevision);
//			task.getRevisions().add(endRevision);
//			for (String string : minerModifications(sVNConfig,startRevision,endRevision)) {
//				System.out.println(string);
//			}
//		} catch (NumberFormatException e) {
//			e.printStackTrace();
//		} catch (SVNException e) {
//			e.printStackTrace();
//		}
	}
	
	public static void checkoutProject(SVNConfig sVNConfig, Revision revision) throws SVNException{
		History history = new History(sVNConfig,ResourcesPlugin.getWorkspace());
		try {
			history.checkoutProject(revision);
		} catch (CoreException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static File checkoutFile(SVNConfig sVNConfig, String fileName, long revision) throws SVNException{
		History history = new History(sVNConfig,ResourcesPlugin.getWorkspace());
		return history.checkoutFile(fileName, revision);
	}
	
	public static Set<String> minerModifications(SVNConfig sVNConfig, Revision startRevision, Revision endRevision) throws NumberFormatException, SVNException {
		History history = new History(sVNConfig,ResourcesPlugin.getWorkspace());
		return history.getChangedMethodsSignatures(startRevision);
	}

}
