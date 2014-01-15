<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
	td.nomeUsuario {
	}	
</style>

<f:view>
	<a4j:keepAlive beanName="forumMensagem" />
	<a4j:keepAlive beanName="listagemCursosForumDocenteMBean"></a4j:keepAlive>
	<%@include file="/stricto/menu_coordenador.jsp" %>
	<h:form id="listagem">
	<c:set var="foruns" value="#{forumMensagem.listaForunsPorCurso}"/>

	<h2><ufrn:subSistema /> > ${forumMensagem.forum.titulo} </h2>

	<div class="descricaoOperacao">
		<b>Caro Usuário,</b>
		<p>
			Este fórum é destinado para discussões relacionadas ao seu curso: todos os alunos do curso e a coordenação tem acesso a ele.
		</p> 
	</div>
	
	<br/>
	
	<div class="infoAltRem">
		<c:if test="${forumMensagem.usuarioAtivo}"><img src="/shared/img/adicionar.gif" style="overflow: visible;" alt="Cadastrar Novo Tópico"/>: 
			<h:commandLink action="#{ forumMensagem.novoForumCurso }" value="Cadastrar Novo Tópico" /></c:if>
		<img src="/sigaa/ava/img/bin.png" style="overflow: visible;" alt="Remover Tópico">: Remover Tópico <br/>
	</div>
								
	<c:if test="${ empty foruns }">
		<center>Nenhum item foi encontrado</center>
		<br/>
		<center> <h:commandButton id="voltar_nao_encontrado" value="<< Voltar" action="#{forum.cancelarForumCursos}" /> </center>
	</c:if>
	
	<c:if test="${ not empty foruns }">
	
		<div align="right" style="margin-bottom:5px;">	
			<h:inputText style="background-image: url(/sigaa/img/buscar.gif);background-repeat: no-repeat;padding-left:17px;" id="filtro" size="20" maxlength="50" value="#{ forumMensagem.filtro }"/>
			<h:commandButton id="buscar" value="Buscar" title="Buscar" action="#{forumMensagem.listarForunsPorCurso}"/>
		</div>
	
		<table class="listagem">
			<caption>Lista dos tópicos ativos</caption>
			<thead>
				<tr>
					<th>Título</th>
					<th>Autor</th>
					<th style="text-align: right">Respostas</th>
					<th style="text-align: center">Última Postagem</th>
					<th colspan=31"></th>
				</tr>
			</thead>
		
		<tbody>
		<c:forEach var="n" items="#{ foruns }" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" >
				
				<td width="50%">
					<h:commandLink id="mostrar" action="#{ forumMensagem.mostrarForumMensagemCurso }"> 
						<h:outputText value="#{ n.titulo }"/>
						
						<f:param name="idForumMensagem" value="#{ n.id }"/>
						<f:param name="id" value="#{ n.forum.id }"/>
					</h:commandLink> 
				</td>
				
				<td class="nomeUsuario"> ${ n.usuario.pessoa.nomeResumido } </td>
				<td style="text-align: right"> ${n.respostas } </td>
				<td style="text-align: center">
					<ufrn:format type="dataHora" valor="${  n.ultimaPostagem != null ? n.ultimaPostagem : n.data }"/>
				</td>
				
				<td class="icon">
				<c:if test="${ n.usuario.id == acesso.usuario.id 
				|| forumMensagem.coordenadorDoCursoEscolhido || acesso.secretarioGraduacao || forumMensagem.gestorForumCurso 
				|| (forum.portalCoordenadorLato && forumMensagem.forum.turma.curso.id == curso.cursoAtualCoordenacao.id) 
				|| (forum.portalCoordenadorStricto && forumMensagem.forum.turma.curso.id == curso.cursoAtualCoordenacao.id) }" >
					<h:commandLink id="remover" action="#{ forumMensagem.removerMensagensCurso }" title="Remover Tópico"
						onclick="return(confirm('Se excluir este tópico TODAS as mensagens que ele possui também serão removidas. Tem certeza?'));">
						<f:param name="id" value="#{ n.id }"/>
						<h:graphicImage value="/img/delete.gif"/>
					</h:commandLink>
				</c:if>
				</td>
				
			</tr>
		</c:forEach>
		</tbody>
			<tfoot>
				<tr>
					<td colspan="21">
						<center><h:commandButton id="voltar" value="<< Voltar" action="#{forum.cancelarForumCursos}" /></center> 
					</td>
				</tr>
			</tfoot>
	</table>
	
	<div style="text-align: center;"> 
    <h:commandButton image="/img/voltar.gif" actionListener="#{paginacao.previousPage}" rendered="#{paginacao.paginaAtual > 0 }"/>
    <h:selectOneMenu value="#{paginacao.paginaAtual}" valueChangeListener="#{paginacao.changePage}" onchange="submit()" immediate="true">
	<f:selectItems id="paramPagina" value="#{paginacao.listaPaginas}"/>
    </h:selectOneMenu>
    <h:commandButton image="/img/avancar.gif" actionListener="#{paginacao.nextPage}"  rendered="#{paginacao.paginaAtual < (paginacao.totalPaginas - 1)}"/>
    <br/><br/>
 
    <em><h:outputText value="#{paginacao.totalRegistros }"/> Registro(s) Encontrado(s)</em>
	</div>
	</c:if>

</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>