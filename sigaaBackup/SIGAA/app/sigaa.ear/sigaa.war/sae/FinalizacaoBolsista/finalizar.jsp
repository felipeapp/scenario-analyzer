<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<a4j:keepAlive beanName="finalizarBolsaAuxilioMBean" />

	<h2 class="tituloPagina"> <ufrn:subSistema/> > Finalização dos Bolsistas </h2>
	<h:form id="form">
		<table class="formulario" width="100%" id="tableImportacao">
			<caption>Finalizar Bolsistas</caption>
			<thead>
				<tr>
					<th style="text-align:center;"><input type="checkbox" onclick="selecionaTodos(this);"/></th>
					<th style="text-align:center;">Ano Semestre</th>
					<td>Discente</td>
					<th style="text-align:center;">Bolsa</th>
					<th style="text-align:center;">Situação</th>
				</tr>
			
			</thead>
				<c:forEach items="#{ finalizarBolsaAuxilioMBean.bolsasAuxilio }" var="linha" varStatus="status">
					<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
						<td style="text-align:center;"><h:selectBooleanCheckbox styleClass="selecionar" value="#{linha.bolsaAuxilio.selecionado}"/></td>
						<td style="text-align:center;">${ linha.ano }.${ linha.periodo }</td>
						<td>${ linha.bolsaAuxilio.discente.matricula } - ${ linha.bolsaAuxilio.discente.pessoa.nome }</td>
						<td style="text-align:center;">${ linha.bolsaAuxilio.tipoBolsaAuxilio.denominacao }</td>
						<td style="text-align:center;">${ linha.bolsaAuxilio.situacaoBolsa.denominacao }</td>
					</tr>
				
				</c:forEach>
				
				<tfoot>
					<tr>
						<td colspan="6" align="center">
							<h:commandButton value="Finalizar" action="#{finalizarBolsaAuxilioMBean.finalizarBolsistas}" id="finalizar" />
							<h:commandButton value="Cancelar" action="#{finalizarBolsaAuxilioMBean.cancelar}" id="cancelarOperacao" onclick="#{confirm}" /> 
						</td>
					</tr>
				</tfoot>

		</table>
	</h:form>
	
	<br>
	<center><html:img page="/img/required.gif" style="vertical-align: top;" /> <span
		class="fontePequena"> Campos de preenchimento obrigatório. </span> <br>
	<br>
</center>

</f:view>

<script type="text/javascript">

function selecionaTodos(chk){
	$A(document.getElementsByClassName('selecionar')).each(function(e) {
		e.checked = chk.checked;
	});
}

</script>	

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>