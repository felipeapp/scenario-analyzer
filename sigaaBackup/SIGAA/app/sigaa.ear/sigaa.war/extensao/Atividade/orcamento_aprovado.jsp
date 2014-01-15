<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<%@page import="br.ufrn.sigaa.projetos.dominio.FuncaoMembro"%>
<%@page import="br.ufrn.sigaa.extensao.dominio.TipoAtividadeExtensao"%>
<%@page import="br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto"%>

<f:view>
	<h2>Visualiza��o do Or�amento Concedido a A��o</h2>

<h:form id="form">

	<h3 class="tituloTabelaRelatorio">DADOS DA A��O DE EXTENS�O</h3>
	<table class="tabelaRelatorio" width="100%">
	<tbody>
		
		<%-- DADOS GERAIS, DE TODOS OS TIPOS DE A��O --%>
		

		<tr>
			<th><b> C�digo da A��o: </b> </th>
			<td><h:outputText value="#{atividadeExtensao.atividadeSelecionada.codigo}"/></td>
		</tr>

	
		<tr>
			<th><b> T�tulo  da A��o: </b> </th>
			<td><h:outputText value="#{atividadeExtensao.atividadeSelecionada.titulo}"/></td>
		</tr>
	
		<tr>
			<th><b> Tipo de A��o: </b> </th>
			<td> <h:outputText value="#{atividadeExtensao.atividadeSelecionada.tipoAtividadeExtensao.descricao}"/> </td>
		</tr>
	
	
		<tr>
			<th><b> Situa��o da A��o: </b> </th>
			<td>
				<font color="${(atividadeExtensao.atividadeSelecionada.situacaoProjeto.id eq EM_EXECUCAO)?'black':'red'}">
					<h:outputText value="#{atividadeExtensao.atividadeSelecionada.situacaoProjeto.descricao}"/>
				</font>
			</td>
		</tr>

		<tr>
			<th><b> Unidade Proponente:</b></th>
			<td><h:outputText value="#{atividadeExtensao.atividadeSelecionada.unidade.siglaNome}" /></td>
		</tr>

		<tr>
			<th><b> Unidade Or�ament�ria:</b></th>
			<td><h:outputText value="#{atividadeExtensao.atividadeSelecionada.projeto.unidadeOrcamentaria.siglaNome}" /></td>
		</tr>

		<tr>
			<th><b>Fonte de Financiamento:</b></th>
			<td>
				<h:outputText value="#{atividadeExtensao.atividadeSelecionada.fonteFinanciamentoString}"/>
			</td>
		</tr>
		
		<tr>
			<th><b>N� Bolsas Solicitadas:</b></th>
			<td>
				<h:outputText value="#{atividadeExtensao.atividadeSelecionada.bolsasSolicitadas}"/>
			</td>
		</tr>
		
		<tr>
			<th><b>N� Bolsas Concedidas:</b></th>
			<td>
				<h:outputText value="#{atividadeExtensao.atividadeSelecionada.bolsasConcedidas}"/>
			</td>
		</tr>


		<!-- OR�AMENTO EM_EXECUCAO  -->
		<c:if test="${not empty atividadeExtensao.atividadeSelecionada.orcamentosConsolidados}">
			<tr>
				<td colspan="2">
				
						<h3 style="text-align: center; background: #EFF3FA; font-size: 12px">Or�amento</h3>

							<t:dataTable id="dt_aprovado" value="#{atividadeExtensao.atividadeSelecionada.orcamentosConsolidados}" var="consolidacao"
							 align="center" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar" rowIndexVar="index" forceIdIndex="true">
										<t:column>
											<f:facet name="header"><f:verbatim>Descri��o</f:verbatim></f:facet>
											<h:outputText value="#{consolidacao.elementoDespesa.descricao}" />
										</t:column>

                                        <t:column>
                                            <f:facet name="header"><f:verbatim>Financ. Solicitado</f:verbatim></f:facet>
                                            <h:outputText value="#{consolidacao.fundo}"><f:convertNumber pattern="R$ #,##0.00"/></h:outputText>
                                        </t:column>

										<t:column>
											<f:facet name="header"><f:verbatim>Financ. Concedido</f:verbatim></f:facet>
											<h:outputText value="#{consolidacao.fundoConcedido}"><f:convertNumber pattern="R$ #,##0.00"/></h:outputText>
										</t:column>
						</t:dataTable>

				</td>
			</tr>	
		</c:if>
	
	</tbody>
</table>


</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>