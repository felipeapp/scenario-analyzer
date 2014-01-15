<%@ page contentType="text/html; charset=ISO-8859-1" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/tags/ufrn" prefix="ufrn" %>

<html>
<body>
<f:view>
	<c:set var="ultimosLivros" value="${livroRegistroDiplomas.ultimosLivros}" />
	<c:if test="${empty ultimosLivros}">
		Não há livros cadastrados.
	</c:if>
	<table width="95%" align="center" class="listagem">
		<thead>
			<tr>
				<th align="left" width="10%">Título</th>
				<th align="left">Cursos</th>
				<th align="left" width="30%">Instituição</th>
				<th style="text-align: center;">Aberto</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="item" items="#{ultimosLivros}" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
					<td align="left">${item.titulo}</td>
					<td align="left">${item.descricaoCursos}</td>
					<td align="left">${item.instituicao}</td>
					<td style="text-align: center;"><ufrn:format type="SimNao" valor="${item.ativo}" /></td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</f:view>
</body>
</html>