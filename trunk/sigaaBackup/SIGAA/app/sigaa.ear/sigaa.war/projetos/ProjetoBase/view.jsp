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
	<h2><ufrn:subSistema /> > Visualização da Ação Acadêmica Integrada</h2>
<h:form>

	<table class="formulario" width="100%" >
	<caption> DADOS DA AÇÃO ACADÊMICA INTEGRADA</caption>
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
			<td class="titulo"  colspan="2"><b>Unidade Orçamentária</b></td>
		</tr>

		<tr>
			<td><h:outputText value="#{projetoBase.projeto.unidade.siglaNome}"/></td>
			<td><h:outputText value="#{projetoBase.projeto.unidadeOrcamentaria.siglaNome != null ? projetoBase.projeto.unidadeOrcamentaria.siglaNome : '<font color=\"red\">Não definida</font>'}" escape="false"/></td>
		</tr>
	
		<tr>
			<td class="titulo"><b> Área do CNPq:</b> </td>
			<td class="titulo" colspan="2"><b> Abrangência: </b></td>
		</tr>
	
		<tr>
			<td><h:outputText value="#{projetoBase.projeto.areaConhecimentoCnpq.nome}"/></td>
			<td  colspan="2"><h:outputText value="#{projetoBase.projeto.abrangencia.descricao}"/></td>
		</tr>

		<tr>
			<td class="titulo"><b> Bolsas Solicitadas:</b> </td>
			<td class="titulo" colspan="2"><b> Bolsas Concedidas: </b></td>
		</tr>
	
		<tr>
			<td><h:outputText value="#{projetoBase.projeto.bolsasSolicitadas}"/></td>
			<td  colspan="2"><h:outputText value="#{projetoBase.projeto.bolsasConcedidas}"/></td>
		</tr>
		
		<tr>
			<td class="titulo"><b> Situação: </b>	</td>
			<td class="titulo" colspan="2"><b> Fonte de Financiamento: </b></td>
		</tr>

		<tr>
			<td><h:outputText value="#{projetoBase.projeto.situacaoProjeto.descricao}"/> </td>
			<td colspan="2"><h:outputText value="#{projetoBase.projeto.fonteFinanciamentoString}"/></td>
		</tr>
		
		<tr>
	        <td class="titulo"><b>Dimensão acadêmica da proposta:</b></td>
			<td class="titulo"  colspan="2"><b> Convênio: </b></td>
	    </tr>
		<tr>
	        <td>
	             <h:outputText value="Ensino e Pesquisa" rendered="#{(projetoBase.projeto.ensino && projetoBase.projeto.pesquisa && !projetoBase.projeto.extensao)}"/> 
	             <h:outputText value="Ensino e Extensão" rendered="#{(projetoBase.projeto.ensino && projetoBase.projeto.extensao && !projetoBase.projeto.pesquisa)}"/>
	             <h:outputText value="Pesquisa e Extensão" rendered="#{(projetoBase.projeto.extensao && projetoBase.projeto.pesquisa && !projetoBase.projeto.ensino)}"/> 
	             <h:outputText value="Ensino, Pesquisa e Extensão" rendered="#{(projetoBase.projeto.extensao && projetoBase.projeto.pesquisa && projetoBase.projeto.ensino)}"/>
	        </td>
	        <td colspan="2"><h:outputText value="#{projetoBase.projeto.convenio ? 'SIM' : 'NÃO'}"/></td>
        </tr>
	
		<tr>
			<td colspan="3" class="subFormulario">Contato</td>
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
			<b> Resumo:  </b><br/>
			<h:outputText value="#{projetoBase.projeto.resumo}" escape="false"/> </td>
		</tr>
		
		<tr>
			<td colspan="3" align="justify"> 
				<b> Justificativa: </b><br/>
				<h:outputText value="#{projetoBase.projeto.justificativa}" escape="false"/> 
			</td>
		</tr>
		
		<tr>
			<td colspan="3" align="justify">
				<b>Objetivos: <br/></b>
				<h:outputText value="#{projetoBase.projeto.objetivos}" escape="false"/>			
			</td>
		</tr>
		
		<tr>
			<td colspan="3" align="justify">
				<b>Resultados: <br/></b>
				<h:outputText value="#{projetoBase.projeto.resultados}" escape="false"/>
			</td>
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
				<td colspan="6" class="subFormulario">Membros da Equipe</td>
		</tr>
		<tr>
				<td colspan="7">
							<t:dataTable value="#{projetoBase.projeto.equipe}" var="membro" rowClasses="linhaPar, linhaImpar"  
								align="center" width="100%"  id="tbEquipe" rendered="#{not empty projetoBase.projeto.equipe}">
										<t:column>
											<f:facet name="header"><f:verbatim>Nome</f:verbatim></f:facet>
											<h:outputText value="#{membro.pessoa.nome}" />
										</t:column>
										
										<t:column>
											<f:facet name="header"><f:verbatim>Função</f:verbatim></f:facet>
											<h:outputText value="<font color=#{membro.coordenadorProjeto ? 'red' : 'black'}>" escape="false"/>
															<h:outputText value="#{membro.funcaoMembro.descricao}" rendered="#{not empty membro.pessoa}" />
											<h:outputText value="</font>" escape="false"/>
										</t:column>										
										
										<t:column>
											<f:facet name="header"><f:verbatim>Departamento</f:verbatim></f:facet>
											<h:outputText value="#{membro.servidor.unidade.sigla}" rendered="#{not empty membro.servidor}" />
										</t:column>
										
										<t:column style="text-align: center">
											<f:facet name="header"><f:verbatim><center>Início</center></f:verbatim></f:facet>
											<h:outputText value="#{membro.dataInicio}"/>
										</t:column>

										<t:column style="text-align: center">
											<f:facet name="header"><f:verbatim><center>Fim</center></f:verbatim></f:facet>
											<h:outputText value="#{membro.dataFim}"/>
										</t:column>
										
							</t:dataTable>
 						<h:outputText value="<center><font color='red'>Membros da equipe ainda não foram cadastrados.</font></center>" rendered="#{(empty projetoBase.projeto.equipe)}" escape="false"/>
				</td>
		</tr>
		<tr>
			<td colspan="6" class="subFormulario">
			<div class="infoAltRem">
 					<h:graphicImage value="/img/report.png"style="overflow: visible;" styleClass="noborder"/>: Visualizar	Plano de Trabalho    
			</div>
			Discentes com Planos de Trabalho</td>
		</tr>
		<tr>
			<td colspan="7">
				<table class="listagem" width="100%" id="tbDis">
							<thead>
								<tr>
									<th style="text-align: left">Matrícula</th>
									<th style="text-align: left">Nome</th>
									<th style="text-align: left">Vínculo</th>
									<th style="text-align: left">Situação</th>
									<th style="text-align: center">Início</th>
									<th style="text-align: center">Fim</th>
									<th></th>
								</tr>
							</thead>

							<tbody>
								<c:forEach items="#{projetoBase.projeto.discentesProjeto}" var="dp" 
										varStatus="status">
									<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
										<td><h:outputText value="#{dp.discente.matricula}" /> </td>
										<td><h:outputText value="#{dp.discente.nome}" /></td>
										<td><h:outputText value="#{dp.tipoVinculo.descricao}" /></td>
										<td><h:outputText value="#{dp.situacaoDiscenteProjeto.descricao}" /></td>
										<td style="text-align: center"><h:outputText value="#{dp.dataInicio}" /></td>
										<td style="text-align: center"><h:outputText value="#{dp.dataFim}" /></td>
										<td width="2%" >
											<h:commandLink action="#{planoTrabalhoProjeto.view}" style="border: 0;" title="Visualizar Plano de Trabalho" rendered="#{not empty dp.planoTrabalhoProjeto}">
							       				<f:param name="id" value="#{dp.id}"/>
					               				<h:graphicImage url="/img/report.png" />
											</h:commandLink>
									</td>
									</tr>
								</c:forEach>
						</tbody>
				</table>
								
				<h:outputText value="<center><font color='red'>Discentes não informados</font></center>"
						rendered="#{empty projetoBase.projeto.discentes}" escape="false" />
			</td>
		</tr>
		 
			<!-- ORÇAMENTO DETALHADO -->
				<c:if test="${not empty projetoBase.projeto.orcamento}">
					<tr>
						<td colspan="6" class="subFormulario">Orçamento Detalhado</td>
					</tr>
					<tr>
						<td colspan="7">		
									<table class="listagem" width="100%">
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
		
											<c:if test="${empty projetoBase.tabelaOrcamentaria}">
												<tr><td colspan="6" align="center"><font color="red">Não há itens de despesas cadastrados</font> </td></tr>
											</c:if>
		
										</tbody>
								</table>
						
						</td>
					</tr>	
				</c:if>
				
				<tr>
					<td colspan="6" class="subFormulario">Consolidação do Orçamento Solicitado</td>
				</tr>
				<tr>
					<td colspan="6">
						<table class="listagem" width="100%" id="dt">
							<thead>
								<tr>
									<th style="text-align: left">Descrição</th>
									<th>FAEx (Interno)</th>
									<th>Funpec</th>
									<th>Outros (Externo)</th>
									<th>Total Rubrica</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach items="#{projetoBase.projeto.orcamentoConsolidado}" 
										var="consolidacao" varStatus="status">
									<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
										<td><h:outputText value="#{consolidacao.elementoDespesa.descricao}" /></td>
										<td align="right">
											<h:outputText value="#{consolidacao.fundo}">
												<f:convertNumber pattern="R$ #,##0.00" />
											</h:outputText>
										</td>
										<td align="right">
											<h:outputText value="#{consolidacao.fundacao}">
												<f:convertNumber pattern="R$ #,##0.00" />
											</h:outputText>
										</td>
										<td align="right">
											<h:outputText value="#{consolidacao.outros}">
												<f:convertNumber pattern="R$ #,##0.00" />
											</h:outputText>
										</td>
										<td align="right">
											<h:outputText value="#{consolidacao.totalConsolidado}">
												<f:convertNumber pattern="R$ #,##0.00" />
											</h:outputText>
										</td>
									</tr>
								</c:forEach>
								<c:if test="${empty projetoBase.projeto.orcamentoConsolidado}">
                                       <tr>
                                           <td colspan="6" align="center"><font color="red">Não há itens de despesas cadastrados</font></td>
                                       </tr>
                                   </c:if>									
							</tbody>
						</table>
					</td>
				</tr>				
				
				<tr>
					<td colspan="6" class="subFormulario">Cronograma</td>
				</tr>
			
				<tr> 
					<td colspan="7" style="margin:0; padding: 0;">
						<div style="overflow: auto; width: 100%">
						<table id="cronograma" class="listagem" width="100%">
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
					<td colspan="6" class="subFormulario">
						<div class="infoAltRem">
	   						<h:graphicImage value="/img/view.gif"style="overflow: visible;" styleClass="noborder"/>: Visualizar	    
						</div>
						Arquivos
					</td>
				</tr>
				<tr>
					<td colspan="7">
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
					<td colspan="6" class="subFormulario">Lista de Fotos</td>
				</tr>
				<tr>
					<td colspan="7">
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
					<td colspan="6" class="subFormulario">Dimensões acadêmicas da proposta</td>
				</tr>
				<tr>
					<td colspan="7">
							<table class="listagem" width="100%">
								<thead>
									<tr>
										<th>Dimensão</th>
										<th></th>
									</tr>
								</thead>
								
								<tbody>								
								<c:if test="${projetoBase.projeto.ensino}">
									<tr>
										<td>Ensino</td>
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
										<td>Pesquisa</td>
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
														<td width="2%">
															<html:link
																action="/pesquisa/projetoPesquisa/criarProjetoPesquisa.do?dispatch=viewProjetoBase&idProjetoBase=${projetoBase.projeto.id}">
																<img src="${ctx}/img/view.gif"
																title="Visualizar"
																alt="Visualizar Projeto de Pesquisa" />
															</html:link>
														</td>
											</tr>
												</c:if>
											</table>
										</td>
									</tr>
								</c:if>
		
								<c:if test="${projetoBase.projeto.extensao}">
									<tr>
										<td>Extensão</td>
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
				
				<c:if test="${not empty projetoBase.projeto.autorizacoesDepartamentos}">
					<tr>
	                    <td colspan="6" class="subtitulo">Lista de Departamentos Envolvidos na Autorização da Proposta</td>
	                </tr>
	                <tr>
	                    <td colspan="6">
	                        <c:set var="flag" value="${true}" />
	                        <table class="listagem" width="100%" id="tbAutorizacao">
	                            <thead>
	                                <tr>
	                                    <th style="text-align: left">Autorização</th>
	                                    <th style="text-align: center">Data/Hora Análise</th>
	                                    <th style="text-align: center">Autorizado</th>
	                                </tr>
	                            </thead>
	                            <tbody>
	                                <c:forEach items="#{projetoBase.projeto.autorizacoesDepartamentos}" var="autorizacao">
	                                    <c:if test="${autorizacao.ativo}">
	                                        <tr class="${flag ? 'linhaPar' : 'linhaImpar'}">
	                                            <c:set var="flag" value="${flag ? false : true}" />
	                                            <td><h:outputText value="#{autorizacao.unidade.nome}" /></td>
	                                            <td align="center">
	                                                <h:outputText value="#{autorizacao.dataAutorizacao}">
	                                                    <f:convertDateTime pattern="dd/MM/yyyy HH:mm:ss" />
	                                                </h:outputText>
	                                            </td>
	                                            <td align="center"><h:outputText value="#{(autorizacao.autorizado == null) 
	                                                        ? 'NÃO ANALISADO' :(autorizacao.autorizado ? 'SIM': 'NÃO')}" /></td>
	                                        </tr>
	                                    </c:if>
	                                </c:forEach>
	                            </tbody>
	                        </table>
	                        
	                    </td>
	                </tr>
                </c:if>
				
				<tr>
					<td colspan="6" class="subFormulario">Histórico do Projeto</td>
				</tr>
				<tr>
					<td colspan="7">
						<table class="listagem" width="100%">
							<thead>
								<tr>
									<th style="text-align: center">Data</th>
									<th style="text-align: left">Situação</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach items="#{projetoBase.projeto.historicoSituacao}" var="historico" varStatus="status">
									<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
										
										<td style="text-align: center">
											<h:outputText value="#{historico.data}">
												<f:convertDateTime pattern="dd/MM/yyyy HH:mm:ss"/>
											</h:outputText>
										</td>
										<td><h:outputText value="#{historico.projeto.situacaoProjeto.descricao}" /></td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					
						<c:if test="${empty projetoBase.projeto.historicoSituacao}">
							<tr>
								<td colspan="6" align="center"><font color="red">Não há registros sobre o histórico do projeto</font></td>
							</tr>
						</c:if>
					</td>
				</tr>
	
	</tbody>
	
	<tfoot>
			<tr>
				<td colspan="3">
					<center><input type="button" value="<< Voltar"	onclick="javascript:history.go(-1)" /></center>
				</td>
			</tr>
	</tfoot>
	
	
</table>

</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>