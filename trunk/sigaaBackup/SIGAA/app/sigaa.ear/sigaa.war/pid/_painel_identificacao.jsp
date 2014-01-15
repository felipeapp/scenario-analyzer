<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib prefix="t" uri="http://myfaces.apache.org/tomahawk" %>

<%@taglib uri="/tags/rich" prefix="rich"%>
<%@taglib uri="/tags/a4j" prefix="a4j"%>

<rich:panel header="PLANO INDIVIDUAL DOCENTE" styleClass="painelIdentificacaoDocente">
	<table>
		<tr>
			<th> PER�ODO DE REFER�NCIA: </th>
			<td> <h:outputText value="#{_pidBean.ano}"/>.<h:outputText value="#{_pidBean.periodo}"/> </td>
		</tr>
		<tr>
			<th> DOCENTE: </th>
			<td> <h:outputText value="#{_pidBean.obj.servidor.nome}" /> </td>
		</tr>
		<tr>
			<th> MATR�CULA: </th>
			<td> <h:outputText value="#{_pidBean.obj.servidor.siape}" /> </td>
		</tr>
		<tr>
			<th> LOTA��O: </th> 
			<td> <h:outputText value="#{_pidBean.obj.servidor.unidade.nome}" /> </td>
		</tr>
	</table>
</rich:panel>