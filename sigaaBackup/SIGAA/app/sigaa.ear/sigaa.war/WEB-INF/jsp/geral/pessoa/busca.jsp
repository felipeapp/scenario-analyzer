<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
 
<%@ taglib uri="/tags/struts-html" prefix="html"  %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"  %> 
<script type="text/javascript">
<!--
	function selectPessoa(nome, id) {
		window.opener.document.getElementById('inputNome').value=nome;
		window.opener.document.getElementById('inputPessoaId').value=id;
		javascript:window.close();
	}
//-->
</script>
<br/>
<h2>
	<fmt:message key="titulo.buscar">
		<fmt:param value="Pessoa"/>
	</fmt:message>
</h2>
<hr>
<%@include file="/WEB-INF/jsp/include/mensagem.jsp"%>

<html:form action="/geral/buscarNomePessoa" method="post" focus="nome">
	<div class="areaDeDados">
		<h2>Busca por Pessoa</h2>
		<div class="dados">
            <div class="head"><input type="radio" name="tipoBusca" value="1" class="noborder"> Nome</input></div>
            <div class="texto"><html:text property="nome" size="30" value="" onfocus="javascript:forms[0].tipoBusca.checked = true;"></html:text></div>
            <br/>
            <div class="botoes">
                <html:submit><fmt:message key="botao.buscar" /></html:submit>
            </div>
		</div>
	</div>
</html:form>
<c:if test="${not empty pessoas}">
	<br>
	<br>
	<div class="areaDeDados lista">
	    <h2>Pessoas Encontradas</h2>
	    <table>
	        <thead>
	        <th>Nome</th>
	        <th>&nbsp;</th>
	        <tbody>
	
			<c:forEach items="${pessoas}" var="pessoa">
				<tr>
					<td> ${pessoa.nome} </td>
					<td width="15">
						<html:img 	page="/img/seta.gif" alt="Selecionar Pessoa" title="Selecionar Pessoa" 
									border="0" onmouseover="this.style.cursor='pointer'" onclick="selectPessoa('${pessoa.nome}','${pessoa.id}');" />
					</td>
				</tr>
			</c:forEach>
			</tbody>  
	    </table>
	</div>
</c:if>
