<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

${ interessadoBolsa.verQualificacao }

<table class="formulario" width="100%" style="border: 0;">
	<caption> ${interessadoBolsa.interessadoBolsa.discente.nome} </caption>
	<tr>
		<th>Telefone:</th>
		<td>${interessadoBolsa.interessadoBolsa.dados.telefone}</td>
	</tr>
	<tr>
		<th>Email:</th>
		<td>${interessadoBolsa.interessadoBolsa.dados.email}</td>
	</tr>					
	<tr>
		<th>Lattes:</th>
		<td>${interessadoBolsa.interessadoBolsa.dados.linkLattes}</td>
	</tr>
	<tr>
		<th>Qualificações:</th>
		<td>${interessadoBolsa.interessadoBolsa.dados.qualificacoes}</td>
	</tr>							
</table>