<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<f:view>
	<h2>Consulte as Inscrições On-line de Cursos e Eventos</h2>
	
    <%@include file="/extensao/InscricaoOnline/include/buscar.jsp"%>

	<c:choose>
		<c:when test="${empty inscricoes}">
			<center><i>Nenhuma ação de extensão localizada</i></center>
		</c:when>

		<c:otherwise>
			<h:form id="form">
				<div class="infoAltRem">
					<h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Ver Ação de Extensão &nbsp;
	    			<h:graphicImage value="/img/seta.gif" style="overflow: visible;"/>: Listar Inscritos / Reenviar Senha  &nbsp;	    			
				</div>
				<table class="listagem" width="100%" cellpadding="0" cellspacing="0">
					<caption class="listagem">Cursos e Eventos localizados (${fn:length(inscricoes)})</caption>
					<thead>
						<tr>
							<th>Título</th>
							<th>Tipo</th>
							<th>Coordenação</th>
							<th style="text-align:center">Inscrições até</th>
							<th style="text-align:right">Vagas restantes</th>
							<th style="text-align:center" width="10%">Situação</th>
							<th width="3%" />
							<th width="3%" />
						</tr>
					</thead>
					<tbody>
						<c:forEach items="#{inscricoes}" var="inscricao" varStatus="status">
							<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
								<td>${inscricao.atividade.titulo}</td>
								<td>${inscricao.atividade.tipoAtividadeExtensao.descricao}</td>
								<td>${inscricao.atividade.coordenacao.pessoa.nome}</td>
								<td style="text-align:center">
									<h:outputText value="#{inscricao.periodoFim}">
										<f:convertDateTime pattern="dd/MM/yyyy" />
									</h:outputText>
								</td>
								<c:choose>
									<c:when test="${not inscricao.temVagas}">
										<td style="text-align:center">
											<span style="color: maroon">PENDENTE</span>
										</td>
									</c:when>
									<c:otherwise>
									<td style=" text-align:right">${inscricao.numeroVagasRestante}</td>
									</c:otherwise>
								</c:choose>
								<td style="text-align:center">${inscricao.aberta ? '<span style="color: #292">ABERTA</span>': 
										'<span style="color: red">FECHADA</span>'}</td>
								<td align="center">
									<h:commandLink title="Ver Ação de Extensão" action="#{atividadeExtensao.view}">
										<f:param name="id" value="#{inscricao.atividade.id}" />
										<h:graphicImage url="/img/view.gif" />
									</h:commandLink>
								</td>
								<td align="center">
							        <h:commandLink title="Listar Inscritos" immediate="true" id="listarInscritos"
                                               action="#{inscricaoAtividade.listarInscritos}">
                                           <f:param name="idInscricao" value="#{inscricao.id}" />
                                           <h:graphicImage url="/img/seta.gif"/>
                                    </h:commandLink>
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</h:form>
		</c:otherwise>
	</c:choose>
	
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>