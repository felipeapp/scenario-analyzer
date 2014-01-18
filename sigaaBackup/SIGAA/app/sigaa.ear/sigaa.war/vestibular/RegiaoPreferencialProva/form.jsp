<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Cadastro de Região Preferencial de Prova</h2>

	<h:form id="form">
		<table class=formulario width="100%">
			<caption class="listagem">Dados da Região Preferencial de
			Prova</caption>
			<tr>
				<th class="required">Denominação:</th>
				<td><h:inputText id="nome"
					value="#{regiaoPreferencialProva.obj.denominacao}" size="60"
					disabled="#{regiaoPreferencialProva.readOnly}" maxlength="255" /></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton
						value="#{regiaoPreferencialProva.confirmButton}"
						action="#{regiaoPreferencialProva.cadastrar}" /> <h:commandButton
						value="Cancelar" action="#{regiaoPreferencialProva.cancelar}"
						onclick="#{confirm}"  /></td>
				</tr>
			</tfoot>
		</table>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>