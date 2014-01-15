<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.comum.dominio.LocalizacaoNoticiaPortal"%>
<link rel="stylesheet" media="all" href="/sigaa/css/portal_docente.css"	type="text/css" />
<script type="text/javascript">
	JAWR.loader.script('/javascript/paineis/noticias.js');
</script>

<f:view>
<%@include file="/lato/menu_coordenador.jsp" %>

<h:outputText value="#{ portalCoordenadorLato.create }" />

<div id="portal-docente">
	<div id="perfil-docente">
		<c:set var="cal" value="${portalCoordenadorLato.calendarioVigente}"/>
<h:form id="formPortal">
<table width="100%">
<tr>
	<td>
	<p align="center" style="font-size: 12pt; font-weight: bold;">
	<br>
	Portal da <br>Coordenação Lato Sensu
	</p>
	<br>
	<p align="center">
		${portalCoordenadorLato.cursoAtualCoordenacao.nome}<br>
		<small>${portalCoordenadorLato.cursoAtualCoordenacao.unidade.nome }</small>
	</p>
	</td>
</tr>

<c:if test="${fn:length(curso.allCursosCoordenacaoNivelCombo) > 0}">
<tr>
	<td valign="top">
    	<h:selectOneMenu valueChangeListener="#{curso.trocarCurso}" onchange="submit()" style="width: 100%">
    		<f:selectItem itemLabel="--> MUDAR DE CURSO <--" itemValue="0"/>
    		<f:selectItems value="#{curso.allCursosCoordenacaoNivelCombo}"/>
    	</h:selectOneMenu>
	</td>
</tr>
</c:if>

</table>
</h:form>
		<div id="agenda-docente">
		<h2>Calendário do Curso</h2>
		<c:set var="proposta" value="${portalCoordenadorLato.cursoAtualCoordenacao.propostaCurso}" />
		<c:set var="_processo" value="${portalCoordenadorLato.processoSeletivoMaisRecente}" />
		<table>
			<tr class="linhaImpar">
				<td colspan="3"><strong>Inscrições para Seleção</strong></td>
			</tr>
			<tr class="linhaPar">
				<td> <ufrn:format type="data" valor="${ _processo.editalProcessoSeletivo.inicioInscricoes }"/> </td>
				<td>a</td>
				<td> <ufrn:format type="data" valor="${ _processo.editalProcessoSeletivo.fimInscricoes }"/> </td>
			</tr>
			<tr class="linhaImpar">
				<td colspan="3"><strong>Período do Curso</strong></td>
			</tr>
			<tr class="linhaPar">
				<td> <ufrn:format type="data" valor="${ portalCoordenadorLato.cursoAtualCoordenacao.dataInicio }"/> </td>
				<td>a</td>
				<td> <ufrn:format type="data" valor="${ portalCoordenadorLato.cursoAtualCoordenacao.dataFim }"/> </td>
			</tr>
			<tr class="linhaImpar">
				<td colspan="3"><strong>Limite para submissão do relatório final</strong></td>
			</tr>
			<tr class="linhaPar">
				<td> </td>
				<td> até  </td>
				<td>  <ufrn:format type="data" valor="${ portalCoordenadorLato.cursoAtualCoordenacao.dataLimiteRelatorio }"/> </td>
			</tr>
		</table>
		</div>
		
		<c:if test="${not empty usuario.servidorAtivo}"> 
			<div id="agenda-docente" style="text-align: center">
				<h:form id="form_links">
					<h:commandLink action="#{portalDocente.linkMemorando}" >
						<h:graphicImage value="/img/memorandos_eletronicos.jpg" alt="Memorandos Eletrônicos" title="Memorandos Eletrônicos" style="border:1px solid #CCC; margin-left:9px;"></h:graphicImage>
					</h:commandLink>
				</h:form>
			</div>
		</c:if>
		
	</div>
</div>

