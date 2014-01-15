<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>

<%
	CheckRoleUtil.checkRole(request, response, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_LOCAL, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
%>

<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_LOCAL, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL }  %>">

<f:view>

	<a4j:keepAlive beanName="tipoDocumentoNormalizacaoCatalogacaoMBean" />

	<h2>  <ufrn:subSistema /> > Novo Tipo de Documento para Normalização e Catalogação na Fonte</h2>
	<br>
	<h:form>

		<h:messages showDetail="true" />

		<table class="formulario" width="60%">
			<caption>Tipo de Documento</caption>
			
			<tr>
				<th class="obrigatorio">Denominação:</th>
				<td><h:inputText value="#{tipoDocumentoNormalizacaoCatalogacaoMBean.obj.denominacao}" readonly="#{tipoDocumentoNormalizacaoCatalogacaoMBean.readOnly}" maxlength="200" size="55"/></td>
			</tr>
			<tfoot>
			<tr>
				<td colspan="2" align="center">
					<h:commandButton id="cmdCadatrarTipoDocumento" value="#{tipoDocumentoNormalizacaoCatalogacaoMBean.confirmButton}"  action="#{tipoDocumentoNormalizacaoCatalogacaoMBean.cadastrar}"/>
					<h:commandButton value="Cancelar"  action="#{tipoDocumentoNormalizacaoCatalogacaoMBean.voltar}"/>
				</td>
			</tr>
			</tfoot>
		</table>
		<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
	</h:form>

</f:view>

</ufrn:checkRole>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>