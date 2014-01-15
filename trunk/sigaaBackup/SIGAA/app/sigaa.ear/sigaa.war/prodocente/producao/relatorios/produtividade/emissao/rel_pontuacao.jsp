<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<link rel="stylesheet" media="all" href="/shared/css/relatorio_produtividade.css"/>

<style>
table thead tr th{
	text-align: left;
}
.pontos {
	text-align: right;
}
.titulo table tbody tr td {
	padding-left: 0px;
}

div.grupo {
margin-left:0;
}

</style>

<f:view>
	<h2> ${relatorioAtividades.obj.titulo} - ${relatorioAtividades.anoVigencia} </h2>
<div id="parametrosRelatorio">

	<table width="100%" id="identificacao">
		<tr>
			<th>Matrícula:</th>
			<td> <h:outputText value="#{relatorioAtividades.docenteRelatorio.siape }" /></td>
			<td rowspan="4">
				<p class="pontosRelatorio">
					<span> <h:outputText value="#{ relatorioAtividades.obj.totalPontos }" >
						   	<f:convertNumber pattern="##.#" /> 
						   </h:outputText> 
					</span> pontos
				</p>
			</td>
		</tr>
		<tr>
			<th>Nome:</th>
			<td>
				<h:outputText value="#{relatorioAtividades.docenteRelatorio.pessoa.nome }" />
			</td>
		</tr>
		<tr>
			<th>Centro:</th>
			<td>
				<h:outputText value="#{relatorioAtividades.docenteRelatorio.unidade.unidadeResponsavel.nome}" />
			</td>
		</tr>
		<tr>
			<th>Departamento:</th>
			<td>
				<h:outputText value="#{relatorioAtividades.docenteRelatorio.unidade.nome}" />
			</td>
		</tr>
		<tr>
			<th nowrap="nowrap">Pontuação (sem tetos):</th>
			<td>
				${relatorioAtividades.obj.totalPontosSemLimite}
			</td>
		</tr>
	</table>
