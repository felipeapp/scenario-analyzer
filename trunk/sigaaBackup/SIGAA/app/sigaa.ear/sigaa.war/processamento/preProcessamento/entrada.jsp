
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

<h2>Pré-Processamento de Matrícula</h2>

<h:messages showDetail="true" />

<h:form id="form">

	<table class="formulario" width="60%">
	<caption>Dados do Pré-Processamento</caption>
	<tbody>
		<tr>
			<th>Ano-Período: </th>
			<td><h:inputText value="#{ preProcessamentoMatricula.ano }" onkeyup="return formatarInteiro(this);" maxlength="4" size="4"/>	- <h:inputText value="#{ preProcessamentoMatricula.periodo }" size="1" maxlength="1" onkeyup="return formatarInteiro(this);"/></td>
		</tr>
	
		<tr>
			<th>Modo: </th>
			<td>
				<h:selectOneMenu value="#{ preProcessamentoMatricula.modo }">
					<f:selectItems value="#{ preProcessamentoMatricula.modos }"/>
				</h:selectOneMenu>
			</td>
		</tr>
		
		<tr>
			<th>Rematricula: </th>
			<td>
				<h:selectOneRadio value="#{ preProcessamentoMatricula.rematricula }">
					<f:selectItems value="#{ preProcessamentoMatricula.simNao }"/>
				</h:selectOneRadio> 
			</td>
		</tr>

		<tr>
			<th>Tipo:</th>
			<td>
				<h:selectOneRadio value="#{ preProcessamentoMatricula.tipo }" layout="pageDirection">
					<f:selectItem itemLabel="Gerar matrículas em espera" itemValue="1"/>
					<f:selectItem itemLabel="Calcular possíveis formandos" itemValue="2"/>
					<f:selectItem itemLabel="Cancelar vínculos ativos anteriores" itemValue="3"/>
					<f:selectItem itemLabel="Relatório de alunos a serem cancelados" itemValue="4"/>
				</h:selectOneRadio> 
			</td>
		</tr>

		<tr>
			<th>Núm. Threads: </th>
			<td><h:inputText value="#{ preProcessamentoMatricula.numThreads }" onkeyup="return formatarInteiro(this);" size="2" maxlength="2"/></td>
		</tr>

		<tr>
			<th>Senha: </th>
			<td><h:inputSecret value="#{ preProcessamentoMatricula.senhaProcessamento }" size="15"/> </td>
		</tr>

	</tbody>
	<tfoot>
		<tr>
			<td colspan="2">
				<h:commandButton value="Processar" id="processar" action="#{ preProcessamentoMatricula.processar }"/>
				<h:commandButton value="Cancelar" action="#{ preProcessamentoMatricula.cancelar }" immediate="true"/>
			</td>
		</tr>
	</tfoot>
	</table>
	
	<a4j:outputPanel>
	
	<rich:progressBar value="#{ preProcessamentoMatricula.atual }" label="#{ preProcessamentoMatricula.atual } %" 
		minValue="0" maxValue="#{ preProcessamentoMatricula.total }" interval="1000" enabled="#{ preProcessamentoMatricula.enabled }"/>
	
	</a4j:outputPanel>
	
</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
