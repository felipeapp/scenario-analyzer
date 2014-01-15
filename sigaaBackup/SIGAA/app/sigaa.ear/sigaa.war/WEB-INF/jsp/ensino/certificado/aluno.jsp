<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<script type="text/javascript" src="/shared/javascript/certificados.js"></script>

<h2 class="tituloPagina"><ufrn:subSistema /> &gt; Expedição de Certificado</h2>

<html:form action="/ensino/gerarCertificado?dispatch=escolheAluno" onsubmit="#">

	<table class="formulario" width="550">
		<caption>Informe o Aluno</caption>
		<tr>
			<td>Nome:</td>
			<td>
				<c:set var="idAjax" value="discente.id" />
				<c:set var="nomeAjax" value="discente.pessoa.nome" />
				<c:set var="usarFuncao" value="true" />
				<c:set var="obrigatorio" value="true" />
				<c:set var="status" value="todos" />
				<%@include file="/WEB-INF/jsp/include/ajax/discente.jsp"%>
			</td>
		</tr>
	</table>
</html:form>

<div class="obrigatorio">Campo de preenchimento obrigatório.</div>

<br/>&nbsp;

<div id="loading">

</div>

<div id="qualificacoes" style="text-align: center;">

</div>
<script type="text/javascript">
Element.hide('qualificacoes');
</script>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
