<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<a4j:keepAlive beanName="analiseManifestacaoOuvidoria" />

<style>
#listagem thead>tr>th {
	border: none;
}
</style>

<script type="text/javascript">var J = jQuery.noConflict();</script>
<script type="text/javascript">
	JAWR.loader.script('/javascript/jquery.tablesorter.min.js');
</script>
<link rel="stylesheet" type="text/css" href="/shared/css/tablesorter/style.css" />

<f:view>
	<h2>
		<ufrn:subSistema /> &gt; Manifestações Encaminhadas
	</h2>
	<h:form id="form">
	
			<table class="formulario" width="70%">
			<caption>Buscar Manifestação</caption>
			<tbody>
				<tr>
					<td width="15%" align="right">
						<h:selectBooleanCheckbox value="#{analiseManifestacaoOuvidoria.buscaNumeroAno }" id="selectNumeroAno" styleClass="noborder"/>
					</td>
			    	<td width="20%"><h:outputLabel value="Número/Ano:" for="selectNumeroAno" /></td> 
			    	<td>
			    		<h:inputText value="#{analiseManifestacaoOuvidoria.numero }" id="numero" size="4" maxlength="4" onkeyup="formatarInteiro(this)" onfocus="javascript:$('form:selectNumeroAno').checked = true;" />
			    		/<h:inputText value="#{analiseManifestacaoOuvidoria.ano }" id="ano" size="4" maxlength="4" onkeyup="formatarInteiro(this)" onfocus="javascript:$('form:selectNumeroAno').checked = true;" />
			    	</td>
			    </tr>
			    <tr>
			    	<td align="right">
						<h:selectBooleanCheckbox value="#{analiseManifestacaoOuvidoria.buscaPeriodo }" id="selectPeriodo" styleClass="noborder"/>
					</td>
					<th style="text-align: left;" nowrap="nowrap"><h:outputLabel value="Período de Cadastro:" for="selectPeriodo" /></th>
					<td> 
						De: <t:inputCalendar value="#{analiseManifestacaoOuvidoria.dataInicial }" renderAsPopup="true" renderPopupButtonAsImage="true" 
									popupDateFormat="dd/MM/yyyy" popupTodayString="Hoje é" id="Data_Inicial" size="10" maxlength="10" onkeypress="return formataData(this,event)" onchange="$('form:selectPeriodo').checked = true;" />
						Até: <t:inputCalendar value="#{analiseManifestacaoOuvidoria.dataFinal }" renderAsPopup="true" renderPopupButtonAsImage="true" 
									popupDateFormat="dd/MM/yyyy" popupTodayString="Hoje é" id="Data_Final" size="10" maxlength="10" onkeypress="return formataData(this,event)" onchange="$('form:selectPeriodo').checked = true;" />
					</td>
				</tr>
				<tr>
					<td align="right">
						<h:selectBooleanCheckbox value="#{analiseManifestacaoOuvidoria.buscaCategoriaAssunto }" id="selectCategoriaAssunto" styleClass="noborder"/>
					</td>
			    	<td><h:outputLabel value="Categoria do Assunto:" for="selectCategoriaAssunto" /></td> 
			    	<td>
			    		<h:selectOneMenu id="selectCategoria" value="#{analiseManifestacaoOuvidoria.categoriaAssuntoManifestacao.id }" onchange="javascript:$('form:selectCategoriaAssunto').checked = true;">
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
			    		<h:inputText value="#{analiseManifestacaoOuvidoria.nome }" id="nome" style="width: 95%" onchange="javascript:$('form:selectNome').checked = true;" />
			    	</td>
			    </tr>
				<tr>
					<td align="right">
						<h:selectBooleanCheckbox value="#{analiseManifestacaoOuvidoria.buscaMatriculaSiape }" id="selectMatriculaSiape" styleClass="noborder"/>
					</td>
			    	<td><h:outputLabel value="Matrícula/SIAPE do Manifestante:" for="selectMatriculaSiape" /></td> 
			    	<td>
			    		<h:inputText value="#{analiseManifestacaoOuvidoria.matriculaSiape }" id="matriculaSiape" size="10" maxlength="10" onkeypress="formatarInteiro(this)" onblur="removeNonNumbersCharacters(this)" onchange="javascript:$('form:selectMatriculaSiape').checked = true;" />
			    	</td>
			    </tr>
				<tr>
					<td align="right">
						<h:selectBooleanCheckbox value="#{analiseManifestacaoOuvidoria.buscaCpf }" id="selectCpf" styleClass="noborder"/>
					</td>
			    	<td><h:outputLabel value="CPF do Manifestante:" for="selectCpf" /></td> 
			    	<td>
						<h:inputText value="#{analiseManifestacaoOuvidoria.cpf }" onkeypress="return formataCPF(this, event);" size="14" maxlength="14" onchange="javascript:$('form:selectCpf').checked = true;">
							<f:converter converterId="convertCpf"/>
							<f:param name="type" value="cpf" />
						</h:inputText>
			    	</td>
			    </tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton value="Buscar" action="#{analiseManifestacaoOuvidoria.buscarManifestacaoEncaminhadas }" id="btnBuscar"/>
						<h:commandButton value="Cancelar" action="#{analiseManifestacaoOuvidoria.cancelar }" onclick="#{confirm}" id="btnCancelar" />
			    	</td>
			    </tr>
			</tfoot>
		</table>
		
		<br/>
		
		<div class="infoAltRem">
			<h:graphicImage value="/img/view.gif" style="overflow: visible;" />: Visualizar Detalhes da Manifestação
			<h:graphicImage value="/img/update_cal.png" style="overflow: visible;" />: Alterar Prazo de Resposta
			<h:graphicImage value="/img/arrow_undo.png" style="overflow: visible;" />: Responder Manifestação
		</div>
	
		<br />

		<table class="listagem tablesorter" id="listagem" width="100%" cellpadding="3px">
			<caption>Manifestações Encaminhadas(${ fn:length(analiseManifestacaoOuvidoria.manifestacoesEncaminhadasPaginadas) })</caption>
				<thead>
					<tr>
						<th style="padding-left: 15px; padding-right: 15px;text-align: center;">Data de Cadastro</th>
						<th style="padding-left: 15px; padding-right: 15px; text-align: right;">Número/Ano</th>
						<th style="padding-left: 15px; padding-right: 15px;" width="10">Categoria do Solicitante</th>
						<th style="padding-left: 15px; padding-right: 15px;">Origem</th>
						<th style="padding-left: 15px; padding-right: 15px;" width="10">Categoria do Assunto</th>
						<th>Assunto</th>
						<th style="padding-left: 15px; padding-right: 15px;">Unidade Responsável</th>
						<th style="padding-left: 15px; padding-right: 15px;">Status</th>
						<th></th>
						<th></th>
						<th></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="#{analiseManifestacaoOuvidoria.manifestacoesEncaminhadasPaginadas }" var="manifestacao" varStatus="status">
						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
							<th style="text-align: center;"><ufrn:format  valor="${manifestacao.dataCadastro }" type="datahora"/></th>
							<th style="padding-left: 15px; padding-right: 15px;text-align: right; padding-left: 15px; padding-right: 15px;">${manifestacao.numeroAno }</th>
							<th style="padding-left: 15px; padding-right: 15px;text-align: left;">${manifestacao.interessadoManifestacao.categoriaSolicitante.descricao }</th>
							<th style="padding-left: 15px; padding-right: 15px;text-align: left;">${manifestacao.origemManifestacao.descricao }</th>
							<th style="padding-left: 15px; padding-right: 15px;text-align: left;">${manifestacao.assuntoManifestacao.categoriaAssuntoManifestacao.descricao }</th>
							<th style="text-align: left;">${manifestacao.assuntoManifestacao.descricao }</th>
							<th style="padding-left: 15px; padding-right: 15px;text-align: left;">${manifestacao.unidadeResponsavel }</th>
							<th style="padding-left: 15px; padding-right: 15px;text-align: left;">${manifestacao.statusManifestacao.descricao }</th>
							<th>
								<h:commandButton image="/img/view.gif" title="Visualizar Detalhes da Manifestação" actionListener="#{analiseManifestacaoOuvidoria.detalharManifestacaoEncaminhada }">
									<f:attribute name="idManifestacao" value="#{manifestacao.id}" />
								</h:commandButton>
							</th>
							<th>
								<h:commandButton image="/img/update_cal.png" title="Alterar Prazo de Resposta" actionListener="#{analiseManifestacaoOuvidoria.alterarPrazoManifestacao }" rendered="#{!manifestacao.statusManifestacao.parecerCadastrado }">
									<f:attribute name="idManifestacao" value="#{manifestacao.id}" />
								</h:commandButton>
								<h:commandButton image="/img/arrow_undo.png" title="Responder Manifestação" actionListener="#{analiseManifestacaoOuvidoria.responderManifestacaoEncaminhada }" rendered="#{manifestacao.statusManifestacao.parecerCadastrado }">
									<f:attribute name="idManifestacao" value="#{manifestacao.id}" />
								</h:commandButton>
							</th>
							<th></th>
						</tr>
					</c:forEach>
				</tbody>
		</table>
		<br/>
		<div style="text-align: center;"> 
		    <h:commandButton image="/img/voltar.gif" actionListener="#{analiseManifestacaoOuvidoria.previousPage}" rendered="#{paginacao.paginaAtual > 0 }"/>
		 
		    <h:selectOneMenu value="#{paginacao.paginaAtual}" valueChangeListener="#{analiseManifestacaoOuvidoria.changePage}" onchange="submit()" immediate="true">
			<f:selectItems id="paramPagina" value="#{paginacao.listaPaginas}"/>
		    </h:selectOneMenu>
		 
		    <h:commandButton image="/img/avancar.gif" actionListener="#{analiseManifestacaoOuvidoria.nextPage}"  rendered="#{paginacao.paginaAtual < (paginacao.totalPaginas - 1)}"/>
		    <br/><br/>
		 
		    <em><h:outputText value="#{paginacao.totalRegistros }"/> Registro(s) Encontrado(s)</em>
		</div>
		<rich:jQuery selector="#listagem" query="tablesorter( {dateFormat: 'uk', headers: {8: { sorter: false },9: { sorter: false },10: { sorter: false },11: { sorter: false } } });" timing="onload" />
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>