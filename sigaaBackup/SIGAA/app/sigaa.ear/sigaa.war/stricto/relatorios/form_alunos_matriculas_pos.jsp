<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h:outputText value="#{relatorioAlunosMatriculasPosBean.create}" />

<h2> <ufrn:subSistema /> &gt; Relatório de Alunos e Matrículas </h2>

<h:form id="form">
<table class="formulario" style="width: 95%">
<caption> Informe os critérios para a emissão do relatório </caption>

	<h:inputHidden id="nomeRelatorio" value="#{relatorioAlunosMatriculasPosBean.nomeRelatorio}" />
	
	<tr>
		<th>Programa: </th>
		<td>
		<c:if test="${acesso.ppg}">
			<h:selectOneMenu id="programa" value="#{relatorioAlunosMatriculasPosBean.unidade.id}">
				<f:selectItem itemLabel=" Todos " itemValue="-1"/>
				<f:selectItems value="#{unidade.allProgramaPosCombo}"/>
			</h:selectOneMenu>
		</c:if>
		<c:if test="${!acesso.ppg}">
			${relatorioAlunosMatriculasPosBean.programaStricto}
		</c:if>
		</td>
	</tr>
	
	<tr>
		<th class="required">Tempo em Meses: </th>
		<td>
			&nbsp;&nbsp;&nbsp; Mestrado: <h:inputText id="mestrado" value="#{relatorioAlunosMatriculasPosBean.mestrado}" size="2" maxlength="2" converter="#{ intConverter }" onkeyup="return formatarInteiro(this);"/> &nbsp;&nbsp;&nbsp;
			Doutorado: <h:inputText id="doutorado" value="#{relatorioAlunosMatriculasPosBean.doutorado}" size="2" maxlength="2" converter="#{ intConverter }" onkeyup="return formatarInteiro(this);"/>
		</td>
	</tr>
	
	<tr>
		<th class="required">Ano: </th>
		<td>
			&nbsp;&nbsp;&nbsp; Ano:<h:inputText id="ano" value="#{relatorioAlunosMatriculasPosBean.ano}" size="4" maxlength="4" converter="#{ intConverter }" onkeyup="return formatarInteiro(this);"/>
		</td>
	</tr>
	
	<tr>
	<th class="required"> Formato do Relatório: </th>
	<td>
		<h:selectOneRadio value="#{relatorioAlunosMatriculasPosBean.formato}" id="formato">
			<f:selectItem itemValue="pdf" itemLabel="PDF" />
			<f:selectItem itemValue="xls" itemLabel="XLS (Excel)" />
			<f:selectItem itemValue="html" itemLabel="HTML" />
		</h:selectOneRadio>
	</td>
	</tr>

	<tfoot>
	<tr>
		<td colspan="2">
			<h:commandButton id="btEmitir" action="#{relatorioAlunosMatriculasPosBean.gerarRelatorio}" value="Emitir Relatório"/>
			<h:commandButton id="btCancelar" action="#{relatorioAlunosMatriculasPosBean.cancelar}" value="Cancelar" onclick="#{confirm}"/>
		</td>
	</tr>
	</tfoot>
</h:form>
</table>
<br />
<div class="obrigatorio">Campos de preenchimento obrigatório</div>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>