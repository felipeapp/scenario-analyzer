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
		<%-- Verifica se possui notificacoes pendentes antes de entrar no portal - evita que o discente burle --%>
		${notificacaoAcademicaDiscente.verificaNotificacoes}
		<f:subview id="menu">
			<c:choose>
			<c:when test="${acesso.acessibilidade}">
				<%-- Menu Deficiente Visual --%>
				<%@include file="menu.jsp" %>
			</c:when>
			<c:otherwise>
				<%@include file="/portais/discente/menu_discente.jsp" %>
			</c:otherwise>
			</c:choose>
			
		</f:subview>
		<h:outputText value="#{portalDiscente.create}"/>
		<div id="perfil-docente">
			<div class="pessoal-docente">
				<div class="foto">
					<c:if test="${usuario.idFoto != null}">
						<img src="${ctx}/verFoto?idArquivo=${usuario.idFoto}&key=${ sf:generateArquivoKey(usuario.idFoto) }" style="width: 100px; height: 125px"/>
					</c:if>
					<c:if test="${usuario.idFoto == null}">
						<img src="${ctx}/img/no_picture.png" width="128" height="125"/>
					</c:if>
				</div>
				<ul>
					<li>
						<a	href="<%="/sigaa/abrirCaixaPostal.jsf?sistema="+String.valueOf(Sistema.SIGAA)%>" >
							Mensagens
						</a>
					</li>
					<li>
						<a class="perfil" href="perfil.jsf">
							Atualizar Foto e Perfil
						</a>
					</li>
					<li>
						<h:form>
							<h:commandLink id="meusDadosPessoais" action="#{ alteracaoDadosDiscente.iniciarAcessoDiscente}" value="Meus Dados Pessoais"/>
						</h:form>
					</li>
					<h:panelGroup rendered="#{portalDiscente.redeUfrnAtiva}">
					<li>
						<a href="${ctx}/entrarSistema.do?sistema=alumini&url=/redesocial/associar_perfil/associar.jsf" >Cadastre-se no Alumini</a>
					</li>
					</h:panelGroup>
				</ul>
				<div class="clear"> </div>
			</div>
			<p class="info-docente">
			
				<span class="nome"> <small><b>${usuario.pessoa.nome}</b></small> </span>

				<c:if test="${portalDiscente.perfil != null}">
					<ufrn:format name="portalDiscente" property="perfil.descricao" type="texto"/>
				</c:if>
			</p>
			<h:form id="form_links">
			<div id="agenda-docente" style="text-align: center">
				<h:commandButton id="forumCursos" image="/img/portal-de-cursos.png" action="#{ forum.listarForunsCurso }" rendered="#{ not portalDiscente.modoReduzido }"/> &nbsp;
				<h:commandButton id="avaliacaoInstitucional" image="/img/avaliacao.jpg" action="#{ calendarioAvaliacaoInstitucionalBean.listar }" rendered="#{ acesso.usuario.discenteAtivo.nivelStr == 'G' && empty acesso.usuario.discenteAtivo.polo }"/>
				<a href="http://www.ufrn.br/capes/" title="Instruções de Acesso ao Portal CAPES fora da UFRN" target="_blank">
					<img src="/sigaa/img/capes.jpg">
				</a>
				<h:commandLink id="buscarComunidadeVirtual" actionListener="#{ buscarComunidadeVirtualMBean.criar }" rendered="#{ not portalDiscente.modoReduzido }" title="Buscar Comunidades Virtuais">
					<h:graphicImage value="/img/cv.gif" width="90" height="50" alt="Comunidade Virtual" style="border:1px solid #CCC; margin-left:9px;"></h:graphicImage>
				</h:commandLink> 
			</div>
			</h:form>
			<c:if test="${usuario.discenteAtivo.graduacao}">
				<p style="text-align: center; font-size: x-small">
				<a href="${linkPublico.urlDownloadPublico}/regulamento_dos_cursos_de_graduacao.pdf" target="_blank">
				Regulamento dos Cursos de Graduação
				</a>
				</p>
			</c:if>
			<br>
			
			<%-- CALENDARIO ACADEMICO GRADUACAO --%>
			<c:if test="${usuario.discenteAtivo.graduacao}">
				<%-- EAD --%>
				<c:if test="${usuario.discenteAtivo.discenteEad}">
					<p style="text-align: center; font-size: x-small">
					<a href="${linkPublico.urlDownloadPublico}/743.pdf" target="_blank">
					Calendário Acadêmico de Graduação EAD Bacharelado
					</a>
					</p>
					<p style="text-align: center; font-size: x-small">
					<a href="${linkPublico.urlDownloadPublico}/764.pdf" target="_blank">
					Calendário Acadêmico de Graduação EAD Licenciatura
					</a>
					</p>				
				</c:if>			
			
				<%-- REGULAR --%>
				<c:if test="${not usuario.discenteAtivo.discenteEad}">
					<p style="text-align: center; font-size: x-small">
					<a href="${linkPublico.urlDownloadPublico}/calendario_universitario.pdf" target="_blank">
					Calendário Acadêmico de Graduação
					</a>
					</p>				
				</c:if>
			</c:if>

			<%-- PRÓXIMOS COMPROMISSOS --%>
			<div id="agenda-docente">
				<h4> Dados Institucionais </h4>
				<table>
					<tr>
						<td> Matrícula: </td>
						<td> ${usuario.discenteAtivo.matricula} </td>
					</tr>
					<c:if test="${usuario.discenteAtivo.graduacao}">
					<tr>
						<td valign="top"> Curso: </td>
						<td> ${usuario.discenteAtivo.curso.descricao} -
						${usuario.discenteAtivo.matrizCurricular.turno.sigla} </td>
					</tr>
					</c:if>
					<c:if test="${!usuario.discenteAtivo.graduacao}">
					<tr>
						<td valign="top"> Curso: </td>
						<td> ${usuario.discenteAtivo.curso.descricao}</td>
					</tr>
					</c:if>
					<tr>
						<td> Nível: </td>
						<td> ${usuario.discenteAtivo.nivelDesc} </td>
					</tr>
					<tr>
						<td> Status: </td>
						<td> ${usuario.discenteAtivo.statusString} </td>
					</tr>
					<tr>
						<td> E-Mail: </td>
						<td>
							<ufrn:format type="texto" name="usuario" property="email" length="18" />
						</td>
					</tr>
					<tr>
						<td> Entrada: </td>
						<td> ${usuario.discenteAtivo.anoIngresso}.${usuario.discenteAtivo.periodoIngresso}</td>
					</tr>
					<c:if test="${usuario.discenteAtivo.graduacao and usuario.discenteAtivo.ira > 0.0}">
					<tr>
						<td> Ingresso: </td>
						<td> ${usuario.discenteAtivo.formaIngresso.descricao}</td>
					</tr>
					</c:if>
					
					<c:if test="${usuario.discenteAtivo.curso.ADistancia and usuario.discenteAtivo.graduacao}">
							<tr>
								<td> Pólo: </td>
								<td> ${usuario.discenteAtivo.polo.descricao } </td>
							</tr>
							<tr>
								<td> Tutores: </td>
								<td>
									<c:if test="${not empty usuario.discenteAtivo.tutores}">
										<table>
										<c:forEach items="#{ usuario.discenteAtivo.tutores }" var="tutor">
											<tr><td>${tutor.pessoa.nome}</td></tr>
										</c:forEach>	
										</table>
									</c:if>	
								</td>	
							</tr>
					</c:if>
					<c:if test="${usuario.discenteAtivo.graduacao and  not empty usuario.discenteAtivo.orientacaoAcademica}">
						<tr>
							<td> Orientador Acadêmico: </td>
							<td> ${usuario.discenteAtivo.orientacaoAcademica.servidor.nome} </td>
						</tr>
					</c:if>
					
					<c:if test="${usuario.discenteAtivo.stricto}">
						${portalDiscente.carregaDadosAlunoPos }
							<tr>
								<td> Orientador: </td>
								<td>
									 <c:choose>
									 	<c:when test="${not empty usuario.discenteAtivo.orientacao}">
											${usuario.discenteAtivo.orientacao.nomeOrientador} 
									 	</c:when>
										<c:otherwise>
											<i>Não definido</i>
										</c:otherwise>
									 </c:choose>
								</td>
							</tr>							
							<tr>
								<td> Área: </td>
								<td> ${usuario.discenteAtivo.area.denominacao} </td>
							</tr>
							<tr>
								<td> Linha de Pesquisa: </td>
								<td> 
									<c:if test="${usuario.discenteAtivo.linha != null }">
										${usuario.discenteAtivo.linha.denominacao}
									</c:if>
									<c:if test="${usuario.discenteAtivo.linha == null }">
										Não Informada pelo Programa
									</c:if>
								 </td>
							</tr>
							<tr>
								<td> Mês Atual: </td>
								<td> ${usuario.discenteAtivo.mesAtual} </td>
							</tr>
							<tr>
								<td> CR cursados: </td>
								<td> ${usuario.discenteAtivo.crTotaisIntegralizados} </td>
							</tr>
					</c:if>
					<%--  Índices Acadêmicos --%>
					<c:if test="${not portalDiscente.modoReduzido and not empty portalDiscente.indicesAcademicoDicente}">
						<tr>
							<td colspan="2">&nbsp;</td>
						</tr>
						<tr>
							<td colspan="2" style="text-align: center; font-style: italic">Índices Acadêmicos</td>
						</tr>
						<tr>
							<td colspan="2">
								<table width="100%">
									<tr>
										<c:forEach var="item" items="${portalDiscente.indicesAcademicoDicente}" varStatus="indice">
											<c:if test="${indice.index % 2 == 0}">
												</tr>
												<tr>
											</c:if>
											<td><acronym title="${item.indice.nome}">${item.indice.sigla}:</acronym></td>
											<td align="right"><div style="margin-right:15px;">${item.valor}</div></td>
										</c:forEach>
									</tr>
									<tr>	
										<td colspan="4" align="center"><h:form><h:commandLink id="detalharIndiceAcademico" style="font-size:9px; margin-right:12px;" action="#{indiceAcademicoMBean.selecionaDiscente}" rendered="#{portalDiscente.passivelEmissaoRelatorioIndices}" value="Detalhar"/></h:form></td>
									</tr>
								</table>
							</td>
						</tr>
						
					</c:if>
					
					<tr>
						<td colspan="2">
							<%--  O JSTL não deixa comparar com o char nivel, por essa maneira "estranha" --%>
							<c:if test="${usuario.discenteAtivo.graduacao}">
								<br>
								<i><center>
								Integralizações:</center></i>
								<br>
								<table>
									<tr>
										<td> CH. Obrigatória Pendente </td>
										<td align="right"> ${usuario.discenteAtivo.chNaoAtividadeObrigPendente + usuario.discenteAtivo.chAtividadeObrigPendente} </td>
									</tr>
									<tr>
										<td> CH. Optativa Pendente </td>
										<td align="right"> ${usuario.discenteAtivo.chOptativaPendente} </td>
									</tr>
									<tr>
										<td> CH. Total Currículo </td>
										<td align="right"> ${usuario.discenteAtivo.curriculo.chTotalMinima} </td>
									</tr>
									<c:set var="totalIntegralizado" value="${100 * ( 1 - usuario.discenteAtivo.chTotalPendente / usuario.discenteAtivo.curriculo.chTotalMinima) }"/>
									<tr>
										<td colspan="2" style="border-width: 1px; border-color: black; border-style: thin; background: white;">
											<div style="border: thin; border-color: black; width: ${totalIntegralizado}%; background: silver; text-align: center;" >
												&nbsp;
											</div>
										</td>
									</tr>
									<tr>
										<td colspan="2" align="center">
													<fmt:formatNumber pattern="00" value="${totalIntegralizado}"/>% Integralizado
										</td>
									</tr>

								</table>

							</c:if>
						</td>
					</tr>
				</table>


			</div>
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

	<%-- ELEIÇÕES PARA DIRETOR DE CENTRO --%>
		
			<h:outputText value="#{eleicao.create}" />
			<c:set var="eleicoes" value="${eleicao.eleicoesAbertas}"/>
			<c:if test="${!empty eleicoes}">

			<c:if test="${empty eleicoes}">
					<p class="vazio"> Nenhuma eleição aberta neste momento </p>
			</c:if>

			<div id="eleicoes-portal" class="simple-panel">
				<h4> Eleições </h4>

				<table>


					<tr><td>
						<h:form id="formEleicoes">
						<h:dataTable var="item" value="#{eleicao.eleicoesAbertas}" id="eleicoes" rowClasses="odd,">

									<t:column styleClass="centerAlign">
										<f:facet name="header"><f:verbatim>Eleição</f:verbatim></f:facet>
										<h:commandLink id="tituloEleicao" action="#{voto.popularVotacao}" value="#{item.titulo}">
											<f:param name="idEleicao" value="#{item.id}"/>
										</h:commandLink>
									</t:column>

									<t:column styleClass="centerAlign">
										<f:facet name="header"><f:verbatim>Data de Finalização</f:verbatim></f:facet>
										<h:outputText value="#{item.dataFim}">
											<f:convertDateTime pattern="dd/MM/yyyy HH:mm" />
										</h:outputText>
									</t:column>
						</h:dataTable>
						</h:form>
					</td></tr>
				</table>
			</div>
			</c:if>					
			
			<%-- LISTA DE TURMAS NO SEMESTRE   --%>
			<div id="turmas-portal" class="simple-panel">
				<h4> Turmas do Semestre   </h4>
				<c:if test="${ portalDiscente.modoReduzido }">
					<p class="vazio"> Listagem de turmas no portal temporariamente indisponível. Para acessá-las, <a href="${ctx}/portais/discente/turmas.jsf">clique aqui</a>.</p>
				</c:if>
				<c:if test="${ not portalDiscente.modoReduzido }">
					<c:set var="turmasAbertas" value="#{ portalDiscente.turmasAbertas }"/>
					<c:if test="${empty turmasAbertas}">
						<c:if test="${ portalDiscente.passivelEmissaoAtestadoMatricula }">
						<p class="vazio"> Nenhuma turma neste semestre </p>
						</c:if>
						<c:if test="${ not portalDiscente.passivelEmissaoAtestadoMatricula }">
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
								<h:form id="formAtualizacoesTurmas">
									<div id="atualizacoes-turma" style="position: relative; padding-bottom: 7%; display: block;">
										<c:if test="${not empty portalDiscente.ultimasAtividadesTurmas }">
											<div class="rotator">
												<c:forEach items="#{portalDiscente.ultimasAtividadesTurmas }" var="at">
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
										<c:if test="${empty portalDiscente.ultimasAtividadesTurmas }">
											<p class="vazio">
												Nenhuma atualização encontrada nas suas turmas.
											</p>
										</c:if>
									</div>
								</h:form>
							</td>
						</tr>
					</table>
					<table style="margin-top: 1%;">
						<thead>
						<tr>
							<c:if test="${usuario.discenteAtivo.infantil}"><th style="text-align:left" width="50%">Componente Curricular</th></c:if>
							<c:if test="${!usuario.discenteAtivo.infantil}"><th width="50%">Componente Curricular</th></c:if>
							<th style="text-align:left">Local</th>
							<th width="15%">Horário</th>
							<th></th>
							<th colspan="2" style="width:40px;">Chat</th>
						</tr>
						</thead>
						<tbody>
						<c:forEach items="#{turmasAbertas}" var="t" varStatus="status">
							<tr class="${status.index % 2 == 0 ? "odd" : "" }">
								<td class="descricao">
									<h:form id="form_acessarTurmaVirtual">
										<input type="hidden" value="${t.id}" name="idTurma" />
										<h:commandLink id="turmaVirtual" action="#{turmaVirtual.entrar}" value="#{t.disciplina.nome}"/>
									</h:form>
								</td>
								<td class="info" style="text-align:left">${t.local}</td>
								<td class="info"><center>${t.descricaoHorarioSemanaAtual}
									<c:if test="${t.disciplina.permiteHorarioFlexivel}">
										*
									</c:if>
									</center>
								</td>
								<td>
									<c:if test="${t.disciplina.permiteHorarioFlexivel}">
										<c:set var="possuiTurmaHorarioFlexivel" value="true" />
										<h:form id="formTurma">
											<p:lightBox iframe="true" width="700" height="480" opacity="0.25" speed="150">  
											    <h:outputLink value="/sigaa/ensino/turma/view_calendario.jsf?idTurma=#{t.id}">  
											        <h:graphicImage value="/img/view_calendario.png" title="Ver Horário Completo da Turma" />  
											    </h:outputLink>  
											 </p:lightBox>  
								        </h:form>
									</c:if>
								</td>
								<td nowrap="nowrap" style="width:50px;">
									<h:form id="form_docente">
										<h:graphicImage value="/img/flag_green.png" title="Chat em Andamento - #{t.usersOnlineChat} pessoa(s). Docente no chat" rendered="#{ t.docenteNoChat }" />
										<h:graphicImage value="/img/flag_yellow.png" title="Chat em Andamento - #{t.usersOnlineChat} pessoa(s). Docente #{ t.docenteOnline ? 'Online' : 'Offline' }" rendered="#{ !t.docenteNoChat && t.usersOnlineChat > 0 }" />
										<h:graphicImage value="/img/flag_grey.png" title="Nenhum usuário no chat. Docente #{ t.docenteOnline ? 'Online' : 'Offline' }" rendered="#{ t.usersOnlineChat == 0 }" />
										&nbsp;
										<h:commandLink id="acessarChatTurma" action="#{ turmaVirtual.createChatParam }" onclick="window.open('/sigaa/entrarChat.do?idchat=#{ t.id }&idusuario=#{ portalDiscente.usuarioLogado.id }&chatName=#{ t.disciplina.nome }&origem=portalDiscente', 'chat_#{ t.id }', 'height=485,width=685,location=0,resizable=1');">
											<f:param name="id" value="#{ t.id }"/>
											<h:graphicImage value="/img/comments.png" alt="Acessar Chat da turma"  title="Acessar Chat da turma - #{t.usersOnlineChat} usuário(s)"/>
										</h:commandLink>&nbsp;
									</h:form>
								</td>
								<td><h:outputText value="#{t.usersOnlineChat}" /></td>
							</tr>
							<tr>
								<td colspan="5" id="linha_${t.id}" style="display: none;"></td>
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

				<c:if test="${portalDiscente.passivelEmissaoAtestadoMatricula}">
				<span class="mais">
					<a href="${ctx}/portais/discente/turmas.jsf">Ver turmas anteriores</a>
				</span>
				</c:if>
				</c:if>
			</div>

			<c:set var="turmasVirtuais" value="${ portalDiscente.turmasVirtuaisHabilitadas }"/>
			<c:if test="${!empty turmasVirtuais}">
			<div id="turmas-habilitadas" class="simple-panel">
			<h4> Turmas Virtuais Habilitadas   </h4>
			<c:if test="${ portalDiscente.modoReduzido }">
				<p class="vazio">
					Listagem de turmas virtuais habilitadas temporariamente indisponível. Para acessá-las, <a href="${ctx}/portais/discente/turmas_habilitadas.jsf">clique aqui</a>.
				</p>
			</c:if>
			<c:if test="${ not portalDiscente.modoReduzido }">

				<c:set var="turmasVirtuais" value="#{ portalDiscente.turmasVirtuaisHabilitadas }"/>
				<c:if test="${!empty turmasVirtuais}">
				<h:form>
					<%@include file="/portais/include/turmas_habilitadas.jsp" %>	
				</h:form>
				</c:if>


			</c:if>
			</div>
			</c:if>
			
			<%-- COMUNIDADES VIRTUAIS --%>
			<div id="participantes" class="simple-panel">
				<h4> Comunidades Virtuais que participa atualmente </h4>
				<c:if test="${ portalDiscente.modoReduzido }">
				<h:form>
				<p class="vazio">
					Listagem de comunidades virtuais temporariamente indisponível. Para acessá-las, <h:commandLink id="verTodasComunidades" action="#{buscarComunidadeVirtualMBean.exibirTodasComunidadesDocente}" value="clique aqui"/>.
				</p>
				</h:form>
				</c:if>
				<c:if test="${ not portalDiscente.modoReduzido }">
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
											<h:commandLink id="entrarComunidadeVirtual" action="#{portalDiscente.entrarComunidadeVirtual}" value="#{c.nome}">
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
			
			<h:form id="formAtividades">
				<div id="avaliacao-portal" class="simple-panel">
					<h4> Minhas atividades  </h4>
					<c:if test="${ portalDiscente.modoReduzido }">
					<p class="vazio">
						Listagem de atividades temporariamente indisponível.
					</p>
					</c:if>
					<c:if test="${ not portalDiscente.modoReduzido }">
					<c:set var="atividades_" value="#{ portalDiscente.proximasAtividades }"/>
					<c:choose>
						<c:when test="${ not empty atividades_ }">
							<table>
								<thead>
								<tr>
									<th style="width:30px;"></th>
									<th style="width:165px;">Data</th>
									<th>Atividade</th>
								</tr>
								</thead>
								<tbody>
									<c:forEach items="#{atividades_}" var="a" varStatus="status">
										<c:set var="image" value=""/>
										<c:if test="${a.inMonth && a.dias >= 0}">
											<c:set var="image" value="<img src='/sigaa/img/prova_mes.png' title='Atividade no mês atual'>"/>
										</c:if>
										<c:if test="${a.inWeek && a.dias >= 0}">
											<c:set var="image" value="<img src='/sigaa/img/prova_semana.png' title='Atividade na Semana'>"/>
										</c:if>
										
										<c:if test="${(a.idTarefa != 0 || a.idQuestionario != 0) && a.concluida && a.dias >= 0}">
											<c:set var="image" value="<img src='/sigaa/img/check.png' title='Atividade concluída'>"/>
										</c:if>
										
										<tr class="${status.index % 2 == 0 ? "odd" : "" }">
											<td style="text-align:center;"> ${image} </td>
											<td>
												<c:if test="${a.dias < 0 }">
													<font color="gray">
														<ufrn:format name="avaliacao" valor="${a.data}" type="data"/> 
														<c:if test="${a.idTarefa != 0 || a.idQuestionario != 0}">${a.horaEntrega}:${a.minutoEntrega}</c:if>
													</font>
												</c:if>
												<c:if test="${a.dias > 0 }">
													<ufrn:format name="avaliacao" valor="${a.data}" type="data"/> 
													<c:if test="${a.idTarefa != 0 || a.idQuestionario != 0}">${a.horaEntrega}:${a.minutoEntrega}</c:if>
													<c:if test="${a.dias == 1 }">
														(${a.dias} dia)
													</c:if>
													<c:if test="${a.dias != 1 }">
														(${a.dias} dias)
													</c:if>
												</c:if>
												<c:if test="${a.dias == 0}">
													<ufrn:format name="avaliacao" valor="${a.data}" type="data"/> 
													<c:if test="${a.idTarefa != 0 || a.idQuestionario != 0}">${a.horaEntrega}:${a.minutoEntrega}</c:if>
													(Hoje)
												</c:if>
											</td>
											<td>
												<small>
													<c:if test="${a.dias < 0 }">
														<font color="gray">
													</c:if>
													${a.turma.disciplina.detalhes.nome}<br> 
													<c:if test="${a.idTarefa == 0 && a.idQuestionario == 0}"><strong>Avaliação:</strong> ${a.descricao}</c:if>
													<c:if test="${a.idTarefa != 0}">
														<strong>Tarefa:</strong>
														<h:commandLink id="visualizarTarefaTurmaVirtual" action="#{turmaVirtual.visualizarTarefa}" value="#{a.descricao}">
															<f:param name="id" value="#{a.idTarefa}" />
															<f:param name="idTurma" value="#{a.turma.id}" />
														</h:commandLink>
													</c:if>
													<c:if test="${a.idQuestionario != 0}">
														<strong>Questionário:</strong>
														<h:commandLink id="visualizarQuestionarioTurmaVirtual" action="#{turmaVirtual.visualizarQuestionario}" value="#{a.descricao}">
															<f:param name="id" value="#{a.idQuestionario}" />
															<f:param name="idTurma" value="#{a.turma.id}" />
														</h:commandLink>
													</c:if>
													<c:if test="${a.dias < 0 }">
														</font>
													</c:if>
												</small>
											</td>
										</tr>
									</c:forEach>
								</tbody>
							</table>
							<a class="mais" href="javascript:alert('Solicite que o seu professor inclua as datas da avaliação na Turma Virtual. Assim, elas aparecem aqui e você se organiza em suas provas')">Minhas avaliações não aparecem!? Clique aqui!</a>
						</c:when>
		
						<c:otherwise>
							<p class="vazio">
								Não há atividades cadastradas para os próximos 15 dias ou decorridos 7 dias.
							</p>
						</c:otherwise>
					</c:choose>
					</c:if>
				</div>
			</h:form>

	<%-- EXIBE OS TOPICOS DO FORUM --%> 
	<div id="forum-portal" class="simple-panel">
		<c:if test="${ portalDiscente.modoReduzido }">
		<h4>Fóruns</h4>
		<h:form>
		<p class="vazio">
			Listagem de fóruns temporariamente indisponível. Para acessá-los, <h:commandLink action="#{ forum.listarForunsCurso }" value="clique aqui"/>.
		</p>
		</h:form>
		</c:if>
		<c:if test="${ not portalDiscente.modoReduzido }">
		<c:set var="foruns" value="#{forumMensagem.forumMensagemPortalDiscente}" />

			<c:if test="${ not empty foruns }">
				<h4>${forumMensagem.forum.titulo} </h4>
			</c:if>
			<c:if test="${ empty foruns }">
				<h4> Forum de Cursos </h4>
			</c:if>

		<h:form>
		<div class="descricaoOperacao">
			Caro Aluno, este fórum é destinado para discussões relacionadas ao seu curso. Todos os alunos 
			do curso e a coordenação tem acesso a ele.
		</div>
		
		<c:if test="${forum.usuarioAtivo}">
		<center>
			<h:commandLink id="novoTopicoForum" 
				action="#{ forumMensagem.novoForumCurso }"
				value="Cadastrar novo tópico para este fórum" />
			&nbsp;&nbsp;
			<h:commandLink id="listarTopicosForum" 
				action="#{ forum.listarForunsCurso }"
				value="Visualizar todos os tópicos para este fórum" />
		</center>
		<br/>
		</c:if>
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

							<td width="50%">
							<h:commandLink id="mostrarForumMensagemCurso"
								action="#{ forumMensagem.mostrarForumMensagemCurso }">
								<h:outputText value="#{ n.titulo }" />
								<f:param name="idForumMensagem" value="#{ n.id }" />
								<f:param name="id" value="#{ n.forum.id }" />
							</h:commandLink>
							</td>

							<!-- <td>${ n.titulo }</td> -->
							<td><acronym title="${ n.usuario.pessoa.nome}"> ${
							n.usuario.login } </acronym></td>
							<td style="text-align: center">${n.respostas }</td>
							<td style="text-align: center">
								<c:if test="${ n.ultimaPostagem != null }">
									<fmt:formatDate pattern="dd/MM/yyyy HH:mm:ss" value="${ n.ultimaPostagem }" />
								</c:if> 
								<c:if test="${ n.ultimaPostagem == null }">
									<fmt:formatDate pattern="dd/MM/yyyy HH:mm:ss" value="${ n.data }" />
								</c:if>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</c:if>
	</h:form>
	
	${ forumMensagem.limparForumMensagemPortalDiscente }
	</c:if>
	</div>
			<%-- LIVROS DA BIBLIOTECA--%>
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