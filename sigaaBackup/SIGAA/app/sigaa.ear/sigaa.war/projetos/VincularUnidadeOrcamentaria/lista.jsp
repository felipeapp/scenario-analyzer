<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.MEMBRO_COMITE_INTEGRADO } %>">

<script type="text/javascript" src="/shared/loadScript?src=javascript/jquery.tablesorter.min.js"></script>

<link rel="stylesheet" type="text/css" href="/shared/css/tablesorter/style.css" />

<%@page import="br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto"%>

<style>
	.em_andamento {
		color: gray;
	}
</style>


<f:view>
	<h2><ufrn:subSistema /> > Vincular Unidade Orcamentária ao Projeto </h2>

 	<a4j:keepAlive beanName="alteracaoProjetoMBean" />

	<%@include file="/projetos/form_busca_projetos.jsp"%>

	<c:set var="acoes" value="#{buscaAcaoAssociada.resultadosBusca}"/>

	<c:if test="${empty acoes}">
		<center><i> Nenhuma Ação Acadêmica Associada foi localizada </i></center>
	</c:if>


	<c:if test="${not empty acoes}">

		<div class="infoAltRem">
		    <h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Ação
		    <h:graphicImage url="/img/projetos/vincular_orcamento.png" width="16" height="16" style="overflow: visible;" rendered="#{acesso.comissaoIntegrada}"/> <h:outputText rendered="#{acesso.comissaoIntegrada}" value=": Vincular Unidade Orçamentária" escape="false"/>		    
		    <br/>
		</div>

	<h:form id="form2">
	
		 <table class="listagem tablesorter" id="listagem">
		    <caption>Ações acadêmicas localizadas (${ fn:length(acoes) })</caption>
	
		      <thead>
		      	<tr>
		      		<th>Nº Inst.</th>
		        	<th>Título</th>
		        	<th>Unidade</th>
		        	<th>Unidade Orçamentária</th>
		        	<th>Situação</th>
		        	<th>&nbsp;</th>
		        	<th>&nbsp;</th>
		        </tr>
		 	</thead>
		 	<tbody>
		 	
			 	 <c:forEach items="#{acoes}" var="acao" varStatus="status">
		               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
		
							<td ${acao.cadastroEmAndamento ? 'class="em_andamento"':''} align="center"> <fmt:formatNumber value="${acao.numeroInstitucional}"  pattern="0000"/>/${acao.ano}</td>
		                    <td ${acao.cadastroEmAndamento ? 'class="em_andamento"':''}>
		                     	${acao.titulo} 
								<h:outputText value="<br/><i>Coordenador(a): #{acao.coordenador.pessoa.nome}</i>" rendered="#{not empty acao.coordenador}" escape="false"/> 							
		                    </td>
		                    <td ${acao.cadastroEmAndamento ? 'class="em_andamento"':''}>${acao.unidade.sigla}</td>
							<td ${acao.cadastroEmAndamento ? 'class="em_andamento"':''}>${(empty acao.unidadeOrcamentaria.nome) ? '<font color="red">NÃO DEFINIDA</font>' : acao.unidadeOrcamentaria.siglaNome}</td>
							<td ${acao.cadastroEmAndamento ? 'class="em_andamento"':''}>${acao.situacaoProjeto.descricao}</td>
							
							<td width="2%">					
								<h:commandLink title="Visualizar Ação" action="#{ projetoBase.view }" id="btnViewAcao">
								    <f:param name="id" value="#{acao.id}"/>
			                   		<h:graphicImage url="/img/view.gif"/>
								</h:commandLink>
							</td>
							
                            <td width="2%">
                                <h:commandLink title="Vincular Unidade Orçamentária" action="#{ alteracaoProjetoMBean.iniciarVincularUnidadeOrcamentaria }" immediate="true" id="btnIniciarVincularUnidade">
                                     <f:param name="id" value="#{acao.id}"/>
						             <h:graphicImage url="/img/projetos/vincular_orcamento.png" width="16" height="16" style="overflow: visible;"/>
                                </h:commandLink>
                            </td>
		              </tr>
		          </c:forEach>
		          
		 	</tbody>
		 </table>
		 <rich:jQuery selector="#listagem" query="tablesorter( {headers: {5: { sorter: false }, 6: { sorter: false } } });" timing="onload" /> 
	</h:form>


	</c:if>

</f:view>

</ufrn:checkRole>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>