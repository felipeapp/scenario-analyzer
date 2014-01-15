<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<f:view>

	<h2><ufrn:subSistema /> >  Cartão de Benefício do Discente </h2>

	<h:form id="form">
	<table class="listagem" width="80%">
	<caption>Associar Discente ao Cartão Benefício</caption>  
	<tbody>
		<tr>
			<th class="obrigatorio">Código do Cartão:</th>
			<td>
				<h:inputText id="codCard" value="#{cartaoBeneficio.cartaoAlimentacao.codigo}" size="5" maxlength="5"  onkeyup="return formatarInteiro(this);" />
			</td>
	    </tr>
		<tr>
			<th class="obrigatorio">Código de Barras:</th>
			<td>
				<h:inputSecret id="codBarra"  maxlength="8" value="#{cartaoBeneficio.cartaoAlimentacao.codBarras}" />
			</td>
	    </tr>
	</tbody>
	<tfoot align="center">
		<tr>
			<td colspan="2">
				<h:commandButton value="#{cartaoBeneficio.confirmButton}" action="#{ cartaoBeneficio.associarDiscente }" id="btAsso" />
				<h:commandButton value="<< Voltar" action="#{ cartaoBeneficio.getListPage}" id="btVoltar" />
				<h:commandButton value="Cancelar" action="#{ cartaoBeneficio.cancelar }" onclick="#{confirm}" id="btCancelar" />
	    	</td>
	    </tr>
	</tfoot>
	</table>

	
	

	
	</h:form>
	
	<br />
	<center><h:graphicImage url="/img/required.gif"
		style="vertical-align: top;" /> <span class="fontePequena">
	Campos de preenchimento obrigatório. </span></center>
	<br />
</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>	