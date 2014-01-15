<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<a4j:keepAlive beanName="analiseManifestacaoResponsavel" />

<f:view>
	<h2>
		<ufrn:subSistema /> &gt; Acompanhar Manifestações
	</h2>

	<h:form id="formBusca">
		<table class="formulario" width="60%">
			<caption>Buscar Manifestação</caption>
			<tbody>
				<tr>
					<td width="20%" align="right">
						<h:selectBooleanCheckbox value="#{analiseManifestacaoResponsavel.buscaNumeroAno }" id="selectNumeroAno" styleClass="noborder"/>
					</td>
			    	<td width="15%"><h:outputLabel value="Número/Ano:" for="selectNumeroAno" /></td> 
			    	<td>
			    		<h:inputText value="#{analiseManifestacaoResponsavel.numero }" id="numero" size="4" maxlength="4" onkeyup="formatarInteiro(this)" onchange="$('formBusca:selectNumeroAno').checked = true;" />
			    		/<h:inputText value="#{analiseManifestacaoResponsavel.ano }" id="ano" size="4" maxlength="4" onkeyup="formatarInteiro(this)" onchange="$('formBusca:selectNumeroAno').checked = true;" />
			    	</td>
			    </tr>
			    <tr>
			    	<td width="20%" align="right">
						<h:selectBooleanCheckbox value="#{analiseManifestacaoResponsavel.buscaPeriodo }" id="selectPeriodo" styleClass="noborder"/>
					</td>
					<th style="text-align: left;" nowrap="nowrap"><h:outputLabel value="Período de Cadastro:" for="selectPeriodo" /></th>
					<td> 
						De: <t:inputCalendar value="#{analiseManifestacaoResponsavel.dataInicial }" renderAsPopup="true" renderPopupButtonAsImage="true" 
									popupDateFormat="dd/MM/yyyy" popupTodayString="Hoje é" id="Data_Inicial" size="10" maxlength="10" onchange="$('formBusca:selectPeriodo').checked = true;" />
						Até: <t:inputCalendar value="#{analiseManifestacaoResponsavel.dataFinal }" renderAsPopup="true" renderPopupButtonAsImage="true" 
									popupDateFormat="dd/MM/yyyy" popupTodayString="Hoje é" id="Data_Final" size="10" maxlength="10" onchange="$('formBusca:selectPeriodo').checked = true;" />
					</td>
				</tr>
				<tr>
					<td width="20%" align="right">
						<h:selectBooleanCheckbox value="#{analiseManifestacaoResponsavel.buscaCategoriaAssunto }" id="selectCategoriaAssunto" styleClass="noborder"/>
					</td>
			    	<td width="15%"><h:outputLabel value="Categoria do Assunto:" for="selectCategoriaAssunto" /></td> 
			    	<td>
			    		<h:selectOneMenu id="selectCategoria" value="#{analiseManifestacaoResponsavel.categoriaAssuntoManifestacao.id }" onchange="javascript:$('formBusca:selectCategoriaAssunto').checked = true;">
							<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
							<f:selectItems value="#{categoriaAssuntoManifestacao.allCategoriasByUnidadeCombo }"/>
						</h:selectOneMenu>
			    	</td>
			    </tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton value="Buscar" action="#{analiseManifestacaoResponsavel.buscarManifestacao }" id="btnBuscar"/>
						<h:commandButton value="Cancelar" action="#{analiseManifestacaoResponsavel.cancelar }" onclick="#{confirm}" id="btnCancelar" />
			    	</td>
			    </tr>
			</tfoot>
		</table>

		<c:if test="${not empty analiseManifestacaoResponsavel.manifestacoesAcompanhamento }">
			<br />
			<div class="infoAltRem">
				<h:graphicImage value="/img/view.gif" style="overflow: visible;" />: Visualizar Detalhes da Manifestação
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
						</tr>
					</thead>
					<tbody>
						<c:forEach items="#{analiseManifestacaoResponsavel.manifestacoesAcompanhamento }" var="manifestacao" varStatus="status">
							<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
								<th style="text-align: center;"><ufrn:format  valor="${manifestacao.dataCadastro }" type="datahora"/></th>
								<th style="padding-right: 10%; text-align: right;">${manifestacao.numeroAno }</th>
								<th style="text-align: left;">${manifestacao.statusManifestacao.descricao }</th>
								<th style="text-align: left;">
									<c:if test="${manifestacao.anonima }">
										<i>Manifestação Anônima</i>
									</c:if>
									<c:if test="${!manifestacao.anonima }">
										${manifestacao.interessadoManifestacao.categoriaSolicitante.descricao }
									</c:if>
								</th>
								<th style="text-align: left;">${manifestacao.titulo }</th>
								<th>
									<h:commandButton image="/img/view.gif" title="Visualizar Detalhes da Manifestação" actionListener="#{analiseManifestacaoResponsavel.detalharManifestacaoBuscada }">
										<f:attribute name="idManifestacao" value="#{manifestacao.id}" />
									</h:commandButton>
								</th>
								<th></th>
							</tr>
						</c:forEach>
					</tbody>
			</table>
			<div style="text-align: center;"> 
			    <h:commandButton image="/img/voltar.gif" actionListener="#{analiseManifestacaoResponsavel.previousPage}" rendered="#{paginacao.paginaAtual > 0 }"/>
			 
			    <h:selectOneMenu value="#{paginacao.paginaAtual}" valueChangeListener="#{analiseManifestacaoResponsavel.changePage}" onchange="submit()" immediate="true">
				<f:selectItems id="paramPagina" value="#{paginacao.listaPaginas}"/>
			    </h:selectOneMenu>
			 
			    <h:commandButton image="/img/avancar.gif" actionListener="#{analiseManifestacaoResponsavel.nextPage}"  rendered="#{paginacao.paginaAtual < (paginacao.totalPaginas - 1)}"/>
			    <br/><br/>
			 
			    <em><h:outputText value="#{paginacao.totalRegistros }"/> Registro(s) Encontrado(s)</em>
			</div>
		</c:if>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>