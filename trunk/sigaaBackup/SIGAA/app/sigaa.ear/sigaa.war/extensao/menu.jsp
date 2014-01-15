<%@page import="br.ufrn.arq.seguranca.sigaa.SipacPapeis"%>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%> 
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<%@page import="br.ufrn.arq.seguranca.sigaa.SipacPapeis"%>

<c:set var="hideSubsistema" value="true" />

<f:view>
	<h:form>
	<h2>Menu da Pr�-Reitoria de Extens�o</h2>

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
									    <li>A��es de Extens�o
								            <ul>
								                <li><h:commandLink action="#{atividadeExtensao.iniciar}" value="Cadastrar Proposta" onclick="setAba('geral')" /></li>
								                <li><h:commandLink action="#{atividadeExtensao.listaAlterarAtividade}" value="Gerenciar Proposta de A��o" onclick="setAba('geral')" /></li>
								                <li><a href="${ctx}/extensao/RelatorioAcaoExtensao/busca.jsf?aba=geral">Gerenciar Relat�rios</a></li>
								                <li><h:commandLink action="#{atividadeExtensao.iniciarBuscarAtividadesPeriodoConclusao}" value="Monitorar Finaliza��o de A��es" onclick="setAba('geral')" /></li>
								                <ufrn:checkRole papeis="<%= new int[] {SigaaPapeis.GESTOR_EXTENSAO} %>">
								                	<li><h:commandLink action="#{expirarTempoCadastro.iniciaEncerrar}" value="Expirar A��es com Cadastro em Andamento" onclick="setAba('geral')" /></li>
								                </ufrn:checkRole>
								                <ufrn:checkRole papeis="<%= new int[] {SigaaPapeis.GESTOR_EXTENSAO} %>">
								                	<li><h:commandLink action="#{recuperarAcoes.iniciaRecuperacao}" value="Recuperar A��es Exclu�das" onclick="setAba('geral')" /></li>
								                </ufrn:checkRole>
								                <ufrn:checkRole papeis="<%= new int[] {SigaaPapeis.PRESIDENTE_COMITE_EXTENSAO} %>">
                                                    <li><h:commandLink action="#{movimentacaoCotasExtensao.iniciar}" value="Movimentar Cotas de Bolsas entre A��es de Extens�o" onclick="setAba('geral')" /> </li>
                                                    <li><h:commandLink action="#{vincularUnidadeOrcamentariaMBean.listar}" value="Vincular Unidade Or�ament�ria" onclick="setAba('geral')" /></li>
                                                </ufrn:checkRole>
                                                <li><h:commandLink action="#{comunicarCoordenadores.preComunicarCoordenadoresExtensao}" value="Comunica��o com Coordenadores" onclick="setAba('geral')"/> </li>
											</ul>
										</li>

										<li>Buscas
									         <ul>
									         	<li><h:commandLink action="#{atividadeExtensao.preLocalizar}"	value="Buscar A��es " onclick="setAba('geral')" /></li>
								                <li><h:commandLink action="#{avaliacaoAtividade.iniciarConsultarAvaliacoesAtividade}" value="Buscar Avalia��es de Propostas" onclick="setAba('geral')" /></li>
												<li><a href="${ctx}/extensao/DiscenteExtensao/busca_discente.jsf?aba=geral">Buscar Discentes de Extens�o</a></li>																												
								            </ul>
										</li>
										
										
										
										<c:if test="${acesso.pareceristaExtensao or acesso.comissaoExtensao}">
										    <li>Avaliar Propostas de Extens�o 
									            <ul>
													<c:if test="${acesso.comissaoExtensao}">
														<li><a href="${ctx}/extensao/AvaliacaoAtividade/lista.jsf?aba=geral">Avaliar Propostas como Membro do Comit� de Extens�o</a></li>
													</c:if>
							
													<c:if test="${acesso.pareceristaExtensao}">
														<li><a href="${ctx}/extensao/AvaliacaoAtividade/lista_parecerista.jsf?aba=geral">Avaliar Propostas como Parecerista Ad Hoc</a></li>
													</c:if>
													<li><a href="${ctx}/extensao/RelatorioAcaoExtensao/busca.jsf?aba=geral">Verificar Relat�rios de A��es de Extens�o</a></li>
										
													<li><h:commandLink action="#{filtroAtividades.irTelaAvaliarPresidenteComite}" value="Avalia��o Final de Propostas (Presidente do comit�)" 
														onclick="setAba('geral')" rendered="#{ filtroAtividades.exibir }"/></li>
												</ul>
											</li>
										</c:if>
											
										<ufrn:checkRole papeis="<%= new int[] {	SipacPapeis.GESTOR_BOLSAS_LOCAL	} %>">
										    <li>Bolsas de Extens�o 
									            <ul>
									            	<li><a href="${ctx}/extensao/AlterarAtividade/lista.jsf?aba=geral">Indicar Discente Bolsista ou Volunt�rio</a></li>									            	
									            	<li><a href="${ctx}/extensao/DiscenteExtensao/lista.jsf?aba=geral">Finalizar Discente Bolsista ou Volunt�rio</a></li>									            	
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
									<h3>Coordena��o de Projetos e Programas de Extens�o</h3>
								
									<ul>
										
									    <li>Valida��es 
								            <ul>
												<li><h:commandLink action="#{solicitacaoReconsideracao.listarSolicitacoesPendentesExtensao}" value="Validar Solicita��es de Reconsidera��o" onclick="setAba('cpp')" /></li>
								                <li>
								                	<h:commandLink action="#{relatorioAcaoExtensao.iniciarValidacaoRelatorios}" value="Validar Relat�rios de Projetos e Programas" onclick="setAba('cpp')">
								                		<f:param value="cpp" name="coordenacao" />
								                	</h:commandLink>
								                </li>
												<li><h:commandLink action="#{autorizacaoDepartamento.listarAutorizacoesAcoesDepartamentosProex}" value="Validar A��es como Chefe de Departamento" onclick="setAba('cpp')" /></li>
            									<li><h:commandLink action="#{autorizacaoDepartamento.listarAutorizacoesRelatorioDepartamentoProex}" value="Validar Relat�rios como Chefe de Departamento" onclick="setAba('cpp')" /></li>
        										<li><h:commandLink action="#{homologacaoBolsistaExtensao.iniciarHomologacaoBolsas}"	value="Homologar Cadastro de Bolsistas do FAEx no SIPAC" onclick="setAba('cpp')" /></li>
        										<li><h:commandLink action="#{finalizacaoBolsistaExtensao.listarBolsistasFinalizados}"	value="Finalizar Bolsistas do FAEx no SIPAC " onclick="setAba('geral')" /></li> 
											</ul>
										</li>
										
										<li>Emiss�o de Documentos
								            <ul>
												<li><a href="${ctx}/extensao/DocumentosAutenticados/form.jsf?aba=cpp">Emitir Certifica��o/Declara��o (membros) </a></li>
												<li><h:commandLink action="#{gerenciarParticipantesExtensaoByGestorExtensaoMBean.selecionarAtividadeExtensao}" value="Emitir Certifica��o/Declara��o (participantes)" onclick="setAba('cpp')" /></li>
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
									<h3>Coordena��o de Cursos, Eventos e Produtos de Extens�o</h3>
									<ul>
										
									    <li>Valida��es 
								            <ul>
												<li><h:commandLink action="#{solicitacaoReconsideracao.listarSolicitacoesPendentesExtensao}" value="Validar Solicita��es de Reconsidera��o" onclick="setAba('cpp')" /></li>
								                <li><h:commandLink action="#{relatorioAcaoExtensao.iniciarValidacaoRelatorios}" value="Validar Relat�rios de Cursos, Eventos e Produtos" onclick="setAba('ccep')">
								                   		<f:param value="ccep" name="coordenacao" />
								                	</h:commandLink>
								                </li>
												<li><h:commandLink action="#{autorizacaoDepartamento.listarAutorizacoesAcoesDepartamentosProex}" value="Validar A��es como Chefe de Departamento" onclick="setAba('ccep')" /></li>
	           									<li><h:commandLink action="#{autorizacaoDepartamento.listarAutorizacoesRelatorioDepartamentoProex}" value="Validar Relat�rios como Chefe de Departamento" onclick="setAba('ccep')" /></li>
												<li><h:commandLink action="#{homologacaoBolsistaExtensao.iniciarHomologacaoBolsas}"	value="Homologar Cadastro de Bolsistas do FAEx no SIPAC" onclick="setAba('cpp')" /></li>
        										<li><h:commandLink action="#{finalizacaoBolsistaExtensao.listarBolsistasFinalizados}"	value="Finalizar Bolsistas do FAEx no SIPAC " onclick="setAba('geral')" /></li>	           																	                
											</ul>
										</li>

                                        <li>Inscri��es On-line
                                            <ul>
                                               <li><h:commandLink action="#{gerenciarInscricoesCursosEventosExtensaoMBean.iniciarGerenciamentoInscricoesByGestor}" value="Gerenciar Inscri��es" onclick="setAba('ccep')" /></li>
                                            </ul>
                                        </li>


										<li>Emitir Documentos
								            <ul>
												<li><a href="${ctx}/extensao/DocumentosAutenticados/form.jsf?aba=ccep">Emitir Certifica��o/Declara��o (membros) </a></li>
												<li><h:commandLink action="#{gerenciarParticipantesExtensaoByGestorExtensaoMBean.selecionarAtividadeExtensao}" value="Emitir Certifica��o/Declara��o (participantes)" onclick="setAba('ccep')" /></li>
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
	
										<li> Avalia��es
											<ul>
								                <li><h:commandLink action="#{avaliacaoAtividade.iniciarConsultarAvaliacoesAtividade}" value="Consultar/Remover Avalia��es" onclick="setAba('comite')" /></li>

												<c:if test="${acesso.comissaoExtensao}">
													<li><a href="${ctx}/extensao/AvaliacaoAtividade/lista.jsf?aba=comite">Avaliar Propostas como Membro do Comit�</a></li>
												</c:if>

												<c:if test="${acesso.pareceristaExtensao}">
													<li><a href="${ctx}/extensao/AvaliacaoAtividade/lista_parecerista.jsf?aba=comite">Avaliar Propostas como Parecerista Ad Hoc</a></li>
												</c:if>
												
												<li><a href="${ctx}/extensao/ClassificarAcoes/lista.jsf?aba=comite">Classificar A��es de Extens�o</a></li>
											</ul>
										</li>
	
										<li> Membros do Comit�
											<ul>
												<li><h:commandLink action="#{membroComissao.preCadastrarMembroComissaoExtensao}" value="Cadastrar Membro da Comiss�o" onclick="setAba('comite')"/> </li>
												<li><h:commandLink action="#{membroComissao.listarMembroComissaoExtensao}" value="Alterar/Remover Membro da Comiss�o" onclick="setAba('comite')"/> </li>
											</ul>
										</li>

										<li> Avaliadores Ad Hoc
											<ul>
												<li><h:commandLink action="#{avaliadorExtensao.preCadastrarAvaliadorExtensao}" value="Cadastrar Avaliador" onclick="setAba('comite')"/> </li>
												<li><h:commandLink action="#{avaliadorExtensao.listarAvaliadorExtensao}" value="Listar/Alterar Avaliador" onclick="setAba('comite')"/> </li>
											</ul>
										</li>
	
										<li> Distribuir A��es
											<ul>
												<li> <h:commandLink action="#{filtroAtividades.irTelaDistribuirManualComiteAdHoc}" value="Distribuir para Avaliadores Ad Hoc (Manual)" onclick="setAba('comite')"/>	</li>
												<li> <h:commandLink action="#{filtroAtividades.irTelaDistribuirAutomaticaComiteAdHoc}" value="Distribuir para Avaliadores Ad Hoc (Autom�tica)" onclick="setAba('comite')"/>	</li>										
												<li> <h:commandLink action="#{filtroAtividades.irTelaDistribuirComiteExtensao}" value="Distribuir para Comit� de Extens�o" onclick="setAba('comite')"/> </li>
											</ul>
										</li>
	
									</ul>
								</div>
	
								<div id="cadastro" class="aba">
									<ul>
										<li>�reas Tem�ticas
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
	
	  						          	<li>Fun��o Membro Equipe
								            <ul>
                                                <li><h:commandLink action="#{funcaoMembroEquipe.iniciarCadastro}" value="Cadastrar" onclick="setAba('cadastro')"/></li>
										        <li><a href="${ctx}/extensao/FuncaoMembro/lista.jsf?aba=cadastro">Listar/Alterar</a> </li>
								            </ul>
										</li>
								
	
