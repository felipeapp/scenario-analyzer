<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>


<h2> <ufrn:subSistema /> > ${situacaoPropostaMBean.confirmButton} de Financiamento </h2>
<f:view>

<h:form id="form">
    <table class="formulario" width="60%">
		<caption>Financiamento de Curso de Lato Sensu</caption>
		<h:inputHidden value="#{financiamentoCursoLatoSensuMBean.obj.id}"/>
		<h:inputHidden value="#{financiamentoCursoLatoSensuMBean.obj.ativo}" />
		<tbody>
			<tr>
 				<th nowrap="nowrap" class="obrigatorio">Descrição:</th>
				<td><h:inputText value="#{financiamentoCursoLatoSensuMBean.obj.descricao}" id="descricao" size="50" /></td>
            </tr>
        </tbody>
		<tfoot>
   			<tr>
				<td colspan="2">
					<h:commandButton value="#{financiamentoCursoLatoSensuMBean.confirmButton}" action="#{financiamentoCursoLatoSensuMBean.cadastrar}" />
					<h:commandButton value="Cancelar" action="#{financiamentoCursoLatoSensuMBean.cancelar}" onclick="#{confirm}" />
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