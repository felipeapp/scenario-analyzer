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
			<th>Tipo de A��o:</th> 
			<td>
				<c:if test="${relatoriosAtividades.tipoAtividadeExtensao.id == 0}"> TODOS OS TIPOS </c:if>
				<h:outputText value="#{relatoriosAtividades.tipoAtividadeExtensao.descricao}"/>
			</td>
		</tr>
				
		<tr>
			<th>A��es com Situa��o:</th> 
			<td><h:outputText value="#{relatoriosAtividades.situacaoAtividade.descricao}"/></td>
		</tr>
		
		<tr>
			<th>Realizadas no per�odo de:</th> 
			<td><h:outputText value="#{relatoriosAtividades.dataInicio}">
					<f:convertDateTime pattern="dd/MM/yyyy" />
				</h:outputText>
			 a  <h:outputText value="#{relatoriosAtividades.dataFim}">
			 		<f:convertDateTime pattern="dd/MM/yyyy" />
				</h:outputText>
			</td>
		</tr>		
	</table>
	
	</div>
	<br />
	
	<h3 class="tituloTabelaRelatorio"> <h:outputText value="Relat�rio de A��es por Local de Realiza��o"/></h3>

	<table class="tabelaRelatorio" width="100%">
		<thead>
			<tr>
				<th>Local de Realiza��o</th>
				<th style="text-align:right;">Total</th>
			</tr>
		</thead>
		<c:set var="total" value="0"/>
		<tbody>
		<c:forEach items="#{relatoriosAtividades.resultado}" var="item">
			<tr class="componentes">
				<td> ${item[0] }</td>
				<td style="text-align: right;"> ${item[1]}</td>
				<c:set var="total"  value="${total + item[1]}"/>
			</tr>
		</c:forEach>
		</tbody>
		<tfoot>
			<tr>
				<td><b><h:outputText value="Total de Locais de Realiza��o:"/></b></td>
				<td style="text-align: right; font-weight: bold"> ${total}</td>
			</tr>
		</tfoot>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>