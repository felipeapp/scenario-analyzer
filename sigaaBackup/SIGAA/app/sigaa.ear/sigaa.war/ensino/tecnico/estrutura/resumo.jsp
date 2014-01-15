<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<a4j:keepAlive beanName="estruturaCurricularTecnicoMBean"/>
<h2> <ufrn:subSistema /> > Dados Gerais > Módulos > Disc. Complementares > Resumo </h2>
<h:form id="form">
	<table class="formulario" style="width: 80%">
	  <caption>Resumo da Estrutura Curricular</caption>
		<h:inputHidden value="#{estruturaCurricularTecnicoMBean.obj.id}" />
			<tr>
				<th width="30%"><b>Código da Estrutura:</b></th>
				<td>
					<h:outputText value="#{estruturaCurricularTecnicoMBean.obj.codigo}" />
				</td>
			</tr>
			<tr>
				<th><b>Curso:</b></th>
				<td nowrap="nowrap"><h:outputText value="#{estruturaCurricularTecnicoMBean.obj.cursoTecnico}"/></td>
			</tr>
			<tr>
				<th><b>Prazos de Conclusão:</b></th>
				<td>
					<h:outputText value="#{estruturaCurricularTecnicoMBean.obj.unidadeTempo.descricao}"/>
				</td>

				<th><b>Mínimo:</b></th>
				<td><h:outputText value="#{estruturaCurricularTecnicoMBean.obj.prazoMinConclusao}"/>
					${estruturaCurricularTecnicoMBean.obj.unidadeTempo.descricao}(s)
				</td>
	
				<th><b>Máximo:</b></th>
				<td><h:outputText value="#{estruturaCurricularTecnicoMBean.obj.prazoMaxConclusao}"/>
					${estruturaCurricularTecnicoMBean.obj.unidadeTempo.descricao}(s)
				</td>
			</tr>
			<tr>
				<th><b>CH Total Módulos (Obrigatórias):</b></th>		
				<td><h:outputText value="#{estruturaCurricularTecnicoMBean.obj.chTotalModulos}"/> ch</td>
			</tr>
			<tr>
				<th><b>Carga horária Complementar:</b></th>				
				<td><h:outputText value="#{estruturaCurricularTecnicoMBean.obj.chTotalDisciplinasComplementares}"/> ch</td>
			</tr>		
			<tr>
				<th><b>Ano - Período de Entrada em Vigor:</b></th>
				<td>
					<h:outputText value="#{estruturaCurricularTecnicoMBean.obj.anoEntradaVigor}"/> -
					<h:outputText value="#{estruturaCurricularTecnicoMBean.obj.periodoEntradaVigor}"/>
				</td>
			</tr>
			<tr>
				<th><b>Ativo:</b></th>
		 		<td><h:outputText value="#{estruturaCurricularTecnicoMBean.obj.ativa == 'true' ? 'Sim' : 'Não'}" /></td>
		 	</tr>
			<tr>
				<th><b>Turno:</b></th>
				<td><h:outputText value="#{estruturaCurricularTecnicoMBean.obj.turno.descricao}" /></td>
			</tr>
			<tr>
				<th><b>Mínimo de Componentes Complementares:</b></th>
				<td><h:outputText value="#{estruturaCurricularTecnicoMBean.obj.chOptativasMinima }ch" /></td>
			</tr>	
			<tr>
				<td colspan="8">
					<table class="subFormulario" width="100%">
						<caption>Módulos Cadastrados</caption>
						<thead>
							<tr>
								<td>Módulo</td>
								<td style="text-align: right;">C.H.</td>
								<td style="text-align: right;">Período de Oferta</td>
							</tr>
						</thead>
						<c:forEach items="#{estruturaCurricularTecnicoMBean.obj.modulosCurriculares}" var="linha">
							<tr>
								<td style="padding-left:2%; background-color: #C8D5EC"><b>${linha.modulo.descricao}</b></td>
								<td style="text-align: right;background-color: #C8D5EC">${linha.modulo.cargaHoraria}</td>
								<td style="text-align: right;background-color: #C8D5EC" width="20%">${linha.periodoOferta}</td>
								<c:if test="${not empty linha.modulo.moduloDisciplinas}">
									<c:forEach var="modulos" items="#{linha.modulo.moduloDisciplinas}" varStatus="status">
										<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
											<td style="padding-left: 50px;" colspan="4">${modulos.disciplina}</td>
										</tr>
									</c:forEach>
								</c:if>
							</tr>
						</c:forEach>
					</table>
				</td>
			</tr>
			<tr>
				<td colspan="8">
					<table class="subFormulario" width="100%">
						<caption>Disciplinas Complementares Cadastradas</caption>
						<thead>
							<tr>
								<td>Disciplina</td>
								<th style="text-align: right;">C.H.</th>
								<th style="text-align: right;">Período de Oferta</th>
							</tr>
						</thead>
						<c:forEach var="linha" items="#{estruturaCurricularTecnicoMBean.obj.disciplinasComplementares}" varStatus="status">
							<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
								<td>${linha.disciplina.detalhes.nome}</td>
								<td style="text-align: right;">${linha.disciplina.detalhes.chTotal}</td>
								<td style="text-align: right;" width="20%">${linha.periodoOferta}</td>				
							</tr>
						</c:forEach>
					</table>
				</td>
			</tr>
		  <tfoot>
			   <tr>
					<td colspan="6">
						<h:commandButton value="#{estruturaCurricularTecnicoMBean.confirmButton}" action="#{estruturaCurricularTecnicoMBean.cadastrar}" id="cadastrar" />
						<h:commandButton value="<< Voltar" action="#{estruturaCurricularTecnicoMBean.viewDisciplina}" id="disciplinas" />
						<h:commandButton value="Cancelar" action="#{estruturaCurricularTecnicoMBean.cancelar}" onclick="#{confirm}" immediate="true" id="cancelar" />
					</td>
			   </tr>
			</tfoot>
  </table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>