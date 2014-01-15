<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2><ufrn:subSistema /> > Tipo de Grupo de Prestação de Serviço</h2>

<table class=formulario>
<h:form> <caption class="listagem">Cadastro de Tipo de Grupo de Prestação de Serviço</caption>	<h:inputHidden value="#{tipoGrupoPrestServico.confirmButton}"/> <h:inputHidden value="#{tipoGrupoPrestServico.obj.id}"/>
<tr>
	<th class="required"> Descrição:</th>
	<td><h:inputText value="#{tipoGrupoPrestServico.obj.descricao}" size="60" maxlength="255" readonly="#{tipoGrupoPrestServico.readOnly}"/></td>
</tr>
<tr>
	<th> Ajuda:</th>
	<td> <h:inputText value="#{tipoGrupoPrestServico.obj.ajuda}" size="60" maxlength="255" readonly="#{tipoGrupoPrestServico.readOnly}"/></td>
</tr>
<tfoot><tr><td colspan=2>
<h:commandButton value="#{tipoGrupoPrestServico.confirmButton}" action="#{tipoGrupoPrestServico.cadastrar}" /> <h:commandButton value="Cancelar" action="#{tipoGrupoPrestServico.cancelar}" /></td>
</tr></tfoot>
</h:form>
</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
