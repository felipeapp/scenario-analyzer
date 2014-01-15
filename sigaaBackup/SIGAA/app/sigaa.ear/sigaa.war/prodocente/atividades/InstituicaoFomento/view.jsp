<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2>Instituição Fomento</h2>
	<hr>
	<h:messages showDetail="true"></h:messages>
	<h:form id="form">
		<table class=formulario width="90%">
			<caption class="listagem">Cadastro de Instituição Fomento</caption>
			<h:inputHidden value="#{instituicaoFomento.confirmButton}" />
			<h:inputHidden value="#{instituicaoFomento.obj.id}" />
			<tr>
				<th class="required">Nome:</th>
				<td><h:inputText value="#{instituicaoFomento.obj.infonome}"
					size="60" maxlength="255" readonly="#{instituicaoFomento.readOnly}"
					id="nome" /></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton
						value="#{instituicaoFomento.confirmButton}"
						action="#{instituicaoFomento.cadastrar}" /> <h:commandButton
						value="Cancelar" action="#{instituicaoFomento.cancelar}" /></td>
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
