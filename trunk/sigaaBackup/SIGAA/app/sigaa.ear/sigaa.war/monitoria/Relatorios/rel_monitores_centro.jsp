<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
	tr.curso td {border-bottom: 1px solid #555;  }
	tr.titulo td {border-bottom: 2.5px solid #555}
	tr.header td {padding: 0px ; background-color: #DEDFE3; border: 1px solid #555; font-weight: bold; border: 1px solid #000;}
	tr.componentes td {padding: 5px 2px 2px; border-bottom: 1px solid #000; border: 1px solid #c0c0c0; }
	tr.total td {padding: 5px 2px 2px; border: 1px solid #000; font-weight: bold;}
	tr.espaco td {padding: 12px;}
</style>
<f:view>

	<table width="100%" style="font-size: 11px;">
		<caption><td align="center"><h2>Relatório de Monitores por Centro</h2></td></b>
	</table>
	<br />
	<br />
	<div id="parametrosRelatorio">
		<table >
			<c:if test="${comissaoMonitoria.unidade.id > 0}">
			<tr>
				<th>Unidade:</th>
				<td> <h:outputText value="#{comissaoMonitoria.unidade.nome}" /></td>
			</tr>
			</c:if>
			<c:if test="${comissaoMonitoria.ano > 0}">
			<tr>
				<th>Ano:</th>
				<td> <h:outputText value="#{comissaoMonitoria.ano}" /></td>
			</tr>
			</c:if>
			<c:if test="${comissaoMonitoria.idTipoMonitor > 0}">
			<tr>
				<th>Tipo de Vínculo:</th>
				<c:if test="${comissaoMonitoria.idTipoMonitor == 2}">
				<td> <h:outputText value="BOLSISTA" /></td>
				</c:if>
				<c:if test="${comissaoMonitoria.idTipoMonitor == 1}">
				<td> <h:outputText value="NÃO REMUNERADO" /></td>
				</c:if>
				<c:if test="${comissaoMonitoria.idTipoMonitor == 3}">
				<td> <h:outputText value="NÃO CLASSIFICADO" /></td>
				</c:if>
				<c:if test="${comissaoMonitoria.idTipoMonitor == 4}">
				<td> <h:outputText value="EM ESPERA" /></td>
				</c:if>
			</tr>
			</c:if>
			
				 
		</table>
	</div>
	
	<br />
	
    <c:set var="_departamento" />
    <c:set var="_total" value="0"/>
    <c:set var="_totalGeral" value="0"/>
    
	<table cellspacing="1" width="100%" style="font-size: 11px;">
		<tr class="titulo">
			<td colspan="8"></td>
		</tr>
		
		<c:forEach items="#{comissaoMonitoria.projetos}" var="linha" varStatus="indice">
		<c:set var="departamentoAtual" value="${linha[4]}"/>
		
		<c:if  test="${_departamento != departamentoAtual}">
			<tr class="header">
				<td colspan="4" style="background-color: #c0c0c0; border: 1px solid #555; " ><b>${linha[4]} - ${linha[6]}</b></td>
			</tr>
			<tr class="header">
				<td align="left">Título</td>
				<td align="center">Ano</td>
				<td align="center">Bolsas Concedidas</td>
				<td align="center">Monitores</td>
			</tr>
		</c:if>
		
		<c:set var="_departamento" value="${departamentoAtual}"/>
		
		<tr class="componentes">
			<td align="left">${linha[1]}</td> <!-- titulo -->
			<td align="center">${linha[3]}</td> <!-- ano -->
			<td align="right">${linha[5]}</td> <!-- bolsas concedidas -->
			<td align="right">${linha[2]}</td> <!-- monitores -->
			<c:set var="_total" value="${_total + linha[2]}" />
		</tr>
		
		<c:set var="proximo" value="${comissaoMonitoria.projetos[indice.index+1][4]}" ></c:set>
		
		<c:if test="${departamentoAtual != proximo }">
			<tr class="componentes" >
				<td align="right" colspan="3"><b>Total:</b></td>
				<td align="right"><b>${_total}</b></td>
				<c:set var="_totalGeral" value="${_totalGeral + _total}"/>
			</tr>
			<tr class="espaco">
				<td colspan="4">&nbsp;</td>
			</tr>
			<c:set var="_total" value="0"/>
		</c:if>
		
		</c:forEach>
		
		<tr class="titulo">
			<td colspan="4" align="right"><b>Total de Monitores: ${_totalGeral}<b/></td>
		</tr>
		
	</table>
	<br />
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>