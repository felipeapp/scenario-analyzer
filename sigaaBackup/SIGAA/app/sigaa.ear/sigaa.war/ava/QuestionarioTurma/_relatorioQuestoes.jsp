				<h1 align="center">${categorias.nome }</h1>
				<c:forEach var="pergunta" items="#{categorias.perguntas}" varStatus="status">
							
					<table>
					
					<tr>
							<td style="font-weight : bold;">
								${status.index+1}. ${pergunta.nome}
							</td>
					</tr>
					<tr>
							<td>
								${pergunta.pergunta}
							</td>
					</tr>
					
					<tr>
						<td>
										
						<c:if test="${pergunta.unicaEscolha}">
							
								<c:forEach var="alternativa" items="#{pergunta.alternativasValidas}">
										<div>${alternativa.alternativa}</div> 
								</c:forEach>
								
								<c:forEach var="alternativa" items="#{pergunta.alternativasValidas}">
									<c:if test="${alternativa.gabarito}">
										<div class="gabarito" style="color: red;font-weight: bold;">Resposta: ${alternativa.alternativa}</div> 
									</c:if>
								</c:forEach>	
							
						</c:if>
						<c:if test="${pergunta.multiplaEscolha}">
								
								<c:forEach var="alternativa" items="#{pergunta.alternativasValidas}">
											<div class="gabarito">
												<c:if test="${alternativa.gabarito}">
													<div style="color: red;font-weight: bold;">(X) ${alternativa.alternativa}</div>
												</c:if>
												<c:if test="${not alternativa.gabarito}">
													<div>( ) ${alternativa.alternativa}</div>
												</c:if>
											</div>
											
											<div class="semGabarito"> 
												<div>( ) ${alternativa.alternativa}</div>
											</div>
								</c:forEach>
								
						</c:if>
						<c:if test="${pergunta.vf}">
							
								<div class="semGabarito"> ( ) Verdadeiro</div>
								<div class="semGabarito"> ( ) Falso</div>
								
								<c:if test="${pergunta.gabaritoVf}">
									<div class="gabarito" style="color: red;font-weight: bold;">(X) Verdadeiro</div>
									<div class="gabarito"> ( ) Falso</div>
								</c:if>
								
								<c:if test="${not pergunta.gabaritoVf}">
									<div class="gabarito"> ( ) Verdadeiro</div>
									<div class="gabarito" style="color: red;font-weight: bold;">(X) Falso</div>
								</c:if>
											
						</c:if>						
						<c:if test="${pergunta.dissertativa}">
								<div class="gabarito" style="color: red;font-weight: bold;">Resposta: ${pergunta.gabaritoDissertativa}</div>
								
						</c:if>
						<c:if test="${pergunta.numerica}">
								<div class="gabarito" style="color: red;font-weight: bold; ">Resposta: ${pergunta.gabaritoNumericaString}</div>
						</c:if>
						<c:if test="${not empty pergunta.feedbackAcerto}">
								<div class="feedbackAcerto"><b>Feedback de Acerto:</b> ${pergunta.feedbackAcerto}</div>
								
						</c:if>
						<c:if test="${not empty  pergunta.feedbackErro}">
								<div class="feedbackErro"><b>Feedback de Erro:</b> ${pergunta.feedbackErro}</div>
						</c:if>
						</td>			
					</tr>
					<br>
				</table>
	
				</c:forEach>