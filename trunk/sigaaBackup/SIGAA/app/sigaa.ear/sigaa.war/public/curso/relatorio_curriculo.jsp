<%@include file="/public/include/cabecalho.jsp"%>
<style>
	tr.tituloRelatorio td {padding: 19px 0 0; border-bottom: 1px solid #555 ; font-weight: bold; font-size: 13px; }
	tr.header td {padding: 3px ; border-bottom: 1px solid #555; background-color: #eee;}
	tr.componentes td {padding: 4px 2px 2px ; border-bottom: 1px dashed #888;}
	.colCodigo{text-align:left !important;}
</style>
<f:view>
	<f:loadBundle basename="br.ufrn.sigaa.sites.jsf.Mensagens" var="idioma"/>
<c:if test="${curriculo.graduacao}">
	<!--  Scripts do YAHOO -->
	<link rel="stylesheet" type="text/css" href="/shared/javascript/yui/tabview/assets/tabs.css">
	<link rel="stylesheet" type="text/css" href="/shared/javascript/yui/tabview/assets/border_tabs.css">
	<script type="text/javascript" src="/shared/javascript/yui/tabview-min.js"></script>

	<style>
		table.formulario th {
			font-weight: bold;
		}
	</style>		
	<script type="text/javascript">
	var criarAbas = function() {
	    var tabView = new YAHOO.widget.TabView('tabs-semestres');
	};
	criarAbas();
	</script>
	
	
	
</c:if>
	<h:messages showDetail="true"></h:messages>
	<h2 class="title"><h:outputText value="#{idioma.componenteCurricular}"/> </h2>

	<h:form id="formulario">
		<div class="legenda">
			<f:verbatim><h:graphicImage url="/img/view.gif" />:&nbsp;</f:verbatim><h:outputText value="#{idioma.visualizarDetalhesComponente}"/>
			<f:verbatim><h:graphicImage url="/img/report.png"/>:&nbsp;</f:verbatim><h:outputText value="#{idioma.visualizarPrograma}"/>
		</div>
		<h3 class="tituloTabela" style="width: 700px">Dados do Currículo</h3>
		<table class="formulario" width="700px">
			<tr>
				<th>Código:</th>
				<td><h:outputText value="#{curriculo.obj.codigo}" /></td>
			</tr>
			<c:if test="${curriculo.graduacao}">
				<tr>
					<th><h:outputText value="#{idioma.matrizCurricular}" />:</th>
					<td><h:outputText value="#{curriculo.obj.matriz.descricao }" /></td>
				</tr>
			</c:if>
			<c:if test="${not curriculo.graduacao}">
				<tr>
					<th><h:outputText value="#{idioma.curso}" />:</th>
					<td><h:outputText value="#{curriculo.obj.curso.descricao}" /></td>
				</tr>
			</c:if>
			<tr>
				<th>Período Letivo de Entrada em Vigor</th>
				<td><h:outputText
					value="#{curriculo.obj.anoEntradaVigor} - #{curriculo.obj.periodoEntradaVigor }" /></td>
			</tr>
			<tr>
				<th><h:outputText value="#{idioma.cargaHoraria}" />:</th>
				<td>
				<table>
					<tr>
						<td>TotalMínima</td>
						<td><h:outputText value="#{curriculo.obj.chTotalMinima}" />,</td>
						<td>Optativas Mínima</td>
						<td><h:outputText value="#{curriculo.obj.chOptativasMinima}" /></td>
					</tr>
				</table>
				</td>
			</tr>
			<tr>
				<th>Prazos em Períodos Letivo:</th>
				<td>
				<table>
					<tr>
						<td><h:outputText value="#{idioma.minimo}" /></td>
						<td><h:outputText value="#{curriculo.obj.semestreConclusaoMinimo}" />,</td>
						<td><h:outputText value="#{idioma.medio}" /></td>
						<td><h:outputText value="#{curriculo.obj.semestreConclusaoIdeal}" />,</td>
						<td><h:outputText value="#{idioma.maximo}" /></td>
						<td><h:outputText value="#{curriculo.obj.semestreConclusaoMaximo}" /></td>
					</tr>
				</table>
				</td>
			</tr>
			<c:if test="${curriculo.graduacao}">
				<tr>
					<th>Créditos por Período Letivo:</th>
					<td>
					<table>
						<tr>
							<td>Mínimo</td>
							<td><h:outputText value="#{curriculo.obj.crMinimoSemestre}" />,</td>
							<td>Médio</td>
							<td><h:outputText value="#{curriculo.obj.crIdealSemestre}" />,</td>
							<td>Máximo</td>
							<td><h:outputText value="#{curriculo.obj.crMaximoSemestre}" /></td>
						</tr>
					</table>
					</td>
				</tr>
			</c:if>
			<c:if test="${curriculo.graduacao}">
				<tr>
					<td colspan="2">
					<c:if test="${not empty curriculo.semestres}">
						<table>
						<c:forEach items="#{curriculo.semestres }" var="semestre">
							<tr class="tituloRelatorio">
								<td colspan="3">${semestre}º <h:outputText value="#{idioma.periodo}"/></td>
							</tr>
							<tr class="header">
								<td><h:outputText value="#{idioma.componenteCurricular}"/></td>
								<td>Natureza</td>
								<td>&nbsp;</td>
							</tr>
							<c:set value="0" var="ch" />
							<c:if test="${not empty curriculo.componentesAdicionados[semestre]}">
								<c:forEach items="#{curriculo.componentesAdicionados[semestre]}" var="cc" varStatus="linha">
									<c:set value="${ch + cc.componente.chTotal }" var="ch" />
									<tr class="componentes">
										<td>${cc.componente.descricao}</td>
										<td width="8%"><i>${(cc.obrigatoria)?'Obrigatória':'Optativa'}</i></td>
										<td width="8%" align="center">
											<h:commandLink title="Visualizar Detalhes do Componente Curricular" action="#{componenteCurricular.detalharComponente}">
												<h:graphicImage url="/img/view.gif"/>
												<f:param name="id" value="#{cc.componente.id}"/>
												<f:param name="publico" value="#{componenteCurricular.consultaPublica}"/>
											</h:commandLink>
											<h:commandLink title="Visualizar Programa" action="#{programaComponente.gerarRelatorioPrograma}">
												<h:graphicImage url="/img/report.png"/>
												<f:param name="idComponente" value="#{cc.componente.id}"/>
											</h:commandLink>
										</td>
									</tr>
								</c:forEach>
							</c:if>
							<tr>
								<td colspan="2"><b>CH Total:</b> ${ch}h. &nbsp;&nbsp;&nbsp; </td>
							</tr>
						</c:forEach>
						</table>
					</c:if>	
					</td>
				</tr>
			</c:if>
			<c:if test="${not curriculo.graduacao}">
				<tr><td colspan="2" align="center">
					<table class="listagem" width="100%" >
						<caption>Componentes desse Currículo</caption>
						<c:set value="0" var="ch" />
						<c:if test="${not empty curriculo.obj.curriculoComponentes}">
							<c:forEach items="${curriculo.obj.curriculoComponentes}" var="cc">
								<c:set value="${ch + cc.componente.chTotal }" var="ch" />
								<tr>
									<td>${cc.componente.descricao}</td>
									<td width="8%"><i>${(cc.obrigatoria)?'Obrigatória':'Optativa'}</i></td>
								</tr>
							</c:forEach>
						</c:if>	
						<tr style="background-color: #EFEBDE">
							<td colspan="3"><b>CH Total:</b> ${ch}h. &nbsp;&nbsp;&nbsp; <b>Total de Créditos:</b> <fmt:formatNumber pattern="##" value="${ch/15 }" /></td>
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
<%@include file="/public/include/rodape.jsp"%>