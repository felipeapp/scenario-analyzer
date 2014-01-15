<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
<h:messages showDetail="true"></h:messages>
<h:outputText value="#{realtorioDocente.create}"></h:outputText>
<h2>Lista de Alunos Concluintes</h2>

<h:form id="form">
<table align="center" class="formulario" width="100%">
	<caption class="listagem">Dados do Relatório</caption>
	<tr>
		<th>Curso: </th>
		<td><h:inputHidden id="id" value="#{relatorioDiscente.curso.id}"></h:inputHidden>
				<h:inputText id="nome"
					value="#{relatorioDiscente.curso.nome}" size="80" /> <ajax:autocomplete
					source="form:nome" target="form:id"
					baseUrl="/sigaa/ajaxCurso" className="autocomplete"
					indicator="indicator" minimumCharacters="3" parameters="nivel=G"
					parser="new ResponseXmlToHtmlListParser()" /> <span id="indicator"
					style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span></td>

	</tr>
	<tr>
		<td colspan="2" align="center">
						<h:commandButton id="gerarRelatorio" value="Gerar Relatório"
						action="#{relatorioDiscente.gerarRelatorioListaAlunosConcluintes}"/> <h:commandButton
						value="Cancelar" action="#{relatorioDiscente.cancelar}" id="cancelar" /></td>
	</tr>

</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>