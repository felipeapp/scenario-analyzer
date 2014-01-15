<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
	tr.curso td {border-bottom: 1px double #555;  }
	tr.titulo td {border-bottom: 2.5px solid #555}
	tr.header td {padding: 0px ; background-color: #DEDFE3; border-bottom: 1px solid #555; font-weight: bold; border: 1px solid #000;}
	tr.componentes td {padding: 5px 2px 2px; border-bottom: 1px solid #000; border: 1px solid #c0c0c0; }
	tr.total td {padding: 5px 2px 2px; border: 1px solid #000; font-weight: bold;}
	tr.espaco td {padding: 12px;}
</style>
<f:view>

	<table width="100%" style="font-size: 11px;">
		<caption><td align="center"><h2>Relatório de Pendências de Indicação</h2></td></b>
	</table>
	<br />
	<br />
	<div id="parametrosRelatorio">
		<table >
			<tr>
				<th>Edital:</th>
				<td> <h:outputText value="#{relatorioPendenciasIndicacao.editalPesquisa.descricao}" /></td>
			</tr>
		</table>
	</div>
	
	<br />
	
    <c:set var="_centro" />
    <c:set var="_total" value="0"/>
    
	<table cellspacing="1" width="100%" style="font-size: 11px;">
		<tr class="titulo">
			<td colspan="5"></td>
		</tr>
		
		<tr class="header" >
			<td align="left"> Departamento</td>
			<td align="left"> Docente</td>
			<td align="left"> Modalidade</td>
			<td align="center"> Recebidas</td>
			<td align="center"> Indicadas</td>
		</tr>
		
		<c:forEach items="#{relatorioPendenciasIndicacao.lista}" var="linha" varStatus="indice">
			<c:if test="${ _centro != linha.idcentro }">
				<c:set var="_centro" value="${ linha.idcentro }"/>
				<tr class="curso">
					<td colspan="5">${ linha.centro }</td>
				</tr>
			</c:if>
			<tr class="componentes">
				<td align="left">  ${linha.departamento}</td>
				<td align="left">  ${linha.docente}</td>
				<td align="left">  ${linha.modalidade}</td>
				<td align="center">  ${linha.recebidas}</td>
				<td align="center">  ${linha.indicadas}</td>
			</tr>
		</c:forEach>
	</table>
	<br />
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>