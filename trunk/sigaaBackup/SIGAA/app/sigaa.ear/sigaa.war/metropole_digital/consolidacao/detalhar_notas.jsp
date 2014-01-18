<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<script>
	JAWR.loader.script('/javascript/jquery/jquery.js');
</script>
<script>
	// Muda o nome do jQuery para J, evitando conflitos com a Prototype.
	var J = jQuery.noConflict();
</script>

<style>
tr.selecionada td {
	background: #C4D2EB;
}
</style>

<a4j:keepAlive beanName="consolidacaoParcialIMD" />
<f:view>
	<h:form>
		<h2>
			<ufrn:subSistema /> >
			<h:commandLink action="#{consolidacaoParcialIMD.voltarFormConsolidacao}"
 				value="Consolidação Parcial" id="votarConsolidacao" /> 
				> Detalhar Notas
		</h2>
		<table class="formulario" style="width: 100%">
			<caption>Detalhamento de Notas</caption>
			<tbody>
				<tr>
					<td colspan="2">
						<table class="subFormulario" style="width: 100%;">
							<caption>ALUNO(A): ${consolidacaoParcialIMD.notasAluno[0].discente.discente.matricula} - ${consolidacaoParcialIMD.notasAluno[0].discente.discente.pessoa.nome} </caption>
							<thead>
								<tr>
									<th >ID</th>
									<th>DISCIPLINA</th>
									<th style="text-align: right">PP</th>
									<th style="text-align: right">PV</th>
									<th style="text-align: right">PT</th>
									<th style="text-align: right">AE</th>
									<th style="text-align: right">PE</th>																									
									<th style="text-align: right">CHNF</th>
								<tr>
							</thead>
							<tbody>
								<c:forEach items="${consolidacaoParcialIMD.notasAluno}" var="item" varStatus="i">						
									<tr class="${i.index % 2 == 0 ? 'linhaPar': 'linhaImpar'}"									
									onMouseOver="$(this).addClassName('selecionada')"
									onMouseOut="$(this).removeClassName('selecionada')">
									
										<td>${item.matriculaComponente.turma.id}</td>
										<td>${item.matriculaComponente.turma.disciplina.nome}</td>										
										
										<c:choose>
											<c:when test="${not empty item.participacaoPresencial}">
												<td style="text-align: right"><fmt:formatNumber value="${item.participacaoPresencial}"  pattern="#0.0"/></td>	
											</c:when>
											<c:otherwise>
												<td style="text-align: right">--</td>										
											</c:otherwise>																									
										</c:choose>
										
										<c:choose>
											<c:when test="${not empty item.participacaoVirtual}">
												<td style="text-align: right"><fmt:formatNumber value="${item.participacaoVirtual}"  pattern="#0.0"/></td>	
											</c:when>
											<c:otherwise>
												<td style="text-align: right">--</td>										
											</c:otherwise>																									
										</c:choose>
										
										<c:choose>
											<c:when test="${not empty item.participacaoTotal.nota}">
												<td style="text-align: right"><fmt:formatNumber value="${item.participacaoTotal.nota}"  pattern="#0.0"/></td>	
											</c:when>
											<c:otherwise>
												<td style="text-align: right">--</td>										
											</c:otherwise>																									
										</c:choose>
										
										<c:choose>
											<c:when test="${not empty item.atividadeOnline.nota}">
												<td style="text-align: right"><fmt:formatNumber value="${item.atividadeOnline.nota}"  pattern="#0.0"/></td>	
											</c:when>
											<c:otherwise>
												<td style="text-align: right">--</td>
											</c:otherwise>																									
										</c:choose>
										
										<c:choose>
											<c:when test="${not empty item.provaEscrita.nota}">
												<td style="text-align: right"><fmt:formatNumber value="${item.provaEscrita.nota}"  pattern="#0.0"/></td>
											</c:when>
											<c:otherwise>
												<td style="text-align: right">--</td>		
											</c:otherwise>																									
										</c:choose>
																												
										<c:choose>
											<c:when test="${not empty item.chnf}">
												<td style="text-align: right">${item.chnf}</td>
											</c:when>
											<c:otherwise>
												<td style="text-align: right">--</td>		
											</c:otherwise>																									
										</c:choose>																																															
									</tr>
								</c:forEach>
							</tbody>					
						</table>
					</td>				
				</tr>
			</tbody>
			
			<tfoot>
				
				
			</tfoot>
		</table>
	
	<div align="center">
				<table style="width: 85%">
					<tr align="center">
						<td style="text-align: right;"><strong>PP:</strong> Participação Presencial</td>		
		  				<td style="text-align: center;"><strong>PV:</strong> Participação Virtual</td>
		  				<td style="text-align: left;"><strong>PT:</strong> Participação Total</td>		  				
		  			</tr>
		  			<tr>
		  				<td style="text-align: center"><strong>AE:</strong> Atividades Executadas no Moodle</td>
		  				<td style="text-align: center"><strong>PE:</strong> Prova Escrita</td>
		  				<td style="text-align: center"><strong>Média:</strong> Média na Disciplina</td>
		  				<td style="text-align: center"><strong>CHNF:</strong> Carga Horária não Frequentada</td>
		  			</tr>
				</table>				
			</div>
			<div class="opcoes" align="right">
			<table style="width: 100%;">
				<tfoot>
					<tr>						
						<td width="50" valign="top"
							style="float: right; text-align: center; margin: 5px 5px 3px;"><h:commandButton
								action="#{consolidacaoParcialIMD.voltarFormConsolidacao}"
								image="/img/consolidacao/nav_left_red.png"
								alt="Voltar" title="Voltar" /></td>
					</tr>
					<tr>
						<td width="60" valign="top"
							style="float: right; text-align: center;"><h:commandLink
								action="#{consolidacaoParcialIMD.voltarFormConsolidacao}"
								value="Voltar" /></td>
					</tr>
				</tfoot>
			</table>
		</div>
		</h:form>
</f:view>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>