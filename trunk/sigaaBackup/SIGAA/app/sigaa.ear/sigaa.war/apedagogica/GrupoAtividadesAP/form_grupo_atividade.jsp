<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
	<a4j:keepAlive beanName="grupoAtividadesAP"></a4j:keepAlive>
	<h2 class="title"><ufrn:subSistema /> > Grupos de Atividades > Cadastrar</h2>

	<h:form id="formgrupoAtividadesAP">

		<div class="descricaoOperacao">
			<p>Caro usu�rio,</p>
			<p>O formul�rio abaixo permite efetuar as opera��es de cadastro e altera��o dos dados de um grupo.</p>
			<p>Para adicionar, alterar ou remover uma atividade pressione o bot�o "Pr�ximo >>".</p>
		</div>
		
		<table class="formulario" width="90%">
			<caption class="formulario">Grupo de Atividade</caption>

			<tr>
				<th  class="required">Denomina��o:</th>
				<td>
					<h:inputText id="grupoAtividade" size="65" maxlength="255"
						 readonly="#{grupoAtividadesAP.readOnly}" 
						value="#{grupoAtividadesAP.obj.denominacao}"/>	
				</td>
			</tr>
			
			<tr>
				<th  class="required">Per�odo de Atividades:</th>
				<td>
					<t:inputCalendar id="dataInicio" title="Data Inicial" rendered="#{!grupoAtividadesAP.readOnly}" 
						value="#{grupoAtividadesAP.obj.inicio}" renderAsPopup="true"
						renderPopupButtonAsImage="true" size="10" popupDateFormat="dd/MM/yyyy"
						onkeypress="return formataData(this,event)" maxlength="10" />
					<h:outputText value="#{grupoAtividadesAP.obj.inicio}" rendered="#{grupoAtividadesAP.readOnly}"></h:outputText>	
						 a  
					<t:inputCalendar rendered="#{!grupoAtividadesAP.readOnly}" 
						id="dataFim" value="#{grupoAtividadesAP.obj.fim}"  title="Data Final"
						renderAsPopup="true" renderPopupButtonAsImage="true" size="10" popupDateFormat="dd/MM/yyyy"
						onkeypress="return formataData(this,event)" maxlength="10" />
					<h:outputText value="#{grupoAtividadesAP.obj.fim}" rendered="#{grupoAtividadesAP.readOnly}"></h:outputText>
				</td>
			</tr>
			
			<tr>
				<th  class="required">Per�odo de Inscri��o:</th>
				<td>
					<t:inputCalendar id="inicioInscricao" title="Data Inicial Inscri��o" rendered="#{!grupoAtividadesAP.readOnly}" 
						value="#{grupoAtividadesAP.obj.inicioInscricao}" renderAsPopup="true"
						renderPopupButtonAsImage="true" size="10" popupDateFormat="dd/MM/yyyy"
						onkeypress="return formataData(this,event)" maxlength="10" />
					<h:outputText value="#{grupoAtividadesAP.obj.inicioInscricao}" rendered="#{grupoAtividadesAP.readOnly}"></h:outputText>	
						 a  
					<t:inputCalendar rendered="#{!grupoAtividadesAP.readOnly}" 
						id="fimInscricao" value="#{grupoAtividadesAP.obj.fimInscricao}"  title="Data Final Inscri��o"
						renderAsPopup="true" renderPopupButtonAsImage="true" size="10" popupDateFormat="dd/MM/yyyy"
						onkeypress="return formataData(this,event)" maxlength="10" />
					<h:outputText value="#{grupoAtividadesAP.obj.fimInscricao}" rendered="#{grupoAtividadesAP.readOnly}"></h:outputText>
				</td>
			</tr>
			
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{grupoAtividadesAP.listar}" 
							  immediate="true" id="btncancelar"/>
						<h:commandButton value="Pr�ximo >>" 
							action="#{grupoAtividadesAP.formAtividade}" id="btnFormAtividade"/> 
					</td>
				</tr>
			</tfoot>
		</table>
		
	</h:form>
	
	<br>
	
	<c:if test="${!grupoAtividadesAP.readOnly}">
	<center>
		<%@include file="/WEB-INF/jsp/include/_campos_obrigatorios.jsp"%>
	</center>
	</c:if>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
