<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
 
<%@ taglib uri="/tags/struts-html" prefix="html"  %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"  %> 
<%@ taglib uri="/tags/sigaa" prefix="sigaa"  %> 

<script type="text/javascript">
<!--
	function selectServidor(nome, id) {
		window.opener.document.getElementById('inputNome').value=nome;
		window.opener.document.getElementById('inputServidorId').value=id;
		javascript:window.close();
	}
//-->

<!--
    
	function selectServidorLato(nome, id, retornoNome, retornoId) {
		window.opener.document.getElementById(retornoNome).value=nome;
		window.opener.document.getElementById(retornoId).value=id;
		javascript:window.close();
	}
//-->
</script>
<br/>
<h2>
	<fmt:message key="titulo.buscar">
		<fmt:param value="Servidor"/>
	</fmt:message>
</h2>
<hr>
<%@include file="/WEB-INF/jsp/include/mensagem.jsp"%>

<html:form action="/geral/buscarNomeServidor" method="post" focus="nome">
	<div class="areaDeDados">
		<h2>Busca por Servidor</h2>
		<div class="dados">

<c:if test="${not empty param.localRetorno}">
		<c:set var="localRetorno" value="${param.localRetorno}" scope="session"/>
</c:if>

            <div class="head"><input type="radio" name="tipoBusca" value="1" class="noborder">Nome</input></div>
            <div class="texto"><html:text property="nome" size="30" value="" onfocus="javascript:forms[0].tipoBusca[0].checked = true;"></html:text></div>
            <br/>
            <div class="head"><input type="radio" name="tipoBusca" value="2" class="noborder">Matrícula</input></div>
            <div class="texto"><html:text property="matricula" size="30" value="" onfocus="javascript:forms[0].tipoBusca[1].checked = true;"></html:text></div>
            <br/>
            <div class="botoes">
                <html:submit><fmt:message key="botao.buscar" /></html:submit>
            </div>
		</div>
	</div>
</html:form>
<c:if test="${not empty servidores}">
	<br>
	<br>
	<div class="areaDeDados lista">
	    <h2>Servidores Encontrados</h2>
	    <table>
	        <thead>
	        <th>Matrícula</th>
	        <th>Nome</th>
	        <th>&nbsp;</th>
	        <tbody>
	
			<c:forEach items="${servidores}" var="servidor">
				<tr>
					<td> ${servidor.siape} </td>
					<td> ${servidor.pessoa.nome} </td>
					<td width="15">	
					 <c:choose>	
						<c:when test = '${not empty localRetorno}'>
						<html:img 	page="/img/seta.gif" alt="Selecionar Servidor" title="Selecionar Servidor" 
									border="0" onmouseover="this.style.cursor='pointer'" onclick="selectServidorLato('${servidor.pessoa.nome}','${servidor.id}' , 'inputNome${localRetorno}', 'inputServidorId${localRetorno}' );" />
					    </c:when>
						<c:otherwise>
						<html:img 	page="/img/seta.gif" alt="Selecionar Servidor " title="Selecionar Servidor" 
									border="0" onmouseover="this.style.cursor='pointer'" onclick="selectServidor('${servidor.pessoa.nome}','${servidor.id}');" />
						</c:otherwise>
 					</c:choose>
					</td>

				</tr>
			</c:forEach>
			</tbody>  
	    </table>
	</div>
	<%--
 <sigaa:paginacao action="/geral/buscarNomeServidor" denominacao="Servidores" genero="M">
	<input type="hidden" value="${nome}" name="nome" />
	<input type="hidden" value="${matricula}" name="matricula" />
	<input type="hidden" value="${tipoBusca}" name="tipoBusca" />
	<input type="hidden" name="acao" />
 </sigaa:paginacao>	    
	 --%>
</c:if>
