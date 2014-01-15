<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<a4j:keepAlive beanName="avaliadorProjetoMbean" />
<f:view>
	<h2><ufrn:subSistema /> > Avaliadores de Projetos</h2>
	<h:form id="form">

	<table class="formulario" width="80%">
		<caption class="listagem"> Cadastrar Avaliador(a) </caption>
		<tr>
			<c:choose>
				<c:when test="${avaliadorProjetoMbean.confirmButton == 'Cadastrar'}">
					<th width="25%" class="required">Avaliador(a): </th>
				</c:when>
				<c:otherwise>
					<th width="25%">
						<b>Avaliador(a):</b>
					</th>
				</c:otherwise>
			</c:choose>
			
			<td>
				<c:if test="${avaliadorProjetoMbean.confirmButton == 'Cadastrar'}">
						<h:inputHidden id="idUsuario" value="#{avaliadorProjetoMbean.obj.usuario.id}"/>
						<h:inputText id="nomeUsuario" value="#{avaliadorProjetoMbean.obj.usuario.pessoa.nome}" style="width: 90%"/>
						<ajax:autocomplete source="form:nomeUsuario" target="form:idUsuario"
							baseUrl="/sigaa/ajaxUsuario" className="autocomplete"
							indicator="indicatorDocente" minimumCharacters="3" parameters="docente=true"
							parser="new ResponseXmlToHtmlListParser()" />
						<span id="indicatorDocente" style="display:none; "> 
						<img src="/sigaa/img/indicator.gif"  alt="Carregando..." title="Carregando..."/> 
						</span>						
				</c:if>
				 <c:if test="${avaliadorProjetoMbean.confirmButton == 'Alterar'}">
				 	${avaliadorProjetoMbean.obj.usuario.nome}
				 </c:if>				
			</td>
		</tr>	
	
		<tr>
			<th class="required">Área de Conhecimento:</th>
			<td>
				<h:selectOneMenu id="areaConhecimento1"	
					value="#{avaliadorProjetoMbean.obj.areaConhecimento1.id}"	
					readonly="#{avaliadorProjetoMbean.readOnly}">
					<f:selectItem itemValue="0" itemLabel=" -- SELECIONE --"/>
					<f:selectItems value="#{area.allGrandesAreasCombo}" />
				</h:selectOneMenu>
			</td>
		</tr>
	
	<%-- A partir de agora(22/08/2011) cada parecerista só precisa fazer a seleção de 1 área somente. 
	 	 Não precisa mais escolher uma área secundária.
	 	 O sistema agora seta a segunda área para null.
		<tr>
			<th>2ª Área de Conhecimento:</th>
			<td>
				<h:selectOneMenu id="areaConhecimento2"	
					value="#{avaliadorProjetoMbean.obj.areaConhecimento2.id}"	
					readonly="#{avaliadorProjetoMbean.readOnly}">
					<f:selectItem itemValue="0" itemLabel=" -- SELECIONE --"/>
					<f:selectItems value="#{area.allGrandesAreasCombo}" />
				</h:selectOneMenu>
			</td>
		</tr>
	--%>
		<tr>
			<th  class="required"> Data de Início: </th>
			<td> 
				<t:inputCalendar value="#{avaliadorProjetoMbean.obj.dataInicio}" id="dataInicio"
							renderAsPopup="true" renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy" 
			                size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"  
			                maxlength="10" popupTodayString="Hoje é">
			                <f:converter converterId="convertData" />
			    </t:inputCalendar>
			</td>
		</tr>
		
		<tr>
			<th  class="required"> Data de Fim: </th>
			<td> 
				<t:inputCalendar value="#{avaliadorProjetoMbean.obj.dataFim}" id="dataFim"
							renderAsPopup="true" renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy" 
			                size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"  maxlength="10" popupTodayString="Hoje é">
			                <f:converter converterId="convertData" />
			    </t:inputCalendar>
			</td>
		</tr>
		
		
		<tfoot>
			<tr>
				<td colspan="2">
						<h:commandButton value="#{avaliadorProjetoMbean.confirmButton}" action="#{avaliadorProjetoMbean.cadastrar}"/>
						<c:choose>
							<c:when test="${avaliadorProjetoMbean.confirmButton == 'Cadastrar'}">
								<h:commandButton value="Cancelar" action="#{avaliadorProjetoMbean.cancelar}" onclick="#{confirm}" immediate="true"/>
							</c:when>
							<c:otherwise>
								<h:commandButton value="Cancelar" action="#{avaliadorProjetoMbean.listar}" onclick="#{confirm}" immediate="true"/>
							</c:otherwise>
						</c:choose>
				</td>
			</tr>
		</tfoot>

	</table>

	<center>
		<h:graphicImage url="/img/required.gif" style="vertical-align: top;"/>
		<span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	</center>
			
</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>