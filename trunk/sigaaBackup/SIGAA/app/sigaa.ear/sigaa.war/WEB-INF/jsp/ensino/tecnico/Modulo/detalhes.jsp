<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2>
	<html:link action="/ensino/tecnico/modulo/wizard?dispatch=cancelar">
		<ufrn:subSistema semLink="true"/>
	</html:link>
	&gt; ${ sessionScope.operacao eq 'view' ? 'Resumo' : 'Remo��o' } 
	<ufrn:subSistema teste="tecnico"> do M�dulo</ufrn:subSistema>
	<ufrn:subSistema teste="not tecnico"> da S�rie</ufrn:subSistema>
</h2>
<style>
<!--
table.formulario th {font-weight: bold;}
-->
</style>
<html:form action="/ensino/tecnico/modulo/wizard"  method="post" >
	<html:hidden property="obj.id" />
	<table class="formulario" width="600">
	<caption>
	Dados
		<ufrn:subSistema teste="tecnico"> do M�dulo</ufrn:subSistema>
		<ufrn:subSistema teste="not tecnico"> da S�rie</ufrn:subSistema>
	</caption>
	<tbody>
		<tr>
		<th width="90">
		C�digo:
		</th>
		<td colspan="3">
		${ moduloTecnicoForm.obj.codigo }
		</td>
		</tr>

		<tr>
		<th>
		Descri��o:
		</th>
		<td colspan="3">
		${ moduloTecnicoForm.obj.descricao }
		</td>
		</tr>

		<c:if test="${ not empty moduloTecnicoForm.obj.moduloDisciplinas }">
		<tr>
		<th valign="top">Disciplinas do M�dulo:</th>
		<td colspan="3">
			<table style="border-collapse: collapse;">
			<c:forEach items="${ moduloTecnicoForm.obj.moduloDisciplinas }" var="md">
			<tr>
			<td>${ md.disciplina.descricao }</td>
			</tr>
			</c:forEach>
			</table>
		</td>
		</tr>
		</c:if>

		<tr>
		<th>
		C.H. Total:
		</th>
		<td width="100">
		${ moduloTecnicoForm.obj.cargaHoraria } hrs
		</td>
		<th width="140">
		C.H. Complementar:
		</th>
		<td>
		${ moduloTecnicoForm.obj.cargaHoraria - moduloTecnicoForm.obj.chDisciplinas } hrs
		</td>
		</tr>


	</tbody>

	<tfoot>
		<tr>
		<td colspan="4">
			<c:if test="${param.dispatch == 'remove'}">
				<html:button dispatch="chamaModelo" value="Remover" />
				<html:button dispatch="cancelar" value="Cancelar" cancelar="true"/>
			</c:if>
			<c:if test="${param.dispatch == 'view'}">
				<input value="<< Voltar" type="button" onclick="javascript:window.history.back();"/>
			</c:if>
		</td>
		</tr>
	</tfoot>
	</table>
</html:form>
<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>