<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<style>
	tr.curso td {padding: 4px 0 2px 22px; border-bottom: 1px solid #555; font-weight: bold; margin-top: 100px; }
	tr.header td {padding: 0px ; background-color: #eee; border-bottom: 1px solid #555; font-weight: bold; border: 1px solid #000;}
</style>

<f:view>
		<h2>RELATÓRIO DE UTILIZAÇÃO DA TURMA VIRTUAL</h2>
		
		<div id="parametrosRelatorio">
			<table >
				
				<tr>
					<th>Edital:</th>
					<td> <h:outputText value="#{ buscaAvaliacaoProjetoPesquisaMBean.obj.projetoPesquisa.edital.descricao }"/> </td>
				</tr>
				<tr>
					<th>Quantidade de Avaliações:</th>
					<td> <h:outputText value="#{ buscaAvaliacaoProjetoPesquisaMBean.quantidadeAvaliacoes }"/> </td>
				</tr>
			</table>
		</div>

		<table class="tabelaRelatorio">
			<c:set var="_unidade" />
			
			<c:forEach items="#{buscaAvaliacaoProjetoPesquisaMBean.avaliacoes}" var="item">
				<tr>
					<c:set var="unidadeAtual" value="${item.projetoPesquisa.projeto.unidade.nome}"/>
					 <c:if test="${_unidade != unidadeAtual}">
 						<tr class="curso">
							<td colspan="2" style="padding-top: 15px;"><b>${ item.projetoPesquisa.projeto.unidade.nome }</b></td>
						</tr>
							<tr class="header">
								<td> Projeto </td>
								<td> Coordenador </td>
							</tr>
					 </c:if>
				
					<td style="border: 1px solid #000;">${ item.projetoPesquisa.codigoTitulo }</td>
					<td style="border: 1px solid #000;">${ item.projetoPesquisa.projeto.coordenador.pessoa.nome }</td>
				</tr>
				
				<c:set var="_unidade" value="${ unidadeAtual }"/>
								
			</c:forEach>
			
		</table>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>