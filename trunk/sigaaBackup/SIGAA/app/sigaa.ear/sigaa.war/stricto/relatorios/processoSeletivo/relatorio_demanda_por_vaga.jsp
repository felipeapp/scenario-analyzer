<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<f:view>
	<h2>Relatório de Processos Seletivos (Demandas x Vagas)</h2>

	<div id="parametrosRelatorio">
		<table>
			<tr>
				<th><b>Programa:</b></th>
				<td>
			    	${processoSeletivo.unidade.nome}
				</td>
			</tr>	
			<tr>
				<th><b>Nível:</b></th>
				<td>
			    	${processoSeletivo.descricaoNivel}
				</td>
			</tr>	
			<c:if test="${processoSeletivo.dataInicio != null}">
				<tr>
					<th><b>Data de Início:</b></th>
					<td>
				    	<ufrn:format type="data" valor="${processoSeletivo.dataInicio}"/>
					</td>
				</tr>						
			</c:if>
			<c:if test="${processoSeletivo.dataFim != null}">
				<tr>
					<th><b>Data de Fim:</b></th>
					<td>
				    	<ufrn:format type="data" valor="${processoSeletivo.dataFim}"/>
					</td>
				</tr>							
			</c:if>
		</table>	
	</div>
	<br/>
	<table class="tabelaRelatorioBorda" width="100%">
		<thead>
			<tr>
				<th>Curso</th>
				<th width="80">Nível</th>
				<th style="text-align: center;" width="70">Início Inscrição</th>
				<th style="text-align: center;" width="70">Fim Inscrição</th>
				<th style="text-align: right;">Vagas</th>
				<th style="text-align: right;" width="50">Total Inscritos</th>
				<th style="text-align: right;" width="50">Demanda</th>
			</tr>
		</thead>
		
		<tbody>
			<c:forEach items="${processoSeletivo.listaResultado}" var="linha">
				<tr>
					<td> ${linha.curso}</td>
					<td> ${linha.nivel}</td>
					<td align="center"> <ufrn:format type="data" valor="${linha.inicioInscricoes}"/> </td>
					<td align="center"> <ufrn:format type="data" valor="${linha.fimInscricoes}"/> </td>
					<td align="right"> ${linha.vagas}</td>
					<td align="right"> ${linha.totalInscritos}</td>
					<td align="right"> <fmt:formatNumber type="percentage" pattern="#,##0.00" value="${linha.demanda}"/></td>
				</tr>
			</c:forEach>
		</tbody>
	</table>	
	
</f:view>


<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
