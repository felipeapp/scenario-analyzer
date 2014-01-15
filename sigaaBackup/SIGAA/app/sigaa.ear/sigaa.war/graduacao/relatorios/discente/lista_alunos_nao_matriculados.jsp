<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
	.colTipo{width: 30px;text-align: center !important;}
	.colMatricula{width: 80px;text-align: right !important;}
	.colBolsa{width: 120px;}
	.solicitacaoPendete{font-size: 16px;font-weight: bold;}
</style>
<f:view>
    <h2 class="tituloTabela"><b>Lista dos Alunos Ativos n�o Matriculados</b></h2>
    <div id="parametrosRelatorio">
		<table>
			<tr>
				<th>Programa:</th>
				<td>
					${relatorioDiscente.matrizCurricular.curso.unidade.nome}			
				</td>
			</tr>	   
			<tr>
				<th>Ano-Per�odo: </th>
				<td>
				    ${relatorioDiscente.ano}.${relatorioDiscente.periodo}
				</td>		
			</tr>	
	
		</table>
	</div>
		
	<br/>
	
	<c:set var="varNivel" value=""/>
	
	<c:forEach items="#{relatorioDiscente.listaDiscente}" var="linha" varStatus="i">
		<c:if test="${varNivel != linha.nivel}">
			<c:if test='${varNivel != ""}'>			
				</table>   		
			   	<br/>
			</c:if>			 
			<c:set var="varNivel" value="${linha.nivel}"/>
			<table class="tabelaRelatorioBorda" width="100%">
			    <caption>${varNivel}</caption>
				<thead>
					<tr>
						<th class="colMatricula">
							Matr�cula 
						</th>
						<th>
							Nome 
						</th>
						<th class="colTipo">
							Tipo
						</th>
						<th class="colBolsa">
							Tipo da Bolsa
						</th>
					</tr>
				</thead>		
		</c:if> 
				<tbody>
					<tr>
						<td  class="colMatricula">
							${linha.matricula}
						</td>
						<td>
							${linha.nome} 
							<c:if test="${not empty linha.status}"><span class="solicitacaoPendete">*</span></c:if>
							<c:if test="${not empty linha.municipio_polo}"> - P�LO ${linha.municipio_polo}</c:if>
							<br/>
							<i><small>ORIENTADOR(A): ${linha.orientador}</small></i>
						</td>
						<td  class="colTipo">
						    <c:if test="${linha.tipo == 1}">
						    	R
						    </c:if>
						    <c:if test="${linha.tipo ==2 }">
						    	E
						    </c:if>					    
						</td>				
						<td class="colBolsa">
						    <c:if test="${empty linha.tipo_bolsa}">
						    	N�O POSSUI BOLSA
						    </c:if>
						    <c:if test="${!empty linha.tipo_bolsa}">
						    	${linha.tipo_bolsa}
						    </c:if>					    
						</td>					
					</tr>
					
				</tbody>					
	</c:forEach>
	</table>
	<br/>		
	
	<table class="tabelaRelatorioBorda" width="100%">
		<caption>Legenda</caption>
		<tbody>
			<tr>
				<th width="15px">R</th>
				<td>Regular</td>
			</tr>
			<tr>
				<th width="15px">E</th>
				<td>Especial</td>
			</tr>
			<tr>
				<th width="15px">*</th>
				<td>
					Todos discentes com	solicita��o de matr�cula pendente de an�lise
				</td>
			</tr>
		</tbody>
	</table>			
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
