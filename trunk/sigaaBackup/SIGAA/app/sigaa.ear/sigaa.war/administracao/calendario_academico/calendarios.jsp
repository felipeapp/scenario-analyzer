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
//Atualiza a data de an�lise de matr�cula para ficar igual a da matr�cula
function atualizaDataMatriculaAnalise(){
	<c:if test="${calendario.obj.stricto}">
		if (document.getElementById('calForm:inicioCoordenacaoAnaliseMatricula').value == '')
			document.getElementById('calForm:inicioCoordenacaoAnaliseMatricula').value = document.getElementById('calForm:inicioMatriculaOnline').value;
		if (document.getElementById('calForm:fimCoordenacaoAnaliseMatricula').value == '')
			document.getElementById('calForm:fimCoordenacaoAnaliseMatricula').value = document.getElementById('calForm:fimMatriculaOnline').value;
	</c:if>	
}
// Atualiza a data de an�lise de re-matr�cula para ficar igual a da re-matr�cula 
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
	<h2 class="title"><ufrn:subSistema /> > Calend�rio Acad�mico -
	Calend�rios Dispon�veis</h2>

	<table class="visualizacao">
		<c:if test="${calendario.obj.unidade.id > 0}">
			<tr>
				<th width="20%">Unidade Respons�vel:</th>
				<td>${calendario.obj.unidade.nome }</td>
			</tr>
		</c:if>
		<c:if test="${calendario.obj.nivelDescr != 'DESCONHECIDO'}">
			<tr>
				<th>N�vel de Ensino:</th>
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
				<th>Conv�nio Acad�mico:</th>
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
		<caption>CALEND�RIOS ACAD�MICOS</caption>
		<tr>
			<td valign="top">
			<div id="tabs-eventos" class="yui-navset">
			<ul class="yui-nav">
				<li class="${(calendario.abaEventosAcademicos)?'selected':'' }"><a
					href="#academicos"><em>Eventos Acad�micos</em></a></li>
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
						<th class="obrigatorio">Ano - Per�odo:</th>
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
							<th>Tornar esse calend�rio vigente:</th>
							<td><h:selectBooleanCheckbox
								value="#{calendario.obj.vigente}" id="vigencia" /></td>
						</tr>
					</c:if>

					<c:if test="${calendario.obj.graduacao}">
						<tr>
							<th>Ano - Per�odo de F�rias Vigente:</th>
							<td width="50%"><h:inputText
								value="#{calendario.obj.anoFeriasVigente}" id="anoFeriasVigente"
								size="4" maxlength="4" onkeyup="formatarInteiro(this);" converter="#{ intConveter }" /> - <h:inputText
								value="#{calendario.obj.periodoFeriasVigente}"
								id="periodoFeriasVigente" size="1" maxlength="1"
								onkeyup="formatarInteiro(this);"  converter="#{ intConveter }"/></td>
						</tr>
						<tr>
							<th>Ano - Per�odo das Turmas na Solicita��o:</th>
							<td width="50%"><h:inputText
								value="#{calendario.obj.anoNovasTurmas}" id="anoNovasTurmas"
								size="4" maxlength="4" onkeyup="formatarInteiro(this);" converter="#{ intConveter }" /> - <h:inputText
								value="#{calendario.obj.periodoNovasTurmas}"
								id="periodoNovasTurmas" size="1" maxlength="1"
								onkeyup="formatarInteiro(this);"  converter="#{ intConveter }" /></td>
						</tr>
					</c:if>

					<tr>
						<th width="50%" class="${calendario.obj.stricto ? 'obrigatorio' : ''}">Per�odo Letivo:</th>
						<td>de <t:inputCalendar popupDateFormat="dd/MM/yyyy"
							renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
							maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
							value="#{calendario.obj.inicioPeriodoLetivo}"
							id="inicioPeriodoLetivo"
							title="In�cio do Per�odo Letivo">
							<f:converter converterId="convertData" />
						</t:inputCalendar> at� <t:inputCalendar popupDateFormat="dd/MM/yyyy"
							renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
							maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
							value="#{calendario.obj.fimPeriodoLetivo}" id="fimPeriodoLetivo"
							title="Fim do Per�odo Letivo">
							<f:converter converterId="convertData" />
						</t:inputCalendar></td>
					</tr>

					<c:if test="${calendario.obj.graduacao}">
						<tr>
							<th>Per�odo de F�rias:</th>
							<td>de <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.inicioFerias}" id="inicioFerias"
								title="In�cio do Per�odo F�rias">
								<f:converter converterId="convertData" />
							</t:inputCalendar> at� <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.fimFerias}" id="fimFerias"
								title="Fim do Per�odo F�rias">
								<f:converter converterId="convertData" />
							</t:inputCalendar></td>
						</tr>
					</c:if>
					<tr>
						<td class="subFormulario" colspan="2">Datas para Turmas de
						Regulares</td>
					</tr>
					<tr>
						<th>Consolida��o de Turmas:</th>
						<td>de <t:inputCalendar popupDateFormat="dd/MM/yyyy"
							renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
							maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
							value="#{calendario.obj.inicioConsolidacaoTurma}"
							id="inicioConsolidacaoTurma"
							title="In�cio da Consolida��o de Turmas">
							<f:converter converterId="convertData" />
						</t:inputCalendar> at� <t:inputCalendar popupDateFormat="dd/MM/yyyy"
							renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
							maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
							value="#{calendario.obj.fimConsolidacaoTurma}"
							id="fimConsolidacaoTurma"
							title="Fim da Consolida��o de Turmas">
							<f:converter converterId="convertData" />
						</t:inputCalendar></td>
					</tr>
					
					<tr>
						<th>Consolida��o Parcial de Turmas:</th>
						<td>de <t:inputCalendar popupDateFormat="dd/MM/yyyy"
							renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
							maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
							value="#{calendario.obj.inicioConsolidacaoParcialTurma}"
							id="inicioConsolidacaoParcialTurma"
							title="In�cio da Consolida��o Parcial de Turmas">
							<f:converter converterId="convertData" />
						</t:inputCalendar> at� <t:inputCalendar popupDateFormat="dd/MM/yyyy"
							renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
							maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
							value="#{calendario.obj.fimConsolidacaoParcialTurma}"
							id="fimConsolidacaoParcialTurma"
							title="Fim da Consolida��o Parcial de Turmas">
							<f:converter converterId="convertData" />
						</t:inputCalendar></td>
					</tr>

					<c:if test="${calendario.obj.graduacao}">
						<tr>
							<th>Solicita��o de Cadastro de Turmas do Pr�ximo Per�odo:</th>
							<td>de <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.inicioSolicitacaoTurma}"
								id="inicioSolicitacaoTurma"
								title="In�cio da Solicita��o de Cadastro de Turmas do Pr�ximo Per�odo">
								<f:converter converterId="convertData" />
							</t:inputCalendar> at� <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								value="#{calendario.obj.fimSolicitacaoTurma}"
								id="fimSolicitacaoTurma" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								title="Fim da Solicita��o de Cadastro de Turmas do Pr�ximo Per�odo">
								<f:converter converterId="convertData" />
							</t:inputCalendar></td>
						</tr>
						<tr>
							<th>Sugest�o de Turmas pelo Chefe do Departamento para o Pr�ximo Per�odo:</th>
							<td>de <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.inicioSugestaoTurmaChefe}"
								id="inicioSugestaoTurmaChefe"
								title="In�cio da Sugest�o de Turmas pelo Chefe do Departamento para o Pr�ximo Per�odo">
								<f:converter converterId="convertData" />
							</t:inputCalendar> at� <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								value="#{calendario.obj.fimSugestaoTurmaChefe}"
								id="fimSugestaoTurmaChefe" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								title="Fim da Sugest�o de Turmas pelo Chefe do Departamento para o Pr�ximo Per�odo">
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
								title="In�cio do Cadastro de Turmas">
								<f:converter converterId="convertData" />
							</t:inputCalendar> at� <t:inputCalendar popupDateFormat="dd/MM/yyyy"
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
								title="In�cio do Trancamento de Turmas">
								<f:converter converterId="convertData" />
							</t:inputCalendar> at� <t:inputCalendar popupDateFormat="dd/MM/yyyy"
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
								title="In�cio do Trancamento de Programa">
								<f:converter converterId="convertData" />
							</t:inputCalendar> at� <t:inputCalendar popupDateFormat="dd/MM/yyyy"
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
								title="In�cio do Trancamento de Programa a Posteriori">
								<f:converter converterId="convertData" />
							</t:inputCalendar> at� <t:inputCalendar popupDateFormat="dd/MM/yyyy"
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
							F�rias</td>
						</tr>
						<tr>
							<th>Requerimento de Turmas de F�rias:</th>
							<td>de <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.inicioRequerimentoTurmaFerias}"
								id="inicioRequerimentoTurmaFerias"
								title="In�cio do Requerimento de Turmas de F�rias">
								<f:converter converterId="convertData" />
							</t:inputCalendar> at� <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.fimRequerimentoTurmaFerias}"
								id="fimRequerimentoTurmaFerias"
								title="Fim do Requerimento de Turmas de F�rias">
								<f:converter converterId="convertData" />
							</t:inputCalendar></td>
						</tr>
						<tr>
							<th>Solicita��o de Cadastro de Turmas de F�rias:</th>
							<td>de <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.inicioSolicitacaoTurmaFerias}"
								id="inicioSolicitacaoTurmaFerias"
								title="In�cio da Solicita��o de Cadastro de Turmas de F�rias">
								<f:converter converterId="convertData" />
							</t:inputCalendar> at� <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.fimSolicitacaoTurmaFerias}"
								id="fimSolicitacaoTurmaFerias"
								title="Fim da Solicita��o de Cadastro de Turmas de F�rias">
								<f:converter converterId="convertData" />
							</t:inputCalendar></td>
						</tr>
						<tr>
							<th>Cadastro de Turmas de F�rias:</th>
							<td>de <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.inicioCadastroTurmaFerias}"
								id="inicioCadastroTurmaFerias"
								title="In�cio do Cadastro de Turmas de F�rias">
								<f:converter converterId="convertData" />
							</t:inputCalendar> at� <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.fimCadastroTurmaFerias}"
								id="fimCadastroTurmaFerias"
								title="Fim do Cadastro de Turmas de F�rias">
								<f:converter converterId="convertData" />
							</t:inputCalendar></td>
						</tr>
						<tr>
							<th>Matr�cula em Turmas de F�rias:</th>
							<td>de <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.inicioMatriculaTurmaFerias}"
								id="inicioMatriculaTurmaFerias"
								title="In�cio da Matr�cula em Turmas de F�rias">
								<f:converter converterId="convertData" />
							</t:inputCalendar> at� <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.fimMatriculaTurmaFerias}"
								id="fimMatriculaTurmaFerias"
								title="Fim da Matr�cula em Turmas de F�rias">
								<f:converter converterId="convertData" />
							</t:inputCalendar></td>
						</tr>
						<tr>
							<th>Matr�cula Extraordin�ria de F�rias:</th>
							<td>de <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.inicioMatriculaExtraordinariaFerias}"
								id="inicioMatriculaExtraordinariaFerias"
								title="In�cio da Matr�cula Extraordin�ria de F�rias">
								<f:converter converterId="convertData" />
							</t:inputCalendar> at� <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.fimMatriculaExtraordinariaFerias}"
								id="fimMatriculaExtraordinariaFerias"
								title="Fim da Matr�cula Extraordin�ria de F�rias">
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
								title="In�cio do Requerimento de Ensino Individualizado">
								<f:converter converterId="convertData" />
							</t:inputCalendar> at� <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.fimRequerimentoEnsinoIndiv}"
								id="fimRequerimentoEnsinoIndiv"
								title="Fim do Requerimento de Ensino Individualizado">
								<f:converter converterId="convertData" />
							</t:inputCalendar></td>
						</tr>
						<tr>
							<th>Solicita��o de turma de ensino individualizado:</th>
							<td>de <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.inicioSolicitacaoTurmaEnsinoIndiv}"
								id="inicioSolicitacaoTurmaEnsinoIndiv"
								title="In�cio da Solicita��o de turma de ensino individualizado">
								<f:converter converterId="convertData" />
							</t:inputCalendar> at� <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.fimSolicitacaoTurmaEnsinoIndiv}"
								id="fimSolicitacaoTurmaEnsinoIndiv"
								title="Fim da Solicita��o de turma de ensino individualizado">
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
								title="In�cio do Cadastro de turma de ensino individualizado">
								<f:converter converterId="convertData" />
							</t:inputCalendar> at� <t:inputCalendar popupDateFormat="dd/MM/yyyy"
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
							<th>Plano de Matr�culas:</th>
							<td>de <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.inicioCadastroPlanoMatricula}"
								id="inicioCadastroPlanoMatricula"
								title="In�cio do Cadastro de Plano de Turmas">
								<f:converter converterId="convertData" />
							</t:inputCalendar> at� <t:inputCalendar popupDateFormat="dd/MM/yyyy"
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
								title="In�cio do Cadastramento de Discentes">
								<f:converter converterId="convertData" />
							</t:inputCalendar> at� <t:inputCalendar popupDateFormat="dd/MM/yyyy"
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
						<td class="subFormulario" colspan="2">Datas para Per�odos de
						Matr�culas e Processamentos</td>
					</tr>
					<tr>
						<th>Matr�cula OnLine:</th>
						<td>de <t:inputCalendar popupDateFormat="dd/MM/yyyy"
							renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
							maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))" onchange="atualizaDataMatriculaAnalise();"
							value="#{calendario.obj.inicioMatriculaOnline}"
							id="inicioMatriculaOnline"
							title="In�cio da Matr�cula OnLine">
							<f:converter converterId="convertData" />
						</t:inputCalendar> at� <t:inputCalendar popupDateFormat="dd/MM/yyyy"
							renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
							maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))" onchange="atualizaDataMatriculaAnalise();"
							value="#{calendario.obj.fimMatriculaOnline}"
							id="fimMatriculaOnline"
							title="Fim da Matr�cula OnLine">
							<f:converter converterId="convertData" />
						</t:inputCalendar></td>
					</tr>
					<c:if test="${calendario.obj.graduacao}">
						<tr>
							<th>Matr�cula de Alunos Ingressantes:</th>
							<td>de <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.inicioMatriculaAlunoCadastrado}"
								id="inicioMatriculaAlunoCadastrado"
								title="In�cio da Matr�cula de Alunos Ingressantes">
								<f:converter converterId="convertData" />
							</t:inputCalendar> at� <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.fimMatriculaAlunoCadastrado}"
								id="fimMatriculaAlunoCadastrado"
								title="Fim da Matr�cula de Alunos Ingressantes">
								<f:converter converterId="convertData" />
							</t:inputCalendar></td>
						</tr>
						<tr>
							<th>Matr�cula de Aluno Especial:</th>
							<td>de <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.inicioMatriculaAlunoEspecial}"
								id="inicioMatriculaAlunoEspecial"
								title="In�cio da Matr�cula de Aluno Especial">
								<f:converter converterId="convertData" />
							</t:inputCalendar> at� <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.fimMatriculaAlunoEspecial}"
								id="fimMatriculaAlunoEspecial"
								title="Fim da Matr�cula de Aluno Especial">
								<f:converter converterId="convertData" />
							</t:inputCalendar></td>
						</tr>
					</c:if>

					<c:if test="${!calendario.obj.stricto}">
						<tr>
							<th>An�lise dos Coordenadores/Orientadores da Matr�cula:</th>
							<td>de <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.inicioCoordenacaoAnaliseMatricula}"
								id="inicioCoordenacaoAnaliseMatricula"
								title="In�cio da An�lise dos Coordenadores/Orientadores da Matr�cula">
								<f:converter converterId="convertData" />
							</t:inputCalendar> at� <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.fimCoordenacaoAnaliseMatricula}"
								id="fimCoordenacaoAnaliseMatricula"
								title="Fim da An�lise dos Coordenadores/Orientadores da Matr�cula">
								<f:converter converterId="convertData" />
							</t:inputCalendar></td>
						</tr>
					</c:if>

					<c:if test="${calendario.obj.graduacao}">
						<tr>
							<th>An�lise dos Discentes:</th>
							<td>de <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.inicioDiscenteAnaliseMatricula}"
								id="inicioDiscenteAnaliseMatricula"
								title="In�cio da An�lise dos Discentes">
								<f:converter converterId="convertData" />
							</t:inputCalendar> at� <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.fimDiscenteAnaliseMatricula}"
								id="fimDiscenteAnaliseMatricula"
								title="Fim da An�lise dos Discentes">
								<f:converter converterId="convertData" />
							</t:inputCalendar></td>
						</tr>
						<tr>
							<th>Valida��o do V�nculo de Ingressantes:</th>
							<td>de <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.inicioValidacaoVinculoIngressante}"
								id="inicioValidacaoVinculoIngressante"
								title="In�cio da Valida��o do V�nculo de Ingressantes">
								<f:converter converterId="convertData" />
							</t:inputCalendar> at� <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.fimValidacaoVinculoIngressante}"
								id="fimValidacaoVinculoIngressante"
								title="Fim da Valida��o do V�nculo de Ingressantes">
								<f:converter converterId="convertData" />
							</t:inputCalendar></td>
						</tr>
						<tr>
							<th>Ajustes das Matr�culas/Turmas:</th>
							<td>de <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.inicioAjustesMatricula}"
								id="inicioAjustesMatricula"
								title="In�cio do Ajustes das Matr�culas/Turmas">
								<f:converter converterId="convertData" />
							</t:inputCalendar> at� <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.fimAjustesMatricula}"
								id="fimAjustesMatricula"
								title="Fim do Ajustes das Matr�culas/Turmas">
								<f:converter converterId="convertData" />
							</t:inputCalendar></td>
						</tr>
						<tr>
							<th>Processamento de Matr�cula:</th>
							<td>de <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.inicioProcessamentoMatricula}"
								id="inicioProcessamentoMatricula"
								title="In�cio do Processamento de Matr�cula">
								<f:converter converterId="convertData" />
							</t:inputCalendar> at� <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.fimProcessamentoMatricula}"
								id="fimProcessamentoMatricula"
								title="Fim do Processamento de Matr�cula">
								<f:converter converterId="convertData" />
							</t:inputCalendar></td>
						</tr>
					</c:if>
					<tr>
						<td class="subFormulario" colspan="2">Datas para Per�odos de
						Rematr�culas e Processamentos</td>
					</tr>
					<tr>
						<th>Re-Matr�cula:</th>
						<td>de <t:inputCalendar popupDateFormat="dd/MM/yyyy"
							renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
							maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))" onchange="atualizaDataReMatriculaAnalise();"
							value="#{calendario.obj.inicioReMatricula}"
							id="inicioReMatricula"
							title="In�cio da Re-Matr�cula">
							<f:converter converterId="convertData" />
						</t:inputCalendar> at� <t:inputCalendar popupDateFormat="dd/MM/yyyy"
							renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
							maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))" onchange="atualizaDataReMatriculaAnalise();"
							value="#{calendario.obj.fimReMatricula}" id="fimReMatricula"
							title="Fim da Re-Matr�cula">
							<f:converter converterId="convertData" />
						</t:inputCalendar></td>
					</tr>
					<c:if test="${!calendario.obj.stricto}">
						<tr>
							<th>An�lise dos Coordenadores/Orientadores para Re-Matr�cula:</th>
							<td>de <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.inicioCoordenacaoAnaliseReMatricula}"
								id="inicioCoordenacaoAnaliseReMatricula"
								title="In�cio da An�lise dos Coordenadores/Orientadores para Re-Matr�cula">
								<f:converter converterId="convertData" />
							</t:inputCalendar> at� <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.fimCoordenacaoAnaliseReMatricula}"
								id="fimCoordenacaoAnaliseReMatricula"
								title="Fim da An�lise dos Coordenadores/Orientadores para Re-Matr�cula">
								<f:converter converterId="convertData" />
							</t:inputCalendar></td>
						</tr>
					</c:if>
					<c:if test="${calendario.obj.graduacao}">
						<tr>
							<th>An�lise dos Discentes para Re-Matr�cula:</th>
							<td>de <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.inicioDiscenteAnaliseReMatricula}"
								id="inicioDiscenteAnaliseReMatricula"
								title="In�cio da An�lise dos Discentes para Re-Matr�cula">
								<f:converter converterId="convertData" />
							</t:inputCalendar> at� <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.fimDiscenteAnaliseReMatricula}"
								id="fimDiscenteAnaliseReMatricula"
								title="Fim da An�lise dos Discentes para Re-Matr�cula">
								<f:converter converterId="convertData" />
							</t:inputCalendar></td>
						</tr>
						<tr>
							<th>Ajustes das Re-Matr�culas/Turmas:</th>
							<td>de <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.inicioAjustesReMatricula}"
								id="inicioAjustesReMatricula"
								title="In�cio dos Ajustes das Re-Matr�culas/Turmas">
								<f:converter converterId="convertData" />
							</t:inputCalendar> at� <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.fimAjustesReMatricula}"
								id="fimAjustesReMatricula"
								title="Fim dos Ajustes das Re-Matr�culas/Turmas">
								<f:converter converterId="convertData" />
							</t:inputCalendar></td>
						</tr>
						<tr>
							<th>Processamento de Re-Matr�cula:</th>
							<td>de <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.inicioProcessamentoReMatricula}"
								id="inicioProcessamentoReMatricula"
								title="In�cio do Processamento de Re-Matr�cula">
								<f:converter converterId="convertData" />
							</t:inputCalendar> at� <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.fimProcessamentoReMatricula}"
								id="fimProcessamentoReMatricula"
								title="Fim do Processamento de Re-Matr�cula">
								<f:converter converterId="convertData" />
							</t:inputCalendar></td>
						</tr>
						<tr>
							<th>Matr�cula Extraordin�ria:</th>
							<td>de <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.inicioMatriculaExtraordinaria}"
								id="inicioMatriculaExtraordinaria"
								title="In�cio da Matr�cula Extraordin�ria">
								<f:converter converterId="convertData" />
							</t:inputCalendar> at� <t:inputCalendar popupDateFormat="dd/MM/yyyy"
								renderAsPopup="true" renderPopupButtonAsImage="true" size="11"
								maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"
								value="#{calendario.obj.fimMatriculaExtraordinaria}"
								id="fimMatriculaExtraordinaria"
								title="Fim da Matr�cula Extraordin�ria">
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
						<td class="required">Per�odo:</td>
						<td width="35%"><t:inputCalendar popupDateFormat="dd/MM/yyyy"
							renderAsPopup="true" onkeypress="formataData(this, event)"
							renderPopupButtonAsImage="true" size="11"
							value="#{calendario.extra.inicio}" id="inicioExtra"
							title="In�cio do Per�do do Evento">
							<f:converter converterId="convertData" />
						</t:inputCalendar> at� <t:inputCalendar popupDateFormat="dd/MM/yyyy"
							renderAsPopup="true" onkeypress="formataData(this, event)"
							renderPopupButtonAsImage="true" size="11"
							value="#{calendario.extra.fim}" id="fimExtra"
							title="Fim do Per�do do Evento">
							<f:converter converterId="convertData" />
						</t:inputCalendar></td>
						<td width="22%">Suspens�o das atividades?</td>
						<td><h:selectOneRadio
							value="#{calendario.extra.suspensaoAtividades}" id="susp">
							<f:selectItem itemValue="true" itemLabel="Sim" id="sim" />
							<f:selectItem itemValue="false" itemLabel="N�o" id="nao" />
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
								<td><label for="calForm:interesseCoordenacoes">Coordena��es</label></td>
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
									<td colspan="2">Eventos inclu�dos</td>
	
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
		<span class="fontePequena">Campos de preenchimento obrigat�rio. </span> 
	</center>		
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
