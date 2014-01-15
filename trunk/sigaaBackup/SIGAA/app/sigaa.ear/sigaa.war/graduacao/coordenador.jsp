<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>
<%@page import="br.ufrn.comum.dominio.LocalizacaoNoticiaPortal"%>
<link rel="stylesheet" media="all" href="/sigaa/css/portal_docente.css" type="text/css" />

<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO } %>">
<script type="text/javascript">
	JAWR.loader.script('/javascript/paineis/noticias.js');
</script>

<f:view>
<%@include file="/graduacao/menu_coordenador.jsp" %>
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
	Portal da <br>Coordenação ${(acesso.coordenacaoProbasica?'PROBASICA':' de Curso')}
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
    		<f:selectItem itemLabel="--> MUDAR DE CURSO <--" itemValue="0"/>
    		<f:selectItems value="#{curso.allCursosCoordenacaoNivelCombo}"/>
    	</h:selectOneMenu>
	</td>
</tr>
</c:if>
</table>
</h:form>


		<div id="agenda-docente">
		<h2>Calendário  ${cal.ano}.${cal.periodo}</h2>
		<table>

			<tr class="linhaImpar">
				<td colspan="3"><strong>Solicitação de turmas</strong></td>
			</tr>
			<tr class="linhaPar">
				<td> <ufrn:format type="data" valor="${ cal.inicioSolicitacaoTurma }"/> </td>
				<td>a</td>
				<td> <ufrn:format type="data" valor="${ cal.fimSolicitacaoTurma }"/> </td>
			</tr>
			<tr class="linhaImpar">
				<td colspan="3"><strong>Solicitações on-line de matrícula</strong></td>
			</tr>
			<tr class="linhaPar">
				<td> <ufrn:format type="data" valor="${ cal.inicioMatriculaOnline }"/> </td>
				<td>a</td>
				<td> <ufrn:format type="data" valor="${ cal.fimMatriculaOnline }"/> </td>
			</tr>
			<tr class="linhaImpar">
				<td colspan="3"><strong>Análise das solicitações de matrícula</strong></td>
			</tr>
			<tr class="linhaPar">
				<td> <ufrn:format type="data" valor="${ cal.inicioCoordenacaoAnaliseMatricula }"/> </td>
				<td>a</td>
				<td> <ufrn:format type="data" valor="${ cal.fimCoordenacaoAnaliseMatricula }"/> </td>
			</tr>
			<tr class="linhaImpar">
				<td colspan="3"><strong>Último dia para trancamento</strong></td>
			</tr>
			<tr class="linhaPar">
				<td> </td>
				<td> até  </td>
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
						<h:graphicImage value="/img/memorandos_eletronicos.jpg" alt="Memorandos Eletrônicos" title="Memorandos Eletrônicos" style="border:1px solid #CCC; margin-left:9px;"></h:graphicImage>
					</h:commandLink>
				</h:form>
			</div>
		</c:if>
	
	</div>
