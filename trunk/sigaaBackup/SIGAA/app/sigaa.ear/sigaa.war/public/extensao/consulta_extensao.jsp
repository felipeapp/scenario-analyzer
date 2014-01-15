<%@include file="/public/include/cabecalho.jsp" %>
<%@page import="br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto"%>
<style>
	.camposBusca{
		width: 560px;
	}
</style>
<f:view>
	<a4j:keepAlive beanName="consultaPublicaAtividadeExtensao"/>
	<h2>Consulte as atividades de extensão desenvolvidas pela ${ configSistema['siglaInstituicao'] }</h2>
	<br />
	
	<h:form id="formBuscaAtividade">
		<h:outputText value="#{consultaPublicaAtividadeExtensao.create}" />
		<table class="formulario" width="80%">
			<caption>Busca por Ações de Extensão</caption>
			<tbody>
				<tr>
					<td><h:selectBooleanCheckbox value="#{consultaPublicaAtividadeExtensao.checkBuscaTitulo}" id="selectBuscaTitulo" 
								styleClass="noborder" /> </td>
	    			<td> <label for="nomeProjeto"> Título da Ação </label> </td>
	    			<td><h:inputText id="buscaTitulo" value="#{consultaPublicaAtividadeExtensao.buscaNomeAtividade}" styleClass="camposBusca" 
	    						onfocus="javascript:$('formBuscaAtividade:selectBuscaTitulo').checked = true;" /></td>
	    		</tr>
	
				<tr>
					<td><h:selectBooleanCheckbox value="#{consultaPublicaAtividadeExtensao.checkBuscaTipoAtividade}" 
								id="selectBuscaTipoAtividade" styleClass="noborder" /> </td>
	    			<td> <label for="situacaoProjeto"> Tipo de Atividade </label> </td>
	    			<td><h:selectOneMenu id="buscaTipoAcao" styleClass="camposBusca" value="#{consultaPublicaAtividadeExtensao.buscaTipoAtividade}" 
	    	 					onfocus="javascript:$('formBuscaAtividade:selectBuscaTipoAtividade').checked = true;">
							<f:selectItem itemLabel="-- SELECIONE UM TIPO DE AÇÃO --" itemValue="0" />
	    	 				<f:selectItems value="#{tipoAtividadeExtensao.allCombo}" />
	    	 			</h:selectOneMenu>	    	 
	    	 		</td>
	    		</tr>

				<tr>
					<td><h:selectBooleanCheckbox value="#{consultaPublicaAtividadeExtensao.checkBuscaUnidadeProponente}" 
								id="selectBuscaUnidadeProponente" styleClass="noborder" /> </td>
	    			<td><label for="unidade"> Unidade Responsável: </label> </td>
	    			<td>
	    				<h:selectOneMenu id="buscaUnidade" styleClass="camposBusca" value="#{consultaPublicaAtividadeExtensao.buscaUnidade}" 
	    					onfocus="javascript:$('formBuscaAtividade:selectBuscaUnidadeProponente').checked = true;">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE UM DEPARTAMENTO --" />
							<f:selectItems value="#{unidade.unidadesProponentesProjetosCombo}"/> 	
						</h:selectOneMenu>
	    			</td>
	    		</tr>
	    		
				<tr>
					<td><h:selectBooleanCheckbox value="#{consultaPublicaAtividadeExtensao.checkBuscaServidor}" id="selectBuscaServidor"
							 styleClass="noborder" />  </td>
					<td>Coordenador:</td>
					<td>

						<h:inputText value="#{consultaPublicaAtividadeExtensao.membroEquipe.servidor.pessoa.nome}" id="nomeServidor" size="59"
						  styleClass="camposBusca"/>
						<rich:suggestionbox for="nomeServidor" width="450" height="100" minChars="3" id="suggestionNomeServ" 
								suggestionAction="#{servidorAutoCompleteMBean.autocompleteNomeServidor}" var="_servidor" 
								fetchValue="#{_servidor.pessoa.nome}">
								<h:column>
									<h:outputText value="#{_servidor.pessoa.nome}" />
								</h:column>
					        <a4j:support event="onselect" reRender="selectBuscaServidor">
						        <f:param name="apenasAtivos" value="false" />
								<f:setPropertyActionListener value="#{_servidor.id}" target="#{consultaPublicaAtividadeExtensao.membroEquipe.servidor.id}" />
								<f:setPropertyActionListener value="#{true}" target="#{consultaPublicaAtividadeExtensao.checkBuscaServidor}" />
						    </a4j:support>
						</rich:suggestionbox>
					
					</td>
				</tr>
		
				<tr>
					<td><h:selectBooleanCheckbox value="#{consultaPublicaAtividadeExtensao.checkBuscaAno}" id="selectBuscaAno" 
								styleClass="noborder" /></td>
	    			<td><label for="nomeProjeto"> Ano </label> </td>
	    			<td><h:inputText id="buscaAno" value="#{consultaPublicaAtividadeExtensao.buscaAno}" size="10" 
	    				onkeyup="return formatarInteiro(this);"
	    					onfocus="javascript:$('formBuscaAtividade:selectBuscaAno').checked = true;" /></td>
	   			 </tr>	

			</tbody>
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton value="Buscar" action="#{consultaPublicaAtividadeExtensao.localizarPublic }" id="btBuscar" />&nbsp;
						<h:commandButton value="Cancelar" action="#{consultaPublicaAtividadeExtensao.cancelar}" id="limpar" />
	    			</td>
	    		</tr>
			</tfoot>
		</table>
	</h:form>
	
	<br /><br /><br />

	<!-- ######################################################################################################################## -->	
	<!-- Resultado da busca -->
	
	<c:set var="atividades" value="#{consultaPublicaAtividadeExtensao.atividadesLocalizadas}"/>

	<c:if test="${empty atividades}">
		<center><h3><i>Nenhuma ação de extensão localizada</i></h3></center>
	</c:if>

	<c:if test="${not empty atividades}">

		<h:form id="form">
		
			<c:set var="SUBMETIDO" value="<%= String.valueOf(TipoSituacaoProjeto.EXTENSAO_SUBMETIDO) %>" scope="application" />
			<c:set var="CEA" value="<%= String.valueOf(TipoSituacaoProjeto.EXTENSAO_CADASTRO_EM_ANDAMENTO) %>" scope="application" />
	
			<table class="listagem" width="100%">
				<caption>Ações de extensão localizadas (${ fn:length(atividades) })</caption>
		    	<thead>
		      		<tr>
		        		<th>Ano/Título</th>
		        		<th>Tipo</th>
		        		<th>Departamento</th>
		       		</tr>
		 		</thead>
		 		<tbody>
		 		
		 	 		<c:forEach items="#{atividades}" var="item" varStatus="status">
	            		<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
							<td> 
								<h:commandLink value="#{item.anoTitulo}" title="Visualizar Ação" 
										action="#{inscricaoParticipanteAtividadeMBean.visualizarDadosCursoEvento}">
						    		<f:param name="idAtividadeExtensaoSelecionada" value="#{item.id}" />
						    		<f:param name="acao" value="#{consultaPublicaAtividadeExtensao.buscaTipoAtividade}" />
								</h:commandLink>
							</td>
							<td> ${item.tipoAtividadeExtensao.descricao} </td>
							<td>${item.unidade.sigla}</td>
	        			</tr>
	         		</c:forEach>
		          
		 		</tbody>
		 	</table>
		</h:form>

	</c:if>
	
	<!-- ######################################################################################################################### -->
	
	<br />
	<div style="width: 80%; text-align: center; margin: 0 auto;">
		<a href="/sigaa/public/home.jsf" style="color: #404E82;">&lt;&lt; voltar ao menu principal</a>
	</div>
	<br />

	<script type="text/javascript">
		var lista = getEl(document).getChildrenByClassName('descricao')
		for (i = 0; i < lista.size(); i++) {
			lista[i].setDisplayed(false);
		}
	</script>	

	<c:if test="${consultaPublicaAtividadeExtensao.checkBuscaTipoAtividade}">
		<script type="text/javascript">
			$('formBuscaAtividade:selectBuscaTipoAtividade').checked = true;
		</script>
	</c:if>

</f:view>
<%@include file="/public/include/rodape.jsp" %>