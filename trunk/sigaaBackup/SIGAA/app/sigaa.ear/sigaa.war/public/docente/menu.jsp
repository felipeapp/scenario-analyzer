<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<h:outputText value="#{portalPublicoDocente.iniciar}" />
<c:set var="portal" value="${portalPublicoDocente}" />
<c:set var="docente" value="${portalPublicoDocente.docente}" />

<div id="left" class="barra_professor">
	<div class="foto_professor">
		<c:if test="${usuario.idFoto != null}">
			<img src="${ctx}/verFoto?idFoto=<h:outputText value="#{usuario.idFoto}"/>&key=${ sf:generateArquivoKey(usuario.idFoto) }" height="120"/>
		</c:if>
		<c:if test="${usuario.idFoto == null}">
			<img src="${ctx}/img/no_picture.png" height="120"/>
		</c:if>
	</div>
	<h3>${docente.nome}</h3>
	<h3 style="font-weight: normal; font-size: 0.8em;>"> ${docente.unidade.nome} </h3>
	<ul class="menu_professor">
		<li class="perfil_pessoal"><a href="${ctx}/public/docente/portal.jsf">Perfil Pessoal</a></li>
		<li class="publicacoes"><a href="#">Produção Intelectual</a></li>
		<li class="disciplinas_ministradas"><a href="#">Disciplinas Ministradas</a></li>
		<li class="projetos_pesquisa"><a href="#">Projetos de Pesquisa</a></li>
		<li class="projetos_extensao"><a href="#">Projetos de Extens&atilde;o</a></li>
		<li class="projetos_monitoria"><a href="#">Projetos de Monitoria</a></li>
	</ul>
</div>

<div id="center">