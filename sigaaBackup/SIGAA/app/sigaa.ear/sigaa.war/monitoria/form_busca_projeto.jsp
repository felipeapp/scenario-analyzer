<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

 	<h:form id="formBusca">
 	
	<h:outputText value="#{projetoMonitoria.create}" />	

	<table class="formulario" width="90%">
	<caption>Busca por Projetos</caption>
	<tbody>
		<tr>
			<td>
				<h:selectBooleanCheckbox value="#{projetoMonitoria.checkBuscaTitulo}" 
					id="selectBuscaTitulo" styleClass="noborder" />
			</td>
	    	<td> 
	    		<label for="nomeProjeto"> Título do Projeto: </label> 
	    	</td>
	    	<td> 
	    		<h:inputText value="#{projetoMonitoria.buscaNomeProjeto}" size="70" 
	    			onchange="javascript:$('formBusca:selectBuscaTitulo').checked = true;" />
	    	</td>
	    </tr>

	    <tr>
			<td>
				<h:selectBooleanCheckbox value="#{projetoMonitoria.checkBuscaTipoProjeto}" 
					id="selectBuscaTipoProjeto" styleClass="noborder" />
			</td>
	    	<td> 
	    		<label for="situacaoProjeto"> Tipo de Projeto: </label> 
	    	</td>
	    	<td>
	    	 	<h:selectOneMenu value="#{projetoMonitoria.buscaTipoProjeto.id}" style="width: 550px"
	    	 		onchange="javascript:$('formBusca:selectBuscaTipoProjeto').checked = true;">
					<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
					<f:selectItems value="#{projetoMonitoria.allTipoProjetoEnsinoCombo}" />
 			 	</h:selectOneMenu>
	    	 </td>
	    </tr>    

	    <tr>
			<td>
				<h:selectBooleanCheckbox value="#{projetoMonitoria.checkBuscaServidor}" 
					id="selectBuscaServidor" styleClass="noborder" />
			</td>
	    	<td> 
	    		<label for="nomeProjeto"> Servidor Envolvido: </label> 
	    	</td>
	    	<td> 
				<h:inputText value="#{projetoMonitoria.servidor.pessoa.nome}" id="nome" size="60" 
					onchange="javascript:$('formBusca:selectBuscaServidor').checked = true;" />
				<h:inputHidden value="#{projetoMonitoria.servidor.id}" id="idServidor" />
	
				<ajax:autocomplete source="formBusca:nome" target="formBusca:idServidor" 
					baseUrl="/sigaa/ajaxServidor" className="autocomplete" indicator="indicator" 
					minimumCharacters="3" parameters="tipo=todos,inativos=true"
					parser="new ResponseXmlToHtmlListParser()"/>	
				<span id="indicator" style="display:none;"> 
					<img src="/sigaa/img/indicator.gif" /> 
				</span>	    	
	    	</td>
	    </tr>

	    <tr>
			<td>
				<h:selectBooleanCheckbox value="#{projetoMonitoria.checkBuscaEdital}" 
					id="selectBuscaEdital" styleClass="noborder" />
			</td>
	    	<td> 
	    		<label for="nomeProjeto"> Edital do Projeto: </label> 
	    	</td>
	    	<td>
	    		<h:selectOneMenu onchange="javascript:$('formBusca:selectBuscaEdital').checked = true;" 
	    			value="#{projetoMonitoria.edital.id}" style="width: 550px">
					<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
					<f:selectItems value="#{editalMonitoria.allCombo}"/>
				</h:selectOneMenu>
			</td>
	    </tr>
		
		<tr>
			<td>
				<h:selectBooleanCheckbox value="#{projetoMonitoria.checkBuscaAno}" 
					id="selectBuscaAno" styleClass="noborder" />
			</td>
	    	<td> 
	    		<label for="anoProjeto"> Ano do Projeto: </label> 
	    	</td>
	    	<td> 
	    		<h:inputText value="#{projetoMonitoria.buscaAnoProjeto}" size="10" 
	    			onchange="javascript:$('formBusca:selectBuscaAno').checked = true;" />
	    	</td>
	    </tr>
		
		<tr>
			<td>
				<h:selectBooleanCheckbox value="#{projetoMonitoria.checkBuscaSituacao}" 
					id="selectBuscaSituacao" styleClass="noborder" />
			</td>
	    	<td> 
	    		<label for="situacaoProjeto"> Situação do Projeto: </label> 
	    	</td>
	    	<td>
	    		<h:selectOneMenu value="#{projetoMonitoria.buscaSituacaoProjeto.id}" 
	    			onchange="javascript:$('formBusca:selectBuscaSituacao').checked = true;">
					<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
					<f:selectItems value="#{projetoMonitoria.tipoSituacaoProjetoCombo}" />
 			 	</h:selectOneMenu>
	    	 </td>
	    </tr>
	    <c:if test="${acesso.monitoria}">
	    <tr>
			<td>
				<h:selectBooleanCheckbox value="#{projetoMonitoria.checkBuscaSemRelatorio}" 
					id="selectBuscaSemRelatorio" styleClass="noborder" />
			</td>
	    	<td> 
	    		<label for="formBusca:selectBuscaSemRelatorio"> Projetos Sem Relatório: </label> 
	    	</td>
	    	<td>
	    		<h:selectOneMenu value="#{projetoMonitoria.tipoRelatorio}" id="semRelatorio"
	    			onchange="javascript:$('formBusca:selectBuscaSemRelatorio').checked = true;">
	    			<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
					<f:selectItem itemValue="1" itemLabel="PARCIAL" />
					<f:selectItem itemValue="2" itemLabel="FINAL" />
 			 	</h:selectOneMenu>
	    	 </td>
	    </tr>
	    
	    <tr>
			<td>
				<h:selectBooleanCheckbox value="#{projetoMonitoria.checkBuscaProjetoAssociado}" 
					id="selectBuscaAssociado" styleClass="noborder" />
			</td>
	    	<td> 
	    		<label for="formBusca:selectBuscaAssociado"> Dimensão Acadêmica: </label> 
	    	</td>
	    	<td>
	    		<h:selectOneMenu value="#{projetoMonitoria.buscaProjetoAssociado}" id="associado"
	    			onchange="javascript:$('formBusca:selectBuscaAssociado').checked = true;">
	    			<f:selectItem itemValue="" itemLabel="-- SELECIONE --" />
					<f:selectItem itemValue="true" itemLabel="PROJETO ASSOCIADO" />
					<f:selectItem itemValue="false" itemLabel="PROJETO ISOLADO" />
 			 	</h:selectOneMenu>
	    	 </td>
	    </tr>
	    
	    </c:if>
   		<tr>
   			<td>
				<h:selectBooleanCheckbox value="#{projetoMonitoria.checkBuscaCentro}" 
					id="selectBuscaCentro" styleClass="noborder" />
			</td>
			<td> 
				<label for="centro">Centro do Projeto:</label>
			</td>
			<td>
				<h:selectOneMenu id="centro" value="#{projetoMonitoria.buscaCentro.id}" style="width: 400px" 
					onchange="javascript:$('formBusca:selectBuscaCentro').checked = true;">
					<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
					<f:selectItems value="#{unidade.centrosEspecificasEscolas}" />
				</h:selectOneMenu>
			</td>
		</tr>
		
		<tr>
			<td>
				<h:selectBooleanCheckbox value="#{projetoMonitoria.checkBuscaComponente}" 
					id="selectBuscaComponente" styleClass="noborder" />
			</td>
	    	<td> 
	    		<label for="situacaoProjeto"> Componente Curricular: </label> 
	    	</td>
	    	<td>
				<table>
					<tr>
						<td>Código do Componente: </td>
						<td>
							<h:inputText id="codigo" value="#{projetoMonitoria.componente.disciplina.codigo}" 
								size="15" onchange="javascript:$('formBusca:selectBuscaComponente').checked = true;" /> 
						</td>
					</tr>
					<tr>
						<td>
							<input type="hidden" id="id" name="id" value="0"/> Nome do Componente: 
						</td>
						<td>
							<input type="text" id="disciplina" name="disciplina" size="50" 
								onchange="javascript:$('formBusca:selectBuscaComponente').checked = true;"/>
							<span id="indicator" style="display:none;"> 
								<img src="/sigaa/img/indicator.gif" /> 
							</span>
						</td>
					</tr>
				</table>

				<ajax:autocomplete source="disciplina" target="id"
					baseUrl="/sigaa/ajaxDisciplina" className="autocomplete"
					indicator="indicator" minimumCharacters="3" parameters="nivel=G"
					parser="new ResponseXmlToHtmlListParser()" />
	    	 </td>
	    </tr>
	    
	    <c:if test="${acesso.monitoria}">
			<tr>
				<td>
					<h:selectBooleanCheckbox value="#{projetoMonitoria.checkBuscaUsuario}" 
						id="selectBuscaUsuario" styleClass="noborder" />
				</td>
		    	<td> 
		    		<label for="nomeProjeto"> Usuário que cadastrou: </label> 
		    	</td>
		    	<td> 
					<h:inputText value="#{projetoMonitoria.usuarioBusca.pessoa.nome}" id="nomeUsuario" 
						size="60" onchange="javascript:$('formBusca:selectBuscaUsuario').checked = true;" />
					<h:inputHidden value="#{projetoMonitoria.usuarioBusca.id}" id="idUsuario" />
		
					<ajax:autocomplete source="formBusca:nomeUsuario" target="formBusca:idUsuario"
						baseUrl="/sigaa/ajaxServidor" className="autocomplete"
						indicator="indicatorUsuario" minimumCharacters="3" parameters="tipo=todos,inativos=true"
						parser="new ResponseXmlToHtmlListParser()" />
		
					<span id="indicatorUsuario" style="display:none;"> 
						<img src="/sigaa/img/indicator.gif" /> 
					</span>	    	
		    	</td>
		    </tr>	 
	    </c:if>   

	</tbody>
	<tfoot>
		<tr>
			<td colspan="3">
				<h:commandButton value="Buscar" action="#{projetoMonitoria.iniciarLocalizacao}" />
				<h:commandButton value="Cancelar" action="#{projetoMonitoria.cancelar}" onclick="#{confirm}" />
	    	</td>
	    </tr>
	</tfoot>
	</table>

	</h:form>
	
	<br />
	<br />
	