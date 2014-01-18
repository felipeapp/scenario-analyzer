<h:form id="formMenuDrop" rendered="#{ turmaVirtual.config.template == 2 }">
	<div id="menuDropDown">
		
		<rich:toolBar style="background:url('/sigaa/ava/img/bgmenuhorizontal.png');border-bottom:1px solid #99BBE8;">
		
			<rich:dropDownMenu>
               	
				<f:facet name="label"> 
					<h:panelGroup>
						<table><tr>
							<th style="text-align:left;padding-right:5px;"><h:graphicImage value="/ava/img/group.png" styleClass="pic"/></th>
								<td>Turma</td>
						</tr></table>
					</h:panelGroup>
				</f:facet>
              		
          		<rich:menuItem id="menuPrincipal" submitMode="server" value="Principal" action="#{menuTurma.acessarPrincipal}" />
          		<rich:menuItem id="menuGerenciarPerfil" submitMode="server" value="Gerenciar Perfil" action="#{ menuTurma.acessarGerenciarPerfil }"  rendered="#{ turmaVirtual.discente }" />
				<rich:menuItem id="menuVisualizarPC" submitMode="server" value="Plano de Curso" action="#{ menuTurma.acessarVisualizarPlanoCurso }"  rendered="#{ (turmaVirtual.discente || permissaoAva.permissaoAcesso ) && empty turmaVirtual.turma.polo }" />              	
              	
              	<rich:menuItem id="menuRegistrarAulaEnsinoIndividual" submitMode="server" value="Registrar Aula de Ensino Individual" action="#{menuTurma.acessarRegistrarAulaEnsinoIndividual}" rendered="#{ (turmaVirtual.docente || permissaoAva.permissaoUsuario.docente) && turmaVirtual.turma.turmaEnsinoIndividual }" />
              	<rich:menuItem id="menuTA" submitMode="server" value="T�picos de Aula" action="#{ menuTurma.acessarConteudoProgramadoDiario }"  rendered="#{ (turmaVirtual.docente || permissaoAva.permissaoUsuario.docente) && empty turmaVirtual.turma.polo }" />
				<rich:menuItem id="menuAcessarPC" submitMode="server" value="Plano de Curso" action="#{ menuTurma.acessarPlanoCurso }"  rendered="#{ turmaVirtual.docente && empty turmaVirtual.turma.polo && !turmaVirtual.turma.infantil }" />				
				<rich:menuItem id="menuAcessarCP" submitMode="server" value="Conte�do Programado" action="#{ menuTurma.acessarConteudoProgramado }"  rendered="#{ (turmaVirtual.docente || permissaoAva.permissaoUsuario.docente) && empty turmaVirtual.turma.polo }" />
              	
              	
              	<rich:menuItem id="menuParticipantes" submitMode="server" value="Participantes" action="#{menuTurma.acessarParticipantes}" />
		        <rich:menuItem id="menuVisualizarPrograma" submitMode="server" value="Visualizar Programa" rendered="#{turmaVirtual.turma.disciplina.nivel == 'G'}" action="#{menuTurma.acessarVisualizarPrograma}" />
				<rich:menuItem id="menuAcessarForuns" submitMode="server" value="F�runs" action="#{menuTurma.acessarForuns}" />
				
				<rich:menuItem id="menuChatTurma" submitMode="ajax" value="Chat da Turma" action="#{ turmaVirtual.createChat }" oncomplete="window.open('/shared/EntrarChat?idchat=#{ turmaVirtual.turma.id }&idusuario=#{ turmaVirtual.usuarioLogado.id }&passkey=#{ turmaVirtual.chatPassKey }&chatName=#{ turmaVirtual.turma.disciplina.nome }&origem=turmaVirtual', 'chat_#{ turmaVirtual.turma.id }', 'height=485,width=685,location=0,resizable=0'); return false;" />
				<rich:menuItem id="menuChatVideo" submitMode="ajax" value="Video Chat Ao Vivo" action="#{ turmaVirtual.createChat }" oncomplete='return exibirJanelaVideoChat("#{ turmaVirtual.turma.id }", #{chatTurmaBean.usuarioLogado.id}, "#{turmaVirtual.chatPassKey}", "#{ turmaVirtual.turma.disciplina.nome }", "#{ turmaVirtual.usuarioLogado.pessoa.nome }", #{ (turmaVirtual.docente || permissaoAva.permissaoUsuario.docente) ? "true" : "false" }, "#{ turmaVirtual.enderecoServidorVideo }");'>
					<f:facet name="icon"><h:graphicImage value="/ava/img/videochat.png" /></f:facet>
				</rich:menuItem>
				<rich:menuItem id="menuChatAgendados" submitMode="ajax" value="Chats Agendados" action="#{ chatTurmaBean.listar }" />				
		        
		        <rich:menuItem id="menuNoticias" submitMode="server" value="Not�cias" action="#{menuTurma.acessarNoticias}" />
		        
		        <rich:menuItem id="menuTwitterDocente" submitMode="server" value="Twitter" action="#{menuTurma.acessarTwitterDocente}" rendered="#{ turmaVirtual.docente || permissaoAva.permissaoUsuario.docente }" />
				<rich:menuItem id="menuTwitterDiscente" submitMode="server" value="Twitter" action="#{menuTurma.acessarTwitterDiscente}" rendered="#{ !turmaVirtual.docente && !permissaoAva.permissaoUsuario.docente }" />
				
				
	        	<rich:menuItem id="menuRegistrarAulaExtra" submitMode="server" value="Registrar Aula Extra" action="#{menuTurma.acessarRegistrarAulaExtra}" rendered="#{ turmaVirtual.docente || permissaoAva.permissaoUsuario.docente }" />
	        	<rich:menuItem id="menuVisualizarComoAluno" submitMode="server" value="Visualizar como Aluno" action="#{menuTurma.acessarVisualizarComoDiscente}" rendered="#{ turmaVirtual.docente }" />
		        <rich:menuItem id="menuVisualizarComoDocente" style="color:#FF0000;" submitMode="server" value="Visualizar como Docente" rendered="#{ turmaVirtual.discente && turmaVirtual.acessoDocente }" action="#{menuTurma.acessarVisualizarComoDiscente}" />
              	</rich:dropDownMenu>
              	
              	<rich:dropDownMenu>
               	
				<f:facet name="label"> 
					<h:panelGroup>
						<table><tr>
							<th style="text-align:left;padding-right:5px;"><h:graphicImage value="/ava/img/vcard.png" styleClass="pic"/></th>
								<td>Alunos</td>
						</tr></table>
					</h:panelGroup>
				</f:facet>
				
				<rich:menuItem id="menuAlunosTrancados" submitMode="server" value="Alunos Trancados" rendered="#{(turmaVirtual.docente || permissaoAva.permissaoUsuario.docente) && !turmaVirtual.turma.infantil}" action="#{ menuTurma.acessarAlunosTrancados}" />
				<rich:menuItem id="menuGerenciarGrupos" submitMode="server" value="Gerenciar Grupos" rendered="#{turmaVirtual.docente || permissaoAva.permissaoUsuario.docente}" action="#{ menuTurma.acessarGerenciarGrupos }" />
				<rich:menuItem id="menuFrequencia" submitMode="server" value="Frequ�ncia" rendered="#{ turmaVirtual.discente && !turmaVirtual.acessoDocente && !turmaVirtual.turma.infantil}" action="#{menuTurma.acessarFrequencia}" />
				<rich:menuItem id="menuVerGrupo" submitMode="server" value="Ver Grupo" rendered="#{ turmaVirtual.discente }" action="#{menuTurma.acessarVerGrupo}" />
				<rich:menuItem id="menuVerNotas" submitMode="server" value="Ver Notas" rendered="#{ turmaVirtual.discente && !turmaVirtual.turma.infantil}" action="#{menuTurma.acessarVerNotas}" />
				
				<rich:menuItem id="menuLancarFreq" submitMode="server" value="Lan�ar Frequ�ncia" rendered="#{(turmaVirtual.docente || permissaoAva.permissaoUsuario.docente) && empty turmaVirtual.turma.subturmas && !turmaVirtual.turma.infantil}" action="#{menuTurma.acessarLancarFrequencia}" />
				<rich:menuItem id="menuLancarFreqST" submitMode="server" value="Lan�ar Frequ�ncia" rendered="#{ (turmaVirtual.docente || permissaoAva.permissaoUsuario.docente) && not empty turmaVirtual.turma.subturmas && !turmaVirtual.turma.infantil}" action="#{menuTurma.acessarLancarFrequenciaST}" />
				<rich:menuItem id="menuLancarFreqPL" submitMode="server" value="Lan�ar Freq. em Planilha" rendered="#{(turmaVirtual.docente || permissaoAva.permissaoUsuario.docente) && empty turmaVirtual.turma.subturmas && !turmaVirtual.turma.infantil}" action="#{menuTurma.acessarLancarFrequenciaEmPlanilha}" target="_blank" />
				<rich:menuItem id="menuLancarFreqPLST" submitMode="server" value="Lan�ar Freq. em Planilha" rendered="#{ (turmaVirtual.docente || permissaoAva.permissaoUsuario.docente) && not empty turmaVirtual.turma.subturmas && !turmaVirtual.turma.infantil}" action="#{menuTurma.acessarLancarFrequenciaEmPlanilhaST}" />
				<rich:menuItem id="menuRegistrarEvolucao" submitMode="server" value="Registrar Form. de Evolu��o" rendered="#{(turmaVirtual.docente || permissaoAva.permissaoUsuario.docente) && turmaVirtual.turma.infantil}" action="#{menuTurma.acessarRegistrarEvolucao}" />
				<rich:menuItem id="menuFormularioEvolucao" submitMode="server" value="Criar Form. de Evolu��o" rendered="#{(turmaVirtual.docente || permissaoAva.permissaoUsuario.docente) && turmaVirtual.turma.infantil}" action="#{menuTurma.acessarFormularioEvolucao}" />
				<rich:menuItem id="menuLancarNotas" submitMode="server" value="Lan�ar Notas" rendered="#{(turmaVirtual.docente || permissaoAva.permissaoUsuario.docente) && empty turmaVirtual.turma.subturmas && !turmaVirtual.turma.infantil}" action="#{menuTurma.acessarLancarNotas}" />
				<rich:menuItem id="menuLancarNotasST" submitMode="server" value="Lan�ar Notas" rendered="#{(turmaVirtual.docente || permissaoAva.permissaoUsuario.docente) && not empty turmaVirtual.turma.subturmas && !turmaVirtual.turma.infantil}" action="#{menuTurma.acessarLancarNotasST}" />
              	</rich:dropDownMenu>
              	
              	<rich:dropDownMenu rendered="#{ turmaVirtual.permissaoDocente }">
               	
				<f:facet name="label"> 
					<h:panelGroup>
						<table><tr>
							<th style="text-align:left;padding-right:5px;"><h:graphicImage value="/ava/img/book_open.png" styleClass="pic"/></th>
								<td>Di�rio Eletr�nico</td>
						</tr></table>
					</h:panelGroup>
				</f:facet>
				
	    		<rich:menuItem id="menuConteudoProgramado" submitMode="server" value="Conte�do Programado" action="#{menuTurma.acessarConteudoProgramadoDiario}" />
				<rich:menuItem id="menuDiarioDeTurma" submitMode="server" value="Di�rio de Turma" action="#{menuTurma.acessarDiarioDeTurma}" />
		    	<rich:menuItem id="menuListaDePresenca" submitMode="server" value="Lista de Presen�a" rendered="#{ empty turmaVirtual.turma.subturmas}" action="#{menuTurma.acessarListaDePresenca}" />
				<rich:menuItem id="menuListaDePresencaST" submitMode="server" value="Lista de Presen�a" rendered="#{ not empty turmaVirtual.turma.subturmas}" action="#{menuTurma.acessarListaDePresencaST}" />
				<rich:menuItem id="menuMapaDeFrequencia" submitMode="server" value="Mapa de Frequ�ncia" action="#{menuTurma.acessarMapaDeFrequencia}" />
				<rich:menuItem id="menuTotalDeFaltasPorUnidade" submitMode="server" value="Total de faltas por unidade" rendered="#{turmaVirtual.turma.disciplina.nivel == 'G'}" action="#{menuTurma.acessarTotalDeFaltasPorUnidade}" />
              	</rich:dropDownMenu>
              	
              	
              	<rich:dropDownMenu>
               	
				<f:facet name="label"> 
					<h:panelGroup>
						<table><tr>
							<th style="text-align:left;padding-right:5px;"><h:graphicImage value="/ava/img/materiais.png" styleClass="pic"/></th>
								<td>Materiais</td>
						</tr></table>
					</h:panelGroup>
				</f:facet>
				
	    		<rich:menuItem id="menuConteudo" icon="/img/porta_arquivos/icones/conteudo.png" submitMode="server" value="Conte�do" action="#{menuTurma.acessarConteudo}" />
	        	<rich:menuItem id="menuPortaArquivos" submitMode="server" value="Porta-Arquivos" action="#{menuTurma.acessarPortaArquivos}" rendered="#{ turmaVirtual.docente }" />
	        	<rich:menuItem id="menuInserirVariosArquivosNaTurma" icon="/img/porta_arquivos/icones/desconhecido.png" submitMode="server" value="Inserir Arquivos na Turma" action="#{menuTurma.acessarInserirVariosArquivoNaTurma}" rendered="#{ turmaVirtual.docente || permissaoAva.permissaoUsuario.inserirArquivo || permissaoAva.permissaoUsuario.docente }" />
		        <rich:menuItem id="menuReferencias" icon="/img/portal_turma/site_add.png" submitMode="server" value="Refer�ncias" action="#{menuTurma.acessarReferencias}" />
		        <rich:menuItem id="menuVideos" icon="/img/portal_turma/video.png" submitMode="server" value="V�deos" action="#{menuTurma.acessarCadastrarVideoAula}" rendered="#{ turmaVirtual.docente || permissaoAva.permissaoUsuario.docente }" />
		        <rich:menuItem id="menuVideosDiscente" icon="/img/portal_turma/video.png" submitMode="server" value="V�deos" action="#{menuTurma.acessarListarVideos}" rendered="#{ turmaVirtual.discente && !permissaoAva.permissaoUsuario.docente }" />
		        <rich:menuItem id="menuArquivoDiscentene" icon="/img/porta_arquivos/icones/desconhecido.png" submitMode="server" value="Arquivos" action="#{menuTurma.acessarListarArquivos}" rendered="#{ turmaVirtual.discente && !permissaoAva.permissaoUsuario.docente }" />
              	</rich:dropDownMenu>
              	
              	
              	
              	<rich:dropDownMenu>
               	
				<f:facet name="label"> 
					<h:panelGroup>
						<table><tr>
							<th style="text-align:left;padding-right:5px;"><h:graphicImage value="/ava/img/report_edit.png" styleClass="pic"/></th>
								<td>Atividades</td>
						</tr></table>
					</h:panelGroup>
				</f:facet>
				
	    		<rich:menuItem id="menuAvaliacoes" submitMode="server" value="Avalia��es" action="#{menuTurma.acessarAvaliacoes}" />
		        <rich:menuItem id="menuEnquetes" icon="/ava/img/enquete.png" submitMode="server" value="Enquetes" action="#{menuTurma.acessarEnquetes}" />
		        <rich:menuItem id="menuTarefas" icon="/img/porta_arquivos/icones/tarefa.png" submitMode="server" value="Tarefas" action="#{menuTurma.acessarTarefas}" />
		        <rich:menuItem id="menuQuestionariosDiscente" submitMode="server" value="Question�rios" action="#{menuTurma.acessarQuestionariosDiscente}" rendered="#{ !turmaVirtual.docente && !permissaoAva.permissaoUsuario.inserirArquivo && !permissaoAva.permissaoUsuario.docente }" />
		        <rich:menuItem id="menuSortearParticipacoes" submitMode="server" value="Sortear Participantes" rendered="#{configuracoesAva.turmaChamadaBiometrica == true}" action="#{menuTurma.acessarSortearParticipantes}" />
		        
		        
		        <rich:menuGroup value="Question�rios" icon="/ava/img/questionario.png" rendered="#{ turmaVirtual.docente || permissaoAva.permissaoUsuario.docente }">
               	
					<f:facet name="label"> 
						<h:panelGroup>
							<table><tr>
								<th style="text-align:left;padding-right:5px;"><h:graphicImage value="/ava/img/report_edit.png" styleClass="pic"/></th>
									<td>Atividades</td>
							</tr></table>
						</h:panelGroup>
					</f:facet>
		        
		        	<rich:menuItem id="menuQuestionarios" submitMode="server" value="Listar Question�rios" action="#{menuTurma.acessarQuestionariosDocente}" rendered="#{ turmaVirtual.docente || permissaoAva.permissaoUsuario.docente }" />
		        	<rich:menuItem id="menuBancoDeQuestoes" submitMode="server" value="Banco de quest�es" action="#{menuTurma.acessarBancoDeQuestoes}" />
		        	<rich:menuItem id="menuCriarNovoQuestionario" submitMode="server" value="Criar novo question�rio" action="#{menuTurma.acessarNovoQuestionario}" />
		        </rich:menuGroup>
		        
              	</rich:dropDownMenu>
              	
              	<rich:dropDownMenu rendered="#{ turmaVirtual.permissaoDocente }">
               	
				<f:facet name="label"> 
					<h:panelGroup>
						<table><tr>
							<th style="text-align:left;padding-right:5px;"><h:graphicImage value="/ava/img/folder_wrench.png" styleClass="pic"/></th>
								<td>Configura��es</td>
						</tr></table>
					</h:panelGroup>
				</f:facet>
				
				<rich:menuItem id="menuConfigurarTurma" submitMode="server" value="Configurar Turma" action="#{menuTurma.acessarConfigurarTurma}" />
		        <rich:menuItem id="menuImportacaoDados" submitMode="server" value="Importa��o de Dados" action="#{menuTurma.acessarImportacaoDeDados}" />
				<rich:menuItem id="menuPermissoes" submitMode="server" value="Permiss�es" rendered="#{ turmaVirtual.docente }" action="#{menuTurma.acessarPermissoes}" />
				<rich:menuItem id="menuPublicarTV" submitMode="server" value="Publicar Turma Virtual" rendered="#{ turmaVirtual.docente }" action="#{menuTurma.acessarPublicarTurmaVirtual}" />
              	</rich:dropDownMenu>
              	
              	
              	
              	<rich:dropDownMenu>
               	
				<f:facet name="label"> 
					<h:panelGroup>
						<table><tr>
							<th style="text-align:left;padding-right:5px;"><h:graphicImage value="/ava/img/page_white_put.png" styleClass="pic"/></th>
								<td>Estat�stica</td>
						</tr></table>
					</h:panelGroup>
				</f:facet>
				
				<rich:menuItem id="menuEstatisticas" submitMode="server" value="Estat�sticas" action="#{menuTurma.acessarEstatisticasDeAcesso}" />
		        <rich:menuItem id="menuSituacaoDiscentes" submitMode="server" value="Situa��o dos Discentes" action="#{menuTurma.acessarEstatisticas}" />
		        <rich:menuItem id="menuEstatisticasNotas" submitMode="server" value="Estat�sticas de Notas" action="#{menuTurma.acessarEstatisticasNotas}" rendered="#{  (turmaVirtual.docente || permissaoAva.permissaoUsuario.docente || (turmaVirtual.discente && turmaVirtual.config.mostrarEstatisticaNotas)) && (empty turmaVirtual.turma.subturmas)  }"/>
				<rich:menuItem id="menuEstatisticasNotasST" submitMode="server" value="Estat�sticas de Notas" action="#{menuTurma.acessarEstatisticasNotasST}" rendered="#{ (turmaVirtual.docente || permissaoAva.permissaoUsuario.docente || (turmaVirtual.discente && turmaVirtual.config.mostrarEstatisticaNotas)) && (not empty turmaVirtual.turma.subturmas) }"/>
						
				<rich:menuItem id="menuRelatorioAcesso" submitMode="server" value="Relat�rio de Acesso" rendered="#{ turmaVirtual.docente || permissaoAva.permissaoUsuario.docente}" action="#{menuTurma.acessarRelatorioAcesso}" />
				
				<rich:menuItem id="menuRelatoriosAcessoNOVO" submitMode="server" value="Relat�rio de A��es" rendered="#{ turmaVirtual.docente || permissaoAva.permissaoUsuario.docente}" action="#{menuTurma.acessarRelatorioAcessos}" />
				
				<rich:menuItem id="menuGraficoAcesso" submitMode="server" value="Gr�fico de Acesso" rendered="#{ turmaVirtual.docente || permissaoAva.permissaoUsuario.docente}" action="#{menuTurma.acessarGraficoDeAcesso}" />
		        
              	</rich:dropDownMenu>
              	
              	<rich:dropDownMenu>
              		<f:facet name="label"> 
					<h:panelGroup>
						<table><tr>
							<th style="text-align:left;padding-right:5px;"><h:graphicImage value="/ava/img/small_ajuda.gif" styleClass="pic"/></th>
								<td>Ajuda</td>
						</tr></table>
					</h:panelGroup>
				</f:facet>
				
               	<rich:menuItem id="menuAjuda" submitMode="server" value="Manual da Turma Virtual" action="#{menuTurma.acessarManualDaTurmaVirtual}" target="_BLANK"  />
			</rich:dropDownMenu>

		</rich:toolBar>
	</div>
</h:form>