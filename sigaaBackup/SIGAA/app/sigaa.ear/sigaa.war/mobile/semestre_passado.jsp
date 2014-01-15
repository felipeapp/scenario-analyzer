<%@include file="/mobile/commons/cabecalho.jsp" %>

	<f:view>
			
			<h2> Semestre: ${consultaNotasMobileMBean.consultaNotas.anoSemestreAnterior}.${consultaNotasMobileMBean.consultaNotas.periodoSemestreAnterior} </h2>
			
			<h:form>
				<h:commandButton action="#{consultaNotasMobileMBean.listaSemestresAnterioresAtual}" value="Voltar" /> <br/>
			</h:form>
			
			<!--===== GRADUACAO ====-->
			<c:if test="${consultaNotasMobileMBean.consultaNotas.discente.nivel == 'G'}">
				<br/>
				<c:forEach items="${consultaNotasMobileMBean.consultaNotas.disciplinasAnteriores}" var="item">
			
						${item.componente.detalhes.nome} <br/>
						
							<c:if test="${ item.consolidada == true }">
									Média Final: ${item.mediaFinal} <br/>
							</c:if>
							
							<c:if test="${ item.consolidada == false }">
									Sem media cadastrada.
							</c:if>
							
						<hr/>
				</c:forEach>
			</c:if>
			
			<!--===== POS-GRADUACAO ====-->
			<c:if test="${consultaNotasMobileMBean.consultaNotas.discente.nivel != 'G' && consultaNotasMobileMBean.consultaNotas.discente.nivel != 'T'}">
			
				<c:forEach items="${consultaNotasMobileMBean.consultaNotas.disciplinasAnteriores}" var="iter">
			
						${iter.componente.detalhes.nome} <br/>
						
							<c:if test="${ iter.consolidada == true }">
									Conceito: ${ sf:descricaoConceito(iter.mediaFinal) } <br/>
							</c:if>
							
							<c:if test="${ iter.consolidada == false }">
									Sem conceito cadastrado.
							</c:if>
							
						<hr/>
				</c:forEach>
			</c:if>
			
				<!--===== TECNICO  ====-->
				<c:if test="${consultaNotasMobileMBean.consultaNotas.discente.nivel == 'T' && consultaNotasMobileMBean.tecnicoMusica == true}">
					
					<c:forEach items="${consultaNotasMobileMBean.consultaNotas.disciplinasAnteriores}" var="varTec">
		
							${varTec.componente.detalhes.nome} <br/>
							
								<c:if test="${ varTec.consolidada == true}">
										
									Media final: ${ varTec.mediaFinal } <br/>
									
								</c:if>
								<c:if test="${ varTec.consolidada == false}">		
									Sem media cadastrada. <br/>
								</c:if>
							<hr/>
						
					</c:forEach>
					
				</c:if>
				
				<c:if test="${consultaNotasMobileMBean.consultaNotas.discente.nivel == 'T' && consultaNotasMobileMBean.tecnicoMusica == false}">
				
						<c:forEach items="${consultaNotasMobileMBean.consultaNotas.disciplinasAnteriores}" var="varTec">
		
							${varTec.componente.detalhes.nome} <br/>
							
								<c:if test="${ varTec.apto != null}">
									SITUAÇÃO: ${ varTec.apto } <br/>
								</c:if>
								
								<c:if test="${ varTec.apto == null}">
									Sem media cadastrada. <br/>
								</c:if>
								
							<hr/>
						
						</c:forEach>
				</c:if>
			
	</f:view>	

<%@include file="/mobile/commons/rodape.jsp" %>	
