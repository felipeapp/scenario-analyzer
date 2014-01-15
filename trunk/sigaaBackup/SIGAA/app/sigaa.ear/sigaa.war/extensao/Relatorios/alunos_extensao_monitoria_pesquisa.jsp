<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h:messages showDetail="true"></h:messages>
<h:outputText value="#{relatorioAlunosExtensaoMonitoriaPesquisaBean.create}"></h:outputText>
<h2><ufrn:subSistema /> &gt; Relatório de alunos em atividades de extensão, monitoria e pesquisa </h2>

<h:form id="form">
<table align="center" class="formulario" width="100%">
	<caption class="listagem">Dados do Relatório</caption>
	<c:if test="${not relatorioAlunosExtensaoMonitoriaPesquisaBean.coordenador}">
		<tr>
			<th class="obrigatorio">Curso:</th>
			<td>
				<h:selectOneMenu value="#{relatorioAlunosExtensaoMonitoriaPesquisaBean.curso.id}" id="curso">
					<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
					<f:selectItems value="#{curso.allCursoGraduacaoCombo}" />
				</h:selectOneMenu>
			</td>
		</tr>
	</c:if>
	<c:if test="${relatorioAlunosExtensaoMonitoriaPesquisaBean.coordenador}">
		<tr>
			<th>Curso:</th>
			<td>${ relatorioAlunosExtensaoMonitoriaPesquisaBean.curso.descricao }</td>
			<h:inputHidden id="cursoHidden" value="#{relatorioAlunosExtensaoMonitoriaPesquisaBean.curso.id}" />
		</tr>
	</c:if>
	<tr>
		<th class="obrigatorio">Ano:</th>
		<td>
			<h:inputText id="ano" value="#{relatorioAlunosExtensaoMonitoriaPesquisaBean.ano}" size="4" maxlength="4" onkeyup="formatarInteiro(this);"/>
		</td>
	</tr>
	<tr>
		<td colspan="2" align="center">
			<h:commandButton id="gerarRelatorio" value="Gerar Relatório"
			action="#{relatorioAlunosExtensaoMonitoriaPesquisaBean.gerarRelatorio}"/> <h:commandButton
			value="Cancelar" action="#{relatorioAlunosExtensaoMonitoriaPesquisaBean.cancelar}" id="cancelar" onclick="#{confirm}"/>
		</td>
	</tr>

</table>
<div class="obrigatorio">Campos de preenchimento obrigatório</div>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>