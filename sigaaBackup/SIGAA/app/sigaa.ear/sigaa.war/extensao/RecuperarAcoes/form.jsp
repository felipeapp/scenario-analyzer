<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
	<h2> <ufrn:subSistema/> > Recuperar Ações de Extensão Excluídas</h2>
	<h:form id="formConsulta">
		<div class="descricaoOperacao">
			<b>Atenção:</b>
			<ul>
			<li> Serão listadas somente as ações que foram removidas. </li>
			<li>Ações recuperadas passam a ter a situação anterior a remoção. </li>
			</ul>
		</div>
		<table class="formulario" width="75%">
			<caption class="formulario">Dados da Busca</caption>
			<tbody>
				
				
				<tr>
					<td> <h:selectBooleanCheckbox value="#{recuperarAcoes.checkBuscaAno}" id="selectBuscaAno" styleClass="noborder" /> </td>
			    	<td> <label for="ano"> Ano: </label> </td>
			    	<td> <h:inputText id="buscaAno" value="#{recuperarAcoes.buscaAno}" maxlength="4" size="4" 
			    		onkeyup="return formatarInteiro(this)" onfocus="javascript:$('formConsulta:selectBuscaAno').checked = true;" />
			    	</td>
			    </tr>
				<tr>
					<td><h:selectBooleanCheckbox value="#{recuperarAcoes.checkBuscaTitulo}" id="selectBuscaTitulo" styleClass="noborder"/> </td>
					<td><label>Título:</label></td>
					<td><h:inputText value="#{recuperarAcoes.buscaTitulo}" onfocus="$('formConsulta:selectBuscaTitulo').checked = true;" size="80"/> </td>
				</tr>
				<tr>
					<td><h:selectBooleanCheckbox value="#{recuperarAcoes.checkBuscaServidor}" 
							id="selectBuscaServidor" styleClass="noborder" />
					</td>
					<td><label>Servidor:</label></td>
					<td>
					<h:inputHidden id="buscaServidor" value="#{recuperarAcoes.membroEquipe.servidor.id}"></h:inputHidden>
					<h:inputText id="buscaNome"	value="#{recuperarAcoes.membroEquipe.servidor.pessoa.nome}" size="80" 
						onfocus="javascript:$('formConsulta:selectBuscaServidor').checked = true;" /> 
						<ajax:autocomplete
							source="formConsulta:buscaNome" target="formConsulta:buscaServidor"
							baseUrl="/sigaa/ajaxServidor" className="autocomplete"
							indicator="indicator" minimumCharacters="3" parameters="tipo=todos"
							parser="new ResponseXmlToHtmlListParser()" /> 
						<span id="indicator" style="display:none;"> <img src="/sigaa/img/indicator.gif" /> </span>
					</td>
				</tr>
	    
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton id="buscarAcoes" value="Buscar" action="#{recuperarAcoes.buscarAcoes}" /> 
						<h:commandButton value="Cancelar" action="#{recuperarAcoes.cancelar}" id="cancelar" immediate="true" onclick="#{confirm}" />
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<br />
	<br />
	<c:if test="${not empty recuperarAcoes.atividades and recuperarAcoes.realizouBusca}">
		<%@include file="/extensao/RecuperarAcoes/listaIn.jsp"%>
	</c:if>
	<c:if test="${empty recuperarAcoes.atividades and recuperarAcoes.realizouBusca}" >
			<center><font color='red'>Nenhuma ação de extensão encontrada com os dados passados.</font></center>
	</c:if>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>