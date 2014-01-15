<%@include file="/public/include/cabecalho.jsp" %>


<style>

h3 {
    border-bottom: 1px solid #DDD;
    color: olive;
    font-size: 1.2em;
    font-variant: small-caps;
    margin-bottom: 10px;
    padding: 2px 0 1px 24px;
}

.popUp {
    padding: 5px;
    border: 1px solid #FFC30E;
    width: 200px;
    color: #9C7600;
    background-color: #FFFBB8;
    text-align: left;
    position: absolute;
    left: 0;
    top: -5px;
    visibility: hidden;
    overflow: visible;
    z-index: 100;
}


table.listagem tr.atividadePaiSubAtividade td{
	background: #C8D5EC;
	font-weight: bold;
	padding-left: 20px;
}


</style>

<f:view>
	<h2>Consulte as Inscrições On-line de Cursos e Eventos</h2>
	
    <%@include file="/extensao/InscricaoOnline/include/buscar.jsp"%>
    
<h:form id="form">    

	<h3>Lista de Cursos e Eventos Para Inscrição</h3>
	<c:choose>
		<c:when test="${empty inscricoes}">
			<center><i>Nenhuma ação de extensão localizada</i></center>
		</c:when>

		<c:otherwise>
			
				<div class="infoAltRem">
					<h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Ver Detalhes &nbsp;
	    			<h:graphicImage value="/img/seta.gif" style="overflow: visible;"/>: Inscrever-se &nbsp;
	    			<h:graphicImage value="/img/email_go.png" style="overflow: visible;"/>: Reenviar Senha de Acesso
				</div>
				<table class="listagem" width="100%" cellpadding="0" cellspacing="0">
					<caption class="listagem">Cursos e Eventos localizados (${fn:length(inscricoes)})</caption>
					<thead>
						<tr>
							<th>Título</th>
							<th width="10%">Tipo</th>
							<th style="text-align:center">Inscrições até</th>
							<th style="text-align:right">Total de Vagas</th>
							<th style="text-align:right">Inscrições Solicitadas</th>
							<th style="text-align:right">Inscrições Aceitas</th>
							<th style="text-align:center" width="10%">Situação</th>
							<th width="3%" />
							<th width="3%" />
							<th width="3%" />
						</tr>
					</thead>
					<tbody>
						<c:forEach items="#{inscricoes}" var="inscricao" varStatus="status">
							<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
								<td>
									${inscricao.atividade.titulo}<br/>
									<font style="font-size: x-small;"><i>Coordenação: ${inscricao.atividade.coordenacao.pessoa.nome}</i></font>								
								</td>
								<td>${inscricao.atividade.tipoAtividadeExtensao.descricao}</td>
								<td style="text-align:center">
									<h:outputText value="#{inscricao.periodoFim}">
										<f:convertDateTime pattern="dd/MM/yyyy" />
									</h:outputText>
								</td>
								<c:choose>
									<c:when test="${not inscricao.temVagas}">
										<td style="text-align:center" colspan="3">
											<span style="color: maroon">PENDENTE</span>
										</td>
									</c:when>
									<c:otherwise>
										<td style="text-align:right">${inscricao.quantidadeVagas}</td>
			                            <td style="text-align:right">${inscricao.numeroConfirmados + inscricao.numeroAceitos}</td>
			                            <td style="text-align:right">${inscricao.numeroAceitos}</td>
									</c:otherwise>
								</c:choose>
								<td style="text-align:center">${inscricao.aberta ? '<span style="color: #292">ABERTA</span>': 
										'<span style="color: red">FECHADA</span>'}</td>
								<td align="center">
									<h:outputLink title="Ver Detalhes" value="/sigaa/link/public/extensao/viewCursoEvento">
										<f:param name="id" value="#{inscricao.atividade.id}" />
										<h:graphicImage url="/img/view.gif" />
									</h:outputLink>
								</td>
								<td align="center">
									<h:outputLink title="Inscrever-se" rendered="#{inscricao.aberta}"
											value="/sigaa/link/public/extensao/formInscricaoOnline">
										<f:param name="id" value="#{inscricao.id}" />
										<f:param name="flag" value="#{inscricao.temVagas}" />
										<h:graphicImage url="/img/seta.gif" />
									</h:outputLink>
								</td>
								<td align="center">
                                    <h:outputLink title="Reenviar Senha de Acesso"
                                            value="/sigaa/link/public/extensao/formReenviarCodigoInscricaoOnline">
                                        <f:param name="id" value="#{inscricao.id}" />
                                        <f:param name="subAtv" value="false" />
                                        <h:graphicImage url="/img/email_go.png" />
                                    </h:outputLink>
                                </td>
								
							</tr>
						</c:forEach>
					</tbody>
				</table>
			
		</c:otherwise>
	</c:choose>
	
	
	

	<br/><br/><br/>
	
		
	
	<h3>Lista de Mini Cursos e Mini Eventos Para Inscrição</h3>
	<c:choose>
		<c:when test="${empty inscricoesSubAtividades}">
			<center><i>Nenhum Mini Cursos e Mini Eventos Localizada</i></center>
		</c:when>

		<c:otherwise>
			
				<table class="listagem" width="100%" cellpadding="0" cellspacing="0">
					<caption class="listagem">Mini Cursos e Mini Eventos localizados (${fn:length(inscricoesSubAtividades)})</caption>
					<thead>
						<tr>
							<th>Título</th>
							<th width="10%">Tipo da Atividade</th>
							<th style="text-align:center">Inscrições até</th>
							<th style="text-align:right">Total de Vagas</th>
							<th style="text-align:right">Inscrições Solicitadas</th>
							<th style="text-align:right">Inscrições Aceitas</th>
							<th style="text-align:center" width="10%">Situação</th>
							<th width="3%" />
							<th width="3%" />
							<th width="3%" />
						</tr>
					</thead>
					<tbody>
					
						<c:set var="idAtividadeExtensao" value="-1" scope="request"/>
					
						<c:forEach items="#{inscricoesSubAtividades}" var="inscricao" varStatus="status">
							
							<c:if test="${ idAtividadeExtensao != inscricao.atividade.id}">
								<c:set var="idAtividadeExtensao" value="${inscricao.atividade.id}" scope="request" />
								<tr class="atividadePaiSubAtividade">
									<td colspan="10"> Curso ou Evento</td>
								</tr>
								<tr class="atividadePaiSubAtividade">
									<td colspan="10"> ${inscricao.atividade.codigo} - ${inscricao.atividade.titulo}</td>
								</tr>
							</c:if>
							
							<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
								<td>
									${inscricao.subAtividade.titulo}<br/>
									<font style="font-size: x-small;"><i>Coordenação: ${inscricao.atividade.coordenacao.pessoa.nome}</i></font>								
								</td>
								<td>${inscricao.atividade.tipoAtividadeExtensao.descricao}</td>
								<td style="text-align:center">
									<h:outputText value="#{inscricao.periodoFim}">
										<f:convertDateTime pattern="dd/MM/yyyy" />
									</h:outputText>
								</td>
								
								<td style="text-align:right">${inscricao.quantidadeVagas}</td>
			                    <td style="text-align:right">${inscricao.numeroConfirmados + inscricao.numeroAceitos}</td>
			                    <td style="text-align:right">${inscricao.numeroAceitos}</td>
								
								<td style="text-align:center">${inscricao.aberta ? '<span style="color: #292">ABERTA</span>': 
										'<span style="color: red">FECHADA</span>'}</td>
								<td align="center">
									<h:outputLink title="Ver Detalhes" value="/sigaa/link/public/extensao/viewSubAtividade">
										<f:param name="idSub" value="#{inscricao.subAtividade.id}" />
										<h:graphicImage url="/img/view.gif" />
									</h:outputLink>
								</td>
								<td align="center">
									<h:outputLink title="Inscrever-se" rendered="#{inscricao.aberta}"
											value="/sigaa/link/public/extensao/formInscricaoSubAtividadeOnline">
										<f:param name="id" value="#{inscricao.id}" />
										<f:param name="flag" value="#{inscricao.temVagas}" />
										<h:graphicImage url="/img/seta.gif" />
									</h:outputLink>
								</td>
								<td align="center">
                                    <h:outputLink title="Reenviar Senha de Acesso"
                                            value="/sigaa/link/public/extensao/formReenviarCodigoInscricaoOnline">
                                        <f:param name="id" value="#{inscricao.id}" />
                                        <f:param name="subAtv" value="true" />
                                        <h:graphicImage url="/img/email_go.png" />
                                    </h:outputLink>
                                </td>								
							</tr>
						</c:forEach>
					</tbody>
				</table>
			
		</c:otherwise>
	</c:choose>
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
</h:form>	
	
	
	
	<br />
	<br />
	<div style="width: 80%; text-align: center; margin: 0 auto;">
		<a href="/sigaa/public/home.jsf" style="color: #404E82;">&lt;&lt; voltar ao menu principal</a>
	</div>
	<br />
	
</f:view>

<%@include file="/public/include/rodape.jsp" %>