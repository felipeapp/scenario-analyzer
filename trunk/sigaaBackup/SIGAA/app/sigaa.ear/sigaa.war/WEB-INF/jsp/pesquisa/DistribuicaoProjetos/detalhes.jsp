<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<style>
	#relatorio table tr td.espaco{
		height: 20px;
	}

	#relatorio table tr td.espaco_projetos{
		height: 8px;
	}

	#relatorio table tr.area td{
		font-weight: bold;
		padding: 1px 5px;
		border: 1px solid #444;
		border-width: 1px 0px;
		font-size: 1.2em;
		background: #EEE;
		text-align: center;
		font-variant: small-caps;
	}

	#relatorio table tr.campos td{
		font-weight: bold;
		padding: 3px;
	}

	#relatorio table tr.projeto td{
		font-weight: bold;
		padding: 1px 5px;
		border-bottom: 1px solid #444;
		font-size: 1em;
	}

	#relatorio table tr.consultor td{
		padding: 2px 0 2px 15px;
		border-bottom: 1px dashed #444;
	}

	#relatorio table tr.total td{
		padding: 3px;
		border-top: 1px solid #222;
		font-weight: bold;
	}
</style>

<h2> Avaliações Pendentes de Projetos de Pesquisa </h2>

<table width="100%">

	<tbody>
		<c:if test="${not empty avaliacoes}">

			<c:set var="areaAtual" value="0" />
			<c:set var="projetoAtual" value="0" />
			<c:set var="totalProjetos" value="0" />
		    <c:forEach items="${avaliacoes}" var="avaliacao" varStatus="status">

				<c:if test="${ areaAtual != avaliacao.projetoPesquisa.areaConhecimentoCnpq.grandeArea.id}">
					<c:set var="areaAtual" value="${avaliacao.projetoPesquisa.areaConhecimentoCnpq.grandeArea.id}" />

					<c:if test="${status.index != 0 }">
						<tr><td class="espaco"></td></tr>
					</c:if>

					<tr class="area">
						<td colspan="2">
							${avaliacao.projetoPesquisa.areaConhecimentoCnpq.grandeArea.nome}
						</td>
		    		</tr>
				</c:if>

				<c:if test="${ projetoAtual != avaliacao.projetoPesquisa.id}">
					<c:set var="projetoAtual" value="${avaliacao.projetoPesquisa.id}" />
					<c:set var="totalProjetos" value="${totalProjetos + 1}" />
					<tr><td colspan="2" class="espaco_projetos"></td></tr>
					<tr class="projeto">
			    		<td colspan="2">
			    			${avaliacao.projetoPesquisa.codigo}
			    			${avaliacao.projetoPesquisa.titulo}
			    		</td>
		    		</tr>
				</c:if>

				<tr class="consultor">
		    		<td width="60%"> ${ avaliacao.consultor.nome } </td>
		    		<td> ${ avaliacao.consultor.email }</td>
		    	</tr>
			</c:forEach>

		</c:if>
		<c:if test="${empty avaliacoes}">
				<tr>
					<td align="center" colspan="2"><font color="red">Nenhuma avaliação pendente foi encontrada</font></td>
				</tr>
		</c:if>
	</tbody>
	<c:if test="${not empty avaliacoes}">
	<tfoot>
		<tr>
			<td colspan="5" align="center">
			<br />
			<strong>  ${totalProjetos} Projetos Distribuidos </strong>
			</td>
		</tr>
	</tfoot>
	</c:if>
</table>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>