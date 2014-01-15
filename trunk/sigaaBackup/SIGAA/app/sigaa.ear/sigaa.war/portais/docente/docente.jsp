<%@include file="/WEB-INF/jsp/include/cabecalho.jsp" %>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>
<%@page import="br.ufrn.comum.dominio.LocalizacaoNoticiaPortal"%>
<%@taglib uri="/tags/primefaces-p" prefix="p"%>
<% CheckRoleUtil.checkRole(request, response, new int[] {SigaaPapeis.DOCENTE} ); %>

<jwr:style src="/css/portal_docente.css" media="all"/>
<script type="text/javascript" src="/shared/javascript/paineis/painel_generico.js"></script>

<script type="text/javascript">
	JAWR.loader.script('/javascript/paineis/noticias.js');
</script>
<style>
#cboxLoadedContent iframe {
	background-color:#FFFFFF;
}
</style>

<f:view>
	<p:resources />
	<div id="portal-docente">
		<f:subview id="menu">
		<%@include file="/portais/docente/menu_docente.jsp" %>
		</f:subview>
		
		<div id="perfil-docente">
			<div class="pessoal-docente">
				<div class="foto">
					<c:if test="${usuario.idFoto != null}">
						<img src="${ctx}/verFoto?idFoto=<h:outputText value="#{usuario.idFoto}"/>&key=${ sf:generateArquivoKey(usuario.idFoto) }" width="128" height="125"/>
					</c:if>
					<c:if test="${usuario.idFoto == null}">
						<img src="${ctx}/img/no_picture.png" width="128" height="125"/>
					</c:if>
				</div>
				<h:form>
				
				<ul>
					<li>
						<a	href="<%="/sigaa/abrirCaixaPostal.jsf?sistema="+String.valueOf(Sistema.SIGAA)%>" >
							Minhas Mensagens
						</a>
					</li>
					<li>
						<a class="perfil" href="perfil.jsf">
							Trocar Foto
						</a>
					</li>
					<li>
						<a class="perfil" href="perfil.jsf">
							 Editar Dados do Site Pessoal do Docente
						</a>
					</li>
					<%--
					<li>
						<h:commandLink  action="#{calendarioAgendaMBean.iniciarCalendario}" value="Ver Agenda das Turmas" id="verAgendaTurmas"/>
					</li>
					--%> 
				</ul>
  			    </h:form>
				<div class="clear"> </div>

			</div>

			<p class="info-docente">
				<c:if test="${not empty usuario.servidorAtivo}"> 
					<center>
						Sua página pessoal do SIGAA: 
						<a href="${portalDocente.linkPaginaPublicaDocente}/${usuario.login}">${portalDocente.linkPaginaPublicaDocente}/${usuario.login}</a>
					</center><br>
				</c:if>
				
					
				<h:form id="form_links">
					<div id="agenda-docente" style="text-align: center">
					<a href="http://www.ufrn.br/capes/" title="Instruções de Acesso ao Portal CAPES fora da UFRN" target="_blank">
						<img src="/sigaa/img/capes.jpg">
					</a> 
					<c:if test="${not empty usuario.servidorAtivo}"> 
						<h:commandLink action="#{portalDocente.linkMemorando}" >
							<h:graphicImage value="/img/memorandos_eletronicos.jpg" alt="Memorandos Eletrônicos" title="Memorandos Eletrônicos" style="border:1px solid #CCC; margin-left:9px;"></h:graphicImage>
						</h:commandLink>
					</c:if>
					</div>
				</h:form>
				<p style="text-align: center; font-size: x-small">
					
					<a href="${linkPublico.urlDownloadPublico}/regulamento_dos_cursos_de_graduacao.pdf" target="_blank">
						Regulamento dos Cursos de Graduação
					</a> 
					<br/><br/>
					<a href="${linkPublico.urlDownloadPublico}/calendario_universitario.pdf" target="_blank">
						Calendário Universitário
					</a>
					<br/>
				</p>
				
				<div id="agenda-docente">
					<h4> Dados Pessoais </h4>

					<c:if test="${usuario.vinculoAtivo.vinculoServidor}">

					<table>
						<tr>	
							<td> Siape: </td>
							<td>${usuario.servidorAtivo.siape} </td>
						</tr>
						<tr>
							<td> Categoria: </td>
							<td>${usuario.servidorAtivo.categoria.descricao} </td>
						</tr>
						<tr>
							<td> Titulação: </td>
							<td>  ${usuario.servidorAtivo.formacao.denominacao}</td>
						</tr>
						<tr>
							<td> Regime Trabalho: </td>
							<td>
								<c:if test="${!usuario.servidorAtivo.dedicacaoExclusiva}">
									${usuario.servidorAtivo.regimeTrabalho}h
								</c:if>
								<c:if test="${usuario.servidorAtivo.dedicacaoExclusiva}">
									Dedicação Exclusiva
								</c:if>
								</td>
						</tr>
						
						<c:if test="${not empty portalDocente.designacoes}">
						<tr>
							<td> Designações: </td>
						</tr>
						<tr>
							<td colspan="2">
							<ul>
							<c:forEach items="#{portalDocente.designacoes}" var="d">
									<li>${d.descricaoAtividade }</li>
							</c:forEach>
							</ul>
							</td>
						</tr>
						</c:if>

						<tr>
							<td> E-mail: </td>
							<td>  ${usuario.email}</td>
						</tr>

						<c:if test="${acesso.orientadorAcademico}">
						<tr>
							<td> Orientações Acadêmicas: </td>
							<td>
								<h:form>
									<h:commandLink value="#{acesso.orientacoesAcademicas}" action="#{ orientacaoAcademica.listar }"/>
								</h:form>
							</td>
						</tr>
						</c:if>

					</table>

					</c:if>

					<c:if test="${usuario.vinculoAtivo.vinculoDocenteExterno}">

					<table>
						<tr>
							<td> Tipo: </td>
							<td> DOCENTE EXTERNO </td>
						</tr>
						<tr>
							<td> Instituição: </td>
							<td> ${usuario.docenteExterno.instituicao.sigla} </td>
						</tr>
						<tr>
							<td> Titulação: </td>
							<td> ${usuario.docenteExterno.formacao.denominacao}</td>
						</tr>
					</table>

					</c:if>

				</div>

				<p style="margin: 4px">
				<c:if test="${portalDocente.perfil != null}">
					<br>
					<ufrn:format name="portalDocente" property="perfil.descricao" type="texto"/>
				</c:if>
				</p>
				<br>
			</p>

		</div>

		<div id="main-docente">

			<%-- NOTÍCIAS --%>
			<script type="text/javascript">
			var fcontent=new Array()
			<c:forEach var="noticia" items="${noticiaPortal.noticiasDocente}" varStatus="loop">
			fcontent[${ loop.index }]="<p class=\"noticia destaque\"><a href=\"#\" id=\"noticia_${ noticia.id }\">${ sf:escapeHtml(noticia.titulo) }</a></p><p class=\"descricao\"><a href=\"#\" style=\"font-weight: normal; text-decoration: none\" id=\"noticia_${ noticia.id }\">${  sf:escapeHtml(noticia.descricaoResumida) }</a></p>";
			</c:forEach>
			</script>
			<div id="noticias-portal">
				<c:if test="${ not empty noticiaPortal.noticiasDocente }">
				<script>
					<%-- essas variaveis sao passadas para o scroller-portal.js --%>				
					var portal = "<%= LocalizacaoNoticiaPortal.getPortal(LocalizacaoNoticiaPortal.PORTAL_DOCENTE).getMd5() %>";
					var sistema = "${ request.contextPath }";
				</script>
				<script type="text/javascript" src="/shared/javascript/scroller-portal.js"></script>
				</c:if>
				<c:if test="${ empty noticiaPortal.noticiasDocente }">
				<p class="noticia destaque"> <br/>Não há notícias cadastradas.</p>
				</c:if>
			</div>

			<%-- LISTA DE TURMAS NO SEMESTRE --%>
			<div id="turmas-portal" class="simple-panel">
				<h4> Minhas turmas no semestre </h4>
				
				<c:if test="${empty portalDocente.turmasAbertas }">
					<center><i> Nenhuma turma aberta encontrada </i></center>
				</c:if>
				
				<h:form>
				
				<c:if test="${not empty portalDocente.turmasAbertas }">
				<table>
					<tbody>

					<tr>
						<th>Componente Curricular</th>
						<th width="5%">CR/CHD*</th>
						<th width="10%" style="text-align: center">&nbsp;Horário</th>
						<th width="5%">&nbsp;Alunos**</th>
						<th width="5%" style="text-align:center;">Chat</th>
					</tr>
					
					<c:set var="nivelAtual" value="" />
					<c:forEach items="#{portalDocente.turmasAbertas}" var="turma" varStatus="status">
						<!-- agrupando as turmas por nivel -->
						<c:if test="${nivelAtual != turma.disciplina.nivelDesc}">
							<c:set var="nivelAtual" value="${turma.disciplina.nivelDesc}"/>
							<tr class="nivel">
								<td colspan="5">
									${turma.disciplina.nivelDesc}
								</td>
							</tr>
						</c:if> 
					
						<tr class="${status.index % 2 == 0 ? "odd" : "" }">
							<td class="descricao" colspan="5">
								<c:if test="${!turma.infantil}">
									<h:commandLink action="#{turmaVirtual.entrar}" value="#{turma.disciplina.codigo} - #{turma.disciplina.nome} - T#{turma.codigo}">
										<f:param name="idTurma" value="#{turma.id}"/>
									</h:commandLink>  
								</c:if>
								<c:if test="${turma.infantil}">
									<c:if test="${turma.descricaoHorario == 'M'}">
										 <h:commandLink action="#{turmaVirtual.entrar}" value="#{turma.disciplina.codigo} - #{turma.disciplina.nome} - Manhã - #{turma.codigo}">
											<f:param name="idTurma" value="#{turma.id}"/>
										</h:commandLink>
									</c:if>
									<c:if test="${turma.descricaoHorario == 'T'}">
										 <h:commandLink action="#{turmaVirtual.entrar}" value="#{turma.disciplina.codigo} - #{turma.disciplina.nome} - Tarde - #{turma.codigo}">
											<f:param name="idTurma" value="#{turma.id}"/>
										</h:commandLink>
									</c:if>
									  
								</c:if>
								<span class="situacaoTurma">(${turma.situacaoTurma.descricao})</span>
								<c:if test="${turma.disciplina.permiteHorarioFlexivel}">
									<h:outputText value="***" />
								</c:if>
							</td>
						</tr>
						<tr class="${status.index % 2 == 0 ? "odd" : "" }">
							<td  class="info" style="text-align: left;">
								<c:choose>
									<c:when test="${not empty turma.subturmas}">
										<c:forEach items="#{turma.subturmas}" var="subTurma">
										${subTurma.ano}.${subTurma.periodo} 
										<c:set var="localTurma" value="" />
										<c:if test="${ empty subTurma.polo }">
											<c:set var="localTurma" value="${subTurma.local}" />
										</c:if>
										<c:if test="${ not empty subTurma.polo }">
											<c:set var="localTurma" value="${ subTurma.polo.descricao }" />
										</c:if>
										<c:if test="${not empty localTurma}">
											 T${subTurma.codigo } - Local: ${localTurma}
										</c:if>
										<span class="situacaoTurma">(${subTurma.situacaoTurma.descricao})</span>
										<br />
									</c:forEach>
									</c:when>
									<c:otherwise>
										${turma.ano}.${turma.periodo} 
										<c:set var="localTurma" value="" />
										<c:if test="${ empty turma.polo }">
											<c:set var="localTurma" value="${turma.local}" />
										</c:if>
										<c:if test="${ not empty turma.polo }">
											<c:set var="localTurma" value="${ turma.polo.descricao }" />
										</c:if>
										<c:if test="${not empty localTurma}">
											 Local: ${localTurma}
										</c:if>
									</c:otherwise>
								</c:choose>
							</td>
							<td class="info" style="text-align: center;">
								<c:choose>
									<c:when test="${turma.tecnico}">
										<ufrn:format type="valorint" valor="${turma.disciplina.detalhes.chTotal / 15}" /> / ${turma.chDedicadaDocente} 
									</c:when>
									<c:otherwise>
										<c:choose>
											<c:when test="${not empty turma.subturmas}">
												<c:forEach items="#{turma.subturmas}" var="subTurma">
													${subTurma.disciplina.detalhes.crTotal} / ${subTurma.chDedicadaDocente} <br />
												</c:forEach>
											</c:when>
											<c:otherwise>
												${turma.disciplina.detalhes.crTotal} / ${turma.chDedicadaDocente}
											</c:otherwise>
										</c:choose>
									</c:otherwise>
								</c:choose>
							</td>
							<td class="info" style="text-align: center">
								<c:choose>
									<c:when test="${not empty turma.subturmas}">
										<c:forEach items="#{turma.subturmas}" var="subTurma">
											<c:if test="${subTurma.disciplina.permiteHorarioFlexivel}">
												<h:form id="formSubTurma">
													<p:lightBox iframe="true" width="800" height="600" opacity="0.25" speed="150">  
											        	${subTurma.descricaoHorarioSemanaAtual}
												    	<h:outputLink value="/sigaa/ensino/turma/view_calendario.jsf?idTurma=#{subTurma.id}">  
												        	<h:graphicImage value="/img/view_calendario.png" title="Ver Horário Completo da Turma" />  
												    	</h:outputLink>  
													</p:lightBox>  
												</h:form>
											</c:if>
											<c:if test="${!subTurma.disciplina.permiteHorarioFlexivel}">
												${subTurma.descricaoHorarioSemanaAtual} <br />
											</c:if>
										</c:forEach>
									</c:when>
									<c:otherwise>
										<c:if test="${turma.disciplina.permiteHorarioFlexivel}">
											<p:lightBox iframe="true" width="800" height="600" opacity="0.25" speed="150">
												${turma.descricaoHorarioSemanaAtual}
											    <h:outputLink value="/sigaa/ensino/turma/view_calendario.jsf?idTurma=#{turma.id}">  
											        <h:graphicImage value="/img/view_calendario.png" title="Ver Horário Completo da Turma" />  
											    </h:outputLink>  
											 </p:lightBox>  
										</c:if>
										<c:if test="${!turma.disciplina.permiteHorarioFlexivel}">
											${turma.descricaoHorarioSemanaAtual}
										</c:if>
									</c:otherwise>
								</c:choose>
							</td>
							<td class="info" style="text-align: center">
								<c:choose>
									<c:when test="${not empty turma.subturmas}">
										<c:forEach items="#{turma.subturmas}" var="subTurma">
											${ subTurma.totalMatriculados } / ${ subTurma.capacidadeAluno } <br />
										</c:forEach>
									</c:when>
									<c:otherwise>
										${ turma.totalMatriculados } / ${ turma.capacidadeAluno }
									</c:otherwise>
								</c:choose>
							</td>
							<td nowrap="nowrap" style="width:50px;">
									<h:graphicImage value="/img/flag_yellow.png" title="Chat em Andamento - #{turma.usersOnlineChat} pessoa(s)." rendered="#{turma.usersOnlineChat > 0 }" />
									<h:graphicImage value="/img/flag_grey.png" title="Nenhum usuário no chat." rendered="#{ turma.usersOnlineChat == 0 }" />
									&nbsp;
									<h:commandLink id="acessarChatTurma" action="#{ turmaVirtual.createChatParam }" onclick="window.open('/sigaa/entrarChat.do?idchat=#{ turma.id }&idusuario=#{ portalDocente.usuarioLogado.id }&chatName=#{ turma.disciplina.nome }&origem=portalDocente', 'chat_#{ turma.id }', 'height=485,width=685,location=0,resizable=1');" >
										<f:param name="id" value="#{ turma.id }"/>
										<h:graphicImage value="/img/comments.png" alt="Acessar Chat da turma"  title="Acessar Chat da turma - #{turma.usersOnlineChat} usuário(s)"/>
									</h:commandLink>&nbsp;
									<h:outputText value="#{turma.usersOnlineChat}" />
								
							</td>
						</tr>
					</c:forEach>
					</tbody>
				</table>
				</c:if>
				
				<span class="mais">
					<h:commandLink  action="#{agendaTurmasBean.viewAgendaTurmas}" value="Ver Agenda das Turmas" id="verAgenda"/> | 
					<c:if test="${not empty portalDocente.turmasAbertas}">
						<h:commandLink  action="#{gradeDocente.gerarGrade}" value="Grade de Horários"/> |
					</c:if>
					<a href="${ctx}/portais/docente/turmas.jsf">Ver turmas anteriores</a>
				</span>
				<span class="mais" style="text-align: left;">
				* Total de Créditos da Disciplina / Sua carga horária dedicada na turma ou subturma<br/>
				** Total de alunos matriculados / Capacidade da turma<br/>
				*** A turma possui horário flexível e o horário exibido é da semana atual.
				</span>

				</h:form>
				
			</div>

			<c:set var="turmasVirtuais" value="#{ portalDocente.turmasVirtuaisHabilitadas }"/>
			<c:if test="${!empty turmasVirtuais}">
			<h:form>
			<div id="turmas-habilitadas" class="simple-panel">
				<h4> Turmas Virtuais Habilitadas   </h4>
				<%@include file="/portais/include/turmas_habilitadas.jsp" %>	
			</div>
			</h:form>
			</c:if>
			
			<c:set var="comunidadesVirtuais" value="#{ buscarComunidadeVirtualMBean.comunidadesPorPessoa }"/>
			<h:form>
			<div id="comunidades-virtuais" class="simple-panel">
				<h4> Comunidades Virtuais </h4>

				<center>
					<h:commandLink actionListener="#{ comunidadeVirtualMBean.criar }" value="Criar Comunidade Virtual" /><br/>
					<h:commandLink actionListener="#{ buscarComunidadeVirtualMBean.criar }" value="Buscar Comunidades Virtuais" />
				</center>

				<c:if test="${!empty comunidadesVirtuais}">
				<table>
					<thead>
					<tr>
						<th>Nome</th>
					</tr>
					</thead>
					<tbody>
						<c:forEach items="#{comunidadesVirtuais}" var="t" varStatus="status" end="5">
							<tr class="${status.index % 2 == 0 ? "odd" : "" }">
								<td class="descricao">
									<c:if test="${t.ativa == true}">
									<h:commandLink action="#{comunidadeVirtualMBean.entrar}" value="#{t.nome}">
										<f:param value="#{t.id}" name="idComunidade" />
									</h:commandLink>
									</c:if>
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
					<center> <h:commandLink action="#{buscarComunidadeVirtualMBean.exibirTodasComunidadesDocente}" value="Ver todas as Comunidades..."/> </center>
				</c:if>

			</div>
			</h:form>

			<%-- LISTA DE EDITAIS --%>
			<div id="editais-portal" class="simple-panel">
				<h4> Editais Publicados

				</h4>
				<table>
					<thead>
						<tr>
							<th style="text-align: left;"> Edital </th>
							<th> Período de Submissões </th>
							<th> </th>
						</tr>
					</thead>
					<tbody>
					<c:forEach items="${portalDocente.editais}" var="edital" varStatus="status">
						<tr class="${status.index % 2 != 0 ? "odd" : "" }">
							<td class="tipo">${edital.tipoString}</td>
							<td class="periodo" nowrap="nowrap">
								<fmt:formatDate value="${ edital.inicioSubmissao }" pattern="dd/MM/yyyy"/>
								a <fmt:formatDate value="${ edital.fimSubmissao }" pattern="dd/MM/yyyy"/>
							</td>
							<td nowrap="nowrap" class="icone">
								<c:choose>
									<c:when test="${not empty edital.idArquivo}">
										<a href="/shared/verArquivo?idArquivo=${edital.idArquivo}&key=${ sf:generateArquivoKey(edital.idArquivo) }"
											 title="Baixar o edital" target="_blank">
											<img src="/shared/img/icones/download.png" alt="Baixar o edital"/>
										</a>
									</c:when>
									<c:otherwise>
										<img src="${ctx}/img/download_indisponivel.png" 
											title="O edital não foi disponibilizado" alt="O edital não foi disponibilizado"/>
									</c:otherwise>
								</c:choose>	
							</td>	
							<td nowrap="nowrap" class="icone">
								<c:choose>
									<c:when test="${edital.emAberto}">
										<c:choose>
											<c:when test="${edital.tipoString == 'MONITORIA'}">
												<h:form style="display: inline;">
												<h:commandLink action="#{projetoMonitoria.iniciarProjetoMonitoria}">
													<h:graphicImage url="/img/seta.gif" alt="Submeter Proposta de Projeto" title="Submeter Proposta de Projeto"/>
												</h:commandLink>
												</h:form>
											</c:when>
											<c:when test="${edital.tipoString == 'PESQUISA' && edital.editalConcessao}">
												<html:link
													action="pesquisa/planoTrabalho/wizard.do?dispatch=selecionarEdital&solicitacaoCota=true&idEdital=${edital.id}">
													<img src="${ctx}/img/seta.gif" alt="Solicitar Cota de Bolsa" title="Solicitar Cota de Bolsa" />
												</html:link>
											</c:when>
											<c:when test="${edital.tipoString == 'EXTENSÃO'}">
												<h:form style="display: inline;">
												<h:commandLink action="#{atividadeExtensao.listarCadastrosEmAndamento}">
													<h:graphicImage url="/img/seta.gif" alt="Submeter Proposta de Ação de Extensão" title="Submeter Proposta de Ação de Extensão" />
												</h:commandLink>
												</h:form>
											</c:when>
											<c:when test="${edital.tipoString == 'AÇÕES ACADÊMICAS INTEGRADAS'}">
												<h:form style="display: inline;">
												<h:commandLink action="#{projetoBase.iniciar}">
													<h:graphicImage url="/img/seta.gif" alt="Submeter Proposta de Ação de Acadêmica Integrada" title="Submeter Proposta de Ação de Acadêmica Integrada" />
												</h:commandLink>
												</h:form>
											</c:when>
											<c:otherwise>
												<img src="${ctx}/img/seta_cinza.gif" alt="As submissões não disponíveis" title="As submissões estão fechadas" />
											</c:otherwise>
										</c:choose>
									</c:when>
									<c:otherwise>
										<img src="${ctx}/img/seta_cinza.gif" alt="As submissões estão fechadas" title="As submissões estão fechadas" />
									</c:otherwise>
								</c:choose>
							</td>
						</tr>
						<tr class="${status.index % 2 != 0 ? "odd" : "" }">
							<td class="descricao" colspan="4">
								<c:choose>
									<c:when test="${not empty edital.idArquivo}">
										<a href="/shared/verArquivo?idArquivo=${edital.idArquivo}&key=${ sf:generateArquivoKey(edital.idArquivo) }" 
											target="_blank" title="Baixar o edital">${edital.descricao}</a>
									</c:when>
									<c:otherwise>
										<a href="#" onClick="javascript: return false;" class="semArquivo" 
											title="O edital não foi disponibilizado">
												${edital.descricao}
										</a>
									</c:otherwise>
								</c:choose>	
							</td>
						</tr>
					</c:forEach>
					</tbody>
				</table>
					<h:form id="busca">
						<h:commandLink action="#{editalMBean.getBusca}"  value="ver todos os editais" styleClass="mais"/>
					</h:form>
			</div>
			
			<%-- EXIBE OS TOPICOS DO FORUM DE TURMAS DE ENSINO MÉDIO--%> 
			<c:if test="${forumMedio.exibeForumMedioDocente}">
				<div id="forum-portal" class="simple-panel">
					<%@include file="/portais/discente/medio/forum_turma_docente.jsp" %>
				</div>
			</c:if>
			
		</div>


		<div class="clear"> </div>
	</div>
</f:view>
<script type="text/javascript">
	PainelNoticias.init('/sigaa/portais/docente/viewNoticia.jsf');
</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>