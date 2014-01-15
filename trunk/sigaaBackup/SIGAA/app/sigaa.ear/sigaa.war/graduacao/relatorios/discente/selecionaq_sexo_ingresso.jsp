<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
<h:messages showDetail="true"></h:messages>
<h:outputText value="#{realtorioDiscente.create}"></h:outputText>
<h2><ufrn:subSistema /> > Relatório de Quantitativos de Alunos por Sexo e Ingresso</h2>

<h:form id="form">
<table align="center" class="formulario" width="100%">
	<caption class="listagem">Dados do Relatório</caption>
	<tr>
		<th>Ano-Período: </th>

		<td><h:selectOneMenu value="#{relatorioDiscente.ano}"
					id="ano">
					<f:selectItems value="#{relatorioDiscente.anos}" />
				</h:selectOneMenu>-<h:selectOneMenu value="#{relatorioDiscente.periodo}"
					id="periodo">
					<f:selectItems value="#{relatorioDiscente.periodos}" />
				</h:selectOneMenu></td>

	</tr>
	<tr>
		<th>Pesquisar em:</th>
		<td>
			<h:selectOneRadio id="opcao" value="#{relatorioDiscente.todosCentros}" onchange="submit();">
				<f:selectItem itemLabel="Todos os Centros" itemValue="#{true}"  />
				<f:selectItem itemLabel="Centro (Unidade)"  itemValue="#{false}" />
			</h:selectOneRadio>
		</td>
	</tr>
	<h:panelGroup rendered="#{!relatorioDiscente.todosCentros }">
		<tr>
			<th>Centro (Unidade): </th>
			<td><h:inputHidden id="id" value="#{relatorioDiscente.centro.id}"></h:inputHidden>
					<h:inputText id="nome"
						value="#{relatorioDiscente.centro.nome}" size="80" /> <ajax:autocomplete
						source="form:nome" target="form:id"
						baseUrl="/sigaa/ajaxUnidade" className="autocomplete"
						indicator="indicator" minimumCharacters="3" parameters="nivel=C"
						parser="new ResponseXmlToHtmlListParser()" /> <span id="indicator"
						style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span></td>
	
		</tr>
	</h:panelGroup>
	<tr>
		<td colspan="2" align="center">
						<h:commandButton id="gerarRelatorio" value="Gerar Relatório"
						action="#{relatorioDiscente.gerarRelatorioQuantitativoAlunoSexoIngresso}"/> <h:commandButton
						value="Cancelar" action="#{relatorioDiscente.cancelar}" id="cancelar" /></td>
	</tr>

</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>