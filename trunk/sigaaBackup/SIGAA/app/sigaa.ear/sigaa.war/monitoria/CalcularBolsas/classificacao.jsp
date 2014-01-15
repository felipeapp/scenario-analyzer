<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>


<f:view>

<h2 class="tituloPagina">Lista de Projetos Recomendados por Coloca��o</h2>

<strong>Projetos do Edital:</strong> <h:outputText value="#{ calcularBolsas.edital.descricao }"/><br/>
<strong>Total de bolsas para distribuir:</strong> <h:outputText value="#{ calcularBolsas.edital.numeroBolsas }"/><br/>

<br/>
	<table class="tabelaRelatorio" width="100%">
			<caption class="listagem">Classifica��o dos Projetos de Ensino</caption>

	      <thead>
	      	<tr>
	        	<th>Class.</th>
	        	<th>Projeto de Ensino</th>
	        	<th>Centro</th>
	        	<th title="M�dia">MF</th>
	        	<th title="Bolsas Solicitadas">BS</th>
	        	<th title="Bolsas Concedidas">BC</th>	        	
	        	<th title="N�o Remuneradas">NR</th>	        		        	
	        </tr>
	 	</thead>
	 	<tbody>
			<c:forEach items="${ calcularBolsas.projetos }" var="projeto" varStatus="status">			
		      	<tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">		      
		      		  
				    <td> ${ status.count }� </td>
					<td> ${ projeto.titulo } </td>
					<td> ${ projeto.unidade.sigla } </td>
					<td> <fmt:formatNumber  pattern="#0.00" value="${ projeto.mediaFinal }" />	</td>
					<td> ${ projeto.bolsasSolicitadas } </td>
					<td> ${ projeto.bolsasConcedidas } </td>					
					<td> ${ projeto.bolsasSolicitadas - projeto.bolsasConcedidas } </td>					
				</tr>
			</c:forEach>
		</tbody>
	</table>
	
	<div class="infoAltRem">[ MF = M�dia Final, BS = Bolsas Solicitadas, BC = Bolsas Concedidas, NR = Bolsas N�o Remuneradas ]</div>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>