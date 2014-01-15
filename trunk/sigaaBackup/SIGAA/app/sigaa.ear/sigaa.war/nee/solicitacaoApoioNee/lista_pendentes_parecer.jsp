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
	<h2> <ufrn:subSistema /> &gt; Alunos com Solicitações de Apoio Pendentes de Parecer</h2>
	<h:outputText value="#{solicitacaoApoioNee.create}" />
	<div class="infoAltRem">
		<h:graphicImage value="//img/seta.gif" style="overflow: visible;" />: Emitir Parecer<br />
	</div>
	<table class="listagem">
		<caption>Lista de Solicitações Pendentes de Parecer (${ fn:length(solicitacaoApoioNee.solicitacaoApoioPendentesParecer) })</caption>
		<thead>
			<tr>
				<th> Discente </th>
				<th> Situação </th>
				<th> Telefone(s) </th>
				<th style="text-align: center"> Data de Solicitação </th>
				<th></th>
			</tr>
		</thead>
		<tbody>
			<c:set var="idFiltro" value="-1" />
			<c:forEach var="item" items="${solicitacaoApoioNee.solicitacaoApoioPendentesParecer}" varStatus="loop">
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
								<c:out value="${item.statusAtendimento.denominacao}"/>
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
						<h:form>
							<input type="hidden" value="${item.id}" name="id"/>
							<h:commandButton  title="Emitir Parecer" image="/img/seta.gif" alt="Remover" action="#{solicitacaoApoioNee.atualizar}" style="border: 0;"/>
						</h:form>
					</td>
				</tr>
			</c:forEach>
			<c:if test="${fn:length(solicitacaoApoioNee.solicitacaoApoioPendentesParecer) == 0}">
				<tr><td align="center"> <h:outputText value="Não há Solicitações de Apoio a CAENE pendentes de Parecer." /> </td></tr>
			</c:if>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="6" align="center">
					<h:form>
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{solicitacaoApoioNee.cancelar}" id="btnCancelar"/>
					</h:form>
				</td>
			</tr>
		</tfoot>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
