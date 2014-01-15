<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
<h:messages showDetail="true"></h:messages>
<h:outputText value="#{realtorioDiscente.create}"></h:outputText>
<h2>Relatório de Alunos matriculados em disciplinas com carga horária de estágio</h2>

<h:form id="form">
<table align="center" class="formulario" width="100%">
	<caption class="listagem">Dados do Relatório</caption>
	<tr>
		<th>Centro do Aluno: </th>
		<td><h:inputHidden id="idCentro" value="#{relatorioDiscente.centro.id}"></h:inputHidden>
				<h:inputText id="nomeCentro"
					value="#{relatorioDiscente.centro.nome}" size="80" /> <ajax:autocomplete
					source="form:nomeCentro" target="form:idCentro"
					baseUrl="/sigaa/ajaxUnidade" className="autocomplete"
					indicator="indicatorCentro" minimumCharacters="3" parameters="nivel=C"
					parser="new ResponseXmlToHtmlListParser()" /> <span id="indicatorCentro"
					style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span></td>

	</tr>
	<tr>
		<th>Curso do Aluno: </th>
		<td><h:inputHidden id="idCurso" value="#{relatorioDiscente.curso.id}"></h:inputHidden>
				<h:inputText id="nomeCurso"
					value="#{relatorioDiscente.discente.pessoa.nome}" size="80" /> <ajax:autocomplete
					source="form:nomeCurso" target="form:idCurso"
					baseUrl="/sigaa/ajaxCurso" className="autocomplete"
					indicator="indicatorCurso" minimumCharacters="3" parameters="nivel=G"
					parser="new ResponseXmlToHtmlListParser()" /> <span id="indicatorCurso"
					style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span></td>

	</tr>
	<tr>
		<td colspan="2" align="center">
						<h:commandButton id="gerarRelatorio" value="Gerar Relatório"
						action="#{relatorioDiscente.gerarRelatorioListaAlunoMatriculadosEstagio}"/> <h:commandButton
						value="Cancelar" action="#{relatorioDiscente.cancelar}" id="cancelar" /></td>
	</tr>

</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>