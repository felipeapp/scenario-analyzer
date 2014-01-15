<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>


<%@page import="br.ufrn.sigaa.portal.jsf.NoticiaPortalMBean"%>
<style>
	table.listagem tr.portal td {
		background: #EEE;
		border-bottom: 1px solid #DDD;
		padding: 3px 10px; 
		font-weight: bold;
		color: #404E82;
	}
	
	table.listagem td.links a {
		padding: 2px;
	}	
</style>

<f:view>

	<h2><ufrn:subSistema /> > Lista de Notícias dos Portais</h2>
	
	<div id="ajuda" class="descricaoOperacao">
		<p>Caro Usuário,</p>
		<p>
			Somente as <%= NoticiaPortalMBean.QTD_NOTICIAS_CURSO_PROGRAMA %> últimas notícias cadastradas com status <i>publicada</i> e não expiradas serão listadas na página principal do módulo.
		</p>
	</div>	
	
	<h:outputText value="#{noticiaPortalDiscente.create}" />

	<h:form>
		<div class="infoAltRem">
			 <h:graphicImage value="/img/baixo.gif" style="overflow: visible;"/>: Baixar Arquivo
			 <h:graphicImage value="/img/publicar.png" style="overflow: visible;"/>: Publicar	
			 <h:graphicImage value="/img/despublicar.png" style="overflow: visible;"/>: Despublicar
			 <br />
			 <h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar
			 <h:graphicImage value="/img/alterar.gif" style="overflow: visible;"/>: Alterar	 
			 <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Excluir
		</div>
	</h:form>

	<br />
	<h:form>
	<c:choose>
		<c:when test="${not empty noticiaPortalDiscente.resultadoBusca}">	
		<table class="listagem">
			<caption>Notícias Cadastradas para os Portais</caption>
			<thead>
			<tr>
				<th style="text-align: center">Publicada</th>
				<th>Título da Notícia</th>
				<th width="20%" style="text-align: center">Criada Em</th>
				<th width="20%" style="text-align: center">Expira Após </th>
				<th> </th>
				<th></th>
			</tr>
			</thead>
			
			<c:set var="portal" />
			<c:forEach items="#{noticiaPortalDiscente.resultadoBusca}" var="item" varStatus="status">
			
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td align="center" style="color: ${ item.publicada ? '#292' : '#922' }"> 
						<b>${item.publicada ? 'Sim' : 'Não' }</b> 
					</td>
					<td>${item.titulo}</td>
					<td align="center"> <ufrn:format type="dataHora" valor="${item.criadoEm }"/></td>
					<td align="center"> ${item.descricaoExpiracao } </td>
					<td>  
						<c:if test="${not empty item.idArquivo}">
							<h:commandLink id="idVisualizarArquivo" action="#{noticiaPortalDiscente.visualizarArquivo}" title="Arquivo">
								<f:param name="idArquivo" value="#{ item.idArquivo }"/>
								<h:graphicImage value="/img/baixo.gif" alt="Baixar Arquivo" title="Baixar Arquivo" />
							</h:commandLink>
						</c:if>
					</td>
					<td width="12%" nowrap="nowrap" class="links">
						<h:commandLink title="Publicar notícia" action="#{noticiaPortalDiscente.publicar}" rendered="#{!item.publicada}" >
							<h:graphicImage url="/img/publicar.png"/>
							<f:param name="id" value="#{item.id}" />
						</h:commandLink>				
						<h:commandLink title="Despublicar notícia" action="#{noticiaPortalDiscente.despublicar}" rendered="#{item.publicada}" >
							<h:graphicImage url="/img/despublicar.png"/>
							<f:param name="id" value="#{item.id}" />
						</h:commandLink>
										
						<h:commandLink title="Visualizar" action="#{noticiaPortalDiscente.view}" >
							<h:graphicImage url="/img/view.gif"/>
							<f:param name="id" value="#{item.id}" />
						</h:commandLink>
						<h:commandLink title="Alterar" action="#{noticiaPortalDiscente.iniciarAlterarNoticia}">
							<h:graphicImage url="/img/alterar.gif"/>
							<f:param name="id" value="#{item.id}" />
						</h:commandLink>
						<h:commandLink title="Remover" action="#{noticiaPortalDiscente.removerNoticia}" onclick="return confirm('Deseja continuar a Operação?');">
							<h:graphicImage url="/img/delete.gif"/>
							<f:param name="id" value="#{item.id}" />
						</h:commandLink>
					</td>
				</tr>
				
			</c:forEach>
		</table>
		</c:when>
		<c:otherwise>
			<center>Não existem notícias cadastradas até o momento.</center>
		</c:otherwise> 
	</c:choose>
	</h:form>
	<div id="divLinkVoltar" style="width:70%; margin-left:auto;margin-right:auto;margin-top:20px; text-align:center;">
		<a href="javascript:history.back();"> << Voltar </a>	
	</div>			
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
