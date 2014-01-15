<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/tags/ufrn" prefix="ufrn" %>
<f:view>


<%@include file="/public/stricto/portal/cabecalho.jsp" %>

<link rel="stylesheet" media="all" href="/shared/css/ufrn.css" type="text/css" />
<link href="/sigaa/public/css/geral.css" rel="stylesheet" type="text/css" />
<link rel="stylesheet" href="/sigaa/public/css/public.css" rel="stylesheet" type="text/css" />
<link href="/sigaa/public/css/portalPublico.css" rel="stylesheet" type="text/css" />

<h:form id="formMenuPortalPublicoPrograma">
<div id="left" class="barra_portalPublico">
	<h3>${docente.nome}</h3>
	<h3 style="font-weight: normal; font-size: 0.8em;>"> ${docente.unidade.nome} </h3>
	<ul class="menu_portalPublico">
		<li class="cursos"><h:commandLink value="Cursos" action="#{consultaPublicaCursos.exibirCursosDoPrograma}" /></li>
		<li class="disciplinas"><a href="#">Oferta de Disciplinas</a></li>
		<!-- <li class="turmas"><a href="#">Turmas</a></li> -->
		<li class="equipeDocente"><a href="#">Equipe Docente</a></li>
		<li class="linhasPesquisa"><a href="#">Linhas de Pesquisa</a></li>						
	</ul>
	<a class="home-link" href="${ctx}/public/" alt="Página inicial" title="Página inicial"> Ir ao Menu Principal </a>
</div>
<div id="center">


<input type="hidden" name="idPrograma" value="${consultaPublicaCursos.programa.id}" />
<div id="id-titulo">
	<h3>${consultaPublicaCursos.programa.siglaAcademica} - ${fn:toLowerCase(consultaPublicaCursos.programa.nome)} </h3>
	<p class="departamento">${consultaPublicaCursos.programa.siglaAcademica} - ${consultaPublicaCursos.programa.nome}</p>
</div>



<%-- PÁGINA INICIAL --%>
<c:if test="${consultaPublicaCursos.clicouEmPaginaInicial}">
		<%-- Cursos --%>
		<div id="programa-cursos" class="programa">
			<h4>Cursos</h4>
			<ul>
				<c:forEach items="#{consultaPublicaCursos.cursos}" var="curso">
					<li>
						<h:commandLink value="#{curso.descricao}" action="#{consultaPublicaCursos.detalharCurso}">
							<f:param id="idCurso" value="#{curso.id}" />
						</h:commandLink>
					</li>
				</c:forEach>
			</ul>
			<br />
		</div>
		<%-- Fim Cursos --%>
		
		<%-- Linhas De Pesquisa --%>
		<c:set var="linhasDePesquisa" value="${consultaPublicaCursos.linhasDePesquisa}" />
		<c:if test="${not empty linhasDePesquisa}">
		<div id="programa-linhasPesquisa" class="programa">
			<h4>Linhas de Pesquisa</h4>
			<ul>
				<c:forEach items="${linhasDePesquisa}" var="linhaPesquisa">
					<li>${linhaPesquisa.denominacao}</li>
				</c:forEach>
			</ul>
			<br />
		</div>
		</c:if>
		<%-- Fim Linhas De Pesquisa --%>
</c:if>
<%-- FIM PÁGINA INICIAL --%>

<%-- CURSOS --%>
<c:if test="${consultaPublicaCursos.clicouEmCursos}">
		<div id="programa-cursos" class="programa">
			<h4>Cursos</h4>
			<ul>
				<c:forEach items="#{consultaPublicaCursos.cursos}" var="curso">
					<li>
						<h:commandLink value="#{curso.descricao}" action="#{consultaPublicaCursos.detalhes}">
							<f:param id="idCurso" value="#{curso.id}" />
						</h:commandLink>
					</li>
				</c:forEach>
			</ul>
			<br />
		</div>
</c:if>
<%-- FIM CURSOS --%>

<%-- DETALHAR CURSO --%>
<c:if test="${consultaPublicaCursos.clicouEmDetalharCurso}">
		<div id="programa-cursos" class="programa">
			<h4>Cursos >> consultaPublicaCursos.curso.descricao </h4>
			<jsp:include page="../../curso/resumo_curso.jsp" />
			<br />
		</div>
</c:if>
<%-- FIM DETALHAR CURSO --%>

</h:form>
</f:view>
<%@include file="/public/include/rodape.jsp" %>
