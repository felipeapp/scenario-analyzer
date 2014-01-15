<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> > Autoriza��o para Solicita��es de Reconsidera��es para Requisitos Formais da Proposta de Projeto</h2>

		<br/>
		<h:form id="relatorio">

			<h:inputHidden value="#{autorizacaoReconsideracao.obj.id}" />
			<h:inputHidden value="#{autorizacaoReconsideracao.obj.projetoEnsino.id}" />

				
			<table class="formulario" width="95%">
				<caption class="listagem">An�lise do pedido de reconsidera��o por requisitos formais</caption>
	

			<tr>
				<td colspan="2">
				
					<table class="subFormulario" width="100%">
					   <caption>Detalhes do Projeto de Ensino</caption>

						<tr>
							<th width="15%"><b>Ano - T�tulo:</b></th>
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
					<caption>  Solicita��o de Reconsidera��o</caption>
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
							<th><b>Autorizar Reconsidera��o:</b></th>
							<td>						
								<h:selectOneRadio
									value="#{autorizacaoReconsideracao.obj.autorizado}"	id="radioAutorizado">
									<f:selectItem itemLabel="Sim" itemValue="true" />
									<f:selectItem itemLabel="N�o" itemValue="false" />
								</h:selectOneRadio>
								</b>
							</td>
						</tr>
			
			
						<tr>
						    <th width="15%"><b>Descri��o do Parecer:</b></th>
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