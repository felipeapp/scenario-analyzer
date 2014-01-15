<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
	<h:outputText value="#{relatorioDiscente.create}"></h:outputText>
	<h2><ufrn:subSistema /> &gt; Relatório de Alunos com trancamento em um determinado Componente Curricular</h2>
	
	<h:form id="form">
		<table align="center" class="formulario" width="40%">
			<caption class="listagem">Dados do Relatório</caption>
			<tr>
				<th>Ano-Período: </th>
				<td>
					<h:selectOneMenu value="#{relatorioDiscente.ano}" id="ano">
						<f:selectItems value="#{relatorioDiscente.anos}" />
					</h:selectOneMenu>
					-
					<h:selectOneMenu value="#{relatorioDiscente.periodo}" id="periodo">
						<f:selectItems value="#{relatorioDiscente.periodos}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<ufrn:subSistema teste="not portalCoordenadorStricto">
				<tr>
					<th>Componente Curricular: </th>
					<td>
						<h:inputHidden id="idDisciplina" value="#{relatorioDiscente.disciplina.id}"></h:inputHidden>
						<h:inputText id="nomeDisciplina" value="#{relatorioDiscente.disciplina.detalhes.nome}" size="80" />
						<ajax:autocomplete
								source="form:nomeDisciplina" target="form:idDisciplina"
								baseUrl="/sigaa/ajaxDisciplina" className="autocomplete"
								indicator="indicatorDisciplina" minimumCharacters="3" parameters="nivel=G"
								parser="new ResponseXmlToHtmlListParser()" />
						<span id="indicatorDisciplina" style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span>
					</td>
				</tr>
			</ufrn:subSistema>
			<tfoot>
				<tr>
					<td colspan="2" align="center">
						<h:commandButton id="gerarRelatorio" value="Gerar Relatório" action="#{relatorioDiscente.gerarRelatorioListaTrancamentosComponente}"/>
						<h:commandButton value="Cancelar" action="#{relatorioDiscente.cancelar}" id="cancelar" onclick="#{confirm}"/>
					</td>
				</tr>
			</tfoot>
		
		</table>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>