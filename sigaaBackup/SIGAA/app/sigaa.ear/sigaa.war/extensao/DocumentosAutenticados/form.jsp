<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h:messages showDetail="true"></h:messages>
	<h2><ufrn:subSistema /> > Emissão de Certificado/Declaração</h2>
	<br>


	<%@include file="/extensao/form_busca_atividade.jsp"%>

	
	<div class="infoAltRem">
		    <h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Ação de Extensão		    
	    	<h:graphicImage value="/img/seta.gif" style="overflow: visible;"/>: Selecionar Membros da Ação  
	</div>

	<br/>

	<c:set var="atividades" value="#{atividadeExtensao.atividadesLocalizadas}"/>

	<c:if test="${empty atividades}">
		<center><i> Nenhuma ação de extensão localizada </i></center>
	</c:if>


	<c:if test="${not empty atividades}">

	<h:form>

		 <table class="listagem">
		    <caption>Ações de extensão localizadas (${ fn:length(atividades) })</caption>
	
		      <thead>
		      	<tr>
		      		<th>Código</th>
		        	<th width="50%">Título</th>
		        	<th>Unidade</th>
		        	<th>Situação</th>
		        	<th>Dimensão Acadêmica</th>
		        	<th>&nbsp;</th>
		        	<th>&nbsp;</th>		        	
		        </tr>
		 	</thead>
		 	<tbody>
		 	
			 	 <c:forEach items="#{atividades}" var="atividade" varStatus="status">
		               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
		
							<td> ${atividade.codigo} </td>
		                    <td> ${atividade.titulo} 
		                    	<h:outputText value="<br/><i>Coordenador(a): #{atividade.coordenacao.pessoa.nome}</i>" rendered="#{not empty atividade.coordenacao}" escape="false"/>
		                    </td>
		                    <td> ${atividade.unidade.sigla} </td>
							<td> ${atividade.situacaoProjeto.descricao} </td>
							<td>${atividade.projeto.extensao ? 'ASSOCIADO' : 'EXTENSÃO'}</td>
							<td>					
								<h:commandLink title="Visualizar Ação" action="#{ atividadeExtensao.view }" immediate="true">
								        <f:param name="id" value="#{atividade.id}"/>
			                   			<h:graphicImage url="/img/view.gif" />
								</h:commandLink>
							</td>
							<td>
								<h:commandLink title="Selecionar Membros da Ação" action="#{ documentosAutenticadosExtensao.selecionarMembro }" immediate="true">
								        <f:param name="id" value="#{atividade.projeto.id}"/>
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
