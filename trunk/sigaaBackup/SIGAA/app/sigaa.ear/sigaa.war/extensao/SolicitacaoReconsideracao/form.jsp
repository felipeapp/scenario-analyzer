<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<%@include file="/portais/docente/menu_docente.jsp" %>
	
	<h:outputText value="#{solicitacaoReconsideracao.create}" />
	
	<h2><ufrn:subSistema /> > Solicitação de Reconsideração de Ações de Extensão</h2>
	
	<div class="descricaoOperacao">
				<b> Senhor(a) Professor(a), </b> <br/><br/>	
				Após o envio dessa solicitação e aprovação da Pró-Reitoria de Extensão, esta proposta passará para a situação CADASTRO EM ANDAMENTO, permitindo que seja alterada e submetida novamente para nova análise da Pró-Reitoria.				
	</div>
	
	
	<table class="formulario" width="100%">
	<caption class="listagem"> Solicitação de Reconsideração de Ações de Extensão </caption>
	
	<c:if test="${not empty solicitacaoReconsideracao.acoesExtensaoReconsideraveis}">
	<tr>
    	<td>
			<h:form id="form">
			
				<h:inputHidden value="#{solicitacaoReconsideracao.confirmButton}" id="confirmButton"/>
				<h:inputHidden value="#{solicitacaoReconsideracao.obj.id}" id="idSolicitacao"/>
				<h:inputHidden value="#{solicitacaoReconsideracao.obj.atividade.id}" id="id"/>
		
				<tr>
					<td>
					 	<b>Ação de Extensão: </b><h:outputText value="#{solicitacaoReconsideracao.obj.atividade.anoTitulo}"/><br/>
					</td>
				</tr>
				
				<tr>
					<td>
					 	<b>Coordenação: </b><h:outputText value="#{solicitacaoReconsideracao.obj.atividade.coordenacao.pessoa.nome}"/><br/>
					</td>
				</tr>

				<tr>
					<td class="subFormulario">Lista de Avaliações</td>
				</tr>
		
				<tr>				
						<td>
							<t:dataTable value="#{ solicitacaoReconsideracao.obj.atividade.avaliacoes }" var="avaliacao" width="100%" rowIndexVar="index" forceIdIndex="true">
								<t:column>
									<h:outputText value="<b>Avaliação #{index+1}: </b>" escape="false"/>
									<f:verbatim><br/><b>Data da Avaliação: </b></f:verbatim><h:outputText value="#{avaliacao.dataAvaliacao}"><f:convertDateTime pattern="dd/MM/yyyy HH:mm:ss"/></h:outputText>
									<f:verbatim><br/><b>Parecer do Avaliador:</b><br/></f:verbatim>
									<h:outputText value="#{avaliacao.justificativa}"/>
									<f:verbatim></b><br/></f:verbatim>
								</t:column>
							</t:dataTable>
			 	   		</td>
				 </tr>
		
			<tr>
				<td class="subFormulario">Solicitação</td>
			</tr>
		
			 <tr>
				<td>
					<b>Justificativa do Pedido: </b><h:graphicImage url="/img/required.gif" style="vertical-align: top;"/><br/>
					<h:inputTextarea value="#{solicitacaoReconsideracao.obj.justificativa}" rows="5" style="width:98%" readonly="#{solicitacaoReconsideracao.readOnly}" id="justificativaPedido"/> 
				</td>
			</tr>
		
				<br/>
				<br/>
			
				<tfoot>
					<tr>
						<td colspan="2">
							<c:if test="${solicitacaoReconsideracao.confirmButton == 'Remover'}">
								<h:commandButton value="#{solicitacaoReconsideracao.confirmButton}" action="#{solicitacaoReconsideracao.remover}"/>
							</c:if>
		
							<c:if test="${solicitacaoReconsideracao.confirmButton != 'Remover'}">
								<h:commandButton value="Cadastrar" action="#{solicitacaoReconsideracao.cadastrarSolicitacaoExtensao}"/>
							</c:if>
		
							<h:commandButton value="Cancelar" action="#{solicitacaoReconsideracao.cancelar}" onclick="#{confirm}"/>
						</td>
					</tr>
				</tfoot>
			</h:form>
		</td>
	</tr>
	</c:if>

	<c:if test="${empty solicitacaoReconsideracao.acoesExtensaoReconsideraveis}">
		<tr>
			<td>
				 <br/>
		         <center><font color="red">Não há ações de extensão passivas de reconsideração <br/> ou o usuário atual não é coordenador de ações ativas</font></center>
		         <br/>
	        </td>
		</tr>
	</c:if>

	</table>
	
 <br />
 <center>
 <h:graphicImage url="/img/required.gif" style="vertical-align: top;"/>
 <span class="fontePequena"> Campos de preenchimento obrigatório. </span>
 </center>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>