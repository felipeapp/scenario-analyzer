<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Tipo de Atividade Complementar</h2>


	<center>
			<h:messages/>
			<h:form>
			<div class="infoAltRem" style="text-align: center; width: 100%">
				<h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
	  			<h:commandLink value="Listar" action="#{tipoAtividadeComplementar.listar}"/>
			</div>
			</h:form>
	</center>

	<table class=formulario width="50%">
		<h:form>
			<caption class="listagem">Cadastro de Tipo de Atividade Complementar</caption>
			<h:inputHidden value="#{tipoAtividadeComplementar.confirmButton}" />
			<h:inputHidden value="#{tipoAtividadeComplementar.obj.id}" />
			<tr>
				<th class="required">Descrição:</th>
				<td><h:inputText  size="60" maxlength="255"
					readonly="#{tipoAtividadeComplementar.readOnly}" value="#{tipoAtividadeComplementar.obj.descricao}" /></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton
						value="#{tipoAtividadeComplementar.confirmButton}"
						action="#{tipoAtividadeComplementar.cadastrar}" /> <h:commandButton
						value="Cancelar" onclick="#{confirm}" action="#{tipoAtividadeComplementar.cancelar}" /></td>
				</tr>
			</tfoot>
		</h:form>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>