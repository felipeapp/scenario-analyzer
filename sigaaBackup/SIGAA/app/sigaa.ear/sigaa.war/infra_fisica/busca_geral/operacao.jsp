<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib uri="/tags/rich" prefix="rich"%>
<%@taglib uri="/tags/a4j" prefix="a4j"%>

<h:commandButton id="view" action="#{buscaEspacoFisico.view}" image="/img/view.gif" />
<h:commandButton id="alterar" action="#{espacoFisicoBean.iniciarAlterarEspacoFisico}" image="/img/alterar.gif" />
<a4j:commandButton id="remover" actionListener="#{espacoFisicoBean.removerEspacoFisico}" image="/img/delete.gif" reRender="outPutPanelResultado" />
