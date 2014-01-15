<%@include file="/public/include/cabecalho.jsp"%>
<style>
	table.visualizacao th {font-weight: bold;}
</style>
<f:view>
	<f:loadBundle basename="br.ufrn.sigaa.sites.jsf.Mensagens" var="idioma"/>
	<h:outputText value="#{componenteCurricular.create}" />
	
	<h2 class="title"><h:outputText value="#{idioma.detalhesEstruturaCurricular}" /></h2>

	<h:form id="formulario">
		<div class="legenda">
			<f:verbatim><h:graphicImage url="/img/view.gif" />:&nbsp;</f:verbatim><h:outputText value="#{idioma.visualizarDetalhesComponente}"/>
			<f:verbatim><h:graphicImage url="/img/report.png"/>:&nbsp;</f:verbatim><h:outputText value="#{idioma.visualizarPrograma}"/>
		</div>
		<table class="visualizacao" style="width:85%">
			<caption class="formulario">Dados do Currículo</caption>
			<tr>
				<th><h:outputText value="#{idioma.codigo}"/>:</th>
				<td><h:outputText value="#{curriculo.obj.codigo }" /></td>
			</tr>
			<c:if test="${curriculo.graduacao}">
				<tr>
					<th>Matriz Curricular:</th>
					<td><h:outputText value="#{curriculo.obj.matriz.descricao }" /></td>
				</tr>
			</c:if>
			<c:if test="${!curriculo.graduacao}">
				<tr>
					<th><h:outputText value="#{idioma.curso}"/>:</th>
					<td><h:outputText value="#{curriculo.obj.curso.descricao}" /></td>
				</tr>
			</c:if>
			<tr>
				<th><h:outputText value="#{idioma.periodoLetivoEntradaVigor}"/></th>
				<td><h:outputText
					value="#{curriculo.obj.anoEntradaVigor} - #{curriculo.obj.periodoEntradaVigor }" /></td>
			</tr>
			<tr>
				<th><h:outputText value="#{idioma.cargaHoraria}"/>:</th>
				<td>
				<table>
					<tr>
						<th><h:outputText value="#{idioma.totalMinima}"/></th>
						<td><h:outputText value="#{curriculo.obj.chTotalMinima}" /></td>
						<th><h:outputText value="#{idioma.optativaMinima}"/></th>
						<td><h:outputText value="#{curriculo.obj.chOptativasMinima}" /></td>
					</tr>
				</table>
				</td>
			</tr>
			<tr>
				<th><h:outputText value="#{idioma.creditoObrigatorio}" />:</th>
				<td>
					${curriculo.obj.crPraticos + curriculo.obj.crTeoricos} Total - ( ${curriculo.obj.crPraticos}  Práticos ) / ( ${curriculo.obj.crTeoricos} Teóricos )
				</td>
			</tr>
			<tr>
				<th><h:outputText value="#{idioma.cargaHorariaObrigatoria}" />:</th>
				<td>
					${curriculo.obj.chPraticos + curriculo.obj.chTeoricos} Total - ( ${curriculo.obj.chPraticos} Práticos ) / ( ${curriculo.obj.chTeoricos} Teóricos )
				</td>
			</tr>
			<tr>
				<th><h:outputText value="#{idioma.cargaHorariaObrigAtivAcadEsp}" />:</th>
				<td>
					${curriculo.obj.chAAE} hrs
				</td>
			</tr>			
			<tr>
				<th><h:outputText value="#{idioma.prazoPeriodoLetivo}"/>:</th>
				<td>
				<table>
					<tr>
						<th><h:outputText value="#{idioma.minimo}"/></th>
						<td><h:outputText value="#{curriculo.obj.semestreConclusaoMinimo}" /></td>
						<th><h:outputText value="#{idioma.medio}"/></th>
						<td><h:outputText value="#{curriculo.obj.semestreConclusaoIdeal}" /></td>
						<th><h:outputText value="#{idioma.maximo}"/></th>
						<td><h:outputText value="#{curriculo.obj.semestreConclusaoMaximo}" /></td>
					</tr>
				</table>
				</td>
			</tr>
			<c:if test="${curriculo.graduacao}">
				<tr>
					<th><h:outputText value="#{idioma.creditoSemestre}"/>:</th>
					<td>
					<table>
						<tr>
							<th><h:outputText value="#{idioma.minimo}"/></th>
							<td><h:outputText value="#{curriculo.obj.crMinimoSemestre}" /></td>
							<th><h:outputText value="#{idioma.medio}"/></th>
							<td><h:outputText value="#{curriculo.obj.crIdealSemestre}" /></td>
							<th><h:outputText value="#{idioma.maximo}"/></th>
							<td><h:outputText value="#{curriculo.obj.crMaximoSemestre}" /></td>
						</tr>
					</table>
					</td>
				</tr>
			</c:if>
			<c:if test="${curriculo.graduacao}">
				<tr>
					<td colspan="2 align="center">
						<div id="tabs-semestres" class="yui-navset">
						<ul class="yui-nav">
							<c:if test="${not empty curriculo.semestres }">
								<c:forEach items="${curriculo.semestres }" var="semestre" varStatus="i">
								<li class="${(i.count == curriculo.exibirSemestre)?'selected':'' }"><a href="#semestre${semestre}"><em>${semestre }&ordm;  <h:outputText value="#{idioma.nivel}"/></em></a></li>
								</c:forEach>
							</c:if>
						</ul>
						<div class="yui-content">
						
						<c:if test="${not empty curriculo.semestres }">
							<c:forEach items="#{curriculo.semestres }" var="semestre">
							<c:set value="0" var="ch" />
							
							<div id="semestre${semestre }">
								<table class="subFormulario" width="100%" >
									<caption>${semestre }&ordm; <h:outputText value="#{idioma.nivel}"/></caption>
									<c:if test="${not empty curriculo.componentesAdicionados[semestre]}">
										<c:forEach items="#{curriculo.componentesAdicionados[semestre]}" var="cc" varStatus="loop">
										<c:set value="${ch + cc.componente.chTotal }" var="ch" />
											<tr class="${loop.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
												<td>${cc.componente.descricao}</td>
												<td width="8%">
													<i>
														<h:outputText value="#{idioma.obrigatoria}" rendered="#{cc.obrigatoria}" />
														<h:outputText value="#{idioma.optativa}" rendered="#{!cc.obrigatoria}"/>
													</i>
												</td>
												<td style="text-align: center">
													<h:commandLink title="#{idioma.visualizarDetalhesComponente}" action="#{componenteCurricular.detalharComponente}">
														<h:graphicImage url="/img/view.gif"/>
													<f:param name="id" value="#{cc.componente.id}"/>
													<f:param name="publico" value="#{componenteCurricular.consultaPublica}"/>
													</h:commandLink>
		
													<h:commandLink title="#{idioma.visualizarPrograma}" action="#{programaComponente.gerarRelatorioPrograma}">
														<h:graphicImage url="/img/report.png"/>
														<f:param name="idComponente" value="#{cc.componente.id}"/>
													</h:commandLink>
												</td>
											</tr>
										</c:forEach>
									</c:if>	
									<tr style="background-color: #EFEBDE">
									<td colspan="3"><b><h:outputText value="#{idioma.cargaHorariaTotal}"/>:</b> ${ch}hrs. &nbsp;&nbsp;&nbsp; </td>
									</tr>
								</table>
							</div>
							</c:forEach>
						</c:if>
					</div>
					</td>
				</tr>
			</c:if>
			<c:if test="${!curriculo.graduacao}">
				<tr><td colspan="2" align="center">
					<table class="listagem" width="100%" >
						<caption>Componentes desse Currículo</caption>
						<c:set value="0" var="ch" />
						<c:if test="${not empty curriculo.obj.curriculoComponentes}">
							<c:forEach items="${curriculo.obj.curriculoComponentes}" var="cc">
								<c:set value="${ch + cc.componente.chTotal }" var="ch" />
								<tr>
									<td>${cc.componente.descricao}</td>
									<td width="8%">
									<i>
									<h:outputText value="#{idioma.obrigatoria}" rendered="#{cc.obrigatoria=='Obrigatória'}" />
									<h:outputText value="#{idioma.optativa}" rendered="#{cc.obrigatoria=='Optativa'}"/>
									</i>
								</td>
								</tr>
							</c:forEach>
						</c:if>
						<tr style="background-color: #EFEBDE">
							<td colspan="3"><b><h:outputText value="#{idioma.cargaHorariaTotal}"/>:</b> ${ch}hrs. &nbsp;&nbsp;&nbsp;
							 <b><h:outputText value="#{idioma.totalCredito}"/>:</b> <fmt:formatNumber pattern="##" value="${ch/15 }" /></td>
						</tr>
					</table>
				</td></tr>
			</c:if>
			<tfoot>
				<tr>
					<td colspan="2">&nbsp;</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<br>
	<center>
	<a href="javascript: history.go(-1);"> << <h:outputText value="#{idioma.voltar}"/> </a>
	</center>
</f:view>

<c:if test="${curriculo.graduacao}">
	<!--  Scripts do YAHOO -->
	<link rel="stylesheet" type="text/css" href="/shared/javascript/yui/tabview/assets/tabs.css">
	<link rel="stylesheet" type="text/css" href="/shared/javascript/yui/tabview/assets/border_tabs.css">
	<script type="text/javascript" src="/shared/javascript/yui/tabview-min.js"></script>

	<script type="text/javascript">
	var criarAbas = function() {
	    var tabView = new YAHOO.widget.TabView('tabs-semestres');
	};
	criarAbas();
	</script>
</c:if>
<%@include file="/public/include/rodape.jsp"%>
