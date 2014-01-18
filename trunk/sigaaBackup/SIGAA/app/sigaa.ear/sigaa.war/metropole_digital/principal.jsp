<%@include file="/WEB-INF/jsp/include/cabecalho.jsp" %>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>
<%@page import="br.ufrn.comum.dominio.LocalizacaoNoticiaPortal"%>

<script type="text/javascript" src="/shared/javascript/paineis/turma_componentes.js"></script>
<script type="text/javascript" src="/shared/javascript/paineis/painel_generico.js"></script>
<link href="/sigaa/metropole_digital/css/busca_turma.css" rel="stylesheet" type="text/css" />

<%@taglib uri="/tags/primefaces-p" prefix="p"%>
<% CheckRoleUtil.checkRole(request, response, new int[] {SigaaPapeis.TUTOR_IMD} ); %>

<jwr:style src="/css/portal_docente.css" media="all"/>


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
			<%@include file="/metropole_digital/menus/menu_portal_tutoria.jsp" %>	
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
						<li><a href="<%="/sigaa/abrirCaixaPostal.jsf?sistema="+String.valueOf(Sistema.SIGAA)%>" > Minhas Mensagens </a></li>
						<li><a class="perfil" href="perfil.jsf"> Trocar Foto </a></li>
						<li><a class="perfil" href="perfil.jsf"> <h:commandLink action="#{ dadosPessoaisTutor.iniciarAlteracaoDadosTutor}" value="Atualizar Dados Pessoais" /> </a></li>
					</ul>
				</h:form>
				<div class="clear"> </div>

			</div>	
				
			<div id="agenda-docente">
				<h4> Dados Pessoais </h4>

				<c:if test="${usuario.vinculoAtivo.vinculoServidor}">

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
									<h:commandLink value="#{portalTutoria.qtdAlunosOrientandos}" action="#{ portalTutoria.verAlunos }" />
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
						<td> TUTOR </td>
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
				
				<c:if test="${empty portalTutoria.turmasTutoria }">
					<center><i> Nenhuma turma aberta encontrada. </i></center>
				</c:if>
				
				<h:form>
				
					<c:if test="${not empty portalTutoria.turmasTutoria }">
						<table>
							<tbody>
		
							<tr>
								<th colspan="2">TURMAS</th>			
							</tr>
							
							<c:set var="nivelAtual" value="" />
							<c:forEach items="#{portalTutoria.turmasTutoria}" var="turmaEntrada" varStatus="status">
								
								
								<tr class="${status.index % 2 == 0 ? "odd" : "" }">
									
									<td class="descricao" style="width: 97%;">
									 	<c:if test="${not empty turmaEntrada.opcaoPoloGrupo.polo.observacao }" >
									 		<b>${turmaEntrada.anoReferencia}.${turmaEntrada.periodoReferencia} - ${turmaEntrada.especializacao.descricao} - ${turmaEntrada.cursoTecnico.nome} - ${turmaEntrada.opcaoPoloGrupo.polo.cidade.nome }-${turmaEntrada.opcaoPoloGrupo.polo.cidade.unidadeFederativa.sigla } (${turmaEntrada.opcaoPoloGrupo.polo.observacao })</b>
									 	</c:if>
										<c:if test="${empty turmaEntrada.opcaoPoloGrupo.polo.observacao }" >
									 		<b>${turmaEntrada.anoReferencia}.${turmaEntrada.periodoReferencia} - ${turmaEntrada.especializacao.descricao} - ${turmaEntrada.cursoTecnico.nome} - ${turmaEntrada.opcaoPoloGrupo.polo.cidade.nome }-${turmaEntrada.opcaoPoloGrupo.polo.cidade.unidadeFederativa.sigla }</b>
									 	</c:if> 	
									</td>
									
									<td><img src="${ctx}/img/biblioteca/emprestimos_ativos.png" onclick="exibirOpcoes(${turmaEntrada.id});" style="cursor: pointer" title="Visualizar Opções"/></td>
								
								
									<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}" style="display: none" id="trOpcoes${turmaEntrada.id}">
										<td colspan="3">
							            <c:set var="bgCor" value="${ s.index % 2 == 0 ? '#F9FBFD' : '#EDF1F8' }" />
							             
										<ul class="listaOpcoes" style="width: 472px;">
										
										
											<li id="lancarFrequencia">
												<h:commandLink styleClass="noborder" title="Lançar Frequência" action="#{lancamentoFrequenciaIMD.redirecionarFrequencia}" >
													<f:param name="id" value="#{turmaEntrada.id}"/>
													Lançar Frequência
												</h:commandLink>
											</li>
											
											<li id="relatorioFrequencia">
												<h:commandLink  styleClass="noborder" title="Relatório Geral de Frequência" action="#{lancamentoFrequenciaIMD.redirecionarSelecaoFiltrosRelatorioFrequencia}" >
													<f:param name="id" value="#{turmaEntrada.id}"/>
													Relatório Geral de Frequência
												</h:commandLink>	
											</li>
											
											<li id="lancarNotasSemanais">
												<h:commandLink styleClass="noborder" title="Lançar de Notas por Período" action="#{lancamentoNotasSemanais.preLancarNotasSemanais}" >
													<f:param name="id" value="#{turmaEntrada.id}"/>
													Lançar Notas por Período
												</h:commandLink>	
											</li>
											
											<li id="relatorioFreqDiscente">
												<h:commandLink styleClass="noborder" title="Relatório Frequência por Discente" action="#{lancamentoFrequenciaIMD.redicionarSelecaoDiscente}" >
													<f:param name="id" value="#{turmaEntrada.id}"/>
													Relatório Freq. por Discente
												</h:commandLink>		
											</li>
											
											<li id="lancarNotasDisciplina">
												<h:commandLink  styleClass="noborder" title="Lançamento de Notas por Disciplina" action="#{lancamentoNotasDisciplina.listarDisciplinasTurma}" >
													<f:param name="id" value="#{turmaEntrada.id}"/>
													Lançar Notas por Disciplina
												</h:commandLink>		
											</li>
											
											<li id="consolidacaoParcial">
												<h:commandLink  styleClass="noborder" title="Consolidação Parcial de Notas" action="#{consolidacaoParcialIMD.relatorioConsolidacaoParcial}" >
													<f:param name="id" value="#{turmaEntrada.id}"/>
													Consolidação Parcial de Notas
												</h:commandLink>		
											</li>
											
											<li id="consolidacaoFinal">
												<h:commandLink  styleClass="noborder" title="Consolidação Final de Notas" action="#{consolidacaoFinalIMD.cadastroNotasRecuperacao}" >
													<f:param name="id" value="#{turmaEntrada.id}"/>
													Consolidação Final de Notas
												</h:commandLink>		
											</li>
										
										
												
										<li style="clear: both; float: none; background-image: none;"></li>
										</ul>
										
										</td>
									</tr>
								
							</c:forEach>
							</tbody>
						</table>
					</c:if>		
			
				</h:form>
				
			</div>
			
		</div>


		<div class="clear"> </div>
	</div>
</f:view>
<script type="text/javascript">
	PainelNoticias.init('/sigaa/portais/docente/viewNoticia.jsf');
</script>

<script type="text/javascript">
<!--
	
	function exibirOpcoes(idDiscente){
		var linha = 'trOpcoes'+ idDiscente;
		$(linha).toggle();
	}
//-->
</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>