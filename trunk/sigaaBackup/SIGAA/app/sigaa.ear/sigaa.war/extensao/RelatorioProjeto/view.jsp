<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<style>
.scrollbar {
  	margin-left: 155px;
  	
	width: 98%;
	overflow:auto;
}
</style>


<f:view>
	<h2>Visualiza��o de Relat�rio</h2>

	<h:form id="formRelatorioProjetos">
		
		<h3 class="tituloTabelaRelatorio"> RELAT�RIO DE PROJETOS DE EXTENS�O </h3>
		<table class="tabelaRelatorio" width="100%">

		<tr>
			<th><b> C�digo:</b></th>
			<td style="width: 900%">
					<h:outputText	value="#{relatorioAcaoExtensao.obj.atividade.codigo}"/>
			</td>
		</tr>

		<tr>
			<th><b> T�tulo:</b></th>
			<td><h:outputText	value="#{relatorioAcaoExtensao.obj.atividade.titulo}"/></td>
		</tr>
		
		<tr>
			<th>Tipo de a��o:</th>
			<td><h:outputText value="#{relatorioAcaoExtensao.obj.atividade.tipoAtividadeExtensao.descricao}"/></td>
		</tr>
		
		<tr>
			<th><b> �rea Tem�tica:</b></th>
			<td>
					<h:outputText	value="#{relatorioAcaoExtensao.obj.atividade.areaTematicaPrincipal.descricao}"/>
			</td>
		</tr>

		<tr>
			<th><b> Coordenador(a):</b></th>
			<td>
					<h:outputText	value="#{relatorioAcaoExtensao.obj.atividade.coordenacao.pessoa.nome}"/> 
					<c:if test="${relatorioAcaoExtensao.obj.atividade.coordenacao.pessoa.email != null }">
						<h:outputText	value=" - #{relatorioAcaoExtensao.obj.atividade.coordenacao.pessoa.email}"/>
					</c:if>
			</td>
		</tr>


		<tr>
			<th><b> Tipo de Relat�rio:</b></th>
			<td>
					<h:outputText	value="#{relatorioAcaoExtensao.obj.tipoRelatorio.descricao}"/>
			</td>
		</tr>
		
		<tr>
			<th><b> Per�odo da A��o:</b></th>
			<td>
				<fmt:formatDate value="${relatorioAcaoExtensao.obj.atividade.projeto.dataInicio}" pattern="dd/MM/yyyy" />  
				a 
				<fmt:formatDate value="${relatorioAcaoExtensao.obj.atividade.projeto.dataFim}" pattern="dd/MM/yyyy" />
				</td>
		</tr>
		
		<tr>
			<th><b> P�blico Estimado:</b></th>
			<td>
				<h:outputText value="#{relatorioAcaoExtensao.obj.atividade.publicoEstimado}" id="publicoEstimado"/> pessoas
			</td>	
		</tr>
		
		<tr>
			<th><b> P�blico Real Atingido:</b></th>
			<td>
				<h:outputText value="#{relatorioAcaoExtensao.obj.publicoRealAtingido}" id="publicoRealAtingido"/> pessoas
			</td>	
		</tr>

		<tr>
			<th>Situa��o do Relat�rio:</th>
			<td>
				<h:outputText value="Enviado em " rendered="#{relatorioAcaoExtensao.obj.dataEnvio != null}" />
				<fmt:formatDate value="${relatorioAcaoExtensao.obj.dataEnvio}" pattern="dd/MM/yyyy HH:mm:ss" />
				<h:outputText value="<font color=red>CADASTRO EM ANDAMENTO<font>" escape="false" 
						rendered="#{relatorioAcaoExtensao.obj.dataEnvio == null}" />
			</td>
		</tr>
		
		<tr>
			<th><b> Financiamento Interno:</b></th>
			<td>
					<c:if test="${relatorioAcaoExtensao.obj.atividade.financiamentoExterno}">
						<h:outputText	value="N�O"/>
					</c:if>
					<c:if test="${!relatorioAcaoExtensao.obj.atividade.financiamentoExterno}">
						<h:outputText	value="SIM"/>
					</c:if>
					
			</td>
		</tr>
		
		<tr>
			<th>Esta a��o foi realizada:</th>
			<td><h:outputText value="#{relatorioAcaoExtensao.obj.acaoRealizada ? 'SIM' : (relatorioAcaoExtensao.obj.acaoRealizada == null ? '-' : 'N�O')}" id="acaoFoiRealizada" /></td>	
		</tr>
	
	
		<c:if test="${ not relatorioAcaoExtensao.exibeNovoRelatorioFinalExtensao }">
			<tr>
				<td colspan="2" class="subFormulario"> Detalhamento das atividades desenvolvidas:</td>
			</tr>	
	
			<c:if test="${relatorioAcaoExtensao.obj.acaoRealizada != null && !relatorioAcaoExtensao.obj.acaoRealizada }">
				<tr>
					<td colspan="2" style="text-align: justify;"><b>Motivo da n�o realiza��o desta a��o:</b><br />
						<h:outputText value="#{relatorioAcaoExtensao.obj.motivoAcaoNaoRealizada}" id="motivoNaoRealizacao" />
					</td>	
				</tr>
			</c:if>
	
	
			<tr>
				<td colspan="2" style="text-align: justify;"> <b>Existe rela��o objetiva entre a proposta pedag�gica do curso e a proposta do projeto de extens�o? Justifique:</b><br/>
					<h:outputText value="#{relatorioAcaoExtensao.obj.relacaoPropostaCurso}" id="relacaoPropostaCurso"/>
				</td>	
			</tr>
			
			<tr>	
				<td colspan="2"><b>Outras a��es realizadas vinculadas ao projeto:</b><br/>
					<t:dataList value="#{relatorioAcaoExtensao.obj.outrasAcoesProjeto}" var="acao">
							<t:column>
									<h:outputText value="#{acao.descricao}, " escape="false" />
							</t:column>
					 </t:dataList>
				</td>
			</tr>
			
			<tr>	
				<td colspan="2"><b>Apresenta��o do projeto em eventos de extens�o:</b><br/>
					<t:dataList value="#{ relatorioAcaoExtensao.obj.apresentacaoProjetos }" var="apresentacao">
							<t:column>
											<h:outputText value="#{apresentacao.descricao}, " escape="false" />
							</t:column>
					</t:dataList>
				</td>
			</tr>
			
			<tr>	
				<td colspan="2"><b> Produ��o acad�mica gerada:</b><br/>
						<t:dataList value="#{ relatorioAcaoExtensao.obj.producoesGeradas }" var="prod">
								<t:column>
										<h:outputText value="#{prod.descricao}, " escape="false"/>
								</t:column>
						 </t:dataList>
				</td>
			</tr>
	
			<c:choose>
			
				<c:when test="${ relatorioAcaoExtensao.novoFormulario }">

					<tr>
						<td colspan="2"><b> Apresenta��o em Eventos Cient�ficos: </b>
							<h:outputText value="#{relatorioAcaoExtensao.obj.apresentacaoEventoCientifico}"/> apresenta��es.
						</td>	
					</tr>
			
					<tr>
						<td colspan="2" style="text-align: justify;"><b> Resumo sobre a apresenta��o: </b> <br />
							<p style="padding-left: 15px;"><h:outputText value="#{relatorioAcaoExtensao.obj.observacaoApresentacao}"/></p>
						</td>	
					</tr>
			
					<tr>
						<td colspan="2"><b> Artigos Cient�ficos produzidos a partir da a��o de extens�o: </b>
							<h:outputText value="#{relatorioAcaoExtensao.obj.artigosEventoCientifico}" /> artigos
						</td>	
					</tr>
			
					<tr>
						<td colspan="2" style="text-align: justify;"><b> Resumo sobre o Artigo: </b> <br />
							<p style="padding-left: 15px;"><h:outputText value="#{relatorioAcaoExtensao.obj.observacaoArtigo}" /></p>
						</td>	
					</tr>
			
					<tr>
						<td colspan="2" ><b> Outras produ��es geradas a partir da a��o de Extens�o: </b>
							<h:outputText value="#{relatorioAcaoExtensao.obj.producoesCientifico}" /> produ��es
						</td>	
					</tr>
			
					<tr>
						<td colspan="2" style="text-align: justify;"><b> Resumo sobre a Produ��o: </b> <br />
							<p style="padding-left: 15px;"><h:outputText value="#{relatorioAcaoExtensao.obj.observacaoProducao}" /></p>
						</td>	
					</tr>
			
					<tr>
						<td colspan="2" class="subFormulario">Informa��es do Projeto</td>
					</tr>
			
					<tr>
						<td colspan="2" style="text-align: justify;"><b> Dificuldades Encontradas: </b> <br />
							<p style="padding-left: 15px;"><h:outputText value="#{relatorioAcaoExtensao.obj.dificuldadesEncontradas}" /></p>
						</td>	
					</tr>
			
					<tr>
						<td colspan="2" style="text-align: justify;"><b> Observa��es Gerais: </b> <br />
							<p style="padding-left: 15px;"><h:outputText value="#{relatorioAcaoExtensao.obj.observacoesGerais}" /></p>
						</td>	
					</tr>
								
				</c:when>
				<c:otherwise>
					<tr>
						<td colspan="2" style="text-align: justify;"><b>Atividades Realizadas:</b><br />
							<h:outputText value="#{relatorioAcaoExtensao.obj.atividadesRealizadas}" id="atividades" />
						</td>	
					</tr>
		
					<tr>
						<td colspan="2" style="text-align: justify;"><b> Resultados Obtidos: Qualitativos.</b><br />
							<h:outputText  value="#{relatorioAcaoExtensao.obj.resultadosQualitativos}" id="qualitativos" />
						</td>	
					</tr>
		
					<tr>
						<td colspan="2" style="text-align: justify;"><b> Resultados Obtidos: Quantitativos.</b><br />
							<h:outputText value="#{relatorioAcaoExtensao.obj.resultadosQuantitativos}" id="quantitativos" />
						</td>	
					</tr>
		
					<tr>
						<td colspan="2" style="text-align: justify;"><b> Dificuldades Encontradas:</b><br />
							<h:outputText value="#{relatorioAcaoExtensao.obj.dificuldadesEncontradas}" id="dificuldades" />
						</td>	
					</tr>
				
					<tr>
						<td colspan="2" style="text-align: justify;"><b> Ajustes Realizados Durante a Execu��o da A��o:</b><br />
							<h:outputText value="#{relatorioAcaoExtensao.obj.ajustesDuranteExecucao}" id="ajustes" />
						</td>	
					</tr>
				</c:otherwise>
			</c:choose>
			
			<tr>
				<td  colspan="2"></td>
			</tr>
		
		</c:if>
		
		<tr>
			<td colspan="2" style="font-weight: bold; background-color: #DEDFE3"> Membros da Equipe </td>
		</tr>	
		
		
		<c:if test="${not empty relatorioAcaoExtensao.obj.atividade.membrosEquipe}">
					<tr>
						<td colspan="2">
				
									<t:dataTable value="#{relatorioAcaoExtensao.obj.atividade.membrosEquipe}" var="membro" 
										align="center" width="100%" styleClass="tabelaRelatorio" rowClasses="linhaPar, linhaImpar" id="tbEquipe">
												<t:column>
													<f:facet name="header"><f:verbatim>Nome</f:verbatim></f:facet>
													<h:outputText value="#{membro.pessoa.nome}" />
												</t:column>
							
												<t:column>
													<f:facet name="header"><f:verbatim>Categoria</f:verbatim></f:facet>
													<h:outputText value="#{membro.categoriaMembro.descricao}" />
												</t:column>										
												
												<t:column>
													<f:facet name="header"><f:verbatim>Fun��o</f:verbatim></f:facet>
													<h:outputText value="<font color='red'>" rendered="#{membro.funcaoMembro.id == COORDENADOR}"  escape="false"/>
																	<h:outputText value="#{membro.funcaoMembro.descricao}" rendered="#{not empty membro.pessoa}" />
													<h:outputText value="</font>" rendered="#{membro.funcaoMembro.id == COORDENADOR}"  escape="false"/>
												</t:column>										
												
												<t:column>
													<f:facet name="header"><f:verbatim>Departamento</f:verbatim></f:facet>
													<h:outputText value="#{membro.servidor.unidade.sigla}" rendered="#{not empty membro.servidor}" />
												</t:column>
												<t:column>
													<f:facet name="header">
														<f:verbatim>In�cio</f:verbatim>
													</f:facet>
													<h:outputText value="#{membro.dataInicio}" />
												</t:column>
					
												<t:column>
													<f:facet name="header">
														<f:verbatim>Fim</f:verbatim>
													</f:facet>
													<h:outputText value="#{membro.dataFim}" />
												</t:column>											
												
									</t:dataTable>
						</td>
				</tr>
		</c:if>
		
		<tr>
			<td  colspan="2"></td>
		</tr>
		
		<c:if test="${ not relatorioAcaoExtensao.exibeNovoRelatorioFinalExtensao }">
			<tr>
				<td colspan="2" style="font-weight: bold; background-color: #DEDFE3">Lista de Arquivos</td>
			</tr>
			
			<tr>
				<td colspan="2" >
					
					<c:forEach  items="${relatorioAcaoExtensao.obj.arquivos}" var="anexo" >
						<tr width="100%">
							<td width="97%">
								${anexo.descricao}
							</td>
							<td width="3%" style="text-align: right;">
							<a href="/shared/verArquivo?idArquivo=${anexo.idArquivo}&key=${ sf:generateArquivoKey(anexo.idArquivo) }" target="_blank"><img  src="${ctx}/img/view.gif"/></a>
							</td>
						</tr>
						
					</c:forEach>
					
				</td>
			</tr>
		
			<c:if test="${empty relatorioAcaoExtensao.obj.arquivos}">
					<tr><td colspan="6" align="center"><font color="red">N�o h� arquivos adicionados ao relat�rio</font> </td></tr>
			</c:if>
		</c:if>
		
		<tr>
			<td  colspan="2"></td>
		</tr>
		
		<c:if test="${ not relatorioAcaoExtensao.exibeNovoRelatorioFinalExtensao }">
			<tr>
				<td colspan="2" style="font-weight: bold; background-color: #DEDFE3"> Detalhamento de utiliza��o dos recursos financeiros </td>
			</tr>	

			<tr>
				<td colspan="2">
					<table class="listagem">
						<thead>
							<tr>
								<td>
									<c:if test="${not empty relatorioAcaoExtensao.obj.detalhamentoRecursos}">
													<t:dataTable id="dt" value="#{relatorioAcaoExtensao.obj.detalhamentoRecursos}" var="consolidacao"
													 align="center" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar" rowIndexVar="index" 
													 forceIdIndex="true">
																<t:column>
																	<f:facet name="header"><f:verbatim>Descri��o</f:verbatim></f:facet>
																	<h:outputText value="#{consolidacao.elemento.descricao}" />
																</t:column>

																<t:column>
																	<f:facet name="header"><f:verbatim>FAEx (Interno)</f:verbatim></f:facet>
																	<f:verbatim>R$ </f:verbatim>
																	<h:outputText value="#{consolidacao.faex}"/>
																</t:column>

																<t:column>
																	<f:facet name="header"><f:verbatim>Funpec</f:verbatim></f:facet>
																	<f:verbatim>R$ </f:verbatim>
																	<h:outputText value="#{consolidacao.funpec}"/>
																</t:column>


																<t:column>
																	<f:facet name="header"><f:verbatim>Outros (Externo)</f:verbatim></f:facet>
																	<f:verbatim>R$ </f:verbatim>
																	<h:outputText value="#{consolidacao.outros}" />
																</t:column>
												</t:dataTable>
									</c:if>
								</td>
						</tr>
					</thead>
							
					<c:if test="${empty relatorioAcaoExtensao.obj.detalhamentoRecursos}">
						<tr><td colspan="6" align="center"><font color="red">N�o h� itens de despesas cadastrados</font> </td></tr>
					</c:if>
					
					</table>
				</td>
			</tr>
		</c:if>		
		
		<tr>
			<td  colspan="2"></td>
		</tr>

		<tr>
			<td colspan="2" style="font-weight: bold; background-color: #DEDFE3">Atividades Desenvolvidas</td>
		</tr>
		
		<tr>
			<td colspan="3">
				<c:forEach items="#{relatorioAcaoExtensao.obj.andamento}" var="andamento" varStatus="st1">
						<c:if test="${ objetivoAtual != andamento.atividade.objetivo.id }">
							<tr style="font-weight: bold; background-color: #DEDFE3">
								<td align="right" width="100%" colspan="7">
									<h:outputText value="#{andamento.atividade.objetivo.objetivo}" />
								</td>
							</tr>
							<tr>
								<td colspan="3">
									<table style="width: 100%;" class="listagem">
										<tr style="font-weight: bold;">
											<td colspan="2">									
												<b>Atividades Relacionadas:</b>
											</td>
											<td style="text-align: center;">								
												<b>Per�odo Realiza��o:</b>
											</td>
											<td style="text-align: center;">									
												<b>Carga Hor�ria:</b>
											</td>
											<td style="text-align: center;">									
												<b>Andamento Objetivo:</b>
											</td>
											<td style="text-align: center;">									
												<b>Situa��o Objetivo:</b>
											</td>
											<td style="text-align: center;">									
											</td>
										</tr>
						</c:if>
		
										<tr>
											<td colspan="2" width="25%;">
												${st1.index + 1}. <h:outputText value="#{andamento.atividade.descricao}" />
											</td>
											<td width="20%;" align="center">	
												<h:outputText value="#{andamento.atividade.dataInicio}" id="dataInicioAtividade"><f:convertDateTime pattern="dd/MM/yyyy"  /></h:outputText>
													<c:if test="${not empty andamento.atividade.dataFim}">
														&nbsp; a &nbsp; 
													</c:if> 
												<h:outputText value="#{andamento.atividade.dataFim}" id="dataFimAtividade"><f:convertDateTime pattern="dd/MM/yyyy"  /></h:outputText>						
											</td>
											<td style="text-align: center;" width="10%">
												<h:outputText value="#{ andamento.atividade.cargaHoraria }" /> h
											</td>
											<td style="text-align: center;" width="10%">
												${ andamento.andamentoAtividade } %
											</td>
											<td style="text-align: center;" width="10%">
												${ andamento.descricaoSituacao }
											</td>
											<td style="text-align: center;" width="3%">
												<h:graphicImage value="/img/extensao/andamento.png" rendered="#{ andamento.andamento }" title="Em Andamento"/>
												<h:graphicImage value="/img/extensao/yellow.png" rendered="#{ andamento.atrasada }" title="Atrasada"/>
												<h:graphicImage value="/img/extensao/green.png" rendered="#{ andamento.concluida }" title="Conclu�da"/> 
												<h:graphicImage value="/img/extensao/red.png" rendered="#{ andamento.cancelada }" title="Cancelada"/>
											</td>
										</tr>
						
						<c:set value="#{ andamento.atividade.objetivo.id }" var="objetivoAtual" />
						<c:set var="proximo" value="${relatorioAcaoExtensao.obj.andamento[st1.index + 1].atividade.objetivo.id}" />
						<c:if test="${ objetivoAtual != proximo }">
									
										<tr>
											<td colspan="7"><b> Digite um breve relato sobre a execu��o do objetivo</b> <br />
												<h:outputText style="width:99%" value="#{ andamento.atividade.objetivo.observacaoExecucao }"/>
											</td>	
										</tr>
									</table>
								</td>
							</tr>
						</c:if>						
				</c:forEach>
			</td>
		</tr>

		<tr>
			<td  colspan="2"></td>
		</tr>
		
		<tr>
			<td colspan="2" style="font-weight: bold; background-color: #DEDFE3">Valida��o do Departamento</td>
		</tr>

		<tr>
				<th width="20%"><b>Departamento:</b></th>
				<td>	
					<h:outputText value="#{relatorioAcaoExtensao.obj.atividade.unidade.nome}"/>
				</td>
		</tr>		


		<tr>
				<th><b>Avaliador(a):</b></th>
				<td>	
					<h:outputText value="#{relatorioAcaoExtensao.obj.registroEntradaDepartamento.usuario.nome}"/>
				</td>
		</tr>		


		<tr>
				<th><b>Data An�lise:</b></th>
				<td>	
					<fmt:formatDate value="${relatorioAcaoExtensao.obj.dataValidacaoDepartamento}" pattern="dd/MM/yyyy HH:mm:ss"/>
				</td>
		</tr>		


		<tr>
				<th><b>Parecer Depto.:</b></th>
				<td>	
					<h:outputText id="tipoParecerDepto"	value="#{relatorioAcaoExtensao.obj.tipoParecerDepartamento != null ? relatorioAcaoExtensao.obj.tipoParecerDepartamento.descricao : 'N�O ANALISADO' }"/>
				</td>
		</tr>		
		<tr>			
				<th><b>Justificativa: </b></th>
				<td style="text-align: justify;">
					<h:outputText  id="parecerDepartamento" value="#{relatorioAcaoExtensao.obj.parecerDepartamento}"/>
				</td>
		</tr>


		<tr>
			<td  colspan="2"></td>
		</tr>

		<tr>
				<td colspan="2" style="font-weight: bold; background-color: #DEDFE3">Valida��o da Proex</td>
		</tr>
		
		<tr>
				<th><b>Data An�lise:</b></th>
				<td>	
					<fmt:formatDate value="${relatorioAcaoExtensao.obj.dataValidacaoProex}" pattern="dd/MM/yyyy HH:mm:ss"/>
				</td>
		</tr>		


		<tr>
				<th><b>Avaliador(a):</b></th>
				<td>	
					<h:outputText value="#{relatorioAcaoExtensao.obj.registroEntradaProex.usuario.nome}"/>
				</td>
		</tr>		


		<tr>
				<th><b>Parecer PROEx:</b></th>
				<td>	
					<h:outputText id="tipoParecerProex"	value="#{relatorioAcaoExtensao.obj.tipoParecerProex != null ? relatorioAcaoExtensao.obj.tipoParecerProex.descricao : 'N�O ANALISADO' }"/>
				</td>
		</tr>		
		<tr>			
				<th><b>Justificativa:</b></th>
				<td style="text-align: justify;">
					<h:outputText  id="parecerProex" value="#{relatorioAcaoExtensao.obj.parecerProex}"/>
				</td>
		</tr>
	</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>