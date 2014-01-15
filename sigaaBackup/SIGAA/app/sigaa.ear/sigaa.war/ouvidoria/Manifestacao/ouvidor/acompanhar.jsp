<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<a4j:keepAlive beanName="analiseManifestacaoOuvidoria" />

<f:view>
	<h2>
		<ufrn:subSistema /> &gt; Acompanhar Manifestações
	</h2>

	<h:form id="formBusca">
		<table class="formulario" width="70%">
			<caption>Buscar Manifestação</caption>
			<tbody>
				<tr>
					<td width="15%" align="right">
						<h:selectBooleanCheckbox value="#{analiseManifestacaoOuvidoria.buscaNumeroAno }" id="selectNumeroAno" styleClass="noborder"/>
					</td>
			    	<td width="20%"><h:outputLabel value="Número/Ano:" for="selectNumeroAno" /></td> 
			    	<td>
			    		<h:inputText value="#{analiseManifestacaoOuvidoria.numero }" id="numero" size="4" maxlength="4" onkeyup="formatarInteiro(this)" onfocus="javascript:$('formBusca:selectNumeroAno').checked = true;" />
			    		/<h:inputText value="#{analiseManifestacaoOuvidoria.ano }" id="ano" size="4" maxlength="4" onkeyup="formatarInteiro(this)" onfocus="javascript:$('formBusca:selectNumeroAno').checked = true;" />
			    	</td>
			    </tr>
			    <tr>
			    	<td align="right">
						<h:selectBooleanCheckbox value="#{analiseManifestacaoOuvidoria.buscaPeriodo }" id="selectPeriodo" styleClass="noborder"/>
					</td>
					<th style="text-align: left;" nowrap="nowrap"><h:outputLabel value="Período de Cadastro:" for="selectPeriodo" /></th>
					<td> 
						De: <t:inputCalendar value="#{analiseManifestacaoOuvidoria.dataInicial }" renderAsPopup="true" renderPopupButtonAsImage="true" 
									popupDateFormat="dd/MM/yyyy" popupTodayString="Hoje é" id="Data_Inicial" size="10" maxlength="10" onkeypress="return formataData(this,event)" onchange="$('formBusca:selectPeriodo').checked = true;" />
						Até: <t:inputCalendar value="#{analiseManifestacaoOuvidoria.dataFinal }" renderAsPopup="true" renderPopupButtonAsImage="true" 
									popupDateFormat="dd/MM/yyyy" popupTodayString="Hoje é" id="Data_Final" size="10" maxlength="10" onkeypress="return formataData(this,event)" onchange="$('formBusca:selectPeriodo').checked = true;" />
					</td>
				</tr>
				<tr>
					<td align="right">
						<h:selectBooleanCheckbox value="#{analiseManifestacaoOuvidoria.buscaCategoriaAssunto }" id="selectCategoriaAssunto" styleClass="noborder"/>
					</td>
			    	<td><h:outputLabel value="Categoria do Assunto:" for="selectCategoriaAssunto" /></td> 
			    	<td>
			    		<h:selectOneMenu id="selectCategoria" value="#{analiseManifestacaoOuvidoria.categoriaAssuntoManifestacao.id }" onchange="javascript:$('formBusca:selectCategoriaAssunto').checked = true;">
							<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
							<f:selectItems value="#{categoriaAssuntoManifestacao.allCategoriasAtivasCombo }"/>
						</h:selectOneMenu>
			    	</td>
			    </tr>
				<tr>
					<td align="right">
						<h:selectBooleanCheckbox value="#{analiseManifestacaoOuvidoria.buscaNome }" id="selectNome" styleClass="noborder"/>
					</td>
			    	<td><h:outputLabel value="Nome do Manifestante:" for="selectNome" /></td> 
			    	<td>
			    		<h:inputText value="#{analiseManifestacaoOuvidoria.nome }" id="nome" style="width: 95%" onchange="javascript:$('formBusca:selectNome').checked = true;" />
			    	</td>
			    </tr>
				<tr>
					<td align="right">
						<h:selectBooleanCheckbox value="#{analiseManifestacaoOuvidoria.buscaMatriculaSiape }" id="selectMatriculaSiape" styleClass="noborder"/>
					</td>
			    	<td><h:outputLabel value="Matrícula/SIAPE do Manifestante:" for="selectMatriculaSiape" /></td> 
			    	<td>
			    		<h:inputText value="#{analiseManifestacaoOuvidoria.matriculaSiape }" id="matriculaSiape" size="10" maxlength="10" onkeypress="formatarInteiro(this)" onblur="removeNonNumbersCharacters(this)" onchange="javascript:$('formBusca:selectMatriculaSiape').checked = true;" />
			    	</td>
			    </tr>
				<tr>
					<td align="right">
						<h:selectBooleanCheckbox value="#{analiseManifestacaoOuvidoria.buscaCpf }" id="selectCpf" styleClass="noborder"/>
					</td>
			    	<td><h:outputLabel value="CPF do Manifestante:" for="selectCpf" /></td> 
			    	<td>
						<h:inputText value="#{analiseManifestacaoOuvidoria.cpf }" onkeypress="return formataCPF(this, event);" size="14" maxlength="14" onchange="javascript:$('formBusca:selectCpf').checked = true;">
							<f:converter converterId="convertCpf"/>
							<f:param name="type" value="cpf" />
						</h:inputText>
			    	</td>
			    </tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton value="Buscar" action="#{analiseManifestacaoOuvidoria.buscarManifestacao }" id="btnBuscar"/>
						<h:commandButton value="Cancelar" action="#{analiseManifestacaoOuvidoria.cancelar }" onclick="#{confirm}" id="btnCancelar" />
			    	</td>
			    </tr>
			</tfoot>
		</table>

		<c:if test="${not empty analiseManifestacaoOuvidoria.manifestacoesAcompanhamento }">
			<br />
			<div class="infoAltRem">
				<h:graphicImage value="/img/view.gif" style="overflow: visible;" />: Visualizar Detalhes da Manifestação
				<ufrn:checkRole	papeis="<%= new int[] { SigaaPapeis.OUVIDOR } %>">
					<h:graphicImage value="/img/check.png" style="overflow: visible;" />: Finalizar Manifestação
				</ufrn:checkRole>
				<h:graphicImage value="/img/notificar.png" style="overflow: visible;" />: Solicitar Esclarecimentos
			</div>
	
			<br />
			
			<table class="listagem" width="100%" cellpadding="3px">
				<caption>Manifestações Encontradas</caption>
					<thead>
						<tr>
							<th style="text-align: center;">Data de Cadastro</th>
							<th style="padding-right: 10%; text-align: right;">Número/Ano</th>
							<th>Status</th>
							<th>Categoria do Solicitante</th>
							<th>Título</th>
							<th></th>
							<th></th>
							<th></th>
							<th></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="#{analiseManifestacaoOuvidoria.manifestacoesAcompanhamento }" var="manifestacao" varStatus="status">
							<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
								<th style="text-align: center;"><ufrn:format  valor="${manifestacao.dataCadastro }" type="datahora"/></th>
								<th style="text-align: right; padding-right: 10%;">${manifestacao.numeroAno }</th>
								<th style="text-align: left;">${manifestacao.statusManifestacao.descricao }</th>
								<th style="text-align: left;">${manifestacao.interessadoManifestacao.categoriaSolicitante.descricao }</th>
								<th style="text-align: left;">${manifestacao.titulo }</th>
								<th>
									<h:commandButton image="/img/view.gif" title="Visualizar Detalhes da Manifestação" actionListener="#{analiseManifestacaoOuvidoria.detalharManifestacaoBuscada }">
										<f:attribute name="idManifestacao" value="#{manifestacao.id}" />
									</h:commandButton>
								</th>
								<th>
									<h:commandLink title="Solicitar Esclarecimentos" action="#{esclarecimentoOuvidoria.solicitarEsclarecimentos }" rendered="#{!manifestacao.finalizada && analiseManifestacaoOuvidoria.usuarioOuvidor}">
										<h:graphicImage value="/img/notificar.png"/>
										<f:param name="idManifestacao" value="#{manifestacao.id}" />
										<f:param name="pendentes" value="false" />
									</h:commandLink>
								</th>
								<th>
									<h:commandButton image="/img/check.png" title="Finalizar Manifestação" actionListener="#{analiseManifestacaoOuvidoria.finalizarManifestacaoRespondidaAcompanhar }" rendered="#{!manifestacao.finalizada && analiseManifestacaoOuvidoria.usuarioOuvidor}">
										<f:attribute name="idManifestacao" value="#{manifestacao.id}" />
									</h:commandButton>
								</th>
								<th></th>
							</tr>
						</c:forEach>
					</tbody>
			</table>
			<div style="text-align: center;"> 
			    <h:commandButton image="/img/voltar.gif" actionListener="#{analiseManifestacaoOuvidoria.previousPage}" rendered="#{paginacao.paginaAtual > 0 }"/>
			 
			    <h:selectOneMenu value="#{paginacao.paginaAtual}" valueChangeListener="#{analiseManifestacaoOuvidoria.changePage}" onchange="submit()" immediate="true">
				<f:selectItems id="paramPagina" value="#{paginacao.listaPaginas}"/>
			    </h:selectOneMenu>
			 
			    <h:commandButton image="/img/avancar.gif" actionListener="#{analiseManifestacaoOuvidoria.nextPage}"  rendered="#{paginacao.paginaAtual < (paginacao.totalPaginas - 1)}"/>
			    <br/><br/>
			 
			    <em><h:outputText value="#{paginacao.totalRegistros }"/> Registro(s) Encontrado(s)</em>
			</div>
		</c:if>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>