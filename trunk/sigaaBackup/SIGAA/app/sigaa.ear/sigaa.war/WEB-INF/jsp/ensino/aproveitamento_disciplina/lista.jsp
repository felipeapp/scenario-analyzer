<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<h2 class="tituloPagina">
<ufrn:subSistema /> &gt;
Aproveitamentos de Disciplinas</h2>
<html:form
	action="/ensino/cadastroAproveitamentoDisciplina.do">
	<html:hidden property="buscar" value="true" />
	<table class="formulario" width="75%">
		<caption>Busca por Aproveitamentos</caption>

		<tbody>
			<tr>
				<td></td>
				<td><html:radio property="tipoBusca" value="1" styleClass="noborder"
					styleId="buscaCodigo" /></td>
				<th nowrap="nowrap">Aluno:</th>
				<td>
				<c:set var="idAjax" value="obj.discente.id" />
				<c:set var="nomeAjax" value="obj.discente.pessoa.nome" />
				<%@include file="/WEB-INF/jsp/include/ajax/discente.jsp"%></td>
			</tr>
			<tr>
				<td></td>
				<td><html:radio property="tipoBusca" value="2" styleClass="noborder"
					styleId="buscaCodigo" /></td>
				<th nowrap="nowrap">Disciplina:</th>
				<td>
				<c:set var="idAjax" value="obj.disciplina.id" /> <c:set
					var="nomeAjax" value="obj.disciplina.nome" /> <%@include
					file="/WEB-INF/jsp/include/ajax/disciplina.jsp"%></td>
			</tr>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="4" align="center">
					<html:button value="Buscar" dispatch="list" />
				<html:button value="Cancelar" dispatch="cancelar" />
				</td>
			</tr>
		</tfoot>
	</table>
</html:form>
<br>
<ufrn:table collection="${lista}"
	properties="discente.matricula,discente.pessoa.nome,disciplina.codigo,disciplina.nome,tipoConcessao.descricao"
	headers="Matrícula, Nome, Código, Disciplina, Concessão"
	title="Aproveitamentos" crud="true" />

<script type="text/javascript">
		function disciplinaOnFocus() {
			javascript:document.forms[0].tipoBusca[1].checked = true;
		}
		function discenteOnFocus() {
			javascript:document.forms[0].tipoBusca[0].checked = true;
		}
</script>

<br>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>
