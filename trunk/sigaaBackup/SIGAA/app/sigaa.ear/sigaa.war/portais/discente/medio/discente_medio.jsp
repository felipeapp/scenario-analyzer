<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@taglib uri="/tags/primefaces-p" prefix="p"%>
<jwr:style src="/css/portal_docente.css" media="all"/>
<jwr:style src="/css/agenda.css" media="all" />
<script type="text/javascript">
	JAWR.loader.script('/javascript/paineis/noticias.js');
    JAWR.loader.script('/bundles/js/jquery.js');
</script>
<script type="text/javascript" src="/shared/javascript/jquery/jquery-1.4.4.min.js"></script>
<script src="/sigaa/javascript/rotator.js" type="text/javascript" ></script>


<style>
#cboxLoadedContent iframe {
	background-color:#FFFFFF;
}
</style>

<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>

<%@page import="br.ufrn.comum.dominio.LocalizacaoNoticiaPortal"%>

<f:view>
	<p:resources />
	<div id="portal-docente">
		<f:subview id="menu">
			<c:choose>
			<c:when test="${acesso.acessibilidade}">
				<%-- Menu Deficiente Visual --%>
				<%@include file="menu.jsp" %>
			</c:when>
			<c:otherwise>
				<%@include file="menu_discente_medio.jsp" %>
			</c:otherwise>
			</c:choose>
			
		</f:subview>
		<h:outputText value="#{portalDiscenteMedio.create}"/>
		<div id="perfil-docente">
			<%@include file="perfil_discente_medio.jsp" %>
		</div>
		
		<div id="main-docente">

			<%-- NOTÍCIAS --%>
			<h:outputText value="#{noticiaPortal.create}" />
			<c:set var="noticias" value="${ noticiaPortal.noticiasDiscente }"/>
			<script type="text/javascript">
				var fcontent=new Array()
				<c:forEach var="noticia" items="${ noticias }" varStatus="loop">
				fcontent[${ loop.index }]="<p class=\"noticia destaque\"><a href=\"#\" id=\"noticia_${ noticia.id }\">${ sf:escapeHtml(noticia.titulo) }</a></p><p class=\"descricao\"><a href=\"#\" style=\"font-weight: normal; text-decoration: none\" id=\"noticia_${ noticia.id }\">${  sf:escapeHtml(noticia.descricaoResumida) }</a></p>";
				</c:forEach>
			</script>
			<div id="noticias-portal">
				<c:if test="${ not empty noticias }">
					<script>
						<%-- essas variaveis sao passadas para o scroller-portal.js --%>				
						var portal = "<%= LocalizacaoNoticiaPortal.getPortal(LocalizacaoNoticiaPortal.PORTAL_DISCENTE).getMd5() %>";
						var sistema = "${ request.contextPath }";
					</script>
					
					<script type="text/javascript" src="/shared/javascript/scroller-portal.js"></script>
				</c:if>
				<c:if test="${ empty noticias }">
					<p class="noticia destaque"> <br/>Não há notícias cadastradas.</p>
				</c:if>
			</div>
			<%-- /NOTÍCIAS --%>
			
			<%-- LISTA DE TURMAS NO SEMESTRE   --%>
			<div id="turmas-portal" class="simple-panel">
				<h4> Disciplinas de Turmas Matriculadas </h4>
				<c:if test="${ portalDiscenteMedio.modoReduzido }">
					<p class="vazio"> 
						Listagem de turmas no portal temporariamente indisponível. Para acessá-las, 
						<a href="${ctx}/portais/discente/turmas.jsf">clique aqui</a>.
					</p>
				</c:if>
				<c:if test="${ not portalDiscenteMedio.modoReduzido }">
					<c:set var="turmasAbertas" value="#{ portalDiscenteMedio.disciplinasAbertas }"/>
					<c:if test="${empty turmasAbertas}">
						<c:if test="${ portalDiscenteMedio.passivelEmissaoAtestadoMatricula }">
							<p class="vazio"> Nenhuma turma neste semestre </p>
						</c:if>
						<c:if test="${ not portalDiscenteMedio.passivelEmissaoAtestadoMatricula }">
							<p class="vazio">Não é possível visualizar as turmas. <br/>Processamento de matrícula em andamento.</p>
						</c:if>
					</c:if>
	
					<c:if test="${!empty turmasAbertas}">					
					<table class="subFormulario" style="font-size: x-small;">
						<thead>
							<tr>
								<th style="background: #DEDFE3; width: 90%;">Últimas Atualizações</th>
								<th style="background: #DEDFE3;" nowrap="nowrap">
									<a class="rotator-prev" href="#"><<</a> 
									<a class="rotator-pause" href="#">Parar</a> 
									<a class="rotator-continue" href="#">Continuar</a> 
									<a class="rotator-next" href="#">>></a>
								</th>
							</tr>
						</thead>
							<tr>
								<td>
									<h:form>
										<div id="atualizacoes-turma" style="position: relative; padding-bottom: 7%; display: block;">
											<c:if test="${portalDiscenteMedio.modoReduzido }">
												<h:form>
												<p class="vazio">
													Últimas Atualizações das Turmas Virtuais indisponíveis no momento.
												</p>
												</h:form>
											</c:if>
											
											<c:if test="${!portalDiscenteMedio.modoReduzido && not empty portalDiscenteMedio.ultimasAtividadesTurmas }">
											<div class="rotator">
												<c:forEach items="#{portalDiscenteMedio.ultimasAtividadesTurmas }" var="at">
													<table>
														<tr>
															<td>
																<ufrn:format valor="${at.data }" type="data" /> - 
																<h:commandLink value="#{at.turma.disciplina.nome }" action="#{turmaVirtual.entrar }" >
																	<f:param name="idTurma" value="#{at.turma.id}" />
																</h:commandLink>							
															</td>
														</tr>
														<tr>
															<td><ufrn:format type="texto" valor="${at.descricao }" length="140"></ufrn:format></td>
														</tr>
													</table>
												</c:forEach>
											</div>
											</c:if>
										</div>
									</h:form>
								</td>
							</tr>
					</table>
					<table>
						<thead>
						<tr>
							<th width="30%" style="text-align:left">Disciplina</th>
							<th width="10%" style="text-align:left">Série</th>
							<th width="10%" style="text-align:center">Turma</th>
							<th width="20%" style="text-align:left">Local</th>
							<th width="10%">Horário</th>
							<th></th>
							<th>Chat</th>
						</tr>
						</thead>
						<tbody>
						<c:forEach items="#{turmasAbertas}" var="t" varStatus="status">
							<tr class="${status.index % 2 == 0 ? "odd" : "" }">
								<td class="descricao">
									<h:form id="form_acessarTurmaVirtual">
										<input type="hidden" value="${t.turma.id}" name="idTurma" />
										<h:commandLink id="turmaVirtual" action="#{turmaVirtual.entrar}" value="#{t.turma.disciplina.nome}"/>
										${t.descricaoDependencia}
									</h:form>
								</td>
								<td class="info" style="text-align:left">${t.turmaSerie.serie.descricaoCompleta}</td>
								<td class="info" style="text-align:center">${t.turmaSerie.nome}</td>
								<td class="info" style="text-align:left">${t.turma.local}</td>
								<td class="info"><center>${t.turma.descricaoHorarioSemanaAtual}
									<c:if test="${t.turma.disciplina.permiteHorarioFlexivel}">  *  </c:if>
									</center>
								</td>
								<td>
									<c:if test="${t.turma.disciplina.permiteHorarioFlexivel}">
										<c:set var="possuiTurmaHorarioFlexivel" value="true" />
										<h:form id="formTurma">
											<p:lightBox iframe="true" width="700" height="480" opacity="0.25" speed="150">  
											    <h:outputLink value="/sigaa/ensino/turma/view_calendario.jsf?idTurma=#{t.turma.id}">  
											        <h:graphicImage value="/img/view_calendario.png" title="Ver Horário Completo da Turma" />  
											    </h:outputLink>  
											 </p:lightBox>  
								        </h:form>
									</c:if>
								</td>
								<td nowrap="nowrap">
									<h:form id="form_docente">
									<c:if test="${ t.turma.docenteOnline }">
										<c:if test="${ t.turma.docenteNoChat }">
											<img src="/sigaa/img/flag_green.png" alt="Docente(s) no chat" title="Docente(s) no chat"/>
										</c:if>
										<c:if test="${ !t.turma.docenteNoChat }">
											<img src="/sigaa/img/flag_yellow.png" alt="Docente(s) online" title="Docente(s) online"/>
										</c:if>
									</c:if>
									<c:if test="${ !t.turma.docenteOnline }">
										<img src="/sigaa/img/flag_red.png" alt="Docente(s) offline" title="Docente(s) offline"/>
									</c:if>
									<h:commandLink id="acessarChatTurma" action="#{ turmaVirtual.createChatParam }" onclick="window.open('/sigaa/entrarChat.do?idchat=#{ t.turma.id }&idusuario=#{ portalDiscenteMedio.usuarioLogado.id }&chatName=#{ t.turma.disciplina.nome }&origem=portalDiscente', 'chat_#{ t.turma.id }', 'height=485,width=685,location=0,resizable=1');">
										<f:param name="id" value="#{ t.turma.id }"/>
										<h:graphicImage value="/img/comments.png" alt="Acessar Chat da turma"  title="Acessar Chat da turma - #{t.turma.usersOnlineChat} usuário(s)"/>
									</h:commandLink>
									</h:form>
								</td>
							</tr>
							<tr>
								<td colspan="5" id="linha_${t.turma.id}" style="display: none;"></td>
							</tr>
						</c:forEach>
						</tbody>
					</table>
					<span class="mais" style="text-align: left;">
					<c:if test="${possuiTurmaHorarioFlexivel}" >
						* A turma possui horário flexível e o horário exibido é da semana atual.
					</c:if>
					</span>
					</c:if>

				<c:if test="${portalDiscenteMedio.passivelEmissaoAtestadoMatricula}">
				<span class="mais">
					<a href="${ctx}/portais/discente/medio/turmas.jsf">Ver disciplinas anteriores</a>
				</span>
				</c:if>
				</c:if>
			</div>

			<c:set var="turmasVirtuais" value="${ portalDiscenteMedio.turmasVirtuaisHabilitadas }"/>
			<c:if test="${!empty turmasVirtuais}">
				<div id="turmas-habilitadas" class="simple-panel">
				<h4> Turmas Virtuais Habilitadas   </h4>
				<c:if test="${ portalDiscenteMedio.modoReduzido }">
					<p class="vazio">
						Listagem de turmas virtuais habilitadas temporariamente indisponível. Para acessá-las, <a href="${ctx}/portais/discente/turmas_habilitadas.jsf">clique aqui</a>.
					</p>
				</c:if>
				<c:if test="${ not portalDiscenteMedio.modoReduzido }">
	
					<table>
						<thead>
						<tr>
							<th>Disciplina</th>
							<th>Créditos</th>
							<th>Horário</th>
						</tr>
						</thead>
						<tbody>
						<c:forEach items="#{portalDiscenteMedio.turmasVirtuaisHabilitadas}" var="t" varStatus="status">
								<tr class="${status.index % 2 == 0 ? "odd" : "" }">
									<td class="descricao">
										<h:form>
											<input type="hidden" value="${t.id}" name="idTurma" />
											<h:commandLink id="turmasVirtuaisHabilitadas" action="#{turmaVirtual.entrar}" value="#{t.ano} - #{t.disciplina.nome} - T#{t.codigo}"/>
										</h:form>
									</td>
									<td class="info"><center>${t.disciplina.crTotal}</center></td>
									<td class="info" width="35%"><center>${t.descricaoHorario}</center></td>
								</tr>
						</c:forEach>
						</tbody>
					</table>
	
				</c:if>
				</div>
			</c:if>
			<%-- /LISTA DE TURMAS NO SEMESTRE   --%>
			
			<%-- COMUNIDADES VIRTUAIS --%>
			<div id="participantes" class="simple-panel">
				<h4> Comunidades Virtuais que participa atualmente </h4>
				<c:if test="${ portalDiscenteMedio.modoReduzido }">
				<h:form>
				<p class="vazio">
					Listagem de comunidades virtuais temporariamente indisponível. Para acessá-las, <h:commandLink id="verTodasComunidades" action="#{buscarComunidadeVirtualMBean.exibirTodasComunidadesDocente}" value="clique aqui"/>.
				</p>
				</h:form>
				</c:if>
				<c:if test="${ not portalDiscenteMedio.modoReduzido }">
				<c:set var="_comunidades" value="#{ buscarComunidadeVirtualMBean.comunidadesPorPessoa }"/>
				<h:form rendered="#{not empty _comunidades}">
					<table>
	
						<thead>
						<tr>
							<th>Nome</th>
						</tr>
						</thead>
						<tbody>
						<c:forEach items="#{_comunidades}" var="c" varStatus="status">
								<tr class="${status.index % 2 == 0 ? "odd" : "" }">
									<td class="descricao">
										<c:if test="${c.ativa == true}">
											<h:commandLink id="entrarComunidadeVirtual" action="#{portalDiscenteMedio.entrarComunidadeVirtual}" value="#{c.nome}">
												<f:param value="#{c.id}" name="idComunidade" />
											</h:commandLink>
										</c:if>
									</td>
								</tr>
						</c:forEach>
						</tbody>
					</table>
				<table>
					<center> <h:commandLink id="verTodasComunidades" action="#{buscarComunidadeVirtualMBean.exibirTodasComunidadesDocente}" value="Ver todas as Comunidades..."/> </center>
				</table>
				</h:form>
				</c:if>
			</div>
			<%-- /COMUNIDADES VIRTUAIS --%>
			
			<%-- EXIBE OS TOPICOS DO FORUM --%> 
			<div id="forum-portal" class="simple-panel">
			<%@include file="forum_turma_medio.jsp" %>
			</div>
		</div>
		<div class="clear"> </div>
	</div>
	
</f:view>

<style>
	#avaliacao-portal th, #avaliacao-portal td{
		text-align:left;
	}
</style>

<script type="text/javascript">
	PainelNoticias.init('/sigaa/portais/docente/viewNoticia.jsf');
</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
