<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%@ taglib uri="/tags/struts-html" prefix="html"  %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"  %>

<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<h2 class="tituloPagina">
	<fmt:message key="titulo.listar">
		<fmt:param value="Alunos da Turma de Entrada de Técnico"/>
	</fmt:message>
</h2>

<c:if test="${not empty tecDiscentes}">
	<br>
	<div class="areaDeDados lista">
	    <h2>Alunos da Turma de Entrada Selecionada</h2>
	    <table>
	        <thead>
	        <th>Matrícula</th>
	        <th>Nome</th>

	        <tbody>

			<c:forEach items="${tecDiscentes}" var="tecDiscente">
				<tr>
					<td> ${tecDiscente.matricula} </td>
                    <td> ${tecDiscente.nome} </td>
				</tr>
			</c:forEach>
			</tbody>
	    </table>
	</div>
	<br>
	<center>
	<div class="botoes">
	<c:if test="${apenasVer != 'sim'}">
		<html:form action="/ensino/tecnico/carregarTecTurmaEntrada" method="post">
	    	<html:hidden name="tecTurmaEntrada" property="id"/>
	        	<html:submit><fmt:message key="botao.confirmar" /></html:submit>
	    </html:form>
	</c:if>
		<html:button  property="" onclick="javascript:history.go(-1);">Selecionar outra Turma de Entrada</html:button>
	</div>
	</center>
</c:if>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
