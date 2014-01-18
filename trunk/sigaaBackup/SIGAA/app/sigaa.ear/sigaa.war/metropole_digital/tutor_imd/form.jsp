<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp" %>
<a4j:keepAlive beanName="tutorIMD"/>
<f:view>
	
	<h2><ufrn:subSistema /> > Cadastro de Tutor do IMD</h2>
	
	<h:form>
		<div class="infoAltRem" style="width: 60%">
			<h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
			<a href="${ctx}/metropole_digital/tutor_imd/lista.jsf" >Listar Tutores do IMD</a>
		</div>
    
		<table class="formulario" width="60%">
			<caption class="listagem">Cadastro de Tutor do IMD</caption>
			<tr>
				<td colspan="2">
					<h:inputHidden value="#{tutorIMD.tutor.pessoa.id}"/>
					<h:inputHidden value="#{tutorIMD.tutor.id}"/>
				</td>
			<tr>
			<tr>
				<th style="width: 30%;" align="right"><b>Nome: </b></th>
				<td style="width: 70%;" align="left">${tutorIMD.tutor.pessoa.nome}</td>
			</tr>
			<tr>
				<th style="width: 30%;" align="right"><b>CPF: </b></th>
				<td style="width: 70%;" align="left"><ufrn:format type="cpf_cnpj" valor="${tutorIMD.tutor.pessoa.cpf_cnpj}" /></td>
			</tr>
			<tr><td colspan="2"><br /></td><tr>
			
			<tr>
				<th class="obrigatorio">Pólo:</th>
				<td>
					<h:selectOneMenu value="#{ tutorIMD.tutor.polo.id }" id="selectPolos">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
						<f:selectItems value="#{ tutorIMD.polosCombo }"/>
					</h:selectOneMenu>
				</td>
			</tr>
			
			<tr>
				<th class="obrigatorio">Data início:</th>
				<td>
					<t:inputCalendar value="#{tutorIMD.tutor.dataInicio}"
						size="10" maxlength="10" readonly="#{tutorIMD.readOnly}"
						renderAsPopup="true" renderPopupButtonAsImage="true" id="dataInicio" onkeypress="javascript:formataData(this,event); return ApenasNumeros(event);" title="Data início" alt="Data início" />									
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Data fim:</th>
				<td>
					<t:inputCalendar value="#{tutorIMD.tutor.dataFim}"
						size="10" maxlength="10" readonly="#{tutorIMD.readOnly}"
						renderAsPopup="true" renderPopupButtonAsImage="true" id="dataFim" onkeypress="javascript:formataData(this,event); return ApenasNumeros(event);" title="Data fim" alt="Data fim" />									
				</td>
			</tr>
			
		<!-- Botões -->
		
		<tfoot>
			<tr>
				<td colspan="2">
					<c:if test="${tutorIMD.tutor.id <= 0}">
						<h:commandButton value="#{tutorIMD.confirmButton}" action="#{tutorIMD.cadastrar}" />
						<h:commandButton value="Cancelar" action="#{tutorIMD.cancelar}" onclick="#{confirm}"/>
					</c:if> 
					<c:if test="${tutorIMD.tutor.id > 0 }">
						<h:commandButton value="Alterar" action="#{tutorIMD.cadastrar}" />
						<h:commandButton value="Cancelar" action="#{tutorIMD.cancelarAlteracao}" onclick="#{confirm}"/>
					</c:if> 
				</td>
			</tr>
		</tfoot>
	 	<!-- Fim botões -->
	 	
	 	</table>
		
	</h:form>
	
</f:view>
<%@ include file="/WEB-INF/jsp/include/rodape.jsp" %>