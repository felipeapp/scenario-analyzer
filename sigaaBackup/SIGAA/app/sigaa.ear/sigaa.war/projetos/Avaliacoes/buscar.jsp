<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<style type="text/css">
       .direita {text-align: right !important;border:none !important; }
       .esquerda {text-align: left !important;border:none !important; }
       .centro {text-align: center !important;border:none !important; }
	   .dr-table-header-continue {background: #DEDFE3; }
       .dr-table-cell {border:none;}
       .dr-subtable-cell{border:none;}
       .caption {
       			margin: 0 auto !important;
				padding: 3px 0 !important;
				font-family:Verdana;
  				font-style:normal;
  				font-variant:small-caps;
  				font-weight:bold;
				color: #FFF;
				letter-spacing: 1px !important;
				text-align: center !important;
				background: url(/shared/img/bg_caption.gif) center repeat-x;
       	}
</style>

<f:view>

	<h2><ufrn:subSistema /> > Buscar Avaliações </h2>

	<h:form id="form">
	<table class="formulario" width="90%">
	<caption>Busca por Avaliações de Projetos</caption>  
	<tbody>
		<tr>
			<td width="1%"> <h:selectBooleanCheckbox value="#{buscaAvaliacoesProjetosBean.checkBuscaTitulo}" id="selectBuscaTitulo" styleClass="noborder" /> </td>
	    	<td width="16%"> <label for="buscaTitulo">Título do Projeto:</label> </td>
	    	<td> <h:inputText id="buscaTitulo" value="#{buscaAvaliacoesProjetosBean.buscaTitulo}" size="70" 
	    		onfocus="javascript:$('form:selectBuscaTitulo').checked = true;" />
	    	</td>
	    </tr>

		<tr>
			<td> <h:selectBooleanCheckbox value="#{buscaAvaliacoesProjetosBean.checkBuscaAno}" id="selectBuscaAno" styleClass="noborder" /> </td>
	    	<td> <label for="buscaAno">Ano:</label> </td>
	    	<td> <h:inputText id="buscaAno" value="#{buscaAvaliacoesProjetosBean.buscaAno}" maxlength="4" size="4" 
	    		onkeyup="return formatarInteiro(this)" onfocus="javascript:$('form:selectBuscaAno').checked = true;" />
	    	</td>
	    </tr>
	    
		<tr>
			<td><h:selectBooleanCheckbox value="#{buscaAvaliacoesProjetosBean.checkBuscaUnidade}" id="selectBuscaUnidade" styleClass="noborder"/> </td>
	    	<td> <label for="buscaUnidade">Unidade Proponente:</label> </td>
	    	<td>
				<h:selectOneMenu id="buscaUnidade" value="#{buscaAvaliacoesProjetosBean.buscaUnidade}" style="width: 90%" 
					onfocus="javascript:$('form:selectBuscaUnidade').checked = true;"
					onchange="javascript:setarLabel(this,'nomeUnidade');">
					<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />					
					<f:selectItems value="#{unidade.unidadesProponentesProjetosCombo}"/>
				</h:selectOneMenu>
	    	 </td>
	    </tr>

		<tr>
			<td><h:selectBooleanCheckbox value="#{buscaAvaliacoesProjetosBean.checkBuscaAvaliador}" id="selectBuscaAvaliador" styleClass="noborder" />
			</td>
			<td><label for="buscaAvaliador">Avaliador(a):</label></td>
			<td>
				<h:inputHidden id="buscaAvaliador" value="#{buscaAvaliacoesProjetosBean.buscaAvaliador.usuario.id}" />
				<h:inputText id="buscaNome"	value="#{buscaAvaliacoesProjetosBean.buscaAvaliador.usuario.pessoa.nome}" size="70" onfocus="javascript:$('form:selectBuscaAvaliador').checked = true;" />
				<ajax:autocomplete source="form:buscaNome" target="form:buscaAvaliador"
					baseUrl="/sigaa/ajaxUsuario" className="autocomplete"
					indicator="indicatorDocente" minimumCharacters="3" parameters="docente=true"
					parser="new ResponseXmlToHtmlListParser()" />
					<span id="indicatorDocente" style="display:none; "><img src="/sigaa/img/indicator.gif"  alt="Carregando..." title="Carregando..."/></span>					
			</td>
		</tr>

		<tr>
			<td><h:selectBooleanCheckbox value="#{buscaAvaliacoesProjetosBean.checkBuscaSituacao}" id="selectBuscaSituacao" styleClass="noborder" />  
			</td>
	    	<td> <label for="buscaSituacao"> Situação da Avaliação: </label> </td>
	    	<td>
	    		<h:selectOneMenu id="buscaSituacao" value="#{buscaAvaliacoesProjetosBean.buscaSituacao}" 
	    			onfocus="javascript:$('form:selectBuscaSituacao').checked = true;"
	    			onchange="javascript:setarLabel(this,'nomeSituacao');">
					<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
	    	 		<f:selectItems value="#{buscaAvaliacoesProjetosBean.allSituacaoAvaliacaoCombo}" />
 			 	</h:selectOneMenu>
	    	 </td>
		</tr>

	</tbody>
	<tfoot>
		<tr>
			<td colspan="3">
				<h:commandButton value="Buscar" action="#{ buscaAvaliacoesProjetosBean.buscar }" id="btBuscar" />
				<h:commandButton value="Cancelar" action="#{ buscaAvaliacoesProjetosBean.cancelar }" onclick="#{confirm}" id="btCancelar" />
	    	</td>
	    </tr>
	</tfoot>
	</table>

	<br/>

	<center>
		<div class="infoAltRem">
			<h:graphicImage value="/img/view.gif"style="overflow: visible;"/>: Visualizar Avaliação
			<h:graphicImage value="/img/delete.gif"style="overflow: visible;"/>: Remover Avaliação
		</div>
	</center>
	
  	<rich:dataTable id="dtAvaliacoesEncontradas"  value="#{buscaAvaliacoesProjetosBean.projetos}" 
 		var="projeto" binding="#{buscaAvaliacoesProjetosBean.uiData}" width="100%"
 		rendered="#{not empty buscaAvaliacoesProjetosBean.projetos}" style="background: #C8D5EC; padding: 2px 0 2px 5px;"
 		columnClasses="esquerda, esquerda, esquerda, direita, esquerda, centro, centro ">
 		
		<f:facet name="header">
			<rich:columnGroup columnClasses="esquerda, esquerda, esquerda, direita, esquerda, centro, centro ">
				<rich:column colspan="7" styleClass="caption"><h:outputText id="labelTituloBusca" value="Lista de Avaliações Encontradas (#{buscaAvaliacoesProjetosBean.totalAvaliacoesLocalizadas})" /></rich:column>
				<rich:column breakBefore="true"><h:outputText id="labelAvaliador" value="Avaliador(a)"/></rich:column>
				<rich:column><h:outputText value="Tipo de Avaliação"/></rich:column>
				<rich:column><h:outputText value="Comissão"/></rich:column>
				<rich:column><h:outputText value="Avaliação"/></rich:column>
				<rich:column><h:outputText value="Situação"/></rich:column>
				<rich:column></rich:column>
			</rich:columnGroup>
		</f:facet>
		
		<rich:column colspan="7">
			<h:commandLink title="Visualizar Projeto" action="#{ projetoBase.view }" immediate="true" id="lnkViewProjeto">
		        <f:param name="id" value="#{projeto.id}"/>
				<h:outputText value="<b>#{projeto.anoTitulo}</b>" escape="false"/>
			</h:commandLink>
		</rich:column>
		
		<rich:subTable value="#{projeto.avaliacoes}"  var="avaliacao" rowClasses="linhaPar, linhaImpar" 
			binding="#{buscaAvaliacoesProjetosBean.uiData}" columnClasses="esquerda, esquerda, esquerda, direita, esquerda, centro, centro "
			rowKeyVar="keyVar" id="subTableAvaliacoes">

			<t:column>
				<h:outputText value="#{avaliacao.avaliador.pessoa.nome}" />
			</t:column>
	
			<t:column>
				<h:outputText value="#{avaliacao.distribuicao.modeloAvaliacao.tipoAvaliacao.descricao}" />		
			</t:column>
			
			<t:column>
				<h:outputText value="#{avaliacao.distribuicao.tipoAvaliador.descricao}" />		
			</t:column>
	
			<t:column>
				<h:outputText value="#{avaliacao.nota}">
					<f:convertNumber pattern="##0.0"/>
				</h:outputText>		
			</t:column>
	
			<t:column>
				<h:outputText value="#{avaliacao.situacao.descricao}" />		
			</t:column>
	
			<t:column>
				<h:commandButton action="#{buscaAvaliacoesProjetosBean.view}" image="/img/view.gif" 
					value="Visualizar Avaliação" id="btViewAvaliacao" title="Visualizar Avaliação"/>
				<h:commandButton action="#{buscaAvaliacoesProjetosBean.preRemover}" image="/img/delete.gif" 
					value="Remover Avaliação" id="btRemoverAvaliacao" title="Remover Avaliação"/>
			</t:column>
			
		</rich:subTable>
		
 	</rich:dataTable>
	<center><h:outputText  value="<i>Nenhuma avaliação foi encontrada.</i>" rendered="#{empty buscaAvaliacoesProjetosBean.projetos}" escape="false"/></center>

	</h:form>
</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>	