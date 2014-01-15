<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
	<a4j:keepAlive beanName="grupoAtividadesAP"></a4j:keepAlive>
	<h2 class="title"><ufrn:subSistema /> > Grupos de Atividades > Cadastrar</h2>

	<h:form id="formgrupoAtividadesAP">

		<div class="descricaoOperacao">
			<p>Caro usuário,</p>
			<p>O formulário abaixo permite efetuar as operações de cadastro e alteração dos dados de um grupo.</p>
			<p>Para adicionar, alterar ou remover uma atividade pressione o botão "Próximo >>".</p>
		</div>
		
		<table class="formulario" width="90%">
			<caption class="formulario">Grupo de Atividade</caption>

			<tr>
				<th  class="required">Denominação:</th>
				<td>
					<h:inputText id="grupoAtividade" size="65" maxlength="255"
						 readonly="#{grupoAtividadesAP.readOnly}" 
						value="#{grupoAtividadesAP.obj.denominacao}"/>	
				</td>
			</tr>
			
			<tr>
				<th  class="required">Período de Atividades:</th>
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
				<th  class="required">Período de Inscrição:</th>
				<td>
					<t:inputCalendar id="inicioInscricao" title="Data Inicial Inscrição" rendered="#{!grupoAtividadesAP.readOnly}" 
						value="#{grupoAtividadesAP.obj.inicioInscricao}" renderAsPopup="true"
						renderPopupButtonAsImage="true" size="10" popupDateFormat="dd/MM/yyyy"
						onkeypress="return formataData(this,event)" maxlength="10" />
					<h:outputText value="#{grupoAtividadesAP.obj.inicioInscricao}" rendered="#{grupoAtividadesAP.readOnly}"></h:outputText>	
						 a  
					<t:inputCalendar rendered="#{!grupoAtividadesAP.readOnly}" 
						id="fimInscricao" value="#{grupoAtividadesAP.obj.fimInscricao}"  title="Data Final Inscrição"
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
						<h:commandButton value="Próximo >>" 
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
