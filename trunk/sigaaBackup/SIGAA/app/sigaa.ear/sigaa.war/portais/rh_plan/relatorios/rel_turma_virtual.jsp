<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
	tr.curso td {padding: 20px 0 0; border-bottom: 1px solid #555}
	tr.header td {padding: 0px ; background-color: #eee; border-bottom: 1px solid #555; font-weight: bold; border: 1px solid #000;}
	tr.componentes td {padding: 5px 2px 2px; border-bottom: 1px solid #000; border: 1px solid #000;}
	tr.total td {padding: 5px 2px 2px; border: 1px solid #000; font-weight: bold;}
</style>
<f:view>

	<table width="100%" style="font-size: 11px;">
		<caption><td align="center"><b>RELATÓRIO DE UTILIZAÇÃO DA TURMA VIRTUAL</b></td></b>
	</table>
	<br />
	<table width="100%">
		<tr class="curso">
		<td><b>Ano - Período:</b> 
		<h:outputText value="#{relatoriosPlanejamento.ano}"/>.<h:outputText value="#{relatoriosPlanejamento.periodo}"/>	
		</td>
	</table>
	<br />

    <c:set var="_departamento" />
    
	<table cellspacing="1" width="100%" style="font-size: 10px;">
		<c:forEach items="#{relatoriosPlanejamento.lista}" var="linha" varStatus="indice">
			
			<c:set var="departamentoAtual" value="${linha.departamento}"/>
			  <c:if test="${_departamento != departamentoAtual}">
					<tr class="curso">
						<td colspan="10"><b>${linha.departamento} / ${linha.centro}</b></td>
					</tr>
					<tr class="header">
						<td align="left"> Nome</td>
						<td align="right"> Tópicos de Aula</td>
						<td align="right"> Turmas</td>
						<td align="right"> Arquivos</td>
						<td align="right"> Frequências</td>
						<td align="right"> Conteúdos</td>
						<td align="right"> Tarefas</td>
						<td align="right"> Enquetes</td>
						<td align="right"> Notícias</td>
						<td align="right"> Mensagens Fórum</td>
						<td align="right"> Questionários</td>
						<td align="right"> Chats</td>
						<td align="right"> Vídeos</td>
						<td align="right"> Twitter</td>
					</tr>
						<c:set var="_departamento" value="${departamentoAtual}"/>
				</c:if>
					<tr class="componentes">
						<td align="left"><b> ${linha.nome}</b></td>
						<td align="right"> ${linha.topico_aulas}</td>
						<td align="right"> ${linha.turmas}</td>
						<td align="right"> ${linha.arquivos}</td>
						<td align="right"> ${linha.frequencia}</td>
						<td align="right"> ${linha.conteudos}</td>
						<td align="right"> ${linha.tarefas}</td>
						<td align="right"> ${linha.enquetes}</td>
						<td align="right"> ${linha.noticias}</td>
						<td align="right"> ${linha.mensagens_forum}</td>
						<td align="right"> ${linha.questionarios}</td>
						<td align="right"> ${linha.chat}</td>
						<td align="right"> ${linha.videos}</td>
						<td align="right"> ${linha.twitter}</td>
						
						<c:set var="enquete_" value="${enquete_  + linha.enquetes}"/>
						<c:set var="turma_" value="${turma_  + linha.turmas}"/>
						<c:set var="topico" value="${topico + linha.topico_aulas}"/>
						<c:set var="arquivo" value="${arquivo + linha.arquivos}"/>
						<c:set var="frequencia" value="${frequencia + linha.frequencia}"/>
						<c:set var="conteudo" value="${conteudo + linha.conteudos}"/>
						<c:set var="tarefa" value="${tarefa + linha.tarefas}"/>
						<c:set var="noticias" value="${noticias + linha.noticias}"/>
						<c:set var="mensagens" value="${mensagens + linha.mensagens_forum}"/>
						<c:set var="questionarios" value="${questionarios + linha.questionarios}"/>
						<c:set var="chat" value="${chat + linha.chat}"/>
						<c:set var="videos" value="${videos + linha.videos}"/>
						<c:set var="twitter" value="${twitter + linha.twitter}"/>
					</tr>
					
					<c:set var="proximo" value="${relatoriosPlanejamento.lista[indice.index+1].departamento}" ></c:set>
					
					<c:if test="${departamentoAtual != proximo}">
						<tr class="total">
							<td align="right">Total:</td>
							<td align="right">${topico}</td>
							<td align="right">${turma_}</td>
							<td align="right">${arquivo}</td>
							<td align="right">${frequencia}</td>
							<td align="right">${conteudo}</td>
							<td align="right">${tarefa}</td>
							<td align="right">${enquete_}</td>
							<td align="right">${noticias}</td>
							<td align="right">${mensagens}</td>
							<td align="right">${questionarios}</td>
							<td align="right">${chat}</td>
							<td align="right">${videos}</td>
							<td align="right">${twitter}</td>
						</tr>
						<c:set var="topico" value="0"/>
						<c:set var="turma_" value="0"/>
						<c:set var="arquivo" value="0"/>
						<c:set var="frequencia" value="0"/>
						<c:set var="conteudo" value="0"/>
						<c:set var="tarefa" value="0"/>
						<c:set var="noticias" value="0"/>
						<c:set var="enquete_" value="0"/>
						<c:set var="mensagens" value="0"/>
						<c:set var="questionarios" value="0"/>
						<c:set var="chat" value="0"/>
						<c:set var="videos" value="0"/>
						<c:set var="twitter" value="0"/>
					</c:if>
		</c:forEach>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>