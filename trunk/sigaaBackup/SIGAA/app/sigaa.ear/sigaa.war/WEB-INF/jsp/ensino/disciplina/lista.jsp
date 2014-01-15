<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>

<h2 class="tituloPagina">
	<html:link action="/ensino/cadastroDisciplina?dispatch=cancelar">
	<ufrn:subSistema semLink="true"/>
	</html:link>
	<ufrn:subSistema teste="infantil">
	&gt; Nível Infantil
	</ufrn:subSistema>
	<ufrn:subSistema teste="not infantil">
	&gt; Disciplinas
	</ufrn:subSistema>
</h2>

<html:form action="/ensino/cadastroDisciplina" method="post">
<table class="formulario" width="50%">
<caption>Busca por Disciplina</caption>
<tbody>
	<tr>
		<td> <html:radio property="tipoBusca" value="1" styleClass="noborder" styleId="buscaCodigo" /> </td>
    	<td> <label for="buscaCodigo">Código:</label> </td>
    	<td> <html:text property="obj.codigo" size="30" maxlength="10" onfocus="javascript:forms[0].tipoBusca[0].checked = true;" onkeyup="CAPS(this)"></html:text></td>
    </tr>
    <tr>
    	<td> <html:radio property="tipoBusca" value="2" styleClass="noborder" styleId="buscaNome"/> </td>
    	<td> <label for="buscaNome">Nome:</label> </td>
        <td> <html:text property="obj.nome" size="30" maxlength="255" onfocus="javascript:forms[0].tipoBusca[1].checked = true;" onkeyup="CAPS(this)"></html:text> </td>
    </tr>
    <c:if test="${ not empty  cursos}">
    <tr>
    	<td> <html:radio property="tipoBusca" value="3" styleClass="noborder" styleId="buscaCurso"/> </td>
    	<td> <label for="buscaCurso">Curso:</label> </td>
        <td>
        <html:select property="curso.id" onfocus="javascript:forms[0].tipoBusca[2].checked = true;">
        	<html:options collection="cursos" property="id" labelProperty="nomeCompleto"/>
        </html:select>
        </td>
    </tr>
    </c:if>
    <tr>
    	<td> <html:radio property="tipoBusca" value="4" styleClass="noborder" styleId="buscaTodos" /> </td>
    	<td> <label for="buscaTodos">Todos</label></td>
	</tr>
</tbody>
<tfoot>
	<tr>
		<td colspan="3">
			<html:button dispatch="list" value="Buscar" />
			<html:hidden property="buscar" value="true"/> 
			<html:button dispatch="cancelar" value="Cancelar" cancelar="true"/>
    	</td>
    </tr>
</tfoot>
</table>
</html:form>
<br>
<ufrn:table collection="${lista}" pageSize="15" properties="detalhes.codigo, nome, detalhes.chTotal, ativo" headers="Código, Nome, CH. Total, Ativo"
title="Disciplinas" crud="true"
propertiesLinks="?dispatch=view&id={id}&page=0;"
crudRoles="<%=new int[] {SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.GESTOR_INFANTIL, SigaaPapeis.GESTOR_MEDIO, SigaaPapeis.GESTOR_LATO} %>" />

<br /><br />

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
