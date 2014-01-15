<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
	<h:outputText value="#{relatoriosLato.create}"></h:outputText>
	<h2><ufrn:subSistema /> > Ranking de Alunos por Curso</h2>

	<h:form id="form">
		<table class="formulario" width="70%">
			<caption class="listagem">Dados do Relatório</caption>
			<tr>
				<th>Curso:</th>
						
				<td>
				 	<h:inputHidden id="idCurso" value="#{relatoriosLato.idCurso}"></h:inputHidden>
					<h:inputText id="nomeCurso"
							value="#{relatoriosLato.curso}" size="80"/> 
					<ajax:autocomplete source="form:nomeCurso" target="form:idCurso"
							baseUrl="/sigaa/ajaxCurso" className="autocomplete"
							indicator="indicatorCurso" minimumCharacters="3" parameters="nivel=L"
							parser="new ResponseXmlToHtmlListParser()" /> 
					<span id="indicatorCurso" style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span>
				</td>
		
			</tr>
			<tfoot>
				<tr>
					<td colspan="2" align="center">
						<h:commandButton id="gerarRelatorio" value="Gerar Relatório"
							action="#{relatoriosLato.gerarRankingCurso}"/> <h:commandButton
							value="Cancelar" action="#{relatoriosLato.cancelar}" id="cancelar" /></td>
				</tr>
			</tfoot>
		</table>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>