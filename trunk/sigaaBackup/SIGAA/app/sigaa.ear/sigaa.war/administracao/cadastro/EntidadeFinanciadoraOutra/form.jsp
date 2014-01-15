<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2>Entidade Financiadora Outra</h2>

	<center>
			<h:messages/>
			<h:form>
			<div class="infoAltRem" style="text-align: left; width: 100%">
				<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>
	  			<h:commandLink value="Listar" action="#{entidadeFinanciadoraOutra.listar}"/>
			</div>
			</h:form>
	</center>

<table class=formulario width="100%">
		<h:form>
			<caption class="listagem">Cadastro de Entidade Financiadora Outra</caption>
			<h:inputHidden value="#{entidadeFinanciadoraOutra.confirmButton}" />
			<h:inputHidden value="#{entidadeFinanciadoraOutra.obj.id}" />
			<tr>
				<th>Descrição:</th>
				<td><h:inputText value="#{entidadeFinanciadoraOutra.obj.descricao}" readonly="#{entidadeFinanciadoraOutra.readOnly}" /></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton
						value="#{entidadeFinanciadoraOutra.confirmButton}"
						action="#{entidadeFinanciadoraOutra.cadastrar}" /> <h:commandButton
						value="Cancelar" onclick="#{confirm}" action="#{entidadeFinanciadoraOutra.cancelar}" /></td>
				</tr>
			</tfoot>
		</h:form>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>