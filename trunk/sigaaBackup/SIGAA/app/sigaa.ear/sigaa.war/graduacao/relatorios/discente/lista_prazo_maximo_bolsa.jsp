<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
hr{
		color: #000 !important;
		background-color: #000 !important;
	}
	
</style>
<f:view>
    <h2 class="tituloTabela"><b>Relatório de Bolsistas por Período</b></h2>
    <div id="parametrosRelatorio">
		<table>
			<tr>
				<th>Programa:</th>
				<td>
					${relatorioDiscente.matrizCurricular.curso.unidade.nome}			
				</td>
			</tr>	   
			<tr>
				<th>Período: </th>
				<td>
				${relatorioDiscente.ano} - ${relatorioDiscente.periodo} 
				</td>		
			</tr>	
	
		</table>
	</div>
	
		
	<br/>
	
	<c:set var="varNivel" value=""/>
	<c:set var="total" value="0"/>
	<c:forEach items="#{relatorioDiscente.listaDiscenteBolsa}" var="linha">
		<c:if test="${varNivel != linha.nivel}">
			<c:if test='${varNivel != ""}'>			
				</table>   		
			</c:if>			 
			<c:set var="varNivel" value="${linha.nivel}"/>
			<table class="tabelaRelatorioBorda" width="100%">
			    <caption id="nivel">${varNivel}</caption>
				<thead>
					<tr>
						<th style="width:100px;">
							Matrícula 
						</th>
						<th style="width:300px;">
							Nome 
						</th>
						<th style="width:300px;">
							Status Discente
						</th>						
						<th style="text-align: center;">
							Data Inicio
						</th>
						<th style="text-align: center;">
							Data Fim
						</th>
						<th>
							Tipo da Bolsa
						</th>
						<th>
							Status da Bolsa
						</th>						
					</tr>
				</thead>		
		</c:if> 
		<c:if test="${!empty linha.tipoBolsa}">
				<tbody>
					<tr>
						<td>
							${linha.matricula}
						</td>
						<td>
							${linha.nome}
							<c:if test="${not empty linha.municipio_polo}"> - PÓLO ${linha.municipio_polo}</c:if>
						</td>
						<td>
							${linha.statusDiscente}
						</td>						
						<td style="text-align: center;">
							<ufrn:format type="data" valor="${linha.dataInicio}"/>
						</td>
						<td style="text-align: center;">
							<ufrn:format type="data" valor="${linha.dataFim}"/>
						</td>																
						<td>
						    <c:if test="${!empty linha.tipoBolsa}">
						    	${linha.tipoBolsa}
						    </c:if>					    
						</td>
						<td>
							${linha.statusBolsa}
						</td>
						<c:set var="total" value="${total + 1}" />	
					</tr>
				</tbody>	
		</c:if>		
			
	</c:forEach>
			</table>
				<hr>
			<b>Total de bolsas: </b>${total}
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
