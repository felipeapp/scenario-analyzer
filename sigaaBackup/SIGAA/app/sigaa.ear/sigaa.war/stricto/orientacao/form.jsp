<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<%@page import="br.ufrn.sigaa.ensino.graduacao.jsf.OrientacaoAcademicaMBean"%>


<f:view>
	<h2 class="title"><ufrn:subSistema /> > Gerênciar Orientações >
		<c:if test="${orientacaoAcademica.cadastrar}">
			Cadastrar Orientação
		</c:if>
		<c:if test="${orientacaoAcademica.finalizar}">
			Finalizar Orientação
		</c:if>		
		<c:if test="${orientacaoAcademica.cancelar}">
			Cancelar Orientação
		</c:if>
		<c:if test="${!orientacaoAcademica.cancelar &&  !orientacaoAcademica.cadastrar && !orientacaoAcademica.finalizar}">
			Alterar Orientação
		</c:if>					
	</h2>
	
	<c:set value="#{orientacaoAcademica.discenteBusca}" var="discente" />
	<%@ include file="/graduacao/info_discente.jsp"%>
	
	<h:form id="formCadastroOrientacao">
		<a4j:keepAlive beanName="orientacaoAcademica" />
		<h:outputText value="#{orientacaoAcademica.create}"></h:outputText>
		<h:inputHidden value="#{orientacaoAcademica.tipoBuscaDocente}" id="idTipoBusca"/>
		<table class="formulario">
			<caption class="formulario">Dados da Orientação</caption>
			
			<c:if test="${orientacaoAcademica.cadastrar}">
			<tr>
				<td colspan="2">
					<div id="abas-equipePrograma">
						<div id="membrosPrograma" class="aba" >
						<table width="100%">
								<c:if test="${!acesso.ppg}">
								<tr>
								<th class="required">Docente:</th>
								<td>
									<h:selectOneMenu  id="docentePrograma" value="#{ orientacaoAcademica.equipe.id }" rendered="#{!acesso.ppg}">
										<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
										<f:selectItems value="#{ equipePrograma.docentesProgramaCombo}"/>
									</h:selectOneMenu>
								</td>
								</tr>
								</c:if>
								<c:if test="${acesso.ppg}">
								<tr>
								<th>Programa:</th>
								<td>
									<h:selectOneMenu value="#{equipePrograma.obj.programa.id}" id="Programa" onchange="submit()"
										valueChangeListener="#{equipePrograma.carregarEquipeDoPrograma}"  rendered="#{acesso.ppg}">
										<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
										<f:selectItems value="#{unidade.allProgramaPosCombo}" />
									</h:selectOneMenu>
								</td>
								</tr>
								<tr>
								<th class="required">Docente:</th>
								<td>
									<h:selectOneMenu  id="docentePrograma2" value="#{ orientacaoAcademica.equipe.id }" rendered="#{acesso.ppg}">
										<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
										<f:selectItems value="#{ equipePrograma.docentesProgramaObjCombo}"/>
									</h:selectOneMenu>
								</td>
								</tr>
								</c:if>
						</table>		
						</div>
						
						<div id="externoPrograma" class="aba">
						<table width="100%">
							<tr>
								<th class="required">Docente:</th>
								<td colspan="2">
									<h:inputHidden value="#{orientacaoAcademica.idDocente}" id="idDocente"/>
									<c:set var="idAjax" value="formCadastroOrientacao:idDocente"/>
									<c:set var="nomeAjax" value="orientacaoAcademica.nome"/>
									<c:set var="cedidos" value="true"/>
									<%@include file="/WEB-INF/jsp/include/ajax/docente_jsf.jsp" %>
								</td>	
							</tr>
						</table>
						</div>
					</div>					

				</td>
			</tr>
			
			<tr>
				<th class="required">Tipo de Orientação:</th>
				<td>
					<h:selectOneRadio value="#{ orientacaoAcademica.orientacao.tipoOrientacao }" id="tipoOrientacao" layout="pageDirection">
						<f:selectItems value="#{ teseOrientada.tipoOrientacaoDocente }"/>
					</h:selectOneRadio>
				</td>
			</tr>
			</c:if>
			
			<c:if test="${!orientacaoAcademica.cadastrar}">
			<tr>
				<th>Docente:</th>
				<td>
					${orientacaoAcademica.orientacao.descricaoOrientador}
				</td>
			</tr>			
			<tr>
				<th>Tipo de Orientação:</th>
				<td>
					${orientacaoAcademica.orientacao.tipoOrientacaoString}
				</td>
			</tr>
			</c:if>

			<c:if test="${!orientacaoAcademica.finalizar}">
			<tr>
				<th class="${!orientacaoAcademica.cancelar ? 'required' : ''}">Data de início:</th>
				<td>
					<t:inputCalendar renderAsPopup="true" renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy"
					size="10" maxlength="10" onkeypress="return formataData(this,event)" title="Data de Início"
					value="#{orientacaoAcademica.orientacao.inicio}" id="inicioOrientacao" rendered="#{!orientacaoAcademica.cancelar}">
					<f:converter converterId="convertData"/>
					</t:inputCalendar>
					
					<h:outputText value="#{orientacaoAcademica.orientacao.inicio}" rendered="#{orientacaoAcademica.cancelar}"/>
				</td>
			</tr>
			</c:if>

			<c:if test="${orientacaoAcademica.finalizar}">
			
			<tr>
				<th>Data de início:</th>
				<td>
					<ufrn:format type="data" valor="${orientacaoAcademica.orientacao.inicio}"/>
				</td>
			</tr>
			<tr>
				<th class="required">Data de finalização:</th>
				<td>
					<t:inputCalendar renderAsPopup="true" renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy"
					size="10" maxlength="10" onkeypress="formataData(this,event)" title="Data de Finalização"
					value="#{orientacaoAcademica.orientacao.fim}" id="fimOrientacao"  disabled="#{orientacaoAcademica.cancelar}">
					<f:converter converterId="convertData"/>
					</t:inputCalendar>
				</td>
			</tr>
			</c:if>

			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="#{orientacaoAcademica.confirmButton}" action="#{orientacaoAcademica.cadastrar}" id="cadastrar"/>
						<h:commandButton value="<< Voltar" action="#{orientacaoAcademica.telaListaOrientadores}" id="voltar"/>
						<h:commandButton value="Cancelar" action="#{orientacaoAcademica.cancelar}" onclick="#{confirm}" id="cancelar"/>
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<br>
	<center><html:img page="/img/required.gif" style="vertical-align: top;" /> <span
		class="fontePequena"> Campos de preenchimento obrigatório. </span> <br>
	<br>
	</center>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

