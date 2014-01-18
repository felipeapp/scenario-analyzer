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
		
		<div class="infoAltRem">
			<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: Avaliar Apresentação de Trabalho
			<h:graphicImage url="/img/monitoria/document_new.png" width="16px"/>: Criar nova Avaliação de Resumo
		</div>
		
		<table class="formulario" width="100%">
			<caption>Avaliações de Trabalhos do CIC (${ fn:length(avaliacaoApresentacaoResumoBean.avaliacoes) })</caption>
				<c:set var="_codigo" value=" "/>
					<c:forEach items="#{avaliacaoApresentacaoResumoBean.avaliacoes}" var="av">
						<c:if test="${ _codigo != av.resumo.codigo }">
					        <thead>
								<tr>
									<td class="subFormulario" colspan="7"> 
										${av.resumo.codigo} - ${av.resumo.titulo}
									</td>
									<td class="subFormulario">										
										<h:commandLink action="#{avaliacaoApresentacaoResumoBean.criarAvaliacao}">
 											<f:param name="idResumo" value="#{av.resumo.id}"/> 
											<h:graphicImage url="/img/monitoria/document_new.png" width="16px" title="Criar nova Avaliação de Resumo"/>
 										</h:commandLink>
									</td>
								</tr>						
						      	<tr>
						        	<th>Avaliador</th>
						        	<th>Nº Painel</th>
						        	<th>Código</th>
						        	<th>Autor</th>
						        	<th>Orientador</th>	        	
						        	<th>Status</th>
						        	<th>Média</th>
						        	<th width="5%"></th>
						        </tr>
						 	</thead>
						</c:if>
						
						<tr>
							<td> ${av.avaliador.docente.pessoa.nome} </td>
							<td> ${av.resumo.numeroPainel} </td>
							<td> ${av.resumo.codigo} </td>
							<td> ${av.resumo.autor.nome} </td>
							<td> ${av.resumo.orientador.nome} </td>
							<td> ${av.resumo.statusString} </td>
							<td> ${av.media} </td>
							<td>
								<h:commandLink action="#{avaliacaoApresentacaoResumoBean.popularAvaliacao}">
 									<f:param name="id" value="#{av.id}"/> 
									<h:graphicImage url="/img/seta.gif" title="Avaliar Apresentação de Resumo"/>
 								</h:commandLink>
							</td>
						</tr>
						
					<c:set var="_codigo" value="${av.resumo.codigo}"/>
					</c:forEach>
						
			<tfoot>
				<tr>
					<td colspan="8"><h:commandButton id="btnConcluir" value="Concluir Avaliações" action="#{avaliacaoApresentacaoResumoBean.cancelar}"/></td>
				</tr>
			</tfoot>
		</table>
	</h:form>
		
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
