<%@include file="/public/include/cabecalho.jsp" %>
<%@taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<style>
	/* Alterações necessárias para aumento do panel de visualização dos links dos menus, para acomodar mais links. */
	#bg-bottom{
		min-height: 380px;
	  	height: 380px;
	}
	#opcoes-modulo .painel {
		overflow: visible;
	}
</style>
<f:view>
<c:if test="${modo != 'classico' }">
	<script type="text/javascript">
		<%String uagent = request.getHeader("User-Agent").toLowerCase(); %>
	
		if (<%=AmbienteUtils.isMobileUserAgent(uagent) %>) {
			window.location = "<%=request.getContextPath()%>/mobile/touch/public/principal.jsf";
		}
	
	</script>
</c:if>
<div id="menu">

	<div id="bg-top">&nbsp;</div>
	<div id="bg-bottom">
		<div id="modulos">
			<div id="l-academico" class="item item-over" >
				<a href="#"><span> Acadêmico </span></a>
			</div>
			<div id="l-biblioteca" class="item" >
				<a href="#"><span> Biblioteca </span></a>
			</div>
			<div id="l-ensino" class="item">
				<a href="#"><span> Ensino </span></a>
			</div>
			<div id="l-extensao" class="item">
				<a href="#"><span> Extensão </span></a>
			</div>
			<div id="l-graduacao" class="item" >
				<a href="#"><span> Graduação </span></a>
			</div>
			<div id="l-pesquisa" class="item">
				<a href="#"><span> Pesquisa </span></a>
			</div>
			<div class="item inativo">
				<a href="#"><span> Pós-Graduação </span></a>
			</div>
			<div id="l-stricto" class="item sub-item">
				<a href="#"><span> Stricto Sensu </span></a>
			</div>
			<div id="l-lato" class="item sub-item"> 
				<a href="#"><a href="#"><span> Lato Sensu </span></a>
			</div>
			<div id="l-processo" class="item">
				<a href="#"><span> Processos Seletivos </span></a>
			</div>
			<div id="l-tecnico" class="item">
				<a href="#"><span> Técnico </span></a>
			</div>
			<div id="l-ouvidoria" class="item">
				<a href="#"><span> Ouvidoria </span></a>
			</div>
		</div>

		<div id="opcoes-modulo"  >
			<div id="opcoes-slider" >
				<div id="p-academico" class="painel" >
					<%@include file="/public/menus/academico.jsp" %>
				</div>
				<div id="p-graduacao" class="painel">
					<%@include file="/public/menus/graduacao.jsp" %>
				</div>
				<div id="p-ensino" class="painel">
					<%@include file="/public/menus/ensino.jsp" %>
				</div>
				<div id="p-pesquisa" class="painel">
					<%@include file="/public/menus/pesquisa.jsp" %>
				</div>
				<div id="p-stricto" class="painel">
					<%@include file="/public/menus/stricto.jsp" %>
				</div>
				<div id="p-lato" class="painel">
					<%@include file="/public/menus/lato.jsp" %>
				</div>
				<div id="p-tecnico" class="painel">
					<%@include file="/public/menus/tecnico.jsp" %>
				</div>
				<div id="p-extensao" class="painel">
					<%@include file="/public/menus/extensao.jsp" %>
				</div>
				<div id="p-processo" class="painel">
					<%@include file="/public/menus/processo.jsp" %>
				</div>
				<div id="p-biblioteca" class="painel">
					<%@include file="/public/menus/biblioteca.jsp" %>
				</div>
				<div id="p-ouvidoria" class="painel">
					<%@include file="/public/menus/ouvidoria.jsp" %>
				</div>
			</div>
		</div>
	</div>
	<br clear="all">
</div>

<br clear="all"/>
<div id="noticias">
	<h3> Notícias e Comunicados </h3>

	<h:outputText value="#{noticiaPortal.create}"/>

	<c:set var="noticias" value="#{noticiaPortal.noticiasPortalPublico}" />
	<c:if test="${empty noticias}">
		<div style="text-align: center; font-style: italic; padding: 10px">
		Não há notícias cadastradas
		</div>
	</c:if>

	<c:forEach var="noticia" items="#{noticias}" varStatus="loop">
	<div class="noticia">
		<h4>
			<h:outputText value="#{noticia.titulo}"/>
			<span class="data">
		  		<h:outputText value="#{noticia.criadoEm}" rendered="#{not empty noticia.criadoEm}"/>
			</span>
		</h4>
		<p>
		 <h:outputText escape="false" value="#{noticia.descricao}"  converter="#{textoConverter}"/>
		</p>
		<c:if test="${not empty noticia.idArquivo && noticia.idArquivo>0}">
		<p>
			<a href="/sigaa/verProducao?idProducao=<h:outputText value="#{noticia.idArquivo}"/>&key=${ sf:generateArquivoKey(noticia.idArquivo) }" target="_blank">
				<f:verbatim><img src="/shared/img/download.png"/></f:verbatim>
				<h:outputText value="#{noticia.nomeArquivo}" rendered="#{not empty noticia.nomeArquivo}" />
				<h:outputText value=" Baixar Anexo" rendered="#{empty noticia.nomeArquivo}" />
			</a>
		</p>	
		</c:if>
	</div>
	</c:forEach>
</div>

<script>
	var ativo = Ext.get('${sessionScope.aba}');
	<c:if test="${not empty param.menuAtivo}">
		var ativo = Ext.get('${param.menuAtivo}');
	</c:if>
</script>
<script type="text/javascript" src="/shared/javascript/public/menu.js"> </script>
</f:view>
<%@include file="/public/include/rodape.jsp" %>