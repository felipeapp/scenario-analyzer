<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>

<c:set var="hideSubsistema" value="true" />
<f:view>
	<h:form>
	<h2>Menu de Extens�o para Servidores T�cnicos-administrativos</h2>

	<input type="hidden" name="aba" id="aba"/>
		<div id="operacoes-subsistema" class="reduzido" >

				<div id="atividade" class="aba">
					<ul>
						    <li>A��es de Extens�o
					            <ul>
					            	<li>Submiss�o de Propostas
						            	<ul>
						            		<li><h:commandLink action="#{atividadeExtensao.listarCadastrosEmAndamento}" value="Submeter Propostas"  onclick="setAba('atividade')"/></li>
						            		 <c:if test="${acesso.coordenadorExtensao}">
						                		<li><h:commandLink action="#{solicitacaoReconsideracao.iniciarSolicitacaoExtensao}" value="Solicitar Reconsidera��o de Avalia��o"  onclick="setAba('atividade')"/></li>
						                	 </c:if>
						                	 <li><h:commandLink action="#{atividadeExtensao.preLocalizar}"	value="Consultar a��es"  onclick="setAba('atividade')"/></li>		
						            	</ul>
					            	</li>
					            </ul>
					             <c:if test="${acesso.coordenadorExtensao}">
					            <ul>
					            	<li>Inscri��es
						            	<ul>
							                <li><h:commandLink action="#{gerenciarInscricoesCursosEventosExtensaoMBean.listarCursosEventosParaGerenciarInscricao}" value="Gerenciar Inscri��es"  onclick="setAba('atividade')"/></li>	
										     <li><h:commandLink id="cmdLinkQuestionariosInscricaoExtensao" action="#{questionarioBean.gerenciarInscricaoAtividade}" value="Question�rios para Inscri��o"  onclick="setAba('atividade')"/></li>	                
						            	</ul>
					            	</li>
					            </ul>
					            </c:if>		
					            
					            <ul>
					            	<li>Gerenciar A��es
						            	<ul>
						            		<li><h:commandLink action="#{atividadeExtensao.listarMinhasAtividades}"	value="Listar Minhas A��es"  onclick="setAba('atividade')"/></li>
						            		<c:if test="${acesso.coordenadorExtensao}">
						            			<li><h:commandLink value="Gerenciar Participantes" action="#{listaAtividadesParticipantesExtensaoMBean.listarAtividadesComParticipantesCoordenador}" onclick="setAba('atividade')"/></li>
						            			<li><h:commandLink value="Equipe Organizadora" action="#{membroProjeto.gerenciarMembrosProjeto}"  onclick="setAba('atividade')" /></li>
						            		</c:if>
						            		<li><h:commandLink action="#{documentosAutenticadosExtensao.participacoesServidorUsuarioLogado}" value="Certificados e Declara��es"  onclick="setAba('atividade')" /></li>
						            		<li><h:commandLink action="#{expirarTempoCadastro.iniciaBuscaAcoesEncerradas}" value="A��es com Tempo de Cadastro Expirado"  onclick="setAba('atividade')" /></li>
						            	</ul>
					            	</li>
					            </ul>
					            
					            <%-- 
					            <ul>
					            	<li><h:commandLink action="#{atividadeExtensao.listarMinhasAtividades}"	value="Listar Minhas A��es"/></li>
					               
					                					                
					                <li><h:commandLink action="#{documentosAutenticadosExtensao.participacoesServidorUsuarioLogado}" value="Certificados e Declara��es" /></li>
					                <c:if test="${acesso.coordenadorExtensao}">
					                	<li><h:commandLink action="#{solicitacaoReconsideracao.iniciarSolicitacaoExtensao}" value="Solicitar Reconsidera��o de Avalia��o"/></li>
					                	<li><h:commandLink action="#{gerenciarInscricoesCursosEventosExtensaoMBean.listarCursosEventosParaGerenciarInscricao}" value="Gerenciar Inscri��es"/></li>	
								        <li><h:commandLink id="cmdLinkQuestionariosInscricaoExtensao" action="#{questionarioBean.gerenciarInscricaoAtividade}" value="Question�rios para Inscri��o"/></li>	                
					                </c:if>					                
								</ul>
								--%>
							</li>

						<c:if test="${acesso.coordenadorExtensao}">
						    <li>Planos de Trabalho 
					            <ul>
					            	<li><h:commandLink value="Listar Meus Planos de Trabalho" action="#{planoTrabalhoExtensao.listarPlanosCoordenador}"/></li>
					                <li><h:commandLink value="Cadastrar Plano de Trabalho de Bolsista" action="#{planoTrabalhoExtensao.iniciarCadastroPlanoBolsista}"/></li>
					                <li><h:commandLink value="Cadastrar Plano de Trabalho de Volunt�rio" action="#{planoTrabalhoExtensao.iniciarCadastroPlanoVoluntario}"/></li>
					                <li><h:commandLink value="Indicar/Substituir Bolsista" action="#{planoTrabalhoExtensao.iniciarAlterarPlano}" /></li>
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
										<li><a href="${ctx}/extensao/AvaliacaoAtividade/lista.jsf?aba=atividade">Avaliar Propostas como Membro do Comit� de Extens�o</a></li>
									</c:if>
			
									<c:if test="${acesso.pareceristaExtensao}">
										<li><a href="${ctx}/extensao/AvaliacaoAtividade/lista_parecerista.jsf?aba=atividade">Avaliar Propostas como Parecerista Ad Hoc</a></li>
									</c:if>
									<li><a href="${ctx}/extensao/RelatorioAcaoExtensao/busca.jsf?aba=geral">Verificar Relat�rios de A��es de Extens�o</a></li>
								</ul>
							</li>
						</c:if>
					</ul>

					<ul>
						<c:if test="${acesso.chefeUnidade}">
						    <li>Chefia 
					            <ul>
							    	<li><h:commandLink action="#{autorizacaoDepartamento.autorizacaoChefe}"	value="Autorizar A��es de Extens�o" rendered="#{acesso.chefeUnidade || acesso.diretorCentro}"/></li>
							    	<li><h:commandLink action="#{autorizacaoDepartamento.autorizacaoRelatorioChefe}"	value="Validar Relat�rios de A��es de Extens�o" rendered="#{acesso.chefeUnidade || acesso.diretorCentro}"/></li>
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
						        abas.addTab('atividade', "A��o de Extens�o");
						        abas.activate('atividade');

						    }
						};

						YAHOO.ext.EventManager.onDocumentReady(Abas.init, Abas, true);
			
				</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>