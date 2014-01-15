<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<h2 class="tituloPagina">
<ufrn:subSistema /> &gt
<c:if test="${ param['dispatch'] == 'remove' }">
Destativação
</c:if>
<c:if test="${ param['dispatch'] != 'remove' }">
${ (formAproveitamentoDisciplina.obj.id == 0? "Cadastro": "Atualização") }
</c:if>
de Aproveitamento de Disciplinas</h2>

<html:form
	action="/ensino/cadastroAproveitamentoDisciplina"
	focus="obj.discente.pessoa.nome" styleId="form">
	<table class="formulario" width="90%">
		<caption>Dados do Aproveitamento</caption>
		<html:hidden property="obj.id" />

		<tbody>
			<tr>
				<th nowrap="nowrap">Aluno:</th>
				<td>
				<c:set var="idAjax" value="obj.discente.id" />
				<c:set var="nomeAjax" value="obj.discente.pessoa.nome" />
				<c:set var="obrigatorio" value="true" />
				<%@include file="/WEB-INF/jsp/include/ajax/discente.jsp"%>
				</td>
			</tr>
			<tr>
				<th nowrap="nowrap">Disciplina:</th>
				<td>
				<c:set var="idAjax" value="obj.disciplina.id" /> <c:set
					var="nomeAjax" value="obj.disciplina.nome" /> <%@include
					file="/WEB-INF/jsp/include/ajax/disciplina.jsp"%>
				</td>
			</tr>
			<tr>
				<th nowrap="nowrap">Concessão:</th>
				<td><html:select property="obj.tipoConcessao.id">
	                <html:option value=""> --SELECIONE-- </html:option>
	                <html:options collection="concessoes" property="id" labelProperty="descricao" />
                </html:select></td>
			</tr>
			<tr>
				<td colspan="4" align="center">
				<html:button dispatch="persist" value="Confirmar" />
				<input value="Cancelar" type="button" onclick="javascript:cancelar('form');"/>
				</td>
			</tr>
		</tbody>
	</table>
</html:form>

<c:if test="${ param['dispatch'] == 'remove' }">
<script type="text/javascript">
	new Remocao('form');
</script>
</c:if>

<br>
<center><html:img page="/img/required.gif" style="vertical-align: top;" />
<span class="fontePequena"> Campos de preenchimento obrigatório. </span>
</center>
<br>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>
