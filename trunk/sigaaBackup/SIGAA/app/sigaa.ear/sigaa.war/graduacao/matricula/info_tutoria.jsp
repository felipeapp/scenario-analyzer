<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<%@ include file="/graduacao/matricula/cabecalho_matricula.jsp"%>

	<div class="descMatriculaGraduacaoMBeanricaoOperacao">
		<h4>Caro(a) Aluno(a),</h4> <br />
		<p> Nesta tela você deve selecionar os horários que tem disponível para a tutoria presencial.
		</p>
	</div>

	<c:set var="discente" value="#{matriculaGraduacao.discente}"/>
	<%@include file="/graduacao/info_discente.jsp"%>

	<h:form id="f">
		<table class="formulario" width="65%">
			<caption>Informe os horários disponíveis para esse discente nesse semestre</caption>
			<thead>
			<tr>
				<td width="55%">Dia da Semana</td>
				<td width="45%" align="center">Turno</td>
			</tr>
			</thead>
			<tbody>
			 
			 <tr>
				<td colspan="2">
				
				 <table width="100%">
				 
					 <c:forEach items="#{matriculaGraduacao.horariosTutoria}" var="horario" varStatus="status" >
					 
					  <tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
						<td width="55%">
							<h:selectBooleanCheckbox id="dia_${horario.diaSemana}" value="#{horario.selecionado}" />
							<label for="dia_${horario.diaSemana}">${horario.diaSemanaString}</label>
						</td>
						
						<td valign="top">
		
							<table width="100%">
								<tr>
									<td width="15%">
									
										<h:selectBooleanCheckbox id="M_${horario.diaSemana}" value="#{horario.matutino}" />
										<label for="f:M_${horario.diaSemana}">Matutino</label>
									</td>
									<td width="15%">
										<h:selectBooleanCheckbox id="V_${horario.diaSemana}" value="#{horario.vespertino}" />
										<label for="f:V_${horario.diaSemana}">Vespertino</label>
									</td>
									<td width="15%">
										<h:selectBooleanCheckbox id="N_${horario.diaSemana}" value="#{horario.noturno}"  />
										<label for="f:N_${horario.diaSemana}">Noturno</label>
									</td>
								</tr>
							</table>
						</td>
					</tr>
				 
				</c:forEach>
				 
				</table>
			 
				</td>
			</tr>
			 			
			 
			</tbody>
			<br>
			<tfoot>
				<tr>
					<td colspan="2">		
						<center>
							<h:commandButton value="Selecionar Outro Discente" action="#{matriculaGraduacao.buscarDiscente}" id="outroDiscnt" rendered="#{not matriculaGraduacao.alunoEadDefinindoHorarioTutoria}"/>													
							<h:commandButton value="#{matriculaGraduacao.confirmButtonHorarioTutoriaPresencial}" action="#{matriculaGraduacao.submeterInfoTutoria}" id="proxPasso"/>
							<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{matriculaGraduacao.cancelarMatricula}" id="cancelarMatricula"/>
						</center>
					</td>
				</tr>
			</tfoot>
		</table>
		
	</h:form>



</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
