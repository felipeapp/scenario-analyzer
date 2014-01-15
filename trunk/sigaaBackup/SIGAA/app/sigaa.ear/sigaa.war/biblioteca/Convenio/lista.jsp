<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>

<f:view>
	<h2><ufrn:subSistema /> > Convênios </h2>
	
	<div class="descricaoOperacao">
		<p>Caro Usuário, </p>
		<p>Esta página lista os convênios firmados para realização de empréstimos a comunidades externas ao sistema.</p> 
	</div>
	
	<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL} %>">
		<div class="infoAltRem" style="width: 100%">
			<img src="/shared/img/adicionar.gif" style="overflow: visible;"/>:
			<a href="${ctx}/biblioteca/Convenio/form.jsf">Cadastrar Novo Convênio</a> 
			<img src="/shared/img/alterar.gif" style="overflow: visible;"/>: Alterar 
			<img src="/shared/img/delete.gif" style="overflow: visible;"/>: Remover 
		</div>
	</ufrn:checkRole>
	
	<h:form>
	
	
		<%-- Para não ficar buscando várias vezes no banco --%>
		<c:set var="listaConvenios" value="${convenioBiblioteca.all}" scope="request" />
	
	
	
		<c:if test="${ not empty listaConvenios }">
		
			<table class="listagem">
				<caption>Lista de Convênios (${convenioBiblioteca.size})</caption>
				<thead>
					<tr>
						<th>Nome</th>
						<th style="text-align:center;">Início</th>
						<th style="text-align:center;">Fim</th>
						<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL} %>">
							<th width="20"></th><th width="20"></th>
						</ufrn:checkRole>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="#{listaConvenios}" var="item" varStatus="loop">
						<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
							<td>${item.nome}</td>
							<td style="text-align:center;"><ufrn:format type="data" valor="${item.dataInicio}" /></td>
							<td style="text-align:center;"><ufrn:format type="data" valor="${item.dataFim}" /></td>
							
							<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL} %>">
								<td>
									<h:commandLink action="#{ convenioBiblioteca.atualizar }">
										<f:verbatim><img src="/shared/img/alterar.gif" alt="Alterar" title="Alterar"/></f:verbatim>
										<f:param name="id" value="#{ item.id }" />
									</h:commandLink>
								</td>
								<td>
									<h:commandLink action="#{ convenioBiblioteca.remover }" onclick="#{confirmDelete}">
										<f:verbatim><img src="/shared/img/delete.gif" alt="Remover" title="Remover"/></f:verbatim>
										<f:param name="id" value="#{ item.id }" />
									</h:commandLink>
								</td>
							</ufrn:checkRole>
							
						</tr>
					</c:forEach>
				</tbody>
				
				<tfoot>
					<tr>
						<td colspan="5" style="text-align: center;">
							<h:commandButton value="<< Voltar" action="#{convenioBiblioteca.cancelar}"></h:commandButton>
						</td>
					</tr>
				</tfoot>
				
			</table>
		</c:if>
		<c:if test="${ empty listaConvenios }">
			<table class="listagem">
				<caption> Lista de Convenios</caption>
				<tr><td style="color:#FF0000;text-align:center;">Não há convênios cadastrados.</td></tr>
			</table>
		</c:if>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
