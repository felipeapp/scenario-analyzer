<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h:messages showDetail="true"></h:messages>
	<h2><ufrn:subSistema /> > Emiss�o de Certificado/Declara��o</h2>
	<br>


	<%@include file="/extensao/form_busca_atividade.jsp"%>

	
	<div class="infoAltRem">
		    <h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar A��o de Extens�o		    
	    	<h:graphicImage value="/img/seta.gif" style="overflow: visible;"/>: Selecionar Membros da A��o  
	</div>

	<br/>

	<c:set var="atividades" value="#{atividadeExtensao.atividadesLocalizadas}"/>

	<c:if test="${empty atividades}">
		<center><i> Nenhuma atividade localizada </i></center>
	</c:if>


	<c:if test="${not empty atividades}">

	<h:form>

		 <table class="listagem">
		    <caption>A��es de extens�o localizadas (${ fn:length(atividades) })</caption>
	
		      <thead>
		      	<tr>
		      		<th>C�digo</th>
		        	<th width="50%">T�tulo</th>
		        	<th>Unidade</th>
		        	<th>Situa��o</th>
		        	<th>&nbsp;</th>
		        	<th>&nbsp;</th>		        	
		        </tr>
		 	</thead>
		 	<tbody>
		 	
			 	 <c:forEach items="#{atividades}" var="atividade" varStatus="status">
		               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
		
							<td> ${atividade.codigo} </td>
		                    <td> ${atividade.titulo} </td>
		                    <td> ${atividade.unidade.sigla} </td>
							<td> ${atividade.situacaoProjeto.descricao} </td>
							<td>					
								<h:commandLink title="Visualizar A��o" action="#{ atividadeExtensao.view }" immediate="true">
								        <f:param name="idAtividade" value="#{atividade.id}"/>
			                   			<h:graphicImage url="/img/view.gif" />
								</h:commandLink>
							</td>
							<td>
								<h:commandLink title="Selecionar Membros da A��o" action="#{ declaracaoExtensao.selecionarMembro }" immediate="true">
								        <f:param name="idAtividade" value="#{atividade.id}"/>
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
