<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
	td.nomeUsuario {
	}	
</style>

<f:view>
	<a4j:keepAlive beanName="forumMensagemMedio" />
	<a4j:keepAlive beanName="listagemCursosForumDocenteMBean"></a4j:keepAlive>
	<%@include file="/stricto/menu_coordenador.jsp" %>
	<h:form id="listagem">
	<c:set var="foruns" value="#{forumMensagemMedio.listaForunsPorTurmaSerie}"/>

	<h2><ufrn:subSistema /> > ${forumMensagemMedio.forum.titulo} </h2>

	<div class="descricaoOperacao">
		<b>Caro Usuário,</b>
		<p>
			Este fórum é destinado para discussões relacionadas a sua turma atual: 
			todos os alunos e docentes da turma tem acesso a ele.
		</p> 
	</div>
	
	<br/>
	
	<div class="infoAltRem">
		<c:if test="${forumMensagemMedio.usuarioAtivo}">
			<img src="/shared/img/adicionar.gif" style="overflow: visible;" alt="Cadastrar Novo Tópico"/>: 
			<h:commandLink action="#{ forumMensagemMedio.novoForumTurmaSerie }" value="Cadastrar Novo Tópico" />
		</c:if>
		<img src="/sigaa/ava/img/bin.png" style="overflow: visible;" alt="Remover Tópico">: Remover Tópico</br>
	</div>
		
	<c:if test="${ empty foruns }">
		<center>Nenhum item foi encontrado</center>
		<br/>
		<center> <h:commandButton id="voltar_nao_encontrado" value="<< Voltar" action="#{forumMedio.cancelarForumCursos}" /> </center>
	</c:if>
	
	<c:if test="${ not empty foruns }">
		<table class="listagem">
			<caption>Lista dos tópicos ativos</caption>
			<thead>
				<tr>
					<th>Turma</th>
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
				<td width="10%">${ n.forum.turmaSerie.descricaoCompleta }</td>
				<td width="50%">
					<h:commandLink id="mostrar" action="#{ forumMensagemMedio.mostrarForumMensagemTurmaSerie }"> 
						<h:outputText value="#{ n.titulo }"/>
						
						<f:param name="idForumMensagem" value="#{ n.id }"/>
						<f:param name="id" value="#{ n.forum.id }"/>
					</h:commandLink> (${ n.forum.nivelDescricao })
				</td>
				
				<td class="nomeUsuario"> ${ n.usuario.pessoa.nomeResumido } </td>
				<td style="text-align: right"> ${n.respostas } </td>
				<td style="text-align: center">
					<ufrn:format type="dataHora" valor="${  n.ultimaPostagem != null ? n.ultimaPostagem : n.data }"/>
				</td>
				
				<td class="icon">
				<c:if test="${ n.usuario.id == acesso.usuario.id}" >
					<h:commandLink id="remover" action="#{ forumMensagemMedio.removerMensagensTurmaSerie }" title="Remover Tópico"
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
						<center> <h:commandButton id="voltar" value="<< Voltar" action="#{forumMedio.cancelarForumCursos}" /> </center>
					</td>
				</tr>
			</tfoot>
	</table>
	
	<div style="text-align: center;"> 
    <h:commandButton image="/img/voltar.gif" actionListener="#{paginacao.previousPage}" rendered="#{paginacao.paginaAtual > 0 }"/>
 
     
    <em><h:outputText value="#{fn:length(foruns) }"/> Registro(s) Encontrado(s)</em>
	</div>
	</c:if>

</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>