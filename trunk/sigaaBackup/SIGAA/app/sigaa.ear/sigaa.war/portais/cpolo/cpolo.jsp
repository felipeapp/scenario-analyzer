<%@include file="/WEB-INF/jsp/include/cabecalho.jsp" %>
<link rel="stylesheet" media="all" href="/sigaa/css/portal_docente.css" type="text/css" />

<f:view>
	
	<div id="portal-docente">
	
		<f:subview id="menu">
			<%@include file="/portais/cpolo/menu_cpolo.jsp" %>
		</f:subview>
		
			<h:outputText value="#{portalCoordPolo.create}"/>

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
			<li><a	href="<%="/sigaa/abrirCaixaPostal.jsf?sistema="+String.valueOf(Sistema.SIGAA)%>" > Minhas Mensagens </a></li>
			<li><a class="perfil" href="perfil.jsf"> Trocar Foto </a></li>
			<li><a class="perfil" href="perfil.jsf"> <h:commandLink action="#{ dadosPessoaisCoordenador.iniciarAlteracaoDadosCoordenadorPolo}" value="Atualizar Dados Pessoais" /> </a></li>
		</ul>
	</h:form>
	
	<div class="clear"></div>
	</div>

	<p class="info-docente"><span class="nome"> 
		<b>
			<small>${usuario.pessoa.nome}</small>
		</b>
		
	<hr/>
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
				<tr>
					<td>Pólo:</td>
					<td>${portalCoordPolo.coordPolo.polo.cidade.nomeUF }</td>
				</tr>
			</table>
	
	<center><B>Ajuda</B></center>
	<hr>
	<img src="${ctx}/img/view.gif"> Notas do discente <br>
	<img src="${ctx}/img/buscar.gif"> Visualizar Avaliações do Aluno<br>
	<img src="${ctx}/img/report.png"> Histórico do aluno<br>

	</div>

	<p style="margin: 4px"><c:if test="${portalCoordPolo.perfil != null}">
		<br>
		<ufrn:format name="portalCoordPolo" property="perfil.descricao"
			type="texto" />
	</c:if></p>
	<br>
	</p>

	<%-- PRÓXIMOS COMPROMISSOS --%></div>

	<div id="main-docente">
				
			<h:outputText value="#{noticiaPortal.create}" />
			
			<%-- NOTÍCIAS --%>
			<script type="text/javascript">
			var fcontent=new Array()
			<c:forEach var="noticia" items="${noticiaPortal.noticiasTutor}" varStatus="loop">
			fcontent[${ loop.index }]="<p class=\"noticia destaque\"><a href=\"#\" id=\"noticia_${ noticia.id }\">${ noticia.titulo }</a></p><p class=\"descricao\"><a href=\"#\" style=\"font-weight: normal; text-decoration: none\" id=\"noticia_${ noticia.id }\">${  sf:escapeHtml(noticia.descricaoResumida) }</a></p>";
			</c:forEach>
			</script>
			<div id="noticias-portal">
				<c:if test="${ not empty noticiaPortal.noticiasTutor }">
				<script type="text/javascript" src="/shared/javascript/scroller-portal.js"></script>
				</c:if>
				<c:if test="${ empty noticiaPortal.noticiasTutor }">
				<p class="noticia destaque"> <br/>Não há notícias cadastradas.</p>
				</c:if>
			</div>
			
			<c:if test="${ portalCoordPolo.avaliacoesAbertas != null}">
			<!--  <h4 style="color: #333; font-size: 8pt; font-variant: small-caps; margin: 2.2em 0 0 2.2em;"> Avaliações Abertas </h4> -->
			
			<div class="simple-panel">
				<b>Avaliações Abertas</b>
				<table>
					<thead>
					<tr><th>${ portalCoordPolo.administracao ? 'Componentes Curriculares' : 'Semanas' }</th></tr>
					</thead>
					
					<h:form>
					<tr><td align="center">
					
					<c:forEach var="semanaV" items="#{ portalCoordPolo.avaliacoesAbertas }" varStatus="status">		
						
						<c:if test="${ !portalCoordPolo.administracao }">
							<h:commandLink action="#{ fichaAvaliacaoEad.iniciar }" value="#{semanaV.descricao}">         						
								<f:param name="semana" value="#{semanaV.semana}"/>    
								<f:param name="avaliar" value="true"/>    													
							</h:commandLink>							
							&nbsp;&nbsp;
						</c:if>
						
						<c:if test="${ portalCoordPolo.administracao }">
							<h:commandLink action="#{ fichaAvaliacaoEad.iniciar }" value="#{semanaV.descricao}">         						
								<f:param name="componente" value="#{semanaV.semana}"/>    
								<f:param name="avaliar" value="true"/>    													
							</h:commandLink>							
							<br/>
						</c:if>	
								
					</c:forEach>		
								
					</td></tr>
					</h:form>
					
				</table>
			</div>
			</c:if>

			<!--  <h4 style="color: #333; font-size: 8pt; font-variant: small-caps; margin: 2.2em 0 0 2.2em;"> Alunos Orientados </h4>  -->
			
			<!-- <c:set var="alunos" value="${portalCoordPolo.alunosOrientados}"/> -->
			
			<div class="simple-panel">
			
				<h:form>
				
					<th><b>Curso:</b></th>
					
						<a4j:region>
							<h:selectOneMenu id="tipo" valueChangeListener="#{portalCoordPolo.changeCurso}">
								<f:selectItems value="#{portalCoordPolo.allCursosCombo}" />
								<a4j:support event="onchange" reRender="out"/>
							</h:selectOneMenu>
							<a4j:status>
				                <f:facet name="start"><h:graphicImage  value="/img/indicator.gif"/></f:facet>
				            </a4j:status>
					</a4j:region>
					
					<input type="hidden" id="idTu" value="${portalCoordPolo.curso.id}" />
								
				</h:form>
				
				<br>
				
				<td>									

							<h:panelGroup id="out">
							
							<table>
									<b>Alunos Encontrados</b><br><br>
									<thead>
									<tr>
										<th width="15%">Matricula</th>
										<th width="85%">Nome</th>
									</tr>
									</thead>
																			
									<tbody>
		
										<c:forEach items="${portalCoordPolo.alunosOrientados}" var="aluno" varStatus="status">
										<tr class="${status.index % 2 == 0 ? "odd" : "" }">
											<td>
												${ aluno.matricula }
											</td>
											<td>
												${ aluno.pessoa.nome }
											</td>
											<td>
												
											</td>
											<td>
												<h:form>
													<input type="hidden" name="idDiscente" value="${ aluno.id }"/>
													<h:commandButton image="/img/report.png" action="#{ fichaAvaliacaoEad.historico }" title="Histórico do aluno" value="Histórico Aluno"/>								
												</h:form>
											</td>
											<td>
												<h:form>
													<input type="hidden" name="discente" value="${ aluno.id }"/>
													<h:commandButton image="/img/buscar.gif" action="#{ portalCoordPolo.fichaAvaliacao }" title="Visualizar Avaliações do Aluno" value="Ficha"/>
												</h:form>
											</td>
											<td>
												<h:form>
													<input type="hidden" name="discente" value="${ aluno.id }"/>	
													<h:commandButton image="/img/view.gif" action="#{ relatorioNotasAluno.gerarRelatorio }" title="Notas do discente" alt="Ver Notas"/>
												</h:form>
											</td>
									
										</tr>
										</c:forEach>
									
													</tbody>
					</table>			
							</h:panelGroup>

				</td>
			</div>
			

		</div>
	</div>

</f:view>
<script type="text/javascript" src="/shared/loadScript?src=javascript/paineis/noticias.js"> </script>
<script type="text/javascript">
	PainelNoticias.init('/sigaa/portais/docente/viewNoticia.jsf');
</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>