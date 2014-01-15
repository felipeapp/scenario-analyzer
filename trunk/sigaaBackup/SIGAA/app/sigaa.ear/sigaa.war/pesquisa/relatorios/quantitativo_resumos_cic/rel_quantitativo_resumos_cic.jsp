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
		<caption><td align="center"><h2>Relatório Quantitativo de Resumos CIC</h2></td></b>
	</table>
	<br />
	<br />
	<div id="parametrosRelatorio">
		<table >
			<c:if test="${ relatorioResumosCic.congresso.id > 0 }">
			<tr>
				<th>Congresso de Iniciação Científica:</th>
				<td> <h:outputText value="#{relatorioResumosCic.congresso.descricao}" /></td>
			</tr>
			</c:if>
				 
		</table>
	</div>
	
	<br />
	<c:set var="_centro" />
    <c:set var="_ano" />
    <c:set var="_total" value="0"/>
    <c:set var="_totalGeral" value="0"/>
    
	<table cellspacing="1" width="100%" style="font-size: 11px;">
		<tr class="titulo">
			<td colspan="2"></td>
		</tr>
		<c:forEach items="#{relatorioResumosCic.lista}" var="linha" varStatus="indice">
		<c:set var="centroAtual" value="${linha.centro}"/>
		
		<c:if  test="${_centro != centroAtual}">
			<tr class="componentes">
				<td colspan="2" style="background-color: #c0c0c0; border: 1px solid #555; " ><b>${linha.unidade} - ${linha.centro}</b></td>
			</tr>
			<tr class="header" >
				<td align="left"> Curso</td>
				<td align="right"> Quantidade de Resumos</td>
			</tr>
		</c:if>
		<c:set var="_centro" value="${centroAtual}"/>
			<tr class="componentes">
				<td align="left">  ${linha.curso} - ${linha.municipio} </td>
				<td align="right">  ${linha.quantidade_resumos}</td>
				<c:set var="_total" value="${_total + linha.quantidade_resumos}" />
			</tr>
			
		<c:set var="proximo" value="${relatorioResumosCic.lista[indice.index+1].centro}" ></c:set>
		<c:if test="${centroAtual != proximo }">
			<tr class="componentes" >
				<td align="right" colspan="1"><b>Total:</b></td>
				<td align="right"><b>${_total}</b></td>
				<c:set var="_totalGeral" value="${_totalGeral + _total}"/>
			</tr>
			<tr class="espaco">
				<td colspan="2">&nbsp;</td>
			</tr>
			<c:set var="_total" value="0"/>
		</c:if>
		</c:forEach>
		<tr class="titulo">
			<td colspan="2" align="right"><b>Total de Resumos: ${_totalGeral}<b/></td>
		</tr>
	</table>
	<br />
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>