<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2 class="tituloPagina">
	<html:link action="/verMenuTecnico">Ensino Técnico</html:link> &gt;
	Atestado de Matrícula > Selecionar Aluno
</h2>

<form action="/ensino/verAtestado?dispatch=listarAlunos" method="post">
<table class="formulario" width="50%">
<caption>Busca por Aluno</caption>
<tbody>
	<tr>
    	<td> <input type="radio" name="teste" property="tipoBusca" value="1" styleClass="noborder" /> </td>
    	<td> Matrícula  </td>
        <td> <input type="text" name="teste" property="matricula" size="30" onfocus="javascript:forms[0].tipoBusca[0].checked = true;"/> </td>
    </tr>
    <tr>
    	<td> <input type="radio" name="teste" property="tipoBusca" value="2" styleClass="noborder" /> </td>
    	<td> Nome  </td>
        <td> <input type="text" property="nome" size="30" onfocus="javascript:forms[0].tipoBusca[1].checked = true;"/> </td>
    </tr>
    <tr>
    	<td> <input type="radio" name="teste" property="tipoBusca" value="3" styleClass="noborder" /> </td>
    	<td> Todos </td>
	</tr>
</tbody>
<tfoot>
	<tr>
		<td colspan="3">
			<input type="hidden" property="buscar" value="true"/>
			<input type="submit" value="Buscar"/>
    	</td>
    </tr>
</tfoot>
</table>
</form>
<c:if test="${empty lista}">
<br><div style="font-style: italic; text-align:center">Nenhum registro encontrado</div>
</c:if>
<c:if test="${not empty lista}">
	<br/>
	<div class="infoAltRem">
	    <html:img page="/img/seta.gif"/>
	    : Selecionar Aluno
	</div>

    <table class="listagem">
    <caption>Alunos Cadastrados</caption>
        <thead>
        <tr>
        	<th>Matrícula</th>
	        <th>Nome</th>
	        <th colspan="2">&nbsp;</th>
		</tr>

        <tbody>
        <c:forEach items="${lista}" var="aluno" varStatus="status">
            <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
                   <td> ${aluno.matricula} </td>
                   <td> ${aluno.pessoa.nome} </td>
                   <td width="15">
                   <a href="/ensino/verAtestado?dispatch=carregarAtestado&id=${ aluno.id }" target="_blank">
                       <img src="<%= request.getContextPath() %>/img/seta.gif" alt="Selecionar este Aluno" title="Selecionar este Aluno" border="0"/>
                   </a>
                   </td>
            </tr>
        </c:forEach>
       </tbody>
    </table>
</c:if>

<br /><br />
<center>
	<a href="/verMenuTecnico.do">Menu do Ensino Técnico</a>
</center>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
