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
	
	<h3 class="tituloTabelaRelatorio"> <h:outputText value="Relatório de Docentes por Nível de Ensino"/></h3>

	<table class="tabelaRelatorio" width="100%">
		<thead>
			<tr>
				<th>Nível de Ensino</th>
				<th style="text-align:right;">Total</th>
			</tr>
		</thead>
		<c:set var="total" value="0"/>
		<tbody>
		<c:forEach items="#{relatorioPlanejamentoMBean.lista}" var="item">
			<tr class="componentes">
				<td> ${item[0] }</td>
				<td style="text-align: right;"> ${item[1]}</td>
				<c:set var="total"  value="${total + item[1]}"/>
			</tr>
		</c:forEach>
		</tbody>
		<tfoot>
			<tr>
				<td><b><h:outputText value="Total de Docentes:"/></b></td>
				<td style="text-align: right; font-weight: bold"> ${total}</td>
			</tr>
		</tfoot>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>