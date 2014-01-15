<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
	tr.curso td {padding: 15px 0 0; border-bottom: 1px solid #555}
	tr.header td {padding: 3px ; border-bottom: 1px solid #555; background-color: #eee;}
	tr.discente td {border-bottom: 1px solid #888; font-weight: bold; padding-top: 7px;}
	tr.componentes td {padding: 4px 2px 2px ; border-bottom: 1px dashed #888;}
	tr.componentes td.assinatura { padding:2px; border-bottom: 1px solid #888;  width: 40%;}
</style>
<f:view>
	
	<h2>RELATÓRIO GERAL DE EXTENSÃO</h2>

	<div id="parametrosRelatorio">
	<table>	
		<tr>
			<th>Tipo de Relatório:</th>
			<td><h:outputText value="#{relatorioPlanejamentoMBean.nomeRelatorioAtual}" /></td>
		</tr>
		<c:if test="${relatorioPlanejamentoMBean.buscaAreaTematica != null and relatorioPlanejamentoMBean.buscaAreaTematica.id > 0}">
			<tr>
				<th>Área Temática:</th>
				<td><h:outputText value="#{relatorioPlanejamentoMBean.buscaAreaTematica.descricao}" /></td>
			</tr>		
		</c:if>
		
		<tr>
			<th>Ações com Situação:</th> 
			<td><h:outputText value="#{relatorioPlanejamentoMBean.situacaoAcao.descricao}"/></td>
		</tr>
		
		<tr>
			<th>Realizadas no período de:</th> 
			<td><h:outputText value="#{relatorioPlanejamentoMBean.dataInicio}">
					<f:convertDateTime pattern="dd/MM/yyyy" />
				</h:outputText>
			 a  <h:outputText value="#{relatorioPlanejamentoMBean.dataFim}">
			 		<f:convertDateTime pattern="dd/MM/yyyy" />
				</h:outputText>
			</td>
		</tr>		
	</table>
	</div>
	<br />
	<h3 class="tituloTabelaRelatorio"><h:outputText value="#{relatorioPlanejamentoMBean.nomeRelatorioAtual}"/> (<h:outputText value="#{relatorioPlanejamentoMBean.buscaAreaTematica.descricao}" />)</h3>

	<table class="tabelaRelatorio" width="100%">
		<thead>
			<tr>
				<th>Tipo de Produto</th> 	
				<th>Quant.</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="#{relatorioPlanejamentoMBean.lista}" var="linha" >
			<tr class="componentes">
				<td> ${linha.tipoproduto}</td>
				<td style="text-align: right;"> ${linha.totalprodutos}</td>
				
				<c:set var="totalprodutos" value="${totalprodutos + linha.totalprodutos}"/>
			</tr>
		</c:forEach>
		</tbody>
		<tfoot>
			<tr>
				<td>Total:</td>
				<td style="text-align: right;"> ${totalprodutos}</td>
			</tr>
		</tfoot>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>