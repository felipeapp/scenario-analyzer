<%@page import="br.ufrn.arq.seguranca.sigaa.SipacPapeis"%>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%> 
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<%@page import="br.ufrn.arq.seguranca.sigaa.SipacPapeis"%>

<c:set var="hideSubsistema" value="true" />

<f:view>
	<h:form>
	<h2>Menu de Ações Acadêmicas Integradas</h2>

	<input type="hidden" name="aba" id="aba" />
		<div id="operacoes-subsistema" class="reduzido">
		
		
			<div id="geral" class="aba">
				<ul>
				    <li>Ações Acadêmicas Integradas
			            <ul>
							<li><h:commandLink action="#{ avaliacaoProjetoBean.listarAvaliacoes }" value="Avaliar" onclick="setAba('comite')" /></li>			                
			                <li><h:commandLink action="#{ buscaAcaoAssociada.iniciar }" value="Buscar" onclick="setAba('geral')"/></li>
						</ul>
					</li>
				</ul>
					

				<ufrn:checkRole papeis="<%= new int[] {SigaaPapeis.MEMBRO_COMITE_INTEGRADO} %>">
					<ul>
						<li>Calendário 
							<ul>
								<li><h:commandLink action="#{calendarioAcoesAssociadas.preCadastrar}" value="Cadastrar" onclick="setAba('geral')"/></li>
								<li><h:commandLink action="#{calendarioAcoesAssociadas.listarCalendarios}" value="Listar/Alterar" onclick="setAba('geral')" /></li>
							</ul>
						</li>
					</ul>
					
					<ul>
					    <li>Discentes
				            <ul>
								<li><h:commandLink action="#{discenteProjetoBean.iniciarBusca}" value="Buscar" onclick="setAba('geral')" /></li>
							</ul>
						</li>
					</ul>

					<ul>					
						<li>Relatórios
				            <ul>
				                <li><h:commandLink action="#{ buscaRelatoriosProjetosBean.iniciarBusca }" value="Buscar" onclick="setAba('geral')"/></li>
							</ul>
						</li>	
					</ul>					
										
				</ufrn:checkRole>
			</div>
							
			<ufrn:checkRole papeis="<%= new int[] {SigaaPapeis.MEMBRO_COMITE_INTEGRADO} %>">
				<div id="comite" class="aba">
				
					<ul>
					    <li>Ações Acadêmicas Integradas
				            <ul>
								<li><h:commandLink action="#{avaliacaoProjetoBean.listarAvaliacoes}" value="Avaliar" onclick="setAba('comite')" /></li>
								<li><h:commandLink action="#{buscaAcaoAssociada.iniciar}" value="Buscar" onclick="setAba('comite')"/></li>			                
								<li><h:commandLink action="#{comunicarCoordenadores.preComunicarCoordenadores}" value="Comunicação com Coordenadores" onclick="setAba('comite')"/> </li>
								<li><h:commandLink action="#{alteracaoProjetoMBean.listaConcederRecursos}" value="Conceder Recursos" onclick="setAba('comite')"/></li>
			                	<li><h:commandLink action="#{alteracaoProjetoMBean.iniciarAlterarProjeto}" value="Gerenciar" onclick="setAba('comite')" /></li>
			                	<li><h:commandLink action="#{solicitacaoReconsideracao.listarSolicitacoesPendentesProjeto}" value="Reconsiderações" onclick="setAba('comite')" /></li>
			                	<li><h:commandLink action="#{alteracaoProjetoMBean.listaVincularUnidadeOrcamentaria}" value="Vincular Unidade Orcamentária" onclick="setAba('comite')"/></li>
			                	<li><h:commandLink action="#{recuperarAcoesAssociadas.iniciaRecuperacao}" value="Recuperar Ações Integradas Excluídas" onclick="setAba('comite')" /></li>
							</ul>
						</li>
					</ul>

				
					<ul>
						<li> Avaliações
							<ul>
								<li><h:commandLink action="#{buscaAvaliacoesProjetosBean.iniciarBusca}" value="Buscar" onclick="setAba('comite')"/></li>
								<li><h:commandLink action="#{distribuicaoProjetoMbean.listar}" value="Distribuir" onclick="setAba('comite')"/></li>								
							</ul>
						</li>
						
						<li>Publicar Resultados
							<ul>
								<li><h:commandLink action="#{distribuicaoProjetoMbean.listaConsolidarAvaliacoes}" value="Consolidar Avaliações" onclick="setAba('comite')"/></li>
								<li><h:commandLink action="#{classificarProjetosBean.preView}" value="Classificar" onclick="setAba('comite')"/></li>
							</ul>
						</li>									
				
						<li> Comitê Integrado de Ensino, Pesquisa e Extensão
							<ul>
								<li><h:commandLink action="#{membroComissao.preCadastrarMembroCIEPE}" value="Cadastrar" onclick="setAba('comite')" /></li>
								<li><h:commandLink action="#{membroComissao.listarMembroCIEPE}" value="Listar/Alterar" onclick="setAba('comite')" /></li>
							</ul>
						</li>
						
						<li>Consultores Ad hoc		
							<ul>
								<li><h:commandLink action="#{avaliadorProjetoMbean.preCadastrar}" value="Cadastrar" onclick="setAba('comite')" /></li>
								<li><h:commandLink action="#{avaliadorProjetoMbean.listar}" value="Listar/Alterar" onclick="setAba('comite')" /></li>
							</ul>
						</li>
						
						<li> Configurar Avaliações
							<ul>
								<li>Grupos
									<ul>
										<li><h:commandLink action="#{grupoAvaliacao.preCadastrarGrupo}" value="Cadastrar" onclick="setAba('comite')" /></li>
										<li><h:commandLink action="#{grupoAvaliacao.iniciarBuscaGrupos}" value="Listar/Alterar" onclick="setAba('comite')" /></li>
									</ul>
								</li>	
							</ul>
	
							<ul>
								<li>Perguntas
									<ul>
										<li><h:commandLink action="#{grupoAvaliacao.iniciarCadastroPergunta}" value="Cadastrar" onclick="setAba('comite')" /></li>
										<li><h:commandLink action="#{grupoAvaliacao.iniciarBuscaPerguntas}" value="Listar/Alterar" onclick="setAba('comite')" /></li>
									</ul>
								</li>	
							</ul>
	
							<ul>
								<li>Questionários
									<ul>
										<li><h:commandLink action="#{questionarioAvaliacao.iniciarCadastroQuestionario}" value="Cadastrar" onclick="setAba('comite')" /></li>
										<li><h:commandLink action="#{questionarioAvaliacao.iniciarBuscaQuestionarios}" value="Listar/Alterar" onclick="setAba('comite')" /></li>
									</ul>
								</li>	
							</ul>
	
							<ul>
								<li>Modelos
									<ul>
										<li><h:commandLink action="#{modeloAvaliacao.iniciarCadastroModeloAvaliacao}" value="Cadastrar" onclick="setAba('comite')" /></li>
										<li><h:commandLink action="#{modeloAvaliacao.iniciarBuscaModelos}" value="Listar/Alterar" onclick="setAba('comite')" /></li>
									</ul>
								</li>	
							</ul>						
						</li>
						
						<li>Editais
					         <ul>
					         	<li><h:commandLink action="#{editalMBean.preCadastrar}"	value="Cadastrar" onclick="setAba('geral')"/></li>
					         	<li><h:commandLink action="#{editalMBean.getBusca}"	value="Buscar" onclick="setAba('geral')" /></li>								                
					         	<li><h:commandLink action="#{editalMBean.listar}"	value="Listar/Alterar" onclick="setAba('geral')"/></li>
				            </ul>
						</li>
						
						<li>Situações da Avaliação		
							<ul>
								<li><h:commandLink action="#{situacaoAvaliacaoBean.preCadastrar}" value="Cadastrar" onclick="setAba('comite')" /></li>
								<li><h:commandLink action="#{situacaoAvaliacaoBean.toLista}" value="Listar/Alterar" onclick="setAba('comite')" /></li>
							</ul>
						</li>
						
						
						<li>Relatórios de Ações Acadêmicas Integradas  
							<ul>
								<li><h:commandLink action="#{validacaoRelatorioBean.relatoriosPendenteComite}" value="Validar Relatórios de Ações Integradas" onclick="setAba('comite')" /></li>
							</ul>
						</li>
						
					</ul>
										
				</div>
			</ufrn:checkRole>
			
			<ufrn:checkRole papeis="<%=new int[]{SigaaPapeis.MEMBRO_COMITE_INTEGRADO}%>">
				<div id="relatorios" class="aba">
					<ul>
						<li> Quantitativos
							<ul>
								<li><h:commandLink action="#{relatoriosAcaoAcademica.iniciarRelatorioQuantProjSubReuni}" value="Ações Submetidas" onclick="setAba('comite')" /></li>
							</ul>
						</li>
					</ul>
				</div>
			</ufrn:checkRole>

			
			<ufrn:checkRole papeis="<%=new int[]{SigaaPapeis.GESTOR_BOLSAS_ACOES_ASSOCIADAS}%>">
				<div id="bolsas" class="aba">
					<ul>
					    <li>Discentes dos Projetos
				            <ul>
								<li><h:commandLink action="#{discenteProjetoBean.iniciarBusca}" value="Buscar" onclick="setAba('bolsas')" /></li>
								<li><h:commandLink action="#{discenteProjetoBean.iniciarDadosBancariosDiscentes}" value="Dados Bancários" onclick="setAba('bolsas')" /></li>
							</ul>
						</li>
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
		        
		        <ufrn:checkRole papeis="<%=new int[]{SigaaPapeis.MEMBRO_COMITE_INTEGRADO, SigaaPapeis.AVALIADOR_ACOES_ASSOCIADAS, SigaaPapeis.GESTOR_BOLSAS_ACOES_ASSOCIADAS}%>">
		        	abas.addTab('geral', "Informações Gerais");
		        </ufrn:checkRole>
		        
		        <ufrn:checkRole papeis="<%=new int[]{SigaaPapeis.MEMBRO_COMITE_INTEGRADO}%>">
					abas.addTab('comite', "Comitê Interno");
				</ufrn:checkRole>
				
				<ufrn:checkRole papeis="<%=new int[]{SigaaPapeis.MEMBRO_COMITE_INTEGRADO}%>">
					abas.addTab('relatorios', "Relatórios");
				</ufrn:checkRole>

				<ufrn:checkRole papeis="<%=new int[]{SigaaPapeis.GESTOR_BOLSAS_ACOES_ASSOCIADAS}%>">
					abas.addTab('bolsas', "Bolsas");
				</ufrn:checkRole>

				
				abas.activate('geral');
	        
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