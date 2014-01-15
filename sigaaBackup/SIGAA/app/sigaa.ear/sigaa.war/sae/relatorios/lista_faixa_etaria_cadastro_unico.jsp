<%@taglib uri="/tags/a4j" prefix="a4j"%>
<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<style>
	tr.foot td {padding: 3px ; border-bottom: 1px solid #555; background-color: #eee; font-weight: bold; font-size: 13px; }
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
</style>

<f:view>

	<h2>Relatório de Discentes Prioritários Por Faixa Etária</h2>

	<div id="parametrosRelatorio">
		<table >
			<tr>
				<th>Ano-Período:</th>
				<td>${ relatorioCadUnicoFaixaEtaria.ano }.${ relatorioCadUnicoFaixaEtaria.periodo }</td>
			</tr>
			<tr>
				<th>Sexo:</th>
				<td>
				<c:choose>
      				<c:when test="${relatorioCadUnicoFaixaEtaria.sexo eq 0}">Ambos</c:when>
      				<c:when test="${relatorioCadUnicoFaixaEtaria.sexo eq 'M'}">Masculino</c:when>
      				<c:when test="${relatorioCadUnicoFaixaEtaria.sexo eq 'F'}">Feminino</c:when>
   				</c:choose>
				</td>
			</tr>
			<c:if test="${not empty relatorioCadUnicoFaixaEtaria.anoConclusao and relatorioCadUnicoFaixaEtaria.anoConclusao ne 0 
				and not empty relatorioCadUnicoFaixaEtaria.periodoConclusao and relatorioCadUnicoFaixaEtaria.periodoConclusao ne 0}">
				<tr>
					<th>Ano Conclusão:</th>
					<td>${ relatorioCadUnicoFaixaEtaria.anoConclusao }.${ relatorioCadUnicoFaixaEtaria.periodoConclusao }</td>
				</tr>
			</c:if>		
			<c:if test="${relatorioCadUnicoFaixaEtaria.residente == true}">
				<tr>
					<th>Condição:</th>
					<td>Somente Residentes</td>
				</tr>
			</c:if>				
		</table>
	</div>
	<br/>
	<c:set var="totalGeral" value="0"/>
	<table class="tabelaRelatorioBorda" width="100%">	
		<thead>
		<tr>
			<th> Faixa Etária </th>
			<th colspan="9" class="alinharDireita"> Quantidade </th>
		</tr>
		</thead>
		
		<tr>
			<td> Até 18 </td>
			<td class="alinharDireita"> ${relatorioCadUnicoFaixaEtaria.resultado[0].faixa_1} </td>
			<c:set var="totalGeral" value="${totalGeral + relatorioCadUnicoFaixaEtaria.resultado[0].faixa_1}"/>
		</tr>
		<tr>
			<td> 19-23 </td>
			<td class="alinharDireita"> ${relatorioCadUnicoFaixaEtaria.resultado[0].faixa_2} </td>
			<c:set var="totalGeral" value="${totalGeral + relatorioCadUnicoFaixaEtaria.resultado[0].faixa_2}"/>
		</tr>
		<tr>
			<td> 24-28 </td>		
			<td class="alinharDireita"> ${relatorioCadUnicoFaixaEtaria.resultado[0].faixa_3} </td>
			<c:set var="totalGeral" value="${totalGeral + relatorioCadUnicoFaixaEtaria.resultado[0].faixa_3}"/>
		</tr>
		
		<tr>
			<td> 29-33 </td>
			<td class="alinharDireita"> ${relatorioCadUnicoFaixaEtaria.resultado[0].faixa_4} </td>
			<c:set var="totalGeral" value="${totalGeral + relatorioCadUnicoFaixaEtaria.resultado[0].faixa_4}"/>
		</tr>
		
		<tr>
			<td> 34-38 </td>		
			<td class="alinharDireita"> ${relatorioCadUnicoFaixaEtaria.resultado[0].faixa_5} </td>
			<c:set var="totalGeral" value="${totalGeral + relatorioCadUnicoFaixaEtaria.resultado[0].faixa_5}"/>
		</tr>
		
		<tr>
			<td> 39-43 </td>
			<td class="alinharDireita"> ${relatorioCadUnicoFaixaEtaria.resultado[0].faixa_6} </td>
			<c:set var="totalGeral" value="${totalGeral + relatorioCadUnicoFaixaEtaria.resultado[0].faixa_6}"/>
		</tr>
		
		<tr>
			<td> 44-48 </td>
			<td class="alinharDireita"> ${relatorioCadUnicoFaixaEtaria.resultado[0].faixa_7} </td>
			<c:set var="totalGeral" value="${totalGeral + relatorioCadUnicoFaixaEtaria.resultado[0].faixa_7}"/>
		</tr>
		
		<tr>
			<td> 49-53</td>
			<td class="alinharDireita"> ${relatorioCadUnicoFaixaEtaria.resultado[0].faixa_8} </td>
			<c:set var="totalGeral" value="${totalGeral + relatorioCadUnicoFaixaEtaria.resultado[0].faixa_8}"/>
		</tr>
		<tr>
			<td>54-58</td>
			<td class="alinharDireita"> ${relatorioCadUnicoFaixaEtaria.resultado[0].faixa_9} </td>
			<c:set var="totalGeral" value="${totalGeral + relatorioCadUnicoFaixaEtaria.resultado[0].faixa_9}"/>
		</tr>
		<tr>
			<td>Acima de 58</td>
			<td class="alinharDireita"> ${relatorioCadUnicoFaixaEtaria.resultado[0].faixa_10} </td>
			<c:set var="totalGeral" value="${totalGeral + relatorioCadUnicoFaixaEtaria.resultado[0].faixa_10}"/>
		</tr>		
		<tr class="foot">
			<td>Total de Alunos Prioritários¹</td>
			<td class="alinharDireita">${ totalGeral }</td>
		</tr>		
	</table>
	<br/>
	<div id="parametrosRelatorio">
		<table>
			<tr>
				<td>¹ Resolução Nº 169/2008-CONSEPE</td>
			</tr>
		</table>
	</div>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>