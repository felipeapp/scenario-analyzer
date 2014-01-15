
	<a4j:outputPanel id="tabsWrapper" layout="block">
		<rich:tabPanel id="projSubTabPanel" switchType="ajax" selectedTab="#{pesquisaMBean.subAbaProjetosAtiva}">
			<rich:tab id="tabPesquisa" onclick="changeTab('tabPesquisa');Event.stop(event);">
				<f:facet name="label">
			         <h:panelGroup>
			             <h:outputText value="Projetos de Pesquisa"/>
			         </h:panelGroup>
			    </f:facet>
			    <ul>
					<li> Projetos de Pesquisa
			        	<ul>
							<li>
								<h:commandLink value="Gerenciar" actionListener="#{pesquisaMBean.redirecionar}" onclick="setAba('projetos')">
									<f:param name="url" value="/pesquisa/projetoPesquisa/buscarProjetos.do?dispatch=consulta&popular=true" />
								</h:commandLink>
							</li>
			            	<li>
			            		<h:commandLink value="Alterar Situação" actionListener="#{pesquisaMBean.redirecionar}" onclick="setAba('projetos')">
									<f:param name="url" value="/pesquisa/projetoPesquisa/buscarProjetos.do?dispatch=alterarSituacao&popular=true" />
								</h:commandLink>
			            	</li>
						 	<li>
						 		<h:commandLink value="Consultar" actionListener="#{pesquisaMBean.redirecionar}" onclick="setAba('projetos')">
									<f:param name="url" value="/pesquisa/projetoPesquisa/buscarProjetos.do?dispatch=consulta&popular=true&consulta=true" />
								</h:commandLink>
						 	</li>
						 	<li>
						 		<h:commandLink value="Cadastrar Projeto Interno" actionListener="#{pesquisaMBean.redirecionar}" onclick="setAba('projetos')">
									<f:param name="url" value="/pesquisa/projetoPesquisa/criarProjetoPesquisa.do?dispatch=popular&interno=true" />
								</h:commandLink>
						 	</li>
						 	<li>
						 		<h:commandLink value="Cadastrar Projeto Externo" actionListener="#{pesquisaMBean.redirecionar}" onclick="setAba('projetos')">
									<f:param name="url" value="/pesquisa/projetoPesquisa/criarProjetoPesquisa.do?dispatch=popular" />
								</h:commandLink>
						 	</li>
			        	</ul>
					</li>
			        <li> Distribuição para Consultores
			            <ul>
							<li>
								<h:commandLink value="Distribuir Automaticamente" actionListener="#{pesquisaMBean.redirecionar}" onclick="setAba('projetos')">
									<f:param name="url" value="/pesquisa/distribuirProjetoPesquisa.do?dispatch=popularAutomatica" />
								</h:commandLink>
							</li>
							<li>
								<h:commandLink value="Distribuir Automaticamente para Consultores Especiais" actionListener="#{pesquisaMBean.redirecionar}" onclick="setAba('projetos')">
									<f:param name="url" value="/pesquisa/distribuirProjetoPesquisa.do?dispatch=popularAutomaticaEspeciais" />
								</h:commandLink>
							</li>
							<li>
								<h:commandLink value="Distribuir Manualmente" actionListener="#{pesquisaMBean.redirecionar}" onclick="setAba('projetos')">
									<f:param name="url" value="/pesquisa/distribuirProjetoPesquisa.do?dispatch=popularManual" />
								</h:commandLink>
							</li>
							<li>
								<h:commandLink value="Notificar Consultores" actionListener="#{pesquisaMBean.redirecionar}" onclick="setAba('projetos')">
									<f:param name="url" value="/pesquisa/notificarConsultores.do?dispatch=popular" />
								</h:commandLink>
							</li>
							<li>
								<h:commandLink value="Gerenciar Consultoria Especial" actionListener="#{pesquisaMBean.redirecionar}" onclick="setAba('projetos')">
									<f:param name="url" value="/pesquisa/cadastroConsultoriaEspecial.do?dispatch=edit" />
								</h:commandLink>
							</li>
			            </ul>
					</li>
			        <li> Avaliação de Projetos
			            <ul>
			            	<li>
								<h:commandLink value="Encerrar avaliações pendentes" actionListener="#{pesquisaMBean.redirecionar}" onclick="setAba('projetos')">
									<f:param name="url" value="/pesquisa/avaliarProjetoPesquisa.do?dispatch=listarPendentes" />
								</h:commandLink>
			            	</li>
							<li>
								<h:commandLink value="Consultar Avaliações" actionListener="#{pesquisaMBean.redirecionar}" onclick="setAba('projetos')">
									<f:param name="url" value="/pesquisa/avaliarProjetoPesquisa.do?dispatch=list&popular=true" />
								</h:commandLink>
							</li>
			            	<li>
								<h:commandLink value="Analisar Avaliações" actionListener="#{pesquisaMBean.redirecionar}" onclick="setAba('projetos')">
									<f:param name="url" value="/pesquisa/analisarAvaliacoes.do?dispatch=list&popular=true" />
								</h:commandLink>
			            	</li>
			            	<li>
			            		<h:commandLink value="Quantidade de Avaliações por Projeto" action="#{ buscaAvaliacaoProjetoPesquisaMBean.iniciarBuscaAvaliacoesProjeto }" onclick="setAba('projetos')"/>
			            	</li>
			            </ul>
					</li>
			    	<li> Outras Operações
			    		<ul>
			        		<li>
			        			<h:commandLink value="Editar Parâmetros do Módulo" actionListener="#{pesquisaMBean.redirecionar}" onclick="setAba('projetos')">
									<f:param name="url" value="/pesquisa/manterParametrosPesquisa.do?dispatch=popular" />
								</h:commandLink>
			        		</li>
			        		<li><h:commandLink value="Calendário" action="#{ calendarioPesquisa.iniciar }" onclick="setAba('projetos')"/></li>
			    		</ul>
					</li>
					
					<li> Projetos de Infra-Estrutura em Pesquisa
						<ul>
							<li><h:commandLink value="Cadastrar" action="#{ projetoInfraPesq.iniciar }" onclick="setAba('projetos')"/></li>
							<li><h:commandLink value="Alterar/Remover" action="#{ projetoInfraPesq.listar }" onclick="setAba('projetos')"/></li>
						</ul>
					</li>
				
					<li> Cotas
						<ul>
						    <li><h:commandLink value="Cadastrar" action="#{ cotaBolsasMBean.preCadastrar }" onclick="setAba('projetos')"/></li>
				  	        <li><h:commandLink value="Listar/Alterar" action="#{ cotaBolsasMBean.listar }" onclick="setAba('projetos')"/></li>
						</ul>
					</li>
					
					<li> Editais
						<ul>
						    <li><h:commandLink value="Cadastrar" action="#{ editalPesquisaMBean.preCadastrar }" onclick="setAba('projetos')"/></li>
				  	        <li><h:commandLink value="Alterar/Remover" action="#{ editalPesquisaMBean.iniciar }" onclick="setAba('projetos')"/></li>
						</ul>
					</li>
			
					<li> Configuração de Unidades
						<ul>
						    <li><h:commandLink value="Cadastrar" action="#{ siglaUnidadePesquisaMBean.preCadastrar }" onclick="setAba('projetos')"/></li>
				  	        <li><h:commandLink value="Listar/Alterar" action="#{ siglaUnidadePesquisaMBean.listar }" onclick="setAba('projetos')"/></li>
						</ul>
					</li>
				</ul>
			</rich:tab>
			<rich:tab id="tabApoio" onclick="changeTab('tabApoio');Event.stop(event);">
				<f:facet name="label">
			         <h:panelGroup>
			             <h:outputText value="Projetos de Apoio"/>
			         </h:panelGroup>
			    </f:facet>
				<ul>
				<%-- 
					<li> Projetos de Apoio a Grupos de Pesquisa
						<ul>
							<li><h:commandLink action="#{projetoApoioGrupoPesquisaMBean.listar}" value="Listar/Alterar" onclick="setAba('projetos')"/></li>								
						</ul>
					</li>
				--%>
					<li> Projetos de Apoio a Novos Pesquisadores
						<ul>
							<li><h:commandLink action="#{buscaProjetoApoioNovosPesquisadoresMBean.iniciar}" value="Listar/Alterar" onclick="setAba('projetos')"/></li>								
						</ul>
					</li>
				
					<li> Avaliações
						<ul>
							<li><h:commandLink action="#{buscaAvaliacoesProjetosBean.iniciarBusca}" value="Buscar" onclick="setAba('projetos')"/></li>
							<li><h:commandLink action="#{distribuicaoProjetoMbean.listar}" value="Distribuir" onclick="setAba('projetos')"/></li>								
						</ul>
					</li>
					
					<li>Publicar Resultados
						<ul>
							<li><h:commandLink action="#{distribuicaoProjetoMbean.listaConsolidarAvaliacoes}" value="Consolidar Avaliações" onclick="setAba('projetos')"/></li>
							<li><h:commandLink action="#{classificarProjetosBean.preView}" value="Classificar" onclick="setAba('projetos')"/></li>
						</ul>
					</li>									
			
					<li>Consultores Ad hoc		
						<ul>
							<li><h:commandLink action="#{avaliadorProjetoMbean.preCadastrar}" value="Cadastrar" onclick="setAba('projetos')" /></li>
							<li><h:commandLink action="#{avaliadorProjetoMbean.listar}" value="Listar/Alterar" onclick="setAba('projetos')" /></li>
						</ul>
					</li>
					
					<li> Configurar Avaliações
						<ul>
							<li>Grupos
								<ul>
									<li><h:commandLink action="#{grupoAvaliacao.preCadastrarGrupo}" value="Cadastrar" onclick="setAba('projetos')" /></li>
									<li><h:commandLink action="#{grupoAvaliacao.iniciarBuscaGrupos}" value="Listar/Alterar" onclick="setAba('projetos')" /></li>
								</ul>
							</li>	
						</ul>
			
						<ul>
							<li>Perguntas
								<ul>
									<li><h:commandLink action="#{grupoAvaliacao.iniciarCadastroPergunta}" value="Cadastrar" onclick="setAba('projetos')" /></li>
									<li><h:commandLink action="#{grupoAvaliacao.iniciarBuscaPerguntas}" value="Listar/Alterar" onclick="setAba('projetos')" /></li>
								</ul>
							</li>	
						</ul>
			
						<ul>
							<li>Questionários
								<ul>
									<li><h:commandLink action="#{questionarioAvaliacao.iniciarCadastroQuestionario}" value="Cadastrar" onclick="setAba('projetos')" /></li>
									<li><h:commandLink action="#{questionarioAvaliacao.iniciarBuscaQuestionarios}" value="Listar/Alterar" onclick="setAba('projetos')" /></li>
								</ul>
							</li>	
						</ul>
			
						<ul>
							<li>Modelos
								<ul>
									<li><h:commandLink action="#{modeloAvaliacao.iniciarCadastroModeloAvaliacao}" value="Cadastrar" onclick="setAba('projetos')" /></li>
									<li><h:commandLink action="#{modeloAvaliacao.iniciarBuscaModelos}" value="Listar/Alterar" onclick="setAba('projetos')" /></li>
								</ul>
							</li>	
						</ul>						
					</li>
			    </ul>
			</rich:tab>
		</rich:tabPanel>
	</a4j:outputPanel>
	<a4j:jsFunction id="changeTabFunc" reRender="tabsWrapper" name="changeTab" actionListener="#{pesquisaMBean.changeActiveProjSubTab}">
		<f:param name="selectedTab" />
	</a4j:jsFunction>
