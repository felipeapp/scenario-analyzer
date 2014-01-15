<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

<h2> <ufrn:subSistema /> > Cadastro de Not�cia no Portal</h2>
	
	<c:set var="noticia" value="#{noticiaPortalDiscente.obj}" />
	<table class="visualizacao">
		<caption>Detalhes da Not�cia</caption>
		
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
			<th width="40%">Localiza��o:</th>
			<td> ${ noticia.localizacaoDesc } </td>
		</tr>
		<tr>
			<th>T�tulo:</th>
			<td> ${noticia.titulo} </td>
		</tr>
		<tr>
			<th>Corpo da Not�cia:</th>
			<td> ${noticia.descricao} </td>
		</tr>
		<tr>
			<th>Esta not�cia dever� receber um destaque maior na exbi��o?</th>
			<td> ${noticia.destaque == true ? 'Sim' : 'N�o' } </td>
		</tr>
		
		<tr>
			<th>Esta not�cia encontra-se publicada?</th>
			<td> ${noticia.publicada == true ? 'Sim' : 'N�o' }
				<c:if test="${noticia.publicada and not empty noticia.expirarEm}">
				(at� <fmt:formatDate value="${noticia.expirarEm}"/>)
				</c:if>			
			</td>
		</tr>
		
		<tr>
			<th>Not�cia criada em:</th>
			<td> <fmt:formatDate value="${noticia.criadoEm}"/> (Usu�rio: ${ noticia.criadoPor.login }) </td>
		</tr>
		
	</table>
	<br />
	<h:form>
		<center><h:commandLink action="#{noticiaPortalDiscente.listarNoticias}"><< Voltar</h:commandLink></center>
	</h:form>		
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>