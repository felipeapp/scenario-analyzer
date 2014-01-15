<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
table.noticia {
	background: #FFF;
	width: 100%;
	height: 100%;
}
</style>
<f:view>
<h:outputText value="#{ noticiaPortal.create }"/>
<c:set var="noticia" value="${ noticiaPortal.noticiaRequest }"/>
<table class="noticia">
<tr><td valign="top">
<br />
<h1>${ noticia.titulo }</h1>
<br />
<p><ufrn:format name="noticia" property="descricao" type="texto"/> </p>
<br />
<p><fmt:formatDate value="${ noticia.criadoEm }" pattern="dd/MM/yyyy HH:mm"/></p>
</td></tr>
</table>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>