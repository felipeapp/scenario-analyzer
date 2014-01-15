<%-- Docentes --%>
<c:set var="professores" value="#{turmaVirtual.docentesTurma}"/>

<c:if test="${not empty professores}">
	<fieldset>
		<legend> Professores (${ fn:length(professores) })</legend></fieldset>
		<table class="participantes">
			<c:forEach items="${ professores }" var="item" varStatus="loop">
			<tr class="${loop.index % 2 == 0 ? 'odd' : 'even' }">
				<td width="72" align="center">
					<c:if test="${item.docente.idFoto != null && item.docente.idFoto != 0 }"><img src="${ctx}/verFoto?idFoto=${item.docente.idFoto}&key=${ sf:generateArquivoKey(item.docente.idFoto) }" width="60" height="75" />	</c:if>
					<c:if test="${item.docente.idFoto == null || item.docente.idFoto == 0}"><h:graphicImage value="/img/no_picture.png" width="60" height="75" /></c:if>
				</td>
				<td valign="top">
		
					<c:if test="${item.docente != null}">
						<c:if test="${item.docente.primeiroUsuario.online}">
							<h:graphicImage value="/img/portal_turma/online.png" title="Usuário On-Line no SIGAA" />
						</c:if>
						<c:if test="${!item.docente.primeiroUsuario.online}">
							<h:graphicImage value="/img/portal_turma/offline.png" title="Usuário Off-Line no SIGAA" />
						</c:if>
						<strong>
							<a href="${portalDocente.linkPaginaPublicaDocente}/${item.docente.primeiroUsuario.login}"  target="_blank" title="Acesse a página pública deste docente" >${item.docente.pessoa.nome}</a>
						</strong> <br/>
						Departamento: <em>${item.docente.unidade.nome}</em> <br/>
						Formação: <em>${item.docente.formacao.denominacao}</em> <br/>
						Usuário: <em>${ item.docente.primeiroUsuario.login }</em> <br/>
						E-Mail: <em>${ item.docente.primeiroUsuario.email }</em> <br/>
						<c:if test="${ item.docente.perfil.descricao != null }">
						<ufrn:format name="item" property="docente.perfil.descricao" type="texto" length="500"/>
						</c:if>
					</c:if>
		
					<c:if test="${item.docenteExterno != null}">
		
						<strong>${item.docenteExterno.pessoa.nome}</strong> <br/>
						Departamento: <em>${item.docenteExterno.unidade.nome}</em> <br/>
						Formação: <em>${item.docenteExterno.formacao.denominacao}</em> <br/>
		
						<ufrn:format name="item" property="docente.perfil.descricao" type="texto" length="500"/>
		
					</c:if>
		
					<c:if test="${ not empty turmaVirtual.turma.subturmas }">
					Turma(s): <em>${ item.turma.codigo }</em><br/>
					</c:if>
		
				</td>
				<td width="20">
					<c:if test="${ item.docente.primeiroUsuario != null}">
					<a class="naoImprimir" href="javascript://nop/" onclick="Mensagem.show(<%=""+br.ufrn.arq.caixa_postal.Mensagem.MENSAGEM%>, '${ item.docente.primeiroUsuario.login }','${ turmaVirtual.turma.descricaoCodigo }');"><img src="${ctx}/img/email_go.png" alt="Enviar Mensagem" title="Enviar Mensagem"/></a>
					</c:if>
				</td>
			</tr>
		</c:forEach>
		</table>
</c:if>

<%-- Docência assistida --%>
<c:set var="planoDocencia" value="#{turmaVirtual.docenciaAssistida}"/>

