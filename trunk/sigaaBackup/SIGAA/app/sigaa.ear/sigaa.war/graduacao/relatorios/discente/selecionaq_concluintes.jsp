<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
<h:messages showDetail="true"></h:messages>
<h:outputText value="#{realtorioDiscente.create}"></h:outputText>
<h2>Quantitativos de Alunos Concluintes</h2>

<h:form id="form">
<table align="center" class="formulario" width="100%">
	<caption class="listagem">Dados do Relatório</caption>
	<tr>
		<th>Ano-Período Conclusão: </th>

		<td><h:selectOneMenu value="#{relatorioDiscente.ano}"
					id="ano">
					<f:selectItems value="#{relatorioDiscente.anos}" />
				</h:selectOneMenu>-<h:selectOneMenu value="#{relatorioDiscente.periodo}"
					id="periodo">
					<f:selectItems value="#{relatorioDiscente.periodos}" />
				</h:selectOneMenu> <h:selectBooleanCheckbox id="todos" value="#{relatorioDiscente.todos}" onclick="desabilitarAnoSemestre()" /> Todos</td>

	</tr>
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
		<th>Centro (Unidade): </th>
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
		<td colspan="2" align="center">
						<h:commandButton id="gerarRelatorio" value="Gerar Relatório"
						action="#{relatorioDiscente.gerarRelatorioQuantitativoAlunosConcluintes}"/> <h:commandButton
						value="Cancelar" action="#{relatorioDiscente.cancelar}" id="cancelar" /></td>
	</tr>

</table>
</h:form>
<script language="javascript">
	function desabilitarAnoSemestre(){
		if($('form:todos').checked){
			$('form:ano').disabled = true;
			$('form:periodo').disabled= true;
		}else {
			$('form:ano').disabled  = false;
			$('form:periodo').disabled  = false;
		}
	}

</script>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>