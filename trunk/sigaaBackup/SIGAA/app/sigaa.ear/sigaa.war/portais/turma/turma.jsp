<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<f:view>
<%@include file="/portais/turma/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/loadScript?src=javascript/websnapr.js"></script>
<link rel="stylesheet" media="screen" href="/shared/css/websnapr.css"/>

<h:outputText value="#{principalTurma.carregaDados}"/>

<c:set var="noticias" value="${ portalTurma.noticias }"/>
<c:set var="atividades" value="${ portalTurma.ultimasAtividades }"/>

<script type="text/javascript">
var fcontent=new Array()
<c:forEach var="noticia" items="${ noticias }" varStatus="loop">
fcontent[${ loop.index }]="<a href='#' id='noticia_${ noticia.id }'><strong><fmt:formatDate pattern="dd/MM/yyyy" value="${ noticia.data }"/></strong> <br />${ noticia.descricao }</a>"
</c:forEach>
</script>

		<div id="conteudo-cima">
			<div id="noticias-turma" class="item-conteudo-cima">
				<div id="scroll">
					<h4>Notícias</h4>
					<c:if test="${ not empty noticias }">
						<script type="text/javascript" src="/shared/javascript/scroller.js"></script>
					</c:if>
					<c:if test="${ empty noticias }">
					<table width="220" border="0" cellspacing="0" cellpadding="0"><tr><td valign="middle" align="left"><strong>» </strong> Não há notícias cadastradas.</td></tr></table>
					</c:if>
				</div>
			</div>

			<div id="atividades-turma" class="item-conteudo-cima">
				<h4>Últimas Atividades</h4>
				<ul>
				<c:if test="${ not empty atividades }">
				<c:forEach var="atividade" items="${ atividades }">
					<li>
						<span class="data"><ufrn:format name="atividade" property="data" type="data"/></span>
						<span class="descricao">${ atividade.descricao }</span>
					</li>
				</c:forEach>
				</c:if>
				<c:if test="${ empty atividades }">
				<li>» Nenhuma atividade registrada</li>
				</c:if>
				</ul>
			</div>
		</div>

		<%-- == == == == == == CENTRO DO SITE == == == == == == == == --%>
	 	<div id="abas-turma" style="width: 99%">

				<%-- Histórico de Aulas --%>
				<div id="aulas-turma" style="padding: 10px;">

					<c:forEach var="item" items="${ principalTurma.topicos }">
						<div id="topico_aula" style="margin-left: ${20*item.nivel}px; margin-bottom: 10px; position: relative">
						<h:form>
						<h4> ${ item.descricao }  </h4>
						<div style="position: absolute; top: 0; right: 0">
							<h:commandButton action="#{arquivoUsuario.inserirArquivoTurma}" image="/img/portal_turma/page_white_powerpoint.png" title="Adicionar Arquivo" rendered="#{ acesso.docente  || permissao.docente }"/>
							<input type="hidden"  name="idTopico"  value="${item.id}" />
							<input type="hidden" value="${item.id}" name="id" />
							<h:commandButton image="/img/portal_turma/layout_add.png" action="#{topicoAula.cadastrarFilho}" title="Cadastrar Sub-Tópico" rendered="#{ acesso.docente  || permissao.docente }"/>
							<h:commandButton image="/img/portal_turma/site_add.png" action="#{indicacaoReferencia.inserirReferencia}" title="Cadastrar Referência (Site, Livro, ..)" rendered="#{ acesso.docente  || permissao.docente }"/>
							<h:commandButton image="/img/portal_turma/enviar_tarefa.png" action="#{tarefaTurma.inserirTarefa}" title="Cadastrar Tarefa"  rendered="#{ acesso.docente  || permissao.docente }"/>
							<h:commandButton image="/img/portal_turma/page_add.png" action="#{conteudoTurma.inserirConteudo}" title="Cadastrar Conteúdo" rendered="#{ acesso.docente  || permissao.docente }"/>
							<h:commandButton image="/img/alterar.gif" value="Alterar" action="#{topicoAula.atualizar}" title="Editar Tópico"/>
							<h:commandButton image="/img/delete.gif" alt="Remover" action="#{topicoAula.remover}" title="Remover Tópico" onclick="return(confirm('Deseja realmente excluir este registro?'))"/>
						</div>

						</h:form>

						<p class="descricao-aula">
						<small><fmt:formatDate value="${ item.data }" pattern="dd/MM/yyyy"/>- <fmt:formatDate value="${ item.fim }" pattern="dd/MM/yyyy"/></small><br/>
						${ item.conteudo }
						</p>
						<ul class="materiais">
						<c:if test="${ not empty item.materiais }">
							<c:forEach var="material" items="${ item.materiais }">
								<li>
								<c:choose>

								<c:when test="${ material.tipoArquivo }">
									<h:form>
									 <a href="${ctx}/verProducao?idProducao=${ material.arquivo.idArquivo }&key=${ sf:generateArquivoKey(material.arquivo.idArquivo) }" target="_blank" title="${material.descricao}"><img src="${ ctx }/${ material.icone }" title="${material.descricao}"> ${ material.nome }</a>
									 <c:if test="${ acesso.docente or permissao.inserirArquivo }">
										<input type="hidden" name="id" value="${ material.id }"/>
										<h:commandButton image="/img/porta_arquivos/delete.gif" action="#{ arquivoUsuario.removerAssociacaoArquivo }"  onclick="return(confirm('Deseja realmente excluir este registro?'))"/>
									</c:if>
									</h:form>
								</c:when>

								<c:when test="${ material.tipoIndicacao }">
									<a href="${material.url}" class="websnapr" target="_blank"><img src="${ ctx }/${ material.icone }"/> ${ material.nome }</a>
								</c:when>

								<c:when test="${ material.tipoTarefa }">
									<a href="${ctx}/portais/turma/TarefaTurma/form.jsf" title="${material.conteudo}"><img src="${ ctx }/${ material.icone }"/> ${ material.nome }</a>
								</c:when>

								<c:when test="${ material.tipoConteudo }">
									<a href="${ctx}/portais/turma/ConteudoTurma/view.jsf?id=${material.id}"><img src="${ ctx }/${ material.icone }"/> ${ material.nome }</a>
								</c:when>

								</c:choose>
								</li>
							</c:forEach>
						</c:if>
						</ul>

						</div>
					</c:forEach>

				</div>

		</div>

</f:view>

<script type="text/javascript" src="/sigaa/javascript/turma.js"> </script>
<script type="text/javascript" src="/sigaa/javascript/noticias-turma.js"> </script>

<%@include file="/portais/turma/rodape.jsp"%>