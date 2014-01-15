<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<c:set var="confirmDelete" value="if (!confirm('Confirma a remoção desta coordenação?')) return false" scope="request"/>
<style>
	.naoLida 	{ color: red; }
	
	table.listagem tr.curso td{
		background: #C8D5EC;
		font-weight: bold;
		padding-left: 20px;
	}
</style>
<f:view>
	<h2> <ufrn:subSistema /> &gt; Atendimentos de NEE</h2>
	<h:outputText value="#{solicitacaoApoioNee.create}" />
	<div class="infoAltRem">
	 <img src="${ctx}/img/alterar.gif"/>: Alterar
		<h:graphicImage value="/img/view.gif" style="overflow: visible;" /> : Visualizar Detalhes
		<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" /> : Alterar Situação de Atendimento
		<h:graphicImage value="/img/report.png" style="overflow: visible;" /> : Alterar Parecer de NEE
		<br/>
		
	</div>
	<table class="listagem">
		<caption>Lista de Solicitações de Apoio a CAENE (${ fn:length(solicitacaoApoioNee.allSolicitacoesNee) })</caption>
		<thead>
			<tr>
				<th> Discente </th>
				<th> Situação </th>
				<th> Telefone(s) </th>
				<th style="text-align: center"> Data de Solicitação </th>
				<th colspan="4"></th>
			</tr>
		</thead>
		<tbody>
			<c:set var="idFiltro" value="-1" />
			<c:forEach var="item" items="${solicitacaoApoioNee.allSolicitacoesNee}" varStatus="loop">
				<c:set var="idLoop" value="${item.discente.curso.id}" />
				
				<c:if test="${ idFiltro != idLoop}">
					<c:set var="idFiltro" value="${item.discente.curso.id}" />
					<tr class="curso">
						<td colspan="9">
							${item.discente.curso.descricao}
						</td>
					</tr>
				</c:if>	
				
				<tr class="${ loop.index % 2 == 0 ? 'linhaPar': 'linhaImpar' }">
					<td> ${item.discente.nome} (${item.discente.matricula})</td>
					<td> 
						<c:choose>
							<c:when test="${item.lida || solicitacaoApoioNee.solicitacaoCoordenador}">
								<c:out value="${item.statusString}"/>
							</c:when>
							<c:otherwise>
								<div  style="color: red;"><c:out value="NÃO LIDA" /></div>	
							</c:otherwise>
						</c:choose>
					</td>
					<td> 
						${item.discente.pessoa.telefone} 
						<c:if test="${item.discente.pessoa.telefone != '' and item.discente.pessoa.celular != ''}">/</c:if> 
						${item.discente.pessoa.celular}
					</td>
					<td style="text-align: center"> <fmt:formatDate value="${item.dataCadastro }" pattern="dd/MM/yyyy"/> </td>
					<td>
						<h:commandLink action="#{solicitacaoApoioNee.visualizar}" id="linkVisualizarDetalhes"> 
							<h:graphicImage url="/img/view.gif" alt="Visualizar Detalhes" title="Visualizar Detalhes"/>
							<f:param name="id" value="#{item.id}"/>
						</h:commandLink>
					</td>
					<td>	
						<h:commandLink action="#{solicitacaoApoioNee.visualizar}" id="linkAlterarSituacaoAtendimento"> 
							<h:graphicImage url="/img/alterar.gif" alt="Alterar Situação de Atendimento" title="Alterar Situação de Atendimento"/>
							<f:param name="id" value="#{item.id}"/>
						</h:commandLink>
					</td>
					<td>	
						<h:commandLink action="#{solicitacaoApoioNee.atualizar}" id="linkVisualizarSolicitacao"> 
							<h:graphicImage url="/img/alterar.gif" alt="Alterar Parecer de NEE" title="Alterar Parecer de NEE"/>
							<f:param name="id" value="#{item.id}"/>
						</h:commandLink>
					</td>
				</tr>
			</c:forEach>
			<c:if test="${fn:length(solicitacaoApoioNee.allSolicitacoesNee) == 0}">
				<tr><td align="center"> <h:outputText value="Não há Solicitações de Apoio a CAENE em Atendimento." /> </td></tr>
			</c:if>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="9" align="center">
					<input type="button" value="<< Voltar"  onclick="javascript: history.back();" id="voltar" />
				</td>
			</tr>
		</tfoot>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
