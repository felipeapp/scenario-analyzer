<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Auditoria da Geração de Diplomas </h2>

<div class="descricaoOperacao">
	Este formulário permite auditar a geração do arquivo, no formato PDF, de diplomas. Para tanto, informe pelo menos um parâmetro para realizar a busca. 
</div>

	<h:form id="form">
	<table class="formulario" width="80%">
		<caption>Informe os Parâmetros da Busca </caption>
		<tbody>
			<c:if test="${fn:length(logGeracaoDiploma.niveisHabilitados) > 1}">
				<tr>
					<td width="2%">
					</td>
					<th style="text-align: right;" width="130px" class="obrigatorio">Nível de Ensino:</th>
					<td> 
						<h:selectOneMenu value="#{logGeracaoDiploma.nivelEnsinoEspecifico}" id="nivelEnsinoEspecifico">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{logGeracaoDiploma.niveisHabilitadosCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
			</c:if>
			<tr>
				<td width="5%">
					<h:selectBooleanCheckbox value="#{logGeracaoDiploma.buscaRegistro}" id="buscaRegistro"/>
				</td>
				<td width="30%">Nº do Registro do Diploma:</td>
				<td>
					<h:inputText value="#{logGeracaoDiploma.numeroRegistro}" id="numeroRegistro"  size="6" maxlength="6" 
					 onkeyup="return formatarInteiro(this);" converter="#{ intConverter }" 
					 onfocus="$('form:buscaRegistro').checked = true;" />
				</td>
			</tr>
			<tr>
				<td>
					<h:selectBooleanCheckbox value="#{logGeracaoDiploma.buscaMatricula}" id="buscaMatricula"/>
				</td>
				<td>Matrícula do Discente:</td>
				<td>
					<h:inputText value="#{logGeracaoDiploma.matricula}" id="matricula"  size="14" maxlength="12"  
					 onkeyup="return formatarInteiro(this);" converter="#{ intConverter }" 
					 onfocus="$('form:buscaMatricula').checked = true;" />
				</td>
			</tr>
			<tr>
				<td>
					<h:selectBooleanCheckbox value="#{logGeracaoDiploma.buscaNomeDiscente}" id="buscaNomeDiscente"/>
				</td>
				<td>Nome do Discente:</td>
				<td>
					<h:inputText value="#{logGeracaoDiploma.nomeDiscente}" id="nomeDiscente"  size="50" maxlength="120" 
					 onfocus="$('form:buscaNomeDiscente').checked = true;" />
				</td>
			</tr>
			<tr>
				<td>
					<h:selectBooleanCheckbox value="#{logGeracaoDiploma.buscaUsuario}" id="buscaUsuario"/>
				</td>
				<td>Nome do Usuário:</td>
				<td>
					<h:inputText value="#{logGeracaoDiploma.nomeUsuario}" id="nomeUsuario" size="50" maxlength="120"
						onfocus="$('form:buscaUsuario').checked = true;" />
				</td>
			</tr>
			<tr>
				<td>
					<h:selectBooleanCheckbox value="#{logGeracaoDiploma.buscaData}" id="buscaData"/>
				</td>
				<td>Data da Geração:</td>
				<td>
					<t:inputCalendar value="#{logGeracaoDiploma.data}" size="10" maxlength="10"
				 	 	onkeypress="return(formatarMascara(this,event,'##/##/####'))" popupDateFormat="dd/MM/yyyy" id="data" 
				 		renderAsPopup="true" renderPopupButtonAsImage="true"
				 		onfocus="$('form:buscaData').checked = true;" onchange="$('form:buscaData').checked = true;" >
				 	<f:converter converterId="convertData"/>
				</t:inputCalendar>
				</td>
			</tr>
		</tbody>
		<tfoot>
		<tr>
			<td colspan="3" align="center">
				<h:commandButton action="#{logGeracaoDiploma.buscarImpressao}" value="Buscar" id="btnbusca"/>
				<h:commandButton action="#{logGeracaoDiploma.cancelar}" value="Cancelar" onclick="#{confirm}" id="btncancelar"/>
			</td>
		</tr>
		</tfoot>
	</table>
	<br/>
	<c:if test="${not empty logGeracaoDiploma.listaLogGeracaoDiplomas}">
		<table class="listagem" >
			<caption>Lista de Logs de Geração de Diplomas</caption>
			<thead>
				<tr>
					<th style="text-align: center;">Data/Hora</th>
					<th>Usuário</th>
					<th>Nº dos Registros de Diplomas Gerados</th>
					<th width="6%">2ª Via</th>
				</tr>
			</thead>
			<c:forEach items="#{logGeracaoDiploma.listaLogGeracaoDiplomas}" var="item" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td align="center">
						<ufrn:format type="datahorasec" valor="${ item.data }" />
					</td>
					<td>
						${ item.registroEntrada.usuario.nome }
					</td>
					<td>
						${ item.registros }
					</td>
					<td align="center">
						<ufrn:format type="simnao" valor="${ item.segundaVia }" />
					</td>
				</tr>
			</c:forEach>
		</table>
	</c:if>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>