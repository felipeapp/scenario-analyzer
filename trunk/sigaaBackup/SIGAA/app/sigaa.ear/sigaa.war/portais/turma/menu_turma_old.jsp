<%-- MENU DE OPÇÕES PARA O DOCENTE --%>
<%@taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<div id="menu-dropdown">
	<c:if test="${portalTurma.profResponsavel}">
	<div class="wrapper"><h:form>
		<t:jscookMenu layout="hbr" theme="ThemeOffice"
			styleLocation="/css/jscookmenu" id="menuTurma">
			<input type="hidden" name="jscook_action" />

			<t:navigationMenuItem itemLabel="Turma" icon="/img/portal_turma/turma_menu.gif">

				<t:navigationMenuItem itemLabel="#{portalTurma.turmaSelecionada.disciplina.nome}" actionListener="#{menu.redirecionar}" itemValue="/portais/turma/turma.jsf"/>
				<t:navigationMenuItem split="true"/>

				<t:navigationMenuItem itemLabel="Participantes" actionListener="#{menu.redirecionar}" itemValue="/portais/turma/ParticipantesTurma/participantes.jsf" />
				<t:navigationMenuItem itemLabel="Agenda de Aulas" action="#{conteudoAula.carregarConteudo}"/>
				<t:navigationMenuItem itemLabel="Agenda de Avaliações" actionListener="#{menu.redirecionar}" itemValue="/portais/turma/AvaliacaoData/form.jsf"/>
				<t:navigationMenuItem itemLabel="Editar Informações da Turma" action="#{ turmaVirtual.atualizar }"/>

				<t:navigationMenuItem itemLabel="Freqüências">
					<t:navigationMenuItem itemLabel="Lançar Freqüências" actionListener="#{ menu.redirecionar }" itemValue="/portais/turma/FrequenciaAluno/form.jsf"/>
					<t:navigationMenuItem itemLabel="Mapa de Freqüências" action="#{ frequenciaAluno.mapaFrequencia }"/>
				</t:navigationMenuItem>

				<t:navigationMenuItem itemLabel="Impressos">
					<t:navigationMenuItem itemLabel="Lista de Presença" actionListener="#{portalTurma.visualizaListaPresenca}" target="_blank"/>
					<t:navigationMenuItem itemLabel="Diário de Classe"  actionListener="#{ diarioClasse.gerarDiarioClasse }" target="_blank" />
				</t:navigationMenuItem>

				<t:navigationMenuItem itemLabel="Lançar Notas" action="#{consolidarTurma.consolidaTurmaPortal}" />
				<t:navigationMenuItem itemLabel="Notícias" actionListener="#{menu.redirecionar}" itemValue="/portais/turma/NoticiaTurma/form.jsf"/>
			</t:navigationMenuItem>

			<t:navigationMenuItem itemLabel="Material" icon="/img/portal_turma/material_menu.gif">
				<t:navigationMenuItem itemLabel="Associar Arquivos à Turma" action="#{ arquivosTurma.escolheAula }"/>
				<t:navigationMenuItem itemLabel="Conteúdo">
					<t:navigationMenuItem itemLabel="Inserir Conteúdo" actionListener="#{menu.redirecionar}" itemValue="/portais/turma/ConteudoTurma/form.jsf"/>
					<t:navigationMenuItem itemLabel="Listar Conteúdo" actionListener="#{menu.redirecionar}" itemValue="/portais/turma/ConteudoTurma/lista.jsf"/>
				</t:navigationMenuItem>
				<t:navigationMenuItem itemLabel="Porta-Arquivos" actionListener="#{menu.redirecionar}" itemValue="/portais/porta_arquivos/view.jsf"/>
				<t:navigationMenuItem itemLabel="Sites na Internet" actionListener="#{menu.redirecionar}" itemValue="/portais/turma/TurmaFavoritos/form.jsf"/>
			</t:navigationMenuItem>

			<t:navigationMenuItem itemLabel="Atividades" icon="/img/portal_turma/atividades_menu.gif">
				<t:navigationMenuItem itemLabel="Chat" />
				<t:navigationMenuItem itemLabel="Fórum" actionListener="#{menu.redirecionar}" itemValue="/portais/turma/Forum/index.jsf"/>
				<t:navigationMenuItem itemLabel="Pesquisa de Opinião" actionListener="#{menu.redirecionar}" itemValue="/portais/turma/Enquete/form.jsf" />
				<t:navigationMenuItem itemLabel="Tarefas">
					<t:navigationMenuItem itemLabel="Nova Tarefa" actionListener="#{ menu.redirecionar }" itemValue="/portais/turma/TarefaTurma/form.jsf"/>
					<t:navigationMenuItem itemLabel="Listar Tarefas" actionListener="#{ menu.redirecionar }" itemValue="/portais/turma/TarefaTurma/lista.jsf"/>
				</t:navigationMenuItem>
			</t:navigationMenuItem>

			<t:navigationMenuItem itemLabel="Outros" icon="/img/portal_turma/outros_menu.gif">
				<t:navigationMenuItem itemLabel="Ajuda" />
				<t:navigationMenuItem itemLabel="Portal Docente" actionListener="#{menu.redirecionar}" itemValue="/portais/docente/docente.jsf"/>
			</t:navigationMenuItem>

		</t:jscookMenu>
	</h:form></div>
	</c:if>
</div>
