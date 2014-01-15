<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<%@include file="/portais/docente/menu_docente.jsp" %>
	
	<h:outputText value="#{solicitacaoReconsideracao.create}" />
	
	<h2><ufrn:subSistema /> > Solicita��o de Reconsidera��o de A��es de Extens�o</h2>
	
	<div class="descricaoOperacao">
				<b> Senhor(a) Professor(a), </b> <br/><br/>	
				Ap�s o envio dessa solicita��o e aprova��o da Pr�-Reitoria de Extens�o, esta proposta passar� para a situa��o CADASTRO EM ANDAMENTO, permitindo que seja alterada e submetida novamente para nova an�lise da Pr�-Reitoria.				
	</div>
	
	
	<table class="formulario" width="100%">
	<caption class="listagem"> Solicita��o de Reconsidera��o de A��es de Extens�o </caption>
	
	<c:if test="${not empty solicitacaoReconsideracao.acoesExtensaoReconsideraveis}">
	<tr>
    	<td>
			<h:form id="form">
			
				<h:inputHidden value="#{solicitacaoReconsideracao.confirmButton}" id="confirmButton"/>
				<h:inputHidden value="#{solicitacaoReconsideracao.obj.id}" id="idSolicitacao"/>
				<h:inputHidden value="#{solicitacaoReconsideracao.obj.atividade.id}" id="id"/>
		
				<tr>
					<td>
					 	<b>A��o de Extens�o: </b><h:outputText value="#{solicitacaoReconsideracao.obj.atividade.anoTitulo}"/><br/>
					</td>
				</tr>
				
				<tr>
					<td>
					 	<b>Coordena��o: </b><h:outputText value="#{solicitacaoReconsideracao.obj.atividade.coordenacao.pessoa.nome}"/><br/>
					</td>
				</tr>

				<tr>
					<td class="subFormulario">Lista de Avalia��es</td>
				</tr>
		
				<tr>				
						<td>
							<t:dataTable value="#{ solicitacaoReconsideracao.obj.atividade.avaliacoes }" var="avaliacao" width="100%" rowIndexVar="index" forceIdIndex="true">
								<t:column>
									<h:outputText value="<b>Avalia��o #{index+1}: </b>" escape="false"/>
									<f:verbatim><br/><b>Data da Avalia��o: </b></f:verbatim><h:outputText value="#{avaliacao.dataAvaliacao}"><f:convertDateTime pattern="dd/MM/yyyy HH:mm:ss"/></h:outputText>
									<f:verbatim><br/><b>Parecer do Avaliador:</b><br/></f:verbatim>
									<h:outputText value="#{avaliacao.justificativa}"/>
									<f:verbatim></b><br/></f:verbatim>
								</t:column>
							</t:dataTable>
			 	   		</td>
				 </tr>
		
			<tr>
				<td class="subFormulario">Solicita��o</td>
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
		         <center><font color="red">N�o h� a��es de extens�o passivas de reconsidera��o <br/> ou o usu�rio atual n�o � coordenador de a��es ativas</font></center>
		         <br/>
	        </td>
		</tr>
	</c:if>

	</table>
	
 <br />
 <center>
 <h:graphicImage url="/img/required.gif" style="vertical-align: top;"/>
 <span class="fontePequena"> Campos de preenchimento obrigat�rio. </span>
 </center>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>