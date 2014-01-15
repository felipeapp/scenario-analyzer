<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2> <ufrn:subSistema /> &gt; Documento Legal</h2>
	
<h:form>
  <table class="formulario" width="75%">
	<caption> Cadastro de Documentos Legais</caption>
		<tr>
			<th width="40%" class="obrigatorio">Curso:</th>
			<td>
				<h:selectOneMenu value="#{ documentoLegalMBean.obj.curso.id }" >
					<f:selectItem itemValue="0" itemLabel=" -- SELECIONE -- " />
					<f:selectItems value="#{ curso.allCursoTecnicoCombo }"/>
				</h:selectOneMenu>
			</td>
		</tr>
		<tr>
			<th class="obrigatorio">Nome do Documento:</th>
			<td> <h:inputText value="#{ documentoLegalMBean.obj.nomeDocumento }" readonly="#{documentoLegalMBean.readOnly}" 
							disabled="#{documentoLegalMBean.readOnly}" maxlength="200" onkeyup="CAPS(this);" />
			</td>
		</tr>
		<tr>
			<th class="obrigatorio">Número do Documento:</th>
			<td> <h:inputText value="#{ documentoLegalMBean.obj.numeroDocumento }" readonly="#{documentoLegalMBean.readOnly}" 
							disabled="#{documentoLegalMBean.readOnly}" maxlength="200" onkeyup="return formatarInteiro(this);" />
			</td>
		</tr>
		<tr>
			<th class="obrigatorio">Número do Parecer / Despacho:</th>
			<td> <h:inputText value="#{ documentoLegalMBean.obj.numeroParecer }" readonly="#{documentoLegalMBean.readOnly}" 
							disabled="#{documentoLegalMBean.readOnly}" maxlength="20" onkeyup="CAPS(this);" />
			</td>
		</tr>
		<tr>
			<th>Data de Parecer/Despacho:</th>
			  <td>
			 	<t:inputCalendar value="#{documentoLegalMBean.obj.dataParecer}" id="dataParecer" size="10" maxlength="10" 
	    			onkeypress="return(formatarMascara(this,event,'##/##/####'))" popupDateFormat="dd/MM/yyyy" 
	    			renderAsPopup="true" renderPopupButtonAsImage="true" title="Data_Parecer">
	      			<f:converter converterId="convertData" />
				</t:inputCalendar>
			</td>
		</tr>
		<tr>
			<th>Data Publicação:</th>
			<td>
			 	<t:inputCalendar value="#{documentoLegalMBean.obj.dataPublicacao}" id="dataPublicacao" size="10" maxlength="10" 
	    			onkeypress="return(formatarMascara(this,event,'##/##/####'))" popupDateFormat="dd/MM/yyyy" 
	    			renderAsPopup="true" renderPopupButtonAsImage="true" title="Data_Publicacao">
	      			<f:converter converterId="convertData"/>
				</t:inputCalendar>
			</td>
		</tr>
		<tr>
			<th>Data da Aprovação:</th>
			<td> 
			 	<t:inputCalendar value="#{documentoLegalMBean.obj.dataAprovacao}" id="dataAprovacao" size="10" maxlength="10" 
	    			onkeypress="return(formatarMascara(this,event,'##/##/####'))" popupDateFormat="dd/MM/yyyy" 
	    			renderAsPopup="true" renderPopupButtonAsImage="true" title="Data_Aprovacao">
	      			<f:converter converterId="convertData"/>
				</t:inputCalendar>
			</td>
		</tr>
		<tr>
			<th>Local da Publicação:</th>
			<td> <h:inputText value="#{ documentoLegalMBean.obj.localPublicacao }" readonly="#{documentoLegalMBean.readOnly}" 
							disabled="#{documentoLegalMBean.readOnly}" maxlength="200" onkeyup="CAPS(this);" />
			</td>
		</tr>
		<tr>
			<th>Validade: </th>
			<td> <h:inputText value="#{ documentoLegalMBean.obj.validade }" readonly="#{documentoLegalMBean.readOnly}" 
					disabled="#{documentoLegalMBean.readOnly}" maxlength="2" onkeyup="return formatarInteiro(this);" /> (em Anos)
			</td>
		</tr>

	<tfoot>
		<tr>
			<td colspan="2">
				<h:inputHidden value="#{documentoLegalMBean.obj.id}"/>
				<c:choose>
					<c:when test="${documentoLegalMBean.obj.id > 0}">
						<h:commandButton value="Alterar" action="#{documentoLegalMBean.cadastrar}"/>
						<h:commandButton value="<< Voltar" action="#{documentoLegalMBean.listar}" immediate="true"/>
					</c:when>
					<c:otherwise>
						<h:commandButton value="#{documentoLegalMBean.confirmButton}" action="#{documentoLegalMBean.cadastrar}"/>
					</c:otherwise>
				</c:choose>
				<h:commandButton value="Cancelar" action="#{documentoLegalMBean.cancelar}" onclick="#{confirm}" immediate="true"/>
			</td>
	   </tr>
	</tfoot>

  </table>
<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp" %>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>