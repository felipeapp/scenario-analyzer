<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<a4j:keepAlive beanName="planoDocenciaAssistidaMBean" />
	<h2> <ufrn:subSistema /> &gt; Plano de Docência Assistida </h2>
	
	<center>
		<div class="infoAltRem">
			<h:form>
				<c:if test="${planoDocenciaAssistidaMBean.portalDiscente}">															
					<h:graphicImage value="/img/alterar.gif"/>: Preencher/Alterar Plano de Docência Assistida<br/>
				</c:if>
				<c:if test="${planoDocenciaAssistidaMBean.portalPpg}">
					<h:graphicImage value="/img/alterar.gif"/>: Alterar Plano de Docência Assistida
				</c:if>
				<h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Plano de Docência Assistida
				<c:if test="${planoDocenciaAssistidaMBean.portalPpg || planoDocenciaAssistidaMBean.portalCoordenadorStricto}">
					<h:graphicImage value="/img/report.png"/>: Visualizar Relatório Semestral<br/>								
					<h:graphicImage value="/img/seta.gif" style="overflow: visible;"/>: Analisar Plano de Docência Assistida 	
				</c:if>
				<c:if test="${planoDocenciaAssistidaMBean.portalDiscente}">	
					<h:graphicImage value="/img/report.png"/>: Preencher/Visualizar Relatório Semestral
				</c:if>
				<h:graphicImage value="../../../shared/img/icones/download.png" style="overflow: visible;"/>: Download do Arquivo Anexado
				<c:if test="${planoDocenciaAssistidaMBean.portalPpg || planoDocenciaAssistidaMBean.portalCoordenadorStricto}">
					<h:graphicImage value="/img/cal_prefs.png" style="overflow: visible;"/>: Visualizar Histórico de Movimentações
				</c:if>	
			</h:form>
		</div>
	</center>			
	
	<h:form>
		<c:if test="${planoDocenciaAssistidaMBean.portalDiscente}">
			<table class="visualizacao" style="width: 100%">
				<caption>Dados do Aluno</caption>
				<tr>
					<th>
						Nome:
					</th>
					<td>
					    ${planoDocenciaAssistidaMBean.discenteUsuario.discente.matricula} - ${planoDocenciaAssistidaMBean.discenteUsuario.discente.pessoa.nome}
					</td>		
				</tr>
				<tr>
					<th>
						Programa:
					</th>
					<td>
					    ${planoDocenciaAssistidaMBean.discenteUsuario.discente.unidade.nome}
					</td>		
				</tr>	
				<tr>
					<th>
						Orientador:
					</th>
					<td>
					    ${planoDocenciaAssistidaMBean.orientacao.descricaoOrientador}
					</td>		
				</tr>		
				<tr>
					<th>
						Nível:
					</th>
					<td>
					    ${planoDocenciaAssistidaMBean.discenteUsuario.discente.nivelDesc}
					</td>		
				</tr>
			</table>	
		</c:if>	
		<br/>
		<c:if test="${planoDocenciaAssistidaMBean.portalDiscente}">
			<%@include file="include_lista_plano_docencia.jsp"%>	
			<br/>
		</c:if>
		<table class="listagem" style="width: 100%">
			<caption class="listagem">Lista de Indicações</caption>
			<c:if test="${not empty planoDocenciaAssistidaMBean.allIndicacoes}">			
				<thead>
					<tr>
						<th>Plano de Trabalho</th>
						<th>Discente</th>
						<th>Nível</th>
						<th align="center">Ano/Período</th>
						<th>Status</th>
						<th colspan="9"></th>
					</tr>
				</thead>
				<c:set var="statusPlano" value="0"/>
				<c:forEach items="#{planoDocenciaAssistidaMBean.allIndicacoes}" var="item">				
					<c:forEach items="#{item.periodosIndicacao}" var="periodo">
						<c:if test="${not empty periodo.planoDocenciaAssistida}">
							<c:forEach items="#{periodo.planoDocenciaAssistida}" var="plano">
								<tr class="${statusPlano % 2 == 0 ? "linhaPar" : "linhaImpar" }">	
									<c:set var="statusPlano" value="${statusPlano + 1}"/>
																												
									<%@include file="include_lista_indicacoes.jsp"%>
									<c:choose>
										<c:when test="${not plano.ativo}">
											<td style="color:red; font-weight: bold;">INATIVO</td>
										</c:when>
										<c:when test="${plano.reprovado}">
											<td style="color:red; font-weight: bold;">${plano.descricaoStatus}</td>
										</c:when>
										<c:when test="${plano.aprovado}">
											<td style="color:green; font-weight: bold;">${plano.descricaoStatus}</td>
										</c:when>
										<c:when test="${ plano.concluido}">
											<td style="color:#4169E1; font-weight: bold;">${plano.descricaoStatus}</td>
										</c:when>
										<c:otherwise>
											<td style="color:black; font-weight: normal;">${plano.descricaoStatus}</td>
										</c:otherwise>						
									</c:choose>																					
									<%@include file="include_acoes.jsp"%>
								</tr>
							</c:forEach>						
						</c:if>
						<c:if test="${empty periodo.planoDocenciaAssistida}">
							<tr class="${statusPlano % 2 == 0 ? "linhaPar" : "linhaImpar" }">		
								<c:set var="statusPlano" value="${statusPlano + 1}"/>
											
								<%@include file="include_lista_indicacoes.jsp"%>		
								<td colspan="9">
									<c:if test="${item.planoTrabalho.aprovado && planoDocenciaAssistidaMBean.portalDiscente }">		
										<h:commandLink title="Preencher Plano de Docência Assistida" action="#{planoDocenciaAssistidaMBean.iniciarCadastro}" id="linkPreencherPlano">
											<h:graphicImage value="/img/alterar.gif"/>
											<f:setPropertyActionListener value="#{periodo}" target="#{planoDocenciaAssistidaMBean.periodoIndicacao}"/>
										</h:commandLink>		
									</c:if>
								</td>																							 																				
							</tr>							
						</c:if>
					</c:forEach>
				</c:forEach>										
			</c:if>
			<c:if test="${empty planoDocenciaAssistidaMBean.allIndicacoes}">
				<tr>
					<td colspan="9" style="text-align: center;">
						<i>Nenhuma indicação encontrada para o discente logado.</i>
					</td>
				</tr>	
			</c:if>			
		</table>		
	</h:form>
	
	<c:if test="${planoDocenciaAssistidaMBean.portalPpg || planoDocenciaAssistidaMBean.portalCoordenadorStricto}">
		<div class="voltar">
			<a href="javascript:history.back();"> Voltar </a>
		</div>
	</c:if>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
	