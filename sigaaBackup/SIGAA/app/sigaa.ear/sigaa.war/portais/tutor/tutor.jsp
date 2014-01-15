<%@include file="/WEB-INF/jsp/include/cabecalho.jsp" %>
<%@page import="br.ufrn.comum.dominio.LocalizacaoNoticiaPortal"%>
<link rel="stylesheet" media="all" href="/sigaa/css/portal_docente.css" type="text/css" />

<f:view>
	
	<div id="portal-docente">
	
		<f:subview id="menu">
			<%@include file="/portais/tutor/menu_tutor.jsp" %>
		</f:subview>
		
			<h:outputText value="#{portalTutor.create}"/>

			<input type="hidden" name="id" value="${ usuario.discenteAtivo.id }"/>

			<div id="perfil-docente">
			<div class="pessoal-docente">
			<div class="foto">
			
			<c:if test="${usuario.idFoto != null}">
				<img src="${ctx}/verFoto?idFoto=<h:outputText value="#{usuario.idFoto}"/>&key=${ sf:generateArquivoKey(usuario.idFoto) }" width="60" height="110" />
			</c:if> 
			
			<c:if test="${usuario.idFoto == null}">
				<img src="${ctx}/img/no_picture.png" width="90" height="120" />
			</c:if>
	</div>

	<h:form>
		<ul>
			<li><a href="<%="/sigaa/abrirCaixaPostal.jsf?sistema="+String.valueOf(Sistema.SIGAA)%>" > Minhas Mensagens </a></li>
			<li><a class="perfil" href="perfil.jsf"> Trocar Foto </a></li>
			<li><a class="perfil" href="perfil.jsf"> <h:commandLink action="#{ dadosPessoaisTutor.iniciarAlteracaoDadosTutor}" value="Atualizar Dados Pessoais" /> </a></li>
		</ul>
	</h:form>
	
	<div class="clear"></div>
	</div>

	<p class="info-docente"><span class="nome"> 
		<b>
			<small>${usuario.pessoa.nome}</small>
		</b>
		
	<hr>
	</span>
	
	<div id="agenda-docente">
	<h4>Dados Pessoais</h4>

			<table>
				<tr>
					<td>Nome:</td>
					<td>${usuario.pessoa.nome}</td>
				</tr>
				<tr>
					<td>E-Mail:</td>
					<td>${usuario.email}</td>
				</tr>
				<c:if test="${usuario.tutor.presencial}">
					<tr>
						<td>Cursos:</td>
						<td>
							<table>
							<c:forEach items="#{ usuario.tutor.poloCursos }" var="poloCursos">
								<tr><td>${poloCursos.curso.nome}</td></tr>
							</c:forEach>	
							</table>	
						</td>
					</tr>
					<tr>
						<td>Pólo:</td>
						<td>${usuario.tutor.poloCurso.polo.cidade.nomeUF }</td>
					</tr>
					<tr>
						<td>Alunos Orientados:</td>
							<td><h:form>
								<h:commandLink value="#{portalTutor.qtdAlunosOrientandos}" action="#{ portalTutor.verAlunos }" />
							</h:form>
						</td>
					</tr>
				</c:if>
			</table>
	
	<c:if test="${usuario.tutor.presencial}">
	<center><B>Ajuda</B></center>
	<hr>
		<c:if test="${ usuario.tutor.acessoCompleto }">
			<img src="${ctx}/img/icones/page_white_edit.png"> Avaliar o aluno<br>
		</c:if>	
		<img src="${ctx}/img/report.png"> Histórico do aluno<br>
		<c:if test="${ usuario.tutor.acessoCompleto }">
			<img src="${ctx}/img/buscar.gif"> Visualizar Avaliações do Aluno<br>
		</c:if>	
		<img src="${ctx}/img/view.gif"> Notas do discente <br>
		<img src="${ctx}/img/email_go.png"> Enviar mensagem para o Aluno <br>
	</c:if>

	</div>

	<p style="margin: 4px"><c:if test="${portalTutor.perfil != null}">
		<br>
		<ufrn:format name="portalTutor" property="perfil.descricao"
			type="texto" />
	</c:if></p>
	<br>
	</p>

	<%-- PRÓXIMOS COMPROMISSOS --%></div>

	<div id="main-docente">
				
			<%-- NOTÍCIAS --%>
			<script type="text/javascript">
			var fcontent=new Array()
			<c:forEach var="noticia" items="${noticiaPortal.noticiasTutor}" varStatus="loop">
			fcontent[${ loop.index }]="<p class=\"noticia destaque\"><a href=\"#\" id=\"noticia_${ noticia.id }\">${ noticia.titulo }</a></p><p class=\"descricao\"><a href=\"#\" style=\"font-weight: normal; text-decoration: none\" id=\"noticia_${ noticia.id }\">${  sf:escapeHtml(noticia.descricaoResumida) }</a></p>";
			</c:forEach>
			</script>
			<div id="noticias-portal">
				<c:if test="${ not empty noticiaPortal.noticiasTutor }">
				
				<script>
					<%-- essas variaveis sao passadas para o scroller-portal.js --%>				
					var portal = "<%= LocalizacaoNoticiaPortal.getPortal(LocalizacaoNoticiaPortal.PORTAL_TUTOR).getMd5() %>";
					var sistema = "${ request.contextPath }";
				</script>				
				
				<script type="text/javascript" src="/shared/javascript/scroller-portal.js"></script>
				</c:if>
				<c:if test="${ empty noticiaPortal.noticiasTutor }">
				<p class="noticia destaque"> <br/>Não há notícias cadastradas.</p>
				</c:if>
			</div>
			
			<%-- Solicitações de Matrícula --%>
			<c:if test="${portalTutor.alunoEadFazMatriculaOnline && usuario.tutor.acessoCompleto}">
			
				<div class="simple-panel">
				<h4>Matrículas On-Line Pendentes de Orientação</h4>
				<c:set var="solMatriculas" value="#{portalTutor.solicitacoesMatricula}" />
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
								action="#{analiseSolicitacaoMatricula.selecionaDiscente}" id="btaoSelecionaDiscente"
								title="Selecionar Discente">
									<f:param name="id" value="#{sol.id}" />
									<h:graphicImage url="/img/seta.gif" />
							</h:commandLink>
						</h:column>	
					</h:dataTable>
					
					<h:commandLink action="#{analiseSolicitacaoMatricula.iniciar}" id="btnInniciar" rendered="#{portalTutor.totalPreMatriculas > 0}"
						value="ver todas as matrículas on-line (#{portalTutor.totalPreMatriculas})"/>					
				</h:form>
				
				</div>
			</c:if>
			<%-- FIM -- Solicitações de Matrícula --  FIM  --%>
			
			<c:set var="turmasVirtuais" value="${ portalTutor.turmasVirtuaisHabilitadas }"/>
			<c:if test="${!empty turmasVirtuais}">
			<div id="turmas-habilitadas" class="simple-panel">
			<h4> Turmas Virtuais Habilitadas   </h4>
				<table>
					<thead>
					<tr>
						<th style="text-align:left">Disciplina</th>
						<th>Créditos</th>
						<th>Pólo</th>
					</tr>
					</thead>
					<tbody>
					<c:forEach items="#{portalTutor.turmasVirtuaisHabilitadas}" var="t" varStatus="status">
							<tr class="${status.index % 2 == 0 ? "odd" : "" }">
								<td class="descricao">
									<h:form>
										<input type="hidden" value="${t.id}" name="idTurma" />
										<h:commandLink id="turmasVirtuaisHabilitadas" action="#{turmaVirtual.entrar}" value="#{t.anoPeriodo} - #{t.disciplina.nome} - T#{t.codigo}"/>
									</h:form>
								</td>
								<td class="info"><center>${t.disciplina.crTotal}</center></td>
								<td class="info" width="35%"><center>${t.polo.descricao}</center></td>
							</tr>
					</c:forEach>
					</tbody>
				</table>		
			</div>
			</c:if>
			
			<c:if test="${usuario.tutor.presencial}">
				<div class="simple-panel">	
					<h4>Cursos Habilitados</h4>
				    <h:form>			
						<h:selectOneMenu value="#{portalTutor.idPoloCursoEscolhido}" valueChangeListener="#{portalTutor.carregarAlunos}" id="poloCurso">
							<f:selectItem itemValue="0" itemLabel="-- TODOS OS CURSOS --" />
							<f:selectItems value="#{portalTutor.poloCursosCombo}" />
							<a4j:support event="onchange" onsubmit="true" reRender="alunosOrientados"/>
						</h:selectOneMenu>
				    </h:form>								
					
				</div>
				</br>
				<h:form>
				<div class="simple-panel" style="margin: 0">				
					<table>										
						<thead>
							<tr>
								<td colspan="7"><b>Alunos Orientados</b></td>
							</tr>
							<tr>
								<td colspan="7" style="text-align: right;">
								    <h:commandLink action="#{ portalTutor.enviarMensagemTodosAlunos }">
								    	<h:graphicImage value="/img/email_go.png" title="Enviar mensagem para Todos os Alunos Listados"/>
								    	Enviar mensagem para todos os Alunos Listados
								    </h:commandLink>
								</td>
							</tr>
						</thead>
					</table>
				</div>	
				<t:dataTable id="alunosOrientados"  value="#{portalTutor.alunosOrientados}" var="aluno" rowClasses="linhaPar,linhaImpar" style="width:100%;">
		
					<t:column >			
						<f:facet name="header">
							<f:verbatim><p align="left"><h:outputText value="Matrícula" /></p></f:verbatim>
						</f:facet>
						<h:outputText value="#{ aluno.matricula}"/>
					</t:column>
					<t:column>
						<f:facet name="header">
							<f:verbatim><p align="left"><h:outputText value="Nome" /></p></f:verbatim>
						</f:facet>
						<h:outputText value="#{ aluno.pessoa.nome}" escape="false" />
					</t:column>
					<t:column>
						<a4j:outputPanel rendered="#{ usuario.tutor.acessoCompleto }">
							<h:commandLink action="#{ fichaAvaliacaoEad.iniciar }" title="Avaliar o aluno" >
								<f:param name="discente" value="#{aluno.id}" />
								<f:param name="poloCurso" value="#{portalTutor.idPoloCursoEscolhido}" />
								<f:param name="avaliar" value="true" />
								<h:graphicImage url="/img/icones/page_white_edit.png"/>
							</h:commandLink>
						</a4j:outputPanel>	
					</t:column>
					<t:column>			
							<h:commandLink action="#{ fichaAvaliacaoEad.historico }" title="Histórico do aluno">
								<f:param name="idDiscente" value="#{aluno.id}" />
								<h:graphicImage url="/img/report.png"/>
							</h:commandLink>														
					</t:column>
					<t:column>
						<a4j:outputPanel rendered="#{ usuario.tutor.acessoCompleto }">
							<h:commandLink action="#{ fichaAvaliacaoEad.fichaDiscente }" title="Visualizar Avaliações do Aluno">
								<f:param name="discente" value="#{aluno.id}" />
								<h:graphicImage url="/img/buscar.gif"/>
							</h:commandLink>						
						</a4j:outputPanel>	
					</t:column>
					<t:column>
						<h:commandLink action="#{ relatorioNotasAluno.gerarRelatorio }" title="Notas do discente">
							<f:param name="discente" value="#{aluno.id}" />
							<h:graphicImage url="/img/view.gif"/>
						</h:commandLink>
					</t:column>
					<t:column>
						<h:commandLink action="#{ portalTutor.enviarMensagemAluno }" title="Enviar mensagem para o Aluno" >
							<f:param name="discente" value="#{aluno.id}" />
							<h:graphicImage url="/img/email_go.png"/>
						</h:commandLink>
					</t:column>
				</t:dataTable>
				</h:form>
				</c:if>
			</div>
		<div class="clear"> </div>
	</div>
</f:view>
<script type="text/javascript" src="/shared/loadScript?src=javascript/paineis/noticias.js"> </script>
<script type="text/javascript">
	PainelNoticias.init('/sigaa/portais/docente/viewNoticia.jsf');
</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>