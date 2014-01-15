<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<style>
table.formulario th{
	font-weight: bold;
}
</style>

<h2 class="tituloPagina"><ufrn:steps /></h2>
<table class="formulario" width="70%">
	<caption>Turma</caption>
	<tr>
		<td colspan="4" style="font-weight: bold;text-align: center">${turmaForm.obj.disciplina.nome}
		 <ufrn:subSistema teste="not lato"> (${turmaForm.obj.ano} -
		${turmaForm.obj.periodo})</ufrn:subSistema></td>
	</tr>
	<tr>
		<th width="20%">Carga Horária:</th>
		<td width="15%">${turmaForm.obj.disciplina.chTotal} hrs</td>
		<th width="15%">Código:</th>
		<td>${turmaForm.obj.codigo}</td>
	</tr>

</table>
<br>
<html:form action="/ensino/criarTurma" method="post">
	<table class="formulario" width="95%">
		<caption>Horários</caption>
		<tr>
			<td align="center">
			<%@include file="/WEB-INF/jsp/include/horario.jsp"%>
			</td>
		</tr>
		<tfoot>
			<tr>
				<td colspan="4">
				<center><html:button view="docentes"><< Alterar Docentes</html:button> <html:button
					view="dadosGerais"><< << Alterar Dados Básicos</html:button> <html:button dispatch="cancelar"
					value="Cancelar" /> <html:button dispatch="resumo">Confirmar Turma >></html:button></center>
				</td>
			</tr>
		</tfoot>
	</table>
</html:form>
<br>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
