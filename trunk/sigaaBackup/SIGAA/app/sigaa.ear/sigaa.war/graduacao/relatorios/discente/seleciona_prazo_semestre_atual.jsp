<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h:messages showDetail="true"></h:messages>
<h:outputText value="#{realtorioDocente.create}"></h:outputText>
<h2>Lista de alunos ativos com tempo máximo de conclusão no ano semestre atual</h2>

<h:form id="form">
<table align="center" class="formulario" width="100%">
	<caption class="listagem">Dados do Relatório</caption>
	<tr>
		<th>Centro: </th>
		<td><h:selectOneMenu style="width: 380px;" value="#{relatorioDiscente.centro.id}"
					id="departamento">
					<f:selectItem itemValue="0" itemLabel="TODOS" />
					<f:selectItems value="#{unidade.allCentroCombo}" />
				</h:selectOneMenu></td>
	</tr>
	<tr>
		<th>Curso: </th>
		<td><h:inputHidden id="idCurso" value="#{relatorioDiscente.curso.id}"></h:inputHidden>
				<h:inputText id="nomeCurso"
					value="#{relatorioDiscente.curso.nome}" size="80" /> <ajax:autocomplete
					source="form:nomeCurso" target="form:idCurso"
					baseUrl="/sigaa/ajaxCurso" className="autocomplete"
					indicator="indicatorCurso" minimumCharacters="3" parameters="nivel=G"
					parser="new ResponseXmlToHtmlListParser()" /> <span id="indicatorCurso"
					style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span></td>
	</tr>
	<tr>
		<td colspan="2" align="center">
						<h:commandButton id="gerarRelatorio" value="Gerar Relatório"
						action="#{relatorioDiscente.gerarRelatorioListaAlunoPrazoSemestreAtual}"/> <h:commandButton
						value="Cancelar" action="#{relatorioDiscente.cancelar}" id="cancelar" /></td>
	</tr>

</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>