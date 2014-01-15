<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
		<h2>Entidade Financiadora de Estágio</h2>



	<center>
			<h:messages/>
			<h:form>
			<div class="infoAltRem" style="text-align: left; width: 100%">
				<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>
	  			<h:commandLink value="Listar" action="#{entidadeFinanciadoraEstagio.listar}"/>
			</div>
			</h:form>
	</center>

<table class=formulario width="100%">
		<h:form>
			<caption class="listagem">Cadastro de Entidade Financiadora de Estágio</caption>
			<h:inputHidden value="#{entidadeFinanciadoraEstagio.confirmButton}" />
			<h:inputHidden value="#{entidadeFinanciadoraEstagio.obj.id}" />
			<tr>
				<th>Descrição:</th>
				<td><h:inputText size="60" maxlength="255" readonly="#{entidadeFinanciadoraEstagio.readOnly}"
					value="#{entidadeFinanciadoraEstagio.obj.descricao}" /></td>
			</tr>
			<tr>
				<th>Governamental:</th>
				<td><h:selectBooleanCheckbox disabled="#{entidadeFinanciadoraEstagio.readOnly}"
					value="#{entidadeFinanciadoraEstagio.obj.governamental}" /></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton
						value="#{entidadeFinanciadoraEstagio.confirmButton}"
						action="#{entidadeFinanciadoraEstagio.cadastrar}" /> <h:commandButton
						value="Cancelar" onclick="#{confirm}" action="#{entidadeFinanciadoraEstagio.cancelar}" /></td>
				</tr>
			</tfoot>
		</h:form>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>