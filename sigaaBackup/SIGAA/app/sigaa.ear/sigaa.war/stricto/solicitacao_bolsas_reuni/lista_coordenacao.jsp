<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<a4j:keepAlive beanName="solicitacaoBolsasReuniBean" />

	<h2> <ufrn:subSistema /> &gt; Solicitações de Bolsas REUNI de Assistência ao Ensino </h2>

	<div class="descricaoOperacao">
		<p>
			<b> Caro Coordenador, </b>
		</p> 	
		<p>
			Estão listadas aqui todas as solicitações de bolsas REUNI cadastradas pelo seu Programa de Pós-Graduação.
			Através desta página será possível consultar os dados de uma solicitação e acampanhar sua situação 
			dentro do processo e avaliação das propostas pela Pró-Reitoria de Pós-Graduação.
		</p>
	
	</div>

	<div class="infoAltRem" style="width:80%">
        <h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar solicitação
	</div>

	<h:form id="form">
	<table class="listagem" style="width:80%">
		<caption> Solicitações cadastradas </caption>
		<thead>
		<tr>
			<th> Edital </th>
			<th> N&ordm; de Bolsas Solicitadas </th>
			<th> Status </th>
			<th> </th>
		</tr>
		</thead>
		<tbody>
		<c:forEach items="#{solicitacaoBolsasReuniBean.all}" var="_solicitacao">
			<tr>
				<td> ${_solicitacao.edital.descricao} </td>
				<td align="center"> ${ fn:length(_solicitacao.planos) } </td>
				<td> ${ _solicitacao.descricaoStatus } </td>
				<td> 
					<h:commandButton image="/img/view.gif" title="Visualizar solicitação" action="#{solicitacaoBolsasReuniBean.view}" styleClass="noborder" id="verSolicit">
						<f:setPropertyActionListener value="#{_solicitacao}" target="#{solicitacaoBolsasReuniBean.obj}"/>
					</h:commandButton>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>	