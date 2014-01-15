<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<%@include file="/portais/docente/menu_docente.jsp" %>
	
	<h:outputText value="#{autorizacaoReconsideracao.create}" />
	
	<h2><ufrn:subSistema /> > Solicitação de Reanálise dos Requisitos Formais de Projetos de Ensino</h2>

	<table class="formulario" width="100%">
	<caption class="listagem"> Solicitação de Reanálise de Projetos de Ensino </caption>
	
	<tr>
    	<td>
			<h:form>
					<h:inputHidden value="#{autorizacaoReconsideracao.confirmButton}"/>
					<h:inputHidden value="#{autorizacaoReconsideracao.obj.id}"/>
					<h:inputHidden value="#{autorizacaoReconsideracao.obj.projetoEnsino.id}"/>
		
				<tr>
					<td>
					 	Projeto:
						<b><h:outputText value="#{autorizacaoReconsideracao.obj.projetoEnsino.titulo}"/></b><br/>
						<br/>
					</td>
				</tr>
		
		
			 <tr>
				<td>
					Justificativa do Pedido: <br/>
					<b><h:inputTextarea value="#{autorizacaoReconsideracao.obj.justificativaSolicitacao}" rows="10" cols="118" readonly="#{autorizacaoReconsideracao.readOnly}" id="justificativaDoPeriodo"/></b>
				</td>
			</tr>
		
				<br/>
				<br/>
			
				<tfoot>
					<tr>
						<td colspan="2">
		
							<c:if test="${autorizacaoReconsideracao.confirmButton != 'Remover'}">
								<h:commandButton value="#{autorizacaoReconsideracao.confirmButton}" action="#{autorizacaoReconsideracao.solicitarReconsideracaoExtensao}"/>
							</c:if>
		
							<c:if test="${autorizacaoReconsideracao.confirmButton == 'Remover'}">
								<h:commandButton value="#{autorizacaoReconsideracao.confirmButton}" action="#{autorizacaoReconsideracao.remover}"/>
							</c:if>
		
							<h:commandButton value="Cancelar" action="#{autorizacaoReconsideracao.cancelar}"/>
						</td>
					</tr>
				</tfoot>
			</h:form>
		</td>
	</tr>


	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>