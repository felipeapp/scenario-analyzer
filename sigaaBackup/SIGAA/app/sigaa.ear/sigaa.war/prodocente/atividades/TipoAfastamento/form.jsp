<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Tipo de Afastamento</h2>

	<h:form>
	 <div class="infoAltRem" style="width: 100%">
	  <h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
	  <h:commandLink action="#{afastamentoProdocente.listar}" value="Listar Tipos de Afastamento Cadastrados"/>
	 </div>
    </h:form>

	<h:messages showDetail="true" />
	<h:form id="form">
	<table class=formulario width="50%">
			<caption class="listagem">Cadastro de Afastamento</caption>
			<h:inputHidden value="#{afastamentoProdocente.confirmButton}" />
			<h:inputHidden value="#{afastamentoProdocente.obj.id}" />
			<h:inputHidden value="#{afastamentoProdocente.obj.ativo}" />
			<tr>
				<th class="required">Descrição:</th>
				<td><h:inputText value="#{afastamentoProdocente.obj.descricao}" size="60"
					maxlength="255" readonly="#{afastamentoProdocente.readOnly}" id="descricao" /></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton
						value="#{afastamentoProdocente.confirmButton}"
						action="#{afastamentoProdocente.cadastrar}" /> <h:commandButton
						value="Cancelar" action="#{afastamentoProdocente.cancelar}" onclick="#{confirm}"/></td>
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