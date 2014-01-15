<%-- MENU DE OP��ES PARA O DOCENTE --%>
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
				<t:navigationMenuItem itemLabel="Agenda de Avalia��es" actionListener="#{menu.redirecionar}" itemValue="/portais/turma/AvaliacaoData/form.jsf"/>
				<t:navigationMenuItem itemLabel="Editar Informa��es da Turma" action="#{ turmaVirtual.atualizar }"/>

				<t:navigationMenuItem itemLabel="Freq��ncias">
					<t:navigationMenuItem itemLabel="Lan�ar Freq��ncias" actionListener="#{ menu.redirecionar }" itemValue="/portais/turma/FrequenciaAluno/form.jsf"/>
					<t:navigationMenuItem itemLabel="Mapa de Freq��ncias" action="#{ frequenciaAluno.mapaFrequencia }"/>
				</t:navigationMenuItem>

				<t:navigationMenuItem itemLabel="Impressos">
					<t:navigationMenuItem itemLabel="Lista de Presen�a" actionListener="#{portalTurma.visualizaListaPresenca}" target="_blank"/>
					<t:navigationMenuItem itemLabel="Di�rio de Classe"  actionListener="#{ diarioClasse.gerarDiarioClasse }" target="_blank" />
				</t:navigationMenuItem>

				<t:navigationMenuItem itemLabel="Lan�ar Notas" action="#{consolidarTurma.consolidaTurmaPortal}" />
				<t:navigationMenuItem itemLabel="Not�cias" actionListener="#{menu.redirecionar}" itemValue="/portais/turma/NoticiaTurma/form.jsf"/>
			</t:navigationMenuItem>

			<t:navigationMenuItem itemLabel="Material" icon="/img/portal_turma/material_menu.gif">
				<t:navigationMenuItem itemLabel="Associar Arquivos � Turma" action="#{ arquivosTurma.escolheAula }"/>
				<t:navigationMenuItem itemLabel="Conte�do">
					<t:navigationMenuItem itemLabel="Inserir Conte�do" actionListener="#{menu.redirecionar}" itemValue="/portais/turma/ConteudoTurma/form.jsf"/>
					<t:navigationMenuItem itemLabel="Listar Conte�do" actionListener="#{menu.redirecionar}" itemValue="/portais/turma/ConteudoTurma/lista.jsf"/>
				</t:navigationMenuItem>
				<t:navigationMenuItem itemLabel="Porta-Arquivos" actionListener="#{menu.redirecionar}" itemValue="/portais/porta_arquivos/view.jsf"/>
				<t:navigationMenuItem itemLabel="Sites na Internet" actionListener="#{menu.redirecionar}" itemValue="/portais/turma/TurmaFavoritos/form.jsf"/>
			</t:navigationMenuItem>

			<t:navigationMenuItem itemLabel="Atividades" icon="/img/portal_turma/atividades_menu.gif">
				<t:navigationMenuItem itemLabel="Chat" />
				<t:navigationMenuItem itemLabel="F�rum" actionListener="#{menu.redirecionar}" itemValue="/portais/turma/Forum/index.jsf"/>
				<t:navigationMenuItem itemLabel="Pesquisa de Opini�o" actionListener="#{menu.redirecionar}" itemValue="/portais/turma/Enquete/form.jsf" />
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