<!--  							            <li>Natureza Servi�o-->
<!--								            <ul>-->
<!--                                                <li><h:commandLink action="#{naturezaServico.iniciarCadastro}" value="Cadastrar" onclick="setAba('cadastro')"/></li>-->
<!--										        <li><a href="${ctx}/extensao/NaturezaServico/lista.jsf?aba=cadastro">Listar/Alterar</a> </li>-->
<!--								            </ul>-->
<!--										</li>-->
	
<!--							           <li>Tipo de Grupo Presta��o Servi�os-->
<!--								            <ul>-->
<!--                                                <li><h:commandLink action="#{tipoGrupoPrestServico.iniciarCadastro}" value="Cadastrar" onclick="setAba('cadastro')"/></li>												                -->
<!--										        <li><a href="${ctx}/extensao/TipoGrupoPrestServico/lista.jsf?aba=cadastro">Listar/Alterar</a> </li>-->
<!--								            </ul>-->
<!--										</li>-->
	
										<li>Tipo de P�blico Alvo
											<ul>
                                                <li><h:commandLink action="#{tipoPublicoAlvo.preCadastrar}" value="Cadastrar" onclick="setAba('cadastro')"/></li>
												<li><a href="${ctx}/extensao/TipoPublicoAlvo/lista.jsf?aba=cadastro">Alterar/Remover</a></li>
											</ul>
										</li>
														
										<li>Grupo de P�blico Alvo
											<ul>
												<li> <h:commandLink action="#{grupoPublicoAlvo.preCadastrar}" value="Cadastrar" onclick="setAba('cadastro');"/> </li>
												<li><a href="${ctx}/extensao/GrupoPublicoAlvo/lista.jsf?aba=cadastro">Alterar/Remover</a></li>
											</ul>
										</li>
														
										<li>Tipos de Participa��o A��o de Extens�o 
											<ul>
												<li> <h:commandLink action="#{tipoParticipacaoAcaoExtensao.preCadastrar}" value="Cadastrar" onclick="setAba('cadastro');"/> </li>
												<li><h:commandLink action="#{tipoParticipacaoAcaoExtensao.listar}" value="Alterar/Remover" onclick="setAba('cadastro');"/> </li>
											</ul>
										</li>
														
										<li> Grupo de Itens de Avalia��o
											<ul>
												<li><h:commandLink action="#{grupoItemAvaliacaoExtensao.iniciarCadastroGrupo}" value="Cadastrar" onclick="setAba('cadastro');"/> </li>																
												<li> <a href="${ctx}/extensao/GrupoItemAvaliacao/lista.jsf?aba=adm">Alterar/Remover </a></li>
											</ul>
										</li>
														
										<li> Itens de Avalia��o
											<ul>
												<li> <h:commandLink action="#{itemAvaliacaoExtensao.iniciarCadastroItem}" value="Cadastrar" onclick="setAba('cadastro');"/> </li>
												<li> <a href="${ctx}/extensao/ItemAvaliacaoExtensao/lista.jsf?aba=adm">Alterar/Remover </a></li>
											</ul>
										</li>
	
										<li>Calend�rio de Extens�o
											<ul>
												<li><h:commandLink action="#{calendarioExtensao.preCadastrar}" value="Cadastrar/Alterar" onclick="setAba('cadastro');" /></li>
											</ul>
										</li>

										<li>Ficha do question�rio para as a��es de extens�o
											<ul>
												<li> <h:commandLink action="#{questionarioBean.iniciarCadastroFichaExtensao}" value="Cadastrar" onclick="setAba('cadastro');"/> </li>
												<li><h:commandLink action="#{questionarioBean.gerenciarQuestionarioAcaoExtensao}" value="Alterar/Remover" onclick="setAba('cadastro')" /></li>
											</ul>
											<ul>
								            	<li>Associa��es
									            	<ul>
														<li><h:commandLink action="#{questionarioProjetoExtensaoMBean.preCadastrar}" value="Associar aos Usu�rios" onclick="setAba('cadastro')" /></li>
														<li><h:commandLink action="#{questionarioProjetoExtensaoMBean.listar}" value="Listar Associa��es" onclick="setAba('cadastro')" /></li>
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

									<li>Relat�rios Gerais
								         <ul>
											<li><h:commandLink action="#{relatoriosAtividades.iniciarRelatorioEquipePorModalidade}" value="Relat�rio Nominal do Total de Membros por Tipo"/></li>
											<li><h:commandLink action="#{relatoriosAtividades.iniciarRelatorioDiscenteExtensaoPorModalidade}" value="Relat�rio Nominal do Total de Discentes de Extens�o por Tipo"/></li>
											<li><h:commandLink action="#{relatoriosAtividades.iniciarRelatorioNominalAcoesLocalidade}" value="Relat�rio Nominal de A��es por Localidade"/></li>
											<li><h:commandLink action="#{relatoriosAtividades.iniciarRelatorioAcoesFinanciamentoInternoFaexExterno}" value="Relat�rio das A��es que Receberam Financiamento Interno (#{siglasExtensaoMBean.siglaFundoExtensaoPadrao})/Financiamento Externo"/></li>
											<li><h:commandLink action="#{relatoriosAtividades.iniciarRelatorioAcoesLocalRealizacao}" value="Relat�rio das A��es por Local de Realiza��o"/></li>
							            </ul>
									</li>
									
									<li>Relat�rios (INEP)
								         <ul>
							                <li><h:commandLink action="#{relatoriosExtensaoCenso.relatorioTotalProgramasProjetosVinculados}" value="N�mero Total de Programas e seus Respectivos Projetos Vinculados" onclick="setAba('relatorios')" /></li>
							                <li><h:commandLink action="#{relatoriosExtensaoCenso.relatorioTotalProjetosNaoVinculados}" value="N�mero Total de Projetos n�o-Vinculados, P�blico Atendido e Pessoas Envolvidas na Execu��o, Segundo a �rea Tem�tica" onclick="setAba('relatorios')" /></li>
							                <li><h:commandLink action="#{relatoriosExtensaoCenso.relatorioTotalCursosSegundoAreaConhecimento}" value="N�mero Total de Cursos, Total de Carga Hor�ria, Concluintes e Ministrantes em Curso de Extens�o Presencial, Segundo a �rea de Conhecimento CNPq" onclick="setAba('relatorios')" /></li>
							                <li><h:commandLink action="#{relatoriosExtensaoCenso.relatorioTotalEventosSegundoTematicaExtensao}" value="N�mero Total de Eventos Desenvolvidos, por Tipo de Evento e P�blico Participante, Segundo �rea Tem�tica de Extens�o" onclick="setAba('relatorios')" /></li>
							            </ul>
									</li>
									
									<li> Dados de A��es por Edital
										<ul>
																											
											<li><h:commandLink action="#{relatoriosAtividades.iniciarRelatorioDescentralizacaoRecursosFaex}" value="Relat�rio para Descentraliza��o de Recursos do #{siglasExtensaoMBean.siglaFundoExtensaoPadrao}" onclick="setAba('relatorios')"/></li>
											
										</ul>
									</li>
									
									<li>Contatos 
								         <ul>
											<li><h:commandLink action="#{atividadeExtensao.preLocalizarRelatorioCoordenador}" value="Relat�rio com Dados para Contato com Coordenador(a)" onclick="setAba('relatorios')" /></li>
							            </ul>
									</li>
									
									<li>Discentes de Extens�o 
								         <ul>
											<li><a href="${ctx}/extensao/Relatorios/dados_bancarios_discentes_form.jsf?aba=relatorios">Dados Banc�rios de Discentes de Extens�o</a></li>
											<li><h:commandLink action="#{atividadeExtensao.preLocalizar}" value="Relat�rio de Alunos em A��es de Extens�o, Monitoria e Pesquisa" onclick="setAba('relatorios')" /></li>
											<li><h:commandLink action="#{relatoriosAtividades.preLocalizarAcoesSemPlano}" value="Relat�rio de todos os projetos de extens�o que n�o cadastraram planos de trabalho de seus alunos ou sem planos de trabalho" onclick="setAba('relatorios')" /></li>
											
											<%-- Chama a action do struts LogonSistemaAction que cria um passaporte e abre o sipac na pagina do relat�rio de bolsas --%>
                                            <% if (Sistema.isSipacAtivo()) { %>
                                                <li> <ufrn:link action="entrarSistema" param="sistema=sipac&url=/sipac/bolsas/relatorios/situacao_social_bolsas_filtros.jsf"> Situa��o Social dos Bolsistas </ufrn:link> </li>
                                            <% } %>
											
							            </ul>
									</li>

									<li>Relat�rios Quantitativos
										<ul>
											<li><h:commandLink action="#{relatorioPlanejamentoMBean.iniciarRelatorioResumoParticipantes}" value="Total de A��es e Participantes Ativos por �rea Tem�tica" onclick="setAba('relatorios')"/></li>
											<li><h:commandLink action="#{relatorioTotalAcaoEdital.iniciarRelatorioAcoesEdital}" value=" Total de A��es de Extens�o que Concorreram a Editais P�blicos" onclick="setAba('relatorios')"/></li>
											<li><h:commandLink action="#{relatorioPlanejamentoMBean.iniciarRelatorioDiscentesPorVinculoENivel}" value="Total de Discentes Ativos por N�vel e V�nculo" onclick="setAba('relatorios')"/></li>
											<li><h:commandLink action="#{relatorioPlanejamentoMBean.iniciarRelatorioDiscentesPorNivel}" value="Total de Discentes Ativos por N�vel de Ensino" onclick="setAba('relatorios')"/></li>
											<li><h:commandLink action="#{relatorioPlanejamentoMBean.iniciarRelatorioPlanosTrabalho}" value="Total de Discentes com Planos de Trabalho" onclick="setAba('relatorios')"/></li>
											<li><h:commandLink action="#{relatoriosAtividades.iniciarRelatorioTotalDiscentesParticipantesPlanoTrabalhoExtensao}" value="Total de Discentes com Planos de Trabalho Participantes de A��es de Extens�o" onclick="setAba('relatorios')"/></li>
											<li><h:commandLink action="#{relatorioPlanejamentoMBean.iniciarRelatorioDiscenteNaEquipe}" value="Total de Discentes como membros da equipe" onclick="setAba('relatorios')"/></li>
											<li><h:commandLink action="#{relatoriosAtividades.iniciarRelatorioTotalParticipantesProjetoDiscentes}" value="Total de Discentes das Equipes dos Projetos Participantes de A��es de Extens�o" onclick="setAba('relatorios')"/></li>
											<li><h:commandLink action="#{relatoriosAtividades.iniciarRelatorioTotalParticipantesDocentesExtensao}" value="Total de Docentes Participantes de A��es de Extens�o" onclick="setAba('relatorios')"/></li>
											<li><h:commandLink action="#{relatorioPlanejamentoMBean.iniciarRelatorioDocentesPorNivel}" value="Total de Docentes por N�vel" onclick="setAba('relatorios')"/></li>
											<li><h:commandLink action="#{relatorioPlanejamentoMBean.iniciarRelatorioDocentesDetalhado}" value="Total de Docentes por Tipo de A��o" onclick="setAba('relatorios')"/></li>
											<li><h:commandLink action="#{relatorioPlanejamentoMBean.iniciarRelatorioExternoDetalhado}" value="Total de Participantes Externos por Tipo de A��o" onclick="setAba('relatorios')"/></li>
											<li><h:commandLink action="#{relatorioPlanejamentoMBean.iniciarRelatorioResumoProdutos}" value="Total de Produtos Ativos por �rea Tem�tica" onclick="setAba('relatorios')"/></li>
											<li><h:commandLink action="#{relatorioPublicoAtingido.iniciarRelatorioPublicoAtingido}" value="Total de P�blico Atingido com Base nos Relat�rios Submetidos"/></li>
											<li><h:commandLink action="#{relatoriosAtividades.iniciarRelatorioPublicoEstimado}" value="Total de P�blico Estimado x P�blico Atingido" onclick="setAba('relatorios')"/></li>
											<li><h:commandLink action="#{relatorioPlanejamentoMBean.iniciarRelatorioTecnicoAdmDetalhado}" value="Total de T�cnicos Admin. por Tipo de A��o" onclick="setAba('relatorios')"/></li>
										</ul>
									</li>

								</ul>
							</div>

						 <%-- Para que serve isso se tem o menu_ta.jsp ???? 
						     Se algu�m entender o motivo de alguns servidores acessam por aqui eu outro por menu_ta.jsp me explique.
						 --%>
						<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.EXTENSAO_TECNICO_ADMINISTRATIVO } %>">
							<div id="minhas_acoes" class="aba">
								<ul>
									<li>A��es de Extens�o
									
									<ul>
						            	<li>Submiss�o de Propostas
							            	<ul>
							            		<li><h:commandLink action="#{atividadeExtensao.listarCadastrosEmAndamento}" value="Submeter Propostas" onclick="setAba('minhas_acoes')" /></li>
							            		 <c:if test="${acesso.coordenadorExtensao}">
							                		<li><h:commandLink action="#{solicitacaoReconsideracao.iniciarSolicitacaoExtensao}" value="Solicitar Reconsidera��o de Avalia��o"  onclick="setAba('minhas_acoes')"/></li>
							                	 </c:if>
							                	 <li><h:commandLink action="#{atividadeExtensao.preLocalizar}"	value="Consultar a��es" onclick="setAba('minhas_acoes')"/></li>		
							            	</ul>
						            	</li>
						            </ul>
						            <c:if test="${acesso.coordenadorExtensao}">
						            <ul>
						            	<li>Inscri��es
							            	<ul> 
								                <li><h:commandLink action="#{gerenciarInscricoesCursosEventosExtensaoMBean.listarCursosEventosParaGerenciarInscricao}" value="Gerenciar Inscri��es" onclick="setAba('minhas_acoes')"/></li>	
											    <li><h:commandLink id="cmdLinkQuestionariosInscricaoExtensao" action="#{questionarioBean.gerenciarInscricaoAtividade}" value="Question�rios para Inscri��o" onclick="setAba('minhas_acoes')"/></li>
							            	</ul>
						            	</li>
						            </ul>
						            </c:if>
						            
						            <ul>
						            	<li>Gerenciar A��es
							            	<ul>
							            		<li><h:commandLink value="Listar Minhas A��es" action="#{atividadeExtensao.listarMinhasAtividades}" onclick="setAba('minhas_acoes')"/></li>
												<li><h:commandLink value="A��es com Tempo de Cadastro Expirado" action="#{expirarTempoCadastro.iniciaBuscaAcoesEncerradas}" onclick="setAba('atividade')" /></li>
							            		<li><h:commandLink value="Gerenciar Participantes" action="#{listaAtividadesParticipantesExtensaoMBean.listarAtividadesComParticipantesCoordenador}" onclick="setAba('minhas_acoes')"/></li>
							            		<li><h:commandLink value="Gerenciar Equipe Organizadora" action="#{membroProjeto.gerenciarMembrosProjeto}" onclick="setAba('minhas_acoes')"/></li>
												<li><h:commandLink value="Certificados e Declara��es" action="#{documentosAutenticadosExtensao.participacoesServidorUsuarioLogado}" onclick="setAba('minhas_acoes')"/></li>
							            	</ul>
						            	</li>
						            </ul>
					            
					           		
									
			
									<c:if test="${acesso.coordenadorExtensao}">
									    <li>Planos de Trabalho 
								            <ul>
								            	<li><h:commandLink value="Listar Meus Planos de Trabalho" action="#{planoTrabalhoExtensao.listarPlanosCoordenador}" onclick="setAba('minhas_acoes')"/></li>
								                <li><h:commandLink value="Cadastrar Plano de Trabalho de Bolsista" action="#{planoTrabalhoExtensao.iniciarCadastroPlanoBolsista}" onclick="setAba('minhas_acoes')"/></li>
								            	<li><h:commandLink value="Cadastrar Plano de Trabalho de Volunt�rio" action="#{planoTrabalhoExtensao.iniciarCadastroPlanoVoluntario}" onclick="setAba('minhas_acoes')"/></li>
								                <li><h:commandLink value="Indicar/Substituir Bolsista" action="#{planoTrabalhoExtensao.iniciarAlterarPlano}" onclick="setAba('minhas_acoes')"/></li>
											</ul>
										</li>
									</c:if>
			
			
									<c:if test="${acesso.coordenadorExtensao}">
									    <li>Relat�rios 
								            <ul>
								                <li><h:commandLink value="Relat�rios de A��es de Extens�o" action="#{relatorioAcaoExtensao.iniciarCadastroRelatorio}"/></li>
					                			<li><h:commandLink value="Relat�rios de Discentes de Extens�o" action="#{relatorioBolsistaExtensao.listarRelatoriosDiscentes}"/></li>
											</ul>
										</li>
									</c:if>
			
									
									<c:if test="${acesso.pareceristaExtensao or acesso.comissaoExtensao}">
									    <li>Avaliar Propostas de Extens�o 
								            <ul>
												<c:if test="${acesso.comissaoExtensao}">
													<li><a href="${ctx}/extensao/AvaliacaoAtividade/lista.jsf?aba=minhas_acoes">Avaliar Propostas como Membro do Comit� de Extens�o</a></li>
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
										    	<li><h:commandLink action="#{autorizacaoDepartamento.autorizacaoChefe}"	value="Autorizar A��es de Extens�o" rendered="#{acesso.chefeUnidade || acesso.diretorCentro}" onclick="setAba('minhas_acoes')"/></li>
										    	<li><h:commandLink action="#{autorizacaoDepartamento.autorizacaoRelatorioChefe}" value="Validar Relat�rios de A��es de Extens�o" rendered="#{acesso.chefeUnidade || acesso.diretorCentro}" onclick="setAba('minhas_acoes')"/></li>
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
						        	abas.addTab('geral', "Informa��es Gerais");
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
									abas.addTab('comite', "Comit� de Extens�o");								
									abas.addTab('cadastro', "Cadastros");
								</ufrn:checkRole>

								<ufrn:checkRole papeis="<%=new int[]{SigaaPapeis.EXTENSAO_TECNICO_ADMINISTRATIVO}%>">								
		        					abas.addTab('minhas_acoes', "Gerenciar Minhas A��es");
		        				</ufrn:checkRole>
		        				
								abas.addTab('relatorios', "Relat�rios");
								
								
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