<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<html>

<f:view>
	
	<h2><ufrn:subSistema /> > Retornar alunos trancados</h2>
	
	
	<div class="descricaoOperacao">
		<h4>Caro Usuário,</h4> <br />
		<p>
			Abaixo está a lista de discentes que estão com o status TRANCADO porém não possuem
			trancamento no ano.período atual.
			Selecione os alunos que deseja retornar para o status ATIVO. 
		</p>
	</div>
	
	<h:form id="form">

	<c:if test="${not empty retornoTrancamento.listaDiscentes}">
		<br />
		<table class="listagem">
			
			<caption>Discentes trancados (${ fn:length(retornoTrancamento.listaDiscentes) })</caption>
			
			<thead>
				<tr>
					<th></th>
					<th>Matrícula</th>
					<th>Nome</th>
					<th>Curso</th>
				</tr>
			</thead>

			<tbody>
			
				<tr>
					<td><input type="checkbox" name="checkTodos" id="checkTodos" onclick="marcarTodos(this)" class="noborder"/></td>
					<td colspan="3">
						<b><label for="checkTodos">Selecionar Todos</label></b>
					</td>
				</tr>
			
				<c:forEach items="#{retornoTrancamento.listaDiscentes}" var="discente" varStatus="status">

					<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
						<td>
							<input type="checkbox" name="items" id="check_${discente.id}" value="${discente.id}" />
						</td>
				
						<td> <label for="check_${discente.id}">${discente.matricula}</label> </td>
						<td> <label for="check_${discente.id}">${discente.nome}</label> </td>
						<td> <label for="check_${discente.id}">${discente.curso.descricao}</label> </td>
						
					</tr>
					
				</c:forEach>
			</tbody>

			<tfoot>
				<tr>
					<td colspan="5" style="text-align: center;">
						<h:commandButton action="#{retornoTrancamento.cancelar}" value="Cancelar" id="cancelar"/>
						<h:commandButton action="#{retornoTrancamento.confirmar}" value="Confirmar retorno" id="retorno" />
					</td>
				</tr>
			</tfoot>

		</table>
	</c:if>
	
	
	<c:if test="${empty retornoTrancamento.listaDiscentes}">

		<table class="listagem">
			
			<caption>Discentes trancados (${ fn:length(retornoTrancamento.listaDiscentes) })</caption>
			
			<tr>
				<td align="center">
					Não existe nenhum discente apto para ser retornado de trancamento!
				</td>
			</tr>
				
		</table>
			
	</c:if>
	
	</h:form>

</f:view>
<script type="text/javascript">
<!--
function marcarTodos(chk) {
	var checks = document.getElementsByName('items');
	for (i=0;i<checks.length;i++) {
		checks[i].checked = chk.checked;
	}
}
//-->
</script>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
</html>