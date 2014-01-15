<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<h2 class="tituloPagina">
	<html:link action="ensino/ead/cadastroVeiculacoesCurso?dispatch=cancelar">
		<ufrn:subSistema semLink="true"/>
	</html:link>
	&gt; Busca de Curso
</h2>

<html:form action="ensino/ead/cadastroVeiculacoesCurso?dispatch=list" method="post">
<html:hidden property="buscar" value="true"/>
<table class="formulario" width="50%">
<caption>Busca por Curso</caption>
<tbody>
	<tr>
        <td> <html:radio property="tipoBusca" value="1" styleClass="noborder" styleId="buscaCodigo"/> </td>
        <td> <label for="buscaCodigo">Código na ${ configSistema['siglaInstituicao'] }</label> </td>
        <td> <html:text property="obj.curso.codigo" size="30" onkeyup="CAPS(this)" onfocus="javascript:forms[0].tipoBusca[0].checked = true;"/> </td>
    </tr>
    <tr>
    	<td> <html:radio property="tipoBusca" value="2" styleClass="noborder" styleId="buscaNome"/> </td>
    	<td> <label for="buscaNome">Nome</label> </td>
    	<td> <html:text property="obj.curso.descricao" size="30" onkeyup="CAPS(this)" onfocus="javascript:forms[0].tipoBusca[1].checked = true;"/> </td>
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

<c:if test="${not empty lista}">
<br>
<div class="infoAltRem">
	<html:img page="/img/seta.gif"/> : Selecionar Curso
</div>
	<ufrn:table collection="${lista}" properties="codigoInep, codigo, nome" headers="Cód. INEP, Cód. UFRN, Nome"
	title="Cursos" crud="false" links="src='${ctx}/img/seta.gif',?id={id}&dispatch=carregaVeiculacoes"
	linksRoles="<%=new int[][] {new int[] {SigaaPapeis.GESTOR_TECNICO}} %>"/>
</c:if>

<br/><br/>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>
