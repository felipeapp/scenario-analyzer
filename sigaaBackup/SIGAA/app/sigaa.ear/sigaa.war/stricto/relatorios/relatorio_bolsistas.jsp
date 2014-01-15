<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
    <h2 class="tituloTabela"><b>Relatório de Discentes Bolsistas</b></h2>
    <div id="parametrosRelatorio">
		<table>
			<tr>
				<th>Programa:</th>
				<td>
					${relatorioBolsasStrictoBean.unidade.nome}			
				</td>
			</tr>	   
			<tr>
				<th>Período: </th>
				<td>
				    <ufrn:format type="data" valor="${relatorioBolsasStrictoBean.dataInicial}"/> até 
				    <ufrn:format type="data" valor="${relatorioBolsasStrictoBean.dataFinal}"/>
				</td>		
			</tr>	
	
		</table>
	</div>
		
	<br/>
	
	<c:set var="varNivel" value=""/>
	
	<c:forEach items="#{relatorioBolsasStrictoBean.dadosRelatorio}" var="linha">
		<c:if test="${varNivel != linha.nivel}">
			<c:if test='${varNivel != ""}'>			
				</table>   		
			</c:if>			 
			<c:set var="varNivel" value="${linha.nivel}"/>
			<table class="tabelaRelatorioBorda" width="100%">
			    <caption id="nivel">${varNivel}</caption>
				<thead>
					<tr>
						<th style="width:100px; text-align: center;">
							Matrícula 
						</th>
						<th style="width:300px;">
							Nome 
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
					</tr>
				</thead>		
		</c:if> 
		<c:if test="${!empty linha.tipoBolsa}">
				<tbody>
					<tr>
						<td style="text-align: center;">
							${linha.matricula}
						</td>
						<td>
							${linha.nome}
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
					</tr>
				</tbody>	
		</c:if>				
	</c:forEach>
			</table>	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
