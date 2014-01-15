<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<h2 class="tituloPagina">
	<html:link action="/ensino/latosensu/cadastroHistoricoSituacao.do?dispatch=cancelar">
		<ufrn:subSistema semLink="true"/>
	</html:link> &gt; Alterar Situação de Proposta
</h2>

<html:form action="/ensino/latosensu/cadastroHistoricoSituacao?dispatch=persist" 
		method="post" focus="descricao" onsubmit="return validateLatoOutrosDocumentosForm(this);" >
    <table class="formulario" width="80%">
		<caption>Situação de Proposta de Curso Lato Sensu</caption>
		<html:hidden property="obj.id" />
		<html:hidden property="obj.usuario.id" value="${sessionScope.usuario.id}" />
		<tbody>
			<tr>
				<th nowrap="nowrap">Curso:</th>
				<td><b>${historicoSituacaoForm.cursoLato.nome }</b></td>
            </tr>
            <tr>
            	<th nowrap="nowrap">Situação Atual:</th>
				<td><b>${historicoSituacaoForm.obj.situacao.descricao}</b></td>
			</tr>
            <tr>
 				<th nowrap="nowrap">Nova Situação:</th>
				<td>
					<html:select property="obj.proposta.situacaoProposta.id">
		                <html:option value="0"> -- Selecione -- </html:option>
		                <html:options collection="situacoesproposta" property="id" labelProperty="descricao" />
	                </html:select>
				</td>
            </tr>
            <tr>
				<th nowrap="nowrap">Despacho:</th>
				<td><html:textarea property="obj.observacoes" rows="6" cols="70" /> </td>
			</tr>
		</tbody>
		<tfoot>
            <tr>
				<td colspan="4" align="center">
					<html:submit>Confirmar Alteração</html:submit>
                	<html:button property="" onclick="javascript:location.href='${pageContext.request.contextPath}/verMenuLato.do'">
                		<fmt:message key="botao.cancelar" />
                	</html:button>
                </td>
			</tr>
		</tfoot>
	</table>
</html:form>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>