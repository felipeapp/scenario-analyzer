<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"  %>
<%@ taglib uri="/tags/sigaaFunctions" prefix="sf" %>
<jsp:useBean id="votoBean" class="br.ufrn.sigaa.eleicao.jsf.VotoMBean" scope="request"/>
<jsp:setProperty name="votoBean" property="chapa" value="${param.chapa}"/>
<jsp:setProperty name="votoBean" property="idEleicao" value="${param.idEleicao}"/>


<c:set var="ctx" value="<%= request.getContextPath() %>"/>
<c:set var="candidatoLoop" value="${votoBean.candidato}"/>
<c:set var="chapaLoop" value="${votoBean.chapa}"/>

<c:if test="${chapaLoop == -1}">
	<c:set var="limpar" value="true" />
</c:if>

<c:if test="${candidatoLoop != null}">
<table class="subFormulario" width="100%">
	<caption>Chapa ${candidatoLoop.chapa}</caption>
	
	<tr>
		<td colspan="2" align="center">
		<img src="/sigaa/verFoto?idFoto=${candidatoLoop.idFoto}&key=${ sf:generateArquivoKey(candidatoLoop.idFoto) }" border="0" width="100" height="107">
		</td>
	</tr>
	
	<tr>
		<th>Chapa:</th>
		<td>${candidatoLoop.chapa}</td>
	</tr>

	<tr>
		<th>Descrição:</th>
		<td>${candidatoLoop.descricao}</td>
	</tr>
</table>
</c:if>

<c:if test="${candidatoLoop == null}">

<table class="subFormulario" width="100%">
	<caption>Escolha seu candidato</caption>
	
	<tr>
		<td colspan="2" align="center">
			<img src="${ctx}/img/no_picture.png" width="90" height="120"/>
		</td>
	</tr>

	<c:if test="${limpar}">
	<tr>
		<td colspan="2" align="center">
			ESCOLHA SEU CANDIDATO!
		</td>
	</tr>
	</c:if>

	
	<c:if test="${not limpar}">
	<tr>
		<th>Chapa:</th>
		<td>
			candidato inexistente! <font color="red"><b>(VOTO NULO)</b></font>
		</td>
	</tr>

	</c:if>
	
</table>

</c:if>