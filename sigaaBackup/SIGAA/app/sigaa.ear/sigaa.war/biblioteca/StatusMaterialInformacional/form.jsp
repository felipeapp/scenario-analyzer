<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>


<h2>  <ufrn:subSistema /> > Status do Material Informacional</h2>

<div class="descricaoOperacao"> 
   	<c:if test="${statusMaterialInformacionalMBean.obj.id == 0}">
   		<p> Entre com as informações do novo Status e se ele aceitará que seus materiais sejam emprestados e reservados.</p>
   	</c:if>
   	<c:if test="${statusMaterialInformacionalMBean.obj.id > 0}">
   		<p> Edite as informações do  Status selecionado.</p>
   	</c:if>
</div>	

<f:view>
	
	<br>
	<h:form>
		<a4j:keepAlive beanName="statusMaterialInformacionalMBean" />

		<table class="formulario" width="70%">
			<caption>Status do Material</caption>
			
			<tr>
				<th class="obrigatorio">Descrição:</th>
				<td><h:inputText value="#{statusMaterialInformacionalMBean.obj.descricao}" readonly="#{statusMaterialInformacionalMBean.readOnly}" maxlength="200" size="40"/></td>
			</tr>
			
			<tr>
				<th class="obrigatorio">Permite Empréstimos ?</th>
				<td>
					<h:selectOneRadio  id="selectOneRadioStatusPermiteEmprestimo" value="#{statusMaterialInformacionalMBean.obj.permiteEmprestimo}" readonly="#{statusMaterialInformacionalMBean.readOnly}">
							<f:selectItem itemLabel="SIM" itemValue="true" />
							<f:selectItem itemLabel="NÃO" itemValue="false" />
					</h:selectOneRadio>
				</td>
			</tr>
			
			<tr>
				<th class="obrigatorio">Aceita Reservas ?</th>
				<td>
					<h:selectOneRadio  id="selectOneRadioStatusAceitaReserva" value="#{statusMaterialInformacionalMBean.obj.aceitaReserva}" readonly="#{statusMaterialInformacionalMBean.readOnly}">
							<f:selectItem itemLabel="SIM" itemValue="true" />
							<f:selectItem itemLabel="NÃO" itemValue="false" />
					</h:selectOneRadio>
				</td>
			</tr>
			
			<tfoot>
			<tr>
				<td colspan="2" align="center">
					<h:commandButton value="#{statusMaterialInformacionalMBean.confirmButton}"  action="#{statusMaterialInformacionalMBean.cadastrar}" id="acao"/>
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{statusMaterialInformacionalMBean.listar}" immediate="true" id="cancelar"/>
				</td>
			</tr>
			</tfoot>
		</table>
		
		<%@include file="/WEB-INF/jsp/include/_campos_obrigatorios.jsp"%>
		
	</h:form>

</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>