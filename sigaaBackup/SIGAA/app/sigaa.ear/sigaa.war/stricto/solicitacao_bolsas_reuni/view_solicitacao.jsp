<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<a4j:keepAlive beanName="solicitacaoBolsasReuniBean" />
	<h2> <ufrn:subSistema /> &gt; Solicitação de Bolsas REUNI de Assistência ao Ensino </h2>

	<div class="infoAltRem" style="width:90%">
        <h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Plano de Trabalho
        <c:if test="${planoTrabalhoReuniBean.portalPpg}">        
        	<h:graphicImage value="/img/check.png"/>/<h:graphicImage value="/img/check_cinza.png" style="margin-left:0"/>: Aprovar/Reprovar Plano de Trabalho
        	<h:graphicImage value="/img/alterar.gif" style="overflow: visible;"/>: Alterar Plano de Trabalho<br/>          
        </c:if>
        <c:if test="${(planoTrabalhoReuniBean.portalCoordenadorStricto || planoTrabalhoReuniBean.portalPpg) && solicitacaoBolsasReuniBean.obj.submetida}">
        	<h:graphicImage value="/img/pesquisa/indicar_bolsista.gif" style="overflow: visible;"/>: Indicar Bolsista
        </c:if>
        <c:if test="${(planoTrabalhoReuniBean.portalPpg) && solicitacaoBolsasReuniBean.obj.submetida}">	
        	<h:graphicImage value="/img/user_edit.png" style="overflow: visible;"/>: Alterar Indicação        	
        </c:if>
		<h:graphicImage value="/img/view2.gif" style="overflow: visible;"/>: Visualizar Indicações      
	</div>


	<h:form prependId="false"> 
	<table class="visualizacao" style="width: 100%">
		<caption> Dados da Solicitação </caption>
		<%@include file="_cabecalho_solicitacao.jsp"%>
		<tr>
			<th> Status da Solicitação: </th>
			<td> ${ solicitacaoBolsasReuniBean.obj.descricaoStatus } </td>
		</tr>
	</table>
	
	<table class="listagem">
		<thead>
		<tr>
			<td colspan="11" class="subFormulario" style="text-align: center"> 
				Planos de Trabalho
			</td>
		</tr>
		<tr>
			<td> Categoria </td>
			<td> Nível </td>
			<td> Detalhes </td>
			<td style="text-align: right;"> Alunos beneficiados </td>
			<td> Status </td>
			<td colspan="6"></td>
		</tr>
		<tbody>
		<c:forEach items="#{solicitacaoBolsasReuniBean.obj.planos}" var="_plano" varStatus="rowKey">
			<tr class="<h:outputText value="#{rowKey % 2 == 0 ? 'linhaPar' : 'linhaImpar'}"/>">
				<%@include file="_colunas_plano_trabalho.jsp"%>
				<c:if test="${planoTrabalhoReuniBean.portalPpg}">
					<td style="text-align: right;"> 
						<h:commandButton id="btAprovar" image="/img/check#{not _plano.aprovado ? '_cinza' : ''}.png" title="#{_plano.aprovado ? 'Reprovar' : 'Aprovar'} Plano de Trabalho"
							action="#{planoTrabalhoReuniBean.aprovarReprovar}" styleClass="noborder">
							<f:setPropertyActionListener value="#{_plano}" target="#{planoTrabalhoReuniBean.obj}"/>
						</h:commandButton>
					</td>
					<c:if test="${solicitacaoBolsasReuniBean.obj.submetida}">
						<td>
							<h:commandButton id="alterarPlano" image="/img/alterar.gif" title="Alterar Plano de Trabalho"
								action="#{planoTrabalhoReuniBean.iniciarAlteracao}" styleClass="noborder">
								<f:setPropertyActionListener value="#{_plano}" target="#{planoTrabalhoReuniBean.obj}"/>
							</h:commandButton>					
						</td>
					</c:if>											
				</c:if>					
				<c:if test="${(planoTrabalhoReuniBean.portalPpg || planoTrabalhoReuniBean.portalCoordenadorStricto) && _plano.aprovado && solicitacaoBolsasReuniBean.obj.submetida}">
					<td style="text-align: right;">
						<h:commandButton id="btIndicarBolsista" image="/img/pesquisa/indicar_bolsista.gif" title="Indicar Bolsista"
							action="#{indicacaoBolsistaReuni.iniciarCadastro}" styleClass="noborder">
							<f:setPropertyActionListener value="#{_plano}" target="#{indicacaoBolsistaReuni.planoTrabalhoReuni}"/>
						</h:commandButton>
					</td>
				</c:if>					
				<c:if test="${(planoTrabalhoReuniBean.portalPpg) && _plano.aprovado && solicitacaoBolsasReuniBean.obj.submetida}">	
					<td style="text-align: right;">
						<h:commandButton id="btAltarerIndicacao" image="/img/user_edit.png" title="Alterar Indicação"
							action="#{indicacaoBolsistaReuni.alterar}" styleClass="noborder">
							<f:setPropertyActionListener value="#{_plano}" target="#{indicacaoBolsistaReuni.planoTrabalhoReuni}"/>
						</h:commandButton>
					</td>										
				</c:if>
				<c:if test="${_plano.aprovado && solicitacaoBolsasReuniBean.obj.submetida}">
					<td style="text-align: right;">
						<h:commandButton id="btVisualizarIndicacao" image="/img/view2.gif" title="Visualizar Indicações"
							action="#{planoDocenciaAssistidaMBean.iniciar}" styleClass="noborder">
							<f:setPropertyActionListener value="#{_plano}" target="#{planoDocenciaAssistidaMBean.planoTrabalhoReuni}"/>
						</h:commandButton>
					</td>					
				</c:if>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	</h:form>
	
	
	<c:if test="${not solicitacaoBolsasReuniBean.readOnly}">
	<div class="voltar">
		<a href="javascript:history.back();"> Voltar </a>
	</div>
	</c:if>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
	