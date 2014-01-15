<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/javascript/paineis/painel_detalhar_pd.js"></script>

<f:view>
	<%@include file="/portais/docente/menu_docente.jsp"%>
	<%@include file="../../include_painel.jsp"%>
	<c:set var="dirBase" value="/prodocente/producao/" scope="session" />
	<h2>
		<a href="${ctx}/prodocente/listar_producao.jsf">
 			<h:graphicImage title="Voltar para Tela de Listar Produções Intelectuais" 
 					value="/img/prodocente/voltarproducoes.gif" style="overflow: visible;" />
 		</a>Trabalhos de Conclusão
	</h2>
	<h:form>
		<div class="infoAltRem">
			<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;" /><h:commandLink action="#{banca.formCadastrarCurso}" value="Cadastrar Novo Trabalho de Conclusão" /><h:graphicImage value="/img/alterar.gif"style="overflow: visible;" />: Alterar
        	<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: Remover
	  	  	<h:graphicImage value="/img/link.gif" style="overflow: visible;" />: Enviar Arquivo
	    	<img src="/shared/img/icones/download.png" style="overflow: visible;" />: Baixar Arquivo
		</div>
	</h:form>
	<h:outputText value="#{banca.create}" />
	
	<c:set var="lista" value="${banca.allCurso}" />
	<c:if test="${empty lista}">
		<br />
		<center>
			<span style="color:red;">Nenhum Trabalho de Conclusão Cadastrado.</span>
		</center>
	</c:if>
	
	<c:if test="${not empty lista}">

	<h:form>
		<h:dataTable id="item" var="item" value="#{banca.allCurso}" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar">
		
			<f:facet name="caption">
				<h:outputText value="Lista de Trabalhos de Conclusão" />
			</f:facet>

			<t:column>
				<f:facet name="header">
					<f:verbatim></f:verbatim>
				</f:facet>
				<t:graphicImage></t:graphicImage>
				<c:if test="${item.sequenciaProducao !=null}">
					<ufrn:help img="/img/prodocente/lattes.gif">Origem da Produção: <b>Lattes</b></ufrn:help>
				</c:if>
				<c:if test="${item.sequenciaProducao ==null}">
					<ufrn:help img="/img/icones/producao_menu.gif">Origem da Produção: <b>Prodocente</b></ufrn:help>
				</c:if>
			</t:column>

			<t:column>
				<f:facet name="header">
					<f:verbatim>Título</f:verbatim>
				</f:facet>
				<a4j:commandLink id="showItem" action="#{carregarDadosProducoesMBean.carregar}"
               		oncomplete="Richfaces.showModalPanel('view')" immediate="true" reRender="view">  
               			<h:outputText value="#{item.titulo}" escape="false" />
               		<f:param name="id" value="#{item.id}"/>
				</a4j:commandLink>  
			</t:column>
		
			<t:column>
				<f:facet name="header">
					<f:verbatim></f:verbatim>
				</f:facet>
				<h:outputLink value="/sigaa/verProducao?idProducao=#{item.idArquivo}&key=#{ sf:generateArquivoKey(item.idArquivo) }" title="Visualizar Arquivo" rendered="#{item.idArquivo != null}">
						<img src="/shared/img/icones/download.png" alt="Visualizar Arquivo"/> 
				</h:outputLink>
			</t:column>
		
			<t:column>
				<f:facet name="header">
					<f:verbatim>Autor</f:verbatim>
				</f:facet>
				<h:outputText value="#{item.autor}" escape="false"/>
			</t:column>

			<t:column style="text-align: center;">
				<f:facet name="header">
					<f:verbatim><center>Ano</center></f:verbatim>
				</f:facet>
				<h:outputText style="text-align: center;" value="#{item.anoReferencia}" />
			</t:column>

			<t:column>
				<f:facet name="header">
					<f:verbatim>Situação</f:verbatim>
				</f:facet>
				<h:outputText value="#{item.situacaoDesc}" />
			</t:column>
			
			<t:column>
				<h:commandLink id="atualizar" title="Atualizar" action="#{ banca.atualizar }">
			        <f:param name="id" value="#{item.id}"/>
		    		<h:graphicImage value="/img/alterar.gif"style="overflow: visible;" />
				</h:commandLink>
			</t:column>

			<t:column>
				<h:commandLink id="remover" title="Remover" action="#{ banca.remover }">
			        <f:param name="id" value="#{item.id}"/>
		    		<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />
				</h:commandLink>
			</t:column>

			<t:column>
				<h:commandLink id="enviar_arquivo" title="Enviar Arquivo" action="#{ banca.preEnviarArquivo }">
			        <f:param name="idProducao" value="#{item.id}"/>
		    		<h:graphicImage value="/img/link.gif" style="overflow: visible;" />
				</h:commandLink>
			</t:column>
			
		</h:dataTable>
		</h:form>
	
	<br />
	<h:form id="paginacaoForm">
		<center>
			<h:commandButton image="/img/voltar.gif" actionListener="#{paginacao.previousPage}" rendered="#{paginacao.paginaAtual > 0 }" />

			<h:selectOneMenu value="#{paginacao.paginaAtual}" valueChangeListener="#{paginacao.changePage}" 
					onchange="submit()" immediate="true">
				<f:selectItems id="paramPagina" value="#{paginacao.listaPaginas}" />
			</h:selectOneMenu>
			<h:commandButton image="/img/avancar.gif" actionListener="#{paginacao.nextPage}" 
						rendered="#{paginacao.paginaAtual < (paginacao.totalPaginas - 1)}" />
			<br/>
			<i><h:outputText value="#{paginacao.totalRegistros }"/> Registro(s) Encontrado(s)</i>
		</center>
	</h:form>

	</c:if>
	<h:outputText value="#{banca.dropList}" />
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>