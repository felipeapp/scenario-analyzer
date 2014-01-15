<%@page import="br.ufrn.arq.seguranca.sigaa.SipacPapeis"%>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%> 
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<%@page import="br.ufrn.arq.seguranca.sigaa.SipacPapeis"%>

<c:set var="hideSubsistema" value="true" />

<f:view>
	<h:form>
	<h2>Menu da Pró-Reitoria de Extensão</h2>

	<input type="hidden" name="aba" id="aba" />
		<div id="operacoes-subsistema" class="reduzido">

							<ufrn:checkRole papeis="<%= new int[] { 
									SigaaPapeis.APOIO_TECNICO_COORDENACAO_EXTENSAO, 
									SigaaPapeis.COORDENADOR_PROGRAMAS_PROJETOS_EXTENSAO, 
									SigaaPapeis.COORDENADOR_CURSOS_EVENTOS_PRODUTOS_EXTENSAO, 
									SigaaPapeis.PRESIDENTE_COMITE_EXTENSAO, SigaaPapeis.GESTOR_EXTENSAO, 
									SigaaPapeis.EXTENSAO_TECNICO_ADMINISTRATIVO } %>">
									
								<div id="geral" class="aba">
									<ul>
									    <li>Ações de Extensão
								            <ul>
								                <li><h:commandLink action="#{atividadeExtensao.iniciar}" value="Cadastrar Proposta" onclick="setAba('geral')" /></li>
								                <li><h:commandLink action="#{atividadeExtensao.listaAlterarAtividade}" value="Gerenciar Proposta de Ação" onclick="setAba('geral')" /></li>
								                <li><a href="${ctx}/extensao/RelatorioAcaoExtensao/busca.jsf?aba=geral">Gerenciar Relatórios</a></li>
								                <li><h:commandLink action="#{atividadeExtensao.iniciarBuscarAtividadesPeriodoConclusao}" value="Monitorar Finalização de Ações" onclick="setAba('geral')" /></li>
								                <ufrn:checkRole papeis="<%= new int[] {SigaaPapeis.GESTOR_EXTENSAO} %>">
								                	<li><h:commandLink action="#{expirarTempoCadastro.iniciaEncerrar}" value="Expirar Ações com Cadastro em Andamento" onclick="setAba('geral')" /></li>
								                </ufrn:checkRole>
								                <ufrn:checkRole papeis="<%= new int[] {SigaaPapeis.GESTOR_EXTENSAO} %>">
								                	<li><h:commandLink action="#{recuperarAcoes.iniciaRecuperacao}" value="Recuperar Ações Excluídas" onclick="setAba('geral')" /></li>
								                </ufrn:checkRole>
								                <ufrn:checkRole papeis="<%= new int[] {SigaaPapeis.PRESIDENTE_COMITE_EXTENSAO} %>">
                                                    <li><h:commandLink action="#{movimentacaoCotasExtensao.iniciar}" value="Movimentar Cotas de Bolsas entre Ações de Extensão" onclick="setAba('geral')" /> </li>
                                                    <li><h:commandLink action="#{vincularUnidadeOrcamentariaMBean.listar}" value="Vincular Unidade Orçamentária" onclick="setAba('geral')" /></li>
                                                </ufrn:checkRole>
                                                <li><h:commandLink action="#{comunicarCoordenadores.preComunicarCoordenadoresExtensao}" value="Comunicação com Coordenadores" onclick="setAba('geral')"/> </li>
											</ul>
										</li>

										<li>Buscas
									         <ul>
									         	<li><h:commandLink action="#{atividadeExtensao.preLocalizar}"	value="Buscar Ações " onclick="setAba('geral')" /></li>
								                <li><h:commandLink action="#{avaliacaoAtividade.iniciarConsultarAvaliacoesAtividade}" value="Buscar Avaliações de Propostas" onclick="setAba('geral')" /></li>
												<li><a href="${ctx}/extensao/DiscenteExtensao/busca_discente.jsf?aba=geral">Buscar Discentes de Extensão</a></li>																												
								            </ul>
										</li>
										
										
										
										<c:if test="${acesso.pareceristaExtensao or acesso.comissaoExtensao}">
										    <li>Avaliar Propostas de Extensão 
									            <ul>
													<c:if test="${acesso.comissaoExtensao}">
														<li><a href="${ctx}/extensao/AvaliacaoAtividade/lista.jsf?aba=geral">Avaliar Propostas como Membro do Comitê de Extensão</a></li>
													</c:if>
							
													<c:if test="${acesso.pareceristaExtensao}">
														<li><a href="${ctx}/extensao/AvaliacaoAtividade/lista_parecerista.jsf?aba=geral">Avaliar Propostas como Parecerista Ad Hoc</a></li>
													</c:if>
													<li><a href="${ctx}/extensao/RelatorioAcaoExtensao/busca.jsf?aba=geral">Verificar Relatórios de Ações de Extensão</a></li>
										
													<li><h:commandLink action="#{filtroAtividades.irTelaAvaliarPresidenteComite}" value="Avaliação Final de Propostas (Presidente do comitê)" 
														onclick="setAba('geral')" rendered="#{ filtroAtividades.exibir }"/></li>
												</ul>
											</li>
										</c:if>
											
										<ufrn:checkRole papeis="<%= new int[] {	SipacPapeis.GESTOR_BOLSAS_LOCAL	} %>">
										    <li>Bolsas de Extensão 
									            <ul>
									            	<li><a href="${ctx}/extensao/AlterarAtividade/lista.jsf?aba=geral">Indicar Discente Bolsista ou Voluntário</a></li>									            	
									            	<li><a href="${ctx}/extensao/DiscenteExtensao/lista.jsf?aba=geral">Finalizar Discente Bolsista ou Voluntário</a></li>									            	
									            	<li><h:commandLink action="#{homologacaoBolsistaExtensao.iniciarHomologacaoBolsas}"	value="Homologar Cadastro de Bolsistas do FAEx no SIPAC" onclick="setAba('geral')" /></li>
									            	<li><h:commandLink action="#{finalizacaoBolsistaExtensao.listarBolsistasFinalizados}"	value="Finalizar Bolsistas do FAEx no SIPAC " onclick="setAba('geral')" /></li>
												</ul>
											</li>
										</ufrn:checkRole>					
										
									</ul>
								</div>
							</ufrn:checkRole>
							
							
							<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.COORDENADOR_PROGRAMAS_PROJETOS_EXTENSAO, SigaaPapeis.PRESIDENTE_COMITE_EXTENSAO } %>">
								<div id="cpp" class="aba">
									<h3>Coordenação de Projetos e Programas de Extensão</h3>
								
									<ul>
										
									    <li>Validações 
								            <ul>
												<li><h:commandLink action="#{solicitacaoReconsideracao.listarSolicitacoesPendentesExtensao}" value="Validar Solicitações de Reconsideração" onclick="setAba('cpp')" /></li>
								                <li>
								                	<h:commandLink action="#{relatorioAcaoExtensao.iniciarValidacaoRelatorios}" value="Validar Relatórios de Projetos e Programas" onclick="setAba('cpp')">
								                		<f:param value="cpp" name="coordenacao" />
								                	</h:commandLink>
								                </li>
												<li><h:commandLink action="#{autorizacaoDepartamento.listarAutorizacoesAcoesDepartamentosProex}" value="Validar Ações como Chefe de Departamento" onclick="setAba('cpp')" /></li>
            									<li><h:commandLink action="#{autorizacaoDepartamento.listarAutorizacoesRelatorioDepartamentoProex}" value="Validar Relatórios como Chefe de Departamento" onclick="setAba('cpp')" /></li>
        										<li><h:commandLink action="#{homologacaoBolsistaExtensao.iniciarHomologacaoBolsas}"	value="Homologar Cadastro de Bolsistas do FAEx no SIPAC" onclick="setAba('cpp')" /></li>
        										<li><h:commandLink action="#{finalizacaoBolsistaExtensao.listarBolsistasFinalizados}"	value="Finalizar Bolsistas do FAEx no SIPAC " onclick="setAba('geral')" /></li> 
											</ul>
										</li>
										
										<li>Emissão de Documentos
								            <ul>
												<li><a href="${ctx}/extensao/DocumentosAutenticados/form.jsf?aba=cpp">Emitir Certificação/Declaração (membros) </a></li>
												<li><h:commandLink action="#{gerenciarParticipantesExtensaoByGestorExtensaoMBean.selecionarAtividadeExtensao}" value="Emitir Certificação/Declaração (participantes)" onclick="setAba('cpp')" /></li>
											</ul>
										</li>

										
                                        <li><h:commandLink action="#{gerenciarCadastrosParticipantesMBean.iniciarAlteracaoCadastroParticipante}" value="Gerenciar Cadastro de Participantes" onclick="setAba('cpp')" /></li>
                                         

								        <li>Editais
								            <ul>
								                <li><h:commandLink action="#{editalExtensao.preCadastrar}" value="Cadastrar" onclick="setAba('cpp')"/></li>
												<li><h:commandLink action="#{editalExtensao.listar}" value="Alterar/Remover" onclick="setAba('cpp')"/></li>
								            </ul>
										</li>
										
									</ul>
								</div>
							</ufrn:checkRole>
							
							<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.COORDENADOR_CURSOS_EVENTOS_PRODUTOS_EXTENSAO, SigaaPapeis.PRESIDENTE_COMITE_EXTENSAO } %>">
								<div id="ccep" class="aba">
									<h3>Coordenação de Cursos, Eventos e Produtos de Extensão</h3>
									<ul>
										
									    <li>Validações 
								            <ul>
												<li><h:commandLink action="#{solicitacaoReconsideracao.listarSolicitacoesPendentesExtensao}" value="Validar Solicitações de Reconsideração" onclick="setAba('cpp')" /></li>
								                <li><h:commandLink action="#{relatorioAcaoExtensao.iniciarValidacaoRelatorios}" value="Validar Relatórios de Cursos, Eventos e Produtos" onclick="setAba('ccep')">
								                   		<f:param value="ccep" name="coordenacao" />
								                	</h:commandLink>
								                </li>
												<li><h:commandLink action="#{autorizacaoDepartamento.listarAutorizacoesAcoesDepartamentosProex}" value="Validar Ações como Chefe de Departamento" onclick="setAba('ccep')" /></li>
	           									<li><h:commandLink action="#{autorizacaoDepartamento.listarAutorizacoesRelatorioDepartamentoProex}" value="Validar Relatórios como Chefe de Departamento" onclick="setAba('ccep')" /></li>
												<li><h:commandLink action="#{homologacaoBolsistaExtensao.iniciarHomologacaoBolsas}"	value="Homologar Cadastro de Bolsistas do FAEx no SIPAC" onclick="setAba('cpp')" /></li>
        										<li><h:commandLink action="#{finalizacaoBolsistaExtensao.listarBolsistasFinalizados}"	value="Finalizar Bolsistas do FAEx no SIPAC " onclick="setAba('geral')" /></li>	           																	                
											</ul>
										</li>

                                        <li>Inscrições On-line
                                            <ul>
                                               <li><h:commandLink action="#{gerenciarInscricoesCursosEventosExtensaoMBean.iniciarGerenciamentoInscricoesByGestor}" value="Gerenciar Inscrições" onclick="setAba('ccep')" /></li>
                                            </ul>
                                        </li>


										<li>Emitir Documentos
								            <ul>
												<li><a href="${ctx}/extensao/DocumentosAutenticados/form.jsf?aba=ccep">Emitir Certificação/Declaração (membros) </a></li>
												<li><h:commandLink action="#{gerenciarParticipantesExtensaoByGestorExtensaoMBean.selecionarAtividadeExtensao}" value="Emitir Certificação/Declaração (participantes)" onclick="setAba('ccep')" /></li>
											</ul>
										</li>

										
                                        <li><h:commandLink action="#{gerenciarCadastrosParticipantesMBean.iniciarAlteracaoCadastroParticipante}" value="Gerenciar Cadastro de Participantes" onclick="setAba('ccep')" /></li>
                                           


										<li>Editais
								            <ul>
								                <li><h:commandLink action="#{editalExtensao.preCadastrar}" value="Cadastrar" onclick="setAba('ccep')"/></li>
												<li><h:commandLink action="#{editalExtensao.listar}" value="Alterar/Remover" onclick="setAba('ccep')"/></li>
								            </ul>
										</li>
										
							           <li>Tipos de Cursos e Eventos
								            <ul>
								                <li><h:commandLink action="#{tipoCursoEventoExtensao.iniciarCadastro}" value="Cadastrar" onclick="setAba('ccep')"/></li>
										        <li><a href="${ctx}/extensao/TipoCursoEventoExtensao/lista.jsf?aba=ccep">Listar/Alterar</a> </li>
								            </ul>
										</li>

							           <li>Tipos de Produto
								            <ul>
								                <li><h:commandLink action="#{tipoProduto.iniciarCadastro}" value="Cadastrar" onclick="setAba('ccep')"/></li>
										        <li><a href="${ctx}/extensao/TipoProduto/lista.jsf?aba=ccep">Listar/Alterar</a> </li>
								            </ul>
										</li>									
										
									</ul>
								</div>
							</ufrn:checkRole>

							<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.COORDENADOR_CURSOS_EVENTOS_PRODUTOS_EXTENSAO, SigaaPapeis.COORDENADOR_PROGRAMAS_PROJETOS_EXTENSAO, SigaaPapeis.PRESIDENTE_COMITE_EXTENSAO } %>">
								<div id="comite" class="aba">
									<ul>
	
										<li> Avaliações
											<ul>
								                <li><h:commandLink action="#{avaliacaoAtividade.iniciarConsultarAvaliacoesAtividade}" value="Consultar/Remover Avaliações" onclick="setAba('comite')" /></li>

												<c:if test="${acesso.comissaoExtensao}">
													<li><a href="${ctx}/extensao/AvaliacaoAtividade/lista.jsf?aba=comite">Avaliar Propostas como Membro do Comitê</a></li>
												</c:if>

												<c:if test="${acesso.pareceristaExtensao}">
													<li><a href="${ctx}/extensao/AvaliacaoAtividade/lista_parecerista.jsf?aba=comite">Avaliar Propostas como Parecerista Ad Hoc</a></li>
												</c:if>
												
												<li><a href="${ctx}/extensao/ClassificarAcoes/lista.jsf?aba=comite">Classificar Ações de Extensão</a></li>
											</ul>
										</li>
	
										<li> Membros do Comitê
											<ul>
												<li><h:commandLink action="#{membroComissao.preCadastrarMembroComissaoExtensao}" value="Cadastrar Membro da Comissão" onclick="setAba('comite')"/> </li>
												<li><h:commandLink action="#{membroComissao.listarMembroComissaoExtensao}" value="Alterar/Remover Membro da Comissão" onclick="setAba('comite')"/> </li>
											</ul>
										</li>

										<li> Avaliadores Ad Hoc
											<ul>
												<li><h:commandLink action="#{avaliadorExtensao.preCadastrarAvaliadorExtensao}" value="Cadastrar Avaliador" onclick="setAba('comite')"/> </li>
												<li><h:commandLink action="#{avaliadorExtensao.listarAvaliadorExtensao}" value="Listar/Alterar Avaliador" onclick="setAba('comite')"/> </li>
											</ul>
										</li>
	
										<li> Distribuir Ações
											<ul>
												<li> <h:commandLink action="#{filtroAtividades.irTelaDistribuirManualComiteAdHoc}" value="Distribuir para Avaliadores Ad Hoc (Manual)" onclick="setAba('comite')"/>	</li>
												<li> <h:commandLink action="#{filtroAtividades.irTelaDistribuirAutomaticaComiteAdHoc}" value="Distribuir para Avaliadores Ad Hoc (Automática)" onclick="setAba('comite')"/>	</li>										
												<li> <h:commandLink action="#{filtroAtividades.irTelaDistribuirComiteExtensao}" value="Distribuir para Comitê de Extensão" onclick="setAba('comite')"/> </li>
											</ul>
										</li>
	
									</ul>
								</div>
	
								<div id="cadastro" class="aba">
									<ul>
										<li>Áreas Temáticas
											<ul>
												<li><h:commandLink action="#{areaTematica.iniciarCadastro}" value="Cadastrar" onclick="setAba('cadastro')"/></li>
												<li><a href="${ctx}/extensao/AreaTematica/lista.jsf?aba=cadastro">Listar/Alterar</a> </li>
											</ul>
										</li>
	
