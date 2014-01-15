<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	
	<h2><ufrn:subSistema /> > Analisar Solicita��o de Reconsidera��o</h2>
	
	<h:form id="form">	
	
	<h:inputHidden id="idSolicitacaoReconsideracao" value="#{solicitacaoReconsideracao.obj.id}"/>
	
	<table class="formulario" width="100%">	
	<caption class="listagem"> Analisando Solicita��o de Reconsidera��o </caption>

	<tr>
		<th><b>A��o:</b></th>
		<td><h:outputText value="#{solicitacaoReconsideracao.obj.atividade.anoTitulo}"/></td>
	</tr>

	<tr>
		<th><b>Tipo:</b> </th>
		<td><h:outputText value="#{solicitacaoReconsideracao.obj.atividade.tipoAtividadeExtensao.descricao}"/></td>
	</tr>

    <tr>
        <th><b>Coordena��o:</b> </th>
        <td><h:outputText value="#{solicitacaoReconsideracao.obj.atividade.projeto.coordenador.pessoa.nome}"/></td>
    </tr>

	<tr>
		<th><b>Unidade Proponente:</b></th>
		<td><h:outputText value="#{solicitacaoReconsideracao.obj.atividade.unidade.nome}"/></td>
	</tr>

	<tr>
		<th><b>�rea Principal:</b></th>
		<td><h:outputText value="#{solicitacaoReconsideracao.obj.atividade.areaTematicaPrincipal.descricao}"/></td>
	</tr>


	<tr>
		<th><b> Dados completos:</b></th>
		<td><h:commandLink title="Visualizar A��o de Extens�o"
			action="#{ atividadeExtensao.view }" immediate="true" id="BtnViewDadosCompletosAcao">
			<f:param name="id" value="#{solicitacaoReconsideracao.obj.atividade.id}" />
			<h:graphicImage url="/img/view.gif" />
		</h:commandLink></td>
	</tr>




	<tr>
		<td colspan="2" class="subFormulario">Solicita��o</td>
	</tr>	



	<tr>
		<th width="15%"><b>Submiss�o:</b></th>
		<td><fmt:formatDate value="${solicitacaoReconsideracao.obj.dataSolicitacao}" pattern="dd/MM/yyyy HH:mm:ss" /> </td>
	</tr>


	<tr>
		<th><b>Justificativa:</b></th>
		<td>
			<c:if test="${not empty solicitacaoReconsideracao.obj.justificativa}">
				<ufrn:format type="texto" name="solicitacaoReconsideracao" property="obj.justificativa" />
			</c:if>		
		</td>
	</tr>

	<tr>
		<td colspan="2" class="subFormulario">Parecer</td>
	</tr>	


	<tr>
		<th><b>Avalia��es:</b></th>
		<td>
		<table class="formulario" width="100%">
				<tr>
					<td>
						<t:dataTable id="avaliacoes"
							value="#{solicitacaoReconsideracao.obj.atividade.avaliacoes}" 
							var="aval" align="center" width="100%" styleClass="listagem"
							rowClasses="linhaPar, linhaImpar">
							
							<t:column>
								<f:facet name="header">
									<f:verbatim>Avaliador(a)</f:verbatim>
								</f:facet>
								<h:outputText value="#{aval.avaliador.pessoa.nome}" />
							</t:column>
		
							<t:column styleClass="centerAlign">
								<f:facet name="header">
									<f:verbatim>Situa��o</f:verbatim>
								</f:facet>
								<h:outputText value="#{aval.parecer.descricao}"	rendered="#{not empty aval.dataAvaliacao}" />
								<h:outputText value="<font color='red'>N�O AVALIADO</font>"	rendered="#{empty aval.dataAvaliacao}" escape="false"/>	
							</t:column>
		
							<t:column styleClass="centerAlign">
								<f:facet name="header">
									<f:verbatim>Data Avalia��o</f:verbatim>
								</f:facet>
								<h:outputText value="#{aval.dataAvaliacao}" />
							</t:column>
		
							<t:column>
								<h:commandLink title="Visualizar Avalia��o"
									action="#{ avaliacaoAtividade.view }" id="LnkViewAvaliacao">
									<f:param name="idAvaliacao" value="#{aval.id}" />
									<h:graphicImage url="/img/extensao/document_chart.png" />
								</h:commandLink>
							</t:column>
							
						</t:dataTable>
					</td>
				</tr>
	
				<c:if test="${empty solicitacaoReconsideracao.obj.atividade.avaliacoes}">
					<tr>
						<td colspan="6" align="center"><font color="red">N�o h�	avaliadores analisando esta a��o de extens�o.</font></td>
					</tr>
				</c:if>
	
			</table>

		</td>
	</tr>

	
	<tr>
		<th><b>Situa��o:</b></th>
		<td><h:selectOneRadio
			value="#{solicitacaoReconsideracao.obj.aprovado}" id="OptAprovarSolicitacao">
			<f:selectItem itemValue="true" itemLabel="Aprovar solicita��o" />
			<f:selectItem itemValue="false" itemLabel="N�O aprovar solicita��o" />
		</h:selectOneRadio></td>
	</tr>


	<tr>
		<th class="obrigatorio"><b>Parecer:</b></th>
		<td><h:inputTextarea style="width: 98%" rows="4"
			id="justificativaAvaliacao"
			value="#{solicitacaoReconsideracao.obj.parecer}" /></td>
	</tr>

	<tfoot>
		<tr>
			<td colspan="2">			
				<h:commandButton value="Confirmar" action="#{solicitacaoReconsideracao.analisarSolicitacaoExtensao}" rendered="#{acesso.extensao}" id="BtnConfirmar"/>
				<h:commandButton value="<< Voltar" action="#{solicitacaoReconsideracao.listarSolicitacoesPendentesExtensao}" id="BtnVoltar"/>
				<h:commandButton value="Cancelar" action="#{solicitacaoReconsideracao.cancelar}" onclick="#{confirm}" id="BtnCancelar"/>
			</td>
		</tr>
	</tfoot>

	</table>
	<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>