<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> &gt; Lista de Apresentações de Resumos do CIC para Avaliação</h2>
	<h:outputText value="#{avaliacaoApresentacaoResumoBean.create}" />
	<h:form id="form">
		<table class="formulario" width="100%">
			<caption>Busca das Avaliações de Trabalhos do CIC</caption>
			<tr>
				<td width="3%"><h:selectBooleanCheckbox value="#{avaliacaoApresentacaoResumoBean.filtroAvaliador}" id="checkAvaliador" styleClass="noborder"/> </td>
				<td width="22%"><label for="checkAvaliador" onclick="$('form:checkAvaliador').checked = !$('form:checkAvaliador').checked;">Avaliador:</label></td>
				<td>
					<a4j:region id="avaliadorReg">
						<h:inputText id="avaliador" value="#{avaliacaoApresentacaoResumoBean.nomeAvaliador}" size="80" maxlength="150" onfocus="$('form:checkAvaliador').checked = true;"/>
						<rich:suggestionbox id="suggestionAvaliador"  width="400" height="100" minChars="3" 
						    for="avaliador" suggestionAction="#{avaliadorCIC.autoComplete}" 
						    var="_avaliador" fetchValue="#{_avaliador.docente.siapeNome}"
						    onsubmit="$('indicatorAvaliador').style.display='inline';" 
					      	oncomplete="$('indicatorAvaliador').style.display='none';">
					      	<h:column>
						      	<h:outputText value="#{_avaliador.docente.siapeNome}"/>
					      	</h:column>
					      	<a4j:support event="onselect">
								<f:setPropertyActionListener value="#{_avaliador.id}" target="#{avaliacaoApresentacaoResumoBean.idAvaliador}"  />
					      	</a4j:support>
						</rich:suggestionbox>
						<img id="indicatorAvaliador" src="/sigaa/img/indicator.gif" style="display: none;">
					</a4j:region>
				</td>
			</tr>
			
			<tr>
				<td width="3%"><h:selectBooleanCheckbox value="#{avaliacaoApresentacaoResumoBean.filtroAluno}" id="checkAluno" styleClass="noborder"/> </td>
				<td width="22%"><label for="checkAluno" onclick="$('form:checkAluno').checked = !$('form:checkAluno').checked;">Aluno:</label></td>
				<td>
					<h:inputText id="nomealuno" value="#{avaliacaoApresentacaoResumoBean.nomeAluno}" size="80" maxlength="150" onfocus="$('form:checkAluno').checked = true;"/>
				</td>
			</tr>
			
			<tr>
				<td width="3%"><h:selectBooleanCheckbox value="#{avaliacaoApresentacaoResumoBean.filtroOrientador}" id="checkOrientador" styleClass="noborder"/> </td>
				<td width="22%"><label for="checkOrientador" onclick="$('form:checkOrientador').checked = !$('form:checkOrientador').checked;">Orientador:</label></td>
				<td>
					<h:inputText id="nomeorientador" value="#{avaliacaoApresentacaoResumoBean.nomeOrientador}" size="80" maxlength="150" onfocus="$('form:checkOrientador').checked = true;"/>
					<rich:suggestionbox id="suggestion"  width="430" height="100" minChars="3" 
						 	 frequency="0" ignoreDupResponses="true" selfRendered="true" requestDelay="200" 
						 	for="nomeorientador" suggestionAction="#{servidorAutoCompleteMBean.autocompleteNomeServidor}" 
						 	var="_servidor" fetchValue="#{_servidor.pessoa.nome}">
						<h:column>
							<h:outputText value="#{_servidor.siape} - #{_servidor.pessoa.nome} (#{_servidor.unidade.nome})"/>
						</h:column>
						
				        <a4j:support event="onselect">
							<f:setPropertyActionListener value="#{_servidor.id}" target="#{avaliacaoApresentacaoResumoBean.idOrientador}"  />
						</a4j:support>
						
					  </rich:suggestionbox>
				</td>
			</tr>
			
			<tr>
				<td width="3%"><h:selectBooleanCheckbox value="#{avaliacaoApresentacaoResumoBean.filtroCodResumo}" id="checkCodResumo" styleClass="noborder"/> </td>
				<td width="22%"><label for="checkCodResumo" onclick="$('form:checkCodResumo').checked = !$('form:checkCodResumo').checked;">Código do Resumo:</label></td>
				<td>
					<h:inputText id="codresumo" value="#{avaliacaoApresentacaoResumoBean.codResumo}" size="10" maxlength="6" onfocus="$('form:checkCodResumo').checked = true;"/>
				</td>
			</tr>
			
			<tr>
				<td width="3%"><h:selectBooleanCheckbox value="#{avaliacaoApresentacaoResumoBean.filtroNumPainel}" id="checkNumPainel" styleClass="noborder"/> </td>
				<td width="22%"><label for="checkNumPainel" onclick="$('form:checkNumPainel').checked = !$('form:checkNumPainel').checked;">Número do Painel:</label></td>
				<td>
					<h:inputText id="numpainel" value="#{avaliacaoApresentacaoResumoBean.numPainel}" size="4" maxlength="3" onkeyup="formatarInteiro(this);" onfocus="$('form:checkNumPainel').checked = true;"/>
				</td>
			</tr>
			
			<tfoot>
			<tr>
				<td colspan="3">
					<h:commandButton id="btnBuscar" action="#{avaliacaoApresentacaoResumoBean.buscarAvaliacoes}" value="Buscar"/>
					<h:commandButton id="btnCancelar" action="#{avaliacaoApresentacaoResumoBean.cancelar}" value="Cancelar" onclick="#{confirm}"/>
				</td>
			</tr>
			</tfoot>
		</table>
		<br/>
		
		<table class="formulario" width="100%">
			<caption>Avaliações de Trabalhos do CIC (${ fn:length(avaliacaoApresentacaoResumoBean.avaliacoes) })</caption>
			<tbody>
				<tr>
					<td colspan="2">
						<div class="infoAltRem">
							<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: Avaliar Apresentação de Trabalho<br/>
						</div>
						<t:dataTable value="#{avaliacaoApresentacaoResumoBean.avaliacoes}" var="av" id="avaliacoes" rowIndexVar="index" width="100%" rowClasses="linhaPar, linhaImpar" styleClass="listagem">
							
							<t:column>
								<f:facet name="header">
									<f:verbatim>Avaliador</f:verbatim>
								</f:facet>
								<h:outputText value="#{av.avaliador.docente.pessoa.nome}" />
							</t:column>
							
							<t:column>
								<f:facet name="header">
									<f:verbatim>Nº Painel</f:verbatim>
								</f:facet>
								<h:outputText value="#{av.resumo.numeroPainel}" />
							</t:column>
						
							<t:column>
								<f:facet name="header">
									<f:verbatim>Código</f:verbatim>
								</f:facet>
								<h:outputText value="#{av.resumo.codigo}" />
							</t:column>
							
							<t:column>
								<f:facet name="header">
									<f:verbatim>Autor</f:verbatim>
								</f:facet>
								<h:outputText value="#{av.resumo.autor.nome}" />
							</t:column>
							
							<t:column>
								<f:facet name="header">
									<f:verbatim>Orientador</f:verbatim>
								</f:facet>
								<h:outputText value="#{av.resumo.orientador.nome}" />
							</t:column>
							
							<t:column>
								<f:facet name="header">
									<f:verbatim>Status</f:verbatim>
								</f:facet>
								<h:outputText value="#{av.resumo.statusString}" />
							</t:column>
							
							<t:column>
								<f:facet name="header">
									<f:verbatim>Média</f:verbatim>
								</f:facet>
								<h:outputText value="#{av.media}" />
							</t:column>
							
							<t:column width="5%" styleClass="centerAlign">
								<f:facet name="header"><f:verbatim></f:verbatim></f:facet>
								<h:commandLink action="#{avaliacaoApresentacaoResumoBean.popularAvaliacao}">
									<f:param name="id" value="#{av.id}"/>
									<h:graphicImage url="/img/seta.gif" title="Avaliar Apresentação de Resumo"/>
								</h:commandLink>
							</t:column>
						</t:dataTable>
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2"><h:commandButton id="btnConcluir" value="Concluir Avaliações" action="#{avaliacaoApresentacaoResumoBean.cancelar}"/></td>
				</tr>
			</tfoot>
		</table>
	</h:form>
		
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