<c:if test="${not empty planoDocencia }">
	<br />
	<fieldset>
		<legend> Docência Assistida (${ fn:length(planoDocencia) })</legend>
	</fieldset>
		<table class="participantes">
			<c:forEach items="#{ planoDocencia }" var="item" varStatus="loop">
			<tr class="${loop.index % 2 == 0 ? 'odd' : 'even' }">
				<td width="72" align="center">
					<c:if test="${item.discente.idFoto != null && item.discente.idFoto != 0 }"><img src="${ctx}/verFoto?idFoto=${item.discente.idFoto}&key=${ sf:generateArquivoKey(item.discente.idFoto) }" width="60" height="75" />	</c:if>
					<c:if test="${item.discente.idFoto == null || item.discente.idFoto == 0}"><h:graphicImage value="/img/no_picture.png" width="60" height="75" /></c:if>
				</td>
				<td valign="top">
					<c:if test="${item.discente != null}">
						<c:if test="${item.discente.usuario.online}">
							<h:graphicImage value="/img/portal_turma/online.png" title="Usuário On-Line no SIGAA" />
						</c:if>
						<c:if test="${!item.discente.usuario.online}">
							<h:graphicImage value="/img/portal_turma/offline.png" title="Usuário Off-Line no SIGAA" />
						</c:if>
						<strong>
							<h:commandLink id="linkPlanoDocencia" action="#{planoDocenciaAssistidaMBean.viewImpressao}" title="Visualizar o Plano de Docência Assistida" target="_blank">
								${item.discente.nome}
								<f:setPropertyActionListener value="#{item}" target="#{planoDocenciaAssistidaMBean.obj}"/>
							</h:commandLink>							
						</strong> <br/>
						Departamento: <em>${item.discente.curso.unidade.nome}</em> <br/>
						Usuário: <em>${ item.discente.usuario.login }</em> <br/>
						E-Mail: <em>${ item.discente.usuario.email }</em> <br/>
					</c:if>
		
					<c:if test="${ not empty turmaVirtual.turma.subturmas }">
					Turma(s): <em>${ item.turmaDocenciaAssistida.turma.codigo }</em><br/>
					</c:if>
		
				</td>
				<td width="20">
					<c:if test="${ item.discente.usuario.login != null}">
					<a class="naoImprimir" href="javascript://nop/" onclick="Mensagem.show(<%=""+br.ufrn.arq.caixa_postal.Mensagem.MENSAGEM%>, '${ item.discente.usuario.login }');"><img src="${ctx}/img/email_go.png" alt="Enviar Mensagem" title="Enviar Mensagem"/></a>
					</c:if>
				</td>
			</tr>
		</c:forEach>
		</table>
</c:if>