<script type="text/javascript">
var Abas = function() {

	var setTipoBusca = function(e, aba) {
		var idAba = aba;
		var categoria = getEl('formCadastroOrientacao:idTipoBusca');
		switch(idAba.id) {
			case 'membrosPrograma' : categoria.dom.value = <%= OrientacaoAcademicaMBean.DOCENTE_PROGRAMA %>; break;
			case 'externoPrograma' : categoria.dom.value = <%= OrientacaoAcademicaMBean.DOCENTE_EXTERNO_PROGRAMA %>; break;
		}
	};
	return {
	    init : function(){
	        var abas = new YAHOO.ext.TabPanel('abas-equipePrograma');
			abas.on('tabchange', setTipoBusca);
			<c:if test="${acesso.algumUsuarioStricto}">
	        	abas.addTab('membrosPrograma', "Docentes do Programa")
	        </c:if>
			abas.addTab('externoPrograma', "Docentes Que Não Pertencem ao Programa");
			
			switch( getEl('formCadastroOrientacao:idTipoBusca').dom.value ) {
				case ''+<%=OrientacaoAcademicaMBean.DOCENTE_PROGRAMA%>:  abas.activate('membrosPrograma'); break;
				case ''+<%=OrientacaoAcademicaMBean.DOCENTE_EXTERNO_PROGRAMA%>:  abas.activate('externoPrograma'); break;
				default: abas.activate('membrosPrograma'); break;
			}
	    }
    }
}();

YAHOO.ext.EventManager.onDocumentReady(Abas.init, Abas, true);

buscarDocentePor_1('buscaAjaxDocenteUFRN_1');
</script>
