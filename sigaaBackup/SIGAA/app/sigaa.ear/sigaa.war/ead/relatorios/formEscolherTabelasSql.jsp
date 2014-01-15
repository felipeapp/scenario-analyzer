<%@include file="/WEB-INF/jsp/include/cabecalho.jsp" %>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>
<f:view>
	<a4j:keepAlive beanName="extrairDadosEadSql" />
	<h2><ufrn:subSistema /> > Extrair Dados em SQl</h2>
	<h:outputText value="#{extrairDadosEadSql.create}" />
	<h:messages showDetail="true"/>
	<h:form id="busca">
		<h:inputHidden value="#{extrairDadosEadSql.metropoleDigital}" />
		<table class="formulario" width="40%">
			<caption>Tabelas a Exportar</caption>
			<thead>
				<tr>
					<th style="text-align:center;"><input type="checkbox" onclick="selecionaTodos(this);"></th>
					<th>Tabela</th>
				</tr>
			</thead>
			
			<tbody>
				<c:forEach items="#{extrairDadosEadSql.tabelas}" var="t">
					<tr>
						<td style="text-align:center;"><h:selectBooleanCheckbox styleClass="selecionar" value="#{t.selecionada}"/></td>
						<td>${t.nome}</td>
					</tr>
				</c:forEach>
			</tbody>
			
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Extrair Dados" action="#{extrairDadosEadSql.gerar}" /> 
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{extrairDadosEadSql.cancelar}" />
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>

</f:view>

<script type="text/javascript">
function selecionaTodos(chk){
	$A(document.getElementsByClassName('selecionar')).each(function(e) {
		e.checked = chk.checked;
	});
}
</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
