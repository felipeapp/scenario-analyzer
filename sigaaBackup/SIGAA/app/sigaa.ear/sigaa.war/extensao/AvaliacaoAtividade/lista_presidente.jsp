<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<script type="text/javascript" src="/shared/loadScript?src=javascript/jquery.tablesorter.min.js"></script>
<link rel="stylesheet" type="text/css" href="/shared/css/tablesorter/style.css" />

<f:view>

	<%@include file="/portais/docente/menu_docente.jsp"%>

	<h:messages showDetail="true"></h:messages>
	<h2><ufrn:subSistema /> > Avaliar Proposta de Ação de Extensão</h2>
	

	<%@include file="/extensao/barra_filtro_atividade.jsp"%>


	<c:set var="ativids" value="#{filtroAtividades.resultadosBusca}"/>


						
			<h:form id="formListaAtividadesAvaliacao">
			
			<%-- 
				EXPORTA LISTA DE ATIVIDADES COM SITUAÇÃO 'AGUARDANDO AVALIAÇÃO' PARA ARQUIVO EM FORMATO .XLS
			
				<center><h:commandButton value="Exportar Lista Completa para Avaliação Final" action="#{ avaliacaoAtividade.exportarListaAcoes }" immediate="true" /></center> 
			
			--%>
			
			<br/>

				<c:if test="${not empty ativids}">
					
					<div class="infoAltRem">
					
					<h:graphicImage value="/img/view.gif" style="overflow: visible;" />: Visualizar Ação de Extensão 
						
						<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />
						<h:outputText value=": Avaliar Ação de Extensão" />
						<br>
						<h:graphicImage value="/img/extensao/bullet_square_green.png" title="Financiamento Interno" style="overflow: visible;" />
						<h:outputText value=": Financiamento Interno" />	
						<h:graphicImage value="/img/extensao/bullet_square_red.png" title="Financiamento Externo" style="overflow: visible;" />
						<h:outputText value=": Financiamento Externo" />
						<h:graphicImage value="/img/extensao/bullet_square_blue.png" title="Auto Financiamento" style="overflow: visible;" />
						<h:outputText value=": Auto Financiamento" />	
						<h:graphicImage value="/img/extensao/bullet_square_yellow.png" title="Convênio Funpec" style="overflow: visible;" />
						<h:outputText value=": Convênio Funpec" />
						
				
					</div>
						
						
					<br/>
		
						
					 <table class="listagem tablesorter" id="listagem">
					    <caption>Lista de Ações encontradas (${ fn:length(ativids) })</caption>
				
					     <thead>
					      	<tr>
					      	    <th>Código</th>
					        	<th>Título</th>
					        	<th>Área Temática</th>
					        	<th>Financiamento</th>
					        	<th>&nbsp;</th>
								<th>&nbsp;</th>		        	
					        </tr>
						 </thead>
		
						<tbody>
				
							<c:set var="unidadeAtividade" value=""/>	 		 	
					       	<c:forEach items="#{ativids}" var="ativid" varStatus="status">
					
						            <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
						                <td> ${ativid.codigo} </td>
					                    <td> ${ativid.titulo} </td>
					                    <td> ${ ativid.areaTematicaPrincipal.descricao } </td>	
										<td> 
											<h:graphicImage url="/img/extensao/bullet_square_green.png" title="Financiamento Interno" rendered="#{ativid.financiamentoInterno}"/>		
											<h:graphicImage url="/img/extensao/bullet_square_red.png" title="Financiamento Externo" rendered="#{ativid.financiamentoExterno}"/>
											<h:graphicImage url="/img/extensao/bullet_square_blue.png" title="Auto Financiamento" rendered="#{ativid.autoFinanciado}"/>
											<h:graphicImage url="/img/extensao/bullet_square_yellow.png" title="Convênio Funpec" rendered="#{ativid.convenioFunpec}"/>
										</td>
										<td width="2%">
											<h:commandLink title="Visualizar Ação de Extensão"
												action="#{ atividadeExtensao.view }" immediate="true">
												<f:param name="id" value="#{ativid.id}" />
												<h:graphicImage url="/img/view.gif" />
											</h:commandLink>
											
										</td>
										<td width="2%">
											<h:commandLink title="Avaliar Ação de Extensão"
												action="#{ avaliacaoAtividade.iniciarAvaliacaoPresidenteComite }"
												immediate="true" rendered="#{ativid.situacaoProjeto.id == 109}"> <%-- aguardando avaliacao--%>
												<f:param name="id" value="#{ativid.id}" />
												<h:graphicImage url="/img/seta.gif" />
											</h:commandLink>
										</td>
					                </tr>
					        </c:forEach>
		
						</tbody>
					</table>
				</c:if>
				
			<rich:jQuery selector="#listagem" query="tablesorter( {headers: {4: { sorter: false },5: { sorter: false } } });" timing="onload" />
		</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>