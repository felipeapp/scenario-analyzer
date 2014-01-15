<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
	<h2> <ufrn:subSistema /> &gt; Notificação de Invenção &gt; Participação Institucional</h2>

	<h:form id="form">

		<table class="formulario" width="90%">
			<caption>Participações</caption>
			
			<tr>
				<td class="subFormulario" colspan="2"> Participação Institucional </td>
			</tr>
			
			<tr>
				<th  class="required"> Entidade Financiadora:</th>
				<td>
					<h:selectOneMenu id="entidade" value="#{invencao.financiamento.entidadeFinanciadora.id}" >
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
						<f:selectItems value="#{entidadeFinanciadora.allCombo}"/>
					</h:selectOneMenu>
				</td>
			</tr>
			
			<tr>
				<td colspan="2">
					<p style="font-style: italic; text-align: center; background: #F5F5F5">
			        Caso não exista a entidade financiadora desejada, solicite o cadastro junto à Pró-Reitoria de Pesquisa.
			        </p>
			    </td>
			</tr>
			
			<tr>
				<th  class="required"> Há co-titularidade?</th>
				<td>
					<h:selectOneRadio value="#{invencao.financiamento.cotitularidade}">
						<f:selectItems value="#{tipoBolsaPesquisa.simNao}"/>
					</h:selectOneRadio>
				</td>
			</tr>
			
			<tr>
				<th>Número do Processo do Convênio:</th>
				<td>
					<h:inputText id="numeroProcessoConvenio" value="#{invencao.financiamento.numeroProcessoConvenio}" size="20" maxlength="20" onkeyup="formatarInteiro(this);"/>
				</td>
			</tr>
		
			<tr>
				<td colspan="2" align="center">
					<br/>
					<h:commandButton action="#{invencao.adicionarFinanciamento}" value="Adicionar" id="btAdicionar" />
					<br/>
				</td>
			</tr>
			
			<c:if test="${not empty invencao.obj.financiamentos}">
				<tr>
					<td colspan="2">
						<div class="infoAltRem">
							<h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Financiamento
						</div>
					</td>
				</tr>
				<tr>
					<td colspan="2" class="subFormulario">	Lista de Financiamentos </td>
				</tr>
			
				<tr>
					<td colspan="2">
						<input type="hidden" value="0" id="idFinanciamentoInvencao" name="idFinanciamentoInvencao"/>
			
						<t:dataTable id="dataTableFinanciamentos" value="#{invencao.obj.financiamentos}" var="financiamento" align="center" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar">
			
							<t:column >
								<f:facet name="header"><f:verbatim>Entidade Financiadora</f:verbatim></f:facet>
								<h:outputText value="#{financiamento.entidadeFinanciadora.nome}" />
							</t:column>
							
							<t:column >
								<f:facet name="header"><f:verbatim>Número do Processo do Convênio</f:verbatim></f:facet>
								<h:outputText value="#{financiamento.numeroProcessoConvenio}"/>
							</t:column>
			
							<t:column>
								<h:commandButton image="/img/delete.gif" action="#{invencao.removerFinanciamento}"
									title="Remover Financiamento"  alt="Remover Financiamento"   
									onclick="$(idFinanciamentoInvencao).value=#{financiamento.id}; return confirm('Deseja Remover este Financiamento da Invenção?')" id="remFinanciamento" />
							</t:column>
			
						</t:dataTable>
					</td>
				</tr>
			</c:if>
			
			<tfoot>
			<tr>
				<td colspan="4">
					<h:panelGroup id="botoes">
						<h:commandButton value="<< Voltar" action="#{invencao.telaPropriedadeTerceiros}" />
						<h:commandButton value="Cancelar" action="#{invencao.cancelar}" onclick="#{confirm}" />
						<h:commandButton value="Avançar >>" action="#{invencao.submeterFinanciamentos}" />
					</h:panelGroup>
				</td>
			</tr>
			</tfoot>
		</table>
		
	<br/>
	<div class="obrigatorio"> Campos de preenchimento obrigatório. </div>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
