	<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/javascript/paineis/painel_detalhar_pd.js"></script>

<f:view>
	<%@include file="/portais/docente/menu_docente.jsp"%>
	<c:set var="dirBase" value="/prodocente/producao/" scope="session"/>
	<h2>
	 <a href="${ctx}/prodocente/listar_producao.jsf">
 		<h:graphicImage title="Voltar para Tela de Listar Produções Intelectuais" value="/img/prodocente/voltarproducoes.gif" style="overflow: visible;"/>
 	</a>
	Apresentação Em Evento
	</h2>
	<h:form>
	<div class="infoAltRem">
		 <h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/><h:commandLink action="#{apresentacaoEmEvento.preCadastrar}" value="Cadastrar"/><h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar
	    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover
  	    <h:graphicImage value="/img/link.gif" style="overflow: visible;"/>: Enviar Arquivo
	    <img src="/shared/img/icones/download.png" style="overflow: visible;"/>: Baixar Arquivo
	</div>
	</h:form>

	<h:outputText value="#{apresentacaoEmEvento.create}" />


<c:set var="lista" value="${apresentacaoEmEvento.all}" />
<c:if test="${empty lista}">
	<br />
	<center>
	<span style="color:red;">Nenhuma Apresentação Em Evento Encontrada.</span>
	</center>
</c:if>
<c:if test="${not empty lista}">



	<table class=listagem style="width:100%" border="1">
		<caption class="listagem">Lista de Apresentação Em Evento</caption>
		<thead>
			<tr>
			<td></td>
			<td>Titulo</td>
			<td></td>
			<td>Ano </td>
			<td>Tipo da Participação</td>
			<td>Evento</td>
			<td>Ano de Referência</td>
			<td>Situação</td>
			<td></td>
			<td></td>
			<td></td>
			</tr>
		</thead>
		<c:forEach items="${apresentacaoEmEvento.all}" var="item" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td align="center">
					<c:if test="${item.sequenciaProducao !=null}">
						<ufrn:help img="/img/prodocente/lattes.gif">Origem da Produção: <b>Lattes</b></ufrn:help>
					</c:if>
					<c:if test="${item.sequenciaProducao ==null}">
						<ufrn:help img="/img/icones/producao_menu.gif">Origem da Produção: <b>Prodocente</b></ufrn:help>
					</c:if>
				</td>
				<td>
					<a href="#" onclick="exibirPainel(${item.id }, false, 0)" style="font-weight: lighter;" >
					 ${item.titulo}
					</a>
				</td>
				<td align="center">
				<c:if test="${item.idArquivo != null}">
					<a href="/sigaa/verProducao?idProducao=${item.idArquivo}&key=${ sf:generateArquivoKey(item.idArquivo) }" target="_blank">
						  	<img src="/shared/img/icones/download.png" border="0" alt="Baixar Arquivo da Produção" title="Baixar Arquivo da Produção" />
					</a>
				</c:if>
				</td>
				<td> ${item.anoReferencia} </td>
				<td>${item.tipoParticipacao.descricao} </td>
				<td>${item.evento}</td>
				<td>${item.anoReferencia}</td>
				<td>${item.situacaoDesc}</td>
				<td width=20>
					<h:form>
					<input type="hidden" value="${item.id}" name="id" /> <h:commandButton
						image="/img/alterar.gif" value="Alterar" alt="Alterar"
						action="#{apresentacaoEmEvento.atualizar}" />
					</h:form>
				</td>
				<td width=25>
				 <h:form>
				   <input type="hidden" value="${item.id}" name="id" /> <h:commandButton
						image="/img/delete.gif" alt="Remover"
						action="#{apresentacaoEmEvento.remover}" onclick="#{confirmDelete}"/>
				 </h:form>
				</td>
				<td width=25>
				 <h:form>
				 <input type="hidden" value="${item.id}" name="idProducao" /> <h:commandButton
						image="/img/link.gif" alt="Enviar Arquivo"
						action="#{producao.preEnviarArquivo}" />
				 </h:form>
				</td>
			</tr>
		</c:forEach>
	</table>
	<br />
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
<h:outputText value="#{apresentacaoEmEvento.dropList}"/>
</f:view>

<script type="text/javascript">
<!--
	function exibirPainel(id, todosBotoes, idArquivo) {
		var getURL = function(t) {
			var url = t.responseText;
			PainelDetalhar.show(url, todosBotoes, idArquivo);
		}
		// buscar url
		var ajax = new Ajax.Request('/sigaa/ajaxPreValidacaoProDoc',
		{
			method: 'get',
			parameters: 'id=' + id +'&idArquivo=' + idArquivo,
			onSuccess: getURL
		});
	}

//-->
</script>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>