<% request.setAttribute("res1024","true"); %>
<%@include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>

<style>
	div.opcoes { margin: 4px 0; }
	div.opcoes a { font-size: 0.5em; }
	tr.alunoSelecionado { background: #FF8888; }
</style>

<f:view>
	
	<center><h3>Média das Notas das Semanas da Disciplina - Avaliações dos Tutores</h3></center>
	
	<br/>
	
	<h2>${ relatorioConsolidacao.turma.descricaoSemDocente }</h2>

		
		<div>
			<table class="tabelaRelatorio" width="100%">
			
				<tr>
					<th>Matrícula</th>
					<th>Aluno</th>
					<c:forEach items="#{relatorioConsolidacao.itensAvaliacao }" var="itemAvaliacao">
						<th style="text-align: center;">${itemAvaliacao}</th>
					</c:forEach>
					<th style="text-align: center;">Média</th>
				</tr>
				
				<c:set var="_item" />
				
				<c:forEach items="${relatorioConsolidacao.listaNotas}" var="item">
					<tr>
						<td>${item.discente.matricula }</td>
						<td>${item.discente.pessoa.nome }</td>
						<c:if test="${not empty item.medias}">
							<c:forEach items="#{item.medias }" var="media">
								<c:set var="itemAtual" value="${media.item.id }" />
								<c:if test="${itemAtual == _item }">
									/<fmt:formatNumber value="${media.media}" maxFractionDigits="2" /></td>
								</c:if>
								<c:if test="${itemAtual != _item }">
									<c:set var="_item" value="${media.item.id }"/>
									<td style="text-align: center;"><fmt:formatNumber value="${media.media}" maxFractionDigits="2" />
								</c:if>
							</c:forEach>
							<c:forEach items="#{item.mediasGerais }" var="mediaGeral" varStatus="loop">
								<c:if test="${loop.first && item.semanaMaximaAvaliacao > 0 }">
									<td style="text-align: center;"><fmt:formatNumber value="${mediaGeral}" maxFractionDigits="2" />
								</c:if>
								<c:if test="${not loop.first && item.semanaMaximaAvaliacao > item.metodologia.numeroAulasPrimeiraUnidade && not item.metodologia.umaProva }">
									/<fmt:formatNumber value="${mediaGeral}" maxFractionDigits="2" /></td>
								</c:if>
							</c:forEach>
						</c:if>
						<c:if test="${empty item.medias}">
							<c:forEach items="#{relatorioConsolidacao.itensAvaliacao }" var="itemAvaliacao">
								<td style="text-align: center;">-</td>
							</c:forEach>
							<td style="text-align: center;">-</td>
						</c:if>
					</tr>
			    </c:forEach>
			</table>
		</div>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
