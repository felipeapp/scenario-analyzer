<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
    <h2 class="tituloTabela"><b>Lista dos Alunos Reprovados</b></h2>
    <div id="parametrosRelatorio">
		<table>
			<tr>
				<th>Programa:</th>
				<td>
					${relatorioDiscente.matrizCurricular.curso.unidade.nome}			
				</td>
			</tr>	   
			<tr>
				<th>Ano-Período: </th>
				<td>
				    ${relatorioDiscente.ano}.${relatorioDiscente.periodo}
				</td>		
			</tr>	
	
		</table>
	</div>
		
	<br/>
	<c:set var="varNivel" value=""/>
	
	<c:forEach items="#{relatorioDiscente.listaDiscente}" var="linha">
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
						
						<c:if test="${relatorioDiscente.mostrarTipoBolsaEmRelatorioAlunosReprovados}">						
							<th>
								Tipo da Bolsa
							</th>						
						</c:if>
					</tr>
				</thead>		
		</c:if> 
				<tbody>
					<tr>
						<td>
							${linha.matricula}
						</td>
						<td>
							${linha.nome}
							<c:if test="${not empty linha.municipio_polo}"> - PÓLO ${linha.municipio_polo}</c:if>
						</td>				
						
						<c:if test="${relatorioDiscente.mostrarTipoBolsaEmRelatorioAlunosReprovados}">
						
							<td>
							    <c:if test="${empty linha.tipo_bolsa}">
							    	NÃO POSSUI BOLSA
							    </c:if>
							    <c:if test="${!empty linha.tipo_bolsa}">
							    	${linha.tipo_bolsa}
							    </c:if>					    
							</td>
						
						</c:if>					
						
					</tr>
				</tbody>					
	</c:forEach>
			</table>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
