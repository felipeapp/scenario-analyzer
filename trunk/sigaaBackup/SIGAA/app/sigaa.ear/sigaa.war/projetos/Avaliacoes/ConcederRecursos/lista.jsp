<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<script type="text/javascript" src="/shared/loadScript?src=javascript/jquery.tablesorter.min.js"></script>
<link rel="stylesheet" type="text/css" href="/shared/css/tablesorter/style.css" />

<%@page import="br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto"%>

<style>
	.em_andamento {
		color: gray;
	}
</style>


<f:view>
	<h2><ufrn:subSistema /> > Conceder Recursos </h2>

 	<a4j:keepAlive beanName="alteracaoProjetoMBean" />

	<%@include file="/projetos/form_busca_projetos.jsp"%>

	<c:set var="acoes" value="#{buscaAcaoAssociada.resultadosBusca}"/>

	<c:if test="${not empty acoes}">

		<div class="infoAltRem">
		    <h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Ação
		    <h:graphicImage value="/img/projeto/orcamento.png" width="16px" height="16px" style="overflow: visible;" rendered="#{acesso.comissaoIntegrada}"/> <h:outputText rendered="#{acesso.comissaoIntegrada}" value=": Conceder Recursos" escape="false"/>
		</div>

	<h:form prependId="true">
	
		<c:set var="SUBMETIDO" value="<%= String.valueOf(TipoSituacaoProjeto.PROJETO_BASE_SUBMETIDO) %>" scope="application"/>
		<c:set var="CADASTRO_EM_ANDAMENTO" value="<%= String.valueOf(TipoSituacaoProjeto.PROJETO_BASE_CADASTRO_EM_ANDAMENTO) %>" scope="application"/>
	
		 <table class="listagem tablesorter" id="listagem">
		    <caption>Ações acadêmicas localizadas (${ fn:length(acoes) })</caption>
	
		      <thead>
		      	<tr>
		        	<th>Class.</th>
		        	<th>Média</th>
		      		<th>Nº Inst.</th>
		        	<th>Título</th>
		        	<th>Unidade</th>
		        	<th>Situação</th>
		        	<th>&nbsp;</th>
		        	<th>&nbsp;</th>
		        </tr>
		 	</thead>
		 	<tbody>
		 	
			 	 <c:forEach items="#{acoes}" var="acao" varStatus="status">
		               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
		
							<td ${acao.situacaoProjeto.id == CADASTRO_EM_ANDAMENTO ? 'class="em_andamento"':''} align="right">${acao.classificacao}</td>
							<td ${acao.situacaoProjeto.id == CADASTRO_EM_ANDAMENTO ? 'class="em_andamento"':''} align="right"><fmt:formatNumber value="${acao.media}" pattern="#0.0"/></td>
							<td ${acao.situacaoProjeto.id == CADASTRO_EM_ANDAMENTO ? 'class="em_andamento"':''} align="center"> <fmt:formatNumber value="${acao.numeroInstitucional}"  pattern="0000"/>/${acao.ano}</td>
		                    <td ${acao.situacaoProjeto.id == CADASTRO_EM_ANDAMENTO ? 'class="em_andamento"':''}>
		                     	${acao.titulo} 
								<h:outputText value="<br/><i>Coordenador(a): #{acao.coordenador.pessoa.nome}</i>" rendered="#{not empty acao.coordenador}" escape="false"/> 							
		                    </td>
		                    <td ${acao.situacaoProjeto.id == CADASTRO_EM_ANDAMENTO ? 'class="em_andamento"':''}>${acao.unidade.sigla}</td>
							<td ${acao.situacaoProjeto.id == CADASTRO_EM_ANDAMENTO ? 'class="em_andamento"':''}>${acao.situacaoProjeto.descricao}</td>
							
							<td width="2%">					
								<h:commandLink title="Visualizar Ação" action="#{ projetoBase.view }">
								    <f:param name="id" value="#{acao.id}"/>
			                   		<h:graphicImage url="/img/view.gif"/>
								</h:commandLink>
							</td>
							
							<td width="2%">
								<h:commandLink title="Conceder Recursos" action="#{ alteracaoProjetoMBean.iniciarConcederRecursos }" immediate="true" rendered="#{acesso.comissaoIntegrada && acao.permitidoConcederRecursos}">
								        <f:param name="id" value="#{acao.id}"/>
							    		<h:graphicImage value="/img/projeto/orcamento.png" width="16px" height="16px" />
								</h:commandLink>
							</td>
							
		              </tr>
		          </c:forEach>
		          
		 	</tbody>
		 </table>
		 <rich:jQuery selector="#listagem" query="tablesorter( {headers: { 6: { sorter: false },7: { sorter: false } } });" timing="onload" /> 
	</h:form>

	</c:if>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>