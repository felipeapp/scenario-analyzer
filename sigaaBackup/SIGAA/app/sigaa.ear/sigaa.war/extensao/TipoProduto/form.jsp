<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Tipo de Produto</h2>

	<table class=formulario>
		<h:form>
			<caption class="listagem">Cadastro de Tipo de Produto</caption>
			<h:inputHidden value="#{tipoProduto.confirmButton}" />
			<input type="hidden" value="${tipoProduto.obj.id}" id="id" name="id"/>
			<tr>
				<th class="required">Descrição:</th>
				<td><h:inputText value="#{tipoProduto.obj.descricao}" size="60"
					maxlength="255" readonly="#{tipoProduto.readOnly}" /></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2>
						<h:commandButton
						value="#{tipoProduto.confirmButton}"
						action="#{tipoProduto.cadastrar}" rendered="#{tipoProduto.confirmButton != 'Remover'}"/>

						<h:commandButton
						value="#{tipoProduto.confirmButton}"
						action="#{tipoProduto.inativar}" rendered="#{tipoProduto.confirmButton == 'Remover'}"/>

						 
						<h:commandButton
						value="Cancelar" action="#{tipoProduto.cancelar}" /></td>
				</tr>
			</tfoot>
		</h:form>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
