<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h:outputText value="#{aproveitamentoAutomatico.create}" />
	<h2 class="title"><ufrn:subSistema /> > Aproveitamento Automático de Estudos</h2>
	<h:form id="formAproveitamento">
	<table class="listagem">
	<caption>Selecione os Componentes Curriculares que Serão Aproveitados (${fn:length(aproveitamentoAutomatico.matriculas)})</caption>
		<thead>
				<td>
					<input type="checkbox" name="checkTodos" id="checkTodos" onclick="marcarTodos(this)" class="noborder"/>
				</td>
				<td>
					<b><label for="checkTodos">Selecionar Todos</label></b>
				</td>
				<td>Componente</td>
				<td>Situação</td>
				
		</thead>

		<tbody>
				
			<c:forEach items="${aproveitamentoAutomatico.matriculas}" var="mat" varStatus="status">

				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
					<td>
						<input type="checkbox" name="selecao" id="check_${mat.id}" value="${mat.id}" class="noborder"/>
					</td>
					<td style="font-size: x-small; font-style: italic;">${mat.tipoIntegralizacaoDescricao}</td>
					<td>${mat.componenteDescricao}</td>
					<td>${mat.situacaoMatricula.descricao}</td>
					
				</tr>

			</c:forEach>

		</tbody>

		<tfoot>
			<tr>
				<td colspan="5" align="center">
					<h:commandButton value="Gravar Aproveitamento" action="#{aproveitamentoAutomatico.gravarAproveitamento}" id="gravarAproceitamento"/>
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{aproveitamentoAutomatico.cancelar}" id="cacelarOperacao"/>
					<h:commandButton value="<< Voltar" action="#{aproveitamentoAutomatico.telaInfoDiscentes}" id="telaInformacaoDiscente"/>
				</td>
			</tr>
		</tfoot>
	</table>
	</h:form>

</f:view>
<script type="text/javascript">
<!--
function marcarTodos(chk) {
	var checks = document.getElementsByName('selecao');
	for (i=0;i<checks.length;i++) {
		checks[i].checked = chk.checked;
	}
}
//-->
</script>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>