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
	};
	criarAbas();
	</script>
</c:if>
	<h:messages showDetail="true"></h:messages>
	<h2 class="title">Estrutura Curricular de Matrizes Curriculares &gt; Resumo</h2>

	<h:form id="formulario">

		<table class="formulario" width="700px">
			<caption class="formulario">Dados do Curr�culo</caption>
			<tr>
				<th class="rotulo">C�digo: </th>
				<td><h:outputText value="#{curriculo.obj.codigo }" /></td>
			</tr>
			<tr>
				<th class="rotulo">Matriz Curricular: </th>
				<td><h:outputText value="#{curriculo.obj.matriz.descricao }" /></td>
			</tr>
			<tr>
				<th class="rotulo">Per�odo Letivo de Entrada em Vigor: </th>
				<td><h:outputText
					value="#{curriculo.obj.anoEntradaVigor} - #{curriculo.obj.periodoEntradaVigor }" /></td>
			</tr>
			<tr>
				<th class="rotulo">Carga Hor�ria:</th>
				<td>
				<table>
					<tr>
						<th>Total M�nima</th>
						<td><h:outputText value="#{curriculo.obj.chTotalMinima}" /></td>
						<th>Optativas M�nima</th>
						<td><h:outputText value="#{curriculo.obj.chOptativasMinima}" /></td>
					</tr>
				</table>
				</td>
			</tr>
			
			<tr>
				<th class="rotulo">Cr�ditos Obrigat�rios:</th>
				<td>
					${curriculo.obj.crPraticos + curriculo.obj.crTeoricos} Total - ( ${curriculo.obj.crPraticos}  Pr�ticos ) / ( ${curriculo.obj.crTeoricos} Te�ricos )
				</td>
			</tr>
			<tr>
				<th class="rotulo">Carga Hor�ria Obrigat�ria: </th>
				<td>
					${curriculo.obj.chPraticos + curriculo.obj.chTeoricos} Total - ( ${curriculo.obj.chPraticos} Pr�ticos ) / ( ${curriculo.obj.chTeoricos} Te�ricos )
				</td>
			</tr>
			<tr>
				<th class="rotulo">Carga Hor�ria Obrigat�ria de Atividade Acad�mica Espec�fica: </th>
				<td>
					${curriculo.obj.chAAE} hrs
				</td>
			</tr>
			<tr>
				<th class="rotulo">Carga Hor�ria de Componentes Eletivos: </th>
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
				<th class="rotulo">Carga Hor�ria por Per�odo Letivo:</th>
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
			<tr>
				<th class="rotulo">Cr�ditos por Per�odo Letivo:</th>
				<td>
				<table>
					<tr>
						<th>M�nimo</th>
						<td>(<h:outputText value="#{curriculo.obj.crMinimoSemestre}" /> cr.) </td>
						<th>M�dio</th>
						<td>(<h:outputText value="#{curriculo.obj.crIdealSemestre}" /> cr.) </td>
						<th>M�ximo</th>
						<td>(<h:outputText value="#{curriculo.obj.crMaximoSemestre}" /> cr.) </td>
					</tr>
				</table>
				</td>
			</tr>
			<tr>
				<th class="rotulo">Prazos em Per�odos Letivos:</th>
				<td>
				<table>
					<tr>
						<th>M�nimo</th>
						<td>(<h:outputText value="#{curriculo.obj.semestreConclusaoMinimo}" />)</td>
						<th>M�dio</th>
						<td>(<h:outputText value="#{curriculo.obj.semestreConclusaoIdeal}" />)</td>
						<th>M�ximo</th>
						<td>(<h:outputText value="#{curriculo.obj.semestreConclusaoMaximo}" />)</td>
					</tr>
				</table>
				</td>
			</tr>
				<tr>
					<td colspan="2" align="center">
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
			<tfoot>
				<tr>
					<td colspan="2">
						<input type="hidden" value="${curriculo.obj.id}" name="id">
						<c:choose>
							<c:when test="${curriculo.confirmButton == 'Ativar'}">
								<h:commandButton value="Ativar" action="#{curriculo.cadastrarCurriculo}" id="btnConfirmar"/>
							</c:when>
							<c:when test="${curriculo.confirmButton == 'Inativar'}">
								<h:commandButton value="Inativar" action="#{curriculo.cadastrarCurriculo}" id="btnConfirmar"/>
							</c:when>
							<c:otherwise>
								<h:commandButton value="Confirmar" action="#{curriculo.cadastrarCurriculo}" id="btnConfirmar"/>
							</c:otherwise>
						</c:choose>
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{curriculo.cancelar}" id="btnCancelar"/>
					</td>
				</tr>
				<c:if test="${curriculo.confirmButton != 'Ativar' && curriculo.confirmButton != 'Inativar' }">
				<tr>
					<td colspan="2"><h:commandButton value="<< Dados Gerais" id="btnDadosGerais"
						action="#{curriculo.voltarDadosGerais}" /> 
						<h:commandButton value="<< Componentes" id="btnComponentes"
						action="#{curriculo.voltarComponentes}" />
						<h:commandButton value="<< TCC Definitivo" id="btnTcc"
						action="#{curriculo.telaTccDefinitivo}" />
						<h:commandButton value="<< CH Optativa" id="btnChOpt"
						action="#{curriculo.voltarChOptativa}" />
						</td>
				</tr>
				</c:if>
			</tfoot>
		</table>
		
		<c:set var="exibirApenasSenha" value="true" scope="request"/>
		<%@include file="/WEB-INF/jsp/include/confirma_senha.jsp"%>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
