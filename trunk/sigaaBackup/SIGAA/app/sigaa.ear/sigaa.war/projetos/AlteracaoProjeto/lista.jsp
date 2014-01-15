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
	<h2><ufrn:subSistema /> > Gerenciar A��o Acad�mica </h2>

 	<a4j:keepAlive beanName="alteracaoProjetoMBean" />

	<%@include file="/projetos/form_busca_projetos.jsp"%>

	<c:set var="acoes" value="#{buscaAcaoAssociada.resultadosBusca}"/>

	<c:if test="${empty acoes}">
		<center><i> Nenhuma A��o Acad�mica Integrada foi localizada </i></center>
	</c:if>


	<c:if test="${not empty acoes}">

		<div class="infoAltRem">
		    <h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar A��o
		    <h:graphicImage value="/img/alterar.gif" style="overflow: visible;" rendered="#{acesso.comissaoIntegrada}"/> <h:outputText rendered="#{acesso.comissaoIntegrada}" value=": Alterar Projeto Base" escape="false"/>
		    <h:graphicImage value="/img/table_go.png" style="overflow: visible;" rendered="#{acesso.comissaoIntegrada}"/> <h:outputText rendered="#{acesso.comissaoIntegrada}" value=": Alterar A��es Vinculadas" escape="false"/>
		    <br/>
		    <h:graphicImage value="/img/alterar_old.gif" style="overflow: visible;" rendered="#{acesso.comissaoIntegrada}"/> <h:outputText rendered="#{acesso.comissaoIntegrada}" value=": Alterar Situa��o" escape="false"/>
		    <h:graphicImage url="/img/table_refresh.png" width="16" height="16" style="overflow: visible;" rendered="#{acesso.comissaoIntegrada}"/> <h:outputText rendered="#{acesso.comissaoIntegrada}" value=": Alterar Or�amento" escape="false"/>
		    <h:graphicImage url="/img/projeto/orcamento.png" width="16" height="16" style="overflow: visible;" rendered="#{acesso.comissaoIntegrada}"/> <h:outputText rendered="#{acesso.comissaoIntegrada}" value=": Visualizar Or�amento" escape="false"/>
		    <br/>
		    <h:graphicImage url="/img/extensao/businessman_refresh.png" width="16" height="16" style="overflow: visible;" rendered="#{acesso.comissaoIntegrada}"/> <h:outputText rendered="#{acesso.comissaoIntegrada}" value=": Gerenciar Membros" escape="false"/>
		    <h:graphicImage value="/img/delete.gif" style="overflow: visible;" rendered="#{acesso.comissaoIntegrada}"/> <h:outputText rendered="#{acesso.comissaoIntegrada}" value=": Remover" escape="false"/>		    
		    <br/>
		</div>

	<h:form>
	
		<c:set var="SUBMETIDO" value="<%= String.valueOf(TipoSituacaoProjeto.PROJETO_BASE_SUBMETIDO) %>" scope="application"/>
		<c:set var="CADASTRO_EM_ANDAMENTO" value="<%= String.valueOf(TipoSituacaoProjeto.PROJETO_BASE_CADASTRO_EM_ANDAMENTO) %>" scope="application"/>
	
		 <table class="listagem tablesorter" id="listagem">
		    <caption>A��es acad�micas localizadas (${ fn:length(acoes) })</caption>
	
		      <thead>
		      	<tr>
		      		<th>N� Inst.</th>
		        	<th>T�tulo</th>
		        	<th>Unidade</th>
		        	<th>Situa��o</th>
		        	<th>&nbsp;</th>
		        	<th>&nbsp;</th>
		        	<th>&nbsp;</th>
		        	<th>&nbsp;</th>
		        	<th>&nbsp;</th>
		        	<th>&nbsp;</th>		        	
		        	<th>&nbsp;</th>
		        	<th>&nbsp;</th>
		        </tr>
		 	</thead>
		 	<tbody>
		 	
			 	 <c:forEach items="#{acoes}" var="acao" varStatus="status">
		               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
		
							<td ${acao.situacaoProjeto.id == CADASTRO_EM_ANDAMENTO ? 'class="em_andamento"':''} align="center"> <fmt:formatNumber value="${acao.numeroInstitucional}"  pattern="0000"/>/${acao.ano}</td>
		                    <td ${acao.situacaoProjeto.id == CADASTRO_EM_ANDAMENTO ? 'class="em_andamento"':''}>
		                     	${acao.titulo} 
								<h:outputText value="<br/><i>Coordenador(a): #{acao.coordenador.pessoa.nome}</i>" rendered="#{not empty acao.coordenador}" escape="false"/> 							
		                    </td>
		                    <td ${acao.situacaoProjeto.id == CADASTRO_EM_ANDAMENTO ? 'class="em_andamento"':''}>${acao.unidade.sigla}</td>
							<td ${acao.situacaoProjeto.id == CADASTRO_EM_ANDAMENTO ? 'class="em_andamento"':''}>${acao.situacaoProjeto.descricao}</td>
							
							<td width="2%">					
								<h:commandLink title="Visualizar A��o" action="#{ projetoBase.view }">
								    <f:param name="id" value="#{acao.id}"/>
			                   		<h:graphicImage url="/img/view.gif"/>
								</h:commandLink>
							</td>
							
							<td width="2%">
								<h:commandLink title="Alterar Projeto Base" action="#{ alteracaoProjetoMBean.alterarProjetoBase }" immediate="true" rendered="#{acesso.comissaoIntegrada}">
								        <f:param name="id" value="#{acao.id}"/>
							    		<h:graphicImage url="/img/alterar.gif" />
								</h:commandLink>
							</td>
											
							<td width="2%">
								<h:commandLink title="Alterar A��es Vinculadas" action="#{ alteracaoProjetoMBean.alterarAcoesVinculadas }" immediate="true" rendered="#{acesso.comissaoIntegrada}">
								        <f:param name="id" value="#{acao.id}"/>
							    		<h:graphicImage url="/img/table_go.png" />
								</h:commandLink>
							</td>
							
							<td width="2%">		
								<h:commandLink title="Alterar Situa��o" action="#{ alteracaoProjetoMBean.iniciarAlterarSituacao }" immediate="true" rendered="#{acesso.comissaoIntegrada}">
							       <f:param name="id" value="#{acao.id}"/>
				                   <h:graphicImage url="/img/alterar_old.gif" />
								</h:commandLink>
							</td>
							
							<td width="2%">		
								<h:commandLink title="Gerenciar Membros" action="#{ membroProjeto.iniciarAlterarCoordenador }" immediate="true" rendered="#{acesso.comissaoIntegrada}">
							       <f:param name="id" value="#{acao.id}"/>
				                   <h:graphicImage url="/img/extensao/businessman_refresh.png"  />
								</h:commandLink>
							</td>

							<td width="2%">		
								<h:commandLink title="Alterar Or�amento" action="#{ alteracaoProjetoMBean.iniciarAlterarOrcamento }" immediate="true" rendered="#{acesso.comissaoIntegrada}">
							       <f:param name="id" value="#{acao.id}"/>
				                   <h:graphicImage url="/img/table_refresh.png" width="16" height="16" />
								</h:commandLink>
							</td>

							
							<td width="2%">
								<h:commandLink title="Visualizar Or�amento" action="#{ projetoBase.viewOrcamento }" immediate="true">
								        <f:param name="id" value="#{acao.id}"/>
							    		<h:graphicImage url="/img/projeto/orcamento.png" width="16" height="16" />
								</h:commandLink>
							</td>

							<td>
								<h:commandLink title="Remover" action="#{ projetoBase.preRemoverCiepe }" immediate="true" rendered="#{acesso.comissaoIntegrada}">
					       		 <f:param name="id" value="#{acao.id}"/>
				    			<h:graphicImage url="/img/delete.gif" width="16" height="16" />
				    			</h:commandLink>
							</td>
										
							
		              </tr>
		          </c:forEach>
		          
		 	</tbody>
		 </table>
		 <rich:jQuery selector="#listagem" query="tablesorter( {headers: {5: { sorter: false }, 6: { sorter: false },7: { sorter: false }, 8: { sorter: false }, 9: { sorter: false }, 10: { sorter: false }, 11: { sorter: false } } });" timing="onload" /> 
	</h:form>


	</c:if>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>