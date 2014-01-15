<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<h2> <ufrn:subSistema /> &gt; Cadastro de Plano de Trabalho sem cota &gt; Escolha do Projeto </h2>

<style>
	table.listagem tr.ano td {
		background-color: #C4D2EB;
		padding: 8px 10px 2px;
		border-bottom: 1px solid #BBB;
		font-variant: small-caps;

		font-style: italic;
	}
</style>

<div class="descricaoOperacao">
	<h3>
		Caro docente,
	</h3>
	<br />
	<p>
		Esta operação destina-se ao registro de planos de trabalho sem vínculo com cotas de bolsas controladas pela ${ configSistema['siglaInstituicao'] }.
		Se seu projeto não está listado abaixo, verifique se ele possui as seguintes características:
	</p>
	<ul>
		<li> Está sob sua coordenação; </li>
		<li> Possui status EM EXECUÇÃO;</li>
		<li> Encontra-se com o período vigente.</li>
	</ul>
</div>

<c:if test="${not empty lista}">

	<center>
		<div class="infoAltRem">
			<html:img page="/img/seta.gif" style="overflow: visible;" />: Selecionar Projeto
			<br/>
		</div>
	</center>

	<table cellpadding="3" class="listagem">
		<caption class="listagem">Projetos de Pesquisa</caption>
		<thead>
			<tr>
				<th style="text-align: left;">Código</th>
				<th style="text-align: left;">Título</th>
				<th style="text-align: left;">Situação</th>
				<th style="text-align: left;">Período do Projeto</th>
				<th style="text-align: center;">Planos de Trabalho</th>
				<th></th>
			</tr>
		</thead>
		<tbody>
			<c:set var="ano"/>
			
			<c:forEach items="${lista}" var="projeto" varStatus="status">
				<c:if test="${projeto.codigo.ano != ano}">
					<c:set var="ano" value="${ projeto.codigo.ano }" />
					<tr class="ano">
						<td colspan="6">
							Ano: ${ano}
						</td>
					</tr>
				</c:if>
				<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
					<td align="left" nowrap="nowrap">
						${projeto.codigo}
					</td>
					<td style="text-align: left;">
						${projeto.titulo}
					</td>
					<td align="left" nowrap="nowrap">
						${projeto.situacaoProjeto.descricao}
					</td>
					<td align="left" nowrap="nowrap">
						<fmt:formatDate value="${projeto.dataInicio}"pattern="dd/MM/yyyy" />
						a <fmt:formatDate value="${projeto.dataFim}"pattern="dd/MM/yyyy" />
					</td>
					<td align="center" nowrap="nowrap">
						${ fn:length( projeto.planosTrabalho) }
					</td>
		
					<td align="center">
						<html:link action="/pesquisa/planoTrabalho/wizard?dispatch=selecionarProjeto&idProjeto=${projeto.id}">
							<img src="${ctx}/img/seta.gif"
								title="Selecionar Projeto"
								alt="Selecionar Projeto"/>
						</html:link>
					</td>
				</tr>
			</c:forEach>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="6" style="text-align: center; font-weight: bold;	">
					${ fn:length(lista) } ${ fn:length(lista) > 1 ? 'projetos' : 'projeto'} de pesquisa ${ fn:length(lista) > 1 ? 'encontrados' : 'encontrado'}
				</td>
			</tr>
		</tfoot>
	</table>
</c:if>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
