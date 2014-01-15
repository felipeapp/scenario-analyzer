<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<h2><ufrn:subSistema/> &gt; Solicita��es de Levantamento Bibliogr�fico</h2>

	<div class="descricaoOperacao">
		<p>Abaixo s�o exibidas as suas solicita��es, ordenadas por data de solicita��o, indicando sua situa��o atual.</p>
		<p>As poss�veis situa��es  para solicita��es de levantamento bibliogr�fico s�o: "Validada", quando est�
		   esperando o levantamento e "Finalizada", quando o levantamento j� foi feito.</p>
		<p>As solicita��es de levantamento bibliogr�fico de infra-estrutura, iniciam com a situa��o "Aguardando
		   Valida��o" e s� poder�o ser finalizadas ap�s valida��o pela diretoria da Biblioteca Central.</p> 
	</div>

	<h:form id="listagem">
		<div class="infoAltRem">
		
			<h:graphicImage value="/img/adicionar.gif" />
			<h:commandLink value="Nova Solicita��o de Levantamento Bibliogr�fico"
					action="#{levantamentoBibliograficoInfraMBean.solicitarLevantamentoBibliograficoIndividual}" />
			<h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar solicita��o
		    
		</div>
			
		<table class="listagem" width=80%>
			<caption>Solicita��es</caption>

			<thead>
				<tr>
				<th style="text-align:center;width:100px;">Data da solicita��o</th>
				<th>Assunto</th>
				<th>Biblioteca</th>
				<th style="text-align:center;width:100px;">Situa��o</th>
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