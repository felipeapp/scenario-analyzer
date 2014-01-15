<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<!--  Scripts do YAHOO -->
<link rel="stylesheet" type="text/css"
	href="/shared/javascript/yui/tabview/assets/tabs.css">
<link rel="stylesheet" type="text/css"
	href="/shared/javascript/yui/tabview/assets/border_tabs.css">
<script type="text/javascript"
	src="/shared/javascript/yui/tabview-min.js"></script>


<script type="text/javascript">
var criarAbas = function() {
    var tabView = new YAHOO.widget.TabView('tabs-eventos');
};
criarAbas();
//Atualiza a data de análise de matrícula para ficar igual a da matrícula
function atualizaDataMatriculaAnalise(){
	<c:if test="${calendario.obj.stricto}">
		if (document.getElementById('calForm:inicioCoordenacaoAnaliseMatricula').value == '')
			document.getElementById('calForm:inicioCoordenacaoAnaliseMatricula').value = document.getElementById('calForm:inicioMatriculaOnline').value;
		if (document.getElementById('calForm:fimCoordenacaoAnaliseMatricula').value == '')
			document.getElementById('calForm:fimCoordenacaoAnaliseMatricula').value = document.getElementById('calForm:fimMatriculaOnline').value;
	</c:if>	
}
// Atualiza a data de análise de re-matrícula para ficar igual a da re-matrícula 
function atualizaDataReMatriculaAnalise(){
	<c:if test="${calendario.obj.stricto}">
		if (document.getElementById('calForm:inicioCoordenacaoAnaliseReMatricula').value == '')
			document.getElementById('calForm:inicioCoordenacaoAnaliseReMatricula').value = document.getElementById('calForm:inicioReMatricula').value;
		if (document.getElementById('calForm:fimCoordenacaoAnaliseReMatricula').value == '')
			document.getElementById('calForm:fimCoordenacaoAnaliseReMatricula').value = document.getElementById('calForm:fimReMatricula').value;
	</c:if>
}
</script>
<style>
	.yui-navset{
		margin: 0 auto !important;
		position: inherit !important;
	}
