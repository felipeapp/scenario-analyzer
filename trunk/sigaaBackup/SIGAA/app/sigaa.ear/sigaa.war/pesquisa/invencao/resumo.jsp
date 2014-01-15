<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<h2> <ufrn:subSistema /> &gt; Declaração de Invenção</h2>

<h:form id="form">
	<h:inputHidden value="#{invencao.confirmButton}" />
	<h:inputHidden value="#{invencao.obj.id}" />

	<table class="formulario" width="100%">
		<caption class="listagem">Resumo da Invenção</caption>
		
		<%@include file="/pesquisa/invencao/include/dados_invencao.jsp"%>
		
		<tfoot>
		<tr>
			<td colspan="4">
				<h:commandButton value="Submeter Notificação de Invenção" action="#{invencao.submeterNotificacaoInvencao}" id="btConfirmar" rendered="#{not invencao.readOnly}"/>
				<h:commandButton value="<< Voltar" action="#{invencao.telaInventores}" id="btVoltar" rendered="#{not invencao.readOnly}"/>  
				<h:commandButton value="Cancelar" action="#{invencao.cancelar}" onclick="#{confirm}" immediate="true" id="btCancelar" rendered="#{not invencao.readOnly}"/>
			</td>
		</tr>
		</tfoot>
	</table>
</h:form>

<c:if test="${invencao.readOnly}">
	<br/>
	<center>
		<a href="#" onclick="history.go(-1);"><< Voltar</a>
	</center>
</c:if>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
