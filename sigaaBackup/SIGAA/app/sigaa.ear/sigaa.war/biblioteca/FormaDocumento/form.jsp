<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>

<%
	CheckRoleUtil.checkRole(request, response, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
%>

<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL }  %>">

<h2>  <ufrn:subSistema /> > Formas de Documento</h2>

<div class="descricaoOperacao"> 
   	<c:if test="${formaDocumentoMBean.obj.id == 0}">
   		<p> Entre com as informações da nova Forma de Documento.</p>
   	</c:if>
   	<c:if test="${tipoEmprestimo.obj.id > 0}">
   		<p> Edite as informações da Forma de Documento selecionada.</p>
   	</c:if>
</div>	

<f:view>
	
	<br>
	<h:form>
		
		<a4j:keepAlive beanName="formaDocumentoMBean"></a4j:keepAlive>

		<table class="formulario" width="70%">
			<caption>Forma do Documento</caption>
			
			<tr>
				<th class="obrigatorio">Denominação:</th>
				<td><h:inputText value="#{formaDocumentoMBean.obj.denominacao}" readonly="#{formaDocumentoMBean.readOnly}" maxlength="60" size="40"/></td>
			</tr>
			<tfoot>
			<tr>
				<td colspan="2" align="center">
					<h:commandButton value="#{formaDocumentoMBean.confirmButton}"  
								action="#{formaDocumentoMBean.cadastrar}"
								id="acao"/>
								
					<h:commandButton value="Cancelar" onclick="#{confirm}"  
							     action="#{formaDocumentoMBean.listar}" 
							     immediate="true" id="cancelar"/>
				</td>
			</tr>
			</tfoot>
		</table>
		<div class="obrigatorio">Campos de preenchimento obrigatório.</div>
	</h:form>

</f:view>

</ufrn:checkRole>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>