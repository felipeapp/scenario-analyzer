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

<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.COORDENADOR_CURSO_STRICTO , SigaaPapeis.SECRETARIA_POS } %>">


<f:view>
<%@include file="menu_coordenador.jsp" %>

<div id="portal-docente">
	<div id="perfil-docente">
		<h:outputText value="#{calendario.create}" /> 
		<h:outputText value="#{portalCoordenacaoStricto.create}" /> 
		<c:set var="cal" value="${calendario.calendarioVigenteProgramaAtivo}" />
		 
		<h:form>
		<table width="100%">
			<tr>
				<td>
					<p align="center" style="font-size: 12pt; font-weight: bold;"><br>
					Coordenação de Pós-Graduação</p>
					<p align="center">${portalCoordenacaoStrictoBean.programaStricto.nome}<br>
					</p>
				</td>
			</tr>
			
			<c:if test="${not empty portalCoordenacaoStrictoBean.cursoAtualCoordenadroStricto}">
			<tr>
				<td>
					<p align="center" style="font-size: 12pt; font-weight: bold;">Curso</p>
					<p align="center">${portalCoordenacaoStrictoBean.cursoAtualCoordenadroStricto.nomeCursoStricto}<br>
					</p>
				</td>
			</tr>
			</c:if>
			
			<c:if test="${fn:length(curso.allCursosCoordenacaoNivelCombo) > 0}">
				<tr>
					<td valign="top">
						<h:selectOneMenu id="cbMudarPrograma" valueChangeListener="#{curso.trocarCurso}" 
						onchange="submit()" style="width: 100%">
		    			<f:selectItem itemLabel="-- MUDAR DE PROGRAMA --" itemValue="0"/>
		    			<f:selectItems value="#{curso.allCursosCoordenacaoNivelCombo}"/>
						</h:selectOneMenu>
					</td>
				</tr>
			</c:if>
			
			
			<c:if test="${fn:length(curso.allCursosCoordenadorStrictoCombo) > 0}">
				<tr>
					<td valign="top">
						<h:selectOneMenu id="cbMudarCurso"  valueChangeListener="#{curso.carregarCursoCoordenadorStricto}" 
						onchange="submit()" style="width: 100%">
		    				<f:selectItem itemLabel="--> SELECIONAR CURSO <--" itemValue="0"/>
		    				<f:selectItems value="#{curso.allCursosCoordenadorStrictoCombo}"/>
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
				<td colspan="3"><strong>Período letivo</strong></td>
			</tr>
			<tr class="linhaPar">
				<td> <ufrn:format type="data" valor="${ cal.inicioPeriodoLetivo }"/> </td>
				<td>a</td>
				<td> <ufrn:format type="data" valor="${ cal.fimPeriodoLetivo }"/> </td>
			</tr>
			<tr class="linhaImpar">
				<td colspan="3"><strong>Matrícula On-Line</strong></td>
			</tr>
			<tr class="linhaPar">
			<td><ufrn:format type="data"
				valor="${ cal.inicioMatriculaOnline }" /></td>
				<td>a</td>
			<td><ufrn:format type="data" valor="${ cal.fimMatriculaOnline }" />
			</td>
			</tr>
			<tr class="linhaImpar">
				<td colspan="3"><strong>Análise Matrícula On-Line</strong></td>
			</tr>
			<tr class="linhaPar">
			<td><ufrn:format type="data"
				valor="${ cal.inicioCoordenacaoAnaliseMatricula }" /></td>
				<td>a</td>
			<td><ufrn:format type="data"
				valor="${ cal.fimCoordenacaoAnaliseMatricula }" /></td>
			</tr>
			<tr class="linhaImpar">
				<td colspan="3"><strong>Re-Matricula</strong></td>
			</tr>
			<tr class="linhaPar">
			<td><ufrn:format type="data" valor="${ cal.inicioReMatricula }" />
			</td>
				<td>a</td>
			<td><ufrn:format type="data" valor="${ cal.fimReMatricula }" />
			</td>
			</tr>
		</table>
		</div><br />
		
		<c:if test="${not empty usuario.servidorAtivo}"> 
			<div id="agenda-docente" style="text-align: center">
				<h:form id="form_links">
					<h:commandLink action="#{portalDocente.linkMemorando}" >
						<h:graphicImage value="/img/memorandos_eletronicos.jpg" alt="Memorandos Eletrônicos" title="Memorandos Eletrônicos" style="border:1px solid #CCC; margin-left:9px;"></h:graphicImage>
					</h:commandLink>
				</h:form>
			</div>
		</c:if>
		
	<div id="resoluções"><br />
		<h2>Resoluções</h2>
	<table
		style="width: 96%; margin-left: 5px; margin-top: -10px; font-size: x-small; text-align: center;">
			<tr class="linhaImpar">
			<td><a href="${linkPublico.urlDownloadPublico}/portaria_328r_06_funpec_ufrn.pdf"
				target="_blank" style="font-weight: normal; color: black;" id="link1">
			Portaria Nº 328/R-06, Normas de Gerenciamento de Projetos ${ configSistema['siglaInstituicao'] } x
			FUNPEC </a></td>
			</tr>
			<tr class="linhaPar">
			<td><a href="http://www.ppg.ufrn.br/conteudo/documentos/legislacao/resolucao_172__2010___consepe.pdf"
				target="_blank" style="font-weight: normal; color: black;" id="link2">
			Resolução Nº 172/2010 - Estabelece normas para afastamentos do pessoal
			docente da ${ configSistema['siglaInstituicao'] } </a></td>
			</tr>
			<tr class="linhaPar">
			<td><a
				href="${linkPublico.urlDownloadPublico}/Resolucao006-2008-CONSAD.doc"
				target="_blank" style="font-weight: normal; color: black;" id="link4">
			Resolução Nº 006/2008-CONSAD, 27 de março de 2008 </a></td>
			</tr>
			<tr class="linhaImpar">
			<td><a
				href="${linkPublico.urlDownloadPublico}/res0722004.pdf"
				target="_blank" style="font-weight: normal; color: black;" id="link5">
			Resolução Nº 072/2004- CONSEPE, 09 de novembro de 2004 (Dispõe sobre
			Normas dos Programas e Cursos de Pós-Graduação da ${ configSistema['siglaInstituicao'] }) </a></td>
			</tr>
			<tr class="linhaPar">
			<td><a href="http://www.ppg.ufrn.br/conteudo/documentos/legislacao/resolucao_n_0632010.pdf"
				target="_blank" style="font-weight: normal; color: black;" id="link6">
			Resolução Nº 063/2010 - Assistência à Docência da ${ configSistema['siglaInstituicao'] } </a></td>
			</tr>
		</table>
		<br/>
		</div>
		
	</div>
