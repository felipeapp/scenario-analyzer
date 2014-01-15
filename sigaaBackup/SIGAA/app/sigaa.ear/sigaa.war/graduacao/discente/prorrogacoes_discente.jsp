<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<c:set var="confirmDelete" value="if (!confirm('Tem certeza que deseja cancelar esta prorrogação de prazo?')) return false" scope="request"/>

<f:view>
	<h2 class="title"> <ufrn:subSistema /> > Cancelar Prorrogação de Prazo de Conclusão</h2>
	<div class="descricaoOperacao">
		<p>Caro usuário,</p>
		<p> O cancelamento de prorrogações de prazos de conclusão será viável apenas
		    para prorrogações registradas no ano e período vigente.</p>
	</div>
	<center>
	<div class="infoAltRem">
		<h4>Legenda</h4>
        <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover 
	</div>
	</center>

	<h:outputText value="#{prorrogacao.create}" />
	<table class="formulario" width="100%">
		<caption>Cancelar Prorrogação de Prazo</caption>
	
		<thead>
			<td>Tipo</td>
			<td>Número de ${prorrogacao.programaStricto == null ? 'Períodos' : 'Meses'}</td>
			<td>Data</td>
			<td>Usuário que realizou</td>
			<td></td>
		</thead>
	
		<c:if test="${empty prorrogacao.prorrogacoes}">
			<tr><td colspan="3" align="center">
			<font color="red">Não há nenhuma prorrogação de prazo registrada para este aluno</font>
			</td></tr>
		</c:if>
	
		<c:if test="${not empty prorrogacao.prorrogacoes}">
		<c:set var="cal" value="#{prorrogacao.calendarioVigente}"/>
		<c:forEach var="item" items="${prorrogacao.prorrogacoes}" varStatus="status">
		<tr>
			<td>${item.tipoMovimentacaoAluno.descricao}</td>
			<td>${item.valorMovimentacao}</td>
			<td><fmt:formatDate value="${item.dataOcorrencia}" pattern="dd/MM/yyyy"/></td>
			<td>${item.usuarioCadastro.pessoa}</td>
			<td>
				<c:if test="${prorrogacao.calendarioVigente.ano == item.anoReferencia && prorrogacao.calendarioVigente.periodo == item.periodoReferencia}">
				<h:form>
					<input type="hidden" value="${item.id}" name="id" /> 
					<h:commandButton image="/img/delete.gif" alt="Remover" action="#{prorrogacao.cancelarProrrogacao}" onclick="#{confirmDelete}" id="removerProrrogacao"/>
				</h:form>
				</c:if>
			</td>
		</tr>
		</c:forEach>
		</c:if>
		<tfoot>
			<tr>
				<td colspan="5">
				<h:form id="form2">
					<h:commandButton value="<< Escolher Outro Discente" action="#{prorrogacao.buscarDiscenteCancelarProrrogacao}" id="btnCancelarProrrogacao"/> 
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{prorrogacao.cancelar}" id="btnCancelar"/>
				</h:form>
				</td>
			</tr>
		</tfoot>
	</table>


</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
