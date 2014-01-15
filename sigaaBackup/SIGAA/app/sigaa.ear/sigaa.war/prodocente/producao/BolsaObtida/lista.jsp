<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/javascript/paineis/painel_detalhar_pd.js"></script>

<f:view>
	<%@include file="/portais/docente/menu_docente.jsp"%>
	<%@include file="../../include_painel.jsp"%>
	<c:set var="dirBase" value="/prodocente/producao/" scope="session"/>
	<h2>
		 <a href="${ctx}/prodocente/listar_producao.jsf">
 		<h:graphicImage title="Voltar para Tela de Listar Produções Intelectuais" value="/img/prodocente/voltarproducoes.gif" style="overflow: visible;"/>
 		</a>
		Bolsa Obtida
	</h2>
	<h:form>
	<div class="infoAltRem">
		 <h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/><h:commandLink action="#{bolsaObtida.preCadastrar}" value="Cadastrar Bolsa Obtida"/><h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar Bolsa Obtida
	    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Bolsa Obtida
	</div>
	</h:form>
	<h:outputText value="#{bolsaObtida.create}" />

<c:set var="lista" value="${bolsaObtida.all}" />
<c:if test="${empty lista}">
	<br />
	<center>
	<span style="color:red;">Nenhuma bolsa Encontrada.</span>
	</center>
</c:if>
<c:if test="${not empty lista}">


	<table class="listagem" style="width:100%" border="1">
		<caption class="listagem">Bolsas Obtidas</caption>
		<thead>
			<tr>
			<td>Informação</td>
			<td>Sub-Área</td>
			<td style="text-align: center;">Data de Início</td>
			<td style="text-align: center;">Data Final</td>
			<td>Tipo de Bolsa</td>
			<td>Insitituicao Fomento</td>
			<td style="text-align: center;">Ano de Referência</td>
			<td>Situação</td>
			<td></td>
			<td></td>
			<td></td>
		</thead>
		<h:form>
		<c:forEach items="#{bolsaObtida.all}" var="item" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">

				<td>
	               		<a4j:commandLink id="showItem" action="#{carregarDadosProducoesMBean.carregar}"
               		oncomplete="Richfaces.showModalPanel('view')" immediate="true" reRender="view">  
               			${item.titulo}
               			<f:param name="id" value="#{item.id}"/>
				    </a4j:commandLink>  

					<c:if test="${ empty item.informacao }">
					 <a href="#" onclick="exibirPainel(${item.id }, false, 0)" class="linkAbrePainel">
					 	<span style="color: #A0A0A0; font-size: 10px;;">
					 	Sem informação cadastrada.
					 	<br />
					 	Clique para detalhar produção.
					 	</span>
					 </a>
					</c:if>

				</td>
				<td>
					 	${item.subArea.nome}
			    </td>

				<td style="text-align: center;"><fmt:formatDate  value="${item.periodoInicio }" pattern="MM/yyyy" /></td>
				<td style="text-align: center;"><fmt:formatDate  value="${item.periodoFim }" pattern="MM/yyyy" /></td>
				<td>${item.tipoBolsaProdocente.descricao}</td>

				<c:if test="${item.instituicaoFomento.id==0}" >
					<td>${item.instituicao}</td>
				</c:if>


				<c:if test="${item.instituicaoFomento.id!=0}" >
					<td>${item.instituicaoFomento.nome}</td>
				</c:if>

				<td style="text-align: center;">${item.anoReferencia}</td>
				<td>${item.situacaoDesc}</td>

				<td width=20>
					<h:commandLink action="#{bolsaObtida.atualizar}" >
						<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" title="Alterar"/>
						<f:param name="id" value="#{item.id}"/>
					</h:commandLink>
				</td>						
				<td width=25>
					<h:commandLink action="#{bolsaObtida.remover}"  onclick="#{confirmDelete}">
						<h:graphicImage value="/img/delete.gif" style="overflow: visible;" title="Remover"/>
						<f:param name="id" value="#{item.id}"/>
					</h:commandLink>
				</td>

			</tr>
		</c:forEach>
	  </h:form>
	</table>
	
	<br />
	<center>
	<h:form id="paginacaoForm">

		<center>

		<h:commandButton image="/img/voltar.gif" actionListener="#{paginacao.previousPage}" rendered="#{paginacao.paginaAtual > 0 }"/>

		<h:selectOneMenu value="#{paginacao.paginaAtual}" valueChangeListener="#{paginacao.changePage}" onchange="submit()" immediate="true">

		<f:selectItems id="paramPagina" value="#{paginacao.listaPaginas}"/>

		</h:selectOneMenu>

		<h:commandButton image="/img/avancar.gif" actionListener="#{paginacao.nextPage}" rendered="#{paginacao.paginaAtual < (paginacao.totalPaginas - 1)}"/>

		<br/>

		<i><h:outputText value="#{paginacao.totalRegistros }"/> Registro(s) Encontrado(s)</i>

		</center>

	</h:form>

</c:if>
<h:outputText value="#{bolsaObtida.dropList}" />
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>