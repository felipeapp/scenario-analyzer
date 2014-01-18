<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<a4j:keepAlive beanName="estruturaCurricularTecnicoMBean"/>
<h2> <ufrn:subSistema /> > Visualizar Estrutura Curricular </h2>
<h:form id="form">
	<table class="formulario" style="width: 70%">
	  <caption>Dados do Curr�culo</caption>
		<h:inputHidden value="#{estruturaCurricularTecnicoMBean.obj.id}" />
			<tr>
				<th width="25%"><b>C�digo da Estrutura:</b></th>
				<td colspan="5">
					<h:outputText value="#{estruturaCurricularTecnicoMBean.obj.codigo}" />
				</td>
			</tr>
			<tr>
				<th><b>Curso:</b></th>
				<td colspan="5"><h:outputText value="#{estruturaCurricularTecnicoMBean.obj.cursoTecnico}"/></td>
			</tr>
			<tr>
				<th><b>Prazos de Conclus�o:</b></th>
				<td width="30%">
					<h:outputText value="#{estruturaCurricularTecnicoMBean.obj.unidadeTempo.descricao}"/>
				</td>

				<th><b>M�nimo:</b></th>
				<td><h:outputText value="#{estruturaCurricularTecnicoMBean.obj.prazoMinConclusao}"/>
					${estruturaCurricularTecnicoMBean.obj.unidadeTempo.descricao}(s)
				</td>
	
				<th><b>M�ximo:</b></th>
				<td><h:outputText value="#{estruturaCurricularTecnicoMBean.obj.prazoMaxConclusao}"/>
					${estruturaCurricularTecnicoMBean.obj.unidadeTempo.descricao}(s)
				</td>
			</tr>
			<tr>
				<th><b>Carga Hor�ria:</b></th>
				<td><h:outputText value="#{estruturaCurricularTecnicoMBean.obj.chTotalModulos}"/></td>
			</tr>
			<tr>
				<th><b>Ano - Per�odo de Entrada em Vigor:</b></th>
				<td>
					<h:outputText value="#{estruturaCurricularTecnicoMBean.obj.anoEntradaVigor}"/> -
					<h:outputText value="#{estruturaCurricularTecnicoMBean.obj.periodoEntradaVigor}"/>
				</td>
			</tr>
			<tr>
				<th><b>Ativo:</b></th>
		 		<td><h:outputText value="#{estruturaCurricularTecnicoMBean.obj.ativa == 'true' ? 'Sim' : 'N�o'}" /></td>
		 	</tr>
			<tr>
				<th><b>Turno:</b></th>
				<td><h:outputText value="#{estruturaCurricularTecnicoMBean.obj.turno.descricao}" /></td>
			</tr>
			<tr>
				<td colspan="8">
					<table class="subFormulario" width="100%">
						<caption>M�dulos</caption>
						<thead>
							<tr>
								<td>M�dulo / Disciplina</td>
								<td style="text-align: right;">C.H.</td>
								<td style="text-align: right;">Pr. Oferta</td>
							</tr>
						</thead>
						<c:forEach items="#{estruturaCurricularTecnicoMBean.obj.modulosCurriculares}" var="linha">
							<tr>
								<td><b>${linha.modulo.descricao}</b></td>
								<td style="text-align: right;">${linha.modulo.cargaHoraria}</td>
								<td style="text-align: right;" width="15%">${linha.periodoOferta}</td>
								<c:forEach var="modulos" items="#{linha.modulo.moduloDisciplinas}">
									<tr>
										<td style="padding-left: 50px;" colspan="4">${modulos.disciplina}</td>
									</tr>
								</c:forEach>
							</tr>
						</c:forEach>
					</table>
				</td>
			</tr>
			<tr>
				<td colspan="8">
					<table class="subFormulario" width="100%">
						<caption>Disciplinas Complementares</caption>
						<thead>
							<tr>
								<td>Disciplina</td>
								<td style="text-align: right;">C.H.</td>
								<td style="text-align: right;">Pr. Oferta</td>
							</tr>
						</thead>
						<c:forEach var="linha" items="#{estruturaCurricularTecnicoMBean.obj.disciplinasComplementares}">
							<tr>
								<td>${linha.disciplina.detalhes.nome}</td>
								<td style="text-align: right;">${linha.disciplina.detalhes.chTotal}</td>
								<td style="text-align: right;" width="15%">${linha.periodoOferta}</td>				
							</tr>
						</c:forEach>
					</table>
				</td>
			</tr>
		  <tfoot>
			   <tr>
					<td colspan="6">
						<h:commandButton value="<< Voltar" action="#{estruturaCurricularTecnicoMBean.listar}" id="listar" rendered="#{estruturaCurricularTecnicoMBean.tecnico}"/>
						<h:commandButton value="Cancelar" action="#{estruturaCurricularTecnicoMBean.cancelar}" onclick="#{confirm}" id="cancelar" />
					</td>
			   </tr>
			</tfoot>
  </table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>