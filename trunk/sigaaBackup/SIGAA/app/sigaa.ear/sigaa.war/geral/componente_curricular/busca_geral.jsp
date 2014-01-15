<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>

<c:if test="${acesso.tutorEad}">
	<%@include file="/portais/tutor/menu_tutor.jsp" %>
</c:if>
<c:if test="${acesso.coordenadorPolo}">
	<%@include file="/portais/cpolo/menu_cpolo.jsp" %>
</c:if>
<c:if test="${acesso.coordenadorCursoStricto and componenteCurricular.portalCoordenadorStricto}">
	<%@include file="/stricto/menu_coordenador.jsp" %>
</c:if>
<c:if test="${acesso.discente and componenteCurricular.portalDiscente}">
	<%@include file="/portais/discente/menu_discente.jsp" %>
</c:if>
<c:if test="${acesso.docente and componenteCurricular.portalDocente}">
	<%@include file="/portais/docente/menu_docente.jsp" %>
</c:if>
	<h2><ufrn:subSistema/> > Consulta Geral de Componentes Curriculares</h2>
	<br>

	<h:outputText value="#{componenteCurricular.create}" />
	<h:form id="formBusca">
		<table class="formulario" width="90%">
			<caption>Informe os Critérios de Consulta</caption>
			<tbody>
				<tr>
					<td width="3%"> <h:selectBooleanCheckbox value="#{componenteCurricular.filtroNivel}" id="checkNivel" styleClass="noborder"/> </td>
					<td width="20%"> <label for="checkNivel" onclick="$('formBusca:checkNivel').checked = !$('formBusca:checkNivel').checked;">Nível:</label></td>
					<td>
						<h:selectOneMenu value="#{componenteCurricular.componenteBusca.nivel}" onchange="$('formBusca:checkNivel').checked = true;">
							<f:selectItems value="#{buscaTurmaBean.niveisCombo}"/>
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<td><h:selectBooleanCheckbox value="#{componenteCurricular.filtroCodigo}" id="checkCodigo" styleClass="noborder"/> </td>
					<td><label for="checkCodigo" onclick="$('formBusca:checkCodigo').checked = !$('formBusca:checkCodigo').checked;">Código:</label></td>
					<td><h:inputText size="10" value="#{componenteCurricular.componenteBusca.codigo }"
						onfocus="$('formBusca:checkCodigo').checked = true;" onkeyup="CAPS(this)"  /></td>
				</tr>
				<tr>
					<td><h:selectBooleanCheckbox value="#{componenteCurricular.filtroNome}" id="checkNome" styleClass="noborder"/> </td>
					<td><label for="checkNome" onclick="$('formBusca:checkNome').checked = !$('formBusca:checkNome').checked;">Nome da Disciplina:</label></td>
					<td><h:inputText value="#{componenteCurricular.componenteBusca.nome }" style="width: 95%"
						onfocus="$('formBusca:checkNome').checked = true;" /></td>
				</tr>
				<tr>
					<td><h:selectBooleanCheckbox value="#{componenteCurricular.filtroPreRequisito}" id="checkPreRequisito" styleClass="noborder"/> </td>
					<td><label for="checkPreRequisito" onclick="$('formBusca:checkPreRequisito').checked = !$('formBusca:checkPreRequisito').checked;">Pré-Requisito:</label></td>
					<td>
						<f:subview id="form">
						
						<h:inputHidden id="idPreRequisito" value="#{componenteCurricular.componenteBusca.detalhes.preRequisito}"></h:inputHidden>
						<h:inputText id="nomeDisciplinaPreRequisito" value="" size="80" onfocus="$('formBusca:checkPreRequisito').checked = true;" onkeyup="CAPS(this)"/>
							<ajax:autocomplete
							source="formBusca:form:nomeDisciplinaPreRequisito" target="formBusca:form:idPreRequisito"
							baseUrl="/sigaa/ajaxDisciplina" className="autocomplete"
							indicator="indicatorDisciplina1" minimumCharacters="4"
							parameters="nivel= ,todosOsProgramas=true"
							parser="new ResponseXmlToHtmlListParser()" />
							<span id="indicatorDisciplina1" style="display:none; ">
							<img src="/sigaa/img/indicator.gif" /> </span>
							
						</f:subview>	
					</td>
				</tr>
				<tr>
					<td><h:selectBooleanCheckbox value="#{componenteCurricular.filtroCoRequisito}" id="checkCoRequisito" styleClass="noborder"/> </td>
					<td><label for="checkCoRequisito" onclick="$('formBusca:checkCoRequisito').checked = !$('formBusca:checkCoRequisito').checked;">Co-Requisito:</label></td>
					<td>
						<f:subview id="form2">					
						<h:inputHidden id="idCoRequisito" value="#{componenteCurricular.componenteBusca.detalhes.coRequisito}"></h:inputHidden>
						<h:inputText id="nomeDisciplinaCoRequisito" value="" size="80" onfocus="$('formBusca:checkCoRequisito').checked = true;" onkeyup="CAPS(this)"/>
							<ajax:autocomplete
							source="formBusca:form2:nomeDisciplinaCoRequisito" target="formBusca:form2:idCoRequisito"
							baseUrl="/sigaa/ajaxDisciplina" className="autocomplete"
							indicator="indicatorDisciplina2" minimumCharacters="4"
							parameters="nivel= ,todosOsProgramas=true"
							parser="new ResponseXmlToHtmlListParser()" />
							<span id="indicatorDisciplina2" style="display:none; ">
							<img src="/sigaa/img/indicator.gif" /> </span>
						</f:subview>	
					</td>
				</tr>
				<tr>
					<td><h:selectBooleanCheckbox value="#{componenteCurricular.filtroEquivalencia}" id="checkEquivalencia" styleClass="noborder" onclick="escolherPeriodoEquivalencia(this)"/> </td>
					<td><label for="checkEquivalencia" onclick="$('formBusca:checkEquivalencia').checked = !$('formBusca:checkEquivalencia').checked;">Equivalência:</label></td>
					<td>
						<f:subview id="form3">	
						<h:inputHidden id="idEquivalencia" value="#{componenteCurricular.componenteBusca.detalhes.equivalencia}"></h:inputHidden>
						<h:inputText id="nomeDisciplinaEquivalencia" value="" size="80" onfocus="$('formBusca:checkEquivalencia').checked = true; $('linhaPeriodoEquivalencia').show() " onkeyup="CAPS(this)"/>
							<ajax:autocomplete
							source="formBusca:form3:nomeDisciplinaEquivalencia" target="formBusca:form3:idEquivalencia"
							baseUrl="/sigaa/ajaxDisciplina" className="autocomplete"
							indicator="indicatorDisciplina3" minimumCharacters="4"
							parameters="nivel= ,todosOsProgramas=true"
							parser="new ResponseXmlToHtmlListParser()" />
							<span id="indicatorDisciplina3" style="display:none; ">
							<img src="/sigaa/img/indicator.gif" /> </span>
						</f:subview>
					</td>
				</tr>
				
				<tr id="linhaPeriodoEquivalencia">
					<td><h:selectBooleanCheckbox value="#{componenteCurricular.filtroPeriodoEquivalencia}" id="checkPeriodoEquivalencia" styleClass="noborder"/> </td>
					<td><label for="checkPeriodoEquivalencia" onclick="$('formBusca:checkPeriodoEquivalencia').checked = !$('formBusca:checkPeriodoEquivalencia').checked;">Período de Equivalência:</label></td>
					<td>
						<label>Data Inicial:</label>
				
						<t:inputCalendar id="Data_Inicial"
							value="#{ componenteCurricular.dataInicioEquivalencia }"
							renderAsPopup="true"
							readonly="#{componenteCurricular.readOnly}"
							disabled="#{componenteCurricular.readOnly}"
							renderPopupButtonAsImage="true"
							popupDateFormat="dd/MM/yyyy" popupTodayString="Hoje é"
							onchange="$('formBusca:checkPeriodoEquivalencia').checked = true;"
							onkeypress="$('formBusca:checkPeriodoEquivalencia').checked = true; return(formataData(this,event))" 
							size="10"
							maxlength="10">
							<f:converter converterId="convertData" />
						</t:inputCalendar>
					
						<label style="margin-left:20px;">Data Final:</label> 
						
						<t:inputCalendar id="dataFim"
							value="#{ componenteCurricular.dataFimEquivalencia }"
							renderAsPopup="true"
							readonly="#{componenteCurricular.readOnly}"
							disabled="#{componenteCurricular.readOnly}"
							renderPopupButtonAsImage="true"
							popupDateFormat="dd/MM/yyyy" popupTodayString="Hoje é"
							onchange="$('formBusca:checkPeriodoEquivalencia').checked = true;"
							onkeypress="$('formBusca:checkPeriodoEquivalencia').checked = true;return(formataData(this,event));" 
							size="10"
							maxlength="10">
							<f:converter converterId="convertData" />
						</t:inputCalendar>
						<ufrn:help img="/img/ajuda.gif">Utilize o filtro por período de equivalência, para os casos de equivalências antigas ao registro atual do componente.</ufrn:help>
					</td>
				</tr>
				
				<tr>
					<td><h:selectBooleanCheckbox value="#{componenteCurricular.filtroUnidade}" id="checkUnidade" styleClass="noborder"/> </td>
					<td><label for="checkUnidade" onclick="$('formBusca:checkUnidade').checked = !$('formBusca:checkUnidade').checked;">Unidade Responsável:</label></td>
					<td><h:selectOneMenu id="unidades" style="width: 95%"
						value="#{componenteCurricular.componenteBusca.unidade.id}"
						onchange="$('formBusca:checkUnidade').checked = true;">
						<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
						<f:selectItems value="#{unidade.allDetentorasComponentesCombo}" />
					</h:selectOneMenu></td>
				</tr>
				<tr>
					<td><h:selectBooleanCheckbox value="#{componenteCurricular.filtroTipo}" id="checkTipo" styleClass="noborder"/> </td>
					<td><label for="checkTipo" onclick="$('formBusca:checkTipo').checked = !$('formBusca:checkTipo').checked;">Tipo do componente:</label></td>
					<td><h:selectOneMenu id="tipos" style="width: 50%"
						value="#{componenteCurricular.componenteBusca.tipoComponente.id}"
						onchange="$('formBusca:checkTipo').checked = true;">
						<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
						<f:selectItems value="#{componenteCurricular.tiposComponentes}" />
					</h:selectOneMenu></td>
				</tr>
				<tr>
					<td><h:selectBooleanCheckbox value="#{componenteCurricular.filtroModalidade}" id="checkModalidade" styleClass="noborder"/> </td>
					<td><label for="checkModalidade" onclick="$('formBusca:checkModalidade').checked = !$('formBusca:checkModalidade').checked;">Modalidade:</label></td>
					<td><h:selectOneMenu id="modalidades" style="width: 50%"
						value="#{componenteCurricular.componenteBusca.modalidadeEducacao.id}"
						onchange="$('formBusca:checkModalidade').checked = true;">
						<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
						<f:selectItems value="#{componenteCurricular.allModalidades}" />
					</h:selectOneMenu></td>
				</tr>
				<tr>
					<td><h:selectBooleanCheckbox value="#{componenteCurricular.filtroRelatorio}" id="checkRel" styleClass="noborder"/> </td>
					<td colspan="2"><label for="checkRel" onclick="$('formBusca:checkRel').checked = !$('formBusca:checkRel').checked;">Exibir resultado da consulta em formato de relatório</label></td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3"><h:commandButton action="#{componenteCurricular.buscarComponente}" value="Buscar" id="btnBuscar"/> <h:commandButton
						value="Cancelar" onclick="#{confirm}" action="#{componenteCurricular.cancelar}" id="btnCancelar"/></td>
				</tr>
			</tfoot>
		</table>
	</h:form>

	<br>

	<c:if test="${not empty  componenteCurricular.componentes}">

	<div class="infoAltRem">
	<h:graphicImage value="/img/view.gif" style="overflow: visible;" />:
	Visualizar Componente Curricular
	<h:graphicImage value="/img/imprimir.gif" style="overflow: visible;" />:
	Relatório Para Impressão
	<h:graphicImage value="/img/report.png" style="overflow: visible;" />:
	Programa Atual do Componente<br />
	</div>

	<table class="listagem">
		<caption class="listagem">Componentes Curriculares Encontrados (${ fn:length(componenteCurricular.componentes)})</caption>

		<thead>
			<tr>
				<td width="5%" style="text-align: left;">Código</td>
				<td>Nome</td>
				<td>Nível de Ensino</td>
				<td width="10%" style="text-align: right;">CR Total</td>
				<td width="10%" style="text-align: right;">CH Total</td>
				<td width="15%">Tipo</td>
				<c:if test="${ !componenteCurricular.modoOperador && acesso.coordenadorCursoGrad }">
					<td width="7%" style="text-align: center">Status</td>
				</c:if>
				<td width="2%"></td>
				<td width="2%"></td>
				<td width="2%"></td>
			</tr>
		</thead>
		<tbody>
		<h:form>
			<c:forEach items="#{componenteCurricular.componentes}" var="componente" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size: xx-small">
					<td style="text-align: left;">${componente.codigo}</td>
					<td>${componente.detalhes.nome}</td>
					<td>${componente.descricaoNivelEnsino}</td>
					<td style="text-align: right;">${componente.detalhes.crTotal}</td>
					<td style="text-align: right;">${componente.detalhes.chTotal}</td>
					<td>${componente.tipoComponente.descricao}</td>
					
					<c:if test="${!componenteCurricular.modoOperador && acesso.coordenadorCursoGrad}">
						<td style="${ componente.ativo ? '' : 'color:red;'}" align="center"><ufrn:format type="simnao" valor="${componente.ativo}"/></td>
					</c:if>
					
					<td>
						<h:commandLink
							 action="#{componenteCurricular.detalharComponente}"
							style="border: 0;" title="Visualizar Componente Curricular">
							<h:graphicImage url="/img/view.gif"/>
							<f:param name="id" value="#{componente.id}"/>
						</h:commandLink>
					</td>
					<td>
						<h:commandLink action="#{componenteCurricular.imprimirComponente}" style="border: 0;" title="Relatório Para Impressão">
							<h:graphicImage url="/img/imprimir.gif" alt="Relatório Para Impressão"/>
							<f:param name="id" value="#{componente.id}"/>
						</h:commandLink>
					</td>
					<td>
						<h:commandLink
							action="#{programaComponente.gerarRelatorioPrograma}" rendered="#{componente.graduacao}"
							style="border: 0;" title="Programa Atual do Componente">
							<h:graphicImage url="/img/report.png"/>
							<f:param name="idComponente" value="#{componente.id}"/>
						</h:commandLink>
					</td>
				</tr>
			</c:forEach>
			</h:form>
		</tbody>
	</table>
	</c:if>


<script type="text/javascript">

	function escolherPeriodoEquivalencia(e) {
		
		if (e.checked) {
			$('linhaPeriodoEquivalencia').show();
		} else {
			$('linhaPeriodoEquivalencia').hide();
		}
		$('formBusca:checkEquivalencia').focus();
	}
	
	escolherPeriodoEquivalencia($('formBusca:checkEquivalencia'));
	
</script>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>