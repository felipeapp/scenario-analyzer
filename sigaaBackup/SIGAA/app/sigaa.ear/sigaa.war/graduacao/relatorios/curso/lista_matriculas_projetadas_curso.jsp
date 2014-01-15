<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
	<h2 class="tituloTabela"><b>Lista de Matrículas Projetadas</b></h2>
	<div id="parametrosRelatorio">
		<table>
			<tr>
				<th>Ano:</th>
				<td>
					${relatorioCurso.ano}			
				</td>
			</tr>	   		
		</table>
	</div>
	
    <c:set var="varUnidade" value=""/>
    <c:set var="varTotal" value="0"/>
    <c:set var="varTotalVagas" value="0"/>
    <c:set var="varTotalGeral" value="0"/>
    <c:set var="varTotalVagasGeral" value="0"/>    
	<c:forEach items="#{relatorioCurso.listaCurso}" var="linha" varStatus="status">
		<c:if test="${varUnidade != linha.unidade}">
			<c:if test='${varUnidade != ""}'>
				<tr>
					<td style="text-align: right;"><b>Totais:</b></td>
					<td style="text-align: right;"><b>${varTotalVagas}</b></td>
					<td style="text-align: center;"><b>-</b></td>
					<td style="text-align: center;"><b>-</b></td>
					<td style="text-align: center;"><b>-</b></td>	
	 				<td style="text-align: right;"><b>${varTotal}</b></td>
				</tr>
			    <c:set var="varTotalGeral" value="${varTotalGeral + varTotal}"/>
			    <c:set var="varTotalVagasGeral" value="${varTotalVagasGeral + varTotalVagas}"/>      							
				<c:set var="varTotal" value="0"/>
    			<c:set var="varTotalVagas" value="0"/>
				</table>   		
			</c:if>			 
			<c:set var="varUnidade" value="${linha.unidade}"/>
				<br/>
				<table class="tabelaRelatorioBorda">
					<caption>${linha.unidade}</caption>
					<thead>
						<tr>
							<th style="width:200px;">
								Curso 
							</th>
							<th style="width:80px;text-align: right;">
								Total Vagas
							</th>
							<th style="width:80px;text-align: right;">
								Fator Retenção 
							</th>
							<th style="width:80px;text-align: right;">
								Peso Grupo
							</th>			
							<th style="width:80px;text-align: right;">
								Duração Padrão
							</th>
							<th style="width:100px;text-align: right;">
								Total
							</th>					
						</tr>
					</thead>
			</c:if>
		
		<c:set var="varTotal" value="${varTotal + linha.total}"/>
		<c:set var="varTotalVagas" value="${varTotalVagas + linha.totalVagas}"/>
		
		<tr class='${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}'>							
			<td style="text-align: left;">${linha.curso}</td>
			<td style="text-align: right;">${linha.totalVagas}</td>
			<td style="text-align: right;">${linha.fator_retencao}</td>
			<td style="text-align: right;">${linha.peso_grupo}</td>
			<td style="text-align: right;">${linha.duracao_padrao}</td>
			<td style="text-align: right;">${linha.total}</td>									
		</tr>			

	</c:forEach>
		<tr>
			<td style="text-align: right;"><b>Totais:</b></td>
			<td style="text-align: right;"><b>${varTotalVagas }</b></td>
			<td style="text-align: center;"><b>-</b></td>
			<td style="text-align: center;"><b>-</b></td>
			<td style="text-align: center;"><b>-</b></td>
			<td style="text-align: right;"><b>${varTotal}</b></td>
		</tr>
		<c:set var="varTotalGeral" value="${varTotalGeral + varTotal}"/>
		<c:set var="varTotalVagasGeral" value="${varTotalVagasGeral + varTotalVagas}"/>
	</table>		
	<br/>
	<table class="tabelaRelatorioBorda">
		<tr>
			<th style="width:200px;text-align: right;"><b>Total Geral:</b></th>
			<th style="width:80px;text-align: right;"><b>${varTotalVagasGeral }</b></th>
			<th style="width:80px;text-align: center;"><b>-</b></th>
			<th style="width:80px;text-align: center;"><b>-</b></th>
			<th style="width:80px;text-align: center;"><b>-</b></th>						
			<th style="width:100px;text-align: right;"><b>${varTotalGeral}</b></th>
		</tr>
	</table>					
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
