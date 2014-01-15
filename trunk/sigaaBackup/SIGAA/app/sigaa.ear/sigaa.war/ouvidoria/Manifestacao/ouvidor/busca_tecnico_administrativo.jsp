<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<a4j:keepAlive beanName="manifestacaoOuvidoria" />

<style>
	table.listagem tr.curso td{
		background: #C8D5EC;
		font-weight: bold;
		padding-left: 20px;
	}
	
	table.listagem td.detalhesDiscente { display: none; padding: 0;}
</style>

<f:view>
	<h:form id="formulario">
		<h2><ufrn:subSistema /> &gt; Buscar Servidor Técnico Administrativo</h2>
		
		<div id="ajuda" class="descricaoOperacao">
			<p>Para prosseguir com o cadastro, favor buscar e selecionar o servidor técnico administrativo desejado utilizando o formulário abaixo.</p>
		</div>
		
		<table class="formulario" style="width:58%;">
			<caption> Informe os critérios de busca</caption>
			<tbody>
				<tr>
					<td width="2%">
						<h:selectBooleanCheckbox value="#{manifestacaoOuvidoria.buscaMatricula}" styleClass="noborder" id="checkMatricula" />
					</td>
					<th style="text-align: left" width="130px"> 
						<label for="checkMatricula"	onclick="$('formulario:checkMatricula').checked = !$('formulario:checkMatricula').checked;">
							SIAPE:</label></th>
					<td> 
						<h:inputText value="#{manifestacaoOuvidoria.matricula}" size="14" id="matriculaDiscente" maxlength="12"
								onfocus="getEl('formulario:checkMatricula').dom.checked = true;" onkeyup="return formatarInteiro(this);" />
					</td>
				</tr>
				<tr>
					<td><h:selectBooleanCheckbox value="#{manifestacaoOuvidoria.buscaNome}" styleClass="noborder" id="checkNome" /></td>
					<th style="text-align: left"> 
						<label for="checkNome" onclick="$('formulario:checkNome').checked = !$('formulario:checkNome').checked;">
							Nome do Servidor:</label></th>
					<td><h:inputText  value="#{manifestacaoOuvidoria.nome}" size="60" maxlength="60" id="nomeDiscente" 
								onfocus="getEl('formulario:checkNome').dom.checked = true;"/> </td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton action="#{manifestacaoOuvidoria.buscarTecnicoAdministrativo}" value="Buscar" id="buscar" />
						<h:commandButton id="btn_anterior" value="<< Passo Anterior" action="#{manifestacaoOuvidoria.paginaTipoSolicitante }" />
						<h:commandButton action="#{manifestacaoOuvidoria.cancelar}" value="Cancelar" id="cancelar" onclick="#{confirm}" />
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	
	<br /><br />
	
	<h:form id="form">
		<c:if test="${manifestacaoOuvidoria.servidores != null && not empty manifestacaoOuvidoria.servidores }">
			<center>
				<div class="infoAltRem" style="width:80%;"> 
					<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: Selecionar Servidor
				</div>
			</center>
			<br />
			
			<table class="listagem" style="width: 80%;">
				<caption> Resultado da busca (${fn:length(manifestacaoOuvidoria.servidores)}) </caption>
				<thead>
					<tr>
						<th style="text-align: right; padding-right: 1%;" width="7%"> SIAPE </th>
						<th> Servidor </th>
						<th> Lotação </th>
						<th width="2%"> </th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="#{manifestacaoOuvidoria.servidores}" var="servidor" varStatus="status">
					<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td style="text-align: right; padding-right: 1%;"> ${servidor.siape} </td>
						<td> ${servidor.nome} </td>
						<td> ${servidor.unidade} </td>
						<td> 
							<h:commandButton image="/img/seta.gif" title="Selecionar Servidor" actionListener="#{manifestacaoOuvidoria.selecionarPessoa }" styleClass="noborder">
								<f:attribute name="idSelecionado" value="#{servidor.id}" />
							</h:commandButton>					
						</td>
					</tr>
					</c:forEach>
				</tbody>
			</table>
		</c:if>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>