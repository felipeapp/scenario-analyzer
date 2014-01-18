<link rel="stylesheet" type="text/css" href="/shared/css/tablesorter/style.css" />
<table class="listagem tablesorter" id="listagem" style="width: 100%;">
	<caption class="listagem">${nomeCaption} (${fn:length(ofertas)})</caption>
	<thead>
		<tr>
			<th style="width: 30%;">Título</th>
			<th style="text-align: right; padding-right: 20px;">Vagas</th>
			<th style="text-align: right; padding-right: 20px;">Valor da Bolsa</th>
			<c:if test="${not ofertaEstagioMBean.portalDiscente}">
				<th style="text-align: center;padding-right: 20px;">Início da Publicação</th>
				<th style="text-align: center;padding-right: 20px;">Fim da Publicação</th>
				<th>Status</th>
			</c:if>
			<c:if test="${ofertaEstagioMBean.portalDiscente}">
				<th style="text-align: center;padding-right: 20px;">Fim das Inscrições</th>
			</c:if>
			<td style="width:2%;"></td>
			<c:if test="${(ofertaEstagioMBean.portalDiscente || ofertaEstagioMBean.portalCoordenadorGraduacao || convenioEstagioMBean.permiteAnalisarConvenio)}">
				<td style="width:2%;"></td>
			</c:if>
			<c:if test="${ofertaEstagioMBean.portalConcedenteEstagio || ofertaEstagioMBean.portalCoordenadorGraduacao || convenioEstagioMBean.convenioEstagio}">
				<td style="width:2%;"></td>
			</c:if>						
			<c:if test="${ofertaEstagioMBean.portalCoordenadorGraduacao || ofertaEstagioMBean.portalConcedenteEstagio || convenioEstagioMBean.permiteAnalisarConvenio}">
				<td style="width:2%;"></td>
			</c:if>
			<c:if test="${convenioEstagioMBean.permiteAnalisarConvenio}">
				<td style="width:2%;"></td>
			</c:if>
		</tr>
	</thead> 
	<tbody>
	<c:forEach items="#{ofertas}" var="oferta" varStatus="loop">	
		<tr class="${loop.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
			<td>${oferta.titulo}</td>
			<td style="text-align: right;">${oferta.numeroVagas}</td>
			<td style="text-align: right;"><ufrn:format type="moeda" valor="${oferta.valorBolsa}"></ufrn:format></td>
			<c:if test="${not ofertaEstagioMBean.portalDiscente}">
				<td style="text-align: center;"><ufrn:format type="data" valor="${oferta.dataInicioPublicacao}"></ufrn:format></td>
				<td style="text-align: center;"><ufrn:format type="data" valor="${oferta.dataFimPublicacao}"></ufrn:format></td>
				<td>${oferta.status.descricao}</td>
			</c:if>
			<c:if test="${ofertaEstagioMBean.portalDiscente}">
				<td style="text-align: center;"><ufrn:format type="data" valor="${oferta.dataFimPublicacao}"></ufrn:format></td>
			</c:if>			
			
			<td style="width:2%;">
				<h:commandLink action="#{ofertaEstagioMBean.view}" title="Visualizar Oferta de Estágio">
					<h:graphicImage value="/img/view.gif" alt="Visualizar Oferta de Estágio"/>
					<f:setPropertyActionListener value="#{oferta}" target="#{ofertaEstagioMBean.obj}"/>
				</h:commandLink>
			</td>
			<c:if test="${(ofertaEstagioMBean.portalDiscente || ofertaEstagioMBean.portalCoordenadorGraduacao || convenioEstagioMBean.permiteAnalisarConvenio)}">
				<td style="width:2%;">
					<h:commandLink action="#{interesseOfertaMBean.iniciar}" title="#{(ofertaEstagioMBean.portalDiscente ? 'Inscrever-se em Processo Seletivo' : 'Inscrever Discente em Processo Seletivo')}"
					rendered="#{oferta.aprovado}">
						<h:graphicImage value="#{(ofertaEstagioMBean.portalDiscente ? '/img/seta.gif' : '/img/adicionar.gif')}" 
						alt="#{(ofertaEstagioMBean.portalDiscente ? 'Inscrever-se em Processo Seletivo' : 'Inscrever Discente em Processo Seletivo')}"/>
						<f:setPropertyActionListener value="#{oferta}" target="#{interesseOfertaMBean.ofertaEstagio}"/>
					</h:commandLink>
				</td>
			</c:if>
			<c:if test="${ofertaEstagioMBean.portalConcedenteEstagio || ofertaEstagioMBean.portalCoordenadorGraduacao || convenioEstagioMBean.convenioEstagio}">
				<td style="width:2%;">
					<h:commandLink action="#{interesseOfertaMBean.view}" title="Visualizar Inscrições"
					rendered="#{oferta.aprovado}">
						<h:graphicImage value="/img/estagio/view_interesse.png" alt="Visualizar Inscrições"/>
						<f:setPropertyActionListener value="#{oferta}" target="#{interesseOfertaMBean.ofertaEstagio}"/>
					</h:commandLink>
				</td>
			</c:if>						
			<c:if test="${ofertaEstagioMBean.portalCoordenadorGraduacao || ofertaEstagioMBean.portalConcedenteEstagio || convenioEstagioMBean.permiteAnalisarConvenio}">
				<td style="width:2%;">
					<h:commandLink action="#{ofertaEstagioMBean.alterar}" title="Alterar Oferta de Estágio" >
						<h:graphicImage value="/img/alterar.gif" alt="Alterar Oferta de Estágio"/>
						<f:setPropertyActionListener value="#{oferta}" target="#{ofertaEstagioMBean.obj}"/>
					</h:commandLink>
				</td>
			</c:if>
			<c:if test="${convenioEstagioMBean.permiteAnalisarConvenio}">
				<td style="width:2%;">
					<h:commandLink action="#{ofertaEstagioMBean.iniciarAnalise}" title="Analisar Oferta de Estágio" >
						<h:graphicImage value="/img/seta.gif" alt="Analisar Oferta de Estágio"/>
						<f:setPropertyActionListener value="#{oferta}" target="#{ofertaEstagioMBean.obj}"/>
					</h:commandLink>
				</td>
			</c:if>
		</tr>					
	</c:forEach>
	</tbody>
</table>	
