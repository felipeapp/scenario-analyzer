
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<link href="/shared/javascript/ext-2.0.a.1/resources/css/progress.css" rel="stylesheet" type="text/css" />
<link href="/sigaa/javascript/processamento.css" rel="stylesheet" type="text/css" />

<h2>Realizar Processamento de Matrícula</h2>

<f:view>

<h:form id="form">

	<table class="formulario" width="60%">
	<caption>Dados do Processamento</caption>
		<tr>
			<th>Ano-Período: </th>
			<td><h:inputText value="#{ resultadoProcessamentoMatriculaBean.ano }" size="4" maxlength="4" onkeyup="return formatarInteiro(this);"/>	- <h:inputText value="#{ resultadoProcessamentoMatriculaBean.periodo }" size="1" maxlength="1" onkeyup="return formatarInteiro(this);"/></td>
		</tr>
		<tr>
			<th>Tipo: </th>
			<td><h:selectOneRadio value="#{ resultadoProcessamentoMatriculaBean.tipo }">
					<f:selectItem itemLabel="Regular" itemValue="true"/>
					<f:selectItem itemLabel="Férias" itemValue="false"/>
				</h:selectOneRadio>
		</td>
		</tr>
		<tr>
			<th>Rematrícula: </th>
			<td><h:selectOneRadio value="#{ resultadoProcessamentoMatriculaBean.rematricula }">
					<f:selectItems value="#{resultadoProcessamentoMatriculaBean.simNao}"/>
				</h:selectOneRadio>
		</td>
		</tr>
	<tfoot>
		<tr>
			<td colspan="2">
				<h:commandButton value="Processar" id="processar" action="#{ resultadoProcessamentoMatriculaBean.gerar }"/>
				<h:commandButton value="Cancelar" action="#{ resultadoProcessamentoMatriculaBean.cancelar }"/>
			</td>
		</tr>
	</tfoot>
	</table>
	
	<div id="progresso" style="display: none">
		<h1>Turmas Processadas </h1>
	
		<div id="pgb"></div>
		<span class="status"><strong>Status: </strong> <span id="pgbtext"></span></span>
		<input type="hidden" id="tipoProgresso" value="processamento"/>		
	</div>
	
</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

<c:if test="${ processamentoMatricula.processamentoIniciado }">
	<script type="text/javascript" src="/shared/loadScript?src=javascript/ext-2.0.a.1/ext-base.js"> </script>
	<script type="text/javascript" src="/shared/loadScript?src=javascript/ext-2.0.a.1/ext-all.js"> </script>
	<script type="text/javascript" src="/sigaa/javascript/processamento.js"></script>
</c:if>