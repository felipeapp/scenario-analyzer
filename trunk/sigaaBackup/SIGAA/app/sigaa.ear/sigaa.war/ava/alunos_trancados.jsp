<%@include file="/ava/cabecalho.jsp" %>

<style>
	span.situacao { color: #922; }
</style>

<f:view>
<%@include file="/ava/menu.jsp" %>
	
	<h:form>

<fieldset>
	
	<c:set var="alunos" value="#{turmaVirtual.discentesTrancados}"/>
	<legend>Alunos Trancados (${ fn:length(alunos) })</legend>
	<c:if test="${not empty alunos }">
		<br />
	
			<table class="participantes">
				<c:forEach items="#{ alunos }" var="item" varStatus="loop">
					<c:if test="${ item.esconder == null || item.esconder == false }">
						<c:if test="${loop.index % 2 == 0 }">
						<tr class="${loop.index % 4 == 0 ? 'odd' : 'even' }">
						</c:if>
							<td width="47">
								<c:if test="${item.discente.idFoto != null}"><img src="${ctx}/verFoto?idFoto=${item.discente.idFoto}&key=${ sf:generateArquivoKey(item.discente.idFoto) }" width="48" height="60"/></c:if>
								<c:if test="${item.discente.idFoto == null}"><img src="${ctx}/img/no_picture.png" width="48" height="60"/></c:if>
							</td>
							<td valign="top">
								<c:if test="${item.discente.usuario.online && !turmaVirtual.imprimir}">
									<img src="${ctx}/img/portal_turma/online.png" title="Usuário On-Line no SIGAA"/>
								</c:if>
								<c:if test="${!item.discente.usuario.online && !turmaVirtual.imprimir}">
									<img src="${ctx}/img/portal_turma/offline.png" title="Usuário Off-Line no SIGAA"/>
								</c:if>
								<strong>
									${item.discente.pessoa.nome}
																	
									<c:if test="${item.trancado or item.cancelada}">
										<span class="situacao">(${item.situacaoMatricula.descricao})</span>
									</c:if>
								</strong><br/>
								Curso: <em>${item.discente.curso.descricao} </em><br/>
								Matrícula: <em>${item.discente.matricula}</em> <br/>
								Usuário: <em>${ item.discente.usuario != null ? item.discente.usuario.login : "Sem cadastro no sistema" } </em><br/>
								E-mail: <em>${ item.discente.pessoa.email != null ? item.discente.pessoa.email : "Desconhecido" }</em>
								<c:if test="${ not empty turmaVirtual.turma.subturmas }"><br/>
								Turma: <em>${ item.turma.codigo }</em>
								</c:if>
							</td>
							<td width="20">
								<c:if test="${ item.discente.usuario != null && !turmaVirtual.imprimir }">
								<a href="javascript://nop/" onclick="Mensagem.show(<%=""+br.ufrn.arq.caixa_postal.Mensagem.MENSAGEM%>, '${ item.discente.usuario.login }');"><img src="${ctx}/img/email_go.png" alt="Enviar Mensagem" title="Enviar Mensagem"/></a>
								</c:if> 
							</td>
						<c:if test="${loop.index % 2 == 1 }">
						</tr>
						</c:if>
					</c:if>
				</c:forEach>
			</table>

	</c:if>
	
	<c:if test="${ empty alunos }">
		<p class="empty-listing">Nenhum aluno trancou esta disciplina!</p>
	</c:if>
</fieldset>
	
		
	</h:form>

</f:view>

<%@include file="/ava/rodape.jsp"%>