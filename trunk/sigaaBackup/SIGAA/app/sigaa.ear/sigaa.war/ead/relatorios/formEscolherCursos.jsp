<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

	<h:messages />

	<h2><ufrn:subSistema /> > Relatório de Alunos por Turma</h2>
	<h:form id="busca">
		<table class="formulario" width="55%">
			<caption>Filtros para o Relatório</caption>
			
			<tbody>
				<tr>
					<th width="90">Curso:</th>
					<td>
						<h:selectOneMenu value="#{relatorioAlunosEad.idCurso}">
							<f:selectItem itemValue="0" itemLabel="Todos" />
							<f:selectItems value="#{relatorioAlunosEad.cursosCombo}" />
						</h:selectOneMenu>	
					</td>
				</tr>
				<tr>
					<th>Ano:</th>
					<td>
						<h:selectOneMenu value="#{relatorioAlunosEad.ano}">
							<f:selectItems value="#{relatorioAlunosEad.anos}" />
						</h:selectOneMenu>	
					</td>
				</tr>	
				<tr>
					<th>Semestre:</th>
					<td>
						<h:selectOneMenu value="#{relatorioAlunosEad.semestre}">
							<f:selectItem itemValue="1" itemLabel="1" />
							<f:selectItem itemValue="2" itemLabel="2" />
						</h:selectOneMenu>	
					</td>
				</tr>
				<tr>
					<th>Apenas turmas abertas:</th>
					<td>
						<h:selectOneMenu value="#{relatorioAlunosEad.turmaAberta}">
							<f:selectItems value="#{ relatorioAlunosEad.simNao }"/>
						</h:selectOneMenu>	
						<ufrn:help>Lista apenas as turmas que ainda estão abertas.</ufrn:help>
					</td>
				</tr>
			</tbody>
			
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Buscar" action="#{relatorioAlunosEad.gerar}" /> 
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{relatorioAlunosEad.cancelar}" />
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>

</f:view>

<script type="text/javascript">
function selecionaTodos(chk){
	$A(document.getElementsByClassName('selecionar')).each(function(e) {
		e.checked = chk.checked;
	});
}
</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
