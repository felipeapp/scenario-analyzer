<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
	<%@include file="/portais/docente/menu_docente.jsp"%>

	<h2><ufrn:subSistema /> > Avaliaca��o da A��o de Extens�o</h2>

	<h:form>


	<%@include file="/extensao/Atividade/include/dados_avaliar_atividade.jsp"%>

		
	<br/>


	<table class="formulario" width="100%">
			<caption class="listagem">OUTRAS AVALIA��ES</caption>
			<tr>
				<td><t:dataTable
					value="#{avaliacaoAtividade.avaliacoesParciais}" var="aval"
					align="center" width="100%" styleClass="listagem"
					rowClasses="linhaPar, linhaImpar">
					<t:column>
						<f:facet name="header">
							<f:verbatim>Avaliador(a)</f:verbatim>
						</f:facet>
						<h:outputText value="#{aval.avaliador.pessoa.nome}" />
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
							<f:verbatim>Tipo de Avaliador</f:verbatim>
						</f:facet>
						<h:outputText value="#{aval.tipoAvaliacao.descricao}" />
					</t:column>
					
					<t:column styleClass="centerAlign">
						<f:facet name="header">
							<f:verbatim>M�dia</f:verbatim>
						</f:facet>
						<h:outputText value="#{aval.nota}" rendered="#{aval.avaliacaoParecerista}">
							<f:convertNumber pattern="#,##0.00" />
						</h:outputText>
						<h:outputText value="-" rendered="#{aval.avaliacaoComite}" />
					</t:column>					

					<t:column styleClass="centerAlign">
						<f:facet name="header">
							<f:verbatim>Situa��o</f:verbatim>
						</f:facet>
						<h:outputText value="#{aval.statusAvaliacao.descricao}" />
					</t:column>

					<t:column>
						<h:commandLink title="Visualiza Avalia��o Completa" id="lnkViewAvaliacao" action="#{ avaliacaoAtividade.view }" immediate="true">
							<f:param name="idAvaliacao" value="#{aval.id}" />
							<h:graphicImage url="/img/extensao/document_chart.png" />
						</h:commandLink>
					</t:column>

				</t:dataTable></td>
			</tr>

			<c:if test="${empty avaliacaoAtividade.avaliacoesParciais}">
				<tr>
					<td colspan="6" align="center"><font color="red">N�o h�
					avaliadores analisando esta a��o de extens�o</font></td>
				</tr>
			</c:if>

		</table>


		<br />
		
		<table class="formulario" width="100%">

			<caption class="listagem">MINHA AVALIA��O</caption>

			<tr>
				<th width="20%"><b>Avaliador: </b></th>
				<td><h:outputText
					value="#{avaliacaoAtividade.obj.membroComissao.servidor.pessoa.nome}" />
				</td>
			</tr>

			<tr>
				<th><b>Or�amento: </b></th>
				<td>
					<c:if test="${not empty avaliacaoAtividade.obj.orcamentoProposto}">

						<table class="listagem">
							<caption class="listagem">Orcamento Aprovado</caption>
							<tr>
								<td><t:dataTable id="orcamentoAprovado"
									value="#{avaliacaoAtividade.obj.orcamentoProposto}"
									var="proposto" align="center" width="95%" styleClass="listagem"
									rowClasses="linhaPar, linhaImpar" rowIndexVar="index"
									forceIdIndex="true">
									<t:column>
										<f:facet name="header">
											<f:verbatim>Descri��o</f:verbatim>
										</f:facet>
										<h:outputText value="#{proposto.elementoDespesa.descricao}" />
										<span class="obrigatorio" />
									</t:column>
	
									<t:column>
										<f:facet name="header">
											<f:verbatim>Valor Aprovado (FAEx)</f:verbatim>
										</f:facet>
										<f:verbatim
											rendered="#{atividadeExtensao.obj.financiamentoInterno}">R$ </f:verbatim>
										<h:inputText value="#{proposto.valorProposto}"
											id="valorProposto" size="12" title="Valor proposto" label="Valor Proposto"
											onkeypress="return(formataValor(this, event, 2))"
											rendered="#{atividadeExtensao.obj.financiamentoInterno}"
											style="text-align: right">
											<f:convertNumber pattern="#,##0.00" />
										</h:inputText>
										<h:outputText value="[N�o solicitou financiamento interno]"
											rendered="#{!atividadeExtensao.obj.financiamentoInterno}"/>
									</t:column>
								</t:dataTable></td>
							</tr>
	
						</table>

					</c:if>
					<c:if test="${empty avaliacaoAtividade.obj.orcamentoProposto}">
						[N�o solicitou financiamento interno]
					</c:if>			
				
				
				</td>
			</tr>

	
			<tr>
				<th class="obrigatorio"><b>Bolsas Aprovadas: </b></th>
				<td>
				<h:inputText id="bolsasAprovadas" title="Bolsas Aprovadas" label="Bolsas Aprovadas" value="#{avaliacaoAtividade.obj.bolsasPropostas}" size="5" rendered="#{atividadeExtensao.obj.financiamentoInterno}"/>
				<h:outputText value="[N�o solicitou financiamento interno]"	rendered="#{!atividadeExtensao.obj.financiamentoInterno}"/>
				</td>
			</tr>



			<tr>

				<th><b>Parecer:</b></th>
				<td><h:selectOneRadio id="radParecer"
					value="#{avaliacaoAtividade.obj.parecer.id}">
					<f:selectItem itemValue="1" itemLabel="Aprovar proposta" />
					<f:selectItem itemValue="2" itemLabel="Aprovar com recomenda��o"/>
					<f:selectItem itemValue="3" itemLabel="N�O aprovar proposta" />
					
				</h:selectOneRadio></td>
			</tr>


			<tr>
				<th><b>Justificativa:</b></th>
				<td><h:inputTextarea style="width: 98%" rows="4"
					id="txtJustificativa"
					value="#{avaliacaoAtividade.obj.justificativa}" /></td>
			</tr>


			<!-- BOTOES -->
			<tfoot>
				<tr>
					<td colspan="2">
					   <h:commandButton value="Confirmar Avalia��o" id="btnAvaliar"	action="#{ avaliacaoAtividade.confirmarAvaliacaoPresidenteComite }" /> 
					   <h:commandButton value="Cancelar" id="btnCancelar" action="#{ avaliacaoAtividade.cancelar }" onclick="#{confirm}"/>
					</td>
				</tr>
			</tfoot>

		</table>
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>