<div id="main-docente">
	<%-- Notícias --%>
	<script type="text/javascript">
	var fcontent=new Array()
	<c:forEach var="noticia" items="${noticiaPortal.noticiasCoordenadorLato}" varStatus="loop">
	fcontent[${ loop.index }]="<p class=\"noticia destaque\"><a href=\"#\" id=\"noticia_${ noticia.id }\">${ noticia.titulo }</a></p><p class=\"descricao\"><a href=\"#\" style=\"font-weight: normal; text-decoration: none\" id=\"noticia_${ noticia.id }\">${  sf:escapeHtml(noticia.descricaoResumida) }</a></p>";
	</c:forEach>
	</script>
	<div id="noticias-portal">
		<c:if test="${not empty noticiaPortal.noticiasCoordenadorLato}">
			<script>
				<%-- essas variaveis sao passadas para o scroller-portal.js --%>			
				var portal = "<%= LocalizacaoNoticiaPortal.getPortal(LocalizacaoNoticiaPortal.PORTAL_COORDENADOR_LATO).getMd5() %>";
				var sistema = "${ request.contextPath }";
			</script>		
		<script type="text/javascript" src="/shared/javascript/scroller-portal.js"></script>
		</c:if>
		<c:if test="${ empty noticiaPortal.noticiasCoordenadorLato }">
		<p class="noticia destaque"> <br/>Não há notícias cadastradas.</p>
		</c:if>
	</div>

	<%-- Acompanhamento do Curso --%>
	<h:outputText value="#{lato.create}" />
	<div class="simple-panel">
		<h4>Acompanhamento do Curso</h4>
		<table>
		<tbody>
		<tr>
			<td><b>Código</b></td>
			<td><b>Nome</b></td>
			<td><b>Situação</b></td>
		</tr>
		<h:form id="formDisciplinas">
		
		<c:forEach items="#{portalCoordenadorLato.acompanhamentoCurso}" var="linha" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "odd" : "" }">
				<td class="descricao" >${linha.disciplina.codigo} </td>
				<c:if test="${not empty linha.turma}">
					<td class="descricao">
						${linha.disciplina.nome}
						<br/>Turma ${linha.turma.codigo}
						<br/>Período: <ufrn:format type="data" valor="${linha.turma.dataInicio}" /> a
						<ufrn:format type="data" valor="${linha.turma.dataFim}" />
						<c:if test="${not empty linha.turma.local}">Local: ${linha.turma.local}</c:if>
					</td>
					<td class="info">
							<h:commandLink action="#{turmaVirtual.entrar}" value="#{linha.turma.situacaoTurma.descricao}" title="Acesse a turma virtual">
								<f:param name="idTurma" value="#{linha.turma.id}"/>
							</h:commandLink>
					</td>
					<td class="info">
						<h:commandLink  action="#{buscaTurmaBean.selecionaTurma }" title="Listar Alunos">
							<h:graphicImage url="/img/user.png"/>
							<f:param name="id" value="#{linha.turma.id}"/>
						</h:commandLink>
					</td>
				</c:if>
				<c:if test="${empty linha.turma}">
					<td class="descricao"> ${linha.disciplina.nome} </td>
					<td class="info">
						<a href="${ctx}/ensino/criarTurma.do?dispatch=popular&idDisciplina=${linha.disciplina.id}" title="Criar turma">
							Não Iniciada
						</a>
					</td>
				</c:if>
			</tr>
		</c:forEach>
		</h:form>
		</tbody>
	</table>
	</div>
	
	<!-- EXIBE OS TOPICOS DO FORUM --> 
	<div id="forum-portal" class="simple-panel">
		<c:set var="foruns" value="#{forumMensagem.forumMensagemCoordenacaoLato}" /> 

			<c:if test="${ not empty foruns }">
				<h4>${forumMensagem.forum.titulo} </h4>
			</c:if>
			<c:if test="${ empty foruns }">
				<h4> Forum de Cursos </h4>
			</c:if>
			
			<h:form>
			<div class="descricaoOperacao">Caro Coordenador, este fórum é destinado para discussões relacionadas ao seu curso. 
			Todos os alunos do curso e a coordenação tem acesso a ele.<br />
			</div>
			
			<center><h:commandLink action="#{ forum.listarForunsCurso }" value="Cadastrar novo tópico para este fórum" /></center>
			<br/>

			<c:if test="${ empty foruns }">
				<center>Nenhum item foi encontrado</center>
			</c:if>

			<c:if test="${ not empty foruns }">

				<table class="listagem">
					<thead>
						<tr>
							<th>Título</th>
							<th>Autor</th>
							<th style="text-align: center">Respostas</th>
							<th style="text-align: center">Data</th>
							<th></th>
							</th>
							<th></th>
						</tr>
					</thead>

					<tbody>

						<c:forEach var="n" items="#{ foruns }" varStatus="status" end="5">
							<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" >

								<td width="50%"><h:commandLink
									action="#{ forumMensagem.mostrarForumMensagemCurso }">
									<h:outputText value="#{ n.titulo }" />
									<f:param name="idForumMensagem" value="#{ n.id }" />
									<f:param name="id" value="#{ n.forum.id }" />
								</h:commandLink></td>

								<!-- <td>${ n.titulo }</td> -->
								<td><acronym title="${ n.usuario.pessoa.nome}">
								${ n.usuario.login } </acronym></td>
								<td style="text-align: center">${n.respostas }</td>
								<td style="text-align: center"><c:if
									test="${ n.ultimaPostagem != null }">
									<fmt:formatDate pattern="dd/MM/yyyy"
										value="${ n.ultimaPostagem }" />
								</c:if> <c:if test="${ n.ultimaPostagem == null }">
									<fmt:formatDate pattern="dd/MM/yyyy" value="${ n.data }" />
								</c:if></td>

								<!-- 
								<c:if test="${ acesso.coordenadorCursoGrad }">
									<td><h:commandLink action="#{ forumMensagem.removerMensagensCurso }" onclick="return(confirm('Deseja realmente excluir este item?'));"><f:param name="id" value="#{ n.id }"/><h:graphicImage value="/img/delete.gif"/></h:commandLink></td>
							    </c:if>
							    
							    <c:if test="${ !acesso.coordenadorCursoGrad && n.usuario.id == forumMensagem.discenteLogado}">
									<td class="icon"><h:commandLink action="#{ forumMensagem.removerMensagensCurso }" onclick="return(confirm('Deseja realmente excluir este item?'));"><f:param name="id" value="#{ n.id }"/><h:graphicImage value="/img/delete.gif"/></h:commandLink></td>
							    </c:if>
							    -->
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</c:if>
		</h:form>
		</div>
	
	
</div>

<script type="text/javascript">
	PainelNoticias.init('/sigaa/portais/docente/viewNoticia.jsf');
</script>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
