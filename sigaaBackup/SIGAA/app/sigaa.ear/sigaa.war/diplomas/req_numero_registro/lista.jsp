<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Auditoria da Requisição de Números de Registro de Diplomas </h2>

<div class="descricaoOperacao">Este formulário permite auditar a
	requisição de números para o registro de diplomas, tanto de discentes
	internos quanto de discentes externos à Instituição.</div>

	<h:form id="form">
	<a4j:keepAlive beanName="requisicaoNumeroRegistro"></a4j:keepAlive>
	
	<table class="formulario" width="80%">
		<caption>Informe os Parâmetros da Busca </caption>
		<tbody>
		<c:if test="${fn:length(requisicaoNumeroRegistro.niveisHabilitados) > 1}">
				<tr>
					<td width="2%">
					</td>
					<th style="text-align: right;" width="130px" class="obrigatorio">Nível de Ensino:</th>
					<td> 
						<h:selectOneMenu value="#{requisicaoNumeroRegistro.obj.nivel}" id="nivelEnsinoEspecifico">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{requisicaoNumeroRegistro.niveisHabilitadosCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
			</c:if>
		<tr>
			<td width="5%">
				<h:selectBooleanCheckbox value="#{requisicaoNumeroRegistro.buscaRegistro}" id="buscaRegistro"/>
			</td>
			<td width="30%">Nº do Registro do Diploma:</td>
			<td>
				<h:inputText value="#{requisicaoNumeroRegistro.numeroRegistro}" id="numeroRegistro"  size="6" maxlength="6" 
				 onkeyup="return formatarInteiro(this);" converter="#{ intConverter }" 
				 onfocus="$('form:buscaRegistro').checked = true;" />
			</td>
		</tr>	
		<tr>
			<td>
				<h:selectBooleanCheckbox value="#{requisicaoNumeroRegistro.buscaUsuario}" id="buscaUsuario"/>
			</td>
			<td>Nome do Usuário:</td>
			<td>
				<h:inputText value="#{requisicaoNumeroRegistro.nomeUsuario}" id="nomeUsuario" size="50" maxlength="120"
					onfocus="$('form:buscaUsuario').checked = true;" />
			</td>
		</tr>
		<tr>
			<td>
				<h:selectBooleanCheckbox value="#{requisicaoNumeroRegistro.buscaData}" id="buscaData"/>
			</td>
			<td>Data da Requisição:</td>
			<td>
				<t:inputCalendar value="#{requisicaoNumeroRegistro.solicitadoEm}" size="10" maxlength="10"
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
				<h:commandButton action="#{requisicaoNumeroRegistro.buscarRequisicoes}" value="Buscar" id="btnBusca"/>
				<h:commandButton action="#{requisicaoNumeroRegistro.cancelar}" value="Cancelar" onclick="#{confirm}" id="btnCancelar"/>
			</td>
		</tr>
		</tfoot>
	</table>
	<br/>
	<c:if test="${not empty requisicaoNumeroRegistro.listaControleNumeroRegistro}">
		<table class="listagem" >
			<caption>Lista de Requisições de Números de Registros de Diplomas</caption>
			<thead>
				<tr>
					<th style="text-align: center;">Data/Hora</th>
					<th>Usuário</th>
					<th style="text-align: right;">Nº do Registro</th>
					<th style="text-align: center;">Registro Antigo</th>
					<th style="text-align: center;">Registro Externo</th>
				</tr>
			</thead>
			<c:forEach items="#{requisicaoNumeroRegistro.listaControleNumeroRegistro}" var="item" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td align="center">
						<ufrn:format type="datahorasec" valor="${ item.solicitadoEm }" />
					</td>
					<td>
						${ item.registroEntrada.usuario.nome }
					</td>
					<td style="text-align: right;">
						${ item.numeroRegistro}
					</td>
					<td align="center">
						<ufrn:format type="simnao" valor="${ item.registroAntigo }" />
					</td>
					<td align="center">
						<ufrn:format type="simnao" valor="${ item.registroExterno }" />
					</td>
				</tr>
			</c:forEach>
		</table>
	</c:if>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>