</style>
<f:view>
	<%@include file="/stricto/menu_coordenador.jsp"%>
	<h2 class="title"><ufrn:subSistema /> > Calendário Acadêmico -
	Calendários Disponíveis</h2>

	<table class="visualizacao">
		<c:if test="${calendario.obj.unidade.id > 0}">
			<tr>
				<th width="20%">Unidade Responsável:</th>
				<td>${calendario.obj.unidade.nome }</td>
			</tr>
		</c:if>
		<c:if test="${calendario.obj.nivelDescr != 'DESCONHECIDO'}">
			<tr>
				<th>Nível de Ensino:</th>
				<td>${calendario.obj.nivelDescr}</td>
			</tr>
		</c:if>
		<c:if test="${calendario.obj.modalidade.id > 0}">
			<tr>
				<th>Modalidade de Ensino:</th>
				<td>${calendario.obj.modalidade.descricao}</td>
			</tr>
		</c:if>
		<c:if test="${calendario.obj.convenio.id > 0}">
			<tr>
				<th>Convênio Acadêmico:</th>
				<td>${calendario.obj.convenio.descricao }</td>
			</tr>
		</c:if>
		<c:if test="${calendario.obj.curso.id > 0}">
			<tr>
				<th>Curso:</th>
				<td>
				<c:if test="${calendario.obj.curso.stricto}">
					${calendario.obj.curso.nomeCursoStricto}
				</c:if>
				<c:if test="${not calendario.obj.curso.stricto}">
					${calendario.obj.curso.descricao}
				</c:if>
				</td>
			</tr>
		</c:if>
	</table>
	<br>
	<table width="100%" class="formulario"
		style="font-size: x-small; ">
		<caption>CALENDÁRIOS ACADÊMICOS</caption>
		<tr>
			<td valign="top">
			<div id="tabs-eventos" class="yui-navset">
			<ul class="yui-nav">
				<li class="${(calendario.abaEventosAcademicos)?'selected':'' }"><a
					href="#academicos"><em>Eventos Acadêmicos</em></a></li>
				<c:if test="${calendario.obj.stricto}">
					<li class="${(!calendario.abaEventosAcademicos)?'selected':'' }"><a
						href="#outros"><em>Outros Eventos</em></a></li>
				</c:if>
			</ul>
			<div class="yui-content">
			<div id="academicos">
			<table class="subFormulario" width="100%" align="center"
				cellpadding="5">
				<caption class="listagem"><h:form id="formAnoPeriodo">
						Datas para ${calendario.obj.unidade.sigla} <h:selectOneMenu
						value="#{calendario.obj.id}" id="calendarios" onchange="submit()"
						valueChangeListener="#{calendario.carregarCalendario}">
						<f:selectItem itemLabel="-- NOVO --" itemValue="0" />
						<f:selectItems value="#{calendario.calendariosCombo}" />
					</h:selectOneMenu>

				</h:form></caption>
				<h:form id="calForm">
					<h:inputHidden value="#{calendario.obj.id}" />
					<tr>
						<th class="obrigatorio">Ano - Período:</th>
						<td width="50%"><c:if test="${calendario.obj.id == 0}">
							<h:inputText value="#{calendario.obj.ano}" id="ano" size="4" title="ano"
								maxlength="4" onkeyup="formatarInteiro(this);" converter="#{ intConveter }"/> - <h:inputText
								value="#{calendario.obj.periodo}" id="periodo" size="1"  title="periodo"
								maxlength="1" onkeyup="formatarInteiro(this);" converter="#{ intConveter }" />
						</c:if> <c:if test="${calendario.obj.id > 0}">
							<h:outputText value="#{calendario.obj.anoPeriodoVigente}"></h:outputText>
						</c:if></td>
					</tr>
					<c:if test="${not calendario.obj.vigente}">
						<tr>
							<th>Tornar esse calendário vigente:</th>
							<td><h:selectBooleanCheckbox
								value="#{calendario.obj.vigente}" id="vigencia" /></td>
						</tr>
					</c:if>

					<c:if test="${calendario.obj.graduacao}">
						<tr>
							<th>Ano - Período de Férias Vigente:</th>
							<td width="50%"><h:inputText
								value="#{calendario.obj.anoFeriasVigente}" id="anoFeriasVigente"
								size="4" maxlength="4" onkeyup="formatarInteiro(this);" converter="#{ intConveter }" /> - <h:inputText
								value="#{calendario.obj.periodoFeriasVigente}"
								id="periodoFeriasVigente" size="1" maxlength="1"
								onkeyup="formatarInteiro(this);"  converter="#{ intConveter }"/></td>
						</tr>
						<tr>
							<th>Ano - Período das Turmas na Solicitação:</th>
							<td width="50%"><h:inputText
								value="#{calendario.obj.anoNovasTurmas}" id="anoNovasTurmas"
								size="4" maxlength="4" onkeyup="formatarInteiro(this);" converter="#{ intConveter }" /> - <h:inputText
								value="#{calendario.obj.periodoNovasTurmas}"
								id="periodoNovasTurmas" size="1" maxlength="1"
								onkeyup="formatarInteiro(this);"  converter="#{ intConveter }" /></td>
						</tr>
					</c:if>

					<tr>
						<th width="50%" class="${calendario.obj.stricto ? 'obrigatorio' : ''}">Período Letivo:</th>
						<td>de <t:inputCalendar popupDateFormat="dd/MM/yyyy"
							renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
							maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
							value="#{calendario.obj.inicioPeriodoLetivo}"
							id="inicioPeriodoLetivo"
							title="Início do Período Letivo">
							<f:converter converterId="convertData" />
						</t:inputCalendar> até <t:inputCalendar popupDateFormat="dd/MM/yyyy"
							renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
							maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
							value="#{calendario.obj.fimPeriodoLetivo}" id="fimPeriodoLetivo"
							title="Fim do Período Letivo">
							<f:converter converterId="convertData" />
						</t:inputCalendar></td>
					</tr>

					<c:if test="${calendario.obj.graduacao}">
						<tr>
							<th>Período de Férias:</th>
							<td>de <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.inicioFerias}" id="inicioFerias"
								title="Início do Período Férias">
								<f:converter converterId="convertData" />
							</t:inputCalendar> até <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.fimFerias}" id="fimFerias"
								title="Fim do Período Férias">
								<f:converter converterId="convertData" />
							</t:inputCalendar></td>
						</tr>
					</c:if>
					<tr>
						<td class="subFormulario" colspan="2">Datas para Turmas de
						Regulares</td>
					</tr>
					<tr>
						<th>Consolidação de Turmas:</th>
						<td>de <t:inputCalendar popupDateFormat="dd/MM/yyyy"
							renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
							maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
							value="#{calendario.obj.inicioConsolidacaoTurma}"
							id="inicioConsolidacaoTurma"
							title="Início da Consolidação de Turmas">
							<f:converter converterId="convertData" />
						</t:inputCalendar> até <t:inputCalendar popupDateFormat="dd/MM/yyyy"
							renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
							maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
							value="#{calendario.obj.fimConsolidacaoTurma}"
							id="fimConsolidacaoTurma"
							title="Fim da Consolidação de Turmas">
							<f:converter converterId="convertData" />
						</t:inputCalendar></td>
					</tr>
					
					<tr>
						<th>Consolidação Parcial de Turmas:</th>
						<td>de <t:inputCalendar popupDateFormat="dd/MM/yyyy"
							renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
							maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
							value="#{calendario.obj.inicioConsolidacaoParcialTurma}"
							id="inicioConsolidacaoParcialTurma"
							title="Início da Consolidação Parcial de Turmas">
							<f:converter converterId="convertData" />
						</t:inputCalendar> até <t:inputCalendar popupDateFormat="dd/MM/yyyy"
							renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
							maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
							value="#{calendario.obj.fimConsolidacaoParcialTurma}"
							id="fimConsolidacaoParcialTurma"
							title="Fim da Consolidação Parcial de Turmas">
							<f:converter converterId="convertData" />
						</t:inputCalendar></td>
					</tr>

					<c:if test="${calendario.obj.graduacao}">
						<tr>
							<th>Solicitação de Cadastro de Turmas do Próximo Período:</th>
							<td>de <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.inicioSolicitacaoTurma}"
								id="inicioSolicitacaoTurma"
								title="Início da Solicitação de Cadastro de Turmas do Próximo Período">
								<f:converter converterId="convertData" />
							</t:inputCalendar> até <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								value="#{calendario.obj.fimSolicitacaoTurma}"
								id="fimSolicitacaoTurma" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								title="Fim da Solicitação de Cadastro de Turmas do Próximo Período">
								<f:converter converterId="convertData" />
							</t:inputCalendar></td>
						</tr>
						<tr>
							<th>Sugestão de Turmas pelo Chefe do Departamento para o Próximo Período:</th>
							<td>de <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.inicioSugestaoTurmaChefe}"
								id="inicioSugestaoTurmaChefe"
								title="Início da Sugestão de Turmas pelo Chefe do Departamento para o Próximo Período">
								<f:converter converterId="convertData" />
							</t:inputCalendar> até <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								value="#{calendario.obj.fimSugestaoTurmaChefe}"
								id="fimSugestaoTurmaChefe" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								title="Fim da Sugestão de Turmas pelo Chefe do Departamento para o Próximo Período">
								<f:converter converterId="convertData" />
							</t:inputCalendar></td>
						</tr>
						<tr>
							<th>Cadastro de Turmas:</th>
							<td>de <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.inicioCadastroTurma}"
								id="inicioCadastroTurma"
								title="Início do Cadastro de Turmas">
								<f:converter converterId="convertData" />
							</t:inputCalendar> até <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								value="#{calendario.obj.fimCadastroTurma}" id="fimCadastroTurma"
								onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								title="Fim do Cadastro de Turmas">
								<f:converter converterId="convertData" />
							</t:inputCalendar></td>

						</tr>
					</c:if>
					<c:if test="${not calendario.obj.stricto}">
						<tr>
							<th>Trancamento de Turmas:</th>
							<td>de <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.inicioTrancamentoTurma}"
								id="inicioTrancamentoTurma"
								title="Início do Trancamento de Turmas">
								<f:converter converterId="convertData" />
							</t:inputCalendar> até <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.fimTrancamentoTurma}"
								id="fimTrancamentoTurma"
								title="Fim do Trancamento de Turmas">
								<f:converter converterId="convertData" />
							</t:inputCalendar></td>
						</tr>
					</c:if>
					<c:if test="${calendario.obj.graduacao}">
						<tr>
							<th>Trancamento de Programa:</th>
							<td>de <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.inicioTrancamentoPrograma}"
								id="inicioTrancamentoPrograma"
								title="Início do Trancamento de Programa">
								<f:converter converterId="convertData" />
							</t:inputCalendar> até <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.fimTrancamentoPrograma}"
								id="fimTrancamentoPrograma"
								title="Fim do Trancamento de Programa">
								<f:converter converterId="convertData" />
							</t:inputCalendar></td>
						</tr>
						
						<tr>
							<th>Trancamento de Programa a Posteriori:</th>
							<td>de <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.inicioTrancamentoProgramaPosteriori}"
								id="inicioTrancamentoProgramaPosteriori"
								title="Início do Trancamento de Programa a Posteriori">
								<f:converter converterId="convertData" />
							</t:inputCalendar> até <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.fimTrancamentoProgramaPosteriori}"
								id="fimTrancamentoProgramaPosteriori"
								title="Fim do Trancamento de Programa a Posteriori">
								<f:converter converterId="convertData" />
							</t:inputCalendar></td>
						
						</tr>
					
					</c:if>
					
					
					<c:if test="${calendario.obj.graduacao}">
						<tr>
							<td class="subFormulario" colspan="2">Datas para Turmas de
							Férias</td>
						</tr>
						<tr>
							<th>Requerimento de Turmas de Férias:</th>
							<td>de <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.inicioRequerimentoTurmaFerias}"
								id="inicioRequerimentoTurmaFerias"
								title="Início do Requerimento de Turmas de Férias">
								<f:converter converterId="convertData" />
							</t:inputCalendar> até <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.fimRequerimentoTurmaFerias}"
								id="fimRequerimentoTurmaFerias"
								title="Fim do Requerimento de Turmas de Férias">
								<f:converter converterId="convertData" />
							</t:inputCalendar></td>
						</tr>
						<tr>
							<th>Solicitação de Cadastro de Turmas de Férias:</th>
							<td>de <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.inicioSolicitacaoTurmaFerias}"
								id="inicioSolicitacaoTurmaFerias"
								title="Início da Solicitação de Cadastro de Turmas de Férias">
								<f:converter converterId="convertData" />
							</t:inputCalendar> até <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.fimSolicitacaoTurmaFerias}"
								id="fimSolicitacaoTurmaFerias"
								title="Fim da Solicitação de Cadastro de Turmas de Férias">
								<f:converter converterId="convertData" />
							</t:inputCalendar></td>
						</tr>
						<tr>
							<th>Cadastro de Turmas de Férias:</th>
							<td>de <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.inicioCadastroTurmaFerias}"
								id="inicioCadastroTurmaFerias"
								title="Início do Cadastro de Turmas de Férias">
								<f:converter converterId="convertData" />
							</t:inputCalendar> até <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.fimCadastroTurmaFerias}"
								id="fimCadastroTurmaFerias"
								title="Fim do Cadastro de Turmas de Férias">
								<f:converter converterId="convertData" />
							</t:inputCalendar></td>
						</tr>
						<tr>
							<th>Matrícula em Turmas de Férias:</th>
							<td>de <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.inicioMatriculaTurmaFerias}"
								id="inicioMatriculaTurmaFerias"
								title="Início da Matrícula em Turmas de Férias">
								<f:converter converterId="convertData" />
							</t:inputCalendar> até <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.fimMatriculaTurmaFerias}"
								id="fimMatriculaTurmaFerias"
								title="Fim da Matrícula em Turmas de Férias">
								<f:converter converterId="convertData" />
							</t:inputCalendar></td>
						</tr>
						<tr>
							<th>Matrícula Extraordinária de Férias:</th>
							<td>de <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.inicioMatriculaExtraordinariaFerias}"
								id="inicioMatriculaExtraordinariaFerias"
								title="Início da Matrícula Extraordinária de Férias">
								<f:converter converterId="convertData" />
							</t:inputCalendar> até <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.fimMatriculaExtraordinariaFerias}"
								id="fimMatriculaExtraordinariaFerias"
								title="Fim da Matrícula Extraordinária de Férias">
								<f:converter converterId="convertData" />
							</t:inputCalendar></td>
						</tr>
						
					</c:if>

					<c:if test="${calendario.obj.graduacao}">
						<tr>
							<td class="subFormulario" colspan="2">Datas para Turmas de
							Ensino Individualizado</td>
						</tr>
						<tr>
							<th>Requerimento de Ensino Individualizado:</th>
							<td>de <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.inicioRequerimentoEnsinoIndiv}"
								id="inicioRequerimentoEnsinoIndiv"
								title="Início do Requerimento de Ensino Individualizado">
								<f:converter converterId="convertData" />
							</t:inputCalendar> até <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.fimRequerimentoEnsinoIndiv}"
								id="fimRequerimentoEnsinoIndiv"
								title="Fim do Requerimento de Ensino Individualizado">
								<f:converter converterId="convertData" />
							</t:inputCalendar></td>
						</tr>
						<tr>
							<th>Solicitação de turma de ensino individualizado:</th>
							<td>de <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.inicioSolicitacaoTurmaEnsinoIndiv}"
								id="inicioSolicitacaoTurmaEnsinoIndiv"
								title="Início da Solicitação de turma de ensino individualizado">
								<f:converter converterId="convertData" />
							</t:inputCalendar> até <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.fimSolicitacaoTurmaEnsinoIndiv}"
								id="fimSolicitacaoTurmaEnsinoIndiv"
								title="Fim da Solicitação de turma de ensino individualizado">
								<f:converter converterId="convertData" />
							</t:inputCalendar></td>
						</tr>
						<tr>
							<th>Cadastro de turma de ensino individualizado:</th>
							<td>de <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.inicioCadastroTurmaEnsinoIndiv}"
								id="inicioCadastroTurmaEnsinoIndiv"
								title="Início do Cadastro de turma de ensino individualizado">
								<f:converter converterId="convertData" />
							</t:inputCalendar> até <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.fimCadastroTurmaEnsinoIndiv}"
								id="fimCadastroTurmaEnsinoIndiv"
								title="Fim do Cadastro de turma de ensino individualizado">
								<f:converter converterId="convertData" />
							</t:inputCalendar></td>
						</tr>
					</c:if>
					
					<c:if test="${calendario.obj.graduacao}">
						<tr>
							<td class="subFormulario" colspan="2">Datas para Cadastros de Ingressantes</td>
						</tr>
						<tr>
							<th>Plano de Matrículas:</th>
							<td>de <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.inicioCadastroPlanoMatricula}"
								id="inicioCadastroPlanoMatricula"
								title="Início do Cadastro de Plano de Turmas">
								<f:converter converterId="convertData" />
							</t:inputCalendar> até <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.fimCadastroPlanoMatricula}"
								id="fimCadastroPlanoMatricula"
								title="Fim do Cadastro de Plano de Turmas">
								<f:converter converterId="convertData" />
							</t:inputCalendar></td>
						</tr>
						<tr>
							<th>Cadastramento de Discentes:</th>
							<td>de <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.inicioCadastramentoDiscente}"
								id="inicioCadastramentoDiscente"
								title="Início do Cadastramento de Discentes">
								<f:converter converterId="convertData" />
							</t:inputCalendar> até <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.fimCadastramentoDiscente}"
								id="fimCadastramentoDiscente"
								title="Fim do Cadastramento de Discentes">
								<f:converter converterId="convertData" />
							</t:inputCalendar></td>
						</tr>

					</c:if>
					<tr>
						<td class="subFormulario" colspan="2">Datas para Períodos de
						Matrículas e Processamentos</td>
					</tr>
					<tr>
						<th>Matrícula OnLine:</th>
						<td>de <t:inputCalendar popupDateFormat="dd/MM/yyyy"
							renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
							maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))" onchange="atualizaDataMatriculaAnalise();"
							value="#{calendario.obj.inicioMatriculaOnline}"
							id="inicioMatriculaOnline"
							title="Início da Matrícula OnLine">
							<f:converter converterId="convertData" />
						</t:inputCalendar> até <t:inputCalendar popupDateFormat="dd/MM/yyyy"
							renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
							maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))" onchange="atualizaDataMatriculaAnalise();"
							value="#{calendario.obj.fimMatriculaOnline}"
							id="fimMatriculaOnline"
							title="Fim da Matrícula OnLine">
							<f:converter converterId="convertData" />
						</t:inputCalendar></td>
					</tr>
					<c:if test="${calendario.obj.graduacao}">
						<tr>
							<th>Matrícula de Alunos Ingressantes:</th>
							<td>de <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.inicioMatriculaAlunoCadastrado}"
								id="inicioMatriculaAlunoCadastrado"
								title="Início da Matrícula de Alunos Ingressantes">
								<f:converter converterId="convertData" />
							</t:inputCalendar> até <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.fimMatriculaAlunoCadastrado}"
								id="fimMatriculaAlunoCadastrado"
								title="Fim da Matrícula de Alunos Ingressantes">
								<f:converter converterId="convertData" />
							</t:inputCalendar></td>
						</tr>
						<tr>
							<th>Matrícula de Aluno Especial:</th>
							<td>de <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.inicioMatriculaAlunoEspecial}"
								id="inicioMatriculaAlunoEspecial"
								title="Início da Matrícula de Aluno Especial">
								<f:converter converterId="convertData" />
							</t:inputCalendar> até <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.fimMatriculaAlunoEspecial}"
								id="fimMatriculaAlunoEspecial"
								title="Fim da Matrícula de Aluno Especial">
								<f:converter converterId="convertData" />
							</t:inputCalendar></td>
						</tr>
					</c:if>

					<c:if test="${!calendario.obj.stricto}">
						<tr>
							<th>Análise dos Coordenadores/Orientadores da Matrícula:</th>
							<td>de <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.inicioCoordenacaoAnaliseMatricula}"
								id="inicioCoordenacaoAnaliseMatricula"
								title="Início da Análise dos Coordenadores/Orientadores da Matrícula">
								<f:converter converterId="convertData" />
							</t:inputCalendar> até <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.fimCoordenacaoAnaliseMatricula}"
								id="fimCoordenacaoAnaliseMatricula"
								title="Fim da Análise dos Coordenadores/Orientadores da Matrícula">
								<f:converter converterId="convertData" />
							</t:inputCalendar></td>
						</tr>
					</c:if>

					<c:if test="${calendario.obj.graduacao}">
						<tr>
							<th>Análise dos Discentes:</th>
							<td>de <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.inicioDiscenteAnaliseMatricula}"
								id="inicioDiscenteAnaliseMatricula"
								title="Início da Análise dos Discentes">
								<f:converter converterId="convertData" />
							</t:inputCalendar> até <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.fimDiscenteAnaliseMatricula}"
								id="fimDiscenteAnaliseMatricula"
								title="Fim da Análise dos Discentes">
								<f:converter converterId="convertData" />
							</t:inputCalendar></td>
						</tr>
						<tr>
							<th>Validação do Vínculo de Ingressantes:</th>
							<td>de <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.inicioValidacaoVinculoIngressante}"
								id="inicioValidacaoVinculoIngressante"
								title="Início da Validação do Vínculo de Ingressantes">
								<f:converter converterId="convertData" />
							</t:inputCalendar> até <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.fimValidacaoVinculoIngressante}"
								id="fimValidacaoVinculoIngressante"
								title="Fim da Validação do Vínculo de Ingressantes">
								<f:converter converterId="convertData" />
							</t:inputCalendar></td>
						</tr>
						<tr>
							<th>Ajustes das Matrículas/Turmas:</th>
							<td>de <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.inicioAjustesMatricula}"
								id="inicioAjustesMatricula"
								title="Início do Ajustes das Matrículas/Turmas">
								<f:converter converterId="convertData" />
							</t:inputCalendar> até <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.fimAjustesMatricula}"
								id="fimAjustesMatricula"
								title="Fim do Ajustes das Matrículas/Turmas">
								<f:converter converterId="convertData" />
							</t:inputCalendar></td>
						</tr>
						<tr>
							<th>Processamento de Matrícula:</th>
							<td>de <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.inicioProcessamentoMatricula}"
								id="inicioProcessamentoMatricula"
								title="Início do Processamento de Matrícula">
								<f:converter converterId="convertData" />
							</t:inputCalendar> até <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.fimProcessamentoMatricula}"
								id="fimProcessamentoMatricula"
								title="Fim do Processamento de Matrícula">
								<f:converter converterId="convertData" />
							</t:inputCalendar></td>
						</tr>
					</c:if>
					<tr>
						<td class="subFormulario" colspan="2">Datas para Períodos de
						Rematrículas e Processamentos</td>
					</tr>
					<tr>
						<th>Re-Matrícula:</th>
						<td>de <t:inputCalendar popupDateFormat="dd/MM/yyyy"
							renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
							maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))" onchange="atualizaDataReMatriculaAnalise();"
							value="#{calendario.obj.inicioReMatricula}"
							id="inicioReMatricula"
							title="Início da Re-Matrícula">
							<f:converter converterId="convertData" />
						</t:inputCalendar> até <t:inputCalendar popupDateFormat="dd/MM/yyyy"
							renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
							maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))" onchange="atualizaDataReMatriculaAnalise();"
							value="#{calendario.obj.fimReMatricula}" id="fimReMatricula"
							title="Fim da Re-Matrícula">
							<f:converter converterId="convertData" />
						</t:inputCalendar></td>
					</tr>
					<c:if test="${!calendario.obj.stricto}">
						<tr>
							<th>Análise dos Coordenadores/Orientadores para Re-Matrícula:</th>
							<td>de <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.inicioCoordenacaoAnaliseReMatricula}"
								id="inicioCoordenacaoAnaliseReMatricula"
								title="Início da Análise dos Coordenadores/Orientadores para Re-Matrícula">
								<f:converter converterId="convertData" />
							</t:inputCalendar> até <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.fimCoordenacaoAnaliseReMatricula}"
								id="fimCoordenacaoAnaliseReMatricula"
								title="Fim da Análise dos Coordenadores/Orientadores para Re-Matrícula">
								<f:converter converterId="convertData" />
							</t:inputCalendar></td>
						</tr>
					</c:if>
					<c:if test="${calendario.obj.graduacao}">
						<tr>
							<th>Análise dos Discentes para Re-Matrícula:</th>
							<td>de <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.inicioDiscenteAnaliseReMatricula}"
								id="inicioDiscenteAnaliseReMatricula"
								title="Início da Análise dos Discentes para Re-Matrícula">
								<f:converter converterId="convertData" />
							</t:inputCalendar> até <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.fimDiscenteAnaliseReMatricula}"
								id="fimDiscenteAnaliseReMatricula"
								title="Fim da Análise dos Discentes para Re-Matrícula">
								<f:converter converterId="convertData" />
							</t:inputCalendar></td>
						</tr>
						<tr>
							<th>Ajustes das Re-Matrículas/Turmas:</th>
							<td>de <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.inicioAjustesReMatricula}"
								id="inicioAjustesReMatricula"
								title="Início dos Ajustes das Re-Matrículas/Turmas">
								<f:converter converterId="convertData" />
							</t:inputCalendar> até <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.fimAjustesReMatricula}"
								id="fimAjustesReMatricula"
								title="Fim dos Ajustes das Re-Matrículas/Turmas">
								<f:converter converterId="convertData" />
							</t:inputCalendar></td>
						</tr>
						<tr>
							<th>Processamento de Re-Matrícula:</th>
							<td>de <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.inicioProcessamentoReMatricula}"
								id="inicioProcessamentoReMatricula"
								title="Início do Processamento de Re-Matrícula">
								<f:converter converterId="convertData" />
							</t:inputCalendar> até <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.fimProcessamentoReMatricula}"
								id="fimProcessamentoReMatricula"
								title="Fim do Processamento de Re-Matrícula">
								<f:converter converterId="convertData" />
							</t:inputCalendar></td>
						</tr>
						<tr>
							<th>Matrícula Extraordinária:</th>
							<td>de <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.inicioMatriculaExtraordinaria}"
								id="inicioMatriculaExtraordinaria"
								title="Início da Matrícula Extraordinária">
								<f:converter converterId="convertData" />
							</t:inputCalendar> até <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.fimMatriculaExtraordinaria}"
								id="fimMatriculaExtraordinaria"
								title="Fim da Matrícula Extraordinária">
								<f:converter converterId="convertData" />
							</t:inputCalendar></td>
						</tr>
					</c:if>
			</table>
			</div>
			<c:if test="${calendario.obj.stricto}">
				<div id="outros">
				<table class="subFormulario" width="100%" align="center">
					<caption class="listagem">Outros eventos</caption>
					<tr>
						<td class="required">Evento:</td>
						<td colspan="3"><h:inputText
							value="#{calendario.extra.descricao}" size="80" id="desc"
							maxlength="80" /></td>
					</tr>
					<tr>
						<td class="required">Período:</td>
						<td width="35%"><t:inputCalendar popupDateFormat="dd/MM/yyyy"
							renderAsPopup="true" onkeypress="formataData(this, event)"
							renderPopupButtonAsImage="true" size="11"
							value="#{calendario.extra.inicio}" id="inicioExtra"
							title="Início do Perído do Evento">
							<f:converter converterId="convertData" />
						</t:inputCalendar> até <t:inputCalendar popupDateFormat="dd/MM/yyyy"
							renderAsPopup="true" onkeypress="formataData(this, event)"
							renderPopupButtonAsImage="true" size="11"
							value="#{calendario.extra.fim}" id="fimExtra"
							title="Fim do Perído do Evento">
							<f:converter converterId="convertData" />
						</t:inputCalendar></td>
						<td width="22%">Suspensão das atividades?</td>
						<td><h:selectOneRadio
							value="#{calendario.extra.suspensaoAtividades}" id="susp">
							<f:selectItem itemValue="true" itemLabel="Sim" id="sim" />
							<f:selectItem itemValue="false" itemLabel="Não" id="nao" />
						</h:selectOneRadio></td>
					</tr>
					<tr>
						<td>Interesses:</td>
						<td colspan="2">
						<table>
							<tr>
								<td><h:selectBooleanCheckbox
									value="#{calendario.extra.alunos}" id="interesseAluno" /></td>
								<td><label for="calForm:interesseAluno">Alunos</label></td>
								<td><h:selectBooleanCheckbox
									value="#{calendario.extra.coordenacoes}"
									id="interesseCoordenacoes" /></td>
								<td><label for="calForm:interesseCoordenacoes">Coordenações</label></td>
								<td><h:selectBooleanCheckbox
									value="#{calendario.extra.departamentos}" id="interesseDeptos" /></td>
								<td><label for="calForm:interesseDeptos">Departamentos</label></td>
								<td><h:selectBooleanCheckbox
									value="#{calendario.extra.docentes}" id="interesseDocentes" /></td>
								<td><label for="calForm:interesseDocentes">Docentes</label></td>
							</tr>
						</table>
						</td>
						<td><h:commandButton value="Incluir Evento Extra"
							id="inclusaoDeEvento" style="width: 240px; cursor:pointer"
							actionListener="#{calendario.addExtra}" /></td>
					</tr>
					<tr>
						<td colspan="4">
							<div class="infoAltRem">
						       	<h:graphicImage url="/img/delete.gif"/>: Remover Evento Extra <br />
							</div>
							
							<table width="100%" style="border-collapse: collapse;  position: relative"
								class="listagem">
								<thead>
									<tr>
									<td colspan="2">Eventos incluídos</td>
	
									<td width="2%"></td>
									</tr>
								</thead>

								<c:if test="${empty calendario.eventosExtraOrdenados }">
									<tr>
										<td colspan="3"><i>Nenhum evento extra cadastrado</i></td>
									</tr>
								</c:if>
								<c:if test="${not empty calendario.eventosExtraOrdenados }">
									<c:forEach items="#{calendario.eventosExtraOrdenados}" var="ev">
											<tr>
												<td width="22%">
												<ufrn:format type="data" valor="${ev.inicio}" />
												<c:if test="${ev.inicio != ev.fim}">
												- 
												<ufrn:format type="data" valor="${ev.fim}" />
												</c:if>
												</td>
												<td>${ev.descricao}</td>
												<td width="5%" align="right">
													<h:commandLink actionListener="#{calendario.remExtra}" id="remEv" 
													 styleClass="noborder" title="Remover Este Evento Extra" onclick="#{confirmDelete}">
														<h:graphicImage url="/img/delete.gif"/>
															<f:param name="id" value="#{ev.id}"/>
															<f:param name="desc" value="#{ev.descricao}"/>
													</h:commandLink>
												</td>
											</tr>
									</c:forEach>
								</c:if>
							</table>
							
						</td>
					</tr>
				</table>
				</div>
			</c:if></div>
			</td>
		</tr>
		<tfoot>
			<tr>
				<td>
					<h:commandButton value="#{calendario.confirmButton}" id="confirmar"	action="#{calendario.confirmar}" /> 
					<h:commandButton action="#{calendario.voltarParametros}" id="voltar" value="<< Voltar" rendered="#{calendario.habilitarVoltar }" />
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{calendario.cancelar}" id="cancelar" />
				</td>
			</tr>
		</tfoot>
		</h:form>
	</table>
	<center>
		<br/>
		<html:img page="/img/required.gif" style="vertical-align: top;" /> 
		<span class="fontePequena">Campos de preenchimento obrigatório. </span> 
	</center>		
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
