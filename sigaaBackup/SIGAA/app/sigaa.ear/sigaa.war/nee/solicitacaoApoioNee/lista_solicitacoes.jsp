<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<c:set var="confirmDelete" value="if (!confirm('Confirma a remo��o desta coordena��o?')) return false" scope="request"/>
<style>
	.naoLida 	{ color: red; }
	
	table.listagem tr.curso td{
		background: #C8D5EC;
		font-weight: bold;
		padding-left: 20px;
	}
</style>
<f:view>
	<h2> <ufrn:subSistema /> &gt; Alunos com Solicita��es de Apoio a CAENE</h2>
	<h:outputText value="#{solicitacaoApoioNee.create}" />
	<div class="infoAltRem">
		<h:graphicImage value="/img/view.gif" style="overflow: visible;" /> : Visualizar Detalhes
		<c:if test="${not solicitacaoApoioNee.infantil}">
			<h:graphicImage value="/img/report.png" style="overflow: visible;" /> : Visualizar Hist�rico
			<h:graphicImage value="/img/view2.gif" style="overflow: visible;" /> : Emitir Atestado de Matr�cula
			<h:graphicImage value="/img/celular_icone.gif" style="overflow: visible;" /> : Visualizar Notas
		</c:if>
		<br/>
		
	</div>
	<table class="listagem">
		<caption>Lista de Solicita��es de Apoio a CAENE (${ fn:length(solicitacaoApoioNee.allSolicitacoesNee) })</caption>
		<thead>
			<tr>
				<th> Discente </th>
				<th> Situa��o </th>
				<th> Telefone(s) </th>
				<th style="text-align: center"> Data de Solicita��o </th>
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
							<c:when test="${ item.lida || !solicitacaoApoioNee.moduloNee }">
								<c:out value="${item.statusAtendimento.denominacao}"/>
							</c:when>
							<c:otherwise>
								<div  style="color: red;"><c:out value="N�O LIDA" /></div>	
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
							<h:commandButton  title="Visualizar Detalhes" image="/img/view.gif" alt="Visualizar Detalhes" action="#{solicitacaoApoioNee.visualizar}" style="border: 0;"/>
						</h:form>
					</td>
					<c:if test="${not solicitacaoApoioNee.infantil}">
						<td>	
							<h:form>
								<input type="hidden" value="${item.id}" name="id"/>
								<h:commandButton  title="Visualizar Hist�rico" image="/img/report.png" alt="Visualizar Hist�rico" action="#{solicitacaoApoioNee.historico}" style="border: 0;"/>
							</h:form>
						</td>
						<td>	
							<h:form>
								<input type="hidden" value="${item.id}" name="id"/>
								<h:commandButton  title="Emitir Atestado de Matr�cula" image="/img/view2.gif" alt="Emitir Atestado de Matr�cula" action="#{solicitacaoApoioNee.atestadoMatricula}" style="border: 0;"/>
							</h:form>
						</td>
						<td>	
							<h:form>
								<input type="hidden" value="${item.discente.id}" name="discente"/>
								<h:commandButton  title="Visualizar Notas" image="/img/celular_icone.gif" alt="Visualizar Notas" action="#{relatorioNotasAluno.gerarRelatorio}" style="border: 0;"/>
							</h:form>
						</td>
					</c:if>
					
				</tr>
			</c:forEach>
			<c:if test="${fn:length(solicitacaoApoioNee.allSolicitacoesNee) == 0}">
				<tr><td align="center"> <h:outputText value="N�o h� Alunos em Atendimento." /> </td></tr>
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
