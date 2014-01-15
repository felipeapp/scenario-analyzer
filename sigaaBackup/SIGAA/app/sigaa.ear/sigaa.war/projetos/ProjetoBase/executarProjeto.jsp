<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<%@page import="br.ufrn.sigaa.projetos.dominio.FuncaoMembro"%>
<%@page import="br.ufrn.sigaa.extensao.dominio.TipoAtividadeExtensao"%>
<%@page import="br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto"%>




<style>
<!--

/* Paineis de opçoes */
.titulo {
	background: #EFF3FA;
}
.subtitulo{
	text-align:center; 
	background:#EDF1F8; 
	color:#333366; 
	font-variant:small-caps;
	font-weight:bold;
	letter-spacing:1px;
	margin:1px 0; 
	border-collapse:collapse; 
	border-spacing:2px;
	font-size:1em; 
	font-family:Verdana,sans-serif; 
	font-size: 12px
}

-->
</style>


<f:view>
	<h2><ufrn:subSistema /> > Visualização da Ação Acadêmica Associada</h2>

<h:form>

	<h3 class="tituloTabela"> DADOS DA AÇÃO </h3>
	<table class="formulario" width="100%" >
	<tbody>
		
		<%-- DADOS GERAIS --%>

		<tr>
			<td class="titulo"><b> Ano: </b> </td>
			<td class="titulo" colspan="2"><b> Título: </b> </td>
		</tr>
	
		<tr>
			<td><h:outputText value="#{projetoBase.projeto.ano}"/></td>
			<td  colspan="2"><h:outputText value="#{projetoBase.projeto.titulo}" escape="false"/></td>
			<td></td>
		</tr>

		<tr>
			<td class="titulo"><b> Nº Institucional: </b> </td>
			<td class="titulo"  colspan="2"><b> Período: </b> </td>
		</tr>

		<tr>
			<td><h:outputText value="#{projetoBase.projeto.numeroInstitucional}"/>/<h:outputText value="#{projetoBase.projeto.ano}"/></td>
			<td colspan="2"><h:outputText value="#{projetoBase.projeto.dataInicio}"/> a <h:outputText value="#{projetoBase.projeto.dataFim}"/></td>
		</tr>

		<tr>
			<td class="titulo"><b> Unidade Proponente: </b> </td>			
			<td class="titulo" colspan="2"><b> Abrangência: </b></td>
		</tr>
	
		<tr>
			<td><h:outputText value="#{projetoBase.projeto.unidade.nome}"/> / <h:outputText value="#{projetoBase.projeto.unidade.gestora.sigla}"/></td>
			<td  colspan="2"><h:outputText value="#{projetoBase.projeto.abrangencia.descricao}"/></td>
		</tr>
	
		<tr>
			<td class="titulo"><b> Área do CNPq:</b> </td>
			<td class="titulo" colspan="2"><b> Fonte de Financiamento: </b></td>
		</tr>
	
		<tr>
			<td><h:outputText value="#{projetoBase.projeto.areaConhecimentoCnpq.nome}"/></td>
			<td colspan="2"><h:outputText value="#{projetoBase.projeto.fonteFinanciamentoString}"/></td>
		</tr>
		
		<tr>
			<td class="titulo"><b> Situação: </b>	</td>
			<td class="titulo"  colspan="2"></td>
		</tr>

		<tr>
			<td><h:outputText value="#{projetoBase.projeto.situacaoProjeto.descricao}"/> </td>
			<td></td>
		</tr>

			<tr>
				<td colspan="3"><h3 class="subtitulo">Contato</h3></td>
			</tr>
			<tr style="background-color:#DEDFE3; font-weight:bold;">
				<td><b>Coordenação</b>	</td>
				<td><b>E-mail</b></td>
				<td><b>Telefone</b></td>
			</tr>
		<tr>
			<td><h:outputText value="#{projetoBase.projeto.coordenador.servidor.pessoa.nome}"/></td>
			<td>
				<a href="mailto:<h:outputText value='#{projetoBase.projeto.coordenador.pessoa.email}'/>" >
					<h:outputText value="#{projetoBase.projeto.coordenador.pessoa.email}" rendered="#{not empty projetoBase.projeto.coordenador.pessoa}" />
				</a>
			</td>
			<td>
				<h:outputText value="#{projetoBase.projeto.coordenador.pessoa.telefone}" rendered="#{not empty projetoBase.projeto.coordenador.pessoa}" />
			</td>
		</tr>


		<tr>
			<td colspan="3"><h3 class="subtitulo">Detalhes da ação</h3></td>
		</tr>


		<tr>
			<td colspan="3" align="justify"> 
				<b> Justificativa: </b><br/>
				<h:outputText value="#{projetoBase.projeto.justificativa}" escape="false"/> 
			</td>
		</tr>
			
		
		<tr>
			<td colspan="3" align="justify">
			<b> Resumo:  </b><br/>
			<h:outputText value="#{projetoBase.projeto.resumo}" escape="false"/> </td>
		</tr>
		
		<tr>
			<td colspan="3" align="justify"> 
				<b> Metodologia: </b><br/>
				<h:outputText value="#{projetoBase.projeto.metodologia}" escape="false"/> 
			</td>
		</tr>

		<tr>
			<td colspan="3" align="justify"> 
				<b> Referências: </b><br/>
				<h:outputText value="#{projetoBase.projeto.referencias}" escape="false"/> 
			</td>
		</tr>

		<tr>
				<td colspan="3">
				<h3 class="subtitulo">Membros da Equipe</h3>
		
							<t:dataTable value="#{projetoBase.projeto.equipe}" var="membro" rowClasses="linhaPar, linhaImpar"  
								align="center" width="100%"  id="tbEquipe" rendered="#{not empty projetoBase.projeto.equipe}">
										<t:column>
											<f:facet name="header"><f:verbatim>Nome</f:verbatim></f:facet>
											<h:outputText value="#{membro.pessoa.nome}" />
										</t:column>
										
										<t:column>
											<f:facet name="header"><f:verbatim>Função</f:verbatim></f:facet>
											<h:outputText value="<font color='red'>" rendered="#{membro.funcaoMembro.id == COORDENADOR}"  escape="false"/>
															<h:outputText value="#{membro.funcaoMembro.descricao}" rendered="#{not empty membro.pessoa}" />
											<h:outputText value="</font>" rendered="#{membro.funcaoMembro.id == COORDENADOR}"  escape="false"/>
										</t:column>										
										
										<t:column>
											<f:facet name="header"><f:verbatim>Departamento</f:verbatim></f:facet>
											<h:outputText value="#{membro.servidor.unidade.sigla}" rendered="#{not empty membro.servidor}" />
										</t:column>
										
										<t:column>
											<f:facet name="header"><f:verbatim>Início</f:verbatim></f:facet>
											<h:outputText value="#{membro.dataInicio}"/>
										</t:column>

										<t:column>
											<f:facet name="header"><f:verbatim>Fim</f:verbatim></f:facet>
											<h:outputText value="#{membro.dataFim}"/>
										</t:column>
										
							</t:dataTable>
 						<h:outputText value="<font color='red'>Membros da equipe ainda não foram cadastrados.</font>" rendered="#{(empty projetoBase.projeto.equipe)}" escape="false"/>
				</td>
		</tr>
		 
			<!-- ORÇAMENTO DETALHADO -->
				<c:if test="${not empty projetoBase.projeto.orcamento}">
					<tr>
						<td colspan="3">
									<h3 class="subtitulo">Orçamento Detalhado</h3>			
									<table class="listagem">
										<thead>
										<tr>
											<th>Descrição</th>
											<th style="text-align: right"  width="15%">Valor Unitário </th>
											<th style="text-align: right"  width="10%">Quant.</th>
											<th style="text-align: right" width="15%">Valor Total </th>
										</tr>
										</thead>
		
										<tbody>
		
											<c:if test="${not empty projetoBase.tabelaOrcamentaria}">
											
												<c:set value="${projetoBase.tabelaOrcamentaria}" var="tabelaOrcamentaria" />
												<c:forEach items="#{tabelaOrcamentaria}" var="tabelaOrc">
														
														<tr  style="background: #EFF0FF; font-weight: bold; padding: 2px 0 2px 5px;">
															<td colspan="5">${ tabelaOrc.key.descricao }</td>
														</tr>
																<c:set value="#{tabelaOrc.value.orcamentos}" var="orcamentos" />
																<c:forEach items="#{orcamentos}" var="orcamento" varStatus="status">
																	<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
																		<td style="padding-left: 20px"> ${orcamento.discriminacao}</td>
																		<td align="right"><fmt:formatNumber currencySymbol="R$  " value="${orcamento.valorUnitario}" type="currency" />  </td>
																		<td align="right">${orcamento.quantidade}</td>
																		<td align="right"><fmt:formatNumber currencySymbol="R$  " value="${orcamento.valorTotal}" type="currency"/>  </td>
																	</tr>
																</c:forEach>
		
														<tr  style="background: #EEE; padding: 2px 0 2px 5px;">
															<td colspan="2">SUB-TOTAL (${ tabelaOrc.key.descricao})</td>
															<td  align="right">${ tabelaOrc.value.quantidadeTotal }</td>
															<td align="right"><fmt:formatNumber currencySymbol="R$  " value="${ tabelaOrc.value.valorTotalRubrica }" type="currency"/></td>
														</tr>
		
														<tr>
															<td colspan="5">&nbsp;</td>
														</tr>
		
												</c:forEach>
											</c:if>
		
												<c:if test="${empty projetoBase.projeto.orcamento}">
													<tr><td colspan="6" align="center"><font color="red">Não há itens de despesas cadastrados</font> </td></tr>
												</c:if>
		
										</tbody>
								</table>
						
						</td>
					</tr>	
				</c:if>
				
				
				
				
				
				
					<tr>
						<td colspan="3" class="subFormulario" style="text-align: center">Cronograma</td>
					</tr>
			
					<tr> <td colspan="3" style="margin:0; padding: 0;">
						<div style="overflow: auto; width: 100%">
						<table id="cronograma" class="subFormulario" width="100%">
						<thead>
							<tr>
								<th width="30%" rowspan="2"> Atividades </th>
								<c:forEach items="${projetoBase.telaCronograma.mesesAno}" var="ano">
								<th colspan="${fn:length(ano.value)}" style="text-align: center" class="inicioAno fimAno">
									${ano.key}
								</th>
								</c:forEach>
							</tr>
							<tr>
								<c:forEach items="${projetoBase.telaCronograma.mesesAno}" var="ano">
									<c:forEach items="${ano.value}" var="mes" varStatus="status">
									<c:set var="classeCabecalho" value=""/>
									<c:if test="${status.first}"> <c:set var="classeCabecalho" value="inicioAno"/> </c:if>
									<c:if test="${status.last}"> <c:set var="classeCabecalho" value="fimAno"/> </c:if>
			
									<th class="${classeCabecalho}" style="text-align: center"> ${mes}	</th>
									</c:forEach>
								</c:forEach>
							</tr>
						</thead>
						<tbody>
							<c:set var="numeroAtividades" value="${fn:length(projetoBase.telaCronograma.cronogramas)}" />
							<c:set var="valoresCheckboxes" value=",${fn:join(projetoBase.telaCronograma.calendario, ',')}" />
							<c:forEach begin="1" end="${numeroAtividades}" varStatus="statusAtividades">
							<tr>
								<th> ${projetoBase.telaCronograma.atividade[statusAtividades.index-1]} </th>
								<c:forEach items="${projetoBase.telaCronograma.mesesAno}" var="ano" varStatus="statusCheckboxes">
									<c:forEach items="${ano.value}" var="mes">
										<c:set var="valorCheckbox" value=",${statusAtividades.index-1}_${mes}_${ano.key}" />
										<c:set var="classeCelula" value=""/>
										<c:if test="${ fn:contains(valoresCheckboxes, valorCheckbox) }">
											<c:set var="classeCelula" value="selecionado"/>
										</c:if>
										<td align="center" class="${classeCelula}" >
											&nbsp;
										</td>
									</c:forEach>
								</c:forEach>
							</tr>
							</c:forEach>
						</tbody>
						</table>
						 </div>
						</td>
				</tr>
				
				<tr>
						<td colspan="3">
										<h3 class="subtitulo">Arquivos</h3>
										<t:dataTable value="#{projetoBase.projeto.arquivos}" var="arquivo" align="center" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar" id="tbArquivo">
												<t:column>
													<f:facet name="header">
														<f:verbatim>Descrição Arquivo</f:verbatim></f:facet>
														<h:outputText value="#{arquivo.descricao}" />
												</t:column>			
												<t:column width="5%">
													<h:commandLink title="Visualizar Arquivo"  action="#{projetoBase.viewArquivo}" immediate="true">
													        <f:param name="idArquivo" value="#{arquivo.idArquivo}"/>
												    		<h:graphicImage url="/img/view.gif" />
													</h:commandLink>
												</t:column>
										</t:dataTable>
									<c:if test="${empty projetoBase.projeto.arquivos}">
										<tr><td colspan="6" align="center"><font color="red">Não há arquivos cadastrados para esta ação</font> </td></tr>
									</c:if>										
						</td>
				</tr>	
				
				<tr>
					<td colspan="3">

						<h3 class="subtitulo">Lista de Fotos</h3>

						<t:dataTable id="dtFotos" value="#{projetoBase.projeto.fotos}" var="foto" align="center" 
							width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar">
					
							<t:column>
								<f:facet name="header"><f:verbatim>Foto</f:verbatim></f:facet>
								<h:outputLink value="/sigaa/verFoto?idFoto=#{foto.idFotoOriginal}&key=#{sf:generateArquivoKey(foto.idFotoOriginal)}" id="link_verfoto_original_" title="Click para ampliar">
									<h:graphicImage url="/verFoto?idFoto=#{foto.idFotoMini}&key=#{sf:generateArquivoKey(foto.idFotoMini)}" width="70" height="70"/>
								</h:outputLink>
							</t:column>
					
							<t:column>
								<f:facet name="header"><f:verbatim>Descrição</f:verbatim></f:facet>
								<h:outputText value="#{foto.descricao}" />
							</t:column>					
						</t:dataTable>
						
						<c:if test="${empty projetoBase.projeto.fotos}">
								<tr><td colspan="6" align="center"><font color="red">Não há fotos cadastradas para esta ação</font> </td></tr>
						</c:if>
													
					</td>
				</tr>
				
				<tr>
					<td colspan="3">

							<h3 class="subtitulo">Dimensão acadêmica da proposta</h3>
							<table class="listagem" width="100%">
								<thead>
									<tr>
										<th>Dimensão</th>
										<th>Projetos</th>
									</tr>
								</thead>
								
								<tbody>								
								<c:if test="${projetoBase.projeto.ensino}">
									<tr>
										<td><h3>Ensino</h3></td>
										<td>
											<table width="100%">
												<c:if test="${projetoBase.projeto.programaMonitoria}">
													<tr>
														<td align="left">Projeto de monitoria</td>
														<td width="15%"><h:outputText value="Pendente de envio" rendered="#{!projetoBase.projeto.programaMonitoriaSubmetido}"/></td>
														<td width="2%"><h:commandLink title="Visualizar"
															action="#{ projetoMonitoria.viewMonitoriaAssociado }"
															immediate="true"
															rendered="#{projetoBase.projeto.programaMonitoriaSubmetido}">
															<f:param name="id" value="#{projetoBase.projeto.id}" />
															<h:graphicImage value="/img/view.gif"
																style="overflow: visible;" />
														</h:commandLink></td>
														
													</tr>
												</c:if>
			
												<c:if test="${projetoBase.projeto.melhoriaQualidadeEnsino}">
													<tr>
														<td align="left">Projeto de melhoria da qualidade do ensino</td>
														<td width="15%"><h:outputText value="Pendente de envio" rendered="#{!projetoBase.projeto.melhoriaQualidadeEnsinoSubmetido}"/></td>
														<td width="2%"><h:commandLink title="Visualizar"
															action="#{ projetoMonitoria.viewMelhoriaAssociado }"
															immediate="true"
															rendered="#{projetoBase.projeto.melhoriaQualidadeEnsinoSubmetido}">
															<f:param name="id" value="#{projetoBase.projeto.id}" />
															<h:graphicImage value="/img/view.gif"
																style="overflow: visible;" />
														</h:commandLink></td>
													</tr>
												</c:if>
											</table>
										</td>
									</tr>
								</c:if>
		
								<c:if test="${projetoBase.projeto.pesquisa}">
									<tr class="linhaImpar">
										<td><h3>Pesquisa</h3></td>
										<td>
											<table width="100%">
												<c:if test="${projetoBase.projeto.apoioGrupoPesquisa}">
													<tr class="linhaImpar">
														<td align="left">Projeto de apoio a grupo de pesquisa</td>
														<td width="15%"></td>
														<td width="2%"><h:graphicImage value="/img/view.gif"
															style="overflow: visible;" rendered="false" /></td>
													</tr>
												</c:if>
												<c:if test="${projetoBase.projeto.apoioNovosPesquisadores}">
													<tr class="linhaImpar">
														<td align="left">Projeto de apoio a novos pesquisadores</td>
														<td width="15%"></td>
														<td width="2%"><h:graphicImage value="/img/view.gif"
															style="overflow: visible;" rendered="false" /></td>
													</tr>
												</c:if>
												<c:if test="${projetoBase.projeto.iniciacaoCientifica}">
													<tr class="linhaImpar">
														<td align="left">Programa institucional de bolsas de iniciação científica</td>
														<td width="15%"><h:outputText value="Pendente de envio" rendered="#{!projetoBase.projeto.iniciacaoCientificaSubmetido}"/></td>
														<td width="2%"><h:commandLink
															action="#{projetoBase.visualizarProjetoPesquisaAssociado}"
															rendered="#{projetoBase.projeto.iniciacaoCientificaSubmetido}">
															<f:param name="id" value="#{projetoBase.projeto.id}" />
															<f:param name="linkPesquisa"
																value="/pesquisa/projetoPesquisa/criarProjetoPesquisa.do?dispatch=viewProjetoBase&idProjetoBase=" />
															<h:graphicImage value="/img/view.gif"
																style="overflow: visible;" />
														</h:commandLink></td>
													</tr>
												</c:if>
											</table>
										</td>
									</tr>
								</c:if>
		
								<c:if test="${projetoBase.projeto.extensao}">
									<tr>
										<td><h3>Extensão</h3></td>
										<td>
											<table width="100%">		
												<c:if test="${projetoBase.projeto.programaExtensao}">
													<tr>
														<td align="left">Programa</td>
														<td width="15%"><h:outputText value="Pendente de envio" rendered="#{!projetoBase.projeto.programaExtensaoSubmetido}"/></td>
														<td width="2%"><h:commandLink title="Visualizar"
															action="#{ atividadeExtensao.view }" immediate="true"
															rendered="#{projetoBase.projeto.programaExtensaoSubmetido}">
															<f:param name="id" value="#{projetoBase.projeto.id}" />
															<f:param name="ACAO_ASSOCIADA" value="true" />
															<f:param name="print" value="true" />
															<f:param name="TIPO_ACAO" value="1" /> <%-- PROGRAMA --%>
															<h:graphicImage value="/img/view.gif"
																style="overflow: visible;" />
														</h:commandLink></td>
													</tr>
												</c:if>
												<c:if test="${projetoBase.projeto.projetoExtensao}">
													<tr>
														<td align="left">Projeto</td>
														<td width="15%"><h:outputText value="Pendente de envio" rendered="#{!projetoBase.projeto.projetoExtensaoSubmetido}"/></td>
														<td width="2%"><h:commandLink title="Visualizar"
															action="#{ atividadeExtensao.view }" immediate="true"
															rendered="#{projetoBase.projeto.projetoExtensaoSubmetido}">
															<f:param name="id" value="#{projetoBase.projeto.id}" />
															<f:param name="ACAO_ASSOCIADA" value="true" />
															<f:param name="print" value="true" />
															<f:param name="TIPO_ACAO" value="2" /> <%-- PROJETO --%>															
															<h:graphicImage value="/img/view.gif"
																style="overflow: visible;" />
														</h:commandLink></td>
													</tr>
												</c:if>
												<c:if test="${projetoBase.projeto.cursoExtensao}">
													<tr>
														<td align="left">Curso</td>
														<td width="15%"><h:outputText value="Pendente de envio" rendered="#{!projetoBase.projeto.cursoExtensaoSubmetido}"/></td>
														<td width="2%"><h:commandLink title="Visualizar"
															action="#{ atividadeExtensao.view }" immediate="true"
															rendered="#{projetoBase.projeto.cursoExtensaoSubmetido}">
															<f:param name="id" value="#{projetoBase.projeto.id}" />
															<f:param name="ACAO_ASSOCIADA" value="true" />
															<f:param name="print" value="true" />
															<f:param name="TIPO_ACAO" value="3" /> <%-- CURSO --%>
															<h:graphicImage value="/img/view.gif"
																style="overflow: visible;" />
														</h:commandLink></td>
													</tr>
												</c:if>
			
												<c:if test="${projetoBase.projeto.eventoExtensao}">
													<tr>
														<td align="left">Evento</td>
														<td width="15%"><h:outputText value="Pendente de envio" rendered="#{!projetoBase.projeto.eventoExtensaoSubmetido}"/></td>
														<td width="2%"><h:commandLink title="Visualizar"
															action="#{ atividadeExtensao.view }" immediate="true"
															rendered="#{projetoBase.projeto.eventoExtensaoSubmetido}">
															<f:param name="id" value="#{projetoBase.projeto.id}" />
															<f:param name="ACAO_ASSOCIADA" value="true" />
															<f:param name="print" value="true" />
															<f:param name="TIPO_ACAO" value="4" /> <%-- EVENTO --%>
															<h:graphicImage value="/img/view.gif"
																style="overflow: visible;" />
														</h:commandLink></td>
													</tr>
												</c:if>
											</table>
										</td>
									</tr>
								</c:if>
								</tbody>
							</table>
					</td>
				</tr>

	
	</tbody>
	
	<tfoot>
				<tr>
					
					<td colspan="3">
						<h:commandButton value="Executar Projeto" action="#{projetoBase.executarProjeto }" id="btConfirmarExecucao"/>
						<h:commandButton value="Não Executar Projeto" action="#{projetoBase.naoExecutarProjeto }" id="btConfirmarNaoExecucao"/>
						<h:commandButton value="Cancelar" action="#{projetoBase.cancelar}" onclick="#{confirm}" id="btCancelar"/>
					</td>							
				</tr>
	</tfoot>
	
	
</table>

</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>