<!--					                    <li>Fator Gerador-->
<!--								            <ul>-->
<!--                                                <li><h:commandLink action="#{fatorGerador.iniciarCadastro}" value="Cadastrar" onclick="setAba('cadastro')"/></li>-->
<!--												<li><a href="${ctx}/extensao/FatorGerador/lista.jsf?aba=cadastro">Listar/Alterar</a> </li>-->
<!--											</ul>-->
<!--										</li>-->
	
<!--					                    <li>Forma Compromisso-->
<!--								            <ul>-->
<!--                                                <li><h:commandLink action="#{formaCompromisso.iniciarCadastro}" value="Cadastrar" onclick="setAba('cadastro')"/></li>-->
<!--										        <li><a href="${ctx}/extensao/FormaCompromisso/lista.jsf?aba=cadastro">Listar/Alterar</a> </li>-->
<!--								            </ul>-->
<!--										</li>-->
	
	  						          	<li>Função Membro Equipe
								            <ul>
                                                <li><h:commandLink action="#{funcaoMembroEquipe.iniciarCadastro}" value="Cadastrar" onclick="setAba('cadastro')"/></li>
										        <li><a href="${ctx}/extensao/FuncaoMembro/lista.jsf?aba=cadastro">Listar/Alterar</a> </li>
								            </ul>
										</li>
								
	
