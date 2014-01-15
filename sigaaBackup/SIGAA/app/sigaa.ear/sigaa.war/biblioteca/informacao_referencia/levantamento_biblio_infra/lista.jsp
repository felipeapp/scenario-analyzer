<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<h2><ufrn:subSistema/> &gt; Solicitações de Levantamento Bibliográfico</h2>

	<div class="descricaoOperacao">
		<p>Abaixo são exibidas as suas solicitações, ordenadas por data de solicitação, indicando sua situação atual.</p>
		<p>As possíveis situações  para solicitações de levantamento bibliográfico são: "Validada", quando está
		   esperando o levantamento e "Finalizada", quando o levantamento já foi feito.</p>
		<p>As solicitações de levantamento bibliográfico de infra-estrutura, iniciam com a situação "Aguardando
		   Validação" e só poderão ser finalizadas após validação pela diretoria da Biblioteca Central.</p> 
	</div>

	<h:form id="listagem">
		<div class="infoAltRem">
		
			<h:graphicImage value="/img/adicionar.gif" />
			<h:commandLink value="Nova Solicitação de Levantamento Bibliográfico"
					action="#{levantamentoBibliograficoInfraMBean.solicitarLevantamentoBibliograficoIndividual}" />
			<h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar solicitação
		    
		</div>
			
		<table class="listagem" width=80%>
			<caption>Solicitações</caption>

			<thead>
				<tr>
				<th style="text-align:center;width:100px;">Data da solicitação</th>
				<th>Assunto</th>
				<th>Biblioteca</th>
				<th style="text-align:center;width:100px;">Situação</th>
				<th width="20"></th>
			</thead>
			
			<tbody>
		
				<c:forEach items="#{levantamentoBibliograficoInfraMBean.solicitacoes}" var="s" varStatus="loop">
					
					<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td style="text-align:center;">
							<ufrn:format type="data" valor="${s.dataSolicitacao}" /> 
						</td>
						
						<td>${s.detalhesAssunto}</td>
						
						<td>${s.bibliotecaResponsavel.descricao}</td>
						
						<td style="text-align:center;">${s.descricaoSituacao}</td>
						
						<td>
							<h:commandLink
									action="#{ levantamentoBibliograficoInfraMBean.visualizarSolicitacaoPreenchida }">
								<f:param name="id" value="#{s.id}"/>
								<h:graphicImage value="/img/view.gif" title="Visualizar Dados Pesquisados" />
							</h:commandLink>						
						</td>
					</tr>
				</c:forEach>
			
			</tbody>
	
		</table>

	</h:form>
	<br/>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>