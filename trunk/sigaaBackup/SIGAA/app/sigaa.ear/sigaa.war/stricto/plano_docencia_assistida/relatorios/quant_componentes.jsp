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
		<tbody>
			<c:set var="ativos" value="0"/>
			<c:set var="aprovados" value="0"/>
			<c:set var="reprovados" value="0"/>
			<c:set var="trancados" value="0"/>
			<c:set var="tUnidAtivos" value="0"/>
			<c:set var="tUnidAprovados" value="0"/>
			<c:set var="tUnidReprovados" value="0"/>
			<c:set var="tUnidTrancados" value="0"/>
			<c:set var="idUnidade" value="0"/>
			<c:forEach var="item" items="${ relatoriosDocenciaAssistidaMBean.listagem }" varStatus="loop">
				<c:if test="${idUnidade != item.id_unidade}">
					<c:if test="${!loop.first}">
						<tr class="linhaCinza">
							<td style="text-align: right">Total do Departamento:</td>					
							<td style="text-align: right">${tUnidAtivos}</td>
							<td style="text-align: right">${tUnidAprovados}</td>
							<td style="text-align: right">${tUnidReprovados}</td>
							<td style="text-align: right">${tUnidTrancados}</td>
						</tr>
						<tr><td colspan="5">&nbsp;</td></tr>						
					</c:if>
					<tr class="linhaCinza">
						<td colspan="5">${item.departamento}</td>
					</tr>
					<tr class="linhaCinza">
						<th align="left">Componente Curricular</th>
						<th style="text-align: right; width: 80px;">Ativos</th>
						<th style="text-align: right; width: 80px;">Aprovados</th>
						<th style="text-align: right; width: 80px;">Reprovados</th>
						<th style="text-align: right; width: 80px;">Trancados</th>	
					</tr>					
					<c:set var="tUnidAtivos" value="0"/>
					<c:set var="tUnidAprovados" value="0"/>
					<c:set var="tUnidReprovados" value="0"/>
					<c:set var="tUnidTrancados" value="0"/>
				</c:if>
				<tr>
					<td align="left">${ item.codigo } - ${ item.disciplina }</td>					
					<td style="text-align: right">${item.ativos}</td>
					<td style="text-align: right">${item.aprovados}</td>
					<td style="text-align: right">${item.reprovados}</td>
					<td style="text-align: right">${item.trancados}</td>
				</tr>
				
				<c:set var="tUnidAtivos" value="${tUnidAtivos + item.ativos }"/>
				<c:set var="tUnidAprovados" value="${tUnidAprovados + item.aprovados }"/>
				<c:set var="tUnidReprovados" value="${tUnidReprovados + item.reprovados }"/>
				<c:set var="tUnidTrancados" value="${tUnidTrancados + item.trancados }"/>				
					
				<c:set var="ativos" value="${ativos + item.ativos}"/>
				<c:set var="aprovados" value="${aprovados + item.aprovados}"/>
				<c:set var="reprovados" value="${reprovados + item.reprovados}"/>
				<c:set var="trancados" value="${trancados + item.trancados}"/>				
				
				<c:set var="idUnidade" value="${item.id_unidade}"/>
			</c:forEach>
			<tr class="linhaCinza">
				<td style="text-align: right">Total do Depatamento:</td>						
				<td style="text-align: right">${tUnidAtivos}</td>
				<td style="text-align: right">${tUnidAprovados}</td>
				<td style="text-align: right">${tUnidReprovados}</td>
				<td style="text-align: right">${tUnidTrancados}</td>
			</tr>
			<tr><td colspan="5">&nbsp;</td></tr>			
			<tr class="linhaCinza">
				<td style="text-align: right">Total Geral:</td>					
				<td style="text-align: right">${ativos}</td>
				<td style="text-align: right">${aprovados}</td>
				<td style="text-align: right">${reprovados}</td>
				<td style="text-align: right">${trancados}</td>
			</tr>				
		</tbody>
	</table>
	
</f:view>

<%@ include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>