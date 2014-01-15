<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<h2 class="tituloPagina">
	<ufrn:subSistema />
	&gt; Qualificações
</h2>

<html:form action="/ensino/tecnico/criarQualificacao?dispatch=list" method="post">
<table class="formulario" width="50%">
<caption>Busca por Qualificação</caption>
<tbody>
	<tr>
		<td> <html:radio property="tipoBusca" value="1" styleClass="noborder" styleId="checkCurso"/> </td>
    	<td><label for="checkCurso">Curso</label></td>
    	<td>
    	<html:select property="obj.cursoTecnico.id" onfocus="javascript:forms[0].tipoBusca[0].checked = true;">
    	<html:options collection="cursos" property="id" labelProperty="codigoNome"/>
    	</html:select>
    	</td>
    </tr>
    <tr>
    	<td> <html:radio property="tipoBusca" value="2" styleClass="noborder" styleId="checkDescricao"/> </td>
    	<td><label for="checkDescricao">Descrição</label></td>
        <td> <html:text property="obj.descricao" size="30" onfocus="javascript:forms[0].tipoBusca[1].checked = true;"></html:text> </td>
    </tr>
    <tr>
    	<td> <html:radio property="tipoBusca" value="3" styleClass="noborder" styleId="checkTodos" /> </td>
    	<td><label for="checkTodos">Todos</label> </td>
	</tr>
</tbody>
<tfoot>
	<tr>
		<td colspan="3">
			<html:hidden property="buscar" value="true"/>
			<html:submit>Buscar</html:submit>
    	</td>
    </tr>
</tfoot>
</table>
</html:form>

	<br/>
	<ufrn:table collection="${lista}"
	properties="descricao, cursoTecnico.nome"
	headers="Qualificação, Curso Técnico"
	title="Qualificações" crud="false"
	links="src='${ctx}/img/alterar.gif',?dispatch=popular&id={id};
		   src='${ctx}/img/delete.gif',?dispatch=prepararRemover&id={id};
		   src='${ctx}/img/view.gif',?dispatch=view&id={id}"
	linksRoles="<%=new int[][]{
				   new int[] {SigaaPapeis.GESTOR_TECNICO},
				   new int[] {SigaaPapeis.GESTOR_TECNICO},
				   null
		   } %>"/>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
