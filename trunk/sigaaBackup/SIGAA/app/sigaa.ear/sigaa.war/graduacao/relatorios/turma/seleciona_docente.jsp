<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
<h:messages showDetail="true"></h:messages>
<h:outputText value="#{realtorioDiscente.create}"></h:outputText>
<h2><ufrn:subSistema /> &gt; Relatório de Turmas por Quantidade de docentes</h2>

<h:form id="form">
<table align="center" class="formulario" width="60%">
	<caption>Dados do Relatório</caption>
	<tr>
		<th>Unidade:</th>
		<td><h:inputHidden id="idCentro" value="#{relatorioTurma.centro.id}"></h:inputHidden>
				<h:inputText id="nomeCentro"
					value="#{relatorioTurma.centro.nome}" size="80" /> <ajax:autocomplete
					source="form:nomeCentro" target="form:idCentro"
					baseUrl="/sigaa/ajaxUnidade" className="autocomplete"
					indicator="indicatorCentro" minimumCharacters="3" parameters="nivel=C"
					parser="new ResponseXmlToHtmlListParser()" /> <span id="indicatorCentro"
					style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span></td>
	</tr>
	<tr>
		<th class="required">Ano-Período</th>
		<td>
			<h:inputText size="4" maxlength="4" value="#{relatorioTurma.ano}" id="ano" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }" styleClass="numerico"/> - <h:inputText size="2" maxlength="1" id="periodo" value="#{relatorioTurma.periodo}" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }" styleClass="numerico"/> 
		</td>
	</tr>
	<tfoot>
	<tr>
		<td colspan="2" align="center">
			<h:commandButton id="gerarRelatorio" value="Gerar Relatório" action="#{relatorioTurma.gerarRelatorioListaTurmasDocentes}"/>
			<h:commandButton value="Cancelar" action="#{relatorioTurma.cancelar}" id="cancelar" immediate="true" onclick="#{confirm}" />
		</td>
	</tr>
	</tfoot>
</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%></h2>
