<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	
	<h2><ufrn:subSistema/> > Cursos para acesso aos Fóruns</h2>
	<a4j:keepAlive beanName="listagemCursosForumDocenteMBean"></a4j:keepAlive>
	
	<h:form>
		<div class="infoAltRem">
    
			<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: 
			Selecionar Curso
			
		</div> 
				
		<table class="listagem">
			<caption>Curso(s) Com Participação do Docente - Turmas de <h:outputText value="#{listagemCursosForumDocenteMBean.ano}.#{listagemCursosForumDocenteMBean.periodo}"/> </caption>
			<thead>
				<tr>
					<td width="8%">Unidade</td>
					<td>Cidade</td>
					<td>Curso</td>
					<td width="10%">Modalidade</td>
					<td width="10%">Nível</td>
					<th width="1%"></th>
				</tr>
			</thead>
			<tbody>
			<c:if test="${empty listagemCursosForumDocenteMBean.cursosAtuadosPeloDocente}">
				<tr><td colspan="6" style="text-align: center;">Nenhum Curso Encontrado.</td></tr>
			</c:if>
			
			<c:forEach items="#{listagemCursosForumDocenteMBean.cursosAtuadosPeloDocente}" var="curso" varStatus="loop">
			<tr class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
				<td>${curso.unidade.sigla}</td>
				<td>${curso.municipio.nome}</td>
				<td>${curso.descricao}</td>
				<td>${curso.modalidadeEducacao.descricao}</td>
				<td>${curso.nivelDescricao}</td>
				<td>
					<h:commandLink id="selecionarCurso" title="Selecionar o Curso" action="#{forum.listarForunsCurso}">
						<h:graphicImage alt="Selecionar o Curso" value="/img/seta.gif"/>
						<f:param name="id_curso" value="#{curso.id}" />
					</h:commandLink>
				</td>
			</tr>
			</c:forEach>
			</tbody>
		</table>	
		
		<br></br>
		<div align="center"> <b>Outros Cursos:</b>
		<h:selectOneMenu id="cursosAnteriores" value="#{listagemCursosForumDocenteMBean.anoPeriodo }" 
			valueChangeListener="#{listagemCursosForumDocenteMBean.recarregaListaForunsCurso}" 
			onchange="submit()" immediate="true" >
			<f:selectItems value="#{ listagemCursosForumDocenteMBean.anoPeriodoComCursos }"/>
		</h:selectOneMenu> 
		</div> 
		
		<br/>

		<c:if test="${not empty listagemCursosForumDocenteMBean.forunsAcessiveis}">		
			<div class="infoAltRem">
	    
				<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: 
				Selecionar Fórum
				
			</div> 		
			
			<table class="listagem">
				<caption>Lista de Fóruns Acessíveis Pelo Docente (${fn:length(listagemCursosForumDocenteMBean.forunsAcessiveis)})</caption>
				<thead>
					<tr>
						<th>Fórum</th>
						<th>Autor</th>
						<th style="text-align: center">Data Criação</th>
						<th width="1%"></th>
					</tr>
				</thead>
				
				<tbody>
					<c:forEach var="n" items="#{ listagemCursosForumDocenteMBean.forunsAcessiveis }" varStatus="status">
						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" >
							<td><h:outputText value="#{ n.forum.titulo }"/></td>
							<td class="nomeUsuario"> ${ n.forum.usuario.pessoa.nomeResumido } </td>
							<td style="text-align: center">
								<ufrn:format type="dataHora" valor="${ n.forum.data }"/>
							</td>
							<td>
								<h:commandLink action="#{forumMensagem.listarMensagemPorForum}" title="Selecionar Fórum">
									<h:graphicImage alt="Selecionar Fórum" value="/img/seta.gif"/>
									<f:param name="id" value="#{n.forum.id}" />
									<f:param name="idCurso" value="#{n.forum.idCursoCoordenador}" />
								</h:commandLink>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</c:if>			
		
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>