
<%@include file="/ava/cabecalho.jsp" %>

<f:view>

<style>
	.botao-medio {
			margin-bottom:0px !important;
			height:60px !important;
	}
</style>

<%@include file="/ava/menu.jsp" %>
<a4j:keepAlive beanName="dataAvaliacao"/>
<h:form>
	<c:set var="avaliacoes" value="#{ dataAvaliacao.listagem }"/>
	<fieldset>
		<legend>Datas de Avaliações</legend>
		
		<c:if test="${ turmaVirtual.docente }">
			<div class="descricaoOperacao">
				Professor, informe através do Link <b>Cadastrar Data de Avaliação</b> 
				os dias das avaliações de sua turma. Dessa forma, você facilita a vida dos alunos pois 
				o sistema gera um calendário de todas as avaliações que o aluno visualiza e se organiza.
			</div>
		</c:if>
		
		<c:if test="${ turmaVirtual.docente || permissaoAva.permissaoUsuario.docente }">
			<div class="menu-botoes" style="text-align:center;width:210px;margin: 0 auto;">
				<ul class="menu-interno">
						<li class="botao-medio novaAvaliacao;">
							<h:commandLink action="#{ dataAvaliacao.novo }">
								<p style="margin-left:2px;padding-top:11px;font-variant:small-caps;font-size:1.3em;font-weight:bold;">Cadastrar Data de Avaliação</p> 
							</h:commandLink>
						</li>
				</ul>	
				<div style="clear:both;"></div>	
			</div>
		</c:if>
		
		<c:if test="${ empty avaliacoes }">
			<p class="empty-listing">Nenhum item foi encontrado</p>
		</c:if>
		
		<c:if test="${ not empty avaliacoes }">	
			<div class="infoAltRem">
				<img src="${ctx}/ava/img/zoom.png"/>: Visualizar
				<c:if test="${ turmaVirtual.docente || permissaoAva.permissaoUsuario.docente }">
					<img src="${ctx}/ava/img/page_edit.png"/>: Alterar
					<img src="${ctx}/ava/img/bin.png"/>: Remover
				</c:if>
			</div>
			
			<table class="listing">
				<thead>
					<tr><th>Data</th><th style="text-align:center;">Hora</th><th style="text-align:left;">Descrição</th><th></th><c:if test="${ turmaVirtual.docente || permissaoAva.permissaoUsuario.docente }"><th></th><th></th></c:if></tr>
				</thead>
				<tbody>
					<c:forEach var="a" items="#{ avaliacoes }" varStatus="loop">
					<tr class="${ loop.index % 2 == 0 ? 'even' : 'odd' }">
						<td class="first width75"><fmt:formatDate value="${ a.data }" pattern="dd/MM/yyyy"/></td>
						<td style="text-align:center;" class="width120">${ a.hora }</td>
						<td>${ a.descricao }</td>
						<td class="icon">
							<h:commandLink title="Visualizar" action="#{ dataAvaliacao.mostrar }"><f:param name="id" value="#{ a.id }"/><h:graphicImage value="/ava/img/zoom.png"/></h:commandLink>
						</td>
					    <c:if test="${ turmaVirtual.docente || permissaoAva.permissaoUsuario.docente }">
					    <td class="icon">
					    	<h:commandLink title="Alterar" action="#{ dataAvaliacao.editar }"><f:param name="id" value="#{ a.id }"/><h:graphicImage value="/ava/img/page_edit.png"/></h:commandLink>
					    </td>
					    <td class="icon">
					    	<h:commandLink title="Remover" action="#{ dataAvaliacao.remover }" onclick="return(confirm('Deseja realmente excluir esta avaliação?'));" styleClass="confirm-remover"><f:param name="id" value="#{ a.id }"/><h:graphicImage value="/ava/img/bin.png"/></h:commandLink>
					    </td>
					    </c:if>
					</tr>
					</c:forEach>
				</tbody>
			</table>
		</c:if>
	</fieldset>
</h:form>
</f:view>
<%@include file="/ava/rodape.jsp" %>
