<%@include file="/WEB-INF/jsp/include/cabecalho.jsp" %>

<style>
	tr.dadosTurma th {
		background: #EEE;
		border-bottom: 1px solid #DDD;
		font-weight: bold;
	}
	tr.dadosTurma td {
		background: #EEE;
		border-bottom: 1px solid #DDD;
	}
	tr.totalAlunos td {
		text-align: center;
	}
	tr.totalAlunos td input {
		color: #292;
		font-weight: bold;
		font-size: 1.3em;
		padding: 3px;
		text-align: center;
	}
	.colLeft{text-align: left; }
	.colCenter{text-align: center; }
	.colRight{text-align: right; }
	
</style>

<f:view>
	<h2> <ufrn:subSistema /> &gt; Mudança Coletiva de Matriz Curricular  &gt; Definir Alunos</h2>
	<h:form id="alunos">

	<table class="visualizacao" style="width: 90%">
		<caption>Dados da Matriz Curricular de Origem</caption>
		<tr>
			<th>Curso:</th>
			<td><h:outputText value="#{mudancaColetivaMatrizCurricular.matrizAtual.curso.descricao}" /></td>
		</tr>
		<tr>
			<th>Matriz Curricular de Origem:</th>
			<td><h:outputText value="#{mudancaColetivaMatrizCurricular.matrizAtual.descricao}" /></td>
		</tr>
		<tr>
			<th>Estrutura Curricular:</th>
			<td>${mudancaColetivaMatrizCurricular.curriculoAtual.id ne 0 ? mudancaColetivaMatrizCurricular.curriculoAtual : 'Todos'}</td>
		</tr>
		<tr>
			<th>Ano de Ingresso:</th>
			<td>${mudancaColetivaMatrizCurricular.anoIngresso > 0 ? mudancaColetivaMatrizCurricular.anoIngresso : 'Todos'}</td>
		</tr>
	</table>
	<br>
	<table class="formulario" width="90%">
		<caption class="formulario">Defina os discentes para a mudança de Matriz Curricular (${fn:length(mudancaColetivaMatrizCurricular.discentes)})</caption>
		<thead>
			<tr>
				<th width="1%" align="center"><input type="checkbox" onclick="checkAll(this)" style="margin-left: 10px;" class="check" id="checkTodos"/></th>
				<th width="3%">Matrícula</th>
				<th width="15%">Discente</th>
				<th width="5%">Status</th>
				<th width="20%">Matriz</th>
				<th width="5%">Currículo</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="#{mudancaColetivaMatrizCurricular.discentes}" var="discente" varStatus="status">	
				<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
					<td style="text-align: center;"><h:selectBooleanCheckbox value="#{discente.selecionado}" styleClass="check" id="selecionaDiscente"/></td>
					<td><h:outputLabel for="selecionaDiscente" value="#{ discente.matricula }"/></td>	
					<td><h:outputLabel for="selecionaDiscente" value="#{ discente.nome }"/></td>
					<td>${discente.statusString}</td>
					<td>${discente.matrizCurricular.descricao}</td>
					<td>${discente.curriculo.descricao}</td>
				</tr>
			</c:forEach>	
		</tbody>		
		<tfoot>
			<tr>
			<td colspan="8" align="center">
				<h:commandButton value="Selecionar Matriz de Destino" action="#{mudancaColetivaMatrizCurricular.efetuarMudancaMatriz}" />
				<h:commandButton value="<< Voltar" action="#{mudancaColetivaMatrizCurricular.voltarMatrizOrigem}" />
				<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{mudancaColetivaMatrizCurricular.cancelar}" />
			</td>
			</tr>
		</tfoot>		
	</table>
</h:form>

<br>
<center><h:graphicImage url="/img/required.gif" style="vertical-align: top;" /><span
	class="fontePequena"> Campos de preenchimento obrigatório. </span> <br>
</center>

</f:view>
<script type="text/javascript">
function checkAll(chk) {
	$A(document.getElementsByClassName('check')).each(function(e) {
		e.checked = chk.checked;
	});
}
history.forward(); 	
</script>
<%@include file="/WEB-INF/jsp/include/rodape.jsp" %>