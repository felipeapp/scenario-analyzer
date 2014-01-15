
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib uri="/tags/struts-html" prefix="html"  %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<f:view>
	<h:outputText value="#{ arquivoUsuario.create }"/>

	<html:form action="cadastrarPasta" styleId="form-remover-pasta">
	<table width="100%" class="form-porta-arquivos">

		<tr>
			<th>Remover:</th>
			<td>
				<select name="pasta.pai.id" id="remover-pasta-pai">
				<c:forEach var="item" items="${ arquivoUsuario.pastasUsuarioSemRaizCombo }">
					<option value="${ item.value }">${ item.label }</option>
				</c:forEach>
				</select>
			</td>
		</tr>

	</table>
	</html:form>

</f:view>

