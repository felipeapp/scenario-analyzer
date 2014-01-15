<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2> <ufrn:subSistema /> > Renda Familiar</h2>
<h:form id="form">
	<table class="formulario">
	<caption> Cadastro de Renda Familiar</caption>
	<tr>
		<th class="required"> Descrição: </th>
		<td> <h:inputText id="descricao" value="#{ rendaFamiliarMBean.obj.descricao }" size="80" maxlength="100" readonly="#{rendaFamiliarMBean.readOnly}" disabled="#{rendaFamiliarMBean.readOnly}" />
	</td>
	</tr>
		<tfoot>
			<tr>
				<td colspan="2">
					<h:inputHidden id="hiddenId" value="#{rendaFamiliarMBean.obj.id}"/>
					<h:commandButton id="btnCadastrar" value="#{rendaFamiliarMBean.confirmButton}" action="#{rendaFamiliarMBean.cadastrar}"/>
					<c:if test="${rendaFamiliarMBean.obj.id > 0}">
						<h:commandButton id="btnVoltar" value="<< Voltar" action="#{rendaFamiliarMBean.listar}" immediate="true"/>
					</c:if>
					<h:commandButton id="btnCancelar" value="Cancelar" action="#{rendaFamiliarMBean.cancelar}" onclick="#{confirm}" immediate="true"/>
				</td>
			</tr>
		</tfoot>
	</table>
	<br/>
	<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp" %>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
