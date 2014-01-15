<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
 
<%@ taglib uri="/tags/struts-html" prefix="html"  %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"  %> 
<script type="text/javascript">
<!--
	function selectDiscente(nome, id, matricula) {
		window.opener.document.getElementById('inputNomeDiscente').value=nome;
		window.opener.document.getElementById('inputDiscenteId').value=id;
		window.opener.document.getElementById('inputDiscenteMatricula').value=matricula;
		javascript:window.close();
	}
//-->
</script>
<br/>
<h2>
	<fmt:message key="titulo.buscar">
		<fmt:param value="Discente"/>
	</fmt:message>
</h2>
<hr>
<%@include file="/WEB-INF/jsp/include/mensagem.jsp"%>

<html:form action="/geral/buscarNomeDiscente" method="post" focus="nome">
	<div class="areaDeDados">
		<h2>Busca por Discente</h2>
		<div class="dados">
            <div class="head"><input type="radio" name="tipoBusca" value="1" class="noborder"> Matrícula</input></div>
            <div class="texto"><html:text property="matricula" size="30" value="" onfocus="javascript:forms[0].tipoBusca[0].checked = true;"></html:text></div>
            <br/>
            <div class="head"><input type="radio" name="tipoBusca" value="2" class="noborder"> Nome</input></div>
            <div class="texto"><html:text property="nome" size="30" value="" onfocus="javascript:forms[0].tipoBusca[1].checked = true;"></html:text></div>
            <br/>
            <div class="botoes">
                <html:submit><fmt:message key="botao.buscar" /></html:submit>
            </div>
		</div>
	</div>
</html:form>
<c:if test="${not empty discentes}">
	<br>
	<br>
	<div class="areaDeDados lista">
	    <h2>Discentes Encontrados</h2>
	    <table>
	        <thead>
	        <th>Matrícula</th>
	        <th>Nome</th>
	        <th>&nbsp;</th>
	        <tbody>
	
			<c:forEach items="${discentes}" var="discente">
				<tr>
					<td> ${discente.matricula} </td>				
					<td> ${discente.pessoa.nome} </td>
					<td width="15">
						<html:img 	page="/img/seta.gif" alt="Selecionar Discente" title="Selecionar Discente" 
									border="0" onmouseover="this.style.cursor='pointer'" onclick="selectDiscente('${discente.pessoa.nome}','${discente.id}','${discente.matricula}');" />
					</td>
				</tr>
			</c:forEach>
			</tbody>  
	    </table>
	</div>
</c:if>
