<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp" %>

<f:view>
	<a4j:keepAlive beanName="matriculaDiscenteSemTurma"/>	
	<h2><ufrn:subSistema /> > Matricular Discentes sem Turma > Sele��o da Turma</h2>
		
	<h:form>
		<div class="infoAltRem" style="width: 100%">
			<h:graphicImage value="/img/seta.gif"style="overflow: visible;"/>
			Selecionar Turma
		</div>
    
		<table class="formulario" width="100%">
			<caption class="listagem">Sele��o da turma</caption>
			<tr>
				<td colspan="2">
					<h:inputHidden value="#{matriculaDiscenteSemTurma.idOpcaoPoloSelecionado}"/>
				</td>
			<tr>
			
			<tr>
				<th class="obrigatorio">Op��o P�lo Grupo:</th>
				<td>
					<h:selectOneMenu value="#{matriculaDiscenteSemTurma.idOpcaoPoloSelecionado}" id="opcaoPoloGrupo" required="true">
						<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
						<f:selectItems value="#{matriculaDiscenteSemTurma.opcaoPolosCombo}" />
						<a4j:support event="onchange" reRender="turmas" />
					</h:selectOneMenu>
				</td>
			</tr>
			
			<tr>
				<td colspan="2" style="height: 10px;"></td>
			</tr>
			<tr>
					<td colspan="2">
						<a4j:outputPanel id="turmas">
							<table class="subFormulario" style="width: 100%">
								<c:if test="${!empty matriculaDiscenteSemTurma.listaTurmas}">
									<caption>Turmas vinculadas a Op��o P�lo Grupo selecionada
										(${fn:length(matriculaDiscenteSemTurma.listaTurmas)})</caption>

									<thead>
										<tr>
											<th style="text-align: left;">Ano - Per�odo</th>
											<th style="text-align: left;">Turma</th>
											<th style="text-align: left;">Local</th>
											<th style="text-align: left;">Hor�rio</th>
											<th></th>

										</tr>
									</thead>

									<tbody>
										<c:forEach var="linha" items="#{matriculaDiscenteSemTurma.listaTurmas}" varStatus="status">
											<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
												<td width="15%" style="text-align: left;">${linha.anoReferencia} - ${linha.periodoReferencia}</td>
												<td width="15%" style="text-align: left;">${linha.especializacao.descricao}</td>
												<td width="15%" style="text-align: left;">${linha.dadosTurmaIMD.local}</td>
												<td width="15%"style="text-align: left;">${linha.dadosTurmaIMD.horario}</td>
												
												<td width="3%" align="right">
													<h:commandLink action="#{matriculaDiscenteSemTurma.salvarTurmaOpcaoPolo}">
														<h:graphicImage value="/img/seta.gif" style="overflow: visible;" title="Selecionar Turma" alt="Selecionar Turma" />  
														<f:param name="idTurma" value="#{linha.id}"/>
													</h:commandLink>
												</td>
											</tr>
										</c:forEach>
									</tbody>
								</c:if>
							</table>
						</a4j:outputPanel>
					</td>
				</tr>
			
		<!-- Bot�es -->
		
		<tfoot>
			<tr>
				<td colspan="2">
					
					<h:commandButton value="Cancelar" action="#{matriculaDiscenteSemTurma.cancelar}" onclick="#{confirm}"/>
				</td>
			</tr>
		</tfoot>
		
	 	<!-- Fim bot�es -->
	 	
	 	</table>
		
	</h:form>
	
</f:view>
<%@ include file="/WEB-INF/jsp/include/rodape.jsp" %>