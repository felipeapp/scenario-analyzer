<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	
	<h2><ufrn:subSistema/> > Vincular Servidor a Fórum de Curso</h2>
	<a4j:keepAlive beanName="forumCursoDocente"></a4j:keepAlive>
	
	<div class="descricaoOperacao">
		<p><b>Caso Usuário,</b></p>
		<br/>
		<p>Nesta tela será possível conceder acesso ao <b>Docente</b> a um determinado <b>Fórum de Curso</b>.</p>
	</div>
	
	<h:form id="form">
		<table class="formulario" width="70%">
			<caption>Busca de Fóruns</caption>
			<tr>
				<th style="font-weight: bold; text-align: right;">Docente:</th>
				<td>${forumCursoDocente.servidor.pessoa.nome}</td>
			</tr>
			<tr>
				<th class="obrigatorio">Nível:</th>
				<td>
					<h:selectOneMenu value="#{forumCursoDocente.nivel}" id="nivel" onchange="submit()">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{nivelEnsino.allCombo}" />
					</h:selectOneMenu>					
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Curso:</th>
				<td>
					<h:selectOneMenu value="#{forumCursoDocente.curso.id}" id="curso" >
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{forumCursoDocente.cursoNivel}" />
					</h:selectOneMenu>			
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton id="buscar" value="Buscar" action="#{forumCursoDocente.buscar}" />
						<h:commandButton id="voltar" value="<< Voltar" action="#{forumCursoDocente.iniciar}" />
						<h:commandButton id="cancelar" value="Cancelar" action="#{forumCursoDocente.cancelar}" immediate="true" onclick="#{confirm}"/> 
					</td>
				</tr>
			</tfoot>		
		</table>
		<br/>
		<c:if test="${not empty forumCursoDocente.listaForunsPorCurso}">
			<div class="infoAltRem">
				<h:graphicImage value="/img/adicionar.gif"/>: Adicionar Fórum
			</div>
			
			<table class="listagem">
				<caption>Lista de Fóruns Encontrados (${fn:length(forumCursoDocente.listaForunsPorCurso)})</caption>
				<thead>
					<tr>
						<th style="width: 60%">Título</th>
						<th style="width: 20%">Autor</th>
						<th style="width: 20%; text-align: center">Data de Criação</th>
						<th width="1%"></th>
					</tr>
				</thead>
				
				<tbody>
					<c:if test="${empty forumCursoDocente.listaForunsPorCurso}">
						<tr>
							<td colspan="4" style="text-align: center;">Nenhum Fórum encontrado.</td>
						</tr>
					</c:if>
			
			
					<c:forEach var="n" items="#{ forumCursoDocente.listaForunsPorCurso }" varStatus="status">
						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" >
							<td><h:outputText value="#{ n.titulo }"/></td>
							<td class="nomeUsuario"> ${ n.usuario.pessoa.nomeResumido } </td>
							<td style="text-align: center">
								<ufrn:format type="dataHora" valor="${ n.data }"/>
							</td>
							<td>
								<h:commandLink action="#{forumCursoDocente.adicionarForum}" title="Adicionar Fórum">
									<h:graphicImage value="/img/adicionar.gif"/>
									<f:param name="id" value="#{n.id}"/>
								</h:commandLink>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</c:if>
		
		<br/>
		
		<%@ include file="_foruns_docente.jsp" %>		
	</h:form>

	<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp" %>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>