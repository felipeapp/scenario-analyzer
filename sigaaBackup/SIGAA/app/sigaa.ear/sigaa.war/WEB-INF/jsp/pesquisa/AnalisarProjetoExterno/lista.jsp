<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/tags/struts-html" prefix="html"  %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"  %>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<h2>
	<html:link action="/verMenuPesquisa">Pesquisa</html:link> >
	Analise de Projetos de Pesquisa
</h2>
<hr>
<%@include file="/WEB-INF/jsp/include/mensagem.jsp"%>

<html:form action="/pesquisa/analisarProjetoExterno" method="post" focus="nome">

		<table class="formulario" align="center" width="80%">
			<caption class="listagem">Busca por Projeto de Pesquisa </caption>

			<tr>
				<td> <input type="radio" name="tipoBusca" value="grandeArea" class="noborder">Grande Area</input> </td>
				<%-- 
				<td>
					<select>
						<option value=""> Opções </option>
						<c:forEach items="grandeAreas" var="grandeArea">
							<option value="${grandeArea.id}"> ${grandeArea.nome} </option>
						</c:forEach>
					</select>
				</td>
				--%>
			</tr>

			<tr>
				<td> <input type="radio" name="tipoBusca" value="todos" class="noborder"> Todos</input> </td>
				<td> </td>
			</tr>
			
			<tfoot>
				<tr>
					<td colspan=2>
					       <html:button dispatch="list" value="Buscar"/>
					       <html:hidden property="buscar" value="true"/>
					</td>
				</tr>
			</tfoot>
		</table>
</html:form>

<br/><br/>

<%-- 
<html:form action="/pesquisa/analisarProjetoExterno" method="post">
--%>
<c:if test="${not empty lista}">

<table class="listagem" align="center" border="1" style="width:100%">
<caption class="listagem">Projetos de Pesquisa</caption>

<thead>
<tr>
	<th width="20%">Código do Projeto</th>
	<th width="70%">Título</th>        
	<th>Analisar</th>        
</tr>
</thead>

<tbody>

	<c:forEach items="${lista}" var="projeto" varStatus="status">
	<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
		<td>${projeto.codigo}</td>
		<td>${projeto.nome}</td>
		<td align="center"><html:link action="/pesquisa/analisarProjetoExterno?dispatch=popular&obj.id=${projeto.id}"><html:image src="${ctx}/img/pesquisa/avaliar.gif"></html:image> </html:link></td>
	</tr>
	</c:forEach>

</tbody>
			
<tfoot>
	<tr>
		<td colspan="9" align="center">
	   		<html:button dispatch="cancelar" value="Cancelar"/>
		</td>
	</tr>
</tfoot>

</table>

</c:if>

<%-- 
</html:form>
--%>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>