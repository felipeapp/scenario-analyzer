<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2>Tipo de Orientação do Discente</h2>


	<center>
			<h:messages/>
			<h:form>
			<div class="infoAltRem" style="text-align: left; width: 100%">
				<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>
	  			<h:commandLink value="Listar" action="#{tipoOrientacaoDiscente.listar}"/>
			</div>
			</h:form>
	</center>


	<table class=formulario width="100%">
		<h:form>
			<caption class="listagem">Cadastro de Tipo de Orientação do Discente</caption>
			<h:inputHidden value="#{tipoOrientacaoDiscente.confirmButton}" />
			<h:inputHidden value="#{tipoOrientacaoDiscente.obj.id}" />
			<tr>
				<th>Descrição:</th>
				<td><h:inputText size="60" maxlength="255"
					readonly="#{tipoOrientacaoDiscente.readOnly}"  value="#{tipoOrientacaoDiscente.obj.descricao}" /></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton
						value="#{tipoOrientacaoDiscente.confirmButton}"
						action="#{tipoOrientacaoDiscente.cadastrar}" /> <h:commandButton
						value="Cancelar" onclick="#{confirm}" action="#{tipoOrientacaoDiscente.cancelar}" /></td>
				</tr>
			</tfoot>
		</h:form>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>