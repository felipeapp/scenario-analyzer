<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>

<c:set var="hideSubsistema" value="true" />
<f:view>
	<h:form>
	<h2>Menu de Extensão para Servidores Técnicos-administrativos</h2>

	<input type="hidden" name="aba" id="aba"/>
		<div id="operacoes-subsistema" class="reduzido" >

				<div id="atividade" class="aba">
					<ul>
						    <li>Ações de Extensão
					            <ul>
					            	<li>Submissão de Propostas
						            	<ul>
						            		<li><h:commandLink action="#{atividadeExtensao.listarCadastrosEmAndamento}" value="Submeter Propostas"  onclick="setAba('atividade')"/></li>
						            		 <c:if test="${acesso.coordenadorExtensao}">
						                		<li><h:commandLink action="#{solicitacaoReconsideracao.iniciarSolicitacaoExtensao}" value="Solicitar Reconsideração de Avaliação"  onclick="setAba('atividade')"/></li>
						                	 </c:if>
						                	 <li><h:commandLink action="#{atividadeExtensao.preLocalizar}"	value="Consultar ações"  onclick="setAba('atividade')"/></li>		
						            	</ul>
					            	</li>
					            </ul>
					             <c:if test="${acesso.coordenadorExtensao}">
					            <ul>
					            	<li>Inscrições
						            	<ul>
							                <li><h:commandLink action="#{gerenciarInscricoesCursosEventosExtensaoMBean.listarCursosEventosParaGerenciarInscricao}" value="Gerenciar Inscrições"  onclick="setAba('atividade')"/></li>	
										     <li><h:commandLink id="cmdLinkQuestionariosInscricaoExtensao" action="#{questionarioBean.gerenciarInscricaoAtividade}" value="Questionários para Inscrição"  onclick="setAba('atividade')"/></li>	                
						            	</ul>
					            	</li>
					            </ul>
					            </c:if>		
					            
					            <ul>
					            	<li>Gerenciar Ações
						            	<ul>
						            		<li><h:commandLink action="#{atividadeExtensao.listarMinhasAtividades}"	value="Listar Minhas Ações"  onclick="setAba('atividade')"/></li>
						            		<c:if test="${acesso.coordenadorExtensao}">
						            			<li><h:commandLink value="Gerenciar Participantes" action="#{listaAtividadesParticipantesExtensaoMBean.listarAtividadesComParticipantesCoordenador}" onclick="setAba('atividade')"/></li>
						            			<li><h:commandLink value="Equipe Organizadora" action="#{membroProjeto.gerenciarMembrosProjeto}"  onclick="setAba('atividade')" /></li>
						            		</c:if>
						            		<li><h:commandLink action="#{documentosAutenticadosExtensao.participacoesServidorUsuarioLogado}" value="Certificados e Declarações"  onclick="setAba('atividade')" /></li>
						            		<li><h:commandLink action="#{expirarTempoCadastro.iniciaBuscaAcoesEncerradas}" value="Ações com Tempo de Cadastro Expirado"  onclick="setAba('atividade')" /></li>
						            	</ul>
					            	</li>
					            </ul>
					            
					            <%-- 
					            <ul>
					            	<li><h:commandLink action="#{atividadeExtensao.listarMinhasAtividades}"	value="Listar Minhas Ações"/></li>
					               
					                					                
					                <li><h:commandLink action="#{documentosAutenticadosExtensao.participacoesServidorUsuarioLogado}" value="Certificados e Declarações" /></li>
					                <c:if test="${acesso.coordenadorExtensao}">
					                	<li><h:commandLink action="#{solicitacaoReconsideracao.iniciarSolicitacaoExtensao}" value="Solicitar Reconsideração de Avaliação"/></li>
					                	<li><h:commandLink action="#{gerenciarInscricoesCursosEventosExtensaoMBean.listarCursosEventosParaGerenciarInscricao}" value="Gerenciar Inscrições"/></li>	
								        <li><h:commandLink id="cmdLinkQuestionariosInscricaoExtensao" action="#{questionarioBean.gerenciarInscricaoAtividade}" value="Questionários para Inscrição"/></li>	                
					                </c:if>					                
								</ul>
								--%>
							</li>

						<c:if test="${acesso.coordenadorExtensao}">
						    <li>Planos de Trabalho 
					            <ul>
					            	<li><h:commandLink value="Listar Meus Planos de Trabalho" action="#{planoTrabalhoExtensao.listarPlanosCoordenador}"/></li>
					                <li><h:commandLink value="Cadastrar Plano de Trabalho de Bolsista" action="#{planoTrabalhoExtensao.iniciarCadastroPlanoBolsista}"/></li>
					                <li><h:commandLink value="Cadastrar Plano de Trabalho de Voluntário" action="#{planoTrabalhoExtensao.iniciarCadastroPlanoVoluntario}"/></li>
					                <li><h:commandLink value="Indicar/Substituir Bolsista" action="#{planoTrabalhoExtensao.iniciarAlterarPlano}" /></li>
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
										<li><a href="${ctx}/extensao/AvaliacaoAtividade/lista.jsf?aba=atividade">Avaliar Propostas como Membro do Comitê de Extensão</a></li>
									</c:if>
			
									<c:if test="${acesso.pareceristaExtensao}">
										<li><a href="${ctx}/extensao/AvaliacaoAtividade/lista_parecerista.jsf?aba=atividade">Avaliar Propostas como Parecerista Ad Hoc</a></li>
									</c:if>
									<li><a href="${ctx}/extensao/RelatorioAcaoExtensao/busca.jsf?aba=geral">Verificar Relatórios de Ações de Extensão</a></li>
								</ul>
							</li>
						</c:if>
					</ul>

					<ul>
						<c:if test="${acesso.chefeUnidade}">
						    <li>Chefia 
					            <ul>
							    	<li><h:commandLink action="#{autorizacaoDepartamento.autorizacaoChefe}"	value="Autorizar Ações de Extensão" rendered="#{acesso.chefeUnidade || acesso.diretorCentro}"/></li>
							    	<li><h:commandLink action="#{autorizacaoDepartamento.autorizacaoRelatorioChefe}"	value="Validar Relatórios de Ações de Extensão" rendered="#{acesso.chefeUnidade || acesso.diretorCentro}"/></li>
								</ul>
							</li>
						</c:if>
					</ul>										
			 </div>
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
						        abas.addTab('atividade', "Ação de Extensão");
						        abas.activate('atividade');

						    }
						};

						YAHOO.ext.EventManager.onDocumentReady(Abas.init, Abas, true);
			
				</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>