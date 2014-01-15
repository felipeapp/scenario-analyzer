<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<c:if test="${empty param.ajaxRequest }">
	<h2 class="title"><ufrn:subSistema/> &gt; Cadastrar Componente Curricular &gt; Confirmação</h2>
</c:if>
<h:outputText value="#{componenteCurricular.carregarComponente}" />

<h:form id="form">

<br>
<div align="center">
	<h:commandButton image="/img/printer_ok.png"
	title="Gerar Comprovante da Solicitação" action="#{componenteCurricular.imprimirComprovante}" id="gerarCmprovante"/> <br>
	<h:commandLink action="#{componenteCurricular.imprimirComprovante}" value="Imprimir Comprovante da Solicitação" id="imprimirComprovant"/>
</div>
<br>

<table class="listagem" style="width: 80%">
	<caption class="formulario">Confirmação de Cadastro de Componente Curricular</caption>
	<c:choose>
	<c:when test="${acesso.chefeDepartamento or acesso.secretarioDepartamento or acesso.coordenadorCursoGrad or acesso.secretarioGraduacao}">
	<tr>
		<td>A solicitação de cadastro do componente com número <b>${componenteCurricular.idComponente}</b> 
		foi realizada com sucesso. <br>
		Clique em imprimir comprovante caso queira imprimir o mesmo. </td>
	</tr>
	</c:when>
	<c:otherwise>
	<tr>
		<td>O Componente curricular ${componenteCurricular.obj.descricaoResumida}  
		foi cadastrado com sucesso.<br> 
		Clique em imprimir comprovante caso queira imprimir o mesmo. </td>
	</tr>
	</c:otherwise>
	</c:choose>
	

</table>
</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>