<!--  							            <li>Natureza Serviço-->
<!--								            <ul>-->
<!--                                                <li><h:commandLink action="#{naturezaServico.iniciarCadastro}" value="Cadastrar" onclick="setAba('cadastro')"/></li>-->
<!--										        <li><a href="${ctx}/extensao/NaturezaServico/lista.jsf?aba=cadastro">Listar/Alterar</a> </li>-->
<!--								            </ul>-->
<!--										</li>-->
	
<!--							           <li>Tipo de Grupo Prestação Serviços-->
<!--								            <ul>-->
<!--                                                <li><h:commandLink action="#{tipoGrupoPrestServico.iniciarCadastro}" value="Cadastrar" onclick="setAba('cadastro')"/></li>												                -->
<!--										        <li><a href="${ctx}/extensao/TipoGrupoPrestServico/lista.jsf?aba=cadastro">Listar/Alterar</a> </li>-->
<!--								            </ul>-->
<!--										</li>-->
	
										<li>Tipo de Público Alvo
											<ul>
                                                <li><h:commandLink action="#{tipoPublicoAlvo.preCadastrar}" value="Cadastrar" onclick="setAba('cadastro')"/></li>
												<li><a href="${ctx}/extensao/TipoPublicoAlvo/lista.jsf?aba=cadastro">Alterar/Remover</a></li>
											</ul>
										</li>
														
										<li>Grupo de Público Alvo
											<ul>
												<li> <h:commandLink action="#{grupoPublicoAlvo.preCadastrar}" value="Cadastrar" onclick="setAba('cadastro');"/> </li>
												<li><a href="${ctx}/extensao/GrupoPublicoAlvo/lista.jsf?aba=cadastro">Alterar/Remover</a></li>
											</ul>
										</li>
														
										<li>Tipos de Participação Ação de Extensão 
											<ul>
												<li> <h:commandLink action="#{tipoParticipacaoAcaoExtensao.preCadastrar}" value="Cadastrar" onclick="setAba('cadastro');"/> </li>
												<li><h:commandLink action="#{tipoParticipacaoAcaoExtensao.listar}" value="Alterar/Remover" onclick="setAba('cadastro');"/> </li>
											</ul>
										</li>
														
										<li> Grupo de Itens de Avaliação
											<ul>
												<li><h:commandLink action="#{grupoItemAvaliacaoExtensao.iniciarCadastroGrupo}" value="Cadastrar" onclick="setAba('cadastro');"/> </li>																
												<li> <a href="${ctx}/extensao/GrupoItemAvaliacao/lista.jsf?aba=adm">Alterar/Remover </a></li>
											</ul>
										</li>
														
										<li> Itens de Avaliação
											<ul>
												<li> <h:commandLink action="#{itemAvaliacaoExtensao.iniciarCadastroItem}" value="Cadastrar" onclick="setAba('cadastro');"/> </li>
												<li> <a href="${ctx}/extensao/ItemAvaliacaoExtensao/lista.jsf?aba=adm">Alterar/Remover </a></li>
											</ul>
										</li>
	
										<li>Calendário de Extensão
											<ul>
												<li><h:commandLink action="#{calendarioExtensao.preCadastrar}" value="Cadastrar/Alterar" onclick="setAba('cadastro');" /></li>
											</ul>
										</li>

										<li>Ficha do questionário para as ações de extensão
											<ul>
												<li> <h:commandLink action="#{questionarioBean.iniciarCadastroFichaExtensao}" value="Cadastrar" onclick="setAba('cadastro');"/> </li>
												<li><h:commandLink action="#{questionarioBean.gerenciarQuestionarioAcaoExtensao}" value="Alterar/Remover" onclick="setAba('cadastro')" /></li>
											</ul>
											<ul>
								            	<li>Associações
									            	<ul>
														<li><h:commandLink action="#{questionarioProjetoExtensaoMBean.preCadastrar}" value="Associar aos Usuários" onclick="setAba('cadastro')" /></li>
														<li><h:commandLink action="#{questionarioProjetoExtensaoMBean.listar}" value="Listar Associações" onclick="setAba('cadastro')" /></li>
									            	</ul>
								            	</li>
								            </ul>
										</li>
										
										
										<li>Modalidade dos Participantes
											<ul>
												<li><h:commandLink action="#{modalidadeParticipanteMBean.preCadastrar}" value="Cadastrar" onclick="setAba('cadastro')"/></li>
												<li><h:commandLink action="#{modalidadeParticipanteMBean.listar}" value="Listar/Alterar" onclick="setAba('cadastro')"/></li>
											</ul>
										</li>
										
									</ul>
								</div>
							</ufrn:checkRole>
							
							<div id="relatorios" class="aba">
								<ul>

									<li>Relatórios Gerais
								         <ul>
											<li><h:commandLink action="#{relatoriosAtividades.iniciarRelatorioEquipePorModalidade}" value="Relatório Nominal do Total de Membros por Tipo"/></li>
											<li><h:commandLink action="#{relatoriosAtividades.iniciarRelatorioDiscenteExtensaoPorModalidade}" value="Relatório Nominal do Total de Discentes de Extensão por Tipo"/></li>
											<li><h:commandLink action="#{relatoriosAtividades.iniciarRelatorioNominalAcoesLocalidade}" value="Relatório Nominal de Ações por Localidade"/></li>
											<li><h:commandLink action="#{relatoriosAtividades.iniciarRelatorioAcoesFinanciamentoInternoFaexExterno}" value="Relatório das Ações que Receberam Financiamento Interno (#{siglasExtensaoMBean.siglaFundoExtensaoPadrao})/Financiamento Externo"/></li>
											<li><h:commandLink action="#{relatoriosAtividades.iniciarRelatorioAcoesLocalRealizacao}" value="Relatório das Ações por Local de Realização"/></li>
							            </ul>
									</li>
									
									<li>Relatórios (INEP)
								         <ul>
							                <li><h:commandLink action="#{relatoriosExtensaoCenso.relatorioTotalProgramasProjetosVinculados}" value="Número Total de Programas e seus Respectivos Projetos Vinculados" onclick="setAba('relatorios')" /></li>
							                <li><h:commandLink action="#{relatoriosExtensaoCenso.relatorioTotalProjetosNaoVinculados}" value="Número Total de Projetos não-Vinculados, Público Atendido e Pessoas Envolvidas na Execução, Segundo a Área Temática" onclick="setAba('relatorios')" /></li>
							                <li><h:commandLink action="#{relatoriosExtensaoCenso.relatorioTotalCursosSegundoAreaConhecimento}" value="Número Total de Cursos, Total de Carga Horária, Concluintes e Ministrantes em Curso de Extensão Presencial, Segundo a área de Conhecimento CNPq" onclick="setAba('relatorios')" /></li>
							                <li><h:commandLink action="#{relatoriosExtensaoCenso.relatorioTotalEventosSegundoTematicaExtensao}" value="Número Total de Eventos Desenvolvidos, por Tipo de Evento e Público Participante, Segundo Área Temática de Extensão" onclick="setAba('relatorios')" /></li>
							            </ul>
									</li>
									
									<li> Dados de Ações por Edital
										<ul>
																											
											<li><h:commandLink action="#{relatoriosAtividades.iniciarRelatorioDescentralizacaoRecursosFaex}" value="Relatório para Descentralização de Recursos do #{siglasExtensaoMBean.siglaFundoExtensaoPadrao}" onclick="setAba('relatorios')"/></li>
											
										</ul>
									</li>
									
									<li>Contatos 
								         <ul>
											<li><h:commandLink action="#{atividadeExtensao.preLocalizarRelatorioCoordenador}" value="Relatório com Dados para Contato com Coordenador(a)" onclick="setAba('relatorios')" /></li>
							            </ul>
									</li>
									
									<li>Discentes de Extensão 
								         <ul>
											<li><a href="${ctx}/extensao/Relatorios/dados_bancarios_discentes_form.jsf?aba=relatorios">Dados Bancários de Discentes de Extensão</a></li>
											<li><h:commandLink action="#{atividadeExtensao.preLocalizar}" value="Relatório de Alunos em Ações de Extensão, Monitoria e Pesquisa" onclick="setAba('relatorios')" /></li>
											<li><h:commandLink action="#{relatoriosAtividades.preLocalizarAcoesSemPlano}" value="Relatório de todos os projetos de extensão que não cadastraram planos de trabalho de seus alunos ou sem planos de trabalho" onclick="setAba('relatorios')" /></li>
											
											<%-- Chama a action do struts LogonSistemaAction que cria um passaporte e abre o sipac na pagina do relatório de bolsas --%>
                                            <% if (Sistema.isSipacAtivo()) { %>
                                                <li> <ufrn:link action="entrarSistema" param="sistema=sipac&url=/sipac/bolsas/relatorios/situacao_social_bolsas_filtros.jsf"> Situação Social dos Bolsistas </ufrn:link> </li>
                                            <% } %>
											
							            </ul>
									</li>

									<li>Relatórios Quantitativos
										<ul>
											<li><h:commandLink action="#{relatorioPlanejamentoMBean.iniciarRelatorioResumoParticipantes}" value="Total de Ações e Participantes Ativos por Área Temática" onclick="setAba('relatorios')"/></li>
											<li><h:commandLink action="#{relatorioTotalAcaoEdital.iniciarRelatorioAcoesEdital}" value=" Total de Ações de Extensão que Concorreram a Editais Públicos" onclick="setAba('relatorios')"/></li>
											<li><h:commandLink action="#{relatorioPlanejamentoMBean.iniciarRelatorioDiscentesPorVinculoENivel}" value="Total de Discentes Ativos por Nível e Vínculo" onclick="setAba('relatorios')"/></li>
											<li><h:commandLink action="#{relatorioPlanejamentoMBean.iniciarRelatorioDiscentesPorNivel}" value="Total de Discentes Ativos por Nível de Ensino" onclick="setAba('relatorios')"/></li>
											<li><h:commandLink action="#{relatorioPlanejamentoMBean.iniciarRelatorioPlanosTrabalho}" value="Total de Discentes com Planos de Trabalho" onclick="setAba('relatorios')"/></li>
											<li><h:commandLink action="#{relatoriosAtividades.iniciarRelatorioTotalDiscentesParticipantesPlanoTrabalhoExtensao}" value="Total de Discentes com Planos de Trabalho Participantes de Ações de Extensão" onclick="setAba('relatorios')"/></li>
											<li><h:commandLink action="#{relatorioPlanejamentoMBean.iniciarRelatorioDiscenteNaEquipe}" value="Total de Discentes como membros da equipe" onclick="setAba('relatorios')"/></li>
											<li><h:commandLink action="#{relatoriosAtividades.iniciarRelatorioTotalParticipantesProjetoDiscentes}" value="Total de Discentes das Equipes dos Projetos Participantes de Ações de Extensão" onclick="setAba('relatorios')"/></li>
											<li><h:commandLink action="#{relatoriosAtividades.iniciarRelatorioTotalParticipantesDocentesExtensao}" value="Total de Docentes Participantes de Ações de Extensão" onclick="setAba('relatorios')"/></li>
											<li><h:commandLink action="#{relatorioPlanejamentoMBean.iniciarRelatorioDocentesPorNivel}" value="Total de Docentes por Nível" onclick="setAba('relatorios')"/></li>
											<li><h:commandLink action="#{relatorioPlanejamentoMBean.iniciarRelatorioDocentesDetalhado}" value="Total de Docentes por Tipo de Ação" onclick="setAba('relatorios')"/></li>
											<li><h:commandLink action="#{relatorioPlanejamentoMBean.iniciarRelatorioExternoDetalhado}" value="Total de Participantes Externos por Tipo de Ação" onclick="setAba('relatorios')"/></li>
											<li><h:commandLink action="#{relatorioPlanejamentoMBean.iniciarRelatorioResumoProdutos}" value="Total de Produtos Ativos por Área Temática" onclick="setAba('relatorios')"/></li>
											<li><h:commandLink action="#{relatorioPublicoAtingido.iniciarRelatorioPublicoAtingido}" value="Total de Público Atingido com Base nos Relatórios Submetidos"/></li>
											<li><h:commandLink action="#{relatoriosAtividades.iniciarRelatorioPublicoEstimado}" value="Total de Público Estimado x Público Atingido" onclick="setAba('relatorios')"/></li>
											<li><h:commandLink action="#{relatorioPlanejamentoMBean.iniciarRelatorioTecnicoAdmDetalhado}" value="Total de Técnicos Admin. por Tipo de Ação" onclick="setAba('relatorios')"/></li>
										</ul>
									</li>

								</ul>
							</div>

						 <%-- Para que serve isso se tem o menu_ta.jsp ???? 
						     Se alguém entender o motivo de alguns servidores acessam por aqui eu outro por menu_ta.jsp me explique.
						 --%>
						<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.EXTENSAO_TECNICO_ADMINISTRATIVO } %>">
							<div id="minhas_acoes" class="aba">
								<ul>
									<li>Ações de Extensão
									
									<ul>
						            	<li>Submissão de Propostas
							            	<ul>
							            		<li><h:commandLink action="#{atividadeExtensao.listarCadastrosEmAndamento}" value="Submeter Propostas" onclick="setAba('minhas_acoes')" /></li>
							            		 <c:if test="${acesso.coordenadorExtensao}">
							                		<li><h:commandLink action="#{solicitacaoReconsideracao.iniciarSolicitacaoExtensao}" value="Solicitar Reconsideração de Avaliação"  onclick="setAba('minhas_acoes')"/></li>
							                	 </c:if>
							                	 <li><h:commandLink action="#{atividadeExtensao.preLocalizar}"	value="Consultar ações" onclick="setAba('minhas_acoes')"/></li>		
							            	</ul>
						            	</li>
						            </ul>
						            <c:if test="${acesso.coordenadorExtensao}">
						            <ul>
						            	<li>Inscrições
							            	<ul> 
								                <li><h:commandLink action="#{gerenciarInscricoesCursosEventosExtensaoMBean.listarCursosEventosParaGerenciarInscricao}" value="Gerenciar Inscrições" onclick="setAba('minhas_acoes')"/></li>	
											    <li><h:commandLink id="cmdLinkQuestionariosInscricaoExtensao" action="#{questionarioBean.gerenciarInscricaoAtividade}" value="Questionários para Inscrição" onclick="setAba('minhas_acoes')"/></li>
							            	</ul>
						            	</li>
						            </ul>
						            </c:if>
						            
						            <ul>
						            	<li>Gerenciar Ações
							            	<ul>
							            		<li><h:commandLink value="Listar Minhas Ações" action="#{atividadeExtensao.listarMinhasAtividades}" onclick="setAba('minhas_acoes')"/></li>
												<li><h:commandLink value="Ações com Tempo de Cadastro Expirado" action="#{expirarTempoCadastro.iniciaBuscaAcoesEncerradas}" onclick="setAba('atividade')" /></li>
							            		<li><h:commandLink value="Gerenciar Participantes" action="#{listaAtividadesParticipantesExtensaoMBean.listarAtividadesComParticipantesCoordenador}" onclick="setAba('minhas_acoes')"/></li>
							            		<li><h:commandLink value="Gerenciar Equipe Organizadora" action="#{membroProjeto.gerenciarMembrosProjeto}" onclick="setAba('minhas_acoes')"/></li>
												<li><h:commandLink value="Certificados e Declarações" action="#{documentosAutenticadosExtensao.participacoesServidorUsuarioLogado}" onclick="setAba('minhas_acoes')"/></li>
							            	</ul>
						            	</li>
						            </ul>
					            
					           		
									
			
									<c:if test="${acesso.coordenadorExtensao}">
									    <li>Planos de Trabalho 
								            <ul>
								            	<li><h:commandLink value="Listar Meus Planos de Trabalho" action="#{planoTrabalhoExtensao.listarPlanosCoordenador}" onclick="setAba('minhas_acoes')"/></li>
								                <li><h:commandLink value="Cadastrar Plano de Trabalho de Bolsista" action="#{planoTrabalhoExtensao.iniciarCadastroPlanoBolsista}" onclick="setAba('minhas_acoes')"/></li>
								            	<li><h:commandLink value="Cadastrar Plano de Trabalho de Voluntário" action="#{planoTrabalhoExtensao.iniciarCadastroPlanoVoluntario}" onclick="setAba('minhas_acoes')"/></li>
								                <li><h:commandLink value="Indicar/Substituir Bolsista" action="#{planoTrabalhoExtensao.iniciarAlterarPlano}" onclick="setAba('minhas_acoes')"/></li>
											</ul>
										</li>
									</c:if>
			
			
									<c:if test="${acesso.coordenadorExtensao}">
									    <li>Relatórios 
								            <ul>
								                <li><h:commandLink value="Relatórios de Ações de Extensão" action="#{relatorioAcaoExtensao.iniciarCadastroRelatorio}"/></li>
					                			<li><h:commandLink value="Relatórios de Discentes de Extensão" action="#{relatorioBolsistaExtensao.listarRelatoriosDiscentes}"/></li>
											</ul>
										</li>
									</c:if>
			
									
									<c:if test="${acesso.pareceristaExtensao or acesso.comissaoExtensao}">
									    <li>Avaliar Propostas de Extensão 
								            <ul>
												<c:if test="${acesso.comissaoExtensao}">
													<li><a href="${ctx}/extensao/AvaliacaoAtividade/lista.jsf?aba=minhas_acoes">Avaliar Propostas como Membro do Comitê de Extensão</a></li>
												</c:if>
						
												<c:if test="${acesso.pareceristaExtensao}">
													<li><a href="${ctx}/extensao/AvaliacaoAtividade/lista_parecerista.jsf?aba=minhas_acoes">Avaliar Propostas como Parecerista Ad Hoc</a></li>
												</c:if>
											</ul>
										</li>
									</c:if>
								</ul>
			
								<ul>
									<c:if test="${acesso.chefeUnidade}">
									    <li>Chefia 
								            <ul>
										    	<li><h:commandLink action="#{autorizacaoDepartamento.autorizacaoChefe}"	value="Autorizar Ações de Extensão" rendered="#{acesso.chefeUnidade || acesso.diretorCentro}" onclick="setAba('minhas_acoes')"/></li>
										    	<li><h:commandLink action="#{autorizacaoDepartamento.autorizacaoRelatorioChefe}" value="Validar Relatórios de Ações de Extensão" rendered="#{acesso.chefeUnidade || acesso.diretorCentro}" onclick="setAba('minhas_acoes')"/></li>
											</ul>
										</li>
									</c:if>
								</ul>										
						 </div>
					</ufrn:checkRole>
				</div>
	</h:form>
