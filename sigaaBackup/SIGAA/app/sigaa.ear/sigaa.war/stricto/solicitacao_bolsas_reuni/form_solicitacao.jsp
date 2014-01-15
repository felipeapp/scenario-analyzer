<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<a4j:keepAlive beanName="solicitacaoBolsasReuniBean" />
		
	<h2> <ufrn:subSistema /> &gt; Solicitação de Bolsas REUNI de Assistência ao Ensino </h2>
	
	<div class="descricaoOperacao">
		<p>
			<b> Caro Coordenador, </b>
		</p> 	
		<p>
			Através deste formulário será possível realizar as solicitações de bolsa REUNI de assistência ao ensino.
			Cada solicitação será caracterizada por um conjunto de planos de trabalho, referentes a cada bolsa pleiteada.
		</p>
		<p>
			A cada novo plano adicionado será possível salvar os dados já cadastrados para posterior edição através da opção 
			'Salvar e Continuar'. Entretanto, ao finalizar o cadastro dos planos é necessário, para efetivar sua solicitação, 
			selecionar a opção 'Confirmar Solicitação'.  
		</p> 	
	</div>
	
	<h:form>

		<div class="infoAltRem">
			<c:if test="${planoTrabalhoReuniBean.portalCoordenadorStricto}">
				<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>
				<h:commandLink action="#{planoTrabalhoReuniBean.iniciarCadastro}" value="Adicionar um novo plano de trabalho" id="btAddPlanoTrab"/>
			</c:if>
	        <h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar plano de trabalho
	        <h:graphicImage value="/img/alterar.gif" style="overflow: visible;"/>: Alterar plano de trabalho
	        <h:graphicImage value="/img/garbage.png" style="overflow: visible;"/>: Remover plano de trabalho
		</div>
	
		<table class="visualizacao" style="width: 100%;">
			<caption>Resumo da Solicitação</caption>
			<%@include file="_cabecalho_solicitacao.jsp"%>
		</table>
		
		<table class="formulario" style="width: 100%;">
			<thead>
				<tr>
					<td colspan="8" class="subFormulario"> Planos de trabalho vinculados </td>
				</tr>
				
				<c:if test="${not empty solicitacaoBolsasReuniBean.obj.planos}">
				<tr>
					<td> Categoria </td>
					<td> Nível </td>
					<td> Detalhes </td>
					<td> Alunos beneficiados </td>
					<td  colspan="4" width="3%"> </td>
				</tr>
				</c:if>
			</thead>
			<tbody>
				<a4j:repeat value="#{solicitacaoBolsasReuniBean.planosDataModel}" var="_plano" rowKeyVar="rowKey">
				<tr class="<h:outputText value="#{rowKey % 2 == 0 ? 'linhaPar' : 'linhaImpar'}"/>">
					 <%@include file="_colunas_plano_trabalho.jsp"%>
					<td> 
						<h:commandButton id="alterarPlano" image="/img/alterar.gif" title="Alterar Plano de Trabalho" 
							action="#{planoTrabalhoReuniBean.iniciarAlteracao}" styleClass="noborder">
							<f:setPropertyActionListener value="#{_plano}" target="#{planoTrabalhoReuniBean.obj}"/>
						</h:commandButton>
					</td>
					<td> 
						<h:commandButton id="removerPlano" image="/img/garbage.png" title="Remover Plano de Trabalho" onclick="#{confirmDelete}" 
							action="#{solicitacaoBolsasReuniBean.removerPlanoTrabalho}" styleClass="noborder"/>
					</td>					
				</tr>	
				</a4j:repeat>

				<c:if test="${empty solicitacaoBolsasReuniBean.obj.planos}">
					<tr>
						<td colspan="8" style="text-align: center; color: #922; padding: 1em; font-weight: bold;"> 
							Nenhum plano de trabalho foi associado a esta solicitação.
						</td>
					</tr>
				</c:if>				
				
			</tbody>
			<tfoot>
				<tr>
					<td colspan="8">
					    <h:commandButton id="btSalvar" value="Salvar e Continuar" action="#{solicitacaoBolsasReuniBean.salvar}" rendered="#{planoTrabalhoReuniBean.portalCoordenadorStricto}"/>
						<h:commandButton id="btConfirmar" value="Confirmar Solicitação" action="#{solicitacaoBolsasReuniBean.submeterProposta}"/> 
						<h:commandButton id="btCancelar" value="Cancelar" action="#{solicitacaoBolsasReuniBean.cancelar}" immediate="true"/>
					</td> 
				</tr>
			</tfoot>
		</table>
	</h:form>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
	