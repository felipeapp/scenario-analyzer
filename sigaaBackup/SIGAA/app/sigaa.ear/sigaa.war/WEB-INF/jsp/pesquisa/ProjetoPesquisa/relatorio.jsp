<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<style>
	#ranking thead tr td {
		border: 1px solid #999;
		border-width: 1px 0;
		background: #EEE;
	}

	#ranking tbody tr td {
		border-bottom: 1px dashed #AAA;
		padding-top: 5px;
	}

	#ranking tbody tr.divisao td {
		border-bottom: 1px solid #555;
		padding-left: 10px;
		padding-bottom: 10px;
	}

	#ranking tbody tr.centro td {
		font-size: 1.3em;
		padding: 10px 10px 3px;
		border-bottom: 1px solid #555;
		font-weight: bold;
		letter-spacing: 1px;
		background: #EEE;
	}
</style>


	<table width="100%">
		<caption><b>Relatório de Projetos de Pesquisa</b></caption>
		<c:forEach items="${projetoPesquisaForm.filtros}" var="filtro">
		<c:choose>
			<c:when test="${filtro == 2}">
				<tr>
					<th width="25%">Tipo:</th>
					<td><b> ${ projetoPesquisaForm.obj.interno ? 'Interno' : 'Externo' } </b></td>
				</tr>
			</c:when>
			<c:when test="${filtro == 22}">
				<tr>
					<th >Código:</th>
					<td><b> ${ projetoPesquisaForm.codigo } </b></td>
				</tr>
			</c:when>
			<c:when test="${filtro == 10}">
				<tr>
					<th >Ano:</th>
					<td><b> ${ projetoPesquisaForm.obj.codigo.ano } </b></td>
				</tr>
			</c:when>
			<c:when test="${filtro == 11}">
				<tr>
					<th >Pesquisador:</th>
					<td><b> ${ projetoPesquisaForm.membroProjeto.servidor.pessoa.nome } </b></td>
				</tr>
			</c:when>
			<c:when test="${filtro == 13}">
				<tr>
					<th >Centro:</th>
					<td><b> ${ projetoPesquisaForm.centro.nome } </b></td>
				</tr>
			</c:when>
			<c:when test="${filtro == 14}">
				<tr>
					<th >Unidade:</th>
					<td><b> ${ projetoPesquisaForm.unidade.nome } </b></td>
				</tr>
			</c:when>
			<c:when test="${filtro == 20}">
				<tr>
					<th >Título:</th>
					<td><b> ${ projetoPesquisaForm.titulo } </b></td>
				</tr>
			</c:when>
			<c:when test="${filtro == 21}">
				<tr>
					<th >Objetivos:</th>
					<td><b> ${ projetoPesquisaForm.objetivos } </b></td>
				</tr>
			</c:when>
			<c:when test="${filtro == 23}">
				<tr>
					<th >Linha de Pesquisa:</th>
					<td><b> ${ projetoPesquisaForm.obj.linhaPesquisa.nome } </b></td>
				</tr>
			</c:when>
			<c:when test="${filtro == 15}">
				<tr>
					<th >Área de Conhecimento:</th>
					<td><b> ${ projetoPesquisaForm.subarea.nome } </b></td>
				</tr>
			</c:when>
			<c:when test="${filtro == 16}">
				<tr>
					<th >Grupo de Pesquisa:</th>
					<td><b> ${ projetoPesquisaForm.obj.linhaPesquisa.grupoPesquisa.nome } </b></td>
				</tr>
			</c:when>
			<c:when test="${filtro == 17}">
				<tr>
					<th >Agência Financiadora:</th>
					<td><b> ${ projetoPesquisaForm.financiamentoProjetoPesq.entidadeFinanciadora.nome } </b></td>
				</tr>
			</c:when>
			<c:when test="${filtro == 18}">
				<tr>
					<th >Situação do Projeto:</th>
					<td><b> ${ projetoPesquisaForm.obj.situacaoProjeto.descricao } </b></td>
				</tr>
			</c:when>
			<c:when test="${filtro == 19}">
				<tr>
					<th >Relatório Final:</th>
					<td><b> ${ projetoPesquisaForm.relatorioSubmetido ? 'Submetido' : 'Não Submetido' } </b></td>
				</tr>
			</c:when>
		</c:choose>
		</c:forEach>
		
	</table>
	
	<br />
	<table width="100%">
	<tr>
		<td>
		<table id="ranking" style="width: 100%;">
			
			<c:set var="centro" />

			<c:forEach items="${ lista }" var="projeto" varStatus="loop">

				<c:if test="${ centro != projeto.centro.nome }">
					<c:set var="centro" value="${ projeto.centro.nome }"/>

					<tr class="centro">
						<td colspan="2"> ${ projeto.centro.nome } </td>
					</tr>
				</c:if>
				
				<tr>
					<td><b>Projeto:</b></td>
					<td>${ projeto.codigoTitulo }</td>
				</tr>

				<tr>
					<td><b>Coordenador:</b></td>
					<td>${ projeto.coordenador.pessoa.nome } (${ projeto.coordenador.unidade.sigla })</td>
				</tr>
				
				<tr>
					<td colspan="2"><b>Objetivos:</b></td>
				</tr>
				
				<tr class="divisao">
					<td colspan="2">
						<ufrn:format type="texto" name="projeto" property="objetivos"/>
					</td>
				</tr>
			</c:forEach>

		</table>
		</td>

	</tr>
	</table>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
