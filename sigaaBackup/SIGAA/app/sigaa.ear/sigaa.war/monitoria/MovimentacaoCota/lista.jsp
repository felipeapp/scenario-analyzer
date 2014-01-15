<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto"%>

<f:view>

	<h:outputText value="#{movimentacaoCota.create}"/>	

	<h2><ufrn:subSistema /> > Movimentação de Cotas Entre Projetos</h2>
	

	<div class="descricaoOperacao">
		A movimentação de cotas só é permitida entre Projetos de Monitoria em execução.		
	</div>

	<%@include file="/monitoria/form_busca_projeto.jsp"%>

	<c:set var="projetos" value="#{projetoMonitoria.projetosLocalizados}"/>


	<c:if test="${empty projetos}">
	<center><i> Nenhum Projeto localizado </i></center>
	</c:if>


	<c:if test="${not empty projetos}">

		<div class="infoAltRem">
		    <h:graphicImage value="/img/seta.gif"style="overflow: visible;"/>: Alterar Cota do Projeto
		    <br/>
		</div>
		<br/>
	
		<h:form>
		 <table class="listagem">
		    <caption>Projetos de Monitoria Encontrados (${ fn:length(projetos) })</caption>
	
		      <thead>
		      	<tr>
		        	<th width="60%">Título</th>
		        	<th>Situação</th>
		        	<th>Bolsas Solicitadas</th>
		        	<th>Bolsas Concedidas</th>
		        	<th>&nbsp;</th>
		        </tr>
		 	</thead>
		 	<tbody>
		 	
	       	<c:forEach items="#{projetos}" var="projeto" varStatus="status">
	               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
	
	                    <td> ${projeto.anoTitulo} </td>
						<td> ${projeto.situacaoProjeto.descricao} </td>
	                    <td> ${projeto.bolsasSolicitadas} </td>
	                    <td> ${projeto.bolsasConcedidas} </td>
						<td width="5%">					
								<h:commandLink  title="Adicionar Cota de Projeto" action="#{ movimentacaoCotasMonitoria.selecionarProjeto }" 
									immediate="true" styleClass="noborder" rendered="#{projeto.monitoriaEmExecucao}">
								    	<f:param name="id" value="#{projeto.id}"/>
								      	<h:graphicImage url="/img/seta.gif" />
								</h:commandLink>															
						</td>
	              </tr>
	          </c:forEach>
		 	</tbody>
		 </table>
	 </h:form>
	 
	</c:if>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>