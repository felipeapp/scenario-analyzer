<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h:messages/>
	<h2 class="tituloPagina"> <ufrn:subSistema/> > Buscar Bolsa Auxílio por Aluno</h2>

	<div class="infoAltRem">
	    <h:graphicImage value="/img/flag_green.png" style="overflow: visible;"/>: Solicitar Renovação da Bolsa
	    <h:graphicImage value="/img/flag_red.png" style="overflow: visible;"/>: Não Solicitar Renovação da Bolsa
	</div>

	<h:form>
	
		<table class="listagem" width="100">
		
			<caption> Bolsas Passíveis de Renovação </caption>
			<thead>
				<tr>
					<th style="text-align: center;">Ano Período</th>
					<th>Tipo Bolsa</th>
					<th>Situação da Bolsa</th>
					<th></th>
					<th></th>
			</thead>
	
			<tbody>
				<c:forEach items="#{renovacaoBolsaAuxilioMBean.bolsas}" var="_bolsa" varStatus="loop">
					<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td style="text-align: center;">${_bolsa.ano}.${_bolsa.periodo}</td>
						<td>${_bolsa.tipoBolsaAuxilio.denominacao}</td>
						<td>${_bolsa.situacaoBolsa.denominacao}</td>
						<td width="5%">
							<h:commandLink action="#{ renovacaoBolsaAuxilioMBean.selecionaTipoBolsa }">
								<f:param name="id" value="#{ _bolsa.id }"/>
								<h:graphicImage value="/img/flag_green.png" title="Solicitar Renovação da Bolsa" />
							</h:commandLink> 		
						</td>
						<td width="5%">
							<h:commandLink action="#{ renovacaoBolsaAuxilioMBean.naoSolicitarRenovacaoBolsa }">
								<f:param name="id" value="#{ _bolsa.id }"/>
								<h:graphicImage value="/img/flag_red.png" title="Não Solicitar Renovação da Bolsa" />
							</h:commandLink> 		
						</td>
					</tr>
				</c:forEach>	
			</tbody>
			
		</table>
	</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>