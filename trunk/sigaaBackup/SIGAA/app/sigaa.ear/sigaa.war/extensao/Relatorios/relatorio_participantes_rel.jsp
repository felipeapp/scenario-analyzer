<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<style>
	table.listagem tr.curso td{
		background: #C8D5EC;
		font-weight: bold;
		padding-left: 20px;
	}
</style>

<f:view>

	<h:outputText value="#{relatoriosAtividades.create}"/>
	<h2>Relatório Totais de Participantes Cadastrados em Ações de Extensão</h2>
	<br/>

	<c:set var="result" value="${relatoriosAtividades.resultadoQuantitativo}"/>

	<c:if test="${empty result}">
	<center><i> Nenhuma ação de extensão localizada </i></center>
	</c:if>

	<c:if test="${not empty result}">
	
	<div id="parametrosRelatorio">
		<table>
			
			<c:if test="${relatoriosAtividades.checkBuscaAno}">
				<tr>
					<th>Ano da Ação:</th>
					<td>${relatoriosAtividades.ano}</td>
				</tr>
			</c:if>
			
			<c:if test="${relatoriosAtividades.checkBuscaTipoAtividade}">
				<tr>
					<th>Tipo da Ação:</th>
					<td>${relatoriosAtividades.buscaTipoAcao}</td>
				</tr>
			</c:if>

			<c:if test="${relatoriosAtividades.checkBuscaTipoParticipante}">
				<tr>
					<th>Tipo de Participante:</th>
					<td>${relatoriosAtividades.buscaTipoParticipante}</td>
				</tr>
			</c:if>
			
			<c:if test="${relatoriosAtividades.checkBuscaUnidade}">
				<tr>
					<th>Unidade:</th>
					<td>${relatoriosAtividades.buscaNomeUnidade}</td>
				</tr>
			</c:if>
			
			<c:if test="${relatoriosAtividades.checkBuscaPeriodo}">
				<tr>
					<th>Período:</th>
					<td><h:outputText value="#{relatoriosAtividades.dataInicio}">
						<f:convertDateTime pattern="dd/MM/yyyy" />
					</h:outputText> a <h:outputText value="#{relatoriosAtividades.dataFim}">
						<f:convertDateTime pattern="dd/MM/yyyy" />
					</h:outputText></td>
				</tr>
			</c:if>

		</table>
		</div>
		<br />
		 <h3 class="tituloTabelaRelatorio"> 
		 	AÇÕES LOCALIZADAS <c:if test="${(relatoriosAtividades.checkBuscaSituacaoAtividade)}">COM SITUAÇÃO '${relatoriosAtividades.situacaoAtividade.descricao}'</c:if>
		 </h3>
		 <table class="tabelaRelatorio">
		      <thead>       
		      	<tr>
					<th style="text-align:center">Ano</th>		        	
		        	<th>Tipo de Ação</th>
		        	<th>Unidade</th>
					<th>Tipo Participante</th>
		        	<th style="text-align:right">Quantidade</th>		        	
		        </tr>
		 	</thead>
			<tbody>
				<c:set value="0" var="total_publico" />
				<c:forEach items="${result}" var="r" varStatus="status">
					<tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
						<td style="text-align:center">${r[4]}</td>
						<td>${r[0]}</td>
						<td>${r[3]}</td>
						<td>${r[1]}</td>
						<td style="text-align:right">${r[2]}</td>
						<c:set value="${total_publico + r[2]}" var="total_publico" />
					</tr>
				</c:forEach>
				<tr class="linhaPar">
					<td></td>
					<td></td>
					<td></td>
					<td><b>Total</b></td>
					<td style="text-align:right"><b><fmt:formatNumber
						value="${total_publico}" /></b></td>
				</tr>

			</tbody>

		</table>
	
	</c:if>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>