<aj4:region>
	<%-- Monitores --%>
	
	<%-- Na tarefa 87203, Gleydson pediu que esta parte fosse comentada da view. --%>
	
	<%--<c:set var="monitores" value="#{turmaVirtual.monitores}"/>
	
	<c:if test="${not empty monitores }">
		<br />
		<fieldset>
			<legend> Monitores (${ fn:length(monitores) })</legend>
		</fieldset>
			<table class="participantes">
			<c:forEach items="#{ monitores }" var="item" varStatus="loop">
				<c:if test="${loop.index % 2 == 0 }">
				<tr class="${loop.index % 4 == 0 ? 'odd' : 'even' }">
				</c:if>
					<td width="47">
						<c:if test="${item.idFoto != null}"><img src="${ctx}/verFoto?idFoto=${item.discente.idFoto}&key=${ sf:generateArquivoKey(item.discente.idFoto) }" width="48" height="60"/></c:if>
						<c:if test="${item.idFoto == null}"><img src="${ctx}/img/no_picture.png" width="48" height="60"/></c:if>
					</td>
					<td valign="top">
						<c:if test="${item.usuario.online}">
							<img src="${ctx}/img/portal_turma/online.png" title="Usuário On-Line no SIGAA"/>
						</c:if>
						<c:if test="${!item.usuario.online}">
							<img src="${ctx}/img/portal_turma/offline.png" title="Usuário Off-Line no SIGAA"/>
						</c:if>
						<strong>
							${item.pessoa.nome}
							
							<c:set var="idPessoaPerfil" value="#{ item.pessoa.id }" />
							<%@include file="/ava/PerfilUsuarioAva/link_perfil.jsp" %>
							
						</strong><br/>
						Curso: <em>${item.curso.descricao} </em><br/>
						Matrícula: <em>${item.matricula}</em> <br/>
						Usuário: <em>${ item.usuario != null ? item.usuario.login : "Sem cadastro no sistema" } </em><br/>
						E-mail: <em>${ item.pessoa.email != null ? item.pessoa.email : "Desconhecido" }</em>

					</td>
					<td width="20">
						<c:if test="${ item.usuario != null}">
						<a class="naoImprimir" href="javascript://nop/" onclick="Mensagem.show(<%=""+br.ufrn.arq.caixa_postal.Mensagem.MENSAGEM%>, '${ item.discente.usuario.login }');"><img src="${ctx}/img/email_go.png" alt="Enviar Mensagem" title="Enviar Mensagem"/></a>
						</c:if> 
					</td>
				<c:if test="${loop.index % 2 == 1 }">
				</tr>
				</c:if>
			</c:forEach>
			</table>
	</c:if>--%>
	
	<%-- Discentes --%>
	<c:set var="alunos" value="#{turmaVirtual.discentesTurma}"/>
	
	<c:if test="${not empty alunos }">
		<br />
		<fieldset>
			<c:set var="loopIndex" value="${1}" />
			<legend> Alunos (${ fn:length(alunos)})</legend>
		</fieldset>
			<table class="participantes">
				<c:forEach items="#{ alunos }" var="item" varStatus="loop">
					<c:if test="${ item.esconder == null || item.esconder == false }">
						<c:set var="loopIndex" value="${loopIndex + 1}" />
						<c:if test="${loopIndex % 2 == 0 }">
						<tr class="${loopIndex % 4 == 0 ? 'odd' : 'even' }">
						</c:if>
							<td width="47">
								<c:if test="${item.discente.idFoto != null}"><img src="${ctx}/verFoto?idFoto=${item.discente.idFoto}&key=${ sf:generateArquivoKey(item.discente.idFoto) }" width="48" height="60"/></c:if>
								<c:if test="${item.discente.idFoto == null}"><img src="${ctx}/img/no_picture.png" width="48" height="60"/></c:if>
							</td>
							<td valign="top">
								<c:if test="${item.discente.usuario.online}">
									<img src="${ctx}/img/portal_turma/online.png" title="Usuário On-Line no SIGAA"/>
								</c:if>
								<c:if test="${!item.discente.usuario.online}">
									<img src="${ctx}/img/portal_turma/offline.png" title="Usuário Off-Line no SIGAA"/>
								</c:if>
								<strong>
									${item.discente.pessoa.nome}
									
									<c:set var="idPessoaPerfil" value="#{ item.discente.pessoa.id }" />
									<%@include file="/ava/PerfilUsuarioAva/link_perfil.jsp" %>
									
									<c:if test="${item.trancado or item.cancelada}">
										<span class="situacao">(${item.situacaoMatricula.descricao})</span>
									</c:if>
								</strong><br/>
								Curso: <em>${item.discente.curso.descricao} </em><br/>
								Matrícula: <em>${item.discente.matricula}</em> <br/>
								Usuário: <em>${ item.discente.usuario != null ? item.discente.usuario.login : "Sem cadastro no sistema" } </em><br/>
								<c:if test="${ item.discente.infantil }">
								Responsável(1): <em>${item.discente.responsavel.nome}</em><br/>
								Responsável(2): <em>${item.discente.outroResponsavel.nome}</em><br/>
								Tel. Fixo: <em>(${item.discente.pessoa.codigoAreaNacionalTelefoneFixo})${item.discente.pessoa.telefone}</em><br/>
								</c:if>
								E-mail: <em>${ item.discente.pessoa.email != null ? item.discente.pessoa.email : "Desconhecido" } </em>
								<c:if test="${ not empty turmaVirtual.turma.subturmas }"><br/>
								Turma: <em>${ item.turma.codigo }</em>
								</c:if>
							</td>
							<td width="20">
								
								<c:if test="${ fn:length(item.discente.solicitacoesApoioNee) > 0 && (turmaVirtual.docente || permissaoAva.permissaoUsuario.docente) }">
									<c:set var="idDiscente_nee" value="#{ item.discente.id }" />
									<%@include file="/ava/nee/link_parecer_nee.jsp" %>
								</c:if>
								
								<c:if test="${ item.discente.usuario != null}">
								<a class="naoImprimir" href="javascript://nop/" onclick="Mensagem.show(<%=""+br.ufrn.arq.caixa_postal.Mensagem.MENSAGEM%>, '${ item.discente.usuario.login }');"><img src="${ctx}/img/email_go.png" alt="Enviar Mensagem" title="Enviar Mensagem"/></a>
								</c:if> 
							</td>
						<c:if test="${loopIndex % 2 == 1 }">
						</tr>
						</c:if>
					</c:if>
				</c:forEach>
			</table>
			<br/>
			
			<c:if test="${acesso.docente}">
				<div align="center" style="color: blue;font-weight:bold;">
				<a onclick="mostrarEmails()" href="#" >Lista de E-mail dos alunos da turma</a>
				<ufrn:help>
					Caso deseje enviar um email para todos os discentes desta turma, clique no link ao lado, copie a lista de emails e cole no programa de envio de email que utiliza. 
				</ufrn:help>
				</div>
				<br />
				<div id="divEmails" align="center" style="display: none;border: 1px solid;">
					<h:outputText>
						<c:forEach var="item" items="#{ alunos }" varStatus="status">
							${ item.discente.pessoa.email } <c:if test="${not status.last}">;</c:if>
						</c:forEach>
					</h:outputText>
				</div>
			</c:if>
	</c:if>
	
<script type="text/javascript">

function mostrarEmails() {
	$("divEmails").toggle();
}
</script>

</aj4:region>