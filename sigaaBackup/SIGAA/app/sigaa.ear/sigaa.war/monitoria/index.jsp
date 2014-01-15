<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%> 
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>

<c:set var="hideSubsistema" value="true" />
<f:view>
	<h:form>
	<h2>Menu Monitoria</h2>

	<input type="hidden" name="aba" id="aba"/>
				<div id="operacoes-subsistema" class="reduzido" >
							<div id="projetos" class="aba">
								<ul>
								
									<li>Consultar Projetos
										<ul>
											<li><a href="${ctx}/monitoria/ProjetoMonitoria/consultar_projeto.jsf?aba=projetos">Consultar Projetos</a></li>
											<li><a href="${ctx}/monitoria/RelatorioProjetoMonitoria/busca.jsf?aba=projetos">Consultar Relatórios Parciais e Finais dos Projetos </a></li>									
											<li><h:commandLink action="#{discenteMonitoria.listarInscricoesSelecaoAbertas}" value="Lista de Projetos com Processo Seletivo Cadastrado" onclick="setAba('projetos')"/> </li>										
											<li><a href="${ctx}/monitoria/ProjetoMonitoria/consultar_avaliadores.jsf?aba=projetos">Visualizar Avaliadores do Projeto</a></li>
										</ul>
									</li>	
								
								
									<li>Administrar Projetos
										<ul>
											<%-- <li><a href="${ctx}/monitoria/AlterarComponentesObrigatorios/lista_projetos.jsf?aba=projetos">Alterar Componentes Obrigatórios</a></li> --%>									
											<li><a href="${ctx}/monitoria/SelecaoCoordenador/consultar_projeto.jsf?aba=projetos">Alterar Coordenador</a></li>
											<li><a href="${ctx}/monitoria/AlterarEquipeDocente/lista.jsf?aba=projetos">Alterar Docentes do Projeto</a></li>												
											<li><a href="${ctx}/monitoria/AlterarSituacaoProjeto/lista.jsf?aba=projetos">Gerenciar Projetos</a></li>
											<li><h:commandLink action="#{alterarStatusProjetoMonitoriaMBean.iniciarSelecao}" value="Alterar Situação dos projeto" onclick="setAba('projetos')"/></li>
											<li><a href="${ctx}/monitoria/CadastrarEquipeDocente/lista.jsf?aba=projetos">Cadastrar Novo Docente</a></li>															
											<li><h:commandLink action="#{movimentacaoCotasMonitoria.iniciar}" value="Movimentar Cotas Entre Projetos" onclick="setAba('projetos')"/></li>
											<li><h:commandLink action="#{projetoMonitoria.iniciarProjetoMonitoria}" value="Submeter Projeto de Monitoria Interno" onclick="setAba('projetos')"/></li>
											<li><h:commandLink action="#{projetoMonitoria.iniciarProjetoMonitoriaExterno}" value="Submeter Projeto de Monitoria Externo" onclick="setAba('projetos')"/></li>
										</ul>
									</li>
									
									<li>Validações
										<ul>
											<li><h:commandLink action="#{autorizacaoProjetoMonitoria.listarAutorizacoesDepartamentos}" value="Validar Projetos nos Departamentos" onclick="setAba('projetos')" /></li>
											<li><h:commandLink action="#{autorizacaoReconsideracao.listarSolicitacoes}" value="Validar Reconsideração de Requisitos Formais" onclick="setAba('projetos')"/></li>
											<li><h:commandLink action="#{solicitacaoReconsideracao.listarSolicitacoesPendentesMonitoria}" value="Validar Reconsideração de Avaliação " onclick="setAba('projetos')" /></li>
										</ul>
									</li>	
	
									<li>Resumos do SID
										<ul>
											<li><h:commandLink action="#{resumoSid.consultarFrequenciaSID}" value="Consultas, Certificados e Freqüências do SID " onclick="setAba('projetos')"/> </li>														
											<li><h:commandLink action="#{resumoSid.relatorioSubmissao}" value="Quem Enviou Resumo SID" onclick="setAba('projetos')"/> </li>
										</ul>
									</li>	
								</ul>
							</div>
							
							<div id="monitores"  class="aba" >
								<ul>
								
								 <!-- <li>Bolsas PEC-G
										<ul>
											<li><h:commandLink action="#{bolsista.listar}" value="Alterar Bolsista PEC-G" onclick="setAba('monitores')"/> </li>
											<li><h:commandLink action="#{bolsista.preCadastrarPecg}" value="Cadastrar Bolsista PEC-G" onclick="setAba('monitores')"/> </li>
										</ul>
									</li>  -->


									<li>Gerenciar Monitores
										<ul>
											<li><h:commandLink action="#{comissaoMonitoria.alterarMonitor}" value="Alterar Monitoria" onclick="setAba('monitores')"/> </li>																						
											<li><h:commandLink action="#{comissaoMonitoria.cadastraMonitor}" value="Cadastrar Novo Monitor" onclick="setAba('monitores')"/> </li>
											<li><h:commandLink action="#{comissaoMonitoria.novoRelatorioAtividades}" value="Cadastrar Novo Relatório de Atividades" onclick="setAba('monitores')"/> </li>																						
											<li><h:commandLink action="#{comissaoMonitoria.consultarMonitor}" value="Emitir Certificado ou Declaração" onclick="setAba('monitores')"/> </li>
											<c:if test="${monitoria.frequenciaMonitoria}">									
												<li><h:commandLink action="#{cancelarBolsaMonitoria.iniciarCancelamentoBolsas}" value="Monitores pendentes de envio de frequência (Cancelar Bolsas)" onclick="setAba('monitores')"/> </li>
											</c:if>
											<c:if test="${!monitoria.frequenciaMonitoria}">									
												<li><h:commandLink action="#{cancelarBolsaMonitoria.iniciarCancelamentoBolsas}" value="Cancelar Bolsas" onclick="setAba('monitores')"/> </li>
											</c:if>
										</ul>
									</li>

									<li>Relatórios
										<ul>	
											<c:if test="${monitoria.frequenciaMonitoria}">									
												<li><h:commandLink action="#{atividadeMonitor.relatorioAtividades}" value="Relatório de Atividades (Freqüência)" onclick="setAba('monitores')"/></li>
											</c:if>
											<li><a href="${ctx}/monitoria/RelatorioMonitor/busca.jsf?aba=monitores">Relatórios Parciais, Finais e de Desligamento</a></li>									
											<li><h:commandLink action="#{relatorioMonitor.listarRelatoriosDesligamento}" value="Validar Relatórios de Desligamento" onclick="setAba('monitores')"/> </li>											
										</ul>
									</li>																		
									
									<li>Seleções
										<ul>
											<li><h:commandLink action="#{validaSelecaoMonitor.iniciarBuscaSelecoes}" value="Seleção de Monitores" onclick="setAba('monitores')"/></li>	
										</ul>
									</li>
									
									
									<li>Homologação de Bolsas
										<ul>
											<li><h:commandLink action="#{homologacaoBolsistaMonitoria.iniciarHomologacaoBolsas}" value="Homologar Cadastro de Bolsistas no SIPAC" onclick="setAba('monitores')"/> </li>
										</ul>
									</li>																		
								</ul>
							</div>
							
							
							<div id="adm" class="aba">
											
									<ul>
											<li>Configurar e Publicar Editais
												<ul>
													<li><h:commandLink action="#{editalMonitoria.preCadastrar}" value="Cadastrar" onclick="setAba('adm')"/></li>
													<li><h:commandLink action="#{editalMonitoria.listar}" value="Alterar/Remover" onclick="setAba('adm')"/></li>
												</ul>
											</li>
		
											<c:if test="${monitoria.frequenciaMonitoria}">
												<li>Configurar Mês de Recebimento de Freqüência
													<ul>
														<li><h:commandLink action="#{envioFrequencia.iniciarEnvioFrequencia}" value="Cadastrar/Alterar" onclick="setAba('adm')"/> </li>
													</ul>
												</li>
											</c:if>
		
											<li>Calendário de Monitoria
												<ul>
													<li><h:commandLink action="#{calendarioMonitoria.iniciarCadastroCalendario}" value="Cadastrar/Alterar" onclick="setAba('adm')"/> </li>
												</ul>
											</li>
											
											
											<li> Grupo de Itens de Avaliação
												<ul>
													<li><h:commandLink action="#{grupoItemAvaliacao.iniciarCadastroGrupo}" value="Cadastrar" onclick="setAba('adm')"/> </li>
																										
													<li> <a href="${ctx}/monitoria/GrupoItemAvaliacao/lista.jsf?aba=adm">Alterar/Remover </a></li>
												</ul>
											</li>
									</ul>
													
									<ul>
										<li> Itens de Avaliação
											<ul>
												<li> <a href="${ctx}/monitoria/ItemAvaliacaoMonitoria/form.jsf?aba=adm">Cadastrar </a></li>
												<li> <a href="${ctx}/monitoria/ItemAvaliacaoMonitoria/lista.jsf?aba=adm">Alterar/Remover </a></li>
											</ul>
										</li>
	
	
										<li>Possíveis Status de Projetos
											<ul>
											<li><a href="${ctx}/monitoria/TipoSituacaoProjeto/form.jsf?aba=adm">Cadastrar</a></li>
											<li><a href="${ctx}/monitoria/TipoSituacaoProjeto/lista.jsf?aba=adm">Alterar/Remover</a></li>
											</ul>
										</li>
									</ul>
							</div>
							
							<div id="comissoes" class="aba">
								<ul>

									<li> Avaliações
										<ul>
											<li><h:commandLink action="#{comissaoMonitoria.avaliaProjeto}" value="Avaliar Projetos" onclick="setAba('comissoes')"/></li>
											<li><h:commandLink action="#{publicarResultado.iniciarBuscarAvaliacoesEdital}" value="Avaliar Projetos com Discrepância" onclick="setAba('comissoes')"/></li>
											<li><h:commandLink action="#{comissaoMonitoria.avaliaResumoSID}" value="Avaliar Resumo SID" onclick="setAba('comissoes')"/></li>
								            <li><h:commandLink action="#{avalRelatorioProjetoMonitoria.listar}"	value="Avaliar Relatórios de Projetos" onclick="setAba('comissoes')" /></li>
								            
										</ul>
									</li>
									
									<li> Publicar Resultado
										<ul>
											<li><a href="${ctx}/monitoria/CalcularBolsas/busca_edital.jsf?aba=comissoes">Calcular Quantidade de Bolsas por Projeto</a></li>
											<li><a href="${ctx}/monitoria/ClassificarProjetos/busca_edital.jsf?aba=comissoes">Classificar Projetos por Colocação</a></li>
											<li><h:commandLink action="#{publicarResultado.iniciarBuscarAvaliacoesEdital}"	value="Publicar Resultado das Avaliações" onclick="setAba('comissoes')" /></li>
										</ul>
									</li>
									
									<li> Editar Comissão
										<ul>
											<li><h:commandLink action="#{membroComissao.preCadastrarMembroComissaoMonitoria}" value="Cadastrar Membro da Comissão" onclick="setAba('comissoes')"/> </li>
											<li><h:commandLink action="#{membroComissao.listarMembroComissaoMonitoria}" value="Alterar/Remover Membro da Comissão" onclick="setAba('comissoes')"/> </li>									
										</ul>
									</li>
									
									<li> Distribuições
										<ul>
											<li><a href="${ctx}/monitoria/DistribuicaoProjeto/lista.jsf?aba=comissoes">Distribuir Projetos de Ensino</a></li>
											<li><a href="${ctx}/monitoria/DistribuicaoRelatorioProjeto/lista.jsf?aba=comissoes">Distribuir Relatórios de Projetos</a></li>
											<li><a href="${ctx}/monitoria/DistribuicaoResumoSid/lista.jsf?aba=comissoes">Distribuir Resumos do SID</a></li>											
											<li><h:commandLink action="#{membroComissao.iniciarConsultarProjetosAvaliador}" value="Visualizar Projetos do Membro da Comissão" onclick="setAba('comissoes')"/> </li>
											<li><h:commandLink action="#{membroComissao.iniciarConsultarRelatoriosAvaliador}" value="Visualizar Relatórios do Membro da Comissão" onclick="setAba('comissoes')"/> </li>
											<li><h:commandLink action="#{membroComissao.iniciarConsultarResumoSidAvaliador}" value="Visualizar Resumo SID do Membro da Comissão" onclick="setAba('comissoes')"/> </li>																												
										</ul>										
									</li>
								</ul>
							</div>
							
							<div id="relatorios"  class="aba">
								<ul>
								
									<li> Monitores
										<ul>
											<li>  
												<h:commandLink action="#{comissaoMonitoria.relatorioMonitoresPorProjeto}" value="Relatório de Monitores por Projeto" onclick="setAba('relatorios')">
													<f:param value="true" name="menu"/>
												</h:commandLink>
											</li>
											<li>  
												<h:commandLink action="#{comissaoMonitoria.relatorioQuantitativoMonitores}" value="Relatório Quantitativo de Monitores por Projeto" onclick="setAba('relatorios')">
													<f:param value="true" name="menu"/>
												</h:commandLink> 
											</li>
							
											<li>  
												<h:commandLink action="#{comissaoMonitoria.relatorioMonitoresMes}" value="Relatório de Monitores do Mês" onclick="setAba('relatorios')">
													<f:param value="true" name="menu"/>
												</h:commandLink>
											</li>								
											
											<li><a href="${ctx}/monitoria/Relatorios/dados_bancarios_monitores_form.jsf?aba=relatorios">Dados Bancários dos Monitores</a></li>
																			
											<li>  
												<h:commandLink action="#{comissaoMonitoria.iniciarRelatorioMonitoresPorCentro}" value="Relatório de Monitores por Centro" onclick="setAba('relatorios')">
													<f:param value="true" name="menu"/>
												</h:commandLink>
											</li>
											
											<li>  
												<h:commandLink action="#{relatorioRendimentoComponente.iniciarRelatorioRendimentoComponente}" value="Relatório de Análise de Rendimentos por Departamento" onclick="setAba('relatorios')">
												</h:commandLink>
											</li>
											
										</ul>
										</li>	
									<li> Projetos
										<ul>
											<li><h:commandLink action="#{projetoMonitoria.iniciarLocalizacaoRelatorioGeral}" value="Quadro Geral de Projetos" onclick="setAba('relatorios')"/></li>
											<li><a href="${ctx}/monitoria/Relatorios/informativo_sintetico.jsf?aba=relatorios">Informativo Sintético</a></li>
											
											<li>  
												<h:commandLink action="#{projetoMonitoria.iniciarRelatorioProjetosAtivosComMonitoresAtivosInativos}" value="Relatorio de projetos que estão ativos e com monitores ativos ou inativos" onclick="setAba('relatorios')"/>
											</li>
											<li>  
												<h:commandLink action="#{relatorioReducaoBolsa.iniciaRelatorio}" value="Relatório de projetos renovados com redução de bolsas" onclick="setAba('relatorios')"/>
											</li>
											<li>
												<h:commandLink action="#{resumoSid.relatorioResumoSid}" value="Relatório de Resumos SID" onclick="setAba('relatorios')"/>
											</li>
										</ul>
									</li>
									
										
									
								</ul>
							</div>
				</div>

				<script>
						var Abas = {
						    init : function(){
						        var abas = new YAHOO.ext.TabPanel('operacoes-subsistema');
						        abas.addTab('projetos', "Projetos");
						        abas.addTab('monitores', "Monitores");
						        abas.addTab('adm', "Administração");
								abas.addTab('comissoes', "Comissões");
								abas.addTab('relatorios', "Relatórios");
						        abas.activate('projetos');
						        
						        <c:if test="${sessionScope.aba != null}">
							    	abas.activate('${sessionScope.aba}');
							    </c:if>
						                
						    }
						};
						
						YAHOO.ext.EventManager.onDocumentReady(Abas.init, Abas, true);
						function setAba(aba) {
							document.getElementById('aba').value = aba;
						}
				</script>

	</h:form>
</f:view>

<div class="linkRodape">
	<html:link action="/verMenuPrincipal">Menu Principal</html:link>
</div>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>