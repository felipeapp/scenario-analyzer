<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%> 
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<script type="text/javascript" src="/shared/loadScript?src=javascript/jquery.tablesorter.min.js"></script>

<link rel="stylesheet" type="text/css" href="/shared/css/tablesorter/style.css" />

<%@page import="br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto"%>

<style>
	.em_andamento {
		color: gray;
	}
</style>

<f:view>
<%@include file="/portais/docente/menu_docente.jsp"%>

	<h:messages/>
	<h2> <ufrn:subSistema /> > Consultar Ações Acadêmicas Integradas</h2> 

	<%@include file="/projetos/form_busca_projetos.jsp"%>

	<c:set var="acoes" value="#{buscaAcaoAssociada.resultadosBusca}"/>

	<c:if test="${not empty acoes}">
	
	
	<div class="infoAltRem">
		 <h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Ação	
		 <h:graphicImage value="/img/extensao/financiamento_faex.png" style="overflow: visible;width:17px;height:17px;"/>: Visualizar Orçamento Consolidado
		 <h:graphicImage url="/img/extensao/printer.png" />: Versão para impressão 
	</div>

	<br/>

	<h:form id="form_lista">
		<c:set var="SUBMETIDO" value="<%= String.valueOf(TipoSituacaoProjeto.PROJETO_BASE_SUBMETIDO) %>" scope="application"/>
		<c:set var="CADASTRO_EM_ANDAMENTO" value="<%= String.valueOf(TipoSituacaoProjeto.PROJETO_BASE_CADASTRO_EM_ANDAMENTO) %>" scope="application"/>
	
		 <table class="listagem tablesorter" id="listagem">
		    <caption>Ações acadêmicas localizadas (${ fn:length(acoes) })</caption>
	
	        <thead>
		      	<tr>
		      		<th width="6%">Nº Inst.</th>
		        	<th>Título</th>
		        	<th>Unidade</th>
		        	<th>Situação</th>
		        	<th>&nbsp;</th>
		        	<th>&nbsp;</th>
		        	<th>&nbsp;</th>
		        </tr>
		 	</thead>
		 	<tbody>
		 		
		 		<a4j:repeat value="#{acoes}" var="acao" rowKeyVar="index">
		 			<tr>
		 			<h:panelGroup rendered="#{index % 2 == 0}">
		 				<tr class="linhaPar">
		 			</h:panelGroup>
		 			<h:panelGroup rendered="#{index % 2 != 0}">
		 				<tr class="linhaImpar">
		 			</h:panelGroup>
		 			
		 			<h:panelGroup rendered="#{acao.situacaoProjeto.id == CADASTRO_EM_ANDAMENTO}">
		 				<td class="em_andamento" align="center"> 
		 					<h:outputText value="#{acao.numeroInstitucional}">
		 						<f:convertNumber pattern="0000"/> 
		 					</h:outputText>/<h:outputText value="#{acao.ano}"/>
		 				</td>
						<td class="em_andamento">
	                     	<h:outputText value="#{acao.titulo}" /> 
							<h:outputText value="<br/><i>Coordenador(a): #{acao.coordenador.pessoa.nome}</i>" rendered="#{not empty acao.coordenador}" escape="false"/> 							
	                    </td>
	                    <td class="em_andamento"><h:outputText value="#{acao.unidade.sigla}"/></td>
						<td class="em_andamento"><h:outputText value="#{acao.situacaoProjeto.descricao}"/></td>
		 			</h:panelGroup>
		 			<h:panelGroup rendered="#{acao.situacaoProjeto.id != CADASTRO_EM_ANDAMENTO}">
		 				<td align="center"> 
		 					<h:outputText value="#{acao.numeroInstitucional}">
		 						<f:convertNumber pattern="0000"/> 
		 					</h:outputText>/<h:outputText value="#{acao.ano}"/>
		 				</td>
						<td>
	                     	<h:outputText value="#{acao.titulo}" /> 
							<h:outputText value="<br/><i>Coordenador(a): #{acao.coordenador.pessoa.nome}</i>" rendered="#{not empty acao.coordenador}" escape="false"/> 							
	                    </td>
	                    <td><h:outputText value="#{acao.unidade.sigla}"/></td>
						<td><h:outputText value="#{acao.situacaoProjeto.descricao}"/></td>
		 			</h:panelGroup>
		               
						<%-- comandos liberados inclusive para ALUNOS --%>
						<td>					
							<h:commandLink action="#{ projetoBase.view }" id="link_projetoBase_view">
							    <f:param name="id" value="#{acao.id}"/>
							    <h:graphicImage url="/img/view.gif" alt="Visualizar Ação" title="Visualizar Ação"/>
							</h:commandLink>
						</td>

						<td>					
							<h:commandLink action="#{ projetoBase.viewOrcamento }" id="viewOrcamentoProjeto">
							    <f:param name="id" value="#{acao.id}"/>
							    <h:graphicImage url="/img/extensao/financiamento_faex.png" alt="Visualizar Orçamento Consolidado" 
							    	style="overflow: visible;width:18px;height:18px;" title="Visualizar Orçamento Consolidado" />
							</h:commandLink>
						</td>

						<td>
							<h:commandLink action="#{ projetoBase.viewImpressao }" id="impressao">
								<f:param name="id" value="#{acao.id}"/>
							 	<h:graphicImage url="/img/extensao/printer.png" alt="Versão para impressão" title="Versão para impressão"/>
							</h:commandLink>
						</td>

		              </tr>
		          </a4j:repeat>
		          
		 	</tbody>
		 </table>
		 <rich:jQuery selector="#listagem" query="tablesorter( {headers: {4: { sorter: false } } });" timing="onload" /> 
	</h:form>

	</c:if>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>