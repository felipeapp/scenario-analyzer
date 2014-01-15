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
				<th>Tipo da Ação</th> 	
				<th style="text-align: right;">Ações</th>
				<th style="text-align: right;">Discentes</th>  
				<th style="text-align: right;">Docentes</th>
				<th style="text-align: right;">Téc. Admin.</th>
				<th style="text-align: right;">Comunidade Externa</th>
			</tr>
		</thead>
		<tbody>
		<c:set var="total" value="0"/>
		<c:forEach items="#{relatorioPlanejamentoMBean.lista}" var="linha" >
			<tr class="componentes">
				<td> ${linha.tipoacao}</td>
				<td style="text-align: right;"> ${linha.totalacoes}</td>
				<td style="text-align: right;"> ${linha.totaldiscentes}</td>
				<td style="text-align: right;"> ${linha.totaldocentes}</td>
				<td style="text-align: right;"> ${linha.totaltecnicosadministrativo}</td>
				<td style="text-align: right;"> ${linha.totalcomunidadeexterna}
				
				<c:set var="totalacoes" value="${totalacoes + linha.totalacoes}"/>
				<c:set var="totaldiscentes" value="${totaldiscentes + linha.totaldiscentes}"/>
				<c:set var="totaldocentes" value="${totaldocentes + linha.totaldocentes}"/>
				<c:set var="totaltec" value="${totaltec + linha.totaltecnicosadministrativo}"/>
				<c:set var="totalcomunidadeexterna" value="${totalcomunidadeexterna + linha.totalcomunidadeexterna}"/>
			</tr>
		</c:forEach>
		</tbody>
		<tfoot>
			<tr>
				<td>Totais:</td>
				<td style="text-align: right;"> ${totalacoes}</td>
				<td style="text-align: right;"> ${totaldiscentes}</td>
				<td style="text-align: right;"> ${totaldocentes}</td>
				<td style="text-align: right;"> ${totaltec}</td>
				<td style="text-align: right;"> ${totalcomunidadeexterna}
			</tr>
		</tfoot>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>