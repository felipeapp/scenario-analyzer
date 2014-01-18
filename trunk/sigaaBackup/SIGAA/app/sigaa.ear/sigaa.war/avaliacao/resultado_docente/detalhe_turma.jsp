<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@taglib prefix="cewolf" uri="/tags/cewolf" %>
<jsp:useBean id="dados" class="br.ufrn.sigaa.avaliacao.jsf.ResultadoAvaliacaoDatasetProducer" scope="page" />

<style>
	.columnClasses {
		vertical-align: top;
	}
</style>

<f:view>
	<%@include file="/portais/docente/menu_docente.jsp"%>
	<h2><ufrn:subSistema /> > Resultado da Avaliação Institucional</h2>

		<h:form id="formulario">
		<rich:tabPanel selectedTab="#{portalResultadoAvaliacao.abaSelecionada}" id="tabPanel">
			<c:if test="${acesso.avaliacao}">
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
					<f:param id="idDocente" name="idPessoa" value="#{abaItem.id_pessoa}"/>
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
													<c:set var="tipoGrafico" value="verticalbar" />
													<c:set var="xLabel" value="Pergunta" />
													<c:set var="yLabel" value="Média" />
													<c:if test="${pergunta.simNao}">
														<c:set var="tipoGrafico" value="stackedhorizontalbar" />
														<c:set var="xLabel" value="Pergunta" />
														<c:set var="yLabel" value="% Sim/Não" />
													</c:if>
												</c:forEach>
												<cewolf:chart id="#{grupo.titulo}" type="${tipoGrafico}" xaxislabel="${xLabel}" yaxislabel="${yLabel}"> 
													<cewolf:colorpaint color="#D3E1F1"/>
													<cewolf:data> 
														<cewolf:producer id="dados"> 
															<cewolf:param name="resultados" value="${portalResultadoAvaliacao.resultadosDocentes}"/>
															<cewolf:param name="idPessoa" value="${ portalResultadoAvaliacao.pessoa.id }"/>
															<cewolf:param name="grupo" value="${grupo.titulo}"/>
															<cewolf:param name="idResultado" value="${ portalResultadoAvaliacao.idResultado }"/>
														</cewolf:producer>
													</cewolf:data>
												</cewolf:chart> 
												<h4>${grupo.titulo} - ${grupo.descricao }</h4>
												<cewolf:img chartid="#{grupo.titulo}" renderer="/cewolf" width="450" height="350">
													<cewolf:map tooltipgeneratorid="dados"/> 
												</cewolf:img>
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
													<th width="5%" style="text-align: right;">Turma</th>
													<th width="5%" style="text-align: right;">Discentes</th>
													<th width="10%" style="text-align: right;">Trancamentos</th>
													<th width="5%" style="text-align: right;">Média Geral</th>
													<th width="5%" style="text-align: right;">Desvio Padrão Geral</th>
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
															<c:if test="${linha.id != portalResultadoAvaliacao.idResultado}">
																<h:commandLink action="#{portalResultadoAvaliacao.detalhaTurma}">
																	<f:param name="idResultado" value="#{linha.id}"/>
																	<h:graphicImage  value="/img/biblioteca/baixo.gif" style="overflow: visible;" />
																</h:commandLink>
															</c:if>
															<c:if test="${linha.id == portalResultadoAvaliacao.idResultado}">
																<h:commandLink action="#{portalResultadoAvaliacao.exibeResumo}">
																	<f:param id="idDocenteResumo" name="idPessoa" value="#{abaItem.id_pessoa}"/>
																	<f:param id="anoResumo" name="ano" value="#{abaItem.ano}"/>
																	<f:param id="periodoResumo" name="periodo" value="#{abaItem.periodo}"/>
																	<f:param id="idFormularioResumo" name="idFormulario" value="#{abaItem.id_formulario_avaliacao}"/>
																	<h:graphicImage  value="/img/biblioteca/cima.gif" style="overflow: visible;" />
																</h:commandLink>
															</c:if>	
														</td>
													</tr>
													<!-- Detalhe das notas -->
													<c:if test="${linha.id == portalResultadoAvaliacao.idResultado}">
														<tr>
															<td colspan="7">
																<table class="visualizacao" width="100%">
																	<caption>Detalhe das Médias por Pergunta</caption>
																	<tbody>
																		<c:set var="grupoAtual" value=""/>
																		<c:set var="fechaTabela" value="false" />
																		<c:set var="ordem" value="1" />
																		<c:forEach items="#{portalResultadoAvaliacao.mediaNotas}" var="media" varStatus="loop">
																			<c:set var="grupoLoop" value="${ media.pergunta.grupo.titulo}"/>
																			<c:if test="${grupoLoop != grupoAtual}">
																				<c:set var="grupoAtual" value="${grupoLoop}"/>
																				<c:set var="ordem" value="1" />
																				<tr class="header">
																					<td colspan="4" class="subFormulario">${media.pergunta.grupo.titulo}</td>
																				</tr>
																				<tr class="header, caixaCinza">
																					<th width="5%"  style="text-align: right;">Ordem</th>
																					<th width="70%" style="text-align: left;">Pergunta</th>
																					<th width="5%"  style="text-align: right;">Média</th>
																					<th width="5%"  style="text-align: right;">Desvio Padrão</th>
																				</tr>
																			</c:if>
																			<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
																				<td style="text-align: right;">${ordem}</td>
																				<td style="text-align: left;">${media.pergunta.descricao}</td>
																				<td style="text-align: right;"><ufrn:format type="valor" valor="${media.media}"/></td>
																				<td style="text-align: right;"><ufrn:format type="valor" valor="${media.desvioPadrao}"/></td>
																			</tr>
																			<c:set var="ordem" value="${ordem + 1}" />
																		</c:forEach>
																		<c:forEach items="#{portalResultadoAvaliacao.percentuaisSimNao}" var="percentual" varStatus="loop">
																			<c:set var="grupoLoop" value="${ percentual.pergunta.grupo.titulo}"/>
																			<c:if test="${grupoLoop != grupoAtual}">
																				<c:set var="grupoAtual" value="${grupoLoop}"/>
																				<c:set var="ordem" value="1" />
																				<tr class="header">
																					<td colspan="4" class="subFormulario">${percentual.pergunta.grupo.titulo}</td>
																				</tr>
																				<tr class="header, caixaCinza">
																					<th width="5%"  style="text-align: right;">Ordem</th>
																					<th width="70%" style="text-align: left;">Pergunta</th>
																					<th width="5%"  style="text-align: right;">%Sim</th>
																					<th width="5%"  style="text-align: right;">%Não</th>
																				</tr>
																			</c:if>
																			<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
																				<td style="text-align: right;">${ordem}</td>
																				<td style="text-align: left;">${percentual.pergunta.descricao}</td>
																				<td style="text-align: right;"><ufrn:format type="valor" valor="${percentual.percentualSim}"/>%</td>
																				<td style="text-align: right;"><ufrn:format type="valor" valor="${percentual.percentualNao}"/>%</td>
																			</tr>
																			<c:set var="ordem" value="${ordem + 1}" />
																		</c:forEach>
																	</tbody>
																</table>
															</td>
														</tr>
													</c:if>
												</c:forEach>
											</tbody>
											<tfoot>
												<tr>
													<td colspan="4" style="text-align: right;font-weight: bold;">Média Geral no Período:</td>
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