<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
	tr.curso td {border-bottom: 1px solid #555;  }
	tr.titulo td {border-bottom: 2.5px solid #555}
	tr.header td {padding: 0px ; background-color: #EFEFEF; border-bottom: 1px solid #555; font-weight: bold; border: 1px solid #000;}
	tr.componentes td {padding: 5px 2px 2px; border-bottom: 1px solid #000; border: 1px solid #c0c0c0; }
	tr.total td {padding: 5px 2px 2px; border: 1px solid #000; font-weight: bold;}
	tr.espaco td {padding: 12px;}
</style>
<f:view>

	<table width="100%" style="font-size: 11px;">
		<caption><td align="center"><h2>Total de Público Atingido com Base nos Relatórios Submetidos</h2></td></b>
	</table>
	<br />
	<br />
	<div id="parametrosRelatorio">
		<table >
			<c:if test="${ not empty relatorioPublicoAtingido.dataInicio }">
			<tr>
				<th>Data Inicial Projeto :</th>
				<td> <h:outputText value="#{relatorioPublicoAtingido.dataInicio}" /></td>
			</tr>
			</c:if>
			<c:if test="${ not empty relatorioPublicoAtingido.dataFim }">
			<tr>
				<th>Data Final Projeto :</th>
				<td> <h:outputText value="#{relatorioPublicoAtingido.dataFim}" /></td>
			</tr>
			</c:if>
				 
		</table>
	</div>
	
	<br />
	<c:set var="_tipoRelatorio" />
    <c:set var="_ano" />
    <c:set var="_total" value="0"/>
    <c:set var="_totalGeral" value="0"/>
    
	<table cellspacing="1" width="100%" style="font-size: 11px;">
		<tr class="titulo">
			<td colspan="6"></td>
		</tr>
		<c:forEach items="#{relatorioPublicoAtingido.lista}" var="linha" varStatus="indice">
		<c:set var="tipoAtual" value="${linha.tipo}"/>
		
		<c:if  test="${_tipoRelatorio != tipoAtual}">
			<tr class="componentes">
				<td colspan="6" style="background-color: #c0c0c0; border: 1px solid #555; " ><b>${linha.tipo}</b></td>
			</tr>
			<tr class="header" >
				<td align="left"> Projeto</td>
				<td align="center"> Período Projeto</td>
				<td align="left"> Tipo Relatório</td>
				<td align="center"> Data Relatório</td>
				<td align="left"> Coordenador</td>
				<td align="right"> Público Atingido</td>
			</tr>
		</c:if>
		<c:set var="_tipoRelatorio" value="${tipoAtual}"/>
			<tr class="componentes">
				<td align="left">  ${linha.titulo} </td>
				<td align="center"> <ufrn:format type="data" valor="${linha.ini}" /> a <ufrn:format type="data" valor="${linha.fi}" />  </td>
				<td align="left">  ${linha.discriminador}</td>
				<td align="center"> <ufrn:format type="data" valor="${linha.data}" /> </td>
				<td align="left">  ${linha.coordenador}</td>
				<td align="right">  ${linha.publico}</td>
				<c:set var="_total" value="${_total + linha.publico}" />
			</tr>
			
		<c:set var="proximo" value="${relatorioPublicoAtingido.lista[indice.index+1].tipo}" ></c:set>
		<c:if test="${tipoAtual != proximo }">
			<tr class="componentes" >
				<td align="right" colspan="4"><b>Total:</b></td>
				<td align="right"><b>${_total}</b></td>
				<c:set var="_totalGeral" value="${_totalGeral + _total}"/>
			</tr>
			<tr class="espaco">
				<td colspan="6">&nbsp;</td>
			</tr>
			<c:set var="_total" value="0"/>
		</c:if>
		</c:forEach>
		<tr class="titulo">
			<td colspan="6" align="right"><b>Público Total: ${_totalGeral}<b/></td>
		</tr>
	</table>
	<br />
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>