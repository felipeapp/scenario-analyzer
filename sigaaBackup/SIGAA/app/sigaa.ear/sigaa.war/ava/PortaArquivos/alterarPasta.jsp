
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib uri="/tags/struts-html" prefix="html"  %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<f:view>
	<h:outputText value="#{ arquivoUsuario.create }"/>
	<c:set var="pasta" value="${ arquivoUsuario.pastaSelecionada }"/>
	<html:form action="cadastrarPasta" styleId="form-alterar-pasta">
	<table width="100%" class="form-porta-arquivos">

		<tr>
			<th>Dentro de:</th>
			<td>
				<select name="pasta.pai.id" id="alterar-pasta-pai">
				<c:forEach var="item" items="${ arquivoUsuario.pastasUsuarioCombo }">
					<option value="${ item.value }" ${ item.value == pasta.pai.id ? 'selected="selected"' : '' }>${ item.label }</option>
				</c:forEach>		
				</select>
			</td>
		</tr>
			
		<tr>
			<th>Nome:</th>
			<td>
				<input type="text" name="pasta.nome" id="alterar-pasta-nome" size="50" value="${ pasta.nome }"/>
				<input type="hidden" name="pasta.id" id="alterar-pasta-id" value="${ pasta.id }"/>
			</td>
		</tr>

	</table>
	</html:form>
				
</f:view>
