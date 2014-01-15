<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<style>
	.colIcone{text-align: right !important;width: 1%;}
	.colPeriodo{text-align: center !important;width: 20%;}
	.footer{text-align: center;}
</style>

<f:view>
	<a4j:keepAlive beanName="inscricaoAtividadeAP"></a4j:keepAlive>
	<h2 class="title"><ufrn:subSistema />  
	<c:if test="${!inscricaoAtividadeAP.excluir}">
	> Inscri��o para Participa��o em Atividade > Selecionar Participante
	</c:if>
	<c:if test="${inscricaoAtividadeAP.excluir}">
	> Remover Participa��o de Atividade > Selecionar Participante
	</c:if>
	</h2>

	<h:form id="formSelecionaDocente" prependId="true">

		<div class="descricaoOperacao">
			<p>Caro usu�rio,</p>
			<c:if test="${!inscricaoAtividadeAP.excluir}">
				<p>Para realizar uma inscri��o retroativa, por favor selecione um participante, preenchendo o campo AUTOCOMPLETAR abaixo.</p>
			</c:if>
			<c:if test="${!inscricaoAtividadeAP.excluir}">
				<p>Para realizar remo��o de um participantes de alguma atividade, por favor selecione um participante, preenchendo o campo AUTOCOMPLETAR abaixo.</p>
			</c:if>
			<p>Ap�s selecionar o participante, pressione o bot�o "Pr�ximo >>".</p>
		</div>
		
		
		<table class="listagem"  width="80%" >
			<caption>Selecionar Participante</caption>
			<tbody>
				<tr>
					<th class="required">Participante:</th>
					<td>
						<h:inputText value="#{inscricaoAtividadeAP.nomeDocente}" id="nomeServidor" size="65"/>
						<rich:suggestionbox for="nomeServidor" width="450" height="100" minChars="3" id="suggestionNomeServidor" 
								frequency="0" selfRendered="true" requestDelay="200" 
								suggestionAction="#{servidorAutoCompleteMBean.autocompleteNomeServidor}" var="_servidor" 
								fetchValue="#{_servidor.pessoa.nome}">	
								<h:column>
									<h:outputText value="#{_servidor.pessoa.nome}" />
								</h:column>							        
							        <f:param name="apenasAtivos" value="#{true}" />
							        <f:param name="categorias" value="#{inscricaoAtividadeAP.categoriasParticipantes}" />
							        <a4j:support event="onselect" reRender="formSelecionaDocente" 
							        	actionListener="#{inscricaoAtividadeAP.carregarDocente}" >
										<f:param name="apenasAtivos" value="#{true}" />
										<f:attribute name="idServidor" value="#{_servidor.id}"  />
									</a4j:support>
						</rich:suggestionbox>
					</td>
				</tr>
			</tbody>
			
			<tfoot>
				<tr>
					<td colspan="2" align="center">
						<h:commandButton value="Cancelar" action="#{inscricaoAtividadeAP.cancelar}" 
								 onclick="#{confirm}"	immediate="true" title="Cancelar"/>
						<h:commandButton value="Pr�ximo >>" action="#{inscricaoAtividadeAP.submeterDocente}" rendered="#{!inscricaoAtividadeAP.excluir}" title="Pr�ximo"/>
						<h:commandButton value="Pr�ximo >>" action="#{inscricaoAtividadeAP.removerDocente}" rendered="#{inscricaoAtividadeAP.excluir}" title="Pr�ximo"/>
					</td>
				</tr>				 
			</tfoot>
			
		</table>
		
	</h:form>
	
	<%@include file="/WEB-INF/jsp/include/_campos_obrigatorios.jsp"%>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
