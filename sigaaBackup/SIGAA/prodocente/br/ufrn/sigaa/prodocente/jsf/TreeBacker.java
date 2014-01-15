/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '08/11/2006'
 *
 */
package br.ufrn.sigaa.prodocente.jsf;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.validator.ValidatorException;

import org.apache.myfaces.custom.tree2.HtmlTree;
import org.apache.myfaces.custom.tree2.TreeModel;
import org.apache.myfaces.custom.tree2.TreeModelBase;
import org.apache.myfaces.custom.tree2.TreeNode;
import org.apache.myfaces.custom.tree2.TreeNodeBase;

public class TreeBacker
    implements Serializable
{

    public TreeBacker()
    {
    }

    public TreeNode getTreeData()
    {
        TreeNode treeData = new TreeNodeBase("foo-folder", "Inbox", false);
        TreeNodeBase personNode = new TreeNodeBase("person", "Frank Foo", false);
        personNode.getChildren().add(new TreeNodeBase("foo-folder", "Requires Foo", false));
        TreeNodeBase folderNode = new TreeNodeBase("foo-folder", "Requires Foo Reviewer", false);
        folderNode.getChildren().add(new TreeNodeBase("document", "X050001", true));
        folderNode.getChildren().add(new TreeNodeBase("document", "X050002", true));
        folderNode.getChildren().add(new TreeNodeBase("document", "X050003", true));
        personNode.getChildren().add(folderNode);
        personNode.getChildren().add(new TreeNodeBase("foo-folder", "Requires Foo Recommendation", false));
        folderNode = new TreeNodeBase("foo-folder", "Requires Foo Approval", false);
        folderNode.getChildren().add(new TreeNodeBase("document", "J050001", true));
        folderNode.getChildren().add(new TreeNodeBase("document", "J050002", true));
        folderNode.getChildren().add(new TreeNodeBase("document", "J050003", true));
        folderNode.getChildren().add(new TreeNodeBase("document", "E050011", true));
        folderNode.getChildren().add(new TreeNodeBase("document", "R050002", true));
        folderNode.getChildren().add(new TreeNodeBase("document", "C050003", true));
        personNode.getChildren().add(folderNode);
        folderNode = new TreeNodeBase("bar-folder", "Requires Bar Processing", false);
        folderNode.getChildren().add(new TreeNodeBase("document", "X050003", true));
        folderNode.getChildren().add(new TreeNodeBase("document", "X050011", true));
        folderNode.getChildren().add(new TreeNodeBase("document", "F050002", true));
        folderNode.getChildren().add(new TreeNodeBase("document", "G050003", true));
        personNode.getChildren().add(folderNode);
        folderNode = new TreeNodeBase("bar-folder", "Requires Bar Approval", false);
        folderNode.getChildren().add(new TreeNodeBase("document", "J050006", true));
        folderNode.getChildren().add(new TreeNodeBase("document", "J050007", true));
        personNode.getChildren().add(folderNode);
        treeData.getChildren().add(personNode);
        personNode = new TreeNodeBase("person", "Betty Bar", false);
        personNode.getChildren().add(new TreeNodeBase("foo-folder", "Requires Foo", false));
        folderNode = new TreeNodeBase("foo-folder", "Requires Foo Reviewer", false);
        folderNode.getChildren().add(new TreeNodeBase("document", "X012000", true));
        folderNode.getChildren().add(new TreeNodeBase("document", "X013000", true));
        folderNode.getChildren().add(new TreeNodeBase("document", "X014000", true));
        personNode.getChildren().add(folderNode);
        folderNode = new TreeNodeBase("foo-folder", "Requires Foo Recommendation", false);
        folderNode.getChildren().add(new TreeNodeBase("document", "J010026", true));
        folderNode.getChildren().add(new TreeNodeBase("document", "J020002", true));
        folderNode.getChildren().add(new TreeNodeBase("document", "J030103", true));
        folderNode.getChildren().add(new TreeNodeBase("document", "E030214", true));
        folderNode.getChildren().add(new TreeNodeBase("document", "R020444", true));
        folderNode.getChildren().add(new TreeNodeBase("document", "C010000", true));
        personNode.getChildren().add(folderNode);
        personNode.getChildren().add(new TreeNodeBase("foo-folder", "Requires Foo Approval", false));
        folderNode = new TreeNodeBase("bar-folder", "Requires Bar Processing", false);
        folderNode.getChildren().add(new TreeNodeBase("document", "T052003", true));
        folderNode.getChildren().add(new TreeNodeBase("document", "T020011", true));
        personNode.getChildren().add(folderNode);
        folderNode = new TreeNodeBase("bar-folder", "Requires Bar Approval", false);
        folderNode.getChildren().add(new TreeNodeBase("document", "J010002", true));
        folderNode.getChildren().add(new TreeNodeBase("document", "J030047", true));
        folderNode.getChildren().add(new TreeNodeBase("document", "F030112", true));
        personNode.getChildren().add(folderNode);
        treeData.getChildren().add(personNode);
        return treeData;
    }

    public TreeModel getExpandedTreeData()
    {
        return new TreeModelBase(getTreeData());
    }

    public void setTree(HtmlTree tree)
    {
        _tree = tree;
    }

    public HtmlTree getTree()
    {
        return _tree;
    }

    public String expandAll()
    {
        _tree.expandAll();
        return null;
    }

    public void setNodePath(String nodePath)
    {
        _nodePath = nodePath;
    }

    public String getNodePath()
    {
        return _nodePath;
    }

    public void checkPath(FacesContext context, UIComponent component, Object value)
    {
        FacesMessage message = null;
        String path[] = _tree.getPathInformation(value.toString());
        for(int i = 0; i < path.length; i++)
        {
            String nodeId = path[i];
            try
            {
                _tree.setNodeId(nodeId);
            }
            catch(Exception e)
            {
                throw new ValidatorException(message, e);
            }
            if(_tree.getNode().isLeaf())
            {
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid node path (cannot expand a leaf): " + nodeId, "Invalid node path (cannot expand a leaf): " + nodeId);
                throw new ValidatorException(message);
            }
        }

    }

    public void expandPath(ActionEvent event)
    {
        _tree.expandPath(_tree.getPathInformation(_nodePath));
    }

    private static final long serialVersionUID = 1L;
    private TreeModelBase _treeModel;
    private HtmlTree _tree;
    private String _nodePath;
}
