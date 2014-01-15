<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>


<h2> <ufrn:subSistema /> > ${situacaoPropostaMBean.confirmButton} de Situação das Propostas </h2>
<f:view>

<h:form id="form">
    <table class="formulario" width="60%">
		<caption>Situação de Proposta de Curso de Lato Sensu</caption>
		<h:inputHidden value="#{situacaoPropostaMBean.obj.id}"/>
		<h:inputHidden value="#{situacaoPropostaMBean.obj.ativo}" />
		<tbody>
			<tr>
 				<th nowrap="nowrap" class="obrigatorio">Descrição:</th>
				<td><h:inputText value="#{situacaoPropostaMBean.obj.descricao}" id="descricao" size="50" /></td>
            </tr>
			<tr>
 				<th nowrap="nowrap">Situação Válida?</th>
				<td><h:selectBooleanCheckbox value="#{situacaoPropostaMBean.obj.valida}" id="valida" /></td>
            </tr>
        </tbody>
		<tfoot>
   			<tr>
				<td colspan="2">
					<h:commandButton value="#{situacaoPropostaMBean.confirmButton}" action="#{situacaoPropostaMBean.cadastrar}" />
					<h:commandButton value="Cancelar" action="#{situacaoPropostaMBean.cancelar}" onclick="#{confirm}" />
                </td>
			</tr>
		</tfoot>
	</table>
</h:form>

	<br />
	<center>
		<h:graphicImage url="/img/required.gif" style="vertical-align: top;" />
		<span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	</center>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>