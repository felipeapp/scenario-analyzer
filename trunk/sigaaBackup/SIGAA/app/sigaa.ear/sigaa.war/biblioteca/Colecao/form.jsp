<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>

<%
	CheckRoleUtil.checkRole(request, response, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
%>

<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL }  %>">

<f:view>
	<h2>  <ufrn:subSistema /> > Cole��es</h2>
	<br>
	
	<div class="descricaoOperacao"> 
    	<c:if test="${colecaoMBean.obj.id == 0}">
    		<p> Entre com as informa��es da nova cole��o e se ele aceitar� que seus materiais sejam utilizados no registro de consultas locais.</p>
    	</c:if>
    	<c:if test="${colecaoMBean.obj.id > 0}">
    		<p> Edite as informa��es da cole��o selecionada.</p>
    	</c:if>
    </div>	
	
	<h:form>
	
		<a4j:keepAlive beanName="colecaoMBean" />


		<table class="formulario" width="80%">
			<caption>Cole��o</caption>
			
			<tr>
				<th class="obrigatorio">C�digo:</th>
				<td colspan="2"><h:inputText value="#{colecaoMBean.obj.codigo}" readonly="#{colecaoMBean.readOnly}" maxlength="20" size="10" onkeyup="CAPS(this);"/></td>
			</tr>
			<tr>
				<th class="obrigatorio">Descri��o:</th>
				<td colspan="2"><h:inputText value="#{colecaoMBean.obj.descricao}" readonly="#{colecaoMBean.readOnly}" maxlength="200" size="55"/></td>
			</tr>
			<tr>
				<th class="obrigatorio">Utilizado para Registro de Consultas:</th>
				<td style="width: 20%;">
					
					<h:selectOneRadio  id="selectOneRadioStatusPermiteEmprestimo" value="#{colecaoMBean.obj.contagemMovimentacao}" readonly="#{statusMaterialInformacionalMBean.readOnly}">
							<f:selectItem itemLabel="SIM" itemValue="true" />
							<f:selectItem itemLabel="N�O" itemValue="false" />
					</h:selectOneRadio>
					
				</td>
				<td>
					<ufrn:help>Indica se a cole��o � utilizada para o registro de consultas locais na biblioteca.</ufrn:help>
				</td>
			</tr>
			<tfoot>
			<tr>
				<td colspan="3" align="center">
					<h:commandButton value="#{colecaoMBean.confirmButton}"  
								action="#{colecaoMBean.cadastrar}"
								id="acao"/>
											
					<h:commandButton value="Cancelar" onclick="#{confirm}"  
							     action="#{colecaoMBean.voltar}" 
							     immediate="true" id="cancelar"/>
				</td>
			</tr>
			</tfoot>
		</table>
		<div class="obrigatorio">Campos de preenchimento obrigat�rio.</div>
	</h:form>

</f:view>

</ufrn:checkRole>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>