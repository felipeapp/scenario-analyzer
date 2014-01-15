<%@include file="/mobile/commons/cabecalho.jsp" %>

	<f:view>

		<a href="menu.jsf">Menu Principal</a><br/>
		
		<h:form id="formConsultaNotas">
			
			<h:commandButton action="#{consultaNotasMobileMBean.listaSemestresAnterioresAtual}"
			 value="Semestres anteriores" /> <br/><br/>
				
			<table class="listagemMobile">
				<caption>Dados do Discente</caption>
				<tbody>
					<tr>
						<th>Período Letivo:</th>
					</tr>
					<tr>	
						<td>
							<h:outputText value="#{consultaNotasMobileMBean.consultaNotas.anoAtual}"/>.<h:outputText value="#{consultaNotasMobileMBean.consultaNotas.periodoAtual}"/>
						</td>
					</tr>
					<tr>
						<th>Matrícula:</th>
					</tr>
					<tr>
						<td>
							<h:outputText value="#{consultaNotasMobileMBean.consultaNotas.discente.matricula}"/>
						</td>
					</tr>	
					<tr>
						<th>Discente:</th>
					<tr>
						<td>
							<h:outputText value="#{usuario.pessoa.nome}"/>
						</td>
					</tr>	
				</tbody>
			</table>	
				
			<table class="listagemMobile">		
				<tbody>
				<c:choose>
					<c:when test="${consultaNotasMobileMBean.consultaNotas.discente.graduacao}">
						<th>MC:</th>
						<td>
							<h:outputText value="#{consultaNotasMobileMBean.consultaNotas.mediaGeral}">
						 		<f:convertNumber maxFractionDigits="4" type="number"></f:convertNumber>
							</h:outputText>
						</td>	
					</c:when>
					
					<c:when test="${consultaNotasMobileMBean.consultaNotas.discente.mestrado}">
						<th>Coeficiente:</th>
						<td> 
							${ sf:descricaoConceito(consultaNotasMobileMBean.consultaNotas.mediaGeral) }
						</td> 
					</c:when>
					
					<c:when test="${consultaNotasMobileMBean.consultaNotas.discente.tecnico 
							&& consultaNotasMobileMBean.tecnicoMusica == true}">
						<th>Média Geral:</th>
						<td>
							<h:outputText value="#{consultaNotasMobileMBean.consultaNotas.mediaGeral}">
							 	<f:convertNumber maxFractionDigits="4" type="number"></f:convertNumber>
							</h:outputText>
						</td>
					</c:when>
				</c:choose>
				</tbody>
			</table>	
			<c:set var="anoAtual" value="#{consultaNotasMobileMBean.consultaNotas.anoAtual}" />
			<br/>		
		
		<!--===== GRADUACAO ====-->
		<c:if test="${consultaNotasMobileMBean.consultaNotas.discente.graduacao}">
			<table class="listagemMobile">		
				<tbody>	
					<c:forEach items="${consultaNotasMobileMBean.matriculas}" var="item">
						<c:if test="${item.ano == anoAtual}">
							<tr>
								<td class="subListagemMobile">		
									${item.componente.detalhes.nome}
								</td>
							</tr>		
							<tr>
								<td>
								<c:if test="${ item.consolidada == true }">
									<c:forEach items="${item.notas}" var="notas">	
										Unidade ${notas.unidade}: ${notas.nota} <br/>
									</c:forEach>
										Média Final: ${item.mediaFinal} <br/>
								</c:if>
							
								<c:if test="${ item.consolidada == false }">
										<c:set var="notasTemp" value="${item.notas}"/>
									
										<c:if test="${empty notasTemp}">
											Notas não cadastradas.
										</c:if>		
										
								</c:if>
								
								<c:if test="${ item.consolidada == false }">
									<c:forEach items="${item.notas}" var="notas">
											<c:if test="${notas.nota != null}">
												Unidade ${notas.unidade}: ${notas.nota} <br/>
											</c:if>
									</c:forEach>
								</c:if>
								</td>
							</tr>	
						</c:if>
					</c:forEach>
				</tbody>
			</table>									
		</c:if>
		
		<!--===== PÓS-GRADUACAO ====-->
		<c:if test="${!consultaNotasMobileMBean.consultaNotas.discente.graduacao 
			&& !consultaNotasMobileMBean.consultaNotas.discente.tecnico}">
			<table class="listagemMobile">		
				<tbody>	
					<c:forEach items="${consultaNotasMobileMBean.matriculas}" var="item">
						<c:if test="${item.ano == anoAtual}">
							<tr>
								<td class="subListagemMobile">		
									${item.componente.detalhes.nome}
								</td>
							</tr>		
							<tr>
								<td>
								<c:if test="${ item.consolidada == true}">
										
									Conceito: ${ sf:descricaoConceito(item.mediaFinal) } <br/>
									
								</c:if>
								<c:if test="${ item.consolidada == false}">
										
									Sem conceito cadastrado. <br/>
									
								</c:if>
								</td>
							</tr>	
						</c:if>
					</c:forEach>
				</tbody>
			</table>								
		</c:if>
		
		<!--===== TECNICO MUSICA ====-->
		<c:if test="${consultaNotasMobileMBean.consultaNotas.discente.tecnico && consultaNotasMobileMBean.tecnicoMusica}">
			<table class="listagemMobile">		
				<tbody>		
					<c:forEach items="${consultaNotasMobileMBean.matriculas}" var="item">
						<c:if test="${item.ano == anoAtual}">
							<tr>
								<td class="subListagemMobile">		
									${item.componente.detalhes.nome}
								</td>
							</tr>		
							<tr>
								<td>	
									<c:if test="${ item.consolidada == true }">
										<c:forEach items="${item.notas}" var="notas">	
											Unidade ${notas.unidade}: ${notas.nota} <br/>
										</c:forEach>
										Média Geral: ${item.mediaFinal} <br/>
									</c:if>
									<c:if test="${ item.consolidada == false }">
										<c:set var="notasTemp" value="${item.notas}"/>
										
										<c:if test="${empty notasTemp}">
											Notas não cadastradas.
										</c:if>
										
										<c:forEach items="${item.notas}" var="notas">
											<c:if test="${notas.nota != null}">
													Unidade ${notas.unidade}: ${notas.nota} <br/>
											</c:if>
										</c:forEach>
									</c:if>
								</td>			
							</tr>
						</c:if>
					</c:forEach>
				</tbody>			
			</table>
		</c:if>
		
		<!--===== TECNICO ENFERMAGEM ====-->
		<c:if test="${consultaNotasMobileMBean.consultaNotas.discente.tecnico && !consultaNotasMobileMBean.tecnicoMusica}">
			<table class="listagemMobile">		
				<tbody>			
					<c:forEach items="${consultaNotasMobileMBean.matriculas}" var="varTec">
						<c:if test="${varTec.ano == anoAtual}">
							<tr>
								<td class="subListagemMobile">		
									${varTec.componente.detalhes.nome}
								</td>
							</tr>	
							<tr>
								<td> 
									<c:if test="${ varTec.apto != null}">
										SITUAÇÃO: ${ varTec.apto } <br/>
									</c:if>
									
									<c:if test="${ varTec.apto == null}">
										Sem média cadastrada. <br/>
									</c:if>
								</td>
							</tr>
						</c:if>						
					</c:forEach>
				</tbody>		
			</table>		
		</c:if>
		
	</h:form>	
		
	</f:view>	

<%@include file="/mobile/commons/rodape.jsp" %>
	
