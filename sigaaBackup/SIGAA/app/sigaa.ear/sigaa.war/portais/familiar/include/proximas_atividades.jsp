<table class="listagem" style="margin-top: 5px;">

	<tr>
		<td class="subFormulario">Próximas Atividades</td>
	</tr>	
	
	<tr>
		<td>
		<table style="border-collapse: separate; width: 100%;">
				<tr>
					<td style="vertical-align: top;">
						<h:form id="formAtividades">
							<div id="avaliacao-portal" class="simple-panel">
								<c:set var="atividades_" value="#{ portalFamiliar.proximasAtividades }"/>
								<c:choose>
									<c:when test="${ not empty atividades_ }">
										<table width="100%">
											<thead>
											<tr>
												<th style="width:30px;"></th>
												<th style="width:150px;">Data</th>
												<th>Atividade</th>
											</tr>
											</thead>
											<tbody>
												<c:forEach items="#{atividades_}" var="a" varStatus="status">
													<c:set var="image" value=""/>
													<c:if test="${a.inMonth && a.dias >= 0}">
														<c:set var="image" value="<img src='/sigaa/img/prova_mes.png' title='Atividade no mês atual'>"/>
													</c:if>
													<c:if test="${a.inWeek && a.dias >= 0}">
														<c:set var="image" value="<img src='/sigaa/img/prova_semana.png' title='Atividade na Semana'>"/>
													</c:if>
													
													<c:if test="${a.idTarefa != 0 && a.concluida && a.dias >= 0}">
														<c:set var="image" value="<img src='/sigaa/img/check.png' title='Atividade concluída'>"/>
													</c:if>
													
													<tr class="${status.index % 2 == 0 ? "odd" : "" }">
														<td style="text-align:center;"> ${image} </td>
														<td>
															<c:if test="${a.dias < 0 }">
																<font color="gray">
																	<ufrn:format name="avaliacao" valor="${a.data}" type="data"/>
																</font>
															</c:if>
															<c:if test="${a.dias > 0 }">
																<ufrn:format name="avaliacao" valor="${a.data}" type="data"/>
																<c:if test="${a.dias == 1 }">
																	(${a.dias} dia)
																</c:if>
																<c:if test="${a.dias != 1 }">
																	(${a.dias} dias)
																</c:if>
															</c:if>
															<c:if test="${a.dias == 0}">
																<ufrn:format name="avaliacao" valor="${a.data}" type="data"/>
																(Hoje)
															</c:if>
														</td>
														<td>
															<small>
																<c:if test="${a.dias < 0 }">
																	<font color="gray">
																</c:if>
																${a.turma.disciplina.detalhes.nome}<br> 
																<c:if test="${a.idTarefa == 0}"><strong>Avaliação:</strong> ${a.descricao}</c:if>
																<c:if test="${a.idTarefa != 0}">
																	<strong>Tarefa:</strong>
																	${a.descricao}																					
																</c:if>
																<c:if test="${a.dias < 0 }">
																	</font>
																</c:if>
															</small>
														</td>
													</tr>
												</c:forEach>
											</tbody>
										</table>
									</c:when>
					
									<c:otherwise>
										<p class="vazio">
											<i>Não há atividades cadastradas para os próximos 15 dias ou decorridos 7 dias.</i>
										</p>
									</c:otherwise>
								</c:choose>
							</div>
						</h:form>
					</td>
				</tr>
			</table>							
		</td>
	</tr>
</table>	