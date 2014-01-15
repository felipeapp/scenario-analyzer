<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
<%@include file="/portais/docente/menu_docente.jsp"%>


	<h2> <ufrn:subSistema /> > Avaliacação da Ação de Extensão</h2>

	<h:form id="formAvaliacaoAtividade">
	
	
		<%@include file="/extensao/Atividade/include/dados_avaliar_atividade.jsp"%>
		
		<br/>
	
		<table class="formulario" width="100%">
			<caption class="listagem">OUTRAS AVALIAÇÕES</caption>
			<tr>
				<td><t:dataTable id="avaliacoes"
					value="#{avaliacaoAtividade.avaliacoesParciais}" var="aval"
					align="center" width="100%" styleClass="listagem"
					rowClasses="linhaPar, linhaImpar">
					<t:column>
						<f:facet name="header">
							<f:verbatim>Avaliador(a)</f:verbatim>
						</f:facet>
						<h:outputText
							value="#{aval.avaliadorAtividadeExtensao.servidor.pessoa.nome}" />
					</t:column>

					<t:column styleClass="centerAlign">
						<f:facet name="header">
							<f:verbatim>Parecer</f:verbatim>
						</f:facet>
						<h:outputText value="#{aval.parecer.descricao}"
							rendered="#{not empty aval.dataAvaliacao}" />
					</t:column>

					<t:column styleClass="centerAlign">
						<f:facet name="header">
							<f:verbatim>Nota</f:verbatim>
						</f:facet>
						<h:outputText value="#{aval.nota}"
							rendered="#{not empty aval.dataAvaliacao}" />
					</t:column>

					<t:column styleClass="centerAlign">
						<f:facet name="header">
							<f:verbatim>Data Avaliação</f:verbatim>
						</f:facet>
						<h:outputText value="#{aval.dataAvaliacao}" />
					</t:column>

					<t:column styleClass="centerAlign">
						<f:facet name="header">
							<f:verbatim>Situação</f:verbatim>
						</f:facet>
						<h:outputText value="#{aval.statusAvaliacao.descricao}" />
					</t:column>

					<t:column>
						<h:commandLink id="visualizaAvaliacaoCompleta" title="Visualiza Avaliação Completa"
							action="#{ avaliacaoAtividade.view }" immediate="true">
							<f:param name="idAvaliacao" value="#{aval.id}" />
							<h:graphicImage url="/img/extensao/document_chart.png" />
						</h:commandLink>
					</t:column>
				</t:dataTable></td>
			</tr>

			<c:if test="${empty avaliacaoAtividade.avaliacoesParciais}">
				<tr>
					<td colspan="6" align="center"><font color="red">Não há
					avaliadores analisando esta ação de extensão</font></td>
				</tr>
			</c:if>

		</table>


		<br />


		<table class="formulario" width="100%">

			<caption class="listagem">MINHA AVALIAÇÃO</caption>

			<tr>
				<th width="15%"><b>Avaliador: </b></th>
				<td><h:outputText
					value="#{avaliacaoAtividade.obj.membroComissao.servidor.pessoa.nome}" />
				</td>
			</tr>

			<tr>

				<th><b>Parecer:</b></th>
				<td><h:selectOneRadio id="rbParecer"
					value="#{avaliacaoAtividade.obj.parecer.id}">
					<f:selectItem itemValue="1" itemLabel="Aprovar Proposta" />
					<f:selectItem itemValue="3" itemLabel="NÃO Aprovar Proposta" />
					<f:selectItem itemValue="5" itemLabel="Devolver para Ajustes do Coordenador" />
				</h:selectOneRadio></td>
			</tr>

			<tr>
				<th><b>Bolsas Propostas: </b></th>
				<td><h:inputText id="bolsasPropostas"
					value="#{avaliacaoAtividade.obj.bolsasPropostas}" size="5" /></td>
			</tr>

			<tr>
				<th><b>Orçamento: </b></th>
				<td>

				<c:if
					test="${not empty avaliacaoAtividade.obj.orcamentoProposto}">


					<table class="listagem">
						<caption class="listagem">Orcamento Proposto</caption>
						<tr>
							<td><t:dataTable id="dtOrcamentoProposto"
								value="#{avaliacaoAtividade.obj.orcamentoProposto}"
								var="proposto" align="center" width="95%" styleClass="listagem"
								rowClasses="linhaPar, linhaImpar" rowIndexVar="index"
								forceIdIndex="true">
								<t:column>
									<f:facet name="header">
										<f:verbatim>Descrição</f:verbatim>
									</f:facet>
									<h:outputText value="#{proposto.elementoDespesa.descricao}" />
								</t:column>

								<t:column>
									<f:facet name="header">
										<f:verbatim>Valor Proposto (${siglasExtensaoMBean.siglaFundoExtensaoPadrao})</f:verbatim>
									</f:facet>
									<f:verbatim
										rendered="#{atividadeExtensao.obj.financiamentoInterno}">R$ </f:verbatim>
									<h:inputText value="#{proposto.valorProposto}"
										id="valorProposto" size="12"
										onkeypress="return(formataValor(this, event, 2))"
										rendered="#{atividadeExtensao.obj.financiamentoInterno}" style="text-align: right">
										<f:convertNumber pattern="#,##0.00" />
									</h:inputText>
									
									<h:outputText value="[Não solicitou financiamento interno]"
											rendered="#{!atividadeExtensao.obj.financiamentoInterno}"/>
								</t:column>
							</t:dataTable></td>
						</tr>

					</table>
				</c:if>
				<c:if	test="${empty avaliacaoAtividade.obj.orcamentoProposto}">
						<font color="red">Recursos não solicitados</font>	
				</c:if>
				 
				</td>
			</tr>

			<tr>
				<th><b>Justificativa:</b></th>
				<td><h:inputTextarea style="width: 98%" rows="4"
					id="justificativaAvaliacao"
					value="#{avaliacaoAtividade.obj.justificativa}" /></td>
			</tr>

			</tbody>

			<!-- BOTOES -->
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton id="btConfirmarAvaliacao" value="Confirmar Avaliação"	action="#{ avaliacaoAtividade.confirmarAvaliacaoMembroComite }" /> 
						<h:commandButton value="Cancelar" action="#{ avaliacaoAtividade.cancelar }" onclick="#{confirm}" />
					</td>
				</tr>
			</tfoot>

		</table>
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>