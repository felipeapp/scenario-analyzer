<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<f:view>
	<h2><ufrn:subSistema /> > Avaliadores de Ações de Extensão</h2>
	<h:form id="form">

		<h:inputHidden value="#{avaliadorExtensao.confirmButton}"/>
		<h:inputHidden value="#{avaliadorExtensao.obj.id}"/>
	
		<table class="formulario" width="80%">
		<caption class="listagem"> Cadastrar Avaliador(a) </caption>
	
	
		<tr>
			
			<c:choose>
				<c:when test="${avaliadorExtensao.confirmButton == 'Cadastrar'}">
					<th width="20%" class="required">
						Servidor(a):
					</th>
				</c:when>
				<c:otherwise>
					<th width="20%">
						<b>Servidor(a):</b>
					</th>
				</c:otherwise>
			</c:choose>
			
			<td> 
				 <c:if test="${avaliadorExtensao.confirmButton == 'Cadastrar'}">
						<h:inputText value="#{avaliadorExtensao.obj.servidor.pessoa.nome}" id="nome" size="60" readonly="#{avaliadorExtensao.readOnly}"/>
							 <h:inputHidden value="#{avaliadorExtensao.obj.servidor.id}" id="idServidor"/>
				
							 <ajax:autocomplete source="form:nome" target="form:idServidor"
								baseUrl="/sigaa/ajaxServidor" className="autocomplete"
								indicator="indicator" minimumCharacters="3" parameters="tipo=todos"
								parser="new ResponseXmlToHtmlListParser()" />
				
							<span id="indicator" style="display:none;"> <img src="/sigaa/img/indicator.gif" /> </span>
				</c:if>
				 <c:if test="${avaliadorExtensao.confirmButton == 'Alterar'}">
				 	${avaliadorExtensao.obj.servidor.pessoa.nome}
				 </c:if>
			</td>
		</tr>	
		
	
		<tr>
			<th class="required">Área Temática:</th>
			<td>
				<h:selectOneMenu id="areaTematica"	
					value="#{avaliadorExtensao.obj.areaTematica.id}"	
					readonly="#{avaliadorExtensao.readOnly}" style="width: 70%;">
					<f:selectItem itemValue="0" itemLabel=" -- SELECIONE UMA ÁREA TEMÁTICA --"/>
					<f:selectItems value="#{areaTematica.allCombo}"/>
				</h:selectOneMenu>
			</td>
		</tr>
	
	<%-- A partir de agora(22/08/2011) cada parecerista só precisa fazer a seleção de 1 área somente. 
		 Não precisa mais escolher uma área secundária.
		 O sistema agora seta a segunda área para null.
		<tr>
			<th>2ª Área Temática:</th>
			<td>
				<h:selectOneMenu id="areaTematica2"	
					value="#{avaliadorExtensao.obj.areaTematica2.id}"	
					readonly="#{avaliadorExtensao.readOnly}" style="width: 70%;">
					<f:selectItem itemValue="0" itemLabel=" -- SELECIONE UMA ÁREA TEMÁTICA --"/>
					<f:selectItems value="#{areaTematica.allCombo}"/>
				</h:selectOneMenu>
			</td>
		</tr>
	--%>
	
		<tr>
			<th  class="required"> Data Início Mandato: </th>
			<td> 
				<t:inputCalendar value="#{avaliadorExtensao.obj.dataInicio}" readonly="#{avaliadorExtensao.readOnly}" renderAsPopup="true" 
					renderPopupButtonAsImage="true" size="10" maxlength="10" onkeypress="return(formataData(this,event))" id="DataInicioMandato" title="Data Início Mandato">
					<f:convertDateTime pattern="dd/MM/yyyy"/>
				</t:inputCalendar>
			</td>
		</tr>
		
		<tfoot>
			<tr>
				<td colspan="2">
						<h:commandButton value="#{avaliadorExtensao.confirmButton}" action="#{avaliadorExtensao.cadastrar}"/>
						<h:commandButton value="Cancelar" action="#{avaliadorExtensao.listPage}" onclick="#{confirm}"/>
				</td>
			</tr>
		</tfoot>
	
		</table>
	</h:form>

	<center>
		<h:graphicImage url="/img/required.gif" style="vertical-align: top;"/>
		<span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	</center>
			
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>