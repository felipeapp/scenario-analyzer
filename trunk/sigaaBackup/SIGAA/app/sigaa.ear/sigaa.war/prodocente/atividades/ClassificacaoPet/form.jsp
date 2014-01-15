<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Classificação do Pet</h2>
	<div align="center">
		<h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
		<ufrn:link action="/prodocente/atividades/ClassificacaoPet/lista.jsf" value="Lista da Classificação do Pet"/>
	</div>
	<h:messages showDetail="true"></h:messages>
	<h:form id="form">
	<table class=formulario width="50%">
			<caption class="listagem">Cadastro de Tipo de Classificação do Pet</caption>
			<h:inputHidden value="#{classificacaoPet.confirmButton}" />
			<h:inputHidden value="#{classificacaoPet.obj.id}" />
			<h:inputHidden value="#{classificacaoPet.obj.ativo}" />
			<tr>
				<th class="required">Descrição:</th>
				<td><h:inputText value="#{classificacaoPet.obj.descricao}"
					size="60" maxlength="255" readonly="#{classificacaoPet.readOnly}"
					id="descricao" /></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton
						value="#{classificacaoPet.confirmButton}" id="confirmar"
						action="#{classificacaoPet.cadastrar}" /> <h:commandButton id="cancelar"
						value="Cancelar" action="#{classificacaoPet.cancelar}" onclick="#{confirm}" immediate="true" /></td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<br>
	<center>
	<h:graphicImage url="/img/required.gif" style="vertical-align: top;"/>
	<span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	</center>
	<br>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
