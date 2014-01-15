
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<link href="/shared/javascript/ext-2.0.a.1/resources/css/progress.css" rel="stylesheet" type="text/css" />
<link href="/sigaa/javascript/processamento.css" rel="stylesheet" type="text/css" />

<h2>Realizar Processamento de Matrícula</h2>

<f:view>
<h:messages showDetail="true" />

<h:form id="form">

	<table class="formulario" width="60%">
	<caption>Dados do Processamento</caption>
	<tbody>
		<tr>
			<th>Ano-Período: </th>
			<td><h:inputText value="#{ processamentoMatricula.ano }" onkeyup="return formatarInteiro(this);" size="4" maxlength="4"/>	- <h:inputText value="#{ processamentoMatricula.periodo }" size="1" maxlength="1" onkeyup="return formatarInteiro(this);"/></td>
		</tr>
	
		<tr>
			<th>Modo: </th>
			<td>
				<h:selectOneMenu value="#{ processamentoMatricula.modo }">
					<f:selectItems value="#{ preProcessamentoMatricula.modos }"/>
				</h:selectOneMenu>
			</td>
		</tr>

		<tr>
			<th>Rematricula: </th>
			<td>
				<h:selectOneRadio value="#{ processamentoMatricula.rematricula }">
					<f:selectItems value="#{ processamentoMatricula.simNao }"/>
				</h:selectOneRadio> 
			</td>
		</tr>

		<tr>
			<th>Tipo:</th>
			<td>
				<h:selectOneRadio value="#{ processamentoMatricula.tipo }" layout="pageDirection">
					<f:selectItem itemLabel="Processar Matrículas" itemValue="1"/>
					<f:selectItem itemLabel="Pós-processamento de blocos" itemValue="2"/>
					<f:selectItem itemLabel="Pós-processamento de co-requisitos" itemValue="3"/>
					<f:selectItem itemLabel="Ativar alunos cadastrados" itemValue="5"/>
				</h:selectOneRadio> 
			</td>
		</tr>

		<tr>
			<th>Senha: </th>
			<td><h:inputSecret value="#{ processamentoMatricula.senhaProcessamento }" size="15"/> </td>
		</tr>
		
	</tbody>
	<tfoot>
		<tr>
			<td colspan="2">
				<h:commandButton value="Processar" id="processar" action="#{ processamentoMatricula.processar }"/>
				<h:commandButton value="Cancelar" action="#{ processamentoMatricula.cancelar }" immediate="true"/>
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
