<%@taglib uri="/tags/a4j" prefix="a4j"%>
<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
.alinharDireita{ 
	text-align:right !important;
}
.alinharEsquerda{ 
	text-align:left !important;
} 
.alinharCentro{ 
	text-align:center !important;
}
.destacado{
	color: red;
}
tr.foot td {padding: 3px ; border-bottom: 1px solid #555; background-color: #eee; font-weight: bold; font-size: 13px; }
</style>
<f:view>
<c:if test="${relatoriosSaeMBean.relatorioBolsasSIPAC == false}">
	<h2>Relatório de Discentes Prioritários que solicitaram Bolsa Auxílio </h2>
</c:if>
<c:if test="${relatoriosSaeMBean.relatorioBolsasSIPAC == true}">
 	<h2>Relatório de Bolsistas (SIPAC) e Situação de Carência </h2>
 </c:if>
    <c:if test="${relatoriosSaeMBean.relatorioBolsasSIPAC == false}">
		<div id="parametrosRelatorio">
			<table>
				<tr>
					<th>Tipo da Bolsa:</th>
					<td>${ relatoriosSaeMBean.tipoBolsaAuxilio.denominacao }</td>
				</tr>
				<tr>
					<th>Ano-Período:</th>
					<td> 
						${ relatoriosSaeMBean.ano } - ${ relatoriosSaeMBean.periodo }
					</td>
				</tr>
				<tr>
					<th>Curso:</th>
					<td>
						<c:if test="${ not empty relatoriosSaeMBean.curso.descricao }"> 
							${ relatoriosSaeMBean.curso.descricao }
						</c:if>
						<c:if test="${ empty relatoriosSaeMBean.curso.descricao }"> 
							TODOS
						</c:if>
					</td>
				</tr>
			</table>
		</div>
		<br>
		<c:set var="sim" value="0"></c:set>
		<table class="tabelaRelatorioBorda" width="100%">	
			<thead>
			<tr>
				<th class="alinharCentro"> Matrícula </th>
				<th class="alinharEsquerda"> Discente </th>
				<th class="alinharCentro"> Prioritário? </th>
			</tr>
			</thead>
				<c:forEach var="item" items="#{relatoriosSaeMBean.listaDiscentes}">
					<tr>
						<td class="alinharCentro"> ${item.matricula} </td>
						<td class="alinharEsquerda"> ${item.pessoa.nome} </td>										
						<td align="center"> 
							<c:if test="${item.carente}">
								SIM
								<c:set var="sim" value="${sim + 1}" ></c:set>
							</c:if> 
							<c:if test="${!item.carente}">NÃO</c:if>
						</td>
					</tr>
				</c:forEach>
		</table>
		<table align="center">
			<tfoot>
			<tr>
				<td colspan="6" style="text-align: center; font-weight: bold;">
					Discente(s) encontrado(s): ${fn:length(relatoriosSaeMBean.listaDiscentes)} 
					</td>
			</tr>
			<c:if test="${not empty relatoriosSaeMBean.listaDiscentes }">
			<tr>
				<td colspan="6" style="text-align: center; font-weight: bold;">
					Discente(s) carente(s): ${ sim } 
					(<ufrn:format type="valor" valor="${((sim/fn:length(relatoriosSaeMBean.listaDiscentes)) *100) }"/>%)
				</td>
			</tr>
			<tr>
				<td colspan="6" style="text-align: center; font-weight: bold;">
				 <c:set var="nao" value=" ${fn:length(relatoriosSaeMBean.listaDiscentes) - sim}"/> 
					Discente(s) não carente(s): ${nao} 
					(<ufrn:format type="valor" valor="${((nao/fn:length(relatoriosSaeMBean.listaDiscentes)) *100) }"/>%)
				</td>
			</tr>
			</c:if>
			</tfoot>
		</table>
	</c:if>
	
    <c:if test="${relatoriosSaeMBean.relatorioBolsasSIPAC == true}">
    	<div id="parametrosRelatorio">
			<table>
				<tr>
					<th>Tipo da Bolsa:</th>
					<td>${ relatoriosSaeMBean.tipoBolsaSIPAC.descricao }</td>
				</tr>
			</table>
		</div>
			
		<br>
		
		<table class="tabelaRelatorioBorda" width="100%">	
			<thead>
			<tr>
				<th class="alinharCentro"> Matrícula </th>
				<th class="alinharEsquerda"> Discente </th>
				<th class="alinharCentro"> Prioritário? </th>
			</tr>
			</thead>
				<c:forEach var="item" items="#{relatoriosSaeMBean.listaBolsistasSIPAC}">
					<tr>
					   	<td class="alinharCentro"> ${item.discente.matricula} </td>
					   	<td class="alinharEsquerda">  ${item.discente.pessoa.nome} </td>										
					    <td align="center"> 
						  <c:if test="${item.carente}">SIM</c:if> 
						  <c:if test="${!item.carente}">NÃO</c:if>
					   </td>
					</tr>
				</c:forEach>
		</table>
		
 		<table align="center">
   			<tfoot>
				<tr>
					<td colspan="6" style="text-align: center; font-weight: bold;">
						${fn:length(relatoriosSaeMBean.listaBolsistasSIPAC)} discente(s) encontrado(s)
					</td>
				</tr>
			</tfoot>
		</table>
			
	</c:if>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>