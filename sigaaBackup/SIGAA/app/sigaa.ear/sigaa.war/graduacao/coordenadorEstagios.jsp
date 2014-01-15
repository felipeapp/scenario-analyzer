<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>
<%@page import="br.ufrn.comum.dominio.LocalizacaoNoticiaPortal"%>
<link rel="stylesheet" media="all" href="/sigaa/css/portal_docente.css" type="text/css" />

<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.COORDENADOR_ESTAGIOS } %>">
<script type="text/javascript">
	JAWR.loader.script('/javascript/paineis/noticias.js');
</script>

<f:view>
<%@include file="/graduacao/menu_coordenador_estagios.jsp" %>
<h:outputText value="#{ portalCoordenadorGrad.create }" />

<div id="portal-docente">
	<div id="perfil-docente">
		<c:set var="cal" value="#{portalCoordenadorGrad.calendarioVigente}"/>
<h:form>
<table width="100%">
<tr>
	<td>
	<p align="center" style="font-size: 12pt; font-weight: bold;">
	<br>
	Portal da <br>Coordena��o de Est�gios
	</p>
	<br>
	<p align="center">
		${portalCoordenadorGrad.cursoAtualCoordenacao.descricao}<br>
		<small>${portalCoordenadorGrad.cursoAtualCoordenacao.unidade.nome }</small>
	</p>
	</td>
</tr>

<c:if test="${fn:length(curso.allCursosCoordenacaoNivelCombo) > 0}">
<tr>
	<td valign="top">
    	<h:selectOneMenu valueChangeListener="#{curso.trocarCurso}" onchange="submit()" style="width: 100%">
    		<f:selectItem itemLabel="-- MUDAR DE CURSO --" itemValue="0"/>
    		<f:selectItems value="#{curso.allCursosCoordenacaoNivelCombo}"/>
    	</h:selectOneMenu>
	</td>
</tr>
</c:if>
</table>
</h:form>


		<div id="agenda-docente">
		<h2>Calend�rio  ${cal.ano}.${cal.periodo}</h2>
		<table>

			<tr class="linhaImpar">
				<td colspan="3"><strong>Solicita��o de turmas</strong></td>
			</tr>
			<tr class="linhaPar">
				<td> <ufrn:format type="data" valor="${ cal.inicioSolicitacaoTurma }"/> </td>
				<td>a</td>
				<td> <ufrn:format type="data" valor="${ cal.fimSolicitacaoTurma }"/> </td>
			</tr>
			<tr class="linhaImpar">
				<td colspan="3"><strong>Solicita��es on-line de matr�cula</strong></td>
			</tr>
			<tr class="linhaPar">
				<td> <ufrn:format type="data" valor="${ cal.inicioMatriculaOnline }"/> </td>
				<td>a</td>
				<td> <ufrn:format type="data" valor="${ cal.fimMatriculaOnline }"/> </td>
			</tr>
			<tr class="linhaImpar">
				<td colspan="3"><strong>An�lise das solicita��es de matr�cula</strong></td>
			</tr>
			<tr class="linhaPar">
				<td> <ufrn:format type="data" valor="${ cal.inicioCoordenacaoAnaliseMatricula }"/> </td>
				<td>a</td>
				<td> <ufrn:format type="data" valor="${ cal.fimCoordenacaoAnaliseMatricula }"/> </td>
			</tr>
			<tr class="linhaImpar">
				<td colspan="3"><strong>�ltimo dia para trancamento</strong></td>
			</tr>
			<tr class="linhaPar">
				<td> </td>
				<td> at�  </td>
				<td>  <ufrn:format type="data" valor="${ cal.fimTrancamentoTurma }"/> </td>
			</tr>
			<tr class="linhaImpar">
				<td colspan="3"><strong>Re-Matricula</strong></td>
			</tr>
			<tr class="linhaPar">
				<td> <ufrn:format type="data" valor="${ cal.inicioReMatricula }"/> </td>
				<td>a</td>
				<td> <ufrn:format type="data" valor="${ cal.fimReMatricula }"/> </td>
			</tr>
		</table>
		</div>

		<c:if test="${not empty usuario.servidorAtivo}"> 
			<div id="agenda-docente" style="text-align: center">
				<h:form id="form_links">
					<h:commandLink action="#{portalDocente.linkMemorando}" id="linkMemorando">
						<h:graphicImage value="/img/memorandos_eletronicos.jpg" alt="Memorandos Eletr�nicos" title="Memorandos Eletr�nicos" style="border:1px solid #CCC; margin-left:9px;"></h:graphicImage>
					</h:commandLink>
				</h:form>
			</div>
		</c:if>
	
	</div>
