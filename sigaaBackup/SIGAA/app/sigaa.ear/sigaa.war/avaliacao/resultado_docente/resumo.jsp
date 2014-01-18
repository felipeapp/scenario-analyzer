<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@taglib prefix="cewolf" uri="/tags/cewolf" %>
<jsp:useBean id="dados" class="br.ufrn.sigaa.avaliacao.jsf.ResultadoAvaliacaoDatasetProducer" scope="page" />
<jsp:useBean id="sigaaSubSistemas" class="br.ufrn.arq.seguranca.SigaaSubsistemas" scope="page"/>

<style>
	.columnClasses {
		vertical-align: top;
		text-align: center;
		width: 50%
	}
</style>

<f:view>
	<c:set var="_portalDocente"   value="<%=sigaaSubSistemas.PORTAL_DOCENTE%>"/>
	<ufrn:checkSubSistema subsistema="<%=sigaaSubSistemas.PORTAL_DOCENTE.getId()%>">
		<%@include file="/portais/docente/menu_docente.jsp"%>
	</ufrn:checkSubSistema>
	<h2><ufrn:subSistema /> > Resultado da Avaliação Institucional</h2>

	<h:form id="formulario">
		<rich:tabPanel selectedTab="#{portalResultadoAvaliacao.abaSelecionada}" id="tabPanel">
			<c:if test="${acesso.avaliacao && portalResultadoAvaliacao.portalAvaliacaoInstitucional}">
				<rich:tab  
					action="#{relatorioAvaliacaoMBean.iniciarResultadoAnalitico}"
					label="Selecionar Outro Docente" id="selecionarOutroDocente">
				</rich:tab>
			</c:if>
			<c:if test="${fn:length(portalResultadoAvaliacao.listaResultados) > 1}">
				<%@include file="/avaliacao/resultado_docente/aba_evolucao.jsp" %>
			</c:if>
			<c:forEach items="#{portalResultadoAvaliacao.listaResultados}" var="abaItem" varStatus="status">
				<rich:tab  
					action="#{portalResultadoAvaliacao.exibeResumo}"
					label="#{abaItem.rotuloAba}" id="aba" 
					name="#{abaItem.rotuloAba}">
					<f:param id="idPessoa" name="idPessoa" value="#{abaItem.id_pessoa}"/>
					<f:param id="ano" name="ano" value="#{abaItem.ano}"/>
					<f:param id="periodo" name="periodo" value="#{abaItem.periodo}"/>
					<f:param id="idFormulario" name="idFormulario" value="#{abaItem.id_formulario_avaliacao}"/>
					<c:if test="${portalResultadoAvaliacao.abaSelecionada == abaItem.rotuloAba}">
						<t:panelGrid id="avaliacaoContentPanel" columns="2" columnClasses="columnClasses, columnClasses">
							<t:panelGroup>
								<rich:tabPanel id="graphsTabbedPanel" switchType="client">
									<c:forEach items="#{portalResultadoAvaliacao.grupoPerguntas}" var="grupo" varStatus="grupoStatus">
										<c:if test="${grupo.avaliaTurmas}">
											<rich:tab label="#{grupo.titulo}" id="abasGraficos" >
												<c:forEach items="${grupo.perguntas}" var="pergunta" varStatus="perguntaStatus" end="0">
													<c:set var="tipoSimNao" value="${pergunta.simNao}" />
												</c:forEach>
												<c:if test="${!tipoSimNao}" >
													<cewolf:chart id="#{grupo.titulo}" type="verticalbar" xaxislabel="Pergunta" yaxislabel="Média"> 
														<cewolf:colorpaint color="#D3E1F1"/>
														<cewolf:data> 
															<cewolf:producer id="dados"> 
																<cewolf:param name="resultados" value="${portalResultadoAvaliacao.resultadosDocentes}"/>
																<cewolf:param name="idPessoa" value="${ portalResultadoAvaliacao.pessoa.id }"/>
																<cewolf:param name="grupo" value="${grupo.titulo}"/>
															</cewolf:producer>
														</cewolf:data>
													</cewolf:chart>
													<h4>${grupo.titulo} - ${grupo.descricao }</h4>
													<cewolf:img chartid="#{grupo.titulo}" renderer="/cewolf" width="450" height="350">
														<cewolf:map tooltipgeneratorid="dados"/> 
													</cewolf:img> 
												</c:if>
												<!-- Gráficos de percentual de respostas sim/não -->
												<c:if test="${tipoSimNao}" >
													<h4>${grupo.titulo} - ${grupo.descricao }</h4>
													<br/>
													<c:forEach items="#{portalResultadoAvaliacao.resultadosDocentes}" var="linha" varStatus="loop">
														<cewolf:chart id="#{grupo.titulo}" type="stackedhorizontalbar" xaxislabel="Pergunta" yaxislabel="% Sim/Não"> 
															<cewolf:colorpaint color="#D3E1F1"/>
																<cewolf:data>
																	<cewolf:producer id="dados"> 
																		<cewolf:param name="resultados" value="${portalResultadoAvaliacao.resultadosDocentes}"/>
																		<cewolf:param name="idPessoa" value="${ portalResultadoAvaliacao.pessoa.id }"/>
																		<cewolf:param name="idTurma" value="${ linha.docenteTurma.turma.id }"/>
																		<cewolf:param name="grupo" value="${grupo.titulo}"/>
																	</cewolf:producer>
																</cewolf:data>
														</cewolf:chart> 
														<h4>${linha.docenteTurma.turma.disciplina.descricaoResumida}</h4>
														<cewolf:img chartid="#{grupo.titulo}" renderer="/cewolf" width="450" height="180">
															<cewolf:map tooltipgeneratorid="dados"/> 
														</cewolf:img>
													</c:forEach>
													<br/><br/>
												</c:if>
												<br/><br/>
												<table class="listagem">
													<tr><td colspan="2" class="subFormulario">Legenda</td></tr>
													<c:forEach items="${grupo.perguntas}" var="pergunta" varStatus="perguntaStatus">
														<c:if test="${pergunta.avaliarTurmas}" >
															<tr>
																<td><b>${grupoStatus.index + 1}.${perguntaStatus.index + 1}</b></td>
																<td valign="top">${pergunta.descricao}</td>
															</tr>
														</c:if> 
													</c:forEach>
												</table>
											</rich:tab>
										</c:if>
									</c:forEach>
								</rich:tabPanel>	
							</t:panelGroup>
							<t:panelGroup>
								<rich:tabPanel id="mediasTabbedPanel" switchType="client">
									<rich:tab label="Média das Turmas" id="mediaTurmasTab">
										<div class="infoAltRem">
											<h:graphicImage value="/img/biblioteca/baixo.gif"  style="overflow: visible;" />: Detalhar Médias
											<h:graphicImage value="/img/biblioteca/cima.gif"  style="overflow: visible;" />: Esconder Detalhes
										</div>
										<table class="visualizacao" width="450">
										<caption>Média das Turmas no Semestre</caption>
											<thead>
												<tr>
													<th width="70%" style="text-align: left;">Componente Curricular</th>
													<th width="5%"  style="text-align: right;">Turma</th>
													<th width="5%" style="text-align: right;">Discentes</th>
													<th width="10%" style="text-align: right;">Trancamentos</th>
													<th width="5%" style="text-align: right;">Média Geral</th>
													<th width="5%" style="text-align: right;">DP Geral</th>
													<th></th>
												</tr>
											</thead>
											<tbody>
												<c:forEach items="#{portalResultadoAvaliacao.resultadosDocentes}" var="linha" varStatus="loop">
													<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
														<td align="left">${ linha.docenteTurma.turma.disciplina.descricaoResumida }</td>
														<td align="right">${ linha.docenteTurma.turma.codigo }</td>
														<td align="right">${ linha.numDiscentes }</td>
														<td align="right">${ linha.numTrancamentos }</td>
														<c:if test="${not empty linha.mediaGeral}">
															<td align="right"><ufrn:format type="valor" valor="${ linha.mediaGeral }"/></td>
															<td align="right"><ufrn:format type="valor" valor="${ linha.desvioPadraoGeral }"/></td>
														</c:if>
														<c:if test="${empty linha.mediaGeral }">
															<td align="right">N/A</td>
															<td align="right">N/A</td>
														</c:if>
														<td align="center">
															<h:commandLink action="#{portalResultadoAvaliacao.detalhaTurma}">
																<f:param name="idResultado" value="#{linha.id}"/>
																<h:graphicImage  value="/img/biblioteca/baixo.gif" style="overflow: visible;" />
															</h:commandLink>
														</td>
													</tr>
												</c:forEach>
											</tbody>
											<tfoot>
												<tr>
													<td colspan="4" style="text-align: right;font-weight: bold;"> 
														Média Geral no Período<sup>[1]</sup>:
													</td>
													<td style="text-align: right;font-weight: bold;"><ufrn:format type="valor" valor="${ portalResultadoAvaliacao.mediaGeralSemestre }"/></td>
													<td>-</td>
													<td></td>
												</tr>
												<tr>
													<td colspan="7" style="text-align: center;">
														<h:commandButton action="#{portalResultadoAvaliacao.viewResultadoDocente}" value="Gerar Relatório Analítico das Médias"></h:commandButton>
													</td>
												</tr>
											</tfoot>
										</table>
										<br/><b>[1]</b>: a Média Geral no Período corresponde a média das notas da ${portalResultadoAvaliacao.dimensaoMediaGeral}.
									</rich:tab>
									</rich:tabPanel>
							</t:panelGroup>
						</t:panelGrid>
					</c:if>
				</rich:tab>
			</c:forEach>
		</rich:tabPanel>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>