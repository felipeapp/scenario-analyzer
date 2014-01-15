<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<c:if test="${curriculo.graduacao}">
	<!--  Scripts do YAHOO -->
	<link rel="stylesheet" type="text/css" href="/shared/javascript/yui/tabview/assets/tabs.css">
	<link rel="stylesheet" type="text/css" href="/shared/javascript/yui/tabview/assets/border_tabs.css">
	<script type="text/javascript" src="/shared/javascript/yui/tabview-min.js"></script>

	<script type="text/javascript">
	var criarAbas = function() {
	    var tabView = new YAHOO.widget.TabView('tabs-semestres');
	    var tabView2 = new YAHOO.widget.TabView('tabs-gruposoptativas');
	};
	criarAbas();
	</script>
</c:if>
	<h:messages showDetail="true"></h:messages>
	<h2 class="title">Detalhes da Estrutura Curricular</h2>

	<h:form id="formulario">

		<table class="formulario" width="700px">
			<caption class="formulario">Dados da Estrutura Curricular</caption>
			<tr>
				<th>Código:</th>
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
					<th>Curso:</th>
					<td><h:outputText value="#{curriculo.obj.curso.descricao}" /></td>
				</tr>
			</c:if>
			<tr>
				<th>Período Letivo de Entrada em Vigor</th>
				<td><h:outputText
					value="#{curriculo.obj.anoEntradaVigor}.#{curriculo.obj.periodoEntradaVigor }" /></td>
			</tr>
			<tr>
				<th>Carga Horária:</th>
				<td>
				<table width="70%">
					<tr>
						<th>TotalMínima:</th>
						<td><h:outputText value="#{curriculo.obj.chTotalMinima}" />h</td>
						<th>Optativas Mínima:</th>
						<td><h:outputText value="#{curriculo.obj.chOptativasMinima}" />h</td>
					</tr>
				</table>
				</td>
			</tr>
			<tr>	
				<th>Créditos Por Período Letivo:</th>
				<td>
					<table width="70%">
						<tr>
							<th>Mínimo: </th>
							<td>
								<h:outputText value="#{curriculo.obj.crMinimoSemestre}"/> 
							</td>
							<th>Regulamentar: </th>
							<td>
								<h:outputText  value="#{curriculo.obj.crIdealSemestre}"/> 
							</td>
							<th>Máximo: </th>
							<td>
								<h:outputText value="#{curriculo.obj.crMaximoSemestre}" /> 
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<th>
					Prazo Para Conclusão: <br />
					<span class="info">${curriculo.graduacao? '(em semestres)' : '(em meses)'}</span>
				</th>
				<td>
		  		<table width="70%">
					<tr>
						<th>Mínimo:</th>
						<td>
							<h:outputText value="#{curriculo.obj.semestreConclusaoMinimo}" /> 
						</td>
						<th>Médio:</th>
						<td>
							<h:outputText value="#{curriculo.obj.semestreConclusaoIdeal}" /> 
						</td>
						<th>Máximo:</th>
						<td>
							<h:outputText value="#{curriculo.obj.semestreConclusaoMaximo}" /> 
						</td>
					</tr>
				</table>
				</td>
			</tr>
			<tr>
				<th>Créditos Obrigatórios:</th>
				<td>
					${curriculo.obj.crPraticos + curriculo.obj.crTeoricos} Total - ( ${curriculo.obj.crPraticos}  Práticos ) / ( ${curriculo.obj.crTeoricos} Teóricos )
				</td>
			</tr>
			<tr>
				<th>Carga Horária Obrigatória:</th>
				<td>
					${curriculo.obj.chPraticos + curriculo.obj.chTeoricos} Total - ( ${curriculo.obj.chPraticos} Práticos ) / ( ${curriculo.obj.chTeoricos} Teóricos )
				</td>
			</tr>
			<tr>
				<th>Carga Horária Obrigatória de Atividade Acadêmica Específica:</th>
				<td>
					${curriculo.obj.chAAE} hrs
				</td>
			</tr>
			<tr>
				<th>Carga Horária de Componentes Eletivos: </th>
				<td>
				<table>
					<tr>
						<th>Máxima</th>
						<td>(<h:outputText value="#{curriculo.obj.maxEletivos}" /> horas)</td>
						<th></th>
						<td></td>
						<th></th>
						<td></td>
					</tr>
				</table>
				</td>
			</tr>
			<tr>
				<th>Carga Horária por Período Letivo:</th>
				<td>
				<table>
					<tr>
						<th>Mínima</th>
						<td>(<h:outputText value="#{curriculo.obj.chMinimaSemestre}" /> horas)</td>
						<th></th>
						<td></td>
						<th></th>
						<td></td>
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
							<th>Mínimo</th>
							<td><h:outputText value="#{curriculo.obj.crMinimoSemestre}" /></td>
							<th>Médio</th>
							<td><h:outputText value="#{curriculo.obj.crIdealSemestre}" /></td>
							<th>Máximo</th>
							<td><h:outputText value="#{curriculo.obj.crMaximoSemestre}" /></td>
						</tr>
					</table>
					</td>
				</tr>
			</c:if>
			<tr>
				<th>Prazos em Períodos Letivos:</th>
				<td>
				<table>
					<tr>
						<th>Mínimo</th>
						<td><h:outputText value="#{curriculo.obj.semestreConclusaoMinimo}" /></td>
						<th>Médio</th>
						<td><h:outputText value="#{curriculo.obj.semestreConclusaoIdeal}" /></td>
						<th>Máximo</th>
						<td><h:outputText value="#{curriculo.obj.semestreConclusaoMaximo}" /></td>
					</tr>
				</table>
				</td>
			</tr>
			<c:if test="${curriculo.graduacao}">
				<tr>
					<td colspan="2 align="center">
						<div id="tabs-semestres" class="yui-navset">
						<ul class="yui-nav">
							<c:forEach items="${curriculo.semestres }" var="semestre" varStatus="i">
							<li class="${(i.count == curriculo.exibirSemestre)?'selected':'' }"><a href="#semestre${semestre}"><em>${semestre }&ordm;</em></a></li>
							</c:forEach>
						</ul>
						<div class="yui-content">
						<c:forEach items="${curriculo.semestres }" var="semestre">
						<c:set value="0" var="ch" />
						<div id="semestre${semestre }">
							<table class="listagem" width="100%" >
								<caption>${semestre }&ordm; Nível</caption>
								<c:forEach items="${curriculo.componentesAdicionados[semestre]}" var="cc" varStatus="linha">
								<c:set value="${ch + cc.componente.chTotal }" var="ch" />
									<tr>
										<td>${cc.componente.descricao}</td>
										<td width="8%"><i>${(cc.obrigatoria)?'Obrigatória':'Optativa'}</i></td>
									</tr>
								</c:forEach>
								<tr style="background-color: #EFEBDE">
								<td colspan="2"><b>CH Total:</b> ${ch}hrs. &nbsp;&nbsp;&nbsp; </td>
								</tr>
							</table>
						</div>
					</c:forEach>
					</div>
					</td>
				</tr>
				
				<c:if test="${ not empty curriculo.obj.enfases }">
				<tr>
					<td colspan="2" align="left">
						<div id="tabs-gruposoptativas" class="yui-navset">
						<ul class="yui-nav">
							<c:forEach items="${curriculo.obj.enfases }" var="grupo" varStatus="i">
							<li class="${ i.first ? 'selected' : '' }"><a href="#grupo${grupo.id}"><em>Grupo de Optativas ${ i.index + 1 }</em></a></li>
							</c:forEach>
						</ul>
						<div class="yui-content">
						<c:forEach items="${curriculo.obj.enfases }" var="grupo">
						<c:set value="0" var="ch" />
						<div id="grupo${grupo.id} }">
							<table class="listagem" width="100%" >
								<caption>${grupo.descricao }</caption>
								<c:forEach items="${grupo.componentes}" var="cc" varStatus="linha">
								<c:set value="${ch + cc.componente.chTotal }" var="ch" />
									<tr>
										<td>${cc.componente.descricao}</td>
										<td width="8%"><i>${(cc.obrigatoria)?'Obrigatória':'Optativa'}</i></td>
									</tr>
								</c:forEach>
								<tr style="background-color: #EFEBDE">
								<td colspan="2"><b>CH Total:</b> ${ch}hrs. &nbsp;&nbsp;&nbsp; </td>
								</tr>
								<tr style="background-color: #EFEBDE">
								<td colspan="2"><b>CH Mínima</b> ${ grupo.chMinima }hrs. </td>
								</tr>
							</table>
						</div>
						</c:forEach>
						</div>
					</td>
				</tr>
				</c:if>
			</c:if>
			<c:if test="${!curriculo.graduacao}">
				<tr><td colspan="2" align="center">
					<table class="listagem" width="100%" >
						<caption>Componentes desse Currículo</caption>
						<c:set value="0" var="ch" />
						<c:forEach items="${curriculo.obj.curriculoComponentes}" var="cc">
							<c:set value="${ch + cc.componente.chTotal }" var="ch" />
							<tr>
								<td>${cc.componente.descricao}</td>
								<td width="8%"><i>${(cc.obrigatoria)?'Obrigatória':'Optativa'}</i></td>
							</tr>
						</c:forEach>
						<tr style="background-color: #EFEBDE">
							<td colspan="3"><b>CH Total:</b> ${ch}hrs. &nbsp;&nbsp;&nbsp; <b>Total de Créditos:</b> <fmt:formatNumber pattern="##" value="${ch/15 }" /></td>
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
	<a href="javascript: history.go(-1);"> << Voltar </a>
	</center>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
