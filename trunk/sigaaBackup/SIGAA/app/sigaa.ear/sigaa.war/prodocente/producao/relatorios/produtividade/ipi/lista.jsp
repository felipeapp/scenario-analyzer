<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<style>
	td.icones a { padding: 2px 3px; }
</style>

<f:view>
	<c:set var="dirBase" value="/prodocente/producao/" scope="session"/>
	<h2><ufrn:subSistema /> > Relatório de Produtividade do docente</h2>
	<h:outputText value="#{classificacaoRelatorio.create}" />
	
	<h:form id="form">
		<table class="formulario" width="80%">
			<caption>Selecione os Critérios de Consulta</caption>
			<tr>
				<th width="30%">Ano de Referência:</th>
				<td>
					<h:inputText value="#{classificacaoRelatorio.obj.anoVigencia}" size="4" id="anoVigencia"/>
				</td>
			</tr>
			<tr>
				<th>Relatório:</th>
				<td>
					<h:selectOneMenu id="idRelatorio" value="#{classificacaoRelatorio.obj.relatorioProdutividade.id}" style="width: 90%">
						<f:selectItems value="#{relatorioAtividades.allCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton id="gerarRelatorio" value="Consultar" action="#{classificacaoRelatorio.consultar}" />
					<h:commandButton id="cancelar" value="Cancelar" action="#{classificacaoRelatorio.cancelar}" onclick="#{confirm}"/>
				</td>
			</tr>
			</tfoot>
		</table>
	</h:form>	
	<c:if test="${classificacaoRelatorio.consulta}">
	<br />
	<div class="infoAltRem">
		<h:graphicImage value="/img/star.png" style="overflow: visible;" />: Calcular IPI médios e FPPIs
		<h:graphicImage value="/img/alterar_ipi.png" style="overflow: visible;" />: Alterar IPI
		<h:graphicImage value="/img/view2.gif" style="overflow: visible;" />: Visualizar Ranking Geral
		<h:graphicImage value="/img/prodocente/script_code.png" style="overflow: visible;" />: Visualizar Ranking por Centro
		<h:graphicImage value="/img/prodocente/script_code_red.png" style="overflow: visible;" />: Visualizar Ranking por Departamento
		<h:graphicImage value="/img/porta_arquivos/icones/xls.png" style="overflow: visible;" />: Relatório Avaliação para Pesquisa
		<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: Excluir Classificação
	</div>	
	
	<br />
	<h:form>
	<table class="listagem">
		<caption class="listagem">Lista de Classificações de Relatório</caption>
		<thead>
			<tr>
			<td width="15%">Ano de Referência</td>
			<td width="15%">Relatório</td>
			<td width="15%">Gerado em</td>
			<td width="15%">Status</td>
			<td width="45%">Finalidade</td>
			<td></td>
			</tr>
		</thead>
		<c:forEach items="#{classificacaoRelatorio.classificacoes}" var="item" varStatus="status">

			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td align="center">${item.anoVigencia}</td>
				<td>${item.relatorioProdutividade.titulo}</td>
				<td><fmt:formatDate value="${item.dataClassificacao}" pattern="dd/MM/yyyy HH:mm"/> </td>
				<td>${item.statusProcessamentoString}</td>
				<td>${item.finalidade}</td>
				
				
				<td nowrap="nowrap" class="icones">
					<h:commandLink styleClass="noborder" title="Calcular IPI médios e FPPIs" 
						action="#{classificacaoRelatorio.calculcarFPPI}" rendered="#{item.processadaComSucesso}">
						<h:graphicImage url="/img/star.png"/>
						<f:param name="id" value="#{item.id}"/>
					</h:commandLink>
				 	<h:commandLink styleClass="noborder" title="Alterar IPI" 
						action="#{classificacaoRelatorio.redirectPreAlterarIPI}" rendered="#{item.processadaComSucesso}">
						<h:graphicImage url="/img/alterar_ipi.png"/>
						<f:param name="id" value="#{item.id}"/>
					</h:commandLink>
					<h:commandLink styleClass="noborder" title="Visualizar Ranking Geral" 
						action="#{classificacaoRelatorio.montarRanking}" rendered="#{item.processadaComSucesso}">
						<h:graphicImage url="/img/view2.gif"/>
						<f:param name="id" value="#{item.id}"/>
					</h:commandLink>
					<h:commandLink styleClass="noborder" title="Visualizar Ranking por Centro" 
						action="#{classificacaoRelatorio.montarRankingCentro}" rendered="#{item.processadaComSucesso}">
						<h:graphicImage url="/img/prodocente/script_code.png"/>
						<f:param name="id" value="#{item.id}"/>
					</h:commandLink>
					<h:commandLink styleClass="noborder" title="Visualizar Ranking por Departamento" 
						action="#{classificacaoRelatorio.montarRankingDepartamento}" rendered="#{item.processadaComSucesso}">
						<h:graphicImage url="/img/prodocente/script_code_red.png"/>
						<f:param name="id" value="#{item.id}"/>
					</h:commandLink>
					<h:commandLink styleClass="noborder" title="Relatório Avaliação para Pesquisa" 
						action="#{classificacaoRelatorio.exportarRelatorio}" rendered="#{item.processadaComSucesso}">
						<h:graphicImage url="/img/porta_arquivos/icones/xls.png"/>
						<f:param name="id" value="#{item.id}"/>
					</h:commandLink>
					
					<h:commandLink styleClass="noborder" title="Excluir Classificação" 
						action="#{classificacaoRelatorio.desativar}" onclick="#{confirmDelete}">
						<h:graphicImage url="/img/delete.gif"/>
						<f:param name="id" value="#{item.id}"/>
					</h:commandLink>
				</td>
			</tr>
		</c:forEach>
	</table>
	</h:form>
	</c:if>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>