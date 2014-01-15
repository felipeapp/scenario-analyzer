<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>


<f:view>
	<h2><ufrn:subSistema /> > Tipos de Empréstimo </h2>
	
	<div class="descricaoOperacao"> 
    	<p> Abaixo estão listados os Tipos de Empréstimos que podem ser realizados no sistema. </p>
    	<br/>
    	<p> <strong>Um Tipo de Empréstimo indica situações especiais em que um empréstimo pode ocorrer, permindo que um mesmo material possua prazos e quantidades diferentes 
    		dependendo do Tipo de Empréstimo escolhido.</strong>
    	</p>
    	<p> Não podem existir dois os mais Tipos de Empréstimos com a mesma descrição no sistema.  Existem alguns Tipos de Empréstimos 
    	que são fixos no sistema e não podem ser alterados, os demais podem ser criados e removidos livremente.</p>
		<br/>
		<p><strong>IMPORTANTE:</strong> Após cadatrar um novo Tipo de Empréstimo, deve-se configurar nas repectivas Políticas de Empréstimos os prazos e quantidades que os materiais emprestados por esse Tipo de Empréstimos possuirão.</p>
	</div>
	
	<a4j:keepAlive beanName="tipoEmprestimo" />
	
	<h:form>

		<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL} %>">
			<div class="infoAltRem" style="width:80%;">
				<h:graphicImage value="/img/adicionar.gif" />
				<h:commandLink action="#{tipoEmprestimo.preCadastrar}" value="Novo Tipo de Empréstimo" />
	
				<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />: Alterar Tipo de Empréstimo
				<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: Remover Tipo de Empréstimo
			</div>
		</ufrn:checkRole> 



		<table class="listagem" style="width:80%;">
			<caption>Lista de Tipos de Empréstimos (${tipoEmprestimo.size})</caption>
			<thead>
				<tr>
					<th>Descrição</th>
					
					<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL} %>">
						<th width="20"></th>
						<th width="20"></th>
					</ufrn:checkRole>
					
				</tr>
			</thead>
			<c:forEach items="#{tipoEmprestimo.all}" var="t" varStatus="status">
				<tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td>${t.descricao}</td>
					
					<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL} %>">
					<td>
						<h:commandLink title="Alterar Tipo de Empréstimo" action="#{tipoEmprestimo.preAtualizar}" rendered="#{t.alteravel}">
							<f:param name="id" value="#{t.id}" />
							<h:graphicImage url="/img/alterar.gif" alt="Alterar Tipo" />
						</h:commandLink>
					</td>
					<td>
						<h:commandLink title="Remover Tipo de Empréstimo"  rendered="#{t.alteravel}"
							action="#{tipoEmprestimo.remover}" onclick="return confirm('Tem certeza que deseja remover este Tipo de Empréstimo ?');">
							<f:param name="idTipoEmprestimo" value="#{t.id}" />
							<h:graphicImage url="/img/delete.gif" alt="Remover Tipo." />
						</h:commandLink>
					</td>
					</ufrn:checkRole>
				</tr>
			</c:forEach>
			
			<tfoot>
				<tr>
					<td colspan="3" style="text-align: center;">
						<h:commandButton value="Cancelar" action="#{tipoEmprestimo.cancelar}" />
					</td>
				</tr>
			</tfoot>
		</table>

	</h:form>

</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>