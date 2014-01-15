<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp" %>

<style>
	*{
		font-family:"Verdana";
		color:#000000;
		text-decoration:none;
		font-size:9pt;
	}
	
	h1{
		font-size:11pt;
		margin:0px;
		font-weight:bold;
		text-align:center;
	}
	
	h2{
		font-size:12pt;
		margin:0px;
		color:#000000;
		border:none;
		background:none;
		text-align:center;
	}
	
	h3{
		border-bottom:2px solid #CCCCCC;
		margin-top:15px;
		margin-bottom:10px;
		font-size:10pt;
	}
	
	img{
		border:none;
	}
	fieldset { 
		border: 0;
		border-top: 2px solid #15428B;
		width: 97%;
	}
	fieldset legend {
		color: #15428B;
		font-size: 1.5em;
		font-variant: small-caps;
		font-weight: bold;
		margin: 1px 0;
		background-color: white;
	}
	.pageBreak {
		page-break-before: always;
	}
</style>

<%@ taglib uri="/tags/a4j" prefix="a4j"%>

<f:view>
	<c:set var="primeiraPagina" value="${-1}" />
	<c:set var="linhas" value="${0}" />
	<h1>${turmaVirtual.turma.descricaoSemDocente }</h1>
	<h2>Lista de participantes</h2>
	
	<%-- Docentes --%>
	<c:set var="professores" value="#{turmaVirtual.docentesTurma}"/>
	<c:if test="${not empty professores}">
		<fieldset>
			<legend> Professores (${ fn:length(professores) })</legend>
		</fieldset>
		<div>
			<table class="participantes">
				<c:forEach items="${ professores }" var="item" varStatus="loop">
				<c:if test="${(primeiraPagina < 0 && linhas > 5) || (primeiraPagina > 0 && linhas > 7) }">
					<c:set var="primeiraPagina" value="${1}" />
					<c:set var="linhas" value="${0}" />
					<tr class="pageBreak" />
				</c:if>
				<c:set var="linhas" value="${linhas + 1 }" />
				<tr class="${loop.index % 2 == 0 ? 'odd' : 'even' }">
					<td width="72" align="center">
						<c:if test="${item.docente.idFoto != null && item.docente.idFoto != 0 }"><img src="${ctx}/verFoto?idFoto=${item.docente.idFoto}&key=${ sf:generateArquivoKey(item.docente.idFoto) }" width="60" height="75" />	</c:if>
						<c:if test="${item.docente.idFoto == null || item.docente.idFoto == 0}"><h:graphicImage value="/img/no_picture.png" width="60" height="75" /></c:if>
					</td>
					<td valign="top">
						<c:if test="${item.docente != null}">
							<strong>${item.docente.pessoa.nome}</strong> <br/>
							Departamento: <em>${item.docente.unidade.nome}</em> <br/>
							Formação: <em>${item.docente.formacao.denominacao}</em> <br/>
							Usuário: <em>${ item.docente.primeiroUsuario.login }</em> <br/>
							E-Mail: <em>${ item.docente.primeiroUsuario.email }</em> <br/>
						</c:if>
			
						<c:if test="${item.docenteExterno != null}">
							<strong>${item.docenteExterno.pessoa.nome}</strong> <br/>
							Departamento: <em>${item.docenteExterno.unidade.nome}</em> <br/>
							Formação: <em>${item.docenteExterno.formacao.denominacao}</em> <br/>
						</c:if>
			
						<c:if test="${ not empty turmaVirtual.turma.subturmas }">
							Turma(s): <em>${ item.turma.codigo }</em><br/>
						</c:if>
					</td>
					<td width="10"></td>
				</tr>
			</c:forEach>
			</table>
		</div>
	</c:if>
	
	<%-- Docência assistida --%>
	<c:set var="planoDocencia" value="#{turmaVirtual.docenciaAssistida}"/>
	<c:if test="${not empty planoDocencia }">
		<br />
		<fieldset>
			<legend> Docência Assistida (${ fn:length(planoDocencia) })</legend>
		</fieldset>
		<div>
			<table class="participantes">
				<c:forEach items="#{ planoDocencia }" var="item" varStatus="loop">
				<c:if test="${(primeiraPagina < 0 && linhas > 5) || (primeiraPagina > 0 && linhas > 7) }">
					<c:set var="primeiraPagina" value="${1}" />
					<c:set var="linhas" value="${0}" />
					<tr class="pageBreak" />
				</c:if>
				<c:set var="linhas" value="${linhas + 1 }" />
				<tr class="${loop.index % 2 == 0 ? 'odd' : 'even' }">
					<td width="72" align="center">
						<c:if test="${item.discente.idFoto != null && item.discente.idFoto != 0 }"><img src="${ctx}/verFoto?idFoto=${item.discente.idFoto}&key=${ sf:generateArquivoKey(item.discente.idFoto) }" width="60" height="75" />	</c:if>
						<c:if test="${item.discente.idFoto == null || item.discente.idFoto == 0}"><h:graphicImage value="/img/no_picture.png" width="60" height="75" /></c:if>
					</td>
					<td valign="top">
						<c:if test="${item.discente != null}">
							<strong>${item.discente.nome}</strong> <br/>
							Departamento: <em>${item.discente.curso.unidade.nome}</em> <br/>
							Usuário: <em>${ item.discente.usuario.login }</em> <br/>
							E-Mail: <em>${ item.discente.usuario.email }</em> <br/>
						</c:if>
			
						<c:if test="${ not empty turmaVirtual.turma.subturmas }">
							Turma(s): <em>${ item.turmaDocenciaAssistida.turma.codigo }</em><br/>
						</c:if>
					</td>
					<td width="10"></td>
				</tr>
			</c:forEach>
			</table>
		</div>
	</c:if>
	
	<%-- Monitores --%>
	<c:set var="monitores" value="#{turmaVirtual.monitores}"/>
	<c:if test="${not empty monitores }">
		<br />
		<fieldset>
			<legend> Monitores (${ fn:length(monitores) })</legend>
		</fieldset>
		<div>
			<table class="participantes">
			<c:forEach items="#{ monitores }" var="item" varStatus="loop">
				<c:if test="${loop.index % 2 == 0 }">
				<tr class="${loop.index % 4 == 0 ? 'odd' : 'even' }">
					<c:if test="${(primeiraPagina < 0 && linhas > 5) || (primeiraPagina > 0 && linhas > 7) }">
						<c:set var="primeiraPagina" value="${1}" />
						<c:set var="linhas" value="${0}" />
						<tr class="pageBreak" />
					</c:if>
					<c:set var="linhas" value="${linhas + 1 }" />
				</c:if>
					<td width="47">
						<c:if test="${item.discente.idFoto != null}"><img src="${ctx}/verFoto?idFoto=${item.discente.idFoto}&key=${ sf:generateArquivoKey(item.discente.idFoto) }" width="48" height="60"/></c:if>
						<c:if test="${item.discente.idFoto == null}"><img src="${ctx}/img/no_picture.png" width="48" height="60"/></c:if>
					</td>
					<td valign="top">
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
					<td width="10"></td>
				<c:if test="${loop.index % 2 == 1 }">
				</tr>
				</c:if>
			</c:forEach>
			</table>
		</div>
	</c:if>
	
	<%-- Discentes --%>
	<c:set var="alunos" value="#{turmaVirtual.discentesTurma}"/>
	
	<c:if test="${not empty alunos }">
		<br />
		<c:set var="loopIndex" value="${1}" />
		<fieldset>
			<legend> Alunos (${ fn:length(alunos) - fn:length(monitores) })</legend>
		</fieldset>
		<div>
			<table class="participantes">
				<c:forEach items="#{ alunos }" var="item" varStatus="loop">
					<c:if test="${ item.esconder == null || item.esconder == false }">
						<c:set var="loopIndex" value="${loopIndex + 1}" />
						<c:if test="${loopIndex % 2 == 0 }">
							<c:if test="${(primeiraPagina < 0 && linhas > 5) || (primeiraPagina > 0 && linhas > 7) }">
								<c:set var="primeiraPagina" value="${1}" />
								<c:set var="linhas" value="${0}" />
								<tr class="pageBreak" />
							</c:if>
							<c:set var="linhas" value="${linhas + 1 }" />
						<tr class="${loopIndex % 4 == 0 ? 'odd' : 'even' }" style="line-height:15px;">
						</c:if>
							<td width="47">
								<c:if test="${item.discente.idFoto != null}"><img src="${ctx}/verFoto?idFoto=${item.discente.idFoto}&key=${ sf:generateArquivoKey(item.discente.idFoto) }" width="48" height="60"/></c:if>
								<c:if test="${item.discente.idFoto == null}"><img src="${ctx}/img/no_picture.png" width="48" height="60"/></c:if>
							</td>
							<td valign="top">
								<strong>
									${item.discente.pessoa.nome}
									<h:outputText value="(#{item.situacaoMatricula.descricao})" rendered="#{item.trancado or item.cancelada}" style="color: #992222;" />
								</strong><br/>
								Curso: <em>${item.discente.curso.descricao} </em><br/>
								Matrícula: <em>${item.discente.matricula}</em> <br/>
								Usuário: <em>${ item.discente.usuario != null ? item.discente.usuario.login : "Sem cadastro no sistema" } </em><br/>
								E-mail: <em>${ item.discente.pessoa.email != null ? item.discente.pessoa.email : "Desconhecido" } </em>
								<c:if test="${ not empty turmaVirtual.turma.subturmas }"><br/>
									Turma: <em>${ item.turma.codigo }</em>
								</c:if>
							</td>
							<td width="10"></td>
						<c:if test="${loopIndex % 2 == 1 }">
						</tr>
						</c:if>
					</c:if>
				</c:forEach>
			</table>
		</div>
	</c:if>
	</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp" %>
<script>window.print();</script>
${turmaVirtual.concluirImpressao}