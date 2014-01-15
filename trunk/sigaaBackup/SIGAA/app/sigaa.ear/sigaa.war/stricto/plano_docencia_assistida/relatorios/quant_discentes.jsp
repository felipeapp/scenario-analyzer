<%@ include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<a4j:keepAlive beanName="relatoriosDocenciaAssistidaMBean" />
<style>
	table.tabelaRelatorio tbody td {
		border-bottom: 1px solid black;
	}
</style>

<f:view>

	<h2>${relatoriosDocenciaAssistidaMBean.tituloRelatorio}</h2>
	
	<div id="parametrosRelatorio">
		<table>
			<c:if test="${relatoriosDocenciaAssistidaMBean.unidade != null && relatoriosDocenciaAssistidaMBean.unidade.id > 0}">
				<tr>
					<th>Programa:</th>
					<td>${relatoriosDocenciaAssistidaMBean.unidade.nome}</td>
				</tr>
			</c:if>
			<c:if test="${relatoriosDocenciaAssistidaMBean.modalidadeBolsa != null && relatoriosDocenciaAssistidaMBean.modalidadeBolsa.id > 0}">
				<tr>
					<th>Modalidade da Bolsa:</th>
					<td>${relatoriosDocenciaAssistidaMBean.modalidadeBolsa.descricao}</td>
				</tr>
			</c:if>
			<c:if test="${!empty relatoriosDocenciaAssistidaMBean.descricaoNivel}">
				<tr>
					<th>Nível:</th>
					<td>${relatoriosDocenciaAssistidaMBean.descricaoNivel}</td>
				</tr>
			</c:if>			
			<tr>
				<th>Ano-Período:</th>
				<td><h:outputText value="#{relatoriosDocenciaAssistidaMBean.ano}"/>.
					<h:outputText value="#{relatoriosDocenciaAssistidaMBean.periodo}"/></td>
			</tr>
		</table>
	</div>	
	
	<br />

	<table class="tabelaRelatorio" width="100%" align="center">
		<thead>
			<tr>
				<th align="left">Componente Curricular</th>
				<th style="text-align: right">Alunos Atendidos</th>
			</tr>
			<tr><td colspan="2">&nbsp;</td></tr>
		</thead>
		<tbody>
			<c:set var="total" value="0"/>
			<c:set var="totalUnidade" value="0"/>
			<c:set var="idUnidade" value="0"/>
			<c:forEach var="item" items="${ relatoriosDocenciaAssistidaMBean.listagem }" varStatus="loop">
				<c:if test="${idUnidade != item.idunidade}">
					<c:if test="${!loop.first}">
						<tr class="linhaCinza">
							<td style="text-align: right">TOTAL:</td>					
							<td style="text-align: right">${totalUnidade}</td>
						</tr>
						<tr><td colspan="2">&nbsp;</td></tr>						
					</c:if>
					<tr class="linhaCinza">
						<td colspan="2" style="text-align: center;">${item.departamento}</td>
					</tr>
					<c:set var="totalUnidade" value="0"/>
				</c:if>
				<tr>
					<td align="left">${ item.codigo } - ${ item.componente }</td>					
					<td style="text-align: right">${item.total}</td>
				</tr>
				<c:set var="total" value="${total + item.total}"/>
				<c:set var="totalUnidade" value="${totalUnidade + item.total}"/>
				<c:set var="idUnidade" value="${item.idunidade}"/>
			</c:forEach>
			<tr class="linhaCinza">
				<td style="text-align: right">TOTAL:</td>					
				<td style="text-align: right">${totalUnidade}</td>
			</tr>
			<tr><td colspan="2">&nbsp;</td></tr>			
			<tr class="linhaCinza">
				<td style="text-align: right">TOTAL GERAL:</td>					
				<td style="text-align: right">${total}</td>
			</tr>				
		</tbody>
	</table>
	
</f:view>

<%@ include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>