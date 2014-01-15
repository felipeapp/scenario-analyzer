<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
	tr.curso td {padding: 15px 0 0; border-bottom: 1px solid #000000; color: black;}
	tr.header td {padding: 3px ; border-bottom: 1px solid #555; background-color: #eee;}
	tr.foot td {padding: 3px ; border-bottom: 1px solid #555; background-color: #eee; font-weight: bold; font-size: 13px; }
	tr.discente td {border-bottom: 1px solid #888; font-weight: bold; padding-top: 7px;}
	tr.componentes td {padding: 4px 2px 2px ; border-bottom: 1px dashed #888;}
	tr.componentes td.assinatura { padding:2px; border-bottom: 1px solid #888;  width: 40%;}
	hr{
		color: #000 !important;
		background-color: #000 !important;
	}
	
</style>
<f:view>
	
	<h2 class="tituloTabela"><b>Lista de Docentes para Eleição</b></h2>
	
	<table cellspacing="1" width="100%" style="font-size: 10px;">
		<tr class="header">
			<td style="text-align: right;"><b>SIAPE</b></td>
			<td><b>Nome</b></td>
			<td><b>Assinatura</b></td>
		</tr>
		<c:forEach items="#{relatorioEleicaoCoordenadoMBean.servidores}" var="linha">
			<c:set var="total" value="${total + 1}" />
			<tr class="componentes">
				<td style="text-align: right;">
					${linha.siape}
				</td>
				<td>
					${linha.nome}
				</td>
				<td class="assinatura">
				</td>
			</tr>
		</c:forEach>
	<c:if test="${total>0}">
		<tr class="foot">
			<td colspan="3" style="text-align: center;">Total: ${total} </td>
		</tr>
	
	</c:if>
	<c:if test="${total<1}">
		<p><i>Nenhum resultado encontrado.</i></p>
	</c:if>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>