<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
<a4j:keepAlive beanName="solicitacaoReposicaoProva" />

<h2>Solicita��o de Reposi��o de Avalia��o</h2>

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
		<th>N�vel:</th>
		<td>${solicitacaoReposicaoProva.obj.discente.nivelDesc}</td>		
	</tr>		
	<tr>
		<td colspan="2" class="subFormulario">
			<h3 class="tituloTabelaRelatorio">Dados da Solicita��o</h3>
		</td>
	</tr>	
	<tr>
		<th>Data da Solicita��o:</th>
		<td><ufrn:format type="dataHora" valor="${solicitacaoReposicaoProva.obj.dataCadastro}"/></td>		
	</tr>	
	<tr>
		<th>Turma/Disciplina:</th>
		<td>${solicitacaoReposicaoProva.obj.turma.descricaoCodigo}</td>		
	</tr>			
	<tr>
		<th>Avalia��o Perdida:</th>
		<td>${solicitacaoReposicaoProva.obj.dataAvaliacao.descricao}</td>		
	</tr>	
	<tr>
		<th>Data da Avalia��o Perdida:</th>
		<td><ufrn:format type="data" valor="${solicitacaoReposicaoProva.obj.dataAvaliacao.data}"/> �s ${solicitacaoReposicaoProva.obj.dataAvaliacao.hora}</td>		
	</tr>		
	<tr>
		<th>Situa��o:</th>
		<td>${solicitacaoReposicaoProva.obj.descricaoStatusDocente}</td>		
	</tr>	
	<tr>
		<td colspan="2" class="subFormulario"> Justificativa da Solicita��o:</td>
	</tr>		
	<tr>
		<td colspan="2">
		    ${solicitacaoReposicaoProva.obj.justificativa}
		</td>		
	</tr>
	<c:if test="${solicitacaoReposicaoProva.obj.deferido}">
		<tr>
			<td colspan="2" class="subFormulario">
				<h3 class="tituloTabelaRelatorio">Dados da Aprecia��o</h3>
			</td>
		</tr>
		<tr>
			<th>Data/Hora da Prova:</th>
			<td>
				<ufrn:format type="data" valor="${solicitacaoReposicaoProva.obj.dataProvaSugerida}"/> �s
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
			<th>Observa��o:</th>
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