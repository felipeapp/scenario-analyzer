<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<%@page import="br.ufrn.comum.dominio.Papel"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<h2><ufrn:steps /> &gt; Consulta de Estrutura Curricular Técnica </h2>

<html:form action="/ensino/tecnico/estruturaCurricular/wizard?dispatch=list"  method="post" >
	<input type="hidden" name="page" value="${param.page + 0}"/>
	<table class="formulario" width="400">
	<caption>Busca por Estrutura Curricular</caption>
	<tbody>

		<tr>
		<td>
		<html:radio property="tipoBusca" value="1" styleId="checkCurso"/>
		</td>
    	<td><label for="checkCurso">Curso</label></td>
    	<td>
    	<html:select property="cursoTecnico.id" onfocus="marcaCheckBox('checkCurso')">
		<html:option value="">-- SELECIONE --</html:option>
		<html:options collection="cursos" property="id" labelProperty="codigoNome"/>
		</html:select>
    	</td>
		</tr>

		<tr>
		<td>
		<html:radio property="tipoBusca" value="2" styleId="checkTodos"/>
		</td>
    	<td><label for="checkTodos">Todos</label></td>
    	<td>
    	</td>
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
<br/>
<div class="infoAltRem">
<!--
<ufrn:checkRole papel="<%= SigaaPapeis.GESTOR_TECNICO%>">
<html:img page="/img/alterar.gif" style="overflow: visible;"/>: Alterar Estruturas Curriculares
<html:img page="/img/delete.gif" style="overflow: visible;"/>: Remover Estruturas Curriculares <Br>
</ufrn:checkRole>
-->
<html:img page="/img/view.gif" style="overflow: visible;"/>: Visualizar Estrutura Curricular
</div>
</c:if>
<ufrn:table collection="${lista}" properties="cursoTecnico.codigoNome, codigo, anoPeriodoEntradaVigor, cargaHoraria, ativa"
	headers="Curso, Cód., Ano-Per. Ent. Vigor, C.H., Ativa"
	title="Estruturas Curriculares" pageSize="15"
	links="src='${ctx}/img/view.gif',?dispatch=visualizar&id={id}&page=null,Visualizar Estrutura Curricular;"
	
	linksRoles="<%= new int[][] {
					null,
					} %>" />

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>