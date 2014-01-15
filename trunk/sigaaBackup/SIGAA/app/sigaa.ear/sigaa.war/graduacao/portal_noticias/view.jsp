<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

<h2> <ufrn:subSistema /> > Cadastro de Notícia no Portal</h2>
	
	<c:set var="noticia" value="#{noticiaPortalDiscente.obj}" />
	<table class="visualizacao">
		<caption>Detalhes da Notícia</caption>
		
		<c:if test="${not empty noticiaPortalDiscente.curso}">
			<tr>
				<th width="40%">Curso:</th>
				<td> ${ noticiaPortalDiscente.curso.descricao } </td>
			</tr>
		</c:if>
		<c:if test="${not empty noticiaPortalDiscente.programa}">
			<tr>
				<th width="40%">Programa:</th>
				<td> ${ noticiaPortalDiscente.programa.nome } </td>
			</tr>
		</c:if>		
		<tr>
			<th width="40%">Localização:</th>
			<td> ${ noticia.localizacaoDesc } </td>
		</tr>
		<tr>
			<th>Título:</th>
			<td> ${noticia.titulo} </td>
		</tr>
		<tr>
			<th>Corpo da Notícia:</th>
			<td> ${noticia.descricao} </td>
		</tr>
		<tr>
			<th>Esta notícia deverá receber um destaque maior na exbição?</th>
			<td> ${noticia.destaque == true ? 'Sim' : 'Não' } </td>
		</tr>
		
		<tr>
			<th>Esta notícia encontra-se publicada?</th>
			<td> ${noticia.publicada == true ? 'Sim' : 'Não' }
				<c:if test="${noticia.publicada and not empty noticia.expirarEm}">
				(até <fmt:formatDate value="${noticia.expirarEm}"/>)
				</c:if>			
			</td>
		</tr>
		
		<tr>
			<th>Notícia criada em:</th>
			<td> <fmt:formatDate value="${noticia.criadoEm}"/> (Usuário: ${ noticia.criadoPor.login }) </td>
		</tr>
		
	</table>
	<br />
	<h:form>
		<center><h:commandLink action="#{noticiaPortalDiscente.listarNoticias}"><< Voltar</h:commandLink></center>
	</h:form>		
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>