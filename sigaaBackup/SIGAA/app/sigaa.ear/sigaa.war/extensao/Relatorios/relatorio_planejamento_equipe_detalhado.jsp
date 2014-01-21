<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
	tr.curso td {padding: 15px 0 0; border-bottom: 1px solid #555}
	tr.header td {padding: 3px ; border-bottom: 1px solid #555; background-color: #eee;}
	tr.discente td {border-bottom: 1px solid #888; font-weight: bold; padding-top: 7px;}
	tr.componentes td {padding: 4px 2px 2px ; border-bottom: 1px dashed #888;}
	tr.componentes td.assinatura { padding:2px; border-bottom: 1px solid #888;  width: 40%;}
</style>
<f:view>
	<h2>RELAT�RIO GERAL DE EXTENS�O</h2>

	<div id="parametrosRelatorio">
	<table>
		<tr>
			<th>Tipo de Relat�rio:</th>
			<td><h:outputText value="#{relatorioPlanejamentoMBean.nomeRelatorioAtual}" /></td>
		</tr>		
		<c:if test="${relatorioPlanejamentoMBean.buscaTipoAcao != null and relatorioPlanejamentoMBean.buscaTipoAcao.id > 0}">
			<tr>
				<th>Tipo de A��o:</th>
				<td><h:outputText value="#{relatorioPlanejamentoMBean.buscaTipoAcao.descricao}" /></td>
			</tr>		
		</c:if>		
		<tr>
			<th>A��es com Situa��o:</th> 
			<td><h:outputText value="#{relatorioPlanejamentoMBean.situacaoAcao.descricao}"/></td></tr>
		<tr>
			<th>Realizadas no per�odo de:</th> 
			<td> 
			<h:outputText value="#{relatorioPlanejamentoMBean.dataInicio}">
				<f:convertDateTime pattern="dd/MM/yyyy"  />
			</h:outputText> 
			 a 
			<h:outputText value="#{relatorioPlanejamentoMBean.dataFim}">
				<f:convertDateTime pattern="dd/MM/yyyy"  />
			</h:outputText> 
			</td></tr>
	</table>
	</div>
	<br />
	<h3 class="tituloTabelaRelatorio">
		<h:outputText value="#{relatorioPlanejamentoMBean.nomeRelatorioAtual}"/>
	</h3>

	<table class="tabelaRelatorio" width="100%">
		<thead>
			<tr>
				<th>Tipo da A��o</th> 	
				<th>Categoria</th>		
				<th>Fun��o na Equipe</th>
				<th style="text-align: right;">Total</th>			
			</tr>
		</thead>
		<c:set var="totalDiscentes" value="0"/>
		<tbody>
		<c:forEach items="#{relatorioPlanejamentoMBean.lista}" var="linha">
			<tr class="componentes">
				<td> ${linha.tipo_acao }</td>
				<td> ${linha.categoria}</td>
				<td> ${linha.funcao}</td>
				<td style="text-align: right;"> ${linha.total}	</td>
				<c:set var="totalDiscentes"  value="${totalDiscentes + linha.total}"/>
			</tr>
		</c:forEach>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="2"><b><h:outputText value="#{relatorioPlanejamentoMBean.descricaoTotalRelatorioAtual}"/>:</b></td>
				<td colspan="2" style="text-align: right;"><b>${totalDiscentes}</b> </td>
			</tr>
		</tfoot>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>