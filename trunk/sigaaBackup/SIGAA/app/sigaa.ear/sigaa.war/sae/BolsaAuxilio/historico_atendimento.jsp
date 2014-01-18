<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<script type=text/javascript>  

	J = jQuery.noConflict();
	function habilitarDetalhes(idSolicitacao) {
		var linha = idSolicitacao;
		if ( J('#tbObservacoes_'+linha).css('display') == 'none' ) {
			if (/msie/.test( navigator.userAgent.toLowerCase() ))
				J('#tbObservacoes_'+linha).css('display', 'inline-block');
			else
				J('#tbObservacoes_'+linha).css('display', '');			
		} else {
			J('#tbObservacoes_'+linha).css('display', 'none');		
		}
	}

</script>


<f:view>
	<h2 class="tituloPagina"> <ufrn:subSistema/> > Histórico Atendimento </h2>

	<h:form>
	
	<table class="formulario" width="100%">
	
	<div class="infoAltRem">
		<h:graphicImage value="/img/cronograma/limpar.gif" style="overflow: visible;"/>: Ver Histórico
	</div>
	
	<caption> Histórico Atendimento Discente </caption>
		<tbody>
			<thead>
				<tr>
					<th style="text-align: center;">Ano/Período</th>
					<th style="text-align: center;">Matrícula</th>
					<th>Discente</th>
					<th>Tipo da bolsa SAE</th>
					<th>Situação</th>
					<th></th>
				</tr>
			</thead>

			<c:forEach items="#{ buscarBolsaAuxilioMBean.bolsas }" var="bolsaAuxPeriodo" varStatus="loop">
					<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						
							<td style="text-align: center;">${bolsaAuxPeriodo.ano}.${bolsaAuxPeriodo.periodo}</td>
							<td style="text-align: center;">${bolsaAuxPeriodo.bolsaAuxilio.discente.matricula}</td>
							<td>${bolsaAuxPeriodo.bolsaAuxilio.discente.pessoa.nome}</td>
							<td>${bolsaAuxPeriodo.bolsaAuxilio.tipoBolsaAuxilio.denominacao}</td>
							<td>${bolsaAuxPeriodo.bolsaAuxilio.situacaoBolsa.denominacao}</td>
							<td>
								<a href="javascript: void(0);" onclick="habilitarDetalhes(${bolsaAuxPeriodo.bolsaAuxilio.id});" title="Visualizar Detalhes da Solicitação">
									<img src="${ctx}/img/cronograma/limpar.gif" />
									<h:graphicImage value="/img/indicator.gif" id="indicator_${bolsaAuxPeriodo.bolsaAuxilio.id}" style="display: none;" /> 
								</a>
							</td>
					</tr>
					<tr>
						<td colspan="7">
							<table style="width: 100%; display: none;" id="tbObservacoes_${ bolsaAuxPeriodo.bolsaAuxilio.id }">
								<tr>
									<td class="subFormulario" colspan="7">Observações</td>
								</tr>
								<tr>
									<td style="text-align: justify;">${bolsaAuxPeriodo.bolsaAuxilio.observacoes}</td>
								</tr>
							</table>
						</td>
					</tr>	
			</c:forEach>	
		</tbody>
	</table>
	<br/><br/>
		<center>
			<h:commandButton action="#{ buscarBolsaAuxilioMBean.selecionaDiscente }" value="<< Voltar" />
		</center>	
	</h:form>
	<br/>		
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>