<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<h2 class="tituloPagina">
<ufrn:subSistema /> &gt;
${tipoAlteracao }
</h2>

<html:form
	action="/ensino/alterarMatriculaDisciplina?dispatch=discentes">
	<table class="formulario" width="75%">
		<caption>Busca por Aluno</caption>

		<tbody>
			<tr>
				<td></td>
				<td><html:radio property="tipoBusca" value="1"
					styleId="buscaCodigo"  /></td>
				<th nowrap="nowrap"><label for="buscaCodigo">Matricula:</label></th>
				<td>
				<html:text onfocus="marcaCheckBox('buscaCodigo')"
					property="obj.matricula.discente.matricula" size="10" maxlength="12"/>
				</td>
			</tr>
			<tr>
				<td></td>
				<td><html:radio property="tipoBusca" value="2"
					styleId="buscaDiscente" /></td>
				<th nowrap="nowrap"><label for="buscaDiscente">Aluno:</label></th>
				<td>
				<html:text onfocus="marcaCheckBox('buscaDiscente')"
					property="obj.matricula.discente.pessoa.nome" size="60" maxlength="55"/>
				</td>
			</tr>
			<tr>
				<td></td>
				<td><html:radio property="tipoBusca" value="3"
					styleId="buscaTodos"  /></td>
				<th nowrap="nowrap"><label for="buscaTodos">Todos</label></th>
			</tr>

		</tbody>
			<html:hidden property="buscar" value="true" />
		<tfoot>
			<tr>
				<td colspan="4" align="center"><html:submit>
					<fmt:message key="botao.buscar" />
				</html:submit>

				<html:button value="Cancelar" dispatch="cancelar" />
				</td>
			</tr>
		</tfoot>
	</table>
</html:form>
<br>
<c:if test="${not empty lista }">
	<div class="infoAltRem">
    <html:img page="/img/seta.gif"/> : Selecionar Aluno
	</div>
</c:if>
<ufrn:table collection="${lista}"
	properties="matricula,pessoa.nome,anoEntrada"
	headers="Matrícula, Nome, Ano"
	title="Aluno" crud="false"
	links="src='${ctx}/img/seta.gif',?id={id}&dispatch=carregarMatriculas"
	/>

<br>
<script type="text/javascript">
<!--
	function discenteOnFocus() {
		marcaCheckBox('buscaDiscente');
	}
//-->
</script>
<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>
