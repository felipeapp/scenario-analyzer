<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>

<h2 class="tituloPagina">
	<html:link action="ensino/tecnico/cadastroCursoTecnico.do?dispatch=cancelar">
		<ufrn:subSistema semLink="true"/>
	</html:link>
	&gt; Cursos
</h2>

<html:form action="ensino/tecnico/cadastroCursoTecnico?dispatch=list" method="post">
<html:hidden property="buscar" value="true"/>
<c:if test="${param.page != 'null'}">
	<input type="hidden" name="page" value="${param.page + 0}"/>
</c:if>
<table class="formulario" width="50%">
<caption>Busca por Curso</caption>
<tbody>
	<tr>
        <td> <html:radio property="tipoBusca" value="1" styleClass="noborder" styleId="buscaCodigo"/> </td>
        <td> <label for="buscaCodigo">Código na ${ configSistema['siglaInstituicao'] }</label> </td>
        <td> <html:text property="obj.codigo" size="10" onkeyup="CAPS(this)" onfocus="javascript:forms[0].tipoBusca[0].checked = true;"/> </td>
    </tr>
    <tr>
    	<td> <html:radio property="tipoBusca" value="2" styleClass="noborder" styleId="buscaNome"/> </td>
    	<td> <label for="buscaNome">Nome</label> </td>
    	<td> <html:text property="obj.nome" size="30" onkeyup="CAPS(this)" onfocus="javascript:forms[0].tipoBusca[1].checked = true;"/> </td>
    </tr>
    <tr>
    	<td><html:radio property="tipoBusca" value="3" styleClass="noborder" styleId="buscaTodos"/></td>
    	<td> <label for="buscaTodos">Todos</label> </td>
    </tr>
</tbody>
<tfoot>
	<tr><td colspan="3"><html:submit><fmt:message key="botao.buscar" /></html:submit></td></tr>
</tfoot>
</table>
</html:form>

<br>
	<ufrn:table collection="${lista}" properties="codigo, nome, ativo" headers="Cód. UFRN, Nome, Ativo"
	title="Cursos" crud="true" pageSize="10" crudRoles="<%=new int[] {SigaaPapeis.GESTOR_TECNICO} %>" />

<br/><br/>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>
