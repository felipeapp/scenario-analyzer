package br.ufrn.ppgsc.scenario.analyzer.miner.git;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.eclipse.jgit.api.BlameCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;


public class JGitPrintContent {
	
	public static void main(String[] args) throws Exception {
		File gitWorkDir = new File("/Users/leosilva/Documents/Estudo/Mestrado/projects/netty/");
		Git git = Git.open(gitWorkDir);
		Repository repo = git.getRepository();
		
		// commit inicial da Tag
		ObjectId commitID = repo.resolve("1512a4dccacfd9bf14f06b5c96b2ec46d22760c2");
		
		BlameCommand blame = new BlameCommand(repo);
		blame.setFilePath("README.md");
		blame.setStartCommit(commitID);
		BlameResult blameResult = blame.call();
		
		// read the number of lines from the commit to not look at changes in the working copy
        int lines = countFiles(repo, commitID, "README.md");
        for (int i = 0; i < lines; i++) {
            RevCommit commit = blameResult.getSourceCommit(i);
            System.out.println("Line: " + i + ": " + commit);
            System.out.println(commit.getTree());
        }

        System.out.println("Displayed commits responsible for " + lines + " lines of README.md");

        repo.close();
		
//		ObjectId lastCommitId = repo.resolve(Constants.HEAD);
//		RevWalk revWalk = new RevWalk(repo);
//		RevCommit commit = revWalk.parseCommit(lastCommitId);
//		RevTree tree = commit.getTree();
//		TreeWalk treeWalk = new TreeWalk(repo);
//		treeWalk.addTree(tree);
//		treeWalk.setRecursive(true);
//		treeWalk.setFilter(PathFilter.create("file1.txt"));
//		
//		if (!treeWalk.next()) {
//			System.out.println("Nothing found!");
//			return;
//		}
//		ObjectId objectId = treeWalk.getObjectId(0);
//		ObjectLoader loader = repo.open(objectId);
//		ByteArrayOutputStream out = new ByteArrayOutputStream();
//		loader.copyTo(out);
//		System.out.println("file1.txt:\n" + out.toString());		
	}
	
	
	private static int countFiles(Repository repository, ObjectId commitID, String name) throws IOException {
        RevWalk revWalk = new RevWalk(repository);
        RevCommit commit = revWalk.parseCommit(commitID);
        RevTree tree = commit.getTree();
        System.out.println("Having tree: " + tree);

        // now try to find a specific file
        TreeWalk treeWalk = new TreeWalk(repository);
        treeWalk.addTree(tree);
        treeWalk.setRecursive(true);
        treeWalk.setFilter(PathFilter.create(name));
        if (!treeWalk.next()) {
            throw new IllegalStateException("Did not find expected file 'README.md'");
        }

        ObjectId objectId = treeWalk.getObjectId(0);
        ObjectLoader loader = repository.open(objectId);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // and then one can the loader to read the file
        loader.copyTo(stream);

        return IOUtils.readLines(new ByteArrayInputStream(stream.toByteArray())).size();
    }
	
	
}
