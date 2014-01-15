<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/javascript/consulta_cep.js"></script>
<f:view>
	<h2><ufrn:subSistema /> > Remover Associação entre Local de Aplicação de Prova e
	Processo Seletivo</h2>

	<h:form id="form">
		<table class=formulario width="80%">
			<caption class="listagem">Remover a associação entre:</caption>
			<tr>
				<th>Processo Seletivo:</th>
				<td><h:outputText
					value="#{localAplicacaoProcessoSeletivo.obj.processoSeletivoVestibular.nome}" /></td>
			</tr>
			<tr>
				<th>Local de Aplicação de Prova:</th>
				<td><h:outputText
					value="#{localAplicacaoProcessoSeletivo.obj.localAplicacaoProva.nome}" /></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="2"><h:commandButton
						value="#{localAplicacaoProcessoSeletivo.confirmButton}"
						action="#{localAplicacaoProcessoSeletivo.remover}" /> <h:commandButton
						value="Cancelar" onclick="#{confirm}"
						action="#{localAplicacaoProcessoSeletivo.cancelar}"
						immediate="true" /></td>
				</tr>
			</tfoot>
		</table>

	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>