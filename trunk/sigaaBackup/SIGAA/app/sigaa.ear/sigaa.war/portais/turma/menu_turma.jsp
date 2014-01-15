<h:form>
<div id="menu_turma">
<ul>
	<li class="menu_group turma">
		<h5>Turma</h5>
		<ul>
			<li> <a href="${ctx}/portais/turma/turma.jsf"> Principal </a> </li>
			<c:if test="${acesso.docente || permissao.docente }">
			<li> <a href="${ctx}/portais/turma/TopicoAula/form.jsf">T�picos de Aula </a> </li>
			</c:if>
			</li>
			<li> <a href="${ctx}/portais/turma/ParticipantesTurma/participantes.jsf?expanded=true"> Participantes </a> </li>
			<c:if test="${acesso.docente}">
			<li> <a href="${ctx}/portais/turma/PermissaoAva/lista.jsf?expanded=true">Permiss�es</a> </li>
			</c:if>
			<c:if test="${acesso.docente || permissao.docente}">
			<li> <a href="${ctx}/portais/turma/TurmaVirtual/form.jsf">Plano de Ensino</a></li>
			</c:if> 
			<c:if test="${acesso.discente}">
			<li> <a href="${ctx}/portais/turma/TurmaVirtual/view.jsf">Plano de Ensino</a></li>
			</c:if> 
			<li> <a href="${ctx}/portais/turma/Forum/mensagens.jsf?idForum=${ portalTurma.mural.id }">Mural</a> </li>
			<li> <a href="${ctx}/portais/turma/NoticiaTurma/form.jsf">Not�cias</a> </li>
			<li>
				<h:commandLink value="Importar Dados" action="#{ importacaoDadosTurma.iniciar }"/>
			</li>
			<%-- <li> <a href="#">Programa</a> </li> --%>
		</ul>
	</li>
	<li class="menu_group alunos"> 
		<h5>ALUNOS</h5>
		<ul>
			<li> <h:commandLink action="#{ frequenciaAluno.frequenciaAluno }" value="Freq��ncia" rendered="#{ acesso.discente }"/> </li>
			<li> <h:commandLink action="#{ relatorioConsolidacao.notasDiscente }" value="Notas" rendered="#{ acesso.discente }"/> </li>
			<c:if test="${acesso.docente || permissao.docente}">
			<li> <h:commandLink value="Lan�ar Frequ�ncia" action="#{ frequenciaAluno.lancar }"/></li> 
			<li> <h:commandLink action="#{ frequenciaAluno.mapaFrequencia }" value="Mapa de Freq��ncia" rendered="#{acesso.docente || permissao.docente }">  </h:commandLink> </li>
			</c:if>
			<li> <h:commandLink value="Notas" action="#{consolidarTurma.consolidaTurmaPortal}" rendered="#{ acesso.docente }"/>			
		</ul>
	</li>
	<c:if test="${acesso.docente || permissao.docente}">
	<li class="menu_group impressos">
		<h5>Impressos</h5>
		<ul>
			<li> <h:commandLink value="Di�rio de Turma"  action="#{ diarioClasse.gerarDiarioClasse }" rendered="#{acesso.docente || permissao.docente}"/> </li>
			<li> <h:commandLink value="Lista de Presen�a" action="#{portalTurma.visualizaListaPresenca}" rendered="#{acesso.docente || permissao.docente}"/> </li>
		</ul>
	</li>
	</c:if>
	<li class="menu_group material"> 
		<h5>Material</h5>
		<ul>
			<li> <a href="${ctx}/portais/turma/ConteudoTurma/lista.jsf">Conte�do </a> </li>
			<c:if test="${ acesso.docente || permissao.inserirArquivo }">
			<li> <h:commandLink value="Inserir Arquivo na Turma" action="#{ arquivoUsuario.inserirArquivoTurma }"/> </li>
			</c:if>
			<c:if test="${acesso.docente}">
			<li> <a href="${ctx}/portais/turma/PortaArquivos/view.jsf?expanded=true"> Porta-Arquivos </a> </li>
			</c:if>
			<li> <h:commandLink value="Refer�ncias" action="#{ indicacaoReferencia.inserirReferencia }"/> </li>
		</ul>
	</li>
	<li class="menu_group atividades">
		<h5>Atividades</h5>
		<ul>
			<li> <a href="${ctx}/portais/turma/AvaliacaoData/form.jsf">Avalia��es </a> </li>
			<li> <a href="${ctx}/portais/turma/Enquete/form.jsf"> Enquetes </a> </li>
			<li> <a href="${ctx}/portais/turma/Forum/index.jsf">F�rum </a> </li>
			<li> <a href="${ctx}/portais/turma/TarefaTurma/form.jsf"> Tarefas </a> </li>
		</ul>
	</li>
</ul>
</div>
</h:form>