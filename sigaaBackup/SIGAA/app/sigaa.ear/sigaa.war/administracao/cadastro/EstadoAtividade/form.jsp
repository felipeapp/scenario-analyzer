<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2>Estado de Atividade</h2>

	<center>
			<h:messages/>
			<h:form>
			<div class="infoAltRem" style="text-align: left; width: 100%">
				<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>
	  			<h:commandLink value="Listar" action="#{estadoAtividade.listar}"/>
			</div>
			</h:form>
	</center>

<table class=formulario width="100%">
		<h:form>
			<caption class="listagem">Cadastro de Estado de Atividade</caption>
			<h:inputHidden value="#{estadoAtividade.confirmButton}" />
			<h:inputHidden value="#{estadoAtividade.obj.id}" />
			<tr>
				<th>Descrição:</th>
				<td><h:inputText readonly="#{estadoAtividade.readOnly}" value="#{estadoAtividade.obj.descricao}" /></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton
						value="#{estadoAtividade.confirmButton}"
						action="#{estadoAtividade.cadastrar}" /> <h:commandButton
						value="Cancelar" onclick="#{confirm}" action="#{estadoAtividade.cancelar}" /></td>
				</tr>
			</tfoot>
		</h:form>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>