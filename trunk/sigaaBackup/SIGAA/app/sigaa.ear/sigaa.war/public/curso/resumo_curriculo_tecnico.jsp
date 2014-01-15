<%@include file="/public/include/cabecalho.jsp"%>
<style>
		table.formulario th {
			font-weight: bold;
		}
	</style>
<f:view>
	<f:loadBundle basename="br.ufrn.sigaa.sites.jsf.Mensagens" var="idioma"/>
	<h2 class="title"><h:outputText value="#{idioma.detalhesEstruturaCurricular}" /> </h2>

	<h:form id="formulario">
		<table width="85%" class="formulario">
			<caption><h:outputText value="#{idioma.resumoEstruturaCurricular}" /></caption>
			<tbody>
				<tr>
					<th ><h:outputText value="#{idioma.codigo}" />:</th>
					<td colspan="4">${consultaPublicaCursos.estruturaCurricularTecnica.codigo}</td>
				</tr>

				<tr>
					<th><h:outputText value="#{idioma.curso}"/>:</th>
					<td colspan="4">${consultaPublicaCursos.estruturaCurricularTecnica.cursoTecnico.nome}</td>
				</tr>

				<tr>
					<th><h:outputText value="#{idioma.prazoConclusao}"/> (<h:outputText value="#{idioma.minimo}"/>):</th>
					<td width="100">
						${consultaPublicaCursos.estruturaCurricularTecnica.prazoMinConclusao} 
						${consultaPublicaCursos.estruturaCurricularTecnica.unidadeTempo.descricao} (s)
					</td>
					<th><h:outputText value="#{idioma.prazoConclusao}"/> (<h:outputText value="#{idioma.maximo}"/>):</th>
					<td>
						${consultaPublicaCursos.estruturaCurricularTecnica.prazoMaxConclusao}
						${consultaPublicaCursos.estruturaCurricularTecnica.unidadeTempo.descricao} (s)
					</td>
					<td width="100"></td>
				</tr>

				<tr>
					<th><h:outputText value="#{idioma.cargaHoraria}"/>:</th>
					<td colspan="4">${consultaPublicaCursos.estruturaCurricularTecnica.chTotalModulos}</td>
				</tr>

				<tr>
					<th><h:outputText value="#{idioma.periodoLetivoEntradaVigor}"/>:</th>
					<td>${consultaPublicaCursos.estruturaCurricularTecnica.anoEntradaVigor } - ${consultaPublicaCursos.estruturaCurricularTecnica.periodoEntradaVigor}</td>
					<th><h:outputText value="#{idioma.ativa}"/>:</th>
					<td colspan="2"><ufrn:format type="bool_sn"
						valor="${ consultaPublicaCursos.estruturaCurricularTecnica.ativa }" />
					</td>
				</tr>

				<tr>
					<th><h:outputText value="#{idioma.turno}"/>:</th>
					<td colspan="4">${consultaPublicaCursos.estruturaCurricularTecnica.turno.descricao }</td>
				</tr>

				<%-- LISTA DE MÓDULOS --%>
				<c:if test="${ not empty consultaPublicaCursos.estruturaCurricularTecnica.modulosCurriculares }">
					<tr>
						<td colspan="5">
						<table class="subFormulario" width="99%">
							<thead>
								<td><b>Módulos cadastrados</b></td>
								<td width="60px" align="center" ><b><h:outputText value="#{idioma.cargaHoraria}"/></b></td>
								<td width="90px" align="center" ><b>Pr. Oferta</b></td>
							</thead>

							<tbody>
								<c:forEach items="${consultaPublicaCursos.estruturaCurricularTecnica.modulosCurriculares}" var="moduloCurricular">
									<tr>
										<td><b>${moduloCurricular.modulo.descricao}</b></td>
										<td align="center" width="60px">${moduloCurricular.modulo.cargaHoraria} hrs</td>
										<td align="center" width="90px">${moduloCurricular.periodoOferta}</td>
									</tr>
									<tr class="disciplinas${moduloCurricular.modulo.id}">
										<td colspan="3" style="padding-left: 30px;">
											<table width="100%" class="disciplinas"	id="disciplinas${moduloCurricular.modulo.id}">
												<c:if test="${not empty moduloCurricular.modulo.disciplinas}">
												<tr>
													<td colspan="3" style="font-size: 9px;"><b><h:outputText value="#{idioma.disciplina}"/>:</b>
													</td>
												</tr>
												<c:forEach items="${moduloCurricular.modulo.disciplinas}"
													var="disciplina">
													<c:if test="${disciplina.descricao != null}">
														<tr>
															<td style="font-size: 9px;">${disciplina.descricao}</td>
														</tr>
													</c:if>
												</c:forEach>
												</c:if>
											</table>
										</td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
						</td>
					</tr>
				</c:if>

				<c:if test="${ not empty consultaPublicaCursos.estruturaCurricularTecnica.disciplinasComplementares }">
					<%-- LISTA DE DISCIPLINAS COMPLEMENTARES --%>
					<tr>
						<td colspan="5">
							<table class="subFormulario" width="100%">
								<thead>
									<td><h:outputText value="#{idioma.disciplinaEletivaCadastrada}"/></td>
									<td width="50"><h:outputText value="#{idioma.cargaHoraria}"/></td>
									<td width="30">Pr. Oferta</td>
								</thead>
	
								<tbody>
									<c:forEach items="${consultaPublicaCursos.estruturaCurricularTecnica.disciplinasComplementares}" var="disciplinaComplementar">
										<tr>
											<td>${disciplinaComplementar.disciplina.codigo} - ${disciplinaComplementar.disciplina.nome}</td>
											<td align="center">${disciplinaComplementar.disciplina.chTotal}</td>
											<td align="center">${disciplinaComplementar.periodoOferta}</td>
										</tr>
									</c:forEach>
								</tbody>
							</table>
						</td>
					</tr>
				</c:if>
			</tbody>
		</table>
	</h:form>
	<br>
	<center><a href="javascript: history.go(-1);"> << <h:outputText value="#{idioma.voltar}"/></a>
	</center>
</f:view>

<c:if test="${curriculo.graduacao}">
	<!--  Scripts do YAHOO -->
	<link rel="stylesheet" type="text/css"
		href="/shared/javascript/yui/tabview/assets/tabs.css">
	<link rel="stylesheet" type="text/css"
		href="/shared/javascript/yui/tabview/assets/border_tabs.css">
	<script type="text/javascript"
		src="/shared/javascript/yui/tabview-min.js"></script>

	<script type="text/javascript">
	var criarAbas = function() {
		var tabView = new YAHOO.widget.TabView('tabs-semestres');
	};
	criarAbas();
</script>
</c:if>
<%@include file="/public/include/rodape.jsp"%>
