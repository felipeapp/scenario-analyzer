<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> &gt; Detalhes do Curso</h2>
	<br/>
	<h:form id="form">
		<table class="listagem" width="90%">
			<caption>CURSO ${relatoriosLato.detalhesCursoLato.curso.nome }</caption>
			<tbody>
				<tr>
					<th class="rotulo">Centro:</th>
					<td>
						<h:outputText value="#{relatoriosLato.detalhesCursoLato.curso.unidade.gestora.nome}" />
					</td>
				</tr>			
				<tr>
					<th class="rotulo">Período:</th>
					<td>
						<h:outputText value="#{relatoriosLato.detalhesCursoLato.curso.dataInicio}" /> -
						<h:outputText value="#{relatoriosLato.detalhesCursoLato.curso.dataFim}" />
					</td>
				</tr>
				<tr>
					<th class="rotulo">Coordenador:</th>
					<td>
						<h:outputText value="#{relatoriosLato.detalhesCursoLato.curso.coordenacao.servidor.pessoa.nome}" />
					</td>
				</tr>
				<tr>
					<th class="rotulo">Alunos Matriculados:</th>
					<td>
						<h:outputText value="#{relatoriosLato.detalhesCursoLato.numeroAlunosMatriculados}" />
					</td>
				</tr>
				<tr>
					<th class="rotulo">Alunos Concluidos:</th>
					<td>
						<h:outputText value="#{relatoriosLato.detalhesCursoLato.numeroAlunosConcluido}" />
					</td>
				</tr>			
				<tr>
					<th class="rotulo">Status:</th>
					<td>
						<h:outputText value="#{relatoriosLato.detalhesCursoLato.curso.propostaCurso.situacaoProposta.descricao}" />
					</td>
				</tr>	
			</tbody>
		</table>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
