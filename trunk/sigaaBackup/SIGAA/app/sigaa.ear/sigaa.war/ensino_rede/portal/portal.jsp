<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.comum.dominio.LocalizacaoNoticiaPortal"%>
<link rel="stylesheet" media="all" href="/sigaa/css/portal_docente.css"	type="text/css" />
<script type="text/javascript">
	JAWR.loader.script('/javascript/paineis/noticias.js');
</script>
<style>
<!--
table.menuRapido {
	text-align: center;
	margin: 0 auto;
}
table.menuRapido td {
	text-align: center;
	font-size: x-small;
	width: 100px;
}
-->
</style>

<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.COORDENADOR_UNIDADE_REDE } %>">

<f:view>
<%@include file="menu_coordenador_rede.jsp" %>

<div id="portal-docente">
	<div id="perfil-docente">
		<h:outputText value="#{calendario.create}" /> 
		<c:set var="cal" value="${calendario.calendarioVigenteProgramaAtivo}" />
		 
		<h:form>
		<table width="100%">
			<tr>
				<td>
					<p align="center" style="font-size: 12pt; font-weight: bold;">
					<br>
					Coordenação</p>
					<p align="center"><h:outputText value="#{portalCoordenadorRedeBean.coordenacao.descricaoDetalhado }"/><br>
					</p>
				</td>
			</tr>
			
			<tr>
				<td>
					<p align="center" style="font-size: 12pt; font-weight: bold;">Curso</p>
					<p align="center"><h:outputText value="#{portalCoordenadorRedeBean.coordenacao.dadosCurso.curso.nome }"/><br>
					</p>
				</td>
			</tr>
		</table>
		</h:form>


		<div id="agenda-docente">
		<h2>Calendário  ${cal.ano}.${cal.periodo}</h2>
		<table>
			<tr class="linhaImpar">
				<td colspan="3"><strong>Período letivo</strong></td>
			</tr>
			<tr class="linhaPar">
				<td> <ufrn:format type="data" valor="${ cal.inicioPeriodoLetivo }"/> </td>
				<td>a</td>
				<td> <ufrn:format type="data" valor="${ cal.fimPeriodoLetivo }"/> </td>
			</tr>
		</table>
		</div><br />
		
	<div id="resoluções"><br />
		<h2>Resoluções</h2>
		<table	style="width: 96%; margin-left: 5px; margin-top: -10px; font-size: x-small; text-align: center;">
			<tr class="linhaImpar">
				<td>
					<a href="${linkPublico.urlDownloadPublico}/portaria_328r_06_funpec_ufrn.pdf" target="_blank" style="font-weight: normal; color: black;" id="link1">
						Edital Profletras 2013 </a>
				</td>
			</tr>
		</table>
		<br/>
		</div>
		
	</div>
</div>

	<div id="main-docente"><%-- Notícias --%> 
	<script type="text/javascript">
		var fcontent=new Array()
		<c:forEach var="noticia" items="${noticiaPortal.noticiasProgramaRede}" varStatus="loop">
		fcontent[${ loop.index }]="<p class=\"noticia destaque\"><a href=\"#\" id=\"noticia_${ noticia.id }\">${ sf:escapeHtml(noticia.titulo) }</a></p><p class=\"descricao\"><a href=\"#\" style=\"font-weight: normal; text-decoration: none\" id=\"noticia_${ noticia.id }\">${  sf:escapeHtml(noticia.descricaoResumida) }</a></p>";
		</c:forEach>
	</script>
	<div id="noticias-portal">
		<c:if test="${ not empty noticiaPortal.noticiasProgramaRede }">
			<script>
					<%-- essas variaveis sao passadas para o scroller-portal.js --%>			
				var portal = "<%= LocalizacaoNoticiaPortal.getPortal(LocalizacaoNoticiaPortal.PORTAL_PROGRAMA_REDE).getMd5() %>";
				var sistema = "${ request.contextPath }";
			</script>		
			<script type="text/javascript" src="/shared/javascript/scroller-portal.js"></script>
		</c:if> 
		<c:if test="${ empty noticiaPortal.noticiasProgramaRede }">
			<p class="noticia destaque"><br />
				Não há notícias cadastradas.
			</p>
		</c:if>
	</div>

	<div class="simple-panel">
		<h4>Turmas Abertas</h4>
		<table style="margin-top: 1%;">
			<thead>
			<tr>
				<th width="50%">Componente Curricular</th>
				<th width="50%">Docentes</th>
			</tr>
			</thead>
			<tbody>
				<c:forEach items="#{portalCoordenadorRedeBean.turmasAbertas}" var="t" varStatus="status">
					<tr class="${status.index % 2 == 0 ? "odd" : "" }">
						<h:form id="form_acessarTurmaVirtual">
						<td class="descricao">
								<h:commandLink id="turmaVirtual" action="#{alterarSituacaoMatriculaRede.iniciarConsolidacaoPortal}" value="#{t.componente.nome}">
									<f:param name="idTurma" value="#{t.id}" />
								</h:commandLink>
						</td>
						<td >
							<c:forEach items="#{t.docentesTurmas}" var="dt" varStatus="status">
								<h:outputText value="#{ dt.docente.pessoa.nome }"></h:outputText><br/>
							</c:forEach>
						</td>
						</h:form>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>

	<div class="simple-panel">
		<h4>Quadro de Enventos</h4>
		<center><i>Nenhum evento localizado</i></center>
	</div>
	
<!-- EXIBE OS TOPICOS DO FORUM --> 

	<div id="forum-portal" class="simple-panel">
		<c:set var="foruns" value="#{forumMensagem.forumMensagemCoordenacaoEnsinoRede}" /> 

			<c:if test="${ not empty foruns }">
				<h4>${forumMensagem.forum.titulo} </h4>
			</c:if>
			<c:if test="${ empty foruns }">
				<h4> Fórum de Programa </h4>
			</c:if>
			
			<h:form>
			<div class="descricaoOperacao">Caro Coordenador, este fórum é destinado para discussões relacionadas ao seu programa de ensino em rede. 
			Todos os demais coordenadores possuem acesso a ele.<br />
			</div>
			
			<center><h:commandLink action="#{ forum.listarForunsPrograma }" value="Visualizar todos os tópicos para este fórum / Cadatrar novo tópico" /></center>
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
						</tr>
					</thead>

					<tbody>

						<c:forEach var="n" items="#{ foruns }" varStatus="status" end="5">
							<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" >

								<td width="50%"><h:commandLink id="btaoMostrarForum"
									action="#{ forumMensagem.mostrarForumMensagemPrograma }">
									<h:outputText value="#{ n.titulo }" title="Ver este Tópico"  />
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
								<td>
									<h:commandLink action="#{forumMensagem.mostrarForumMensagemPrograma}" id="mostrarForumMsgCurso">
										<f:param name="idForumMensagem" value="#{ n.id }" />
										<f:param name="id" value="#{ n.forum.id }" />
										<h:graphicImage url="/img/seta.gif" title="Ver este Tópico" />
									</h:commandLink>
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</c:if>
		</h:form>
		</div>

</f:view>

</ufrn:checkRole>

<script type="text/javascript">
	PainelNoticias.init('/sigaa/portais/docente/viewNoticia.jsf');
</script>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
