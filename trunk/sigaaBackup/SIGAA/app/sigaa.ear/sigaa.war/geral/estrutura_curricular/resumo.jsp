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
				<th>C�digo:</th>
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
				<th>Per�odo Letivo de Entrada em Vigor</th>
				<td><h:outputText
					value="#{curriculo.obj.anoEntradaVigor}.#{curriculo.obj.periodoEntradaVigor }" /></td>
			</tr>
			<tr>
				<th>Carga Hor�ria:</th>
				<td>
				<table width="70%">
					<tr>
						<th>TotalM�nima:</th>
						<td><h:outputText value="#{curriculo.obj.chTotalMinima}" />h</td>
						<th>Optativas M�nima:</th>
						<td><h:outputText value="#{curriculo.obj.chOptativasMinima}" />h</td>
					</tr>
				</table>
				</td>
			</tr>
			<tr>	
				<th>Cr�ditos Por Per�odo Letivo:</th>
				<td>
					<table width="70%">
						<tr>
							<th>M�nimo: </th>
							<td>
								<h:outputText value="#{curriculo.obj.crMinimoSemestre}"/> 
							</td>
							<th>Regulamentar: </th>
							<td>
								<h:outputText  value="#{curriculo.obj.crIdealSemestre}"/> 
							</td>
							<th>M�ximo: </th>
							<td>
								<h:outputText value="#{curriculo.obj.crMaximoSemestre}" /> 
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<th>
					Prazo Para Conclus�o: <br />
					<span class="info">${curriculo.graduacao? '(em semestres)' : '(em meses)'}</span>
				</th>
				<td>
		  		<table width="70%">
					<tr>
						<th>M�nimo:</th>
						<td>
							<h:outputText value="#{curriculo.obj.semestreConclusaoMinimo}" /> 
						</td>
						<th>M�dio:</th>
						<td>
							<h:outputText value="#{curriculo.obj.semestreConclusaoIdeal}" /> 
						</td>
						<th>M�ximo:</th>
						<td>
							<h:outputText value="#{curriculo.obj.semestreConclusaoMaximo}" /> 
						</td>
					</tr>
				</table>
				</td>
			</tr>
			<tr>
				<th>Cr�ditos Obrigat�rios:</th>
				<td>
					${curriculo.obj.crPraticos + curriculo.obj.crTeoricos} Total - ( ${curriculo.obj.crPraticos}  Pr�ticos ) / ( ${curriculo.obj.crTeoricos} Te�ricos )
				</td>
			</tr>
			<tr>
				<th>Carga Hor�ria Obrigat�ria:</th>
				<td>
					${curriculo.obj.chPraticos + curriculo.obj.chTeoricos} Total - ( ${curriculo.obj.chPraticos} Pr�ticos ) / ( ${curriculo.obj.chTeoricos} Te�ricos )
				</td>
			</tr>
			<tr>
				<th>Carga Hor�ria Obrigat�ria de Atividade Acad�mica Espec�fica:</th>
				<td>
					${curriculo.obj.chAAE} hrs
				</td>
			</tr>
			<tr>
				<th>Carga Hor�ria de Componentes Eletivos: </th>
				<td>
				<table>
					<tr>
						<th>M�xima</th>
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
				<th>Carga Hor�ria por Per�odo Letivo:</th>
				<td>
				<table>
					<tr>
						<th>M�nima</th>
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
					<th>Cr�ditos por Per�odo Letivo:</th>
					<td>
					<table>
						<tr>
							<th>M�nimo</th>
							<td><h:outputText value="#{curriculo.obj.crMinimoSemestre}" /></td>
							<th>M�dio</th>
							<td><h:outputText value="#{curriculo.obj.crIdealSemestre}" /></td>
							<th>M�ximo</th>
							<td><h:outputText value="#{curriculo.obj.crMaximoSemestre}" /></td>
						</tr>
					</table>
					</td>
				</tr>
			</c:if>
			<tr>
				<th>Prazos em Per�odos Letivos:</th>
				<td>
				<table>
					<tr>
						<th>M�nimo</th>
						<td><h:outputText value="#{curriculo.obj.semestreConclusaoMinimo}" /></td>
						<th>M�dio</th>
						<td><h:outputText value="#{curriculo.obj.semestreConclusaoIdeal}" /></td>
						<th>M�ximo</th>
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
								<caption>${semestre }&ordm; N�vel</caption>
								<c:forEach items="${curriculo.componentesAdicionados[semestre]}" var="cc" varStatus="linha">
								<c:set value="${ch + cc.componente.chTotal }" var="ch" />
									<tr>
										<td>${cc.componente.descricao}</td>
										<td width="8%"><i>${(cc.obrigatoria)?'Obrigat�ria':'Optativa'}</i></td>
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
										<td width="8%"><i>${(cc.obrigatoria)?'Obrigat�ria':'Optativa'}</i></td>
									</tr>
								</c:forEach>
								<tr style="background-color: #EFEBDE">
								<td colspan="2"><b>CH Total:</b> ${ch}hrs. &nbsp;&nbsp;&nbsp; </td>
								</tr>
								<tr style="background-color: #EFEBDE">
								<td colspan="2"><b>CH M�nima</b> ${ grupo.chMinima }hrs. </td>
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
						<caption>Componentes desse Curr�culo</caption>
						<c:set value="0" var="ch" />
						<c:forEach items="${curriculo.obj.curriculoComponentes}" var="cc">
							<c:set value="${ch + cc.componente.chTotal }" var="ch" />
							<tr>
								<td>${cc.componente.descricao}</td>
								<td width="8%"><i>${(cc.obrigatoria)?'Obrigat�ria':'Optativa'}</i></td>
							</tr>
						</c:forEach>
						<tr style="background-color: #EFEBDE">
							<td colspan="3"><b>CH Total:</b> ${ch}hrs. &nbsp;&nbsp;&nbsp; <b>Total de Cr�ditos:</b> <fmt:formatNumber pattern="##" value="${ch/15 }" /></td>
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
