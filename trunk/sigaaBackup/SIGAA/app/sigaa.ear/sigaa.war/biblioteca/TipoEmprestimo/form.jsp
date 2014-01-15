<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>

<%
	CheckRoleUtil.checkRole(request, response, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
%>

<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL }  %>">

<h2>  <ufrn:subSistema /> > Novo Tipo de Empréstimo</h2>

<div class="descricaoOperacao"> 
   	<c:if test="${tipoEmprestimo.obj.id == 0}">
   		<p> Entre com as informações do novo Tipo de Empréstimo.</p>
   	</c:if>
   	<c:if test="${tipoEmprestimo.obj.id > 0}">
   		<p> Edite as informações do Tipo de Empréstimo selecionado.</p>
   	</c:if>
</div>	

<f:view>
	
	<br>
	<h:form>
	
		<a4j:keepAlive beanName="tipoEmprestimo" />
		

		<table class="formulario" width="70%">
			<caption>Tipo de Empréstimo</caption>
			
			<tr>
				<th class="obrigatorio">Descrição:</th>
				<td><h:inputText value="#{tipoEmprestimo.obj.descricao}" readonly="#{tipoEmprestimo.readOnly}" maxlength="100" size="60" onkeyup="CAPS(this)"/></td>
			</tr>
			
			<tfoot>
				<tr>
					<td colspan="2" align="center">
						<h:commandButton value="#{tipoEmprestimo.confirmButton}"  action="#{tipoEmprestimo.cadastrar}" id="acao"/>
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{tipoEmprestimo.voltar}" immediate="true" id="cancelar"/>
					</td>
				</tr>
			</tfoot>
		</table>
		
		<%@include file="/WEB-INF/jsp/include/_campos_obrigatorios.jsp"%>
		
	</h:form>

</f:view>

</ufrn:checkRole>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>