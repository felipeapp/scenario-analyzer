<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> > Autorização para Solicitações de Reconsiderações para Requisitos Formais da Proposta de Projeto</h2>

		<br/>
		<h:form id="relatorio">

			<h:inputHidden value="#{autorizacaoReconsideracao.obj.id}" />
			<h:inputHidden value="#{autorizacaoReconsideracao.obj.projetoEnsino.id}" />

				
			<table class="formulario" width="95%">
				<caption class="listagem">Análise do pedido de reconsideração por requisitos formais</caption>
	

			<tr>
				<td colspan="2">
				
					<table class="subFormulario" width="100%">
					   <caption>Detalhes do Projeto de Ensino</caption>

						<tr>
							<th width="15%"><b>Ano - Título:</b></th>
							<td align="justify">
							 <h:outputText value="#{autorizacaoReconsideracao.obj.projetoEnsino.anoTitulo}"/>
							</td>
						</tr>


						<tr>
							<th><b>Resumo do Projeto:</b></th>
							<td align="justify">
							 <h:outputText value="#{autorizacaoReconsideracao.obj.projetoEnsino.resumo}"/>
							</td>
						</tr>

					</table>

					<table class="subFormulario" width="100%">
					<caption>  Solicitação de Reconsideração</caption>
						<tr>
							<th width="15%"><b>Justificativa:</b></th>
							<td align="justify">			
								<h:outputText value="#{autorizacaoReconsideracao.obj.justificativaSolicitacao}"/>
							</td>
						  </tr>
					</table>
							
				</td>
			</tr>

			<tr>
				<td>
					<table class="subFormulario" width="100%">
					<caption>Parecer da PROGRAD</caption>
			
						<tr>
							<th><b>Autorizar Reconsideração:</b></th>
							<td>						
								<h:selectOneRadio
									value="#{autorizacaoReconsideracao.obj.autorizado}"	id="radioAutorizado">
									<f:selectItem itemLabel="Sim" itemValue="true" />
									<f:selectItem itemLabel="Não" itemValue="false" />
								</h:selectOneRadio>
								</b>
							</td>
						</tr>
			
			
						<tr>
						    <th width="15%"><b>Descrição do Parecer:</b></th>
							<td>							
								<h:inputTextarea value="#{autorizacaoReconsideracao.obj.parecerPrograd}" rows="10" style="width:95%"/>
							</td>
						</tr>
			
			
						<tfoot>
							<tr>
								<td colspan="2"><h:commandButton
									value="#{autorizacaoReconsideracao.confirmButton}"
									action="#{autorizacaoReconsideracao.autorizar}" /> <h:commandButton
									value="Cancelar" action="#{autorizacaoReconsideracao.cancelar}" /></td>
							</tr>
						</tfoot>
						</table>
						
				</td>
			</tr>
			
	</table>
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>