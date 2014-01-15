<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib prefix="t" uri="http://myfaces.apache.org/tomahawk" %>

<%@taglib uri="/tags/rich" prefix="rich"%>
<%@taglib uri="/tags/a4j" prefix="a4j"%>

<table class="formulario" width="100%">
	<caption class="formulario">Dados Gerais</caption>

	<c:if test="${!questionarioBean.obj.ativo}">
	<tr>
		<td colspan="2" class="questionarioInativo"> Questionário inativo! </td>
	</tr>
	</c:if>

	<tr>
		<th><b>Tipo de Questionário:</b></th>
		<td>${questionarioBean.obj.tipo}</td>
	</tr>
	<tr>
		<th width="20%"><b>Título:</b></th>
		<td>
			<h:outputText id="txtTitulo" value="#{questionarioBean.obj.titulo}"/>
		</td>
	</tr>
	
	<c:if test="${questionarioBean.obj.necessarioPeriodoPublicacao}">
	<tr>
		<th>Disponível de:</th>
		<td> 
			<ufrn:format type="data" valor="${questionarioBean.obj.inicio}" /> 
			a <ufrn:format type="data" valor="${questionarioBean.obj.fim}" />
		</td>
	</tr>
	</c:if>
</table>
