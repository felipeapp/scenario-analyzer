<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2>Ag�ncia Financiadora</h2>
	<hr>
	<h:form>
		<div style="text-align:right">
		<h:commandLink action="#{entidadeFinanciadora.listar}"
			value="Listar Ag�ncias Financiadoras Cadastradas"/>
		</div>
	</h:form>
	<h:messages showDetail="true"></h:messages>
	<h:form id="form">
	<table class=formulario width="90%">
			<caption class="listagem">Cadastro de Ag�ncia Financiadora</caption>
			<h:inputHidden value="#{entidadeFinanciadora.confirmButton}" />
			<h:inputHidden value="#{entidadeFinanciadora.obj.id}" />
			<tr>
				<th class="required">Nome:</th>
				<td><h:inputText value="#{entidadeFinanciadora.obj.nome}"
					size="80" maxlength="255"
					readonly="#{entidadeFinanciadora.readOnly}" id="nome" /></td>
			</tr>

			<tfoot>
				<tr>
					<td colspan=2><h:commandButton
						value="#{entidadeFinanciadora.confirmButton}"
						action="#{entidadeFinanciadora.cadastrar}" /> <h:commandButton
						value="Cancelar" action="#{entidadeFinanciadora.cancelar}" /></td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<br>
	<center>
	<h:graphicImage url="/img/required.gif" style="vertical-align: top;"/>
	<span class="fontePequena"> Campos de preenchimento obrigat�rio. </span>
	</center>
	<br>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
