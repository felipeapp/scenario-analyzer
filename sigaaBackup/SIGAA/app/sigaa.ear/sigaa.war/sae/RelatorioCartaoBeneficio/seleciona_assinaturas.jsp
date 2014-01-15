<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>


<f:view>

	<h2><ufrn:subSistema /> > Relatório para Assinaturas de Discentes com Bolsa Alimentação </h2>

	<h:form id="form">
	<table class="formulario" width="50%">
	<caption>Buscar Discentes</caption>  
	<tbody>
		<tr>
			<td>&nbsp;</td>
			<td class="obrigatorio"><span class="requerid"></span>Período:</td>
			<td><h:inputText value="#{relatorioCartaoBeneficio.ano}" size="4"
				maxlength="4" onkeyup="return formatarInteiro(this);" />
			&nbsp;-&nbsp; <h:inputText value="#{relatorioCartaoBeneficio.periodo}"
				size="1" maxlength="1" onkeyup="return formatarInteiro(this);" />
			</td>
		</tr>

			</tbody>
	<tfoot>
		<tr>
			<td colspan="3">
				<h:commandButton value="Buscar" action="#{ relatorioCartaoBeneficio.buscarDiscentesAssinaturas }" id="btBuscar" />
				<h:commandButton value="Cancelar" action="#{ relatorioCartaoBeneficio.cancelar }" onclick="#{confirm}" id="btCancelar" />
	    	</td>
	    </tr>
	</tfoot>
	</table>

	<br/>

	
	</h:form>
</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>	