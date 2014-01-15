<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp" %>

<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<h2 class="tituloPagina">
		<ufrn:subSistema />
	 &gt; Turmas de Entrada
</h2>

	<html:form action="/ensino/tecnico/cadastroTurmaEntradaTecnico?dispatch=list" method="post">
	
	<table class="formulario" width="50%">
		<caption>Busca por Turmas de Entrada</caption>
		<tbody>
			<tr>
				<td> <html:radio property="tipoBusca" value="1" styleId="checkAno" /> </td>
		    	<td>  <label for="checkAno">Ano:</label> </td>
		    	<td>
		    		<html:text property="obj.anoReferencia" size="5" onfocus="javascript:forms[0].tipoBusca[0].checked = true;"></html:text>
		        </td>
		    </tr>
		    <tr>
				<td> <html:radio property="tipoBusca" value="2" styleId="checkCurso" /> </td>
		    	<td> <label for="checkCurso">Curso:</label> </td>
		    	<td>
		    		<html:select property="obj.cursoTecnico.id" onfocus="javascript:forms[0].tipoBusca[1].checked = true;">
		                <html:option value="">-- SELECIONE --</html:option>
		                <html:options collection="cursos" property="id" labelProperty="codigoNome"/>
		            </html:select>
		        </td>
		    </tr>
		 	<tr>
				<td> <html:radio property="tipoBusca" value="3" styleId="checkEspc" /> </td>
		 		<th>Especialização:</th>
				<td>
					<html:select property="obj.especializacao.id" onfocus="javascript:forms[0].tipoBusca[2].checked = true;">>
		                <html:option value="">> Opções</html:option>
		                <html:options collection="especializacoes" property="id" labelProperty="descricao"/>
		            </html:select>
				</td>
			</tr>
		    <tr>
		    	<td> <html:radio property="tipoBusca" value="4" styleId="checkTodos" /> </td>
		    	<td> <label for="checkTodos">Todos</label> </td>
			</tr>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="3">
					<html:hidden property="buscar" value="true"/>
					<html:submit><fmt:message key="botao.buscar" /></html:submit>
		    	</td>
		    </tr>
		</tfoot>
	</table>
	</html:form>

	<br/>
	<c:if test="${not empty lista }">
		<div class="infoAltRem">
			<html:img page="/img/alterar.gif" style="overflow: visible;"/>
			: Alterar Turma de Entrada
			<html:img page="/img/delete.gif" style="overflow: visible;"/>
			: Desativar Turma de Entrada
		</div>
	</c:if>
	<ufrn:table collection="${lista}" properties="descricao"
		headers="Descrição"
		title="Turmas de Entrada" pageSize="10"
		links="src='${ctx}/img/alterar.gif',?id={id}&dispatch=edit;
			   src='${ctx}/img/delete.gif',?id={id}&dispatch=remove&desativar=true"
		linksRoles="<%=new int[][] {
				   new int[] {SigaaPapeis.GESTOR_TECNICO},
				   new int[] {SigaaPapeis.GESTOR_TECNICO}}%>"
	/>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