</f:view>

<div class="linkRodape">
	<html:link action="/verMenuPrincipal">Menu Principal</html:link>
</div>

<script>
						var Abas = {
						    init : function(){
						        var abas = new YAHOO.ext.TabPanel('operacoes-subsistema');
						        
						        <ufrn:checkRole papeis="<%=new int[]{
								SigaaPapeis.APOIO_TECNICO_COORDENACAO_EXTENSAO,
								SigaaPapeis.COORDENADOR_CURSOS_EVENTOS_PRODUTOS_EXTENSAO,
								SigaaPapeis.COORDENADOR_PROGRAMAS_PROJETOS_EXTENSAO,
								SigaaPapeis.GESTOR_EXTENSAO}%>">
						        	abas.addTab('geral', "Informações Gerais");
						        </ufrn:checkRole>
						        
						        <ufrn:checkRole papeis="<%=new int[]{
						SigaaPapeis.COORDENADOR_PROGRAMAS_PROJETOS_EXTENSAO,
						SigaaPapeis.PRESIDENTE_COMITE_EXTENSAO}%>">
									abas.addTab('cpp', "CPP");
								</ufrn:checkRole>
								
								<ufrn:checkRole papeis="<%=new int[]{
								SigaaPapeis.COORDENADOR_CURSOS_EVENTOS_PRODUTOS_EXTENSAO,
								SigaaPapeis.PRESIDENTE_COMITE_EXTENSAO}%>">
									abas.addTab('ccep', "CCEP");
								</ufrn:checkRole>
								
								<ufrn:checkRole papeis="<%=new int[]{
								SigaaPapeis.COORDENADOR_CURSOS_EVENTOS_PRODUTOS_EXTENSAO,
								SigaaPapeis.COORDENADOR_PROGRAMAS_PROJETOS_EXTENSAO,
								SigaaPapeis.PRESIDENTE_COMITE_EXTENSAO}%>">	
									abas.addTab('comite', "Comitê de Extensão");								
									abas.addTab('cadastro', "Cadastros");
								</ufrn:checkRole>

								<ufrn:checkRole papeis="<%=new int[]{SigaaPapeis.EXTENSAO_TECNICO_ADMINISTRATIVO}%>">								
		        					abas.addTab('minhas_acoes', "Gerenciar Minhas Ações");
		        				</ufrn:checkRole>
		        				
								abas.addTab('relatorios', "Relatórios");
								
								
								abas.activate('relatorios');
								
								<ufrn:checkRole papeis="<%=new int[]{SigaaPapeis.PRESIDENTE_COMITE_EXTENSAO}%>">
						        	abas.activate('comite');
						        </ufrn:checkRole>
								
								<ufrn:checkRole papeis="<%=new int[]{SigaaPapeis.COORDENADOR_PROGRAMAS_PROJETOS_EXTENSAO}%>">
						        	abas.activate('cpp');
						        </ufrn:checkRole>

								<ufrn:checkRole papeis="<%=new int[]{SigaaPapeis.COORDENADOR_CURSOS_EVENTOS_PRODUTOS_EXTENSAO}%>">
						        	abas.activate('ccep');
						        </ufrn:checkRole>

								<ufrn:checkRole papeis="<%=new int[]{
						SigaaPapeis.APOIO_TECNICO_COORDENACAO_EXTENSAO,
						SigaaPapeis.GESTOR_EXTENSAO}%>">
						        	abas.activate('geral');
						        </ufrn:checkRole>
					        

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


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>