</div>

	<div id="main-docente">
				<%-- NOTÍCIAS --%>
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
					<p class="noticia destaque"> <br/>Não há notícias cadastradas.</p>
					</c:if>
				</div>

				<%-- Solicitações de Matrícula --%>
				<div class="simple-panel">
				<h4>Matrículas On-Line Pendentes de Orientação</h4>
				<c:set var="solMatriculas" value="#{portalCoordenadorGrad.solicitacoesMatricula}" />
				<c:if test="${empty  solMatriculas}">
					<i>Não há matrículas pendentes</i>
				</c:if>
				
				<h:form>
					<h:dataTable id="dataTableSolicitacoes" var="sol" value="#{solMatriculas}" rowClasses="odd,''" rendered="#{not empty solMatriculas}">
						<h:column>
							<f:facet name="header">
								<h:outputText value="Matrícula" />
							</f:facet>
							<h:outputText value="#{sol.matricula}" />
						</h:column>
						
						<h:column>
							<f:facet name="header">
								<h:outputText value="Nome" />
							</f:facet>
							<h:outputText value="#{sol.pessoa.nome}" />
						</h:column>						
						
						<h:column>
							<f:facet name="header">
							</f:facet>
							<h:commandLink
								action="#{analiseSolicitacaoMatricula.selecionaDiscente}"
								title="Selecionar Discente">
									<f:param name="id" value="#{sol.id}" />
									<h:graphicImage url="/img/seta.gif" />
							</h:commandLink>
						</h:column>	
					</h:dataTable>
					
					<a4j:commandLink id="btnOrdenar" actionListener="#{portalCoordenadorGrad.ordernarOrientacaoMatricula}" 
						value="#{portalCoordenadorGrad.orderByNome ? 'ordernar por matricula' : 'ordernar por nome' }"
						rendered="#{portalCoordenadorGrad.totalPreMatriculas > 0}"
						reRender="btnOrdenar, dataTableSolicitacoes">
					</a4j:commandLink>							
					
										
					<h:outputText value=" | "  rendered="#{portalCoordenadorGrad.totalPreMatriculas > 0}" />					
					
					<h:commandLink action="#{analiseSolicitacaoMatricula.iniciar}" 
						rendered="#{portalCoordenadorGrad.totalPreMatriculas > 0}"
						value="ver todas matrículas on-line (#{portalCoordenadorGrad.totalPreMatriculas})"/>					
				</h:form>
				
				<%-- 
				<c:if test="${not empty solMatriculas}">
				<table>
					<thead>
					<tr>
						<th>Matrícula</th>
						<th>Nome</th>
						<th></th>
					</tr>
					</thead>
					<tbody>
					<h:form>
					<c:forEach items="#{solMatriculas}" var="d" varStatus="status">
						<tr class="${status.index % 2 == 0 ? "odd" : "" }">
							<td class="info" width="10%">${d.matricula} </td>
							<td class="info" align="center">${d.pessoa.nome}</td>
							<td width="3%">
								<h:commandLink action="#{analiseSolicitacaoMatricula.selecionaDiscente}" title="Selecionar Discente">
									<f:param name="id" value="#{d.id}" />
									<h:graphicImage url="/img/seta.gif" />
								</h:commandLink>
							</td>
						</tr>
					</c:forEach>
					</h:form>
					</tbody>
				</table>
					<h:form>
					<h:commandLink action="#{analiseSolicitacaoMatricula.iniciar}" value="ver todas matrículas on-line (#{portalCoordenadorGrad.totalPreMatriculas})"/>
					</h:form>
				</c:if>
				--%>
				</div>

				<div class="simple-panel">
				<h4>Trancamentos Pendentes de Orientação</h4>
				<c:set var="solTrancamentos" value="#{portalCoordenadorGrad.solicitacoesTrancamento}" />
				<c:if test="${empty  solTrancamentos}">
					<i>Não há trancamentos pendentes</i>
				</c:if>
				<c:if test="${not empty solTrancamentos}">
				<h:form>
				<table>
					<thead>
					<tr>
						<th style="text-align: right;padding-left: 2px;padding-right: 2px;">Matrícula</th>
						<th style="text-align: left;padding-left: 2px;padding-right: 2px;">Nome</th>
						<th></th>
					</tr>
					</thead>
					<tbody>
					<c:forEach items="#{solTrancamentos}" var="d" varStatus="status">
						<tr class="${status.index % 2 == 0 ? "odd" : "" }">
							<td class="info" width="15%"  style="text-align: right;">${d.matricula} </td>
							<td class="info"  style="text-align: left;">${d.pessoa.nome}</td>
							<td width="3%">
								<c:if test="${ portalCoordenadorGrad.cursoAtualCoordenacao.ADistancia }">
								<h:commandLink action="#{atenderTrancamentoMatricula.selecionarAlunoEad}">
									<f:param name="idAluno" value="#{d.id}" />
									<h:graphicImage url="/img/seta.gif" title="Selecionar Discente" />
								</h:commandLink>
								</c:if>
								<c:if test="${ !portalCoordenadorGrad.cursoAtualCoordenacao.ADistancia }">
								<h:commandLink action="#{atenderTrancamentoMatricula.selecionarAluno}">
									<f:param name="idAluno" value="#{d.id}" />
									<h:graphicImage url="/img/seta.gif" title="Selecionar Discente" />
								</h:commandLink>
								</c:if>
							</td>
						</tr>
					</c:forEach>
					</tbody>
				</table>
				<h:commandLink action="#{atenderTrancamentoMatricula.iniciarAtendimentoSolicitacaoGraduacao}" value="ver todos trancamentos (#{portalCoordenadorGrad.totalTrancamentos})"/>
				</h:form>
				</c:if>
				</div>
	<%@include file="/geral/atendimento_aluno/comum_atendimento.jsp" %>			

	<!-- EXIBE OS TOPICOS DO FORUM --> 
	<c:if test="${ acesso.coordenadorCursoGrad || acesso.secretarioGraduacao }">

	<div id="forum-portal" class="simple-panel">
		<c:set var="foruns" value="#{forumMensagem.forumMensagemCoordenacaoGraducao}" /> 

			<c:if test="${ not empty foruns }">
				<h4>${forumMensagem.forum.titulo} </h4>
			</c:if>
			<c:if test="${ empty foruns }">
				<h4> Fórum de Cursos </h4>
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
						</tr>
					</thead>

					<tbody>

						<c:forEach var="n" items="#{ foruns }" varStatus="status" end="5">
							<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" >

								<td width="50%"><h:commandLink
									action="#{ forumMensagem.mostrarForumMensagemCurso }">
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
									<h:commandLink action="#{forumMensagem.mostrarForumMensagemCurso}">
										<f:param name="idForumMensagem" value="#{ n.id }" />
										<f:param name="id" value="#{ n.forum.id }" />
										<h:graphicImage url="/img/seta.gif" title="Ver este Tópico" />
									</h:commandLink>
								</td>

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
	</c:if>
	
	</div>
<script type="text/javascript">
	PainelNoticias.init('/sigaa/portais/docente/viewNoticia.jsf');
</script>

</f:view>
</ufrn:checkRole>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
