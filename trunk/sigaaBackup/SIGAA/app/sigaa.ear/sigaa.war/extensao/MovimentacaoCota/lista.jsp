<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto"%>

<f:view>

	<h:outputText value="#{movimentacaoCotas.create}"/>	
	<h:outputText value="#{atividadeExtensao.create}"/>		
	
	<h2><ufrn:subSistema /> > Movimentar Cotas de Bolsas Entre A��es de Extens�o</h2>
	

	<table width="100%" class="subFormulario">
		<tr>
		<td width="40"><html:img page="/img/help.png"/> </td>
		<td valign="top" style="text-align: justify">
		 Somente propostas de A��es APROVADAS pelo comit� de extens�o pode ter cotas de bolsas cadastradas.
		 Os demais projetos N�O poder�o ter cotas cadastradas.<br/>
		 Localize o projeto atrav�s da busca abaixo e clique no �cone para distribuir cotas.
		</td>
		</tr>
	</table>

	<br/>

	<%@include file="/extensao/form_busca_atividade.jsp"%>


	<c:set var="atividades" value="#{atividadeExtensao.atividadesLocalizadas}"/>


	<c:if test="${empty atividades}">
		<center><i> Nenhuma A��o localizada </i></center>
	</c:if>


	<c:if test="${not empty atividades}">

		<div class="infoAltRem">
		    <h:graphicImage value="/img/bolsas.png" style="overflow: visible;"/>: Alterar Cota de Bolsa da A��o
		    <h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar A��o		    
		    <br/>
		</div>
		<br/>
	
		<h:form>
		 <table class="listagem">
		    <caption>A��es de Extens�o Encontradas (${ fn:length(atividades) })</caption>
	
		      <thead>
		      	<tr>
		        	<th>T�tulo</th>
		        	<th>Situa��o</th>
		        	<th>N� Bolsas</th>
		        	<th>&nbsp;</th>
		        	<th>&nbsp;</th>
		        </tr>
		 	</thead>
		 	<tbody>
		 	
	       	<c:forEach items="#{atividades}" var="atividade" varStatus="status">
	               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
	
	                    <td> ${atividade.anoTitulo} </td>
						<td> ${atividade.situacaoProjeto.descricao} </td>
	                    <td> ${atividade.bolsasConcedidas} </td>
						<td>					
							<h:commandLink title="Alterar Cota de Bolsa da A��o" action="#{ movimentacaoCotasExtensao.selecionarAtividade }" style="border: 0;"
								rendered="#{atividade.financiamentoInterno}">
							        <f:param name="id" value="#{atividade.id}"/>
		                   			<h:graphicImage url="/img/bolsas.png"/>
							</h:commandLink>
						</td>
						<td>
							<h:commandLink title="Visualizar A��o" action="#{ atividadeExtensao.view }" style="border: 0;">
							         <f:param name="id" value="#{atividade.id}"/>
		                   			<h:graphicImage url="/img/view.gif" />
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