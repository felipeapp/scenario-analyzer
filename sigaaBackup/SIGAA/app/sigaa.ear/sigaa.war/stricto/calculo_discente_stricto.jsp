<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
<h2><ufrn:subSistema /> > Cálculos de Integralização de Discentes</h2>
<br>
	<h:outputText value="#{calculosDiscente.create}"></h:outputText>
	<h:messages showDetail="true"></h:messages>
	<h:form id="formulario">
		<table class="formulario">
			<caption>Selecione um Discente</caption>
			<tr>
				<td>Discente:</td>
				<td>
				<h:inputHidden id="idDiscente" value="#{calculosDiscente.discenteStricto.id}"></h:inputHidden>
				<h:inputText id="nomeDiscente" value="#{calculosDiscente.discenteStricto.pessoa.nome}" size="80" />
					<ajax:autocomplete
					source="formulario:nomeDiscente" target="formulario:idDiscente"
					baseUrl="/sigaa/ajaxDiscente" className="autocomplete"
					indicator="indicator" minimumCharacters="3" parameters="nivel=S"
					parser="new ResponseXmlToHtmlListParser()" />
					<span id="indicator" style="display:none; ">
					<img src="/sigaa/img/indicator.gif" /> </span>
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="2">
					<h:commandButton value="Calcular" action="#{calculosDiscente.calcularStricto}"/>
					<h:commandButton value="Cancelar" action="#{calculosDiscente.cancelar}" immediate="true" onclick="#{confirm}" />
					</td>
				</tr>
			</tfoot>
		</table>
	    
	</h:form>

</f:view>
<script type="text/javascript">
<!--
$('formulario:nomeDiscente').focus();
//-->
</script>
<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>