<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.PRESIDENTE_COMITE_EXTENSAO } %>">

<script type="text/javascript" src="/shared/loadScript?src=javascript/jquery.tablesorter.min.js"></script>
<link rel="stylesheet" type="text/css" href="/shared/css/tablesorter/style.css" />

<%@page import="br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto"%>

<style>
	.em_andamento {
		color: gray;
	}
</style>


<f:view>
	<h2><ufrn:subSistema /> > Vincular Unidade Orcamentária à Ação </h2>

 	<a4j:keepAlive beanName="vincularUnidadeOrcamentariaMBean" />

    <div class="descricaoOperacao">
       Através desta página o usuário poderá buscar Ações de Extensão utilizando a combinação de parâmetros conforme suas necessidades.
       As ações localizadas poderão ser vinculadas a uma Unidade Orçamentária através do link correspondente. 
    </div>

	<%@include file="/extensao/form_busca_atividade.jsp"%>

    <c:set var="acoes" value="#{atividadeExtensao.atividadesLocalizadas}"/>

    <c:if test="${not empty acoes}">

		<div class="infoAltRem">
		    <h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Ação
		    <h:graphicImage url="/img/projetos/vincular_orcamento.png" width="16" height="16" style="overflow: visible;"/> <h:outputText value=": Vincular Unidade Orçamentária" escape="false"/>		    
		    <br/>
		</div>

	<h:form id="form2">
	
		 <table class="listagem tablesorter" id="listagem">
		    <caption>Ações acadêmicas localizadas (${ fn:length(acoes) })</caption>
	
		      <thead>
		      	<tr>
		      		<th style="text-align: center;">Código</th>
		        	<th>Título</th>
		        	<th>Unidade Proponente</th>
		        	<th>Unidade Orçamentária</th>
		        	<th>Situação</th>
		        	<th>&nbsp;</th>
		        	<th>&nbsp;</th>
		        </tr>
		 	</thead>
		 	<tbody>
		 	
			 	 <c:forEach items="#{acoes}" var="acao" varStatus="status">
		               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
		
							<td ${acao.projeto.cadastroEmAndamento ? 'class="em_andamento"':''} style="text-align: center;"> ${acao.codigo}</td>
		                    <td ${acao.projeto.cadastroEmAndamento ? 'class="em_andamento"':''}>
		                     	${acao.titulo} 
								<h:outputText value="<br/><i>Coordenador(a): #{acao.projeto.coordenador.pessoa.nome}</i>" rendered="#{not empty acao.projeto.coordenador}" escape="false"/> 							
		                    </td>
		                    <td ${acao.projeto.cadastroEmAndamento ? 'class="em_andamento"':''}>${acao.unidade.sigla}</td>
							<td ${acao.projeto.cadastroEmAndamento ? 'class="em_andamento"':''}>${(empty acao.projeto.unidadeOrcamentaria.nome) ? '<font color="red">NÃO DEFINIDA</font>' : acao.projeto.unidadeOrcamentaria.siglaNome}</td>
							<td ${acao.projeto.cadastroEmAndamento ? 'class="em_andamento"':''}>${acao.situacaoProjeto.descricao}</td>
							
							<td width="2%">					
								<h:commandLink title="Visualizar Ação" action="#{ atividadeExtensao.view }" id="btnViewAcao">
								    <f:param name="id" value="#{acao.id}"/>
			                   		<h:graphicImage url="/img/view.gif"/>
								</h:commandLink>
							</td>
							
                            <td width="2%">
                                <h:commandLink title="Vincular Unidade Orçamentária" action="#{ vincularUnidadeOrcamentariaMBean.iniciar }" 
                                    immediate="true" id="btnIniciarVincularUnidade" rendered="#{acao.projetoIsolado}">
                                     <f:param name="id" value="#{acao.projeto.id}" id="idProjeto"/>
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