</div>
<br />
<br />

	<c:forEach items="${relatorioAtividades.obj.grupoRelatorioProdutividadeCollection}" var="grupo">

		<h4>
			${grupo.numeroTopico} - ${grupo.titulo}
			<span class="limite">
			<c:choose>
				<c:when test="${grupo.pontuacaoMaxima == 0.0}">
				(sem limite de pontos)
				</c:when>
				<c:otherwise>
				(limitado a ${grupo.pontuacaoMaxima} pontos)
				</c:otherwise>
			</c:choose>
			</span>
		</h4>

		<c:if test="${ grupo.vazio }">
			<p class="vazio"> Não foram encontrados itens para este grupo </p>
		</c:if>

		<c:forEach items="${grupo.grupoItemCollection}" var="itemGrupo" varStatus="linha">

			<c:if test="${ not empty itemGrupo.producoes && !itemGrupo.atividade }">
				<%--  Tabelas de produções --%>
				<div class="grupo">
				<h5> ${grupo.numeroTopico}.${linha.count}. ${itemGrupo.itemRelatorioProdutividade.descricao} </h5>
				<table class="producoes">
					<thead>
						<tr class="subFormulario">
							<th> </th>
							<th> </th>
							<th width="80%">Título</th>
							<th class="ano">Ano</th>
							<th class="pontos">Pontos</th>
						</tr>
					</thead>
					<tbody>
					<c:forEach items="${itemGrupo.producoes}" var="producaoIntelectual" varStatus="row">
						<tr>
							<th>
								${grupo.numeroTopico}.${linha.count}.${row.count}
							</th>
							<th>
								<c:choose>
									<c:when test="${producaoIntelectual.producao.classValidacao == 'pendente' }">
										<img src="${ctx}/img/prodocente/validacao/pendente.gif"/>
									</c:when>
									<c:when test="${producaoIntelectual.producao.classValidacao == 'validado' }">
										<img src="${ctx}/img/prodocente/validacao/validado.gif"/>
									</c:when>
									<c:when test="${producaoIntelectual.producao.classValidacao == 'invalidado' }">
										<img src="${ctx}/img/prodocente/validacao/invalidado.gif"/>
									</c:when>
								</c:choose>
							</th>
							<td class="titulo"> ${producaoIntelectual.nomeAtividade} </td>
							<td class="ano"> ${ producaoIntelectual.ano } </td>
							<td class="pontos">
								<c:choose>
									<c:when test="${ producaoIntelectual.validado }">
										<fmt:formatNumber value="${ itemGrupo.pontosItem }" pattern="#0.0"/>
									</c:when>
									<c:otherwise> 0.0 </c:otherwise>
								</c:choose>
							</td>
						</tr>
					</c:forEach>
					</tbody>
					<tfoot>
					<tr>
						<td colspan="3">
							Total do Item
							<span>
							<c:choose>
								<c:when test="${itemGrupo.limitePontuacao == 0.0}">
								(sem limite de pontos)
								</c:when>
								<c:otherwise>
								(limitado a ${itemGrupo.limitePontuacao} pontos)
								</c:otherwise>
							</c:choose>
							</span>
						</td>
						<td class="pontos" colspan="2"> 
							<fmt:formatNumber value="${ itemGrupo.totalPontos }" pattern="#0.0"/>
						</td>
					</tr>
					</tfoot>
				</table>
				</div>
			</c:if>

			<c:if test="${ not empty itemGrupo.atividades && itemGrupo.atividade }">
				<%--  Tabelas de atividades --%>
				<div class="grupo">
				<h5> ${grupo.numeroTopico}.${linha.count}. ${itemGrupo.itemRelatorioProdutividade.descricao} </h5>
				<table class="producoes">
					<c:forEach items="${itemGrupo.atividades}" var="atividade" varStatus="status">
						<c:if test="${ status.index == 0}">
							<thead>
								<tr class="subFormulario">
									<td> </td>
									${atividade.tituloView}
									<td colspan="3"></td>
									<td class="pontos">Pontos</td>
								</tr>
							</thead>
						</c:if>
						<tbody>
						<tr>
							<td> ${grupo.numeroTopico}.${linha.count}.${status.count} </td>
							${atividade.itemView}
							<jsp:setProperty name="itemGrupo" property="atividadeCorrente" value="${atividade}"/>
							<td colspan="3"></td>
							<td class="pontos">
								<fmt:formatNumber value="${ itemGrupo.pontosItem }" pattern="#0.0"/>
							</td>
						</tr>
						</tbody>
					</c:forEach>
					<tfoot>
					<tr>
						<td colspan="3">
							Total do Item
							<span>
							<c:choose>
								<c:when test="${itemGrupo.limitePontuacao == 0.0}">
								(sem limite de pontos)
								</c:when>
								<c:otherwise>
								(limitado a ${itemGrupo.limitePontuacao} pontos)
								</c:otherwise>
							</c:choose>
							</span>
						</td>
						<td class="pontos" colspan="9">
							<fmt:formatNumber value="${ itemGrupo.totalPontos }" pattern="#0.0"/>
						</td>
					</tr>
					</tfoot>
				</table>
				</div>
			</c:if>
		</c:forEach>

		

	</c:forEach>

	<div id="quadro-resumo">
		<h4> Quadro Resumo </h4>

		<table>
			<tbody>
			<c:forEach items="${relatorioAtividades.obj.grupoRelatorioProdutividadeCollection}" var="grupo">
				<tr class="grupo">
					<td> ${grupo.numeroTopico}. ${grupo.titulo} </td>
					<td class="pontos"> ${grupo.totalPontos} </td>
				</tr>

				<c:forEach items="${grupo.grupoItemCollection}" var="itemGrupo" varStatus="linha">
					<c:if test="${itemGrupo.totalPontos > 0 }">
					<tr class="item">
						<td>
							${grupo.numeroTopico}.${linha.count}. ${itemGrupo.itemRelatorioProdutividade.descricao}
						</td>
						<td class="pontos">
							${itemGrupo.totalPontos}
						</td>
					</tr>
					</c:if>
				</c:forEach>
			</c:forEach>
			</tbody>
			<tfoot>
				<tr>
					<td> Total de Pontos </td>
					<td class="pontos"> ${ relatorioAtividades.obj.totalPontos } </td>
				</tr>
			</tfoot>
		</table>
	</div>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
