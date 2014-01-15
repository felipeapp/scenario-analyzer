<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
<a4j:keepAlive beanName="solicitacaoReposicaoProva" />

<h2>Solicitação de Reposição de Avaliação</h2>

<table class="listagem, tabelaRelatorio" width="100%">
	<tr>
		<td colspan="2" class="subFormulario">
			<h3 class="tituloTabelaRelatorio">Dados do Aluno</h3>
		</td>
	</tr>	
	<tr>
		<th>Nome:</th>
		<td>${solicitacaoReposicaoProva.obj.discente.matricula} - ${solicitacaoReposicaoProva.obj.discente.pessoa.nome}</td>		
	</tr>
	<tr>
		<th>Curso:</th>
		<td>${solicitacaoReposicaoProva.obj.discente.curso.descricao}</td>		
	</tr>		
	<tr>
		<th>Nível:</th>
		<td>${solicitacaoReposicaoProva.obj.discente.nivelDesc}</td>		
	</tr>		
	<tr>
		<td colspan="2" class="subFormulario">
			<h3 class="tituloTabelaRelatorio">Dados da Solicitação</h3>
		</td>
	</tr>	
	<tr>
		<th>Data da Solicitação:</th>
		<td><ufrn:format type="dataHora" valor="${solicitacaoReposicaoProva.obj.dataCadastro}"/></td>		
	</tr>	
	<tr>
		<th>Turma/Disciplina:</th>
		<td>${solicitacaoReposicaoProva.obj.turma.descricaoCodigo}</td>		
	</tr>			
	<tr>
		<th>Avaliação Perdida:</th>
		<td>${solicitacaoReposicaoProva.obj.dataAvaliacao.descricao}</td>		
	</tr>	
	<tr>
		<th>Data da Avaliação Perdida:</th>
		<td><ufrn:format type="data" valor="${solicitacaoReposicaoProva.obj.dataAvaliacao.data}"/> às ${solicitacaoReposicaoProva.obj.dataAvaliacao.hora}</td>		
	</tr>		
	<tr>
		<th>Situação:</th>
		<td>${solicitacaoReposicaoProva.obj.descricaoStatusDocente}</td>		
	</tr>	
	<tr>
		<td colspan="2" class="subFormulario"> Justificativa da Solicitação:</td>
	</tr>		
	<tr>
		<td colspan="2">
		    ${solicitacaoReposicaoProva.obj.justificativa}
		</td>		
	</tr>
	<c:if test="${solicitacaoReposicaoProva.obj.deferido}">
		<tr>
			<td colspan="2" class="subFormulario">
				<h3 class="tituloTabelaRelatorio">Dados da Apreciação</h3>
			</td>
		</tr>
		<tr>
			<th>Data/Hora da Prova:</th>
			<td>
				<ufrn:format type="data" valor="${solicitacaoReposicaoProva.obj.dataProvaSugerida}"/> às
				<ufrn:format type="hora" valor="${solicitacaoReposicaoProva.obj.dataProvaSugerida}"/>			
			</td>
		</tr>
		<tr>
			<th>Local:</th>
			<td>
				${solicitacaoReposicaoProva.obj.localProva}
			</td>
		</tr>
		<tr>
			<th>Observação:</th>
			<td>
				<p>${solicitacaoReposicaoProva.obj.observacaoHomologacao}</p>
			</td>
		</tr>		
	</c:if>
	<c:if test="${solicitacaoReposicaoProva.obj.indeferido}">
		<tr>
			<td colspan="2" class="subFormulario">
				<h3 class="tituloTabelaRelatorio">Motivo do Indeferimento:</h3>
			</td>			
		</tr>
		<tr>
			<td colspan="2">
				<p>${solicitacaoReposicaoProva.obj.observacaoHomologacao}</p>
			</td>
		</tr>	
	</c:if>
</table>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>