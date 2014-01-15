<h:form id="formMenu" rendered="#{ turmaVirtual.config == null || turmaVirtual.config.template == 1 }">
		
	<div id="barraEsquerda" class="ui-layout-west">
	
		<table style="width:100%;border-bottom:1px solid #99BBE8;" cellspacing="0"><tr><td style="color:#15428B;background:url('/sigaa/ava/img/painel_bg.png');height:24px;text-align:center;font-weight:bold;font-size:8pt;">Menu Turma Virtual</td></tr></table>
		
		<rich:panelBar selectedPanel="#{menuTurma.menuExpandido}">
			<rich:panelBarItem name="menuTurma" label="Turma" headerClass="itemMenuHeaderTurma" contentStyle="overflow:hidden;">
				<h:commandLink action="#{menuTurma.acessarPrincipal}">
					<div class="itemMenu">Principal</div>
				</h:commandLink>
		
				<c:if test="${ turmaVirtual.discente }">    
					<h:commandLink action="#{menuTurma.acessarGerenciarPerfil}">
						<div class="itemMenu">Gerenciar Perfil</div>
					</h:commandLink>
				</c:if>
				
				<c:if test="${ turmaVirtual.discente || permissaoAva.permissaoAcesso}">    
		   	
					<c:if test="${ empty turmaVirtual.turma.polo && !turmaVirtual.turma.infantil }">
						<h:commandLink action="#{menuTurma.acessarVisualizarPlanoCurso}">
							<div class="itemMenu">Plano de Curso</div>
						</h:commandLink>
					</c:if>
				</c:if>
					
				<c:if test="${ (turmaVirtual.docente || permissaoAva.permissaoUsuario.docente) }">
				
					<h:commandLink action="#{menuTurma.acessarConteudoProgramadoDiario}">
						<div class="itemMenu">Tópicos de Aula</div>
					</h:commandLink>
				
					<c:if test="${ turmaVirtual.docente && !turmaVirtual.turma.infantil }">
						<h:commandLink action="#{menuTurma.acessarPlanoCurso}">
							<div class="itemMenu">Plano de Curso</div>
						</h:commandLink>
					</c:if>
				 				
					<c:if test="${ empty turmaVirtual.turma.polo }">
						<h:commandLink action="#{menuTurma.acessarConteudoProgramado}">
							<div class="itemMenu">Conteúdo Programado</div>
						</h:commandLink>
					</c:if>	
				</c:if>
		
				<h:commandLink action="#{menuTurma.acessarParticipantes}">
					<div class="itemMenu">Participantes</div>
				</h:commandLink>
		
				<h:commandLink rendered="#{turmaVirtual.turma.disciplina.nivel == 'G'}" action="#{menuTurma.acessarVisualizarPrograma}">
					<div class="itemMenu">Visualizar Programa</div>
				</h:commandLink>
		
				<h:commandLink action="#{menuTurma.acessarVisualizarComoDiscente}" rendered="#{ turmaVirtual.discente && turmaVirtual.acessoDocente }">
					<div class="itemMenu" style="color:red;font-weight:bold;">Visualizar como Docente</div>
				</h:commandLink>
		
				<h:commandLink action="#{menuTurma.acessarForuns}">
					<div class="itemMenu">Fóruns</div>
				</h:commandLink>
		
				<a4j:commandLink action="#{ turmaVirtual.createChat }" oncomplete="window.open('/shared/EntrarChat?idchat=#{ turmaVirtual.turma.id }&idusuario=#{ turmaVirtual.usuarioLogado.id }&passkey=#{ turmaVirtual.chatPassKey }&chatName=#{ turmaVirtual.turma.disciplina.nome }&origem=turmaVirtual', 'chat_#{ turmaVirtual.turma.id }', 'height=485,width=685,location=0,resizable=0'); return false;">
					<div class="itemMenu">Chat da Turma</div>
				</a4j:commandLink>
				
				<a4j:commandLink action="#{ turmaVirtual.createChat }" oncomplete='return exibirJanelaVideoChat("#{ turmaVirtual.turma.id }", #{chatTurmaBean.usuarioLogado.id}, "#{turmaVirtual.chatPassKey}", "#{ turmaVirtual.turma.disciplina.nome }", "#{ turmaVirtual.usuarioLogado.pessoa.nome }", #{ (turmaVirtual.docente || permissaoAva.permissaoUsuario.docente) ? "true" : "false" }, "#{ turmaVirtual.enderecoServidorVideo }");'>
					<div class="itemMenu"><h:graphicImage value="/ava/img/videochat.png" style="margin-left:-18px;" /> Video Chat Ao Vivo </div>
				</a4j:commandLink>

				<h:commandLink action="#{ chatTurmaBean.listar }">
					<div class="itemMenu">Chats Agendados</div>
				</h:commandLink>
		
				<h:commandLink action="#{menuTurma.acessarNoticias}">
					<div class="itemMenu">Notícias</div>
				</h:commandLink>
				
				<h:commandLink action="#{menuTurma.acessarTwitterDocente}" rendered="#{ turmaVirtual.docente || permissaoAva.permissaoUsuario.docente }">
					 <div class="itemMenu">Twitter</div>
				</h:commandLink>
				
				<h:commandLink action="#{menuTurma.acessarTwitterDiscente}" rendered="#{ !turmaVirtual.docente && !permissaoAva.permissaoUsuario.docente }">
					 <div class="itemMenu">Twitter</div>
				</h:commandLink>
		
				<h:commandLink action="#{menuTurma.acessarRegistrarAulaExtra}" rendered="#{ turmaVirtual.docente || permissaoAva.permissaoUsuario.docente }">
					<div class="itemMenu">Registrar Aula Extra</div>
				</h:commandLink>
				
				<h:commandLink action="#{menuTurma.acessarRegistrarAulaEnsinoIndividual}" rendered="#{ (turmaVirtual.docente || permissaoAva.permissaoUsuario.docente) && turmaVirtual.turma.turmaEnsinoIndividual }">
					<div class="itemMenu" style="font-size:1em; padding-bottom: 8px;">Registrar Aula de<br/><p style="margin-left: 0px;">Ensino Individual</p></div>
				</h:commandLink>
				
				<h:commandLink action="#{menuTurma.acessarVisualizarComoDiscente}" rendered="#{ turmaVirtual.docente }">
					<div class="itemMenu">Visualizar como Aluno</div>
				</h:commandLink>
			
			</rich:panelBarItem>
		   			
			<rich:panelBarItem name="menuAlunos" label="Alunos" headerClass="itemMenuHeaderAlunos" contentStyle="overflow:hidden;">
				<h:commandLink rendered="#{(turmaVirtual.docente || permissaoAva.permissaoUsuario.docente) && !turmaVirtual.turma.infantil}" action="#{ menuTurma.acessarAlunosTrancados}">
					<div class="itemMenu">Alunos Trancados</div>
				</h:commandLink>
		  			
				<h:commandLink rendered="#{turmaVirtual.docente || permissaoAva.permissaoUsuario.docente}" action="#{ menuTurma.acessarGerenciarGrupos }">
					<div class="itemMenu">Gerenciar Grupos</div>
				</h:commandLink>
				
				<h:commandLink rendered="#{ turmaVirtual.discente && !turmaVirtual.acessoDocente && !turmaVirtual.turma.infantil}" action="#{menuTurma.acessarFrequencia}">
					<div class="itemMenu">Frequência</div>
				</h:commandLink>
				
				<h:commandLink rendered="#{ turmaVirtual.discente }" action="#{menuTurma.acessarVerGrupo}">
					<div class="itemMenu">Ver Grupo</div>
				</h:commandLink>
				
				<h:commandLink rendered="#{ turmaVirtual.discente && !turmaVirtual.turma.infantil}" action="#{menuTurma.acessarVerNotas}">
					<div class="itemMenu">Ver Notas</div>
				</h:commandLink>
		
				<c:if test="${turmaVirtual.docente || permissaoAva.permissaoUsuario.docente}">
					<h:commandLink rendered="#{empty turmaVirtual.turma.subturmas && !turmaVirtual.turma.infantil}" action="#{menuTurma.acessarLancarFrequencia}">
						<div class="itemMenu">Lançar Frequência</div>
					</h:commandLink>
					
					<h:commandLink rendered="#{ not empty turmaVirtual.turma.subturmas && !turmaVirtual.turma.infantil}" action="#{menuTurma.acessarLancarFrequenciaST}">
						<div class="itemMenu">Lançar Frequência</div>
					</h:commandLink>
					
					<h:commandLink rendered="#{empty turmaVirtual.turma.subturmas && !turmaVirtual.turma.infantil}" action="#{menuTurma.acessarLancarFrequenciaEmPlanilha}" target="_blank">
						<div class="itemMenu">Lançar Freq. em Planilha</div>
					</h:commandLink>
					
					<h:commandLink rendered="#{ not empty turmaVirtual.turma.subturmas && !turmaVirtual.turma.infantil}" action="#{menuTurma.acessarLancarFrequenciaEmPlanilhaST}">
						<div class="itemMenu">Lançar Freq. em Planilha</div>
					</h:commandLink>
					
					<h:commandLink rendered="#{turmaVirtual.turma.infantil}" action="#{menuTurma.acessarRegistrarEvolucao}">
						<div class="itemMenu">Registrar Form. Evolução</div>
					</h:commandLink>
					
					<h:commandLink rendered="#{turmaVirtual.turma.infantil}" action="#{menuTurma.acessarFormularioEvolucao}">
						<div class="itemMenu">Criar Form. de Evolução</div>
					</h:commandLink>
					
					<h:commandLink rendered="#{empty turmaVirtual.turma.subturmas && !turmaVirtual.turma.infantil}" action="#{menuTurma.acessarLancarNotas}">
						<div class="itemMenu">Lançar Notas</div>
					</h:commandLink>
					
					<h:commandLink rendered="#{not empty turmaVirtual.turma.subturmas && !turmaVirtual.turma.infantil}" action="#{menuTurma.acessarLancarNotasST}">
						<div class="itemMenu">Lançar Notas</div>
					</h:commandLink>
				</c:if>
			</rich:panelBarItem>
		    
			<rich:panelBarItem name="menuDiario" label="Diário Eletrônico" headerClass="itemMenuHeaderDE" contentStyle="overflow:hidden;" rendered="#{ turmaVirtual.permissaoDocente }">
				<h:commandLink action="#{menuTurma.acessarConteudoProgramadoDiario}">
					<div class="itemMenu">Conteúdo Programado</div>
				</h:commandLink>
		 		
				<h:commandLink action="#{menuTurma.acessarDiarioDeTurma}">
					<div class="itemMenu">Diário de Turma</div>
				</h:commandLink>
				
				<h:commandLink rendered="#{ empty turmaVirtual.turma.subturmas}" action="#{menuTurma.acessarListaDePresenca}">
					<div class="itemMenu">Lista de Presença</div>
				</h:commandLink>
				  	
				<h:commandLink rendered="#{ not empty turmaVirtual.turma.subturmas}" action="#{menuTurma.acessarListaDePresencaST}">
					<div class="itemMenu">Lista de Presença</div>
				</h:commandLink>
				
				<h:commandLink action="#{menuTurma.acessarMapaDeFrequencia}">
					<div class="itemMenu">Mapa de Frequência</div>
				</h:commandLink>
				
				<h:commandLink rendered="#{turmaVirtual.turma.disciplina.nivel == 'G'}" action="#{menuTurma.acessarTotalDeFaltasPorUnidade}">
					<div class="itemMenu">Total de faltas por unidade</div>
				</h:commandLink>
			</rich:panelBarItem>
		
			<rich:panelBarItem name="menuMateriais" label="Materiais" headerClass="itemMenuHeaderMateriais" contentStyle="overflow:hidden;">
				<h:commandLink action="#{menuTurma.acessarConteudo}">
					<div class="itemMenu">Conteúdo/Página web</div>
				</h:commandLink>
			
				<h:commandLink action="#{menuTurma.acessarPortaArquivos}" rendered="#{ turmaVirtual.docente }">
					<div class="itemMenu">Porta-Arquivos</div>
				</h:commandLink>
				
				<h:commandLink action="#{menuTurma.acessarInserirVariosArquivoNaTurma}" rendered="#{ turmaVirtual.docente || permissaoAva.permissaoUsuario.inserirArquivo || permissaoAva.permissaoUsuario.docente }">
					<div class="itemMenu">Inserir Arquivos na Turma</div>
				</h:commandLink>
				
				<h:commandLink action="#{menuTurma.acessarReferencias}">
					<div class="itemMenu">Referências</div>
				</h:commandLink>
				
				<h:commandLink action="#{menuTurma.acessarCadastrarVideoAula}" rendered="#{ turmaVirtual.docente || permissaoAva.permissaoUsuario.docente }">
					<div class="itemMenu">Vídeos</div>
				</h:commandLink>
				
				<h:commandLink action="#{menuTurma.acessarListarVideos}" rendered="#{ turmaVirtual.discente && !permissaoAva.permissaoUsuario.docente }">
					<div class="itemMenu">Vídeos</div>
				</h:commandLink>
				
				<h:commandLink action="#{menuTurma.acessarListarArquivos}" rendered="#{ turmaVirtual.discente }">
					<div class="itemMenu">Arquivos</div>
				</h:commandLink>
			</rich:panelBarItem>
					    
			<rich:panelBarItem name="menuAtividades" label="Atividades" headerClass="itemMenuHeaderAtividades" contentStyle="overflow:hidden;">
				<h:commandLink action="#{menuTurma.acessarAvaliacoes}">
					<div class="itemMenu">Avaliações</div>
				</h:commandLink>
				
				<h:commandLink action="#{menuTurma.acessarEnquetes}">
					<div class="itemMenu">Enquetes</div>
				</h:commandLink>
				
				<h:commandLink action="#{menuTurma.acessarTarefas}">
					<div class="itemMenu">Tarefas</div>
				</h:commandLink>
				
				<h:commandLink action="#{menuTurma.acessarQuestionariosDocente}" rendered="#{ turmaVirtual.docente || permissaoAva.permissaoUsuario.docente }">
					<div class="itemMenu">Questionários</div>
				</h:commandLink>
				
				<h:commandLink action="#{menuTurma.acessarQuestionariosDiscente}" rendered="#{ !turmaVirtual.docente && !permissaoAva.permissaoUsuario.inserirArquivo && !permissaoAva.permissaoUsuario.docente }">
					<div class="itemMenu">Questionários</div>
				</h:commandLink>
				
				<h:commandLink rendered="#{configuracoesAva.turmaChamadaBiometrica == true}" action="#{menuTurma.acessarSortearParticipantes}">
					<div class="itemMenu">Sortear Participantes</div>
				</h:commandLink>
		
				<rich:panelBar rendered="#{ turmaVirtual.docente || permissaoAva.permissaoUsuario.docente }">
					<rich:panelBarItem name="menuQuestionarios" label="Questionários" headerClass="itemMenuHeaderQuestionarios" contentStyle="overflow:hidden;">
						<h:commandLink action="#{menuTurma.acessarBancoDeQuestoes}">
							<div class="itemMenu">Banco de questões</div>
						</h:commandLink>
				
						<h:commandLink action="#{menuTurma.acessarNovoQuestionario}">
							<div class="itemMenu">Criar novo questionário</div>
						</h:commandLink>
					</rich:panelBarItem>
				</rich:panelBar>
			</rich:panelBarItem>
		  
			<rich:panelBarItem name="menuConfiguracoes" label="Configurações" headerClass="itemMenuHeaderConfig" contentStyle="overflow:hidden;" rendered="#{ turmaVirtual.permissaoDocente }">
				<h:commandLink rendered="#{turmaVirtual.docente || permissaoAva.permissaoUsuario.docente}" action="#{menuTurma.acessarConfigurarTurma}">
					<div class="itemMenu">Configurar Turma</div>
				</h:commandLink>
				
				<h:commandLink action="#{menuTurma.acessarImportacaoDeDados}">
					<div class="itemMenu">Importação de Dados</div>
				</h:commandLink>
				      
				<h:commandLink rendered="#{ turmaVirtual.docente }" action="#{menuTurma.acessarPermissoes}">
					<div class="itemMenu">Permissões</div>
				</h:commandLink>
				
				<h:commandLink rendered="#{ turmaVirtual.docente }" action="#{menuTurma.acessarPublicarTurmaVirtual}">
					<div class="itemMenu">Publicar Turma Virtual</div>
				</h:commandLink>
			</rich:panelBarItem>
		
			<rich:panelBarItem name="menuEstatistica" label="Estatística" headerClass="itemMenuHeaderRelatorios" contentStyle="overflow:hidden;">
				<h:commandLink action="#{menuTurma.acessarEstatisticas}">
					<div class="itemMenu">Situação dos Discentes</div>
				</h:commandLink>
				
				<h:commandLink action="#{menuTurma.acessarEstatisticasNotas}" rendered="#{ (turmaVirtual.docente || permissaoAva.permissaoUsuario.docente || (turmaVirtual.discente && turmaVirtual.config.mostrarEstatisticaNotas)) && empty turmaVirtual.turma.subturmas }">
					<div class="itemMenu">Estatísticas de Notas</div>
				</h:commandLink>
				
				<h:commandLink rendered="#{ (turmaVirtual.docente || permissaoAva.permissaoUsuario.docente || (turmaVirtual.discente && turmaVirtual.config.mostrarEstatisticaNotas)) && not empty turmaVirtual.turma.subturmas}" action="#{menuTurma.acessarEstatisticasNotasST}">
						<div class="itemMenu">Estatísticas de Notas</div>
				</h:commandLink>
				
				<h:commandLink rendered="#{ turmaVirtual.docente || permissaoAva.permissaoUsuario.docente}" action="#{menuTurma.acessarRelatorioAcesso}">
					<div class="itemMenu">Relatório de Acesso</div>
				</h:commandLink>
				
				<h:commandLink rendered="#{ turmaVirtual.docente || permissaoAva.permissaoUsuario.docente}" action="#{menuTurma.acessarRelatorioAcessos}">
					<div class="itemMenu">Relatório de Ações</div>
				</h:commandLink>
				
				<h:commandLink rendered="#{ turmaVirtual.docente || permissaoAva.permissaoUsuario.docente}" action="#{menuTurma.acessarGraficoDeAcesso}">
					<div class="itemMenu">Gráfico de Acesso</div>
				</h:commandLink>
			</rich:panelBarItem>
		
			<rich:panelBarItem label="Ajuda" headerClass="itemMenuHeaderAjuda" contentStyle="overflow:hidden;">
				<h:commandLink action="#{menuTurma.acessarManualDaTurmaVirtual}" target="_BLANK">
					<div class="itemMenu">Manual da Turma Virtual</div>
				</h:commandLink>
			</rich:panelBarItem>
		</rich:panelBar>
	</div>

	<c:set var="permissaoDocenteAva" value="${ turmaVirtual.permissaoDocente }" scope="session"/>
--</h:form>