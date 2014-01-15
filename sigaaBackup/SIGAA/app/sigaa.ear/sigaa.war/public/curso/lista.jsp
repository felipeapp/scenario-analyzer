<%@include file="/public/include/cabecalho.jsp"%>
<%@taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<script>
	var marcar = function(idCheck) {
		$(idCheck).checked = true;
	}
</script>
<f:view>
	<h2 class="title">Consulta de Cursos -
	${consultaPublicaCursos.descricaoNivel}</h2>

	<%-- Descrição e orientações para a consulta --%>
	<div class="descricaoOperacao">
	<p>Através desta página você pode consultar os cursos de
	${consultaPublicaCursos.descricaoNivel} ofereridos pela ${ configSistema['siglaInstituicao'] }. </p>
	<p>Para cada curso listado é possível consultar mais detalhes sobre ele,
	incluíndo as estruturas curriculares ou as disciplinas por módulos
	disponíveis.</p>
	<p>Utilize o formulário abaixo para filtrar os cursos de acordo com
	os critérios desejados.</p>
	</div>

	<%-- Formulário de consulta --%>
	<h:form id="form">
		<input type="hidden" name="nivel"
			value="${consultaPublicaCursos.obj.nivel}" />
		<table class="formulario" style="width: 60%;">
			<caption>Informe os critérios de filtragem</caption>
			<tr>
				<td width="3%"><h:selectBooleanCheckbox styleClass="noborder"
					value="#{consultaPublicaCursos.filtroNome}" id="checkNome" /></td>
				<td width="25%">Nome do Curso:</td>
				<td><h:inputText value="#{consultaPublicaCursos.obj.nome}" id="nomeDoCurso"
					style="width:95%;" onkeydown="marcar('form:checkNome')"
					onchange="marcar('form:checkNome');" /></td>
			</tr>

			<c:if
				test="${consultaPublicaCursos.graduacao or consultaPublicaCursos.lato}">
				<tr>
					<td><h:selectBooleanCheckbox styleClass="noborder"
						value="#{consultaPublicaCursos.filtroModalidade}"
						id="checkModalidade" /></td>
					<td>Modalidade de Ensino:</td>
					<td><h:selectOneMenu id="modalidadeDeEnsino"
						value="#{consultaPublicaCursos.obj.modalidadeEducacao.id}"
						onchange="marcar('form:checkModalidade');" style="width: 60%">
						<f:selectItem itemValue="0"
							itemLabel="-- SELECIONE UMA MODALIDADE --" />
						<f:selectItems value="#{modalidadeEducacao.allCombo}" />
					</h:selectOneMenu></td>
				</tr>
			</c:if>
			<c:if test="${consultaPublicaCursos.stricto}">
				<tr>
					<td>
					<h:selectBooleanCheckbox styleClass="noborder"
						value="#{consultaPublicaCursos.filtroNivel}" id="checkNivel" />
					</td>
					<td>Nível:</td>
					<td><h:selectOneMenu id="nivel"
						value="#{consultaPublicaCursos.obj.nivel}"
						onchange="marcar('form:checkNivel');" style="width: 60%">
						<f:selectItem itemValue="S" itemLabel="MESTRADO E DOUTORADO" />
						<f:selectItem itemValue="E" itemLabel="MESTRADO" />
						<f:selectItem itemValue="D" itemLabel="DOUTORADO" />
					</h:selectOneMenu></td>
				</tr>
			</c:if>
			<tfoot>
				<tr>
					<td colspan="3"><h:commandButton id="consultarCursos"
						action="#{consultaPublicaCursos.consultar}" value="Consultar" />
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>

	<%-- Lista dos cursos encontrados --%>
	<c:set var="cursos" value="#{consultaPublicaCursos.allCursos}" />
	<c:if test="${not empty cursos}">
		<br />
		<div class="legenda" style="width: 90%;">
			<f:verbatim><h:graphicImage value="/img/view.gif"/>:</f:verbatim>
			<c:choose>
				<c:when test="${consultaPublicaCursos.stricto}">
				&nbsp;Visualizar Detalhes do Curso
				</c:when>
				<c:otherwise>
				&nbsp;Visualizar Página do Curso
				</c:otherwise>
			</c:choose>
		</div>	
	
		<br />
		<h:form id="formListagemCursos">
			<input type="hidden" name="nivel"
				value="${consultaPublicaCursos.obj.nivel}" />
			<table class="listagem" style="width: 90%;">
				<caption>Cursos</caption>
				<thead>
					<tr>
						<th>Nome</th>
						<c:if test="${consultaPublicaCursos.stricto}">
						<th>Nível</th>
						</c:if>
						<c:if test="${!consultaPublicaCursos.lato}">
						<th>Sede</th>
						</c:if>
						<c:if test="${consultaPublicaCursos.graduacao}">
							<th>Modalidade</th>
							<th>Coordenador</th>
						</c:if>

						<th></th>

					</tr>
				</thead>

				<tbody>
					<c:set var="unidadeLoop" />
					<c:forEach items="#{cursos}" var="itemCurso" varStatus="loop">
						<c:if test="${itemCurso.unidade.id != unidadeLoop}">
							<c:set var="unidadeLoop" value="${itemCurso.unidade.id}" />
							<tr>
								<td colspan="5" class="subFormulario" style="cursor:pointer" >
								${itemCurso.unidade.sigla} - ${itemCurso.unidade.nome} 

								</td>
							</tr>
						</c:if>

						<tr class="${loop.index%2==0?'linhaPar':'linhaImpar'}">
							
							<td>
							${itemCurso.nome}								
							</td>
							
							<c:if test="${consultaPublicaCursos.stricto}">
								<td>
								${itemCurso.nivelDescricao}
								</td>
							</c:if>
							
							<c:if test="${!consultaPublicaCursos.lato}">
								<td>
								${itemCurso.municipio.nome}
								<c:if test="${empty itemCurso.municipio.nome}">
									Não informada
								</c:if>
								</td>
							</c:if>
							
							<c:if test="${consultaPublicaCursos.graduacao}">
								<td>${itemCurso.modalidadeEducacao.descricao}</td>
								<td>${itemCurso.coordenacaoAtual.servidor.pessoa.nome}</td>
							</c:if>

							<td>
							<c:choose>
								<c:when test="${consultaPublicaCursos.stricto}">
									<h:commandLink title="Visualizar Detalhes do Curso"
									action="#{consultaPublicaCursos.detalhes}">
									<f:param name="id" value="#{itemCurso.id}" />
									<h:graphicImage url="/img/view.gif" />
									</h:commandLink>
								</c:when>
								<c:otherwise>
									<a href="portal.jsf?id=${itemCurso.id}&lc=pt_BR" title="Visualizar Página do Curso">
										<h:graphicImage url="/img/view.gif" />
									</a>
								</c:otherwise>
							</c:choose>	
							<%--
						
							--%>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</h:form>
	</c:if>

	<%@include file="/public/include/voltar.jsp"%>
</f:view>
<%@include file="/public/include/rodape.jsp"%>