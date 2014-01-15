<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<h2 class="tituloPagina">
<html:link action="/ensino/tecnico/modulo/wizard?dispatch=cancelar">
<ufrn:subSistema semLink="true"/>
</html:link>
&gt;
<ufrn:subSistema teste="tecnico"> Módulos</ufrn:subSistema>
<ufrn:subSistema teste="not tecnico"> Séries</ufrn:subSistema>

</h2>

<html:form action="/ensino/tecnico/modulo/wizard?dispatch=list"  method="post" focus="obj.descricao" >
	<input type="hidden" name="page" value="${param.page + 0}"/>
	<table class="formulario" width="400">
	<caption>Busca por Módulo</caption>
	<tbody>
		<tr>
		<td><html:radio property="tipoBusca" value="1" styleId="checkCod"/></td>
    	<td><label for="checkCod">Código</label></td>
    	<td>
    	<html:text property="obj.codigo" size="8" onkeyup="CAPS(this)" maxlength="7"
    		onfocus="javascript:forms[0].tipoBusca[0].checked = true;" />
    	</td>
		</tr>

		<tr>
		<td><html:radio property="tipoBusca" value="2" styleId="checkNome"/></td>
    	<td><label for="checkNome">Nome</label></td>
    	<td>
    	<html:text property="obj.descricao" size="50" onkeyup="CAPS(this)"
    		onfocus="javascript:forms[0].tipoBusca[1].checked = true;" />
    	</td>
		</tr>

		<tr>
		<td><html:radio property="tipoBusca" value="3" styleClass="noborder" /></td>
    	<td>Curso</td>
    	<td>
    	<html:select property="cursoTecnico.id" onfocus="javascript:forms[0].tipoBusca[2].checked = true;">
		<html:option value="">-- SELECIONE --</html:option>
		<html:options collection="cursos" property="id" labelProperty="codigoNome"/>
		</html:select>
    	</td>
		</tr>

		<tr>
		<td><html:radio property="tipoBusca" value="4" styleId="checkTodos"/></td>
    	<td><label for="checkTodos">Todos</label></td>
    	<td></td>
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
<c:if test="${not empty lista}">
<br>
<div class="infoAltRem">
<ufrn:checkRole papel="<%= SigaaPapeis.GESTOR_TECNICO%>">
<html:img page="/img/alterar.gif" style="overflow: visible;"/>
 : Alterar Módulo
<html:img page="/img/delete.gif" style="overflow: visible;"/>
 : Remover Módulo
</ufrn:checkRole>
<html:img page="/img/view.gif" style="overflow: visible;"/>
 : Visualizar Módulo
</div>
</c:if>
<ufrn:table collection="${lista}" properties="codigo, descricao, cargaHoraria" headers="Cód., Nome, C.H."
	title="Módulos"
	links="src='${ctx}/img/view.gif',?dispatch=view&id={id};
			src='${ctx}/img/alterar.gif',?dispatch=edit&id={id}&page=null;
			src='${ctx}/img/delete.gif',?dispatch=remove&id={id}&page=null;"
	crudRoles="<%=new int[]{SigaaPapeis.GESTOR_TECNICO} %>"/>

<br /><br />

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>
