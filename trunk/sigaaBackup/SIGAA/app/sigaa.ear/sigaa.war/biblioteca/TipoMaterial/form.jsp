<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>

<%
	CheckRoleUtil.checkRole(request, response, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
%>

<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL }  %>">

<h2>  <ufrn:subSistema /> > Tipo de Material</h2>

<div class="descricaoOperacao"> 
   	<c:if test="${tipoMaterialMBean.obj.id == 0}">
   		<p> Entre com as informações do novo Tipo de Material.</p>
   	</c:if>
   	<c:if test="${tipoMaterialMBean.obj.id > 0}">
   		<p> Edite as informações do Tipo de Material selecionado.</p>
   	</c:if>
</div>	

<f:view>
	
	<br>
	<h:form>
		<h:inputHidden value="#{tipoMaterialMBean.obj.id}"/>

		<table class="formulario" width="60%">
			<caption>Tipo de Material</caption>
			
			<tr>
				<th class="obrigatorio">Descrição:</th>
				<td><h:inputText value="#{tipoMaterialMBean.obj.descricao}" readonly="#{tipoMaterialMBean.readOnly}" maxlength="60" size="40"/></td>
			</tr>
			<tfoot>
			<tr>
				<td colspan="2" align="center">
				
					<h:commandButton value="#{tipoMaterialMBean.confirmButton}"  action="#{tipoMaterialMBean.cadastrar}" id="acao"/>
								
					<h:commandButton value="Cancelar" onclick="#{confirm}"  action="#{tipoMaterialMBean.voltar}" immediate="true" id="cancelar"/>
					
				</td>
			</tr>
			</tfoot>
		</table>
		
		<%@include file="/WEB-INF/jsp/include/_campos_obrigatorios.jsp"%>
		
	</h:form>

</f:view>

</ufrn:checkRole>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>