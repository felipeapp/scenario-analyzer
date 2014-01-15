<%@taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<html>
<link rel="stylesheet" type="text/css" href="/img/prodocente/basic.css" />

<body>


<f:view>
<h:form>
<h2>Produção Intectual<br>
Departamento: XXXXX</h2>
<hr>
<br>

<t:tree2 id="clientTree" value="#{treeBacker.treeData}" var="node" varNodeToggler="t" showRootNode="false">
        <f:facet name="person">
            <h:panelGroup>
                <f:facet name="expand">
                    <t:graphicImage value="/img/prodocente/person.png" rendered="#{t.nodeExpanded}" border="0"/>
                </f:facet>
                <f:facet name="collapse">
                    <t:graphicImage value="/img/prodocente/person.png" rendered="#{!t.nodeExpanded}" border="0"/>
                </f:facet>
                <h:outputText value="#{node.description}" styleClass="nodeFolder"/>
            </h:panelGroup>
        </f:facet>
        <f:facet name="foo-folder">
            <h:panelGroup>
                <f:facet name="expand">
                    <t:graphicImage value="/img/prodocente/yellow-folder-open.png" rendered="#{t.nodeExpanded}" border="0"/>
                </f:facet>
                <f:facet name="collapse">
                    <t:graphicImage value="/img/prodocente/yellow-folder-closed.png" rendered="#{!t.nodeExpanded}" border="0"/>
                </f:facet>
                <h:outputText value="#{node.description}" styleClass="nodeFolder"/>
                <h:outputText value=" (#{node.childCount})" styleClass="childCount" rendered="#{!empty node.children}"/>
            </h:panelGroup>
        </f:facet>
        <f:facet name="bar-folder">
            <h:panelGroup>
                <f:facet name="expand">
                    <t:graphicImage value="/img/prodocente/blue-folder-open.gif" rendered="#{t.nodeExpanded}" border="0"/>
                </f:facet>
                <f:facet name="collapse">
                    <t:graphicImage value="/img/prodocente/blue-folder-closed.png" rendered="#{!t.nodeExpanded}" border="0"/>
                </f:facet>
                <h:outputText value="#{node.description}" styleClass="nodeFolder"/>
                <h:outputText value=" (#{node.childCount})" styleClass="childCount" rendered="#{!empty node.children}"/>
            </h:panelGroup>
        </f:facet>
        <f:facet name="document">
            <h:panelGroup>
                <h:commandLink immediate="true" styleClass="#{t.nodeSelected ? 'documentSelected':'document'}" actionListener="#{t.setNodeSelected}">
                    <t:graphicImage value="/img/prodocente/document.png" border="0"/>
                    <h:outputText value="#{node.description}"/>
                    <f:param name="docNum" value="#{node.identifier}"/>
                </h:commandLink>
            </h:panelGroup>
        </f:facet>
    </t:tree2>

    <t:inputCalendar value="#{artigo.obj.dataPublicacao}" renderAsPopup="true"/>

    <t:inputHtml value="#{artigo.obj.titulo}"/>

</h:form>

</f:view>

</body>
</html>