</div>

	<div id="main-docente"><%-- Notícias --%> <script
		type="text/javascript">
		var fcontent=new Array()
		<c:forEach var="noticia" items="${noticiaPortal.noticiasCoordenadorStricto}" varStatus="loop">
		fcontent[${ loop.index }]="<p class=\"noticia destaque\"><a href=\"#\" id=\"noticia_${ noticia.id }\">${ sf:escapeHtml(noticia.titulo) }</a></p><p class=\"descricao\"><a href=\"#\" style=\"font-weight: normal; text-decoration: none\" id=\"noticia_${ noticia.id }\">${  sf:escapeHtml(noticia.descricaoResumida) }</a></p>";
		</c:forEach>
	</script>
	<div id="noticias-portal">
		<c:if test="${ not empty noticiaPortal.noticiasCoordenadorStricto }">
			<script>
					<%-- essas variaveis sao passadas para o scroller-portal.js --%>			
				var portal = "<%= LocalizacaoNoticiaPortal.getPortal(LocalizacaoNoticiaPortal.PORTAL_COORDENADOR_STRICTO).getMd5() %>";
				var sistema = "${ request.contextPath }";
			</script>		
			<script type="text/javascript" src="/shared/javascript/scroller-portal.js"></script>
		</c:if> 
		<c:if test="${ empty noticiaPortal.noticiasCoordenadorStricto }">
			<p class="noticia destaque"><br />
				Não há notícias cadastradas.
			</p>
		</c:if>
	</div>
	
	<%-- Solicitações de Matrícula --%>
	<c:if test="${acesso.coordenadorCursoStricto}">
	<div class="simple-panel">
	<h4>Matrículas On-Line Pendentes de Orientação</h4>
	<c:set var="solMatriculas"
		value="#{portalCoordenacaoStrictoBean.solicitacoesMatricula}" />
		
	<c:if test="${empty  solMatriculas}">
		<i>Não há matrículas pendentes</i>
	</c:if>
	
	<style>
		.matricula{
			width: 130px;
			text-align: left;
		}
		
		.nome {
			width: 500px;
			text-align: left;
		}
	</style>
	
	<h:form>
		<h:dataTable id="dataTableSolicitacoes" var="sol" value="#{solMatriculas}" rowClasses="odd,''" rendered="#{not empty solMatriculas}">
			<h:column headerClass="matricula">
				<f:facet name="header">
					<h:outputText value="Matrícula" />
				</f:facet>
				<h:outputText value="#{sol.matricula}" />
			</h:column>
			<h:column headerClass="nome">
				<f:facet name="header">
					<h:outputText value="Nome" />
				</f:facet>
				<h:outputText value="#{sol.pessoa.nome}" />
			</h:column>	
			
			<h:column>
				<f:facet name="header">
				</f:facet>
				<h:commandLink id="btSelecionarDisceteSolicitacoes"
					action="#{analiseSolicitacaoMatricula.selecionaDiscente}"
					title="Selecionar Discente">
						<f:param name="id" value="#{sol.id}" />
						<f:param name="outrosProgramas" value="false" />
						<h:graphicImage url="/img/seta.gif" />
				</h:commandLink>
			</h:column>	
		</h:dataTable>

		<a4j:commandLink id="btnOrdenar" actionListener="#{portalCoordenacaoStrictoBean.ordernarOrientacaoMatricula}" 
			value="#{portalCoordenacaoStrictoBean.orderByNome ? 'ordernar por matrícula' : 'ordernar por nome' }"
			rendered="#{portalCoordenacaoStrictoBean.totalPreMatriculas > 0}"
			reRender="btnOrdenar, dataTableSolicitacoes">
		</a4j:commandLink>		
		
		<h:outputText value=" | " rendered="#{portalCoordenacaoStrictoBean.totalPreMatriculas > 0}" />
		
		<h:commandLink id="btverTodasMatriculasSolicitacoes" action="#{analiseSolicitacaoMatricula.iniciar}"
			value="ver todas matrículas on-line (#{portalCoordenacaoStrictoBean.totalPreMatriculas})" 
			rendered="#{portalCoordenacaoStrictoBean.totalPreMatriculas > 0}" />
		
		<center>
			<div style="z-index: 2;">
				<a4j:status>
					<f:facet name="start">
						<h:graphicImage value="/img/ajax-loader.gif"/>
					</f:facet>
				</a4j:status>
			</div>
		</center>
		
	</h:form>
	</div>
	
	<%-- Solicitações de Matrícula de Outros Programas --%>
	<c:set var="solMatriculasOutrosProgramas" value="#{portalCoordenacaoStrictoBean.solicitacoesMatriculaOutroPrograma}"/>
	<c:if test="${not empty solMatriculasOutrosProgramas}">	
		<div class="simple-panel">	
			<h4>Matrículas On-Line Pendentes de Orientação (Outros Programas)</h4>			
			<h:form>
				<h:dataTable id="dataTableSolicitacoesOutrosProgramas" var="sol" value="#{solMatriculasOutrosProgramas}" rowClasses="odd,''" rendered="#{not empty solMatriculasOutrosProgramas}">
					<h:column headerClass="matricula">
						<f:facet name="header">
							<h:outputText value="Matrícula" />
						</f:facet>
						<h:outputText value="#{sol.matricula}" />
					</h:column>
					<h:column headerClass="nome">
						<f:facet name="header">
							<h:outputText value="Nome" />
						</f:facet>
						<h:outputText value="#{sol.pessoa.nome}" />
					</h:column>	
					
					<h:column>
						<f:facet name="header">
						</f:facet>
						<h:commandLink id="btSelecionarDiscenteOutroPrograma"
							action="#{analiseSolicitacaoMatricula.selecionaDiscente}"
							title="Selecionar Discente">
								<f:param name="id" value="#{sol.id}" />
								<f:param name="outrosProgramas" value="true" />
								<h:graphicImage url="/img/seta.gif" />
						</h:commandLink>
					</h:column>	
				</h:dataTable>
					
				<h:commandLink id="btverTodosOutroPrograma" action="#{analiseSolicitacaoMatricula.iniciarDiscentesOutrosProgramas}"
					value="ver todas as matrículas de outros Programas (#{portalCoordenacaoStrictoBean.totalPendentesOutrosProgramas})" 
					rendered="#{portalCoordenacaoStrictoBean.totalPendentesOutrosProgramas > 0}" />			
			</h:form>		
		</div>
	</c:if>	
	</c:if>
	<div class="simple-panel">
	<h4>Trancamentos Pendentes de Orientação</h4>
	<c:set var="solTrancamentos"
		value="#{portalCoordenacaoStrictoBean.solicitacoesTrancamento}" /> <c:if
		test="${empty  solTrancamentos}">
		<i>Não há trancamentos pendentes</i>
	</c:if> <c:if test="${not empty solTrancamentos}">
	<h:form>
	<table>
		<thead>
		<tr>
			<th>Matrícula</th>
			<th>Nome</th>
			<th></th>
		</tr>
		</thead>
		<tbody>
		<c:forEach items="#{solTrancamentos}" var="d" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "odd" : "" }">
				<td class="info" width="15%">${d.matricula} </td>
				<td class="info">${d.pessoa.nome}</td>
							<td width="3%"><c:if test="${ portalCoordenacaoStrictoBean.cursoAtualCoordenacao.ADistancia }">
								<h:commandLink id="btSelecionarDiscenteTrancamentos"
									action="#{atenderTrancamentoMatricula.selecionarAlunoEad}">
						<f:param name="idAluno" value="#{d.id}" />
						<h:graphicImage url="/img/seta.gif" title="Selecionar Discente" />
					</h:commandLink>
						</c:if> <c:if test="${ !portalCoordenacaoStrictoBean.cursoAtualCoordenacao.ADistancia }">
								<h:commandLink id="btSelecionarDiscenteTrancamentosOutros"
									action="#{atenderTrancamentoMatricula.selecionarAluno}">
						<f:param name="idAluno" value="#{d.id}" />
						<h:graphicImage url="/img/seta.gif" title="Selecionar Discente" />
					</h:commandLink>
							</c:if></td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
			<h:commandLink id="btverTodosTrancamentos"
				action="#{atenderTrancamentoMatricula.iniciarAtendimentoSolicitacaoStricto}"
				value="ver todos trancamentos (#{portalCoordenacaoStrictoBean.totalTrancamentos})" />
	</h:form>
	</c:if></div>

	<%@include file="/geral/atendimento_aluno/comum_atendimento.jsp"%>

	<!-- EXIBE OS TOPICOS DO FORUM --> 
	<c:if test="${ acesso.coordenadorCursoStricto || acesso.secretariaPosGraduacao }">

	<div id="forum-portal" class="simple-panel">
		<c:set var="foruns" value="#{forumMensagem.forumMensagemCoordenacaoStricto}" /> 

			<c:if test="${ not empty foruns }">
				<h4>${forumMensagem.forum.titulo} </h4>
			</c:if>
			<c:if test="${ empty foruns }">
				<h4> Forum de Cursos </h4>
			</c:if>
		
		
		<h:form>
			<div class="descricaoOperacao">Caro Coordenador, este fórum é
			destinado para discussões relacionadas ao seu curso. Todos os alunos
			do curso e a coordenação tem acesso a ele.</div>

			<center><h:commandLink id="btnovoTopico"
				action="#{ forum.listarForunsCurso }" value="Cadastrar novo tópico para este fórum" /></center><br/>

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

								<td width="50%"><h:commandLink id="btMostrarForumCurso"
									action="#{ forumMensagem.mostrarForumMensagemCurso }">
									<h:outputText value="#{ n.titulo }" />
									<f:param name="idForumMensagem" value="#{ n.id }" />
									<f:param name="id" value="#{ n.forum.id }" />
								</h:commandLink> (${ n.forum.nivelDescricao })
								</td> 

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
		</h:form></div>
	</c:if></div>
</f:view>
</ufrn:checkRole>

<ufrn:checkNotRole papeis="<%= new int[] { SigaaPapeis.COORDENADOR_CURSO_STRICTO , SigaaPapeis.SECRETARIA_POS } %>">
	<div style="line-height:200px;text-align:center;font-size:1.3em;font-weight:bold;color: #FF0000;">Você não possui permissão para visualizar esta página.</div>
</ufrn:checkNotRole>
<script type="text/javascript">
	PainelNoticias.init('/sigaa/portais/docente/viewNoticia.jsf');
</script>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
