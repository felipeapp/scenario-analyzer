<% request.setAttribute("res1024","true"); %>
<%@include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>
<%@taglib uri="/tags/jawr" prefix="jwr"%>

<style type="text/css">

.listagem th {font-size: 10px !important; text-align: center; border: 1px solid #888;}

</style>

<script type="text/javascript" src="/shared/jsBundles/jawr_loader.js" ></script>
<script>
	JAWR.loader.script('/javascript/jquery/jquery.js');
</script>
<script>
	var JQuery = jQuery.noConflict();
</script>

<f:view>

	<c:set var="totalNotas" value="0"/>
	<c:set var="somaNotas" value="0"/>	
	<c:set var="regrasNotas" value="#{relatorioConsolidacao.regrasMedio}"/>

	<h3>${relatorioConsolidacao.turma.descricaoTurmaMedio}</h3>
	<hr/>
		<br/>
		<div class="notas" style="clear: both;">
			
			<table class="listagem" width="100%" border="1">
				<tr>
					<th rowspan="2">MATRÍCULA</th>
					<th rowspan="2">DISCENTE</th>	
					
					<c:set var="provaFinal" value=""/>
					<c:forEach items="#{regrasNotas}" var="item">
						<c:if test="${!item.provaFinal}">
							<th colspan="${item.nota ? '2' : '1'}" rowspan="${ item.recuperacao ? '2' : '1' }">
								<h:outputText value="#{item.titulo}"/>
							</th>
						</c:if>
						<c:if test="${item.provaFinal}">
							<c:set var="provaFinal" value="#{item.titulo}"/>		
						</c:if>
					</c:forEach>
							
					<th colspan="${not empty provaFinal ? '3' : '2'}">MÉDIA</th>
					<th rowspan="2">Faltas</th>
					<th rowspan="2">Situação</th>
				</tr>
				<tr>		
					<c:forEach items="#{regrasNotas}" var="item">
						<c:if test="${item.nota}">
							<th>Média</th>
							<c:if test="${item.nota}">
								<th>Faltas</th>						
							</c:if>
						</c:if>
					</c:forEach>			
					<th>Anual</th>	
					<c:if test="${not empty provaFinal}">
						<th>${provaFinal}</th>
					</c:if>
					<th>Final</th>		
				</tr>
				<tbody>
					<c:forEach var="matricula" items="${ relatorioConsolidacao.turma.matriculasDisciplina }" varStatus="i">
					
					<c:if test="${ acesso.dae 
					  or acesso.medio
					  or (acesso.docente and (consolidarTurma.portalDocente or consolidarTurma.turmaVirtual) ) 
					  or (obj.mostrarTodasAsNotas and (!obj.ocultarNotas or consolidarTurma.turma.consolidada) ) 
					  or (!obj.mostrarTodasAsNotas and (usuario.discenteAtivo.id == matricula.discente.id)) }">
					  
						<tr style="${ i.index % 2 == 0 ? 'background-color:#DDDDDD;' : 'background-color:#FFFFFF;' };border: 1px solid #888;">
							<td nowrap="nowrap" style="text-align: right">${ matricula.discente.matricula } </td>
							<td nowrap="nowrap" >${ matricula.discente.pessoa.nome }</td>
							<c:forEach items="#{regrasNotas}" var="item">
								
								<c:if test="${item.nota || item.recuperacao}">
					
									<c:set var="nota" value="-"/>
									<c:set var="faltas" value="-"/>
									<c:forEach items="#{matricula.notas}" var="itemNota">
						
										<c:if test="${itemNota.unidade == item.id}">
											<c:set var="nota" value="#{itemNota.nota}"/>
											<c:set var="faltas" value="#{itemNota.faltas}"/>	
											<c:if test="${nota != '-' && nota < boletimMedioMBean.obj.mediaMinimaPassarPorMedia}">
												<c:set var="cor" value="red"/>		
											</c:if>						
										</c:if>
										
									</c:forEach>
								
									<td>
										<c:choose>
											<c:when test="${nota ne null && nota ne '-'}">
												<fmt:formatNumber pattern="#0.0" value="${nota}"/>
											</c:when>
											<c:otherwise>
												-
											</c:otherwise>
										</c:choose>
									</td>	
									<c:if test="${item.nota}">
										<td>
											<c:choose>
												<c:when test="${nota ne null and faltas ne null}">
													${faltas}	
												</c:when>
												<c:otherwise>
													-
												</c:otherwise>
											</c:choose>
										</td>
									</c:if>					
								</c:if>
			
							</c:forEach>
													
							<c:if test="${matricula.consolidada || acesso.medio}">
								<td class="nota ${corMedia}" style="width: 5%">
									${matricula.mediaParcial}
								</td>	
							</c:if>
							<c:if test="${!matricula.consolidada && !acesso.medio}">
								<td class="nota">-</td>
							</c:if>	
							<c:if test="${!empty provaFinal}">
								<c:if test="${matricula.consolidada || acesso.medio}">
									<td class="nota" style="width: 5%">
										${matricula.recuperacao}
									</td>
								</c:if>
								<c:if test="${!matricula.consolidada && !acesso.medio}">
									<td class="nota">-</td>
								</c:if>					
							</c:if>
							<c:if test="${matricula.consolidada || acesso.medio}">
								<td class="nota" style="width: 5%">
									${matricula.mediaFinal}
								</td>
							</c:if>
							<c:if test="${!matricula.consolidada && !acesso.medio}">
								<td class="nota">-</td>
							</c:if>			
							<td class="nota" style="width: 5%">
								<c:if test="${matricula.consolidada || acesso.medio}">
									${matricula.numeroFaltas}
								</c:if>
								<c:if test="${!matricula.consolidada && !acesso.medio}">
									-
								</c:if>			
							</td>
							<td class="nota" style="width: 5%">
								<c:if test="${matricula.consolidada || acesso.medio}">
									${matricula.situacaoAbrev}				
								</c:if>
								<c:if test="${!matricula.consolidada && !acesso.medio}">
									-
								</c:if>
							</td>	
						</tr>
					</c:if>
					</c:forEach>
				</tbody>
			</table>
		</div>
		<br/>
		
		<c:set var="obj" value="${ turmaVirtual.config }"/>
		<c:if test="${ relatorioConsolidacao.nota and obj.mostrarMediaDaTurma }">
			<c:forEach var="matricula" items="${ relatorioConsolidacao.turma.matriculasDisciplina }" varStatus="i">
				<c:set var="somaNotas" value="${ somaNotas + matricula.media }"/>
				<c:set var="totalNotas" value="${ totalNotas + 1 }"/>
			</c:forEach>	
			<h3 style="text-align: center">Média da Turma: <fmt:formatNumber value="${ somaNotas/ totalNotas }" pattern="#0.0"/></h3>
		</c:if>
		
		
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>