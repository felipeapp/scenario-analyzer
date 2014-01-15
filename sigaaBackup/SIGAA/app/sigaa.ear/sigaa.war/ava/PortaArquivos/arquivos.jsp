<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/format" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<f:view>
<h:outputText value="#{ arquivoUsuario.create }"/>
<?xml?>
<dataset>
<c:forEach var="arquivo" items="${ arquivoUsuario.arquivosDiretorio }" varStatus="loop">
	<arquivo>
		<id>${ arquivo.id }</id>
		<nome>${ arquivo.nome }</nome>
		<tamanho>${ arquivo.tamanho }</tamanho>
		<data><fmt:formatDate pattern="dd/MM/yyyy HH:mm" value="${ arquivo.data }"/></data>
	</arquivo>
</c:forEach>
</dataset>
<%--
	

<table cellpadding="3" cellspacing="0" width="100%">
<thead>
<tr><th></th><th>Nome</th><th>Tamanho</th><th>Data</th><th></th><th></th><th></th></tr>
</thead>

<tr id="linha_${ arquivo.id }" class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
	<td width="2%"><img src="${ pageContext.request.contextPath }/img/prodocente/document.png"/></td>
	<td width="56%"><a href="${pageContext.request.contextPath}/verProducao?idProducao=${ arquivo.idArquivo }" target="_blank" id="nome_${ arquivo.id }">${ arquivo.nome }</a></td>
	<td width="15%" align="right">${ arquivo.tamanho }</td>
	<td width="23%" align="center" nowrap="nowrap"></td>
	<td width="2%">
	<h:form>
	<input type="hidden" name="id" value="${ arquivo.id }"/>
	<h:commandButton action="#{ arquivoUsuario.associarATopico }" image="/img/porta_arquivos/page_link.png" alt="Associar a um tópico" title="Associar a um tópico"/>
	</h:form>
	<td width="2%"><img src="${ pageContext.request.contextPath }/img/porta_arquivos/page_edit.png" id="renomear_${ arquivo.id }" title="Renomear" alt="Renomear" style="cursor: pointer;"/></td>
	<td width="2%"><img src="${ pageContext.request.contextPath }/img/porta_arquivos/page_delete.png" id="apagar_${ arquivo.id }" title="Apagar" alt="Apagar" style="cursor: pointer;" onclick="remover(this)"/></td>
</tr>
</c:forEach>
</table>
--%>
</f:view>