</div>

	<div id="main-docente">
				<%-- NOT�CIAS --%>
				<script type="text/javascript">
				var fcontent=new Array()
				<c:forEach var="noticia" items="${noticiaPortal.noticiasCoordenadorGraduacao}" varStatus="loop">
				fcontent[${ loop.index }]="<p class=\"noticia destaque\"><a href=\"#\" id=\"noticia_${ noticia.id }\">${ sf:escapeHtml(noticia.titulo) }</a></p><p class=\"descricao\"><a href=\"#\" style=\"font-weight: normal; text-decoration: none\" id=\"noticia_${ noticia.id }\">${  sf:escapeHtml(noticia.descricaoResumida) }</a></p>";
				</c:forEach>
				</script>
				<div id="noticias-portal">
					<c:if test="${ not empty noticiaPortal.noticiasCoordenadorGraduacao }">
					<script>
						<%-- essas variaveis sao passadas para o scroller-portal.js --%>
						var portal = "<%= LocalizacaoNoticiaPortal.getPortal(LocalizacaoNoticiaPortal.PORTAL_COORDENADOR_GRADUACAO).getMd5() %>";
						var sistema = "${ request.contextPath }";
					</script>					
					<script type="text/javascript" src="/shared/javascript/scroller-portal.js"></script>
					</c:if>
					<c:if test="${ empty noticiaPortal.noticiasCoordenadorGraduacao }">
					<p class="noticia destaque"> <br/>N�o h� not�cias cadastradas.</p>
					</c:if>
				</div>

	<!-- EXIBE OS TOPICOS DO FORUM --> 
	<div id="forum-portal" class="simple-panel">
		<c:set var="foruns" value="#{forumMensagem.forumMensagemCoordenacaoGraducao}" /> 

			<c:if test="${ not empty foruns }">
				<h4>${forumMensagem.forum.titulo} </h4>
			</c:if>
			<c:if test="${ empty foruns }">
				<h4> F�rum de Cursos </h4>
			</c:if>
			
			<h:form>
			<div class="descricaoOperacao">Caro Coordenador, este f�rum � destinado para discuss�es relacionadas ao seu curso. 
			Todos os alunos do curso e a coordena��o tem acesso a ele.<br />
			</div>
			
			<center><h:commandLink action="#{ forum.listarForunsCurso }" value="Cadastrar novo t�pico para este f�rum" /></center>
			<br/>

			<c:if test="${ empty foruns }">
				<center>Nenhum item foi encontrado</center>
			</c:if>

			<c:if test="${ not empty foruns }">

				<table class="listagem">
					<thead>
						<tr>
							<th>T�tulo</th>
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
									action="#{ forumMensagem.mostrarForumMensagemCurso }">
									<h:outputText value="#{ n.titulo }" title="Ver este T�pico"  />
									<f:param name="idForumMensagem" value="#{ n.id }" />
									<f:param name="id" value="#{ n.forum.id }" />
								</h:commandLink></td>
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
									<h:commandLink action="#{forumMensagem.mostrarForumMensagemCurso}" id="mostrarForumMsgCurso">
										<f:param name="idForumMensagem" value="#{ n.id }" />
										<f:param name="id" value="#{ n.forum.id }" />
										<h:graphicImage url="/img/seta.gif" title="Ver este T�pico" />
									</h:commandLink>
								</td>
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
</ufrn:checkRole>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
