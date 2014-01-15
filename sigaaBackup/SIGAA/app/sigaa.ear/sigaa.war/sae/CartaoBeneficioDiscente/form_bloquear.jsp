<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<f:view>

	<h2><ufrn:subSistema /> >  Cartão de Benefício do Discente </h2>

	<h:form id="form">
	<table class="listagem" width="80%">
	<caption>Bloquear Cartão Benefício</caption>  
	<tbody>
		<tr>
			<th >Discente:</th>
			<td >
				<b><h:outputText id="discenteBloqueio" value="#{cartaoBeneficio.cartaoDiscente.discente.pessoa.nome}"  /></b>
			</td>
		</tr>
		<tr>
			<th class="obrigatorio">Motivo do Bloqueio:</th>
			<td >
				<h:inputTextarea id="motivo" style="width:80%" rows="2" value="#{cartaoBeneficio.cartaoDiscente.cartaoBolsaAlimentacao.motivoBloqueio}" />
			</td>
	    </tr>
	</tbody>
	<tfoot align="center">
		<tr>
			<td colspan="2">
				<h:commandButton value="#{cartaoBeneficio.confirmButton}" action="#{ cartaoBeneficio.bloquearCartao }" id="btBlock" />
				<h:commandButton value="<< Voltar" action="#{ cartaoBeneficio.voltaListaCartoes}" id="btVoltar" />
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