<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> &gt; Gestor de F�rum de Cursos</h2>

		<div class="descricaoOperacao">
			<p>
			 	Abaixo est�o listados os gestores do f�rum do curso de ${gestorForumCursoBean.cursoAtualCoordenacao.nome}.
			</p>
			<p>
			 	Os gestores de f�rum tem as mesmas permiss�es no f�rum que o coordenador do curso, ou seja, 
			 	pode enviar as mensagens com anexo e enviar para os e-mails dos alunos assim como deletar t�picos e mensagens dos t�picos.
		 	</p>
		</div>

	<h:outputText value="#{gestorForumCursoBean.create}" />
	<center>
			<h:messages/>
			<div class="infoAltRem">
				<h:form>
					<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>
					<h:commandLink action="#{gestorForumCursoBean.preCadastrar}" value="Cadastrar"/>
			        <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover <br/>
				</h:form>
			</div>
	</center>

		<table class=listagem>
		  <caption class="listagem">Lista de Gestores de Forum de Curso</caption>
			<thead>
				<tr>
					<td>Docente</td>
					<td>Data de defini��o</td>
					<td></td>
				</tr>
			 </thead>
			<c:if test="${not empty gestorForumCursoBean.lista }">
			<c:forEach items="${gestorForumCursoBean.lista}" var="item" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td>${item.pessoa.nome} </td>
					<td><fmt:formatDate value="${item.dataCadastro}" pattern="dd/MM/yyyy HH:mm" /></td>
					<td width=25>
						<h:form><input type="hidden" value="${item.id}" name="id" /> 
							<h:commandButton image="/img/delete.gif" alt="Remover"
							action="#{gestorForumCursoBean.inativar}" onclick="#{confirmDelete}"/>
						</h:form>
					</td>
				</tr>
			</c:forEach>
			</c:if>
			
			<c:if test="${empty gestorForumCursoBean.lista }">
				<tr><td colspan="3" align="center" style="color: red">
					N�o h� nenhum gestor de f�rum cadastrado para este curso.
				</td></tr>
			</c:if>
		</table>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>