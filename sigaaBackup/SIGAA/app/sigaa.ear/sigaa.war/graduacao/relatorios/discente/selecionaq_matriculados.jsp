<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
<h:messages showDetail="true"></h:messages>
<h:outputText value="#{realtorioDiscente.create}"></h:outputText>
<h2>Quantitativos de Alunos Ativos e Matriculados</h2>

<h:form id="form">
<table align="center" class="formulario" width="100%">
	<caption class="listagem">Dados do Relat�rio</caption>
	<tr>
		<th>Ano-Per�odo: </th>

		<td><h:selectOneMenu value="#{relatorioDiscente.ano}"
					id="ano">
					<f:selectItems value="#{relatorioDiscente.anos}" />
				</h:selectOneMenu>-<h:selectOneMenu value="#{relatorioDiscente.periodo}"
					id="periodo">
					<f:selectItems value="#{relatorioDiscente.periodos}" />
				</h:selectOneMenu></td>

	</tr>
	<tr>
		<th>Curso: </th>
		<td><h:inputHidden id="curso" value="#{relatorioDiscente.curso.id}"></h:inputHidden>
				<h:inputText id="nomeCurso"
					value="#{relatorioDiscente.curso.nome}" size="80" /> <ajax:autocomplete
					source="form:nomeCurso" target="form:curso"
					baseUrl="/sigaa/ajaxCurso" className="autocomplete"
					indicator="indicator" minimumCharacters="3" parameters="nivel=G"
					parser="new ResponseXmlToHtmlListParser()" /> <span id="indicator"
					style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span></td>

	</tr>
	<tr>
		<th>Centro (Unidade): </th>
		<td><h:inputHidden id="centro" value="#{relatorioDiscente.centro.id}"></h:inputHidden>
				<h:inputText id="nomeUnidade"
					value="#{relatorioDiscente.centro.nome}" size="80" /> <ajax:autocomplete
					source="form:nomeUnidade" target="form:centro"
					baseUrl="/sigaa/ajaxUnidade" className="autocomplete"
					indicator="indicator" minimumCharacters="3" parameters="nivel=C"
					parser="new ResponseXmlToHtmlListParser()" /> <span id="indicator"
					style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span></td>

	</tr>
	<tr>
		<td colspan="2" align="center">
						<h:commandButton id="gerarRelatorio" value="Gerar Relat�rio"
						action="#{relatorioDiscente.gerarRelatorioQuantitativoAlunoMatriculados}"/> <h:commandButton
						value="Cancelar" action="#{relatorioDiscente.cancelar}" id="cancelar" /></td>
	</tr>

</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>