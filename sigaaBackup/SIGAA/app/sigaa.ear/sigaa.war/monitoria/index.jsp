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
											<li><a href="${ctx}/monitoria/RelatorioProjetoMonitoria/busca.jsf?aba=projetos">Consultar Relat�rios Parciais e Finais dos Projetos </a></li>									
											<li><h:commandLink action="#{discenteMonitoria.listarInscricoesSelecaoAbertas}" value="Lista de Projetos com Processo Seletivo Cadastrado" onclick="setAba('projetos')"/> </li>										
											<li><a href="${ctx}/monitoria/ProjetoMonitoria/consultar_avaliadores.jsf?aba=projetos">Visualizar Avaliadores do Projeto</a></li>
										</ul>
									</li>	
								
								
									<li>Administrar Projetos
										<ul>
											<%-- <li><a href="${ctx}/monitoria/AlterarComponentesObrigatorios/lista_projetos.jsf?aba=projetos">Alterar Componentes Obrigat�rios</a></li> --%>									
											<li><a href="${ctx}/monitoria/SelecaoCoordenador/consultar_projeto.jsf?aba=projetos">Alterar Coordenador</a></li>
											<li><a href="${ctx}/monitoria/AlterarEquipeDocente/lista.jsf?aba=projetos">Alterar Docentes do Projeto</a></li>												
											<li><a href="${ctx}/monitoria/AlterarSituacaoProjeto/lista.jsf?aba=projetos">Gerenciar Projetos</a></li>
											<li><h:commandLink action="#{alterarStatusProjetoMonitoriaMBean.iniciarSelecao}" value="Alterar Situa��o dos projeto" onclick="setAba('projetos')"/></li>
											<li><a href="${ctx}/monitoria/CadastrarEquipeDocente/lista.jsf?aba=projetos">Cadastrar Novo Docente</a></li>															
											<li><h:commandLink action="#{movimentacaoCotasMonitoria.iniciar}" value="Movimentar Cotas Entre Projetos" onclick="setAba('projetos')"/></li>
											<li><h:commandLink action="#{projetoMonitoria.iniciarProjetoMonitoria}" value="Submeter Projeto de Monitoria Interno" onclick="setAba('projetos')"/></li>
											<li><h:commandLink action="#{projetoMonitoria.iniciarProjetoMonitoriaExterno}" value="Submeter Projeto de Monitoria Externo" onclick="setAba('projetos')"/></li>
										</ul>
									</li>
									
									<li>Valida��es
										<ul>
											<li><h:commandLink action="#{autorizacaoProjetoMonitoria.listarAutorizacoesDepartamentos}" value="Validar Projetos nos Departamentos" onclick="setAba('projetos')" /></li>
											<li><h:commandLink action="#{autorizacaoReconsideracao.listarSolicitacoes}" value="Validar Reconsidera��o de Requisitos Formais" onclick="setAba('projetos')"/></li>
											<li><h:commandLink action="#{solicitacaoReconsideracao.listarSolicitacoesPendentesMonitoria}" value="Validar Reconsidera��o de Avalia��o " onclick="setAba('projetos')" /></li>
										</ul>
									</li>	
	
									<li>Resumos do SID
										<ul>
											<li><h:commandLink action="#{resumoSid.consultarFrequenciaSID}" value="Consultas, Certificados e Freq��ncias do SID " onclick="setAba('projetos')"/> </li>														
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
											<li><h:commandLink action="#{comissaoMonitoria.novoRelatorioAtividades}" value="Cadastrar Novo Relat�rio de Atividades" onclick="setAba('monitores')"/> </li>																						
											<li><h:commandLink action="#{comissaoMonitoria.consultarMonitor}" value="Emitir Certificado ou Declara��o" onclick="setAba('monitores')"/> </li>
											<c:if test="${monitoria.frequenciaMonitoria}">									
												<li><h:commandLink action="#{cancelarBolsaMonitoria.iniciarCancelamentoBolsas}" value="Monitores pendentes de envio de frequ�ncia (Cancelar Bolsas)" onclick="setAba('monitores')"/> </li>
											</c:if>
											<c:if test="${!monitoria.frequenciaMonitoria}">									
												<li><h:commandLink action="#{cancelarBolsaMonitoria.iniciarCancelamentoBolsas}" value="Cancelar Bolsas" onclick="setAba('monitores')"/> </li>
											</c:if>
										</ul>
									</li>

									<li>Relat�rios
										<ul>	
											<c:if test="${monitoria.frequenciaMonitoria}">									
												<li><h:commandLink action="#{atividadeMonitor.relatorioAtividades}" value="Relat�rio de Atividades (Freq��ncia)" onclick="setAba('monitores')"/></li>
											</c:if>
											<li><a href="${ctx}/monitoria/RelatorioMonitor/busca.jsf?aba=monitores">Relat�rios Parciais, Finais e de Desligamento</a></li>									
											<li><h:commandLink action="#{relatorioMonitor.listarRelatoriosDesligamento}" value="Validar Relat�rios de Desligamento" onclick="setAba('monitores')"/> </li>											
										</ul>
									</li>																		
									
									<li>Sele��es
										<ul>
											<li><h:commandLink action="#{validaSelecaoMonitor.iniciarBuscaSelecoes}" value="Sele��o de Monitores" onclick="setAba('monitores')"/></li>	
										</ul>
									</li>
									
									
									<li>Homologa��o de Bolsas
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
												<li>Configurar M�s de Recebimento de Freq��ncia
													<ul>
														<li><h:commandLink action="#{envioFrequencia.iniciarEnvioFrequencia}" value="Cadastrar/Alterar" onclick="setAba('adm')"/> </li>
													</ul>
												</li>
											</c:if>
		
											<li>Calend�rio de Monitoria
												<ul>
													<li><h:commandLink action="#{calendarioMonitoria.iniciarCadastroCalendario}" value="Cadastrar/Alterar" onclick="setAba('adm')"/> </li>
												</ul>
											</li>
											
											
											<li> Grupo de Itens de Avalia��o
												<ul>
													<li><h:commandLink action="#{grupoItemAvaliacao.iniciarCadastroGrupo}" value="Cadastrar" onclick="setAba('adm')"/> </li>
																										
													<li> <a href="${ctx}/monitoria/GrupoItemAvaliacao/lista.jsf?aba=adm">Alterar/Remover </a></li>
												</ul>
											</li>
									</ul>
													
									<ul>
										<li> Itens de Avalia��o
											<ul>
												<li> <a href="${ctx}/monitoria/ItemAvaliacaoMonitoria/form.jsf?aba=adm">Cadastrar </a></li>
												<li> <a href="${ctx}/monitoria/ItemAvaliacaoMonitoria/lista.jsf?aba=adm">Alterar/Remover </a></li>
											</ul>
										</li>
	
	
										<li>Poss�veis Status de Projetos
											<ul>
											<li><a href="${ctx}/monitoria/TipoSituacaoProjeto/form.jsf?aba=adm">Cadastrar</a></li>
											<li><a href="${ctx}/monitoria/TipoSituacaoProjeto/lista.jsf?aba=adm">Alterar/Remover</a></li>
											</ul>
										</li>
									</ul>
							</div>
							
							<div id="comissoes" class="aba">
								<ul>

									<li> Avalia��es
										<ul>
											<li><h:commandLink action="#{comissaoMonitoria.avaliaProjeto}" value="Avaliar Projetos" onclick="setAba('comissoes')"/></li>
											<li><h:commandLink action="#{publicarResultado.iniciarBuscarAvaliacoesEdital}" value="Avaliar Projetos com Discrep�ncia" onclick="setAba('comissoes')"/></li>
											<li><h:commandLink action="#{comissaoMonitoria.avaliaResumoSID}" value="Avaliar Resumo SID" onclick="setAba('comissoes')"/></li>
								            <li><h:commandLink action="#{avalRelatorioProjetoMonitoria.listar}"	value="Avaliar Relat�rios de Projetos" onclick="setAba('comissoes')" /></li>
								            
										</ul>
									</li>
									
									<li> Publicar Resultado
										<ul>
											<li><a href="${ctx}/monitoria/CalcularBolsas/busca_edital.jsf?aba=comissoes">Calcular Quantidade de Bolsas por Projeto</a></li>
											<li><a href="${ctx}/monitoria/ClassificarProjetos/busca_edital.jsf?aba=comissoes">Classificar Projetos por Coloca��o</a></li>
											<li><h:commandLink action="#{publicarResultado.iniciarBuscarAvaliacoesEdital}"	value="Publicar Resultado das Avalia��es" onclick="setAba('comissoes')" /></li>
										</ul>
									</li>
									
									<li> Editar Comiss�o
										<ul>
											<li><h:commandLink action="#{membroComissao.preCadastrarMembroComissaoMonitoria}" value="Cadastrar Membro da Comiss�o" onclick="setAba('comissoes')"/> </li>
											<li><h:commandLink action="#{membroComissao.listarMembroComissaoMonitoria}" value="Alterar/Remover Membro da Comiss�o" onclick="setAba('comissoes')"/> </li>									
										</ul>
									</li>
									
									<li> Distribui��es
										<ul>
											<li><a href="${ctx}/monitoria/DistribuicaoProjeto/lista.jsf?aba=comissoes">Distribuir Projetos de Ensino</a></li>
											<li><a href="${ctx}/monitoria/DistribuicaoRelatorioProjeto/lista.jsf?aba=comissoes">Distribuir Relat�rios de Projetos</a></li>
											<li><a href="${ctx}/monitoria/DistribuicaoResumoSid/lista.jsf?aba=comissoes">Distribuir Resumos do SID</a></li>											
											<li><h:commandLink action="#{membroComissao.iniciarConsultarProjetosAvaliador}" value="Visualizar Projetos do Membro da Comiss�o" onclick="setAba('comissoes')"/> </li>
											<li><h:commandLink action="#{membroComissao.iniciarConsultarRelatoriosAvaliador}" value="Visualizar Relat�rios do Membro da Comiss�o" onclick="setAba('comissoes')"/> </li>
											<li><h:commandLink action="#{membroComissao.iniciarConsultarResumoSidAvaliador}" value="Visualizar Resumo SID do Membro da Comiss�o" onclick="setAba('comissoes')"/> </li>																												
										</ul>										
									</li>
								</ul>
							</div>
							
							<div id="relatorios"  class="aba">
								<ul>
								
									<li> Monitores
										<ul>
											<li>  
												<h:commandLink action="#{comissaoMonitoria.relatorioMonitoresPorProjeto}" value="Relat�rio de Monitores por Projeto" onclick="setAba('relatorios')">
													<f:param value="true" name="menu"/>
												</h:commandLink>
											</li>
											<li>  
												<h:commandLink action="#{comissaoMonitoria.relatorioQuantitativoMonitores}" value="Relat�rio Quantitativo de Monitores por Projeto" onclick="setAba('relatorios')">
													<f:param value="true" name="menu"/>
												</h:commandLink> 
											</li>
							
											<li>  
												<h:commandLink action="#{comissaoMonitoria.relatorioMonitoresMes}" value="Relat�rio de Monitores do M�s" onclick="setAba('relatorios')">
													<f:param value="true" name="menu"/>
												</h:commandLink>
											</li>								
											
											<li><a href="${ctx}/monitoria/Relatorios/dados_bancarios_monitores_form.jsf?aba=relatorios">Dados Banc�rios dos Monitores</a></li>
																			
											<li>  
												<h:commandLink action="#{comissaoMonitoria.iniciarRelatorioMonitoresPorCentro}" value="Relat�rio de Monitores por Centro" onclick="setAba('relatorios')">
													<f:param value="true" name="menu"/>
												</h:commandLink>
											</li>
											
											<li>  
												<h:commandLink action="#{relatorioRendimentoComponente.iniciarRelatorioRendimentoComponente}" value="Relat�rio de An�lise de Rendimentos por Departamento" onclick="setAba('relatorios')">
												</h:commandLink>
											</li>
											
										</ul>
										</li>	
									<li> Projetos
										<ul>
											<li><h:commandLink action="#{projetoMonitoria.iniciarLocalizacaoRelatorioGeral}" value="Quadro Geral de Projetos" onclick="setAba('relatorios')"/></li>
											<li><a href="${ctx}/monitoria/Relatorios/informativo_sintetico.jsf?aba=relatorios">Informativo Sint�tico</a></li>
											
											<li>  
												<h:commandLink action="#{projetoMonitoria.iniciarRelatorioProjetosAtivosComMonitoresAtivosInativos}" value="Relatorio de projetos que est�o ativos e com monitores ativos ou inativos" onclick="setAba('relatorios')"/>
											</li>
											<li>  
												<h:commandLink action="#{relatorioReducaoBolsa.iniciaRelatorio}" value="Relat�rio de projetos renovados com redu��o de bolsas" onclick="setAba('relatorios')"/>
											</li>
											<li>
												<h:commandLink action="#{resumoSid.relatorioResumoSid}" value="Relat�rio de Resumos SID" onclick="setAba('relatorios')"/>
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
						        abas.addTab('adm', "Administra��o");
								abas.addTab('comissoes', "Comiss�es");
								abas.addTab('relatorios', "Relat�rios");
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